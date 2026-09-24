package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseContainerBlockEntity.class)
public abstract class MixinBaseContainerBlockEntity {
   @Inject(method = "setItem(ILnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
   private void setItem(int i, ItemStack itemStack, CallbackInfo ci) {
      if (itemStack.is(TensuraItemTags.DUMMY_REMOVE_ON_LEAVING_HAND)
         && (Boolean)itemStack.getOrDefault((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), false)) {
         itemStack.setCount(0);
      }
   }
}
