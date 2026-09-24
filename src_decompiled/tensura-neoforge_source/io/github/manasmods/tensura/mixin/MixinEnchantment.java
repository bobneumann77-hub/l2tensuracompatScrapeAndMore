package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.manasmods.tensura.data.TensuraTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Enchantment.class)
public class MixinEnchantment {
   @Unique
   private static final Style INHERITANCE_STYLE = Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE);
   @Unique
   private static final Style ENGRAVING_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_AQUA);

   @ModifyArg(
      method = "getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/network/chat/ComponentUtils;mergeStyles(Lnet/minecraft/network/chat/MutableComponent;Lnet/minecraft/network/chat/Style;)Lnet/minecraft/network/chat/MutableComponent;",
         ordinal = 1
      ),
      index = 1
   )
   private static Style getNameColor(Style style, @Local(ordinal = 0, argsOnly = true) Holder<Enchantment> holder) {
      if (holder.is(TensuraTags.Enchantments.INHERITANCE_ENGRAVING)) {
         return INHERITANCE_STYLE;
      } else {
         return holder.is(TensuraTags.Enchantments.ENGRAVING) ? ENGRAVING_STYLE : style;
      }
   }
}
