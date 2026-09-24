package io.github.manasmods.tensura.ability.magic.aspectual.water;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.WaterBladeProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class WaterCutterMagic extends AspectualMagic {
   private static final AspectualMagicConfig.WaterCutter CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).WaterCutter;

   public WaterCutterMagic() {
      super(AspectualMagic.AspectualType.WATER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? (instance.isMastered(entity) ? 1 : -1) : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return mode == 1 ? "water_cutter.repeat" : super.getModeId(instance, mode);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 1 ? CONFIG.magiculeCostRepeat : CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      if (mode == 1) {
         if (heldTicks % 4 != 0 && heldTicks < castTime) {
            return;
         }

         int cast = CONFIG.castTimeRepeat;
         String sec = heldTicks >= cast ? SkillUtils.ROUND_DOUBLE.format(cast / 20.0F) : SkillUtils.ROUND_DOUBLE.format(heldTicks / 20.0F);
         entity.displayClientMessage(
            Component.translatable("tensura.magic.cast_time.max", new Object[]{sec, SkillUtils.ROUND_DOUBLE.format(cast / 20.0F)})
               .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
            true
         );
      } else {
         super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
         if (castTime > 1) {
            MagicCircle.castMagicCircle(
               1.0F,
               25,
               MagicCircleVariant.WATER,
               entity,
               instance.getOrCreateTag(),
               0.75F,
               Vec3.ZERO,
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
         }
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode == 0) {
         return super.onHeld(instance, entity, heldTicks, mode);
      }

      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      if (heldTicks > 0 && heldTicks % CONFIG.castTimeRepeat == 0) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         this.shootWaterBlade(instance, entity, mode);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         entity.swing(InteractionHand.MAIN_HAND, true);
      }

      this.applyCastingVisual(instance, entity, heldTicks, mode);
      return true;
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 0) {
         if (heldTicks >= this.getCastingTime(instance, entity)) {
            if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               this.shootWaterBlade(instance, entity, mode);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               entity.swing(InteractionHand.MAIN_HAND, true);
               instance.addMasteryPoint(entity);
            }
         }
      }
   }

   private void shootWaterBlade(ManasSkillInstance instance, LivingEntity entity, int mode) {
      WaterBladeProjectile blade = new WaterBladeProjectile(entity.level(), entity);
      blade.setSpeed(4.0F);
      blade.setSecondaryDamage(CONFIG.magicDamage);
      blade.setBurnTicks(-1);
      blade.setSkill(entity, instance, this, mode);
      blade.setPos(entity.getEyePosition().add(0.0, -0.25, 0.0).add(entity.getLookAngle().normalize()));
      blade.shootFromRot(entity.getLookAngle());
      entity.level().addFreshEntity(blade);
   }
}
