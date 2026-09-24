package io.github.manasmods.tensura.entity.variant;

import java.util.Arrays;
import java.util.Comparator;
import lombok.Generated;
import net.minecraft.resources.ResourceLocation;

public enum PhantasporeVariant {
   DEFAULT(0, "phantaspore", false),
   RED(1, "phantaspore_red", false),
   BROWN(2, "phantaspore_brown", false),
   CRIMSON(3, "phantaspore_crimson", true),
   WARPED(4, "phantaspore_warped", true);

   private static final PhantasporeVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(PhantasporeVariant::getId))
      .toArray(PhantasporeVariant[]::new);
   private final int id;
   private final String name;
   private final boolean nether;
   private final ResourceLocation texture;

   PhantasporeVariant(int id, String location, boolean nether) {
      this.id = id;
      this.name = location;
      this.nether = nether;
      this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/phantaspore/" + location + ".png");
   }

   public static PhantasporeVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }

   @Generated
   public int getId() {
      return this.id;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public boolean isNether() {
      return this.nether;
   }

   @Generated
   public ResourceLocation getTexture() {
      return this.texture;
   }
}
