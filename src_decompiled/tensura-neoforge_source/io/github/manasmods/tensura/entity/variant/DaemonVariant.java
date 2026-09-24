package io.github.manasmods.tensura.entity.variant;

import io.github.manasmods.tensura.entity.monster.ArchDaemonEntity;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import lombok.Generated;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

public class DaemonVariant {
   public enum Bottom {
      FUR(0, "fur"),
      FUR_TAIL(1, "fur_tail"),
      FUR_TAIL_POINTY(2, "fur_tail_pointy"),
      NORMAL(3, "normal"),
      SKIRT(4, "skirt"),
      WRAPS(5, "wraps");

      private static final DaemonVariant.Bottom[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.Bottom::getId))
         .toArray(DaemonVariant.Bottom[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Bottom(int id, String location) {
         this.id = id;
         this.location = location;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/arch/clothing/bottoms/archdemon_bottoms_" + location + ".png");
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.Bottom byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(ArchDaemonEntity entity) {
         return ((DaemonVariant.Bottom)Util.getRandom(values(), entity.getRandom())).getId();
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

   public enum Coat {
      COAT(0, "coat"),
      COAT_OPEN(1, "coat_open"),
      COAT_SLEEVELESS(2, "coat_sleeveless"),
      VEST(3, "vest"),
      VEST_BERETTA(4, "vest_beretta");

      private static final DaemonVariant.Coat[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.Coat::getId))
         .toArray(DaemonVariant.Coat[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Coat(int id, String location) {
         this.id = id;
         this.location = location;
         this.texture = ResourceLocation.fromNamespaceAndPath(
            "tensura", "textures/entity/daemon/arch/clothing/accessories/archdemon_accessory_" + location + ".png"
         );
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.Coat byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(ArchDaemonEntity entity) {
         return ((DaemonVariant.Coat)Util.getRandom(values(), entity.getRandom())).getId();
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

   public enum EyeBrow {
      NORMAL(0, "eyebrow"),
      FEMININE(1, "eyebrow_feminine"),
      FULL(2, "eyebrow_full");

      private static final DaemonVariant.EyeBrow[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.EyeBrow::getId))
         .toArray(DaemonVariant.EyeBrow[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      EyeBrow(int id, String location) {
         this.id = id;
         this.location = location;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/arch/face/eyebrow/archdemon_" + location + ".png");
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.EyeBrow byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(ArchDaemonEntity entity) {
         return ((DaemonVariant.EyeBrow)Util.getRandom(values(), entity.getRandom())).getId();
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

   public enum EyeLiner {
      DIAGONAL(0, "diagonal"),
      FULL(1, "full"),
      SMALL(2, "small"),
      SPIKEY(3, "spikey"),
      TEARS(4, "tears");

      private static final DaemonVariant.EyeLiner[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.EyeLiner::getId))
         .toArray(DaemonVariant.EyeLiner[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      EyeLiner(int id, String location) {
         this.id = id;
         this.location = location;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/arch/face/eyeliner/archdemon_eyeliner_" + location + ".png");
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.EyeLiner byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(ArchDaemonEntity entity) {
         return ((DaemonVariant.EyeLiner)Util.getRandom(values(), entity.getRandom())).getId();
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

   public enum FacialHair {
      BEARD(0, "beard"),
      CHINTUFT(1, "chintuft"),
      GOATEE(2, "goatee"),
      HANDLEBAR(3, "handlebar"),
      MUSTACHE(4, "mustache"),
      MUTTONCHOPS(5, "muttonchops");

      private static final DaemonVariant.FacialHair[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.FacialHair::getId))
         .toArray(DaemonVariant.FacialHair[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      FacialHair(int id, String location) {
         this.id = id;
         this.location = location;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/arch/hair/facial/archdemon_facial_hair_" + location + ".png");
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.FacialHair byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(ArchDaemonEntity entity) {
         return ((DaemonVariant.FacialHair)Util.getRandom(values(), entity.getRandom())).getId();
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
      FEMALE(1, "female");

      private static final DaemonVariant.Gender[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.Gender::getId))
         .toArray(DaemonVariant.Gender[]::new);
      private final int id;
      private final String location;

      Gender(int id, String location) {
         this.id = id;
         this.location = location;
      }

      public static DaemonVariant.Gender byId(int id) {
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
      LONG_MESSY(0, "long_messy"),
      LONG_PARTED(1, "long_parted"),
      LONG_PONYTAIL(2, "long_ponytail"),
      LONG_SLICKED(3, "long_slicked"),
      MEDIUM_FEMININE(4, "medium_feminine"),
      MEDIUM_FLUFFY(5, "medium_fluffy"),
      MEDIUM_MESSY(6, "medium_messy"),
      MEDIUM_SLICKED(7, "medium_slicked"),
      SHORT_BALDING(8, "short_balding"),
      SHORT_BUZZED(9, "short_buzzed"),
      SHORT_MESSY(10, "short_messy"),
      SHORT_SLICKED(11, "short_slicked");

      private static final DaemonVariant.Hair[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.Hair::getId))
         .toArray(DaemonVariant.Hair[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Hair(int id, String location) {
         this.id = id;
         this.location = location;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/arch/hair/archdemon_hair_" + location + ".png");
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.Hair byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(ArchDaemonEntity entity) {
         return ((DaemonVariant.Hair)Util.getRandom(values(), entity.getRandom())).getId();
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

   public enum Horn {
      CURVY(0, "curvy", 0, 1, 2, 3, 6),
      DUAL(1, "dual", 0, 1, 2, 3, 6),
      DUAL_SMALL(2, "dual_small", 0, 1, 2, 6),
      RAM(3, "ram", 0, 1, 4),
      SINGLE(4, "single", 4, 5),
      SINGLE_SMALL(5, "single_small", 4, 5),
      ZIGZAG(6, "zigzag", 0, 1, 2, 6);

      private static final DaemonVariant.Horn[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.Horn::getId))
         .toArray(DaemonVariant.Horn[]::new);
      private final int id;
      private final String location;
      private final List<Integer> conflict;
      private final ResourceLocation texture;

      Horn(int id, String location, int... conflict) {
         this.id = id;
         this.location = location;
         this.conflict = Arrays.stream(conflict).boxed().toList();
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/arch/horns/archdemon_horn_" + location + ".png");
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.Horn byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static DaemonVariant.Horn getRandom(ArchDaemonEntity entity) {
         return (DaemonVariant.Horn)Util.getRandom(values(), entity.getRandom());
      }

      public static DaemonVariant.Horn getRandom(ArchDaemonEntity entity, DaemonVariant.Horn firstHorn) {
         List<Integer> conflicts = firstHorn.getConflict();
         List<DaemonVariant.Horn> horns = Arrays.stream(values()).filter(horn -> !conflicts.contains(horn.getId())).toList();
         return horns.isEmpty() ? null : horns.get(entity.getRandom().nextInt(horns.size()));
      }

      public static DaemonVariant.Horn getRandom(ArchDaemonEntity entity, DaemonVariant.Horn firstHorn, DaemonVariant.Horn secondHorn) {
         List<DaemonVariant.Horn> horns = Arrays.stream(values())
            .filter(horn -> !firstHorn.getConflict().contains(horn.getId()) && !secondHorn.getConflict().contains(horn.getId()))
            .toList();
         return horns.isEmpty() ? null : horns.get(entity.getRandom().nextInt(horns.size()));
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
      public List<Integer> getConflict() {
         return this.conflict;
      }

      @Generated
      public ResourceLocation getTexture() {
         return this.texture;
      }
   }

   public enum Linage {
      RED(0, "red", -5103070),
      WHITE(1, "white", -855310),
      BLACK(2, "black", -15658735),
      GREEN(3, "green", -13726889),
      YELLOW(4, "yellow", -2047167),
      PURPLE(5, "purple", -8565822),
      BLUE(6, "blue", -12620069);

      private static final DaemonVariant.Linage[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.Linage::getId))
         .toArray(DaemonVariant.Linage[]::new);
      private final int id;
      private final String location;
      private final int baseColor;
      private final EnumMap<DaemonVariant.Gender, ResourceLocation> textures;

      Linage(int id, String location, int baseColor) {
         this.id = id;
         this.location = location;
         this.baseColor = baseColor;
         this.textures = new EnumMap<>(DaemonVariant.Gender.class);

         for (DaemonVariant.Gender g : DaemonVariant.Gender.values()) {
            this.textures
               .put(
                  g,
                  ResourceLocation.fromNamespaceAndPath(
                     "tensura", "textures/entity/daemon/arch/skin/" + g.getLocation() + "/archdemon_" + g.getLocation() + "_" + location + ".png"
                  )
               );
         }
      }

      public ResourceLocation getSkinLocation(ArchDaemonEntity entity) {
         return this.textures.get(entity.getGender());
      }

      public static DaemonVariant.Linage byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static DaemonVariant.Linage getRandom(ArchDaemonEntity entity) {
         return (DaemonVariant.Linage)Util.getRandom(values(), entity.getRandom());
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
      public int getBaseColor() {
         return this.baseColor;
      }

      @Generated
      public EnumMap<DaemonVariant.Gender, ResourceLocation> getTextures() {
         return this.textures;
      }
   }

   public enum NeckAccessory {
      FLUFF(0, "fluff"),
      NECKSCARF(1, "neckscarf"),
      TIE(2, "tie"),
      HEADWRAP(3, "headwrap");

      private static final DaemonVariant.NeckAccessory[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.NeckAccessory::getId))
         .toArray(DaemonVariant.NeckAccessory[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      NeckAccessory(int id, String location) {
         this.id = id;
         this.location = location;
         this.texture = ResourceLocation.fromNamespaceAndPath(
            "tensura", "textures/entity/daemon/arch/clothing/accessories/archdemon_accessory_" + location + ".png"
         );
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.NeckAccessory byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(ArchDaemonEntity entity) {
         return entity.getRandom().nextFloat() < 0.8F
            ? entity.getRandom().nextInt(3)
            : ((DaemonVariant.NeckAccessory)Util.getRandom(values(), entity.getRandom())).getId();
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

   public enum Shoe {
      BOOTS(0, "boots"),
      NORMAL(1, "normal"),
      SMALL(2, "small");

      private static final DaemonVariant.Shoe[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.Shoe::getId))
         .toArray(DaemonVariant.Shoe[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Shoe(int id, String location) {
         this.id = id;
         this.location = location;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/arch/clothing/shoes/archdemon_shoes_" + location + ".png");
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.Shoe byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(ArchDaemonEntity entity) {
         return ((DaemonVariant.Shoe)Util.getRandom(values(), entity.getRandom())).getId();
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

   public enum Teeth {
      FANGS_FEMALE(0, "fangs_female"),
      TUSK(1, "tusk"),
      WIDE(2, "wide"),
      FANGS_MALE(3, "fangs_male");

      private static final DaemonVariant.Teeth[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.Teeth::getId))
         .toArray(DaemonVariant.Teeth[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Teeth(int id, String location) {
         this.id = id;
         this.location = location;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/arch/face/teeth/archdemon_teeth_" + location + ".png");
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.Teeth byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(ArchDaemonEntity entity) {
         if (entity.getGender().equals(DaemonVariant.Gender.MALE)) {
            return entity.getRandom().nextInt(1, 4);
         } else {
            return entity.getRandom().nextBoolean() ? 0 : 1;
         }
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

   public enum Top {
      FORMAL(0, "formal"),
      NORMAL(1, "normal");

      private static final DaemonVariant.Top[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.Top::getId))
         .toArray(DaemonVariant.Top[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Top(int id, String location) {
         this.id = id;
         this.location = location;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/arch/clothing/tops/archdemon_top_" + location + ".png");
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.Top byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(ArchDaemonEntity entity) {
         return entity.getRandom().nextBoolean() ? FORMAL.getId() : NORMAL.getId();
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

   public enum Wings {
      LARGE(0, "large"),
      LARGE_TORN(1, "large_torn"),
      MEDIUM(2, "medium"),
      SMALL(3, "small");

      private static final DaemonVariant.Wings[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(DaemonVariant.Wings::getId))
         .toArray(DaemonVariant.Wings[]::new);
      private final int id;
      private final String location;
      private final ResourceLocation texture;

      Wings(int id, String location) {
         this.id = id;
         this.location = location;
         this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/arch/wings/archdemon_wings_" + location + ".png");
      }

      public ResourceLocation getTextureLocation() {
         return this.texture;
      }

      public static DaemonVariant.Wings byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public static int getRandom(ArchDaemonEntity entity) {
         return ((DaemonVariant.Wings)Util.getRandom(values(), entity.getRandom())).getId();
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
