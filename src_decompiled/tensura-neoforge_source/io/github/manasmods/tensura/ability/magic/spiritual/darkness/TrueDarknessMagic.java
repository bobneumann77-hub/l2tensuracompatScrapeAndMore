package io.github.manasmods.tensura.ability.magic.spiritual.darkness;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.DarkCubeEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TrueDarknessMagic extends SpiritualMagic {
   public static final SpiritualMagicConfig.TrueDarkness CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).TrueDarkness;

   public TrueDarknessMagic() {
      super(Element.DARKNESS, SpiritualMagic.SpiritLevel.GREATER);
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
      instance.getOrCreateTag().putInt("BarrierID", 0);
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
      Level level = entity.level();
      if (heldTicks >= castTime) {
         if (heldTicks == castTime + 1) {
            instance.addMasteryPoint(entity);
         }

         Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
         DarkCubeEntity.spawnTrueCube(
            (EntityType<? extends DarkCubeEntity>)MiscEntityTypes.DARK_CUBE.get(),
            CONFIG.damage,
            CONFIG.radius,
            40,
            true,
            entity.position(),
            entity,
            instance,
            mode,
            cost,
            Pair.of((Double)cost.getFirst() / 10.0, (Double)cost.getSecond() / 10.0),
            heldTicks
         );
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.DARK_RED_LIGHTNING_SPARK.get());
         level.playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_DARK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
         int tick = heldTicks - castTime;
         int holdDuration = instance.isMastered(entity) ? CONFIG.trueDurationMastered : CONFIG.trueDuration;
         this.renderRemainingTime(entity, tick, holdDuration);
         return tick < holdDuration;
      } else {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         MagicCircle.castMagicCircle(
            CONFIG.radius + 1.0F,
            25,
            MagicCircleVariant.DARK,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
         return true;
      }
   }
}
