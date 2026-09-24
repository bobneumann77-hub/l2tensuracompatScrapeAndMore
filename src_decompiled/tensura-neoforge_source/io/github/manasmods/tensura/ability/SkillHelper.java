package io.github.manasmods.tensura.ability;

import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.SkillEvents.UnlockSkillEvent;
import io.github.manasmods.tensura.config.ability.AbilityConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SkillHelper {
   private static final List<Holder<MobEffect>> MISFIRE_EFFECTS = List.of(
      MobEffects.MOVEMENT_SLOWDOWN,
      MobEffects.CONFUSION,
      MobEffects.WEAKNESS,
      MobEffects.BLINDNESS,
      TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY),
      TensuraMobEffects.getReference(TensuraMobEffects.INSANITY),
      TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS)
   );

   public static MutableComponent getAcquiringMessage(ManasSkillInstance instance) {
      if (instance.getMastery() < 0.0) {
         return Component.translatable("tensura.skill.learn_available", new Object[]{instance.getChatDisplayName(true)})
            .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN));
      } else {
         return instance.getRemoveTime() != -1
            ? Component.translatable("tensura.skill.acquire_temporary", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.GOLD)
            : Component.translatable("manascore.skill.learn_skill", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.GOLD);
      }
   }

   public static boolean learnSkill(LivingEntity entity, ManasSkill skill) {
      return learnSkill(entity, skill, -1);
   }

   public static boolean learnSkill(LivingEntity entity, ManasSkill skill, int removeTime) {
      return learnSkill(entity, skill, removeTime, Component.empty());
   }

   public static boolean learnSkill(LivingEntity entity, ManasSkill skill, int removeTime, @Nullable MutableComponent message) {
      return learnSkill(entity, skill.createDefaultInstance(), removeTime, message);
   }

   public static boolean learnSkill(LivingEntity entity, ManasSkillInstance skill) {
      return learnSkill(entity, skill, skill.getRemoveTime());
   }

   public static boolean learnSkill(LivingEntity entity, ManasSkillInstance skill, int removeTime) {
      return learnSkill(entity, skill, removeTime, Component.empty());
   }

   public static boolean learnSkill(LivingEntity entity, ManasSkillInstance skill, int removeTime, @Nullable MutableComponent message) {
      Skills storage = SkillAPI.getSkillsFrom(entity);
      Optional<ManasSkillInstance> optional = storage.getSkill(skill.getSkill());
      if (optional.isEmpty()) {
         if (removeTime != skill.getRemoveTime()) {
            skill.setRemoveTime(removeTime);
         }

         return storage.learnSkill(
            skill, message == null ? null : (!message.getContents().equals(PlainTextContents.EMPTY) ? message : getAcquiringMessage(skill))
         );
      } else {
         ManasSkillInstance instance = optional.get();
         if (instance.toNBT().equals(skill.toNBT())) {
            return false;
         }

         if (instance.getMastery() >= 0.0 && !instance.isTemporarySkill()) {
            return false;
         }

         if (instance.getMastery() >= skill.getMastery()) {
            if (!instance.isTemporarySkill()) {
               return false;
            }

            if (instance.getRemoveTime() == skill.getRemoveTime()) {
               return false;
            }
         }

         ManasSkillInstance clone = ManasSkillInstance.fromNBT(instance.toNBT());
         if (skill.getRemoveTime() != -1 || removeTime != -1) {
            CompoundTag tag = clone.getOrCreateTag();
            tag.putInt("OldRemoval", clone.getRemoveTime());
            tag.putDouble("OldMastery", clone.getMastery());
         }

         CompoundTag tag = skill.getTag();
         if (tag != null && tag.getBoolean("NoMagiculeCost")) {
            clone.getOrCreateTag().putBoolean("NoMagiculeCost", true);
         }

         clone.setRemoveTime(removeTime);
         if (clone.getMastery() < skill.getMastery()) {
            clone.setMastery(skill.getMastery());
         }

         Changeable<MutableComponent> unlockMessage;
         if (message == null) {
            unlockMessage = Changeable.of(null);
         } else {
            unlockMessage = message.getContents().equals(PlainTextContents.EMPTY) ? Changeable.of(getAcquiringMessage(clone)) : Changeable.of(message);
         }

         EventResult result = ((UnlockSkillEvent)SkillEvents.UNLOCK_SKILL.invoker()).unlockSkill(clone, entity, unlockMessage);
         if (result.isFalse()) {
            return false;
         }

         if (unlockMessage.isPresent()) {
            entity.sendSystemMessage((Component)unlockMessage.get());
         }

         instance.deserialize(clone.toNBT());
         if (instance.getMastery() >= 0.0) {
            instance.onLearnSkill(entity);
         }

         instance.markDirty();
         skill.markDirty();
         storage.markDirty();
         return true;
      }
   }

   public static boolean applyLearningPenalty(LivingEntity entity) {
      AbilityConfig.Learning learning = TensuraSkill.BASE_CONFIG.Learning;
      if (entity.getRandom().nextFloat() > learning.failingPenaltyChance) {
         return false;
      }

      int level = learning.failingPenaltyLevel - 1;
      if (level < 0) {
         return false;
      }

      Holder<MobEffect> effect = MISFIRE_EFFECTS.get(entity.getRandom().nextInt(MISFIRE_EFFECTS.size()));
      entity.addEffect(
         new MobEffectInstance(effect, ((MobEffect)effect.value()).isInstantenous() ? 1 : learning.failingPenaltyDuration, level, true, false, true)
      );
      entity.playSound((SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get());
      return true;
   }

   public static void riptidePush(LivingEntity entity, float riptideLevel) {
      riptidePushVehicle(entity, entity, riptideLevel);
   }

   public static void riptidePushVehicle(Entity vehicle, LivingEntity rider, float riptideLevel) {
      float f7 = rider.getYHeadRot();
      float f = rider.getXRot();
      float f1 = -Mth.sin(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f * (float) (Math.PI / 180.0));
      float f2 = -Mth.sin(f * (float) (Math.PI / 180.0));
      float f3 = Mth.cos(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f * (float) (Math.PI / 180.0));
      float f4 = Mth.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
      float f5 = 3.0F * ((1.0F + riptideLevel) / 4.0F);
      f1 *= f5 / f4;
      f2 *= f5 / f4;
      f3 *= f5 / f4;
      vehicle.push(f1, f2, f3);
   }

   public static void knockBack(LivingEntity user, LivingEntity target, float strength) {
      if (!target.getType().is(TensuraEntityTags.NO_FORCED_MOVE)) {
         if (!target.hasInfiniteMaterials()) {
            double d1 = user.getX() - target.getX();

            double d0;
            for (d0 = user.getZ() - target.getZ(); d1 * d1 + d0 * d0 < 1.0E-4; d0 = (Math.random() - Math.random()) * 0.01) {
               d1 = (Math.random() - Math.random()) * 0.01;
            }

            target.knockback(strength, d1, d0);
            target.hurtMarked = true;
         }
      }
   }

   public static void knockBack(Entity entity, Vec3 knockVec, double pushMultiplier, double resistanceNegate, double flyUpPower) {
      knockBack(entity, null, null, knockVec, pushMultiplier, resistanceNegate, flyUpPower);
   }

   public static void knockBack(
      Entity entity,
      @Nullable Entity cause,
      @Nullable ManasSkillInstance skill,
      Vec3 knockVec,
      double pushMultiplier,
      double resistanceNegate,
      double flyUpPower
   ) {
      if (!entity.getType().is(TensuraEntityTags.NO_FORCED_MOVE)) {
         double resistance = entity instanceof LivingEntity living
            ? Mth.clamp(1.0, 0.0, 1.0 - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) + resistanceNegate)
            : 0.0;
         double len = knockVec.length();
         if (!(len < 1.0E-4)) {
            double scale = pushMultiplier * resistance / len;
            double vx = knockVec.x * scale;
            double vy = knockVec.y * scale;
            double vz = knockVec.z * scale;
            if (!(vx * vx + vy * vy + vz * vz <= 0.0)) {
               Changeable<Vec3> changeable = Changeable.of(new Vec3(vx, flyUpPower + vy / 3.0, vz));
               if (!((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker())
                  .move(entity, cause, skill, changeable)
                  .isFalse()) {
                  entity.push((Vec3)changeable.get());
               }
            }
         }
      }
   }

   public static void pushBackFromPos(BlockPos pos, Entity target, float strength) {
      pushBackFromPos(pos, target, null, null, strength);
   }

   public static void pushBackFromPos(BlockPos pos, Entity target, @Nullable Entity cause, @Nullable ManasSkillInstance skill, float strength) {
      pushBackFromPos(pos, target, cause, skill, strength, 0.0);
   }

   public static void pushBackFromPos(
      BlockPos pos, Entity target, @Nullable Entity cause, @Nullable ManasSkillInstance skill, float strength, double resistanceNegate
   ) {
      if (!(target instanceof Player player && player.isCreative()) && !target.isSpectator()) {
         RandomSource random = target.getRandom();
         double e = pos.getX() - target.getX();

         double f;
         for (f = pos.getZ() - target.getZ(); e * e + f * f < 1.0E-4; f = (random.nextDouble() - random.nextDouble()) * 0.01) {
            e = (random.nextDouble() - random.nextDouble()) * 0.01;
         }

         Vec3 vec3 = target.getDeltaMovement();
         if (target instanceof LivingEntity living) {
            living.hasImpulse = true;
            vec3 = living.getDeltaMovement();

            while (e * e + f * f < 1.0E-5F) {
               e = (random.nextDouble() - random.nextDouble()) * 0.01;
               f = (random.nextDouble() - random.nextDouble()) * 0.01;
            }

            double resistance = Mth.clamp(1.0, 0.0, 1.0 - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) + resistanceNegate);
            double horizLen = Math.sqrt(e * e + f * f);
            double s = strength * resistance / horizLen;
            Changeable<Vec3> changeable = Changeable.of(
               new Vec3(
                  vec3.x / 2.0 - e * s, living.onGround() ? Math.min(0.4 * resistance, vec3.y / 2.0 + strength * resistance) : vec3.y, vec3.z / 2.0 - f * s
               )
            );
            if (((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker()).move(target, cause, skill, changeable).isFalse()) {
               return;
            }

            living.setDeltaMovement((Vec3)changeable.get());
         } else {
            target.hasImpulse = true;
            double horizLen = Math.sqrt(e * e + f * f);
            double s = strength / horizLen;
            Changeable<Vec3> changeable = Changeable.of(
               new Vec3(vec3.x / 2.0 - e * s, target.onGround() ? Math.min(0.4, vec3.y / 2.0 + strength) : vec3.y, vec3.z / 2.0 - f * s)
            );
            if (((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker()).move(target, cause, skill, changeable).isFalse()) {
               return;
            }

            target.setDeltaMovement((Vec3)changeable.get());
         }

         target.hurtMarked = true;
      }
   }

   public static void launchBlock(
      Entity entity,
      Vec3 pos,
      int radius,
      int yRadius,
      float upStrength,
      float pushStrength,
      Predicate<BlockState> statePredicate,
      Predicate<BlockPos> posPredicate
   ) {
      launchBlock(entity, pos, radius, yRadius, upStrength, pushStrength, statePredicate, posPredicate, null);
   }

   public static void launchBlock(
      Entity entity,
      Vec3 pos,
      int radius,
      int yRadius,
      float upStrength,
      float pushStrength,
      Predicate<BlockState> statePredicate,
      Predicate<BlockPos> posPredicate,
      @Nullable ManasSkillInstance instance
   ) {
      launchBlock(entity, entity, pos, radius, yRadius, upStrength, pushStrength, statePredicate, posPredicate, instance);
   }

   public static void launchBlock(
      Entity entity,
      @Nullable Entity causer,
      Vec3 pos,
      int radius,
      int yRadius,
      float upStrength,
      float pushStrength,
      Predicate<BlockState> statePredicate,
      Predicate<BlockPos> posPredicate,
      @Nullable ManasSkillInstance instance
   ) {
      Level level = entity.level();
      if (!level.isClientSide()) {
         int yPos = Mth.floor(pos.y()) - 1;
         int xPos = Mth.floor(pos.x());
         int zPos = Mth.floor(pos.z());
         MutableBlockPos cursor = new MutableBlockPos();
         int radiusSqr = radius * radius;

         for (int j = -radius; j <= radius; j++) {
            for (int k = -radius; k <= radius; k++) {
               for (int i = -yRadius; i <= yRadius; i++) {
                  int newYPos = yPos + i;
                  int newXPos = xPos + j;
                  int newZPos = zPos + k;
                  cursor.set(newXPos, newYPos, newZPos);
                  if (!(cursor.distToCenterSqr(pos) > radiusSqr) && posPredicate.test(cursor)) {
                     BlockState blockState = level.getBlockState(cursor);
                     if (statePredicate.test(blockState)
                        && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                           .grief(instance, level, causer, newXPos, newYPos, newZPos)
                           .isFalse()) {
                        BlockPos blockpos = cursor.immutable();
                        FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, blockpos, blockState);
                        fallingBlock.setDeltaMovement(fallingBlock.getDeltaMovement().add(0.0, upStrength, 0.0));
                        pushBackFromPos(entity.blockPosition(), fallingBlock, pushStrength);
                        fallingBlock.move(MoverType.SELF, fallingBlock.getDeltaMovement());
                        fallingBlock.hurtMarked = true;
                        ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                           .grief(instance, level, causer, newXPos, newYPos, newZPos);
                     }
                  }
               }
            }
         }
      }
   }
}
