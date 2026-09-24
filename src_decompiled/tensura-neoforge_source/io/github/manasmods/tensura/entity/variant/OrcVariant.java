package io.github.manasmods.tensura.entity.variant;

import com.google.common.collect.Maps;
import io.github.manasmods.tensura.entity.monster.OrcEntity;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import lombok.Generated;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public enum OrcVariant {
   HAM(0),
   HONEY(1),
   GLAZED(2),
   BAKED(3),
   ROYAL(4),
   ROYAL_LORD(5);

   private static final OrcVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(OrcVariant::getId)).toArray(OrcVariant[]::new);
   private final int id;
   public static final Map<OrcVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<OrcVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(OrcVariant.class), variant -> {
         variant.put(HAM, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/orc_ham.png"));
         variant.put(HONEY, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/orc_honey.png"));
         variant.put(GLAZED, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/orc_glazed.png"));
         variant.put(BAKED, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/orc_baked.png"));
         variant.put(ROYAL, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/orc_royal.png"));
         variant.put(ROYAL_LORD, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/orc_royal.png"));
      }
   );

   OrcVariant(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static OrcVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }

   public enum Neck {
      EMPTY(0, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/clothes/empty.png")),
      NECKWRAP(1, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/clothes/orc_neckwrap.png")),
      SIDECAPE(2, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/clothes/orc_sidecape.png"));

      private static final OrcVariant.Neck[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(OrcVariant.Neck::getId))
         .toArray(OrcVariant.Neck[]::new);
      private final int id;
      private final ResourceLocation location;

      Neck(int id, ResourceLocation location) {
         this.id = id;
         this.location = location;
      }

      public static OrcVariant.Neck byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(OrcEntity entity) {
         return ((OrcVariant.Neck)Util.getRandom(values(), entity.getRandom())).getId();
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
      SHIRT_LONG(0, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/clothes/orc_shirt_long.png")),
      SHIRT_SHORT(1, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/clothes/orc_shirt_short.png")),
      SHIRT_SLEEVELESS(2, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/clothes/orc_shirt_sleeveless.png"));

      private static final OrcVariant.Top[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(OrcVariant.Top::getId))
         .toArray(OrcVariant.Top[]::new);
      private final int id;
      private final ResourceLocation location;

      Top(int id, ResourceLocation location) {
         this.id = id;
         this.location = location;
      }

      public static OrcVariant.Top byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(OrcEntity entity) {
         return ((OrcVariant.Top)Util.getRandom(values(), entity.getRandom())).getId();
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
