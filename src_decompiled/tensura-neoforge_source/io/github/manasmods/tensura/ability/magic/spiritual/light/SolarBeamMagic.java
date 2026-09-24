package io.github.manasmods.tensura.ability.magic.spiritual.light;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.beam.BeamProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SolarBeamMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.SolarBeam CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).SolarBeam;

   public SolarBeamMagic() {
      super(Element.LIGHT, SpiritualMagic.SpiritLevel.MEDIUM);
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

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      instance.getOrCreateTag().putInt("BeamID", 0);
      instance.markDirty();
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      int castTime = this.getCastingTime(instance, entity);
      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > castTime) {
         instance.addMasteryPoint(entity);
      }

      Vec3 circleVec3 = new Vec3(0.0, entity.getBbHeight() * 3.0F / 4.0F - entity.getEyeHeight(), 0.0);
      MagicCircle.castMagicCircle(
         1.5F,
         25,
         MagicCircleVariant.LIGHT,
         entity,
         instance.getOrCreateTag(),
         0.5F,
         circleVec3,
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
      if (heldTicks >= castTime) {
         Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
         BeamProjectile.spawnLastingBeam(
            (EntityType<? extends BeamProjectile>)MiscEntityTypes.SOLAR_BEAM.get(),
            CONFIG.damage,
            1.0F,
            CONFIG.range,
            entity,
            instance,
            mode,
            cost,
            cost,
            heldTicks
         );
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_LIGHT.get(), TensuraSkill.ABILITY_SOUND, 0.8F, 0.5F
            );
      } else {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
      }

      return true;
   }
}
