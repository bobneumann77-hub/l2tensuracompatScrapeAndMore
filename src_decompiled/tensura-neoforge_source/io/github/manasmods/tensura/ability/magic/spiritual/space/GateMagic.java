package io.github.manasmods.tensura.ability.magic.spiritual.space;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.ability.skill.extra.SpatialMotionSkill;
import io.github.manasmods.tensura.ability.subclass.ISpatialMovement;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class GateMagic extends SpiritualMagic implements ISpatialMovement {
   private static final SpiritualMagicConfig.Gate CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Gate;

   public GateMagic() {
      super(Element.SPACE, SpiritualMagic.SpiritLevel.MEDIUM);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public double getPortalCost(double distance) {
      return CONFIG.magiculeCost * distance;
   }

   @Override
   public int getWarpChargeTick(ManasSkillInstance instance, LivingEntity entity) {
      return CONFIG.warpChargeTick;
   }

   @Override
   public int getWarpCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown;
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
         SkillHelper.learnSkill(entity, ((SpatialMotionSkill)ExtraSkills.SPATIAL_MOTION.get()).createLearningInstance(entity));
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            1.0F,
            25,
            MagicCircleVariant.SPACE,
            entity,
            instance.getOrCreateTag(),
            1.0F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   @Override
   public boolean canWarp(ManasSkillInstance instance, LivingEntity entity) {
      return false;
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (SkillUtils.shouldCancelTeleportation(entity)) {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
         }
      } else if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            if (entity instanceof ServerPlayer player) {
               this.openSpatialMovementMenu(player, this);
            }

            entity.swing(InteractionHand.MAIN_HAND, true);
         }
      }
   }
}
