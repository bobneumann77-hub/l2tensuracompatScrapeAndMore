package io.github.manasmods.tensura.entity.ai.behaviour.movement;

import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;

public class MoveOrFlyToWalkTarget<E extends PathfinderMob & IFlying> extends MoveToWalkTarget<E> {
   protected BiFunction<E, BlockPos, Float> startFlightDistance = (entity, pos) -> 3.0F + entity.getBbWidth() / 2.0F;

   public MoveOrFlyToWalkTarget() {
      this.runFor(entity -> entity.getRandom().nextInt(200) + 200);
   }

   public MoveOrFlyToWalkTarget<E> startFlightDistance(BiFunction<E, BlockPos, Float> distance) {
      this.startFlightDistance = distance;
      return this;
   }

   protected boolean attemptNewPath(E entity, WalkTarget walkTarget, boolean reachedCurrentTarget) {
      if (!reachedCurrentTarget && entity.canFly()) {
         float distance = this.startFlightDistance.apply(entity, walkTarget.getTarget().currentBlockPosition());
         if (entity.distanceToSqr(walkTarget.getTarget().currentPosition()) >= distance * distance) {
            entity.setFlying(true);
         }
      }

      return super.attemptNewPath(entity, walkTarget, reachedCurrentTarget);
   }
}
