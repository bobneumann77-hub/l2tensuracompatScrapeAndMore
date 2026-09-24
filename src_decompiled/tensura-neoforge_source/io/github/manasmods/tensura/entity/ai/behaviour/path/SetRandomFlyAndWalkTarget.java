package io.github.manasmods.tensura.entity.ai.behaviour.path;

import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomFlyingTarget;
import org.jetbrains.annotations.Nullable;

public class SetRandomFlyAndWalkTarget<E extends PathfinderMob & IFlying> extends SetRandomFlyingTarget<E> {
   private final int flyChanceFromWalk;
   private final int flyChanceContinue;

   public SetRandomFlyAndWalkTarget(int flyChanceFromWalk, int flyChanceContinue) {
      this.flyChanceFromWalk = flyChanceFromWalk;
      this.flyChanceContinue = flyChanceContinue;
   }

   public SetRandomFlyAndWalkTarget() {
      this(2, 3);
      this.setRadius(15.0, 15.0);
      this.verticalWeight(entity -> 5);
   }

   @Nullable
   protected Vec3 getTargetPos(E entity) {
      int heightWeight = this.verticalWeight.applyAsInt(entity);
      boolean shouldFly;
      if (entity.onGround()) {
         shouldFly = entity.getRandom().nextInt(this.flyChanceFromWalk) > 0;
      } else {
         shouldFly = entity.getRandom().nextInt(this.flyChanceContinue) > 0 && entity.shouldContinueFlying();
      }

      if (!shouldFly) {
         if (entity.onGround()) {
            entity.setFlying(false);
         }

         Vec3 pos = LandRandomPos.getPos(entity, (int)this.radius.xzRadius(), (int)this.radius.yRadius());
         if (pos != null) {
            return pos;
         }

         heightWeight = Math.abs(heightWeight) * -1;
      }

      entity.setFlying(true);
      Vec3 entityFacing = entity.getViewVector(0.0F);
      Vec3 hoverPos = HoverRandomPos.getPos(
         entity, (int)Math.ceil(this.radius.xzRadius()), (int)Math.ceil(this.radius.yRadius()), entityFacing.x, entityFacing.z, (float) (Math.PI / 2), 3, 1
      );
      return hoverPos != null
         ? hoverPos
         : AirAndWaterRandomPos.getPos(
            entity,
            (int)Math.ceil(this.radius.xzRadius()),
            (int)Math.ceil(this.radius.yRadius()),
            heightWeight,
            entityFacing.x,
            entityFacing.z,
            (float) (Math.PI / 2)
         );
   }
}
