package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer {
   @Inject(method = "startSleeping(Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
   public void startSleeping(BlockPos blockPos, CallbackInfo ci) {
      Player player = (Player)this;
      if (player.level().isThundering()) {
         long dayTime = player.level().getDayTime() % 24000L;
         if (dayTime >= 12523L && dayTime <= 23477L) {
            IEffect effect = TensuraStorages.getEffectFrom(player);
            effect.setSleptAtThunderNight(true);
         }
      }
   }
}
