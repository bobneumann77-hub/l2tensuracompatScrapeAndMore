package io.github.manasmods.tensura.ability.magic.spiritual.earth;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EarthStormMagic extends SpiritualMagic {
   public static final SpiritualMagicConfig.EarthStorm CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).EarthStorm;

   public EarthStormMagic() {
      super(Element.EARTH, SpiritualMagic.SpiritLevel.MEDIUM);
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
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      int castTime = this.getCastingTime(instance, entity);
      MagicCircle.castMagicCircle(
         CONFIG.radius,
         25,
         MagicCircleVariant.EARTH,
         entity,
         instance.getOrCreateTag(),
         0.0F,
         Vec3.ZERO,
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
      if (heldTicks >= castTime) {
         if (heldTicks == castTime + 1) {
            instance.addMasteryPoint(entity);
         }

         if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode, 0.16666667F)) {
            return false;
         }

         float radius = CONFIG.radius;
         Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
         BarrierEntity.spawnLastingBarrier(
            (EntityType<? extends BarrierEntity>)MiscEntityTypes.EARTH_STORM.get(),
            0.0F,
            radius,
            0.0F,
            30,
            20.0F,
            entity.position().add(0.0, entity.getBbHeight() / 2.0F - radius, 0.0),
            entity,
            instance,
            mode,
            cost,
            Pair.of((Double)cost.getFirst() / 10.0, (Double)cost.getSecond() / 10.0),
            heldTicks
         );
         int tick = heldTicks - castTime;
         int holdDuration = instance.isMastered(entity) ? CONFIG.stormDurationMastered : CONFIG.stormDuration;
         this.renderRemainingTime(entity, tick, holdDuration);
         return tick < holdDuration;
      } else {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      }
   }
}
