package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum HoverLizardVariant {
   YELLOW(0),
   BLUE(1),
   GREEN(2),
   PURPLE(3),
   RED(4);

   private static final HoverLizardVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(HoverLizardVariant::getId))
      .toArray(HoverLizardVariant[]::new);
   private final int id;
   public static final Map<HoverLizardVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<HoverLizardVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(HoverLizardVariant.class), variant -> {
         variant.put(YELLOW, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hover_lizard/hover_lizard.png"));
         variant.put(BLUE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hover_lizard/hover_lizard_blue.png"));
         variant.put(GREEN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hover_lizard/hover_lizard_green.png"));
         variant.put(PURPLE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hover_lizard/hover_lizard_purple.png"));
         variant.put(RED, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hover_lizard/hover_lizard_red.png"));
      }
   );

   HoverLizardVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static HoverLizardVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }
}
