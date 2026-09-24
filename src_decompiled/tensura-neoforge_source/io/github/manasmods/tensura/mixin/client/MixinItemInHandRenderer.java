package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {
   @WrapOperation(
      method = "tick()V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
      )
   )
   public boolean doesItemMatches(ItemStack oldStack, ItemStack newStack, Operation<Boolean> original) {
      if ((Boolean)original.call(new Object[]{oldStack, oldStack})) {
         return true;
      } else {
         return oldStack.getItem() == newStack.getItem() && oldStack.getCount() == newStack.getCount()
            ? this.tensura$hasSameComponents(oldStack, newStack)
            : false;
      }
   }

   @Unique
   private boolean tensura$hasSameComponents(ItemStack oldStack, ItemStack newStack) {
      DataComponentPatch oldPatch = oldStack.getComponentsPatch()
         .forget(dataComponentType -> dataComponentType == TensuraDataComponents.EP.get() || dataComponentType == TensuraDataComponents.EP_DURABILITY.get());
      DataComponentPatch newPatch = newStack.getComponentsPatch()
         .forget(dataComponentType -> dataComponentType == TensuraDataComponents.EP.get() || dataComponentType == TensuraDataComponents.EP_DURABILITY.get());
      return oldPatch.equals(newPatch);
   }
}
