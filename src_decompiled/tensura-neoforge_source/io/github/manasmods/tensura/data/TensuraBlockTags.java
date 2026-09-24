package io.github.manasmods.tensura.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class TensuraBlockTags {
   public static final TagKey<Block> KILNS = modTag("kilns");
   public static final TagKey<Block> MAGIC_ENGINES = modTag("magic_engines");
   public static final TagKey<Block> WARP_PADS = modTag("warp_pads");
   public static final TagKey<Block> MINEABLE_WITH_SICKLE = vanillaTag("mineable/sickle");
   public static final TagKey<Block> MINEABLE_WITH_MULTITOOL = vanillaTag("mineable/multitool");
   public static final TagKey<Block> NEEDS_MAGISTEEL_TOOL = modTag("needs_magisteel_tool");
   public static final TagKey<Block> ORES = modTag("ores");
   public static final TagKey<Block> ORES_STONE = modTag("ores_stone");
   public static final TagKey<Block> ORES_DEEPSLATE = modTag("ores_deepslate");
   public static final TagKey<Block> ORES_NETHER = modTag("ores_nether");
   public static final TagKey<Block> MAGIC_ORES = modTag("magic_ores");
   public static final TagKey<Block> SILVER_ORES = modTag("silver_ores");
   public static final TagKey<Block> MAGIC_EXPLOSION_IMMUNE = modTag("magic_explosion_immune");
   public static final TagKey<Block> BOSS_IMMUNE = modTag("boss_immune");
   public static final TagKey<Block> MOBS_SPAWNABLE_ON = modTag("mobs_spawnable_on");
   public static final TagKey<Block> BREAKABLE_BY_MONSTER = modTag("breakable_by_monster");
   public static final TagKey<Block> DIGGABLE_BY_MONSTER = modTag("diggable_by_monster");
   public static final TagKey<Block> OPENABLE_BY_NPC = modTag("openable_by_npc");
   public static final TagKey<Block> WEB_BLOCKS = modTag("web_blocks");
   public static final TagKey<Block> STICKY_BLOCKS = modTag("sticky_blocks");
   public static final TagKey<Block> WEB_REPLACEABLE = modTag("web_replaceable");
   public static final TagKey<Block> WEBBED_AVAILABLE = modTag("web_available");
   public static final TagKey<Block> LOOSE_BLOCKS = modTag("loose_blocks");
   public static final TagKey<Block> BLACK_FIRE_SOURCE = modTag("black_fire_source");
   public static final TagKey<Block> EARTH_DOMINATING = modTag("skill/earth_dominating");
   public static final TagKey<Block> EARTH_MANIPULATING = modTag("skill/earth_manipulating");
   public static final TagKey<Block> EARTH_SKILL_BREAKABLE = modTag("skill/earth_skill_breakable");
   public static final TagKey<Block> ORE_SKILL_BREAKABLE = modTag("skill/ore_skill_breakable");
   public static final TagKey<Block> HEAT_SOURCE_BLOCKS = modTag("skill/heat_source_blocks");
   public static final TagKey<Block> TRAP_BLOCKS = modTag("skill/trap_blocks");
   public static final TagKey<Block> TREASURE_BLOCKS = modTag("skill/treasure_blocks");
   public static final TagKey<Block> LABYRINTH_BLOCKS = modTag("labyrinth_blocks");
   public static final TagKey<Block> SKILL_SMELT_EASY = modTag("skill/easy_to_smelt");
   public static final TagKey<Block> SKILL_BREAK_EASY = modTag("skill/easy_to_break");
   public static final TagKey<Block> MULTI_BLOCK_IGNORE = modTag("skill/multi_block_ignore");
   public static final TagKey<Block> SKILL_UNBREAKABLE = modTag("skill/unbreakable");
   public static final TagKey<Block> SKILL_UNOBTAINABLE = modTag("skill/unobtainable");
   public static final TagKey<Block> SKILL_NOT_TELEPORTABLE = modTag("skill/not_teleportable_against");

   static TagKey<Block> modTag(String name) {
      return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
   }

   static TagKey<Block> vanillaTag(String name) {
      return create(ResourceLocation.withDefaultNamespace(name));
   }

   static TagKey<Block> create(ResourceLocation name) {
      return TagKey.create(Registries.BLOCK, name);
   }
}
