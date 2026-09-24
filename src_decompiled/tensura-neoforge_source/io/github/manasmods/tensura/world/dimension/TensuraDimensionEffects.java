package io.github.manasmods.tensura.world.dimension;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TensuraDimensionEffects {
   public static class Hell extends DimensionSpecialEffects {
      public Hell() {
         super(128.0F, false, SkyType.NONE, false, false);
      }

      @NotNull
      public Vec3 getBrightnessDependentFogColor(Vec3 vec3, float v) {
         return vec3;
      }

      public boolean isFoggyAt(int i, int i1) {
         return false;
      }
   }
}
