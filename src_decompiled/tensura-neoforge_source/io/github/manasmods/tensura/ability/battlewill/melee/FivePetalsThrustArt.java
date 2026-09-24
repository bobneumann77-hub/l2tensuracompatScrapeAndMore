package io.github.manasmods.tensura.ability.battlewill.melee;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.magic.misc.HazyBlossomEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.battlewill.MeleeArts;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.Iterator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FivePetalsThrustArt extends Battlewill {
   private static final BattlewillConfig.FivePetalsThrust CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).FivePetalsThrust;
   private static final ResourceLocation SLOW = ResourceLocation.fromNamespaceAndPath("tensura", "five_petals_thrust");

   public FivePetalsThrustArt() {
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, SLOW, CONFIG.chargingSpeed - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         SkillHelper.learnSkill(entity, ((EightPetalsFlashArt)MeleeArts.EIGHT_PETALS_SLASH.get()).createLearningInstance(entity));
      }
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity entity, DamageSource source, Changeable<Float> amount) {
      if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      }

      if (!(source.tensura$getBarrierBypassLevel() >= 2.0F) && source.getDirectEntity() != null) {
         Iterator var5 = entity.level()
            .getEntitiesOfClass(HazyBlossomEntity.class, entity.getBoundingBox().inflate(1.0), orb -> entity.equals(orb.getOwner()))
            .iterator();
         if (var5.hasNext()) {
            HazyBlossomEntity blossom = (HazyBlossomEntity)var5.next();
            if (blossom.getPetals() > 0 && !(blossom.getDamage() <= 0.0F)) {
               blossom.setPetals(blossom.getPetals() - 1);
               if (blossom.getPetals() <= 0) {
                  blossom.remove();
                  instance.getOrCreateTag().putInt("FlowerID", 0);
                  instance.markDirty();
               }

               amount.set(0.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.CHERRY_LEAVES, 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.BLOSSOM.get(), 2.0);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               return false;
            } else {
               return true;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      int id = tag.getInt("FlowerID");
      Entity idEntity = level.getEntity(id);
      int castTime = CONFIG.chargeTick;
      int spawnTime = Math.max(0, castTime - 70);
      if (idEntity instanceof HazyBlossomEntity blossom) {
         if (heldTicks >= spawnTime + 5 && blossom.isInvisible()) {
            blossom.setInvisible(false);
         }

         if (blossom.getAge() > 10) {
            if (entity.isShiftKeyDown() && blossom.isFollowOwner()) {
               this.removeBlossom(instance, blossom);
               level.playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            }

            return false;
         } else {
            blossom.setAge(0);
            if (!entity.getMainHandItem().is(ItemTags.SHARP_WEAPON_ENCHANTABLE) && !entity.getOffhandItem().is(ItemTags.SHARP_WEAPON_ENCHANTABLE)) {
               entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.item").withStyle(ChatFormatting.RED));
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
               tag.putInt("FlowerID", 0);
               instance.markDirty();
               return false;
            }

            if (entity instanceof Player player) {
               if (instance.isMastered(entity) && heldTicks > castTime) {
                  int max = castTime + CONFIG.bonusChargeTick;
                  double power = (double)Math.min(heldTicks, max) / castTime;
                  player.displayClientMessage(
                     Component.translatable(
                           "tensura.skill.power_scale",
                           new Object[]{SkillUtils.ROUND_DOUBLE.format(power) + "/" + SkillUtils.ROUND_DOUBLE.format(max / castTime)}
                        )
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                     true
                  );
                  if (heldTicks % 10 == 0) {
                     TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.BLOSSOM.get(), 1.0);
                  }
               } else {
                  double sec = heldTicks >= castTime ? castTime / 20.0 : heldTicks / 20.0;
                  player.displayClientMessage(
                     Component.translatable("tensura.magic.cast_time.remaining", new Object[]{SkillUtils.ROUND_DOUBLE.format(sec), castTime / 20.0})
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN)),
                     true
                  );
               }

               double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
               TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getPinkAura(0.75F, (float)size, -0.3F), 3, 0.01);
               if (heldTicks % 2 == 0) {
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.25F,
                     2.0F
                  );
               }
            }

            return true;
         }
      } else {
         if (!entity.getMainHandItem().is(ItemTags.SHARP_WEAPON_ENCHANTABLE) && !entity.getOffhandItem().is(ItemTags.SHARP_WEAPON_ENCHANTABLE)) {
            entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.item").withStyle(ChatFormatting.RED));
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
            tag.putInt("FlowerID", 0);
            instance.markDirty();
            return false;
         }

         if (spawnTime > 0 && heldTicks < spawnTime) {
            if (entity instanceof Player player) {
               String sec = heldTicks >= castTime ? SkillUtils.ROUND_DOUBLE.format(castTime / 20.0) : SkillUtils.ROUND_DOUBLE.format(heldTicks / 20.0);
               player.displayClientMessage(
                  Component.translatable("tensura.magic.cast_time.remaining", new Object[]{sec, SkillUtils.ROUND_DOUBLE.format(castTime / 20.0)})
                     .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN)),
                  true
               );
            }

            double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
            TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getPinkAura(0.75F, (float)size, -0.3F), 3, 0.01);
            if (heldTicks % 2 == 0) {
               level.playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  0.25F,
                  2.0F
               );
            }

            return true;
         } else if (heldTicks >= spawnTime) {
            HazyBlossomEntity blossom = new HazyBlossomEntity(entity.level(), entity);
            blossom.setVisualSize(entity.getBbHeight() / 1.8F);
            blossom.setSkill(entity, instance, this, mode);
            blossom.setPetals(CONFIG.blossomPetal);
            blossom.setInvisible(true);
            blossom.setPos(entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0));
            blossom.setLife(110);
            entity.level().addFreshEntity(blossom);
            blossom.triggerAnim("loopController", "start");
            blossom.setYRot(entity.getYRot() % 360.0F);
            blossom.setXRot(entity.getXRot() % 360.0F);
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
            tag.putInt("FlowerID", blossom.getId());
            instance.markDirty();
            return true;
         } else {
            tag.putInt("FlowerID", 0);
            instance.markDirty();
            return false;
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      int id = tag.getInt("FlowerID");
      if (!(level.getEntity(id) instanceof HazyBlossomEntity blossom)) {
         tag.putInt("FlowerID", 0);
         instance.markDirty();
      } else if (!blossom.isFollowOwner() || blossom.getAge() <= 10) {
         if (heldTicks < CONFIG.chargeTick) {
            this.removeBlossom(instance, blossom);
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
         } else {
            int boosted = 0;
            double cost = this.getAuraCost(entity, instance, mode);
            if (instance.isMastered(entity)) {
               boosted = Math.min(CONFIG.bonusChargeTick, heldTicks - CONFIG.chargeTick) / 20;
               cost = this.getAuraCost(entity, instance, mode) + boosted * CONFIG.bonusCost;
               if (EnergyHelper.isOutOfEnergy(entity, cost, 0.0)) {
                  this.removeBlossom(instance, blossom);
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
                  return;
               }
            } else {
               if (EnergyHelper.isOutOfEnergy(entity, cost, 0.0)) {
                  this.removeBlossom(instance, blossom);
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
                  return;
               }

               instance.addMasteryPoint(entity);
            }

            float damage = CONFIG.dashDamage + boosted * CONFIG.bonusDamage;
            blossom.setDamage(damage);
            blossom.setLife(CONFIG.blossomDuration);
            blossom.setSkill(instance);
            blossom.setApCost(cost);
            entity.swing(InteractionHand.MAIN_HAND, true);
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
            double range = CONFIG.dashDistance;
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
                  TensuraParticleHelper.addServerParticlesAroundPos(
                     entity.getRandom(), level, particlePos, (ParticleOptions)TensuraParticleTypes.BLOSSOM.get(), 1.0
                  );
                  TensuraParticleHelper.addServerParticlesAroundPos(entity.getRandom(), level, particlePos, ParticleTypes.CHERRY_LEAVES, 1.0);
                  TensuraParticleHelper.addServerParticlesAroundPos(entity.getRandom(), level, particlePos, ParticleTypes.CHERRY_LEAVES, 2.0);
                  AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos))
                     .inflate(Math.max(entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) / 2.0, 2.0));
                  List<LivingEntity> livingEntityList = level.getEntitiesOfClass(
                     LivingEntity.class, aabb, targetx -> !targetx.is(entity) && !targetx.isAlliedTo(entity)
                  );
                  if (!livingEntityList.isEmpty()) {
                     float baseDamage = (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE);

                     for (LivingEntity target : livingEntityList) {
                        if (target.invulnerableTime < 40) {
                           DamageSource damageSource = this.createSource(instance, entity, TensuraDamageTypes.HAZY_BLOSSOM_THRUST, mode);
                           if (target.hurt(damageSource, damage + baseDamage)) {
                              ItemStack stack = entity.getMainHandItem();
                              stack.getItem().hurtEnemy(stack, target, entity);
                              EnchantmentHelper.doPostAttackEffectsWithItemSource((ServerLevel)level, target, damageSource, stack);
                              TensuraEnchantmentHelper.doAdditionalAfterDamage((ServerLevel)level, target, entity, damageSource, stack, baseDamage);
                              entity.level()
                                 .playSound(
                                    null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, entity.getSoundSource(), 1.0F, 1.0F
                                 );
                              if (level instanceof ServerLevel serverLevel) {
                                 serverLevel.getChunkSource().broadcastAndSend(entity, new ClientboundAnimatePacket(entity, 4));
                              }
                           }

                           TensuraEnchantmentHelper.doAdditionalAfterAttack(
                              (ServerLevel)level, target, entity, damageSource, entity.getMainHandItem(), baseDamage
                           );
                           target.invulnerableTime = 40;
                        }
                     }
                  }
               }

               entity.resetFallDistance();
               entity.unRide();
               entity.teleportTo(vec3.x(), vec3.y(), vec3.z());
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            }
         }
      }
   }

   private void removeBlossom(ManasSkillInstance instance, HazyBlossomEntity blossom) {
      if (blossom.tickCount < 10) {
         blossom.discard();
      } else {
         blossom.setRemoveIn(100);
         blossom.setFollowOwner(false);
      }

      instance.getOrCreateTag().putInt("FlowerID", 0);
      instance.markDirty();
   }
}
