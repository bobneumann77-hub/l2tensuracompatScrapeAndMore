package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.data.TensuraBiomeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public abstract class MixinNaturalSpawner {
   @Inject(method = "isValidPositionForMob(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;D)Z", at = @At("HEAD"), cancellable = true)
   private static void allowDaylightSpawning(ServerLevel serverLevel, Mob mob, double distance, CallbackInfoReturnable<Boolean> cir) {
      if (mob.getType().is(EntityTypeTags.UNDEAD)) {
         if (serverLevel.getBiome(mob.blockPosition()).is(TensuraBiomeTags.IS_MIASMIC)) {
            if (!(distance > mob.getType().getCategory().getDespawnDistance() * mob.getType().getCategory().getDespawnDistance())
               || !mob.removeWhenFarAway(distance)) {
               cir.setReturnValue(mob.checkSpawnObstruction(serverLevel));
            }
         }
      }
   }
}
