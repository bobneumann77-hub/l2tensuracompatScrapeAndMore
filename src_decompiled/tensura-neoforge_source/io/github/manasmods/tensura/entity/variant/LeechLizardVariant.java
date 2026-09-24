package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum LeechLizardVariant {
   GREEN(0),
   BROWN(1),
   TAN(2),
   WHITE(3);

   private static final LeechLizardVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(LeechLizardVariant::getId))
      .toArray(LeechLizardVariant[]::new);
   private final int id;
   public static final Map<LeechLizardVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<LeechLizardVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(LeechLizardVariant.class), variant -> {
         variant.put(GREEN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/leech_lizard/leech_lizard.png"));
         variant.put(BROWN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/leech_lizard/leech_lizard_brown.png"));
         variant.put(TAN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/leech_lizard/leech_lizard_tan.png"));
         variant.put(WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/leech_lizard/leech_lizard_white.png"));
      }
   );

   LeechLizardVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static LeechLizardVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }
}
