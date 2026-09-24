package io.github.manasmods.tensura.neoforge.data.tag;

import io.github.manasmods.tensura.block.template.SimpleLog;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TensuraItemTagProvider extends ItemTagsProvider {
   public TensuraItemTagProvider(PackOutput output, CompletableFuture<Provider> future, CompletableFuture<TagLookup<Block>> provider, ExistingFileHelper helper) {
      super(output, future, provider, "tensura", helper);
   }

   protected void addTags(Provider arg) {
      this.addBlockTags();
      this.addMiscTags();
      this.addGearTags();
      this.addConsumableTags();
      this.addMobRelatedTags();
      this.addNeoForgeTags();
   }

   protected void addBlockTags() {
      this.copy(BlockTags.LOGS, ItemTags.LOGS);
      this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
      this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
      this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
      this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
      this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
      this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
      this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_TRAPDOORS);
      this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
      this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
      this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
      this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
      this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
      this.copy(BlockTags.SLABS, ItemTags.SLABS);
      this.copy(BlockTags.WALLS, ItemTags.WALLS);
      this.copy(BlockTags.SAND, ItemTags.SAND);
      this.copy(TensuraBlockTags.SILVER_ORES, TensuraItemTags.SILVER_ORES);
      this.copy(TensuraBlockTags.MAGIC_ORES, TensuraItemTags.MAGIC_ORES);
      this.tag(TensuraItemTags.PALM_LOGS)
         .add(
            new Item[]{
               ((SimpleLog)TensuraBlocks.PALM_LOG.get()).asItem(),
               ((SimpleLog)TensuraBlocks.PALM_WOOD.get()).asItem(),
               ((SimpleLog)TensuraBlocks.STRIPPED_PALM_WOOD.get()).asItem(),
               ((SimpleLog)TensuraBlocks.STRIPPED_PALM_LOG.get()).asItem()
            }
         );
      this.copy(TensuraBlockTags.WARP_PADS, TensuraItemTags.WARP_PADS);
      this.tag(TensuraItemTags.MOB_SEED_PLANTABLE).addTag(ItemTags.VILLAGER_PLANTABLE_SEEDS).add((Item)TensuraMaterialItems.HIPOKUTE_SEEDS.get());
      this.tag(TensuraItemTags.MONSTER_LEATHERS)
         .add(
            new Item[]{
               (Item)TensuraMobDropItems.MONSTER_LEATHER_D.get(),
               (Item)TensuraMobDropItems.MONSTER_LEATHER_C.get(),
               (Item)TensuraMobDropItems.MONSTER_LEATHER_B.get(),
               (Item)TensuraMobDropItems.MONSTER_LEATHER_A.get(),
               (Item)TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A.get()
            }
         );
   }

   protected void addMiscTags() {
      this.tag(TensuraItemTags.RESET_SCROLLS)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.CHARACTER_RESET_SCROLL.get(),
               (Item)TensuraMaterialItems.SKILL_RESET_SCROLL.get(),
               (Item)TensuraMaterialItems.RACE_RESET_SCROLL.get()
            }
         );
      this.tag(ItemTags.DYEABLE).add((Item)TensuraArmorItems.MITHRIL_HELMET.get());
      this.tag(ItemTags.SMALL_FLOWERS).add(new Item[]{(Item)TensuraMaterialItems.HIPOKUTE_FLOWER.get(), (Item)TensuraMaterialItems.BAFFLEDIL.get()});
      this.tag(ItemTags.BOOKSHELF_BOOKS)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.MAGIC_TOME.get(), (Item)TensuraMaterialItems.UNBOUND_TOME.get(), (Item)TensuraMaterialItems.BATTLEWILL_MANUAL.get()
            }
         );
      this.tag(TensuraItemTags.STONE_FOR_TRADES).add(new Item[]{Items.STONE, Items.ANDESITE, Items.DIORITE, Items.GRANITE, Items.DEEPSLATE, Items.TUFF});
      this.tag(TensuraItemTags.GLAZED_TERRACOTTA)
         .add(
            new Item[]{
               Items.WHITE_GLAZED_TERRACOTTA,
               Items.ORANGE_GLAZED_TERRACOTTA,
               Items.MAGENTA_GLAZED_TERRACOTTA,
               Items.LIGHT_BLUE_GLAZED_TERRACOTTA,
               Items.YELLOW_GLAZED_TERRACOTTA,
               Items.LIME_GLAZED_TERRACOTTA,
               Items.PINK_GLAZED_TERRACOTTA,
               Items.GRAY_GLAZED_TERRACOTTA,
               Items.LIGHT_GRAY_GLAZED_TERRACOTTA,
               Items.CYAN_GLAZED_TERRACOTTA,
               Items.PURPLE_GLAZED_TERRACOTTA,
               Items.BLUE_GLAZED_TERRACOTTA,
               Items.BROWN_GLAZED_TERRACOTTA,
               Items.GREEN_GLAZED_TERRACOTTA,
               Items.RED_GLAZED_TERRACOTTA,
               Items.BLACK_GLAZED_TERRACOTTA
            }
         )
         .addOptional(net.neoforged.neoforge.common.Tags.Items.GLAZED_TERRACOTTAS.location());
      this.tag(TensuraItemTags.CONCRETE_POWDER)
         .add(
            new Item[]{
               Items.WHITE_CONCRETE_POWDER,
               Items.ORANGE_CONCRETE_POWDER,
               Items.MAGENTA_CONCRETE_POWDER,
               Items.LIGHT_BLUE_CONCRETE_POWDER,
               Items.YELLOW_CONCRETE_POWDER,
               Items.LIME_CONCRETE_POWDER,
               Items.PINK_CONCRETE_POWDER,
               Items.GRAY_CONCRETE_POWDER,
               Items.LIGHT_GRAY_CONCRETE_POWDER,
               Items.CYAN_CONCRETE_POWDER,
               Items.PURPLE_CONCRETE_POWDER,
               Items.BLUE_CONCRETE_POWDER,
               Items.BROWN_CONCRETE_POWDER,
               Items.GREEN_CONCRETE_POWDER,
               Items.RED_CONCRETE_POWDER,
               Items.BLACK_CONCRETE_POWDER
            }
         )
         .addOptional(net.neoforged.neoforge.common.Tags.Items.CONCRETE_POWDERS.location());
      this.tag(TensuraItemTags.CONCRETE)
         .add(
            new Item[]{
               Items.WHITE_CONCRETE,
               Items.ORANGE_CONCRETE,
               Items.MAGENTA_CONCRETE,
               Items.LIGHT_BLUE_CONCRETE,
               Items.YELLOW_CONCRETE,
               Items.LIME_CONCRETE,
               Items.PINK_CONCRETE,
               Items.GRAY_CONCRETE,
               Items.LIGHT_GRAY_CONCRETE,
               Items.CYAN_CONCRETE,
               Items.PURPLE_CONCRETE,
               Items.BLUE_CONCRETE,
               Items.BROWN_CONCRETE,
               Items.GREEN_CONCRETE,
               Items.RED_CONCRETE,
               Items.BLACK_CONCRETE
            }
         )
         .addOptional(net.neoforged.neoforge.common.Tags.Items.CONCRETES.location());
      this.tag(TensuraItemTags.DYE)
         .add(
            new Item[]{
               Items.WHITE_DYE,
               Items.ORANGE_DYE,
               Items.MAGENTA_DYE,
               Items.LIGHT_BLUE_DYE,
               Items.YELLOW_DYE,
               Items.LIME_DYE,
               Items.PINK_DYE,
               Items.GRAY_DYE,
               Items.LIGHT_GRAY_DYE,
               Items.CYAN_DYE,
               Items.PURPLE_DYE,
               Items.BLUE_DYE,
               Items.BROWN_DYE,
               Items.GREEN_DYE,
               Items.RED_DYE,
               Items.BLACK_DYE
            }
         )
         .addOptional(net.neoforged.neoforge.common.Tags.Items.DYES.location());
      this.tag(TensuraItemTags.NO_CURSE).addTag(TensuraItemTags.HOLY_ARMAMENTS_ITEMS);
      this.tag(TensuraItemTags.NO_DECRAFT)
         .addTag(ItemTags.WOODEN_PRESSURE_PLATES)
         .addTag(ItemTags.BEDS)
         .addTag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
         .addTag(TensuraItemTags.DYE)
         .addTag(TensuraItemTags.BONE_GOLEMS)
         .add(
            new Item[]{
               Items.HONEY_BLOCK,
               Items.STICK,
               Items.CAKE,
               Items.SUGAR,
               Items.LEATHER,
               Items.ENCHANTED_GOLDEN_APPLE,
               Items.ENCHANTED_BOOK,
               (Item)TensuraConsumableItems.ENCHANTED_SILVER_APPLE.get(),
               (Item)TensuraMaterialItems.WARP_CORE.get(),
               (Item)TensuraMaterialItems.DAEMON_CORE.get()
            }
         );
      this.tag(TensuraItemTags.NPC_KEEP)
         .addTag(ItemTags.SAPLINGS)
         .add(new Item[]{Items.POTATO, Items.CARROT, Items.BEETROOT, (Item)TensuraMaterialItems.HIPOKUTE_SEEDS.get()});
      this.tag(TensuraItemTags.NPC_STORABLE)
         .addTag(TensuraItemTags.BUTCHER_STORABLE)
         .addTag(TensuraItemTags.FARMER_STORABLE)
         .addTag(TensuraItemTags.FISHERMAN_STORABLE)
         .addTag(TensuraItemTags.GUARD_STORABLE)
         .addTag(TensuraItemTags.LUMBERJACK_STORABLE)
         .addTag(TensuraItemTags.SHEPHERD_STORABLE);
      this.tag(TensuraItemTags.BUTCHER_STORABLE).add(new Item[]{Items.PORKCHOP, Items.BEEF, Items.MUTTON, Items.CHICKEN, Items.RABBIT});
      this.tag(TensuraItemTags.FARMER_STORABLE)
         .add(
            new Item[]{
               Items.BREAD,
               Items.POTATO,
               Items.CARROT,
               Items.BEETROOT,
               Items.PUMPKIN,
               Items.POISONOUS_POTATO,
               Items.MELON,
               Items.MELON_SLICE,
               Items.APPLE,
               (Item)TensuraMaterialItems.HIPOKUTE_SEEDS.get(),
               (Item)TensuraMaterialItems.HIPOKUTE_GRASS.get(),
               (Item)TensuraMaterialItems.HIPOKUTE_FLOWER.get()
            }
         );
      this.tag(TensuraItemTags.FISHERMAN_STORABLE)
         .add(
            new Item[]{
               Items.COD,
               Items.SALMON,
               Items.PUFFERFISH,
               Items.TROPICAL_FISH,
               Items.BOW,
               Items.ENCHANTED_BOOK,
               Items.FISHING_ROD,
               Items.NAME_TAG,
               Items.NAUTILUS_SHELL,
               Items.SADDLE,
               Items.LILY_PAD,
               Items.BAMBOO,
               Items.BONE,
               Items.BOWL,
               Items.LEATHER,
               Items.LEATHER_BOOTS,
               Items.ROTTEN_FLESH,
               Items.POTION,
               Items.TRIPWIRE_HOOK,
               Items.STICK,
               Items.STRING,
               Items.INK_SAC
            }
         );
      this.tag(TensuraItemTags.GUARD_STORABLE)
         .addTag(TensuraItemTags.MONSTER_LEATHERS)
         .addTag(TensuraItemTags.MONSTER_CONSUMABLES)
         .addTag(TensuraItemTags.DUBIOUS_CRYSTAL_INGREDIENT)
         .addTag(ItemTags.MEAT)
         .addTag(ItemTags.FISHES);
      this.tag(TensuraItemTags.LUMBERJACK_STORABLE)
         .addTag(ItemTags.LOGS)
         .addTag(ItemTags.LEAVES)
         .addTag(ItemTags.SAPLINGS)
         .add(new Item[]{Items.STICK, Items.APPLE});
      this.tag(TensuraItemTags.SHEPHERD_STORABLE).addTag(ItemTags.WOOL).add(Items.MUTTON);
   }

   protected void addConsumableTags() {
      this.tag(ItemTags.MEAT)
         .add(
            new Item[]{
               (Item)TensuraConsumableItems.RAW_ARMORSAURUS_MEAT.get(),
               (Item)TensuraConsumableItems.RAW_BLADE_TIGER_MEAT.get(),
               (Item)TensuraConsumableItems.CATTLEDEER_BEEF.get(),
               (Item)TensuraConsumableItems.RAW_GIANT_BAT_MEAT.get(),
               (Item)TensuraConsumableItems.RAW_SERPENT_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_ARMORSAURUS_MEAT.get(),
               (Item)TensuraConsumableItems.BLADE_TIGER_STEAK.get(),
               (Item)TensuraConsumableItems.CATTLEDEER_STEAK.get(),
               (Item)TensuraConsumableItems.COOKED_GIANT_BAT_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SERPENT_MEAT.get()
            }
         );
      this.tag(ItemTags.FISHES)
         .add(
            new Item[]{
               (Item)TensuraConsumableItems.RAW_CHARYBDIS_MEAT.get(),
               (Item)TensuraConsumableItems.RAW_MEGALODON_MEAT.get(),
               (Item)TensuraConsumableItems.RAW_SPEAR_TORO_MEAT.get(),
               (Item)TensuraConsumableItems.SPEAR_TORO_FIN.get(),
               (Item)TensuraConsumableItems.RAW_SISSIE_MEAT.get(),
               (Item)TensuraConsumableItems.SISSIE_FIN.get(),
               (Item)TensuraConsumableItems.COOKED_CHARYBDIS_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_MEGALODON_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SPEAR_TORO_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SPEAR_TORO_FIN.get(),
               (Item)TensuraConsumableItems.COOKED_SISSIE_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SISSIE_FIN.get()
            }
         );
      this.tag(TensuraItemTags.EVOLUTION_ESSENCES)
         .add(
            new Item[]{
               (Item)TensuraMobDropItems.DAEMON_ESSENCE.get(),
               (Item)TensuraMobDropItems.DRAGON_ESSENCE.get(),
               (Item)TensuraMobDropItems.ELEMENTAL_ESSENCE.get(),
               (Item)TensuraMobDropItems.ROYAL_BLOOD.get(),
               (Item)TensuraMobDropItems.ZANE_BLOOD.get()
            }
         );
      this.tag(TensuraItemTags.MONSTER_CONSUMABLES).addTag(TensuraItemTags.RAW_MONSTER_CONSUMABLES).addTag(TensuraItemTags.COOKED_MONSTER_CONSUMABLES);
      this.tag(TensuraItemTags.RAW_MONSTER_CONSUMABLES)
         .add(
            new Item[]{
               (Item)TensuraConsumableItems.RAW_ARMORSAURUS_MEAT.get(),
               (Item)TensuraConsumableItems.RAW_BLADE_TIGER_MEAT.get(),
               (Item)TensuraConsumableItems.CATTLEDEER_BEEF.get(),
               (Item)TensuraConsumableItems.RAW_CHARYBDIS_MEAT.get(),
               (Item)TensuraConsumableItems.GIANT_ANT_LEG.get(),
               (Item)TensuraConsumableItems.RAW_GIANT_BAT_MEAT.get(),
               (Item)TensuraConsumableItems.KNIGHT_SPIDER_LEG.get(),
               (Item)TensuraConsumableItems.RAW_MEGALODON_MEAT.get(),
               (Item)TensuraConsumableItems.RAW_SERPENT_MEAT.get(),
               (Item)TensuraConsumableItems.RAW_SPEAR_TORO_MEAT.get(),
               (Item)TensuraConsumableItems.SPEAR_TORO_FIN.get(),
               (Item)TensuraConsumableItems.RAW_SISSIE_MEAT.get(),
               (Item)TensuraConsumableItems.SISSIE_FIN.get(),
               (Item)TensuraConsumableItems.CHILLED_SLIME.get()
            }
         );
      this.tag(TensuraItemTags.COOKED_MONSTER_CONSUMABLES)
         .add(
            new Item[]{
               (Item)TensuraConsumableItems.COOKED_ARMORSAURUS_MEAT.get(),
               (Item)TensuraConsumableItems.BLADE_TIGER_STEAK.get(),
               (Item)TensuraConsumableItems.CATTLEDEER_STEAK.get(),
               (Item)TensuraConsumableItems.COOKED_CHARYBDIS_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_GIANT_ANT_LEG.get(),
               (Item)TensuraConsumableItems.COOKED_GIANT_BAT_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_KNIGHT_SPIDER_LEG.get(),
               (Item)TensuraConsumableItems.COOKED_MEGALODON_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SERPENT_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SPEAR_TORO_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SPEAR_TORO_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SISSIE_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SISSIE_FIN.get()
            }
         );
      this.tag(TensuraItemTags.DUBIOUS_POISON_INGREDIENT)
         .add(new Item[]{Items.PUFFERFISH, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.POISONOUS_POTATO, Items.FERMENTED_SPIDER_EYE})
         .addOptional(net.neoforged.neoforge.common.Tags.Items.FOODS_FOOD_POISONING.location());
      this.tag(TensuraItemTags.DUBIOUS_MAGIC_INGREDIENT)
         .add(
            new Item[]{
               Items.GOLDEN_APPLE,
               Items.GOLDEN_CARROT,
               Items.ENCHANTED_GOLDEN_APPLE,
               (Item)TensuraConsumableItems.SILVER_APPLE.get(),
               (Item)TensuraConsumableItems.ENCHANTED_SILVER_APPLE.get(),
               (Item)TensuraMobDropItems.ELEMENTAL_ESSENCE.get(),
               (Item)TensuraMobDropItems.DAEMON_ESSENCE.get(),
               (Item)TensuraMaterialItems.DAEMON_CORE.get(),
               (Item)TensuraMobDropItems.DRAGON_ESSENCE.get(),
               (Item)TensuraMobDropItems.ZANE_BLOOD.get(),
               (Item)TensuraMobDropItems.ROYAL_BLOOD.get(),
               (Item)TensuraMaterialItems.WATER_ELEMENTAL_SHARD.get(),
               (Item)TensuraMaterialItems.FIRE_ELEMENTAL_SHARD.get(),
               (Item)TensuraMaterialItems.EARTH_ELEMENTAL_SHARD.get(),
               (Item)TensuraMaterialItems.WIND_ELEMENTAL_SHARD.get(),
               (Item)TensuraMaterialItems.SPACE_ELEMENTAL_SHARD.get()
            }
         );
      this.tag(TensuraItemTags.DUBIOUS_RAW_INGREDIENT)
         .add(
            new Item[]{Items.PORKCHOP, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.BEEF, Items.CHICKEN, Items.RABBIT, Items.MUTTON, Items.RABBIT_FOOT}
         )
         .addOptional(net.neoforged.neoforge.common.Tags.Items.FOODS_RAW_MEAT.location())
         .addOptional(net.neoforged.neoforge.common.Tags.Items.FOODS_RAW_FISH.location());
      this.tag(TensuraItemTags.DUBIOUS_EFFECT_INGREDIENT)
         .add(
            new Item[]{
               (Item)TensuraConsumableItems.GIANT_ANT_LEG.get(),
               (Item)TensuraConsumableItems.RAW_GIANT_BAT_MEAT.get(),
               (Item)TensuraConsumableItems.KNIGHT_SPIDER_LEG.get(),
               (Item)TensuraConsumableItems.RAW_SERPENT_MEAT.get()
            }
         );
      this.tag(TensuraItemTags.DUBIOUS_CRYSTAL_INGREDIENT)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.MAGIC_STONE.get(),
               (Item)TensuraMaterialItems.WARP_CORE.get(),
               (Item)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get(),
               (Item)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get(),
               (Item)TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get(),
               Items.AMETHYST_SHARD,
               Items.ECHO_SHARD
            }
         )
         .addOptional(net.neoforged.neoforge.common.Tags.Items.GEMS.location());
      this.tag(TensuraItemTags.DUBIOUS_BREWING_INGREDIENT)
         .add(
            new Item[]{
               Items.BLAZE_POWDER,
               Items.GUNPOWDER,
               Items.DRAGON_BREATH,
               Items.GLOWSTONE,
               Items.REDSTONE,
               Items.NETHER_WART,
               Items.GLOW_BERRIES,
               Items.GLOW_INK_SAC,
               Items.CHORUS_FRUIT
            }
         );
      this.tag(TensuraItemTags.DUBIOUS_MUSHROOM_INGREDIENT)
         .add(new Item[]{Items.MUSHROOM_STEW, Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS})
         .addOptional(net.neoforged.neoforge.common.Tags.Items.MUSHROOMS.location());
      this.tag(ItemTags.COW_FOOD).add((Item)TensuraMaterialItems.THATCH.get());
      this.tag(ItemTags.SHEEP_FOOD).add((Item)TensuraMaterialItems.THATCH.get());
      this.tag(ItemTags.GOAT_FOOD).add((Item)TensuraMaterialItems.THATCH.get());
      this.tag(TensuraItemTags.CATTLE_FOOD)
         .addTag(ItemTags.COW_FOOD)
         .add(new Item[]{(Item)TensuraMaterialItems.HIPOKUTE_GRASS.get(), (Item)TensuraMaterialItems.HIPOKUTE_FLOWER.get()});
      this.tag(TensuraItemTags.PEACOCK_FOOD).add(new Item[]{Items.BEETROOT, Items.APPLE, Items.POTATO, Items.CARROT});
      this.tag(TensuraItemTags.PEACOCK_TAMING_FOOD).add(Items.BEETROOT_SEEDS);
      this.tag(TensuraItemTags.RABBIT_TAMING_FOOD).add(new Item[]{Items.GOLDEN_CARROT, (Item)TensuraMaterialItems.HIPOKUTE_FLOWER.get()});
      this.tag(TensuraItemTags.ANT_FOOD)
         .addTag(ItemTags.MEAT)
         .addTag(ItemTags.FISHES)
         .add(new Item[]{Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE, Items.RABBIT_FOOT, Items.BROWN_MUSHROOM, Items.RED_MUSHROOM});
      this.tag(TensuraItemTags.BEAR_FOOD)
         .addTag(ItemTags.MEAT)
         .addTag(ItemTags.FISHES)
         .add(new Item[]{(Item)TensuraConsumableItems.CATTLEDEER_BEEF.get(), (Item)TensuraConsumableItems.CATTLEDEER_STEAK.get()});
      this.tag(TensuraItemTags.CATERPILLAR_FOOD)
         .addTag(ItemTags.LEAVES)
         .add(
            new Item[]{
               Items.SHORT_GRASS,
               Items.GRASS_BLOCK,
               Items.TALL_GRASS,
               Items.FERN,
               Items.LARGE_FERN,
               (Item)TensuraMaterialItems.HIPOKUTE_GRASS.get(),
               (Item)TensuraMaterialItems.HIPOKUTE_FLOWER.get(),
               (Item)TensuraMaterialItems.BAFFLEDIL.get()
            }
         );
      this.tag(TensuraItemTags.SLIME_FOOD)
         .add(
            new Item[]{
               Items.SLIME_BALL,
               (Item)TensuraMobDropItems.SLIME_CHUNK.get(),
               (Item)TensuraConsumableItems.CHILLED_SLIME.get(),
               (Item)TensuraMaterialItems.MAGIC_ORE.get()
            }
         );
      this.tag(TensuraItemTags.SLIME_TAMING_FOOD)
         .add(
            new Item[]{
               (Item)TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get(),
               (Item)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get(),
               (Item)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()
            }
         );
      this.tag(TensuraItemTags.METAL_SLIME_TAMING_FOOD)
         .add(new Item[]{(Item)TensuraMaterialItems.MAGIC_ORE.get(), (Item)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get()});
      this.tag(TensuraItemTags.SPIRIT_FOOD)
         .add(
            new Item[]{
               (Item)TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get(),
               (Item)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get(),
               (Item)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()
            }
         );
      this.tag(TensuraItemTags.HIPOKUTE_POTION_CONTAINERS)
         .add(new Item[]{(Item)TensuraConsumableItems.WATER_MAGIC_BOTTLE.get(), (Item)TensuraConsumableItems.VACUUMED_WATER_MAGIC_BOTTLE.get()});
      this.tag(TensuraItemTags.HIPOKUTE_POTIONS)
         .add(
            new Item[]{
               (Item)TensuraConsumableItems.LOW_POTION.get(),
               (Item)TensuraConsumableItems.HIGH_POTION.get(),
               (Item)TensuraConsumableItems.FULL_POTION.get(),
               (Item)TensuraConsumableItems.REVIVAL_ELIXIR.get()
            }
         );
      this.tag(TensuraItemTags.ARCANE_POTIONS)
         .add(
            new Item[]{
               (Item)TensuraConsumableItems.LOW_ARCANE_POTION.get(),
               (Item)TensuraConsumableItems.MEDIUM_ARCANE_POTION.get(),
               (Item)TensuraConsumableItems.HIGH_ARCANE_POTION.get()
            }
         );
   }

   protected void addMobRelatedTags() {
      this.tag(TensuraItemTags.RESET_BARGHEST_FLAME).add(Items.WATER_BUCKET);
      this.tag(TensuraItemTags.BARGHEST_FLAME_ORANGE).add(new Item[]{Items.BLAZE_POWDER, Items.BLAZE_ROD, Items.FIRE_CHARGE});
      this.tag(TensuraItemTags.BARGHEST_FLAME_TEAL).add(new Item[]{Items.SOUL_SAND, Items.SOUL_SOIL});
      this.tag(TensuraItemTags.BARGHEST_FLAME_YELLOW).add(new Item[]{Items.IRON_INGOT, Items.RAW_IRON});
      this.tag(TensuraItemTags.BARGHEST_FLAME_RED).add(Items.CALCITE);
      this.tag(TensuraItemTags.BARGHEST_FLAME_GREEN).add(new Item[]{Items.COPPER_INGOT, Items.RAW_COPPER});
      this.tag(TensuraItemTags.BARGHEST_FLAME_PURPLE).add(new Item[]{Items.POTATO, Items.CARROT, Items.POISONOUS_POTATO});
      this.tag(TensuraItemTags.BARGHEST_FLAME_WHITE).add(Items.NETHER_STAR);
      this.tag(TensuraItemTags.MOTH_TEMPT_ITEMS)
         .add(
            new Item[]{
               Items.LANTERN,
               Items.SOUL_LANTERN,
               Items.GLOWSTONE,
               Items.SEA_LANTERN,
               Items.OCHRE_FROGLIGHT,
               Items.VERDANT_FROGLIGHT,
               Items.PEARLESCENT_FROGLIGHT,
               Items.SHROOMLIGHT
            }
         );
      this.tag(ItemTags.PIGLIN_LOVED)
         .add(
            new Item[]{
               (Item)TensuraArmorItems.ORICHALCUM_HELMET.get(),
               (Item)TensuraArmorItems.ORICHALCUM_CHESTPLATE.get(),
               (Item)TensuraArmorItems.ORICHALCUM_LEGGINGS.get(),
               (Item)TensuraArmorItems.ORICHALCUM_BOOTS.get()
            }
         )
         .add(
            new Item[]{
               (Item)TensuraToolItems.ORICHALCUM_PICKAXE.get(),
               (Item)TensuraToolItems.ORICHALCUM_AXE.get(),
               (Item)TensuraToolItems.ORICHALCUM_SHOVEL.get(),
               (Item)TensuraToolItems.ORICHALCUM_HOE.get(),
               (Item)TensuraToolItems.ORICHALCUM_SICKLE.get(),
               (Item)TensuraToolItems.ORICHALCUM_SWORD.get(),
               (Item)TensuraToolItems.ORICHALCUM_SHORT_SWORD.get(),
               (Item)TensuraToolItems.ORICHALCUM_LONG_SWORD.get(),
               (Item)TensuraToolItems.ORICHALCUM_GREAT_SWORD.get(),
               (Item)TensuraToolItems.ORICHALCUM_KATANA.get(),
               (Item)TensuraToolItems.ORICHALCUM_KODACHI.get(),
               (Item)TensuraToolItems.ORICHALCUM_TACHI.get(),
               (Item)TensuraToolItems.ORICHALCUM_ODACHI.get(),
               (Item)TensuraToolItems.ORICHALCUM_SPEAR.get(),
               (Item)TensuraToolItems.GOLDEN_SHORT_SWORD.get(),
               (Item)TensuraToolItems.GOLDEN_LONG_SWORD.get(),
               (Item)TensuraToolItems.GOLDEN_GREAT_SWORD.get(),
               (Item)TensuraToolItems.GOLDEN_KODACHI.get(),
               (Item)TensuraToolItems.GOLDEN_TACHI.get(),
               (Item)TensuraToolItems.GOLDEN_ODACHI.get(),
               (Item)TensuraToolItems.GOLDEN_KATANA.get(),
               (Item)TensuraToolItems.GOLDEN_SPEAR.get(),
               (Item)TensuraToolItems.GOLDEN_SCYTHE.get(),
               (Item)TensuraToolItems.GOLDEN_SICKLE.get(),
               (Item)TensuraMaterialItems.ORICHALCUM_NUGGET.get(),
               (Item)TensuraMaterialItems.ORICHALCUM_INGOT.get(),
               (Item)TensuraBlocks.Items.ORICHALCUM_BLOCK.get()
            }
         );
   }

   protected void addGearTags() {
      this.tag(TensuraItemTags.EMPTY_HAND_POSE).add(new Item[]{(Item)TensuraToolItems.ARMORSAURUS_GAUNTLET.get(), (Item)TensuraToolItems.DRAGON_KNUCKLE.get()});
      this.tag(TensuraItemTags.NO_SLOWNESS_ON_USE).add(new Item[]{(Item)TensuraToolItems.SHORT_BOW.get(), (Item)TensuraToolItems.SHORT_SPIDER_BOW.get()});
      this.tag(TensuraItemTags.STOP_ON_USE).add(new Item[]{(Item)TensuraToolItems.WAR_BOW.get(), (Item)TensuraToolItems.WAR_SPIDER_BOW.get()});
      this.tag(TensuraItemTags.HANDHELD_ENCHANTABLE)
         .addTag(TensuraItemTags.WEAPON_AND_TOOL_ENCHANTABLE)
         .addTag(TensuraItemTags.RANGED_ENCHANTABLE)
         .addTag(ItemTags.FISHING_ENCHANTABLE);
      this.tag(TensuraItemTags.WEAPON_AND_TOOL_ENCHANTABLE)
         .addTag(ItemTags.WEAPON_ENCHANTABLE)
         .addTag(ItemTags.SWORD_ENCHANTABLE)
         .addTag(ItemTags.TRIDENT_ENCHANTABLE)
         .addTag(ItemTags.MINING_ENCHANTABLE)
         .addTag(ItemTags.MACE_ENCHANTABLE);
      this.tag(TensuraItemTags.RANGED_ENCHANTABLE)
         .addTag(TensuraItemTags.SPELL_CAST_WEAPONS)
         .addTag(ItemTags.BOW_ENCHANTABLE)
         .addTag(ItemTags.CROSSBOW_ENCHANTABLE)
         .addTag(TensuraItemTags.SPEARS)
         .addTag(ItemTags.TRIDENT_ENCHANTABLE)
         .add(
            new Item[]{
               (Item)TensuraToolItems.KUNAI.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_KUNAI.get(),
               (Item)TensuraToolItems.WEB_GUN.get(),
               (Item)TensuraToolItems.WALTHER_P99.get()
            }
         );
      this.tag(TensuraItemTags.CAN_LOOK_AT_ENDERMAN)
         .add(
            new Item[]{
               (Item)TensuraArmorItems.ANTI_MAGIC_MASK.get(),
               (Item)TensuraArmorItems.ANGRY_PIERROT_MASK.get(),
               (Item)TensuraArmorItems.CRAZY_PIERROT_MASK.get(),
               (Item)TensuraArmorItems.TEARY_PIERROT_MASK.get(),
               (Item)TensuraArmorItems.WONDER_PIERROT_MASK.get()
            }
         );
      this.tag(TensuraItemTags.CAN_WALK_ON_POWDER_SNOW)
         .add(
            new Item[]{
               (Item)TensuraArmorItems.MONSTER_LEATHER_D_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_C_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_B_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_A_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_BOOTS.get(),
               (Item)TensuraArmorItems.WINGED_SHOES.get(),
               (Item)TensuraArmorItems.DARK_BOOTS.get()
            }
         );
      this.tag(TensuraItemTags.CAN_TOUCH_DRAGON_EGG).add((Item)TensuraToolItems.DRAGON_KNUCKLE.get());
      this.tag(TensuraItemTags.INDESTRUCTIBLE_BY_ENVIRONMENTAL_CAUSE)
         .addTag(TensuraItemTags.HIHIIROKANE_ITEMS)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.SHADOW_STORAGE.get(),
               (Item)TensuraMaterialItems.MARIONETTE_HEART.get(),
               (Item)TensuraMobDropItems.DAEMON_ESSENCE.get(),
               (Item)TensuraMobDropItems.DRAGON_ESSENCE.get(),
               (Item)TensuraMobDropItems.ELEMENTAL_ESSENCE.get(),
               (Item)TensuraMobDropItems.ZANE_BLOOD.get(),
               (Item)TensuraMaterialItems.POUCH_SPECIAL_A.get(),
               (Item)TensuraToolItems.MOONLIGHT.get(),
               (Item)TensuraToolItems.RUHK.get(),
               (Item)TensuraToolItems.ORB_OF_DOMINATION.get()
            }
         );
      this.tag(TensuraItemTags.DUMMY_REMOVE_ON_LEAVING_HAND)
         .addTag(TensuraItemTags.BODY_ARMOR_ITEMS)
         .add(new Item[]{(Item)TensuraMaterialItems.SPATIAL_BAG.get(), (Item)TensuraToolItems.SPATIAL_BLADE.get(), (Item)TensuraToolItems.WALTHER_P99.get()});
      this.tag(TensuraItemTags.STRONG_THREAD).add((Item)TensuraMobDropItems.STEEL_THREAD.get());
      this.tag(TensuraItemTags.BONE_GOLEMS)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.LOW_MAGISTEEL_BONE_GOLEM.get(),
               (Item)TensuraMaterialItems.HIGH_MAGISTEEL_BONE_GOLEM.get(),
               (Item)TensuraMaterialItems.PURE_MAGISTEEL_BONE_GOLEM.get(),
               (Item)TensuraMaterialItems.MITHRIL_BONE_GOLEM.get(),
               (Item)TensuraMaterialItems.ORICHALCUM_BONE_GOLEM.get(),
               (Item)TensuraMaterialItems.ADAMANTITE_BONE_GOLEM.get(),
               (Item)TensuraMaterialItems.HIHIIROKANE_BONE_GOLEM.get()
            }
         );
      this.tag(TensuraItemTags.BODY_ARMOR_ITEMS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.ARMORSAURUS_GAUNTLET.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_HELMET.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_CHESTPLATE.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_LEGGINGS.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_BOOTS.get()
            }
         );
      this.tag(TensuraItemTags.HOLY_ARMAMENTS_ITEMS)
         .add(
            new Item[]{
               (Item)TensuraArmorItems.HOLY_ARMAMENTS_CHESTPLATE.get(),
               (Item)TensuraArmorItems.HOLY_ARMAMENTS_LEGGINGS.get(),
               (Item)TensuraArmorItems.HOLY_ARMAMENTS_BOOTS.get()
            }
         );
      this.tag(TensuraItemTags.PIERROT_MASKS)
         .add(
            new Item[]{
               (Item)TensuraArmorItems.ANGRY_PIERROT_MASK.get(),
               (Item)TensuraArmorItems.CRAZY_PIERROT_MASK.get(),
               (Item)TensuraArmorItems.TEARY_PIERROT_MASK.get(),
               (Item)TensuraArmorItems.WONDER_PIERROT_MASK.get()
            }
         );
      this.tag(TensuraItemTags.PEACOCK_SITTING_HELMETS)
         .add(
            new Item[]{
               (Item)TensuraArmorItems.MONSTER_LEATHER_D_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_C_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_B_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_A_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_HELMET.get()
            }
         );
      this.tag(TensuraItemTags.ADAMANTITE_ITEMS)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.ADAMANTITE_NUGGET.get(),
               (Item)TensuraMaterialItems.ADAMANTITE_INGOT.get(),
               (Item)TensuraToolItems.ADAMANTITE_SWORD.get(),
               (Item)TensuraToolItems.ADAMANTITE_SHORT_SWORD.get(),
               (Item)TensuraToolItems.ADAMANTITE_LONG_SWORD.get(),
               (Item)TensuraToolItems.ADAMANTITE_GREAT_SWORD.get(),
               (Item)TensuraToolItems.ADAMANTITE_KATANA.get(),
               (Item)TensuraToolItems.ADAMANTITE_KODACHI.get(),
               (Item)TensuraToolItems.ADAMANTITE_TACHI.get(),
               (Item)TensuraToolItems.ADAMANTITE_ODACHI.get(),
               (Item)TensuraToolItems.ADAMANTITE_SCYTHE.get(),
               (Item)TensuraToolItems.ADAMANTITE_SPEAR.get(),
               (Item)TensuraToolItems.ADAMANTITE_AXE.get(),
               (Item)TensuraToolItems.ADAMANTITE_PICKAXE.get(),
               (Item)TensuraToolItems.ADAMANTITE_SHOVEL.get(),
               (Item)TensuraToolItems.ADAMANTITE_HOE.get(),
               (Item)TensuraToolItems.ADAMANTITE_SICKLE.get()
            }
         );
      this.tag(TensuraItemTags.HIHIIROKANE_ITEMS)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.HIHIIROKANE_NUGGET.get(),
               (Item)TensuraMaterialItems.HIHIIROKANE_INGOT.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SWORD.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SHORT_SWORD.get(),
               (Item)TensuraToolItems.HIHIIROKANE_LONG_SWORD.get(),
               (Item)TensuraToolItems.HIHIIROKANE_GREAT_SWORD.get(),
               (Item)TensuraToolItems.HIHIIROKANE_KATANA.get(),
               (Item)TensuraToolItems.HIHIIROKANE_KODACHI.get(),
               (Item)TensuraToolItems.HIHIIROKANE_TACHI.get(),
               (Item)TensuraToolItems.HIHIIROKANE_ODACHI.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SCYTHE.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SPEAR.get(),
               (Item)TensuraToolItems.HIHIIROKANE_AXE.get(),
               (Item)TensuraToolItems.HIHIIROKANE_PICKAXE.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SHOVEL.get(),
               (Item)TensuraToolItems.HIHIIROKANE_HOE.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SICKLE.get()
            }
         );
      this.tag(TensuraItemTags.SLOTTING_CAST_EXCLUDED)
         .addTag(TensuraItemTags.SPEARS)
         .addTag(TensuraItemTags.SPELL_CAST_WEAPONS)
         .add(new Item[]{(Item)TensuraToolItems.KUNAI.get(), (Item)TensuraToolItems.PURE_MAGISTEEL_KUNAI.get(), Items.BUNDLE});
      this.tag(TensuraItemTags.INFINITY_ELEMENTAL_CORES).addTag(TensuraItemTags.HIHIIROKANE_ITEMS);
      this.tag(TensuraItemTags.ELEMENTAL_CORES)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.ELEMENT_CORE_EMPTY.get(),
               (Item)TensuraMaterialItems.ELEMENT_CORE_EARTH.get(),
               (Item)TensuraMaterialItems.ELEMENT_CORE_FIRE.get(),
               (Item)TensuraMaterialItems.ELEMENT_CORE_SPACE.get(),
               (Item)TensuraMaterialItems.ELEMENT_CORE_WATER.get(),
               (Item)TensuraMaterialItems.ELEMENT_CORE_WIND.get()
            }
         );
      this.tag(TensuraItemTags.COINS)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.BRONZE_COIN.get(),
               (Item)TensuraMaterialItems.SILVER_COIN.get(),
               (Item)TensuraMaterialItems.GOLD_COIN.get(),
               (Item)TensuraMaterialItems.STELLAR_GOLD_COIN.get()
            }
         );
      this.tag(ItemTags.HEAD_ARMOR)
         .add(
            new Item[]{
               (Item)TensuraArmorItems.MONSTER_LEATHER_D_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_C_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_B_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_A_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_HELMET.get(),
               (Item)TensuraArmorItems.SILVER_HELMET.get(),
               (Item)TensuraArmorItems.LOW_MAGISTEEL_HELMET.get(),
               (Item)TensuraArmorItems.HIGH_MAGISTEEL_HELMET.get(),
               (Item)TensuraArmorItems.MITHRIL_HELMET.get(),
               (Item)TensuraArmorItems.ORICHALCUM_HELMET.get(),
               (Item)TensuraArmorItems.PURE_MAGISTEEL_HELMET.get(),
               (Item)TensuraArmorItems.ADAMANTITE_HELMET.get(),
               (Item)TensuraArmorItems.HIHIIROKANE_HELMET.get(),
               (Item)TensuraArmorItems.ANT_CARAPACE_HELMET.get(),
               (Item)TensuraArmorItems.SERPENT_SCALEMAIL_HELMET.get(),
               (Item)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_HELMET.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_HELMET.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_HELMET.get(),
               (Item)TensuraArmorItems.CHARYBDIS_SCALEMAIL_HELMET.get(),
               (Item)TensuraArmorItems.ANTI_MAGIC_MASK.get(),
               (Item)TensuraArmorItems.ANGRY_PIERROT_MASK.get(),
               (Item)TensuraArmorItems.CRAZY_PIERROT_MASK.get(),
               (Item)TensuraArmorItems.TEARY_PIERROT_MASK.get(),
               (Item)TensuraArmorItems.WONDER_PIERROT_MASK.get()
            }
         );
      this.tag(ItemTags.CHEST_ARMOR)
         .add(
            new Item[]{
               (Item)TensuraArmorItems.MONSTER_LEATHER_D_CHESTPLATE.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_C_CHESTPLATE.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_B_CHESTPLATE.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_A_CHESTPLATE.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_CHESTPLATE.get(),
               (Item)TensuraArmorItems.SILVER_CHESTPLATE.get(),
               (Item)TensuraArmorItems.LOW_MAGISTEEL_CHESTPLATE.get(),
               (Item)TensuraArmorItems.HIGH_MAGISTEEL_CHESTPLATE.get(),
               (Item)TensuraArmorItems.MITHRIL_CHESTPLATE.get(),
               (Item)TensuraArmorItems.ORICHALCUM_CHESTPLATE.get(),
               (Item)TensuraArmorItems.PURE_MAGISTEEL_CHESTPLATE.get(),
               (Item)TensuraArmorItems.ADAMANTITE_CHESTPLATE.get(),
               (Item)TensuraArmorItems.HIHIIROKANE_CHESTPLATE.get(),
               (Item)TensuraArmorItems.ANT_CARAPACE_CHESTPLATE.get(),
               (Item)TensuraArmorItems.SERPENT_SCALEMAIL_CHESTPLATE.get(),
               (Item)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_CHESTPLATE.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_CHESTPLATE.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_CHESTPLATE.get(),
               (Item)TensuraArmorItems.CHARYBDIS_SCALEMAIL_CHESTPLATE.get(),
               (Item)TensuraArmorItems.HOLY_ARMAMENTS_CHESTPLATE.get(),
               (Item)TensuraArmorItems.DARK_JACKET.get()
            }
         );
      this.tag(ItemTags.LEG_ARMOR)
         .add(
            new Item[]{
               (Item)TensuraArmorItems.MONSTER_LEATHER_D_LEGGINGS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_C_LEGGINGS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_B_LEGGINGS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_A_LEGGINGS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_LEGGINGS.get(),
               (Item)TensuraArmorItems.SILVER_LEGGINGS.get(),
               (Item)TensuraArmorItems.LOW_MAGISTEEL_LEGGINGS.get(),
               (Item)TensuraArmorItems.HIGH_MAGISTEEL_LEGGINGS.get(),
               (Item)TensuraArmorItems.MITHRIL_LEGGINGS.get(),
               (Item)TensuraArmorItems.ORICHALCUM_LEGGINGS.get(),
               (Item)TensuraArmorItems.PURE_MAGISTEEL_LEGGINGS.get(),
               (Item)TensuraArmorItems.ADAMANTITE_LEGGINGS.get(),
               (Item)TensuraArmorItems.HIHIIROKANE_LEGGINGS.get(),
               (Item)TensuraArmorItems.ANT_CARAPACE_LEGGINGS.get(),
               (Item)TensuraArmorItems.SERPENT_SCALEMAIL_LEGGINGS.get(),
               (Item)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_LEGGINGS.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_LEGGINGS.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_LEGGINGS.get(),
               (Item)TensuraArmorItems.CHARYBDIS_SCALEMAIL_LEGGINGS.get(),
               (Item)TensuraArmorItems.HOLY_ARMAMENTS_LEGGINGS.get(),
               (Item)TensuraArmorItems.DARK_LEGGINGS.get()
            }
         );
      this.tag(ItemTags.FOOT_ARMOR)
         .add(
            new Item[]{
               (Item)TensuraArmorItems.MONSTER_LEATHER_D_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_C_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_B_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_A_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_BOOTS.get(),
               (Item)TensuraArmorItems.SILVER_BOOTS.get(),
               (Item)TensuraArmorItems.LOW_MAGISTEEL_BOOTS.get(),
               (Item)TensuraArmorItems.HIGH_MAGISTEEL_BOOTS.get(),
               (Item)TensuraArmorItems.MITHRIL_BOOTS.get(),
               (Item)TensuraArmorItems.ORICHALCUM_BOOTS.get(),
               (Item)TensuraArmorItems.PURE_MAGISTEEL_BOOTS.get(),
               (Item)TensuraArmorItems.ADAMANTITE_BOOTS.get(),
               (Item)TensuraArmorItems.HIHIIROKANE_BOOTS.get(),
               (Item)TensuraArmorItems.ANT_CARAPACE_BOOTS.get(),
               (Item)TensuraArmorItems.SERPENT_SCALEMAIL_BOOTS.get(),
               (Item)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_BOOTS.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_BOOTS.get(),
               (Item)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_BOOTS.get(),
               (Item)TensuraArmorItems.CHARYBDIS_SCALEMAIL_BOOTS.get(),
               (Item)TensuraArmorItems.HOLY_ARMAMENTS_BOOTS.get(),
               (Item)TensuraArmorItems.DARK_BOOTS.get(),
               (Item)TensuraArmorItems.WINGED_SHOES.get()
            }
         );
      this.tag(ItemTags.FREEZE_IMMUNE_WEARABLES)
         .add(
            new Item[]{
               (Item)TensuraArmorItems.MONSTER_LEATHER_D_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_C_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_B_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_A_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_HELMET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_D_CHESTPLATE.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_C_CHESTPLATE.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_B_CHESTPLATE.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_A_CHESTPLATE.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_CHESTPLATE.get(),
               (Item)TensuraArmorItems.DARK_JACKET.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_D_LEGGINGS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_C_LEGGINGS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_B_LEGGINGS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_A_LEGGINGS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_LEGGINGS.get(),
               (Item)TensuraArmorItems.DARK_LEGGINGS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_D_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_C_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_B_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_A_BOOTS.get(),
               (Item)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_BOOTS.get(),
               (Item)TensuraArmorItems.DARK_BOOTS.get(),
               (Item)TensuraArmorItems.WINGED_SHOES.get()
            }
         );
      this.tag(ItemTags.SWORDS)
         .addTags(
            new TagKey[]{
               TensuraItemTags.DAGGERS,
               TensuraItemTags.SHORT_SWORDS,
               TensuraItemTags.LONG_SWORDS,
               TensuraItemTags.GREAT_SWORDS,
               TensuraItemTags.KATANAS,
               TensuraItemTags.KODACHIS,
               TensuraItemTags.TACHIS,
               TensuraItemTags.ODACHIS
            }
         )
         .add(
            new Item[]{
               (Item)TensuraToolItems.SILVER_SWORD.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_SWORD.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_SWORD.get(),
               (Item)TensuraToolItems.MITHRIL_SWORD.get(),
               (Item)TensuraToolItems.ORICHALCUM_SWORD.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_SWORD.get(),
               (Item)TensuraToolItems.ADAMANTITE_SWORD.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SWORD.get(),
               (Item)TensuraToolItems.SPATIAL_BLADE.get()
            }
         );
      this.tag(ItemTags.AXES)
         .addTag(TensuraItemTags.MULTITOOLS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.SILVER_AXE.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_AXE.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_AXE.get(),
               (Item)TensuraToolItems.MITHRIL_AXE.get(),
               (Item)TensuraToolItems.ORICHALCUM_AXE.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_AXE.get(),
               (Item)TensuraToolItems.ADAMANTITE_AXE.get(),
               (Item)TensuraToolItems.HIHIIROKANE_AXE.get()
            }
         );
      this.tag(ItemTags.PICKAXES)
         .addTag(TensuraItemTags.MULTITOOLS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.SILVER_PICKAXE.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_PICKAXE.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_PICKAXE.get(),
               (Item)TensuraToolItems.MITHRIL_PICKAXE.get(),
               (Item)TensuraToolItems.ORICHALCUM_PICKAXE.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_PICKAXE.get(),
               (Item)TensuraToolItems.ADAMANTITE_PICKAXE.get(),
               (Item)TensuraToolItems.HIHIIROKANE_PICKAXE.get(),
               (Item)TensuraToolItems.SISSIE_TOOTH_PICKAXE.get()
            }
         );
      this.tag(ItemTags.SHOVELS)
         .addTag(TensuraItemTags.MULTITOOLS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.SILVER_SHOVEL.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_SHOVEL.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_SHOVEL.get(),
               (Item)TensuraToolItems.MITHRIL_SHOVEL.get(),
               (Item)TensuraToolItems.ORICHALCUM_SHOVEL.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_SHOVEL.get(),
               (Item)TensuraToolItems.ADAMANTITE_SHOVEL.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SHOVEL.get()
            }
         );
      this.tag(ItemTags.HOES)
         .addTag(TensuraItemTags.MULTITOOLS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.SILVER_HOE.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_HOE.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_HOE.get(),
               (Item)TensuraToolItems.MITHRIL_HOE.get(),
               (Item)TensuraToolItems.ORICHALCUM_HOE.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_HOE.get(),
               (Item)TensuraToolItems.ADAMANTITE_HOE.get(),
               (Item)TensuraToolItems.HIHIIROKANE_HOE.get()
            }
         );
      this.tag(TensuraItemTags.MULTITOOLS).add(new Item[]{(Item)TensuraToolItems.ARMORSAURUS_GAUNTLET.get(), (Item)TensuraToolItems.DRAGON_KNUCKLE.get()});
      this.tag(TensuraItemTags.FIST_WEAPONS).add(new Item[]{(Item)TensuraToolItems.ARMORSAURUS_GAUNTLET.get(), (Item)TensuraToolItems.DRAGON_KNUCKLE.get()});
      this.tag(TensuraItemTags.DAGGERS).add(new Item[]{(Item)TensuraToolItems.CENTIPEDE_DAGGER.get(), (Item)TensuraToolItems.SPIDER_DAGGER.get()});
      this.tag(TensuraItemTags.SPELL_CAST_WEAPONS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.LOW_MAGIC_STAFF.get(),
               (Item)TensuraToolItems.MEDIUM_MAGIC_STAFF.get(),
               (Item)TensuraToolItems.HIGH_MAGIC_STAFF.get(),
               (Item)TensuraToolItems.SLIME_STAFF.get(),
               (Item)TensuraToolItems.GRIMOIRE_D.get(),
               (Item)TensuraToolItems.GRIMOIRE_C.get(),
               (Item)TensuraToolItems.GRIMOIRE_B.get(),
               (Item)TensuraToolItems.GRIMOIRE_A.get(),
               (Item)TensuraToolItems.GRIMOIRE_SPECIAL_A.get()
            }
         );
      this.tag(TensuraItemTags.MAGIC_GRIMOIRES)
         .add(
            new Item[]{
               (Item)TensuraToolItems.GRIMOIRE_D.get(),
               (Item)TensuraToolItems.GRIMOIRE_C.get(),
               (Item)TensuraToolItems.GRIMOIRE_B.get(),
               (Item)TensuraToolItems.GRIMOIRE_A.get(),
               (Item)TensuraToolItems.GRIMOIRE_SPECIAL_A.get()
            }
         );
      this.tag(TensuraItemTags.MAGIC_STAVES)
         .add(
            new Item[]{
               (Item)TensuraToolItems.LOW_MAGIC_STAFF.get(),
               (Item)TensuraToolItems.MEDIUM_MAGIC_STAFF.get(),
               (Item)TensuraToolItems.HIGH_MAGIC_STAFF.get(),
               (Item)TensuraToolItems.SLIME_STAFF.get()
            }
         );
      this.tag(TensuraItemTags.SHORT_SWORDS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.WOODEN_SHORT_SWORD.get(),
               (Item)TensuraToolItems.STONE_SHORT_SWORD.get(),
               (Item)TensuraToolItems.SILVER_SHORT_SWORD.get(),
               (Item)TensuraToolItems.IRON_SHORT_SWORD.get(),
               (Item)TensuraToolItems.GOLDEN_SHORT_SWORD.get(),
               (Item)TensuraToolItems.DIAMOND_SHORT_SWORD.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_SHORT_SWORD.get(),
               (Item)TensuraToolItems.NETHERITE_SHORT_SWORD.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_SHORT_SWORD.get(),
               (Item)TensuraToolItems.MITHRIL_SHORT_SWORD.get(),
               (Item)TensuraToolItems.ORICHALCUM_SHORT_SWORD.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_SHORT_SWORD.get(),
               (Item)TensuraToolItems.ADAMANTITE_SHORT_SWORD.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SHORT_SWORD.get(),
               (Item)TensuraToolItems.TEMPEST_SCALE_KNIFE.get()
            }
         );
      this.tag(TensuraItemTags.LONG_SWORDS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.WOODEN_LONG_SWORD.get(),
               (Item)TensuraToolItems.STONE_LONG_SWORD.get(),
               (Item)TensuraToolItems.SILVER_LONG_SWORD.get(),
               (Item)TensuraToolItems.IRON_LONG_SWORD.get(),
               (Item)TensuraToolItems.GOLDEN_LONG_SWORD.get(),
               (Item)TensuraToolItems.DIAMOND_LONG_SWORD.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_LONG_SWORD.get(),
               (Item)TensuraToolItems.NETHERITE_LONG_SWORD.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_LONG_SWORD.get(),
               (Item)TensuraToolItems.MITHRIL_LONG_SWORD.get(),
               (Item)TensuraToolItems.ORICHALCUM_LONG_SWORD.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_LONG_SWORD.get(),
               (Item)TensuraToolItems.ADAMANTITE_LONG_SWORD.get(),
               (Item)TensuraToolItems.HIHIIROKANE_LONG_SWORD.get(),
               (Item)TensuraToolItems.TEMPEST_SCALE_SWORD.get(),
               (Item)TensuraToolItems.ICE_BLADE.get(),
               (Item)TensuraToolItems.DEAD_END_RAINBOW.get(),
               (Item)TensuraToolItems.MEAT_CRUSHER.get(),
               (Item)TensuraToolItems.MOONLIGHT.get(),
               (Item)TensuraToolItems.RUHK.get()
            }
         );
      this.tag(TensuraItemTags.GREAT_SWORDS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.WOODEN_GREAT_SWORD.get(),
               (Item)TensuraToolItems.STONE_GREAT_SWORD.get(),
               (Item)TensuraToolItems.SILVER_GREAT_SWORD.get(),
               (Item)TensuraToolItems.IRON_GREAT_SWORD.get(),
               (Item)TensuraToolItems.GOLDEN_GREAT_SWORD.get(),
               (Item)TensuraToolItems.DIAMOND_GREAT_SWORD.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_GREAT_SWORD.get(),
               (Item)TensuraToolItems.NETHERITE_GREAT_SWORD.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_GREAT_SWORD.get(),
               (Item)TensuraToolItems.MITHRIL_GREAT_SWORD.get(),
               (Item)TensuraToolItems.ORICHALCUM_GREAT_SWORD.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_GREAT_SWORD.get(),
               (Item)TensuraToolItems.ADAMANTITE_GREAT_SWORD.get(),
               (Item)TensuraToolItems.HIHIIROKANE_GREAT_SWORD.get()
            }
         );
      this.tag(TensuraItemTags.KATANAS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.WOODEN_KATANA.get(),
               (Item)TensuraToolItems.STONE_KATANA.get(),
               (Item)TensuraToolItems.SILVER_KATANA.get(),
               (Item)TensuraToolItems.IRON_KATANA.get(),
               (Item)TensuraToolItems.GOLDEN_KATANA.get(),
               (Item)TensuraToolItems.DIAMOND_KATANA.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_KATANA.get(),
               (Item)TensuraToolItems.NETHERITE_KATANA.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_KATANA.get(),
               (Item)TensuraToolItems.MITHRIL_KATANA.get(),
               (Item)TensuraToolItems.ORICHALCUM_KATANA.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_KATANA.get(),
               (Item)TensuraToolItems.ADAMANTITE_KATANA.get(),
               (Item)TensuraToolItems.HIHIIROKANE_KATANA.get()
            }
         );
      this.tag(TensuraItemTags.KODACHIS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.WOODEN_KODACHI.get(),
               (Item)TensuraToolItems.STONE_KODACHI.get(),
               (Item)TensuraToolItems.SILVER_KODACHI.get(),
               (Item)TensuraToolItems.IRON_KODACHI.get(),
               (Item)TensuraToolItems.GOLDEN_KODACHI.get(),
               (Item)TensuraToolItems.DIAMOND_KODACHI.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_KODACHI.get(),
               (Item)TensuraToolItems.NETHERITE_KODACHI.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_KODACHI.get(),
               (Item)TensuraToolItems.MITHRIL_KODACHI.get(),
               (Item)TensuraToolItems.ORICHALCUM_KODACHI.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_KODACHI.get(),
               (Item)TensuraToolItems.ADAMANTITE_KODACHI.get(),
               (Item)TensuraToolItems.HIHIIROKANE_KODACHI.get()
            }
         );
      this.tag(TensuraItemTags.TACHIS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.WOODEN_TACHI.get(),
               (Item)TensuraToolItems.STONE_TACHI.get(),
               (Item)TensuraToolItems.SILVER_TACHI.get(),
               (Item)TensuraToolItems.IRON_TACHI.get(),
               (Item)TensuraToolItems.GOLDEN_TACHI.get(),
               (Item)TensuraToolItems.DIAMOND_TACHI.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_TACHI.get(),
               (Item)TensuraToolItems.NETHERITE_TACHI.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_TACHI.get(),
               (Item)TensuraToolItems.MITHRIL_TACHI.get(),
               (Item)TensuraToolItems.ORICHALCUM_TACHI.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_TACHI.get(),
               (Item)TensuraToolItems.ADAMANTITE_TACHI.get(),
               (Item)TensuraToolItems.HIHIIROKANE_TACHI.get()
            }
         );
      this.tag(TensuraItemTags.ODACHIS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.WOODEN_ODACHI.get(),
               (Item)TensuraToolItems.STONE_ODACHI.get(),
               (Item)TensuraToolItems.SILVER_ODACHI.get(),
               (Item)TensuraToolItems.IRON_ODACHI.get(),
               (Item)TensuraToolItems.GOLDEN_ODACHI.get(),
               (Item)TensuraToolItems.DIAMOND_ODACHI.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_ODACHI.get(),
               (Item)TensuraToolItems.NETHERITE_ODACHI.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_ODACHI.get(),
               (Item)TensuraToolItems.MITHRIL_ODACHI.get(),
               (Item)TensuraToolItems.ORICHALCUM_ODACHI.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_ODACHI.get(),
               (Item)TensuraToolItems.ADAMANTITE_ODACHI.get(),
               (Item)TensuraToolItems.HIHIIROKANE_ODACHI.get()
            }
         );
      this.tag(TensuraItemTags.SPEARS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.WOODEN_SPEAR.get(),
               (Item)TensuraToolItems.STONE_SPEAR.get(),
               (Item)TensuraToolItems.SILVER_SPEAR.get(),
               (Item)TensuraToolItems.IRON_SPEAR.get(),
               (Item)TensuraToolItems.GOLDEN_SPEAR.get(),
               (Item)TensuraToolItems.DIAMOND_SPEAR.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_SPEAR.get(),
               (Item)TensuraToolItems.NETHERITE_SPEAR.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_SPEAR.get(),
               (Item)TensuraToolItems.MITHRIL_SPEAR.get(),
               (Item)TensuraToolItems.ORICHALCUM_SPEAR.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_SPEAR.get(),
               (Item)TensuraToolItems.ADAMANTITE_SPEAR.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SPEAR.get(),
               (Item)TensuraToolItems.BEAST_HORN_SPEAR.get(),
               (Item)TensuraToolItems.UNICORN_HORN_SPEAR.get()
            }
         );
      this.tag(TensuraItemTags.SCYTHES)
         .add(
            new Item[]{
               (Item)TensuraToolItems.WOODEN_SCYTHE.get(),
               (Item)TensuraToolItems.STONE_SCYTHE.get(),
               (Item)TensuraToolItems.SILVER_SCYTHE.get(),
               (Item)TensuraToolItems.IRON_SCYTHE.get(),
               (Item)TensuraToolItems.GOLDEN_SCYTHE.get(),
               (Item)TensuraToolItems.DIAMOND_SCYTHE.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_SCYTHE.get(),
               (Item)TensuraToolItems.NETHERITE_SCYTHE.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_SCYTHE.get(),
               (Item)TensuraToolItems.MITHRIL_SCYTHE.get(),
               (Item)TensuraToolItems.ORICHALCUM_SCYTHE.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_SCYTHE.get(),
               (Item)TensuraToolItems.ADAMANTITE_SCYTHE.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SCYTHE.get(),
               (Item)TensuraToolItems.BLADE_TIGER_SCYTHE.get()
            }
         );
      this.tag(TensuraItemTags.TRIDENTS).add(new Item[]{Items.TRIDENT, (Item)TensuraToolItems.VORTEX_SPEAR.get()});
      this.tag(TensuraItemTags.TOOL_RACK_EXCLUDED)
         .addTag(ItemTags.BOW_ENCHANTABLE)
         .addTag(ItemTags.CROSSBOW_ENCHANTABLE)
         .addTag(TensuraItemTags.FIST_WEAPONS)
         .addTag(TensuraItemTags.SHIELDS)
         .addTag(TensuraItemTags.POUCHES)
         .addTag(TensuraItemTags.MAGIC_GRIMOIRES)
         .add((Item)TensuraToolItems.MEAT_CRUSHER.get());
      this.tag(TensuraItemTags.FISHING_RODS).add(Items.FISHING_ROD);
      this.tag(TensuraItemTags.POUCHES)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.POUCH_D.get(),
               (Item)TensuraMaterialItems.POUCH_C.get(),
               (Item)TensuraMaterialItems.POUCH_B.get(),
               (Item)TensuraMaterialItems.POUCH_A.get(),
               (Item)TensuraMaterialItems.POUCH_SPECIAL_A.get()
            }
         );
      this.tag(TensuraItemTags.SICKLES)
         .add(
            new Item[]{
               (Item)TensuraToolItems.WOODEN_SICKLE.get(),
               (Item)TensuraToolItems.STONE_SICKLE.get(),
               (Item)TensuraToolItems.SILVER_SICKLE.get(),
               (Item)TensuraToolItems.IRON_SICKLE.get(),
               (Item)TensuraToolItems.GOLDEN_SICKLE.get(),
               (Item)TensuraToolItems.DIAMOND_SICKLE.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_SICKLE.get(),
               (Item)TensuraToolItems.NETHERITE_SICKLE.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_SICKLE.get(),
               (Item)TensuraToolItems.MITHRIL_SICKLE.get(),
               (Item)TensuraToolItems.ORICHALCUM_SICKLE.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_SICKLE.get(),
               (Item)TensuraToolItems.ADAMANTITE_SICKLE.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SICKLE.get()
            }
         );
      this.tag(TensuraItemTags.SHIELDS)
         .add(
            new Item[]{
               Items.SHIELD,
               (Item)TensuraToolItems.ARMORSAURUS_GAUNTLET.get(),
               (Item)TensuraToolItems.ARMORSAURUS_SHIELD.get(),
               (Item)TensuraToolItems.TEMPEST_SCALE_SHIELD.get()
            }
         );
      this.tag(TensuraItemTags.SPELL_BINDABLE).addTag(TensuraItemTags.SPELL_CAST_WEAPONS).add((Item)TensuraMaterialItems.UNBOUND_TOME.get());
      this.tag(TensuraItemTags.SCHEMATICS)
         .add(
            new Item[]{
               (Item)TensuraSmithingSchematicItems.BASIC_BOWS.get(),
               (Item)TensuraSmithingSchematicItems.SPIDER_BOWS.get(),
               (Item)TensuraSmithingSchematicItems.JAPANESE_SWORD.get(),
               (Item)TensuraSmithingSchematicItems.HUNTING_KNIFE.get(),
               (Item)TensuraSmithingSchematicItems.SHORT_SWORD.get(),
               (Item)TensuraSmithingSchematicItems.LONG_SWORD.get(),
               (Item)TensuraSmithingSchematicItems.GREAT_SWORD.get(),
               (Item)TensuraSmithingSchematicItems.SPEAR.get(),
               (Item)TensuraSmithingSchematicItems.KUNAI.get(),
               (Item)TensuraSmithingSchematicItems.SHIELD.get(),
               (Item)TensuraSmithingSchematicItems.MAGIC_STAFF.get(),
               (Item)TensuraSmithingSchematicItems.LEATHER_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.GOLD_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.IRON_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.SILVER_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.ANT_CARAPACE_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.SERPENT_SCALEMAIL_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.DIAMOND_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.KNIGHT_SPIDER_CARAPACE_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.ARMORSAURUS_SCALEMAIL_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.CHARYBDIS_SCALEMAIL_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.MITHRIL_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.ORICHALCUM_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.ADAMANTITE_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.HIHIIROKANE_GEAR.get(),
               (Item)TensuraSmithingSchematicItems.ANTI_MAGIC_MASK.get(),
               (Item)TensuraSmithingSchematicItems.DARK_SET.get(),
               (Item)TensuraSmithingSchematicItems.PIERROT_MASK.get(),
               (Item)TensuraSmithingSchematicItems.SPATIAL_BLADE.get(),
               (Item)TensuraSmithingSchematicItems.WEB_GUN.get()
            }
         );
      this.tag(ItemTags.DURABILITY_ENCHANTABLE)
         .addTag(TensuraItemTags.SHIELDS)
         .addTag(TensuraItemTags.SICKLES)
         .addTag(TensuraItemTags.SPEARS)
         .addTag(TensuraItemTags.SCYTHES)
         .addTag(TensuraItemTags.TRIDENTS)
         .addTag(TensuraItemTags.FIST_WEAPONS)
         .addTag(TensuraItemTags.SPELL_CAST_WEAPONS)
         .addTag(TensuraItemTags.POUCHES)
         .add(
            new Item[]{
               (Item)TensuraToolItems.SHORT_BOW.get(),
               (Item)TensuraToolItems.LONG_BOW.get(),
               (Item)TensuraToolItems.WAR_BOW.get(),
               (Item)TensuraToolItems.SPIDER_BOW.get(),
               (Item)TensuraToolItems.SHORT_SPIDER_BOW.get(),
               (Item)TensuraToolItems.LONG_SPIDER_BOW.get(),
               (Item)TensuraToolItems.WAR_SPIDER_BOW.get(),
               (Item)TensuraToolItems.ANT_CROSSBOW.get(),
               (Item)TensuraToolItems.KUNAI.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_KUNAI.get(),
               (Item)TensuraToolItems.GOBLIN_CLUB.get(),
               (Item)TensuraToolItems.KANABO.get()
            }
         );
      this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).add(new Item[]{(Item)TensuraToolItems.GOBLIN_CLUB.get(), (Item)TensuraToolItems.KANABO.get()});
      this.tag(ItemTags.MACE_ENCHANTABLE).add(new Item[]{(Item)TensuraToolItems.GOBLIN_CLUB.get(), (Item)TensuraToolItems.KANABO.get()});
      this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
         .add(
            new Item[]{
               (Item)TensuraToolItems.KUNAI.get(), (Item)TensuraToolItems.PURE_MAGISTEEL_KUNAI.get(), (Item)TensuraToolItems.SISSIE_TOOTH_PICKAXE.get()
            }
         )
         .addTag(TensuraItemTags.MULTITOOLS)
         .addTag(TensuraItemTags.SPEARS)
         .addTag(TensuraItemTags.SCYTHES);
      this.tag(ItemTags.BOW_ENCHANTABLE)
         .add(
            new Item[]{
               (Item)TensuraToolItems.SHORT_BOW.get(),
               (Item)TensuraToolItems.LONG_BOW.get(),
               (Item)TensuraToolItems.WAR_BOW.get(),
               (Item)TensuraToolItems.SPIDER_BOW.get(),
               (Item)TensuraToolItems.SHORT_SPIDER_BOW.get(),
               (Item)TensuraToolItems.LONG_SPIDER_BOW.get(),
               (Item)TensuraToolItems.WAR_SPIDER_BOW.get(),
               (Item)TensuraToolItems.KUNAI.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_KUNAI.get()
            }
         );
      this.tag(ItemTags.CROSSBOW_ENCHANTABLE)
         .add(
            new Item[]{
               (Item)TensuraToolItems.ANT_CROSSBOW.get(),
               (Item)TensuraToolItems.WALTHER_P99.get(),
               (Item)TensuraToolItems.KUNAI.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_KUNAI.get()
            }
         );
      this.tag(ItemTags.ARROWS).add(new Item[]{(Item)TensuraToolItems.INVISIBLE_ARROW.get(), (Item)TensuraToolItems.SPEARED_FIN_ARROW.get()});
      this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add((Item)TensuraMobDropItems.ORC_DISASTER_HEAD.get());
      this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add((Item)TensuraArmorItems.BAT_GLIDER.get());
      this.tag(ItemTags.MINING_ENCHANTABLE).addTag(TensuraItemTags.SICKLES);
      this.tag(ItemTags.MINING_LOOT_ENCHANTABLE).addTag(TensuraItemTags.SICKLES);
      this.tag(ItemTags.TRIDENT_ENCHANTABLE)
         .addTag(TensuraItemTags.SPEARS)
         .add(
            new Item[]{
               (Item)TensuraToolItems.KUNAI.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_KUNAI.get(),
               (Item)TensuraToolItems.SPATIAL_BLADE.get(),
               (Item)TensuraToolItems.VORTEX_SPEAR.get()
            }
         );
      this.tag(ItemTags.SWORD_ENCHANTABLE)
         .addTag(TensuraItemTags.MULTITOOLS)
         .addTag(TensuraItemTags.SPEARS)
         .addTag(TensuraItemTags.SCYTHES)
         .add((Item)TensuraToolItems.SISSIE_TOOTH_PICKAXE.get());
      this.tag(ItemTags.TRIM_MATERIALS)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.SILVER_INGOT.get(),
               (Item)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get(),
               (Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get(),
               (Item)TensuraMaterialItems.MITHRIL_INGOT.get(),
               (Item)TensuraMaterialItems.ORICHALCUM_INGOT.get(),
               (Item)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(),
               (Item)TensuraMaterialItems.ADAMANTITE_INGOT.get(),
               (Item)TensuraMaterialItems.HIHIIROKANE_INGOT.get()
            }
         );
   }

   protected void addNeoForgeTags() {
      this.tag(net.neoforged.neoforge.common.Tags.Items.FEATHERS)
         .add(new Item[]{(Item)TensuraMobDropItems.DRAGON_PEACOCK_FEATHER.get(), (Item)TensuraMobDropItems.INVISIBLE_FEATHER.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.MUSIC_DISCS).add((Item)TensuraMaterialItems.MUSIC_DISC_NANODA.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.SEEDS).add((Item)TensuraMaterialItems.HIPOKUTE_SEEDS.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.SLIME_BALLS).add((Item)TensuraMobDropItems.SLIME_CHUNK.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.STRINGS)
         .add(new Item[]{(Item)TensuraMobDropItems.STICKY_THREAD.get(), (Item)TensuraMobDropItems.STEEL_THREAD.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS).addTag(TensuraItemTags.MONSTER_CONSUMABLES);
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_RAW_MEAT).addTag(TensuraItemTags.RAW_MONSTER_CONSUMABLES);
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_COOKED_MEAT).addTag(TensuraItemTags.COOKED_MONSTER_CONSUMABLES);
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_RAW_FISH)
         .add(
            new Item[]{
               (Item)TensuraConsumableItems.RAW_CHARYBDIS_MEAT.get(),
               (Item)TensuraConsumableItems.RAW_MEGALODON_MEAT.get(),
               (Item)TensuraConsumableItems.RAW_SPEAR_TORO_MEAT.get(),
               (Item)TensuraConsumableItems.SPEAR_TORO_FIN.get(),
               (Item)TensuraConsumableItems.RAW_SISSIE_MEAT.get(),
               (Item)TensuraConsumableItems.SISSIE_FIN.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.FOODS_COOKED_FISH)
         .add(
            new Item[]{
               (Item)TensuraConsumableItems.COOKED_CHARYBDIS_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_MEGALODON_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SPEAR_TORO_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SPEAR_TORO_FIN.get(),
               (Item)TensuraConsumableItems.COOKED_SISSIE_MEAT.get(),
               (Item)TensuraConsumableItems.COOKED_SISSIE_FIN.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.INGOTS)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.SILVER_INGOT.get(),
               (Item)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get(),
               (Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get(),
               (Item)TensuraMaterialItems.MITHRIL_INGOT.get(),
               (Item)TensuraMaterialItems.ORICHALCUM_INGOT.get(),
               (Item)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(),
               (Item)TensuraMaterialItems.ADAMANTITE_INGOT.get(),
               (Item)TensuraMaterialItems.HIHIIROKANE_INGOT.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.NUGGETS)
         .add(
            new Item[]{
               (Item)TensuraMaterialItems.SILVER_NUGGET.get(),
               (Item)TensuraMaterialItems.LOW_MAGISTEEL_NUGGET.get(),
               (Item)TensuraMaterialItems.HIGH_MAGISTEEL_NUGGET.get(),
               (Item)TensuraMaterialItems.MITHRIL_NUGGET.get(),
               (Item)TensuraMaterialItems.ORICHALCUM_NUGGET.get(),
               (Item)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(),
               (Item)TensuraMaterialItems.ADAMANTITE_NUGGET.get(),
               (Item)TensuraMaterialItems.HIHIIROKANE_NUGGET.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.RAW_MATERIALS).add((Item)TensuraMaterialItems.RAW_SILVER.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.STORAGE_BLOCKS)
         .add(
            new Item[]{
               (Item)TensuraBlocks.Items.SILVER_BLOCK.get(),
               (Item)TensuraBlocks.Items.LOW_MAGISTEEL_BLOCK.get(),
               (Item)TensuraBlocks.Items.HIGH_MAGISTEEL_BLOCK.get(),
               (Item)TensuraBlocks.Items.MITHRIL_BLOCK.get(),
               (Item)TensuraBlocks.Items.ORICHALCUM_BLOCK.get(),
               (Item)TensuraBlocks.Items.PURE_MAGISTEEL_BLOCK.get(),
               (Item)TensuraBlocks.Items.ADAMANTITE_BLOCK.get(),
               (Item)TensuraBlocks.Items.HIHIIROKANE_BLOCK.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.TOOLS_BOW)
         .add(
            new Item[]{
               (Item)TensuraToolItems.SHORT_BOW.get(),
               (Item)TensuraToolItems.LONG_BOW.get(),
               (Item)TensuraToolItems.WAR_BOW.get(),
               (Item)TensuraToolItems.SPIDER_BOW.get(),
               (Item)TensuraToolItems.SHORT_SPIDER_BOW.get(),
               (Item)TensuraToolItems.LONG_SPIDER_BOW.get(),
               (Item)TensuraToolItems.WAR_SPIDER_BOW.get()
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Items.TOOLS_CROSSBOW).add((Item)TensuraToolItems.ANT_CROSSBOW.get());
      this.tag(net.neoforged.neoforge.common.Tags.Items.TOOLS_SHIELD)
         .add(new Item[]{(Item)TensuraToolItems.ARMORSAURUS_SHIELD.get(), (Item)TensuraToolItems.TEMPEST_SCALE_SHIELD.get()});
      this.tag(net.neoforged.neoforge.common.Tags.Items.TOOLS_SPEAR)
         .add(
            new Item[]{
               (Item)TensuraToolItems.SILVER_SPEAR.get(),
               (Item)TensuraToolItems.LOW_MAGISTEEL_SPEAR.get(),
               (Item)TensuraToolItems.HIGH_MAGISTEEL_SPEAR.get(),
               (Item)TensuraToolItems.MITHRIL_SPEAR.get(),
               (Item)TensuraToolItems.ORICHALCUM_SPEAR.get(),
               (Item)TensuraToolItems.PURE_MAGISTEEL_SPEAR.get(),
               (Item)TensuraToolItems.ADAMANTITE_SPEAR.get(),
               (Item)TensuraToolItems.HIHIIROKANE_SPEAR.get()
            }
         );
   }
}
