package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum CattledeerVariant {
   DEFAULT(0),
   SPOTTED(1),
   HOT(2),
   COLD(3);

   private static final CattledeerVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(CattledeerVariant::getId))
      .toArray(CattledeerVariant[]::new);
   private final int id;
   public static final Map<CattledeerVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<CattledeerVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(CattledeerVariant.class), variant -> {
         variant.put(DEFAULT, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/cattledeer/cattledeer.png"));
         variant.put(SPOTTED, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/cattledeer/cattledeer_spotted.png"));
         variant.put(HOT, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/cattledeer/cattledeer_hot.png"));
         variant.put(COLD, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/cattledeer/cattledeer_cold.png"));
      }
   );

   CattledeerVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static CattledeerVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }
}
