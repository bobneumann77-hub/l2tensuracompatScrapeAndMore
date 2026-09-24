package io.github.manasmods.tensura.entity.ai.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;

public class SwimmableGroundNavigation extends SmoothGroundNavigation {
   public SwimmableGroundNavigation(Mob mob, Level level) {
      super(mob, level);
   }

   public boolean isStableDestination(BlockPos blockPos) {
      return this.canFloat() ? !this.level.getBlockState(blockPos.below()).isAir() : super.isStableDestination(blockPos);
   }
}
