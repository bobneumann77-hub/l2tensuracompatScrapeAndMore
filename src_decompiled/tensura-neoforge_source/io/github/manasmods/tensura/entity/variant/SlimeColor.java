package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import io.github.manasmods.tensura.entity.monster.SlimeEntity;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public enum SlimeColor {
   DEFAULT(0),
   RED(1),
   YELLOW(2),
   LIME(3),
   CYAN(4),
   PINK(5),
   WHITE(6),
   BLUE(7),
   GREEN(8),
   ORANGE(9),
   GRAY(10),
   LIGHT_GRAY(11),
   MAGENTA(12),
   PURPLE(13),
   BROWN(14),
   BLACK(15);

   private static final SlimeColor[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(SlimeColor::getId)).toArray(SlimeColor[]::new);
   private final int id;
   public static final Map<SlimeColor, ResourceLocation> LOCATION_BY_VARIANT = (Map<SlimeColor, ResourceLocation>)Util.make(
      Maps.newEnumMap(SlimeColor.class), variant -> {
         variant.put(DEFAULT, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime.png"));
         variant.put(RED, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_red.png"));
         variant.put(YELLOW, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_yellow.png"));
         variant.put(GREEN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_green.png"));
         variant.put(WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_white.png"));
         variant.put(ORANGE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_orange.png"));
         variant.put(MAGENTA, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_magenta.png"));
         variant.put(BLUE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_blue.png"));
         variant.put(LIME, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_lime.png"));
         variant.put(PINK, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_pink.png"));
         variant.put(GRAY, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_gray.png"));
         variant.put(LIGHT_GRAY, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_light_gray.png"));
         variant.put(CYAN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_cyan.png"));
         variant.put(PURPLE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_purple.png"));
         variant.put(BROWN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_brown.png"));
         variant.put(BLACK, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_black.png"));
      }
   );
   public static final Map<SlimeColor, DyeColor> DYE_BY_VARIANT = (Map<SlimeColor, DyeColor>)Util.make(Maps.newEnumMap(SlimeColor.class), variant -> {
      variant.put(DEFAULT, DyeColor.LIGHT_BLUE);
      variant.put(RED, DyeColor.RED);
      variant.put(YELLOW, DyeColor.YELLOW);
      variant.put(GREEN, DyeColor.GREEN);
      variant.put(WHITE, DyeColor.WHITE);
      variant.put(ORANGE, DyeColor.ORANGE);
      variant.put(MAGENTA, DyeColor.MAGENTA);
      variant.put(BLUE, DyeColor.BLUE);
      variant.put(LIME, DyeColor.LIME);
      variant.put(PINK, DyeColor.PINK);
      variant.put(GRAY, DyeColor.GRAY);
      variant.put(LIGHT_GRAY, DyeColor.LIGHT_GRAY);
      variant.put(CYAN, DyeColor.CYAN);
      variant.put(PURPLE, DyeColor.PURPLE);
      variant.put(BROWN, DyeColor.BROWN);
      variant.put(BLACK, DyeColor.BLACK);
   });

   SlimeColor(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static SlimeColor byId(int id) {
      return BY_ID[id % BY_ID.length];
   }

   public static void setVariantFromColor(SlimeEntity slimeEntity, DyeColor color) {
      for (Entry<SlimeColor, DyeColor> slimeVariantItemEntry : DYE_BY_VARIANT.entrySet()) {
         if (color.equals(slimeVariantItemEntry.getValue())) {
            slimeEntity.setColor(slimeVariantItemEntry.getKey());
         }
      }
   }
}
