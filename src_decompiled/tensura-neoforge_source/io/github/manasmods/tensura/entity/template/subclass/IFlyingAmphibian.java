package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.tensura.entity.ai.controller.FlightMoveController;
import io.github.manasmods.tensura.entity.ai.navigator.DoorOpenAmphibiousNavigation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.pathfinder.PathType;
import net.tslat.smartbrainlib.api.core.navigation.SmoothFlyingPathNavigation;

public interface IFlyingAmphibian extends IFlying, IAmphibian {
   @Override
   void setLandNavigating(boolean var1);

   @Override
   boolean isLandNavigating();

   @Override
   default void initFlying(Mob entity) {
      entity.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      entity.setPathfindingMalus(PathType.FENCE, -1.0F);
      entity.setPathfindingMalus(PathType.WATER, 0.0F);
      entity.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
      if (this.canOpenDoor()) {
         entity.setPathfindingMalus(PathType.DOOR_WOOD_CLOSED, 0.0F);
      }

      this.switchNavigator(entity, false, true);
   }

   @Override
   default void initAmphibian(Mob entity) {
      this.initFlying(entity);
   }

   default void switchNavigator(Mob entity, boolean inWater, boolean onLand) {
      if (onLand || entity.isSleeping()) {
         this.switchNavigator(entity, true);
      } else if (inWater) {
         this.switchMoveControl(new SmoothSwimmingMoveControl(entity, 85, 10, 1.0F, 0.75F, true));
         this.updateNavigator(entity, false);
         this.setLandNavigating(false);
         this.setWasFlying(false);
      } else {
         this.switchNavigator(entity, false);
      }
   }

   @Override
   default void switchNavigator(Mob entity, boolean onLand) {
      if (!onLand && !entity.isSleeping() && this.canFly()) {
         this.switchMoveControl(new FlightMoveController(entity, true));
         this.updateNavigator(entity, true);
         this.setLandNavigating(false);
         this.setWasFlying(true);
         this.onTakeOff();
      } else {
         this.switchMoveControl(new MoveControl(entity) {
            public void tick() {
               if (!entity.isSleeping()) {
                  super.tick();
               }
            }
         });
         this.updateNavigator(entity, false);
         this.setLandNavigating(true);
         this.setWasFlying(false);
         this.onLand();
      }
   }

   default void updateNavigator(Mob entity, boolean flying) {
      if (flying) {
         FlyingPathNavigation navigation = new SmoothFlyingPathNavigation(entity, entity.level());
         navigation.setCanOpenDoors(this.canOpenDoor());
         this.switchNavigation(navigation);
      } else {
         DoorOpenAmphibiousNavigation navigation = new DoorOpenAmphibiousNavigation(entity, entity.level());
         navigation.setCanOpenDoors(this.canOpenDoor());
         this.switchNavigation(navigation);
      }
   }

   @Override
   default void handleFlying(Mob entity) {
      if (!entity.level().isClientSide()) {
         boolean isFlying = this.isFlying() && this.canFly();
         if (isFlying && !this.wasFlying()) {
            this.switchNavigator(entity, false, false);
         } else if (!isFlying) {
            boolean onLand = !entity.isInWaterOrBubble();
            if (!onLand && this.isLandNavigating()) {
               this.switchNavigator(entity, true, false);
            } else if (onLand && !this.isLandNavigating()) {
               this.switchNavigator(entity, false, true);
            }
         }

         if (isFlying && !entity.isInWaterOrBubble()) {
            this.setFlyingTick(this.getFlyingTick() + 1);
            entity.setNoGravity(true);
            if (this.shouldStopFlying(entity)) {
               this.setFlying(false);
            }
         } else {
            this.setFlyingTick(0);
            entity.setNoGravity(false);
         }

         if (entity.isInWaterOrBubble()) {
            this.setSwimmingTick(this.getSwimmingTick() + 1);
         } else {
            this.setSwimmingTick(this.getSwimmingTick() - 1);
         }
      }
   }

   @Override
   default boolean shouldStayInWater(Mob mob) {
      return this.prefersOnLand() ? false : IAmphibian.super.shouldStayInWater(mob);
   }

   default boolean canOpenDoor() {
      return false;
   }

   default boolean prefersOnLand() {
      return false;
   }
}
