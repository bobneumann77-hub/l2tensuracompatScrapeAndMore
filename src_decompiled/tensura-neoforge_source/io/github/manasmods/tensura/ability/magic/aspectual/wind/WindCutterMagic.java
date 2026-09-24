package io.github.manasmods.tensura.ability.magic.aspectual.wind;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.WindBladeProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class WindCutterMagic extends AspectualMagic {
   private static final AspectualMagicConfig.WindCutter CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).WindCutter;

   public WindCutterMagic() {
      super(AspectualMagic.AspectualType.WIND);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
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

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            this.shootWindBlade(instance, entity, mode, new Vec3(0.0, -0.25, 0.0));
            if (instance.isMastered(entity)) {
               this.shootWindBlade(
                  instance,
                  entity,
                  mode,
                  new Vec3(1.0, -0.25, 0.0).xRot(-entity.getXRot() * (float) (Math.PI / 180.0)).yRot(-entity.getYRot() * (float) (Math.PI / 180.0))
               );
               this.shootWindBlade(
                  instance,
                  entity,
                  mode,
                  new Vec3(-1.0, -0.25, 0.0).xRot(-entity.getXRot() * (float) (Math.PI / 180.0)).yRot(-entity.getYRot() * (float) (Math.PI / 180.0))
               );
            }

            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
         }
      }
   }

   private void shootWindBlade(ManasSkillInstance instance, LivingEntity entity, int mode, Vec3 offset) {
      WindBladeProjectile blade = new WindBladeProjectile(entity.level(), entity);
      blade.setSpeed(1.5F);
      blade.setDamage(CONFIG.windDamage);
      blade.setSecondaryDamage(CONFIG.magicDamage);
      blade.setKnockForce(2.0F);
      blade.setSkill(entity, instance, this, mode);
      blade.setNoGravity(true);
      blade.setPos(entity.getEyePosition().add(offset).add(entity.getLookAngle().normalize()));
      blade.shootFromRot(entity.getLookAngle());
      entity.level().addFreshEntity(blade);
   }
}
