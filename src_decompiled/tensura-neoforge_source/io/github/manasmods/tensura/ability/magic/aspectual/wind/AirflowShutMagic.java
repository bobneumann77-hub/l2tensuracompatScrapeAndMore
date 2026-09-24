package io.github.manasmods.tensura.ability.magic.aspectual.wind;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.AirJailEntity;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AirflowShutMagic extends AspectualMagic {
   private static final AspectualMagicConfig.AirflowShut CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).AirflowShut;

   public AirflowShutMagic() {
      super(AspectualMagic.AspectualType.WIND);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
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
      return mode == 1 ? "airflow_shut.expand" : super.getModeId(instance, mode);
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
      float size = mode == 0 ? CONFIG.sphereSize : CONFIG.sphereSizeMastered;
      Vec3 targetPos = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY") + 0.2F, tag.getDouble("circleZ"));
      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      MagicCircle.castMagicCircle(size, 25, targetPos, MagicCircleVariant.WIND, entity, instance.getOrCreateTag(), instance, mode, cost);
      if (heldTicks >= castTime) {
         if (heldTicks == castTime + 1) {
            instance.addMasteryPoint(entity);
         }

         BarrierEntity jail = BarrierEntity.getLastingBarrier(
            (EntityType<? extends BarrierEntity>)MiscEntityTypes.AIR_JAIL.get(),
            0.0F,
            size / 1.5F,
            0.0F,
            CONFIG.sphereDuration,
            20.0F,
            targetPos.add(0.0, size / -5.0F, 0.0),
            entity,
            instance,
            mode,
            cost,
            Pair.of((Double)cost.getFirst() / 2.0, (Double)cost.getSecond() / 2.0),
            heldTicks
         );
         if (jail != null) {
            if (jail.getAge() <= 0 && jail instanceof AirJailEntity airJail) {
               airJail.triggerAnim("controller", "start");
               jail.setTickEachHit(CONFIG.castTimeHold);
               MobEffectInstance silence = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.SILENCE), CONFIG.silenceDuration, CONFIG.silenceLevel - 1, true, false, true
               );
               jail.setMobEffect(silence);
            }

            return (heldTicks - castTime) % CONFIG.castTimeHold != 0 || !this.isOutOfEnergy(entity, instance, (double)0.0, (double)CONFIG.magiculeCostHold);
         } else {
            return true;
         }
      } else {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      }
   }
}
