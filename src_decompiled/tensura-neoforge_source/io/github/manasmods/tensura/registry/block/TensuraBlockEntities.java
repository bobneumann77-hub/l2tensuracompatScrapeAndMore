package io.github.manasmods.tensura.registry.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.block.entity.CharybdisCoreBlockEntity;
import io.github.manasmods.tensura.block.entity.KilnBlockEntity;
import io.github.manasmods.tensura.block.entity.MagicEngineBlockEntity;
import io.github.manasmods.tensura.block.entity.MiningStationBlockEntity;
import io.github.manasmods.tensura.block.entity.OrcDisasterHeadBlockEntity;
import io.github.manasmods.tensura.block.entity.PrayingPathBlockEntity;
import io.github.manasmods.tensura.block.entity.SpellbindingBlockEntity;
import io.github.manasmods.tensura.block.entity.TensuraHangingSignBlockEntity;
import io.github.manasmods.tensura.block.entity.TensuraSignBlockEntity;
import io.github.manasmods.tensura.block.entity.ToolRackBlockEntity;
import io.github.manasmods.tensura.block.entity.WarpPadBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;

public class TensuraBlockEntities {
   private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create("tensura", Registries.BLOCK_ENTITY_TYPE);
   public static final RegistrySupplier<BlockEntityType<TensuraSignBlockEntity>> SIGN = BLOCK_ENTITY.register(
      "sign_block_entity",
      () -> Builder.of(TensuraSignBlockEntity::new, new Block[]{(Block)TensuraBlocks.PALM_STANDING_SIGN.get(), (Block)TensuraBlocks.PALM_WALL_SIGN.get()})
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<TensuraHangingSignBlockEntity>> HANGING_SIGN = BLOCK_ENTITY.register(
      "hanging_sign_block_entity",
      () -> Builder.of(
            TensuraHangingSignBlockEntity::new, new Block[]{(Block)TensuraBlocks.PALM_HANGING_SIGN.get(), (Block)TensuraBlocks.PALM_WALL_HANGING_SIGN.get()}
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<KilnBlockEntity>> KILN = BLOCK_ENTITY.register(
      "kiln_block_entity",
      () -> Builder.of(
            KilnBlockEntity::new,
            new Block[]{(Block)TensuraBlocks.KILN.get(), (Block)TensuraBlocks.KILN_MITHRIL.get(), (Block)TensuraBlocks.KILN_ORICHALCUM.get()}
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<MiningStationBlockEntity>> MINING_STATION = BLOCK_ENTITY.register(
      "mining_station_block_entity", () -> Builder.of(MiningStationBlockEntity::new, new Block[]{(Block)TensuraBlocks.MINING_STATION.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<ToolRackBlockEntity>> TOOL_RACK = BLOCK_ENTITY.register(
      "tool_rack_block_entity",
      () -> Builder.of(
            ToolRackBlockEntity::new,
            new Block[]{
               (Block)TensuraBlocks.OAK_TOOL_RACK.get(),
               (Block)TensuraBlocks.SPRUCE_TOOL_RACK.get(),
               (Block)TensuraBlocks.BIRCH_TOOL_RACK.get(),
               (Block)TensuraBlocks.JUNGLE_TOOL_RACK.get(),
               (Block)TensuraBlocks.ACACIA_TOOL_RACK.get(),
               (Block)TensuraBlocks.DARK_OAK_TOOL_RACK.get(),
               (Block)TensuraBlocks.MANGROVE_TOOL_RACK.get(),
               (Block)TensuraBlocks.CHERRY_TOOL_RACK.get(),
               (Block)TensuraBlocks.BAMBOO_TOOL_RACK.get(),
               (Block)TensuraBlocks.CRIMSON_TOOL_RACK.get(),
               (Block)TensuraBlocks.WARPED_TOOL_RACK.get(),
               (Block)TensuraBlocks.PALM_TOOL_RACK.get()
            }
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<SpellbindingBlockEntity>> SPELLBINDING = BLOCK_ENTITY.register(
      "spellbinding_block_entity", () -> Builder.of(SpellbindingBlockEntity::new, new Block[]{(Block)TensuraBlocks.SPELLBINDING_TABLE.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<MagicEngineBlockEntity>> MAGIC_ENGINE = BLOCK_ENTITY.register(
      "magic_engine_block_entity",
      () -> Builder.of(
            MagicEngineBlockEntity::new,
            new Block[]{
               (Block)TensuraBlocks.BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.STONE_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.TUFF_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.DEEPSLATE_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.MUD_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.PRISMARINE_BRICK_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.RED_NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.POLISHED_BLACKSTONE_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.QUARTZ_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.END_STONE_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.PURPUR_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.LABYRINTH_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.CREAM_LABYRINTH_BRICKS_MAGIC_ENGINE.get(),
               (Block)TensuraBlocks.DARK_LABYRINTH_BRICKS_MAGIC_ENGINE.get()
            }
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<WarpPadBlockEntity>> WARP_PAD = BLOCK_ENTITY.register(
      "warp_pad_block_entity",
      () -> Builder.of(
            WarpPadBlockEntity::new,
            new Block[]{
               (Block)TensuraBlocks.STONE_WARP_PAD.get(),
               (Block)TensuraBlocks.GRANITE_WARP_PAD.get(),
               (Block)TensuraBlocks.DIORITE_WARP_PAD.get(),
               (Block)TensuraBlocks.ANDESITE_WARP_PAD.get(),
               (Block)TensuraBlocks.CALCITE_WARP_PAD.get(),
               (Block)TensuraBlocks.TUFF_WARP_PAD.get(),
               (Block)TensuraBlocks.DEEPSLATE_WARP_PAD.get(),
               (Block)TensuraBlocks.BRICK_WARP_PAD.get(),
               (Block)TensuraBlocks.SANDSTONE_WARP_PAD.get(),
               (Block)TensuraBlocks.RED_SANDSTONE_WARP_PAD.get(),
               (Block)TensuraBlocks.SARASA_SANDSTONE_WARP_PAD.get(),
               (Block)TensuraBlocks.PACKED_MUD_WARP_PAD.get(),
               (Block)TensuraBlocks.PRISMARINE_BRICK_WARP_PAD.get(),
               (Block)TensuraBlocks.NETHER_BRICK_WARP_PAD.get(),
               (Block)TensuraBlocks.RED_NETHER_BRICK_WARP_PAD.get(),
               (Block)TensuraBlocks.BLACKSTONE_WARP_PAD.get(),
               (Block)TensuraBlocks.BASALT_WARP_PAD.get(),
               (Block)TensuraBlocks.QUARTZ_WARP_PAD.get(),
               (Block)TensuraBlocks.END_STONE_WARP_PAD.get(),
               (Block)TensuraBlocks.PURPUR_WARP_PAD.get(),
               (Block)TensuraBlocks.LABYRINTH_BRICK_WARP_PAD.get(),
               (Block)TensuraBlocks.CREAM_LABYRINTH_BRICK_WARP_PAD.get(),
               (Block)TensuraBlocks.DARK_LABYRINTH_BRICK_WARP_PAD.get(),
               (Block)TensuraBlocks.ROYAL_DWARVEN_WARP_PAD.get()
            }
         )
         .build(null)
   );
   public static final RegistrySupplier<BlockEntityType<CharybdisCoreBlockEntity>> CHARYBDIS_CORE = BLOCK_ENTITY.register(
      "charybdis_core_block_entity", () -> Builder.of(CharybdisCoreBlockEntity::new, new Block[]{(Block)TensuraBlocks.CHARYBDIS_CORE.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<OrcDisasterHeadBlockEntity>> ORC_DISASTER_HEAD = BLOCK_ENTITY.register(
      "orc_disaster_head", () -> Builder.of(OrcDisasterHeadBlockEntity::new, new Block[]{(Block)TensuraBlocks.ORC_DISASTER_HEAD.get()}).build(null)
   );
   public static final RegistrySupplier<BlockEntityType<PrayingPathBlockEntity>> PRAYING_PATH = BLOCK_ENTITY.register(
      "praying_path_block_entity", () -> Builder.of(PrayingPathBlockEntity::new, new Block[]{(Block)TensuraBlocks.LABYRINTH_PRAYING_PATH.get()}).build(null)
   );

   public static void init() {
      BLOCK_ENTITY.register();
   }
}
