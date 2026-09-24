package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.navigation.SmoothWaterBoundPathNavigation;

public interface ISwimming {
   void switchLookControl(LookControl var1);

   void switchMoveControl(MoveControl var1);

   void switchNavigation(PathNavigation var1);

   default int getFlopTick() {
      return 0;
   }

   default void setFlopTick(int tick) {
   }

   default void initSwimming(PathfinderMob entity) {
      entity.setPathfindingMalus(PathType.WATER, 0.0F);
      entity.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.switchLookControl(new SmoothSwimmingLookControl(entity, 10));
      this.switchMoveControl(new SmoothSwimmingMoveControl(entity, 85, 10, 1.0F, 0.5F, false));
      this.switchNavigation(new SmoothWaterBoundPathNavigation(entity, entity.level()));
   }

   default void handleFlopping(Mob entity) {
      this.setFlopTick(this.getFlopTick() - 1);
      if (!entity.isInWater() && entity.onGround()) {
         this.setFlopTick(20);
         if (entity.level().isClientSide()) {
            return;
         }

         if (entity.verticalCollision) {
            entity.unRide();
            entity.playSound(this.getFlopSound(), Math.max(1.0F, entity.getBbWidth() / 2.0F), entity.getVoicePitch());
            double dx = 0.0;
            double dz = 0.0;
            if (entity.getNavigation().getTargetPos() != null) {
               dx = entity.getNavigation().getTargetPos().getX() - entity.getX();
               dz = entity.getNavigation().getTargetPos().getZ() - entity.getZ();
               double dist = Math.sqrt(dx * dx + dz * dz);
               if (dist > 0.001) {
                  dx /= dist;
                  dz /= dist;
               } else {
                  dx = entity.getRandom().nextFloat() * 2.0F - 1.0F;
                  dz = entity.getRandom().nextFloat() * 2.0F - 1.0F;
               }
            }

            double motionX = dx * 0.2 + (entity.getRandom().nextFloat() - 0.5) * 0.1;
            double motionZ = dz * 0.2 + (entity.getRandom().nextFloat() - 0.5) * 0.1;
            entity.setDeltaMovement(entity.getDeltaMovement().add(motionX, 0.4, motionZ));
            entity.hasImpulse = true;
         }
      }
   }

   default void handleAirSupply(LivingEntity entity, int i) {
      if (entity.isAlive() && !entity.isInWaterOrBubble()) {
         entity.setAirSupply(i - 1);
         if (entity.getAirSupply() == -20) {
            entity.setAirSupply(0);
            entity.hurt(entity.damageSources().dryOut(), 2.0F);
         }
      } else {
         entity.setAirSupply(entity.getMaxAirSupply());
      }
   }

   default void spawnSwimmingParticle(Mob entity) {
      if (entity.level().isClientSide() && entity.isInWater() && entity.getDeltaMovement().lengthSqr() > 0.03) {
         Vec3 vector3d = entity.getViewVector(0.0F);
         float f = Mth.cos(entity.getYRot() * (float) (Math.PI / 180.0)) * 0.9F;
         float f1 = Mth.sin(entity.getYRot() * (float) (Math.PI / 180.0)) * 0.9F;
         float f2 = 1.2F - entity.getRandom().nextFloat() * 0.7F;

         for (int i = 0; i < 2; i++) {
            entity.level()
               .addParticle(
                  ParticleTypes.DOLPHIN, entity.getX() - vector3d.x * f2 + f, entity.getY() - vector3d.y, entity.getZ() - vector3d.z * f2 + f1, 0.0, 0.0, 0.0
               );
            entity.level()
               .addParticle(
                  ParticleTypes.DOLPHIN, entity.getX() - vector3d.x * f2 - f, entity.getY() - vector3d.y, entity.getZ() - vector3d.z * f2 - f1, 0.0, 0.0, 0.0
               );
         }
      }
   }

   default SoundEvent getFlopSound() {
      return (SoundEvent)TensuraSoundEvents.FISH_FLOP.get();
   }

   default boolean shouldFlop() {
      return this.getFlopTick() > 0;
   }

   static boolean checkDefaultSwimmingSpawnRules(
      EntityType<? extends LivingEntity> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource
   ) {
      int i = levelAccessor.getSeaLevel();
      int j = i - 13;
      return blockPos.getY() >= j
         && blockPos.getY() <= i
         && levelAccessor.getFluidState(blockPos.below()).is(FluidTags.WATER)
         && levelAccessor.getBlockState(blockPos.above()).is(Blocks.WATER);
   }
}
