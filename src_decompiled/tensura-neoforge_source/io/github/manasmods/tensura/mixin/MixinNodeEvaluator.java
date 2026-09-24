package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.block.template.IMagicEngineEvaluatorState;
import io.github.manasmods.tensura.util.MagicEngineHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NodeEvaluator.class)
public abstract class MixinNodeEvaluator implements IMagicEngineEvaluatorState {
   @Shadow
   protected Mob mob;
   @Unique
   private boolean tensura$skipBlocking = false;

   @Inject(method = "prepare(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;)V", at = @At("TAIL"))
   private void tensura$cacheBlockingState(PathNavigationRegion region, Mob mob, CallbackInfo ci) {
      this.tensura$skipBlocking = MagicEngineHelper.shouldSkipForMob(mob);
   }

   @Override
   public boolean tensura$shouldSkipBlocking() {
      return this.tensura$skipBlocking;
   }

   @Override
   public Mob tensura$getMob() {
      return this.mob;
   }
}
