package io.github.manasmods.tensura.util;

import io.github.manasmods.tensura.entity.template.subclass.ILivingPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class SubordinateHelper {
   public static boolean isAlly(LivingEntity entity, LivingEntity target) {
      UUID entityOwner = getSubordinateOwnerUUID(entity);
      UUID targetOwner = getSubordinateOwnerUUID(target);
      if (entityOwner == null && targetOwner == null) {
         return entity.isAlliedTo(target.getTeam());
      } else if (entityOwner != null && entityOwner.equals(targetOwner)) {
         return true;
      } else {
         UUID entityUUID = entity.getUUID();
         UUID targetUUID = target.getUUID();
         if (targetOwner != null && targetOwner.equals(entityUUID)) {
            return true;
         } else if (entityOwner != null && entityOwner.equals(targetUUID)) {
            return true;
         } else if (isSubordinate(target, entity)) {
            return true;
         } else {
            return isSubordinate(entity, target) ? true : entity.isAlliedTo(target.getTeam());
         }
      }
   }

   public static boolean isSubordinate(LivingEntity owner, LivingEntity sub) {
      return isSubordinate(owner, sub, 5);
   }

   public static boolean isSubordinate(LivingEntity owner, LivingEntity sub, IExistence subExistence) {
      if (sub == owner) {
         return false;
      }

      if (Objects.equals(getSubordinateOwnerUUID(subExistence, sub), owner.getUUID())) {
         return true;
      }

      LivingEntity subOwner = getSubordinateOwner(subExistence, sub);
      return subOwner != null && !subOwner.equals(sub) && !subOwner.equals(owner) ? isSubordinate(owner, subOwner, 4) : false;
   }

   private static boolean isSubordinate(LivingEntity owner, LivingEntity sub, int depth) {
      if (depth > 0 && sub != owner) {
         IExistence existence = TensuraStorages.getExistenceFrom(sub);
         if (Objects.equals(getSubordinateOwnerUUID(existence, sub), owner.getUUID())) {
            return true;
         }

         LivingEntity subOwner = getSubordinateOwner(existence, sub);
         return subOwner != null && !subOwner.equals(sub) && !subOwner.equals(owner) ? isSubordinate(owner, subOwner, depth - 1) : false;
      } else {
         return false;
      }
   }

   @Nullable
   public static LivingEntity getSubordinateOwner(LivingEntity sub) {
      return getSubordinateOwner(TensuraStorages.getExistenceFrom(sub), sub);
   }

   @Nullable
   private static LivingEntity getSubordinateOwner(IExistence existence, LivingEntity sub) {
      UUID temporaryOwner = existence.getTemporaryOwner();
      if (temporaryOwner != null) {
         return sub.level().getPlayerByUUID(temporaryOwner);
      } else {
         UUID summoner = existence.getSummoner();
         if (summoner != null && sub.level() instanceof ServerLevel level && level.getEntity(summoner) instanceof LivingEntity summonerEntity) {
            return summonerEntity;
         } else {
            UUID permanentOwner = existence.getPermanentOwner();
            if (permanentOwner != null) {
               return sub.level().getPlayerByUUID(permanentOwner);
            } else {
               return sub instanceof ISubordinate subordinate ? subordinate.getOwner() : null;
            }
         }
      }
   }

   @Nullable
   public static UUID getSubordinateOwnerUUID(LivingEntity sub) {
      return getSubordinateOwnerUUID(TensuraStorages.getExistenceFrom(sub), sub);
   }

   @Nullable
   private static UUID getSubordinateOwnerUUID(IExistence existence, LivingEntity sub) {
      UUID temporaryOwner = existence.getTemporaryOwner();
      if (temporaryOwner != null) {
         return temporaryOwner;
      } else {
         UUID summoner = existence.getSummoner();
         if (summoner != null) {
            return summoner;
         } else {
            UUID permanentOwner = existence.getPermanentOwner();
            if (permanentOwner != null) {
               return permanentOwner;
            } else {
               return sub instanceof ISubordinate subordinate ? subordinate.getOwnerUUID() : null;
            }
         }
      }
   }

   public static boolean isOrderedToStay(LivingEntity sub) {
      return sub instanceof ISubordinate subordinate ? subordinate.isOrderedToSit() : false;
   }

   public static void setStay(Mob living) {
      if (ILivingPartEntity.checkForHead(living) instanceof ISubordinate subordinate) {
         living.getNavigation().stop();
         removeTarget(living);
         subordinate.setWandering(false);
         subordinate.setOrderedToSit(true);
         subordinate.setInSittingPose(true);
      }
   }

   public static void setFollow(Mob living) {
      if (ILivingPartEntity.checkForHead(living) instanceof ISubordinate subordinate) {
         subordinate.setWandering(false);
         subordinate.setOrderedToSit(false);
         subordinate.setInSittingPose(false);
      }
   }

   public static void setWander(Mob living) {
      if (ILivingPartEntity.checkForHead(living) instanceof ISubordinate subordinate) {
         removeTarget(living);
         subordinate.setWandering(true);
         if (subordinate.getOwner() != null) {
            subordinate.setWanderPos(subordinate.getOwner().getOnPos().above());
         }

         subordinate.setOrderedToSit(false);
         subordinate.setInSittingPose(false);
      }
   }

   public static void setNeutral(Mob entity) {
      if (ILivingPartEntity.checkForHead(entity) instanceof ISubordinate tamable) {
         removeTarget(entity);
         tamable.setBehaviour(0);
      }
   }

   public static void setPassive(Mob mob) {
      if (ILivingPartEntity.checkForHead(mob) instanceof ISubordinate tamable) {
         removeTarget(mob);
         tamable.setBehaviour(1);
      }
   }

   public static void setAggressive(LivingEntity entity) {
      if (ILivingPartEntity.checkForHead(entity) instanceof ISubordinate subordinate) {
         subordinate.setBehaviour(2);
      }
   }

   public static void setProtect(Mob entity) {
      if (ILivingPartEntity.checkForHead(entity) instanceof ISubordinate tamable) {
         removeTarget(entity);
         tamable.setBehaviour(3);
      }
   }

   public static void removeTarget(Mob mob) {
      mob.setTarget(null);
      if (mob instanceof NeutralMob entity) {
         entity.forgetCurrentTargetAndRefreshUniversalAnger();
      }

      BrainUtils.clearMemories(mob, new MemoryModuleType[]{MemoryModuleType.WALK_TARGET});
      BrainUtils.clearMemories(mob, new MemoryModuleType[]{MemoryModuleType.HURT_BY_ENTITY});
      BrainUtils.clearMemories(mob, new MemoryModuleType[]{MemoryModuleType.ATTACK_TARGET});
      BrainUtils.clearMemories(mob, new MemoryModuleType[]{MemoryModuleType.ANGRY_AT});
      mob.setLastHurtMob(null);
      mob.setLastHurtByMob(null);
      mob.targetSelector.getAvailableGoals().forEach(WrappedGoal::stop);
   }

   public static void removeTarget(LivingEntity entity) {
      if (entity instanceof NeutralMob mob) {
         mob.forgetCurrentTargetAndRefreshUniversalAnger();
      }

      if (entity instanceof Mob mob) {
         mob.setTarget(null);
         mob.targetSelector.getAvailableGoals().forEach(WrappedGoal::stop);
      }

      entity.setLastHurtMob(null);
      entity.setLastHurtByMob(null);
      BrainUtils.clearMemories(entity, new MemoryModuleType[]{MemoryModuleType.WALK_TARGET});
      BrainUtils.clearMemories(entity, new MemoryModuleType[]{MemoryModuleType.HURT_BY_ENTITY});
      BrainUtils.clearMemories(entity, new MemoryModuleType[]{MemoryModuleType.ATTACK_TARGET});
      BrainUtils.clearMemories(entity, new MemoryModuleType[]{MemoryModuleType.ANGRY_AT});
   }

   public static void removeSpecificTargetInRadius(Entity entity, double radius, Predicate<Mob> predicate) {
      for (Mob mob : entity.level().getEntitiesOfClass(Mob.class, entity.getBoundingBox().inflate(radius))) {
         if (mob.getTarget() == entity && predicate.test(mob)) {
            removeTarget(mob);
         }
      }
   }

   public static void presenceConcealing(LivingEntity pLivingEntity, double radius) {
      if (!pLivingEntity.level().isClientSide()) {
         double concealment = pLivingEntity.getAttributeValue(TensuraAttributes.PRESENCE_CONCEALMENT);
         if (!(concealment < 1.0)) {
            for (Mob mob : pLivingEntity.level().getEntitiesOfClass(Mob.class, pLivingEntity.getBoundingBox().inflate(radius))) {
               if (mob.getTarget() == pLivingEntity && mob.getAttributeValue(TensuraAttributes.PRESENCE_SENSE) <= concealment) {
                  removeTarget(mob);
               }
            }
         }
      }
   }
}
