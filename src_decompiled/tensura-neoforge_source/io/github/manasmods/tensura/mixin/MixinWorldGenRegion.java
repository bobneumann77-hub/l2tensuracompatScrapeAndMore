package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.event.TensuraEntityEvents;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldGenRegion.class)
public abstract class MixinWorldGenRegion {
   @Inject(
      method = "addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
      at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/level/chunk/ChunkAccess;addEntity(Lnet/minecraft/world/entity/Entity;)V"),
      cancellable = true
   )
   private void addFreshEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (((TensuraEntityEvents.AddFreshEvent)TensuraEntityEvents.ADD_FRESH.invoker()).add(entity, (WorldGenRegion)this).isFalse()) {
         cir.setReturnValue(true);
         cir.cancel();
      }
   }
}
