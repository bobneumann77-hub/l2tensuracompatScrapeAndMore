package io.github.manasmods.tensura.entity.variant;

import io.github.manasmods.tensura.entity.monster.GoblinEntity;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import lombok.Generated;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public class GoblinVariant {
   public enum Bottom {
      SHORTS(0, "shorts"),
      PANTS(1, "pants");

      private static final GoblinVariant.Bottom[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(GoblinVariant.Bottom::getId))
         .toArray(GoblinVariant.Bottom[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Bottom(int id, String name) {
         this.id = id;
         this.location = name;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/goblin/unisex/bottom/legs_" + name + ".png");
      }

      public static ResourceLocation getTextureLocation(GoblinEntity entity) {
         return entity.getBottom().texture;
      }

      public static GoblinVariant.Bottom byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(GoblinEntity entity) {
         return ((GoblinVariant.Bottom)Util.getRandom(values(), entity.getRandom())).getId();
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

   public enum Face {
      FACE_A(0, "unisex/faces/face_a", GoblinVariant.Gender.OTHER),
      FACE_B(1, "unisex/faces/face_b", GoblinVariant.Gender.OTHER),
      FACE_C(2, "male/faces/face_c", GoblinVariant.Gender.MALE),
      FACE_D(3, "unisex/faces/face_d", GoblinVariant.Gender.OTHER),
      FACE_E(4, "unisex/faces/face_e", GoblinVariant.Gender.OTHER);

      private static final GoblinVariant.Face[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(GoblinVariant.Face::getId))
         .toArray(GoblinVariant.Face[]::new);
      private static final List<Integer> MALE_LIST = Arrays.stream(values())
         .filter(skin -> skin.getGender() != GoblinVariant.Gender.FEMALE)
         .map(GoblinVariant.Face::getId)
         .toList();
      private static final List<Integer> FEMALE_LIST = Arrays.stream(values())
         .filter(skin -> skin.getGender() != GoblinVariant.Gender.MALE)
         .map(GoblinVariant.Face::getId)
         .toList();
      private final int id;
      private final String location;
      private final GoblinVariant.Gender gender;
      private final ResourceLocation texture;

      Face(int id, String name, GoblinVariant.Gender gender) {
         this.id = id;
         this.location = name;
         this.gender = gender;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/goblin/" + name + ".png");
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static GoblinVariant.Face byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(GoblinVariant.Gender gender, GoblinEntity entity) {
         return gender.equals(GoblinVariant.Gender.FEMALE)
            ? FEMALE_LIST.get(entity.getRandom().nextInt(FEMALE_LIST.size()))
            : MALE_LIST.get(entity.getRandom().nextInt(MALE_LIST.size()));
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
      public GoblinVariant.Gender getGender() {
         return this.gender;
      }

      @Generated
      public ResourceLocation getTexture() {
         return this.texture;
      }
   }

   public enum Gender {
      MALE(0, "male"),
      FEMALE(1, "female"),
      OTHER(2, "unisex");

      private static final GoblinVariant.Gender[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(GoblinVariant.Gender::getId))
         .toArray(GoblinVariant.Gender[]::new);
      private final int id;
      private final String location;

      Gender(int id, String location) {
         this.id = id;
         this.location = location;
      }

      public static GoblinVariant.Gender byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      @Generated
      public int getId() {
         return this.id;
      }

      @Generated
      public String getLocation() {
         return this.location;
      }
   }

   public enum Hair {
      BANDANA(0, "hair_bandana_"),
      LONG(4, "hair_long_"),
      SHORT(8, "hair_short_");

      private static final GoblinVariant.Hair[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(GoblinVariant.Hair::getId))
         .toArray(GoblinVariant.Hair[]::new);
      private final int id;
      private final String location;
      private final EnumMap<GoblinVariant.Gender, ResourceLocation> textures;

      Hair(int id, String name) {
         this.id = id;
         this.location = name;
         this.textures = new EnumMap<>(GoblinVariant.Gender.class);

         for (GoblinVariant.Gender g : GoblinVariant.Gender.values()) {
            this.textures
               .put(
                  g, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/goblin/" + g.getLocation() + "/hair/" + name + g.getLocation() + ".png")
               );
         }
      }

      public static ResourceLocation getTextureLocation(GoblinEntity entity) {
         return entity.getHair().textures.get(entity.getGender());
      }

      public static GoblinVariant.Hair byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(GoblinEntity entity) {
         return ((GoblinVariant.Hair)Util.getRandom(values(), entity.getRandom())).getId();
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
      public EnumMap<GoblinVariant.Gender, ResourceLocation> getTextures() {
         return this.textures;
      }
   }

   public enum Head {
      WHITE(0, "bandana"),
      WHITE_FULL(1, "bandana_full");

      private static final GoblinVariant.Head[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(GoblinVariant.Head::getId))
         .toArray(GoblinVariant.Head[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Head(int id, String name) {
         this.id = id;
         this.location = name;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/goblin/unisex/head/head_" + name + ".png");
      }

      public static ResourceLocation getTextureLocation(GoblinEntity entity) {
         return entity.getHead().texture;
      }

      public static GoblinVariant.Head byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(GoblinEntity entity) {
         return ((GoblinVariant.Head)Util.getRandom(values(), entity.getRandom())).getId();
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

   public enum Skin {
      MEDIUM(0, "mid_"),
      LIGHT(1, "light_"),
      DARK(2, "dark_");

      private static final GoblinVariant.Skin[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(GoblinVariant.Skin::getId))
         .toArray(GoblinVariant.Skin[]::new);
      private final int id;
      private final String location;
      private final EnumMap<GoblinVariant.Gender, ResourceLocation> textures;

      Skin(int id, String name) {
         this.id = id;
         this.location = name;
         this.textures = new EnumMap<>(GoblinVariant.Gender.class);

         for (GoblinVariant.Gender g : GoblinVariant.Gender.values()) {
            this.textures
               .put(
                  g, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/goblin/" + g.getLocation() + "/skin/" + name + g.getLocation() + ".png")
               );
         }
      }

      public static ResourceLocation getTextureLocation(GoblinEntity entity) {
         return entity.getSkin().textures.get(entity.getGender());
      }

      public static GoblinVariant.Skin byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(GoblinEntity entity) {
         return ((GoblinVariant.Skin)Util.getRandom(values(), entity.getRandom())).getId();
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
      public EnumMap<GoblinVariant.Gender, ResourceLocation> getTextures() {
         return this.textures;
      }
   }

   public enum Top {
      T_SHIRT(0, "tshirt"),
      VEST(1, "vest");

      private static final GoblinVariant.Top[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(GoblinVariant.Top::getId))
         .toArray(GoblinVariant.Top[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Top(int id, String name) {
         this.id = id;
         this.location = name;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/goblin/unisex/top/chest_" + name + ".png");
      }

      public static ResourceLocation getTextureLocation(GoblinEntity entity) {
         return entity.getTop().texture;
      }

      public static GoblinVariant.Top byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(GoblinEntity entity) {
         return ((GoblinVariant.Top)Util.getRandom(values(), entity.getRandom())).getId();
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
}
