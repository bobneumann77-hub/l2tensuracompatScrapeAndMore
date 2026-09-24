package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.data.TensuraTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/world/inventory/GrindstoneMenu$2")
public abstract class MixinGrindstoneFirstRepairSlot {
   @ModifyReturnValue(method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
   private boolean mayPlace(boolean original, ItemStack itemStack) {
      if (!original) {
         return false;
      } else {
         return EnchantmentHelper.hasTag(itemStack, TensuraTags.Enchantments.SEALING_CURSE) ? false : original;
      }
   }
}
