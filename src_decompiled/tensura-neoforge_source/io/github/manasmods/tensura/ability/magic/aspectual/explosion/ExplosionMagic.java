package io.github.manasmods.tensura.ability.magic.aspectual.explosion;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.advancement.AbilityTrigger;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.ExplosionCircle;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.field.MagicExplosion;
import io.github.manasmods.tensura.entity.magic.misc.MagicLandmineEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class ExplosionMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Explosion CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Explosion;

   public ExplosionMagic() {
      super(AspectualMagic.AspectualType.EXPLOSION);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTimeLevel;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryGreat;
   }

   @Override
   public int getCastingTime(ManasSkillInstance instance, LivingEntity entity, int time, boolean chantAnnulment) {
      return instance.getRemoveTime() == -3 ? (int)(time * MAGIC_CONFIG.unlearntCastMultiplier) : time;
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
      return mode == 1 ? "explosion.trap" : "explosion.default";
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 1) {
         MagicLandmineEntity landmine = ObjectSelectionHelper.getTargetingEntity(MagicLandmineEntity.class, entity, CONFIG.range, 0.5, false, false, false);
         if (landmine != null && landmine.getOwner() == entity) {
            if (entity.isShiftKeyDown()) {
               landmine.discard();
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            } else {
               landmine.trigger();
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }

            entity.swing(InteractionHand.MAIN_HAND, true);
            this.resetData(instance);
         } else {
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, CONFIG.rangeTrap);
            if (result.getType() == Type.MISS) {
               this.resetData(instance);
            } else {
               Vec3 pos = result.getLocation();
               CompoundTag tag = instance.getOrCreateTag();
               tag.putDouble("circleX", pos.x);
               tag.putDouble("circleY", pos.y);
               tag.putDouble("circleZ", pos.z);
               tag.putInt("PowerScale", 0);
               instance.markDirty();
            }
         }
      } else {
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
         tag.putInt("PowerScale", 0);
         instance.markDirty();
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player player, int heldTicks, int mode, int castTime) {
      if (heldTicks % 4 == 0 || heldTicks >= castTime) {
         CompoundTag tag = instance.getOrCreateTag();
         double max = mode == 1 ? CONFIG.maxLevelTrap : (instance.isMastered(player) ? CONFIG.maxLevelMastered : CONFIG.maxLevel);
         player.displayClientMessage(
            Component.translatable("tensura.skill.power_scale", new Object[]{tag.getInt("PowerScale") / 10.0})
               .append("/" + max)
               .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
            true
         );
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

      CompoundTag tag = instance.getOrCreateTag();
      Vec3 targetPos = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY"), tag.getDouble("circleZ"));
      if (!targetPos.equals(Vec3.ZERO) && (heldTicks != 0 || !this.isCastingBlocked(instance, entity))) {
         if (mode == 0 && !entity.level().canSeeSky(ObjectSelectionHelper.getBlockPos(targetPos).above())) {
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
            tag.putInt("PowerScale", 0);
            instance.markDirty();
            return false;
         }

         double max = mode == 1 ? CONFIG.maxLevelTrap : (instance.isMastered(entity) ? CONFIG.maxLevelMastered : CONFIG.maxLevel);
         int holdTime = this.getDefaultCastTime() / 10;
         if (heldTicks > 0 && heldTicks % holdTime == 0 && tag.getInt("PowerScale") < max * 10.0) {
            tag.putInt("PowerScale", tag.getInt("PowerScale") + 1);
            if (tag.getInt("PowerScale") % 10 == 0) {
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }

            instance.markDirty();
         }

         float power = tag.getInt("PowerScale") / 10.0F;
         float costMultiplier = power * (power + 1.0F) / 2.0F;
         Pair<Double, Double> cost = Pair.of(
            this.getAuraCost(entity, instance, mode) * costMultiplier, this.getMagiculeCost(entity, instance, mode) * costMultiplier
         );
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         if (mode == 1) {
            MagicCircle.castMagicCircle(
               CONFIG.blastRadius,
               25,
               targetPos.add(0.0, 0.1, 0.0),
               MagicCircleVariant.EXPLOSION,
               true,
               entity,
               instance.getOrCreateTag(),
               instance,
               mode,
               cost
            );
            MagicCircle.castMagicCircle(
               "MagicCircleID2",
               0.75F,
               25,
               MagicCircleVariant.EXPLOSION,
               true,
               entity,
               instance.getOrCreateTag(),
               0.75F,
               0.0F,
               Vec3.ZERO,
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
         } else {
            ExplosionCircle.castMagicCircle(CONFIG.blastRadius, power, 21, targetPos, entity, instance.getOrCreateTag(), instance, mode, cost);
            MagicCircle.castMagicCircle(
               "MagicCircleID2",
               entity.getBbWidth() * 4.0F,
               25,
               MagicCircleVariant.EXPLOSION,
               true,
               entity,
               instance.getOrCreateTag(),
               0.0F,
               0.0F,
               Vec3.ZERO,
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
            MagicCircle.castMagicCircle(
               "MagicCircleID3",
               1.25F,
               25,
               MagicCircleVariant.EXPLOSION,
               true,
               entity,
               instance.getOrCreateTag(),
               1.25F,
               0.0F,
               Vec3.ZERO,
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
         }

         return true;
      } else {
         tag.putInt("PowerScale", 0);
         instance.markDirty();
         return false;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      int level = tag.getInt("PowerScale") / 10;
      Vec3 targetPos = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY"), tag.getDouble("circleZ"));
      if (mode == 1) {
         if (targetPos.equals(Vec3.ZERO)) {
            return;
         }

         if (level < 1) {
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
            this.resetData(instance);
            return;
         }
      } else if (entity.isShiftKeyDown() || level < CONFIG.minLevel) {
         this.stopCircle(entity, tag);
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
         this.resetData(instance);
         return;
      }

      int maxLevel = mode == 1 ? CONFIG.maxLevelTrap : (instance.isMastered(entity) ? CONFIG.maxLevelMastered : CONFIG.maxLevel);
      level = Math.min(level, maxLevel);
      float costMultiplier = level * (level + 1) / 2.0F;
      if (EnergyHelper.isOutOfEnergy(entity, instance, mode, costMultiplier)) {
         this.stopCircle(entity, tag);
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
         this.resetData(instance);
      } else if (mode != 1) {
         MagicExplosion explosion = new MagicExplosion(entity.level(), entity);
         explosion.setSecondaryDamage(CONFIG.magicDamage * level);
         explosion.setSkill(entity, instance, this, mode, costMultiplier);
         explosion.setSize(CONFIG.blastRadius * level);
         explosion.setPos(targetPos.add(0.0, 0.2, 0.0));
         entity.level().addFreshEntity(explosion);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         entity.swing(InteractionHand.MAIN_HAND, true);
         if (level >= CONFIG.maxLevelMastered && entity instanceof ServerPlayer player) {
            ((AbilityTrigger)TensuraCriteriaTriggers.SPECIAL_ACTIVATION.get()).trigger(player, this);
         }

         instance.addMasteryPoint(entity);
         this.stopCircle(entity, tag);
         this.resetData(instance);
      } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         MagicLandmineEntity landmine = new MagicLandmineEntity(entity.level(), targetPos.x(), targetPos.y(), targetPos.z(), entity);
         landmine.setSecondaryDamage(CONFIG.magicDamage * level);
         landmine.setSkill(entity, instance, this, mode);
         landmine.setElementalAttack(true);
         landmine.setExplosionType(MagicLandmineEntity.ExplosionType.MAGIC_FULL);
         landmine.setRadius((int)(CONFIG.blastRadius * level));
         entity.level().addFreshEntity(landmine);
         entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TNT_PRIMED, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         entity.swing(InteractionHand.MAIN_HAND, true);
         this.resetData(instance);
      }
   }

   private void resetData(ManasSkillInstance instance) {
      CompoundTag tag = instance.getOrCreateTag();
      tag.putDouble("circleX", 0.0);
      tag.putDouble("circleY", 0.0);
      tag.putDouble("circleZ", 0.0);
      tag.putInt("PowerScale", 0);
      instance.markDirty();
   }

   private void stopCircle(LivingEntity owner, CompoundTag tag) {
      if (owner.level().getEntity(tag.getInt("MagicCircleID")) instanceof ExplosionCircle circle) {
         circle.triggerAnim("controller", "stop");
         circle.setLife(circle.getAge() + 20);
      }
   }
}
