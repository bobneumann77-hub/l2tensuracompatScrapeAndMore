package io.github.manasmods.tensura.entity.ai.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClimbingNavigator extends GroundPathNavigation {
   @Nullable
   private BlockPos pathToPosition;

   public ClimbingNavigator(Mob mob, Level level) {
      super(mob, level);
      this.setCanFloat(true);
   }

   public Path createPath(@NotNull BlockPos pos, int accuracy) {
      this.pathToPosition = pos;
      return super.createPath(pos, accuracy);
   }

   public Path createPath(Entity entity, int i) {
      this.pathToPosition = entity.blockPosition();
      return super.createPath(entity, i);
   }

   public boolean moveTo(@NotNull Entity entity, double speed) {
      Path path = this.createPath(entity, 0);
      if (path != null) {
         return this.moveTo(path, speed);
      }

      this.pathToPosition = entity.blockPosition();
      this.speedModifier = speed;
      return true;
   }

   public void tick() {
      if (!this.isDone()) {
         super.tick();
      } else if (this.pathToPosition != null) {
         if (this.pathToPosition.closerToCenterThan(this.mob.position(), Math.max(this.mob.getBbWidth(), 1.0))
            || this.mob.getY() > this.pathToPosition.getY()
               && new BlockPos(this.pathToPosition.getX(), (int)this.mob.getY(), this.pathToPosition.getZ())
                  .closerToCenterThan(this.mob.position(), Math.max(this.mob.getBbWidth(), 1.0))) {
            this.pathToPosition = null;
         } else {
            this.mob.getMoveControl().setWantedPosition(this.pathToPosition.getX(), this.pathToPosition.getY(), this.pathToPosition.getZ(), this.speedModifier);
         }
      }
   }

   public boolean isStableDestination(BlockPos blockPos) {
      return this.canFloat() ? !this.level.getBlockState(blockPos.below()).isAir() : super.isStableDestination(blockPos);
   }
}
