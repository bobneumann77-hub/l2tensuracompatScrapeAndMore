package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ISpatialMovement;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;

public class SpatialMotionSkill extends Skill implements ISpatialMovement {
   private static final ExtraSkillConfig.SpatialMotion CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).SpatialMotion;

   public SpatialMotionSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "spatial_motion.blink";
         case 1 -> "spatial_motion.warp";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public int getSpatialMovementModeIndex() {
      return 1;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public double getWarpCost(double distance) {
      return CONFIG.magiculeCost * distance;
   }

   @Override
   public double getPortalCost(double distance) {
      return CONFIG.magiculeCostPortal * distance;
   }

   @Override
   public int getWarpChargeTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity) ? CONFIG.warpChargeTickMastered : CONFIG.warpChargeTick;
   }

   @Override
   public int getWarpCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity) ? CONFIG.warpCooldownMastered : CONFIG.warpCooldown;
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0)) {
         if (entity instanceof ServerPlayer player) {
            ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
            data.setMaxWarpPoints(data.getMaxWarpPoints() + 2);
            data.markDirty();
         }
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      if (!(instance.getMastery() < 0.0)) {
         if (entity instanceof ServerPlayer player) {
            ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
            data.setMaxWarpPoints(Math.max(data.getMaxWarpPoints() - 2, 1));
            data.removeWarpPointOverMax(2);
            data.markDirty();
         }
      }
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         SkillHelper.learnSkill(entity, ((SpatialManipulationSkill)ExtraSkills.SPATIAL_MANIPULATION.get()).createLearningInstance(entity));
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (SkillUtils.shouldCancelTeleportation(entity)) {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
         }
      } else {
         Level level = entity.level();
         switch (mode) {
            case 0:
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(
                  level, entity, Fluid.NONE, instance.isMastered(entity) ? CONFIG.blinkRangeMastered : CONFIG.blinkRange
               );
               BlockPos pos = result.getBlockPos().relative(result.getDirection());
               if (level.getBlockState(pos).is(TensuraBlockTags.LABYRINTH_BLOCKS)) {
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

               if (EnergyHelper.isOutOfEnergy(entity, instance, mode, (float)Math.sqrt(entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ())))) {
                  return;
               }

               ISpatialMovement.warp(entity, pos.getCenter().x(), pos.getY(), pos.getCenter().z());
               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.blinkCooldownMastered : CONFIG.blinkCooldown, mode);
               break;
            case 1:
               if (entity instanceof ServerPlayer player) {
                  this.openSpatialMovementMenu(player, this);
               }
         }
      }
   }
}
