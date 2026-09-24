package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum OneEyedOwlVariant {
   GREEN(0),
   BLACK(1),
   BROWN(2),
   DARK_BROWN(3),
   ORANGE(4),
   RED(5),
   YELLOW(6);

   private static final OneEyedOwlVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(OneEyedOwlVariant::getId))
      .toArray(OneEyedOwlVariant[]::new);
   private final int id;
   public static final Map<OneEyedOwlVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<OneEyedOwlVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(OneEyedOwlVariant.class), variant -> {
         variant.put(GREEN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/one_eyed_owl/one_eyed_owl.png"));
         variant.put(BLACK, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/one_eyed_owl/one_eyed_owl_black.png"));
         variant.put(BROWN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/one_eyed_owl/one_eyed_owl_brown.png"));
         variant.put(DARK_BROWN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/one_eyed_owl/one_eyed_owl_dark_brown.png"));
         variant.put(ORANGE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/one_eyed_owl/one_eyed_owl_orange.png"));
         variant.put(RED, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/one_eyed_owl/one_eyed_owl_red.png"));
         variant.put(YELLOW, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/one_eyed_owl/one_eyed_owl_yellow.png"));
      }
   );

   OneEyedOwlVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static OneEyedOwlVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }
}
