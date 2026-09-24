package io.github.manasmods.tensura.neoforge.mixin;

import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Player.class)
public abstract class MixinPlayer {
   @ModifyArg(
      method = "getDisplayName()Lnet/minecraft/network/chat/Component;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/scores/PlayerTeam;formatNameForTeam(Lnet/minecraft/world/scores/Team;Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;"
      ),
      index = 1
   )
   public Component getName(Component component) {
      Player player = (Player)this;
      if (!player.level().getGameRules().getBoolean(TensuraGameRules.TENSURA_DISPLAY_NAME)) {
         return component;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      return (Component)(existence.getName() == null ? component : Component.literal(existence.getName()).withStyle(component.getStyle()));
   }
}
