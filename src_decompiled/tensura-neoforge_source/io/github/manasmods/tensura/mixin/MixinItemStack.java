package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.enchantment.SlottingHelper;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class MixinItemStack {
   @Mutable
   @Shadow
   @Final
   public static Codec<ItemStack> CODEC;

   @Redirect(
      method = "<clinit>()V",
      at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/ItemStack;CODEC:Lcom/mojang/serialization/Codec;", opcode = 179)
   )
   private static void replaceCodecAssignment(Codec<ItemStack> originalCodec) {
      CODEC = Codec.lazyInitialized(
         () -> RecordCodecBuilder.create(
            instance -> instance.group(
                  ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                  ExtraCodecs.intRange(1, 999).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                  DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(itemStack -> itemStack.components.asPatch())
               )
               .apply(instance, ItemStack::new)
         )
      );
   }

   @Inject(
      method = "overrideStackedOnOther(Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/inventory/ClickAction;Lnet/minecraft/world/entity/player/Player;)Z",
      at = @At("RETURN"),
      cancellable = true
   )
   private void overrideStackedOnOther(Slot slot, ClickAction clickAction, Player player, CallbackInfoReturnable<Boolean> cir) {
      if (!(Boolean)cir.getReturnValue()) {
         ItemStack stack = (ItemStack)this;
         if (SlottingHelper.getElementalSlots(player.level(), stack) > 0 && SlottingHelper.putToolUponCore(stack, slot, clickAction, player)) {
            cir.setReturnValue(true);
         }
      }
   }

   @Inject(
      method = "overrideOtherStackedOnMe(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/inventory/ClickAction;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/SlotAccess;)Z",
      at = @At("RETURN"),
      cancellable = true
   )
   private void overrideOtherStackedOnMe(
      ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess, CallbackInfoReturnable<Boolean> cir
   ) {
      if (!(Boolean)cir.getReturnValue()) {
         ItemStack stack = (ItemStack)this;
         if (SlottingHelper.getElementalSlots(pPlayer.level(), stack) > 0 && SlottingHelper.putCoreOnTool(stack, pOther, pSlot, pAction, pPlayer, pAccess)) {
            cir.setReturnValue(true);
         }
      }
   }

   @Inject(method = "releaseUsing(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V", at = @At("HEAD"), cancellable = true)
   private void releaseUsing(Level level, LivingEntity livingEntity, int i, CallbackInfo ci) {
      if (SlottingHelper.onRelease((ItemStack)this, livingEntity, i)) {
         ci.cancel();
      }
   }

   @ModifyReturnValue(method = "canBeHurtBy(Lnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("RETURN"))
   private boolean canBeHurtBy(boolean original, DamageSource damageSource) {
      if (!original) {
         return false;
      }

      ItemStack stack = (ItemStack)this;
      return stack.is(TensuraItemTags.INDESTRUCTIBLE_BY_ENVIRONMENTAL_CAUSE) ? damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) : original;
   }
}
