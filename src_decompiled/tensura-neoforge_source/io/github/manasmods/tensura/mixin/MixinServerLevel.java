package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.event.TensuraLevelEvents;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel extends Level {
   protected MixinServerLevel(
      WritableLevelData writableLevelData,
      ResourceKey<Level> resourceKey,
      RegistryAccess registryAccess,
      Holder<DimensionType> holder,
      Supplier<ProfilerFiller> supplier,
      boolean bl,
      boolean bl2,
      long l,
      int i
   ) {
      super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
   }

   @ModifyReturnValue(method = "getEntityOrPart(I)Lnet/minecraft/world/entity/Entity;", at = @At("RETURN"))
   public Entity getEntityParts(Entity original, int id) {
      return original != null ? original : (Entity)((ServerLevel)this).tensura$getParts().get(id);
   }

   @Inject(
      method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerLevel;getProfiler()Lnet/minecraft/util/profiling/ProfilerFiller;",
         shift = Shift.AFTER
      )
   )
   private void onPreTickChunk(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo ci) {
      ProfilerFiller profiler = this.getProfiler();
      profiler.push("chunk_tick_pre");
      ((TensuraLevelEvents.ChunkTickEvent)TensuraLevelEvents.CHUNK_TICK_PRE.invoker()).tick((ServerLevel)this, pChunk);
      profiler.pop();
   }

   @Inject(method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V", at = @At("RETURN"))
   private void onPostTickChunk(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo ci) {
      ProfilerFiller profiler = this.getProfiler();
      profiler.push("chunk_tick_post");
      ((TensuraLevelEvents.ChunkTickEvent)TensuraLevelEvents.CHUNK_TICK_POST.invoker()).tick((ServerLevel)this, pChunk);
      profiler.pop();
   }
}
