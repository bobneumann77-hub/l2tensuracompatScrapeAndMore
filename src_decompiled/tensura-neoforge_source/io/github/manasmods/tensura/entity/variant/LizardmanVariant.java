package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import io.github.manasmods.tensura.entity.monster.LizardmanEntity;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import lombok.Generated;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum LizardmanVariant {
   GREEN(0),
   BLUE(1),
   PURPLE(2),
   YELLOW(3),
   RED(4),
   DARK_GREEN(5);

   private static final LizardmanVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(LizardmanVariant::getId))
      .toArray(LizardmanVariant[]::new);
   private final int id;
   public static final Map<LizardmanVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<LizardmanVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(LizardmanVariant.class), variant -> {
         variant.put(GREEN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/lizardman_green.png"));
         variant.put(BLUE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/lizardman_blue.png"));
         variant.put(PURPLE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/lizardman_purple.png"));
         variant.put(YELLOW, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/lizardman_yellow.png"));
         variant.put(RED, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/lizardman_red.png"));
         variant.put(DARK_GREEN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/lizardman_dark_green.png"));
      }
   );

   LizardmanVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static LizardmanVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }

   public enum Hair {
      BALD(0, ResourceLocation.fromNamespaceAndPath("tensura", "textures/blank_texture.png")),
      LONG(1, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/hair/lizardman_hair_long.png")),
      SHORT(2, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/hair/lizardman_hair_short.png")),
      SHAGGY(3, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/hair/lizardman_hair_shaggy.png")),
      TWINTAIL(4, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/hair/lizardman_hair_twintail.png")),
      HELMET(5, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/clothes/lizardman_helmet.png")),
      HOOD(6, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/clothes/lizardman_hood.png"));

      private static final LizardmanVariant.Hair[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(LizardmanVariant.Hair::getId))
         .toArray(LizardmanVariant.Hair[]::new);
      private final int id;
      private final ResourceLocation location;

      Hair(int id, ResourceLocation location) {
         this.id = id;
         this.location = location;
      }

      public static LizardmanVariant.Hair byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(LizardmanEntity entity) {
         return ((LizardmanVariant.Hair)Util.getRandom(values(), entity.getRandom())).getId();
      }

      @Generated
      public int getId() {
         return this.id;
      }

      @Generated
      public ResourceLocation getLocation() {
         return this.location;
      }
   }

   public enum Top {
      SHIRT_LONG(0, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/clothes/lizardman_shirt_long.png")),
      SHIRT_SHORT(1, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/clothes/lizardman_shirt_short.png")),
      SHIRT_SLEEVELESS(2, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/clothes/lizardman_shirt_sleeveless.png")),
      CAPE(3, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/clothes/lizardman_cape.png"));

      private static final LizardmanVariant.Top[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(LizardmanVariant.Top::getId))
         .toArray(LizardmanVariant.Top[]::new);
      private final int id;
      private final ResourceLocation location;

      Top(int id, ResourceLocation location) {
         this.id = id;
         this.location = location;
      }

      public static LizardmanVariant.Top byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(LizardmanEntity entity) {
         return ((LizardmanVariant.Top)Util.getRandom(values(), entity.getRandom())).getId();
      }

      @Generated
      public int getId() {
         return this.id;
      }

      @Generated
      public ResourceLocation getLocation() {
         return this.location;
      }
   }
}
