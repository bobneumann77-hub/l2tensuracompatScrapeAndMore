package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum HoundDogVariant {
   DEFAULT(0),
   EVOLVED(1);

   private static final HoundDogVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(HoundDogVariant::getId))
      .toArray(HoundDogVariant[]::new);
   private final int id;
   public static final Map<HoundDogVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<HoundDogVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(HoundDogVariant.class), variant -> {
         variant.put(DEFAULT, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hound_dog/hound_dog.png"));
         variant.put(EVOLVED, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hound_dog/hound_dog_snake.png"));
      }
   );

   HoundDogVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static HoundDogVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }
}
