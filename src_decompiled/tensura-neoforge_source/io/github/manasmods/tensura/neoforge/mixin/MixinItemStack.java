package io.github.manasmods.tensura.neoforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemStack.class)
public class MixinItemStack {
   @ModifyArg(
      method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"
      )
   )
   private int hurtAndBreak(int i, @Local(ordinal = 0, argsOnly = true) LivingEntity owner, @Local(ordinal = 0, argsOnly = true) EquipmentSlot slot) {
      ItemStack stack = (ItemStack)this;
      Changeable<Integer> durability = Changeable.of(i);
      return ((TensuraEntityEvents.PreItemHurtEvent)TensuraEntityEvents.PRE_ITEM_HURT_EVENT.invoker()).hurt(stack, owner, slot, durability).isFalse()
         ? 0
         : (Integer)durability.get();
   }
}
