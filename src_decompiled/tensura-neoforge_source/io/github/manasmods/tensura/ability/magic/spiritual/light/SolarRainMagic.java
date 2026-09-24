package io.github.manasmods.tensura.ability.magic.spiritual.light;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.LightArrowProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SolarRainMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.SolarRain CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).SolarRain;

   public SolarRainMagic() {
      super(Element.LIGHT, SpiritualMagic.SpiritLevel.GREATER);
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
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            4.0F,
            25,
            MagicCircleVariant.LIGHT,
            entity,
            instance.getOrCreateTag(),
            -1.0F,
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
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            double distance = instance.isMastered(entity) ? CONFIG.rangeMastered : CONFIG.range;
            Entity target = ObjectSelectionHelper.getTargetingEntity(entity, distance, false, true);
            Vec3 pos;
            if (target != null) {
               pos = target.getEyePosition();
            } else {
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, distance);
               pos = result.getLocation().add(0.0, 0.5, 0.0);
            }

            if (instance.isMastered(entity)) {
               this.spawnLightArrows(instance, entity, pos, CONFIG.arrowNumber, 2.0, mode);
               this.spawnLightArrows(instance, entity, pos, CONFIG.arrowNumberMastered - CONFIG.arrowNumber, 3.0, mode);
            } else {
               this.spawnLightArrows(instance, entity, pos, CONFIG.arrowNumber, 2.5, mode);
            }

            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_LIGHT.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      }
   }

   private void spawnLightArrows(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, int arrowAmount, double distance, int mode) {
      int arrowRot = 360 / arrowAmount;

      for (int i = 0; i < arrowAmount; i++) {
         Vec3 arrowPos = entity.getEyePosition()
            .add(
               new Vec3(0.0, distance, 0.0)
                  .zRot((arrowRot * i - arrowRot / 2.0F) * (float) (Math.PI / 180.0))
                  .xRot(-entity.getXRot() * (float) (Math.PI / 180.0))
                  .yRot(-entity.getYRot() * (float) (Math.PI / 180.0))
            );
         LightArrowProjectile arrow = new LightArrowProjectile(entity.level(), entity);
         arrow.setSpeed(2.0F);
         arrow.setPos(arrowPos);
         arrow.shootFromRot(pos.subtract(arrowPos).normalize());
         arrow.setLife(50);
         arrow.setDamage(CONFIG.arrowDamage);
         arrow.setPiercingBlock(true);
         arrow.setIgnoreInvulnerabilityOnHit(true);
         arrow.setSkill(entity, instance, this, mode);
         entity.level().addFreshEntity(arrow);
      }
   }
}
