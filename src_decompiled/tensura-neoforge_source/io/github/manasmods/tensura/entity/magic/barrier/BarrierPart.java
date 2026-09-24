package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.tensura.entity.template.TensuraPartEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class BarrierPart extends TensuraPartEntity {
   public final String id;
   private final Direction direction;

   public BarrierPart(BarrierEntity barrier, String name, Direction direction) {
      super(barrier);
      this.id = name;
      this.direction = direction;
      this.blocksBuilding = barrier.blockBuilding();
   }

   public boolean canCollideWith(@NotNull Entity entity) {
      return this.getBarrier().canWalkThrough(entity) ? false : (entity.canBeCollidedWith() || entity.isPushable()) && !this.isPassengerOfSameVehicle(entity);
   }

   public boolean canBeCollidedWith() {
      return !this.getBarrier().canWalkThrough();
   }

   public boolean isPushable() {
      return this.getBarrier().shouldPush();
   }

   public void push(Entity pEntity) {
      if (this.getBarrier().getOwner() != pEntity) {
         super.push(pEntity);
      }
   }

   public BarrierEntity getBarrier() {
      return (BarrierEntity)this.getParent();
   }

   public Direction getBarrierDirection() {
      return this.direction;
   }

   public float getBarrierRadius() {
      return this.getBarrier().getSize();
   }

   @NotNull
   protected AABB makeBoundingBox() {
      return this.direction == null ? super.makeBoundingBox() : this.getDirectionalBoundingBox();
   }

   public AABB getDirectionalBoundingBox() {
      Direction opposite = this.getBarrierDirection().getOpposite();
      float minX = -0.15F;
      float minY = -0.15F;
      float minZ = -0.15F;
      float maxX = 0.15F;
      float maxY = 0.15F;
      float maxZ = 0.15F;
      switch (opposite) {
         case NORTH:
         case SOUTH:
            minX = -this.getBarrierRadius();
            maxX = this.getBarrierRadius();
            minY = -this.getBarrierRadius();
            maxY = this.getBarrierRadius() + this.getBarrier().getHeight();
            break;
         case EAST:
         case WEST:
            minZ = -this.getBarrierRadius();
            maxZ = this.getBarrierRadius();
            minY = -this.getBarrierRadius();
            maxY = this.getBarrierRadius() + this.getBarrier().getHeight();
            break;
         case UP:
         case DOWN:
            minX = -this.getBarrierRadius();
            maxX = this.getBarrierRadius();
            minZ = -this.getBarrierRadius();
            maxZ = this.getBarrierRadius();
      }

      return new AABB(this.getX() + minX, this.getY() + minY, this.getZ() + minZ, this.getX() + maxX, this.getY() + maxY, this.getZ() + maxZ);
   }

   public void setPositionOnDirection() {
      BarrierEntity barrier = this.getBarrier();
      double bx = barrier.getX();
      double by = barrier.getY();
      double bz = barrier.getZ();
      float size = barrier.getSize();
      Direction opposite = this.getBarrierDirection().getOpposite();
      switch (opposite) {
         case NORTH:
            this.setPos(bx, by + size, bz + size);
            break;
         case SOUTH:
            this.setPos(bx, by + size, bz - size);
            break;
         case EAST:
            this.setPos(bx + size, by + size, bz);
            break;
         case WEST:
            this.setPos(bx - size, by + size, bz);
            break;
         case UP:
            this.setPos(bx, by + barrier.getHeight() + size * 2.0F, bz);
            break;
         case DOWN:
            this.setPos(bx, by, bz);
      }

      double px = this.getX();
      double py = this.getY();
      double pz = this.getZ();
      this.xo = px;
      this.yo = py;
      this.zo = pz;
      this.xOld = px;
      this.yOld = py;
      this.zOld = pz;
      this.hurtMarked = true;
   }

   protected void addServerParticlesAroundSelf(ParticleOptions pParticleOption, double randomScale) {
      if (this.level() instanceof ServerLevel serverLevel) {
         RandomSource var14 = this.getRandom();
         AABB aabb = this.getBoundingBox();

         for (int i = 0; i < 5; i++) {
            double d0 = var14.nextGaussian() * 0.02 * randomScale;
            double d1 = var14.nextGaussian() * 0.02 * randomScale;
            double d2 = var14.nextGaussian() * 0.02 * randomScale;
            serverLevel.sendParticles(
               pParticleOption,
               this.getX() + aabb.getXsize() * this.getRandomScale(var14, randomScale),
               this.getY() + aabb.getYsize() * this.getRandomScale(var14, randomScale),
               this.getZ() + aabb.getZsize() * this.getRandomScale(var14, randomScale),
               0,
               d0,
               d1,
               d2,
               1.0
            );
         }
      }
   }

   private double getRandomScale(RandomSource randomSource, double scale) {
      return (2.0 * randomSource.nextDouble() - 1.0) * scale;
   }
}
