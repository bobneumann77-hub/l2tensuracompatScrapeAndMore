package io.github.manasmods.tensura.ability.magic.aspectual.fire;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.FireJailEntity;
import io.github.manasmods.tensura.entity.magic.field.cloud.FireStormCloud;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FireStormMagic extends AspectualMagic {
   private static final AspectualMagicConfig.FireStorm CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).FireStorm;

   public FireStormMagic() {
      super(AspectualMagic.AspectualType.FIRE);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryHigh;
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
      return mode == 1 ? "fire_storm.condensed" : "fire_storm.spread";
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 1) {
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
            tag.putDouble("circleX", pos.x);
            tag.putDouble("circleY", pos.y);
            tag.putDouble("circleZ", pos.z);
            instance.markDirty();
         }
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (mode == 0 && castTime > 0) {
         Vec3 targetPos = ObjectSelectionHelper.getNearestGround(entity.position(), entity.level(), CONFIG.range + 3.0, entity);
         if (entity.distanceToSqr(targetPos) > CONFIG.range * CONFIG.range) {
            return;
         }

         MagicCircle.castTargetedMagicCircle(
            CONFIG.width,
            25,
            targetPos,
            MagicCircleVariant.FLAME,
            false,
            entity,
            instance.getOrCreateTag(),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (mode == 1) {
         if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
            return false;
         }

         if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
            return false;
         }

         CompoundTag tag = instance.getOrCreateTag();
         Vec3 targetPos = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY"), tag.getDouble("circleZ"));
         if (targetPos.equals(Vec3.ZERO)) {
            return false;
         }

         instance.markDirty();
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
         MagicCircle.castMagicCircle(CONFIG.condensedSize, 25, targetPos, MagicCircleVariant.FLAME, entity, instance.getOrCreateTag(), instance, mode, cost);
         return true;
      } else {
         return super.onHeld(instance, entity, heldTicks, mode);
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (mode == 1) {
            CompoundTag tag = instance.getOrCreateTag();
            Vec3 targetPos = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY"), tag.getDouble("circleZ"));
            if (!targetPos.equals(Vec3.ZERO)) {
               if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  FireJailEntity jail = new FireJailEntity(entity.level(), entity);
                  jail.setSkill(entity, instance, this, mode);
                  jail.setSize(CONFIG.condensedSize);
                  jail.setBurnTicks(100);
                  jail.setDamage(CONFIG.fireCondensedDamage);
                  jail.setSecondaryDamage(CONFIG.magicCondensedDamage);
                  jail.setTickEachHit(CONFIG.condensedInterval);
                  jail.setLife(80 + CONFIG.condensedDuration);
                  jail.setPos(targetPos.add(0.0, 0.2 + jail.getSize() / -5.0F, 0.0));
                  entity.level().addFreshEntity(jail);
                  jail.triggerAnim("controller", "start");
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
                  entity.level()
                     .playSound(
                        null, jail.getX(), jail.getY(), jail.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F
                     );
                  entity.swing(InteractionHand.MAIN_HAND, true);
               }
            }
         } else {
            Vec3 targetPos = ObjectSelectionHelper.getNearestGround(entity.position(), entity.level(), CONFIG.range + 3.0, entity);
            if (entity.distanceToSqr(targetPos) > CONFIG.range * CONFIG.range) {
               entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.location").withStyle(ChatFormatting.RED));
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               FireStormCloud cloud = new FireStormCloud(entity.level(), entity);
               cloud.setSize(CONFIG.width);
               cloud.setHeight(CONFIG.height);
               cloud.setLife(CONFIG.stormDuration);
               cloud.setTickEachHit(CONFIG.damageInterval);
               cloud.setTickEachSurge(CONFIG.surgeInterval);
               cloud.setDamage(CONFIG.fireDamage);
               cloud.setSecondaryDamage(CONFIG.magicDamage);
               cloud.setSurgeDamage(CONFIG.fireSurgeDamage);
               cloud.setSecondarySurgeDamage(CONFIG.magicSurgeDamage);
               cloud.setBurnTicks(100);
               cloud.setSkill(entity, instance, this, mode);
               cloud.setPos(targetPos);
               entity.level().addFreshEntity(cloud);
               instance.addMasteryPoint(entity);
               entity.swing(InteractionHand.MAIN_HAND, true);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }
         }
      }
   }
}
