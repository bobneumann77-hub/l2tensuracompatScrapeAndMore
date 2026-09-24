package io.github.manasmods.tensura.entity.ai.navigator;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.navigation.SmoothAmphibiousPathNavigation;

public class DoorOpenAmphibiousNavigation extends SmoothAmphibiousPathNavigation {
   public DoorOpenAmphibiousNavigation(Mob mob, Level level) {
      super(mob, level);
   }

   public boolean prefersShallowSwimming() {
      return true;
   }

   public boolean canOpenDoors() {
      return this.nodeEvaluator.canOpenDoors();
   }

   public void setCanOpenDoors(boolean open) {
      this.nodeEvaluator.setCanOpenDoors(open);
   }

   public boolean canPassDoors() {
      return this.nodeEvaluator.canPassDoors();
   }

   public void setCanPassDoors(boolean pass) {
      this.nodeEvaluator.setCanPassDoors(pass);
   }
}
