package io.github.manasmods.tensura.ability.magic.aspectual.water;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import io.github.manasmods.tensura.entity.magic.barrier.WaterJailEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class WaterJailMagic extends AspectualMagic {
   private static final AspectualMagicConfig.WaterJail CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).WaterJail;

   public WaterJailMagic() {
      super(AspectualMagic.AspectualType.WATER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryHigh;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!this.isCastingBlocked(instance, entity)) {
         Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
         Vec3 pos;
         if (target != null) {
            pos = target.position();
         } else {
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, CONFIG.range);
            pos = result.getLocation();
         }

         CompoundTag tag = instance.getOrCreateTag();
         tag.putInt("BarrierID", 0);
         tag.putDouble("circleX", pos.x);
         tag.putDouble("circleY", pos.y);
         tag.putDouble("circleZ", pos.z);
         instance.markDirty();
      }
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
      CompoundTag tag = instance.getOrCreateTag();
      float size = CONFIG.jailSize;
      Vec3 targetPos = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY") + 0.2F, tag.getDouble("circleZ"));
      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      MagicCircle.castMagicCircle(size, 60, targetPos, MagicCircleVariant.WATER, entity, instance.getOrCreateTag(), instance, mode, cost);
      instance.markDirty();
      if (heldTicks >= castTime) {
         if (heldTicks == castTime + 1) {
            instance.addMasteryPoint(entity);
         }

         BarrierEntity jail = BarrierEntity.getLastingBarrier(
            (EntityType<? extends BarrierEntity>)MiscEntityTypes.WATER_JAIL.get(),
            CONFIG.bladeDamage,
            size / 1.5F,
            0.0F,
            80,
            entity.getMaxHealth() / 2.0F,
            targetPos.add(0.0, size / -5.0F, 0.0),
            entity,
            instance,
            mode,
            cost,
            Pair.of((Double)cost.getFirst() / 2.0, (Double)cost.getSecond() / 2.0),
            heldTicks
         );
         if (jail != null) {
            if (jail.getAge() <= 0 && jail instanceof WaterJailEntity waterJail) {
               waterJail.triggerAnim("controller", "start");
               jail.setSecondaryDamage(CONFIG.magicDamage);
               jail.setTickEachHit(CONFIG.castTimeHold);
            }

            if ((heldTicks - castTime) % CONFIG.castTimeHold == 0 && this.isOutOfEnergy(entity, instance, (double)0.0, (double)CONFIG.magiculeCostHold)) {
               return false;
            }
         }

         int tick = heldTicks - castTime;
         int holdDuration = 40 + (instance.isMastered(entity) ? CONFIG.maxHoldMastered : CONFIG.maxHold);
         this.renderRemainingTime(entity, tick, holdDuration);
         return tick < holdDuration;
      } else {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      }
   }
}
