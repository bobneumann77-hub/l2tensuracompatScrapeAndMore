package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.data.TensuraItemTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/world/inventory/BrewingStandMenu$PotionSlot")
public class MixinBrewingStandMenu {
   @ModifyReturnValue(method = "mayPlaceItem(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
   private static boolean mayPlaceItem(boolean original, ItemStack stack) {
      return stack.is(TensuraItemTags.HIPOKUTE_POTION_CONTAINERS) ? true : original;
   }
}
