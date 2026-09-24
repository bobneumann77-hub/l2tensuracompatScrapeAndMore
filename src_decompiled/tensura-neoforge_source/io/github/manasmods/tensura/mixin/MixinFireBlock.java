package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireBlock.class)
public class MixinFireBlock {
   @WrapOperation(
      method = "tick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/FireBlock;isValidFireLocation(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"
      )
   )
   private boolean isValidFireLocation(FireBlock fire, BlockGetter blockGetter, BlockPos pos, Operation<Boolean> original) {
      return !ObjectSelectionHelper.CONFIG.specialBiomeFireSpread && ((ServerLevel)blockGetter).getBiome(pos).is(TensuraBiomeTags.IS_FIRE_SPREAD_DISABLED)
         ? false
         : (Boolean)original.call(new Object[]{fire, blockGetter, pos});
   }
}
