package io.github.manasmods.tensura.entity.variant;

import java.util.Arrays;
import java.util.Comparator;
import lombok.Generated;
import net.minecraft.resources.ResourceLocation;

public enum MagicCircleVariant {
   EARTH(0, "earth"),
   DARK(1, "dark"),
   FLAME(2, "flame"),
   LIGHT(3, "light"),
   SPACE(4, "space"),
   WATER(5, "water"),
   WIND(6, "wind"),
   HOLY(7, "light"),
   BARRIER(8, "barrier"),
   ENHANCEMENT(9, "enhancement"),
   EXPLOSION(10, "explosion"),
   GRAVITY(11, "gravity"),
   ICE(12, "ice"),
   ILLUSION(13, "illusion"),
   LIGHTNING(14, "lightning"),
   MENTAL(15, "mental"),
   RECOVERY(16, "recovery"),
   NECROMANCY(17, "necromancy"),
   DEMON(18, "summoning_demon"),
   OTHERWORLDER(19, "summoning_otherworlder"),
   MISC(20, "misc");

   private static final MagicCircleVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(MagicCircleVariant::getId))
      .toArray(MagicCircleVariant[]::new);
   private final int id;
   private final String name;
   private final ResourceLocation texture;

   MagicCircleVariant(int id, String name) {
      this.id = id;
      this.name = name;
      this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/magic_circle/" + name + ".png");
   }

   public static MagicCircleVariant byId(int id) {
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
   public ResourceLocation getTexture() {
      return this.texture;
   }
}
