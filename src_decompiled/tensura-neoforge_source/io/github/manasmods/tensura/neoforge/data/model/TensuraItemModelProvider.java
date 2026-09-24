package io.github.manasmods.tensura.neoforge.data.model;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import io.github.manasmods.tensura.registry.item.TensuraSpawnEggs;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.ItemModelGenerators.TrimModelData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TensuraItemModelProvider extends ItemModelProvider {
   public TensuraItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
      super(output, "tensura", existingFileHelper);
   }

   protected void registerModels() {
      this.generateArmors();
      this.generateBlocks();
      this.generateMobDrops();
      this.generateMiscItems();
      this.generateConsumables();
      this.generateWeapons();
      this.generateSchematics();
      this.generateSpawnEggs();
   }

   private void generateArmors() {
      this.basicItem((Item)TensuraArmorItems.BAT_GLIDER.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.WINGED_SHOES.get());
      this.basicItem((Item)TensuraArmorItems.ANGRY_PIERROT_MASK.get());
      this.basicItem((Item)TensuraArmorItems.CRAZY_PIERROT_MASK.get());
      this.basicItem((Item)TensuraArmorItems.TEARY_PIERROT_MASK.get());
      this.basicItem((Item)TensuraArmorItems.WONDER_PIERROT_MASK.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_D_BOOTS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_D_LEGGINGS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_D_CHESTPLATE.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_D_HELMET.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_C_BOOTS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_C_LEGGINGS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_C_CHESTPLATE.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_C_HELMET.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_B_BOOTS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_B_LEGGINGS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_B_CHESTPLATE.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_B_HELMET.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_A_BOOTS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_A_LEGGINGS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_A_CHESTPLATE.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_A_HELMET.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_BOOTS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_LEGGINGS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_CHESTPLATE.get());
      this.basicItem((Item)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_HELMET.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.SILVER_BOOTS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.SILVER_LEGGINGS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.SILVER_CHESTPLATE.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.SILVER_HELMET.get());
      this.basicItem((Item)TensuraArmorItems.LOW_MAGISTEEL_BOOTS.get());
      this.basicItem((Item)TensuraArmorItems.LOW_MAGISTEEL_LEGGINGS.get());
      this.basicItem((Item)TensuraArmorItems.LOW_MAGISTEEL_CHESTPLATE.get());
      this.basicItem((Item)TensuraArmorItems.LOW_MAGISTEEL_HELMET.get());
      this.basicItem((Item)TensuraArmorItems.HIGH_MAGISTEEL_BOOTS.get());
      this.basicItem((Item)TensuraArmorItems.HIGH_MAGISTEEL_LEGGINGS.get());
      this.basicItem((Item)TensuraArmorItems.HIGH_MAGISTEEL_CHESTPLATE.get());
      this.basicItem((Item)TensuraArmorItems.HIGH_MAGISTEEL_HELMET.get());
      this.basicItem((Item)TensuraArmorItems.MITHRIL_BOOTS.get());
      this.basicItem((Item)TensuraArmorItems.MITHRIL_LEGGINGS.get());
      this.basicItem((Item)TensuraArmorItems.MITHRIL_CHESTPLATE.get());
      this.basicItem((Item)TensuraArmorItems.MITHRIL_HELMET.get());
      this.layeredItem(TensuraArmorItems.MITHRIL_HELMET, "_tail", "");
      this.basicItem((Item)TensuraArmorItems.ORICHALCUM_BOOTS.get());
      this.basicItem((Item)TensuraArmorItems.ORICHALCUM_LEGGINGS.get());
      this.basicItem((Item)TensuraArmorItems.ORICHALCUM_CHESTPLATE.get());
      this.basicItem((Item)TensuraArmorItems.ORICHALCUM_HELMET.get());
      this.basicItem((Item)TensuraArmorItems.PURE_MAGISTEEL_BOOTS.get());
      this.basicItem((Item)TensuraArmorItems.PURE_MAGISTEEL_LEGGINGS.get());
      this.basicItem((Item)TensuraArmorItems.PURE_MAGISTEEL_CHESTPLATE.get());
      this.basicItem((Item)TensuraArmorItems.PURE_MAGISTEEL_HELMET.get());
      this.basicItem((Item)TensuraArmorItems.ADAMANTITE_BOOTS.get());
      this.basicItem((Item)TensuraArmorItems.ADAMANTITE_LEGGINGS.get());
      this.basicItem((Item)TensuraArmorItems.ADAMANTITE_CHESTPLATE.get());
      this.basicItem((Item)TensuraArmorItems.ADAMANTITE_HELMET.get());
      this.basicItem((Item)TensuraArmorItems.HIHIIROKANE_BOOTS.get());
      this.basicItem((Item)TensuraArmorItems.HIHIIROKANE_LEGGINGS.get());
      this.basicItem((Item)TensuraArmorItems.HIHIIROKANE_CHESTPLATE.get());
      this.basicItem((Item)TensuraArmorItems.HIHIIROKANE_HELMET.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.ANT_CARAPACE_BOOTS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.ANT_CARAPACE_LEGGINGS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.ANT_CARAPACE_CHESTPLATE.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.ANT_CARAPACE_HELMET.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.SERPENT_SCALEMAIL_BOOTS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.SERPENT_SCALEMAIL_LEGGINGS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.SERPENT_SCALEMAIL_CHESTPLATE.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.SERPENT_SCALEMAIL_HELMET.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_BOOTS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_LEGGINGS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_CHESTPLATE.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_HELMET.get());
      this.basicItem((Item)TensuraArmorItems.ARMORSAURUS_BOOTS.get());
      this.basicItem((Item)TensuraArmorItems.ARMORSAURUS_LEGGINGS.get());
      this.basicItem((Item)TensuraArmorItems.ARMORSAURUS_CHESTPLATE.get());
      this.basicItem((Item)TensuraArmorItems.ARMORSAURUS_HELMET.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_BOOTS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_LEGGINGS.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_CHESTPLATE.get());
      this.armorWithTrim((ArmorItem)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_HELMET.get());
      this.basicItem((Item)TensuraArmorItems.CHARYBDIS_SCALEMAIL_BOOTS.get());
      this.basicItem((Item)TensuraArmorItems.CHARYBDIS_SCALEMAIL_LEGGINGS.get());
      this.basicItem((Item)TensuraArmorItems.CHARYBDIS_SCALEMAIL_CHESTPLATE.get());
      this.basicItem((Item)TensuraArmorItems.CHARYBDIS_SCALEMAIL_HELMET.get());
      this.basicItem((Item)TensuraArmorItems.HOLY_ARMAMENTS_BOOTS.get());
      this.basicItem((Item)TensuraArmorItems.HOLY_ARMAMENTS_LEGGINGS.get());
      this.basicItem((Item)TensuraArmorItems.HOLY_ARMAMENTS_CHESTPLATE.get());
      this.basicItem((Item)TensuraArmorItems.DARK_BOOTS.get());
      this.basicItem((Item)TensuraArmorItems.DARK_LEGGINGS.get());
      this.basicItem((Item)TensuraArmorItems.DARK_JACKET.get());
      this.basicItem((Item)TensuraArmorItems.ANTI_MAGIC_MASK.get());
   }

   private void generateWeapons() {
      this.handheld(TensuraToolItems.GOBLIN_CLUB);
      this.handheld(TensuraToolItems.WOODEN_SHORT_SWORD);
      this.longSword(TensuraToolItems.WOODEN_LONG_SWORD);
      this.greatSword(TensuraToolItems.WOODEN_GREAT_SWORD);
      this.handheld(TensuraToolItems.WOODEN_KATANA);
      this.handheld(TensuraToolItems.WOODEN_KODACHI);
      this.tachi(TensuraToolItems.WOODEN_TACHI);
      this.odachi(TensuraToolItems.WOODEN_ODACHI);
      this.spear(TensuraToolItems.WOODEN_SPEAR);
      this.scythe(TensuraToolItems.WOODEN_SCYTHE);
      this.handheld(TensuraToolItems.WOODEN_SICKLE);
      this.handheld(TensuraToolItems.STONE_SHORT_SWORD);
      this.longSword(TensuraToolItems.STONE_LONG_SWORD);
      this.greatSword(TensuraToolItems.STONE_GREAT_SWORD);
      this.handheld(TensuraToolItems.STONE_KATANA);
      this.handheld(TensuraToolItems.STONE_KODACHI);
      this.tachi(TensuraToolItems.STONE_TACHI);
      this.odachi(TensuraToolItems.STONE_ODACHI);
      this.spear(TensuraToolItems.STONE_SPEAR);
      this.scythe(TensuraToolItems.STONE_SCYTHE);
      this.handheld(TensuraToolItems.STONE_SICKLE);
      this.handheld(TensuraToolItems.IRON_SHORT_SWORD);
      this.longSword(TensuraToolItems.IRON_LONG_SWORD);
      this.greatSword(TensuraToolItems.IRON_GREAT_SWORD);
      this.handheld(TensuraToolItems.IRON_KATANA);
      this.handheld(TensuraToolItems.IRON_KODACHI);
      this.tachi(TensuraToolItems.IRON_TACHI);
      this.odachi(TensuraToolItems.IRON_ODACHI);
      this.spear(TensuraToolItems.IRON_SPEAR);
      this.scythe(TensuraToolItems.IRON_SCYTHE);
      this.handheld(TensuraToolItems.IRON_SICKLE);
      this.handheld(TensuraToolItems.SILVER_SWORD);
      this.handheld(TensuraToolItems.SILVER_SHORT_SWORD);
      this.longSword(TensuraToolItems.SILVER_LONG_SWORD);
      this.greatSword(TensuraToolItems.SILVER_GREAT_SWORD);
      this.handheld(TensuraToolItems.SILVER_KATANA);
      this.handheld(TensuraToolItems.SILVER_KODACHI);
      this.tachi(TensuraToolItems.SILVER_TACHI);
      this.odachi(TensuraToolItems.SILVER_ODACHI);
      this.spear(TensuraToolItems.SILVER_SPEAR);
      this.scythe(TensuraToolItems.SILVER_SCYTHE);
      this.handheld(TensuraToolItems.SILVER_AXE);
      this.handheld(TensuraToolItems.SILVER_PICKAXE);
      this.handheld(TensuraToolItems.SILVER_SHOVEL);
      this.handheld(TensuraToolItems.SILVER_HOE);
      this.handheld(TensuraToolItems.SILVER_SICKLE);
      this.handheld(TensuraToolItems.GOLDEN_SHORT_SWORD);
      this.longSword(TensuraToolItems.GOLDEN_LONG_SWORD);
      this.greatSword(TensuraToolItems.GOLDEN_GREAT_SWORD);
      this.handheld(TensuraToolItems.GOLDEN_KATANA);
      this.handheld(TensuraToolItems.GOLDEN_KODACHI);
      this.tachi(TensuraToolItems.GOLDEN_TACHI);
      this.odachi(TensuraToolItems.GOLDEN_ODACHI);
      this.spear(TensuraToolItems.GOLDEN_SPEAR);
      this.scythe(TensuraToolItems.GOLDEN_SCYTHE);
      this.handheld(TensuraToolItems.GOLDEN_SICKLE);
      this.handheld(TensuraToolItems.DIAMOND_SHORT_SWORD);
      this.longSword(TensuraToolItems.DIAMOND_LONG_SWORD);
      this.greatSword(TensuraToolItems.DIAMOND_GREAT_SWORD);
      this.handheld(TensuraToolItems.DIAMOND_KATANA);
      this.handheld(TensuraToolItems.DIAMOND_KODACHI);
      this.tachi(TensuraToolItems.DIAMOND_TACHI);
      this.odachi(TensuraToolItems.DIAMOND_ODACHI);
      this.spear(TensuraToolItems.DIAMOND_SPEAR);
      this.scythe(TensuraToolItems.DIAMOND_SCYTHE);
      this.handheld(TensuraToolItems.DIAMOND_SICKLE);
      this.handheld(TensuraToolItems.NETHERITE_SHORT_SWORD);
      this.longSword(TensuraToolItems.NETHERITE_LONG_SWORD);
      this.greatSword(TensuraToolItems.NETHERITE_GREAT_SWORD);
      this.handheld(TensuraToolItems.NETHERITE_KATANA);
      this.handheld(TensuraToolItems.NETHERITE_KODACHI);
      this.tachi(TensuraToolItems.NETHERITE_TACHI);
      this.odachi(TensuraToolItems.NETHERITE_ODACHI);
      this.spear(TensuraToolItems.NETHERITE_SPEAR);
      this.scythe(TensuraToolItems.NETHERITE_SCYTHE);
      this.handheld(TensuraToolItems.NETHERITE_SICKLE);
      this.handheld(TensuraToolItems.LOW_MAGISTEEL_SWORD);
      this.handheld(TensuraToolItems.LOW_MAGISTEEL_SHORT_SWORD);
      this.longSword(TensuraToolItems.LOW_MAGISTEEL_LONG_SWORD);
      this.greatSword(TensuraToolItems.LOW_MAGISTEEL_GREAT_SWORD);
      this.handheld(TensuraToolItems.LOW_MAGISTEEL_KATANA);
      this.handheld(TensuraToolItems.LOW_MAGISTEEL_KODACHI);
      this.tachi(TensuraToolItems.LOW_MAGISTEEL_TACHI);
      this.odachi(TensuraToolItems.LOW_MAGISTEEL_ODACHI);
      this.spear(TensuraToolItems.LOW_MAGISTEEL_SPEAR);
      this.scythe(TensuraToolItems.LOW_MAGISTEEL_SCYTHE);
      this.handheld(TensuraToolItems.LOW_MAGISTEEL_AXE);
      this.handheld(TensuraToolItems.LOW_MAGISTEEL_PICKAXE);
      this.handheld(TensuraToolItems.LOW_MAGISTEEL_SHOVEL);
      this.handheld(TensuraToolItems.LOW_MAGISTEEL_HOE);
      this.handheld(TensuraToolItems.LOW_MAGISTEEL_SICKLE);
      this.handheld(TensuraToolItems.HIGH_MAGISTEEL_SWORD);
      this.handheld(TensuraToolItems.HIGH_MAGISTEEL_SHORT_SWORD);
      this.longSword(TensuraToolItems.HIGH_MAGISTEEL_LONG_SWORD);
      this.greatSword(TensuraToolItems.HIGH_MAGISTEEL_GREAT_SWORD);
      this.handheld(TensuraToolItems.HIGH_MAGISTEEL_KATANA);
      this.handheld(TensuraToolItems.HIGH_MAGISTEEL_KODACHI);
      this.tachi(TensuraToolItems.HIGH_MAGISTEEL_TACHI);
      this.odachi(TensuraToolItems.HIGH_MAGISTEEL_ODACHI);
      this.spear(TensuraToolItems.HIGH_MAGISTEEL_SPEAR);
      this.scythe(TensuraToolItems.HIGH_MAGISTEEL_SCYTHE);
      this.handheld(TensuraToolItems.HIGH_MAGISTEEL_AXE);
      this.handheld(TensuraToolItems.HIGH_MAGISTEEL_PICKAXE);
      this.handheld(TensuraToolItems.HIGH_MAGISTEEL_SHOVEL);
      this.handheld(TensuraToolItems.HIGH_MAGISTEEL_HOE);
      this.handheld(TensuraToolItems.HIGH_MAGISTEEL_SICKLE);
      this.handheld(TensuraToolItems.MITHRIL_SWORD);
      this.handheld(TensuraToolItems.MITHRIL_SHORT_SWORD);
      this.longSword(TensuraToolItems.MITHRIL_LONG_SWORD);
      this.greatSword(TensuraToolItems.MITHRIL_GREAT_SWORD);
      this.handheld(TensuraToolItems.MITHRIL_KATANA);
      this.handheld(TensuraToolItems.MITHRIL_KODACHI);
      this.tachi(TensuraToolItems.MITHRIL_TACHI);
      this.odachi(TensuraToolItems.MITHRIL_ODACHI);
      this.spear(TensuraToolItems.MITHRIL_SPEAR);
      this.scythe(TensuraToolItems.MITHRIL_SCYTHE);
      this.handheld(TensuraToolItems.MITHRIL_AXE);
      this.handheld(TensuraToolItems.MITHRIL_PICKAXE);
      this.handheld(TensuraToolItems.MITHRIL_SHOVEL);
      this.handheld(TensuraToolItems.MITHRIL_HOE);
      this.handheld(TensuraToolItems.MITHRIL_SICKLE);
      this.handheld(TensuraToolItems.ORICHALCUM_SWORD);
      this.handheld(TensuraToolItems.ORICHALCUM_SHORT_SWORD);
      this.longSword(TensuraToolItems.ORICHALCUM_LONG_SWORD);
      this.greatSword(TensuraToolItems.ORICHALCUM_GREAT_SWORD);
      this.handheld(TensuraToolItems.ORICHALCUM_KATANA);
      this.handheld(TensuraToolItems.ORICHALCUM_KODACHI);
      this.tachi(TensuraToolItems.ORICHALCUM_TACHI);
      this.odachi(TensuraToolItems.ORICHALCUM_ODACHI);
      this.spear(TensuraToolItems.ORICHALCUM_SPEAR);
      this.scythe(TensuraToolItems.ORICHALCUM_SCYTHE);
      this.handheld(TensuraToolItems.ORICHALCUM_AXE);
      this.handheld(TensuraToolItems.ORICHALCUM_PICKAXE);
      this.handheld(TensuraToolItems.ORICHALCUM_SHOVEL);
      this.handheld(TensuraToolItems.ORICHALCUM_HOE);
      this.handheld(TensuraToolItems.ORICHALCUM_SICKLE);
      this.handheld(TensuraToolItems.PURE_MAGISTEEL_SWORD);
      this.handheld(TensuraToolItems.PURE_MAGISTEEL_SHORT_SWORD);
      this.longSword(TensuraToolItems.PURE_MAGISTEEL_LONG_SWORD);
      this.greatSword(TensuraToolItems.PURE_MAGISTEEL_GREAT_SWORD);
      this.handheld(TensuraToolItems.PURE_MAGISTEEL_KATANA);
      this.handheld(TensuraToolItems.PURE_MAGISTEEL_KODACHI);
      this.tachi(TensuraToolItems.PURE_MAGISTEEL_TACHI);
      this.odachi(TensuraToolItems.PURE_MAGISTEEL_ODACHI);
      this.spear(TensuraToolItems.PURE_MAGISTEEL_SPEAR);
      this.scythe(TensuraToolItems.PURE_MAGISTEEL_SCYTHE);
      this.handheld(TensuraToolItems.PURE_MAGISTEEL_AXE);
      this.handheld(TensuraToolItems.PURE_MAGISTEEL_PICKAXE);
      this.handheld(TensuraToolItems.PURE_MAGISTEEL_SHOVEL);
      this.handheld(TensuraToolItems.PURE_MAGISTEEL_HOE);
      this.handheld(TensuraToolItems.PURE_MAGISTEEL_SICKLE);
      this.handheld(TensuraToolItems.ADAMANTITE_SWORD);
      this.handheld(TensuraToolItems.ADAMANTITE_SHORT_SWORD);
      this.longSword(TensuraToolItems.ADAMANTITE_LONG_SWORD);
      this.greatSword(TensuraToolItems.ADAMANTITE_GREAT_SWORD);
      this.handheld(TensuraToolItems.ADAMANTITE_KATANA);
      this.handheld(TensuraToolItems.ADAMANTITE_KODACHI);
      this.tachi(TensuraToolItems.ADAMANTITE_TACHI);
      this.odachi(TensuraToolItems.ADAMANTITE_ODACHI);
      this.spear(TensuraToolItems.ADAMANTITE_SPEAR);
      this.scythe(TensuraToolItems.ADAMANTITE_SCYTHE);
      this.handheld(TensuraToolItems.ADAMANTITE_AXE);
      this.handheld(TensuraToolItems.ADAMANTITE_PICKAXE);
      this.handheld(TensuraToolItems.ADAMANTITE_SHOVEL);
      this.handheld(TensuraToolItems.ADAMANTITE_HOE);
      this.handheld(TensuraToolItems.ADAMANTITE_SICKLE);
      this.handheldHihiirokaneMinecraft(TensuraToolItems.HIHIIROKANE_SWORD);
      this.handheldHihiirokaneMinecraft(TensuraToolItems.HIHIIROKANE_SHORT_SWORD);
      this.handheldHihiirokane(TensuraToolItems.HIHIIROKANE_LONG_SWORD, "item/long_sword_handheld");
      this.handheldHihiirokane(TensuraToolItems.HIHIIROKANE_GREAT_SWORD, "item/great_sword_handheld");
      this.handheldHihiirokaneMinecraft(TensuraToolItems.HIHIIROKANE_KATANA);
      this.handheldHihiirokaneMinecraft(TensuraToolItems.HIHIIROKANE_KODACHI);
      this.handheldHihiirokane(TensuraToolItems.HIHIIROKANE_TACHI, "item/tachi_handheld");
      this.handheldHihiirokane(TensuraToolItems.HIHIIROKANE_ODACHI, "item/odachi_handheld");
      this.handheldHihiirokane(TensuraToolItems.HIHIIROKANE_SCYTHE, "item/scythe_handheld");
      this.handheldHihiirokaneMinecraft(TensuraToolItems.HIHIIROKANE_PICKAXE);
      this.handheldHihiirokaneMinecraft(TensuraToolItems.HIHIIROKANE_AXE);
      this.handheldHihiirokaneMinecraft(TensuraToolItems.HIHIIROKANE_SHOVEL);
      this.handheldHihiirokaneMinecraft(TensuraToolItems.HIHIIROKANE_HOE);
      this.handheldHihiirokaneMinecraft(TensuraToolItems.HIHIIROKANE_SICKLE);
      RegistrySupplier<Item> item = TensuraToolItems.HIHIIROKANE_SPEAR;
      ResourceLocation texture = this.modLoc("item/" + item.getId().getPath());
      ItemModelBuilder spear = ((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(item.getId().getPath(), this.modLoc("item/spear_handheld")))
            .texture("layer0", texture))
         .override()
         .predicate(ResourceLocation.withDefaultNamespace("inactive"), 1.0F)
         .model(
            ((ItemModelBuilder)this.withExistingParent(item.getId().getPath() + "_inactive", this.modLoc("item/spear_handheld")))
               .texture("layer0", texture + "_inactive")
         )
         .end();
      spear.override()
         .predicate(ResourceLocation.withDefaultNamespace("throwing"), 1.0F)
         .model(
            ((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(item.getId().getPath() + "_throwing", this.modLoc("item/spear_handheld_throwing")))
                  .texture("layer0", texture))
               .override()
               .predicate(ResourceLocation.withDefaultNamespace("inactive"), 1.0F)
               .model(
                  ((ItemModelBuilder)this.withExistingParent(item.getId().getPath() + "_throwing_inactive", this.modLoc("item/spear_handheld_throwing")))
                     .texture("layer0", texture + "_inactive")
               )
               .end()
         );
      this.handheld(TensuraToolItems.TEMPEST_SCALE_KNIFE);
      this.handheld(TensuraToolItems.CENTIPEDE_DAGGER);
      this.handheld(TensuraToolItems.SPIDER_DAGGER);
      this.handheld(TensuraToolItems.SISSIE_TOOTH_PICKAXE);
      this.spear(TensuraToolItems.BEAST_HORN_SPEAR);
      this.spear(TensuraToolItems.UNICORN_HORN_SPEAR);
      this.scythe(TensuraToolItems.BLADE_TIGER_SCYTHE);
      this.bow(TensuraToolItems.SHORT_BOW, "short_bow");
      this.bow(TensuraToolItems.LONG_BOW, "long_bow");
      this.bow(TensuraToolItems.WAR_BOW, "war_bow");
      this.bow(TensuraToolItems.SHORT_SPIDER_BOW, "short_bow");
      this.bow(TensuraToolItems.SPIDER_BOW, "bow");
      this.bow(TensuraToolItems.LONG_SPIDER_BOW, "long_bow");
      this.bow(TensuraToolItems.WAR_SPIDER_BOW, "war_spider_bow");
      this.basicItem((Item)TensuraToolItems.INVISIBLE_ARROW.get());
      this.basicItem((Item)TensuraToolItems.SPEARED_FIN_ARROW.get());
      this.basicItem((Item)TensuraToolItems.ORB_OF_DOMINATION.get());
      this.handheld(TensuraToolItems.SEVERER_BLADE);
      this.basicItem((Item)TensuraToolItems.COPPER_SHELL.get());
      this.basicItem((Item)TensuraToolItems.WEB_CARTRIDGE.get());
      this.basicItem((Item)TensuraToolItems.STICKY_WEB_CARTRIDGE.get());
      this.basicItem((Item)TensuraToolItems.STICKY_STEEL_WEB_CARTRIDGE.get());
   }

   private void generateMobDrops() {
      this.basicItem((Item)TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get());
      this.basicItem((Item)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get());
      this.basicItem((Item)TensuraMobDropItems.MONSTER_LEATHER_D.get());
      this.basicItem((Item)TensuraMobDropItems.MONSTER_LEATHER_C.get());
      this.basicItem((Item)TensuraMobDropItems.MONSTER_LEATHER_B.get());
      this.basicItem((Item)TensuraMobDropItems.MONSTER_LEATHER_A.get());
      this.basicItem((Item)TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A.get());
      this.basicItem((Item)TensuraMobDropItems.GIANT_BAT_WING.get());
      this.basicItem((Item)TensuraMobDropItems.INVISIBLE_FEATHER.get());
      this.basicItem((Item)TensuraMobDropItems.DRAGON_PEACOCK_FEATHER.get());
      this.basicItem((Item)TensuraMobDropItems.HELL_MOTH_SILK.get());
      this.basicItem((Item)TensuraMobDropItems.GEHENNA_MOTH_SILK.get());
      this.basicItem((Item)TensuraMobDropItems.GIANT_ANT_CARAPACE.get());
      this.basicItem((Item)TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE.get());
      this.basicItem((Item)TensuraMobDropItems.INSECTAR_CARAPACE.get());
      this.basicItem((Item)TensuraMobDropItems.ARMORSAURUS_SCALE.get());
      this.basicItem((Item)TensuraMobDropItems.ARMORSAURUS_SHELL.get());
      this.basicItem((Item)TensuraMobDropItems.SERPENT_SCALE.get());
      this.basicItem((Item)TensuraMobDropItems.CHARYBDIS_SCALE.get());
      this.basicItem((Item)TensuraMobDropItems.CENTIPEDE_STINGER.get());
      this.basicItem((Item)TensuraMobDropItems.SPIDER_FANG.get());
      this.handheld(TensuraMobDropItems.BLADE_TIGER_TAIL);
      this.basicItem((Item)TensuraMobDropItems.SLIME_CHUNK.get());
      this.basicItem((Item)TensuraMobDropItems.SLIME_CORE.get());
      this.basicItem((Item)TensuraMobDropItems.STICKY_THREAD.get());
      this.basicItem((Item)TensuraMobDropItems.STEEL_THREAD.get());
      this.basicItem((Item)TensuraMobDropItems.SISSIE_TOOTH.get());
      this.basicItem((Item)TensuraMobDropItems.BEAST_HORN.get());
      this.basicItem((Item)TensuraMobDropItems.UNICORN_HORN.get());
      this.basicItem((Item)TensuraMobDropItems.DAEMON_ESSENCE.get());
      this.basicItem((Item)TensuraMobDropItems.DRAGON_ESSENCE.get());
      this.basicItem((Item)TensuraMobDropItems.ELEMENTAL_ESSENCE.get());
      this.basicItem((Item)TensuraMobDropItems.ROYAL_BLOOD.get());
      this.basicItem((Item)TensuraMobDropItems.ZANE_BLOOD.get());
   }

   private void generateConsumables() {
      this.basicItem((Item)TensuraConsumableItems.DUBIOUS_FOOD.get());
      this.basicItem((Item)TensuraConsumableItems.RAW_BLADE_TIGER_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.BLADE_TIGER_STEAK.get());
      this.basicItem((Item)TensuraConsumableItems.RAW_ARMORSAURUS_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.COOKED_ARMORSAURUS_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.CHILLED_SLIME.get());
      this.basicItem((Item)TensuraConsumableItems.BUCKET_OF_CATTLEDEER_MILK.get());
      this.basicItem((Item)TensuraConsumableItems.CATTLEDEER_BEEF.get());
      this.basicItem((Item)TensuraConsumableItems.CATTLEDEER_STEAK.get());
      this.basicItem((Item)TensuraConsumableItems.RAW_CHARYBDIS_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.COOKED_CHARYBDIS_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.GIANT_ANT_LEG.get());
      this.basicItem((Item)TensuraConsumableItems.COOKED_GIANT_ANT_LEG.get());
      this.basicItem((Item)TensuraConsumableItems.RAW_GIANT_BAT_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.COOKED_GIANT_BAT_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.KNIGHT_SPIDER_LEG.get());
      this.basicItem((Item)TensuraConsumableItems.COOKED_KNIGHT_SPIDER_LEG.get());
      this.basicItem((Item)TensuraConsumableItems.RAW_MEGALODON_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.COOKED_MEGALODON_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.RAW_SERPENT_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.COOKED_SERPENT_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.RAW_SPEAR_TORO_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.COOKED_SPEAR_TORO_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.SPEAR_TORO_FIN.get());
      this.basicItem((Item)TensuraConsumableItems.COOKED_SPEAR_TORO_FIN.get());
      this.basicItem((Item)TensuraConsumableItems.RAW_SISSIE_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.COOKED_SISSIE_MEAT.get());
      this.basicItem((Item)TensuraConsumableItems.SISSIE_FIN.get());
      this.basicItem((Item)TensuraConsumableItems.COOKED_SISSIE_FIN.get());
      this.basicItem((Item)TensuraConsumableItems.SILVER_APPLE.get());
      this.basicItem((Item)TensuraConsumableItems.ENCHANTED_SILVER_APPLE.get());
      this.basicItem((Item)TensuraConsumableItems.MAGIC_BOTTLE.get());
      this.basicItem((Item)TensuraConsumableItems.WATER_MAGIC_BOTTLE.get());
      this.basicItem((Item)TensuraConsumableItems.VACUUMED_WATER_MAGIC_BOTTLE.get());
      this.basicItem((Item)TensuraConsumableItems.LOW_POTION.get());
      this.basicItem((Item)TensuraConsumableItems.HIGH_POTION.get());
      this.basicItem((Item)TensuraConsumableItems.FULL_POTION.get());
      this.basicItem((Item)TensuraConsumableItems.REVIVAL_ELIXIR.get());
      this.basicItem((Item)TensuraConsumableItems.LOW_ARCANE_POTION.get());
      this.basicItem((Item)TensuraConsumableItems.MEDIUM_ARCANE_POTION.get());
      this.basicItem((Item)TensuraConsumableItems.HIGH_ARCANE_POTION.get());
   }

   private void generateMiscItems() {
      this.basicItem((Item)TensuraMaterialItems.MUSIC_DISC_NANODA.get());
      this.basicItem((Item)TensuraMaterialItems.DWARGON_BANNER_PATTERN.get());
      this.basicItem((Item)TensuraMaterialItems.SHADOW_STORAGE.get());
      this.basicItem((Item)TensuraMaterialItems.THATCH.get());
      this.basicItem((Item)TensuraMaterialItems.HIPOKUTE_SEEDS.get());
      this.basicItem((Item)TensuraMaterialItems.HIPOKUTE_GRASS.get());
      this.basicItem((Item)TensuraMaterialItems.HIPOKUTE_FLOWER.get());
      this.basicItem((Item)TensuraMaterialItems.MONSTER_SADDLE.get());
      this.basicItem((Item)TensuraMaterialItems.RAW_SILVER.get());
      this.basicItem((Item)TensuraMaterialItems.SILVER_NUGGET.get());
      this.basicItem((Item)TensuraMaterialItems.MAGIC_ORE.get());
      this.basicItem((Item)TensuraMaterialItems.SILVER_INGOT.get());
      this.basicItem((Item)TensuraMaterialItems.LOW_MAGISTEEL_NUGGET.get());
      this.basicItem((Item)TensuraMaterialItems.HIGH_MAGISTEEL_NUGGET.get());
      this.basicItem((Item)TensuraMaterialItems.MITHRIL_NUGGET.get());
      this.basicItem((Item)TensuraMaterialItems.ORICHALCUM_NUGGET.get());
      this.basicItem((Item)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get());
      this.basicItem((Item)TensuraMaterialItems.ADAMANTITE_NUGGET.get());
      this.basicItem((Item)TensuraMaterialItems.HIHIIROKANE_NUGGET.get());
      this.basicItem((Item)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get());
      this.basicItem((Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get());
      this.basicItem((Item)TensuraMaterialItems.MITHRIL_INGOT.get());
      this.basicItem((Item)TensuraMaterialItems.ORICHALCUM_INGOT.get());
      this.basicItem((Item)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get());
      this.basicItem((Item)TensuraMaterialItems.ADAMANTITE_INGOT.get());
      this.basicItem((Item)TensuraMaterialItems.HIHIIROKANE_INGOT.get());
      this.basicItem((Item)TensuraMaterialItems.MAGIC_STONE.get());
      this.basicItem((Item)TensuraMaterialItems.WARP_CORE.get());
      this.basicItem((Item)TensuraMaterialItems.DAEMON_CORE.get());
      this.basicItem((Item)TensuraMaterialItems.LOW_MAGISTEEL_BONE_GOLEM.get());
      this.basicItem((Item)TensuraMaterialItems.HIGH_MAGISTEEL_BONE_GOLEM.get());
      this.basicItem((Item)TensuraMaterialItems.MITHRIL_BONE_GOLEM.get());
      this.basicItem((Item)TensuraMaterialItems.PURE_MAGISTEEL_BONE_GOLEM.get());
      this.basicItem((Item)TensuraMaterialItems.ORICHALCUM_BONE_GOLEM.get());
      this.basicItem((Item)TensuraMaterialItems.ADAMANTITE_BONE_GOLEM.get());
      this.basicItem((Item)TensuraMaterialItems.HIHIIROKANE_BONE_GOLEM.get());
      this.basicItem((Item)TensuraMaterialItems.ELEMENT_CORE_EMPTY.get());
      this.basicItem((Item)TensuraMaterialItems.ELEMENT_CORE_EARTH.get());
      this.basicItem((Item)TensuraMaterialItems.ELEMENT_CORE_FIRE.get());
      this.basicItem((Item)TensuraMaterialItems.ELEMENT_CORE_SPACE.get());
      this.basicItem((Item)TensuraMaterialItems.ELEMENT_CORE_WATER.get());
      this.basicItem((Item)TensuraMaterialItems.ELEMENT_CORE_WIND.get());
      this.basicItem((Item)TensuraMaterialItems.EARTH_ELEMENTAL_SHARD.get());
      this.basicItem((Item)TensuraMaterialItems.FIRE_ELEMENTAL_SHARD.get());
      this.basicItem((Item)TensuraMaterialItems.SPACE_ELEMENTAL_SHARD.get());
      this.basicItem((Item)TensuraMaterialItems.WATER_ELEMENTAL_SHARD.get());
      this.basicItem((Item)TensuraMaterialItems.WIND_ELEMENTAL_SHARD.get());
      this.basicItem((Item)TensuraMaterialItems.BLACK_FIRE_CHARGE.get());
      this.basicItem((Item)TensuraMaterialItems.BRONZE_COIN.get());
      this.basicItem((Item)TensuraMaterialItems.SILVER_COIN.get());
      this.basicItem((Item)TensuraMaterialItems.GOLD_COIN.get());
      this.basicItem((Item)TensuraMaterialItems.STELLAR_GOLD_COIN.get());
      this.basicItem((Item)TensuraMaterialItems.POUCH_D.get());
      this.basicItem((Item)TensuraMaterialItems.POUCH_C.get());
      this.basicItem((Item)TensuraMaterialItems.POUCH_B.get());
      this.basicItem((Item)TensuraMaterialItems.POUCH_A.get());
      this.basicItem((Item)TensuraMaterialItems.POUCH_SPECIAL_A.get());
      this.basicItem((Item)TensuraMaterialItems.SPATIAL_BAG.get());
      this.basicItem((Item)TensuraMaterialItems.MARIONETTE_HEART.get());
      this.basicItem((Item)TensuraMaterialItems.UNBOUND_TOME.get());
      this.basicItem((Item)TensuraMaterialItems.BATTLEWILL_MANUAL.get());
      this.basicItem((Item)TensuraMaterialItems.RACE_RESET_SCROLL.get());
      this.basicItem((Item)TensuraMaterialItems.SKILL_RESET_SCROLL.get());
      this.basicItem((Item)TensuraMaterialItems.CHARACTER_RESET_SCROLL.get());
   }

   private void generateSchematics() {
      this.basicItem((Item)TensuraSmithingSchematicItems.BASIC_BOWS.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.SPIDER_BOWS.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.JAPANESE_SWORD.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.HUNTING_KNIFE.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.SHORT_SWORD.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.LONG_SWORD.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.GREAT_SWORD.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.SPEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.KUNAI.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.SHIELD.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.MAGIC_STAFF.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.LEATHER_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.GOLD_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.IRON_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.SILVER_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.ANT_CARAPACE_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.SERPENT_SCALEMAIL_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.DIAMOND_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.KNIGHT_SPIDER_CARAPACE_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.ARMORSAURUS_SCALEMAIL_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.CHARYBDIS_SCALEMAIL_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.MITHRIL_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.ORICHALCUM_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.ADAMANTITE_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.HIHIIROKANE_GEAR.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.ANTI_MAGIC_MASK.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.DARK_SET.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.PIERROT_MASK.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.SPATIAL_BLADE.get());
      this.basicItem((Item)TensuraSmithingSchematicItems.WEB_GUN.get());
   }

   private void generateSpawnEggs() {
      this.spawnEggItem((Item)TensuraSpawnEggs.FOLGEN.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.HINATA_SAKAGUCHI.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.KIRARA_MIZUTANI.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.KYOYA_TACHIBANA.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.MAI_FURUKI.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.MARK_LAUREN.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.SHINJI_TANIMURA.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.SHIN_RYUSEI.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.SHIZU.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.SHOGO_TAGUCHI.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.AKASH.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.AQUA_FROG.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.ARCH_DAEMON.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.ARMORSAURUS.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.ARMY_WASP.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.BARGHEST.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.BASILISK.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.BEAST_GNOME.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.BLADE_TIGER.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.BLACK_SPIDER.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.CATTLEDEER.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.CHARYBDIS.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.DIREWOLF.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.DRAGON_PEACOCK.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.DWARF.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.GAZEL_DWARGO.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.ELEMENTAL_COLOSSUS.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.EVIL_CENTIPEDE.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.FEATHERED_SERPENT.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.GIANT_ANT.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.GIANT_BAT.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.GIANT_BEAR.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.GIANT_COD.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.GIANT_SALMON.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.GOBLIN.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.GREATER_DAEMON.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.HELL_CATERPILLAR.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.HELL_MOTH.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.HORNED_BEAR.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.HORNED_RABBIT.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.HOUND_DOG.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.HOVER_LIZARD.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.IFRIT.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.KNIGHT_SPIDER.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.LANDFISH.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.LEECH_LIZARD.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.LESSER_DAEMON.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.LIZARDMAN.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.MEGALODON.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.ONE_EYED_OWL.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.ORC.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.ORC_LORD.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.ORC_DISASTER.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.PEGASUS.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.PEGACORN.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.PHANTASPORE.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.SALAMANDER.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.SISSIE.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.SKELETON.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.SLIME.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.METAL_SLIME.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.SUPERMASSIVE_SLIME.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.SPEAR_TORO.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.SYLPHIDE.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.UNDINE.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.UNICORN.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.TEMPEST_SERPENT.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.WAR_GNOME.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.WINGED_CAT.get());
      this.spawnEggItem((Item)TensuraSpawnEggs.ZOMBIE.get());
   }

   private void generateBlocks() {
      this.simpleItem(TensuraBlocks.Items.PALM_SAPLING, "block/palm_sapling");
      this.cubeBlockItem(TensuraBlocks.PALM_LEAVES);
      this.cubeBlockItem(TensuraBlocks.PALM_LOG);
      this.cubeBlockItem(TensuraBlocks.PALM_WOOD);
      this.cubeBlockItem(TensuraBlocks.STRIPPED_PALM_LOG);
      this.cubeBlockItem(TensuraBlocks.STRIPPED_PALM_WOOD);
      this.cubeBlockItem(TensuraBlocks.PALM_PLANKS);
      this.stairBlockItem(TensuraBlocks.PALM_STAIRS);
      this.slabBlockItem(TensuraBlocks.PALM_SLAB);
      this.fenceItem(TensuraBlocks.PALM_FENCE, TensuraBlocks.PALM_PLANKS);
      this.fenceGateItem(TensuraBlocks.PALM_FENCE_GATE);
      this.trapdoorItem(TensuraBlocks.PALM_TRAPDOOR);
      this.woodenPlateItem(TensuraBlocks.PALM_PRESSURE_PLATE);
      this.buttonItem(TensuraBlocks.PALM_BUTTON, TensuraBlocks.PALM_PLANKS);
      this.simpleItem(TensuraBlocks.Items.PALM_DOOR, "item/palm_door");
      this.simpleItem(TensuraBlocks.Items.PALM_SIGN, "item/palm_sign");
      this.simpleItem(TensuraBlocks.Items.PALM_HANGING_SIGN, "item/palm_hanging_sign");
      this.basicItem((Item)TensuraBlocks.Items.PALM_BOAT.get());
      this.basicItem((Item)TensuraBlocks.Items.PALM_CHEST_BOAT.get());
      this.cubeBlockItem(TensuraBlocks.THATCH_BLOCK);
      this.stairBlockItem(TensuraBlocks.THATCH_STAIRS);
      this.slabBlockItem(TensuraBlocks.THATCH_SLAB);
      this.wallItem(TensuraBlocks.THATCH_WALL, TensuraBlocks.THATCH_BLOCK);
      this.cubeBlockItem(TensuraBlocks.TATAMI_BLOCK);
      this.cubeBlockItem(TensuraBlocks.TATAMI_CARPET);
      this.cubeBlockItem(TensuraBlocks.SINGLE_TATAMI_BLOCK);
      this.cubeBlockItem(TensuraBlocks.SINGLE_TATAMI_CARPET);
      this.cubeBlockItem(TensuraBlocks.SARASA_SAND);
      this.cubeBlockItem(TensuraBlocks.SARASA_SANDSTONE);
      this.cubeBlockItem(TensuraBlocks.CHISELED_SARASA_SANDSTONE);
      this.cubeBlockItem(TensuraBlocks.CUT_SARASA_SANDSTONE);
      this.cubeBlockItem(TensuraBlocks.SMOOTH_SARASA_SANDSTONE);
      this.stairBlockItem(TensuraBlocks.SARASA_SANDSTONE_STAIRS);
      this.stairBlockItem(TensuraBlocks.SMOOTH_SARASA_SANDSTONE_STAIRS);
      this.slabBlockItem(TensuraBlocks.SARASA_SANDSTONE_SLAB);
      this.slabBlockItem(TensuraBlocks.CUT_SARASA_SANDSTONE_SLAB);
      this.slabBlockItem(TensuraBlocks.SMOOTH_SARASA_SANDSTONE_SLAB);
      this.wallItem(TensuraBlocks.SARASA_SANDSTONE_WALL, TensuraBlocks.SARASA_SANDSTONE);
      this.cubeBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.cubeBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.cubeBlockItem(TensuraBlocks.CHISELED_LOW_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.stairBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_STAIRS);
      this.stairBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS);
      this.slabBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_SLAB);
      this.slabBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB);
      this.wallItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_WALL, TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.cubeBlockItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.cubeBlockItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.cubeBlockItem(TensuraBlocks.CHISELED_MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.stairBlockItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_STAIRS);
      this.stairBlockItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS);
      this.slabBlockItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_SLAB);
      this.slabBlockItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB);
      this.wallItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_WALL, TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.cubeBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.cubeBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.cubeBlockItem(TensuraBlocks.CHISELED_HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.stairBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_STAIRS);
      this.stairBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS);
      this.slabBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_SLAB);
      this.slabBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB);
      this.wallItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_WALL, TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.cubeBlockItem(TensuraBlocks.MAGIC_ORE);
      this.cubeBlockItem(TensuraBlocks.SILVER_ORE);
      this.cubeBlockItem(TensuraBlocks.DEEPSLATE_MAGIC_ORE);
      this.cubeBlockItem(TensuraBlocks.DEEPSLATE_SILVER_ORE);
      this.cubeBlockItem(TensuraBlocks.RAW_SILVER_BLOCK);
      this.cubeBlockItem(TensuraBlocks.SILVER_BLOCK);
      this.cubeBlockItem(TensuraBlocks.MAGIC_ORE_BLOCK);
      this.cubeBlockItem(TensuraBlocks.LOW_MAGISTEEL_BLOCK);
      this.cubeBlockItem(TensuraBlocks.HIGH_MAGISTEEL_BLOCK);
      this.cubeBlockItem(TensuraBlocks.PURE_MAGISTEEL_BLOCK);
      this.cubeBlockItem(TensuraBlocks.MITHRIL_BLOCK);
      this.cubeBlockItem(TensuraBlocks.ORICHALCUM_BLOCK);
      this.cubeBlockItem(TensuraBlocks.ADAMANTITE_BLOCK);
      this.cubeBlockItem(TensuraBlocks.HIHIIROKANE_BLOCK);
      this.cubeBlockItem(TensuraBlocks.SPIDER_EGG);
      this.simpleItem(TensuraBlocks.Items.STICKY_COBWEB, "block/sticky_cobweb");
      this.simpleItem(TensuraBlocks.Items.STICKY_STEEL_COBWEB, "block/sticky_steel_cobweb");
      this.simpleItem(TensuraMaterialItems.BAFFLEDIL, "block/baffledil");
      this.cubeBlockItem(TensuraBlocks.LOOSE_DIRT);
      this.cubeBlockItem(TensuraBlocks.LOOSE_GRAVEL);
      this.cubeBlockItem(TensuraBlocks.QUICKMUD);
      this.cubeBlockItem(TensuraBlocks.QUICKSAND);
      this.cubeBlockItem(TensuraBlocks.RED_QUICKSAND);
      this.cubeBlockItem(TensuraBlocks.SARASA_QUICKSAND);
      this.cubeBlockItemVariant(TensuraBlocks.WEB_BLOCK);
      this.stairBlockItemVariant(TensuraBlocks.WEB_STAIRS);
      this.slabBlockItemVariant(TensuraBlocks.WEB_SLAB);
      this.cubeBlockItemVariant(TensuraBlocks.WEBBED_COBBLESTONE);
      this.stairBlockItemVariant(TensuraBlocks.WEBBED_COBBLESTONE_STAIRS);
      this.slabBlockItemVariant(TensuraBlocks.WEBBED_COBBLESTONE_SLAB);
      this.wallItemVariant(TensuraBlocks.WEBBED_COBBLESTONE_WALL, TensuraBlocks.WEBBED_COBBLESTONE);
      this.cubeBlockItemVariant(TensuraBlocks.WEBBED_STONE_BRICKS);
      this.stairBlockItemVariant(TensuraBlocks.WEBBED_STONE_BRICK_STAIRS);
      this.slabBlockItemVariant(TensuraBlocks.WEBBED_STONE_BRICK_SLAB);
      this.wallItemVariant(TensuraBlocks.WEBBED_STONE_BRICK_WALL, TensuraBlocks.WEBBED_STONE_BRICKS);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_BRICKS);
      this.stairBlockItem(TensuraBlocks.LABYRINTH_BRICK_STAIR);
      this.slabBlockItem(TensuraBlocks.LABYRINTH_BRICK_SLAB);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_BRICK_TL);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_BRICK_TR);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_BRICK_BL);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_BRICK_BR);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_STONE);
      this.stairBlockItem(TensuraBlocks.LABYRINTH_STONE_STAIR);
      this.slabBlockItem(TensuraBlocks.LABYRINTH_STONE_SLAB);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_STONE_TL);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_STONE_TR);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_STONE_BL);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_STONE_BR);
      this.cubeBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICKS);
      this.stairBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_STAIR);
      this.slabBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_SLAB);
      this.cubeBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_TL);
      this.cubeBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_TR);
      this.cubeBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_BL);
      this.cubeBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_BR);
      this.cubeBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE);
      this.stairBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_STAIR);
      this.slabBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_SLAB);
      this.cubeBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_TL);
      this.cubeBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_TR);
      this.cubeBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_BL);
      this.cubeBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_BR);
      this.cubeBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICKS);
      this.stairBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_STAIR);
      this.slabBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_SLAB);
      this.cubeBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_TL);
      this.cubeBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_TR);
      this.cubeBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_BL);
      this.cubeBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_BR);
      this.cubeBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE);
      this.stairBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_STAIR);
      this.slabBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_SLAB);
      this.cubeBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_TL);
      this.cubeBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_TR);
      this.cubeBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_BL);
      this.cubeBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_BR);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_LAMP);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_LAMP_TL);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_LAMP_TR);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_LAMP_BL);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_LAMP_BR);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_LIT_LAMP);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_LIT_LAMP_TL);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_LIT_LAMP_TR);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_LIT_LAMP_BL);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_LIT_LAMP_BR);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_CRYSTAL);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_LIGHT_PATH);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_PRAYING_PATH);
      this.stairBlockItem(TensuraBlocks.LABYRINTH_LIGHT_PATH_STAIRS);
      this.slabBlockItem(TensuraBlocks.LABYRINTH_LIGHT_PATH_SLAB);
      this.simpleItem(TensuraBlocks.Items.LABYRINTH_BARRIER_BLOCK, "item/yellow_barrier");
      this.simpleItem(TensuraBlocks.Items.LABYRINTH_PORTAL, "item/pink_barrier");
      this.cubeBlockItem(TensuraBlocks.MINING_STATION);
      this.cubeBlockItem(TensuraBlocks.SMITHING_BENCH);
      this.cubeBlockItem(TensuraBlocks.SPELLBINDING_TABLE);
      this.cubeBlockItem(TensuraBlocks.WOODCUTTER);
      this.cubeBlockItem(TensuraBlocks.OAK_TOOL_RACK);
      this.cubeBlockItem(TensuraBlocks.SPRUCE_TOOL_RACK);
      this.cubeBlockItem(TensuraBlocks.BIRCH_TOOL_RACK);
      this.cubeBlockItem(TensuraBlocks.JUNGLE_TOOL_RACK);
      this.cubeBlockItem(TensuraBlocks.ACACIA_TOOL_RACK);
      this.cubeBlockItem(TensuraBlocks.DARK_OAK_TOOL_RACK);
      this.cubeBlockItem(TensuraBlocks.MANGROVE_TOOL_RACK);
      this.cubeBlockItem(TensuraBlocks.CHERRY_TOOL_RACK);
      this.cubeBlockItem(TensuraBlocks.PALM_TOOL_RACK);
      this.cubeBlockItem(TensuraBlocks.BAMBOO_TOOL_RACK);
      this.cubeBlockItem(TensuraBlocks.CRIMSON_TOOL_RACK);
      this.cubeBlockItem(TensuraBlocks.WARPED_TOOL_RACK);
      this.kilnModel(
         TensuraBlocks.Items.KILN_MITHRIL,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/kiln"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/kiln_mithril_bottom"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/kiln_mithril_top"),
         ResourceLocation.withDefaultNamespace("block/obsidian")
      );
      this.kilnModel(
         TensuraBlocks.Items.KILN_ORICHALCUM,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/kiln"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/kiln_orichalcum_bottom"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/kiln_orichalcum_top"),
         ResourceLocation.withDefaultNamespace("block/obsidian")
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.STONE_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/stone_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.GRANITE_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/granite_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.DIORITE_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/diorite_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.ANDESITE_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/andesite_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.CALCITE_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/calcite_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.TUFF_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/tuff_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.DEEPSLATE_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/deepslate_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.BRICK_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/brick_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.SANDSTONE_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/sandstone_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.RED_SANDSTONE_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/red_sandstone_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.SARASA_SANDSTONE_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/sarasa_sandstone_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.PACKED_MUD_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/packed_mud_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.PRISMARINE_BRICK_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/prismarine_brick_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.NETHER_BRICK_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/nether_brick_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.RED_NETHER_BRICK_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/red_nether_brick_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.BLACKSTONE_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/blackstone_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.BASALT_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/basalt_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.QUARTZ_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/quartz_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.END_STONE_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/end_stone_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.PURPUR_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/purpur_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.ROYAL_DWARVEN_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/royal_dwarven_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.LABYRINTH_BRICKS_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/labyrinth_brick_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.CREAM_LABYRINTH_BRICKS_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/cream_labyrinth_brick_warp_pad"),
         "1"
      );
      this.simpleOverrideModel(
         TensuraBlocks.Items.DARK_LABYRINTH_BRICKS_WARP_PAD,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/warp_pad_large"),
         ResourceLocation.fromNamespaceAndPath("tensura", "block/dark_labyrinth_brick_warp_pad"),
         "1"
      );
      this.cubeBlockItem(TensuraBlocks.BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.STONE_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.TUFF_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.DEEPSLATE_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.MUD_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.PRISMARINE_BRICK_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.RED_NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.POLISHED_BLACKSTONE_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.QUARTZ_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.END_STONE_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.PURPUR_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.LABYRINTH_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICKS_MAGIC_ENGINE);
      this.cubeBlockItem(TensuraBlocks.HELL_PORTAL);
      this.cubeBlockItem(TensuraBlocks.SLIME_CHUNK_BLOCK);
      this.cubeBlockItem(TensuraBlocks.CHILLED_SLIME_BLOCK);
      this.simpleItem(TensuraBlocks.TRAINING_DUMMY, "item/training_dummy");
      this.simpleItem(TensuraMobDropItems.MOTH_EGG, "item/moth_egg");
      this.simpleItem(TensuraMobDropItems.CHARYBDIS_CORE, "item/charybdis_core");
      this.simpleItem(TensuraToolItems.GRIMOIRE_D.getId().getPath() + "_gui", "item/grimoire_d_item");
      this.simpleOverrideModel(
         TensuraToolItems.GRIMOIRE_D,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/grimoire"),
         ResourceLocation.fromNamespaceAndPath("tensura", "item/grimoire_d")
      );
      this.simpleItem(TensuraToolItems.GRIMOIRE_C.getId().getPath() + "_gui", "item/grimoire_c_item");
      this.simpleOverrideModel(
         TensuraToolItems.GRIMOIRE_C,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/grimoire"),
         ResourceLocation.fromNamespaceAndPath("tensura", "item/grimoire_c")
      );
      this.simpleItem(TensuraToolItems.GRIMOIRE_B.getId().getPath() + "_gui", "item/grimoire_b_item");
      this.simpleOverrideModel(
         TensuraToolItems.GRIMOIRE_B,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/grimoire"),
         ResourceLocation.fromNamespaceAndPath("tensura", "item/grimoire_b")
      );
      this.simpleItem(TensuraToolItems.GRIMOIRE_A.getId().getPath() + "_gui", "item/grimoire_a_item");
      this.simpleOverrideModel(
         TensuraToolItems.GRIMOIRE_A,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/grimoire"),
         ResourceLocation.fromNamespaceAndPath("tensura", "item/grimoire_a")
      );
      this.simpleItem(TensuraToolItems.GRIMOIRE_SPECIAL_A.getId().getPath() + "_gui", "item/grimoire_special_a_item");
      this.simpleOverrideModel(
         TensuraToolItems.GRIMOIRE_SPECIAL_A,
         ResourceLocation.fromNamespaceAndPath("tensura", "item/grimoire"),
         ResourceLocation.fromNamespaceAndPath("tensura", "item/grimoire_special_a")
      );
   }

   private void simpleItem(String path, String location) {
      ((ItemModelBuilder)this.withExistingParent(path, ResourceLocation.withDefaultNamespace("item/generated")))
         .texture("layer0", ResourceLocation.fromNamespaceAndPath("tensura", location));
   }

   private void simpleItem(RegistrySupplier<?> item, String location) {
      this.simpleItem(item.getId().getPath(), location);
   }

   public void layeredItem(RegistrySupplier<? extends Item> item, String layer0, String layer1) {
      ResourceLocation location = item.getId();
      ((ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)this.getBuilder(location.toString())).parent(new UncheckedModelFile("item/generated")))
            .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/" + location.getPath() + layer0)))
         .texture("layer1", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/" + location.getPath() + layer1));
   }

   private ItemModelBuilder handheldModdedItem(RegistrySupplier<? extends Item> item, String parent) {
      return (ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(item.getId().getPath(), this.modLoc(parent)))
         .texture("layer0", ResourceLocation.fromNamespaceAndPath("tensura", "item/" + item.getId().getPath()));
   }

   private ItemModelBuilder handheldMinecraftItem(RegistrySupplier<? extends Item> item, String parent) {
      return (ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(item.getId().getPath(), this.mcLoc(parent)))
         .texture("layer0", ResourceLocation.fromNamespaceAndPath("tensura", "item/" + item.getId().getPath()));
   }

   private void handheldHihiirokane(RegistrySupplier<? extends Item> item, String parent) {
      ResourceLocation texture = this.modLoc("item/" + item.getId().getPath());
      this.handheldModdedItem(item, parent)
         .override()
         .predicate(ResourceLocation.withDefaultNamespace("inactive"), 1.0F)
         .model(((ItemModelBuilder)this.withExistingParent(item.getId().getPath() + "_inactive", this.modLoc(parent))).texture("layer0", texture + "_inactive"));
   }

   private void handheldHihiirokaneMinecraft(RegistrySupplier<? extends Item> item) {
      ResourceLocation texture = this.modLoc("item/" + item.getId().getPath());
      this.handheldMinecraftItem(item, "item/handheld")
         .override()
         .predicate(ResourceLocation.withDefaultNamespace("inactive"), 1.0F)
         .model(
            ((ItemModelBuilder)this.withExistingParent(item.getId().getPath() + "_inactive", this.mcLoc("item/handheld")))
               .texture("layer0", texture + "_inactive")
         );
   }

   private ItemModelBuilder handheld(RegistrySupplier<? extends Item> item) {
      return this.handheldMinecraftItem(item, "item/handheld");
   }

   private ItemModelBuilder odachi(RegistrySupplier<Item> item) {
      return this.handheldModdedItem(item, "item/odachi_handheld");
   }

   private ItemModelBuilder greatSword(RegistrySupplier<Item> item) {
      return this.handheldModdedItem(item, "item/great_sword_handheld");
   }

   private ItemModelBuilder tachi(RegistrySupplier<Item> item) {
      return this.handheldModdedItem(item, "item/tachi_handheld");
   }

   private ItemModelBuilder longSword(RegistrySupplier<Item> item) {
      return this.handheldModdedItem(item, "item/long_sword_handheld");
   }

   private ItemModelBuilder scythe(RegistrySupplier<Item> item) {
      return this.handheldModdedItem(item, "item/scythe_handheld");
   }

   private ItemModelBuilder spear(RegistrySupplier<Item> item) {
      ResourceLocation texture = this.modLoc("item/" + item.getId().getPath());
      return ((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(item.getId().getPath(), this.modLoc("item/spear_handheld")))
            .texture("layer0", texture))
         .override()
         .predicate(ResourceLocation.withDefaultNamespace("throwing"), 1.0F)
         .model(
            ((ItemModelBuilder)this.withExistingParent(item.getId().getPath() + "_throwing", this.modLoc("item/spear_handheld_throwing")))
               .texture("layer0", texture)
         )
         .end();
   }

   private void bow(RegistrySupplier<Item> item, String bowType) {
      String path = "item/" + item.getId().getPath();
      ResourceLocation type = this.modLoc("item/" + bowType + "_handheld");
      ItemModelBuilder modelBuilder = ((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(item.getId().getPath(), type))
            .texture("layer0", this.modLoc(path)))
         .override()
         .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0F)
         .model(((ItemModelBuilder)this.withExistingParent(item.getId().getPath() + "_pulling_0", type)).texture("layer0", this.modLoc(path + "_pulling_0")))
         .end();
      modelBuilder.override()
         .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0F)
         .predicate(ResourceLocation.withDefaultNamespace("pull"), 0.65F)
         .model(((ItemModelBuilder)this.withExistingParent(item.getId().getPath() + "_pulling_1", type)).texture("layer0", this.modLoc(path + "_pulling_1")));
      modelBuilder.override()
         .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0F)
         .predicate(ResourceLocation.withDefaultNamespace("pull"), 0.9F)
         .model(((ItemModelBuilder)this.withExistingParent(item.getId().getPath() + "_pulling_2", type)).texture("layer0", this.modLoc(path + "_pulling_2")));
   }

   private void armorWithTrim(ArmorItem armor) {
      ItemModelBuilder armorBuilder = this.basicItem(armor);
      String name = BuiltInRegistries.ITEM.getKey(armor).getPath();

      for (TrimModelData trimModelData : ItemModelGenerators.GENERATED_TRIM_MODELS) {
         ModelFile trimModel = ((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(
                  name + "_" + trimModelData.name() + "_trim", this.mcLoc("item/generated")
               ))
               .texture("layer0", this.modLoc("item/" + name)))
            .texture("layer1", this.mcLoc("trims/items/" + armor.getType().getName() + "_trim_" + trimModelData.name()));
         armorBuilder.override().predicate(ResourceLocation.withDefaultNamespace("trim_type"), trimModelData.itemModelIndex()).model(trimModel).end();
      }
   }

   public void spawnEggItem(RegistrySupplier<Item> item) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.ITEM.getKey((Item)item.get()).getPath(), ResourceLocation.withDefaultNamespace("item/template_spawn_egg")
      );
   }

   private ItemModelBuilder simpleOverrideModel(RegistrySupplier<? extends Item> item, ResourceLocation parent, ResourceLocation texture) {
      return this.simpleOverrideModel(item, parent, texture, "0");
   }

   private ItemModelBuilder simpleOverrideModel(RegistrySupplier<? extends Item> item, ResourceLocation parent, ResourceLocation texture, String id) {
      return (ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(item.getId().getPath(), parent)).texture(id, texture))
         .texture("particle", texture);
   }

   private ItemModelBuilder kilnModel(
      RegistrySupplier<? extends Item> item, ResourceLocation parent, ResourceLocation texture0, ResourceLocation texture1, ResourceLocation particle
   ) {
      return (ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(item.getId().getPath(), parent))
               .texture("0", texture0))
            .texture("1", texture1))
         .texture("particle", particle);
   }

   public void cubeBlockItem(RegistrySupplier<? extends Block> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath())
      );
   }

   public void cubeBlockItem(RegistrySupplier<? extends Block> block, Block toCopy) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(), this.modLoc("block/" + BuiltInRegistries.BLOCK.getKey(toCopy).getPath())
      );
   }

   public void slabBlockItem(RegistrySupplier<SlabBlock> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath())
      );
   }

   public void stairBlockItem(RegistrySupplier<StairBlock> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath())
      );
   }

   public void wallItem(RegistrySupplier<WallBlock> block, RegistrySupplier<? extends Block> baseBlock) {
      ((ItemModelBuilder)this.withExistingParent(BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(), this.mcLoc("block/wall_inventory")))
         .texture("wall", ResourceLocation.fromNamespaceAndPath("tensura", "block/" + BuiltInRegistries.BLOCK.getKey((Block)baseBlock.get()).getPath()));
   }

   public void cubeBlockItemVariant(RegistrySupplier<? extends Block> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath() + "_0")
      );
   }

   public void slabBlockItemVariant(RegistrySupplier<SlabBlock> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath() + "_0")
      );
   }

   public void stairBlockItemVariant(RegistrySupplier<StairBlock> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath() + "_0")
      );
   }

   public void wallItemVariant(RegistrySupplier<WallBlock> block, RegistrySupplier<Block> baseBlock) {
      ((ItemModelBuilder)this.withExistingParent(BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(), this.mcLoc("block/wall_inventory")))
         .texture("wall", ResourceLocation.fromNamespaceAndPath("tensura", "block/" + BuiltInRegistries.BLOCK.getKey((Block)baseBlock.get()).getPath() + "_0"));
   }

   public void woodenPlateItem(RegistrySupplier<PressurePlateBlock> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath())
      );
   }

   public void fenceGateItem(RegistrySupplier<FenceGateBlock> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath())
      );
   }

   public void trapdoorItem(RegistrySupplier<TrapDoorBlock> block) {
      this.withExistingParent(
         BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath() + "_bottom")
      );
   }

   public void fenceItem(RegistrySupplier<FenceBlock> block, RegistrySupplier<Block> baseBlock) {
      ((ItemModelBuilder)this.withExistingParent(BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(), this.mcLoc("block/fence_inventory")))
         .texture("texture", ResourceLocation.fromNamespaceAndPath("tensura", "block/" + BuiltInRegistries.BLOCK.getKey((Block)baseBlock.get()).getPath()));
   }

   public void buttonItem(RegistrySupplier<ButtonBlock> block, RegistrySupplier<Block> baseBlock) {
      ((ItemModelBuilder)this.withExistingParent(BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(), this.mcLoc("block/button_inventory")))
         .texture("texture", ResourceLocation.fromNamespaceAndPath("tensura", "block/" + BuiltInRegistries.BLOCK.getKey((Block)baseBlock.get()).getPath()));
   }

   public void webbedBlockItem(RegistrySupplier<Block> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/variated/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath() + "_0")
      );
   }

   public void webbedStairsItem(RegistrySupplier<StairBlock> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/variated/stairs/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath() + "_0")
      );
   }

   public void webbedSlabItem(RegistrySupplier<SlabBlock> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/variated/slab/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath() + "_0")
      );
   }

   public void webbedWallItem(RegistrySupplier<WallBlock> block) {
      this.withExistingParent(
         "tensura:" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath(),
         this.modLoc("block/variated/wall/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath() + "_inventory")
      );
   }
}
