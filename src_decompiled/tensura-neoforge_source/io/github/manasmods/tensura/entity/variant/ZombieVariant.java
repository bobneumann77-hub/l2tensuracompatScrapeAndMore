package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum ZombieVariant {
   DEFAULT(0),
   HOT(1),
   COLD(2),
   MOSSY(3);

   private static final ZombieVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(ZombieVariant::getId)).toArray(ZombieVariant[]::new);
   private final int id;
   public static final Map<ZombieVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<ZombieVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(ZombieVariant.class), variant -> {
         variant.put(DEFAULT, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/zombie/zombie.png"));
         variant.put(HOT, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/zombie/husk.png"));
         variant.put(COLD, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/zombie/zombie.png"));
         variant.put(MOSSY, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/zombie/drowned.png"));
      }
   );

   ZombieVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static ZombieVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }
}
