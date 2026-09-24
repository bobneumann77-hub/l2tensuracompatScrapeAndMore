package io.github.manasmods.tensura.entity.variant;

import java.util.Arrays;
import java.util.Comparator;
import lombok.Generated;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public enum DirewolfVariant {
   DIREWOLF(0, "direwolf", "direwolf_birthmark"),
   RED_FANG(1, "red_fang"),
   MYSTIC_FIRE_WOLF(2, "mystic_fire_wolf"),
   GREEN_FANG(3, "green_fang"),
   MYSTIC_WIND_WOLF(4, "mystic_wind_wolf"),
   BLUE_FANG(5, "blue_fang"),
   MYSTIC_WATER_WOLF(6, "mystic_water_wolf"),
   BROWN_FANG(7, "brown_fang"),
   MYSTIC_EARTH_WOLF(8, "mystic_earth_wolf"),
   PURPLE_FANG(9, "purple_fang"),
   MYSTIC_SPACE_WOLF(10, "mystic_space_wolf"),
   BLACK_FANG(11, "black_fang"),
   MYSTIC_WOLF(12, "mystic_wolf"),
   TEMPEST_WOLF(13, "tempest_wolf", "direwolf_birthmark"),
   STAR_WOLF(14, "tempest_wolf", "star_wolf"),
   TEMPEST_STAR_WOLF(15, "tempest_wolf", "tempest_star_wolf");

   private static final DirewolfVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(DirewolfVariant::getId))
      .toArray(DirewolfVariant[]::new);
   private final int id;
   private final String name;
   @Nullable
   private final String birthmarkName;
   private final ResourceLocation texture;
   @Nullable
   private final ResourceLocation birthmarkTexture;

   DirewolfVariant(int id, String location) {
      this.id = id;
      this.name = location;
      this.birthmarkName = null;
      this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/direwolf/" + location + ".png");
      this.birthmarkTexture = null;
   }

   DirewolfVariant(int id, String location, @Nullable String birthmarkName) {
      this.id = id;
      this.name = location;
      this.birthmarkName = birthmarkName;
      this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/direwolf/" + location + ".png");
      this.birthmarkTexture = birthmarkName == null
         ? null
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/direwolf/" + birthmarkName + ".png");
   }

   public static DirewolfVariant byId(int id) {
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

   @Nullable
   @Generated
   public String getBirthmarkName() {
      return this.birthmarkName;
   }

   @Generated
   public ResourceLocation getTexture() {
      return this.texture;
   }

   @Nullable
   @Generated
   public ResourceLocation getBirthmarkTexture() {
      return this.birthmarkTexture;
   }
}
