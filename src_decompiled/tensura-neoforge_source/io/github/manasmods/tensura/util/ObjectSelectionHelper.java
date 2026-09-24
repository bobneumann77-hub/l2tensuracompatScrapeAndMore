package io.github.manasmods.tensura.util;

import com.mojang.authlib.GameProfile;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.BlockConfig;
import io.github.manasmods.tensura.entity.template.subclass.ILivingPartEntity;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

public class ObjectSelectionHelper {
   public static final BlockConfig CONFIG = (BlockConfig)ConfigRegistry.getConfig(BlockConfig.class);

   @Nullable
   public static MutableComponent getPlayerNameFromUUID(ServerLevel level, UUID uuid) {
      Player player = level.getPlayerByUUID(uuid);
      if (player != null) {
         return player.getName().copy();
      } else {
         GameProfileCache cache = level.getServer().getProfileCache();
         if (cache == null) {
            return null;
         } else {
            return cache.get(uuid).isEmpty() ? null : Component.literal(((GameProfile)cache.get(uuid).get()).getName());
         }
      }
   }

   @Nullable
   public static Entity getEntityFromUUID(ServerLevel serverLevel, UUID uuid) {
      return getEntityFromUUID(serverLevel, uuid, entity -> true);
   }

   @Nullable
   public static Entity getEntityFromUUID(ServerLevel serverLevel, UUID uuid, Predicate<Entity> predicate) {
      Entity entity = serverLevel.getEntity(uuid);
      if (entity != null && predicate.test(entity)) {
         return entity;
      }

      for (ServerLevel level : serverLevel.getServer().getAllLevels()) {
         Entity target = level.getEntity(uuid);
         if (target != null && predicate.test(target)) {
            entity = target;
            break;
         }
      }

      return entity;
   }

   @Nullable
   public static <T extends Entity> T getTargetingEntity(
      Class<T> targetClass, LivingEntity user, double distance, double radius, boolean nonSubordinate, boolean lineOfSight, boolean requirePhysics
   ) {
      Vec3 view = user.getViewVector(1.0F);
      T target = null;
      AABB expanded = user.getBoundingBox().expandTowards(view.x * distance, view.y * distance, view.z * distance);
      List<T> list = user.level().getEntitiesOfClass(targetClass, expanded, getTargetPredicate(user, nonSubordinate, lineOfSight, requirePhysics));
      if (list.isEmpty()) {
         return null;
      }

      double furthestEntity = distance;
      Vec3 eye = user.getEyePosition(1.0F).add(view.x * -1.0, view.y * -1.0, view.z * -1.0);
      Vec3 furtherEye = eye.add(view.x * distance, view.y * distance, view.z * distance);

      for (T entity : list) {
         AABB aabb = entity.getBoundingBox().inflate(radius);
         Optional<Vec3> optional = aabb.clip(eye, furtherEye);
         if (optional.isPresent() && !aabb.contains(eye)) {
            double closerEntity = eye.distanceTo(optional.get());
            if (closerEntity < furthestEntity || furthestEntity == 0.0) {
               if (entity.getRootVehicle() == user.getRootVehicle()) {
                  if (furthestEntity == 0.0) {
                     target = entity;
                  }
               } else {
                  target = entity;
                  furthestEntity = closerEntity;
               }
            }
         }
      }

      return (T)(target instanceof ILivingPartEntity part && part.getHead() instanceof LivingEntity head ? head : target);
   }

   @Nullable
   public static <T extends Entity> T getTargetingEntity(
      Class<T> targetClass, LivingEntity user, double distance, double radius, boolean nonSubordinate, boolean requirePhysics
   ) {
      return getTargetingEntity(targetClass, user, distance, radius, nonSubordinate, true, requirePhysics);
   }

   @Nullable
   public static <T extends Entity> T getTargetingEntity(Class<T> targetClass, LivingEntity user, double distance, double radius, boolean nonSubordinate) {
      return getTargetingEntity(targetClass, user, distance, radius, nonSubordinate, true, false);
   }

   @Nullable
   public static Entity getTargetingEntity(LivingEntity user, double distance, double radius, boolean nonSubordinate, boolean lineOfSight) {
      return getTargetingEntity(Entity.class, user, distance, radius, nonSubordinate, lineOfSight, false);
   }

