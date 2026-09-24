package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum BasiliskVariant {
   EAGLE(0),
   FALCON(1),
   CHICKEN(2),
   ROOSTER(3);

   private static final BasiliskVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(BasiliskVariant::getId))
      .toArray(BasiliskVariant[]::new);
   private final int id;
   public static final Map<BasiliskVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<BasiliskVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(BasiliskVariant.class), variant -> {
         variant.put(EAGLE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/basilisk/basilisk_eagle.png"));
         variant.put(FALCON, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/basilisk/basilisk_falcon.png"));
         variant.put(CHICKEN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/basilisk/basilisk_chicken.png"));
         variant.put(ROOSTER, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/basilisk/basilisk_rooster.png"));
      }
   );

   BasiliskVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static BasiliskVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }
}
