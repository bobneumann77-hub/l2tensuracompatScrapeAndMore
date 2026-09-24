package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleContainer.class)
public abstract class MixinSimpleContainer {
   @Inject(method = "setItem(ILnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
   private void setItem(int i, ItemStack itemStack, CallbackInfo ci) {
      if (itemStack.is(TensuraItemTags.DUMMY_REMOVE_ON_LEAVING_HAND)
         && (Boolean)itemStack.getOrDefault((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), false)) {
         if ((SimpleContainer)this instanceof TensuraHumanoidEntity.HumanoidContainer) {
            return;
         }

         itemStack.setCount(0);
      }
   }
}
