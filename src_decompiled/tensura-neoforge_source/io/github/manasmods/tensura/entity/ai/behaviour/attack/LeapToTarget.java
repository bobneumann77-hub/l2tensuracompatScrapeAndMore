package io.github.manasmods.tensura.entity.ai.behaviour.attack;

import java.util.function.BiFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.LeapAtTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

public class LeapToTarget<E extends Mob> extends LeapAtTarget<E> {
   protected BiFunction<E, LivingEntity, Float> minRange = (entity, target) -> 3.0F;

   public LeapToTarget(int delayTicks) {
      super(delayTicks);
   }

   public LeapAtTarget<E> minRange(BiFunction<E, LivingEntity, Float> range) {
      this.minRange = range;
      return this;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (!entity.onGround()) {
         return false;
      }

      this.target = BrainUtils.getTargetOfEntity(entity);
      if (!entity.getSensing().hasLineOfSight(this.target)) {
         return false;
      }

      double distance = entity.distanceToSqr(this.target);
      double min = this.minRange.apply(entity, this.target).floatValue();
      if (distance < min * min) {
         return false;
      }

      double max = ((Float)this.leapRange.apply(entity, this.target)).floatValue();
      return distance < max * max;
   }

   protected void start(E entity) {
      super.start(entity);
      if (this.target != null) {
         this.facePoint(entity, this.target.getX(), this.target.getZ());
      }
   }

   private void facePoint(E entity, double x, double y) {
      entity.setYRot((float)(Mth.atan2(y - entity.getZ(), x - entity.getX()) * (180.0 / Math.PI)) - 90.0F);
   }
}
