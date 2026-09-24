package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.item.misc.HipokuteFlowerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowerPotBlock.class)
public abstract class MixinFlowerPotBlock {
   @Shadow
   protected abstract boolean isEmpty();

   @Inject(
      method = "useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/ItemInteractionResult;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void use(
      ItemStack itemStack,
      BlockState blockState,
      Level pLevel,
      BlockPos pPos,
      Player pPlayer,
      InteractionHand pHand,
      BlockHitResult blockHitResult,
      CallbackInfoReturnable<ItemInteractionResult> cir
   ) {
      ItemStack stack = pPlayer.getItemInHand(pHand);
      if (stack.getItem() instanceof HipokuteFlowerItem item && this.isEmpty()) {
         pLevel.setBlock(pPos, item.pottedFlower.get().defaultBlockState(), 3);
         pLevel.gameEvent(pPlayer, GameEvent.BLOCK_CHANGE, pPos);
         pPlayer.awardStat(Stats.POT_FLOWER);
         itemStack.consume(1, pPlayer);
         cir.setReturnValue(ItemInteractionResult.sidedSuccess(pLevel.isClientSide));
      }
   }
}
