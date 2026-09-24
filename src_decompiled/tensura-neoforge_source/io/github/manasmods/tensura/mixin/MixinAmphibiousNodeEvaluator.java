package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.block.template.IMagicEngineEvaluatorState;
import io.github.manasmods.tensura.util.MagicEngineHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AmphibiousNodeEvaluator.class)
public abstract class MixinAmphibiousNodeEvaluator {
   @ModifyReturnValue(
      method = "getPathType(Lnet/minecraft/world/level/pathfinder/PathfindingContext;III)Lnet/minecraft/world/level/pathfinder/PathType;",
      at = @At("RETURN")
   )
   private PathType tensura$blockMagiculeZone(PathType original, PathfindingContext context, int x, int y, int z) {
      IMagicEngineEvaluatorState state = (IMagicEngineEvaluatorState)this;
      if (state.tensura$shouldSkipBlocking()) {
         return original;
      }

      if (original == PathType.BLOCKED) {
         return original;
      }

      Mob mob = state.tensura$getMob();
      return mob != null && MagicEngineHelper.isNodeInZone(mob, x, y, z) ? PathType.BLOCKED : original;
   }
}
