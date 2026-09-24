package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum BarghestFlameVariant {
   BLUE(0),
   ORANGE(1),
   TEAL(2),
   YELLOW(3),
   RED(4),
   GREEN(5),
   PURPLE(6),
   WHITE(7),
   BLACK(8);

   private static final BarghestFlameVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(BarghestFlameVariant::getId))
      .toArray(BarghestFlameVariant[]::new);
   private final int id;
   public static final Map<BarghestFlameVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<BarghestFlameVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(BarghestFlameVariant.class), variant -> {
         variant.put(BLUE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest_flame_blue.png"));
         variant.put(ORANGE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest_flame_orange.png"));
         variant.put(TEAL, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest_flame_teal.png"));
         variant.put(YELLOW, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest_flame_yellow.png"));
         variant.put(RED, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest_flame_red.png"));
         variant.put(GREEN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest_flame_green.png"));
         variant.put(PURPLE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest_flame_purple.png"));
         variant.put(WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest_flame_white.png"));
         variant.put(BLACK, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest_flame_black.png"));
      }
   );

   BarghestFlameVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static BarghestFlameVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }
}
