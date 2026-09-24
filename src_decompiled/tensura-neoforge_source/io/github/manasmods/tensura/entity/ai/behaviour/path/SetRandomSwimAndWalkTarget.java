package io.github.manasmods.tensura.entity.ai.behaviour.path;

import io.github.manasmods.tensura.entity.template.subclass.IAmphibian;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.object.SquareRadius;
import org.jetbrains.annotations.Nullable;

public class SetRandomSwimAndWalkTarget<E extends PathfinderMob & IAmphibian> extends SetRandomWalkTarget<E> {
   protected SquareRadius waterSearchRadius = new SquareRadius(32.0, 32.0);
   private final int swimChanceFromWalk;
   private final int swimChanceContinue;

   public SetRandomSwimAndWalkTarget(int swimChanceFromWalk, int swimChanceContinue) {
      this.swimChanceFromWalk = swimChanceFromWalk;
      this.swimChanceContinue = swimChanceContinue;
   }

   public SetRandomSwimAndWalkTarget() {
      this(5, 3);
      this.setRadius(20.0, 20.0);
   }

   public SetRandomSwimAndWalkTarget<E> setWaterSearchRadius(double xz, double y) {
      this.waterSearchRadius = new SquareRadius(xz, y);
      return this;
   }

   @Nullable
   protected Vec3 getTargetPos(E entity) {
      boolean shouldSwim;
      if (entity.onGround()) {
         shouldSwim = entity.shouldFindWater(entity) || entity.getRandom().nextInt(this.swimChanceFromWalk) > 0;
      } else {
         shouldSwim = entity.shouldStayInWater(entity) && entity.getRandom().nextInt(this.swimChanceContinue) > 0;
      }

      if (shouldSwim) {
         Vec3 swimPos = BehaviorUtils.getRandomSwimmablePos(entity, (int)this.radius.xzRadius(), (int)this.radius.yRadius());
         if (swimPos != null) {
            return swimPos;
         }

         BlockPos water = this.findNearestWater(entity);
         if (water != null) {
            return water.getCenter();
         }
      }

      return LandRandomPos.getPos(entity, (int)this.radius.xzRadius(), (int)this.radius.yRadius());
   }

   public BlockPos findNearestWater(E entity) {
      BlockPos blockpos = null;
      int range = (int)this.waterSearchRadius.xzRadius();

      for (int i = 0; i < 15; i++) {
         BlockPos blockPos = entity.blockPosition().offset(entity.getRandom().nextInt(range) - range / 2, 3, entity.getRandom().nextInt(range) - range / 2);

         while (entity.level().isEmptyBlock(blockPos) && blockPos.getY() > 1) {
            blockPos = blockPos.below();
         }

         if (entity.level().getFluidState(blockPos).is(FluidTags.WATER)) {
            blockpos = blockPos;
         }
      }

      return blockpos;
   }
}
