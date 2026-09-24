package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum PeacockVariant {
   MALE(0),
   FEMALE(1);

   private static final PeacockVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(PeacockVariant::getId)).toArray(PeacockVariant[]::new);
   private final int id;
   public static final Map<PeacockVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<PeacockVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(PeacockVariant.class), variant -> {
         variant.put(MALE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dragon_peacock/dragon_peacock_male.png"));
         variant.put(FEMALE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dragon_peacock/dragon_peacock_female.png"));
      }
   );

   PeacockVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static PeacockVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }
}
