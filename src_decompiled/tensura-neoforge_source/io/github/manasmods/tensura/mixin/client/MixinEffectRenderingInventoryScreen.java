package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.tensura.data.TensuraTags;
import java.util.Collection;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EffectRenderingInventoryScreen.class)
public class MixinEffectRenderingInventoryScreen {
   @WrapOperation(
      method = "renderEffects(Lnet/minecraft/client/gui/GuiGraphics;II)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getActiveEffects()Ljava/util/Collection;")
   )
   private Collection<MobEffectInstance> onEffectRemoved(LocalPlayer player, Operation<Collection<MobEffectInstance>> original) {
      return ((Collection)original.call(new Object[]{player}))
         .stream()
         .filter(instance -> !instance.getEffect().is(TensuraTags.MobEffects.HIDDEN_ICON))
         .toList();
   }
}
