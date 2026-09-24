package io.github.manasmods.tensura.mixin;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.Tensura;
import io.github.manasmods.tensura.event.TensuraLevelEvents;
import io.github.manasmods.tensura.storage.AreaMagiculeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
   @Shadow
   public abstract Iterable<ServerLevel> getAllLevels();

   @Inject(method = "setInitialSpawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/storage/ServerLevelData;ZZ)V", at = @At("RETURN"))
   private static void preventBadBiomeSpawn(ServerLevel level, ServerLevelData data, boolean bonusChest, boolean debugWorld, CallbackInfo ci) {
      BlockPos spawn = data.getSpawnPos();
      Holder<Biome> currentBiome = level.getBiome(spawn);
      if (currentBiome.is(BiomeTags.IS_SAVANNA) || AreaMagiculeHelper.getMagicule(level, spawn) > 5000.0) {
         Tensura.LOG.info("WorldSpawn in a {}!", currentBiome.getRegisteredName());
         Pair<BlockPos, Holder<Biome>> pair = level.findClosestBiome3d(holder -> !holder.is(BiomeTags.IS_SAVANNA), spawn, 1600, 32, 64);
         if (pair != null) {
            data.setSpawn((BlockPos)pair.getFirst(), 0.0F);
            Tensura.LOG.info("Moved the world spawn to {}!", ((BlockPos)pair.getFirst()).toString());
         }
      }
   }

   @Inject(method = "prepareLevels(Lnet/minecraft/server/level/progress/ChunkProgressListener;)V", at = @At("TAIL"))
   private void levelsLoaded(ChunkProgressListener chunkProgressListener, CallbackInfo ci) {
      this.getAllLevels()
         .forEach(level -> ((TensuraLevelEvents.LevelPreparedEvent)TensuraLevelEvents.LEVEL_PREPARED.invoker()).load(level, level.dimensionTypeRegistration()));
   }
}
