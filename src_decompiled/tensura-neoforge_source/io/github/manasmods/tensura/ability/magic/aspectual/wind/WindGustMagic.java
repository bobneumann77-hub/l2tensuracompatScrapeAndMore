package io.github.manasmods.tensura.ability.magic.aspectual.wind;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.breath.BreathEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.phys.Vec3;

public class WindGustMagic extends AspectualMagic {
   private static final AspectualMagicConfig.WindGust CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).WindGust;

   public WindGustMagic() {
      super(AspectualMagic.AspectualType.WIND);
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
      return mode == 1 ? "wind_gust.gust" : super.getModeId(instance, mode);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (mode != 0) {
         MagicCircle.castMagicCircle(
            1.0F,
            25,
            MagicCircleVariant.WIND,
            entity,
            instance.getOrCreateTag(),
            1.0F,
            new Vec3(0.0, entity.getEyeHeight() * -0.1, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      } else if (castTime > 1) {
         MagicCircle.castMagicCircle(
            1.0F,
            25,
            MagicCircleVariant.WIND,
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

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      int castTime = this.getCastingTime(instance, entity);
      if (heldTicks >= castTime) {
         BreathEntity.spawnBreathEntity((EntityType<? extends BreathEntity>)MiscEntityTypes.WIND_BREATH.get(), entity, instance, 0.0F, this, mode);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BREATH_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      } else {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
      }

      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode != 0) {
         instance.getOrCreateTag().putInt("BreathEntity", 0);
         instance.markDirty();
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 0) {
         if (heldTicks >= this.getCastingTime(instance, entity)) {
            if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               WindCharge windCharge;
               if (entity instanceof Player player) {
                  windCharge = new WindCharge(player, entity.level(), entity.position().x(), entity.getEyePosition().y(), entity.position().z());
               } else {
                  windCharge = new WindCharge(
                     entity.level(), entity.position().x(), entity.getEyePosition().y(), entity.position().z(), entity.getViewVector(1.0F)
                  );
               }

               windCharge.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 1.5F, 1.0F);
               entity.level().addFreshEntity(windCharge);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               entity.swing(InteractionHand.MAIN_HAND, true);
               instance.addMasteryPoint(entity);
            }
         }
      }
   }
}
