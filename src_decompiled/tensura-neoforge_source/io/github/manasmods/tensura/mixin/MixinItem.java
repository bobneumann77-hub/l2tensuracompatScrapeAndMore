package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.enchantment.SlottingHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public class MixinItem {
   @ModifyReturnValue(method = "isFoil(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
   private boolean isFoil(boolean original, ItemStack stack) {
      return !original ? false : TensuraEnchantmentHelper.hasNotTag(stack, TensuraTags.Enchantments.ENGRAVING);
   }

   @ModifyReturnValue(method = "getTooltipImage(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional;", at = @At("RETURN"))
   private Optional<TooltipComponent> getTooltipImage(Optional<TooltipComponent> original, ItemStack itemStack) {
      return original.isPresent() ? original : SlottingHelper.tooltipCore(itemStack);
   }

   @ModifyReturnValue(method = "getUseAnimation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/UseAnim;", at = @At("RETURN"))
   private UseAnim getUseAnimation(UseAnim original, ItemStack pStack) {
      if (!original.equals(UseAnim.NONE)) {
         return original;
      } else {
         return SlottingHelper.getContentSize(pStack) > 0 ? UseAnim.BOW : original;
      }
   }

   @ModifyReturnValue(method = "getUseDuration(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"))
   private int getUseDuration(int original, ItemStack itemStack, LivingEntity livingEntity) {
      if (original >= 16) {
         return original;
      } else {
         return SlottingHelper.getContentSize(itemStack) > 0 ? 36000 : original;
      }
   }
}
