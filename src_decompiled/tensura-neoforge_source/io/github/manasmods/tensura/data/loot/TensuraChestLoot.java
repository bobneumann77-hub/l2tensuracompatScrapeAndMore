package io.github.manasmods.tensura.data.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class TensuraChestLoot {
   public static ResourceKey<LootTable> DWARF_GUARD = register("dwarf_guard");
   public static ResourceKey<LootTable> DWARF_MARKET = register("dwarf_market");
   public static ResourceKey<LootTable> DWARF_HOME = register("dwarf_home");
   public static ResourceKey<LootTable> DWARF_LUMBERJACK = register("dwarf_lumberjack");
   public static ResourceKey<LootTable> DWARF_FISHERMAN = register("dwarf_fisherman");
   public static ResourceKey<LootTable> DWARF_FARM = register("dwarf_farm");
   public static ResourceKey<LootTable> DWARF_LIBRARY = register("dwarf_library");
   public static ResourceKey<LootTable> DWARF_SMITHY = register("dwarf_smithy");
   public static ResourceKey<LootTable> DWARF_MAGIC_TRAINER = register("dwarf_magic_trainer");
   public static ResourceKey<LootTable> DWARF_DOJO = register("dwarf_dojo");
   public static ResourceKey<LootTable> DWARF_MERCHANT = register("dwarf_merchant");
   public static ResourceKey<LootTable> DWARF_ROYAL_TOWER = register("dwarf_royal_tower");
   public static ResourceKey<LootTable> GOBLIN_TOWER = register("goblin_tower");
   public static ResourceKey<LootTable> ACACIA_GOBLIN_VILLAGE = register("acacia_goblin_village");
   public static ResourceKey<LootTable> BIRCH_GOBLIN_VILLAGE = register("birch_goblin_village");
   public static ResourceKey<LootTable> JUNGLE_GOBLIN_VILLAGE = register("jungle_goblin_village");
   public static ResourceKey<LootTable> OAK_GOBLIN_VILLAGE = register("oak_goblin_village");
   public static ResourceKey<LootTable> PALM_GOBLIN_VILLAGE = register("palm_goblin_village");
   public static ResourceKey<LootTable> SPRUCE_GOBLIN_VILLAGE = register("spruce_goblin_village");
   public static ResourceKey<LootTable> LIZARDMAN_THRONE = register("lizardman_throne");
   public static ResourceKey<LootTable> LIZARDMAN_BEDROOM = register("lizardman_bedroom");
   public static ResourceKey<LootTable> LIZARDMAN_JAIL = register("lizardman_jail");
   public static ResourceKey<LootTable> LIZARDMAN_MESS_HALL = register("lizardman_mess_hall");
   public static ResourceKey<LootTable> LIZARDMAN_SMITHY = register("lizardman_smithy");
   public static ResourceKey<LootTable> LIZARDMAN_STORAGE = register("lizardman_storage");
   public static ResourceKey<LootTable> LIZARDMAN_TOWER = register("lizardman_tower");
   public static ResourceKey<LootTable> ORC_TENT = register("orc_tent");
   public static ResourceKey<LootTable> ORC_STORAGE = register("orc_storage");
   public static ResourceKey<LootTable> BURIED_WIZARD_TOWER = register("buried_wizard_tower");
   public static ResourceKey<LootTable> BURNT_WIZARD_TOWER = register("burnt_wizard_tower");
   public static ResourceKey<LootTable> FROZEN_WIZARD_TOWER = register("frozen_wizard_tower");
   public static ResourceKey<LootTable> ROTTED_WIZARD_TOWER = register("rotted_wizard_tower");
   public static ResourceKey<LootTable> RUINED_WIZARD_TOWER = register("ruined_wizard_tower");
   public static ResourceKey<LootTable> HELL_RUINS = register("hell_ruins");
   public static ResourceKey<LootTable> SPIDER_NEST = register("spider_nest");

   private static ResourceKey<LootTable> register(String name) {
      return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("tensura", String.format("chests/%s", name)));
   }
}
