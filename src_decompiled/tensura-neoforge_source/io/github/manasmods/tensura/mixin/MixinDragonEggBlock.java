package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.data.TensuraItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DragonEggBlock.class)
public class MixinDragonEggBlock {
   @Inject(
      method = "attack(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void fixedTeleport(BlockState blockState, Level level, BlockPos blockPos, Player player, CallbackInfo ci) {
      if (player.getItemBySlot(EquipmentSlot.MAINHAND).is(TensuraItemTags.CAN_TOUCH_DRAGON_EGG)) {
         ci.cancel();
      }
   }

   @Inject(
      method = "useWithoutItem(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void fixedTeleport(
      BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir
   ) {
      if (player.getItemBySlot(EquipmentSlot.MAINHAND).is(TensuraItemTags.CAN_TOUCH_DRAGON_EGG)) {
         cir.setReturnValue(InteractionResult.PASS);
      }
   }
}
