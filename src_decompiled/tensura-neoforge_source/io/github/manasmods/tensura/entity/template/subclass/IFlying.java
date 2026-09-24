package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.ai.controller.FlightMoveController;
import io.github.manasmods.tensura.entity.ai.navigator.IgnoreCollisionFlyingPathNavigation;
import io.github.manasmods.tensura.entity.ai.navigator.SwimmableGroundNavigation;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.pathfinder.PathType;
import net.tslat.smartbrainlib.api.core.navigation.SmoothFlyingPathNavigation;

public interface IFlying extends FlyingAnimal {
   void switchMoveControl(MoveControl var1);

   void switchNavigation(PathNavigation var1);

   void setFlying(boolean var1);

   void setWasFlying(boolean var1);

   boolean wasFlying();

   void setFlyingTick(int var1);

   int getFlyingTick();

   default boolean canFly() {
      return true;
   }

   default boolean canIgnoreCollisionFlight() {
      return false;
   }

   default void initFlying(Mob entity) {
      entity.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      entity.setPathfindingMalus(PathType.WATER, -1.0F);
      entity.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
      entity.setPathfindingMalus(PathType.FENCE, -1.0F);
      this.switchNavigator(entity, false);
   }

   default void switchNavigator(Mob entity, boolean onLand) {
      if (!onLand && !entity.isSleeping()) {
         this.switchMoveControl(new FlightMoveController(entity, true, this.canIgnoreCollisionFlight()));
         this.switchNavigation(this.getFlyingPathNavigation(entity));
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
         SwimmableGroundNavigation navigation = new SwimmableGroundNavigation(entity, entity.level());
         navigation.setCanFloat(true);
         navigation.setCanPassDoors(true);
         this.switchNavigation(navigation);
         this.setWasFlying(false);
         this.onLand();
      }
   }

   default PathNavigation getFlyingPathNavigation(Mob entity) {
      if (this.canIgnoreCollisionFlight()) {
         return new IgnoreCollisionFlyingPathNavigation(entity, entity.level());
      }

      SmoothFlyingPathNavigation navigation = new SmoothFlyingPathNavigation(entity, entity.level());
      navigation.setCanFloat(true);
      navigation.setCanPassDoors(false);
      return navigation;
   }

   default void onLand() {
   }

   default void onTakeOff() {
   }

   default void handleFlying(Mob entity) {
      if (!entity.level().isClientSide()) {
         boolean isFlying = this.isFlying();
         if (isFlying != this.wasFlying()) {
            this.switchNavigator(entity, !isFlying);
         }

         if (isFlying && entity.isAlive()) {
            this.setFlyingTick(this.getFlyingTick() + 1);
            entity.setNoGravity(true);
            if (this.shouldStopFlying(entity)) {
               this.setFlying(false);
            }
         } else {
            this.setFlyingTick(0);
            entity.setNoGravity(false);
         }
      }
   }

   default boolean shouldContinueFlying() {
      return this.canFly() && this.getFlyingTick() < 200;
   }

   default boolean shouldStopFlying(Mob entity) {
      if (!entity.isSleeping() && !entity.isPassenger()) {
         if (entity.onGround() && this.getFlyingTick() > 10) {
            return true;
         } else {
            return !entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.BURDEN))
               ? false
               : !entity.getType().is(TensuraEntityTags.FULL_GRAVITY_CONTROL);
         }
      } else {
         return true;
      }
   }
}
