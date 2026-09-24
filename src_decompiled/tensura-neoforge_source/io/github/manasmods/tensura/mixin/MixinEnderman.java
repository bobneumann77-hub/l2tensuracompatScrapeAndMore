package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.data.TensuraItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public class MixinEnderman {
   @Inject(method = "isLookingAtMe(Lnet/minecraft/world/entity/player/Player;)Z", at = @At("HEAD"), cancellable = true)
   private void isLookingAtMe(Player player, CallbackInfoReturnable<Boolean> cir) {
      if (player.getItemBySlot(EquipmentSlot.HEAD).is(TensuraItemTags.CAN_LOOK_AT_ENDERMAN)) {
         cir.setReturnValue(false);
         cir.cancel();
      }
   }
}