   @Nullable
   public static Entity getTargetingEntity(
      LivingEntity user, double distance, double radius, boolean nonSubordinate, boolean lineOfSight, boolean requirePhysics
   ) {
      return getTargetingEntity(Entity.class, user, distance, radius, nonSubordinate, lineOfSight, requirePhysics);
   }

   @Nullable
   public static LivingEntity getTargetingEntity(LivingEntity user, double distance, boolean nonSubordinate, boolean lineOfSight) {
      return getTargetingEntity(LivingEntity.class, user, distance, 0.2, nonSubordinate, lineOfSight, true);
   }

   @Nullable
   public static LivingEntity getTargetingEntity(LivingEntity user, double distance, boolean nonSubordinate) {
      return getTargetingEntity(user, distance, nonSubordinate, true);
   }

   public static <T extends Entity> Predicate<T> getTargetPredicate(LivingEntity user, boolean nonSubordinate, boolean lineOfSight, boolean requirePhysics) {
      return entity -> {
         if (entity == user) {
            return false;
         }

         if (nonSubordinate) {
            if (entity.isAlliedTo(user)) {
               return false;
            }

            if (entity instanceof Projectile projectile && projectile.getOwner() == user) {
               return false;
            }
         }

         return lineOfSight && !user.hasLineOfSight(entity) ? false : !requirePhysics || !entity.noPhysics;
      };
   }

   public static BlockPos getBlockPos(Vec3 vec3) {
      return BlockPos.containing(vec3.x(), vec3.y(), vec3.z());
   }

   public static BlockHitResult getPlayerPOVHitResult(Level pLevel, Entity entity, Fluid pFluidMode, double reachDistance) {
      return getPlayerPOVHitResult(pLevel, entity, pFluidMode, Block.COLLIDER, reachDistance);
   }

