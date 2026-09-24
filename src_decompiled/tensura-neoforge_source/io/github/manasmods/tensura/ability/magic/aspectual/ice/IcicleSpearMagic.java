package io.github.manasmods.tensura.ability.magic.aspectual.ice;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.spike.IcicleSpikeEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class IcicleSpearMagic extends AspectualMagic {
   private static final AspectualMagicConfig.IcicleSpear CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).IcicleSpear;

   public IcicleSpearMagic() {
      super(AspectualMagic.AspectualType.ICE);
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
         Level level = entity.level();
         Entity targetingEntity = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
         Vec3 pos;
         if (targetingEntity != null && targetingEntity.onGround() && level.getBlockState(targetingEntity.getOnPos().below()).isSolid()) {
            pos = targetingEntity.position();
         } else {
            BlockHitResult targetPos = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, 20.0);
            pos = targetPos.getLocation();
            if (level.getBlockState(targetPos.getBlockPos()).canBeReplaced()) {
               pos = pos.add(0.0, -1.0, 0.0);
               if (!level.getBlockState(targetPos.getBlockPos().below(2)).isSolid() && !level.getBlockState(targetPos.getBlockPos().below(3)).isSolid()) {
                  pos = null;
               }
            } else if (!level.getBlockState(targetPos.getBlockPos().below()).isSolid() && !level.getBlockState(targetPos.getBlockPos().below(2)).isSolid()) {
               pos = null;
            }
         }

         CompoundTag tag = instance.getOrCreateTag();
         if (pos == null) {
            tag.putDouble("icicleX", 0.0);
            tag.putDouble("icicleY", 0.0);
            tag.putDouble("icicleZ", 0.0);
         } else {
            tag.putDouble("icicleX", pos.x);
            tag.putDouble("icicleY", pos.y);
            tag.putDouble("icicleZ", pos.z);
         }

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

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      CompoundTag tag = instance.getOrCreateTag();
      Vec3 targetPos = new Vec3(tag.getDouble("icicleX"), tag.getDouble("icicleY"), tag.getDouble("icicleZ"));
      if (targetPos.equals(Vec3.ZERO)) {
         return false;
      }

      instance.markDirty();
      this.applyCastingVisual(instance, entity, heldTicks, mode);
      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      MagicCircle.castMagicCircle(
         2.0F * (instance.isMastered(entity) ? CONFIG.icicleRadiusMastered : CONFIG.icicleRadius),
         30,
         targetPos.add(0.0, 0.3, 0.0),
         MagicCircleVariant.ICE,
         entity,
         instance.getOrCreateTag(),
         instance,
         mode,
         cost
      );
      return true;
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         Vec3 targetPos = new Vec3(tag.getDouble("icicleX"), tag.getDouble("icicleY"), tag.getDouble("icicleZ"));
         if (!targetPos.equals(Vec3.ZERO)) {
            if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               IcicleSpikeEntity spike = new IcicleSpikeEntity(entity.level(), entity);
               spike.setSkill(entity, instance, this, mode);
               spike.setSecondaryDamage(CONFIG.magicDamage);
               float radius = instance.isMastered(entity) ? CONFIG.icicleRadiusMastered : CONFIG.icicleRadius;
               spike.setSize(1.0F + radius);
               spike.setPos(targetPos.add(0.0, 0.1, 0.0));
               entity.level().addFreshEntity(spike);
               spike.triggerAnim("controller", "start");
               EffectStorage.setCameraShake(spike, radius * 4.0F, 0.05F, 10);
               this.spawnSmallSpikes(instance, entity, mode, targetPos, radius);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_ICE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               entity.level()
                  .playSound(
                     null, spike.getX(), spike.getY(), spike.getZ(), (SoundEvent)TensuraSoundEvents.CAST_ICE.get(), TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F
                  );
               entity.swing(InteractionHand.MAIN_HAND, true);
               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            }
         }
      }
   }

   private void spawnSmallSpikes(ManasSkillInstance instance, LivingEntity entity, int mode, Vec3 targetPos, float radius) {
      int amount = 8;
      float smallSize = radius / 2.0F;

      for (int i = 0; i < amount; i++) {
         double angle = (Math.PI * 2) / amount * i;
         double dx = Math.cos(angle) * radius;
         double dz = Math.sin(angle) * radius;
         double x = targetPos.x() + dx;
         double y = targetPos.y();
         double z = targetPos.z() + dz;
         IcicleSpikeEntity spike = new IcicleSpikeEntity(entity.level(), entity);
         spike.setSkill(entity, instance, this, mode);
         spike.setSecondaryDamage(CONFIG.magicDamage);
         spike.setSize(smallSize);
         spike.setPos(x, y, z);
         spike.setYaw(-15.0F);
         spike.setPitch(ObjectSelectionHelper.getYRotFromVector(spike.position().subtract(targetPos).normalize()));
         entity.level().addFreshEntity(spike);
         spike.triggerAnim("controller", "start");
      }
   }
}
