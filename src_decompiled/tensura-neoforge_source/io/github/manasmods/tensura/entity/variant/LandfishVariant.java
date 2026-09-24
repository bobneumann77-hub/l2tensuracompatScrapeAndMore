package io.github.manasmods.tensura.entity.variant;

import java.util.Arrays;
import java.util.Comparator;
import lombok.Generated;
import net.minecraft.resources.ResourceLocation;

public enum LandfishVariant {
   GREEN(0, "green"),
   CYAN(1, "cyan"),
   BLUE(2, "blue"),
   COD(3, "cod"),
   SALMON(4, "salmon"),
   PUFFER(5, "puffer"),
   DOLPHIN(6, "dolphin"),
   GUARDIAN(7, "guardian"),
   ELDER_GUARDIAN(8, "elder_guardian");

   private static final LandfishVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(LandfishVariant::getId))
      .toArray(LandfishVariant[]::new);
   private final int id;
   private final String location;
   private final ResourceLocation texture;

   LandfishVariant(int id, String location) {
      this.id = id;
      this.location = location;
      this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/landfish/landfish_" + location + ".png");
   }

   public static LandfishVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }

   public ResourceLocation getTextureLocation() {
      return this.texture;
   }

   @Generated
   public int getId() {
      return this.id;
   }

   @Generated
   public String getLocation() {
      return this.location;
   }

   @Generated
   public ResourceLocation getTexture() {
      return this.texture;
   }
}
