package io.github.manasmods.tensura.world.biome;

import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;

public class BiomeRendererUtils {
   public static float getFogDensity(ClientLevel level, Camera camera, FogMode fogMode, FogType fogType) {
      if (!fogType.equals(FogType.NONE)) {
         return 0.0F;
      } else {
         double cameraHeight = camera.getPosition().y;
         boolean isHell = level.dimension().equals(TensuraDimensions.HELL);
         if (cameraHeight < 60.0 && !isHell) {
            return 0.0F;
         } else {
            Holder<Biome> biome = level.getBiome(camera.getBlockPosition());
            if (!biome.is(TensuraBiomeTags.IS_FOGGY) || !(cameraHeight <= 125.0) && !isHell && fogMode == FogMode.FOG_SKY) {
               return !biome.is(TensuraBiomeTags.HAS_SANDSTORM) || !level.isRaining() && !level.isThundering() ? 0.0F : 1.0F;
            } else {
               return 1.0F;
            }
         }
      }
   }
}
