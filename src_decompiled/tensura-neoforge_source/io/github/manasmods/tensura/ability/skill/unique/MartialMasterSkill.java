package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MartialMasterSkill extends Skill {
   public static final UniqueSkillConfig.MartialMaster CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).MartialMaster;
   private static final ResourceLocation MARTIAL_MASTER = ResourceLocation.fromNamespaceAndPath("tensura", "martial_master");

   public MartialMasterSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0) && !instance.isTemporarySkill()) {
         TensuraSkillInstance eye = new TensuraSkillInstance((ManasSkill)ExtraSkills.HEAVENLY_EYE.get());
         eye.getOrCreateTag().putBoolean("NoMagiculeCost", true);
         SkillHelper.learnSkill(entity, eye);
      }
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity owner, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(owner, instance)) {
         return true;
      }

      if (TensuraDamageHelper.isPhysicalOrBattlewill(source, owner)) {
         amount.set((Float)amount.get() * CONFIG.damageMultiplier);
      }

      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.onGround() || entity.isInWaterOrBubble()) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            instance.addMasteryPoint(entity);
            ServerLevel level = (ServerLevel)entity.level();
            double range = instance.isMastered(entity) ? CONFIG.ultraDistanceMastered : CONFIG.ultraDistance;
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, range);
            BlockPos resultPos = result.getBlockPos().relative(result.getDirection());
            Vec3 vec3 = ObjectSelectionHelper.getFloorPos(resultPos);
            if (!level.getBlockState(resultPos).canBeReplaced()) {
               vec3 = ObjectSelectionHelper.getFloorPos(resultPos.above());
            }

            if (level.getBlockState(resultPos).is(TensuraBlockTags.SKILL_NOT_TELEPORTABLE)) {
               level.playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            } else if (!entity.level().getWorldBorder().isWithinBounds(ObjectSelectionHelper.getBlockPos(vec3))) {
               entity.sendSystemMessage(Component.translatable("tensura.skill.teleport.out_border").withStyle(ChatFormatting.RED));
            } else {
               Vec3 source = entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0);
               Vec3 offSetToTarget = vec3.subtract(source);

               for (int particleIndex = 1; particleIndex < Mth.floor(offSetToTarget.length()); particleIndex++) {
                  Vec3 particlePos = source.add(offSetToTarget.normalize().scale(particleIndex));
                  level.sendParticles(ParticleTypes.CLOUD, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
                  TensuraParticleHelper.addServerParticlesAroundPos(entity.getRandom(), level, particlePos, ParticleTypes.SWEEP_ATTACK, 3.0);
                  TensuraParticleHelper.addServerParticlesAroundPos(entity.getRandom(), level, particlePos, ParticleTypes.SWEEP_ATTACK, 2.0);
                  AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos))
                     .inflate(Math.max(entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE), 2.0));
                  List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, targetx -> !targetx.is(entity) && !targetx.isAlliedTo(entity));
                  if (!list.isEmpty()) {
                     float bonus = instance.isMastered(entity) ? CONFIG.ultraDamageMastered : CONFIG.ultraDamage;
                     float amount = (float)(
                        entity.getAttributeValue(Attributes.ATTACK_DAMAGE) * entity.getAttributeValue(ManasCoreAttributes.CRITICAL_DAMAGE_MULTIPLIER)
                     );

                     for (LivingEntity target : list) {
                        if (target.invulnerableTime < 40) {
                           DamageSource damageSource = this.createSource(instance, entity, DamageTypes.MOB_ATTACK, mode);
                           if (target.hurt(damageSource, amount + bonus)) {
                              ItemStack stack = entity.getMainHandItem();
                              stack.getItem().hurtEnemy(stack, target, entity);
                              EnchantmentHelper.doPostAttackEffectsWithItemSource(level, target, damageSource, stack);
                              TensuraEnchantmentHelper.doAdditionalAfterDamage(level, target, entity, damageSource, stack, amount + bonus);
                              entity.level()
                                 .playSound(
                                    null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, entity.getSoundSource(), 1.0F, 1.0F
                                 );
                              if (level instanceof ServerLevel serverLevel) {
                                 serverLevel.getChunkSource().broadcastAndSend(entity, new ClientboundAnimatePacket(entity, 4));
                              }
                           }

                           TensuraEnchantmentHelper.doAdditionalAfterAttack(level, target, entity, damageSource, entity.getMainHandItem(), amount + bonus);
                           target.invulnerableTime = 40;
                        }
                     }
                  }
               }

               entity.resetFallDistance();
               entity.unRide();
               entity.teleportTo(vec3.x(), vec3.y(), vec3.z());
               entity.swing(InteractionHand.MAIN_HAND, true);
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            }
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, MARTIAL_MASTER, true);
      AttributeInstance dodge = entity.getAttribute(TensuraAttributes.DODGE_STRENGTH);
      if (dodge != null) {
         dodge.addOrReplacePermanentModifier(new AttributeModifier(MARTIAL_MASTER, CONFIG.dodgeStrength, Operation.ADD_VALUE));
      }

      AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
      if (invulnerability != null) {
         invulnerability.addOrReplacePermanentModifier(new AttributeModifier(MARTIAL_MASTER, CONFIG.dodgeInvulnerability, Operation.ADD_VALUE));
      }

      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.addOrReplacePermanentModifier(new AttributeModifier(MARTIAL_MASTER, CONFIG.meleeDodge, Operation.ADD_VALUE));
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.addOrReplacePermanentModifier(new AttributeModifier(MARTIAL_MASTER, CONFIG.projectileDodge, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, MARTIAL_MASTER, false);
      AttributeInstance dodge = entity.getAttribute(TensuraAttributes.DODGE_STRENGTH);
      if (dodge != null) {
         dodge.removeModifier(MARTIAL_MASTER);
      }

      AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
      if (invulnerability != null) {
         invulnerability.removeModifier(MARTIAL_MASTER);
      }

      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.removeModifier(MARTIAL_MASTER);
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.removeModifier(MARTIAL_MASTER);
      }
   }
}
