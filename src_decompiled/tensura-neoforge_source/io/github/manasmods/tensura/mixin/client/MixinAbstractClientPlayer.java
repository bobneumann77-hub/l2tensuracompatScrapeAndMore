package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.event.TensuraLocalPlayerEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer {
   @ModifyReturnValue(method = "getFieldOfViewModifier()F", at = @At("RETURN"))
   private float fovModifierEvent(float original) {
      Player player = Minecraft.getInstance().player;
      return ((TensuraLocalPlayerEvents.TensuraFovModifierEvent)TensuraLocalPlayerEvents.FOV_MODIFIER_EVENT.invoker()).fovModifier(player, original);
   }
}
