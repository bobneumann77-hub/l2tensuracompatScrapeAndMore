package io.github.manasmods.tensura.entity.template.subclass;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.ai.CustomCollisionsBlockCollisions;
import io.github.manasmods.tensura.entity.ai.controller.DiggingMoveController;
import io.github.manasmods.tensura.entity.ai.navigator.DiggingNavigator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public interface IDigging<E extends Mob & IDigging<E>> {
   void switchMoveControl(MoveControl var1);

   void switchNavigation(PathNavigation var1);

   void setLandNavigating(boolean var1);

   boolean isLandNavigating();

   boolean isDigging();

   void setDigging(boolean var1);

   void setDiggingTick(int var1);

   int getDiggingTick();

   default void switchNavigator(E entity, boolean onLand) {
      if (!onLand && !entity.isSleeping()) {
         this.switchMoveControl(new DiggingMoveController(entity));
         this.switchNavigation(new DiggingNavigator(entity, entity.level()));
         this.setLandNavigating(false);
      } else {
         this.switchMoveControl(new MoveControl(entity) {
            public void tick() {
               if (!entity.isSleeping()) {
                  super.tick();
               }
            }
         });
         this.switchNavigation(new GroundPathNavigation(entity, entity.level()));
         this.setLandNavigating(true);
      }
   }

   default void handleDigging(E entity) {
      if (!entity.level().isClientSide()) {
         if (this.isDigging()) {
            this.setDiggingTick(this.getDiggingTick() + 1);
            if (this.isLandNavigating()) {
               this.switchNavigator(entity, false);
               entity.level().broadcastEntityEvent(entity, (byte)77);
            }

            if (this.getDiggingTick() > 40 && !entity.isInWall()) {
               this.setDigging(false);
               this.onDigUp(entity);
            }

            if (!this.isSafeDig(entity, entity.level(), entity.blockPosition())) {
               if (entity.canDigBlock(entity.level().getBlockState(entity.blockPosition().above()))) {
                  entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.1, 0.0));
               }

               if (entity.canDigBlock(entity.level().getBlockState(entity.blockPosition().below()))) {
                  entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, -0.08, 0.0));
               }
            }

            entity.setNoGravity(entity.isInWall());
            entity.resetFallDistance();
         } else {
            this.setDiggingTick(0);
            if (!this.isLandNavigating()) {
               entity.switchNavigator(entity, true);
               entity.level().broadcastEntityEvent(entity, (byte)77);
            }

            entity.setNoGravity(false);
         }
      }
   }

   default boolean shouldSurface(E entity) {
      if (entity.isDigging() && entity.getDiggingTick() > 300) {
         return true;
      }

      LivingEntity target = entity.getTarget();
      return target != null && !target.isInWall();
   }

   default void onDigUp(E entity) {
      entity.setPos(entity.position().add(0.0, 1.0, 0.0));
      entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.35, 0.0));
   }

   default boolean canDigBlock(BlockState state) {
      return state.is(TensuraBlockTags.DIGGABLE_BY_MONSTER) && state.getFluidState().isEmpty() && state.canOcclude();
   }

   default boolean isSafeDig(E entity, BlockGetter level, BlockPos centerPos) {
      double radius = entity.getBbWidth() / 2.0;
      int minX = Mth.floor(centerPos.getX() - radius);
      int maxX = Mth.floor(centerPos.getX() + radius);
      int minZ = Mth.floor(centerPos.getZ() - radius);
      int maxZ = Mth.floor(centerPos.getZ() + radius);
      int minY = centerPos.getY();
      int maxY = centerPos.getY() + Mth.ceil(entity.getBbHeight()) * (this.isDigging() ? 1 : -1);
      MutableBlockPos checkPos = new MutableBlockPos();

      for (int x = minX; x <= maxX; x++) {
         for (int z = minZ; z <= maxZ; z++) {
            for (int y = minY; y < maxY; y++) {
               checkPos.set(x, y, z);
               BlockState state = level.getBlockState(checkPos);
               if (!this.canDigBlock(state)) {
                  return false;
               }
            }

            checkPos.set(x, minY - 1, z);
            BlockState below = level.getBlockState(checkPos);
            if (!this.canDigBlock(below)) {
               return false;
            }
         }
      }

      return true;
   }

   default boolean canPassThrough(BlockPos blockPos, BlockState blockState, VoxelShape voxelShape) {
      return this.isDigging() && this.canDigBlock(blockState);
   }

   default boolean canReach(E entity, BlockPos target) {
      Path path = entity.getNavigation().createPath(target, 0);
      if (path == null) {
         return false;
      }

      Node node = path.getEndNode();
      if (node == null) {
         return false;
      }

      int i = node.x - target.getX();
      int j = node.y - target.getY();
      int k = node.z - target.getZ();
      return i * i + j * j + k * k <= 3.0;
   }

   default Vec3 getAllowedMovementForEntity(E entity, Vec3 vecIN) {
      AABB aabb = entity.getBoundingBox();
      List<VoxelShape> list = entity.level().getEntityCollisions(entity, aabb.expandTowards(vecIN));
      Vec3 vec3 = vecIN.lengthSqr() == 0.0 ? vecIN : this.collideBoundingBox2(entity, vecIN, aabb, entity.level(), list);
      boolean flag = vecIN.x != vec3.x;
      boolean flag1 = vecIN.y != vec3.y;
      boolean flag2 = vecIN.z != vec3.z;
      boolean flag3 = entity.onGround() || flag1 && vecIN.y < 0.0;
      if (entity.maxUpStep() > 0.0F && flag3 && (flag || flag2)) {
         Vec3 vec31 = this.collideBoundingBox2(entity, new Vec3(vecIN.x, entity.maxUpStep(), vecIN.z), aabb, entity.level(), list);
         Vec3 vec32 = this.collideBoundingBox2(entity, new Vec3(0.0, entity.maxUpStep(), 0.0), aabb.expandTowards(vecIN.x, 0.0, vecIN.z), entity.level(), list);
         if (vec32.y < entity.maxUpStep()) {
            Vec3 vec33 = this.collideBoundingBox2(entity, new Vec3(vecIN.x, 0.0, vecIN.z), aabb.move(vec32), entity.level(), list).add(vec32);
            if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
               vec31 = vec33;
            }
         }

         if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
            return vec31.add(this.collideBoundingBox2(entity, new Vec3(0.0, -vec31.y + vecIN.y, 0.0), aabb.move(vec31), entity.level(), list));
         }
      }

      return vec3;
   }

   default Vec3 collideBoundingBox2(@Nullable E entity, Vec3 vec, AABB aabb, Level level, List<VoxelShape> list) {
      Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(list.size() + 1);
      if (!list.isEmpty()) {
         builder.addAll(list);
      }

      WorldBorder worldborder = level.getWorldBorder();
      boolean flag = entity != null && worldborder.isInsideCloseToBorder(entity, aabb.expandTowards(vec));
      if (flag) {
         builder.add(worldborder.getCollisionShape());
      }

      builder.addAll(new CustomCollisionsBlockCollisions(level, entity, aabb.expandTowards(vec)));
      return collideWithShapes2(vec, aabb, builder.build());
   }

   private static Vec3 collideWithShapes2(Vec3 vec, AABB aabb, List<VoxelShape> list) {
      if (list.isEmpty()) {
         return vec;
      }

      double d0 = vec.x;
      double d1 = vec.y;
      double d2 = vec.z;
      if (d1 != 0.0) {
         d1 = Shapes.collide(Axis.Y, aabb, list, d1);
         if (d1 != 0.0) {
            aabb = aabb.move(0.0, d1, 0.0);
         }
      }

      boolean flag = Math.abs(d0) < Math.abs(d2);
      if (flag && d2 != 0.0) {
         d2 = Shapes.collide(Axis.Z, aabb, list, d2);
         if (d2 != 0.0) {
            aabb = aabb.move(0.0, 0.0, d2);
         }
      }

      if (d0 != 0.0) {
         d0 = Shapes.collide(Axis.X, aabb, list, d0);
         if (!flag && d0 != 0.0) {
            aabb = aabb.move(d0, 0.0, 0.0);
         }
      }

      if (!flag && d2 != 0.0) {
         d2 = Shapes.collide(Axis.Z, aabb, list, d2);
      }

      return new Vec3(d0, d1, d2);
   }
}
