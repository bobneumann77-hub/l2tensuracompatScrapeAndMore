package io.github.manasmods.tensura.neoforge.data;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.advancement.TensuraAdvancements;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.battlewill.MeleeArts;
import io.github.manasmods.tensura.registry.battlewill.ProjectileArts;
import io.github.manasmods.tensura.registry.battlewill.UtilityArts;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.block.TensuraPaintingVariants;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraBannerPatterns;
import io.github.manasmods.tensura.registry.item.misc.TensuraTrimMaterials;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.magic.SummoningMagics;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.registry.world.TensuraBiomes;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class TensuraLanguageProvider extends LanguageProvider {
   public TensuraLanguageProvider(PackOutput output) {
      super(output, "tensura", "en_us");
   }

   protected void addTranslations() {
      this.battlewill();
      this.magic();
      this.skills();
      this.skillModes();
      this.attributes();
      this.effects();
      this.entitiesAndSpawnEggs();
      this.entities();
      this.creativeTabs();
      this.structureBlocks();
      this.blocksOfStorage();
      this.palmBlocks();
      this.miscBlocks();
      this.oreBlocks();
      this.armorItems();
      this.gearItems();
      this.uniqueGearItems();
      this.consumableItems();
      this.mobDropItems();
      this.miscItems();
      this.schematicItems();
      this.tooltips();
      this.enchantments();
      this.soundSubtitles();
      this.commandMessages();
      this.chatMessages();
      this.moltenMaterials();
      this.menuTexts();
      this.death();
      this.worldGen();
      this.gameRules();
      this.keybindings();
      this.advancements();
      this.stats();
      this.races();
   }

   private void attributes() {
      this.add("tensura.vanilla_attribute.health", "Health");
      this.add("tensura.vanilla_attribute.health.shortened_name", "HP");
      this.add("tensura.attribute.spiritual_health", "Spiritual Health");
      this.add("tensura.attribute.spiritual_health.shortened_name", "SHP");
      this.add("tensura.attribute.aura.name", "Aura");
      this.add("tensura.attribute.aura.shortened_name", "AP");
      this.add("tensura.attribute.magicule", "Magicule");
      this.add("tensura.attribute.magicule.shortened_name", "MP");
      this.add("tensura.attribute.existence_points", "Existence Points");
      this.add("tensura.attribute.existence_points.shortened_name", "EP");
      this.add("tensura.attribute.block.hardness", "Hardness");
      this.add("tensura.attribute.block.correct_tool", "Correct Tool");
      this.add("tensura.attribute.block.light_level", "Light Level");
      this.add("tensura.attribute.block.redstone_strength", "Redstone Power");
      this.add("tensura.attribute.block.redstone_powered", "Powered");
      this.add("tensura.attribute.block.egg_hatch", "Hatching");
      this.add("tensura.attribute.block.explosion_resistance", "Blast Resistance");
      this.add("tensura.attribute.block.age", "Age");
      this.add("tensura.attribute.block.failed_growth", "Failed Growth");
      this.addAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH, "Max Spiritual Health");
      this.addAttribute(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, "Spiritual Health Regeneration");
      this.addAttribute(TensuraAttributes.MAX_AURA, "Max Aura");
      this.addAttribute(TensuraAttributes.LIMITED_SPIRITUAL_MAX_AURA, "Limited Spiritual Max Aura");
      this.addAttribute(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, "Aura Regeneration Multiplier");
      this.addAttribute(TensuraAttributes.AURA_GAIN, "Aura Gain Percentage");
      this.addAttribute(TensuraAttributes.MAX_MAGICULE, "Max Magicule");
      this.addAttribute(TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE, "Limited Spiritual Max Magicule");
      this.addAttribute(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, "Magicule Regeneration Multiplier");
      this.addAttribute(TensuraAttributes.MAGICULE_GAIN, "Magicule Gain Percentage");
      this.addAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN, "Ability Learning Gain");
      this.addAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN, "Ability Mastery Gain");
      this.addAttribute(TensuraAttributes.HEAT_SENSE_RADIUS, "Heat Sense Radius");
      this.addAttribute(TensuraAttributes.PRESENCE_SENSE, "Presence Sense");
      this.addAttribute(TensuraAttributes.PRESENCE_SENSE_RADIUS, "Presence Sense Radius");
      this.addAttribute(TensuraAttributes.PRESENCE_CONCEALMENT, "Presence Concealment");
      this.addAttribute(TensuraAttributes.ANALYSIS_LEVEL, "Analysis Level");
      this.addAttribute(TensuraAttributes.ANALYSIS_DISTANCE, "Analysis Distance");
      this.addAttribute(TensuraAttributes.VIEW_ZOOM, "View Zoom");
      this.addAttribute(TensuraAttributes.DARK_VISION, "Dark Vision");
      this.addAttribute(TensuraAttributes.DODGE_STRENGTH, "Dodge Strength");
      this.addAttribute(TensuraAttributes.DODGE_INVULNERABILITY, "Dodge Invulnerability");
      this.addAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE, "Auto Melee Dodge Chance");
      this.addAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE, "Auto Projectile Dodge Chance");
      this.addAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE, "Dodge Negate Chance");
      this.addAttribute(TensuraAttributes.CHANT_SPEED, "Chant Speed");
      this.addAttribute(TensuraAttributes.MAGIC_COST_MULTIPLIER, "Magic Cost Multiplier");
      this.addAttribute(TensuraAttributes.MULTILAYER_BARRIER, "Multilayer Barrier");
      this.addAttribute(TensuraAttributes.WARP_SHOT, "Warp Shot");
      this.addAttribute(TensuraAttributes.HEIGHT_MULTIPLIER, "Height Multiplier");
      this.addAttribute(TensuraAttributes.WIDTH_MULTIPLIER, "Width Multiplier");
      this.addAttribute(TensuraAttributes.WATER_CAPACITY, "Water Capacity");
      this.addAttribute(TensuraAttributes.LAVA_CAPACITY, "Lava Capacity");
      this.addAttribute(TensuraAttributes.LAW_DEGRADATION, "Law Degradation");
      this.addAttribute(TensuraAttributes.RESISTANCE_DEGRADATION, "Resistance Degradation");
      this.addAttribute(TensuraAttributes.PHYSICAL_RESIST_DEGRADATION, "Physical Resist Degradation");
      this.addAttribute(TensuraAttributes.EARTH_RESIST_DEGRADATION, "Earth Resist Degradation");
      this.addAttribute(TensuraAttributes.DARKNESS_RESIST_DEGRADATION, "Darkness Resist Degradation");
      this.addAttribute(TensuraAttributes.FLAME_RESIST_DEGRADATION, "Flame Resist Degradation");
      this.addAttribute(TensuraAttributes.LIGHT_RESIST_DEGRADATION, "Light Resist Degradation");
      this.addAttribute(TensuraAttributes.SPACE_RESIST_DEGRADATION, "Space Resist Degradation");
      this.addAttribute(TensuraAttributes.WATER_RESIST_DEGRADATION, "Water Resist Degradation");
      this.addAttribute(TensuraAttributes.WIND_RESIST_DEGRADATION, "Wind Resist Degradation");
      this.addAttribute(TensuraAttributes.GRAVITY_RESIST_DEGRADATION, "Gravity Resist Degradation");
      this.addAttribute(TensuraAttributes.LIGHTNING_RESIST_DEGRADATION, "Lightning Resist Degradation");
      this.addAttribute(TensuraAttributes.EARTH_BOOST, "Earth Attack Boost");
      this.addAttribute(TensuraAttributes.DARKNESS_BOOST, "Darkness Attack Boost");
      this.addAttribute(TensuraAttributes.FLAME_BOOST, "Fire Attack Boost");
      this.addAttribute(TensuraAttributes.LIGHT_BOOST, "Light Attack Boost");
      this.addAttribute(TensuraAttributes.SPACE_BOOST, "Space Attack Boost");
      this.addAttribute(TensuraAttributes.WATER_BOOST, "Water Attack Boost");
      this.addAttribute(TensuraAttributes.WIND_BOOST, "Wind Attack Boost");
      this.addAttribute(TensuraAttributes.MAGIC_INTERFERENCE, "Magic Interference");
      this.addAttribute(TensuraAttributes.MAGIC_RESISTANCE, "Magic Resistance");
      this.addAttribute(TensuraAttributes.MAGIC_BARRIER, "Magic Barrier");
      this.addAttribute(TensuraAttributes.PHYSICAL_BARRIER, "Physical Barrier");
      this.addAttribute(TensuraAttributes.EARTH_RESISTANCE, "Earth Attack Resistance");
      this.addAttribute(TensuraAttributes.DARKNESS_RESISTANCE, "Darkness Attack Resistance");
      this.addAttribute(TensuraAttributes.FLAME_RESISTANCE, "Fire Attack Resistance");
      this.addAttribute(TensuraAttributes.LIGHT_RESISTANCE, "Light Attack Resistance");
      this.addAttribute(TensuraAttributes.SPACE_RESISTANCE, "Space Attack Resistance");
      this.addAttribute(TensuraAttributes.WATER_RESISTANCE, "Water Attack Resistance");
      this.addAttribute(TensuraAttributes.WIND_RESISTANCE, "Wind Attack Resistance");
      this.addAttribute(TensuraAttributes.GRAVITY_BOOST, "Gravity Attack Boost");
      this.addAttribute(TensuraAttributes.LIGHTNING_BOOST, "Lightning Attack Boost");
      this.addAttribute(TensuraAttributes.ILLUSION_BOOST, "Illusion Attack Boost");
      this.addAttribute(TensuraAttributes.SOUND_BOOST, "Sound Attack Boost");
   }

   private void creativeTabs() {
      this.add("item_tab.tensura.armors", "T:R Armors");
      this.add("item_tab.tensura.gears", "T:R Gears");
      this.add("item_tab.tensura.blocks", "T:R Blocks");
      this.add("item_tab.tensura.functional", "T:R Functional Blocks");
      this.add("item_tab.tensura.dungeon", "T:R Dungeon Blocks");
      this.add("item_tab.tensura.food", "T:R Consumables");
      this.add("item_tab.tensura.drops", "T:R Mob Drops");
      this.add("item_tab.tensura.learnable", "T:R Learnable");
      this.add("item_tab.tensura.misc", "T:R Misc");
      this.add("item_tab.tensura.eggs", "T:R Spawn Eggs");
   }

   private void palmBlocks() {
      this.addBlock(TensuraBlocks.PALM_SAPLING, "Palm Sapling");
      this.addBlock(TensuraBlocks.PALM_LEAVES, "Palm Leaves");
      this.addBlock(TensuraBlocks.PALM_LOG, "Palm Log");
      this.addBlock(TensuraBlocks.PALM_WOOD, "Palm Wood");
      this.addBlock(TensuraBlocks.STRIPPED_PALM_LOG, "Stripped Palm Log");
      this.addBlock(TensuraBlocks.STRIPPED_PALM_WOOD, "Stripped Palm Wood");
      this.addBlock(TensuraBlocks.PALM_PLANKS, "Palm Planks");
      this.addBlock(TensuraBlocks.PALM_STAIRS, "Palm Stairs");
      this.addBlock(TensuraBlocks.PALM_SLAB, "Palm Slab");
      this.addBlock(TensuraBlocks.PALM_DOOR, "Palm Door");
      this.addBlock(TensuraBlocks.PALM_TRAPDOOR, "Palm Trapdoor");
      this.addBlock(TensuraBlocks.PALM_FENCE, "Palm Fence");
      this.addBlock(TensuraBlocks.PALM_FENCE_GATE, "Palm Fence Gate");
      this.addBlock(TensuraBlocks.PALM_BUTTON, "Palm Button");
      this.addBlock(TensuraBlocks.PALM_PRESSURE_PLATE, "Palm Pressure Plate");
      this.addBlock(TensuraBlocks.PALM_STANDING_SIGN, "Palm Sign");
      this.addBlock(TensuraBlocks.PALM_HANGING_SIGN, "Palm Hanging Sign");
      this.addItem(TensuraBlocks.Items.PALM_BOAT, "Palm Boat");
      this.addItem(TensuraBlocks.Items.PALM_CHEST_BOAT, "Palm Boat with Chest");
   }

   private void oreBlocks() {
      this.addBlock(TensuraBlocks.MAGIC_ORE, "Magic Ore");
      this.addBlock(TensuraBlocks.SILVER_ORE, "Silver Ore");
      this.addBlock(TensuraBlocks.DEEPSLATE_MAGIC_ORE, "Deepslate Magic Ore");
      this.addBlock(TensuraBlocks.DEEPSLATE_SILVER_ORE, "Deepslate Silver Ore");
   }

   private void blocksOfStorage() {
      this.addBlock(TensuraBlocks.RAW_SILVER_BLOCK, "Block of Raw Silver");
      this.addBlock(TensuraBlocks.SILVER_BLOCK, "Block of Silver");
      this.addBlock(TensuraBlocks.MAGIC_ORE_BLOCK, "Block of Magic Ore");
      this.addBlock(TensuraBlocks.LOW_MAGISTEEL_BLOCK, "Block of Low Magisteel");
      this.addBlock(TensuraBlocks.HIGH_MAGISTEEL_BLOCK, "Block of High Magisteel");
      this.addBlock(TensuraBlocks.MITHRIL_BLOCK, "Block of Mithril");
      this.addBlock(TensuraBlocks.ORICHALCUM_BLOCK, "Block of Orichalcum");
      this.addBlock(TensuraBlocks.PURE_MAGISTEEL_BLOCK, "Block of Pure Magisteel");
      this.addBlock(TensuraBlocks.ADAMANTITE_BLOCK, "Block of Adamantite");
      this.addBlock(TensuraBlocks.HIHIIROKANE_BLOCK, "Block of Hihi'Irokane");
   }

   private void miscBlocks() {
      this.addBlock(TensuraBlocks.KILN, "Kiln");
      this.addBlock(TensuraBlocks.KILN_MITHRIL, "Mithril Kiln");
      this.addBlock(TensuraBlocks.KILN_ORICHALCUM, "Orichalcum Kiln");
      this.addBlock(TensuraBlocks.MINING_STATION, "Mining Station");
      this.addBlock(TensuraBlocks.SMITHING_BENCH, "Smithing Bench");
      this.addBlock(TensuraBlocks.SPELLBINDING_TABLE, "Spellbinding Table");
      this.addBlock(TensuraBlocks.WOODCUTTER, "Woodcutter");
      this.addBlock(TensuraBlocks.OAK_TOOL_RACK, "Oak Tool Rack");
      this.addBlock(TensuraBlocks.SPRUCE_TOOL_RACK, "Spruce Tool Rack");
      this.addBlock(TensuraBlocks.BIRCH_TOOL_RACK, "Birch Tool Rack");
      this.addBlock(TensuraBlocks.JUNGLE_TOOL_RACK, "Jungle Tool Rack");
      this.addBlock(TensuraBlocks.ACACIA_TOOL_RACK, "Acacia Tool Rack");
      this.addBlock(TensuraBlocks.DARK_OAK_TOOL_RACK, "Dark Oak Tool Rack");
      this.addBlock(TensuraBlocks.MANGROVE_TOOL_RACK, "Mangrove Tool Rack");
      this.addBlock(TensuraBlocks.CHERRY_TOOL_RACK, "Cherry Tool Rack");
      this.addBlock(TensuraBlocks.PALM_TOOL_RACK, "Palm Tool Rack");
      this.addBlock(TensuraBlocks.BAMBOO_TOOL_RACK, "Bamboo Tool Rack");
      this.addBlock(TensuraBlocks.CRIMSON_TOOL_RACK, "Crimson Tool Rack");
      this.addBlock(TensuraBlocks.WARPED_TOOL_RACK, "Warped Tool Rack");
      this.addBlock(TensuraBlocks.STONE_WARP_PAD, "Stone Warp Pad");
      this.addBlock(TensuraBlocks.GRANITE_WARP_PAD, "Granite Warp Pad");
      this.addBlock(TensuraBlocks.DIORITE_WARP_PAD, "Diorite Warp Pad");
      this.addBlock(TensuraBlocks.ANDESITE_WARP_PAD, "Andesite Warp Pad");
      this.addBlock(TensuraBlocks.CALCITE_WARP_PAD, "Calcite Warp Pad");
      this.addBlock(TensuraBlocks.TUFF_WARP_PAD, "Tuff Warp Pad");
      this.addBlock(TensuraBlocks.DEEPSLATE_WARP_PAD, "Deepslate Warp Pad");
      this.addBlock(TensuraBlocks.BRICK_WARP_PAD, "Brick Warp Pad");
      this.addBlock(TensuraBlocks.SANDSTONE_WARP_PAD, "Sandstone Warp Pad");
      this.addBlock(TensuraBlocks.RED_SANDSTONE_WARP_PAD, "Red Sandstone Warp Pad");
      this.addBlock(TensuraBlocks.SARASA_SANDSTONE_WARP_PAD, "Sarasa Sandstone Warp Pad");
      this.addBlock(TensuraBlocks.PACKED_MUD_WARP_PAD, "Packed Mud Warp Pad");
      this.addBlock(TensuraBlocks.PRISMARINE_BRICK_WARP_PAD, "Prismarine Brick Warp Pad");
      this.addBlock(TensuraBlocks.NETHER_BRICK_WARP_PAD, "Nether Brick Warp Pad");
      this.addBlock(TensuraBlocks.RED_NETHER_BRICK_WARP_PAD, "Red Nether Brick Warp Pad");
      this.addBlock(TensuraBlocks.BLACKSTONE_WARP_PAD, "Blackstone Warp Pad");
      this.addBlock(TensuraBlocks.BASALT_WARP_PAD, "Basalt Warp Pad");
      this.addBlock(TensuraBlocks.QUARTZ_WARP_PAD, "Quartz Warp Pad");
      this.addBlock(TensuraBlocks.END_STONE_WARP_PAD, "End Stone Warp Pad");
      this.addBlock(TensuraBlocks.PURPUR_WARP_PAD, "Purpur Warp Pad");
      this.addBlock(TensuraBlocks.ROYAL_DWARVEN_WARP_PAD, "Royal Dwarven Warp Pad");
      this.addBlock(TensuraBlocks.LABYRINTH_BRICK_WARP_PAD, "Labyrinth Brick Warp Pad");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_BRICK_WARP_PAD, "Cream Labyrinth Brick Warp Pad");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_BRICK_WARP_PAD, "Dark Labyrinth Brick Warp Pad");
      this.addBlock(TensuraBlocks.BRICKS_MAGIC_ENGINE, "Bricks Magic Engine");
      this.addBlock(TensuraBlocks.STONE_BRICKS_MAGIC_ENGINE, "Stone Bricks Magic Engine");
      this.addBlock(TensuraBlocks.TUFF_BRICKS_MAGIC_ENGINE, "Tuff Bricks Magic Engine");
      this.addBlock(TensuraBlocks.DEEPSLATE_BRICKS_MAGIC_ENGINE, "Deepslate Bricks Magic Engine");
      this.addBlock(TensuraBlocks.MUD_BRICKS_MAGIC_ENGINE, "Mud Bricks Magic Engine");
      this.addBlock(TensuraBlocks.PRISMARINE_BRICK_MAGIC_ENGINE, "Prismarine Bricks Magic Engine");
      this.addBlock(TensuraBlocks.NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE, "Nether Bricks Magic Engine");
      this.addBlock(TensuraBlocks.RED_NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE, "Red Nether Bricks Magic Engine");
      this.addBlock(TensuraBlocks.POLISHED_BLACKSTONE_BRICKS_MAGIC_ENGINE, "Polished Blackstone Bricks Magic Engine");
      this.addBlock(TensuraBlocks.QUARTZ_BRICKS_MAGIC_ENGINE, "Quartz Bricks Magic Engine");
      this.addBlock(TensuraBlocks.END_STONE_BRICKS_MAGIC_ENGINE, "End Stone Bricks Magic Engine");
      this.addBlock(TensuraBlocks.PURPUR_BRICKS_MAGIC_ENGINE, "Purpur Bricks Magic Engine");
      this.addBlock(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE, "Low Quality Magic Crystal Bricks Magic Engine");
      this.addBlock(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE, "Medium Quality Magic Crystal Bricks Magic Engine");
      this.addBlock(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE, "High Quality Magic Crystal Bricks Magic Engine");
      this.addBlock(TensuraBlocks.LABYRINTH_BRICKS_MAGIC_ENGINE, "Labyrinth Bricks Magic Engine");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_BRICKS_MAGIC_ENGINE, "Cream Labyrinth Bricks Magic Engine");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_BRICKS_MAGIC_ENGINE, "Dark Labyrinth Bricks Magic Engine");
      this.addBlock(TensuraBlocks.THATCH_BLOCK, "Thatch Block");
      this.addBlock(TensuraBlocks.THATCH_STAIRS, "Thatch Stairs");
      this.addBlock(TensuraBlocks.THATCH_SLAB, "Thatch Slab");
      this.addBlock(TensuraBlocks.THATCH_WALL, "Thatch Wall");
      this.addBlock(TensuraBlocks.TATAMI_BLOCK, "Tatami Block");
      this.addBlock(TensuraBlocks.TATAMI_CARPET, "Tatami Carpet");
      this.addBlock(TensuraBlocks.SINGLE_TATAMI_BLOCK, "Single Tatami Block");
      this.addBlock(TensuraBlocks.SINGLE_TATAMI_CARPET, "Single Tatami Carpet");
      this.addBlock(TensuraBlocks.SARASA_SAND, "Sarasa Sand");
      this.addBlock(TensuraBlocks.SARASA_SANDSTONE, "Sarasa Sandstone");
      this.addBlock(TensuraBlocks.SARASA_SANDSTONE_STAIRS, "Sarasa Sandstone Stairs");
      this.addBlock(TensuraBlocks.SARASA_SANDSTONE_SLAB, "Sarasa Sandstone Slab");
      this.addBlock(TensuraBlocks.SARASA_SANDSTONE_WALL, "Sarasa Sandstone Wall");
      this.addBlock(TensuraBlocks.CUT_SARASA_SANDSTONE, "Cut Sarasa Sandstone");
      this.addBlock(TensuraBlocks.CUT_SARASA_SANDSTONE_SLAB, "Cut Sarasa Sandstone Slab");
      this.addBlock(TensuraBlocks.SMOOTH_SARASA_SANDSTONE, "Smooth Sarasa Sandstone");
      this.addBlock(TensuraBlocks.SMOOTH_SARASA_SANDSTONE_STAIRS, "Smooth Sarasa Sandstone Stairs");
      this.addBlock(TensuraBlocks.SMOOTH_SARASA_SANDSTONE_SLAB, "Smooth Sarasa Sandstone Slab");
      this.addBlock(TensuraBlocks.CHISELED_SARASA_SANDSTONE, "Chiseled Sarasa Sandstone");
      this.addBlock(TensuraBlocks.STICKY_COBWEB, "Sticky Cobweb");
      this.addBlock(TensuraBlocks.STICKY_STEEL_COBWEB, "Sticky Steel Cobweb");
      this.addBlock(TensuraBlocks.WEB_BLOCK, "Web Block");
      this.addBlock(TensuraBlocks.WEB_STAIRS, "Web Stairs");
      this.addBlock(TensuraBlocks.WEB_SLAB, "Web Slab");
      this.addBlock(TensuraBlocks.WEBBED_COBBLESTONE, "Webbed Cobblestone");
      this.addBlock(TensuraBlocks.WEBBED_COBBLESTONE_STAIRS, "Webbed Cobblestone Stairs");
      this.addBlock(TensuraBlocks.WEBBED_COBBLESTONE_SLAB, "Webbed Cobblestone Slab");
      this.addBlock(TensuraBlocks.WEBBED_COBBLESTONE_WALL, "Webbed Cobblestone Wall");
      this.addBlock(TensuraBlocks.WEBBED_STONE_BRICKS, "Webbed Stone Bricks");
      this.addBlock(TensuraBlocks.WEBBED_STONE_BRICK_STAIRS, "Webbed Stone Brick Stairs");
      this.addBlock(TensuraBlocks.WEBBED_STONE_BRICK_SLAB, "Webbed Stone Brick Slab");
      this.addBlock(TensuraBlocks.WEBBED_STONE_BRICK_WALL, "Webbed Stone Brick Wall");
      this.addBlock(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK, "Low Quality Magic Crystal Block");
      this.addBlock(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_STAIRS, "Low Quality Magic Crystal Stairs");
      this.addBlock(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_SLAB, "Low Quality Magic Crystal Slab");
      this.addBlock(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS, "Low Quality Magic Crystal Bricks");
      this.addBlock(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS, "Low Quality Magic Crystal Brick Stairs");
      this.addBlock(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB, "Low Quality Magic Crystal Brick Slab");
      this.addBlock(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_WALL, "Low Quality Magic Crystal Brick Wall");
      this.addBlock(TensuraBlocks.CHISELED_LOW_QUALITY_MAGIC_CRYSTAL_BRICKS, "Chiseled Low Quality Magic Crystal Bricks");
      this.addBlock(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK, "Medium Quality Magic Crystal Block");
      this.addBlock(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_STAIRS, "Medium Quality Magic Crystal Stairs");
      this.addBlock(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_SLAB, "Medium Quality Magic Crystal Slab");
      this.addBlock(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS, "Medium Quality Magic Crystal Bricks");
      this.addBlock(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS, "Medium Quality Magic Crystal Brick Stairs");
      this.addBlock(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB, "Medium Quality Magic Crystal Brick Slab");
      this.addBlock(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_WALL, "Medium Quality Magic Crystal Brick Wall");
      this.addBlock(TensuraBlocks.CHISELED_MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS, "Chiseled Medium Quality Magic Crystal Bricks");
      this.addBlock(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK, "High Quality Magic Crystal Block");
      this.addBlock(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_STAIRS, "High Quality Magic Crystal Stairs");
      this.addBlock(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_SLAB, "High Quality Magic Crystal Slab");
      this.addBlock(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS, "High Quality Magic Crystal Bricks");
      this.addBlock(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS, "High Quality Magic Crystal Brick Stairs");
      this.addBlock(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB, "High Quality Magic Crystal Brick Slab");
      this.addBlock(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_WALL, "High Quality Magic Crystal Brick Wall");
      this.addBlock(TensuraBlocks.CHISELED_HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS, "Chiseled High Quality Magic Crystal Bricks");
      this.addBlock(TensuraBlocks.SLIME_CHUNK_BLOCK, "Slime Chunk Block");
      this.addBlock(TensuraBlocks.CHILLED_SLIME_BLOCK, "Chilled Slime Block");
      this.addBlock(TensuraBlocks.CHARYBDIS_CORE, "Charybdis Core");
      this.addBlock(TensuraBlocks.MOTH_EGG, "Moth Egg");
      this.addBlock(TensuraBlocks.SPIDER_EGG, "Spider Egg");
      this.addBlock(TensuraBlocks.BLACK_FIRE, "Black Fire");
      this.addBlock(TensuraBlocks.THATCH_BED, "Thatch Bed");
      this.addBlock(TensuraBlocks.TRAINING_DUMMY, "Training Dummy");
      this.addBlock(TensuraBlocks.HIPOKUTE_GRASS, "Hipokute Grass");
      this.addBlock(TensuraBlocks.POTTED_HIPOKUTE_FLOWER, "Potted Hipokute Grass");
      this.addBlock(TensuraBlocks.BAFFLEDIL, "Baffledil");
      this.addBlock(TensuraBlocks.POTTED_BAFFLEDIL, "Potted Baffledil");
      this.addBlock(TensuraBlocks.POTTED_PALM_SAPLING, "Potted Palm Sapling");
      this.addBlock(TensuraBlocks.LOOSE_DIRT, "Loose Dirt");
      this.addBlock(TensuraBlocks.LOOSE_GRAVEL, "Loose Gravel");
      this.addBlock(TensuraBlocks.QUICKMUD, "Quickmud");
      this.addBlock(TensuraBlocks.QUICKSAND, "Quicksand");
      this.addBlock(TensuraBlocks.RED_QUICKSAND, "Red Quicksand");
      this.addBlock(TensuraBlocks.SARASA_QUICKSAND, "Sarasa Quicksand");
      this.addBlock(TensuraBlocks.LIGHT_AIR, "Light Air");
      this.addBlock(TensuraBlocks.SOLID_SPACE, "Solid Space");
   }

   private void structureBlocks() {
      this.addBlock(TensuraBlocks.LABYRINTH_LAMP, "Labyrinth Lamp");
      this.addBlock(TensuraBlocks.LABYRINTH_LAMP_BL, "Labyrinth Lamp Bottom Left");
      this.addBlock(TensuraBlocks.LABYRINTH_LAMP_BR, "Labyrinth Lamp Bottom Right");
      this.addBlock(TensuraBlocks.LABYRINTH_LAMP_TL, "Labyrinth Lamp Top Left");
      this.addBlock(TensuraBlocks.LABYRINTH_LAMP_TR, "Labyrinth Lamp Top Right");
      this.addBlock(TensuraBlocks.LABYRINTH_LIT_LAMP, "Labyrinth Lit Lamp");
      this.addBlock(TensuraBlocks.LABYRINTH_LIT_LAMP_BL, "Labyrinth Lit Lamp Bottom Left");
      this.addBlock(TensuraBlocks.LABYRINTH_LIT_LAMP_BR, "Labyrinth Lit Lamp Bottom Right");
      this.addBlock(TensuraBlocks.LABYRINTH_LIT_LAMP_TL, "Labyrinth Lit Lamp Top Left");
      this.addBlock(TensuraBlocks.LABYRINTH_LIT_LAMP_TR, "Labyrinth Lit Lamp Top Right");
      this.addBlock(TensuraBlocks.LABYRINTH_BRICKS, "Labyrinth Bricks");
      this.addBlock(TensuraBlocks.LABYRINTH_BRICK_STAIR, "Labyrinth Brick Stairs");
      this.addBlock(TensuraBlocks.LABYRINTH_BRICK_SLAB, "Labyrinth Brick Slab");
      this.addBlock(TensuraBlocks.LABYRINTH_BRICK_BL, "Labyrinth Bricks Bottom Left");
      this.addBlock(TensuraBlocks.LABYRINTH_BRICK_BR, "Labyrinth Bricks Bottom Right");
      this.addBlock(TensuraBlocks.LABYRINTH_BRICK_TL, "Labyrinth Bricks Top Left");
      this.addBlock(TensuraBlocks.LABYRINTH_BRICK_TR, "Labyrinth Bricks Top Right");
      this.addBlock(TensuraBlocks.LABYRINTH_STONE, "Labyrinth Stone");
      this.addBlock(TensuraBlocks.LABYRINTH_STONE_STAIR, "Labyrinth Stone Stairs");
      this.addBlock(TensuraBlocks.LABYRINTH_STONE_SLAB, "Labyrinth Stone Slab");
      this.addBlock(TensuraBlocks.LABYRINTH_STONE_BL, "Labyrinth Stone Bottom Left");
      this.addBlock(TensuraBlocks.LABYRINTH_STONE_BR, "Labyrinth Stone Bottom Right");
      this.addBlock(TensuraBlocks.LABYRINTH_STONE_TL, "Labyrinth Stone Top Left");
      this.addBlock(TensuraBlocks.LABYRINTH_STONE_TR, "Labyrinth Stone Top Right");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_BRICKS, "Cream Labyrinth Bricks");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_BRICK_STAIR, "Cream Labyrinth Brick Stairs");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_BRICK_SLAB, "Cream Labyrinth Brick Slab");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_BRICK_BL, "Cream Labyrinth Bricks Bottom Left");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_BRICK_BR, "Cream Labyrinth Bricks Bottom Right");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_BRICK_TL, "Cream Labyrinth Bricks Top Left");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_BRICK_TR, "Cream Labyrinth Bricks Top Right");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_STONE, "Cream Labyrinth Stone");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_STONE_STAIR, "Cream Labyrinth Stone Stairs");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_STONE_SLAB, "Cream Labyrinth Stone Slab");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_STONE_BL, "Cream Labyrinth Stone Bottom Left");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_STONE_BR, "Cream Labyrinth Stone Bottom Right");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_STONE_TL, "Cream Labyrinth Stone Top Left");
      this.addBlock(TensuraBlocks.CREAM_LABYRINTH_STONE_TR, "Cream Labyrinth Stone Top Right");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_BRICKS, "Dark Labyrinth Bricks");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_BRICK_STAIR, "Dark Labyrinth Brick Stairs");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_BRICK_SLAB, "Dark Labyrinth Brick Slab");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_BRICK_BL, "Dark Labyrinth Bricks Bottom Left");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_BRICK_BR, "Dark Labyrinth Bricks Bottom Right");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_BRICK_TL, "Dark Labyrinth Bricks Top Left");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_BRICK_TR, "Dark Labyrinth Bricks Top Right");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_STONE, "Dark Labyrinth Stone");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_STONE_STAIR, "Dark Labyrinth Stone Stairs");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_STONE_SLAB, "Dark Labyrinth Stone Slab");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_STONE_BL, "Dark Labyrinth Stone Bottom Left");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_STONE_BR, "Dark Labyrinth Stone Bottom Right");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_STONE_TL, "Dark Labyrinth Stone Top Left");
      this.addBlock(TensuraBlocks.DARK_LABYRINTH_STONE_TR, "Dark Labyrinth Stone Top Right");
      this.addBlock(TensuraBlocks.LABYRINTH_PORTAL, "Labyrinth Portal");
      this.addBlock(TensuraBlocks.LABYRINTH_BARRIER_BLOCK, "Labyrinth Barrier");
      this.addBlock(TensuraBlocks.LABYRINTH_CRYSTAL, "Labyrinth Crystal");
      this.addBlock(TensuraBlocks.LABYRINTH_PRAYING_PATH, "Labyrinth Praying Path");
      this.addBlock(TensuraBlocks.LABYRINTH_LIGHT_PATH, "Labyrinth Light Path");
      this.addBlock(TensuraBlocks.LABYRINTH_LIGHT_PATH_STAIRS, "Labyrinth Light Path Stairs");
      this.addBlock(TensuraBlocks.LABYRINTH_LIGHT_PATH_SLAB, "Labyrinth Light Path Slab");
      this.addBlock(TensuraBlocks.HELL_PORTAL, "Hell Portal");
      this.addBanner(TensuraBannerPatterns.DWARGON, "Dwargon");
   }

   private void armorItems() {
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_D_HELMET, "Monster Leather Helmet (D)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_D_CHESTPLATE, "Monster Leather Chestplate (D)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_D_LEGGINGS, "Monster Leather Leggings (D)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_D_BOOTS, "Monster Leather Boots (D)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_C_HELMET, "Monster Leather Helmet (C)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_C_CHESTPLATE, "Monster Leather Chestplate (C)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_C_LEGGINGS, "Monster Leather Leggings (C)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_C_BOOTS, "Monster Leather Boots (C)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_B_HELMET, "Monster Leather Helmet (B)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_B_CHESTPLATE, "Monster Leather Chestplate (B)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_B_LEGGINGS, "Monster Leather Leggings (B)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_B_BOOTS, "Monster Leather Boots (B)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_A_HELMET, "Monster Leather Helmet (A)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_A_CHESTPLATE, "Monster Leather Chestplate (A)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_A_LEGGINGS, "Monster Leather Leggings (A)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_A_BOOTS, "Monster Leather Boots (A)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_HELMET, "Monster Leather Helmet (Special A)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_CHESTPLATE, "Monster Leather Chestplate (Special A)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_LEGGINGS, "Monster Leather Leggings (Special A)");
      this.addItem(TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_BOOTS, "Monster Leather Boots (Special A)");
      this.addItem(TensuraArmorItems.SILVER_HELMET, "Silver Helmet");
      this.addItem(TensuraArmorItems.SILVER_CHESTPLATE, "Silver Chestplate");
      this.addItem(TensuraArmorItems.SILVER_LEGGINGS, "Silver Leggings");
      this.addItem(TensuraArmorItems.SILVER_BOOTS, "Silver Boots");
      this.addItem(TensuraArmorItems.ANT_CARAPACE_HELMET, "Ant Carapace Helmet");
      this.addItem(TensuraArmorItems.ANT_CARAPACE_CHESTPLATE, "Ant Carapace Chestplate");
      this.addItem(TensuraArmorItems.ANT_CARAPACE_LEGGINGS, "Ant Carapace Leggings");
      this.addItem(TensuraArmorItems.ANT_CARAPACE_BOOTS, "Ant Carapace Boots");
      this.addItem(TensuraArmorItems.SERPENT_SCALEMAIL_HELMET, "Serpent Scalemail Helmet");
      this.addItem(TensuraArmorItems.SERPENT_SCALEMAIL_CHESTPLATE, "Serpent Scalemail Chestplate");
      this.addItem(TensuraArmorItems.SERPENT_SCALEMAIL_LEGGINGS, "Serpent Scalemail Leggings");
      this.addItem(TensuraArmorItems.SERPENT_SCALEMAIL_BOOTS, "Serpent Scalemail Boots");
      this.addItem(TensuraArmorItems.LOW_MAGISTEEL_HELMET, "Low Magisteel Helmet");
      this.addItem(TensuraArmorItems.LOW_MAGISTEEL_CHESTPLATE, "Low Magisteel Chestplate");
      this.addItem(TensuraArmorItems.LOW_MAGISTEEL_LEGGINGS, "Low Magisteel Leggings");
      this.addItem(TensuraArmorItems.LOW_MAGISTEEL_BOOTS, "Low Magisteel Boots");
      this.addItem(TensuraArmorItems.ARMORSAURUS_HELMET, "Armorsaurus Helmet");
      this.addItem(TensuraArmorItems.ARMORSAURUS_CHESTPLATE, "Armorsaurus Chestplate");
      this.addItem(TensuraArmorItems.ARMORSAURUS_LEGGINGS, "Armorsaurus Leggings");
      this.addItem(TensuraArmorItems.ARMORSAURUS_BOOTS, "Armorsaurus Boots");
      this.addItem(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_HELMET, "Armorsaurus Scalemail Helmet");
      this.addItem(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_CHESTPLATE, "Armorsaurus Scalemail Chestplate");
      this.addItem(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_LEGGINGS, "Armorsaurus Scalemail Leggings");
      this.addItem(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_BOOTS, "Armorsaurus Scalemail Boots");
      this.addItem(TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_HELMET, "Knight Spider Carapace Helmet");
      this.addItem(TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_CHESTPLATE, "Knight Spider Carapace Chestplate");
      this.addItem(TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_LEGGINGS, "Knight Spider Carapace Leggings");
      this.addItem(TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_BOOTS, "Knight Spider Carapace Boots");
      this.addItem(TensuraArmorItems.HIGH_MAGISTEEL_HELMET, "High Magisteel Helmet");
      this.addItem(TensuraArmorItems.HIGH_MAGISTEEL_CHESTPLATE, "High Magisteel Chestplate");
      this.addItem(TensuraArmorItems.HIGH_MAGISTEEL_LEGGINGS, "High Magisteel Leggings");
      this.addItem(TensuraArmorItems.HIGH_MAGISTEEL_BOOTS, "High Magisteel Boots");
      this.addItem(TensuraArmorItems.CHARYBDIS_SCALEMAIL_HELMET, "Charybdis Scalemail Helmet");
      this.addItem(TensuraArmorItems.CHARYBDIS_SCALEMAIL_CHESTPLATE, "Charybdis Scalemail Chestplate");
      this.addItem(TensuraArmorItems.CHARYBDIS_SCALEMAIL_LEGGINGS, "Charybdis Scalemail Leggings");
      this.addItem(TensuraArmorItems.CHARYBDIS_SCALEMAIL_BOOTS, "Charybdis Scalemail Boots");
      this.addItem(TensuraArmorItems.MITHRIL_HELMET, "Mithril Helmet");
      this.addItem(TensuraArmorItems.MITHRIL_CHESTPLATE, "Mithril Chestplate");
      this.addItem(TensuraArmorItems.MITHRIL_LEGGINGS, "Mithril Leggings");
      this.addItem(TensuraArmorItems.MITHRIL_BOOTS, "Mithril Boots");
      this.addItem(TensuraArmorItems.ORICHALCUM_HELMET, "Orichalcum Helmet");
      this.addItem(TensuraArmorItems.ORICHALCUM_CHESTPLATE, "Orichalcum Chestplate");
      this.addItem(TensuraArmorItems.ORICHALCUM_LEGGINGS, "Orichalcum Leggings");
      this.addItem(TensuraArmorItems.ORICHALCUM_BOOTS, "Orichalcum Boots");
      this.addItem(TensuraArmorItems.PURE_MAGISTEEL_HELMET, "Pure Magisteel Helmet");
      this.addItem(TensuraArmorItems.PURE_MAGISTEEL_CHESTPLATE, "Pure Magisteel Chestplate");
      this.addItem(TensuraArmorItems.PURE_MAGISTEEL_LEGGINGS, "Pure Magisteel Leggings");
      this.addItem(TensuraArmorItems.PURE_MAGISTEEL_BOOTS, "Pure Magisteel Boots");
      this.addItem(TensuraArmorItems.ADAMANTITE_HELMET, "Adamantite Helmet");
      this.addItem(TensuraArmorItems.ADAMANTITE_CHESTPLATE, "Adamantite Chestplate");
      this.addItem(TensuraArmorItems.ADAMANTITE_LEGGINGS, "Adamantite Leggings");
      this.addItem(TensuraArmorItems.ADAMANTITE_BOOTS, "Adamantite Boots");
      this.addItem(TensuraArmorItems.HIHIIROKANE_HELMET, "Hihi'Irokane Helmet");
      this.addItem(TensuraArmorItems.HIHIIROKANE_CHESTPLATE, "Hihi'Irokane Chestplate");
      this.addItem(TensuraArmorItems.HIHIIROKANE_LEGGINGS, "Hihi'Irokane Leggings");
      this.addItem(TensuraArmorItems.HIHIIROKANE_BOOTS, "Hihi'Irokane Boots");
      this.addItem(TensuraArmorItems.HOLY_ARMAMENTS_CHESTPLATE, "Holy Armaments Chestplate");
      this.addItem(TensuraArmorItems.HOLY_ARMAMENTS_LEGGINGS, "Holy Armaments Leggings");
      this.addItem(TensuraArmorItems.HOLY_ARMAMENTS_BOOTS, "Holy Armaments Boots");
   }

   private void gearItems() {
      this.addItem(TensuraToolItems.SHORT_BOW, "Short Bow");
      this.addItem(TensuraToolItems.LONG_BOW, "Long Bow");
      this.addItem(TensuraToolItems.WAR_BOW, "War Bow");
      this.addItem(TensuraToolItems.SHORT_SPIDER_BOW, "Short Spider Bow");
      this.addItem(TensuraToolItems.SPIDER_BOW, "Spider Bow");
      this.addItem(TensuraToolItems.LONG_SPIDER_BOW, "Long Spider Bow");
      this.addItem(TensuraToolItems.WAR_SPIDER_BOW, "War Spider Bow");
      this.addItem(TensuraToolItems.ANT_CROSSBOW, "Ant Crossbow");
      this.addItem(TensuraToolItems.INVISIBLE_ARROW, "Invisible Arrow");
      this.addItem(TensuraToolItems.SPEARED_FIN_ARROW, "Speared Fin Arrow");
      this.addItem(TensuraToolItems.KUNAI, "Kunai");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_KUNAI, "Pure Magisteel Kunai");
      this.addItem(TensuraToolItems.GOBLIN_CLUB, "Goblin Club");
      this.addItem(TensuraToolItems.KANABO, "Kanabo");
      this.addItem(TensuraToolItems.ARMORSAURUS_SHIELD, "Armorsaurus Shield");
      this.addItem(TensuraToolItems.TEMPEST_SCALE_SHIELD, "Tempest Scale Shield");
      this.addItem(TensuraToolItems.TEMPEST_SCALE_SWORD, "Tempest Scale Sword");
      this.addItem(TensuraToolItems.TEMPEST_SCALE_KNIFE, "Tempest Scale Knife");
      this.addItem(TensuraToolItems.SISSIE_TOOTH_PICKAXE, "Sissie Tooth Pickaxe");
      this.addItem(TensuraToolItems.CENTIPEDE_DAGGER, "Centipede Dagger");
      this.addItem(TensuraToolItems.SPIDER_DAGGER, "Spider Dagger");
      this.addItem(TensuraToolItems.BEAST_HORN_SPEAR, "Beast Horn Spear");
      this.addItem(TensuraToolItems.UNICORN_HORN_SPEAR, "Unicorn Horn Spear");
      this.addItem(TensuraToolItems.BLADE_TIGER_SCYTHE, "Blade Tiger Scythe");
      this.addItem(TensuraToolItems.LOW_MAGIC_STAFF, "Low Magic Staff");
      this.addItem(TensuraToolItems.MEDIUM_MAGIC_STAFF, "Medium Magic Staff");
      this.addItem(TensuraToolItems.HIGH_MAGIC_STAFF, "High Magic Staff");
      this.addItem(TensuraToolItems.SLIME_STAFF, "Staff of Slime");
      this.addItem(TensuraToolItems.GRIMOIRE_D, "Grimoire (D)");
      this.addItem(TensuraToolItems.GRIMOIRE_C, "Grimoire (C)");
      this.addItem(TensuraToolItems.GRIMOIRE_B, "Grimoire (B)");
      this.addItem(TensuraToolItems.GRIMOIRE_A, "Grimoire (A)");
      this.addItem(TensuraToolItems.GRIMOIRE_SPECIAL_A, "Grimoire (Special A)");
      this.addItem(TensuraToolItems.WOODEN_SHORT_SWORD, "Wooden Short Sword");
      this.addItem(TensuraToolItems.WOODEN_LONG_SWORD, "Wooden Long Sword");
      this.addItem(TensuraToolItems.WOODEN_GREAT_SWORD, "Wooden Great Sword");
      this.addItem(TensuraToolItems.WOODEN_KATANA, "Wooden Katana");
      this.addItem(TensuraToolItems.WOODEN_KODACHI, "Wooden Kodachi");
      this.addItem(TensuraToolItems.WOODEN_TACHI, "Wooden Tachi");
      this.addItem(TensuraToolItems.WOODEN_ODACHI, "Wooden Odachi");
      this.addItem(TensuraToolItems.WOODEN_SICKLE, "Wooden Sickle");
      this.addItem(TensuraToolItems.WOODEN_SPEAR, "Wooden Spear");
      this.addItem(TensuraToolItems.WOODEN_SCYTHE, "Wooden Scythe");
      this.addItem(TensuraToolItems.STONE_SHORT_SWORD, "Stone Short Sword");
      this.addItem(TensuraToolItems.STONE_LONG_SWORD, "Stone Long Sword");
      this.addItem(TensuraToolItems.STONE_GREAT_SWORD, "Stone Great Sword");
      this.addItem(TensuraToolItems.STONE_KATANA, "Stone Katana");
      this.addItem(TensuraToolItems.STONE_KODACHI, "Stone Kodachi");
      this.addItem(TensuraToolItems.STONE_TACHI, "Stone Tachi");
      this.addItem(TensuraToolItems.STONE_ODACHI, "Stone Odachi");
      this.addItem(TensuraToolItems.STONE_SICKLE, "Stone Sickle");
      this.addItem(TensuraToolItems.STONE_SPEAR, "Stone Spear");
      this.addItem(TensuraToolItems.STONE_SCYTHE, "Stone Scythe");
      this.addItem(TensuraToolItems.GOLDEN_SHORT_SWORD, "Golden Short Sword");
      this.addItem(TensuraToolItems.GOLDEN_LONG_SWORD, "Golden Long Sword");
      this.addItem(TensuraToolItems.GOLDEN_GREAT_SWORD, "Golden Great Sword");
      this.addItem(TensuraToolItems.GOLDEN_KATANA, "Golden Katana");
      this.addItem(TensuraToolItems.GOLDEN_KODACHI, "Golden Kodachi");
      this.addItem(TensuraToolItems.GOLDEN_TACHI, "Golden Tachi");
      this.addItem(TensuraToolItems.GOLDEN_ODACHI, "Golden Odachi");
      this.addItem(TensuraToolItems.GOLDEN_SICKLE, "Golden Sickle");
      this.addItem(TensuraToolItems.GOLDEN_SPEAR, "Golden Spear");
      this.addItem(TensuraToolItems.GOLDEN_SCYTHE, "Golden Scythe");
      this.addItem(TensuraToolItems.SILVER_SWORD, "Silver Sword");
      this.addItem(TensuraToolItems.SILVER_SHORT_SWORD, "Silver Short Sword");
      this.addItem(TensuraToolItems.SILVER_LONG_SWORD, "Silver Long Sword");
      this.addItem(TensuraToolItems.SILVER_GREAT_SWORD, "Silver Great Sword");
      this.addItem(TensuraToolItems.SILVER_KATANA, "Silver Katana");
      this.addItem(TensuraToolItems.SILVER_KODACHI, "Silver Kodachi");
      this.addItem(TensuraToolItems.SILVER_TACHI, "Silver Tachi");
      this.addItem(TensuraToolItems.SILVER_ODACHI, "Silver Odachi");
      this.addItem(TensuraToolItems.SILVER_PICKAXE, "Silver Pickaxe");
      this.addItem(TensuraToolItems.SILVER_AXE, "Silver Axe");
      this.addItem(TensuraToolItems.SILVER_SHOVEL, "Silver Shovel");
      this.addItem(TensuraToolItems.SILVER_HOE, "Silver Hoe");
      this.addItem(TensuraToolItems.SILVER_SICKLE, "Silver Sickle");
      this.addItem(TensuraToolItems.SILVER_SPEAR, "Silver Spear");
      this.addItem(TensuraToolItems.SILVER_SCYTHE, "Silver Scythe");
      this.addItem(TensuraToolItems.IRON_SHORT_SWORD, "Iron Short Sword");
      this.addItem(TensuraToolItems.IRON_LONG_SWORD, "Iron Long Sword");
      this.addItem(TensuraToolItems.IRON_GREAT_SWORD, "Iron Great Sword");
      this.addItem(TensuraToolItems.IRON_KATANA, "Iron Katana");
      this.addItem(TensuraToolItems.IRON_KODACHI, "Iron Kodachi");
      this.addItem(TensuraToolItems.IRON_TACHI, "Iron Tachi");
      this.addItem(TensuraToolItems.IRON_ODACHI, "Iron Odachi");
      this.addItem(TensuraToolItems.IRON_SICKLE, "Iron Sickle");
      this.addItem(TensuraToolItems.IRON_SPEAR, "Iron Spear");
      this.addItem(TensuraToolItems.IRON_SCYTHE, "Iron Scythe");
      this.addItem(TensuraToolItems.DIAMOND_SHORT_SWORD, "Diamond Short Sword");
      this.addItem(TensuraToolItems.DIAMOND_LONG_SWORD, "Diamond Long Sword");
      this.addItem(TensuraToolItems.DIAMOND_GREAT_SWORD, "Diamond Great Sword");
      this.addItem(TensuraToolItems.DIAMOND_KATANA, "Diamond Katana");
      this.addItem(TensuraToolItems.DIAMOND_KODACHI, "Diamond Kodachi");
      this.addItem(TensuraToolItems.DIAMOND_TACHI, "Diamond Tachi");
      this.addItem(TensuraToolItems.DIAMOND_ODACHI, "Diamond Odachi");
      this.addItem(TensuraToolItems.DIAMOND_SICKLE, "Diamond Sickle");
      this.addItem(TensuraToolItems.DIAMOND_SPEAR, "Diamond Spear");
      this.addItem(TensuraToolItems.DIAMOND_SCYTHE, "Diamond Scythe");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_SWORD, "Low Magisteel Sword");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_SHORT_SWORD, "Low Magisteel Short Sword");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_LONG_SWORD, "Low Magisteel Long Sword");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_GREAT_SWORD, "Low Magisteel Great Sword");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_KATANA, "Low Magisteel Katana");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_KODACHI, "Low Magisteel Kodachi");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_TACHI, "Low Magisteel Tachi");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_ODACHI, "Low Magisteel Odachi");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_PICKAXE, "Low Magisteel Pickaxe");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_AXE, "Low Magisteel Axe");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_SHOVEL, "Low Magisteel Shovel");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_HOE, "Low Magisteel Hoe");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_SICKLE, "Low Magisteel Sickle");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_SPEAR, "Low Magisteel Spear");
      this.addItem(TensuraToolItems.LOW_MAGISTEEL_SCYTHE, "Low Magisteel Scythe");
      this.addItem(TensuraToolItems.NETHERITE_SHORT_SWORD, "Netherite Short Sword");
      this.addItem(TensuraToolItems.NETHERITE_LONG_SWORD, "Netherite Long Sword");
      this.addItem(TensuraToolItems.NETHERITE_GREAT_SWORD, "Netherite Great Sword");
      this.addItem(TensuraToolItems.NETHERITE_KATANA, "Netherite Katana");
      this.addItem(TensuraToolItems.NETHERITE_KODACHI, "Netherite Kodachi");
      this.addItem(TensuraToolItems.NETHERITE_TACHI, "Netherite Tachi");
      this.addItem(TensuraToolItems.NETHERITE_ODACHI, "Netherite Odachi");
      this.addItem(TensuraToolItems.NETHERITE_SICKLE, "Netherite Sickle");
      this.addItem(TensuraToolItems.NETHERITE_SPEAR, "Netherite Spear");
      this.addItem(TensuraToolItems.NETHERITE_SCYTHE, "Netherite Scythe");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_SWORD, "High Magisteel Sword");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_SHORT_SWORD, "High Magisteel Short Sword");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_LONG_SWORD, "High Magisteel Long Sword");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_GREAT_SWORD, "High Magisteel Great Sword");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_KATANA, "High Magisteel Katana");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_KODACHI, "High Magisteel Kodachi");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_TACHI, "High Magisteel Tachi");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_ODACHI, "High Magisteel Odachi");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_PICKAXE, "High Magisteel Pickaxe");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_AXE, "High Magisteel Axe");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_SHOVEL, "High Magisteel Shovel");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_HOE, "High Magisteel Hoe");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_SICKLE, "High Magisteel Sickle");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_SPEAR, "High Magisteel Spear");
      this.addItem(TensuraToolItems.HIGH_MAGISTEEL_SCYTHE, "High Magisteel Scythe");
      this.addItem(TensuraToolItems.MITHRIL_SWORD, "Mithril Sword");
      this.addItem(TensuraToolItems.MITHRIL_SHORT_SWORD, "Mithril Short Sword");
      this.addItem(TensuraToolItems.MITHRIL_LONG_SWORD, "Mithril Long Sword");
      this.addItem(TensuraToolItems.MITHRIL_GREAT_SWORD, "Mithril Great Sword");
      this.addItem(TensuraToolItems.MITHRIL_KATANA, "Mithril Katana");
      this.addItem(TensuraToolItems.MITHRIL_KODACHI, "Mithril Kodachi");
      this.addItem(TensuraToolItems.MITHRIL_TACHI, "Mithril Tachi");
      this.addItem(TensuraToolItems.MITHRIL_ODACHI, "Mithril Odachi");
      this.addItem(TensuraToolItems.MITHRIL_PICKAXE, "Mithril Pickaxe");
      this.addItem(TensuraToolItems.MITHRIL_AXE, "Mithril Axe");
      this.addItem(TensuraToolItems.MITHRIL_SHOVEL, "Mithril Shovel");
      this.addItem(TensuraToolItems.MITHRIL_HOE, "Mithril Hoe");
      this.addItem(TensuraToolItems.MITHRIL_SICKLE, "Mithril Sickle");
      this.addItem(TensuraToolItems.MITHRIL_SPEAR, "Mithril Spear");
      this.addItem(TensuraToolItems.MITHRIL_SCYTHE, "Mithril Scythe");
      this.addItem(TensuraToolItems.ORICHALCUM_SWORD, "Orichalcum Sword");
      this.addItem(TensuraToolItems.ORICHALCUM_SHORT_SWORD, "Orichalcum Short Sword");
      this.addItem(TensuraToolItems.ORICHALCUM_LONG_SWORD, "Orichalcum Long Sword");
      this.addItem(TensuraToolItems.ORICHALCUM_GREAT_SWORD, "Orichalcum Great Sword");
      this.addItem(TensuraToolItems.ORICHALCUM_KATANA, "Orichalcum Katana");
      this.addItem(TensuraToolItems.ORICHALCUM_KODACHI, "Orichalcum Kodachi");
      this.addItem(TensuraToolItems.ORICHALCUM_TACHI, "Orichalcum Tachi");
      this.addItem(TensuraToolItems.ORICHALCUM_ODACHI, "Orichalcum Odachi");
      this.addItem(TensuraToolItems.ORICHALCUM_PICKAXE, "Orichalcum Pickaxe");
      this.addItem(TensuraToolItems.ORICHALCUM_AXE, "Orichalcum Axe");
      this.addItem(TensuraToolItems.ORICHALCUM_SHOVEL, "Orichalcum Shovel");
      this.addItem(TensuraToolItems.ORICHALCUM_HOE, "Orichalcum Hoe");
      this.addItem(TensuraToolItems.ORICHALCUM_SICKLE, "Orichalcum Sickle");
      this.addItem(TensuraToolItems.ORICHALCUM_SPEAR, "Orichalcum Spear");
      this.addItem(TensuraToolItems.ORICHALCUM_SCYTHE, "Orichalcum Scythe");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_SWORD, "Pure Magisteel Sword");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_SHORT_SWORD, "Pure Magisteel Short Sword");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_LONG_SWORD, "Pure Magisteel Long Sword");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_GREAT_SWORD, "Pure Magisteel Great Sword");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_KATANA, "Pure Magisteel Katana");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_KODACHI, "Pure Magisteel Kodachi");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_TACHI, "Pure Magisteel Tachi");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_ODACHI, "Pure Magisteel Odachi");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_PICKAXE, "Pure Magisteel Pickaxe");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_AXE, "Pure Magisteel Axe");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_SHOVEL, "Pure Magisteel Shovel");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_HOE, "Pure Magisteel Hoe");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_SICKLE, "Pure Magisteel Sickle");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_SPEAR, "Pure Magisteel Spear");
      this.addItem(TensuraToolItems.PURE_MAGISTEEL_SCYTHE, "Pure Magisteel Scythe");
      this.addItem(TensuraToolItems.ADAMANTITE_SWORD, "Adamantite Sword");
      this.addItem(TensuraToolItems.ADAMANTITE_SHORT_SWORD, "Adamantite Short Sword");
      this.addItem(TensuraToolItems.ADAMANTITE_LONG_SWORD, "Adamantite Long Sword");
      this.addItem(TensuraToolItems.ADAMANTITE_GREAT_SWORD, "Adamantite Great Sword");
      this.addItem(TensuraToolItems.ADAMANTITE_KATANA, "Adamantite Katana");
      this.addItem(TensuraToolItems.ADAMANTITE_KODACHI, "Adamantite Kodachi");
      this.addItem(TensuraToolItems.ADAMANTITE_TACHI, "Adamantite Tachi");
      this.addItem(TensuraToolItems.ADAMANTITE_ODACHI, "Adamantite Odachi");
      this.addItem(TensuraToolItems.ADAMANTITE_PICKAXE, "Adamantite Pickaxe");
      this.addItem(TensuraToolItems.ADAMANTITE_AXE, "Adamantite Axe");
      this.addItem(TensuraToolItems.ADAMANTITE_SHOVEL, "Adamantite Shovel");
      this.addItem(TensuraToolItems.ADAMANTITE_HOE, "Adamantite Hoe");
      this.addItem(TensuraToolItems.ADAMANTITE_SICKLE, "Adamantite Sickle");
      this.addItem(TensuraToolItems.ADAMANTITE_SPEAR, "Adamantite Spear");
      this.addItem(TensuraToolItems.ADAMANTITE_SCYTHE, "Adamantite Scythe");
      this.addItem(TensuraToolItems.HIHIIROKANE_SWORD, "Hihi'Irokane Sword");
      this.addItem(TensuraToolItems.HIHIIROKANE_SHORT_SWORD, "Hihi'Irokane Short Sword");
      this.addItem(TensuraToolItems.HIHIIROKANE_LONG_SWORD, "Hihi'Irokane Long Sword");
      this.addItem(TensuraToolItems.HIHIIROKANE_GREAT_SWORD, "Hihi'Irokane Great Sword");
      this.addItem(TensuraToolItems.HIHIIROKANE_KATANA, "Hihi'Irokane Katana");
      this.addItem(TensuraToolItems.HIHIIROKANE_KODACHI, "Hihi'Irokane Kodachi");
      this.addItem(TensuraToolItems.HIHIIROKANE_TACHI, "Hihi'Irokane Tachi");
      this.addItem(TensuraToolItems.HIHIIROKANE_ODACHI, "Hihi'Irokane Odachi");
      this.addItem(TensuraToolItems.HIHIIROKANE_PICKAXE, "Hihi'Irokane Pickaxe");
      this.addItem(TensuraToolItems.HIHIIROKANE_AXE, "Hihi'Irokane Axe");
      this.addItem(TensuraToolItems.HIHIIROKANE_SHOVEL, "Hihi'Irokane Shovel");
      this.addItem(TensuraToolItems.HIHIIROKANE_HOE, "Hihi'Irokane Hoe");
      this.addItem(TensuraToolItems.HIHIIROKANE_SICKLE, "Hihi'Irokane Sickle");
      this.addItem(TensuraToolItems.HIHIIROKANE_SPEAR, "Hihi'Irokane Spear");
      this.addItem(TensuraToolItems.HIHIIROKANE_SCYTHE, "Hihi'Irokane Scythe");
   }

   private void uniqueGearItems() {
      this.addItem(TensuraArmorItems.ANTI_MAGIC_MASK, "Anti-Magic Mask");
      this.addItem(TensuraArmorItems.DARK_JACKET, "Dark Jacket");
      this.addItem(TensuraArmorItems.DARK_LEGGINGS, "Dark Leggings");
      this.addItem(TensuraArmorItems.DARK_BOOTS, "Dark Boots");
      this.addItem(TensuraArmorItems.WINGED_SHOES, "Winged Shoes");
      this.addItem(TensuraArmorItems.BAT_GLIDER, "Bat Glider");
      this.addItem(TensuraArmorItems.CRAZY_PIERROT_MASK, "Crazy Pierrot Mask");
      this.addItem(TensuraArmorItems.ANGRY_PIERROT_MASK, "Angry Pierrot Mask");
      this.addItem(TensuraArmorItems.WONDER_PIERROT_MASK, "Wonder Pierrot Mask");
      this.addItem(TensuraArmorItems.TEARY_PIERROT_MASK, "Teardrop Mask");
      this.addItem(TensuraToolItems.ORB_OF_DOMINATION, "Orb of Domination");
      this.addItem(TensuraToolItems.ARMORSAURUS_GAUNTLET, "Armorsaurus Gauntlet");
      this.addItem(TensuraToolItems.DRAGON_KNUCKLE, "Dragon Knuckle");
      this.addItem(TensuraToolItems.DEAD_END_RAINBOW, "Dead End Rainbow");
      this.addItem(TensuraToolItems.ICE_BLADE, "Ice Blade");
      this.addItem(TensuraToolItems.WALTHER_P99, "Walther P99");
      this.addItem(TensuraToolItems.MEAT_CRUSHER, "Meat Crusher");
      this.addItem(TensuraToolItems.MOONLIGHT, "Moonlight");
      this.addItem(TensuraToolItems.RUHK, "Ruhk");
      this.addItem(TensuraToolItems.SPATIAL_BLADE, "Spatial Blade");
      this.addItem(TensuraToolItems.SEVERER_BLADE, "Severer Blade");
      this.addItem(TensuraToolItems.COPPER_SHELL, "Copper Shell");
      this.addItem(TensuraToolItems.VORTEX_SPEAR, "Vortex Spear");
      this.addItem(TensuraToolItems.WEB_GUN, "Web Gun");
      this.addItem(TensuraToolItems.WEB_CARTRIDGE, "Web Cartridge");
      this.addItem(TensuraToolItems.STICKY_WEB_CARTRIDGE, "Sticky Web Cartridge");
      this.addItem(TensuraToolItems.STICKY_STEEL_WEB_CARTRIDGE, "Sticky Steel Web Cartridge");
   }

   private void consumableItems() {
      this.addItem(TensuraConsumableItems.DUBIOUS_FOOD, "Dubious Food");
      this.addItem(TensuraConsumableItems.RAW_ARMORSAURUS_MEAT, "Raw Armorsaurus Meat");
      this.addItem(TensuraConsumableItems.COOKED_ARMORSAURUS_MEAT, "Cooked Armorsaurus Meat");
      this.addItem(TensuraConsumableItems.BUCKET_OF_CATTLEDEER_MILK, "Cattledeer Milk Bucket");
      this.addItem(TensuraConsumableItems.CATTLEDEER_BEEF, "Cattledeer Beef");
      this.addItem(TensuraConsumableItems.CATTLEDEER_STEAK, "Cattledeer Steak");
      this.addItem(TensuraConsumableItems.BLADE_TIGER_STEAK, "Blade Tiger Steak");
      this.addItem(TensuraConsumableItems.RAW_BLADE_TIGER_MEAT, "Raw Blade Tiger Meat");
      this.addItem(TensuraConsumableItems.RAW_CHARYBDIS_MEAT, "Raw Charybdis Meat");
      this.addItem(TensuraConsumableItems.COOKED_CHARYBDIS_MEAT, "Cooked Charybdis Meat");
      this.addItem(TensuraConsumableItems.GIANT_ANT_LEG, "Giant Ant Leg");
      this.addItem(TensuraConsumableItems.COOKED_GIANT_ANT_LEG, "Cooked Giant Ant Leg");
      this.addItem(TensuraConsumableItems.RAW_GIANT_BAT_MEAT, "Raw Giant Bat Meat");
      this.addItem(TensuraConsumableItems.COOKED_GIANT_BAT_MEAT, "Cooked Giant Bat Meat");
      this.addItem(TensuraConsumableItems.KNIGHT_SPIDER_LEG, "Knight Spider Leg");
      this.addItem(TensuraConsumableItems.COOKED_KNIGHT_SPIDER_LEG, "Cooked Knight Spider Leg");
      this.addItem(TensuraConsumableItems.RAW_MEGALODON_MEAT, "Raw Megalodon Meat");
      this.addItem(TensuraConsumableItems.COOKED_MEGALODON_MEAT, "Cooked Megalodon Meat");
      this.addItem(TensuraConsumableItems.RAW_SERPENT_MEAT, "Raw Serpent Meat");
      this.addItem(TensuraConsumableItems.COOKED_SERPENT_MEAT, "Cooked Serpent Meat");
      this.addItem(TensuraConsumableItems.RAW_SPEAR_TORO_MEAT, "Raw Spear Toro Meat");
      this.addItem(TensuraConsumableItems.COOKED_SPEAR_TORO_MEAT, "Cooked Spear Toro Meat");
      this.addItem(TensuraConsumableItems.SPEAR_TORO_FIN, "Spear Toro Fin");
      this.addItem(TensuraConsumableItems.COOKED_SPEAR_TORO_FIN, "Cooked Spear Toro Fin");
      this.addItem(TensuraConsumableItems.RAW_SISSIE_MEAT, "Raw Sissie Meat");
      this.addItem(TensuraConsumableItems.COOKED_SISSIE_MEAT, "Cooked Sissie Meat");
      this.addItem(TensuraConsumableItems.SISSIE_FIN, "Sissie Fin");
      this.addItem(TensuraConsumableItems.COOKED_SISSIE_FIN, "Cooked Sissie Fin");
      this.addItem(TensuraConsumableItems.CHILLED_SLIME, "Chilled Slime");
      this.addItem(TensuraConsumableItems.SILVER_APPLE, "Silver Apple");
      this.addItem(TensuraConsumableItems.ENCHANTED_SILVER_APPLE, "Enchanted Silver Apple");
      this.addItem(TensuraConsumableItems.MAGIC_BOTTLE, "Magic Bottle");
      this.addItem(TensuraConsumableItems.WATER_MAGIC_BOTTLE, "Magic Bottle of Water");
      this.addItem(TensuraConsumableItems.VACUUMED_WATER_MAGIC_BOTTLE, "Vacuumed Magic Bottle of Water");
      this.addItem(TensuraConsumableItems.LOW_POTION, "Low Potion");
      this.addItem(TensuraConsumableItems.HIGH_POTION, "High Potion");
      this.addItem(TensuraConsumableItems.FULL_POTION, "Full Potion");
      this.addItem(TensuraConsumableItems.REVIVAL_ELIXIR, "Revival Elixir");
      this.addItem(TensuraConsumableItems.LOW_ARCANE_POTION, "Low Arcane Potion");
      this.addItem(TensuraConsumableItems.MEDIUM_ARCANE_POTION, "Medium Arcane Potion");
      this.addItem(TensuraConsumableItems.HIGH_ARCANE_POTION, "High Arcane Potion");
   }

   private void mobDropItems() {
      this.addItem(TensuraMobDropItems.ARMORSAURUS_SCALE, "Armorsaurus Scale");
      this.addItem(TensuraMobDropItems.ARMORSAURUS_SHELL, "Armorsaurus Shell");
      this.addItem(TensuraMobDropItems.MONSTER_LEATHER_D, "Monster Leather (D)");
      this.addItem(TensuraMobDropItems.MONSTER_LEATHER_C, "Monster Leather (C)");
      this.addItem(TensuraMobDropItems.MONSTER_LEATHER_B, "Monster Leather (B)");
      this.addItem(TensuraMobDropItems.MONSTER_LEATHER_A, "Monster Leather (A)");
      this.addItem(TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A, "Monster Leather (Special A)");
      this.addItem(TensuraMobDropItems.CHARYBDIS_SCALE, "Charybdis Scale");
      this.addItem(TensuraMobDropItems.DRAGON_PEACOCK_FEATHER, "Dragon Peacock Feather");
      this.addItem(TensuraMobDropItems.GIANT_ANT_CARAPACE, "Giant Ant Carapace");
      this.addItem(TensuraMobDropItems.GIANT_BAT_WING, "Giant Bat Wing");
      this.addItem(TensuraMobDropItems.HELL_MOTH_SILK, "Hell-Moth Silk");
      this.addItem(TensuraMobDropItems.GEHENNA_MOTH_SILK, "Gehenna-Moth Silk");
      this.addItem(TensuraMobDropItems.INSECTAR_CARAPACE, "Insectar Carapace");
      this.addItem(TensuraMobDropItems.INVISIBLE_FEATHER, "Invisible Feather");
      this.addItem(TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE, "Knight Spider Carapace");
      this.addItem(TensuraMobDropItems.SERPENT_SCALE, "Serpent Scale");
      this.addItem(TensuraMobDropItems.CENTIPEDE_STINGER, "Centipede Stinger");
      this.addItem(TensuraMobDropItems.SISSIE_TOOTH, "Sissie Tooth");
      this.addItem(TensuraMobDropItems.SPIDER_FANG, "Spider Fang");
      this.addItem(TensuraMobDropItems.BLADE_TIGER_TAIL, "Blade Tiger Tail");
      this.addItem(TensuraMobDropItems.SLIME_CHUNK, "Slime Chunk");
      this.addItem(TensuraMobDropItems.SLIME_CORE, "Slime Core");
      this.addItem(TensuraMobDropItems.STICKY_THREAD, "Sticky Thread");
      this.addItem(TensuraMobDropItems.STEEL_THREAD, "Steel Thread");
      this.addItem(TensuraMobDropItems.BEAST_HORN, "Beast Horn");
      this.addItem(TensuraMobDropItems.UNICORN_HORN, "Unicorn Horn");
      this.addItem(TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL, "Low Quality Magic Crystal");
      this.addItem(TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL, "Medium Quality Magic Crystal");
      this.addItem(TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL, "High Quality Magic Crystal");
      this.addItem(TensuraMobDropItems.DAEMON_ESSENCE, "Daemon Essence");
      this.addItem(TensuraMobDropItems.DRAGON_ESSENCE, "Dragon Essence");
      this.addItem(TensuraMobDropItems.ELEMENTAL_ESSENCE, "Elemental Essence");
      this.addItem(TensuraMobDropItems.ROYAL_BLOOD, "Royal Blood");
      this.addItem(TensuraMobDropItems.ZANE_BLOOD, "Zane Blood");
      this.addItem(TensuraMobDropItems.ORC_DISASTER_HEAD, "Orc Disaster Head");
   }

   private void miscItems() {
      this.add("tensura.map.pyramid", "Desert Explorer Map");
      this.add("tensura.map.village", "Village Map");
      this.add("tensura.map.charybdis", "Charybdis Explorer Map");
      this.add("tensura.map.dwarf_village", "Dwarf Village Map");
      this.add("tensura.map.hell_gate", "Hell Gate Explorer Map");
      this.add("tensura.map.labyrinth", "Labyrinth Explorer Map");
      this.add("tensura.map.lizardman_village", "Lizardman Village Map");
      this.addItem(TensuraMaterialItems.RAW_SILVER, "Raw Silver");
      this.addItem(TensuraMaterialItems.SILVER_NUGGET, "Silver Nugget");
      this.addItem(TensuraMaterialItems.SILVER_INGOT, "Silver Ingot");
      this.addItem(TensuraMaterialItems.MAGIC_ORE, "Magic Ore");
      this.addItem(TensuraMaterialItems.LOW_MAGISTEEL_NUGGET, "Low Magisteel Nugget");
      this.addItem(TensuraMaterialItems.LOW_MAGISTEEL_INGOT, "Low Magisteel Ingot");
      this.addItem(TensuraMaterialItems.HIGH_MAGISTEEL_NUGGET, "High Magisteel Nugget");
      this.addItem(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT, "High Magisteel Ingot");
      this.addItem(TensuraMaterialItems.MITHRIL_NUGGET, "Mithril Nugget");
      this.addItem(TensuraMaterialItems.MITHRIL_INGOT, "Mithril Ingot");
      this.addItem(TensuraMaterialItems.ORICHALCUM_NUGGET, "Orichalcum Nugget");
      this.addItem(TensuraMaterialItems.ORICHALCUM_INGOT, "Orichalcum Ingot");
      this.addItem(TensuraMaterialItems.PURE_MAGISTEEL_NUGGET, "Pure Magisteel Nugget");
      this.addItem(TensuraMaterialItems.ADAMANTITE_NUGGET, "Adamantite Nugget");
      this.addItem(TensuraMaterialItems.HIHIIROKANE_NUGGET, "Hihi'Irokane Nugget");
      this.addItem(TensuraMaterialItems.PURE_MAGISTEEL_INGOT, "Pure Magisteel Ingot");
      this.addItem(TensuraMaterialItems.ADAMANTITE_INGOT, "Adamantite Ingot");
      this.addItem(TensuraMaterialItems.HIHIIROKANE_INGOT, "Hihi'Irokane Ingot");
      this.addItem(TensuraMaterialItems.MAGIC_STONE, "Magic Stone");
      this.addItem(TensuraMaterialItems.WARP_CORE, "Warp Core");
      this.addItem(TensuraMaterialItems.ELEMENT_CORE_EMPTY, "Empty Element Core");
      this.addItem(TensuraMaterialItems.EARTH_ELEMENTAL_SHARD, "Elemental Shard (Earth)");
      this.addItem(TensuraMaterialItems.ELEMENT_CORE_EARTH, "Element Core (Earth)");
      this.addItem(TensuraMaterialItems.FIRE_ELEMENTAL_SHARD, "Elemental Shard (Fire)");
      this.addItem(TensuraMaterialItems.ELEMENT_CORE_FIRE, "Element Core (Fire)");
      this.addItem(TensuraMaterialItems.SPACE_ELEMENTAL_SHARD, "Elemental Shard (Space)");
      this.addItem(TensuraMaterialItems.ELEMENT_CORE_SPACE, "Element Core (Space)");
      this.addItem(TensuraMaterialItems.WATER_ELEMENTAL_SHARD, "Elemental Shard (Water)");
      this.addItem(TensuraMaterialItems.ELEMENT_CORE_WATER, "Element Core (Water)");
      this.addItem(TensuraMaterialItems.WIND_ELEMENTAL_SHARD, "Elemental Shard (Wind)");
      this.addItem(TensuraMaterialItems.ELEMENT_CORE_WIND, "Element Core (Wind)");
      this.addItem(TensuraMaterialItems.BLACK_FIRE_CHARGE, "Black Fire Charge");
      this.addItem(TensuraMaterialItems.DAEMON_CORE, "Daemon Core");
      this.addItem(TensuraMaterialItems.LOW_MAGISTEEL_BONE_GOLEM, "Low Magisteel Bone Golem");
      this.addItem(TensuraMaterialItems.HIGH_MAGISTEEL_BONE_GOLEM, "High Magisteel Bone Golem");
      this.addItem(TensuraMaterialItems.MITHRIL_BONE_GOLEM, "Mithril Bone Golem");
      this.addItem(TensuraMaterialItems.PURE_MAGISTEEL_BONE_GOLEM, "Pure Magisteel Bone Golem");
      this.addItem(TensuraMaterialItems.ORICHALCUM_BONE_GOLEM, "Orichalcum Bone Golem");
      this.addItem(TensuraMaterialItems.ADAMANTITE_BONE_GOLEM, "Adamantite Bone Golem");
      this.addItem(TensuraMaterialItems.HIHIIROKANE_BONE_GOLEM, "Hihi'irokane Bone Golem");
      this.addItem(TensuraMaterialItems.BRONZE_COIN, "Bronze Coin");
      this.addItem(TensuraMaterialItems.SILVER_COIN, "Silver Coin");
      this.addItem(TensuraMaterialItems.GOLD_COIN, "Gold Coin");
      this.addItem(TensuraMaterialItems.STELLAR_GOLD_COIN, "Stellar Gold Coin");
      this.addItem(TensuraMaterialItems.POUCH_D, "Coin Pouch (D)");
      this.addItem(TensuraMaterialItems.POUCH_C, "Coin Pouch (C)");
      this.addItem(TensuraMaterialItems.POUCH_B, "Coin Pouch (B)");
      this.addItem(TensuraMaterialItems.POUCH_A, "Coin Pouch (A)");
      this.addItem(TensuraMaterialItems.POUCH_SPECIAL_A, "Coin Pouch (Special A)");
      this.addItem(TensuraMaterialItems.SPATIAL_BAG, "Spatial Bag");
      this.addItem(TensuraMaterialItems.SLIME_IN_A_BUCKET, "Bucket of Slime");
      this.addItem(TensuraMaterialItems.SHADOW_STORAGE, "Shadow Storage");
      this.addItem(TensuraMaterialItems.MONSTER_SADDLE, "Monster Saddle");
      this.addItem(TensuraMaterialItems.THATCH, "Thatch");
      this.addItem(TensuraMaterialItems.HIPOKUTE_FLOWER, "Hipokute Flower");
      this.addItem(TensuraMaterialItems.HIPOKUTE_GRASS, "Hipokute Grass");
      this.addItem(TensuraMaterialItems.HIPOKUTE_SEEDS, "Hipokute Seeds");
      this.addItem(TensuraMaterialItems.MARIONETTE_HEART, "Marionette Heart");
      this.addItem(TensuraMaterialItems.MAGIC_TOME, "Magic Tome");
      this.addItem(TensuraMaterialItems.UNBOUND_TOME, "Unbound Tome");
      this.addItem(TensuraMaterialItems.BATTLEWILL_MANUAL, "Battlewill Manual");
      this.addItem(TensuraMaterialItems.RACE_RESET_SCROLL, "Race Reset Scroll");
      this.addItem(TensuraMaterialItems.SKILL_RESET_SCROLL, "Skill Reset Scroll");
      this.addItem(TensuraMaterialItems.CHARACTER_RESET_SCROLL, "Character Reset Scroll");
      this.addBannerItem(TensuraMaterialItems.DWARGON_BANNER_PATTERN, "Banner Pattern", "Dwargon");
      this.addItem(TensuraMaterialItems.MUSIC_DISC_NANODA, "Music Disc");
      this.add("jukebox_song.tensura.nanoda", "Nanoda!");
      this.addPainting(TensuraPaintingVariants.BLUMUND, "Kingdom of Blumund", "Drum Blumund");
      this.addPainting(TensuraPaintingVariants.FILTWOOD, "Kingdom of Filtwood", "Orthos");
      this.addPainting(TensuraPaintingVariants.FULBROSIA, "Harpy Queendom of Fulbrosia", "Frey");
      this.addPainting(TensuraPaintingVariants.INGRASSIA, "Kingdom of Ingrassia", "King Aegil");
      this.addPainting(TensuraPaintingVariants.LETTER_OF_CHALLENGE, "Letter of Challenge", "Rimuru Tempest & Gard Mjöllmile");
      this.addPainting(TensuraPaintingVariants.RAJA, "Kingdom of Raja", "Towa");
      this.addPainting(TensuraPaintingVariants.SCARLET_BOND, "Scarlet Bond", "Hiiro");
      this.addPainting(TensuraPaintingVariants.SUNFLOWER, "Sunflower Field", "Mitz Vah");
      this.addPainting(TensuraPaintingVariants.WORLD_MAP, "Map of Central World", "Fuse");
      this.addPainting(TensuraPaintingVariants.TEMPEST, "Jura-Tempest Federation", "Rimuru Tempest");
      this.addTrimMaterial(TensuraTrimMaterials.SILVER, "Silver Material");
      this.addTrimMaterial(TensuraTrimMaterials.LOW_MAGISTEEL, "Low Magisteel Material");
      this.addTrimMaterial(TensuraTrimMaterials.HIGH_MAGISTEEL, "High Magisteel Material");
      this.addTrimMaterial(TensuraTrimMaterials.MITHRIL, "Mithril Material");
      this.addTrimMaterial(TensuraTrimMaterials.ORICHALCUM, "Orichalcum Material");
      this.addTrimMaterial(TensuraTrimMaterials.PURE_MAGISTEEL, "Pure Magisteel Material");
      this.addTrimMaterial(TensuraTrimMaterials.ADAMANTITE, "Adamantite Material");
      this.addTrimMaterial(TensuraTrimMaterials.HIHIIROKANE, "Hihi'irokane Material");
   }

   private void schematicItems() {
      this.addItem(TensuraSmithingSchematicItems.BASIC_BOWS, "Basic Bows Schematic");
      this.addItem(TensuraSmithingSchematicItems.SPIDER_BOWS, "Spider Bows Schematic");
      this.addItem(TensuraSmithingSchematicItems.LEATHER_GEAR, "Leather Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR, "Monster Leather Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.GOLD_GEAR, "Gold Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.SILVER_GEAR, "Silver Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.IRON_GEAR, "Iron Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.ANT_CARAPACE_GEAR, "Ant Carapace Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.SERPENT_SCALEMAIL_GEAR, "Serpent Scalemail Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.DIAMOND_GEAR, "Diamond Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.KNIGHT_SPIDER_CARAPACE_GEAR, "Knight Spider Carapace Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR, "Low Magisteel Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.ARMORSAURUS_SCALEMAIL_GEAR, "Armorsaurus Scalemail Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR, "High Magisteel Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.CHARYBDIS_SCALEMAIL_GEAR, "Charybdis Scalemail Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.MITHRIL_GEAR, "Mithril Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.ORICHALCUM_GEAR, "Orichalcum Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.PURE_MAGISTEEL_GEAR, "Pure Magisteel Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.ADAMANTITE_GEAR, "Adamantite Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.HIHIIROKANE_GEAR, "Hihi'Irokane Gear Schematic");
      this.addItem(TensuraSmithingSchematicItems.ANTI_MAGIC_MASK, "Anti-Magic Mask Schematic");
      this.addItem(TensuraSmithingSchematicItems.DARK_SET, "Dark Set Schematic");
      this.addItem(TensuraSmithingSchematicItems.PIERROT_MASK, "Pierrot Mask Schematic");
      this.addItem(TensuraSmithingSchematicItems.JAPANESE_SWORD, "Japanese Sword Schematic");
      this.addItem(TensuraSmithingSchematicItems.SHORT_SWORD, "Short Sword Schematic");
      this.addItem(TensuraSmithingSchematicItems.LONG_SWORD, "Long Sword Schematic");
      this.addItem(TensuraSmithingSchematicItems.GREAT_SWORD, "Great Sword Schematic");
      this.addItem(TensuraSmithingSchematicItems.SPEAR, "Spear Schematic");
      this.addItem(TensuraSmithingSchematicItems.KUNAI, "Kunai Schematic");
      this.addItem(TensuraSmithingSchematicItems.SPATIAL_BLADE, "Spatial Blade Schematic");
      this.addItem(TensuraSmithingSchematicItems.WEB_GUN, "Web Gun Schematic");
      this.addItem(TensuraSmithingSchematicItems.SHIELD, "Shield Schematic");
      this.addItem(TensuraSmithingSchematicItems.MAGIC_STAFF, "Magic Staff Schematic");
      this.addItem(TensuraSmithingSchematicItems.HUNTING_KNIFE, "Dagger Schematic");
   }

   private void enchantments() {
      this.addEnchantment(
         TensuraEnchantments.BARRIER_PIERCING, "Barrier Piercing", "Breaks through any barrier the weapon touches and partially breaches through armors."
      );
      this.addEnchantment(TensuraEnchantments.BREATHING_SUPPORT, "Breathing Support", "Stops air bubbles from draining.");
      this.addEnchantment(TensuraEnchantments.CRUSHING, "Crushing", "Increases a weapons damage in exchange of its durability.");
      this.addEnchantment(TensuraEnchantments.ELEMENTAL_BOOST, "Elemental Boost", "Increases damage output of natural effects for each piece of gear equipped.");
      this.addEnchantment(
         TensuraEnchantments.ELEMENTAL_RESISTANCE, "Elemental Resistance", "Decreases damage input of natural effects for each piece of gear equipped."
      );
      this.addEnchantment(TensuraEnchantments.ENERGY_PROTECTION, "Energy Protection", "Reduces the amount of energy stolen from the wearer.");
      this.addEnchantment(TensuraEnchantments.ENERGY_STEAL, "Energy Steal", "Drains a small percent of EP from the target.");
      this.addEnchantment(TensuraEnchantments.HOLY_WEAPON, "Holy Weapon", "Imbues the weapon with holy damage.");
      this.addEnchantment(TensuraEnchantments.INTANGIBILITY, "Intangibility", "Completely bypasses enemies' armors and shields.");
      this.addEnchantment(TensuraEnchantments.MAGIC_WEAPON, "Magic Weapon", "Imbues the weapon with magic damage.");
      this.addEnchantment(TensuraEnchantments.MAGIC_CAPACITY, "Magic Capacity", "Allows magic items to hold more magic.");
      this.addEnchantment(TensuraEnchantments.MAGIC_PROTECTION, "Magic Protection", "Decreases damage input of magic attacks for each piece of gear equipped.");
      this.addEnchantment(
         TensuraEnchantments.MAGICULE_ABSORPTION, "Magicule Absorption", "Increases the amount of magicule the wearer absorbs from the environment."
      );
      this.addEnchantment(TensuraEnchantments.SEVERANCE, "Severance", "The target will receive damage which cannot be healed by regular means.");
      this.addEnchantment(TensuraEnchantments.SEVERANCE_PROTECTION, "Severance Protection", "Reduces the amount of severance health dealt on the wearer.");
      this.addEnchantment(TensuraEnchantments.SPIRITUAL_PROTECTION, "Spiritual Protection", "Reduces the amount of spiritual damage dealt on the wearer.");
      this.addEnchantment(TensuraEnchantments.SLOTTING, "Slotting", "Allows for elemental cores to be inserted.");
      this.addEnchantment(TensuraEnchantments.SOUL_EATER, "Soul Eater", "Imbues the weapon with spiritual damage.");
      this.addEnchantment(
         TensuraEnchantments.STURDY,
         "Sturdy",
         "Increases the weapon's damage, armor's protection and equipments durability, and makes the item invulnerable to environmental damage."
      );
      this.addEnchantment(TensuraEnchantments.SWIFT, "Swift", "Increases the weapon's damage, attack range and attack speed.");
      this.addEnchantment(TensuraEnchantments.DEAD_END_RAINBOW, "Dead End Rainbow", "Ideally reduce the target's Spiritual Health to 0 after 7 hits.");
      this.addEnchantment(TensuraEnchantments.HOLY_COAT, "Holy Coat", "Multiplies the damage against Monster targets.");
      this.addEnchantment(
         TensuraEnchantments.MAGIC_INTERFERENCE,
         "Magic Interference",
         "Reduces Magic Damage from targets weaker than you, and partially bypasses Magic Barriers of targets."
      );
      this.addEnchantment(TensuraEnchantments.TSUKUMOGAMI, "Tsukumogami", "Makes only the owner of the weapon be able to use its full potential.");
      this.addEnchantment(
         TensuraEnchantments.ENERVATION,
         "Curse of Enervation",
         "Constantly drains the vitality from the wielder and surrounding in exchange for high magical resistance and defense."
      );
      this.addEnchantment(TensuraEnchantments.LETHARGY, "Curse of Lethargy", "Reduces the amount of Energy the weapon and its wielder gain.");
      this.addEnchantment(TensuraEnchantments.SEALING, "Curse of Sealing", "Seals the items from being enchanted or taken enchantments.");
      this.addEnchantment(TensuraEnchantments.STAGNATION, "Curse of Stagnation", "Stops the item's evolution.");
      this.addEnchantment(TensuraEnchantments.RUINATION, "Curse of Ruination", "Stops the item from regenerating and consumes any XP the wielder obtains.");
      this.addEnchantment(
         TensuraEnchantments.VITALITY,
         "Blessing of Vitality",
         "Constantly raises the vitality from the wielder and surrounding while providing high magical resistance and defense."
      );
      this.addEnchantment(TensuraEnchantments.VIGOR, "Blessing of Vigor", "Increases the amount of Energy the weapon and its wielder gain.");
      this.addEnchantment(TensuraEnchantments.TRANSCENDENCE, "Blessing of Transcendence", "Allows the items to gain more engravings with EP.");
      this.addEnchantment(TensuraEnchantments.GROWTH, "Blessing of Growth", "Double the amount of Energy the weapon gains.");
      this.addEnchantment(TensuraEnchantments.RESTORATION, "Blessing of Restoration", "Boost the item's regeneration speed using EP and XP.");
   }

   private void tooltips() {
      this.add("tooltip.tensura.iceblade.tooltip.shift", "A Unique Grade Weapon which once belonged to the Magic Swordsman of Ice.");
      this.add("tooltip.tensura.sniper_pistol.tooltip.mode_magic", "Mode: §bMagic Bullet§r.");
      this.add("tooltip.tensura.sniper_pistol.tooltip.mode_physical", "Mode: §bPhysical Bullet§r.");
      this.add("tooltip.tensura.sniper_pistol.tooltip.mode", "§aShift Right-click§r to change modes.");
      this.add("tooltip.tensura.spatial_blade.tooltip.blade", "§aBlade Mode§r: §eRight-click§r to shoot out blades.");
      this.add("tooltip.tensura.spatial_blade.tooltip.hilt", "§aHilt Mode§r: §eRight-click§r to replenish blades.");
      this.add("tooltip.tensura.web_gun.projectile", "Projectile:");
      this.add("tooltip.tensura.great_sword.tooltip", "§9Only usable with both hands.");
      this.add("tooltip.tensura.long_sword.tooltip", "§9Buffed stats when used with both hands.");
      this.add("tooltip.tensura.press.shift", "Press [§eShift§r] for more Information!");
      this.add("tooltip.tensura.gear_durability_EP", "§6EP: %s/%s");
      this.add(
         "tooltip.tensura.marionette_heart",
         "A rare magic item that can turn the user into a Majin with the cost of half their max HP in damage and most of their current Magicule."
      );
      this.add("tooltip.tensura.shadow_storage.name", "Shadow: %s");
      this.add(
         "tooltip.tensura.reset_scroll.race",
         "Reset the user's Statistic, Naming status, Awakening status, Spirits, Resistances and Race along with its Intrinsic Skills."
      );
      this.add("tooltip.tensura.reset_scroll.skill", "Reset every skill of every type from the user except their Race's Intrinsic Skills.");
      this.add(
         "tooltip.tensura.reset_scroll.skill_warning",
         "This scroll will not cover your Unique SKill's MP cost. Be careful, or else you can end up with no Unique Skills."
      );
      this.add("tooltip.tensura.reset_scroll.character", "Reset everything from the user.");
      this.add(
         "tooltip.tensura.reset_scroll.penalty",
         "The gamerule [resetIncompletePenalty] is enabled, using this scroll while you haven't completed all Reset Counter requirements will result in the lost of %s reset point(s)."
      );
      this.add("tooltip.tensura.reset_scroll.not_safe", "It's not safe to reset here.");
      this.add("tooltip.tensura.reset_scroll.disable", "It's not allowed to reset at this state.");
      this.add("tooltip.tensura.reset_scroll.disable.name", "It's not allowed to reset %s at the current state.");
      this.add("tooltip.tensura.orc_disaster_head", "Trophy...?");
      this.add("tooltip.tensura.charybdis_core.inactive", "Inactive");
      this.add("tooltip.tensura.charybdis_core.active", "Active");
      this.add("tooltip.tensura.charybdis_core.inert", "Inert");
      this.add("tooltip.tensura.spell_cast_item.list", "Used Magic:");
      this.add("tooltip.tensura.spell_cast_item.slime_summon", "Summon Slime");
      this.add("tooltip.tensura.spell_cast_item.slime_summon.summon", "Summon");
      this.add("tooltip.tensura.spell_cast_item.slime_summon.control", "Control");
   }

   private void effects() {
      this.add("effect.tensura.insanity.voices", "The voices won't let you sleep...");
      this.add("effect.tensura.insanity.gaze", "You feel a cold gaze...");
      this.add("effect.tensura.insanity.fear", "You awaken in fear...");
      this.add("effect.tensura.insanity.unknown", "There is something in the dark...");
      this.addPotionItems("glowing", "Glowing");
      this.addPotionItems("hypnotic_efficiency", "Hypnotic Efficiency");
      this.addPotionItems("night_owl", "Night owl");
      this.addEffectAndPotions((MobEffect)TensuraMobEffects.CHILL.get(), "Chill");
      this.addEffectAndPotions((MobEffect)TensuraMobEffects.CORROSION.get(), "Corrosion");
      this.addEffectAndPotions((MobEffect)TensuraMobEffects.FATAL_POISON.get(), "Fatal Poison");
      this.addEffectAndPotions((MobEffect)TensuraMobEffects.FRAGILITY.get(), "Fragility");
      this.addEffectAndPotions((MobEffect)TensuraMobEffects.HYPNOSIS.get(), "Hypnosis");
      this.addEffectAndPotions((MobEffect)TensuraMobEffects.PARALYSIS.get(), "Paralysis");
      this.add((MobEffect)TensuraMobEffects.BURDEN.get(), "Burden");
      this.add((MobEffect)TensuraMobEffects.CURSE.get(), "Curse");
      this.add((MobEffect)TensuraMobEffects.FEAR.get(), "Fear");
      this.add((MobEffect)TensuraMobEffects.FROST.get(), "Frost");
      this.add((MobEffect)TensuraMobEffects.HOLY_DAMAGE.get(), "Holy Damage");
      this.add((MobEffect)TensuraMobEffects.ILLUSION_BOOST.get(), "Illusion Boost");
      this.add((MobEffect)TensuraMobEffects.INFECTION.get(), "Infection");
      this.add((MobEffect)TensuraMobEffects.INSANITY.get(), "Insanity");
      this.add((MobEffect)TensuraMobEffects.MAGICULE_POISON.get(), "Magicule Poison");
      this.add((MobEffect)TensuraMobEffects.MAGICULE_REGENERATION.get(), "Magicule Regeneration");
      this.add((MobEffect)TensuraMobEffects.PETRIFICATION.get(), "Petrification");
      this.add((MobEffect)TensuraMobEffects.SILENCE.get(), "Silence");
      this.add((MobEffect)TensuraMobEffects.SLEEP.get(), "Sleep");
      this.add((MobEffect)TensuraMobEffects.RAMPAGE.get(), "Rampage");
      this.add((MobEffect)TensuraMobEffects.WEBBED.get(), "Webbed");
      this.add((MobEffect)TensuraMobEffects.ALLY_BOOST.get(), "Ally Boost");
      this.add((MobEffect)TensuraMobEffects.AUDITORY_SENSE.get(), "Auditory Sense");
      this.add((MobEffect)TensuraMobEffects.AURA_SWORD.get(), "Aura Sword");
      this.add((MobEffect)TensuraMobEffects.BATS_MODE.get(), "Bats Mode");
      this.add((MobEffect)TensuraMobEffects.BEAST_TRANSFORMATION.get(), "Beast Transformation");
      this.add((MobEffect)TensuraMobEffects.DIAMOND_PATH.get(), "Diamond Path");
      this.add((MobEffect)TensuraMobEffects.DRAGON_MODE.get(), "Dragon Mode");
      this.add((MobEffect)TensuraMobEffects.EARTH_LOCK.get(), "Earth Lock");
      this.add((MobEffect)TensuraMobEffects.ENEMY_SEARCH.get(), "Enemy Search");
      this.add((MobEffect)TensuraMobEffects.ENGORGEMENT.get(), "Engorgement");
      this.add((MobEffect)TensuraMobEffects.FALSIFIER.get(), "Falsifier");
      this.add((MobEffect)TensuraMobEffects.FATE_CHANGE.get(), "Fate Change");
      this.add((MobEffect)TensuraMobEffects.FUTURE_VISION.get(), "Future Vision");
      this.add((MobEffect)TensuraMobEffects.GUARDED.get(), "Guarded");
      this.add((MobEffect)TensuraMobEffects.HAKI_COAT.get(), "Haki Coat");
      this.add((MobEffect)TensuraMobEffects.HEALTHCARE.get(), "Healthcare");
      this.add((MobEffect)TensuraMobEffects.INSPIRATION.get(), "Inspiration");
      this.add((MobEffect)TensuraMobEffects.INSTANT_REGENERATION.get(), "Instant Regeneration");
      this.add((MobEffect)TensuraMobEffects.LUST_DRAIN.get(), "Lust Drain");
      this.add((MobEffect)TensuraMobEffects.MAD_OGRE.get(), "Mad Ogre");
      this.add((MobEffect)TensuraMobEffects.MAGIC_AURA.get(), "Magic Aura");
      this.add((MobEffect)TensuraMobEffects.MAGIC_BARRIER.get(), "Magic Barrier");
      this.add((MobEffect)TensuraMobEffects.MAGIC_ELEMENTAL_TRANSFORMATION.get(), "Magic Elemental Transformation");
      this.add((MobEffect)TensuraMobEffects.OGRE_BERSERKER.get(), "Ogre Berserker");
      this.add((MobEffect)TensuraMobEffects.OGRE_GUILLOTINE.get(), "Ogre Guillotine");
      this.add((MobEffect)TensuraMobEffects.PHYSICAL_BARRIER.get(), "Physical Barrier");
      this.add((MobEffect)TensuraMobEffects.PRESENCE_CONCEALMENT.get(), "Presence Concealment");
      this.add((MobEffect)TensuraMobEffects.PRESENCE_SENSE.get(), "Presence Sense");
      this.add((MobEffect)TensuraMobEffects.PROTECTION.get(), "Protection");
      this.add((MobEffect)TensuraMobEffects.REINFORCEMENT.get(), "Reinforcement");
      this.add((MobEffect)TensuraMobEffects.REST.get(), "Rest");
      this.add((MobEffect)TensuraMobEffects.SELF_REGENERATION.get(), "Self-Regeneration");
      this.add((MobEffect)TensuraMobEffects.SEVERANCE_BLADE.get(), "Severance Blade");
      this.add((MobEffect)TensuraMobEffects.SHADOW_STEP.get(), "Shadow Step");
      this.add((MobEffect)TensuraMobEffects.SPEARHEAD.get(), "Spearhead");
      this.add((MobEffect)TensuraMobEffects.STRENGTHEN.get(), "Strengthen");
      this.add((MobEffect)TensuraMobEffects.WARPING.get(), "Warping");
      this.add((MobEffect)TensuraMobEffects.WIND_PROTECTION.get(), "Wind's Protection");
      this.add((MobEffect)TensuraMobEffects.ANTI_SKILL.get(), "Anti-Skill");
      this.add((MobEffect)TensuraMobEffects.ANTI_MAGIC.get(), "Anti-Magic");
      this.add((MobEffect)TensuraMobEffects.ANTI_SHOCK.get(), "Anti-Shock");
      this.add((MobEffect)TensuraMobEffects.BLACK_BURN.get(), "Black Burn");
      this.add((MobEffect)TensuraMobEffects.CONFUSION.get(), "Confusion");
      this.add((MobEffect)TensuraMobEffects.DISINTEGRATING.get(), "Disintegrating");
      this.add((MobEffect)TensuraMobEffects.ENERGY_BLOCKADE.get(), "Energy Blockade");
      this.add((MobEffect)TensuraMobEffects.DROWSINESS.get(), "Drowsiness");
      this.add((MobEffect)TensuraMobEffects.INFINITE_IMPRISONMENT.get(), "Infinite Imprisonment");
      this.add((MobEffect)TensuraMobEffects.LUST_EMBRACEMENT.get(), "Lust Embracement");
      this.add((MobEffect)TensuraMobEffects.MAGIC_INTERFERENCE.get(), "Magic Interference");
      this.add((MobEffect)TensuraMobEffects.MIND_CONTROL.get(), "Mind Control");
      this.add((MobEffect)TensuraMobEffects.MOVEMENT_INTERFERENCE.get(), "Movement Interference");
      this.add((MobEffect)TensuraMobEffects.OPPRESSION.get(), "Oppression");
      this.add((MobEffect)TensuraMobEffects.SOUL_DRAIN.get(), "Soul Drain");
      this.add((MobEffect)TensuraMobEffects.SPATIAL_BLOCKADE.get(), "Spatial Blockade");
      this.add((MobEffect)TensuraMobEffects.FLASHED_BLINDNESS.get(), "Flashed Blindness");
      this.add((MobEffect)TensuraMobEffects.TRUE_BLINDNESS.get(), "True Blindness");
   }

   private void entitiesAndSpawnEggs() {
      this.add("entity.minecraft.npc.tensura.magic_trainer", "Magic Trainer");
      this.addEntity(HumanEntityTypes.BONE_GOLEM, "Bone Golem");
      this.addEntity(HumanEntityTypes.CLONE, "Clone");
      this.addEntity(HumanEntityTypes.TRAINING_DUMMY, "Training Dummy");
      this.addEntityAndSpawnEgg(HumanEntityTypes.DWARF, "Dwarf");
      this.addEntityAndSpawnEgg(HumanEntityTypes.GAZEL_DWARGO, "Gazel Dwargo");
      this.addEntity(HumanEntityTypes.FALMUTH_KNIGHT, "Falmuth Knight");
      this.addEntityAndSpawnEgg(HumanEntityTypes.FOLGEN, "Folgen");
      this.addEntityAndSpawnEgg(HumanEntityTypes.HINATA_SAKAGUCHI, "Hinata Sakaguchi");
      this.addEntityAndSpawnEgg(HumanEntityTypes.KIRARA_MIZUTANI, "Kirara Mizutani");
      this.addEntityAndSpawnEgg(HumanEntityTypes.KYOYA_TACHIBANA, "Kyoya Tachibana");
      this.addEntityAndSpawnEgg(HumanEntityTypes.MAI_FURUKI, "Mai Furuki");
      this.addEntityAndSpawnEgg(HumanEntityTypes.MARK_LAUREN, "Mark Lauren");
      this.addEntityAndSpawnEgg(HumanEntityTypes.SHINJI_TANIMURA, "Shinji Tanimura");
      this.addEntityAndSpawnEgg(HumanEntityTypes.SHIN_RYUSEI, "Shin Ryusei");
      this.addEntityAndSpawnEgg(HumanEntityTypes.SHIZU, "Shizu");
      this.addEntityAndSpawnEgg(HumanEntityTypes.SHOGO_TAGUCHI, "Shogo Taguchi");
      this.addEntityAndSpawnEgg(HumanEntityTypes.SKELETON, "Skeleton");
      this.addEntityAndSpawnEgg(HumanEntityTypes.ZOMBIE, "Zombie");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.AKASH, "Akash");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.AQUA_FROG, "Aqua Frog");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.ARCH_DAEMON, "Arch Daemon");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.ARMORSAURUS, "Armorsaurus");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.ARMY_WASP, "Army Wasp");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.BARGHEST, "Barghest");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.BASILISK, "Basilisk");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.BEAST_GNOME, "Beast Gnome");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.BLACK_SPIDER, "Black Spider");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.BLADE_TIGER, "Blade Tiger");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.CATTLEDEER, "Cattledeer");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.CHARYBDIS, "Charybdis");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.DIREWOLF, "Direwolf");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.DRAGON_PEACOCK, "Dragon Peacock");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.EVIL_CENTIPEDE, "Evil Centipede");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.ELEMENTAL_COLOSSUS, "Elemental Colossus");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.EVIL_CENTIPEDE_BODY, "Evil Centipede");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.FEATHERED_SERPENT, "Feathered Serpent");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.GIANT_ANT, "Giant Ant");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.GIANT_BAT, "Giant Bat");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.GIANT_BEAR, "Giant Bear");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.GIANT_COD, "Giant Cod");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.GIANT_SALMON, "Giant Salmon");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.GOBLIN, "Goblin");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.GREATER_DAEMON, "Greater Daemon");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.HELL_CATERPILLAR, "Hell Caterpillar");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.HELL_MOTH, "Hell Moth");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.HORNED_BEAR, "Horned Bear");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.HORNED_RABBIT, "Horned Rabbit");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.HOUND_DOG, "Hound Dog");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.HOVER_LIZARD, "Hover Lizard");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.IFRIT, "Ifrit");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.IFRIT_CLONE, "Ifrit");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.KNIGHT_SPIDER, "Knight Spider");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.LANDFISH, "Landfish");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.LEECH_LIZARD, "Leech Lizard");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.LIZARDMAN, "Lizardman");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.MEGALODON, "Megalodon");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.LESSER_DAEMON, "Lesser Daemon");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.ONE_EYED_OWL, "One-eyed Owl");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.ORC, "Orc");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.ORC_LORD, "Orc Lord");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.ORC_DISASTER, "Orc Disaster");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.PEGASUS, "Pegasus");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.PEGACORN, "Pegacorn");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.PHANTASPORE, "Phantaspore");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.SALAMANDER, "Salamander");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.SISSIE, "Sissie");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.SLIME, "Slime");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.METAL_SLIME, "Metal Slime");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.SUPERMASSIVE_SLIME, "Supermassive Slime");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.SPEAR_TORO, "Spear Toro");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.SYLPHIDE, "Sylphide");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.UNDINE, "Undine");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.UNICORN, "Unicorn");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.TEMPEST_SERPENT, "Tempest Serpent");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.TEMPEST_SERPENT_BODY, "Tempest Serpent");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.WAR_GNOME, "War Gnome");
      this.addEntityAndSpawnEgg(MonsterEntityTypes.WINGED_CAT, "Winged Cat");
   }

   private void entities() {
      this.addEntity(MiscEntityTypes.BOAT_ENTITY, "Boat");
      this.addEntity(MiscEntityTypes.CHEST_BOAT_ENTITY, "Boat with Chest");
      this.addEntity(MiscEntityTypes.FISHING_HOOK, "Fishing Hook");
      this.addEntity(MiscEntityTypes.MAGIC_CIRCLE, "Magic Circle");
      this.addEntity(MiscEntityTypes.EXPLOSION_CIRCLE, "Explosion Circle");
      this.addEntity(MiscEntityTypes.AIR_JAIL, "Air Jail");
      this.addEntity(MiscEntityTypes.ANTI_MAGIC_AREA, "Anti-Magic Area");
      this.addEntity(MiscEntityTypes.ANTI_SHOCK_AREA, "Anti-Shock Area");
      this.addEntity(MiscEntityTypes.ACID_RAIN, "Acid Rain");
      this.addEntity(MiscEntityTypes.BLIZZARD, "Blizzard");
      this.addEntity(MiscEntityTypes.BOSS_BARRIER, "Boss Barrier");
      this.addEntity(MiscEntityTypes.DARK_CUBE, "Dark Cube");
      this.addEntity(MiscEntityTypes.DISINTEGRATION, "Disintegration");
      this.addEntity(MiscEntityTypes.EARTH_STORM, "Earth Storm");
      this.addEntity(MiscEntityTypes.FIRE_JAIL, "Fire Jail");
      this.addEntity(MiscEntityTypes.FLARE_CIRCLE, "Flare Circle");
      this.addEntity(MiscEntityTypes.HEALING_RAIN, "Healing Rain");
      this.addEntity(MiscEntityTypes.HEAT_STORM, "Heat Storm");
      this.addEntity(MiscEntityTypes.HOLY_FIELD, "Holy Field");
      this.addEntity(MiscEntityTypes.MEGIDDO_BUBBLE, "Megiddo Bubble");
      this.addEntity(MiscEntityTypes.RANGED_BARRIER, "Barrier");
      this.addEntity(MiscEntityTypes.THUNDER_RAIN, "Thunder Rain");
      this.addEntity(MiscEntityTypes.WATER_JAIL, "Water Jail");
      this.addEntity(MiscEntityTypes.BLACK_LIGHTNING_BLAST, "Black Lightning Blast");
      this.addEntity(MiscEntityTypes.BLOOD_RAY, "Blood Ray");
      this.addEntity(MiscEntityTypes.DARKNESS_CANNON, "Darkness Cannon");
      this.addEntity(MiscEntityTypes.ELECTRO_BLAST, "Electro Blast");
      this.addEntity(MiscEntityTypes.SOLAR_BEAM, "Solar Beam");
      this.addEntity(MiscEntityTypes.SPATIAL_RAY, "Spatial Ray");
      this.addEntity(MiscEntityTypes.PREDATOR_MIST, "Predator Mist");
      this.addEntity(MiscEntityTypes.GOURMET_MIST, "Gourmet Mist");
      this.addEntity(MiscEntityTypes.GLUTTONY_MIST, "Gluttony Mist");
      this.addEntity(MiscEntityTypes.BLACK_FLAME_BREATH, "Black Flame Breath");
      this.addEntity(MiscEntityTypes.FLAME_BREATH, "Flame Breath");
      this.addEntity(MiscEntityTypes.ICE_BREATH, "Ice Breath");
      this.addEntity(MiscEntityTypes.PARALYSING_BREATH, "Paralysing Breath");
      this.addEntity(MiscEntityTypes.THUNDER_BREATH, "Thunder Breath");
      this.addEntity(MiscEntityTypes.WATER_BREATH, "Water Blow");
      this.addEntity(MiscEntityTypes.WIND_BREATH, "Wind Blow");
      this.addEntity(MiscEntityTypes.CURSE_BIND_HANDS, "Curse Bind Hands");
      this.addEntity(MiscEntityTypes.DEATH_BLESSING, "Death Blessing");
      this.addEntity(MiscEntityTypes.BLOOD_MIST, "Blood Mist");
      this.addEntity(MiscEntityTypes.GRAVITY_FIELD, "Gravity Field");
      this.addEntity(MiscEntityTypes.FIRE_STORM, "Fire Storm");
      this.addEntity(MiscEntityTypes.HAKI_FIELD, "Haki");
      this.addEntity(MiscEntityTypes.SACRED_HAKI_FIELD, "Sacred Haki");
      this.addEntity(MiscEntityTypes.HELLFIRE, "Hellfire");
      this.addEntity(MiscEntityTypes.HELL_FLARE, "Hell Flare");
      this.addEntity(MiscEntityTypes.HELL_FLARE_LIMITED, "Hell Flare Limited");
      this.addEntity(MiscEntityTypes.MAGIC_EXPLOSION, "Magic Explosion");
      this.addEntity(MiscEntityTypes.MIASMIC_MIST, "Miasmic Mist");
      this.addEntity(MiscEntityTypes.MARIONETTE_LINES, "Marionette Lines");
      this.addEntity(MiscEntityTypes.MUD_HANDS, "Mud Hands");
      this.addEntity(MiscEntityTypes.SHADOW_BIND_HANDS, "Shadow Bind Hands");
      this.addEntity(MiscEntityTypes.SLEEP_MIST, "Sleep Mist");
      this.addEntity(MiscEntityTypes.LIGHTNING_BOLT, "Lightning Bolt");
      this.addEntity(MiscEntityTypes.BLACK_LIGHTNING_BOLT, "Black Lightning Bolt");
      this.addEntity(MiscEntityTypes.EARTH_SPIKE, "Earth Spike");
      this.addEntity(MiscEntityTypes.ICICLE_SPIKE, "Icicle Spike");
      this.addEntity(MiscEntityTypes.MUD_SPIKE, "Mud Spike");
      this.addEntity(MiscEntityTypes.EARTH_PILLAR, "Earth Pillar");
      this.addEntity(MiscEntityTypes.FIRE_PILLAR, "Fire Pillar");
      this.addEntity(MiscEntityTypes.ICE_PILLAR, "Ice Pillar");
      this.addEntity(MiscEntityTypes.AURA_SHIELD, "Aura Shield");
      this.addEntity(MiscEntityTypes.MAGIC_SHIELD, "Magic Shield");
      this.addEntity(MiscEntityTypes.DEATH_TORNADO, "Death Tornado");
      this.addEntity(MiscEntityTypes.LANDMINE, "Landmine");
      this.addEntity(MiscEntityTypes.CHARYBDIS_CORE, "Charybdis Core");
      this.addEntity(MiscEntityTypes.FALLING_BLOCK, "Falling Block");
      this.addEntity(MiscEntityTypes.MAD_ORBS, "Mad Orbs");
      this.addEntity(MiscEntityTypes.SUMMONING_BEAM, "Summoning Beam");
      this.addEntity(MiscEntityTypes.WARP_PORTAL, "Warp Portal");
      this.addEntity(MiscEntityTypes.HAZY_BLOSSOM, "Hazy Blossom");
      this.addEntity(ProjectileEntityTypes.BULLET, "Bullet");
      this.addEntity(ProjectileEntityTypes.MONSTER_SPIT, "Monster Spit");
      this.addEntity(ProjectileEntityTypes.HEALING_POTION, "Healing Potion");
      this.addEntity(ProjectileEntityTypes.HOLY_WATER, "Holy Water");
      this.addEntity(ProjectileEntityTypes.KUNAI, "Kunai");
      this.addEntity(ProjectileEntityTypes.SEVERER_BLADE, "Severer Blade");
      this.addEntity(ProjectileEntityTypes.SPEAR, "Spear");
      this.addEntity(ProjectileEntityTypes.INVISIBLE_ARROW, "Invisible Arrow");
      this.addEntity(ProjectileEntityTypes.SPEARED_FIN_ARROW, "Speared Fin Arrow");
      this.addEntity(ProjectileEntityTypes.UNICORN_HORN, "Unicorn Horn");
      this.addEntity(ProjectileEntityTypes.THROWN_ITEM, "Thrown Item");
      this.addEntity(ProjectileEntityTypes.WEB_BULLET, "Web Bullet");
      this.addEntity(ProjectileEntityTypes.SNIPER_GRENADE, "Sniper Grenade");
      this.addEntity(ProjectileEntityTypes.AURA_SLASH, "Aura Slash");
      this.addEntity(ProjectileEntityTypes.ACID_BALL, "Acid Ball");
      this.addEntity(ProjectileEntityTypes.AURA_BULLET, "Aura Bullet");
      this.addEntity(ProjectileEntityTypes.BLACK_FLAME_BALL, "Black Flame Ball");
      this.addEntity(ProjectileEntityTypes.BOG_SHOT, "Bog Shot");
      this.addEntity(ProjectileEntityTypes.BOULDER_SHOT, "Boulder Shot");
      this.addEntity(ProjectileEntityTypes.CHAOS_EATER, "Chaos Eater");
      this.addEntity(ProjectileEntityTypes.DIMENSION_CUT, "Dimension Cut");
      this.addEntity(ProjectileEntityTypes.FIRE_BALL, "Fire Ball");
      this.addEntity(ProjectileEntityTypes.FIRE_BOLT, "Fire Bolt");
      this.addEntity(ProjectileEntityTypes.FIRE_LANCE, "Fire Lance");
      this.addEntity(ProjectileEntityTypes.FLAME_ORB, "Flame Orb");
      this.addEntity(ProjectileEntityTypes.FLAME_SPHERE, "Flame Sphere");
      this.addEntity(ProjectileEntityTypes.FLOAT_SPHERE, "Float Sphere");
      this.addEntity(ProjectileEntityTypes.FROST_BALL, "Frost Ball");
      this.addEntity(ProjectileEntityTypes.FUSIONIST_PROJECTILE, "Fusionist Stone");
      this.addEntity(ProjectileEntityTypes.GRAVITY_SPHERE, "Gravity Sphere");
      this.addEntity(ProjectileEntityTypes.HEAT_SPHERE, "Heat Sphere");
      this.addEntity(ProjectileEntityTypes.HELL_FLARE_PROJECTILE, "Hell Flare");
      this.addEntity(ProjectileEntityTypes.ICE_LANCE, "Ice Lance");
      this.addEntity(ProjectileEntityTypes.INVISIBLE_FIRE_BOLT, "Invisible Fire Bolt");
      this.addEntity(ProjectileEntityTypes.LIGHT_ARROW, "Light Arrow");
      this.addEntity(ProjectileEntityTypes.LIGHTNING_LANCE, "Lightning Lance");
      this.addEntity(ProjectileEntityTypes.LIGHTNING_SPHERE, "Lightning Sphere");
      this.addEntity(ProjectileEntityTypes.MAGMA_SHOT, "Magma Shot");
      this.addEntity(ProjectileEntityTypes.MUD_SHOT, "Mud Shot");
      this.addEntity(ProjectileEntityTypes.OBSIDIAN_SHOT, "Obsidian Shot");
      this.addEntity(ProjectileEntityTypes.PLASMA_BALL, "Plasma Ball");
      this.addEntity(ProjectileEntityTypes.POISON_BALL, "Poison Ball");
      this.addEntity(ProjectileEntityTypes.POISON_CUTTER, "Poison Cutter");
      this.addEntity(ProjectileEntityTypes.SEVERANCE_CUTTER, "Severance Cutter");
      this.addEntity(ProjectileEntityTypes.SOLAR_GRENADE, "Solar Grenade");
      this.addEntity(ProjectileEntityTypes.SPACE_CUT, "Space Cut");
      this.addEntity(ProjectileEntityTypes.SPATIAL_ARROW, "Spatial Arrow");
      this.addEntity(ProjectileEntityTypes.STEAM_BALL, "Steam Ball");
      this.addEntity(ProjectileEntityTypes.STONE_SHOT, "Stone Shot");
      this.addEntity(ProjectileEntityTypes.TEMPEST_SCALE, "Tempest Scale");
      this.addEntity(ProjectileEntityTypes.REFLECTOR_ECHO, "Reflector Echo");
      this.addEntity(ProjectileEntityTypes.THUNDER_LANCE, "Thunder Lance");
      this.addEntity(ProjectileEntityTypes.THUNDER_SPHERE, "Thunder Sphere");
      this.addEntity(ProjectileEntityTypes.WATER_BALL, "Water Ball");
      this.addEntity(ProjectileEntityTypes.WIND_BLADE, "Wind Blade");
      this.addEntity(ProjectileEntityTypes.WIND_SPHERE, "Wind Sphere");
      this.addEntity(ProjectileEntityTypes.WIND_TORNADO, "Wind Tornado");
   }

   private void death() {
      this.addDeathMessage(TensuraDamageTypes.DARKNESS_ELEMENTAL, "%1$s was sent to the abyss", "%1$s was sent to the abyss by %2$s");
      this.addDeathMessage(TensuraDamageTypes.EARTH_ELEMENTAL, "%1$s was buried in the ground", "%1$s was buried in the ground by %2$s");
      this.addDeathMessage(TensuraDamageTypes.FIRE_ELEMENTAL, "%1$s was incinerated", "%1$s was incinerated by %2$s");
      this.addDeathMessage(TensuraDamageTypes.GRAVITY_ELEMENTAL, "%1$s was crushed under the pressure", "%1$s was crushed under the pressure by %2$s");
      this.addDeathMessage(TensuraDamageTypes.ICE_ELEMENTAL, "%1$s was frozen to death", "%1$s was frozen to death by %2$s");
      this.addDeathMessage(TensuraDamageTypes.LIGHT_ELEMENTAL, "%1$s was dispersed under the light", "%1$s was dispersed under %2$s's light");
      this.addDeathMessage(TensuraDamageTypes.LIGHTNING_ELEMENTAL, "%1$s was electrocuted", "%1$s was electrocuted by %2$s");
      this.addDeathMessage(TensuraDamageTypes.SPACE_ELEMENTAL, "%1$s was sliced into pieces", "%1$s was sliced into pieces by %2$s");
      this.addDeathMessage(TensuraDamageTypes.WATER_ELEMENTAL, "%1$s couldn't handle the water's pressure", "%1$s couldn't handle the water's pressure by %2$s");
      this.addDeathMessage(TensuraDamageTypes.WIND_ELEMENTAL, "%1$s was swept away", "%1$s was swept away by %2$s");
      this.addDeathMessage(TensuraDamageTypes.BULLET, "%1$s was shot", "%1$s was shot by %2$s");
      this.addDeathMessage(TensuraDamageTypes.BULLET_MAGIC, "%1$s was shot with a magic bullet", "%1$s was shot by %2$s with a magic bullet");
      this.addDeathMessage(TensuraDamageTypes.KUNAI, "%1$s was pierced by a kunai", "%1$s was pierced by a kunai from %2$s");
      this.addDeathMessage(TensuraDamageTypes.SEVERER_BLADE, "%1$s was stricken by a flying blade", "%1$s was stricken by a flying blade from %2$s");
      this.addDeathMessage(TensuraDamageTypes.SPEAR, "%1$s was speared", "%1$s was speared by %2$s");
      this.addDeathMessage(TensuraDamageTypes.TEMPEST_SCALE, "%1$s was pierced by a tempest scale", "%1$s was pierced by a tempest scale from %2$s");
      this.addDeathMessage(TensuraDamageTypes.UNICORN_HORN, "%1$s was turned into glitter", "%1$s was turned into glitter by %2$s");
      this.addDeathMessage(TensuraDamageTypes.CURSE, "%1$s died of curse", "%1$s was cursed by %2$s");
      this.addDeathMessage(TensuraDamageTypes.MAGICULE_POISON, "%1$s died of Magicule Poison", "%1$s died of Magicule Poison by %2$s");
      this.addDeathMessage(TensuraDamageTypes.SUFFOCATE, "%1$s suffocated", "%1$s was suffocated by %2$s");
      this.addDeathMessage(TensuraDamageTypes.CORROSION, "%1$s corroded to death", "%1$s corroded to death by %2$s");
      this.addDeathMessage(TensuraDamageTypes.FATAL_POISON, "%1$s died of poison", "%1$s died of %2$s's poison");
      this.addDeathMessage(TensuraDamageTypes.FEAR, "%1$s died of fear", "%1$s was scared to death by %2$s");
      this.addDeathMessage(TensuraDamageTypes.HOLY_DAMAGE, "%1$s was purged from this world", "%1$s was purged from this world by %2$s");
      this.addDeathMessage(TensuraDamageTypes.INSANITY, "%1$s crazed to death", "%1$s crazed to death by %2$s");
      this.addDeathMessage(TensuraDamageTypes.INFECTION, "%1$s could not find the cure", "%1$s could not find the cure for %2$s's virus");
      this.addDeathMessage(TensuraDamageTypes.PETRIFICATION, "%1$s was turned into stone", "%1$s was turned into stone by %2$s");
      this.addDeathMessage(TensuraDamageTypes.SOUL_SCATTER, "%1$s's soul was scattered", "%1$s's soul was scattered by %2$s");
      this.addDeathMessage(TensuraDamageTypes.AURA_BULLET, "%1$s was shot by an Aura Bullet", "%1$s was shot by %2$s's Aura");
      this.addDeathMessage(TensuraDamageTypes.AURA_SLASH, "%1$s was sliced by an Aura Blade", "%1$s was sliced by %2$s's Aura");
      this.addDeathMessage(TensuraDamageTypes.BLACK_FLAME, "%1$s was scorched by the Flame of Hell", "%1$s %1$s was scorched by %2$s's Flame of Hell");
      this.addDeathMessage(
         TensuraDamageTypes.BLACK_LIGHTNING, "%1$s was obliterated into dust by Black Lightning", "%1$s was obliterated into dust by %2$s's Black Lightning"
      );
      this.addDeathMessage(TensuraDamageTypes.BLOOD_DRAIN, "%1$s was out of blood", "%1$s's blood was drained by %2$s");
      this.addDeathMessage(TensuraDamageTypes.BLOOD_RAY, "%1$s was pierced through by a Blood Ray", "%1$s was pierced through by %2$s's Blood Ray");
      this.addDeathMessage(TensuraDamageTypes.BURN, "%1$s was burnt alive", "%1$s was burnt alive by %2$s");
      this.addDeathMessage(TensuraDamageTypes.DEATH_TORNADO, "%1$s saw the eye of the tornado", "%1$s saw the eye of %2$s's tornado");
      this.addDeathMessage(TensuraDamageTypes.DEATH_BLESS, "%1$s was gently put to sleeps", "%1$s was gently put to sleep by %2$s");
      this.addDeathMessage(TensuraDamageTypes.DEATH_WISH, "%1$s's Death Wish was granted", "%1$s's Death Wish was granted by %2$s");
      this.addDeathMessage(TensuraDamageTypes.DEVOURED, "%1$s was devoured alive", "%1$s was devoured alive by %2$s");
      this.addDeathMessage(TensuraDamageTypes.DIMENSION_RAY, "%1$s was torn to shreds by Dimension Rays", "%1$s was torn to shreds by %2$s's Dimension Rays");
      this.addDeathMessage(
         TensuraDamageTypes.DISINTEGRATION,
         "%1$s was reduced to ashes after feeling the embrace of the divine",
         "%1$s was reduced to ashes after feeling the embrace of the divine from %2$s"
      );
      this.addDeathMessage(TensuraDamageTypes.DROWSY_DEATH, "%1$s drifted into a deep sleep", "%1$s drifted into a deep sleep by %2$s");
      this.addDeathMessage(TensuraDamageTypes.ENERGY_DRAIN, "%1$s was out of energy", "%1$s's life energy was drained by %2$s");
      this.addDeathMessage(TensuraDamageTypes.ENERGY_SOURCE_LOST, "%1$s's Energy Source was cut off", "%1$s's Energy Source was cut off by %2$s");
      this.addDeathMessage(TensuraDamageTypes.FLAME_BREATH, "%1$s was scorched by a breath of fire", "%1$s was scorched by %2$s's breath");
      this.addDeathMessage(TensuraDamageTypes.GRAVITY_EXPLODE, "%1$s exploded from the inside out", "%1$s exploded from the inside out by %2$s's gravity");
      this.addDeathMessage(TensuraDamageTypes.GRAVITY_PRESS, "%1$s snapped under the pressure", "%1$s snapped under the pressure from %2$s");
      this.addDeathMessage(TensuraDamageTypes.HAZY_BLOSSOM_THRUST, "%1$s's vital points were pierced", "%1$s's vital points were pierced by %2$s");
      this.addDeathMessage(TensuraDamageTypes.HEAT_WAVE, "%1$s tried to surf the heat wave", "%1$s couldn't stand %2$s's heat");
      this.addDeathMessage(TensuraDamageTypes.HEART_EAT, "%1$s had their heart eaten", "%1$s had their heart eaten by %2$s");
      this.addDeathMessage(TensuraDamageTypes.ICE_BREATH, "%1$s was frozen by a breath of ice", "%1$s was frozen by %2$s's breath");
      this.addDeathMessage(TensuraDamageTypes.INFINITE_EATER, "%1$s was eaten out of existence", "%1$s was eaten out of existence by %2$s");
      this.addDeathMessage(TensuraDamageTypes.LIGHTNING, "%1$s was struck by lightning", "%1$s was struck by %2$s's lightning");
      this.addDeathMessage(TensuraDamageTypes.MAGIC_GENERIC, "%1$s was killed by magic", "%1$s was killed by %2$s's magic");
      this.addDeathMessage(TensuraDamageTypes.MIND_CRUSH, "%1$s's mind was crushed'", "%1$s's mind was crushed by %2$s");
      this.addDeathMessage(TensuraDamageTypes.MIND_REQUIEM, "%1$s had their final requiem sung", "%1$s had their final requiem sung by %2$s");
      this.addDeathMessage(TensuraDamageTypes.MEGIDDO, "%1$s's brain was pierced through by Megiddo", "%1$s's brain was pierced through by %2$s's Megiddo");
      this.addDeathMessage(TensuraDamageTypes.PARALYZING, "%1$s was paralyzed to death", "%1$s was paralyzed to death by %2$s");
      this.addDeathMessage(TensuraDamageTypes.POISONOUS_BREATH, "%1$s was corroded by breath of acid", "%1$s was corroded by %2$s's breath of acid");
      this.addDeathMessage(TensuraDamageTypes.REFLECTED, "%1$s tasted their own medicine", "%1$s was destroyed by %2$s's reflected attack");
      this.addDeathMessage(TensuraDamageTypes.SOUL_CONSUMED, "%1$s was deprived of their Soul", "%1$s's Soul was deprived by %2$s");
      this.addDeathMessage(TensuraDamageTypes.SEVERANCE, "%1$s was severed", "%1$s was severed by %2$s");
      this.addDeathMessage(TensuraDamageTypes.SOUND_BLAST, "%1$s was blasted apart by a deafening sonic blast", "%1$s was blasted apart by %2$s's sonic blast");
      this.addDeathMessage(TensuraDamageTypes.SYNTHESISE, "%1$s was synthesised", "%1$s was synthesised with %2$s");
      this.addDeathMessage(TensuraDamageTypes.STEEL_THREAD, "%1$s was sliced into pieces by Steel Thread", "%1$s was sliced into pieces by %2$s's Steel Thread");
      this.addDeathMessage(TensuraDamageTypes.SUICIDE, "%1$s was forced to end their own life", "%1$s was forced to end their own life by %2$s");
      this.addDeathMessage(TensuraDamageTypes.THUNDER_BREATH, "%1$s was electrocuted by breath of thunder", "%1$s was electrocuted by %2$s's breath");
      this.addDeathMessage(TensuraDamageTypes.WATER_BLADE, "%1$s was cut by a blade of water", "%1$s was cut by %2$s's blade of water");
      this.addDeathMessage(TensuraDamageTypes.WATER_BREATH, "%1$s was crushed by high-pressure water", "%1$s was crushed by %2$s's high-pressure water");
      this.addDeathMessage(
         TensuraDamageTypes.WICKED_LIGHT_RAY, "%1$s was blasted into bits by Wicked Light Ray", "%1$s was blasted into bits by %2$s's Wicked Light Ray"
      );
      this.addDeathMessage(TensuraDamageTypes.WIND_BREATH, "%1$s got caught up in roaring winds", "%1$s got caught up in roaring winds by %2$s");
   }

   private void worldGen() {
      this.addBiome(TensuraBiomes.ANCIENT_FOREST, "Ancient Forest");
      this.addBiome(TensuraBiomes.BARREN_LAND, "Barren Land");
      this.addBiome(TensuraBiomes.DESERT_OF_DEATH, "Desert of Death");
      this.addBiome(TensuraBiomes.MIASMIC_PLAINS, "Miasmic Plains");
      this.addBiome(TensuraBiomes.UNDERWORLD_BARRENS, "Underworld Barrens");
      this.addBiome(TensuraBiomes.UNDERWORLD_RED_SANDS, "Underworld Red Sands");
      this.addBiome(TensuraBiomes.UNDERWORLD_SANDS, "Underworld Sands");
      this.addBiome(TensuraBiomes.UNDERWORLD_SPIKES, "Underworld Spikes");
      this.addDimension(TensuraDimensions.BOSS_AREA, "Boss Area");
      this.addDimension(TensuraDimensions.HELL, "Hell");
      this.addDimension(TensuraDimensions.LABYRINTH, "Labyrinth");
   }

   private void gameRules() {
      this.add("gamerule.category.ability", "Abilities");
      this.add("gamerule.category.existence", "Existence");
      this.add("gamerule.category.race", "Races");
      this.add("gamerule.category.tensura_player", "Tensura Player");
      this.add("gamerule.category.tensura_misc", "Tensura Miscellaneous");
      this.add("gamerule.demonLordSeed", "Demon Lord Seed EP");
      this.add("gamerule.demonLordSeed.description", "The amount of EP needed to be a Demon Lord Seed");
      this.add("gamerule.demonLordAwaken", "True Demon Lord Soul");
      this.add("gamerule.demonLordAwaken.description", "The number of Soul Points needed to be a True Demon Lord");
      this.add("gamerule.forceHarvestFestival", "Force Harvest Festival Soul");
      this.add("gamerule.forceHarvestFestival.description", "The number of Souls needed to force the player into Harvest Festival for True Demon Lord");
      this.add("gamerule.labyrinthPvp", "Labyrinth PVP");
      this.add("gamerule.labyrinthPvp.description", "Allows players to pvp in the Labyrinth Dimension");
      this.add("gamerule.labyrinthDeath", "Labyrinth Death");
      this.add("gamerule.labyrinthDeath.description", "Allows players to die in the Labyrinth Dimension instead of getting teleported out at 1HP");
      this.add("gamerule.colossusRespawn", "Colossus Respawn");
      this.add(
         "gamerule.colossusRespawn.description",
         "Respawns the Elemental Colossus when a player who hasn't won against a colossus before reaches the arena in the Labyrinth"
      );
      this.add("gamerule.epDeathPenalty", "EP Death Penalty");
      this.add("gamerule.epDeathPenalty.description", "The percentage of EP that the player will lose when respawning");
      this.add("gamerule.mpSkillCost", "Skill Magicule Cost");
      this.add("gamerule.mpSkillCost.description", "The percentage of MP that the player will lose when gaining a new skill");
      this.add("gamerule.epGainMultiplier", "EP Gain Multiplier");
      this.add("gamerule.epGainMultiplier.description", "The multiplier of EP gain that an entity and its gear would get after defeating a target");
      this.add("gamerule.maxMpGain", "Maximum Magicule Gain");
      this.add("gamerule.maxMpGain.description", "Maximum amount of Magicule an entity can gain at once from killing a target");
      this.add("gamerule.maxApGain", "Maximum Aura Gain");
      this.add("gamerule.maxApGain.description", "Maximum amount of Aura an entity can gain at once from killing a target");
      this.add("gamerule.playerEP", "Player EP Drop Percentage");
      this.add("gamerule.playerEP.description", "How much percentage of default EP value that fallen players can be used for EP gain calculation");
      this.add("gamerule.vanillaEP", "Vanilla EP Percentage");
      this.add("gamerule.vanillaEP.description", "How much percentage of default EP value that vanilla mobs can be used for EP gain calculation");
      this.add("gamerule.tensuraEP", "Tensura EP Percentage");
      this.add("gamerule.tensuraEP.description", "How much percentage of default EP value that Tensura mobs can be used for EP gain calculation");
      this.add("gamerule.moddedEP", "Modded EP Percentage");
      this.add("gamerule.moddedEP.description", "How much percentage of default EP value that non-tensura modded mobs can be used for EP gain calculation");
      this.add("gamerule.spawnerEP", "Spawner EP Percentage");
      this.add("gamerule.spawnerEP.description", "How much percentage of default EP value that mobs spawned from Spawners can be used for EP gain calculation");
      this.add("gamerule.rimuruMode", "Starts as Rimuru");
      this.add("gamerule.noUniqueStart", "No Unique Start");
      this.add("gamerule.noUniqueStart.description", "Starts with a buff in MP/AP but no Unique Skills");
      this.add("gamerule.trulyUnique", "Truly Unique");
      this.add("gamerule.trulyUnique.description", "Removes owned Unique Skills from reincarnation skill list");
      this.add("gamerule.resetIncompletePenalty", "Penalty for an incomplete reset");
      this.add(
         "gamerule.resetIncompletePenalty.description",
         "Number of points gets removed from the Reset Counter when a player uses any reset scroll while not meeting the requirement"
      );
      this.add("gamerule.resetCounterBonusUnique", "Bonus Unique with Reset Counter");
      this.add("gamerule.resetCounterBonusUnique.description", "Gains more Unique skills on resetting based on how many Reset Counters per skill");
      this.add("gamerule.resetPerSkillLock", "Reset Counter per Skill Lock");
      this.add("gamerule.resetPerSkillLock.description", "Number of points of reset counter for each Unique Skill lock for the next reset");
      this.add("gamerule.skillBeforeRace", "Skill Before Race");
      this.add("gamerule.skillBeforeRace.description", "Gains Unique Skills before choosing race");
      this.add("gamerule.hardcoreRace", "Hardcore Race");
      this.add("gamerule.hardcoreRace.description", "Makes some Races harder to play as");
      this.add("gamerule.maximumMagicExplosion", "Maximum Magic Explosion Radius");
      this.add("gamerule.maximumMagicExplosion.description", "The maximum radius in block that a Magic Explosion can grief the surrounding");
      this.add("gamerule.skillGriefing", "Ability Griefing");
      this.add("gamerule.skillGriefing.description", "Allows Tensura Ability to grief the surrounding");
      this.add("gamerule.skillSteal", "Ability Steal");
      this.add("gamerule.skillSteal.description", "Enables certain skills to steal Skills from Players instead of copying");
      this.add("gamerule.epSteal", "Energy Steal");
      this.add("gamerule.epSteal.description", "Enables certain skills to steal Energy (EP/MP/AP) from Players");
      this.add("gamerule.playerMindControl", "Mind Control");
      this.add("gamerule.playerMindControl.description", "Enables certain skills to mind control Players");
      this.add("gamerule.disableNullification", "Disable Nullification");
      this.add("gamerule.disableNullification.description", "Disables Nullification from being used");
      this.add("gamerule.disableDaemonAutoMagic", "Disable Daemon Intrinsic Magic Learning");
      this.add("gamerule.disableDaemonAutoMagic.description", "Disables daemons players from auto learn Aspectual Magics");
      this.add("gamerule.disableSpiritualLimit", "Disable Spiritual EP Limit");
      this.add("gamerule.disableSpiritualLimit.description", "Disables the ep limit that applies on spiritual players when entering a physical world");
      this.add("gamerule.playerSummoning", "Player Summoning");
      this.add("gamerule.playerSummoning.description", "Allows players to be summoned by other Players");
      this.add("gamerule.playerNaming", "Player Naming");
      this.add("gamerule.playerNaming.description", "Allows players to be named by others");
      this.add("gamerule.playerManualDodging", "Player Manual Dodging (Experimental)");
      this.add("gamerule.playerManualDodging.description", "Whether if players are allowed to use the keybind-based dodging feature");
      this.add("gamerule.tensuraDisplayName", "Tensura Display Name");
      this.add("gamerule.tensuraDisplayName.description", "Displays the tensura name instead of minecraft name if the player is named");
      this.add("gamerule.npcGrief", "NPC Griefing");
      this.add("gamerule.npcGrief.description", "Allows Tensura humanoid NPCs to interact with the environment regardless of the Mob Griefing gamerule");
      this.add("gamerule.npcWorking", "NPC Working");
      this.add("gamerule.npcWorking.description", "Allows Tensura humanoid NPCs to do their profession/job like farming, chopping trees, fishing, etc.");
   }

   private void battlewill() {
      this.addSkill(MeleeArts.AURA_SLASH, "Aura Slash", "Condense your aura along your blade and release ranged slash.");
      this.addSkill(MeleeArts.AURA_SWORD, "Aura Sword", "Coat your weapon in aura enhancing its blows.");
      this.addSkill(MeleeArts.EARTHSHATTER_KICK, "Earthshatter Kick", "Stomp your foot down upheaving the land around you.");
      this.addSkill(MeleeArts.HEAVY_SLASH, "Heavy Slash", "Channel your aura into your arms and bring down a mountain-splitting slash.");
      this.addSkill(MeleeArts.OGRE_SWORD_GUILLOTINE, "Ogre-sword Guillotine", "Coat your weapon in aura enhancing its blows.");
      this.addSkill(MeleeArts.ROARING_LION_PUNCH, "Roaring Lion Punch", "Focus your aura into a fearsome blow with the regalness of a lion.");
      this.addSkill(
         MeleeArts.FIVE_PETALS_THRUST, "Five Petals Thrust", "Dash and pierce the opponent in five of ten vital points and uses the other five as feints."
      );
      this.addSkill(
         MeleeArts.EIGHT_PETALS_SLASH,
         "Eight Petals Flash",
         "Dash and attack the target's eyes, throat, heart, kidneys, lungs, and groin with eight consecutive and near instant slashes."
      );
      this.addSkill(ProjectileArts.DARK_EIGHT_PALMS, "Dark Eight Palms", "Launch up to eight devastating aura blasts at foes.");
      this.addSkill(ProjectileArts.DEATH_MARCH_DANCE, "Death March Dance", "Gather your aura into a ring of devastating aura spheres that come crashing down.");
      this.addSkill(ProjectileArts.ELEPHANT_STAMPEDE, "Elephant Stampede", "Throw a ring of aura spheres around you.");
      this.addSkill(ProjectileArts.MAGIC_BULLET, "Magic Bullet", "Gather your aura into a powerful blast.");
      this.addSkill(
         ProjectileArts.MAXIMUM_MAGIC_BULLET, "Maximum Magic Bullet", "Gather your aura into a gargantuan blast obliterating all who dare oppose you."
      );
      this.addSkill(ProjectileArts.OGRE_FLAME, "Ogre Flame", "Use your aura to create a pillar of fire.");
      this.addSkill(ProjectileArts.OGRE_SWORD_CANNON, "Ogre-sword Cannon", "Condense your aura into a blade projectile.");
      this.addSkill(UtilityArts.AIR_FLIGHT, "Air Flight", "Use your aura to propel you forward, and hover in air.");
      this.addSkill(UtilityArts.AURA_SHIELD, "Aura Shield", "Create a shield of condensed aura to block attacks.");
      this.addSkill(UtilityArts.BATTLEWILL, "Battlewill", "Channel your will, converting magicules into aura.");
      this.addSkill(UtilityArts.DIAMOND_PATH, "Diamond Path", "Harden your aura around you to block incoming attacks.");
      this.addSkill(UtilityArts.FORMHIDE, "Formhide", "Match your aura to the surroundings, which makes you imperceptible.");
      this.addSkill(UtilityArts.HAZE, "Haze", "Wrap yourself in a cloak of aura concealing yourself from even the most heightened of senses.");
      this.addSkill(UtilityArts.INSTANT_MOVE, "Instant-move", "Gather your aura at your feet to travel faster than the eye can see.");
      this.addSkill(
         UtilityArts.VIOLENT_BREAK, "Violent Break", "Channel your aura, recklessly enhancing your strength and cleansing you of any negative effects."
      );
   }

   private void magic() {
      this.add("tensura.magic.cast_time", "Casting time: %s");
      this.add("tensura.magic.cast_time.max", "Casting time: %s/%s");
      this.add("tensura.magic.cast_time.remaining", "Remaining time: %s/%s");
      this.add("tensura.magic.spiritual.chosen.cooldown", "The Spirits have already listened to your prayers today.");
      this.add("tensura.magic.spiritual.chosen.failed", "The Spirits remain silent. Seek them again at the start of a new day.");
      this.add("tensura.magic.spiritual.chosen.duplicated", "A %s failed to form a contract due to being lower level.");
      this.add("tensura.magic.spiritual.chosen", "A %s has heard your prayers.");
      this.add("tensura.magic.spiritual.spirit_name", "%s Spirit of %s");
      this.add("tensura.magic.spiritual.spirit_name.lord", "Spirit %s of %s");
      this.add("tensura.magic.elemental.darkness", "Darkness");
      this.add("tensura.magic.elemental.earth", "Earth");
      this.add("tensura.magic.elemental.enhancement", "Enhancement");
      this.add("tensura.magic.elemental.explosion", "Explosion");
      this.add("tensura.magic.elemental.fire", "Fire");
      this.add("tensura.magic.elemental.gravity", "Gravity");
      this.add("tensura.magic.elemental.ice", "Ice");
      this.add("tensura.magic.elemental.illusion", "Illusion");
      this.add("tensura.magic.elemental.light", "Light");
      this.add("tensura.magic.elemental.lightning", "Lightning");
      this.add("tensura.magic.elemental.mental", "Mental");
      this.add("tensura.magic.elemental.recovery", "Recovery");
      this.add("tensura.magic.elemental.space", "Space");
      this.add("tensura.magic.elemental.water", "Water");
      this.add("tensura.magic.elemental.wind", "Wind");
      this.add("tensura.magic.elemental.time", "Time");
      this.add("tensura.magic.elemental.battle", "Battle");
      this.add("tensura.magic.elemental.fantasy", "Fantasy");
      this.add("tensura.magic.elemental.holy", "Holy");
      this.add("tensura.magic.elemental.mirc", "Misc");
      this.add("tensura.magic.elemental.unidentified", "Unidentified");
      this.add("tensura.magic.spiritual.level.lesser", "Lesser");
      this.add("tensura.magic.spiritual.level.medium", "Medium");
      this.add("tensura.magic.spiritual.level.greater", "Greater");
      this.add("tensura.magic.spiritual.level.lord", "Lord");
      this.add("tensura.magic.type.aspectual", "Aspectual");
      this.add("tensura.magic.type.spiritual", "Spiritual");
      this.add("tensura.magic.type.summoning", "Summoning");
      this.add("tensura.magic.type.misc", "Miscellaneous");
      this.addSkill(AspectualMagics.MAGIC_WALL, "Magic Wall", "Generate a magic wall to protect the caster.");
      this.addSkill(AspectualMagics.MAGIC_BARRIER, "Magic Barrier", "Coat the caster with a Magic Barrier with the strength based on Health.");
      this.addSkill(AspectualMagics.BARRIER, "Barrier", "Coat the caster with a Physical Barrier with the strength based on Health.");
      this.addSkill(AspectualMagics.REINFORCED_BARRIER, "Reinforced Barrier", "Coat the caster with reinforced Barriers with the strength based on Health.");
      this.addSkill(AspectualMagics.ANTI_SHOCK_AREA, "Anti-Shock Area", "Creates a zone around the user that limits all physical damage taken.");
      this.addSkill(AspectualMagics.ANTI_MAGIC_AREA, "Anti-Magic Area", "Creates a zone around the user that limits all Aspectual and Summoning Magics.");
      this.addSkill(AspectualMagics.EARTH_LOCK, "Earth Lock", "Solidify the earth where the caster desires.");
      this.addSkill(AspectualMagics.LIQUIDIZE, "Liquidize", "Liquidize the earth where the caster desires to turn into deadly traps.");
      this.addSkill(AspectualMagics.EARTH_WALL, "Earth Wall", "Call forth giant earth walls from the ground.");
      this.addSkill(AspectualMagics.MUD_HAND, "Mud Hand", "Bind targets with several hands of mud.");
      this.addSkill(AspectualMagics.STONE_SHOT, "Stone Shot", "Shoot rock spikes toward targets.");
      this.addSkill(AspectualMagics.MUD_SPEARS, "Mud Spears", "Raise mud spikes from the ground to pierce through your enemies.");
      this.addSkill(AspectualMagics.REINFORCEMENT, "Reinforcement", "Greatly enhance the caster's gears durability.");
      this.addSkill(AspectualMagics.STRENGTH, "Strength", "Greatly enhance the caster's strength power.");
      this.addSkill(AspectualMagics.AGILITY, "Agility", "Greatly enhance the caster's movement speed.");
      this.addSkill(AspectualMagics.PROTECTION, "Protection", "Greatly enhance the caster's defence.");
      this.addSkill(
         AspectualMagics.EXPLOSION,
         "Explosion",
         "Summon a single beam of light, that detonates at the target, creating a devastating blast of pure magical power."
      );
      this.addSkill(AspectualMagics.CHAIN_EXPLOSION, "Chain Explosion", "Summon a sequence of explosion that detonates in a chain around targets.");
      this.addSkill(AspectualMagics.FIRE, "Fire", "Shoot magic fiery projectiles.");
      this.addSkill(AspectualMagics.FIRE_LANCE, "Fire Lance", "Shoot fiery lances of magic.");
      this.addSkill(AspectualMagics.FIRE_BALL, "Fire Ball", "Shoot concentrated balls of fire.");
      this.addSkill(AspectualMagics.FIRE_WALL, "Fire Wall", "Create a wall of fire to keep targets away.");
      this.addSkill(AspectualMagics.FIRE_STORM, "Fire Storm", "Call forth fiery surges of flame to incinerate targets.");
      this.addSkill(AspectualMagics.FLOAT, "Float", "Decrease the caster's gravity to float.");
      this.addSkill(AspectualMagics.LIGHTEN, "Lighten", "Lighten the caster's gravity for greater speed and jump power.");
      this.addSkill(AspectualMagics.BURDEN, "Burden", "Shoot energy projectiles that increase the target's gravity.");
      this.addSkill(AspectualMagics.FREEZE, "Freeze", "Solidify water sources into ice.");
      this.addSkill(AspectualMagics.ICICLE_LANCE, "Icicle Lance", "Shoot frozen icicles toward targets.");
      this.addSkill(AspectualMagics.ICICLE_SPEAR, "Icicle Spear", "Strike targets with a giant icicle spike from the ground.");
      this.addSkill(AspectualMagics.ICICLE_RAIN, "Icicle Rain", "Strike targets with a barrage of icicle lances from the sky.");
      this.addSkill(AspectualMagics.ICE_WALL, "Ice Wall", "Call forth walls of ice upon targets.");
      this.addSkill(AspectualMagics.ICE_BLIZZARD, "Ice Blizzard", "Create a powerful storm of snow to freeze your enemies.");
      this.addSkill(AspectualMagics.ICE_BREAKER, "Ice Breaker", "Shoot a giant icicle spear toward targets.");
      this.addSkill(AspectualMagics.FLAME_WALL, "Flame Wall", "Create a wall of illusional fire to keep targets away.");
      this.addSkill(AspectualMagics.CONFUSION, "Confusion", "Apply vision confusion to surrounding targets.");
      this.addSkill(AspectualMagics.INVISIBLE, "Invisible", "Turn the caster invisible to hide from targets.");
      this.addSkill(AspectualMagics.MIRAGE, "Mirage", "Create illusionary clones around the caster.");
      this.addSkill(AspectualMagics.POSSESSION, "Possession", "Possess a weakened material body to gain a new powers from that body.");
      this.addSkill(AspectualMagics.THUNDER_LANCE, "Thunder Lance", "Strike powerful thunder lances of magic.");
      this.addSkill(AspectualMagics.THUNDER, "Thunder", "Strike powerful thunder where the caster looks.");
      this.addSkill(AspectualMagics.THUNDER_ORB, "Thunder Orb", "Call forth a sphere of thunder that strikes any enemy get by.");
      this.addSkill(AspectualMagics.THUNDER_RAIN, "Thunder Rain", "Call forth a wide area of thunder clouds that strike all enemies.");
      this.addSkill(AspectualMagics.DOMINATE, "Dominate", "Dominate over any low-leveled targets.");
      this.addSkill(AspectualMagics.DEMON_DOMINATE, "Demon Dominate", "Dominate over any targets up to the Calamity level.");
      this.addSkill(AspectualMagics.DEMON_MARIONETTE, "Demon Marionette", "Dominate over any targets even at the Disaster level.");
      this.addSkill(AspectualMagics.MENTAL_CRUSH, "Mental Crush", "Crush the mind of targets.");
      this.addSkill(AspectualMagics.HYPNOS, "Hypnos", "Put even the strongest entities into sleep.");
      this.addSkill(AspectualMagics.HEALING, "Healing", "Heal a living being on a low level.");
      this.addSkill(AspectualMagics.HEALING_RAIN, "Healing Rain", "Call forth a rain of healing in a wide radius.");
      this.addSkill(AspectualMagics.RECOVERY, "Recovery", "Greatly recover a living being's health as well as food points.");
      this.addSkill(AspectualMagics.ANTIDOTE, "Antidote", "Recover a living being from nausea, poison and weakness.");
      this.addSkill(AspectualMagics.FULL_RECOVERY, "Full Recovery", "Recover a living being's health and food points to the maximum as well as Absorption.");
      this.addSkill(AspectualMagics.ESCAPE, "Escape", "Connect two points in space to create a escape route.");
      this.addSkill(AspectualMagics.WARP_PORTAL, "Warp Portal", "Tear space asunder connecting two points in space.");
      this.addSkill(AspectualMagics.SPATIAL_STORAGE, "Spatial Storage", "Rift space to create a small pocket dimension to store items.");
      this.addSkill(AspectualMagics.DIMENSION_CUTTER, "Dimension Cutter", "Tear space to send forward a blade of dimension rift.");
      this.addSkill(AspectualMagics.WATER, "Water", "Call forth small amounts of water.");
      this.addSkill(AspectualMagics.DRAINAGE, "Drainage", "Drain the water around the caster.");
      this.addSkill(AspectualMagics.WATER_CUTTER, "Water Cutter", "Fire concentrated blades of water toward targets.");
      this.addSkill(AspectualMagics.WATER_JAIL, "Water Jail", "Surround targets with flows of water.");
      this.addSkill(AspectualMagics.SLEEP_MIST, "Sleep Mist", "Release a wide area of sleeping mist to paralyze targets.");
      this.addSkill(AspectualMagics.ACID_SHELL, "Acid Shell", "Shoots a ball of corrosive acid toward targets.");
      this.addSkill(AspectualMagics.WIND_GUST, "Wind Gust", "Release strong currents of winds to create gusts or wind charges toward targets.");
      this.addSkill(AspectualMagics.WIND_CUTTER, "Wind Cutter", "Fire concentrated blades of wind toward targets.");
      this.addSkill(AspectualMagics.TORNADO_BLADE, "Tornado Blade", "Fire a concentrated sphere of wind toward targets.");
      this.addSkill(AspectualMagics.WIND_PROTECTION, "Wind Protection", "Call forth flows of wind to coat the caster with speed and protection.");
      this.addSkill(AspectualMagics.AIRFLOW_SHUT, "Airflow Shut", "Drain out all the air surrounding the targets and suffocate them.");
      this.addSkill(AspectualMagics.REINCARNATION, "Reincarnation", "Sacrifice a portion of the caster's power to reincarnate.");
      this.addSkill(AspectualMagics.ANALYZE, "Analyze", "Analyze targets' status and power.");
      this.addSkill(AspectualMagics.CLAIRVOYANCE, "Clairvoyance", "Increase the caster's eye sight.");
      this.addSkill(AspectualMagics.DOPPELGANGER, "Doppelganger", "Use percentages of the user's magical power to create identical clones around them.");
      this.addSkill(AspectualMagics.FLIGHT, "Flight", "Propel the caster forward in the air.");
      this.addSkill(AspectualMagics.HEALTHCARE, "Healthcare", "Boost the caster's health and saturation.");
      this.addSkill(AspectualMagics.SEARCH_ENEMY, "Search Enemy", "Highlight hostile mobs around the caster.");
      this.addSkill(SpiritualMagics.DARKNESS, "Darkness", "Call on your spirit to reduce the enemy's vision in a wide area.");
      this.addSkill(SpiritualMagics.SHADOW_BIND, "Shadow Bind", "If the target is standing in shadows, bind them and deal spiritual damage.");
      this.addSkill(SpiritualMagics.DARK_CUBE, "Dark Cube", "Create a cube of darkness that slows movement and deals constant damage.");
      this.addSkill(SpiritualMagics.DARKNESS_CANNON, "Darkness Cannon", "Shoot a long beam which deals massive damage, destroys armor and debilitates enemies.");
      this.addSkill(SpiritualMagics.TRUE_DARKNESS, "True Darkness", "Inflict Blindness on all entities and deal massive spiritual damage.");
      this.addSkill(SpiritualMagics.EARTH, "Earth", "Place down blocks which mimic those from the surrounding environment.");
      this.addSkill(SpiritualMagics.EARTH_SPIKES, "Earth Spikes", "Raises earth spikes from the ground to pierce through your enemies.");
      this.addSkill(SpiritualMagics.EARTH_STORM, "Earth Storm", "Summons a viscous sandstorm and falling rocks around you.");
      this.addSkill(SpiritualMagics.MAGMA_SURGE, "Magma Surge", "Fire a spread of lava that burns and melts anything for a limited time.");
      this.addSkill(SpiritualMagics.EARTH_JAIL, "Earth Jail", "Restrict and weaken a single target, applying debuffs to allow you to finish them off.");
      this.addSkill(SpiritualMagics.FIRE, "Fire", "Use your spirit to start a small fire.");
      this.addSkill(SpiritualMagics.FIRE_BREATH, "Fire Breath", "Breathe flames and incinerate your enemies.");
      this.addSkill(SpiritualMagics.FIRE_BOLT, "Fire Bolt", "Shoots a flaming bolt.");
      this.addSkill(SpiritualMagics.FLARE_CIRCLE, "Flare Circle", "Opens a Gate to Hell from which wicked flames erupt to turn your enemies to ash.");
      this.addSkill(SpiritualMagics.HELLFIRE, "Hellfire", "Summon a sphere of hellfire to deal massive damage in a small area.");
      this.addSkill(SpiritualMagics.LIGHT, "Light", "Summons a temporary light to block out the darkness.");
      this.addSkill(SpiritualMagics.SOLAR_BEAM, "Solar Beam", "Fire a light beam that deals massive damage to undead entities and burns blocks.");
      this.addSkill(SpiritualMagics.SOLAR_WAVE, "Solar Wave", "Shoot a wave of light energy that blinds and slows hit enemies.");
      this.addSkill(SpiritualMagics.SOLAR_RAIN, "Solar Rain", "Shoot many light projectiles that deal massive damage to undead creatures.");
      this.addSkill(
         SpiritualMagics.SOLAR_FLARE, "Solar Flare", "Shoot a shockwave of light energy to give nausea, blindness and slowness to all targets in a wide AOE."
      );
      this.addSkill(SpiritualMagics.SPACE, "Space", "Form invisible platforms to create footholds midair.");
      this.addSkill(SpiritualMagics.GATE, "Gate", "Tear space asunder connecting two points in space.");
      this.addSkill(SpiritualMagics.SHRINK, "Shrink", "Reduce your size to enhance your evasion but increase your vulnerability");
      this.addSkill(SpiritualMagics.TELEPORT, "Teleport", "Quickly blink forward in space to a location within view.");
      this.addSkill(SpiritualMagics.SWIPE, "Swipe", "Slashing through space in a straight line, displacing entities or teleporting the caster.");
      this.addSkill(SpiritualMagics.WATER, "Water", "Call forth or manipulate small amounts of water.");
      this.addSkill(SpiritualMagics.WATER_CUTTER, "Water Cutter", "Fires a concentrated blade to cut your enemies with the power of water.");
      this.addSkill(SpiritualMagics.ACID_RAIN, "Acid Rain", "Summon an acidic cloud which will corrode any afflicted entities.");
      this.addSkill(
         SpiritualMagics.MEGIDDO,
         "Megiddo",
         "Unleash powerful sun blasts using water to kill any nearby foes. It can also be used manually to concentrate fire for any stronger foes."
      );
      this.addSkill(SpiritualMagics.BLIZZARD, "Blizzard", "Creates a powerful storm of ice to slow your enemies and turn the tides of battle.");
      this.addSkill(SpiritualMagics.WIND, "Wind", "Call forth a small gust of wind which can push targets away or the player up.");
      this.addSkill(SpiritualMagics.LIGHTNING_LANCE, "Lightning Lance", "Launch a lighting projectile which will electrify your enemies.");
      this.addSkill(SpiritualMagics.WIND_BLADE, "Wind Blade", "Fires a concentrated blade of wind which has heavy knockback.");
      this.addSkill(
         SpiritualMagics.ELECTRO_BLAST,
         "Electro Blast",
         "Fire a beam attack that pierces through enemies and deals devastating damage while paralyzing your enemies."
      );
      this.addSkill(
         SpiritualMagics.AERIAL_BLADE, "Aerial Blade", "Pull in any nearby entities before dealing massive damage by unleashing the power of your spirit."
      );
      this.addSkill(SpiritualMagics.CREATE_LESSER_UNDEAD, "Create Lesser Undead", "Call forth weak undead from the ground to aid the caster.");
      this.addSkill(SpiritualMagics.CREATE_GREATER_UNDEAD, "Create Greater Undead", "Call forth armed undead from the ground to aid the caster.");
      this.addSkill(SpiritualMagics.CURSE, "Curse", "Release Miasmic Mist to curse targets.");
      this.addSkill(SpiritualMagics.CURSE_BIND, "Curse Bind", "Call forth spirits of the death to bind and corrode targets.");
      this.addSkill(SummoningMagics.SUMMON_BASILISK, "Summon Basilisk", "Summon a Basilisk to temporarily fight for you.");
      this.addSkill(SummoningMagics.SUMMON_DAEMON, "Summon Daemon", "Summon a Daemon from hell to fight for you.");
      this.addSkill(SummoningMagics.SUMMON_HOUND_DOG, "Summon Hound Dog", "Summon a Hound Dog to temporarily fight for you.");
      this.addSkill(SummoningMagics.SUMMON_MEDIUM_ELEMENTAL, "Summon Medium Elemental", "Summon your contracted elemental spirit to temporarily fight for you.");
      this.addSkill(
         SummoningMagics.SUMMON_GREATER_ELEMENTAL,
         "Summon Greater Elemental",
         "Summon your contracted greater elemental spirit to have it purge the world of heretics for you."
      );
      this.addSkill(SummoningMagics.SUMMON_OTHERWORLDER, "Summon Otherworlder", "Summon a human from a different world.");
   }

   private void skills() {
      this.add("tensura.ep.acquire", "%s EP has been acquired.");
      this.add("tensura.ep.acquire_fallen", "%s EP has been acquired from fallen %s.");
      this.add("tensura.ep.acquire_mp", "%s Magicule has been acquired.");
      this.add("tensura.ep.acquire_max_mp", "%s Max Magicule has been acquired.");
      this.add("tensura.ep.acquire_ap", "%s Max Aura has been acquired.");
      this.add("tensura.ep.acquire_max_ap", "%s Max Aura has been acquired.");
      this.add("tensura.ability.activation_failed", "Ability activation failed.");
      this.add("tensura.ability.activation_failed.named", "%s activation failed.");
      this.add("tensura.ability.activation_failed.location", "This ability can not be activated at this location.");
      this.add("tensura.ability.activation_failed.time", "This ability can not be activated during this time.");
      this.add("tensura.ability.activation_failed.location_time", "This ability can not be activated at this location or time.");
      this.add("tensura.ability.activation_failed.gamerule", "This ability can not be activated due to the world's gamerules.");
      this.add("tensura.ability.activation_failed.item", "This ability can not be activated with this item.");
      this.add("tensura.ability.activation_failed.status", "This ability can not be activated with your current status.");
      this.add("tensura.ability.activation_failed.anti_magic", "This magic has been stopped by Anti-Magic.");
      this.add("tensura.ability.activation_failed.plunder", "Failed to copy/steal %s.");
      this.add("tensura.ability.activation_failed.plunder.empty", "Nothing available to copy/steal.");
      this.add("tensura.targeting.not_targeted", "You need to target a Living Entity.");
      this.add("tensura.targeting.not_allowed", "This ability doesn't work on the target.");
      this.add("tensura.targeting.ep_not_meet", "The target's EP doesn't meet the requirement for this ability.");
      this.add("tensura.skill.acquire_temporary", "%s has been temporarily acquired.");
      this.add("tensura.skill.acquire_fallen", "%s has been acquired from fallen %s.");
      this.add("tensura.skill.acquire_learning", "%s has been acquired through learning.");
      this.add("tensura.skill.acquire_mode", "%s's %s has been acquired.");
      this.add("tensura.skill.acquire_mode.default", "%s's Mode has been acquired.");
      this.add("tensura.skill.acquire_failed.mp", "Failed to acquire %s due to the lack of Magicule.");
      this.add("tensura.skill.learn_available", "%s is now available to learn.");
      this.add("tensura.skill.learn_points.added", "You have become closer to acquiring %s.");
      this.add("tensura.skill.learn_points.failed", "%s is currently unable to be learnt.");
      this.add("tensura.skill.learn_points.failed_mastery", "%s requires mastery over %s to be learnt.");
      this.add("tensura.skill.forget.stolen", "%s has been stolen by %s.");
      this.add("tensura.skill.temporary.already_have", "You've already had %s.");
      this.add("tensura.skill.temporary.remove", "Temporary %s has been removed.");
      this.add("tensura.skill.cooldown", "%s is on Cooldown.");
      this.add("tensura.skill.mastery", "You have mastered the usage of %s.");
      this.add("tensura.skill.mastery.all", "%s has mastered the usage of %s abilities.");
      this.add("tensura.skill.mastery.point_added", "You have become closer to mastering %s.");
      this.add("tensura.skill.output_number", "Output Number: %s");
      this.add("tensura.skill.stored_magicule", "Magicule: %s/%s");
      this.add("tensura.skill.power_scale", "Power Scale: %s");
      this.add("tensura.skill.time_held.max", "Activation time: %s/%s");
      this.add("tensura.skill.time_held", "Activation time: %s");
      this.add("tensura.skill.range", "Range: %s");
      this.add("tensura.skill.lack_aura", "Out of Aura.");
      this.add("tensura.skill.lack_aura.toggled_off", "%s has been toggled off due to the lack of Aura.");
      this.add("tensura.skill.lack_magicule", "Out of Magicule.");
      this.add("tensura.skill.lack_magicule.toggled_off", "%s has been toggled off due to the lack of Magicule.");
      this.add("tensura.skill.lack_requirement.toggled_off", "%s has been toggled off due to unmet requirements.");
      this.add("tensura.skill.transformed", "You are already in a transformation.");
      this.add("tensura.skill.magic_interference", "You have been affected by Magic Interference.");
      this.add("tensura.skill.spatial_blockade", "Your spatial moves are blocked.");
      this.add("tensura.skill.escape.too_far", "The escape magic circle is too far away.");
      this.add("tensura.skill.teleport.warp_point.success", "Successfully warped to %s.");
      this.add("tensura.skill.teleport.warp_point.wrong_dimension", "This dimension doesn't match this Warp Point's settings.");
      this.add("tensura.skill.teleport.out_border", "You cannot go outside the world border.");
      this.add("tensura.skill.type.resistance", "Resistance");
      this.add("tensura.skill.type.intrinsic", "Intrinsic");
      this.add("tensura.skill.type.common", "Common");
      this.add("tensura.skill.type.extra", "Extra");
      this.add("tensura.skill.type.unique", "Unique");
      this.add("tensura.skill.type.ultimate", "Ultimate");
      this.add("tensura.skill.mode.default", "Default");
      this.add("tensura.skill.mode.changed", "Changed the mode of %s to %s.");
      this.add("tensura.skill.mode.no_mode", "%s doesn't have another mode.");
      this.add("tensura.skill.mode.cannot_change", "You don't meet conditions to change to that mode of %s.");
      this.add("tensura.skill.mode.need_toggle_off", "%s's Passive Ability needs to be toggled off to use this ability.");
      this.add("tensura.skill.mode.need_toggle_on", "%s's Passive Ability needs to be toggled on to use this ability.");
      this.add("tensura.skill.preset.changed", "Changed active preset to %s.");
      this.add("tensura.skill.preset.no_change", "Your active preset is already %s.");
      this.add("tensura.skill.preset.changed_name", "Changed Preset %s's Name to %s.");
      this.add("tensura.skill.preset.changed_spell", "Changed active spell to %s.");
      this.add("tensura.skill.empty", "None");
      this.addSkill(CommonSkills.COERCION, "Coercion", "Shoot a blast of roar in front of you scaring any afflicted entities.");
      this.addSkill(CommonSkills.CORROSION, "Corrosion", "Empower your attacks with the deadly effect of Corrosion, toggleable when mastered.");
      this.addSkill(CommonSkills.FARSIGHT, "Farsight", "Focus your eyes and become able to see things far away.");
      this.addSkill(
         CommonSkills.GRAVITY_FIELD,
         "Gravity Field",
         "Weaken gravity around yourself to make movement easier or create a variously sized sphere granting previous effects while debuffing enemies."
      );
      this.addSkill(CommonSkills.GRAVITY_FLIGHT, "Gravity Flight", "Manipulate gravity to allow flight, your momentum from before will be continued.");
      this.addSkill(CommonSkills.HYDRAULIC_PROPULSION, "Hydraulic Propulsion", "Propel yourself at high speeds underwater.");
      this.addSkill(CommonSkills.PARALYSIS, "Paralysis", "Empower your attacks with the effect of Paralysis, toggleable when mastered.");
      this.addSkill(CommonSkills.POISON, "Poison", "Empower your attacks with the effect of Poison, toggleable when mastered.");
      this.addSkill(
         CommonSkills.RANGED_BARRIER,
         "Ranged Barrier",
         "Place down differently sized barriers which block enemies in or out. Strong attacks can still destroy them."
      );
      this.addSkill(CommonSkills.SELF_REGENERATION, "Self-Regeneration", "Speed up your body’s natural regeneration to increase your survivability.");
      this.addSkill(CommonSkills.STRENGTH, "Strength", "Use magicule to strengthen your muscles.");
      this.addSkill(CommonSkills.TELEPATHY, "Telepathy", "Give orders to tames when looking at them through commands.");
      this.addSkill(
         CommonSkills.THOUGHT_COMMUNICATION, "Thought Communication", "Send commands to nearby allies and become able to instruct them to attack players."
      );
      this.addSkill(
         CommonSkills.VOICE_CANNON, "Voice Cannon", "Fire powerful blasts of concentrated sound waves by producing loud vocal sounds atomizing weaker targets."
      );
      this.addSkill(CommonSkills.WATER_BLADE, "Water Blade", "Shoot out a blade of water which flies in a straight line.");
      this.addSkill(CommonSkills.WATER_CURRENT_CONTROL, "Water Current Control", "Manipulate water around you to propel yourself in any direction.");
      this.addSkill(IntrinsicSkills.ABSORB_DISSOLVE, "Absorb & Dissolve", "Dissolve specific items to instantly consume them.");
      this.addSkill(
         IntrinsicSkills.BEAST_TRANSFORMATION,
         "Beast Transformation",
         "Transform into a beast to restore your vitality and boost your physical body. The transformation will grant great physical prowess but leave a toll on your body."
      );
      this.addSkill(IntrinsicSkills.BODY_ARMOR, "Body Armor", "Protect yourself from harm by summoning armorsaurus scales around your body.");
      this.addSkill(IntrinsicSkills.CHARM, "Charm", "Use your inherent power to turn your opponents neutral or to temporarily dominate weak opponents.");
      this.addSkill(IntrinsicSkills.DARKNESS_TRANSFORM, "Darkness Transform", "Channel your inner darkness to burn and blind nearby opponents. ");
      this.addSkill(
         IntrinsicSkills.DIVINE_KI_RELEASE,
         "Divine Ki Release",
         "Use your Divine Ki to amplify your battlewill to deal more damage and destroy weaker equipment."
      );
      this.addSkill(
         IntrinsicSkills.BLOOD_MIST,
         "Blood Mist",
         "Spill your own blood to summon a mist which steals the vitality of your enemies and can be blown up to harm any nearby entities or shoot a powerful blood beam."
      );
      this.addSkill(IntrinsicSkills.DRAGON_EAR, "Dragon Ear", "Use your sensitive hearing to precisely pin the location of any nearby mobs which make sound.");
      this.addSkill(IntrinsicSkills.DRAGON_EYE, "Dragon Eye", "Use your powerful sight to access the power of any entities and see in the far distance.");
      this.addSkill(
         IntrinsicSkills.DRAGON_MODE,
         "Dragon Mode",
         "Once a day, draw out your monstrous potential to double your EP and to boost your physical prowess. Doing this will leave a temporary toll on your body."
      );
      this.addSkill(IntrinsicSkills.DRAGON_SKIN, "Dragon Skin", "Transform your skin into scales which become tougher as you gain more EP.");
      this.addSkill(
         IntrinsicSkills.DRAIN,
         "Drain",
         "Steal the vitality of your opponent and temporarily transform this skill into one of theirs to potentially turn the tables."
      );
      this.addSkill(
         IntrinsicSkills.EARTH_TRANSFORM, "Earth Transform", "Channel the powers of the earth to deal increased damage and increase local gravity around you."
      );
      this.addSkill(
         IntrinsicSkills.EYE_OF_TRUTH,
         "Eye of Truth",
         "By obtaining the mythical Hero Egg, the veil of deception is torn apart. No illusion or concealment can deceive your sight, and your vision becomes perfect."
      );
      this.addSkill(IntrinsicSkills.FLAME_BREATH, "Flame Breath", "Spew fire to burn away enemies and set fire to the land.");
      this.addSkill(IntrinsicSkills.FLAME_TRANSFORM, "Flame Transform", "Channel the powers of fire to burn all nearby foes.");
      this.addSkill(IntrinsicSkills.GIANTIFICATION, "Giantification", "Grow beyond your normal size to gain increased strength and reach.");
      this.addSkill(IntrinsicSkills.ICE_BREATH, "Ice Breath", "Spew icy breath to freeze enemies.");
      this.addSkill(IntrinsicSkills.LIGHT_TRANSFORM, "Light Transform", "Channel your inner light to deal light damage and nauseate all nearby entities.");
      this.addSkill(
         IntrinsicSkills.OGRE_BERSERKER,
         "Ogre Berserker",
         "Allow rage to consume you to massively improve your physical powers for a time before the aftereffects set in."
      );
      this.addSkill(IntrinsicSkills.PARALYSING_BREATH, "Paralysing Breath", "Spew a horrifying breath to paralyze your prey.");
      this.addSkill(IntrinsicSkills.POISONOUS_BREATH, "Poisonous Breath", "Use your disgusting breath to corrode and nauseate your targets. ");
      this.addSkill(IntrinsicSkills.POSSESSION, "Possession", "Possess a weakened material body to gain access to a whole new world.");
      this.addSkill(
         IntrinsicSkills.SCALE_ARMOR, "Scale Armor", "Like the lizardmen, move through water and mud with no penalties while receiving a slight defense buff. "
      );
      this.addSkill(IntrinsicSkills.SPACE_TRANSFORM, "Space Transform", "Channel the powers of space to weaken and damage all nearby entities.");
      this.addSkill(IntrinsicSkills.THUNDER_BREATH, "Thunder Breath", "Release thunder from your mouth to damage foes in front of you.");
      this.addSkill(
         IntrinsicSkills.TITANIFICATION,
         "Titanification",
         "Grow into a towering titan to massively increase your strength, defense, and reach while gaining immunity to magic from weaker foes."
      );
      this.addSkill(
         IntrinsicSkills.ULTRASONIC_WAVES,
         "Ultrasonic Waves",
         "Shoot out a screech of sound which damages physical bodies or use echolocation to highlight nearby entities."
      );
      this.addSkill(
         IntrinsicSkills.UNPREDICTABILITY,
         "Unpredictability",
         "In the hands of a True Hero, unpredictability reigns supreme. The Hero sees all, becoming able to ignore dodge and even read the movements of their opponent to disable their critical attacks or their dodge avoidance."
      );
      this.addSkill(IntrinsicSkills.WATER_BREATHING, "Water Breathing", "Remove the requirement of air underwater.");
      this.addSkill(IntrinsicSkills.WATER_TRANSFORM, "Water Transform", "Channel the powers of water to damage and poison all nearby foes.");
      this.addSkill(IntrinsicSkills.WIND_TRANSFORM, "Wind Transform", "Channel the powers of wind to paralyze and damage all nearby entities.");
      this.addSkill(
         ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE,
         "Abnormal Condition Resistance",
         "Resist the effects of weaker abnormal conditions or reduce the severity of stronger ones."
      );
      this.addSkill(ResistanceSkills.COLD_RESISTANCE, "Cold Resistance", "Ignore weak cold damage or resist the damage of powerful attacks.");
      this.addSkill(
         ResistanceSkills.CORROSION_RESISTANCE, "Corrosion Resistance", "Ignore weak corrosive effects or reduce the damage from stronger corrosive attacks."
      );
      this.addSkill(
         ResistanceSkills.DARKNESS_ATTACK_RESISTANCE, "Darkness Attack Resistance", "Ignore weaker darkness damage or reduce the damage of stronger attacks."
      );
      this.addSkill(
         ResistanceSkills.EARTH_ATTACK_RESISTANCE, "Earth Attack Resistance", "Ignore weak earth-based damage or resist the damage from powerful attacks."
      );
      this.addSkill(
         ResistanceSkills.ELECTRICITY_RESISTANCE, "Electricity Resistance", "Ignore weak electrical damage or resist the damage from stronger attacks."
      );
      this.addSkill(
         ResistanceSkills.FLAME_ATTACK_RESISTANCE, "Flame Attack Resistance", "Ignore weaker flame damage or resist the damage from more powerful flames."
      );
      this.addSkill(
         ResistanceSkills.GRAVITY_ATTACK_RESISTANCE,
         "Gravity Attack Resistance",
         "Ignore weaker gravity-based damage or reduce the damage from stronger attacks."
      );
      this.addSkill(ResistanceSkills.HEAT_RESISTANCE, "Heat Resistance", "Ignore weaker heat-based damage or reduce the damage from stronger heat sources.");
      this.addSkill(
         ResistanceSkills.HOLY_ATTACK_RESISTANCE, "Holy Attack Resistance", "Ignore weaker holy attacks or reduce the damage from stronger divine powers."
      );
      this.addSkill(
         ResistanceSkills.LIGHT_ATTACK_RESISTANCE, "Light Attack Resistance", "Ignore weaker light damage or reduce the damage from more powerful attacks."
      );
      this.addSkill(ResistanceSkills.MAGIC_RESISTANCE, "Magic Resistance", "Ignore weaker magical attacks or reduce the damage from stronger spells.");
      this.addSkill(
         ResistanceSkills.PARALYSIS_RESISTANCE, "Paralysis Resistance", "Ignore weaker paralysis effects or reduce the duration of stronger paralysis."
      );
      this.addSkill(ResistanceSkills.PAIN_RESISTANCE, "Pain Resistance", "Resist pain effects.");
      this.addSkill(ResistanceSkills.PIERCE_RESISTANCE, "Pierce Resistance", "Ignore weaker piercing damage or reduce the damage from stronger attacks.");
      this.addSkill(
         ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE, "Physical Attack Resistance", "Ignore weaker physical attacks or reduce the damage from stronger blows."
      );
      this.addSkill(ResistanceSkills.POISON_RESISTANCE, "Poison Resistance", "Ignore weaker poisons or reduce the damage from more potent toxins.");
      this.addSkill(
         ResistanceSkills.SPATIAL_ATTACK_RESISTANCE,
         "Spatial Attack Resistance",
         "Ignore weaker spatial attacks or reduce the damage from stronger dimensional strikes."
      );
      this.addSkill(ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE, "Spiritual Attack Resistance", "Ignore weaker spiritual attacks or resist stronger ones.");
      this.addSkill(
         ResistanceSkills.THERMAL_FLUCTUATION_RESISTANCE,
         "Thermal Fluctuation Resistance",
         "Ignore weaker thermal damage regardless of its type or resist stronger attacks."
      );
      this.addSkill(
         ResistanceSkills.WATER_ATTACK_RESISTANCE, "Water Attack Resistance", "Ignore weaker water-based damage or reduce the damage from stronger attacks."
      );
      this.addSkill(ResistanceSkills.WIND_ATTACK_RESISTANCE, "Wind Attack Resistance", "Ignore weaker wind damage or reduce the damage from stronger attacks.");
      this.addSkill(
         ResistanceSkills.ABNORMAL_CONDITION_NULLIFICATION, "Abnormal Condition Nullification", "Completely negate all abnormal conditions and their effects."
      );
      this.addSkill(ResistanceSkills.COLD_NULLIFICATION, "Cold Nullification", "Completely ignore cold damage.");
      this.addSkill(ResistanceSkills.CORROSION_NULLIFICATION, "Corrosion Nullification", "Completely negate all corrosion damage.");
      this.addSkill(ResistanceSkills.DARKNESS_ATTACK_NULLIFICATION, "Darkness Attack Nullification", "Completely nullify all darkness damage.");
      this.addSkill(ResistanceSkills.EARTH_ATTACK_NULLIFICATION, "Earth Attack Nullification", "Completely nullify all earth damage.");
      this.addSkill(ResistanceSkills.ELECTRICITY_NULLIFICATION, "Electricity Nullification", "Completely nullify all electricity damage.");
      this.addSkill(ResistanceSkills.FLAME_ATTACK_NULLIFICATION, "Flame Attack Nullification", "Completely nullify all flame damage.");
      this.addSkill(ResistanceSkills.GRAVITY_ATTACK_NULLIFICATION, "Gravity Attack Nullification", "Completely nullify all gravity damage.");
      this.addSkill(ResistanceSkills.HEAT_NULLIFICATION, "Heat Nullification", "Completely negate all heat damage.");
      this.addSkill(ResistanceSkills.HOLY_ATTACK_NULLIFICATION, "Holy Attack Nullification", "Completely nullify all holy damage.");
      this.addSkill(ResistanceSkills.LIGHT_ATTACK_NULLIFICATION, "Light Attack Nullification", "Completely nullify all light damage.");
      this.addSkill(ResistanceSkills.MAGIC_NULLIFICATION, "Magic Nullification", "Completely negate all magic damage.");
      this.addSkill(ResistanceSkills.PARALYSIS_NULLIFICATION, "Paralysis Nullification", "Completely negate all paralysis effects.");
      this.addSkill(ResistanceSkills.PIERCE_NULLIFICATION, "Pierce Nullification", "Completely negate all piercing damage.");
      this.addSkill(ResistanceSkills.PAIN_NULLIFICATION, "Pain Nullification", "Completely negate all pain effects.");
      this.addSkill(ResistanceSkills.PHYSICAL_ATTACK_NULLIFICATION, "Physical Attack Nullification", "Completely negate all physical damage.");
      this.addSkill(ResistanceSkills.POISON_NULLIFICATION, "Poison Nullification", "Completely nullify all poison damage.");
      this.addSkill(ResistanceSkills.SPATIAL_ATTACK_NULLIFICATION, "Spatial Attack Nullification", "Completely nullify all spatial damage.");
      this.addSkill(ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION, "Spiritual Attack Nullification", "Completely negate all spiritual damage.");
      this.addSkill(ResistanceSkills.THERMAL_FLUCTUATION_NULLIFICATION, "Thermal Fluctuation Nullification", "Completely negate all thermal damage.");
      this.addSkill(ResistanceSkills.WATER_ATTACK_NULLIFICATION, "Water Attack Nullification", "Completely nullify all water damage.");
      this.addSkill(ResistanceSkills.WIND_ATTACK_NULLIFICATION, "Wind Attack Nullification", "Completely nullify all wind damage.");
      this.addSkill(
         ExtraSkills.ALL_SEEING_EYE,
         "All-seeing Eye",
         "Go into a 3rd person mode, with your increased POV you gain movement and action buffs as well as an upgrade to Presence Sense."
      );
      this.addSkill(
         ExtraSkills.ANALYTICAL_APPRAISAL, "Analytical Appraisal", "Assess the strength of your opponents to gain insight into their overall strength."
      );
      this.addSkill(
         ExtraSkills.BLACK_FLAME,
         "Black Flame",
         "Command the tempest infused flames to spew out black flames at your enemies or even shoot deadly fireballs and hell flares."
      );
      this.addSkill(
         ExtraSkills.BLACK_LIGHTNING,
         "Black Lightning",
         "Shoot black lightning at varying strength, or summon a massive storm which attacks any non-allied mobs. A short range plasma blast can also be used to melt even the strongest of foes."
      );
      this.addSkill(
         ExtraSkills.BODY_DOUBLE,
         "Body Double",
         "Use a tenth of your magical power to summon an identical clone which takes increased damage but will fight for you until the end. The user can exchange places with the clone but risk taking increased damage. "
      );
      this.addSkill(
         ExtraSkills.CHANT_ANNULMENT, "Chant Annulment", "Become able to cast mastered magic instantly, in water or even under the effects of silence."
      );
      this.addSkill(ExtraSkills.DANGER_SENSE, "Danger Sense", "Receive a mental warning when entities with hostile intent enter your proximity.");
      this.addSkill(ExtraSkills.DEMON_LORD_HAKI, "Demon Lord Haki", "Unleash your Demonic aura causing those around you to quake in fear.");
      this.addSkill(ExtraSkills.EARTH_DOMINATION, "Earth Domination", "Boosts the power of  Earth abilities by a large amount.");
      this.addSkill(ExtraSkills.EARTH_MANIPULATION, "Earth Manipulation", "Boosts the power of Earth abilities by a decent amount.");
      this.addSkill(ExtraSkills.FLAME_DOMINATION, "Flame Domination", "Boosts the power of fire abilities by a large amount.");
      this.addSkill(ExtraSkills.FLAME_MANIPULATION, "Flame Manipulation", "Boosts the power of fire abilities by a decent amount.");
      this.addSkill(ExtraSkills.GODWOLF_SENSE, "Godwolf Sense", "Improve your senses to be able to see in the dark or even spot invisible entities nearby.");
      this.addSkill(ExtraSkills.GRAVITY_DOMINATION, "Gravity Domination", "Boosts Gravity abilities by a large amount and allows you to fly without hindrance.");
      this.addSkill(
         ExtraSkills.GRAVITY_MANIPULATION, "Gravity Manipulation", "Boosts Gravity abilities by a decent amount and allows you to fly without hindrance."
      );
      this.addSkill(ExtraSkills.HAKI, "Haki", "Project your aura to cow nearby foes into submission.");
      this.addSkill(ExtraSkills.HEAT_WAVE, "Heat Wave", "Shoot a fiery projectile or summon a short-ranged fire storm which will damage all nearby entities.");
      this.addSkill(
         ExtraSkills.HEAVENLY_EYE, "Heavenly Eye", "Project your otherworldly gaze to see all nearby entities and partially ignore dodging abilities."
      );
      this.addSkill(ExtraSkills.HERO_HAKI, "Hero Haki", "Use your Hero Aura to inspire your allies and terrify your enemies.");
      this.addSkill(
         ExtraSkills.INFINITE_REGENERATION,
         "Infinite Regeneration",
         "Use your massive amount of magicule to instantly regenerate from all but the most grievous of injuries."
      );
      this.addSkill(ExtraSkills.LAW_MANIPULATION, "Law Manipulation", "Bypass many restrictions by manipulating the law of the world.");
      this.addSkill(ExtraSkills.LIGHTNING_DOMINATION, "Lightning Domination", "Boosts Lightning abilities by a large amount.");
      this.addSkill(ExtraSkills.LIGHTNING_MANIPULATION, "Lightning Manipulation", "Boosts Lightning abilities by a decent amount.");
      this.addSkill(ExtraSkills.MAGIC_AURA, "Magic Aura", "Empower your attacks with many elemental aura.");
      this.addSkill(
         ExtraSkills.MAGIC_DARKNESS_TRANSFORM,
         "Magic Darkness Transform",
         "Gain access to the intermediate Darkness Spirit Magic and boost your darkness attacks while buffing yourself and debuffing enemies."
      );
      this.addSkill(
         ExtraSkills.MAGIC_EARTH_TRANSFORM,
         "Magic Earth Transform",
         "Gain access to the intermediate Earth Spirit Magic and boost your earth attacks while buffing yourself and debuffing enemies."
      );
      this.addSkill(
         ExtraSkills.MAGIC_FLAME_TRANSFORM,
         "Magic Flame Transform",
         "Gain access to the intermediate Flame Spirit Magic and boost your fire attacks while buffing yourself and debuffing enemies."
      );
      this.addSkill(ExtraSkills.MAGIC_JAMMING, "Magic Jamming", "Interferes with skills, magics, flight and transformation with mastery.");
      this.addSkill(
         ExtraSkills.MAGIC_LIGHT_TRANSFORM,
         "Magic Light Transform",
         "Gain access to the intermediate Light Spirit Magic and boost your light attacks while buffing yourself and debuffing enemies."
      );
      this.addSkill(ExtraSkills.MAGIC_SENSE, "Magic Sense", "Using magicule to sense entities around.");
      this.addSkill(
         ExtraSkills.MAGIC_SPACE_TRANSFORM,
         "Magic Space Transform",
         "Gain access to the intermediate Space Spirit Magic and boost your spatial attacks while buffing yourself and debuffing enemies."
      );
      this.addSkill(
         ExtraSkills.MAGIC_WATER_TRANSFORM,
         "Magic Water Transform",
         "Gain access to the intermediate Water Spirit Magic and boost your water attacks while buffing yourself and debuffing enemies."
      );
      this.addSkill(
         ExtraSkills.MAGIC_WIND_TRANSFORM,
         "Magic Wind Transform",
         "Gain access to the intermediate Wind Spirit Magic and boost your wind attacks while buffing yourself and debuffing enemies."
      );
      this.addSkill(ExtraSkills.MAJESTY, "Majesty", "By boosting your reputation with the villagers, gain a permanent Hero of the Village effect.");
      this.addSkill(
         ExtraSkills.MANA_MANIPULATION,
         "Mana Manipulation",
         "Grant better control on Mana with less magicule cost on casted Magics and less damage taken from magicule-based attacks."
      );
      this.addSkill(
         ExtraSkills.MOLECULAR_MANIPULATION,
         "Molecular Manipulation",
         "Use your intricate knowledge and power over the building blocks of the world to obtain blocks and move entities."
      );
      this.addSkill(ExtraSkills.MORTAL_FEAR, "Mortal Fear", "Inspire a gripping fear of death in weaker enemies while empowering your allies.");
      this.addSkill(ExtraSkills.MULTILAYER_BARRIER, "Multilayer Barrier", "Protect yourself with a multitude of powerful defensive barriers.");
      this.addSkill(ExtraSkills.SACRED_HAKI, "Sacred Haki", "Strike fear into the hearts of evil and give strength to your allies.");
      this.addSkill(ExtraSkills.SAGE, "Sage", "Increase the speed at which you learn and master skills, magic and arts.");
      this.addSkill(ExtraSkills.SENSE_HEAT_SOURCE, "Sense Heat Source", "Highlights entities that generate heat nearby.");
      this.addSkill(ExtraSkills.SENSE_SOUNDWAVE, "Sense Soundwave", "Empower your ears to precisely locate any nearby entities.");
      this.addSkill(ExtraSkills.SHADOW_MOTION, "Shadow Motion", "Melt into the shadows and move unseen by all but the most keen observers.");
      this.addSkill(ExtraSkills.SNAKE_EYE, "Snake Eye", "Apply various debuff on targets on sight.");
      this.addSkill(ExtraSkills.SOUND_DOMINATION, "Sound Domination", "Boosts Sound abilities by a large amount.");
      this.addSkill(ExtraSkills.SOUND_MANIPULATION, "Sound Manipulation", "Boosts Sound abilities by a decent amount.");
      this.addSkill(
         ExtraSkills.SPATIAL_DOMINATION,
         "Spatial Domination",
         "Boosts Space abilities by a large amount and deals massive damage by splitting a target with dimensional attacks."
      );
      this.addSkill(
         ExtraSkills.SPATIAL_MANIPULATION,
         "Spatial Manipulation",
         "Boosts Space abilities by a decent amount and manipulate space to empower your ranged attacks."
      );
      this.addSkill(ExtraSkills.SPATIAL_MOTION, "Spatial Motion", "Tear space asunder granting yourself and your allies safe passage.");
      this.addSkill(ExtraSkills.STICKY_STEEL_THREAD, "Stick Steel Thread", "Manipulate threads in various form.");
      this.addSkill(ExtraSkills.STEEL_STRENGTH, "Steel Strength", "Strengthen your muscles and gain an increase in damage, toggleable when mastered.");
      this.addSkill(ExtraSkills.STRENGTHEN_BODY, "Strengthen Body", "Strengthen your body to better protect yourself against attacks.");
      this.addSkill(
         ExtraSkills.THOUGHT_ACCELERATION,
         "Thought Acceleration",
         "Increase the speed at which you think to cast magic more quickly as well as to increase your reaction speed."
      );
      this.addSkill(ExtraSkills.ULTRA_INSTINCT, "Ultra-Instinct", "Enhance your sense to dodge physical impact.");
      this.addSkill(ExtraSkills.ULTRASPEED_REGENERATION, "Ultraspeed Regeneration", "Boost your bodies healing to nearly impossible levels ");
      this.addSkill(ExtraSkills.UNIVERSAL_PERCEPTION, "Universal Perception", "Combine all magic, sound and heat sense to detect entities around.");
      this.addSkill(ExtraSkills.WATER_DOMINATION, "Water Domination", "Boosts Water abilities by a large amount.");
      this.addSkill(ExtraSkills.WATER_MANIPULATION, "Water Manipulation", "Boosts Water abilities by a decent amount.");
      this.addSkill(ExtraSkills.WIND_DOMINATION, "Wind Domination", "Boosts Wind abilities by a large amount.");
      this.addSkill(ExtraSkills.WIND_MANIPULATION, "Wind Manipulation", "Boosts Wind abilities by a decent amount.");
      this.addSkill(ExtraSkills.WEATHER_DOMINATION, "Weather Domination", "Use magicule to manipulate the weather with a higher efficiency.");
      this.addSkill(ExtraSkills.WEATHER_MANIPULATION, "Weather Manipulation", "Use magicule to manipulate the weather.");
      this.addSkill(
         UniqueSkills.ABSOLUTE_SEVERANCE,
         "Absolute Severance",
         "Wield the power to cut through any who stand in your path, whether by coating your strikes or launching slashing projectiles that sever all in their path."
      );
      this.addSkill(
         UniqueSkills.ANALYST,
         "Analyst",
         "Enhance cognitive processing, analyze targets and phenomena, accelerate magic casting and deepen understanding of the laws of the world to optimize all actions."
      );
      this.addSkill(
         UniqueSkills.ANTI_SKILL,
         "Anti-Skill",
         "Become immune to damage from Magic, Skills and Battlewill, destroy barriers and block skill usage of other entities."
      );
      this.addSkill(
         UniqueSkills.BERSERKER,
         "Berserker",
         "Feed on your enemies' defeat. Gain EP from kills, destroy equipment faster, and boost your physical stats based on your power level."
      );
      this.addSkill(
         UniqueSkills.BERSERK,
         "Berserk",
         "Massively empower your body and infuse it with a flame aura. Allows you to go into a risky but overwhelmingly powerful berserker mode."
      );
      this.addSkill(UniqueSkills.BEWILDER, "Bewilder", "Manipulate friends and foes, compelling them to act according to your will.");
      this.addSkill(UniqueSkills.CHEF, "Chef", "Purify and renew. Remove all negative effects and restore vitality to those under your care.");
      this.addSkill(
         UniqueSkills.CHOSEN_ONE,
         "Chosen One",
         "Radiate authority and charisma to force fear into the hearts of your enemies and to make them follow you instead, while empowering yourself and your allies."
      );
      this.addSkill(
         UniqueSkills.COMMANDER, "Commander", "Lead the charge with your allies. Empower yourself and your allies to give yourself an overwhelming advantage."
      );
      this.addSkill(UniqueSkills.COOK, "Cook", "Bend reality to ensure your enemies meet their demise by ignoring their dodge and barriers.");
      this.addSkill(UniqueSkills.CREATOR, "Creator", "Create any Unique Skill and use it for a limited amount of time.");
      this.addSkill(UniqueSkills.DEGENERATE, "Degenerate", "Craft, decraft, customize items and absorb strength from weakened enemies.");
      this.addSkill(UniqueSkills.DIVINE_BERSERKER, "Divine Berserker", "Empower your body by a massive amount but beware the aftereffects. ");
      this.addSkill(UniqueSkills.ENGORGER, "Engorger", "Increase your size and boost your physical stats.");
      this.addSkill(UniqueSkills.ENVY, "Envy", "Absorb strength from enemies, buff yourself and debilitate your enemies.");
      this.addSkill(
         UniqueSkills.FALSIFIER, "Falsifier", "See through falsehoods and invisibility, conceal your presence from prying eyes and create illusions."
      );
      this.addSkill(
         UniqueSkills.FIGHTER,
         "Fighter",
         "Achieve mastery through discipline. Learn combat and magic abilities instantly, gain more mastery points, and hit much harder."
      );
      this.addSkill(UniqueSkills.FUSIONIST, "Fusionist", "Turn the world into a weapon, absorb the environment to create powerful mines and grenades.");
      this.addSkill(
         UniqueSkills.GLUTTONY,
         "Gluttony",
         "Absorb all in your path. None can win. Receive and provide skills, gain access to a spatial storage and mimic entities."
      );
      this.addSkill(
         UniqueSkills.GODLY_CRAFTSMAN,
         "Godly Craftsman",
         "Utilize your years of experience to create masterpieces with ease and engrave your weapons for terrifying efficiency."
      );
      this.addSkill(
         UniqueSkills.GOURMAND,
         "Gourmand",
         "Feast on the energy of your opponents. Steal magicule with your attacks and become more powerful when killing others."
      );
      this.addSkill(UniqueSkills.GOURMET, "Gourmet", "Absorb your enemies, gain new powers, and break down anything that stands in your way.");
      this.addSkill(
         UniqueSkills.GREAT_SAGE,
         "Great Sage",
         "Improve your cognitive skills to learn and cast faster, become able to appraise targets and use analysis to copy skills and process materials to craft or clone items or equipment."
      );
      this.addSkill(
         UniqueSkills.GREED,
         "Greed",
         "Take everything. Conquer the world and your enemies with it. Kill anyone who stands in your path while strengthening yourself or your allies."
      );
      this.addSkill(
         UniqueSkills.GUARDIAN,
         "Guardian",
         "Stand as a bastion against the force of your enemies. Absorb the damage from your allies and fortify their defenses."
      );
      this.addSkill(UniqueSkills.HEALER, "Healer", "Mend wounds with a touch or unleash devastating afflictions, capable of both salvation and suffering.");
      this.addSkill(
         UniqueSkills.INFINITY_PRISON,
         "Infinity Prison",
         "Trap enemies in an unbreakable dimensional cage or shield yourself from any threat with your dimensional barrier. Gain access to spatial storage."
      );
      this.addSkill(
         UniqueSkills.LUST, "Lust", "Assert your control over life and death. Drain your enemies of their strength or invigorate those under your wing."
      );
      this.addSkill(
         UniqueSkills.MARTIAL_MASTER,
         "Martial Master",
         "Empower your physical attacks, accelerate your thought process to react and dodge better while beating your enemies to submission. "
      );
      this.addSkill(
         UniqueSkills.MATHEMATICIAN,
         "Mathematician",
         "Use mathematics to improve your combat abilities, become able to use analytical appraisal to assess all."
      );
      this.addSkill(
         UniqueSkills.MERCILESS,
         "Merciless",
         "Instantly kill weakened enemies or drain the life force of those who lack the will to fight or of those much weaker than you."
      );
      this.addSkill(UniqueSkills.MURDERER, "Murderer", "Disappear into the shadows and deal massive damage, also become undetectable by sound.");
      this.addSkill(
         UniqueSkills.MUSICIAN, "Musician", "Turn beautiful music into an instrument of destruction. Use sound to create powerful blasts which ignore armor. "
      );
      this.addSkill(
         UniqueSkills.OBSERVER,
         "Observer",
         "See all, evade all. Instinctively avoid attacks, detect concealed dangers, and identify hidden entities. Nothing escapes your watchful gaze."
      );
      this.addSkill(
         UniqueSkills.OPPRESSOR,
         "Oppressor",
         "Dominate gravity yourself, control attractive and repulsive forces or unleash devastating ranged attacks which will crush anyone unfortunate enough to be in your path."
      );
      this.addSkill(
         UniqueSkills.PREDATOR,
         "Predator",
         "Become the monster you were meant to be. Eat all. Analyze, craft and refine items, gain access to a spatial storage and mimic entities."
      );
      this.addSkill(
         UniqueSkills.PRIDE,
         "Pride",
         "Turn the strength of your opponents into your own and turn the tides of battle. Copy abilities when you are struck by them, provided you meet the necessary conditions."
      );
      this.addSkill(
         UniqueSkills.REAPER,
         "Reaper",
         "Do recon by becoming smaller and more agile while seeing all enemies nearby. Spawn clones, or consume the essence of your enemies to obliterate them in a single strike."
      );
      this.addSkill(
         UniqueSkills.REVERSER,
         "Reverser",
         "Flip the rules. Change alignments, invert buffs and debuffs while shifting strengths and weaknesses to your benefit."
      );
      this.addSkill(
         UniqueSkills.REFLECTOR,
         "Reflector",
         "Turn back the tides of battle by reflecting all of the received damage. Unleash a devastating projectile attack or counter an attack directly."
      );
      this.addSkill(UniqueSkills.RESEARCHER, "Researcher", "Study ancient magic tomes, reverse engineer them, and create wonders beyond imagination.");
      this.addSkill(UniqueSkills.ROYAL_BEAST, "Royal Beast", "Unleash a primal fury and gain a massive physical boost to crush your enemies.");
      this.addSkill(
         UniqueSkills.SEEKER,
         "Seeker",
         "Accelerate thought processing to extreme levels, mastery over magics and manipulate the fundamental laws governing the world to optimize actions and outcomes."
      );
      this.addSkill(
         UniqueSkills.SEER,
         "Seer",
         "See everything. Foresee your opponent’s moves. Dodge or mitigate their attacks and predict their movement to score critical strikes."
      );
      this.addSkill(
         UniqueSkills.SEVERER, "Severer", "Slice through reality and manifest spatial blades which cut through armor and unleash devastating blade storms."
      );
      this.addSkill(
         UniqueSkills.SHADOW_STRIKER,
         "Shadow Striker",
         "Become one with the shadows and deal massive spiritual damage and become immune to lesser presence detection."
      );
      this.addSkill(
         UniqueSkills.SLOTH,
         "Sloth",
         "Grind the world to a halt. Put your enemies into a deadly sleep, drain their power and rest to regain any lost vitality. May lethargy take over."
      );
      this.addSkill(UniqueSkills.SNIPER, "Sniper", "Deal damage from afar while using different bullets to get past resistances and ensure a quick kill.");
      this.addSkill(UniqueSkills.SPEARHEAD, "Spearhead", "Empower and command your allies, and then collect their abilities once they pass on.");
      this.addSkill(
         UniqueSkills.STARVED,
         "Starved",
         "Consume all in your path or have your rampaging subordinates do it. Corrode your enemies, devour their strength, and adding their abilities to your own."
      );
      this.addSkill(
         UniqueSkills.SUPPRESSOR,
         "Suppressor",
         "Restrict teleportation, confuse enemies by swapping places, and use your blink as well as the spatial gate to cross large distances."
      );
      this.addSkill(UniqueSkills.SURVIVOR, "Survivor", "Endure and outheal anything, resist physical and natural damage and withstand overwhelming odds.");
      this.addSkill(
         UniqueSkills.THROWER,
         "Thrower",
         "Use your skill and your precision to throw anything and deal massive damage. You can even shove air or push back your foes."
      );
      this.addSkill(
         UniqueSkills.TRAVELER,
         "Traveler",
         "Move freely through space, teleport large distances or create spatial gates. Manipulate stardust to immense damage."
      );
      this.addSkill(UniqueSkills.TUNER, "Tuner", "Change your fate survive fatal blows, regenerate instantly and manipulate probability.");
      this.addSkill(
         UniqueSkills.UNYIELDING,
         "Unyielding",
         "Draw power from loyal allies. Fortify subordinates and seamlessly switch to a backup body to stay in the fight."
      );
      this.addSkill(
         UniqueSkills.USURPER,
         "Usurper",
         "Seize your enemies' power or their summons. Steal their abilities and take control of their skills, draining their strength for your own."
      );
      this.addSkill(
         UniqueSkills.VILLAIN,
         "Villain",
         "Embody malice and fight on the side of evil. Gain increased power from killing your foes, manipulate your enemies into joining your side and empower your allies."
      );
      this.addSkill(
         UniqueSkills.WRATH,
         "Wrath",
         "Unleash pure rage and use it to get infinitely stronger the longer your anger persists. Beware of the devastating drawback."
      );
   }

   private void skillModes() {
      this.add("tensura.skill.mode.reinforced_barrier.combined", "Combined Barrier");
      this.add("tensura.skill.mode.reinforced_barrier.magic", "Magic Defense");
      this.add("tensura.skill.mode.reinforced_barrier.physic", "Physical Defense");
      this.add("tensura.skill.mode.earth_lock.earth", "Earth Lock");
      this.add("tensura.skill.mode.earth_lock.self", "Self-lock");
      this.add("tensura.skill.mode.earth_wall.5x5x1", "5x5x1 Wall");
      this.add("tensura.skill.mode.earth_wall.5x5x2", "5x5x2 Wall");
      this.add("tensura.skill.mode.earth_wall.5x5x3", "5x5x3 Wall");
      this.add("tensura.skill.mode.earth_wall.10x10x1", "10x10x1 Wall");
      this.add("tensura.skill.mode.fire_lance.single", "Single");
      this.add("tensura.skill.mode.fire_lance.repeat", "Repeat");
      this.add("tensura.skill.mode.fire_storm.spread", "Spread");
      this.add("tensura.skill.mode.fire_storm.condensed", "Condensed");
      this.add("tensura.skill.mode.stone_shot.spread", "Spread Mode");
      this.add("tensura.skill.mode.stone_shot.chain", "Chain Mode");
      this.add("tensura.skill.mode.mud_spears.target", "Targeted Mode");
      this.add("tensura.skill.mode.mud_spears.spread", "Spread Mode");
      this.add("tensura.skill.mode.explosion.default", "Explosion Strike");
      this.add("tensura.skill.mode.explosion.trap", "Explosion Trap");
      this.add("tensura.skill.mode.float.projectile", "Projectile");
      this.add("tensura.skill.mode.spatial_storage.bag", "Spatial Bag");
      this.add("tensura.skill.mode.spatial_storage.dress", "Dress Change");
      this.add("tensura.skill.mode.freeze.frost_walk", "Frost Walk");
      this.add("tensura.skill.mode.icicle_lance.shot", "Icicle Shot");
      this.add("tensura.skill.mode.icicle_rain.repeat", "Repeat");
      this.add("tensura.skill.mode.thunder_lance.single", "Single");
      this.add("tensura.skill.mode.thunder_lance.rain", "Rain");
      this.add("tensura.skill.mode.thunder_rain.rain", "Rain");
      this.add("tensura.skill.mode.thunder_rain.coat", "Coating");
      this.add("tensura.skill.mode.water.1x1", "1x1 Water");
      this.add("tensura.skill.mode.water.3x3", "3x3 Water");
      this.add("tensura.skill.mode.water.5x5", "5x5 Water");
      this.add("tensura.skill.mode.water.10x10", "10x10 Water");
      this.add("tensura.skill.mode.water_cutter.repeat", "Repeat");
      this.add("tensura.skill.mode.wind_gust.gust", "Gust");
      this.add("tensura.skill.mode.airflow_shut.expand", "Expanded");
      this.add("tensura.skill.mode.megiddo.single", "Single Target");
      this.add("tensura.skill.mode.megiddo.autonomous", "Auto Target");
      this.add("tensura.skill.mode.search_enemy.constant", "Constant");
      this.add("tensura.skill.mode.create_undead.zombie", "Zombie");
      this.add("tensura.skill.mode.create_undead.skeleton", "Skeleton");
      this.add("tensura.skill.mode.create_undead.random", "Random Undead");
      this.add("tensura.skill.mode.summon_daemon.random", "Random Daemon");
      this.add("tensura.skill.mode.absolute_severance.coat", "Severance Coat");
      this.add("tensura.skill.mode.absolute_severance.projectile", "Severance Cutter");
      this.add("tensura.skill.mode.analyst.appraisal", "Analytical Appraisal");
      this.add("tensura.skill.mode.analyst.analyze", "Analyze");
      this.add("tensura.skill.mode.berserk.rage", "Rage");
      this.add("tensura.skill.mode.berserk.mad_ogre", "Mad Ogre");
      this.add("tensura.skill.mode.bewilder.target", "Target");
      this.add("tensura.skill.mode.bewilder.area", "Area");
      this.add("tensura.skill.mode.bewilder.charm", "Charm");
      this.add("tensura.skill.mode.bewilder.kill", "Kill");
      this.add("tensura.skill.mode.chosen_one.haki", "Hero's Haki");
      this.add("tensura.skill.mode.chosen_one.charisma", "Hero's Charisma");
      this.add("tensura.skill.mode.commander.movement_communication", "Communication: Movement");
      this.add("tensura.skill.mode.commander.targeting_communication", "Communication: Targeting");
      this.add("tensura.skill.mode.commander.thought_domination", "Thought Domination");
      this.add("tensura.skill.mode.cook.chaotic_fate", "Chaotic Fate");
      this.add("tensura.skill.mode.creator.analytical_appraisal", "Analytical Appraisal");
      this.add("tensura.skill.mode.creator.skill_creation", "Skill Creation");
      this.add("tensura.skill.mode.degenerate.crafting", "Crafting");
      this.add("tensura.skill.mode.degenerate.synthesise", "Synthesise");
      this.add("tensura.skill.mode.degenerate.separate", "Separate");
      this.add("tensura.skill.mode.envy.absorb", "Absorb");
      this.add("tensura.skill.mode.envy.strength_sap", "Strength Sap");
      this.add("tensura.skill.mode.falsifier.concealment", "Presence Concealment");
      this.add("tensura.skill.mode.falsifier.illusion", "Illusion");
      this.add("tensura.skill.mode.falsifier.fake_death", "Fake Death");
      this.add("tensura.skill.falsifier.clear_slot", "Clear Slot");
      this.add("tensura.skill.falsifier.empty", "Empty");
      this.add("tensura.skill.mode.fusionist.disassemble", "Disassemble");
      this.add("tensura.skill.mode.fusionist.fuse", "Fuse");
      this.add("tensura.skill.mode.fusionist.projectile", "Stone");
      this.add("tensura.skill.fusionist.matter_amount", "Fusionist Matters: %s");
      this.add("tensura.skill.fusionist.out_of_matter", "Out of matters to fuse.");
      this.add("tensura.skill.mode.great_sage.analytical_appraisal", "Analytical Appraisal");
      this.add("tensura.skill.mode.great_sage.analysis", "Analysis");
      this.add("tensura.skill.mode.great_sage.refine", "Refining");
      this.add("tensura.skill.mode.greed.spiritual", "Spiritual Domination");
      this.add("tensura.skill.mode.greed.flare", "Greed Flare");
      this.add("tensura.skill.mode.greed.death", "Death Wish");
      this.add("tensura.skill.mode.guardian.grant", "Grant Protection");
      this.add("tensura.skill.mode.guardian.iron_wall", "Iron Wall");
      this.add("tensura.skill.guardian.substitution_notification", "%s damage taken on behalf of %s");
      this.add("tensura.skill.mode.healer.heal", "Heal");
      this.add("tensura.skill.mode.healer.virus", "Infection");
      this.add("tensura.skill.mode.healer.plague", "Plague");
      this.add("tensura.skill.mode.infinity_prison.imprison", "Imprison");
      this.add("tensura.skill.mode.infinity_prison.imaginary_space", "Imaginary Space");
      this.add("tensura.skill.mode.lust.drain", "Drain");
      this.add("tensura.skill.mode.lust.invigorate", "Invigorate");
      this.add("tensura.skill.mode.lust.rebirth", "Rebirth");
      this.add("tensura.skill.mode.lust.embracing_drain", "Embracing Drain");
      this.add("tensura.skill.mode.lust.death_blessing", "Death Blessing");
      this.add("tensura.skill.mode.merciless.steal", "Soul Steal");
      this.add("tensura.skill.mode.merciless.consume", "Soul Consume");
      this.add("tensura.skill.mode.musician.sonic_blast", "Sonic Blast");
      this.add("tensura.skill.mode.musician.sound_wave", "Sound Wave");
      this.add("tensura.skill.mode.musician.mind_requiem", "Mind Requiem");
      this.add("tensura.skill.mode.observer.danger", "Danger Detection");
      this.add("tensura.skill.mode.observer.presence", "Presence Detection");
      this.add("tensura.skill.mode.observer.presence.all", "Changed Presence Sense Mode to All Entities.");
      this.add("tensura.skill.mode.observer.presence.monster", "Changed Presence Sense Mode to Monsters Only.");
      this.add("tensura.skill.mode.observer.presence.traps", "Changed Presence Sense Mode to Traps.");
      this.add("tensura.skill.mode.observer.presence.treasures", "Changed Presence Sense Mode to Treasures.");
      this.add("tensura.skill.mode.oppressor.repel", "Repel");
      this.add("tensura.skill.mode.oppressor.attract", "Attract");
      this.add("tensura.skill.mode.oppressor.oppress", "Oppress");
      this.add("tensura.skill.mode.oppressor.bleve", "Bleve");
      this.add("tensura.skill.mode.oppressor.flicker", "Flicker");
      this.add("tensura.skill.mode.predator.predation", "Predation");
      this.add("tensura.skill.mode.predator.analysis", "Analysis");
      this.add("tensura.skill.mode.predator.stomach", "Stomach");
      this.add("tensura.skill.mode.predator.mimicry", "Mimicry");
      this.add("tensura.skill.mode.predator.isolation", "Isolation");
      this.add("tensura.skill.predator.block_mode.none", "Changed %s's Block Consuming to None.");
      this.add("tensura.skill.predator.block_mode.blocks", "Changed %s's Block Consuming to Blocks.");
      this.add("tensura.skill.predator.block_mode.fluid", "Changed %s's Block Consuming to Fluid.");
      this.add("tensura.skill.predator.block_mode.all", "Changed %s's Block Consuming to All.");
      this.add("tensura.skill.mode.pride.copy", "Copy");
      this.add("tensura.skill.mode.reaper.attack", "Attack");
      this.add("tensura.skill.mode.reaper.eater", "Infinite Eater");
      this.add("tensura.skill.mode.reflector.reflection", "Echo Reflection");
      this.add("tensura.skill.mode.reflector.counter", "Echo Counter");
      this.add("tensura.skill.reflector.remaining_echo", "Echo Points: %s");
      this.add("tensura.skill.mode.reverser.reverse", "Alignment Reverse");
      this.add("tensura.skill.mode.reverser.buff", "Inverted Fusion [Buff]");
      this.add("tensura.skill.mode.reverser.debuff", "Inverted Fusion [Debuff]");
      this.add("tensura.skill.mode.severer.sword", "Dummy Sword");
      this.add("tensura.skill.mode.severer.blade_storm", "Blade Storm");
      this.add("tensura.skill.mode.severer.severance", "Severance");
      this.add("tensura.skill.mode.shadow_striker.ultra_acceleration", "Ultra Acceleration");
      this.add("tensura.skill.mode.shadow_striker.insta_kill", "Insta-kill");
      this.add("tensura.skill.mode.shadow_striker.espionage", "Espionage");
      this.add("tensura.skill.mode.sloth.deep_hypno", "Deep Hypno");
      this.add("tensura.skill.mode.sloth.fallen_hypno", "Fallen Hypno");
      this.add("tensura.skill.mode.sloth.deprive", "Deprive");
      this.add("tensura.skill.mode.sloth.rest", "Rest");
      this.add("tensura.skill.mode.sloth.phantasmal_style", "Phantasmal Style");
      this.add("tensura.skill.mode.sloth.fallen_strike", "Fallen Strike");
      this.add("tensura.skill.mode.sniper.weapon", "Create Weapon");
      this.add("tensura.skill.mode.sniper.spatial", "Spatial Manipulation");
      this.add("tensura.skill.mode.starved.corrosion", "Corrosion");
      this.add("tensura.skill.mode.starved.stomach", "Stomach");
      this.add("tensura.skill.mode.starved.receive", "Receive");
      this.add("tensura.skill.mode.starved.provide", "Provide");
      this.add("tensura.skill.mode.spiritual_domination", "Spiritual Domination");
      this.add("tensura.skill.mode.suppressor.blockade", "Spatial Suppression");
      this.add("tensura.skill.mode.suppressor.swap", "Swap");
      this.add("tensura.skill.mode.suppressor.motion", "Spatial Motion");
      this.add("tensura.skill.mode.traveler.instant_motion", "Instant Motion");
      this.add("tensura.skill.mode.traveler.teleport", "Teleport");
      this.add("tensura.skill.mode.traveler.stardust_arrow", "Stardust Arrow");
      this.add("tensura.skill.mode.traveler.stardust_rain", "Stardust Rain");
      this.add("tensura.skill.mode.unyielding.return", "Default");
      this.add("tensura.skill.mode.unyielding.backup", "Backup");
      this.add("tensura.skill.mode.unyielding.check_point", "The number of Unyielding Points with %s: %s");
      this.add("tensura.skill.mode.unyielding.backup_remove", "Backup vessel removed.");
      this.add("tensura.skill.mode.unyielding.backup_different_dimension", "The Backup vessel is in a different dimension.");
      this.add("tensura.skill.mode.usurper.rob", "Rob");
      this.add("tensura.skill.mode.usurper.copy", "Copy");
      this.add("tensura.skill.mode.usurper.force_takeover", "Force Takeover");
      this.add("tensura.skill.mode.villain.haki", "Demon Lord's Haki");
      this.add("tensura.skill.mode.villain.charisma", "Villain's Charisma");
      this.add("tensura.skill.mode.wrath.breader", "Breeder Reactor");
      this.add("tensura.skill.mode.wrath.enrage", "Enrage");
      this.add("tensura.skill.analytical.analyzing_mode.both", "Changed Analyzing Mode to All.");
      this.add("tensura.skill.analytical.analyzing_mode.entity", "Changed Analyzing Mode to Entities.");
      this.add("tensura.skill.analytical.analyzing_mode.block", "Changed Analyzing Mode to Blocks.");
      this.add("tensura.skill.mode.black_flame.breath", "Flame Breath");
      this.add("tensura.skill.mode.black_flame.ball", "Fireball");
      this.add("tensura.skill.mode.black_flame.hell_flare", "Hell Flare");
      this.add("tensura.skill.mode.black_flame.limited_hell_flare", "Limited Hell Flare");
      this.add("tensura.skill.mode.black_lightning.default", "Default Power");
      this.add("tensura.skill.mode.black_lightning.weak", "Decreased Power");
      this.add("tensura.skill.mode.black_lightning.strong", "Increased Power");
      this.add("tensura.skill.mode.black_lightning.blast", "Blast");
      this.add("tensura.skill.mode.black_lightning.storm", "Death Storm");
      this.add("tensura.skill.mode.blood_mist.ray", "Blood Ray");
      this.add("tensura.skill.mode.body_double.creation", "Clone Creation");
      this.add("tensura.skill.mode.body_double.control", "Clone Control");
      this.add("tensura.skill.mode.body_double.main_too_far", "The main body is too far away.");
      this.add("tensura.skill.mode.earth_manipulation.wall", "Earth Wall");
      this.add("tensura.skill.mode.earth_manipulation.break", "Blocks Break");
      this.add("tensura.skill.mode.earth_manipulation.pit", "Earth Pit");
      this.add("tensura.skill.mode.gravity_field.self", "Self-radius");
      this.add("tensura.skill.mode.gravity_field.5", "10x10 Field");
      this.add("tensura.skill.mode.gravity_field.10", "20x20 Field");
      this.add("tensura.skill.mode.haki.release", "Magicule Release");
      this.add("tensura.skill.mode.haki.coat", "Magicule Coat");
      this.add("tensura.skill.mode.heat_wave.sphere", "Heat Sphere");
      this.add("tensura.skill.mode.heat_wave.storm", "Heat Storm");
      this.add("tensura.skill.mode.law_manipulation.cleanse", "Abnormality Cleanse");
      this.add("tensura.skill.mode.law_manipulation.takeover", "Takeover");
      this.add("tensura.skill.mode.law_manipulation.takeover.success", "%s has been taken over.");
      this.add("tensura.skill.mode.magic_aura.default", "Default");
      this.add("tensura.skill.mode.magic_aura.holy", "Holy");
      this.add("tensura.skill.mode.magic_aura.earth", "Earth");
      this.add("tensura.skill.mode.magic_aura.fire", "Fire");
      this.add("tensura.skill.mode.magic_aura.space", "Space");
      this.add("tensura.skill.mode.magic_aura.water", "Water");
      this.add("tensura.skill.mode.magic_aura.wind", "Wind");
      this.add("tensura.skill.mode.molecular_manipulation.block", "Block");
      this.add("tensura.skill.mode.molecular_manipulation.entity", "Entity");
      this.add("tensura.skill.mode.shadow_motion.default", "Default");
      this.add("tensura.skill.mode.shadow_motion.step", "Shadow Step");
      this.add("tensura.skill.mode.shadow_motion.storage", "Shadow Storage");
      this.add("tensura.skill.mode.snake_eye.corrosion", "Corrosion");
      this.add("tensura.skill.mode.snake_eye.poison", "Poison");
      this.add("tensura.skill.mode.snake_eye.paralysis", "Paralysis");
      this.add("tensura.skill.mode.snake_eye.petrification", "Petrification");
      this.add("tensura.skill.mode.snake_eye.insanity", "Insanity");
      this.add("tensura.skill.mode.spatial_domination.warp_shot", "Warp Shot");
      this.add("tensura.skill.mode.spatial_domination.spatial_cleanse", "Spatial Cleanse");
      this.add("tensura.skill.mode.spatial_domination.ray", "Dimension Ray");
      this.add("tensura.skill.mode.spatial_domination.storm", "Dimension Storm");
      this.add("tensura.skill.mode.spatial_domination.fault_field", "Fault Field");
      this.add("tensura.skill.mode.spatial_motion.blink", "Blink");
      this.add("tensura.skill.mode.spatial_motion.warp", "Warp");
      this.add("tensura.skill.mode.sticky_steel_thread.sticky", "Sticky");
      this.add("tensura.skill.mode.sticky_steel_thread.steel", "Steel");
      this.add("tensura.skill.mode.sticky_steel_thread.slinger", "Slinger");
      this.add("tensura.skill.mode.sticky_steel_thread.arcane_thread", "Arcane Thread Fetters");
      this.add("tensura.skill.mode.thought_communication.movement", "Movement Behaviour");
      this.add("tensura.skill.mode.thought_communication.targeting", "Targeting Behaviour");
      this.add("tensura.skill.mode.ranged_barrier.5", "5x5x5 Barrier");
      this.add("tensura.skill.mode.ranged_barrier.10", "10x10x10 Barrier");
      this.add("tensura.skill.mode.ranged_barrier.20", "20x20x20 Barrier");
      this.add("tensura.skill.mode.ultrasonic_waves.sonic_boom", "Sonic Wave");
      this.add("tensura.skill.mode.ultrasonic_waves.auditory_sense", "Auditory Sense");
      this.add("tensura.skill.mode.weather_manipulation.clear", "Clear Weather");
      this.add("tensura.skill.mode.weather_manipulation.rain", "Rain");
      this.add("tensura.skill.mode.weather_manipulation.thunder", "Thunder Storm");
   }

   private void commandMessages() {
      this.add("tensura.argument.race.invalid", "Input Race was invalid.");
      this.add("tensura.argument.skill.invalid", "Input Instance was invalid.");
      this.add("tensura.command.despawn", "%s is despawned.");
      this.add("tensura.command.despawn.all", "%s entities are despawned.");
      this.add("tensura.command.syncStorage", "%s's Data Storage is synced.");
      this.add("tensura.command.syncStorage.all", "%s entities' Data Storages are synced.");
      this.add("tensura.command.reset.all", "%s is fully reset.");
      this.add("tensura.command.reset.awakening", "%s's Awakening Status is reset.");
      this.add("tensura.command.reset.race", "%s's Race is reset.");
      this.add("tensura.command.reset.skill", "%s's Skills are rerolled.");
      this.add("tensura.command.reset.schematic", "%s's Schematics and Advancements are reset.");
      this.add("tensura.command.reset.stat", "%s's Statistics is reset.");
      this.add("tensura.command.race.get", "%s's Race is %s.");
      this.add("tensura.command.race.no_race", "%s does not have a race.");
      this.add("tensura.command.race.edit", "%s has been reborn as %s.");
      this.add("tensura.command.race.alignment.get", "%s's Alignment is currently %s.");
      this.add("tensura.command.race.alignment.set", "%s's Alignment has been set to %s.");
      this.add("tensura.command.race.spiritual_form.set", "%s's Spiritual Form Status has been set to %s.");
      this.add("tensura.command.race.spiritual_form.get", "%s's Spiritual Form Status is currently %s.");
      this.add("tensura.command.race.evolve.fail", "Failed to evolve.");
      this.add("tensura.command.race.evolve.succeed", "Evolved successfully.");
      this.add("tensura.command.human_kill", "%s's Human Kill point has been set to %s.");
      this.add("tensura.command.human_kill.get", "%s's Human Kill point is currently %s.");
      this.add("tensura.command.demon_lord.soul", "%s's Soul Point has been set to %s.");
      this.add("tensura.command.demon_lord.soul.get", "%s's Soul Point is currently %s.");
      this.add("tensura.command.demon_lord.harvest_tick", "%s's Harvest Festival Tick has been set to %s.");
      this.add("tensura.command.demon_lord.harvest_tick.get", "%s's Harvest Festival Tick is currently %s.");
      this.add("tensura.command.demon_lord.seed", "%s's Demon Lord Seed status has been set to %s.");
      this.add("tensura.command.demon_lord.seed.get", "%s's Demon Lord Seed status is currently %s.");
      this.add("tensura.command.demon_lord.awakened", "%s's True Demon Lord status has been set to %s.");
      this.add("tensura.command.demon_lord.awakened.get", "%s's True Demon Lord status is currently %s.");
      this.add("tensura.command.hero.egg", "%s's Hero Egg status has been set to %s.");
      this.add("tensura.command.hero.egg.get", "%s's Hero Egg status is currently %s.");
      this.add("tensura.command.hero.awakened", "%s's True Hero status has been set to %s.");
      this.add("tensura.command.hero.awakened.get", "%s's True Hero status is currently %s.");
      this.add("tensura.command.aura.get", "%s's Aura is currently %s.");
      this.add("tensura.command.aura.get_max", "%s's Base amount of Max Aura is currently %s.");
      this.add("tensura.command.aura.set", "%s's Aura has been set to %s.");
      this.add("tensura.command.aura.set_max", "%s's Base amount of Max Aura has been set to %s.");
      this.add("tensura.command.magicule.get", "%s's Magicule is currently %s.");
      this.add("tensura.command.magicule.get_max", "%s's Base amount of Max Magicule is currently %s.");
      this.add("tensura.command.magicule.set", "%s's Magicule has been set to %s.");
      this.add("tensura.command.magicule.set_max", "%s's Base amount of Max Magicule has been set to %s.");
      this.add("tensura.command.ep.get", "%s's EP is currently %s.");
      this.add("tensura.command.ep.set", "%s's EP has been set to %s.");
      this.add("tensura.command.ep.get_max", "%s's Base amount of Max EP is currently %s.");
      this.add("tensura.command.ep.set_max", "%s's Base amount of Max EP has been set to %s.");
      this.add("tensura.command.spiritual.get", "%s's Spiritual Health is currently %s.");
      this.add("tensura.command.spiritual.set", "%s's Spiritual Health has been set to %s.");
      this.add("tensura.command.spiritual.get_max", "%s's Base amount of Max Spiritual Health is currently %s.");
      this.add("tensura.command.spiritual.set_max", "%s's Base amount of Max Spiritual Health has been set to %s.");
      this.add("tensura.command.severance.get", "%s's Severance Health amount is currently %s.");
      this.add("tensura.command.severance.set", "%s's Severance Health amount has been set to %s.");
      this.add("tensura.command.reset_counter.get", "%s's Reset Counter is %s.");
      this.add("tensura.command.reset_counter.set", "%s's Reset Counter has been set to %s.");
      this.add("tensura.command.reset_counter.check", "%s's Reset Counter progress:");
      this.add("tensura.command.reset_counter.check_next", "Next Reset progress:");
      this.add("tensura.command.reset_counter.check.race", "- Final Race Evolution: %s");
      this.add("tensura.command.reset_counter.check.awakening", "- Awakening Status: %s");
      this.add("tensura.command.reset_counter.check.boss", "- %s Defeated: %s");
      this.add("tensura.command.reset_counter.check.met", "%s's Reset Counter requirements are all met.");
      this.add("tensura.command.reset_counter.check.full", "All Reset Counter requirements are all met, use a %s to gain your reset counter point.");
      this.add("tensura.command.reset_counter.bonus_lock.get", "%s's Bonus Skill Lock is %s.");
      this.add("tensura.command.reset_counter.bonus_lock.set", "%s's Reset Counter has been set to %s.");
      this.add("tensura.command.reset_counter.lock", "%s has now been locked and saved for the next Reset.");
      this.add("tensura.command.reset_counter.lock.locked", "%s is already currently locked.");
      this.add("tensura.command.reset_counter.lock.not_locked", "%s is currently not locked.");
      this.add("tensura.command.reset_counter.lock.remove", "%s has no longer been locked and saved for the next Reset.");
      this.add("tensura.command.reset_counter.lock.clear", "Removed all locked skills.");
      this.add("tensura.command.reset_counter.lock.empty", "There is 0 locked skills.");
      this.add("tensura.command.reset_counter.lock.list", "List of locked skills: %s.");
      this.add("tensura.command.reset_counter.lock.disabled", "Reset Counter Skill Locking is currently disabled.");
      this.add("tensura.command.reset_counter.lock.unavailable", "%s is unable to be obtained for the next Reset with current settings.");
      this.add("tensura.command.reset_counter.lock.not_enough", "Unable to lock more skills.");
      this.add("tensura.command.sleep_mode.get", "%s's Sleep Mode second is currently %s.");
      this.add("tensura.command.sleep_mode.set", "%s's Sleep Mode second has been set to %s.");
      this.add("tensura.command.max_warp.get", "%s's Max Warp Points is %s.");
      this.add("tensura.command.max_warp.set", "%s's Max Warp Points has been set to %s.");
      this.add("tensura.command.reputation.get", "%s's Dwarven Reputation is %s.");
      this.add("tensura.command.reputation.set", "%s's Dwarven Reputation has been set to %s.");
      this.add("tensura.command.owner.name.get", "%s's Name is currently %s.");
      this.add("tensura.command.owner.name.get.no_name", "%s is not named.");
      this.add("tensura.command.owner.name.set", "%s's Name is set to %s.");
      this.add("tensura.command.owner.name.remove", "%s's Name is removed.");
      this.add("tensura.command.owner.permanent.get", "%s's Permanent Owner is currently %s.");
      this.add("tensura.command.owner.permanent.get.no_owner", "%s is currently not permanently owned by anyone.");
      this.add("tensura.command.owner.permanent.set", "%s's Permanent Owner is set to %s.");
      this.add("tensura.command.owner.permanent.remove", "%s's Permanent Owner is removed.");
      this.add("tensura.command.owner.temporary.get", "%s's Temporary Owner is currently %s.");
      this.add("tensura.command.owner.temporary.get.no_owner", "%s is currently not temporarily owned by anyone.");
      this.add("tensura.command.owner.temporary.set", "%s's Temporary Owner is set to %s.");
      this.add("tensura.command.owner.temporary.remove", "%s's Temporary Owner is removed.");
      this.add("tensura.command.owner.neutral.get", "%s is currently neutral to %s.");
      this.add("tensura.command.owner.neutral.get.empty", "%s is not currently neutral to anything.");
      this.add("tensura.command.owner.neutral.add", "%s is now neutral to %s.");
      this.add("tensura.command.owner.neutral.remove", "%s is no longer neutral to %s if true.");
      this.add("tensura.command.owner.neutral.clear", "%s is no longer neutral to anything if true.");
      this.add("tensura.skill.already_has", "%s already has %s.");
      this.add("tensura.skill.no_skill", "%s does not have %s.");
      this.add("tensura.skill.invalid", "%s was invalid.");
      this.add("tensura.skill.check_flight", "%s is Flying legitimately.");
      this.add("tensura.skill.check_flight.false", "%s is not Flying legitimately.");
      this.add("tensura.skill.granted", "%s has been granted to %s.");
      this.add("tensura.skill.granted_all", "%s instances have been granted to %s.");
      this.add("tensura.skill.revoked", "%s has been revoked from %s.");
      this.add("tensura.skill.revoked_all", "%s Instances have been revoked from %s.");
      this.add("tensura.skill.mastery_point", "%s's Mastery points of %s has been set to %s.");
      this.add("tensura.skill.mastery_point.all", "%s's Mastery points of %s abilities have been set to %s.");
      this.add("tensura.skill.mastery.no_changes", "%s's Mastery points have not been affected.");
      this.add("tensura.skill.mastery.above_max", "%s's maximum Mastery points are %s.");
      this.add("tensura.skill.set_cooldown", "%s's Cooldown of %s's Mode %s has been set to %s seconds.");
      this.add("tensura.skill.set_cooldown.all", "%s's Cooldown of all %s instances has been set to %s seconds.");
      this.add("tensura.skill.set_cooldown.no_changes", "%s's Cooldown have not been affected.");
      this.add("tensura.skill.set_cooldown.all.no_changes", "None of %s's abilities are affected.");
      this.add("tensura.skill.set_remove_time", "%s's Remove Time of %s has been set to %s seconds.");
      this.add("tensura.skill.set_remove_time.all", "%s's Remove Time of all instances has been set to %s seconds.");
      this.add("tensura.skill.set_remove_time.no_changes", "%s's Remove Time have not been affected.");
      this.add("tensura.skill.mode_learning.no_mode", "%s doesn't have Mode %s.");
      this.add("tensura.skill.mode_learning.no_learn", "Mode %s of %s is available without learning.");
      this.add("tensura.skill.mode_learning.no_learn_all", "All Modes of %s is available without learning.");
      this.add("tensura.skill.mode_learning.success", "Learn points in mode %s of %s has been set to %s for %s.");
      this.add("tensura.skill.mode_learning.success_all", "Learn points in %s modes of %s has been set to %s for %s.");
      this.add("tensura.skill.mode_learning.success_everything", "Learn points in every mode of %s instance has been set to Max for %s.");
      this.add("tensura.skill.empty_storage", "%s has no abilities.");
      this.add("tensura.skill.skill_set", "%s has been set on slot %s.");
      this.add("tensura.skill.skill_set.failed", "%s cannot be set in slots.");
      this.add("tensura.skill.do_not_have", "%s does not have %s.");
      this.add("tensura.skill.skill_get", "%s's %s has the following data: %s.");
      this.add("tensura.skill.skill_list", "%s's list of searched abilities: %s.");
      this.add("tensura.skill.skill_list.empty", "%s has no abilities met the requirement.");
      this.add("tensura.skill.toggle_on", "%s has been toggled on for %s.");
      this.add("tensura.skill.toggle_on.already", "%s is already toggled on for %s.");
      this.add("tensura.skill.toggle_all.on", "%s Skills have been toggled on for %s.");
      this.add("tensura.skill.toggle_off", "%s has been toggled off for %s.");
      this.add("tensura.skill.toggle_off.already", "%s is already toggled off for %s.");
      this.add("tensura.skill.toggle_all.off", "%s Skills have been toggled off for %s.");
      this.add("tensura.skill.toggle.failed", "%s cannot be toggled.");
      this.add("tensura.skill.spatial_storage.failed", "%s is not a Spatial Storage ability.");
      this.add("tensura.skill.spatial_storage.full", "%s's Spatial Storage is full.");
      this.add("tensura.command.spirit.blessed", "%s's Spirit Blessed status has been set to %s.");
      this.add("tensura.command.spirit.blessed.get", "%s's Spirit Blessed status is currently %s.");
      this.add("tensura.command.spirit.get", "%s's Spirit Level of %s is %s.");
      this.add("tensura.command.spirit.set", "%s's Spirit Level of %s has been set to %s.");
      this.add("tensura.command.spirit.clear", "Removed all spirits from %s.");
      this.add("tensura.command.spirit.cooldown.set", "%s's Spirit Praying cooldown has been set to %s.");
      this.add("tensura.command.spirit.cooldown.get", "%s's Spirit Praying cooldown is %s.");
      this.add("tensura.command.spirit.list", "%s's Levels in each Elemental Spirit are %s.");
      this.add("tensura.command.spirit.list.empty", "%s has no Spirits contracted.");
      this.add("tensura.command.force_evo.demon_lord", "%s was forced to go through True Demon Lord awakening.");
      this.add("tensura.command.force_evo.hero", "%s was forced to go through True Hero awakening.");
      this.add("tensura.command.force_evo.race", "%s was forced to evolve their race.");
      this.add("tensura.command.force_evo.race.fail", "%s failed to evolve.");
      this.add("tensura.command.force_evo.race.fail_specific", "%s failed to evolve to %s.");
      this.add("tensura.summon.end", "%s's Summoning contract has ended.");
      this.add("tensura.summon.time_out", "The Summoning contract's duration has ended.");
      this.add("tensura.summon.stolen", "Your summon [%s] has been taken over by %s.");
      this.add("tensura.summon.disobey", "Your summon [%s] has decided that you are not worth their loyalty.");
      this.add("tensura.telepathy.subordinate.not_found", "You need to look at a subordinate or a pet.");
      this.add("tensura.telepathy.subordinate_all.no_target", "You need to look at a target to attack.");
      this.add("tensura.telepathy.subordinate_all.not_found", "Can not find any subordinate reachable from your place.");
      this.add("tensura.telepathy.subordinate_all.success", "All reached subordinates have listened to your command.");
      this.add("tensura.telepathy.subordinate_all.stay", "All reached subordinates have been ordered to Stay.");
      this.add("tensura.telepathy.subordinate_all.follow", "All reached subordinates have been ordered to Follow.");
      this.add("tensura.telepathy.subordinate_all.wander", "All reached subordinates have been ordered to Wander.");
      this.add("tensura.telepathy.subordinate_all.rampage", "All reached subordinates have been ordered to Rampage.");
      this.add("tensura.telepathy.subordinate_all.meat_shield", "All reached subordinates have been ordered to form a Meat Shield.");
      this.add("tensura.telepathy.subordinate_all.neutral", "All reached subordinates' behaviour have been set to Neutral.");
      this.add("tensura.telepathy.subordinate_all.passive", "All reached subordinates' behaviour have been set to to Passive.");
      this.add("tensura.telepathy.subordinate_all.aggressive", "All reached subordinates' behaviour have been set to to Aggressive.");
      this.add("tensura.telepathy.subordinate_all.protect", "All reached subordinates' behaviour have been set to to Protect.");
      this.add("tensura.truly_unique.off", "The gamerule Truly Unique is off.");
      this.add("tensura.truly_unique.added", "%s is added to the list with %s as owner.");
      this.add("tensura.truly_unique.already_have", "%s is in the list, owned by %s.");
      this.add("tensura.truly_unique.do_not_have", "%s isn't in the list.");
      this.add("tensura.truly_unique.removed", "%s is removed from the list, previously occupied by %s.");
      this.add("tensura.truly_unique.list.taken", "List of Unique Skill acquired: %s.");
      this.add("tensura.truly_unique.list.taken.empty", "No Unique Skill has been acquired.");
      this.add("tensura.truly_unique.list.remain", "List of Unique Skill remained: %s.");
      this.add("tensura.truly_unique.list.remain.empty", "No Unique Skill is remained.");
      this.add("tensura.command.area_magicule.get", "The current Magicule Level of this chunk is %s.");
      this.add("tensura.command.area_magicule.set", "The current Magicule Level of this chunk is now %s.");
      this.add("tensura.command.area_magicule.get.max", "The Maximum Magicule Level of this chunk is %s.");
      this.add("tensura.command.area_magicule.set.max", "The base Maximum Magicule Level of this chunk is now %s.");
      this.add("tensura.command.area_magicule.get.regeneration", "The Magicule Regeneration Rate of this chunk is %s.");
      this.add("tensura.command.area_magicule.set.regeneration", "The base Magicule Regeneration Rate of this chunk is now %s.");
      this.add("tensura.command.area_magicule.clear_block_modifiers", "Cleared all block magicule modifiers of this chunk.");
      this.add("tensura.command.area_magicule.clear_block_modifiers.radius", "Cleared all block magicule modifiers of %s chunks.");
      this.add("tensura.command.area_magicule.reinitialized", "Reinitialized magicule chunk data of %s chunks.");
      this.add("tensura.command.labyrinth.generating", "The Labyrinth is being generated by the Fairy Queen, please stand by.");
      this.add("tensura.command.labyrinth.entrance.get", "The Labyrinth's entrance point is currently %s, %s, %s");
      this.add("tensura.command.labyrinth.entrance.set", "The Labyrinth's entrance point is set to %s, %s, %s");
      this.add("tensura.command.labyrinth.passed_entrance.get", "The Labyrinth's entrance point when passed is currently %s, %s, %s");
      this.add("tensura.command.labyrinth.passed_entrance.set", "The Labyrinth's entrance point when passed is set to %s, %s, %s");
      this.add("tensura.command.labyrinth.colossus_pos.get", "The Labyrinth's Colossus spawn point is currently %s, %s, %s");
      this.add("tensura.command.labyrinth.colossus_pos.set", "The Labyrinth's Colossus spawn point is set to %s, %s, %s");
      this.add("tensura.command.labyrinth.area_radius.get", "The Labyrinth's Colossus Area radius is currently %s");
      this.add("tensura.command.labyrinth.area_radius.set", "The Labyrinth's Colossus Area radius is set to %s");
      this.add(
         "tensura.command.labyrinth.void_save.get", "The Labyrinth's location to teleport the entity to when falling into the void is currently %s, %s, %s"
      );
      this.add("tensura.command.labyrinth.void_save.set", "The Labyrinth's location to teleport the entity to when falling into the void is set to %s, %s, %s");
      this.add(
         "tensura.command.labyrinth.void_save_passed.get",
         "The Labyrinth's location to teleport the entity to when falling into the void when passed the colossus is currently %s, %s, %s"
      );
      this.add(
         "tensura.command.labyrinth.void_save_passed.set",
         "The Labyrinth's location to teleport the entity to when falling into the void when passed the colossus is set to %s, %s, %s"
      );
      this.add("tensura.command.labyrinth.void_height.get", "The Labyrinth's Y value to determine that the entity has fallen into the void is currently %s");
      this.add("tensura.command.labyrinth.void_height.set", "The Labyrinth's Y value to determine that the entity has fallen into the void is set to %s");
      this.add("tensura.command.labyrinth.passed_list.check_true", "%s is currently marked as passed the Labyrinth's Elemental Colossus");
      this.add("tensura.command.labyrinth.passed_list.check_false", "%s is currently not marked as passed the Labyrinth's Elemental Colossus");
      this.add("tensura.command.labyrinth.passed_list.won.check_true", "%s is currently marked as won the Labyrinth's Elemental Colossus");
      this.add("tensura.command.labyrinth.passed_list.won.check_false", "%s is currently not marked as won the Labyrinth's Elemental Colossus");
      this.add("tensura.command.labyrinth.passed_list.add", "%s is now marked as passed the Labyrinth's Elemental Colossus");
      this.add("tensura.command.labyrinth.passed_list.remove", "%s is no longer marked as passed the Labyrinth's Elemental Colossus");
      this.add("tensura.command.labyrinth.passed_list.won", "%s's mark as won the Labyrinth's Elemental Colossus is now set to %s");
      this.add("tensura.command.labyrinth.colossus_spawned.get", "The Labyrinth is currently marked as having an Elemental Colossus");
      this.add("tensura.command.labyrinth.colossus_spawned.set_true", "The Labyrinth is now marked as having an Elemental Colossus");
      this.add("tensura.command.labyrinth.colossus_spawned.set_false", "The Labyrinth is no longer marked as having an Elemental Colossus");
      this.add("tensura.command.labyrinth.regenerate", "The Labyrinth is set to regenerate during the next Server/World launch.");
      this.add("tensura.command.labyrinth.regenerate.false", "The Labyrinth is set to not regenerate during the next Server/World launch.");
      this.add("tensura.command.labyrinth.regenerate.immediately", "The Labyrinth will regenerate now. Expect brief, severe lag until it's complete.");
      this.add("tensura.command.labyrinth.regenerate.immediately.failure", "The Labyrinth is already being generated. Please wait.");
   }

   private void menuTexts() {
      this.add("soundCategory.ability", "Tensura Abilities");
      this.add("tensura.settings", "Settings");
      this.add("tensura.settings.true", "True");
      this.add("tensura.settings.false", "False");
      this.add("tensura.settings.left", "Left");
      this.add("tensura.settings.right", "Right");
      this.add("tensura.settings.dynamic", "Dynamic");
      this.add("tensura.settings.save", "Save Changes");
      this.add("tensura.settings.reset", "Reset To Default");
      this.add("tensura.settings.edit", "Edit Position / Scale");
      this.addSettingCategory("global", "GLOBAL");
      this.addSettingCategory("menu", "MENU");
      this.addSettingCategory("status", "HUD -> STATUS");
      this.addSettingCategory("status_bars", "HUD -> STATUS BARS");
      this.addSettingCategory("abilities", "HUD -> ABILITIES");
      this.addSettingCategory("analysis", "HUD -> ANALYSIS");
      this.addSettingCategory("decorations", "HUD -> DECORATIONS");
      this.addSettingCategory("miscellaneous", "MISCELLANEOUS");
      this.addSetting("modifyFov", "Modify Field of View", "Should FOV be slightly modified (zoomed out) upon opening a menu", "Default: True");
      this.addSetting(
         "fadeEffects",
         "Enable Fading",
         "Should menus have a fade-in-out effect upon opening and closing them",
         "It takes 10 ticks / half a second for the effect to end before you can do anything",
         "Default: True"
      );
      this.addSetting(
         "scale",
         "Scale Multiplier (Coming Soon)",
         "The value by which to multiply the scale of every menu and its elements (Vanilla options also affect menu scale)",
         "Default: 1.0"
      );
      this.addSetting("blur", "Blur Strength", "How much should the background be blurred", "Default: 1", "0 ~ 5");
      this.addSetting(
         "autoAbilitySlot", "Auto Ability Slots", "Should the ability slots/bars are automatically shown when opening ability GUIS", "Default: False"
      );
      this.addSetting(
         "cameraShakeStrength", "Camera Shake Strength", "The strength multiplier of the Camera shake effect from Tensura's feature", "Default: 1 (100%)"
      );
      this.addSetting("arachnophobia", "Arachnophobia", "Should Tensura spider entities have more friendly textures", "Default: False");
      this.addSetting(
         "dinnerbone", "Dinnerbone", "Flip entities (even players) upside down visually when named \"Dinnerbone\" with Tensura Naming", "Default: False"
      );
      this.addSetting("title_screen", "Tensura Title Screen", "Whether should Tensura: Reincarnated's custom title screen should render", "Default: True");
      this.addSetting("tensuraHud", "Enable Tensura HUD", "Controls if Tensura HUD elements should be rendered at all or not", "Default: True");
      this.addSetting("vanillaHud", "Enable Vanilla HUD", "Controls if Vanilla HUD elements should be rendered at all or not", "Default: False");
      this.addSetting("renderStatus", "Render Status", "Separate check if the Status element should render", "Default: True");
      this.addSetting("defaultStatus", "Default Status Rendering", "If true, dynamically adjusts the element", "Default: True");
      this.addSetting(
         "sideStatus",
         "Status Side",
         "Setting to Left will make the element to render from left to right and vice versa if Right",
         "Setting to Dynamic will allow the renderer to decide dynamically",
         "Will also move other elements that have default rendering set to true",
         "Default: Dynamic"
      );
      this.addSetting("editStatus", "Edit Status Position / Scale", "Ignored if default Rendering is true");
      this.addSetting("renderStatusBars", "Render Status Bars", "Separate check if the Status Bars element should render", "Default: True");
      this.addSetting("defaultStatusBars", "Default Status Bars Rendering", "If true, dynamically adjusts the element", "Default: True");
      this.addDefaultSideSetting("sideStatusBars", "Status Bars Side");
      this.addSetting("editStatusBars", "Edit Status Bars Position / Scale", "Ignored if default rendering is true");
      this.addSetting("renderAbilities", "Render Abilities", "Separate check if the Abilities element should render", "Default: True");
      this.addSetting("defaultAbilities", "Default Abilities Rendering", "If true, dynamically adjusts the element", "Default: True");
      this.addDefaultSideSetting("sideAbilities", "Abilities Side");
      this.addSetting("editAbilities", "Edit Abilities Position / Scale", "Ignored if default Rendering is true");
      this.addSetting("renderAnalysis", "Render Analysis", "Separate check if the Analysis element should render", "Default: True");
      this.addSetting("defaultAnalysis", "Default Analysis Rendering", "If true, dynamically adjusts the element", "Default: True");
      this.addSetting("heartsAnalysis", "Use Hearts", "If true, will render target's hearts instead of HP (1 heart = 2 HP)", "Default: False");
      this.addDefaultSideSetting("sideAnalysis", "Analysis Side");
      this.addSetting("editAnalysis", "Edit Analysis Position / Scale", "Ignored if default rendering is true");
      this.addSetting("opacityAnalysis", "Edit Analysis Opacity", "How opaque should Analysis be", "Default: 1.0", "0 ~ 1.0");
      this.addSetting("renderAirDeco", "Render Air", "Separate check if the Air Decoration should render", "Default: True");
      this.addSetting("defaultAirDeco", "Default Air Rendering", "If true, dynamically adjusts the element", "Default: True");
      this.addDefaultSideSetting("sideAirDeco", "Air Side");
      this.addSetting("editAirDeco", "Edit Air Position / Scale", "Ignored if default rendering is true");
      this.addSetting("renderFoodDeco", "Render Food", "Separate check if the Food Decoration should render", "Default: True");
      this.addSetting("defaultFoodDeco", "Default Food Rendering", "If true, dynamically adjusts the element", "Default: True");
      this.addDefaultSideSetting("sideFoodDeco", "Food Side");
      this.addSetting("editFoodDeco", "Edit Food Position / Scale", "Ignored if default rendering is true");
      this.addSetting("renderArmorDeco", "Render Armor", "Separate check if the Armor Decoration should render", "Default: True");
      this.addSetting("defaultArmorDeco", "Default Armor Rendering", "If true, dynamically adjusts the element", "Default: True");
      this.addDefaultSideSetting("sideArmorDeco", "Armor Side");
      this.addSetting("editArmorDeco", "Edit Armor Position / Scale", "Ignored if default rendering is true");
      this.addSetting("renderBarrierDeco", "Render Barrier", "Separate check if the Barrier Decoration should render", "Default: True");
      this.addSetting("defaultBarrierDeco", "Default Barrier Rendering", "If true, dynamically adjusts the element", "Default: True");
      this.addDefaultSideSetting("sideBarrierDeco", "Barrier Side");
      this.addSetting("editBarrierDeco", "Edit Barrier Position / Scale", "Ignored if default rendering is true");
      this.addSetting("renderMountHpDeco", "Render Mount HP", "Separate check if the Mount Health Decoration should render", "Default: True");
      this.addSetting("defaultMountHpDeco", "Default Mount HP Rendering", "If true, dynamically adjusts the element", "Default: True");
      this.addDefaultSideSetting("sideMountHpDeco", "Mount HP Side");
      this.addSetting("editMountHpDeco", "Edit Mount Health Position / Scale", "Ignored if default rendering is true");
      this.addSetting(
         "renderMountShpDeco", "Render Mount Spiritual HP", "Separate check if the Mount Spiritual Health Decoration should render", "Default: True"
      );
      this.addSetting("defaultMountShpDeco", "Default Mount Spiritual HP Rendering", "If true, dynamically adjusts the element", "Default: True");
      this.addDefaultSideSetting("sideMountShpDeco", "Mount Spiritual HP Side");
      this.addSetting("editMountShpDeco", "Edit Mount Spiritual HP Position / Scale", "Ignored if default rendering is true");
      this.add("tensura.spellbinding.label", "Spellbinding");
      this.add("tensura.anvil.menu.open", "Open Anvil");
      this.add("tensura.jei.smithing.title", "Smithing");
      this.add("tensura.smithing_bench.menu.open", "Open Smithing Bench");
      this.add("tensura.smithing_table.menu.open", "Open Smithing Table");
      this.add("tooltip.tensura.smithing_table.lack_schematic", "Schematic required.");
      this.add("tensura.jei.woodcutting.title", "Woodcutting");
      this.add("tensura.kiln.label", "Kiln");
      this.add("tensura.kiln.smeltery_label", "Smeltery");
      this.add("tooltip.tensura.kiln.magisteel_output", "Convert to Pure Magisteel");
      this.add("tooltip.tensura.kiln.molten_output", "Convert to Ingot/Nugget");
      this.add("tooltip.tensura.kiln.mixing_left", "Previous");
      this.add("tooltip.tensura.kiln.mixing_right", "Next");
      this.add("tooltip.tensura.kiln.molten_item", "%s Molten %s");
      this.add("tooltip.tensura.kiln.empty", "Empty");
      this.add("tensura.jei.melting.title", "Melting");
      this.add("tensura.jei.mixing.title", "Mixing");
      this.add("tensura.jei.smelting.title", "Smelting");
      this.add("tensura.reincarnation", "Reincarnation");
      this.add("tensura.reincarnation.submit", "Select Race");
      this.add("tensura.reincarnation.infobox.1", "Difficulty: %s\nAura Range: %s - %s\nMagicule Range: %s - %s");
      this.add("tensura.reincarnation.infobox.2", "Intrinsic Skills: %s");
      this.add("tensura.reincarnation.random", "Random");
      this.add("tensura.reincarnation.unknown", "Unknown");
      this.add(
         "tensura.reincarnation.random_race",
         "A race randomly selected from a configurable list of options, adding an element of unpredictability to the experience. Are you willing to take the risk?"
      );
      this.add(
         "tooltip.tensura.wip",
         "This feature is a work in progress. It will likely not work as intended and how it currently looks and works may be subject to change in the future."
      );
      this.add("tooltip.tensura.coming_soon", "Coming Soon");
      this.add("tooltip.tensura.coming_soon_feature", "Coming Soon: %s");
      this.add("tooltip.tensura.creative_only", "Creative Mode Only");
      this.add("tooltip.tensura.return", "Return");
      this.add("tensura.skill_menu", "Skills");
      this.add("tensura.magic_menu", "Magic");
      this.add("tensura.battlewill_menu", "Battlewill");
      this.add("tensura.ability_selection", "Ability Selection");
      this.add("tensura.ability_selection.mastery", "Mastery: %s");
      this.add("tensura.ability_selection.learning", "Learn Points: %s");
      this.add("tensura.ability_selection.untoggleable", "Untoggleable");
      this.add("tensura.ability_selection.origin", "[Mod: %s]");
      this.add("tensura.ability_selection.suggestion", "Name or \"f:\"");
      this.add("tensura.ability_selection.cooldowns", "[Cooldowns]");
      this.add("tensura.ability.on_cooldown", "Cooldown: %ss");
      this.add("tensura.ability_selection.lock", "Lock (%s/%s)");
      this.add("tensura.ability_selection.unlock", "Unlock");
      this.add("tensura.ability_selection.lock.wrong", "Lock Requirements Not Met");
      this.add("tensura.ability_selection.lock.not_enough", "More Lock Points Required (%s/%s)");
      this.add("tensura.main_menu", "Status Menu");
      this.add("tensura.main_menu.speed", "Speed: %s");
      this.add("tensura.main_menu.souls", "Souls: %s");
      this.add("tensura.main_menu.magicule", "Magicule: %s/%s");
      this.add("tensura.main_menu.aura", "Aura: %s/%s");
      this.add("tensura.main_menu.existence_points", "EP: %s");
      this.add("tensura.main_menu.health", "HP: %s");
      this.add("tensura.main_menu.spiritual_health", "SHP: %s");
      this.add("tensura.main_menu.armor", "Armor: %s");
      this.add("tensura.main_menu.reset_counter", "Reset Counter: %s");
      this.add("tensura.main_menu.evolution_progress", "Evolution Progress: %s%%");
      this.add("tensura.main_menu.evolution_progress.ready", "Evolution Available");
      this.add("tensura.main_menu.evolution_progress.select", "Select Evolution");
      this.add("tensura.main_menu.evolution_progress.none", "No Evolutions");
      this.add("tensura.main_menu.billion_index", "%sB");
      this.add("tensura.main_menu.million_index", "%sM");
      this.add("tensura.main_menu.tdl_awaken", "Become Demon Lord");
      this.add("tensura.main_menu.th_awaken", "Become Chosen Hero");
      this.add("tensura.evolution_menu", "Evolution Menu");
      this.add("tensura.evolution_menu.track", "Track");
      this.add("tensura.evolution_menu.evolve", "Evolve");
      this.add("tensura.evolution_menu.tracked", "TRACKED");
      this.add("tensura.evolution_menu.requirements", "Requirements");
      this.add("tensura.evolution_menu.ep_requirement", "Reach Existence Points of %s");
      this.add("tensura.evolution_menu.cure_requirement", "Get cured with Weakness and Enchanted Golden Apple");
      this.add("tensura.evolution_menu.acquire_requirement", "Acquire %s");
      this.add("tensura.evolution_menu.mastery_requirement", "Master %s");
      this.add("tensura.evolution_menu.boss_kill_requirement", "Kill %s bosses");
      this.add("tensura.evolution_menu.carrying_requirement", "Carrying %s of %s");
      this.add("tensura.evolution_menu.consume_requirement", "Consume %s of %s");
      this.add("tensura.evolution_menu.name_requirement", "Be named");
      this.add("tensura.evolution_menu.awaken_requirement", "Awaken [%s/%s]");
      this.add("tensura.evolution_menu.physical_body_requirement", "Have a physical body");
      this.add("tensura.evolution_menu.spirit_requirement", "Obtain a Spirit");
      this.add("tensura.evolution_menu.specific_kill_requirement", "Defeat %s(-es)");
      this.add("tensura.evolution_menu.battle_mob_requirement", "Battle %s(-s)");
      this.add("tensura.degenerate_menu.synthesis", "Synthesis");
      this.add("tensura.degenerate_menu.separation", "Separation");
      this.add("tensura.degenerate_menu.crafting", "Crafting");
      this.add("tensura.degenerate_menu.uncrafting", "Uncrafting");
      this.add("tooltip.tensura.degenerate_menu.tab_1", "Crafting & Uncrafting");
      this.add("tooltip.tensura.degenerate_menu.tab_2", "Synthesis & Separation");
      this.add("tensura.spatial_menu", "Spatial Movement");
      this.add("tensura.spatial_menu.points_tab", "Warp Points");
      this.add("tensura.spatial_menu.pads_tab", "Warp Pads");
      this.add("tensura.spatial_menu.get_coordinates", "Insert current coordinates");
      this.add("tensura.spatial_menu.insert_axis", "Insert current %s");
      this.add("tensura.spatial_menu.select_dimension", "Select dimension");
      this.add("tensura.spatial_menu.portal", "Portal");
      this.add("tensura.spatial_menu.warp", "Warp");
      this.add("tensura.spatial_menu.save", "Save location");
      this.add("tensura.spatial_menu.edit", "Edit location");
      this.add("tensura.spatial_menu.edit_warp", "Edit Warp Point");
      this.add("tooltip.tensura.great_sage_menu.automate", "Automate");
      this.add("tooltip.tensura.great_sage_menu.crafting", "Crafting");
      this.add("tooltip.tensura.great_sage_menu.refining", "Refining");
      this.add("tooltip.tensura.great_sage_menu.brew", "Start Brewing");
      this.add("tensura.skill_creator.create_skill", "Create");
      this.add("tensura.researcher_menu.storage_tab", "Spatial Storage");
      this.add("tensura.researcher_menu.enchantment_tab", "Enchanting");
      this.add("tensura.researcher_menu.enchantment_tab.xp_cost", "Xp cost: %s points");
      this.add("tensura.dialogue_window.edit", "Edit");
      this.add("tensura.dialogue_window.save", "Save");
      this.add("tensura.dialogue_window.load", "Load");
      this.add("tensura.dialogue_window.load.specific", "Load %s");
      this.add("tensura.dialogue_window.cancel", "Cancel");
      this.add("tensura.dialogue_window.delete", "Delete");
      this.add("tensura.dialogue_window.rename", "Rename");
      this.add("tensura.dialogue_window.update", "Update");
      this.add("tensura.dialogue_window.confirm", "Confirm");
      this.add("tensura.dialogue_window.save_pos", "Save Location");
      this.add("tensura.dialogue_window.save_name", "Save Name");
   }

   private void moltenMaterials() {
      this.add("tensura.molten.copper.material", "Copper");
      this.add("tensura.molten.iron.material", "Iron");
      this.add("tensura.molten.silver.material", "Silver");
      this.add("tensura.molten.gold.material", "Gold");
      this.add("tensura.molten.magisteel.material", "Magisteel");
      this.add("tensura.molten.netherite.material", "Netherite");
   }

   private void chatMessages() {
      this.add("tensura.message.enabled", "True");
      this.add("tensura.message.disabled", "False");
      this.add("tensura.evolve.demon_lord", "True Demon Lord.");
      this.add("tensura.evolve.demon_lord.seed", "You became a Demon Lord Seed.");
      this.add("tensura.evolve.demon_lord.seed_lost", "You lost your Demon Lord Seed due to Naming.");
      this.add("tensura.evolve.demon_lord.not_seed", "You are not a Demon Lord Seed.");
      this.add("tensura.evolve.demon_lord.lack_soul", "You don't have enough soul points.");
      this.add("tensura.evolve.demon_lord.already", "You are already a True Demon Lord.");
      this.add("tensura.evolve.demon_lord.hero", "You are a True Hero.");
      this.add("tensura.evolve.demon_lord.success", "%s has awakened into a True Demon Lord.");
      this.add("tensura.evolve.demon_lord.subordinate_evolve", "You have evolved thanks to your master's Harvest Festival.");
      this.add("tensura.evolve.hero", "True Hero.");
      this.add("tensura.evolve.hero.egg", "You became a Hero Egg.");
      this.add("tensura.evolve.hero.egg_lost", "You lost your Hero Egg due to Naming.");
      this.add("tensura.evolve.hero.egg_lost.evolution", "You lost your Hero Egg upon evolution.");
      this.add("tensura.evolve.hero.not_egg", "You are not a Hero Egg.");
      this.add("tensura.evolve.hero.already", "You are already a True Hero.");
      this.add("tensura.evolve.hero.demon_lord", "You are a True Demon Lord.");
      this.add("tensura.evolve.hero.boss_requirement", "You don't meet the correct situation requirement.");
      this.add("tensura.evolve.hero.success", "%s has awakened into a True Hero.");
      this.add("tensura.schematic.unlocked", "Unlocked Schematic");
      this.add("tensura.message.position.occupied", "This location is occupied.");
      this.add("tensura.message.bone_golem.low_ep", "%s does not meet the EP requirement of %s.");
      this.add("tensura.message.damage.total", "Total Damage: %s.");
      this.add("tensura.message.npc.refuse_trading", "%s refuses to trade with you.");
      this.add("tensura.message.dwarf.wrong_reputation", "You are not welcomed by the dwarven royal with %s Reputation.");
      this.add("tensura.message.dwarf.king.greet.negative", "Oh? Didn't think you would turn yourself in like this?");
      this.add("tensura.message.dwarf.king.greet.negative_start", "Fine, I will handle you myself.");
      this.add("tensura.message.dwarf.king.greet.positive", "Isn't this our national hero, %s! Come, let's discuss!");
      this.add("tensura.message.dwarf.king.greet.positive_start", "Oh? You want to have a training fight? Let's get into it!");
      this.add("tensura.message.dwarf.king.defeat.positive", "Good job, %s! You beat me, here's your reward.");
      this.add("tensura.message.dwarf.king.defeat.positive.retry", "Good job, %s! You beat me again.");
      this.add("tensura.message.dwarf.king.defeat.negative", "Impossible!");
      this.add("tensura.message.dwarf.king.defeat.negative.retry", "The dwarven king is absent.");
      this.add("tensura.message.pet.follow", "%s is set to Follow.");
      this.add("tensura.message.pet.stay", "%s is set to Stay.");
      this.add("tensura.message.pet.wander", "%s is set to Wander.");
      this.add("tensura.message.pet.self_destruct", "%s is set to Self-Destruct.");
      this.add("tensura.message.pet.neutral", "%s's behaviour is set to Neutral.");
      this.add("tensura.message.pet.passive", "%s's behaviour is set to Passive.");
      this.add("tensura.message.pet.aggressive", "%s's behaviour is set to Aggressive.");
      this.add("tensura.message.pet.protect", "%s's behaviour is set to Protect.");
      this.add("tensura.message.slime_staff.sit", "Every slime in 30-block radius is set to Stay.");
      this.add("tensura.message.slime_staff.follow", "Every slime in 30-block radius is set to Follow.");
      this.add("tensura.message.slime.merge", "Your slimes have been merged!");
      this.add("tensura.message.slime.massive", "Your slime has became a Supermassive Slime!");
      this.add("tensura.message.slime.maxsize", "Your slime has reached its Max Size!");
      this.add("tensura.message.slime.despawn", "Your summoned slime has despawned.");
      this.add("tensura.naming.cannot_name", "The target cannot be named.");
      this.add("tensura.naming.name_owner", "You cannot name your master.");
      this.add("tensura.naming.already_named", "The target is already named.");
      this.add("tensura.naming.had_owner", "The target is faithful with its current owner.");
      this.add("tensura.naming.not_submit", "The target does not want to submit.");
      this.add("tensura.naming.insane", "The target is not sane enough.");
      this.add("tensura.naming.lack_EP", "Not enough EP.");
      this.add("tensura.naming.name_success.no_namer", "You have been given the name %s.");
      this.add("tensura.naming.name_success", "You have been given the name %s by %s.");
      this.add("tensura.naming.nameable_status", "Your nameable status has been set to %s.");
      this.add("tensura.naming.subdue", "Subdue the target");
      this.add("tensura.naming.evolve", "Evolve the target");
      this.add("tensura.naming.endow", "Endow the target");
      this.add("tensura.naming.name", "Name");
      this.add("tensura.naming.randomize", "Randomize");
      this.add("tensura.item.scroll_not_allowed", "%s is not allowed by the server");
      this.add("tensura.boss_fight.full", "This area currently doesn't welcome any further outsider.");
      this.add("tensura.boss_fight.timer.count", "%s seconds left.");
      this.add("tensura.boss_fight.started", "The boss fight has started.");
      this.add("tensura.boss_fight.started_timer", "The boss fight has started and will last %s seconds.");
      this.add("tensura.boss_fight.started_timer.count", "%s seconds left before the fight ends.");
      this.add("tensura.boss_fight.started_timer.minute", "The boss fight has started and will last %s minutes.");
      this.add("tensura.boss_fight.started_timer.minute.count", "%s minutes left before the fight ends.");
      this.add("tensura.boss_fight.start_delay", "The boss fight will start in %s seconds.");
      this.add("tensura.boss_fight.next.start_delay", "The next fight will start in %s seconds.");
      this.add("tensura.boss_fight.ended", "The fight has ended.");
      this.add("tensura.boss_fight.ended_timer", "The fight has ended, you will have %s seconds before being sent away.");
      this.add("tensura.boss_fight.ended_timer.count", "%s seconds left before being sent away.");
      this.add("tensura.boss_fight.teleport_away", "The fight has ended and you have been sent away.");
      this.add("tensura.boss_fight.not_found", "Boss Fight named [%s] does not exist.");
      this.add("tensura.boss_fight.existed", "Boss Fight named [%s] already existed.");
      this.add("tensura.boss_fight.builtin.remove", "Builtin Boss Fight cannot be removed.");
      this.add("tensura.boss_fight.builtin.reset", "Builtin Boss Fight of %s has been reset to default.");
      this.add("tensura.boss_fight.builtin.reset_arena", "Builtin Boss Arena of %s has been reset.");
      this.add("tensura.boss_fight.created", "A new Boss Fight named [%s] has been created/replaced at %s in [%s] with radius of %s blocks.");
      this.add(
         "tensura.boss_fight.created.max_player",
         "A new Boss Fight named [%s] has been created/replaced at %s in [%s] with radius of %s blocks that can hold %s players."
      );
      this.add(
         "tensura.boss_fight.created.timer",
         "A new Boss Fight named [%s] has been created/replaced at %s in [%s] with radius of %s blocks that can hold %s players and lasts %s ticks."
      );
      this.add("tensura.boss_fight.removed", "Boss Fight named [%s] has been removed.");
      this.add("tensura.boss_fight.reloaded", "All Existing Boss Fights have been reloaded from world folder.");
      this.add("tensura.boss_fight.list", "List of created Boss Fights: %s");
      this.add("tensura.boss_fight.list.empty", "There is 0 Boss Fights created.");
      this.add("tensura.boss_fight.joined", "%s has joined the Boss Fight [%s].");
      this.add("tensura.boss_fight.left", "%s has left the Boss Fight [%s].");
      this.add("tensura.boss_fight.get", "Boss Fight [%s]'s data:");
      this.add("tensura.boss_fight.get.status", "Status: %s.");
      this.add("tensura.boss_fight.get.status.started", "Started");
      this.add("tensura.boss_fight.get.status.on_hold", "On Hold");
      this.add("tensura.boss_fight.get.status.not_started", "Not Started");
      this.add("tensura.boss_fight.get.position", "Position: %s.");
      this.add("tensura.boss_fight.get.dimension", "Dimension: %s.");
      this.add("tensura.boss_fight.get.radius", "Radius: %s blocks.");
      this.add("tensura.boss_fight.get.entrance", "Entrance Position: %s.");
      this.add("tensura.boss_fight.get.max_player", "Player: %s/%s.");
      this.add("tensura.boss_fight.get.max_player.handler", "Player Count Handler: %s.");
      this.add("tensura.boss_fight.get.game_mode", "Forced Game Mode: %s.");
      this.add("tensura.boss_fight.get.boss_type", "Boss Type: %s.");
      this.add("tensura.boss_fight.get.boss_position", "Boss Spawn Position: %s.");
      this.add("tensura.boss_fight.get.reset_boss", "Reset Boss on stop: %s.");
      this.add("tensura.boss_fight.get.next_boss", "Next Boss Fight on success: %s");
      this.add("tensura.boss_fight.get.ban_teleportation", "Ban Teleportation: %s.");
      this.add("tensura.boss_fight.get.barrier_sealed", "Has Barrier: %s.");
      this.add("tensura.boss_fight.get.timer", "Timer: %s/%s ticks.");
      this.add("tensura.boss_fight.get.start_delay", "Start Delay: %s/%s ticks.");
      this.add("tensura.boss_fight.get.force_exit.timer", "Force Exit Timer: %s/%s ticks.");
      this.add("tensura.boss_fight.get.force_exit.on_leave", "Force Exit On Leave: %s.");
      this.add("tensura.boss_fight.get.force_exit.handler", "Force Exit Handler: %s.");
      this.add("tensura.boss_fight.get.force_exit.warp_point", "Warp Point - Position: %s - Dimension: [%s]");
      this.add("tensura.boss_fight.get.force_exit.offset", "Offset - %s");
      this.add("tensura.boss_fight.get.force_exit.random", "Random - X-Z range: %s - Y range: %s - Dimension: [%s]");
      this.add("tensura.boss_fight.get.force_exit.spawn_point", "Spawn Point");
      this.add("tensura.boss_fight.get.start_line", "- %s");
      this.add("tensura.boss_fight.get.start_commands", "Start commands:");
      this.add("tensura.boss_fight.get.success_commands", "Success commands:");
      this.add("tensura.boss_fight.get.fail_commands", "Fail commands:");
      this.add("tensura.boss_fight.get.banned_abilities", "Banned Abilities: %s");
      this.add("tensura.boss_fight.edit.center", "Boss Fight [%s]'s center has been set to %s.");
      this.add("tensura.boss_fight.edit.radius", "Boss Fight [%s]'s radius has been set to %s.");
      this.add("tensura.boss_fight.edit.entrance", "Boss Fight [%s]'s player entrance position has been set to %s.");
      this.add("tensura.boss_fight.edit.entrance.removed", "Boss Fight [%s]'s player entrance position has been removed.");
      this.add("tensura.boss_fight.edit.boss", "Boss Fight [%s]'s boss type to summon has been set to %s.");
      this.add("tensura.boss_fight.edit.boss.removed", "Boss Fight [%s]'s boss type to summon has been removed.");
      this.add("tensura.boss_fight.edit.boss.spawn_position", "Boss Fight [%s]'s boss spawn position has been set to %s.");
      this.add("tensura.boss_fight.edit.boss.spawn_position.removed", "Boss Fight [%s]'s boss spawn position has been removed.");
      this.add("tensura.boss_fight.edit.next_boss", "Boss Fight [%s]'s Next Boss Fight on success has been set to [%s].");
      this.add("tensura.boss_fight.edit.next_boss.removed", "Boss Fight [%s]'s Next Boss Fight on success has been removed.");
      this.add("tensura.boss_fight.edit.timer", "Boss Fight [%s]'s timer has been set to %s.");
      this.add("tensura.boss_fight.edit.timer.remove", "Boss Fight [%s]'s timer has been removed.");
      this.add("tensura.boss_fight.edit.start_delay", "Boss Fight [%s]'s start delay timer has been set to %s.");
      this.add("tensura.boss_fight.edit.start_delay.remove", "Boss Fight [%s]'s start delay timer has been removed.");
      this.add(
         "tensura.boss_fight.edit.force_exit.timer",
         "Boss Fight [%s]'s timer for the winners to stay in the area before being forced to exit has been set to %s."
      );
      this.add(
         "tensura.boss_fight.edit.force_exit.timer.remove",
         "Boss Fight [%s]'s timer for the winners to stay in the area before being forced to exit has been removed."
      );
      this.add("tensura.boss_fight.edit.force_exit.handler.spawn_point", "Boss Fight [%s]'s force exit handler function is now teleporting to Spawn Point.");
      this.add("tensura.boss_fight.edit.force_exit.handler.position_set", "Boss Fight [%s]'s force exit handler function is now teleporting to %s in [%s].");
      this.add("tensura.boss_fight.edit.force_exit.on_leave", "Boss Fight [%s]'s option to trigger force exit on leaving has been set to %s.");
      this.add("tensura.boss_fight.edit.player", "Boss Fight [%s]'s max player count has been set to %s.");
      this.add("tensura.boss_fight.edit.player_handler", "Boss Fight [%s]'s player count handler function has been set to %s.");
      this.add("tensura.boss_fight.edit.game_mode", "Boss Fight [%s]'s forced game mode has been set to %s.");
      this.add("tensura.boss_fight.edit.game_mode.remove", "Boss Fight [%s]'s forced game mode has been removed.");
      this.add("tensura.boss_fight.edit.reset_boss", "Boss Fight [%s]'s option to reset boss on ending has been set to %s.");
      this.add("tensura.boss_fight.edit.ban_teleportation", "Boss Fight [%s]'s option to ban teleportation has been set to %s.");
      this.add("tensura.boss_fight.edit.barrier_sealed", "Boss Fight [%s]'s option to have barrier has been set to %s.");
      this.add("tensura.boss_fight.edit.ability.ban", "Boss Fight [%s] has banned the usage of %s.");
      this.add("tensura.boss_fight.edit.ability.unban", "Boss Fight [%s] has unbanned the usage of %s.");
      this.add("tensura.boss_fight.edit.ability.banned", "Boss Fight [%s] is already banning the usage of %s.");
      this.add("tensura.boss_fight.edit.ability.not_banned", "Boss Fight [%s] is currently not banning the usage of %s.");
      this.add("tensura.boss_fight.edit.ability.clear", "Boss Fight [%s] has no longer banned any ability.");
      this.add("tensura.boss_fight.edit.start_command.add", "Boss Fight [%s]'s list of starting commands has added [%s].");
      this.add("tensura.boss_fight.edit.start_command.remove_last", "Boss Fight [%s]'s list of starting commands has removed the latest command of the list.");
      this.add("tensura.boss_fight.edit.start_command.remove_first", "Boss Fight [%s]'s list of starting commands has removed the first command of the list.");
      this.add("tensura.boss_fight.edit.start_command.clear", "Boss Fight [%s]'s list of starting commands has been cleared.");
      this.add("tensura.boss_fight.edit.start_command.empty", "Boss Fight [%s]'s list of starting commands is currently empty.");
      this.add("tensura.boss_fight.edit.success_command.add", "Boss Fight [%s]'s list of commands to run when boss fight succeeds has added [%s].");
      this.add(
         "tensura.boss_fight.edit.success_command.remove_last",
         "Boss Fight [%s]'s list of commands to run when boss fight succeeds has removed the latest command of the list."
      );
      this.add(
         "tensura.boss_fight.edit.success_command.remove_first",
         "Boss Fight [%s]'s list of commands to run when boss fight succeeds has removed the first command of the list."
      );
      this.add("tensura.boss_fight.edit.success_command.clear", "Boss Fight [%s]'s list of commands to run when boss fight succeeds has been cleared.");
      this.add("tensura.boss_fight.edit.success_command.empty", "Boss Fight [%s]'s list of commands to run when boss fight succeeds is currently empty.");
      this.add("tensura.boss_fight.edit.fail_command.add", "Boss Fight [%s]'s list of commands to run when boss fight fails has added [%s].");
      this.add(
         "tensura.boss_fight.edit.fail_command.remove_last",
         "Boss Fight [%s]'s list of commands to run when boss fight fails has removed the latest command of the list."
      );
      this.add(
         "tensura.boss_fight.edit.fail_command.remove_first",
         "Boss Fight [%s]'s list of commands to run when boss fight fails has removed the first command of the list."
      );
      this.add("tensura.boss_fight.edit.fail_command.clear", "Boss Fight [%s]'s list of commands to run when boss fight fails has been cleared.");
      this.add("tensura.boss_fight.edit.fail_command.empty", "Boss Fight [%s]'s list of commands to run when boss fight fails is currently empty.");
      this.add("tensura.world_restriction.banned", "The usage of %s is restricted in this area.");
      this.add("tensura.world_restriction.not_found", "World Restriction Area named [%s] does not exist.");
      this.add("tensura.world_restriction.existed", "World Restriction Area named [%s] already existed.");
      this.add("tensura.world_restriction.created", "A new World Restriction Area named [%s] has been created between %s and %s in [%s].");
      this.add("tensura.world_restriction.created.global", "A new global World Restriction Area named [%s] has been created in [%s].");
      this.add("tensura.world_restriction.removed", "World Restriction Area named [%s] has been removed.");
      this.add("tensura.world_restriction.reloaded", "All Existing World Restriction Areas have been reloaded from world folder.");
      this.add("tensura.world_restriction.list", "List of created World Restriction Areas: %s");
      this.add("tensura.world_restriction.list.empty", "There is 0 World Restriction Areas created.");
      this.add("tensura.world_restriction.get", "World Restriction Area [%s]'s data:");
      this.add("tensura.world_restriction.get.dimension", "Dimension: %s.");
      this.add("tensura.world_restriction.get.corner1", "Corner 1: %s.");
      this.add("tensura.world_restriction.get.corner2", "Corner 2: %s.");
      this.add("tensura.world_restriction.get.global", "Scope: Global (entire dimension).");
      this.add("tensura.world_restriction.get.banned_abilities", "Banned Abilities:");
      this.add("tensura.world_restriction.edit.corner1", "World Restriction Area [%s]'s first corner has been set to %s.");
      this.add("tensura.world_restriction.edit.corner1.removed", "World Restriction Area [%s]'s first corner has been removed.");
      this.add("tensura.world_restriction.edit.corner2", "World Restriction Area [%s]'s second corner has been set to %s.");
      this.add("tensura.world_restriction.edit.corner2.removed", "World Restriction Area [%s]'s second corner has been removed.");
      this.add("tensura.world_restriction.edit.dimension", "World Restriction Area [%s]'s dimension has been set to [%s].");
      this.add("tensura.world_restriction.edit.ability.ban", "World Restriction Area [%s] has banned all modes of %s.");
      this.add("tensura.world_restriction.edit.ability.ban_mode", "World Restriction Area [%s] has banned mode %3$s of %2$s.");
      this.add("tensura.world_restriction.edit.ability.unban", "World Restriction Area [%s] has unbanned the usage of %s.");
      this.add("tensura.world_restriction.edit.ability.unban_mode", "World Restriction Area [%s] has unbanned mode %3$s of %2$s.");
      this.add("tensura.world_restriction.edit.ability.banned", "World Restriction Area [%s] is already banning all modes of %s.");
      this.add("tensura.world_restriction.edit.ability.banned_mode", "World Restriction Area [%s] is already banning mode %3$s of %2$s.");
      this.add("tensura.world_restriction.edit.ability.not_banned", "World Restriction Area [%s] is currently not banning the usage of %s.");
      this.add("tensura.world_restriction.edit.ability.not_banned_mode", "World Restriction Area [%s] is currently not banning mode %3$s of %2$s.");
      this.add("tensura.world_restriction.edit.ability.clear", "World Restriction Area [%s] has no longer banned any ability.");
      this.add("tensura.warp_pad.saved", "New Warp Pad [%s] at %s in [%s] has been saved.");
      this.add("tensura.warp_pad.not_found", "There is no warp pad at provided position.");
      this.add("tensura.warp_pad.no_preset", "Targeted Warp Pad currently does not have any preset.");
      this.add("tensura.warp_pad.no_boss", "Targeted Warp Pad currently does not have any Boss Fight set.");
      this.add("tensura.warp_pad.clear", "Targeted Warp Pad's preset position is now cleared.");
      this.add("tensura.warp_pad.clear.boss_fight", "Targeted Warp Pad's Boss Fight is now removed.");
      this.add(
         "tensura.warp_pad.get.preset",
         "Targeted Warp Pad currently has preset position of %s in [%s] with warp timer of %s ticks and costs %s magicule per block."
      );
      this.add(
         "tensura.warp_pad.get.offset",
         "Targeted Warp Pad currently has preset to offset the users by %s with warp timer of %s ticks and costs %s magicule per block."
      );
      this.add(
         "tensura.warp_pad.get.spawn_point",
         "Targeted Warp Pad currently has preset to teleport users to their spawn point with warp timer of %s ticks and costs %s magicule per block."
      );
      this.add(
         "tensura.warp_pad.get.random",
         "Targeted Warp Pad currently has preset to teleport users to a random position within the range of %s on x-z and %s on y level with warp timer of %s ticks and costs %s magicule per block."
      );
      this.add("tensura.warp_pad.get.boss_fight", "Targeted Warp Pad is currently set to trigger the Boss Fight [%s] when used.");
      this.add(
         "tensura.warp_pad.set.preset", "Targeted Warp Pad's preset position is now %s in [%s] with warp timer of %s ticks and costs %s magicule per block."
      );
      this.add(
         "tensura.warp_pad.set.offset",
         "Targeted Warp Pad's preset position is now the teleporter's position offset by %s with warp timer of %s ticks and costs %s magicule per block."
      );
      this.add(
         "tensura.warp_pad.set.spawn_point",
         "Targeted Warp Pad's preset position is now the teleporter's spawn point with warp timer of %s ticks and costs %s magicule per block."
      );
      this.add(
         "tensura.warp_pad.set.random",
         "Targeted Warp Pad's preset position is now random with the range of %s on x-z and %s on y level in [%s] with warp timer of %s ticks and costs %s magicule per block."
      );
      this.add("tensura.warp_pad.set.boss_fight", "Targeted Warp Pad is now set to trigger the Boss Fight [%s] when used.");
   }

   private void keybindings() {
      this.add(TensuraKeybinds.KEY_0.getName(), "Number Key 0");
      this.add("tensura.keybinding.reload_configs", "Reload all Client Configs");
      this.add("tensura.keybinding.main_gui", "Status Menu");
      this.add("tensura.keybinding.name", "Naming");
      this.add("tensura.keybinding.race_ability", "Race/Mount Ability");
      this.add("tensura.keybinding.dodge", "Dodge (Experimental) / Mount Descend");
      this.add("tensura.keybinding.ability.slot_1", "Ability 1 Activation");
      this.add("tensura.keybinding.ability.slot_2", "Ability 2 Activation");
      this.add("tensura.keybinding.ability.slot_3", "Ability 3 Activation");
      this.add("tensura.keybinding.next_mode", "Next Ability Mode");
      this.add("tensura.keybinding.previous_mode", "Previous Ability Mode");
      this.add("key.categories.hidden", "Hidden");
      this.add("manascore_keybind.category.tensura", "Tensura:Reincarnated");
   }

   private void races() {
      this.add("tensura.race.cooldown", "%s's Ability is on Cooldown.");
      this.add("tensura.alignment.default", "Default");
      this.add("tensura.alignment.majin", "Majin");
      this.add("tensura.alignment.holy", "Holy");
      this.add("tensura.alignment.chaos", "Chaos");
      this.addRace(
         TensuraRaces.BEASTFOLK,
         "Beastfolk",
         "A race that can freely change between their true animal form and a more a human form. They possess immense physical prowess and regenerative capabilities that let them fight without rest."
      );
      this.addRace(TensuraRaces.BEAST_LORD, "Beast Lord", "Beastfolk that has \"evolved the correct way\" and became a Demi-Spiritual Lifeform.");
      this.addRace(TensuraRaces.SPIRIT_BEAST, "Spirit Beast", "Beastfolk that has \"evolved the correct way\" and became a Spiritual Lifeform.");
      this.addRace(TensuraRaces.DIVINE_BEAST, "Divine Beast", "Beastfolk that achieved divinity, possessing an immortal physical body that will never age.");
      this.addRace(
         TensuraRaces.HARPY, "Harpy", "A race of winged Majin with capability to cancel out flight spell and are often referred to as the rulers of the skies."
      );
      this.addRace(TensuraRaces.HARPY_QUEEN, "Harpy Queen", "A Queen Species of Harpy with the ability to reproduce through Parthenogenesis or Virgin Birth.");
      this.addRace(TensuraRaces.SPIRIT_BIRD, "Spirit Bird", "Harpy that has \"evolved the correct way\" and became a Spiritual Lifeform.");
      this.addRace(TensuraRaces.DIVINE_BIRD, "Divine Bird", "Harpy that achieved divinity, possessing an immortal physical body that will never age.");
      this.addRace(
         TensuraRaces.ELF,
         "Elf",
         "A sprite race descended from wind elementals. They possess a fierce talent for elemental magic and are more in tune with nature than most."
      );
      this.addRace(TensuraRaces.ENLIGHTENED_ELF, "Enlightened Elf", "Elf that has \"evolved the correct way\" and became a Demi-Spiritual Lifeform.");
      this.addRace(TensuraRaces.ELF_SAINT, "Elf Saint", "Elf that has \"evolved the correct way\" and became a Spiritual Lifeform.");
      this.addRace(TensuraRaces.DIVINE_ELF, "Divine Elf", "Elf that achieved divinity, possessing an immortal physical body that will never age.");
      this.addRace(
         TensuraRaces.DWARF,
         "Dwarf",
         "A sprite race descended from earth elementals. They possess an immense will and make for fierce albeit rather short soldiers."
      );
      this.addRace(TensuraRaces.ENLIGHTENED_DWARF, "Enlightened Dwarf", "Dwarf that has \"evolved the correct way\" and became a Demi-Spiritual Lifeform.");
      this.addRace(TensuraRaces.DWARF_SAINT, "Dwarf Saint", "Dwarf that has \"evolved the correct way\" and became a Spiritual Lifeform.");
      this.addRace(TensuraRaces.DIVINE_DWARF, "Divine Dwarf", "Dwarf that achieved divinity, possessing an immortal physical body that will never age.");
      this.addRace(
         TensuraRaces.ORC,
         "Orc",
         "A race of beastfolk who lost the ability to shift between man and beast, resulting in a permanent mix of the two. Their physical strength is greater than average but pails in comparison to their original strength as beastfolk."
      );
      this.addRace(
         TensuraRaces.HIGH_ORC,
         "High Orc",
         "The evolved form of Orcs. They are smarter than Orcs while preserving the Orc race's special characteristics, with appearance virtually identical to regular Orcs."
      );
      this.addRace(
         TensuraRaces.ORC_LORD, "Orc Lord", "A very rare and powerful member of the Orc race with high intelligence who appears roughly every few centuries."
      );
      this.addRace(TensuraRaces.ORC_DISASTER, "Orc Disaster", "The evolution of an Orc Lord that is a Demon Lord Seed.");
      this.addRace(TensuraRaces.SPIRIT_BOAR, "Spirit Boar", "Orc that has \"evolved the correct way\" and became a Spiritual Lifeform.");
      this.addRace(TensuraRaces.DIVINE_BOAR, "Divine Boar", "Orc that achieved divinity, possessing an immortal physical body that will never age.");
      this.addRace(
         TensuraRaces.OGRE, "Ogre", "A sprite race descended from fire elementals. They possess immense physical capabilities and a strong Japanese lineage."
      );
      this.addRace(TensuraRaces.ENLIGHTENED_OGRE, "Enlightened Ogre", "Ogre that has \"evolved the correct way\" and became a Demi-Spiritual Lifeform.");
      this.addRace(
         TensuraRaces.KIJIN, "Kijin", "A race of Sprite Demi-Humans descended from Fire Elementals. They have an average lifespan of over a thousand years"
      );
      this.addRace(TensuraRaces.MYSTIC_ONI, "Mystic Oni", "The result of Kijin fully returning to their roots as Elementals, making them Spiritual Lifeforms.");
      this.addRace(
         TensuraRaces.WICKED_ONI,
         "Wicked Oni",
         "A variant of Oni, possessing an essence that leans closer towards that of a Daemon rather than that of a pure Elemental."
      );
      this.addRace(TensuraRaces.SPIRIT_ONI, "Spirit Oni", "Oni that has \"evolved the correct way\" and became a Spiritual Lifeform.");
      this.addRace(TensuraRaces.DEATH_ONI, "Death Oni", "Wicked Oni that has \"evolved the correct way\" and became a Spiritual Lifeform.");
      this.addRace(TensuraRaces.DIVINE_ONI, "Divine Oni", "Oni that achieved divinity, possessing an immortal physical body that will never age.");
      this.addRace(
         TensuraRaces.DIVINE_FIGHTER, "Divine Fighter", "Wicked Oni that achieved divinity, possessing an immortal physical body that will never age."
      );
      this.addRace(TensuraRaces.GOBLIN, "Goblin", "A race of Sprite Demi-Humans. They seem to be descended from the offspring of Dwarves and Oni.");
      this.addRace(TensuraRaces.HOBGOBLIN, "Hobgoblin", "The evolved form of male Goblins that is ranked D on average.");
      this.addRace(
         TensuraRaces.ENLIGHTENED_HOBGOBLIN, "Enlightened Hobgoblin", "Hobgoblin that has \"evolved the correct way\" and became a Demi-Spiritual Lifeform."
      );
      this.addRace(TensuraRaces.HOBGOBLIN_SAINT, "Hobgoblin Saint", "Hobgoblin that has \"evolved the correct way\" and became a Spiritual Lifeform.");
      this.addRace(TensuraRaces.GIANT, "Giant", "A race of powerful Majin most notable for their incredible physical capabilities and Magic Resistance.");
      this.addRace(TensuraRaces.ANCIENT_GIANT, "Ancient Giant", "Ancient form of Giants with even more incredible physical capabilities and abilities.");
      this.addRace(
         TensuraRaces.DIVINE_GIANT, "Divine Giant", "A godly race born from the earth itself, possessing an immortal physical body that will never age."
      );
      this.addRace(
         TensuraRaces.HUMAN,
         "Human",
         "A weak but populous race that relies more on technology and numbers than brute force. Their low magicule count makes skills and magic users a rarity among them, instead favouring battlewill."
      );
      this.addRace(TensuraRaces.ENLIGHTENED_HUMAN, "Enlightened Human", "Human that has \"evolved the correct way\" and became a Demi-Spiritual Lifeform.");
      this.addRace(TensuraRaces.HUMAN_SAINT, "Human Saint", "Human that has \"evolved the correct way\" and became a Spiritual Lifeform.");
      this.addRace(TensuraRaces.DIVINE_HUMAN, "Divine Human", "Human that achieved divinity, possessing an immortal physical body that will never age.");
      this.addRace(
         TensuraRaces.LIZARDMAN, "Lizardman", "A race of scaled people descended from dragons. Their webbed feet give them an advantage in wet terrain."
      );
      this.addRace(
         TensuraRaces.DRAGONEWT,
         "Dragonewt",
         " The evolved form of Lizardmen and descendants of dragons. They have an average lifespan of about two hundred years."
      );
      this.addRace(
         TensuraRaces.TRUE_DRAGONEWT,
         "True Dragonewt",
         "The evolution of Dragonewts and complete Spiritual Lifeforms. They are the second-highest evolutionary stage of Dragonewt known to exist."
      );
      this.addRace(TensuraRaces.DIVINE_DRAGON, "Divine Dragon", "Dragonewt that achieved divinity, possessing an immortal physical body that will never age.");
      this.addRace(
         TensuraRaces.MERFOLK,
         "Merfolk",
         "A sprite race descended from water elementals. Their fish-like bodies give them an insurmountable advantage in water."
      );
      this.addRace(
         TensuraRaces.ENLIGHTENED_MERFOLK, "Enlightened Merfolk", "Merfolk that has \"evolved the correct way\" and became a Demi-Spiritual Lifeform."
      );
      this.addRace(TensuraRaces.MERFOLK_SAINT, "Merfolk Saint", "Merfolk that has \"evolved the correct way\" and became a Spiritual Lifeform.");
      this.addRace(TensuraRaces.DIVINE_FISH, "Divine Fish", "Merfolk that achieved divinity, possessing an immortal physical body that will never age.");
      this.addRace(
         TensuraRaces.SLIME,
         "Slime",
         "A Spectral race of monster that lacks intelligence and ambition. They’re usually passive but are incredibly ruthless once provoked."
      );
      this.addRace(
         TensuraRaces.METAL_SLIME,
         "Metal Slime",
         "A slime that has taken in large quantities of Magic Ore. The dissolved ore inside of it provides a strong resistance towards most forms of damage."
      );
      this.addRace(
         TensuraRaces.DEMON_SLIME,
         "Demon Slime",
         "The ultimate stage of evolution of Slimes. They evolve to this stage by awakening as a True Demon Lord during the Harvest Festival."
      );
      this.addRace(TensuraRaces.GOD_SLIME, "God Slime", "The divine stage of evolution of Slimes.");
      this.addRace(TensuraRaces.GHOUL, "Ghoul", "A vampiric thrall brought about by Blood Raise, highly weakened by sunlight.");
      this.addRace(
         TensuraRaces.VAMPIRE,
         "Vampire",
         "A race of Majin with nearly eternal life. Although they can reproduce, they normally abstain. Hosting overwhelming magical energy and vitality, their flesh never decays."
      );
      this.addRace(TensuraRaces.VAMPIRE_OVERCOMER, "Vampire Overcomer", "Vampires that have evolved to overcome their weakness against the sun.");
      this.addRace(TensuraRaces.VAMPIRE_LORD, "Vampire Lord", "A vampire among the loftiest levels of power, comparable to a Demon Lord.");
      this.addRace(TensuraRaces.DIVINE_VAMPIRE, "Divine Vampire", "Vampire that achieved divinity, possessing an immortal physical body that will never age.");
      this.addRace(TensuraRaces.WIGHT, "Wight", "A Demi-Spiritual skeletal Undead Monster, highly weakened by sunlight.");
      this.addRace(
         TensuraRaces.WIGHT_KING,
         "Wight King",
         "A type of Undead Monster and an evolution of Wights. They possess fearsome power comparable to that of Demon Lord Seeds."
      );
      this.addRace(TensuraRaces.SPIRIT_SKELETON, "Spirit Skeleton", "Wight that has \"evolved the correct way\" and became a Spiritual Lifeform.");
      this.addRace(TensuraRaces.DIVINE_SKELETON, "Divine Skeleton", "Wight that achieved divinity, possessing an immortal physical body that will never age.");
      this.addRace(
         TensuraRaces.LESSER_DAEMON,
         "Lesser Daemon",
         "The lowest level of the daemon race. They spontaneously come into existence within the Daemon Realm where they slowly accumulate experience from fighting and being summoned before eventually evolving into greater daemons."
      );
      this.addRace(
         TensuraRaces.GREATER_DAEMON,
         "Greater Daemon",
         "The daemon race that lesser daemons evolve to when they develop their ego and accumulate enough power."
      );
      this.addRace(
         TensuraRaces.ARCH_DAEMON,
         "Arch Daemon",
         "The peak evolutionary stage that can be reached by daemons in the Daemon Realm. Only the most powerful of greater daemons can ascend to this height."
      );
      this.addRace(
         TensuraRaces.DAEMON_LORD, "Daemon Lord", "One of the most powerful evolutionary stages of daemons, achieved only by the most powerful arch daemons."
      );
      this.addRace(
         TensuraRaces.DEVIL_LORD,
         "Devil Lord",
         "The final stage of evolution for daemons. One emerges when a daemon meets all three requirements of a name, material body, and divinity."
      );
   }

   private void advancements() {
      this.addAdvancement(TensuraAdvancements.Basic.REINCARNATED, "TenSura", "Reincarnation... Successful.");
      this.addAdvancement(TensuraAdvancements.Basic.EMPOWERMENT, "Empowerment", "Kill a monster to increase your Existence Point (EP) for the first time.");
      this.addAdvancement(TensuraAdvancements.Basic.D_RANK, "Just average Human", "Get 1000 EP and reach D Class.");
      this.addAdvancement(TensuraAdvancements.Basic.C_RANK, "You can't C me", "Get 3000 EP and reach C Class.");
      this.addAdvancement(TensuraAdvancements.Basic.B_RANK, "I'm a Bsian", "Get 6000 EP and reach B Class.");
      this.addAdvancement(TensuraAdvancements.Basic.A_RANK, "Such a Hazard!", "Get 10000 EP and reach A Class.");
      this.addAdvancement(TensuraAdvancements.Basic.SA_RANK, "Calamity Monsters!", "Get 100000 EP and reach Special-A Class.");
      this.addAdvancement(TensuraAdvancements.Basic.S_RANK, "Walking Disaster!", "Get 400000 EP and reach S Class.");
      this.addAdvancement(TensuraAdvancements.Basic.SS_RANK, "Literal Catastrophe!", "Get 800000 EP and reach Special-S Class.");
      this.addAdvancement(TensuraAdvancements.Basic.GROWTH_SPURT, "Growth Spurt", "Evolve your race once.");
      this.addAdvancement(TensuraAdvancements.Basic.HIGHER_FORM, "The higher form of Existence!", "Awaken as a Demon Lord or Chosen Hero.");
      this.addAdvancement(
         TensuraAdvancements.Basic.INFAMY_FAMOUS, "Infamy and Famous", "Achieve Mass Naming by finishing raids or killing human mobs without dying."
      );
      this.addAdvancement(TensuraAdvancements.Basic.GETCHA_LEATHERS, "Getcha Leathers!", "Obtain a vanilla Leather.");
      this.addAdvancement(TensuraAdvancements.Basic.GOLD_RUSH, "Gold Rush", "Smelt or find a Gold Ingot.");
      this.addAdvancement(TensuraAdvancements.Basic.ACQUIRE_SILVERWARE, "Acquire Silverware", "Smelt or find a Silver Ingot.");
      this.addAdvancement(TensuraAdvancements.Basic.MAGIC_ORE, "Ore! But Magic?!", "Mine a Magic Ore.");
      this.addAdvancement(TensuraAdvancements.Basic.LOW_MAGISTEEL, "Feeling Low?", "Mix some Iron with Magic Ore in a Kiln and acquire a Low Magisteel Ingot!");
      this.addAdvancement(
         TensuraAdvancements.Basic.HIGH_MAGISTEEL, "Don't get too High!", "Mix some Iron with Magic Ore in a Kiln and acquire a High Magisteel Ingot!"
      );
      this.addAdvancement(TensuraAdvancements.Basic.PURE_MAGISTEEL, "That's the quality!", "Acquire a Pure Magisteel Ingot!");
      this.addAdvancement(
         TensuraAdvancements.Basic.MITHRIL,
         "Isn't this just Silver but Magic?",
         "Mix some Silver with a bit of Magic Ore in a Kiln and acquire a Mithril Ingot!"
      );
      this.addAdvancement(
         TensuraAdvancements.Basic.ORICHALCUM, "Divine Shining Gold!", "Mix some Gold with a bit of Magic Ore in a Kiln and acquire an Orichalcum Ingot!"
      );
      this.addAdvancement(TensuraAdvancements.Basic.ADAMANTITE, "Biological Steel?", "Acquire an Adamantite item!");
      this.addAdvancement(TensuraAdvancements.Basic.HIHIIROKANE, "The Ultimate Metal!", "Acquire an Hihiirokane item!");
      this.addAdvancement(TensuraAdvancements.Basic.LABYRINTH, "Dungeon Encroachment", "Enter the Labyrinth.");
      this.addAdvancement(TensuraAdvancements.Basic.JUST_A_TEST, "Chill! It's just a test!", "Fight the Elemental Colossus and \"die\".");
      this.addAdvancement(TensuraAdvancements.Basic.SPIRIT_PROTECTOR, "The fairy wont like this...", "Defeat the Elemental Colossus.");
      this.addAdvancement(TensuraAdvancements.Basic.ELEMENTALIST, "Elementalist", "Form a contract with any Spirit.");
      this.addAdvancement(TensuraAdvancements.Basic.BLESSED_ONE, "Blessed One!", "Have a Greater Spirit or above in every elemental.");
      this.addAdvancement(TensuraAdvancements.Basic.INFINITY_CORES, "Infinity Cores!", "Use a weapon that has 3 Elemental cores slotted.");
      this.addAdvancement(TensuraAdvancements.Basic.REWIND_TIME, "It's rewind time!", "Use a reset scroll.");
      this.addAdvancement(TensuraAdvancements.Basic.OBTAIN_HIHIIROKANE_HOE, "Why..?", "Own and use a Hihi'Irokane Hoe.");
      this.addAdvancement(TensuraAdvancements.Basic.UNICORN_HORN, "So that's what they do!", "Shoot and kill something with a Unicorn horn from a crossbow.");
      this.addAdvancement(
         TensuraAdvancements.Basic.LIGHT_AS_HORNED_RABBIT,
         "Light as a Horned Rabbit",
         "Walk on powdered snow without sinking, using boots made of monster leathers!"
      );
      this.addAdvancement(
         TensuraAdvancements.Basic.MONSTROUS_DIET,
         "A Monstrous Diet",
         "Eat everything that is edible in Tensura: Reincarnated, even if it's dangerous for you."
      );
      this.addAdvancement(TensuraAdvancements.Basic.MAGIC_SEEDY_PLACE, "A Magic Seedy Place", "Grow a Hipokute Seed.");
      this.addAdvancement(TensuraAdvancements.Basic.HIPOKUTE_FLOWER, "Hipokute makes me hiccup-te", "Obtain a Hipokute Flower.");
      this.addAdvancement(TensuraAdvancements.Basic.GOOD_AS_NEW, "As good as new!", "Consume a Full Potion.");
      this.addAdvancement(TensuraAdvancements.Basic.DELIGHTFUL_TRADE, "It's a deal-ightful trade!", "Complete a trade and obtain coins.");
      this.addAdvancement(TensuraAdvancements.Basic.MY_PRECIOUS, "My Precious", "Obtain your first Gold coin.");
      this.addAdvancement(TensuraAdvancements.Basic.MILLION_DOLLAR, "That's over 1 million dollar!", "Obtain a Stellar Gold coin.");
      this.addAdvancement(TensuraAdvancements.Adventure.REAL_ADVENTURE, "Real Adventures", "The real adventure begins!");
      this.addAdvancement(TensuraAdvancements.Adventure.FAST_LEARNER, "Fast Learner", "Learn an ability.");
      this.addAdvancement(TensuraAdvancements.Adventure.MASTER_SKILL, "Master what you learnt!", "Master an ability.");
      this.addAdvancement(TensuraAdvancements.Adventure.MASTER_UNIQUE_SKILL, "Master of one trade!", "Master a Unique Skill.");
      this.addAdvancement(TensuraAdvancements.Adventure.FORBIDDEN_MANUAL, "The Forbidden Manual!", "Use a Battlewill Manual.");
      this.addAdvancement(TensuraAdvancements.Adventure.YOU_A_WIZARD, "You're A Wizard, Steve", "Use a Magic Tome.");
      this.addAdvancement(TensuraAdvancements.Adventure.WANDERFUL, "This is Wand-erful!", "Obtain a Magic Staff.");
      this.addAdvancement(TensuraAdvancements.Adventure.BOOKED_ON_MAGIC, "Booked on Magic", "Obtain a Magic Grimoire.");
      this.addAdvancement(TensuraAdvancements.Adventure.EXPLOSION, "Explosions, explosions, la la la!", "Cast a max-level Explosion for the first time!");
      this.addAdvancement(TensuraAdvancements.Adventure.MONSTER_TAMER, "Monster Tamer", "Tame any monster in any way.");
      this.addAdvancement(TensuraAdvancements.Adventure.HEAR_ME_DIREWOLVES, "Hear me Direwolves!", "Tame a direwolf.");
      this.addAdvancement(TensuraAdvancements.Adventure.GOOD_BOY, "Who's a good boy?", "Ride a Tempest Star Wolf.");
      this.addAdvancement(TensuraAdvancements.Adventure.NAME_A_MOB, "From now on, your name is...", "Name a mob.");
      this.addAdvancement(
         TensuraAdvancements.Adventure.RULER_OF_MONSTERS,
         "Ruler of Monsters",
         "Make a subordinate of each of these races: Direwolf, Goblin, Lizardman, Orc and Slime."
      );
      this.addAdvancement(TensuraAdvancements.Adventure.MONSTER_RIDER, "Monster Rider", "Craft a Monster Saddle.");
      this.addAdvancement(TensuraAdvancements.Adventure.KILLER_FISH, "Killer fish from Sissiego", "Ride a Sissie.");
      this.addAdvancement(TensuraAdvancements.Adventure.CHOO_CHOO, "Choo Choo!", "Ride an Evil Centipede or Tempest Serpent.");
      this.addAdvancement(TensuraAdvancements.Adventure.TAMED_A_SLIME, "I'm not a bad slime!", "Tame a slime.");
      this.addAdvancement(TensuraAdvancements.Adventure.GET_BUCKETED, "Get bucketed!", "Pick up a slime with a bucket.");
      this.addAdvancement(TensuraAdvancements.Adventure.TRAITOR, "Traitor!", "Consume a slime in a bucket with skills.");
      this.addAdvancement(TensuraAdvancements.Adventure.GROW_A_SLIME, "Thrive, my child!", "Feed a Slime core to a tamed Slime to grow its size.");
      this.addAdvancement(TensuraAdvancements.Adventure.KING_SLIME, "The King has arrived!", "Get your own Supermassive Slime by growing a slime.");
      this.addAdvancement(TensuraAdvancements.Adventure.SLIME_ARMY, "100 slimes vs 1 Gorilla?", "Use a Slime Staff to summon slimes.");
      this.addAdvancement(TensuraAdvancements.Adventure.GETCHA_BETTER_LEATHERS, "Getcha Better Leathers!", "Obtain a monster Leather.");
      this.addAdvancement(TensuraAdvancements.Adventure.BELIEVE_T0_FLY, "I believe I can fly", "Obtain Winged Shoes.");
      this.addAdvancement(TensuraAdvancements.Adventure.RIPOFF_ELYTRA, "Ripoff Elytra, good enough?", "Obtain a Bat Glider.");
      this.addAdvancement(TensuraAdvancements.Adventure.VIGILANT, "VigilAnt", "Slay a Giant Ant and obtain its Carapace.");
      this.addAdvancement(TensuraAdvancements.Adventure.SHELL_LIZARD, "Shell Lizard", "Slay an Armorsaurus and obtain its Scales and Shell.");
      this.addAdvancement(TensuraAdvancements.Adventure.HISS_TORY, "Hiss-tory", "Slay a Tempest Serpent and obtain its Scales.");
      this.addAdvancement(TensuraAdvancements.Adventure.GOODNIGHT_SPIDER, "Goodnight Spider", "Slay a Knight Spider and obtain its Carapace.");
      this.addAdvancement(TensuraAdvancements.Adventure.ARACHNOPHOBIC, "Arachnophobic", "Slay a Black Spider.");
      this.addAdvancement(TensuraAdvancements.Adventure.EAT_OR_BE_EATEN, "Eat or be eaten", "Defeat an Orc Disaster.");
      this.addAdvancement(TensuraAdvancements.Adventure.CONQUEROR_OF_FLAMES, "Conqueror of Flames", "Defeat a natural Ifrit.");
      this.addAdvancement(TensuraAdvancements.Adventure.RULER_OF_THE_SKIES, "Ruler of the Skies", "Defeat a Charybdis.");
      this.addAdvancement(TensuraAdvancements.Adventure.NANODA, "Nanoda!!!", "Obtain the Nanoda disk by having a Charybdis kill an Orc Disaster.");
      this.addAdvancement(TensuraAdvancements.Adventure.GREAT_SAINT_OF_THE_WEST, "The Great Saint of the West", "Defeat Hinata Sakaguchi.");
      this.addAdvancement(TensuraAdvancements.Adventure.HERO_KING, "The Hero King of the Dwarves", "Defeat Gazel Dwargo.");
      this.addAdvancement(TensuraAdvancements.Adventure.START_SMITHING, "Start Smithing and Crafting!", "Use a Schematic.");
      this.addAdvancement(TensuraAdvancements.Adventure.BECOME_NINJA, "Become a real ninja!", "Craft a Kunai.");
      this.addAdvancement(TensuraAdvancements.Adventure.UNHEALABLE_WOUND, "Unhealable wound...", "Craft a Spatial Blade.");
      this.addAdvancement(TensuraAdvancements.Adventure.A_BIT_COLD, "Isn't it a bit cold?", "Craft a Ice Blade.");
      this.addAdvancement(TensuraAdvancements.Adventure.MASTER_SMITH, "The Master Smith!", "Obtain every smithing schematic.");
      this.addAdvancement(TensuraAdvancements.Adventure.NO_NO_SQUARE, "This is my no no square", "Use a Magic Engine to block monster spawn in an area.");
      this.addAdvancement(TensuraAdvancements.Adventure.WAY_STONE, "It's a way stone?", "Obtain a Warp Pad.");
      this.addAdvancement(TensuraAdvancements.Adventure.BETTER_SMELTER, "The better smelter!", "Craft a Kiln.");
      this.addAdvancement(TensuraAdvancements.Adventure.EVEN_BETTER_SMELTER, "The even better smelter!", "Craft a Mithril Kiln.");
      this.addAdvancement(TensuraAdvancements.Adventure.BEST_SMELTER, "The best smelter!", "Craft a Orichalcum Kiln.");
      this.addAdvancement(TensuraAdvancements.Adventure.PIERROT_MASK, "A Clown Troupe!", "Craft a Pierrot Mask.");
      this.addAdvancement(TensuraAdvancements.Adventure.TOO_STRONG, "Bad to be strong sometimes...", "Craft a Dragon Knuckle.");
      this.addAdvancement(TensuraAdvancements.Adventure.HELL, "Welcome to the Underworld!", "Enter Hell Dimension.");
      this.addAdvancement(TensuraAdvancements.Adventure.HELLA_COOL, "Hella Cool!", "Have a Daemon mob as subordinate.");
      this.addAdvancement(TensuraAdvancements.Adventure.BUILD_BODY, "Build a Body", "Obtain a Bone Golem.");
      this.addAdvancement(TensuraAdvancements.Adventure.RAINBOW_IN_HELL, "Rainbow in Hell", "Have an Arch Daemon subordinate of each linage.");
      this.addAdvancement(TensuraAdvancements.Adventure.UNHOLY_TOURISM, "Unholy Tourism", "Explore all Hell biomes.");
      this.addAdvancement(
         TensuraAdvancements.Adventure.OTHERWORLDLY_BIOMES, "Otherworldly Biomes", "Explore all Overworld biomes added by Tensura: Reincarnated."
      );
   }

   private void stats() {
      this.add("stat_type.tensura.boss_killed", "Number of bosses killed.");
      this.add("stat.tensura.boss_defeated", "Number of unique bosses defeated.");
      this.add("stat.tensura.entity_named", "Number of entities named.");
      this.add("stat.tensura.spirit_pray_time", "Number of times prayed for elemental spirits.");
      this.add("stat.tensura.spirit_pray_fail_time", "Number of failed spirit prayers.");
      this.add("stat.tensura.spirit_contracted_time", "Number of successful spirit contracts.");
      this.add("stat.tensura.battlewill_learnt", "Number of Battlewills learnt.");
      this.add("stat.tensura.battlewill_mastered", "Number of Battlewills mastered.");
      this.add("stat.tensura.magic_learnt", "Number of Magics learnt.");
      this.add("stat.tensura.magic_mastered", "Number of Magics mastered.");
      this.add("stat.tensura.skill_learnt", "Number of Skills learnt.");
      this.add("stat.tensura.skill_mastered", "Number of Skills mastered.");
   }

   private void soundSubtitles() {
      this.addSound((SoundEvent)TensuraSoundEvents.NANODA.get(), "Nanoda!");
      this.addSound((SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), "Buff activates");
      this.addSound((SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(), "Buff deactivates");
      this.addSound((SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(), "Debuff activates");
      this.addSound((SoundEvent)TensuraSoundEvents.DEBUFF_DEACTIVATE.get(), "Debuff deactivates");
      this.addSound((SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(), "Defence activates");
      this.addSound((SoundEvent)TensuraSoundEvents.DEFENCE_DEACTIVATE.get(), "Defence deactivates");
      this.addSound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), "Something casts");
      this.addSound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), "Something fails to cast");
      this.addSound((SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(), "Something uncasts");
      this.addSound((SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), "Something heals");
      this.addSound((SoundEvent)TensuraSoundEvents.GENERIC_SPLIT.get(), "Something splits");
      this.addSound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), "Energy drained");
      this.addSound((SoundEvent)TensuraSoundEvents.PRESENCE_CONCEALMENT.get(), "Presence conceals");
      this.addSound((SoundEvent)TensuraSoundEvents.CAST_DARK.get(), "Darkness casted");
      this.addSound((SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), "Earth casted");
      this.addSound((SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), "Fire casted");
      this.addSound((SoundEvent)TensuraSoundEvents.CAST_LIGHT.get(), "Light casted");
      this.addSound((SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get(), "Lightning casted");
      this.addSound((SoundEvent)TensuraSoundEvents.CAST_ICE.get(), "Ice casted");
      this.addSound((SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), "Space casted");
      this.addSound((SoundEvent)TensuraSoundEvents.CAST_WATER.get(), "Water casted");
      this.addSound((SoundEvent)TensuraSoundEvents.CAST_WIND.get(), "Wind casted");
      this.addSound((SoundEvent)TensuraSoundEvents.BLOOD_RAY.get(), "Blood Ray");
      this.addSound((SoundEvent)TensuraSoundEvents.BREATH_FIRE.get(), "Fire Breath");
      this.addSound((SoundEvent)TensuraSoundEvents.BREATH_POISON.get(), "Poison Breath");
      this.addSound((SoundEvent)TensuraSoundEvents.BREATH_THUNDER.get(), "Thunder Breath");
      this.addSound((SoundEvent)TensuraSoundEvents.BREATH_WATER.get(), "Water Breath");
      this.addSound((SoundEvent)TensuraSoundEvents.BREATH_WIND.get(), "Wind Breath");
      this.addSound((SoundEvent)TensuraSoundEvents.COERCION.get(), "Sound blast");
      this.addSound((SoundEvent)TensuraSoundEvents.MIST_RELEASE.get(), "Mist releases");
      this.addSound((SoundEvent)TensuraSoundEvents.TRANSFORM_BEAST.get(), "Beast Transformation");
      this.addSound((SoundEvent)TensuraSoundEvents.TRANSFORM_DRAGON.get(), "Dragon Transformation");
      this.addSound((SoundEvent)TensuraSoundEvents.TRANSFORM_OGRE.get(), "Ogre Berserker activates");
      this.addSound((SoundEvent)TensuraSoundEvents.TRANSFORM_BERSERKER.get(), "Berserker activates");
      this.addSound((SoundEvent)TensuraSoundEvents.HAKI_START.get(), "Haki Starts");
      this.addSound((SoundEvent)TensuraSoundEvents.HAKI_LOOP.get(), "Haki Runs");
      this.addSound((SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), "Barrier breaks");
      this.addSound((SoundEvent)TensuraSoundEvents.STICKY_STEEL_THREAD.get(), "Sticky-Steel Thread activates");
      this.addSound((SoundEvent)TensuraSoundEvents.EATER.get(), "Eater activates");
      this.addSound((SoundEvent)TensuraSoundEvents.MUSIC_BLAST.get(), "Music explodes");
      this.addSound((SoundEvent)TensuraSoundEvents.MUSIC_REQUIEM.get(), "Requiem explodes");
      this.addSound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), "GUI opens");
      this.addSound((SoundEvent)TensuraSoundEvents.PREDATION.get(), "Predation activates");
      this.addSound((SoundEvent)TensuraSoundEvents.REFLECTION.get(), "Reflection");
      this.addSound((SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(), "Earth shatters");
      this.addSound((SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(), "Instant Movement");
      this.addSound((SoundEvent)TensuraSoundEvents.DISINTEGRATION.get(), "Disintegration activates");
      this.addSound((SoundEvent)TensuraSoundEvents.MEGIDDO_SHOOT.get(), "Megiddo shoots");
      this.addSound((SoundEvent)TensuraSoundEvents.SWIPE.get(), "Swipe activates");
      this.addSound((SoundEvent)TensuraSoundEvents.ACID_SIZZLE.get(), "Acid sizzles");
      this.addSound((SoundEvent)TensuraSoundEvents.WIND_BLOW.get(), "Wind blows");
      this.addSound((SoundEvent)TensuraSoundEvents.BIG_BITE.get(), "Big Bite");
      this.addSound((SoundEvent)TensuraSoundEvents.GENERIC_SPLASH.get(), "Something splashes");
      this.addSound((SoundEvent)TensuraSoundEvents.GENERIC_WATER_JUMP.get(), "Something jumps on water");
      this.addSound((SoundEvent)TensuraSoundEvents.ARMORSAURUS_AMBIENT.get(), "Armorsaurus growls");
      this.addSound((SoundEvent)TensuraSoundEvents.ARMORSAURUS_HURT.get(), "Armorsaurus hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.ARMORSAURUS_DEATH.get(), "Armorsaurus dies");
      this.addSound((SoundEvent)TensuraSoundEvents.BARGHEST_AMBIENT.get(), "Barghest breathes");
      this.addSound((SoundEvent)TensuraSoundEvents.BARGHEST_AGGRO.get(), "Barghest growls");
      this.addSound((SoundEvent)TensuraSoundEvents.BARGHEST_HURT.get(), "Barghest hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.BARGHEST_DEATH.get(), "Barghest dies");
      this.addSound((SoundEvent)TensuraSoundEvents.BASILISK_AMBIENT.get(), "Basilisk growls");
      this.addSound((SoundEvent)TensuraSoundEvents.BASILISK_HURT.get(), "Basilisk hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.BASILISK_DEATH.get(), "Basilisk dies");
      this.addSound((SoundEvent)TensuraSoundEvents.BEAR_AMBIENT.get(), "Bear growls");
      this.addSound((SoundEvent)TensuraSoundEvents.BEAR_ATTACK.get(), "Bear attacks");
      this.addSound((SoundEvent)TensuraSoundEvents.BEAR_HURT.get(), "Bear hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.BEAR_DEATH.get(), "Bear dies");
      this.addSound((SoundEvent)TensuraSoundEvents.BIRD_AMBIENT.get(), "Bird screeches");
      this.addSound((SoundEvent)TensuraSoundEvents.BIRD_HURT.get(), "Bird hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.BIRD_DEATH.get(), "Bird dies");
      this.addSound((SoundEvent)TensuraSoundEvents.CAT_AMBIENT.get(), "Cat purrs");
      this.addSound((SoundEvent)TensuraSoundEvents.CAT_AGGRO.get(), "Cat growls");
      this.addSound((SoundEvent)TensuraSoundEvents.CAT_HURT.get(), "Cat hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.CAT_DEATH.get(), "Cat dies");
      this.addSound((SoundEvent)TensuraSoundEvents.CENTIPEDE_AMBIENT.get(), "Centipede breathes");
      this.addSound((SoundEvent)TensuraSoundEvents.CENTIPEDE_HURT.get(), "Centipede hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.CENTIPEDE_DEATH.get(), "Centipede dies");
      this.addSound((SoundEvent)TensuraSoundEvents.DAEMON_AMBIENT.get(), "Daemon breathes");
      this.addSound((SoundEvent)TensuraSoundEvents.DAEMON_HURT.get(), "Daemon hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.DAEMON_DEATH.get(), "Daemon dies");
      this.addSound((SoundEvent)TensuraSoundEvents.DIREWOLF_AMBIENT.get(), "Direwolf woof");
      this.addSound((SoundEvent)TensuraSoundEvents.DIREWOLF_AGGRO.get(), "Direwolf growls");
      this.addSound((SoundEvent)TensuraSoundEvents.DIREWOLF_HOWL.get(), "Direwolf howls");
      this.addSound((SoundEvent)TensuraSoundEvents.DIREWOLF_HURT.get(), "Direwolf hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.DIREWOLF_DEATH.get(), "Direwolf dies");
      this.addSound((SoundEvent)TensuraSoundEvents.DOG_AMBIENT.get(), "Dog woof");
      this.addSound((SoundEvent)TensuraSoundEvents.DOG_AGGRO.get(), "Dog growls");
      this.addSound((SoundEvent)TensuraSoundEvents.DOG_HURT.get(), "Dog hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.DOG_DEATH.get(), "Dog dies");
      this.addSound((SoundEvent)TensuraSoundEvents.FISH_FLOP.get(), "Fish flops");
      this.addSound((SoundEvent)TensuraSoundEvents.FISH_HURT.get(), "Fish hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.FISH_DEATH.get(), "Fish dies");
      this.addSound((SoundEvent)TensuraSoundEvents.GNOME_AMBIENT.get(), "Gnome growls");
      this.addSound((SoundEvent)TensuraSoundEvents.GNOME_HURT.get(), "Gnome hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.GNOME_DEATH.get(), "Gnome dies");
      this.addSound((SoundEvent)TensuraSoundEvents.GOBLIN_AMBIENT.get(), "Goblin talks");
      this.addSound((SoundEvent)TensuraSoundEvents.GOBLIN_AGGRO.get(), "Goblin growls");
      this.addSound((SoundEvent)TensuraSoundEvents.GOBLIN_HURT.get(), "Goblin hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.GOBLIN_DEATH.get(), "Goblin dies");
      this.addSound((SoundEvent)TensuraSoundEvents.GOLEM_AMBIENT.get(), "Golem operates");
      this.addSound((SoundEvent)TensuraSoundEvents.GOLEM_HURT.get(), "Golem hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.GOLEM_DEATH.get(), "Golem dies");
      this.addSound((SoundEvent)TensuraSoundEvents.HOVER_LIZARD_AMBIENT.get(), "Hover Lizard growls");
      this.addSound((SoundEvent)TensuraSoundEvents.HOVER_LIZARD_HURT.get(), "Hover Lizard hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.HOVER_LIZARD_DEATH.get(), "Hover Lizard dies");
      this.addSound((SoundEvent)TensuraSoundEvents.INSECT_AMBIENT.get(), "Insect breathes");
      this.addSound((SoundEvent)TensuraSoundEvents.INSECT_FLY.get(), "Insect flies");
      this.addSound((SoundEvent)TensuraSoundEvents.INSECT_HURT.get(), "Insect hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.INSECT_DEATH.get(), "Insect dies");
      this.addSound((SoundEvent)TensuraSoundEvents.LEECH_LIZARD_AMBIENT.get(), "Leech Lizard growls");
      this.addSound((SoundEvent)TensuraSoundEvents.LEECH_LIZARD_ATTACK.get(), "Leech Lizard attacks");
      this.addSound((SoundEvent)TensuraSoundEvents.LEECH_LIZARD_HURT.get(), "Leech Lizard hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.LEECH_LIZARD_DEATH.get(), "Leech Lizard dies");
      this.addSound((SoundEvent)TensuraSoundEvents.LIZARDMAN_AMBIENT.get(), "Lizardman growls");
      this.addSound((SoundEvent)TensuraSoundEvents.LIZARDMAN_HURT.get(), "Lizardman hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.LIZARDMAN_DEATH.get(), "Lizardman dies");
      this.addSound((SoundEvent)TensuraSoundEvents.MEGALODON_AMBIENT.get(), "Megalodon growls");
      this.addSound((SoundEvent)TensuraSoundEvents.MEGALODON_HURT.get(), "Megalodon hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.MEGALODON_DEATH.get(), "Megalodon dies");
      this.addSound((SoundEvent)TensuraSoundEvents.OWL_AMBIENT.get(), "Owl hoots");
      this.addSound((SoundEvent)TensuraSoundEvents.OWL_HURT.get(), "Owl hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.OWL_DEATH.get(), "Owl dies");
      this.addSound((SoundEvent)TensuraSoundEvents.SISSIE_AMBIENT.get(), "Sissie growls");
      this.addSound((SoundEvent)TensuraSoundEvents.SISSIE_HURT.get(), "Sissie hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.SISSIE_DEATH.get(), "Sissie dies");
      this.addSound((SoundEvent)TensuraSoundEvents.SPEAR_TORO_AMBIENT.get(), "Spear Toro growls");
      this.addSound((SoundEvent)TensuraSoundEvents.SPEAR_TORO_HURT.get(), "Spear Toro hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.SPEAR_TORO_DEATH.get(), "Spear Toro dies");
      this.addSound((SoundEvent)TensuraSoundEvents.SERPENT_AMBIENT.get(), "Serpent hisses");
      this.addSound((SoundEvent)TensuraSoundEvents.SERPENT_HURT.get(), "Serpent hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.SERPENT_DEATH.get(), "Serpent dies");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_AMBIENT.get(), "Flame Spirit breathes");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_HURT.get(), "Flame Spirit hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_DEATH.get(), "Flame Spirit dies");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_SPACE_AMBIENT.get(), "Space Spirit breathes");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_SPACE_HURT.get(), "Space Spirit hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_SPACE_DEATH.get(), "Space Spirit dies");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_WATER_AMBIENT.get(), "Water Spirit breathes");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_WATER_HURT.get(), "Water Spirit hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_WATER_DEATH.get(), "Water Spirit dies");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_WIND_AMBIENT.get(), "Wind Spirit breathes");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_WIND_HURT.get(), "Wind Spirit hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.SPIRIT_WIND_DEATH.get(), "Wind Spirit dies");
      this.addSound((SoundEvent)TensuraSoundEvents.METAL_SLIME_SQUISH.get(), "Metal Slime squishes");
      this.addSound((SoundEvent)TensuraSoundEvents.METAL_SLIME_ATTACK.get(), "Metal Slime attacks");
      this.addSound((SoundEvent)TensuraSoundEvents.METAL_SLIME_HURT.get(), "Metal Slime hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.METAL_SLIME_DEATH.get(), "Metal Slime dies");
      this.addSound((SoundEvent)TensuraSoundEvents.ORC_AMBIENT.get(), "Orc snorts");
      this.addSound((SoundEvent)TensuraSoundEvents.ORC_HURT.get(), "Orc hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.ORC_DEATH.get(), "Orc dies");
      this.addSound((SoundEvent)TensuraSoundEvents.ORC_LAUGH.get(), "Orc laughs");
      this.addSound((SoundEvent)TensuraSoundEvents.ORC_TRANSFORM.get(), "Orc transforms");
      this.addSound((SoundEvent)TensuraSoundEvents.TIGER_AMBIENT.get(), "Tiger growls");
      this.addSound((SoundEvent)TensuraSoundEvents.TIGER_ATTACK.get(), "Tiger attacks");
      this.addSound((SoundEvent)TensuraSoundEvents.TIGER_HURT.get(), "Tiger hurts");
      this.addSound((SoundEvent)TensuraSoundEvents.TIGER_DEATH.get(), "Tiger dies");
   }

   private void addSound(SoundEvent soundEvent, String name) {
      this.add(TensuraSoundProvider.getSubtitle(soundEvent), name);
   }

   protected void addAdvancement(ResourceLocation location, String name, String description) {
      this.add("tensura.advancements." + location.getPath() + ".title", name);
      this.add("tensura.advancements." + location.getPath() + ".description", description);
   }

   protected void addEffectAndPotions(MobEffect effect, String name) {
      this.add(effect, name);
      this.addPotionItems(BuiltInRegistries.MOB_EFFECT.getKey(effect).getPath(), name);
   }

   protected void addPotionItems(String path, String name) {
      this.add("item.minecraft.potion.effect." + path, "Potion of " + name);
      this.add("item.minecraft.splash_potion.effect." + path, "Splash Potion of " + name);
      this.add("item.minecraft.lingering_potion.effect." + path, "Lingering Potion of " + name);
      this.add("item.minecraft.tipped_arrow.effect." + path, "Arrow of " + name);
   }

   protected void addPainting(ResourceKey<PaintingVariant> painting, String name, String author) {
      String translate = "painting." + painting.location().getNamespace() + "." + painting.location().getPath();
      this.add(translate + ".title", name);
      this.add(translate + ".author", author);
   }

   protected void addTrimMaterial(ResourceKey<TrimMaterial> trim, String name) {
      this.add(Util.makeDescriptionId("trim_material", trim.location()), name);
   }

   protected void addEnchantment(ResourceKey<Enchantment> enchantment, String name, String description) {
      String translate = "enchantment." + enchantment.location().getNamespace() + "." + enchantment.location().getPath();
      this.add(translate, name);
      this.add(translate + ".desc", description);
   }

   protected void addDeathMessage(ResourceKey<DamageType> key, String message, String sourceMessage) {
      this.add("death.attack." + TensuraDamageTypes.getMsgId(key), message);
      this.add("death.attack." + TensuraDamageTypes.getMsgId(key) + ".player", message + " whilst fighting %2$s");
      this.add("death.attack." + TensuraDamageTypes.getMsgId(key) + ".item", message + " whilst fighting %2$s wielding %3$s");
      this.add("death.attack." + TensuraDamageTypes.getMsgId(key) + ".source", sourceMessage);
      this.add("death.attack." + TensuraDamageTypes.getMsgId(key) + ".source.item", sourceMessage + " using %3$s");
   }

   protected void addAttribute(Holder<Attribute> holder, String name) {
      this.add(((Attribute)holder.value()).getDescriptionId(), name);
   }

   protected void addEntity(RegistrySupplier<? extends EntityType<?>> entity, String name) {
      this.add(String.format("entity.%s.%s", entity.getId().getNamespace(), entity.getId().getPath().replace('/', '.')), name);
   }

   protected void addEntityAndSpawnEgg(RegistrySupplier<? extends EntityType<?>> entity, String name) {
      String path = entity.getId().getPath().replace('/', '.');
      this.add(String.format("entity.%s.%s", entity.getId().getNamespace(), path), name);
      this.add(String.format("item.%s.%s_spawn_egg", entity.getId().getNamespace(), path), name + " Spawn Egg");
   }

   protected void addRace(RegistrySupplier<? extends ManasRace> race, String name, String notes) {
      this.add(String.format("%s.race.%s", race.getId().getNamespace(), race.getId().getPath().replace('/', '.')), name);
      this.add(String.format("%s.race.%s.description", race.getId().getNamespace(), race.getId().getPath().replace('/', '.')), notes);
   }

   protected void addSkill(RegistrySupplier<? extends ManasSkill> skill, String name, String description) {
      this.add(String.format("%s.skill.%s", skill.getId().getNamespace(), skill.getId().getPath().replace('/', '.')), name);
      this.add(String.format("%s.skill.%s.description", skill.getId().getNamespace(), skill.getId().getPath().replace('/', '.')), description);
   }

   protected void addBanner(ResourceKey<BannerPattern> biome, String name) {
      this.add(String.format("block.tensura.banner.%s", biome.location().toShortLanguageKey()), name);
      this.add(String.format("block.tensura.banner.%s.black", biome.location().toShortLanguageKey()), "Black " + name);
      this.add(String.format("block.tensura.banner.%s.blue", biome.location().toShortLanguageKey()), "Blue " + name);
      this.add(String.format("block.tensura.banner.%s.brown", biome.location().toShortLanguageKey()), "Brown " + name);
      this.add(String.format("block.tensura.banner.%s.cyan", biome.location().toShortLanguageKey()), "Cyan " + name);
      this.add(String.format("block.tensura.banner.%s.gray", biome.location().toShortLanguageKey()), "Gray " + name);
      this.add(String.format("block.tensura.banner.%s.green", biome.location().toShortLanguageKey()), "Green " + name);
      this.add(String.format("block.tensura.banner.%s.light_blue", biome.location().toShortLanguageKey()), "Light Blue " + name);
      this.add(String.format("block.tensura.banner.%s.light_gray", biome.location().toShortLanguageKey()), "Light Gray " + name);
      this.add(String.format("block.tensura.banner.%s.lime", biome.location().toShortLanguageKey()), "Lime " + name);
      this.add(String.format("block.tensura.banner.%s.magenta", biome.location().toShortLanguageKey()), "Magenta " + name);
      this.add(String.format("block.tensura.banner.%s.orange", biome.location().toShortLanguageKey()), "Orange " + name);
      this.add(String.format("block.tensura.banner.%s.pink", biome.location().toShortLanguageKey()), "Pink " + name);
      this.add(String.format("block.tensura.banner.%s.purple", biome.location().toShortLanguageKey()), "Purple " + name);
      this.add(String.format("block.tensura.banner.%s.red", biome.location().toShortLanguageKey()), "Red " + name);
      this.add(String.format("block.tensura.banner.%s.white", biome.location().toShortLanguageKey()), "White " + name);
      this.add(String.format("block.tensura.banner.%s.yellow", biome.location().toShortLanguageKey()), "Yellow " + name);
   }

   protected void addBannerItem(RegistrySupplier<Item> key, String name, String desc) {
      this.add("item.tensura." + key.getKey().location().getPath(), name);
      this.add("item.tensura." + key.getKey().location().getPath() + ".desc", desc);
   }

   protected void addBiome(ResourceKey<Biome> biome, String name) {
      this.add(String.format("biome.%s.%s", biome.location().getNamespace(), biome.location().getPath()), name);
   }

   protected void addSetting(String id, String message, String... description) {
      this.add("tensura.settings." + id, message);

      for (int i = 0; i < description.length; i++) {
         this.add("tensura.settings." + id + ".description" + i, description[i]);
      }
   }

   protected void addSettingCategory(String id, String message) {
      this.add("tensura.settings.category." + id, message);
   }

   protected void addDefaultSideSetting(String id, String message) {
      this.addSetting(
         id,
         message,
         "Setting to Left will make the element to render from left to right and vice versa if Right",
         "Setting to Dynamic will allow the renderer to decide dynamically",
         "Default: Dynamic"
      );
   }
}
