package io.github.manasmods.tensura.ability.magic.spiritual.fire;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FlareCircleMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.FlareCircle CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).FlareCircle;

   public FlareCircleMagic() {
      super(Element.FLAME, SpiritualMagic.SpiritLevel.GREATER);
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
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      Vec3 targetPos = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY") + 0.2F, tag.getDouble("circleZ"));
      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      MagicCircle.castMagicCircle(5.0F, 30, targetPos, MagicCircleVariant.FLAME, entity, instance.getOrCreateTag(), instance, mode, cost);
      if (heldTicks >= castTime) {
         if (heldTicks == castTime + 1) {
            instance.addMasteryPoint(entity);
         }

         BarrierEntity.spawnLastingBarrier(
            (EntityType<? extends BarrierEntity>)MiscEntityTypes.FLARE_CIRCLE.get(),
            CONFIG.flareDamage,
            CONFIG.flareRadius,
            CONFIG.flareHeight,
            50,
            entity.getMaxHealth() / 2.0F,
            targetPos,
            entity,
            instance,
            mode,
            cost,
            Pair.of((Double)cost.getFirst() / 2.0, (Double)cost.getSecond() / 2.0),
            heldTicks
         );
         level.playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
         int tick = heldTicks - castTime;
         int holdDuration = instance.isMastered(entity) ? CONFIG.flareDurationMastered : CONFIG.flareDuration;
         this.renderRemainingTime(entity, tick, holdDuration);
         return tick < holdDuration;
      } else {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      }
   }
}
