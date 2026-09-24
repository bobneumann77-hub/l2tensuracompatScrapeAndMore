package io.github.manasmods.tensura.registry.item.misc;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class TensuraArmorMaterials {
   public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create("tensura", Registries.ARMOR_MATERIAL);
   public static final RegistrySupplier<ArmorMaterial> SILVER = ARMOR_MATERIALS.register(
      "silver",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 2);
            map.put(Type.LEGGINGS, 5);
            map.put(Type.CHESTPLATE, 6);
            map.put(Type.HELMET, 2);
         }),
         20,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.SILVER_INGOT.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "silver"))),
         0.0F,
         0.0F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> LOW_MAGISTEEL = ARMOR_MATERIALS.register(
      "low_magisteel",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 3);
            map.put(Type.LEGGINGS, 6);
            map.put(Type.CHESTPLATE, 8);
            map.put(Type.HELMET, 3);
         }),
         25,
         SoundEvents.ARMOR_EQUIP_IRON,
         () -> Ingredient.of(
            new ItemLike[]{
               (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get(),
               (ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get(),
               (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()
            }
         ),
         List.of(),
         2.5F,
         0.1F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> HIGH_MAGISTEEL = ARMOR_MATERIALS.register(
      "high_magisteel",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 4);
            map.put(Type.LEGGINGS, 7);
            map.put(Type.CHESTPLATE, 9);
            map.put(Type.HELMET, 4);
         }),
         30,
         SoundEvents.ARMOR_EQUIP_IRON,
         () -> Ingredient.of(
            new ItemLike[]{(ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get(), (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}
         ),
         List.of(),
         4.0F,
         0.2F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> MITHRIL = ARMOR_MATERIALS.register(
      "mithril", () -> new ArmorMaterial((Map)Util.make(new EnumMap(Type.class), map -> {
         map.put(Type.BOOTS, 6);
         map.put(Type.LEGGINGS, 9);
         map.put(Type.CHESTPLATE, 11);
         map.put(Type.HELMET, 6);
      }), 35, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.MITHRIL_INGOT.get()}), List.of(), 5.0F, 0.3F)
   );
   public static final RegistrySupplier<ArmorMaterial> ORICHALCUM = ARMOR_MATERIALS.register(
      "orichalcum", () -> new ArmorMaterial((Map)Util.make(new EnumMap(Type.class), map -> {
         map.put(Type.BOOTS, 6);
         map.put(Type.LEGGINGS, 9);
         map.put(Type.CHESTPLATE, 11);
         map.put(Type.HELMET, 6);
      }), 40, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.ORICHALCUM_INGOT.get()}), List.of(), 6.0F, 0.4F)
   );
   public static final RegistrySupplier<ArmorMaterial> PURE_MAGISTEEL = ARMOR_MATERIALS.register(
      "pure_magisteel",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 8);
            map.put(Type.LEGGINGS, 11);
            map.put(Type.CHESTPLATE, 13);
            map.put(Type.HELMET, 8);
         }),
         40,
         SoundEvents.ARMOR_EQUIP_DIAMOND,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()}),
         List.of(),
         7.0F,
         0.6F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> ADAMANTITE = ARMOR_MATERIALS.register(
      "adamantite",
      () -> new ArmorMaterial((Map)Util.make(new EnumMap(Type.class), map -> {
         map.put(Type.BOOTS, 10);
         map.put(Type.LEGGINGS, 13);
         map.put(Type.CHESTPLATE, 15);
         map.put(Type.HELMET, 10);
      }), 45, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.ADAMANTITE_INGOT.get()}), List.of(), 8.0F, 0.8F)
   );
   public static final RegistrySupplier<ArmorMaterial> HIHIIROKANE = ARMOR_MATERIALS.register(
      "hihiirokane",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 12);
            map.put(Type.LEGGINGS, 15);
            map.put(Type.CHESTPLATE, 17);
            map.put(Type.HELMET, 12);
         }),
         50,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.HIHIIROKANE_INGOT.get()}),
         List.of(),
         10.0F,
         1.0F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> CHARYBDIS_SCALEMAIL = ARMOR_MATERIALS.register(
      "charybdis_scalemail",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 9);
            map.put(Type.LEGGINGS, 12);
            map.put(Type.CHESTPLATE, 14);
            map.put(Type.HELMET, 9);
         }),
         38,
         SoundEvents.ARMOR_EQUIP_DIAMOND,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMobDropItems.CHARYBDIS_SCALE.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "silver"))),
         7.5F,
         0.7F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> ARMORSAURUS = ARMOR_MATERIALS.register(
      "armorsaurus", () -> new ArmorMaterial((Map)Util.make(new EnumMap(Type.class), map -> {
         map.put(Type.BOOTS, 5);
         map.put(Type.LEGGINGS, 8);
         map.put(Type.CHESTPLATE, 10);
         map.put(Type.HELMET, 5);
      }), 20, SoundEvents.ARMOR_EQUIP_IRON, Ingredient::of, List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "silver"))), 4.0F, 0.5F)
   );
   public static final RegistrySupplier<ArmorMaterial> ARMORSAURUS_SCALEMAIL = ARMOR_MATERIALS.register(
      "armorsaurus_scalemail",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 4);
            map.put(Type.LEGGINGS, 7);
            map.put(Type.CHESTPLATE, 9);
            map.put(Type.HELMET, 4);
         }),
         20,
         SoundEvents.ARMOR_EQUIP_IRON,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMobDropItems.ARMORSAURUS_SCALE.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "armorsaurus_scalemail"))),
         3.0F,
         0.4F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> SERPENT_SCALEMAIL = ARMOR_MATERIALS.register(
      "serpent_scalemail",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 3);
            map.put(Type.LEGGINGS, 6);
            map.put(Type.CHESTPLATE, 8);
            map.put(Type.HELMET, 3);
         }),
         15,
         SoundEvents.ARMOR_EQUIP_IRON,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMobDropItems.SERPENT_SCALE.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "serpent_scalemail"))),
         2.0F,
         0.2F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> KNIGHT_SPIDER_CARAPACE = ARMOR_MATERIALS.register(
      "knight_spider_carapace",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 2);
            map.put(Type.LEGGINGS, 5);
            map.put(Type.CHESTPLATE, 7);
            map.put(Type.HELMET, 2);
         }),
         15,
         SoundEvents.ARMOR_EQUIP_IRON,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "knight_spider_carapace"))),
         2.0F,
         0.2F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> ANT_CARAPACE = ARMOR_MATERIALS.register(
      "ant_carapace",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 2);
            map.put(Type.LEGGINGS, 5);
            map.put(Type.CHESTPLATE, 7);
            map.put(Type.HELMET, 2);
         }),
         15,
         SoundEvents.ARMOR_EQUIP_IRON,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMobDropItems.GIANT_ANT_CARAPACE.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "ant_carapace"))),
         2.0F,
         0.1F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> MONSTER_LEATHER_D = ARMOR_MATERIALS.register(
      "monster_leather_d",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 1);
            map.put(Type.LEGGINGS, 3);
            map.put(Type.CHESTPLATE, 5);
            map.put(Type.HELMET, 2);
         }),
         15,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "monster_leather_d"))),
         0.5F,
         0.1F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> MONSTER_LEATHER_C = ARMOR_MATERIALS.register(
      "monster_leather_c",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 2);
            map.put(Type.LEGGINGS, 5);
            map.put(Type.CHESTPLATE, 6);
            map.put(Type.HELMET, 2);
         }),
         18,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "monster_leather_c"))),
         1.0F,
         0.2F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> MONSTER_LEATHER_B = ARMOR_MATERIALS.register(
      "monster_leather_b",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 3);
            map.put(Type.LEGGINGS, 6);
            map.put(Type.CHESTPLATE, 8);
            map.put(Type.HELMET, 3);
         }),
         20,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_B.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "monster_leather_b"))),
         2.0F,
         0.3F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> MONSTER_LEATHER_A = ARMOR_MATERIALS.register(
      "monster_leather_a",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 4);
            map.put(Type.LEGGINGS, 7);
            map.put(Type.CHESTPLATE, 9);
            map.put(Type.HELMET, 4);
         }),
         25,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_A.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "monster_leather_a"))),
         4.0F,
         0.4F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> MONSTER_LEATHER_SPECIAL_A = ARMOR_MATERIALS.register(
      "monster_leather_special_a",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 9);
            map.put(Type.LEGGINGS, 12);
            map.put(Type.CHESTPLATE, 14);
            map.put(Type.HELMET, 9);
         }),
         45,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "monster_leather_special_a"))),
         7.5F,
         0.8F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> WINGED_SHOES = ARMOR_MATERIALS.register(
      "winged_shoes",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> map.put(Type.BOOTS, 3)),
         20,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get(), (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get()}),
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("tensura", "winged_shoes"))),
         1.0F,
         0.0F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> DARK = ARMOR_MATERIALS.register(
      "dark",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 4);
            map.put(Type.LEGGINGS, 7);
            map.put(Type.CHESTPLATE, 9);
            map.put(Type.HELMET, 4);
         }),
         20,
         SoundEvents.ARMOR_EQUIP_LEATHER,
         () -> Ingredient.of(
            new ItemLike[]{
               (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_B.get(),
               (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_A.get(),
               (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A.get()
            }
         ),
         List.of(),
         4.0F,
         0.0F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> PIERROT_MASK = ARMOR_MATERIALS.register(
      "pierrot_mask",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> map.put(Type.HELMET, 4)),
         10,
         SoundEvents.ARMOR_EQUIP_IRON,
         () -> Ingredient.of(new ItemLike[]{Items.CLAY_BALL}),
         List.of(),
         0.0F,
         0.0F
      )
   );
   public static final RegistrySupplier<ArmorMaterial> ANTI_MAGIC_MASK = ARMOR_MATERIALS.register(
      "anti_magic_mask",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> map.put(Type.HELMET, 8)),
         35,
         SoundEvents.ARMOR_EQUIP_IRON,
         () -> Ingredient.of(new ItemLike[]{Items.CLAY_BALL}),
         List.of(),
         6.0F,
         0.5F
      )
   );

   public static void init() {
      ARMOR_MATERIALS.register();
   }
}
