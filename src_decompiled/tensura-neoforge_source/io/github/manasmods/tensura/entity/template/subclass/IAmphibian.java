package io.github.manasmods.tensura.entity.template.subclass;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;
import net.tslat.smartbrainlib.api.core.navigation.SmoothAmphibiousPathNavigation;

public interface IAmphibian {
   void switchMoveControl(MoveControl var1);

   void switchNavigation(PathNavigation var1);

   void setLandNavigating(boolean var1);

   boolean isLandNavigating();

   void setSwimmingTick(int var1);

   int getSwimmingTick();

   default void initAmphibian(Mob entity) {
      entity.setPathfindingMalus(PathType.WATER, 0.0F);
      entity.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      this.switchMovementController(entity, false);
      this.switchNavigation(new SmoothAmphibiousPathNavigation(entity, entity.level()));
   }

   default void switchMovementController(Mob entity, boolean onLand) {
      if (!onLand && !entity.isSleeping()) {
         this.switchMoveControl(new SmoothSwimmingMoveControl(entity, 85, 10, 1.0F, 0.75F, true));
         this.setLandNavigating(false);
      } else {
         this.switchMoveControl(new MoveControl(entity) {
            public void tick() {
               if (!entity.isSleeping()) {
                  super.tick();
               }
            }
         });
         this.setLandNavigating(true);
      }
   }

   default void handleSwimming(Mob entity) {
      boolean onLand = !entity.isInWaterOrBubble();
      if (!onLand && this.isLandNavigating()) {
         this.switchMovementController(entity, false);
      }

      if (onLand && !this.isLandNavigating()) {
         this.switchMovementController(entity, true);
      }

      if (!entity.level().isClientSide()) {
         if (entity.isInWaterOrBubble()) {
            this.setSwimmingTick(this.getSwimmingTick() + 1);
         } else {
            this.setSwimmingTick(this.getSwimmingTick() - 1);
         }
      }
   }

   default boolean shouldFindWater(Mob mob) {
      return this.shouldStayInWater(mob) && this.getSwimmingTick() <= -1000;
   }

   default boolean shouldStayInWater(Mob mob) {
      return mob.getTarget() != null && !mob.getTarget().isInWater() ? false : this.getSwimmingTick() <= 600;
   }
}
