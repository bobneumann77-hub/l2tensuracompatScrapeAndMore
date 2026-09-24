package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public interface ISubordinate extends OwnableEntity {
   default EntityGetter level() {
      return null;
   }

   default boolean isTame() {
      return false;
   }

   default void setTame(boolean bl, boolean bl2) {
   }

   @Nullable
   default UUID getOwnerUUID() {
      return null;
   }

   default void setOwnerUUID(@Nullable UUID UUID) {
   }

   default boolean isOwnedBy(LivingEntity livingEntity) {
      return livingEntity == this.getOwner();
   }

   default void tame(Player player) {
      this.setTame(true, true);
      this.setOwnerUUID(player.getUUID());
   }

   default void resetOwner(@Nullable UUID ownerUUID) {
      if (ownerUUID == null) {
         this.setTame(false, false);
         this.setOwnerUUID(null);
      } else {
         this.setTame(true, false);
         this.setOwnerUUID(ownerUUID);
      }

      this.setOrderedToSit(false);
      this.setInSittingPose(false);
      this.setBehaviour(0);
      this.setWandering(false);
   }

   default int getBehaviour() {
      return 0;
   }

   default void setBehaviour(int behaviour) {
   }

   default int getOwnerCommand() {
      return 0;
   }

   default void setOwnerCommand(int command) {
   }

   default boolean isOrderedToSit() {
      return false;
   }

   default void setOrderedToSit(boolean bl) {
   }

   default boolean isInSittingPose() {
      return false;
   }

   default void setInSittingPose(boolean bl) {
   }

   default boolean isWandering() {
      return this.getOwnerCommand() == 1;
   }

   default void setWandering(boolean wandering) {
      this.setOwnerCommand(wandering ? 1 : 0);
   }

   default void setWanderPos(BlockPos pPos) {
   }

   default BlockPos getWanderPos() {
      return BlockPos.ZERO;
   }

   default void cycleBehaviour(Mob subordinate, LivingEntity owner) {
      int behaviour = this.getBehaviour() + 1;

      MutableComponent message = switch (behaviour) {
         case 2 -> Component.translatable("tensura.message.pet.aggressive", new Object[]{subordinate.getDisplayName()});
         case 3 -> {
            SubordinateHelper.removeTarget(subordinate);
            yield Component.translatable("tensura.message.pet.protect", new Object[]{subordinate.getDisplayName()});
         }
         case 4 -> {
            behaviour = 0;
            SubordinateHelper.removeTarget(subordinate);
            yield Component.translatable("tensura.message.pet.neutral", new Object[]{subordinate.getDisplayName()});
         }
         default -> {
            SubordinateHelper.removeTarget(subordinate);
            yield Component.translatable("tensura.message.pet.passive", new Object[]{subordinate.getDisplayName()});
         }
      };
      this.setBehaviour(behaviour);
      if (owner instanceof Player player) {
         player.displayClientMessage(message.setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true);
      }
   }

   default void cycleCommands(Mob subordinate, Player player) {
      if (!subordinate.level().isClientSide()) {
         MutableComponent message;
         if (this.isOrderedToSit()) {
            message = Component.translatable("tensura.message.pet.follow", new Object[]{subordinate.getDisplayName()});
            this.setOrderedToSit(false);
            this.setInSittingPose(false);
            this.setWandering(false);
         } else if (!this.isWandering()) {
            message = Component.translatable("tensura.message.pet.wander", new Object[]{subordinate.getDisplayName()});
            SubordinateHelper.removeTarget(subordinate);
            this.setOrderedToSit(false);
            this.setInSittingPose(false);
            this.setWandering(true);
            this.setWanderPos(player.getOnPos().above());
         } else {
            message = Component.translatable("tensura.message.pet.stay", new Object[]{subordinate.getDisplayName()});
            subordinate.getNavigation().stop();
            this.setOrderedToSit(true);
            this.setInSittingPose(true);
            this.setWandering(false);
            SubordinateHelper.removeTarget(subordinate);
         }

         player.displayClientMessage(message.setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true);
      }
   }

   default boolean canAttackDefault(LivingEntity entity) {
      return (!(entity instanceof Player) || entity.level().getDifficulty() != Difficulty.PEACEFUL) && entity.canBeSeenAsEnemy();
   }

   default boolean shouldTarget(LivingEntity entity) {
      return false;
   }

   default boolean shouldTarget(Mob subordinate, LivingEntity target, Predicate<LivingEntity> nonTameCondition) {
      if (!target.isAlive() || target == subordinate) {
         return false;
      }

      if (!this.canAttackDefault(target)) {
         return false;
      }

      if (subordinate.getAttributeValue(Attributes.FOLLOW_RANGE) <= 0.0) {
         return false;
      }

      MobEffectInstance rampage = subordinate.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
      if (rampage != null && rampage.getAmplifier() >= 2) {
         return true;
      }

      if (this.getBehaviour() == 1 || this.isOrderedToSit()) {
         return false;
      }

      if (subordinate.canAttack(target) && !subordinate.isAlliedTo(target)) {
         if (this.isWandering()) {
            double distance = TensuraBehaviourHelper.CONFIG.tamedWanderRadius;
            if (target.distanceToSqr(Vec3.atCenterOf(this.getWanderPos())) > distance * distance && rampage == null) {
               return false;
            }
         }

         if (subordinate.getBrain() != null) {
            Entity lastHurtBy = (Entity)BrainUtils.getMemory(subordinate, MemoryModuleType.HURT_BY_ENTITY);
            if (lastHurtBy == target) {
               return true;
            }
         }

         LivingEntity owner = SubordinateHelper.getSubordinateOwner(subordinate);
         if (owner == null) {
            if (subordinate.getTarget() == target) {
               return true;
            } else if (subordinate instanceof NeutralMob mob && mob.isAngryAt(target)) {
               return true;
            } else if (!this.isTame() && nonTameCondition.test(target)) {
               return true;
            } else if (this.getBehaviour() == 2) {
               return true;
            } else if (rampage == null) {
               return false;
            } else {
               return rampage.getAmplifier() == 0 ? target.getType().equals(EntityType.PLAYER) : target.getType() != subordinate.getType();
            }
         } else if (owner.isAlliedTo(target)) {
            return false;
         } else if (this.getBehaviour() == 2) {
            return true;
         } else if (subordinate.getTarget() == target) {
            return true;
         } else if (subordinate instanceof NeutralMob mob && mob.isAngryAt(target)) {
            return true;
         } else if (owner.getLastAttacker() == target) {
            return true;
         } else if (owner.getLastHurtMob() == target) {
            return true;
         } else if (owner instanceof Mob mob && target == mob.getTarget()) {
            return true;
         } else if (this.getBehaviour() == 3 && target instanceof Mob mob) {
            LivingEntity entity = mob.getTarget();
            return entity == null ? false : subordinate.isAlliedTo(entity) || owner.isAlliedTo(entity);
         } else if (rampage == null) {
            return false;
         } else {
            return rampage.getAmplifier() == 0 ? target.getType().equals(EntityType.PLAYER) : target.getType() != subordinate.getType();
         }
      } else {
         return false;
      }
   }

   default boolean shouldStopTarget(Mob subordinate, LivingEntity target) {
      if (!target.isAlive() || target == subordinate) {
         return true;
      }

      if (!target.hasInfiniteMaterials() && !target.isSpectator()) {
         double range = subordinate.getAttributeValue(Attributes.FOLLOW_RANGE);
         double maxDist = range * 2.0;
         if (subordinate.distanceToSqr(target) > maxDist * maxDist) {
            return true;
         }

         MobEffectInstance rampage = subordinate.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
         if (rampage != null && rampage.getAmplifier() >= 2) {
            return false;
         }

         if (this.getBehaviour() == 1 || this.isOrderedToSit()) {
            return true;
         }

         if (subordinate.isAlliedTo(target)) {
            return true;
         }

         if (!subordinate.canAttack(target)) {
            return true;
         }

         if (this.isWandering()) {
            double distance = TensuraBehaviourHelper.CONFIG.tamedWanderRadius;
            if (target.distanceToSqr(Vec3.atCenterOf(this.getWanderPos())) > distance * distance) {
               return true;
            }
         }

         LivingEntity owner = SubordinateHelper.getSubordinateOwner(subordinate);
         return owner != null ? owner.isAlliedTo(target) : range <= 0.0;
      } else {
         return true;
      }
   }
}
