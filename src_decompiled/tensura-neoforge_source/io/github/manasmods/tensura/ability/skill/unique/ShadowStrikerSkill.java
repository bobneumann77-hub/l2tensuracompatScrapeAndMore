package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ShadowStrikerSkill extends Skill {
   private static final UniqueSkillConfig.ShadowStriker CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).ShadowStriker;
   private static final ResourceLocation ACCELERATION = ResourceLocation.fromNamespaceAndPath("tensura", "shadow_striker");

   public ShadowStrikerSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "shadow_striker.ultra_acceleration";
         case 1 -> "shadow_striker.insta_kill";
         case 2 -> "shadow_striker.espionage";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 1 ? CONFIG.magiculeCostKill : 0.0;
   }

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 0 ? CONFIG.auraCost : 0.0;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getTag() != null && instance.getTag().getBoolean("Concealing");
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (EnergyHelper.isOutOfEnergy(entity, instance, 2, 5.0F)) {
         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.lack_aura.toggled_off", new Object[]{this.getModeName(instance, 2)}).withStyle(ChatFormatting.RED), false
            );
         }

         if (instance.isToggled()) {
            instance.setToggled(false);
            instance.onToggleOff(entity);
         }

         CompoundTag tag = instance.getOrCreateTag();
         if (tag.getBoolean("Concealing")) {
            tag.putBoolean("Concealing", false);
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT));
         }
      } else {
         entity.addEffect(
            new MobEffectInstance(
               TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT), 220, CONFIG.concealmentLevelMastered - 1, false, false, false
            )
         );
      }
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(attacker, instance, 1)) {
         return true;
      }

      if (instance.onCoolDown(1)) {
         return true;
      }

      if (source.getEntity() != attacker) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return true;
      }

      if (EnergyHelper.isOutOfEnergy(attacker, instance, 1)) {
         return true;
      }

      DamageSource damagesource = this.createSource(instance, attacker, DamageTypes.MOB_ATTACK, 1);
      TensuraDamageHelper.directSpiritualHurt(target, attacker, damagesource, instance.isMastered(attacker) ? CONFIG.killDamageMastered : CONFIG.killDamage);
      instance.setCoolDown(instance.isMastered(target) ? CONFIG.killCooldownMastered : CONFIG.killCooldown, 1);
      return target.isAlive();
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 2) {
         return false;
      }

      if (instance.isMastered(entity)) {
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      entity.addEffect(
         new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT),
            5,
            instance.isMastered(entity) ? CONFIG.concealmentLevelMastered - 1 : CONFIG.concealmentLevel - 1,
            false,
            false,
            false
         )
      );
      if (heldTicks == 0) {
         entity.level()
            .playSound(null, entity.getX(), entity.getY(), entity.getZ(), TensuraSoundEvents.PRESENCE_CONCEALMENT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      if (mode == 2) {
         if (instance.isMastered(entity)) {
            CompoundTag tag = instance.getOrCreateTag();
            if (tag.getBoolean("Concealing")) {
               tag.putBoolean("Concealing", false);
               entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT));
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            } else {
               tag.putBoolean("Concealing", true);
               entity.addEffect(
                  new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT), 220, CONFIG.concealmentLevel - 1, false, false, false
                  )
               );
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.PRESENCE_CONCEALMENT.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            }
         }
      } else if (mode == 0) {
         if (entity.onGround()) {
            if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               instance.addMasteryPoint(entity);
               double range = instance.isMastered(entity) ? CONFIG.ultraDistanceMastered : CONFIG.ultraDistance;
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, Block.COLLIDER, range);
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
                     ((ServerLevel)level).sendParticles(ParticleTypes.CLOUD, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
                     TensuraParticleHelper.addServerParticlesAroundPos(entity.getRandom(), level, particlePos, ParticleTypes.SWEEP_ATTACK, 3.0);
                     TensuraParticleHelper.addServerParticlesAroundPos(entity.getRandom(), level, particlePos, ParticleTypes.SWEEP_ATTACK, 2.0);
                     AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos))
                        .inflate(Math.max(entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE), 2.0));
                     List<LivingEntity> livingEntityList = level.getEntitiesOfClass(
                        LivingEntity.class, aabb, targetx -> !targetx.is(entity) && !targetx.isAlliedTo(entity)
                     );
                     if (!livingEntityList.isEmpty()) {
                        float bonus = instance.isMastered(entity) ? CONFIG.ultraDamageMastered : CONFIG.ultraDamage;
                        float amount = (float)(
                           entity.getAttributeValue(Attributes.ATTACK_DAMAGE) * entity.getAttributeValue(ManasCoreAttributes.CRITICAL_DAMAGE_MULTIPLIER)
                        );

                        for (LivingEntity target : livingEntityList) {
                           if (target.invulnerableTime < 40) {
                              DamageSource damageSource = this.createSource(instance, entity, DamageTypes.MOB_ATTACK, mode);
                              if (target.hurt(damageSource, amount + bonus)) {
                                 ItemStack stack = entity.getMainHandItem();
                                 stack.getItem().hurtEnemy(stack, target, entity);
                                 EnchantmentHelper.doPostAttackEffectsWithItemSource((ServerLevel)level, target, damageSource, stack);
                                 TensuraEnchantmentHelper.doAdditionalAfterDamage((ServerLevel)level, target, entity, damageSource, stack, amount + bonus);
                                 entity.level()
                                    .playSound(
                                       null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, entity.getSoundSource(), 1.0F, 1.0F
                                    );
                                 if (level instanceof ServerLevel serverLevel) {
                                    serverLevel.getChunkSource().broadcastAndSend(entity, new ClientboundAnimatePacket(entity, 4));
                                 }
                              }

                              TensuraEnchantmentHelper.doAdditionalAfterAttack(
                                 (ServerLevel)level, target, entity, damageSource, entity.getMainHandItem(), amount + bonus
                              );
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
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               }
            }
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
      AttributeInstance dodge = entity.getAttribute(TensuraAttributes.DODGE_STRENGTH);
      if (dodge != null) {
         dodge.addOrReplacePermanentModifier(new AttributeModifier(ACCELERATION, CONFIG.dodgeStrength, Operation.ADD_VALUE));
      }

      AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
      if (invulnerability != null) {
         invulnerability.addOrReplacePermanentModifier(new AttributeModifier(ACCELERATION, CONFIG.dodgeInvulnerability, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
      AttributeInstance dodge = entity.getAttribute(TensuraAttributes.DODGE_STRENGTH);
      if (dodge != null) {
         dodge.removeModifier(ACCELERATION);
      }

      AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
      if (invulnerability != null) {
         invulnerability.removeModifier(ACCELERATION);
      }
   }
}
