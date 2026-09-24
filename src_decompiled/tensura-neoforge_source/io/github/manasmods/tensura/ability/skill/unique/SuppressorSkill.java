package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import io.github.manasmods.tensura.ability.subclass.ISpatialMovement;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SuppressorSkill extends Skill implements ISpatialMovement {
   private static final UniqueSkillConfig.Suppressor CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Suppressor;
   protected static final ResourceLocation ACCELERATION = ResourceLocation.fromNamespaceAndPath("tensura", "suppressor");

   public SuppressorSkill() {
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
         case 0 -> "suppressor.blockade";
         case 1 -> "suppressor.swap";
         case 2 -> "suppressor.motion";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public int getSpatialMovementModeIndex() {
      return 2;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCostBlockade;
         case 1 -> CONFIG.magiculeCostSwap;
         default -> 0.0;
      };
   }

   @Override
   public double getWarpCost(double distance) {
      return CONFIG.magiculeCostMotion * distance;
   }

   @Override
   public double getPortalCost(double distance) {
      return CONFIG.magiculeCostPortal * distance;
   }

   @Override
   public int getWarpChargeTick(ManasSkillInstance instance, LivingEntity entity) {
      return CONFIG.warpChargeTick;
   }

   @Override
   public int getWarpCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity) ? CONFIG.motionCooldownMastered : CONFIG.motionCooldown;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getMastery() >= 0.0;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         switch (mode) {
            case 0:
               entity.swing(InteractionHand.MAIN_HAND, true);
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
               List<LivingEntity> list = level.getEntitiesOfClass(
                  LivingEntity.class, entity.getBoundingBox().inflate(25.0), living -> !living.is(entity) && living.isAlive() && !entity.isAlliedTo(living)
               );
               if (list.isEmpty()) {
                  return;
               }

               for (LivingEntity target : list) {
                  MobEffectInstance blockade = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.SPATIAL_BLOCKADE), CONFIG.blockadeDuration, 0, true, false, true
                  );
                  TensuraMobEffect.addEffect(target, blockade, entity, this, mode);
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.PORTAL, 1.0);
               }
               break;
            case 1:
               if (SkillUtils.shouldCancelTeleportation(entity)) {
                  if (entity instanceof Player player) {
                     player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
                  }

                  return;
               }

               Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.swapRange, 0.2, false, false);
               if (target == null) {
                  if (entity instanceof Player player) {
                     player.displayClientMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED), true);
                  }

                  return;
               }

               if (target.getType().is(TensuraEntityTags.NO_FORCED_WARP)) {
                  if (entity instanceof Player player) {
                     player.displayClientMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED), true);
                  }

                  return;
               }

               Changeable<Vec3> userPosition = Changeable.of(new Vec3(entity.position().x(), entity.position().y(), entity.position().z()));
               if (((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
                  .transmission(target, entity, userPosition, WarpPoint.TransmissionType.ABILITY)
                  .isFalse()) {
                  return;
               }

               Changeable<Vec3> targetPosition = Changeable.of(new Vec3(target.position().x(), target.position().y(), target.position().z()));
               if (((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
                  .transmission(entity, entity, targetPosition, WarpPoint.TransmissionType.ABILITY)
                  .isFalse()) {
                  return;
               }

               target.resetFallDistance();
               target.unRide();
               target.teleportTo(((Vec3)userPosition.get()).x(), ((Vec3)userPosition.get()).y(), ((Vec3)userPosition.get()).z());
               target.hurtMarked = true;
               entity.resetFallDistance();
               entity.unRide();
               entity.teleportTo(((Vec3)targetPosition.get()).x(), ((Vec3)targetPosition.get()).y(), ((Vec3)targetPosition.get()).z());
               entity.hurtMarked = true;
               instance.addMasteryPoint(entity);
               entity.swing(InteractionHand.MAIN_HAND, true);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.PORTAL, 1.0);
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.PORTAL, 1.0);
               level.playSound(
                  null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
               break;
            case 2:
               if (SkillUtils.shouldCancelTeleportation(entity)) {
                  if (entity instanceof Player player) {
                     player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
                  }

                  return;
               }

               if (entity.isShiftKeyDown()) {
                  if (entity instanceof ServerPlayer player) {
                     this.openSpatialMovementMenu(player, this);
                  }

                  return;
               }

               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(
                  level, entity, Fluid.NONE, instance.isMastered(entity) ? CONFIG.motionRangeMastered : CONFIG.motionRange
               );
               BlockPos pos = result.getBlockPos().relative(result.getDirection());
               if (level.getBlockState(pos).is(TensuraBlockTags.SKILL_NOT_TELEPORTABLE)) {
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

               if (EnergyHelper.isOutOfEnergy(entity, 0.0, CONFIG.magiculeCostMotion * Math.sqrt(entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ())))) {
                  return;
               }

               ISpatialMovement.warp(entity, pos.getCenter().x(), pos.getY(), pos.getCenter().z());
               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.motionCooldownMastered : CONFIG.motionCooldown, mode);
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, true);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, ACCELERATION, false);
   }
}
