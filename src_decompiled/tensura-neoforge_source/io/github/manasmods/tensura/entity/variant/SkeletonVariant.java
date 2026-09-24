package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum SkeletonVariant {
   DEFAULT(0),
   HOT(1),
   COLD(2),
   MOSSY(3);

   private static final SkeletonVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(SkeletonVariant::getId))
      .toArray(SkeletonVariant[]::new);
   private final int id;
   public static final Map<SkeletonVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<SkeletonVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(SkeletonVariant.class), variant -> {
         variant.put(DEFAULT, ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png"));
         variant.put(HOT, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/skeleton/parched.png"));
         variant.put(COLD, ResourceLocation.withDefaultNamespace("textures/entity/skeleton/stray.png"));
         variant.put(MOSSY, ResourceLocation.withDefaultNamespace("textures/entity/skeleton/bogged.png"));
      }
   );
   public static final Map<SkeletonVariant, ResourceLocation> OVERLAY_BY_VARIANT = (Map<SkeletonVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(SkeletonVariant.class), variant -> {
         variant.put(HOT, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/skeleton/parched_overlay.png"));
         variant.put(COLD, ResourceLocation.withDefaultNamespace("textures/entity/skeleton/stray_overlay.png"));
         variant.put(MOSSY, ResourceLocation.withDefaultNamespace("textures/entity/skeleton/bogged_overlay.png"));
      }
   );

   SkeletonVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static SkeletonVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }
}
