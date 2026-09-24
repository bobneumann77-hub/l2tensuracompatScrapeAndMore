package io.github.manasmods.tensura.entity.ai.behaviour.movement;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.subclass.ITeleportation;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.TriPredicate;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.RandomUtil;

public class TeleportToEntity<E extends PathfinderMob, T extends Entity> extends ExtendedBehaviour<E> {
   protected Function<E, T> teleportEntityProvider = entity -> null;
   protected BiFunction<E, T, Double> maxTeleportDistance = (entity, target) -> 128.0;
   protected BiFunction<E, T, Double> teleportDistance = (entity, target) -> Double.MAX_VALUE;
   protected BiFunction<E, T, Integer> teleportRadius = (entity, target) -> 5;
   protected TriPredicate<E, BlockPos, BlockState> teleportPredicate = this::isTeleportable;
   protected Predicate<E> canTeleportOffGround = entity -> entity.getNavigation().getNodeEvaluator() instanceof SwimNodeEvaluator
      || entity.getNavigation().getNodeEvaluator() instanceof FlyNodeEvaluator;
   protected BiConsumer<E, T> onSuccessTeleport = (entity, target) -> {};

   public TeleportToEntity<E, T> following(Function<E, T> following) {
      this.teleportEntityProvider = following;
      return this;
   }

   public TeleportToEntity<E, T> teleportToTargetAfter(double distance) {
      return this.teleportToTargetAfter((entity, target) -> distance);
   }

   public TeleportToEntity<E, T> maximumTeleportDistance(BiFunction<E, T, Double> distanceProvider) {
      this.maxTeleportDistance = distanceProvider;
      return this;
   }

   public TeleportToEntity<E, T> teleportToTargetAfter(BiFunction<E, T, Double> distanceProvider) {
      this.teleportDistance = distanceProvider;
      return this;
   }

   public TeleportToEntity<E, T> teleportRadius(BiFunction<E, T, Integer> radiusProvider) {
      this.teleportRadius = radiusProvider;
      return this;
   }

   public TeleportToEntity<E, T> canTeleportTo(TriPredicate<E, BlockPos, BlockState> teleportPosPredicate) {
      this.teleportPredicate = teleportPosPredicate;
      return this;
   }

   public TeleportToEntity<E, T> canTeleportOffGroundWhen(Predicate<E> predicate) {
      this.canTeleportOffGround = predicate;
      return this;
   }

   public TeleportToEntity<E, T> onSuccessTeleport(BiConsumer<E, T> callback) {
      this.onSuccessTeleport = callback;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return List.of();
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      T target = this.teleportEntityProvider.apply(entity);
      if (target != null && !target.isSpectator() && entity.level() == target.level()) {
         double maxDist = this.maxTeleportDistance.apply(entity, target);
         if (entity.distanceToSqr(target) > maxDist * maxDist) {
            return false;
         }

         double minDist = this.teleportDistance.apply(entity, target);
         return entity.distanceToSqr(target) > minDist * minDist;
      } else {
         return false;
      }
   }

   protected void start(E entity) {
      T target = this.teleportEntityProvider.apply(entity);
      this.teleportToTarget(entity, target);
   }

   protected void teleportToTarget(E entity, T target) {
      if (entity instanceof ITeleportation teleportation) {
         Vec3 vec3 = new Vec3(entity.getX() - target.getX(), entity.getY(0.5) - target.getEyeY(), entity.getZ() - target.getZ()).normalize();
         int radius = this.teleportRadius.apply(entity, target);
         double tpDistance = target.distanceTo(entity) - radius;
         double d1 = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 8.0 - vec3.x * tpDistance;
         double d2 = entity.getY() + (entity.getRandom().nextInt(16) - 8) - vec3.y * tpDistance;
         double d3 = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 8.0 - vec3.z * tpDistance;
         if (teleportation.teleport(
            entity,
            d1,
            d2,
            d3,
            target.getY(),
            Math.min(target.getY() + radius, entity.level().getMaxBuildHeight() - 5),
            !this.canTeleportOffGround.test(entity)
         )) {
            entity.getNavigation().stop();
            this.onSuccessTeleport.accept(entity, target);
            BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
         }
      } else {
         BlockPos targetPos = target.blockPosition();
         BlockPos teleportPos = this.getTeleportPos(entity, target, targetPos);
         if (!teleportPos.equals(targetPos)) {
            entity.moveTo(teleportPos.getX() + 0.5, teleportPos.getY(), teleportPos.getZ() + 0.5, entity.getYRot(), entity.getXRot());
            entity.getNavigation().stop();
            this.onSuccessTeleport.accept(entity, target);
            BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
         }
      }
   }

   protected BlockPos getTeleportPos(E entity, T target, BlockPos targetPos) {
      Level level = entity.level();
      int radius = this.teleportRadius.apply(entity, target);
      return RandomUtil.getRandomPositionWithinRange(
         targetPos,
         radius,
         radius / 2,
         radius,
         1,
         1,
         1,
         !this.canTeleportOffGround.test(entity),
         level,
         10,
         (state, statePos) -> this.teleportPredicate.test(entity, statePos, state)
      );
   }

   protected boolean isTeleportable(E entity, BlockPos pos, BlockState state) {
      NodeEvaluator nodeEvaluator = entity.getNavigation().getNodeEvaluator();
      PathType pathType = nodeEvaluator.getPathType(new PathfindingContext(entity.level(), entity), pos.getX(), pos.getY(), pos.getZ());
      if (!this.canTeleportOffGround.test(entity)) {
         if (pathType != PathType.WALKABLE) {
            return false;
         }
      } else if (pathType != PathType.OPEN && pathType != PathType.WALKABLE) {
         return false;
      }

      return entity.level().noCollision(entity, entity.getBoundingBox().move(Vec3.atBottomCenterOf(pos).subtract(entity.position())));
   }
}
