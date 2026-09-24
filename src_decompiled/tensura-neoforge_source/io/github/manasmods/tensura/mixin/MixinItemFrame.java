package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemFrame.class)
public abstract class MixinItemFrame {
   @WrapOperation(
      method = "interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ItemFrame;setItem(Lnet/minecraft/world/item/ItemStack;)V")
   )
   public void setItem(ItemFrame frame, ItemStack itemStack, Operation<Void> original) {
      if (!itemStack.is(TensuraItemTags.DUMMY_REMOVE_ON_LEAVING_HAND)
         || !(Boolean)itemStack.getOrDefault((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), false)) {
         original.call(new Object[]{frame, itemStack});
      }
   }
}