   public static BlockHitResult getPlayerPOVHitResult(Level pLevel, Entity entity, Fluid pFluidMode, Block block, double reachDistance) {
      float f = entity.getXRot();
      float f1 = entity.getYRot();
      Vec3 vec3 = entity.getEyePosition();
      float f2 = Mth.cos(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = Mth.sin(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = -Mth.cos(-f * (float) (Math.PI / 180.0));
      float f5 = Mth.sin(-f * (float) (Math.PI / 180.0));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      Vec3 vec31 = vec3.add(f6 * reachDistance, f5 * reachDistance, f7 * reachDistance);
      return pLevel.clip(new ClipContext(vec3, vec31, block, pFluidMode, entity));
   }

   public static BlockHitResult getPlayerPOVHitResultFromPos(Level pLevel, LivingEntity entity, Fluid pFluidMode, double reachDistance, Vec3 fromPos) {
      return getPlayerPOVHitResultFromPos(pLevel, entity, pFluidMode, Block.OUTLINE, reachDistance, fromPos);
   }

   public static BlockHitResult getPlayerPOVHitResultFromPos(
      Level pLevel, LivingEntity entity, Fluid pFluidMode, Block block, double reachDistance, Vec3 fromPos
   ) {
      float f = entity.getXRot();
      float f1 = entity.getYRot();
      float f2 = Mth.cos(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = Mth.sin(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = -Mth.cos(-f * (float) (Math.PI / 180.0));
      float f5 = Mth.sin(-f * (float) (Math.PI / 180.0));
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      Vec3 vec31 = fromPos.add(f6 * reachDistance, f5 * reachDistance, f7 * reachDistance);
      return pLevel.clip(new ClipContext(fromPos, vec31, block, pFluidMode, entity));
   }

   public static Vec3 getFloorPos(BlockPos pos) {
      return new Vec3(Mth.floor(pos.getX()) + 0.5, Mth.floor(pos.getY()), Mth.floor(pos.getZ()) + 0.5);
   }

   public static Vec3 getFloorPos(Vec3 pos) {
      return new Vec3(Mth.floor(pos.x()) + 0.5, Mth.floor(pos.y()), Mth.floor(pos.z()) + 0.5);
   }

   public static Vec3 getNearestGround(Vec3 pos, Level level, double maxDrop, CollisionContext context) {
      Vec3 endDown = new Vec3(pos.x, Math.max(pos.y - maxDrop, level.getMinBuildHeight() - 1), pos.z);
      BlockHitResult hit = level.clip(new ClipContext(pos, endDown, Block.COLLIDER, Fluid.NONE, context));
      return hit.getType() == Type.MISS ? endDown : hit.getLocation();
   }

   public static Vec3 getNearestGround(Vec3 pos, Level level, double range, Entity entity) {
      int minY = level.getMinBuildHeight() - 1;
      int maxY = level.getMaxBuildHeight();
      Vec3 downStart = new Vec3(pos.x, Math.min(pos.y + 0.5, maxY), pos.z);
      Vec3 downEnd = new Vec3(pos.x, Math.max(pos.y - range, minY), pos.z);
      Vec3 upStart = new Vec3(pos.x, Math.max(pos.y - 0.5, minY), pos.z);
      Vec3 upEnd = new Vec3(pos.x, Math.min(pos.y + range, maxY), pos.z);
      BlockHitResult downHit = level.clip(new ClipContext(downStart, downEnd, Block.COLLIDER, Fluid.NONE, entity));
      BlockHitResult upHit = level.clip(new ClipContext(upStart, upEnd, Block.COLLIDER, Fluid.NONE, entity));
      boolean downFound = downHit.getType() != Type.MISS;
      boolean upFound = upHit.getType() != Type.MISS;
      if (!downFound && !upFound) {
         return new Vec3(pos.x, pos.y - range, pos.z);
      } else if (!downFound) {
         return upHit.getLocation();
      } else if (!upFound) {
         return downHit.getLocation();
      } else {
         return Math.abs(downHit.getLocation().y - pos.y) <= Math.abs(upHit.getLocation().y - pos.y) ? downHit.getLocation() : upHit.getLocation();
      }
   }

   public static Iterable<BlockPos> getBlockCappedIteration(Iterable<BlockPos> source, int max) {
      if (max <= 0) {
         return Collections.emptyList();
      } else {
         return max == Integer.MAX_VALUE ? source : () -> new Iterator<BlockPos>() {
            private final Iterator<BlockPos> delegate = source.iterator();
            private int count = 0;

            @Override
            public boolean hasNext() {
               return this.count < max && this.delegate.hasNext();
            }

            public BlockPos next() {
               this.count++;
               return this.delegate.next();
            }
         };
      }
   }

   public static Vec3 getVectorBetween(Vec3 from, Vec3 to) {
      return to.subtract(from);
   }

   public static Vec2 getRotFromVector(Vec3 vec) {
      double x = vec.x;
      double y = vec.y;
      double z = vec.z;
      double length = Math.sqrt(x * x + y * y + z * z);
      if (length == 0.0) {
         return new Vec2(0.0F, 0.0F);
      }

      x /= length;
      y /= length;
      z /= length;
      float xRot = (float)Math.toDegrees(Math.asin(-y));
      float yRot = (float)Math.toDegrees(-Math.atan2(x, z));
      return new Vec2(xRot, yRot);
   }

   public static float getXRotFromVector(Vec3 vec) {
      double x = vec.x;
      double y = vec.y;
      double z = vec.z;
      double length = Math.sqrt(x * x + y * y + z * z);
      if (length == 0.0) {
         return 0.0F;
      }

      y /= length;
      return (float)Math.toDegrees(Math.asin(-y));
   }

   public static float getYRotFromVector(Vec3 vec) {
      double x = vec.x;
      double y = vec.y;
      double z = vec.z;
      double length = Math.sqrt(x * x + y * y + z * z);
      if (length == 0.0) {
         return 0.0F;
      }

      x /= length;
      z /= length;
      return (float)Math.toDegrees(-Math.atan2(x, z));
   }

   public static Vec3 getLerpVector(Vec3 from, Vec3 to, float factor) {
      double x = Mth.lerp(factor, from.x(), to.x());
      double y = Mth.lerp(factor, from.y(), to.y());
      double z = Mth.lerp(factor, from.z(), to.z());
      return new Vec3(x, y, z);
   }
}
