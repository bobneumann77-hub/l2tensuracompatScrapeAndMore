package io.github.manasmods.tensura.mixin.warden;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.ability.SkillUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net/minecraft/world/entity/monster/warden/Warden$VibrationUser")
public abstract class MixinWardenVibrantUser {
   @ModifyReturnValue(
      method = "canReceiveVibration(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)Z",
      at = @At("RETURN")
   )
   public boolean shouldTarget(boolean original, ServerLevel serverLevel, BlockPos blockPos, Holder<GameEvent> holder, Context context) {
      if (!original) {
         return false;
      }

      Entity entity = context.sourceEntity();
      return entity != null && SkillUtils.canBlockSoundDetect(entity) ? false : original;
   }
}
