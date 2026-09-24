package io.github.manasmods.tensura.ability.magic.spiritual.water;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.AcidRainEntity;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AcidRainMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.AcidRain CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).AcidRain;

   public AcidRainMagic() {
      super(Element.WATER, SpiritualMagic.SpiritLevel.MEDIUM);
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
      double radius = instance.isMastered(entity) ? CONFIG.rainRadiusMastered : CONFIG.rainRadius;
      Vec3 offset = new Vec3(0.0, radius * 2.0, 0.0);
      MagicCircle.castMagicCircle(
         (float)radius,
         25,
         MagicCircleVariant.WATER,
         entity,
         instance.getOrCreateTag(),
         0.0F,
         offset.add(0.0, 2.0, 0.0),
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
      if (heldTicks >= castTime) {
         if (WaterMagic.isWaterEvaporated(entity, level)) {
            return false;
         }

         if (heldTicks == castTime + 1) {
            instance.addMasteryPoint(entity);
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         }

         Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
         BarrierEntity.spawnLastingBarrier(
            (EntityType<? extends BarrierEntity>)MiscEntityTypes.ACID_RAIN.get(),
            CONFIG.rainDamage,
            (float)radius,
            0.0F,
            220,
            20.0F,
            entity.position().add(offset),
            entity,
            instance,
            mode,
            cost,
            Pair.of((Double)cost.getFirst() / 3.0, (Double)cost.getSecond() / 3.0),
            heldTicks
         );
         int tick = heldTicks - castTime;
         int holdDuration = CONFIG.rainDuration;
         this.renderRemainingTime(entity, tick, holdDuration);
         if (tick >= holdDuration) {
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            return false;
         } else {
            return true;
         }
      } else {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      int id = tag.getInt("BarrierID");
      if (id != 0) {
         if (!instance.onCoolDown(mode)) {
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         }

         if (entity.level().getEntity(id) instanceof AcidRainEntity rain) {
            rain.setFollowOwner(false);
         } else {
            tag.putInt("BarrierID", 0);
            instance.markDirty();
         }
      }
   }
}
