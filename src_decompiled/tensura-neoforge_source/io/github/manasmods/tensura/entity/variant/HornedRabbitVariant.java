package io.github.manasmods.tensura.entity.variant;

import java.util.Arrays;
import java.util.Comparator;
import lombok.Generated;
import net.minecraft.resources.ResourceLocation;

public enum HornedRabbitVariant {
   SAND(0, "sand"),
   BROWN(1, "brown"),
   BLACK(2, "black"),
   TOAST(3, "toast"),
   SALT(4, "salt"),
   WHITE(5, "white"),
   GOLD(6, "gold");

   private static final HornedRabbitVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(HornedRabbitVariant::getId))
      .toArray(HornedRabbitVariant[]::new);
   private final int id;
   private final String name;
   private final ResourceLocation texture;

   HornedRabbitVariant(int id, String name) {
      this.id = id;
      this.name = name;
      this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/horned_rabbit/horned_rabbit_" + name + ".png");
   }

   public static HornedRabbitVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }

   public static ResourceLocation getLocation(HornedRabbitVariant variant) {
      return variant.texture;
   }

   @Generated
   public int getId() {
      return this.id;
   }

   @Generated
   public String getName() {
      return this.name;
   }
}
