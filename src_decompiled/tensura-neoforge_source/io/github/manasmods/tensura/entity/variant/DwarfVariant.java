package io.github.manasmods.tensura.entity.variant;

import io.github.manasmods.tensura.entity.human.DwarfEntity;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import lombok.Generated;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class DwarfVariant {
   public enum Bottom {
      SHORTS(0, "shorts"),
      PANTS(1, "pants"),
      PANTS_BOOTS(2, "pants_boots");

      private static final DwarfVariant.Bottom[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DwarfVariant.Bottom::getId))
         .toArray(DwarfVariant.Bottom[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Bottom(int id, String name) {
         this.id = id;
         this.location = name;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/unisex/clothing/" + name + ".png");
      }

      public static ResourceLocation getTextureLocation(DwarfEntity entity) {
         return entity.getBottom().texture;
      }

      public static DwarfVariant.Bottom byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(DwarfEntity entity) {
         return ((DwarfVariant.Bottom)Util.getRandom(values(), entity.getRandom())).getId();
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
      FACE_AA(0, "unisex/face/face_aa", DwarfVariant.Gender.OTHER),
      FACE_AB(1, "unisex/face/face_ab", DwarfVariant.Gender.OTHER),
      FACE_AC(1, "unisex/face/face_ac", DwarfVariant.Gender.OTHER),
      FACE_BA(2, "male/face/face_ba", DwarfVariant.Gender.MALE),
      FACE_BB(3, "male/face/face_bb", DwarfVariant.Gender.MALE),
      FACE_BC(4, "male/face/face_bc", DwarfVariant.Gender.MALE),
      FACE_CA(5, "male/face/face_ca", DwarfVariant.Gender.MALE),
      FACE_CB(6, "male/face/face_cb", DwarfVariant.Gender.MALE),
      FACE_CC(7, "male/face/face_cc", DwarfVariant.Gender.MALE),
      FACE_DA(8, "female/face/face_da", DwarfVariant.Gender.FEMALE),
      FACE_DB(9, "female/face/face_db", DwarfVariant.Gender.FEMALE),
      FACE_DC(10, "female/face/face_dc", DwarfVariant.Gender.FEMALE),
      FACE_EA(11, "male/face/face_ea", DwarfVariant.Gender.MALE),
      FACE_EB(12, "male/face/face_eb", DwarfVariant.Gender.MALE),
      FACE_EC(13, "male/face/face_ec", DwarfVariant.Gender.MALE);

      private static final DwarfVariant.Face[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DwarfVariant.Face::getId))
         .toArray(DwarfVariant.Face[]::new);
      private static final List<Integer> MALE_LIST = Arrays.stream(values())
         .filter(skin -> skin.getGender() != DwarfVariant.Gender.FEMALE)
         .map(DwarfVariant.Face::getId)
         .toList();
      private static final List<Integer> FEMALE_LIST = Arrays.stream(values())
         .filter(skin -> skin.getGender() != DwarfVariant.Gender.MALE)
         .map(DwarfVariant.Face::getId)
         .toList();
      private final int id;
      private final String location;
      private final DwarfVariant.Gender gender;
      private final ResourceLocation[] scarTextures;

      Face(int id, String name, DwarfVariant.Gender gender) {
         this.id = id;
         this.location = name;
         this.gender = gender;
         this.scarTextures = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/" + name + ".png"),
            ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/" + name + "l.png"),
            ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/" + name + "r.png")
         };
      }

      public static ResourceLocation getTextureLocation(DwarfEntity entity) {
         int scar = entity.getScar();
         DwarfVariant.Face face = entity.getFace();
         return face.scarTextures[scar >= 0 && scar <= 2 ? scar : 0];
      }

      public static DwarfVariant.Face byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(DwarfVariant.Gender gender, DwarfEntity entity) {
         return gender.equals(DwarfVariant.Gender.FEMALE)
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
      public DwarfVariant.Gender getGender() {
         return this.gender;
      }

      @Generated
      public ResourceLocation[] getScarTextures() {
         return this.scarTextures;
      }
   }

   public enum FacialHair {
      B_CHOPS(0, "beard_chops"),
      B_GOATEE(1, "beard_goatee"),
      B_MOUSTACHE(2, "beard_moustache"),
      M_BEEFY(3, "beefy_mustache"),
      B_BUSHY(4, "bushy_beard"),
      B_SHENANDOAH(5, "bushy_beard_shenandoah"),
      B_PENCIL(6, "pencil_beard"),
      M_THICK(7, "thick_moustache");

      private static final DwarfVariant.FacialHair[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DwarfVariant.FacialHair::getId))
         .toArray(DwarfVariant.FacialHair[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      FacialHair(int id, String name) {
         this.id = id;
         this.location = name;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/male/facial_hair/" + name + ".png");
      }

      public static ResourceLocation getTextureLocation(DwarfEntity entity) {
         return entity.getFacialHair().texture;
      }

      public static DwarfVariant.FacialHair byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(DwarfEntity entity) {
         return ((DwarfVariant.FacialHair)Util.getRandom(values(), entity.getRandom())).getId();
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

   public enum Feet {
      BOOTS(0, "boots"),
      SHOES(1, "shoes");

      private static final DwarfVariant.Feet[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DwarfVariant.Feet::getId))
         .toArray(DwarfVariant.Feet[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Feet(int id, String name) {
         this.id = id;
         this.location = name;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/unisex/clothing/" + name + ".png");
      }

      public static ResourceLocation getTextureLocation(DwarfEntity entity) {
         return entity.getFeet().texture;
      }

      public static DwarfVariant.Feet byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(DwarfEntity entity) {
         return ((DwarfVariant.Feet)Util.getRandom(values(), entity.getRandom())).getId();
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

   public enum Gender {
      MALE(0, "male"),
      FEMALE(1, "female"),
      OTHER(2, "unisex");

      private static final DwarfVariant.Gender[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DwarfVariant.Gender::getId))
         .toArray(DwarfVariant.Gender[]::new);
      private final int id;
      private final String location;

      Gender(int id, String location) {
         this.id = id;
         this.location = location;
      }

      public static DwarfVariant.Gender byId(int id) {
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
      LONG(0, "long_hair_", DwarfVariant.Gender.OTHER),
      LONG_TIED(1, "long_hair_tied_", DwarfVariant.Gender.OTHER),
      SHORT(2, "short_hair_", DwarfVariant.Gender.OTHER),
      BALD(3, "balding_", DwarfVariant.Gender.MALE);

      private static final DwarfVariant.Hair[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DwarfVariant.Hair::getId))
         .toArray(DwarfVariant.Hair[]::new);
      private static final List<Integer> MALE_LIST = Arrays.stream(values())
         .filter(skin -> skin.getGender() != DwarfVariant.Gender.FEMALE)
         .map(DwarfVariant.Hair::getId)
         .toList();
      private static final List<Integer> FEMALE_LIST = Arrays.stream(values())
         .filter(skin -> skin.getGender() != DwarfVariant.Gender.MALE)
         .map(DwarfVariant.Hair::getId)
         .toList();
      private final int id;
      private final String location;
      private final DwarfVariant.Gender gender;
      private final EnumMap<DwarfVariant.Gender, ResourceLocation> textures;

      Hair(int id, String name, DwarfVariant.Gender gender) {
         this.id = id;
         this.location = name;
         this.gender = gender;
         this.textures = new EnumMap<>(DwarfVariant.Gender.class);

         for (DwarfVariant.Gender g : DwarfVariant.Gender.values()) {
            this.textures
               .put(
                  g, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/" + g.getLocation() + "/hair/" + name + g.getLocation() + ".png")
               );
         }
      }

      public static ResourceLocation getTextureLocation(DwarfEntity entity) {
         return entity.getHair().textures.get(entity.getGender());
      }

      public static DwarfVariant.Hair byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(DwarfVariant.Gender gender, DwarfEntity entity) {
         return gender.equals(DwarfVariant.Gender.FEMALE)
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
      public DwarfVariant.Gender getGender() {
         return this.gender;
      }

      @Generated
      public EnumMap<DwarfVariant.Gender, ResourceLocation> getTextures() {
         return this.textures;
      }
   }

   public enum Skin {
      PALE(0, "pale"),
      WHITE(1, "white"),
      TANNED(2, "tanned"),
      BROWN(3, "brown");

      private static final DwarfVariant.Skin[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DwarfVariant.Skin::getId))
         .toArray(DwarfVariant.Skin[]::new);
      private final int id;
      private final String location;
      private final EnumMap<DwarfVariant.Gender, ResourceLocation> textures;

      Skin(int id, String name) {
         this.id = id;
         this.location = name;
         this.textures = new EnumMap<>(DwarfVariant.Gender.class);

         for (DwarfVariant.Gender g : DwarfVariant.Gender.values()) {
            this.textures.put(g, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/" + g.getLocation() + "/skin/" + name + ".png"));
         }
      }

      public static ResourceLocation getTextureLocation(DwarfEntity entity) {
         return entity.getSkin().textures.get(entity.getGender());
      }

      public static DwarfVariant.Skin byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(DwarfEntity entity) {
         return ((DwarfVariant.Skin)Util.getRandom(values(), entity.getRandom())).getId();
      }

      public static int getBreed(DwarfVariant.Skin parentA, DwarfVariant.Skin parentB, RandomSource rand) {
         double avg = (parentA.getId() + parentB.getId()) / 2.0;
         if (avg % 1.0 == 0.0) {
            return (int)avg;
         }

         int lower = (int)Math.floor(avg);
         int upper = (int)Math.ceil(avg);
         return rand.nextBoolean() ? lower : upper;
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
      public EnumMap<DwarfVariant.Gender, ResourceLocation> getTextures() {
         return this.textures;
      }
   }

   public enum Top {
      T_SHIRT(0, "tshirt", DwarfVariant.Gender.OTHER),
      LONG_SHIRT(1, "long_shirt", DwarfVariant.Gender.OTHER),
      VEST(2, "vest", DwarfVariant.Gender.MALE),
      DRESS(3, "dress", DwarfVariant.Gender.FEMALE);

      private static final DwarfVariant.Top[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DwarfVariant.Top::getId))
         .toArray(DwarfVariant.Top[]::new);
      private static final List<Integer> MALE_LIST = Arrays.stream(values())
         .filter(skin -> skin.getGender() != DwarfVariant.Gender.FEMALE)
         .map(DwarfVariant.Top::getId)
         .toList();
      private static final List<Integer> FEMALE_LIST = Arrays.stream(values())
         .filter(skin -> skin.getGender() != DwarfVariant.Gender.MALE)
         .map(DwarfVariant.Top::getId)
         .toList();
      private final int id;
      private final String location;
      private final DwarfVariant.Gender gender;
      private final ResourceLocation texture;

      Top(int id, String name, DwarfVariant.Gender gender) {
         this.id = id;
         this.location = name;
         this.gender = gender;
         String prefix = gender == DwarfVariant.Gender.OTHER ? "unisex" : gender.getLocation();
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/" + prefix + "/clothing/" + name + ".png");
      }

      public static ResourceLocation getTextureLocation(DwarfEntity entity) {
         return entity.getTop().texture;
      }

      public static DwarfVariant.Top byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(DwarfVariant.Gender gender, DwarfEntity entity) {
         return gender.equals(DwarfVariant.Gender.FEMALE)
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
      public DwarfVariant.Gender getGender() {
         return this.gender;
      }

      @Generated
      public ResourceLocation getTexture() {
         return this.texture;
      }
   }
}
