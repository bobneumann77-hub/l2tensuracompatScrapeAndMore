package io.github.manasmods.tensura.neoforge.mixin;

import io.github.manasmods.tensura.data.TensuraBlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IBlockExtension.class)
public interface MixinIBlockExtension {
   @Inject(
      method = "canStickTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z",
      at = @At("HEAD"),
      cancellable = true,
      remap = false
   )
   private void canStickTo(BlockState state, BlockState other, CallbackInfoReturnable<Boolean> cir) {
      if (state.is(TensuraBlockTags.STICKY_BLOCKS) && other.is(TensuraBlockTags.STICKY_BLOCKS) && !state.getBlock().equals(other.getBlock())) {
         cir.setReturnValue(false);
      }
   }
}
