package io.github.manasmods.tensura.entity.template.subclass;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface ITeleportation {
   default void teleportTowards(LivingEntity entity, Entity target, double distance) {
      Vec3 vec3 = new Vec3(entity.getX() - target.getX(), entity.getY(0.5) - target.getEyeY(), entity.getZ() - target.getZ()).normalize();
      double tpDistance = target.distanceTo(entity) - distance;
      double d1 = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 8.0 - vec3.x * tpDistance;
      double d2 = target.getY() + distance * 2.0;
      double d3 = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 8.0 - vec3.z * tpDistance;
      this.teleport(entity, d1, d2, d3, target.getY() - 1.0, entity.level().getMaxBuildHeight() - 5, this.shouldCountMotionBlock());
   }

   default boolean shouldCountMotionBlock() {
      return true;
   }

   default boolean shouldBroadcastTeleport() {
      return true;
   }

   @Nullable
   default SoundEvent getTeleportSound() {
      return SoundEvents.ENDERMAN_TELEPORT;
   }

   default void teleport(LivingEntity entity, double pX, double pY, double pZ) {
      this.teleport(entity, pX, pY, pZ, entity.level().getMinBuildHeight(), entity.level().getMaxBuildHeight(), this.shouldCountMotionBlock());
   }

   default boolean teleport(LivingEntity entity, double pX, double pY, double pZ, double minY, double maxY, boolean countMotionBlock) {
      MutableBlockPos pos = new MutableBlockPos(pX, pY, pZ);
      if (countMotionBlock) {
         while (pos.getY() > minY && !entity.level().getBlockState(pos.below()).blocksMotion()) {
            pos.move(Direction.DOWN);
         }
      } else {
         while (pos.getY() < minY) {
            pos.move(Direction.UP);
         }
      }

      while (pos.getY() > maxY) {
         pos.move(Direction.DOWN);
      }

      BlockState state = entity.level().getBlockState(pos.below());
      if ((!countMotionBlock || state.blocksMotion()) && state.getFluidState().isEmpty()) {
         Vec3 vec3 = entity.position();
         if (this.randomTeleport(entity, pX, Math.max(pos.getY(), minY), pZ, this.shouldBroadcastTeleport())) {
            entity.level().gameEvent(GameEvent.TELEPORT, vec3, Context.of(entity));
            if (!entity.isSilent() && this.getTeleportSound() != null) {
               entity.level().playSound(null, entity.xo, entity.yo, entity.zo, this.getTeleportSound(), entity.getSoundSource(), 1.0F, 1.0F);
               entity.playSound(this.getTeleportSound(), 1.0F, 1.0F);
            }

            return true;
         }
      }

      return false;
   }

   default boolean randomTeleport(LivingEntity entity, double pX, double pY, double pZ, boolean pBroadcastTeleport) {
      Level level = entity.level();
      int blockX = Mth.floor(pX);
      int blockY = Mth.floor(pY);
      int blockZ = Mth.floor(pZ);
      BlockPos targetPos = new BlockPos(blockX, blockY, blockZ);
      if (!level.hasChunkAt(targetPos)) {
         return false;
      }

      double origX = entity.getX();
      double origY = entity.getY();
      double origZ = entity.getZ();
      double landingY = targetPos.getY();
      entity.teleportTo(pX, landingY, pZ);
      boolean validPlacement = level.noCollision(entity) && !level.containsAnyLiquid(entity.getBoundingBox());
      if (!validPlacement) {
         entity.teleportTo(origX, origY, origZ);
         return false;
      }

      if (pBroadcastTeleport) {
         level.broadcastEntityEvent(entity, (byte)46);
      }

      return true;
   }
}
