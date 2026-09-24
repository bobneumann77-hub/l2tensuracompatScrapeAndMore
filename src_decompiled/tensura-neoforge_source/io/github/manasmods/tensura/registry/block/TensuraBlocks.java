package io.github.manasmods.tensura.registry.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.block.BaffledilBlock;
import io.github.manasmods.tensura.block.BlackFireBlock;
import io.github.manasmods.tensura.block.CharybdisCoreBlock;
import io.github.manasmods.tensura.block.ChilledSlimeBlock;
import io.github.manasmods.tensura.block.DummyBlock;
import io.github.manasmods.tensura.block.HellPortal;
import io.github.manasmods.tensura.block.HipokuteFlowerPotBlock;
import io.github.manasmods.tensura.block.HipokuteGrass;
import io.github.manasmods.tensura.block.KilnBlock;
import io.github.manasmods.tensura.block.LabyrinthBarrierBlock;
import io.github.manasmods.tensura.block.LabyrinthLightPathBlock;
import io.github.manasmods.tensura.block.LabyrinthLightPathSlabBlock;
import io.github.manasmods.tensura.block.LabyrinthLightPathStairBlock;
import io.github.manasmods.tensura.block.LabyrinthPortal;
import io.github.manasmods.tensura.block.LabyrinthPrayingPathBlock;
import io.github.manasmods.tensura.block.LightAirBlock;
import io.github.manasmods.tensura.block.MagicEngineBlock;
import io.github.manasmods.tensura.block.MiningStationBlock;
import io.github.manasmods.tensura.block.MothEggBlock;
import io.github.manasmods.tensura.block.OrcDisasterHead;
import io.github.manasmods.tensura.block.SlimeChunkBlock;
import io.github.manasmods.tensura.block.SmithingBenchBlock;
import io.github.manasmods.tensura.block.SolidSpaceBlock;
import io.github.manasmods.tensura.block.SpellbindingBlock;
import io.github.manasmods.tensura.block.SpiderEggBlock;
import io.github.manasmods.tensura.block.StickyCobwebBlock;
import io.github.manasmods.tensura.block.StickySteelCobwebBlock;
import io.github.manasmods.tensura.block.ThatchBed;
import io.github.manasmods.tensura.block.ToolRackBlock;
import io.github.manasmods.tensura.block.WarpPadBlock;
import io.github.manasmods.tensura.block.WebBlock;
import io.github.manasmods.tensura.block.WebbedStoneBlock;
import io.github.manasmods.tensura.block.WoodcutterBlock;
import io.github.manasmods.tensura.block.template.AllSidesDirectionalBlock;
import io.github.manasmods.tensura.block.template.HorizontalCarpetBlock;
import io.github.manasmods.tensura.block.template.LooseBlock;
import io.github.manasmods.tensura.block.template.SidewayDirectionalBlock;
import io.github.manasmods.tensura.block.template.SimpleLeaves;
import io.github.manasmods.tensura.block.template.SimpleLog;
import io.github.manasmods.tensura.block.template.TensuraCeilingHangingSignBlock;
import io.github.manasmods.tensura.block.template.TensuraSapling;
import io.github.manasmods.tensura.block.template.TensuraStandingSignBlock;
import io.github.manasmods.tensura.block.template.TensuraWallHangingSignBlock;
import io.github.manasmods.tensura.block.template.TensuraWallSignBlock;
import io.github.manasmods.tensura.entity.template.TensuraBoatEntity;
import io.github.manasmods.tensura.item.misc.SimpleBlockItem;
import io.github.manasmods.tensura.item.misc.TensuraBoatItem;
import io.github.manasmods.tensura.item.misc.TensuraChestBoatItem;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.world.TensuraConfiguredFeatures;
import io.github.manasmods.tensura.storage.chunk.ChunkStorage;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class TensuraBlocks {
   private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create("tensura", Registries.BLOCK);
   public static final RegistrySupplier<TensuraSapling> PALM_SAPLING = BLOCKS.register(
      "palm_sapling", () -> new TensuraSapling(new TreeGrower("palm", Optional.empty(), Optional.of(TensuraConfiguredFeatures.PALM_TREE), Optional.empty()))
   );
   public static final RegistrySupplier<Block> POTTED_PALM_SAPLING = BLOCKS.register(
      "potted_palm_sapling", () -> new FlowerPotBlock((Block)PALM_SAPLING.get(), Properties.ofFullCopy(Blocks.POTTED_BIRCH_SAPLING).noOcclusion())
   );
   public static final RegistrySupplier<SimpleLeaves> PALM_LEAVES = BLOCKS.register("palm_leaves", SimpleLeaves::new);
   public static final RegistrySupplier<SimpleLog> PALM_LOG = BLOCKS.register("palm_log", () -> new SimpleLog(MapColor.TERRACOTTA_WHITE, MapColor.WOOD));
   public static final RegistrySupplier<SimpleLog> STRIPPED_PALM_LOG = BLOCKS.register(
      "stripped_palm_log", () -> new SimpleLog(MapColor.TERRACOTTA_WHITE, MapColor.WOOD)
   );
   public static final RegistrySupplier<SimpleLog> PALM_WOOD = BLOCKS.register("palm_wood", () -> new SimpleLog(MapColor.TERRACOTTA_WHITE, MapColor.WOOD));
   public static final RegistrySupplier<SimpleLog> STRIPPED_PALM_WOOD = BLOCKS.register(
      "stripped_palm_wood", () -> new SimpleLog(MapColor.TERRACOTTA_WHITE, MapColor.WOOD)
   );
   public static final RegistrySupplier<Block> PALM_PLANKS = BLOCKS.register(
      "palm_planks",
      () -> new Block(Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
         }
      }
   );
   public static final RegistrySupplier<StairBlock> PALM_STAIRS = BLOCKS.register(
      "palm_stairs", () -> new StairBlock(((Block)PALM_PLANKS.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)PALM_PLANKS.get())) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
         }
      }
   );
   public static final RegistrySupplier<SlabBlock> PALM_SLAB = BLOCKS.register(
      "palm_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)PALM_PLANKS.get())) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
         }
      }
   );
   public static final RegistrySupplier<DoorBlock> PALM_DOOR = BLOCKS.register(
      "palm_door",
      () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy((BlockBehaviour)PALM_PLANKS.get()).noOcclusion().pushReaction(PushReaction.DESTROY))
   );
   public static final RegistrySupplier<TrapDoorBlock> PALM_TRAPDOOR = BLOCKS.register(
      "palm_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy((BlockBehaviour)PALM_PLANKS.get()).noOcclusion().pushReaction(PushReaction.DESTROY))
   );
   public static final RegistrySupplier<FenceBlock> PALM_FENCE = BLOCKS.register(
      "palm_fence", () -> new FenceBlock(Properties.ofFullCopy((BlockBehaviour)PALM_PLANKS.get()).forceSolidOn()) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
         }
      }
   );
   public static final RegistrySupplier<FenceGateBlock> PALM_FENCE_GATE = BLOCKS.register(
      "palm_fence_gate", () -> new FenceGateBlock(TensuraWoodTypes.PALM, Properties.ofFullCopy((BlockBehaviour)PALM_PLANKS.get()).forceSolidOn()) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 20;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 5;
         }
      }
   );
   public static final RegistrySupplier<ButtonBlock> PALM_BUTTON = BLOCKS.register(
      "palm_button", () -> new ButtonBlock(BlockSetType.ACACIA, 30, Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY))
   );
   public static final RegistrySupplier<PressurePlateBlock> PALM_PRESSURE_PLATE = BLOCKS.register(
      "palm_pressure_plate",
      () -> new PressurePlateBlock(
         BlockSetType.OAK,
         Properties.of()
            .mapColor(((Block)PALM_PLANKS.get()).defaultMapColor())
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASS)
            .noCollission()
            .strength(0.5F)
            .ignitedByLava()
            .pushReaction(PushReaction.DESTROY)
      )
   );
   public static final RegistrySupplier<TensuraStandingSignBlock> PALM_STANDING_SIGN = BLOCKS.register(
      "palm_sign",
      () -> new TensuraStandingSignBlock(TensuraWoodTypes.PALM, Properties.ofFullCopy(Blocks.OAK_SIGN).mapColor(((Block)PALM_PLANKS.get()).defaultMapColor()))
   );
   public static final RegistrySupplier<TensuraWallSignBlock> PALM_WALL_SIGN = BLOCKS.register(
      "palm_wall_sign",
      () -> new TensuraWallSignBlock(TensuraWoodTypes.PALM, Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).mapColor(((Block)PALM_PLANKS.get()).defaultMapColor()))
   );
   public static final RegistrySupplier<TensuraCeilingHangingSignBlock> PALM_HANGING_SIGN = BLOCKS.register(
      "palm_hanging_sign",
      () -> new TensuraCeilingHangingSignBlock(
         TensuraWoodTypes.PALM, Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).mapColor(((Block)PALM_PLANKS.get()).defaultMapColor())
      )
   );
   public static final RegistrySupplier<TensuraWallHangingSignBlock> PALM_WALL_HANGING_SIGN = BLOCKS.register(
      "palm_wall_hanging_sign",
      () -> new TensuraWallHangingSignBlock(
         TensuraWoodTypes.PALM, Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).mapColor(((Block)PALM_PLANKS.get()).defaultMapColor())
      )
   );
   public static final RegistrySupplier<RotatedPillarBlock> THATCH_BLOCK = BLOCKS.register(
      "thatch_block",
      () -> new HayBlock(
         Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .ignitedByLava()
            .isValidSpawn((state, world, pos, entityLiving) -> false)
            .instrument(NoteBlockInstrument.BANJO)
            .strength(0.5F)
            .sound(SoundType.GRASS)
      ) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
         }
      }
   );
   public static final RegistrySupplier<StairBlock> THATCH_STAIRS = BLOCKS.register(
      "thatch_stairs",
      () -> new StairBlock(((RotatedPillarBlock)THATCH_BLOCK.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)THATCH_BLOCK.get())) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
         }
      }
   );
   public static final RegistrySupplier<SlabBlock> THATCH_SLAB = BLOCKS.register(
      "thatch_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)THATCH_BLOCK.get())) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
         }
      }
   );
   public static final RegistrySupplier<WallBlock> THATCH_WALL = BLOCKS.register(
      "thatch_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)THATCH_BLOCK.get())) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
         }
      }
   );
   public static final RegistrySupplier<Block> TATAMI_BLOCK = BLOCKS.register(
      "tatami_block",
      () -> new AllSidesDirectionalBlock(
         Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .ignitedByLava()
            .isValidSpawn((state, world, pos, entityLiving) -> false)
            .instrument(NoteBlockInstrument.BANJO)
            .strength(1.0F)
            .sound(SoundType.GRASS)
      ) {
         @Override
         public BlockState getStateForPlacement(BlockPlaceContext pContext) {
            return (BlockState)this.defaultBlockState().setValue(FACING, pContext.getClickedFace());
         }

         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
         }
      }
   );
   public static final RegistrySupplier<Block> TATAMI_CARPET = BLOCKS.register(
      "tatami_carpet", () -> new HorizontalCarpetBlock(Properties.ofFullCopy((BlockBehaviour)TATAMI_BLOCK.get())) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
         }
      }
   );
   public static final RegistrySupplier<Block> SINGLE_TATAMI_BLOCK = BLOCKS.register(
      "single_tatami_block", () -> new Block(Properties.ofFullCopy((BlockBehaviour)TATAMI_BLOCK.get())) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
         }
      }
   );
   public static final RegistrySupplier<Block> SINGLE_TATAMI_CARPET = BLOCKS.register(
      "single_tatami_carpet", () -> new CarpetBlock(Properties.ofFullCopy((BlockBehaviour)SINGLE_TATAMI_BLOCK.get())) {
         public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 60;
         }

         public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
            return 30;
         }
      }
   );
   public static final RegistrySupplier<Block> MAGIC_ORE = BLOCKS.register(
      "magic_ore",
      () -> new DropExperienceBlock(
         UniformInt.of(3, 7),
         Properties.of()
            .mapColor(MapColor.STONE)
            .lightLevel(blockState -> 5)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(3.0F, 500.0F)
      )
   );
   public static final RegistrySupplier<Block> SILVER_ORE = BLOCKS.register(
      "silver_ore",
      () -> new DropExperienceBlock(
         UniformInt.of(3, 7),
         Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)
      )
   );
   public static final RegistrySupplier<Block> DEEPSLATE_MAGIC_ORE = BLOCKS.register(
      "deepslate_magic_ore",
      () -> new DropExperienceBlock(
         UniformInt.of(3, 7),
         Properties.ofFullCopy((BlockBehaviour)MAGIC_ORE.get())
            .lightLevel(blockState -> 5)
            .mapColor(MapColor.DEEPSLATE)
            .strength(4.5F, 500.0F)
            .sound(SoundType.DEEPSLATE)
      )
   );
   public static final RegistrySupplier<Block> DEEPSLATE_SILVER_ORE = BLOCKS.register(
      "deepslate_silver_ore",
      () -> new DropExperienceBlock(
         UniformInt.of(3, 7),
         Properties.ofFullCopy((BlockBehaviour)SILVER_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)
      )
   );
   public static final RegistrySupplier<Block> RAW_SILVER_BLOCK = BLOCKS.register(
      "raw_silver_block",
      () -> new Block(Properties.of().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F))
   );
   public static final RegistrySupplier<Block> SILVER_BLOCK = BLOCKS.register(
      "silver_block",
      () -> new Block(
         Properties.of().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BELL).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.METAL)
      )
   );
   public static final RegistrySupplier<Block> MAGIC_ORE_BLOCK = BLOCKS.register(
      "magic_ore_block",
      () -> new AmethystBlock(
         Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .strength(5.0F, 500.0F)
            .sound(SoundType.AMETHYST)
            .requiresCorrectToolForDrops()
            .lightLevel(blockState -> 15)
      )
   );
   public static final RegistrySupplier<Block> LOW_MAGISTEEL_BLOCK = BLOCKS.register(
      "low_magisteel_block",
      () -> new Block(
         Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 500.0F).sound(SoundType.METAL).lightLevel(blockState -> 11)
      )
   );
   public static final RegistrySupplier<Block> HIGH_MAGISTEEL_BLOCK = BLOCKS.register(
      "high_magisteel_block",
      () -> new Block(
         Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(10.0F, 600.0F).sound(SoundType.METAL).lightLevel(blockState -> 13)
      )
   );
   public static final RegistrySupplier<Block> PURE_MAGISTEEL_BLOCK = BLOCKS.register(
      "pure_magisteel_block",
      () -> new Block(
         Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(40.0F, 1200.0F).sound(SoundType.METAL).lightLevel(blockState -> 15)
      )
   );
   public static final RegistrySupplier<Block> MITHRIL_BLOCK = BLOCKS.register(
      "mithril_block",
      () -> new Block(
         Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(20.0F, 800.0F).sound(SoundType.METAL).lightLevel(blockState -> 14)
      )
   );
   public static final RegistrySupplier<Block> ORICHALCUM_BLOCK = BLOCKS.register(
      "orichalcum_block",
      () -> new Block(
         Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(30.0F, 1000.0F).sound(SoundType.METAL).lightLevel(blockState -> 14)
      )
   );
   public static final RegistrySupplier<Block> ADAMANTITE_BLOCK = BLOCKS.register(
      "adamantite_block",
      () -> new Block(
         Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(50.0F, 1400.0F).sound(SoundType.METAL).lightLevel(blockState -> 15)
      )
   );
   public static final RegistrySupplier<Block> HIHIIROKANE_BLOCK = BLOCKS.register(
      "hihiirokane_block",
      () -> new Block(
         Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(60.0F, 1600.0F).sound(SoundType.METAL).lightLevel(blockState -> 15)
      )
   );
   public static final RegistrySupplier<Block> LABYRINTH_LAMP = BLOCKS.register(
      "labyrinth_lamp", () -> new SidewayDirectionalBlock(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(-1.0F, 3600000.0F).noLootTable())
   );
   public static final RegistrySupplier<Block> LABYRINTH_LAMP_TL = BLOCKS.register(
      "labyrinth_lamp_tl", () -> new SidewayDirectionalBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_LAMP.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_LAMP_TR = BLOCKS.register(
      "labyrinth_lamp_tr", () -> new SidewayDirectionalBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_LAMP.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_LAMP_BL = BLOCKS.register(
      "labyrinth_lamp_bl", () -> new SidewayDirectionalBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_LAMP.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_LAMP_BR = BLOCKS.register(
      "labyrinth_lamp_br", () -> new SidewayDirectionalBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_LAMP.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_LIT_LAMP = BLOCKS.register(
      "labyrinth_lit_lamp",
      () -> new SidewayDirectionalBlock(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(-1.0F, 3600000.0F).lightLevel(state -> 15).noLootTable())
   );
   public static final RegistrySupplier<Block> LABYRINTH_LIT_LAMP_TL = BLOCKS.register(
      "labyrinth_lit_lamp_tl", () -> new SidewayDirectionalBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_LIT_LAMP.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_LIT_LAMP_TR = BLOCKS.register(
      "labyrinth_lit_lamp_tr", () -> new SidewayDirectionalBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_LIT_LAMP.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_LIT_LAMP_BL = BLOCKS.register(
      "labyrinth_lit_lamp_bl", () -> new SidewayDirectionalBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_LIT_LAMP.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_LIT_LAMP_BR = BLOCKS.register(
      "labyrinth_lit_lamp_br", () -> new SidewayDirectionalBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_LIT_LAMP.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_BRICKS = BLOCKS.register(
      "labyrinth_bricks", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(-1.0F, 3600000.0F).noLootTable())
   );
   public static final RegistrySupplier<StairBlock> LABYRINTH_BRICK_STAIR = BLOCKS.register(
      "labyrinth_brick_stairs",
      () -> new StairBlock(((Block)LABYRINTH_BRICKS.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<SlabBlock> LABYRINTH_BRICK_SLAB = BLOCKS.register(
      "labyrinth_brick_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_BRICK_TL = BLOCKS.register(
      "labyrinth_bricks_tl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_BRICK_TR = BLOCKS.register(
      "labyrinth_bricks_tr", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_BRICK_BL = BLOCKS.register(
      "labyrinth_bricks_bl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_BRICK_BR = BLOCKS.register(
      "labyrinth_bricks_br", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_STONE = BLOCKS.register(
      "labyrinth_stone", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<StairBlock> LABYRINTH_STONE_STAIR = BLOCKS.register(
      "labyrinth_stone_stairs",
      () -> new StairBlock(((Block)LABYRINTH_STONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<SlabBlock> LABYRINTH_STONE_SLAB = BLOCKS.register(
      "labyrinth_stone_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_STONE_TL = BLOCKS.register(
      "labyrinth_stone_tl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_STONE_TR = BLOCKS.register(
      "labyrinth_stone_tr", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_STONE_BL = BLOCKS.register(
      "labyrinth_stone_bl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_STONE_BR = BLOCKS.register(
      "labyrinth_stone_br", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CREAM_LABYRINTH_BRICKS = BLOCKS.register(
      "cream_labyrinth_bricks", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<StairBlock> CREAM_LABYRINTH_BRICK_STAIR = BLOCKS.register(
      "cream_labyrinth_brick_stairs",
      () -> new StairBlock(((Block)CREAM_LABYRINTH_BRICKS.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<SlabBlock> CREAM_LABYRINTH_BRICK_SLAB = BLOCKS.register(
      "cream_labyrinth_brick_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CREAM_LABYRINTH_BRICK_TL = BLOCKS.register(
      "cream_labyrinth_bricks_tl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CREAM_LABYRINTH_BRICK_TR = BLOCKS.register(
      "cream_labyrinth_bricks_tr", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CREAM_LABYRINTH_BRICK_BL = BLOCKS.register(
      "cream_labyrinth_bricks_bl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CREAM_LABYRINTH_BRICK_BR = BLOCKS.register(
      "cream_labyrinth_bricks_br", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CREAM_LABYRINTH_STONE = BLOCKS.register(
      "cream_labyrinth_stone", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<StairBlock> CREAM_LABYRINTH_STONE_STAIR = BLOCKS.register(
      "cream_labyrinth_stone_stairs",
      () -> new StairBlock(((Block)CREAM_LABYRINTH_STONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<SlabBlock> CREAM_LABYRINTH_STONE_SLAB = BLOCKS.register(
      "cream_labyrinth_stone_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CREAM_LABYRINTH_STONE_TL = BLOCKS.register(
      "cream_labyrinth_stone_tl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CREAM_LABYRINTH_STONE_TR = BLOCKS.register(
      "cream_labyrinth_stone_tr", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CREAM_LABYRINTH_STONE_BL = BLOCKS.register(
      "cream_labyrinth_stone_bl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CREAM_LABYRINTH_STONE_BR = BLOCKS.register(
      "cream_labyrinth_stone_br", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> DARK_LABYRINTH_BRICKS = BLOCKS.register(
      "dark_labyrinth_bricks", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<StairBlock> DARK_LABYRINTH_BRICK_STAIR = BLOCKS.register(
      "dark_labyrinth_brick_stairs",
      () -> new StairBlock(((Block)DARK_LABYRINTH_BRICKS.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<SlabBlock> DARK_LABYRINTH_BRICK_SLAB = BLOCKS.register(
      "dark_labyrinth_brick_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> DARK_LABYRINTH_BRICK_TL = BLOCKS.register(
      "dark_labyrinth_bricks_tl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> DARK_LABYRINTH_BRICK_TR = BLOCKS.register(
      "dark_labyrinth_bricks_tr", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> DARK_LABYRINTH_BRICK_BL = BLOCKS.register(
      "dark_labyrinth_bricks_bl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> DARK_LABYRINTH_BRICK_BR = BLOCKS.register(
      "dark_labyrinth_bricks_br", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> DARK_LABYRINTH_STONE = BLOCKS.register(
      "dark_labyrinth_stone", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<StairBlock> DARK_LABYRINTH_STONE_STAIR = BLOCKS.register(
      "dark_labyrinth_stone_stairs",
      () -> new StairBlock(((Block)DARK_LABYRINTH_STONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<SlabBlock> DARK_LABYRINTH_STONE_SLAB = BLOCKS.register(
      "dark_labyrinth_stone_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> DARK_LABYRINTH_STONE_TL = BLOCKS.register(
      "dark_labyrinth_stone_tl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> DARK_LABYRINTH_STONE_TR = BLOCKS.register(
      "dark_labyrinth_stone_tr", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> DARK_LABYRINTH_STONE_BL = BLOCKS.register(
      "dark_labyrinth_stone_bl", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> DARK_LABYRINTH_STONE_BR = BLOCKS.register(
      "dark_labyrinth_stone_br", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> LABYRINTH_PORTAL = BLOCKS.register("labyrinth_portal", LabyrinthPortal::new);
   public static final RegistrySupplier<Block> LABYRINTH_BARRIER_BLOCK = BLOCKS.register("labyrinth_barrier_block", LabyrinthBarrierBlock::new);
   public static final RegistrySupplier<Block> LABYRINTH_CRYSTAL = BLOCKS.register(
      "labyrinth_crystal", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()).strength(1.5F).sound(SoundType.AMETHYST))
   );
   public static final RegistrySupplier<Block> LABYRINTH_PRAYING_PATH = BLOCKS.register(
      "labyrinth_praying_path",
      () -> new LabyrinthPrayingPathBlock(
         Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .noLootTable()
            .sound(SoundType.AMETHYST)
            .noOcclusion()
            .lightLevel(state -> 15)
            .strength(-1.0F, 3600000.0F)
      )
   );
   public static final RegistrySupplier<Block> LABYRINTH_LIGHT_PATH = BLOCKS.register("labyrinth_light_path", LabyrinthLightPathBlock::new);
   public static final RegistrySupplier<StairBlock> LABYRINTH_LIGHT_PATH_STAIRS = BLOCKS.register(
      "labyrinth_light_path_stairs",
      () -> new LabyrinthLightPathStairBlock(
         ((Block)LABYRINTH_LIGHT_PATH.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)LABYRINTH_LIGHT_PATH.get()).noLootTable()
      )
   );
   public static final RegistrySupplier<SlabBlock> LABYRINTH_LIGHT_PATH_SLAB = BLOCKS.register(
      "labyrinth_light_path_slab", () -> new LabyrinthLightPathSlabBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_LIGHT_PATH.get()).noLootTable())
   );
   public static final RegistrySupplier<Block> HELL_PORTAL = BLOCKS.register("hell_portal", HellPortal::new);
   public static final RegistrySupplier<Block> LOW_QUALITY_MAGIC_CRYSTAL_BLOCK = BLOCKS.register(
      "low_quality_magic_crystal_block",
      () -> new AllSidesDirectionalBlock(Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(1.5F).sound(SoundType.AMETHYST))
   );
   public static final RegistrySupplier<StairBlock> LOW_QUALITY_MAGIC_CRYSTAL_STAIRS = BLOCKS.register(
      "low_quality_magic_crystal_stairs",
      () -> new StairBlock(
         ((Block)LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get())
      )
   );
   public static final RegistrySupplier<SlabBlock> LOW_QUALITY_MAGIC_CRYSTAL_SLAB = BLOCKS.register(
      "low_quality_magic_crystal_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get()))
   );
   public static final RegistrySupplier<Block> LOW_QUALITY_MAGIC_CRYSTAL_BRICKS = BLOCKS.register(
      "low_quality_magic_crystal_bricks", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get()))
   );
   public static final RegistrySupplier<StairBlock> LOW_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS = BLOCKS.register(
      "low_quality_magic_crystal_brick_stairs",
      () -> new StairBlock(
         ((Block)LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get())
      )
   );
   public static final RegistrySupplier<SlabBlock> LOW_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB = BLOCKS.register(
      "low_quality_magic_crystal_brick_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get()))
   );
   public static final RegistrySupplier<WallBlock> LOW_QUALITY_MAGIC_CRYSTAL_BRICK_WALL = BLOCKS.register(
      "low_quality_magic_crystal_brick_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get()))
   );
   public static final RegistrySupplier<Block> CHISELED_LOW_QUALITY_MAGIC_CRYSTAL_BRICKS = BLOCKS.register(
      "chiseled_low_quality_magic_crystal_bricks", () -> new Block(Properties.ofFullCopy((BlockBehaviour)LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get()))
   );
   public static final RegistrySupplier<Block> MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK = BLOCKS.register(
      "medium_quality_magic_crystal_block",
      () -> new AllSidesDirectionalBlock(Properties.ofFullCopy((BlockBehaviour)LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get()).strength(2.0F))
   );
   public static final RegistrySupplier<StairBlock> MEDIUM_QUALITY_MAGIC_CRYSTAL_STAIRS = BLOCKS.register(
      "medium_quality_magic_crystal_stairs",
      () -> new StairBlock(
         ((Block)MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get())
      )
   );
   public static final RegistrySupplier<SlabBlock> MEDIUM_QUALITY_MAGIC_CRYSTAL_SLAB = BLOCKS.register(
      "meidum_quality_magic_crystal_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get()))
   );
   public static final RegistrySupplier<Block> MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS = BLOCKS.register(
      "medium_quality_magic_crystal_bricks", () -> new Block(Properties.ofFullCopy((BlockBehaviour)MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get()))
   );
   public static final RegistrySupplier<StairBlock> MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS = BLOCKS.register(
      "medium_quality_magic_crystal_brick_stairs",
      () -> new StairBlock(
         ((Block)MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()).defaultBlockState(),
         Properties.ofFullCopy((BlockBehaviour)MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get())
      )
   );
   public static final RegistrySupplier<SlabBlock> MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB = BLOCKS.register(
      "medium_quality_magic_crystal_brick_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()))
   );
   public static final RegistrySupplier<WallBlock> MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_WALL = BLOCKS.register(
      "medium_quality_magic_crystal_brick_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CHISELED_MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS = BLOCKS.register(
      "chiseled_medium_quality_magic_crystal_bricks", () -> new Block(Properties.ofFullCopy((BlockBehaviour)MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK = BLOCKS.register(
      "high_quality_magic_crystal_block",
      () -> new AllSidesDirectionalBlock(Properties.ofFullCopy((BlockBehaviour)LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get()).strength(3.0F))
   );
   public static final RegistrySupplier<StairBlock> HIGH_QUALITY_MAGIC_CRYSTAL_STAIRS = BLOCKS.register(
      "high_quality_magic_crystal_stairs",
      () -> new StairBlock(
         ((Block)HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get())
      )
   );
   public static final RegistrySupplier<SlabBlock> HIGH_QUALITY_MAGIC_CRYSTAL_SLAB = BLOCKS.register(
      "high_quality_magic_crystal_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get()))
   );
   public static final RegistrySupplier<Block> HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS = BLOCKS.register(
      "high_quality_magic_crystal_bricks", () -> new Block(Properties.ofFullCopy((BlockBehaviour)HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get()))
   );
   public static final RegistrySupplier<StairBlock> HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS = BLOCKS.register(
      "high_quality_magic_crystal_brick_stairs",
      () -> new StairBlock(
         ((Block)HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get())
      )
   );
   public static final RegistrySupplier<SlabBlock> HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB = BLOCKS.register(
      "high_quality_magic_crystal_brick_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()))
   );
   public static final RegistrySupplier<WallBlock> HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_WALL = BLOCKS.register(
      "high_quality_magic_crystal_brick_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> CHISELED_HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS = BLOCKS.register(
      "chiseled_high_quality_magic_crystal_bricks", () -> new Block(Properties.ofFullCopy((BlockBehaviour)HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> SARASA_SAND = BLOCKS.register(
      "sarasa_sand",
      () -> new ColoredFallingBlock(
         new ColorRGBA(14406560), Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND)
      )
   );
   public static final RegistrySupplier<Block> SARASA_SANDSTONE = BLOCKS.register(
      "sarasa_sandstone",
      () -> new Block(Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.8F))
   );
   public static final RegistrySupplier<StairBlock> SARASA_SANDSTONE_STAIRS = BLOCKS.register(
      "sarasa_sandstone_stairs",
      () -> new StairBlock(((Block)SARASA_SANDSTONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)SARASA_SANDSTONE.get()))
   );
   public static final RegistrySupplier<SlabBlock> SARASA_SANDSTONE_SLAB = BLOCKS.register(
      "sarasa_sandstone_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)SARASA_SANDSTONE.get()))
   );
   public static final RegistrySupplier<WallBlock> SARASA_SANDSTONE_WALL = BLOCKS.register(
      "sarasa_sandstone_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)SARASA_SANDSTONE.get()))
   );
   public static final RegistrySupplier<Block> CHISELED_SARASA_SANDSTONE = BLOCKS.register(
      "chiseled_sarasa_sandstone", () -> new Block(Properties.ofFullCopy((BlockBehaviour)SARASA_SANDSTONE.get()))
   );
   public static final RegistrySupplier<Block> CUT_SARASA_SANDSTONE = BLOCKS.register(
      "cut_sarasa_sandstone", () -> new Block(Properties.ofFullCopy((BlockBehaviour)SARASA_SANDSTONE.get()))
   );
   public static final RegistrySupplier<SlabBlock> CUT_SARASA_SANDSTONE_SLAB = BLOCKS.register(
      "cut_sarasa_sandstone_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)SARASA_SANDSTONE.get()))
   );
   public static final RegistrySupplier<Block> SMOOTH_SARASA_SANDSTONE = BLOCKS.register(
      "smooth_sarasa_sandstone",
      () -> new Block(Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F))
   );
   public static final RegistrySupplier<StairBlock> SMOOTH_SARASA_SANDSTONE_STAIRS = BLOCKS.register(
      "smooth_sarasa_sandstone_stairs",
      () -> new StairBlock(((Block)SMOOTH_SARASA_SANDSTONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)SMOOTH_SARASA_SANDSTONE.get()))
   );
   public static final RegistrySupplier<SlabBlock> SMOOTH_SARASA_SANDSTONE_SLAB = BLOCKS.register(
      "smooth_sarasa_sandstone_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)SMOOTH_SARASA_SANDSTONE.get()))
   );
   public static final RegistrySupplier<Block> STICKY_COBWEB = BLOCKS.register(
      "sticky_cobweb",
      () -> new StickyCobwebBlock(
         Properties.of()
            .mapColor(MapColor.WOOL)
            .sound(SoundType.COBWEB)
            .forceSolidOn()
            .noCollission()
            .noOcclusion()
            .requiresCorrectToolForDrops()
            .noLootTable()
            .strength(6.0F)
            .pushReaction(PushReaction.DESTROY)
            .isValidSpawn(WebBlock::always)
      )
   );
   public static final RegistrySupplier<Block> STICKY_STEEL_COBWEB = BLOCKS.register(
      "sticky_steel_cobweb",
      () -> new StickySteelCobwebBlock(
         Properties.of()
            .mapColor(MapColor.WOOL)
            .sound(SoundType.COBWEB)
            .forceSolidOn()
            .noCollission()
            .noOcclusion()
            .requiresCorrectToolForDrops()
            .noLootTable()
            .strength(8.0F)
            .pushReaction(PushReaction.DESTROY)
            .isValidSpawn(WebBlock::always)
      )
   );
   public static final RegistrySupplier<Block> SLIME_CHUNK_BLOCK = BLOCKS.register(
      "slime_chunk_block",
      () -> new SlimeChunkBlock(Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).friction(0.8F).sound(SoundType.SLIME_BLOCK).noOcclusion())
   );
   public static final RegistrySupplier<Block> CHILLED_SLIME_BLOCK = BLOCKS.register(
      "chilled_slime_block", () -> new ChilledSlimeBlock(Properties.ofFullCopy((BlockBehaviour)SLIME_CHUNK_BLOCK.get()))
   );
   public static final RegistrySupplier<MothEggBlock> MOTH_EGG = BLOCKS.register(
      "moth_egg", () -> new MothEggBlock(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(0.5F).sound(SoundType.WOOL).randomTicks().noOcclusion())
   );
   public static final RegistrySupplier<Block> CHARYBDIS_CORE = BLOCKS.register("charybdis_core", CharybdisCoreBlock::new);
   public static final RegistrySupplier<Block> SPIDER_EGG = BLOCKS.register("spider_egg", SpiderEggBlock::new);
   public static final RegistrySupplier<Block> WEB_BLOCK = BLOCKS.register("web_block", WebBlock::new);
   public static final RegistrySupplier<StairBlock> WEB_STAIRS = BLOCKS.register(
      "web_stairs", () -> new StairBlock(((Block)WEB_BLOCK.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)WEB_BLOCK.get()))
   );
   public static final RegistrySupplier<SlabBlock> WEB_SLAB = BLOCKS.register(
      "web_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)WEB_BLOCK.get()))
   );
   public static final RegistrySupplier<Block> WEBBED_COBBLESTONE = BLOCKS.register("webbed_cobblestone", WebbedStoneBlock::new);
   public static final RegistrySupplier<StairBlock> WEBBED_COBBLESTONE_STAIRS = BLOCKS.register(
      "webbed_cobblestone_stairs",
      () -> new StairBlock(((Block)WEBBED_COBBLESTONE.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)WEBBED_COBBLESTONE.get()))
   );
   public static final RegistrySupplier<SlabBlock> WEBBED_COBBLESTONE_SLAB = BLOCKS.register(
      "webbed_cobblestone_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)WEBBED_COBBLESTONE.get()))
   );
   public static final RegistrySupplier<WallBlock> WEBBED_COBBLESTONE_WALL = BLOCKS.register(
      "webbed_cobblestone_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)WEBBED_COBBLESTONE.get()))
   );
   public static final RegistrySupplier<Block> WEBBED_STONE_BRICKS = BLOCKS.register("webbed_stone_bricks", WebbedStoneBlock::new);
   public static final RegistrySupplier<StairBlock> WEBBED_STONE_BRICK_STAIRS = BLOCKS.register(
      "webbed_stone_brick_stairs",
      () -> new StairBlock(((Block)WEBBED_STONE_BRICKS.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)WEBBED_STONE_BRICKS.get()))
   );
   public static final RegistrySupplier<SlabBlock> WEBBED_STONE_BRICK_SLAB = BLOCKS.register(
      "webbed_stone_brick_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)WEBBED_STONE_BRICKS.get()))
   );
   public static final RegistrySupplier<WallBlock> WEBBED_STONE_BRICK_WALL = BLOCKS.register(
      "webbed_stone_brick_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)WEBBED_STONE_BRICKS.get()))
   );
   public static final RegistrySupplier<Block> BLACK_FIRE = BLOCKS.register("black_fire", BlackFireBlock::new);
   public static final RegistrySupplier<MagicEngineBlock> BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "bricks_magic_engine", () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.BRICKS).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> STONE_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "stone_bricks_magic_engine", () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> TUFF_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "tuff_bricks_magic_engine", () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.TUFF_BRICKS).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> DEEPSLATE_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "deepslate_bricks_magic_engine",
      () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> MUD_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "mud_bricks_magic_engine", () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.MUD_BRICKS).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> PRISMARINE_BRICK_MAGIC_ENGINE = BLOCKS.register(
      "prismarine_bricks_magic_engine",
      () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "nether_bricks_magic_engine", () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.NETHER_BRICKS).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> RED_NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "red_nether_bricks_magic_engine",
      () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> POLISHED_BLACKSTONE_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "polished_blackstone_bricks_magic_engine",
      () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> QUARTZ_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "quartz_bricks_magic_engine", () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.QUARTZ_BRICKS).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> END_STONE_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "end_stone_bricks_magic_engine",
      () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.END_STONE_BRICKS).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> PURPUR_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "purpur_bricks_magic_engine", () -> new MagicEngineBlock(Properties.ofFullCopy(Blocks.PURPUR_BLOCK).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> LOW_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "low_quality_magic_crystal_bricks_magic_engine",
      () -> new MagicEngineBlock(Properties.ofFullCopy((BlockBehaviour)LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get()).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "medium_quality_magic_crystal_bricks_magic_engine",
      () -> new MagicEngineBlock(
         Properties.ofFullCopy((BlockBehaviour)MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get()).lightLevel(MagicEngineBlock.getLightEmission())
      )
   );
   public static final RegistrySupplier<MagicEngineBlock> HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "high_quality_magic_crystal_bricks_magic_engine",
      () -> new MagicEngineBlock(Properties.ofFullCopy((BlockBehaviour)HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get()).lightLevel(MagicEngineBlock.getLightEmission()))
   );
   public static final RegistrySupplier<MagicEngineBlock> LABYRINTH_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "labyrinth_bricks_magic_engine",
      () -> new MagicEngineBlock(
         ChunkStorage.CONFIG.labyrinthMagicEngineReduction,
         ChunkStorage.CONFIG.labyrinthMagicEngineRange,
         true,
         Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()).lightLevel(MagicEngineBlock.getLightEmission())
      )
   );
   public static final RegistrySupplier<MagicEngineBlock> CREAM_LABYRINTH_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "cream_labyrinth_bricks_magic_engine",
      () -> new MagicEngineBlock(
         ChunkStorage.CONFIG.labyrinthMagicEngineReduction,
         ChunkStorage.CONFIG.labyrinthMagicEngineRange,
         true,
         Properties.ofFullCopy((BlockBehaviour)CREAM_LABYRINTH_BRICKS.get()).lightLevel(MagicEngineBlock.getLightEmission())
      )
   );
   public static final RegistrySupplier<MagicEngineBlock> DARK_LABYRINTH_BRICKS_MAGIC_ENGINE = BLOCKS.register(
      "dark_labyrinth_bricks_magic_engine",
      () -> new MagicEngineBlock(
         ChunkStorage.CONFIG.labyrinthMagicEngineReduction,
         ChunkStorage.CONFIG.labyrinthMagicEngineRange,
         true,
         Properties.ofFullCopy((BlockBehaviour)DARK_LABYRINTH_BRICKS.get()).lightLevel(MagicEngineBlock.getLightEmission())
      )
   );
   public static final RegistrySupplier<MiningStationBlock> MINING_STATION = BLOCKS.register("mining_station", MiningStationBlock::new);
   public static final RegistrySupplier<SmithingBenchBlock> SMITHING_BENCH = BLOCKS.register("smithing_bench", SmithingBenchBlock::new);
   public static final RegistrySupplier<SpellbindingBlock> SPELLBINDING_TABLE = BLOCKS.register("spellbinding_table", SpellbindingBlock::new);
   public static final RegistrySupplier<WoodcutterBlock> WOODCUTTER = BLOCKS.register("woodcutter", WoodcutterBlock::new);
   public static final RegistrySupplier<ToolRackBlock> OAK_TOOL_RACK = BLOCKS.register("oak_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<ToolRackBlock> SPRUCE_TOOL_RACK = BLOCKS.register("spruce_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<ToolRackBlock> BIRCH_TOOL_RACK = BLOCKS.register("birch_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<ToolRackBlock> JUNGLE_TOOL_RACK = BLOCKS.register("jungle_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<ToolRackBlock> ACACIA_TOOL_RACK = BLOCKS.register("acacia_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<ToolRackBlock> DARK_OAK_TOOL_RACK = BLOCKS.register("dark_oak_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<ToolRackBlock> MANGROVE_TOOL_RACK = BLOCKS.register("mangrove_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<ToolRackBlock> CHERRY_TOOL_RACK = BLOCKS.register("cherry_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<ToolRackBlock> PALM_TOOL_RACK = BLOCKS.register("palm_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<ToolRackBlock> BAMBOO_TOOL_RACK = BLOCKS.register("bamboo_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<ToolRackBlock> CRIMSON_TOOL_RACK = BLOCKS.register("crimson_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<ToolRackBlock> WARPED_TOOL_RACK = BLOCKS.register("warped_tool_rack", ToolRackBlock::new);
   public static final RegistrySupplier<KilnBlock> KILN = BLOCKS.register("kiln", () -> new KilnBlock(KilnBlock.KilnType.NORMAL));
   public static final RegistrySupplier<KilnBlock> KILN_MITHRIL = BLOCKS.register("kiln_mithril", () -> new KilnBlock(KilnBlock.KilnType.MITHRIL));
   public static final RegistrySupplier<KilnBlock> KILN_ORICHALCUM = BLOCKS.register("kiln_orichalcum", () -> new KilnBlock(KilnBlock.KilnType.ORICHALCUM));
   public static final RegistrySupplier<ThatchBed> THATCH_BED = BLOCKS.register(
      "thatch_bed", () -> new ThatchBed(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.GRASS).noOcclusion())
   );
   public static final RegistrySupplier<DummyBlock> TRAINING_DUMMY = BLOCKS.register("training_dummy", DummyBlock::new);
   public static final RegistrySupplier<WarpPadBlock> STONE_WARP_PAD = BLOCKS.register(
      "stone_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.STONE_BRICKS).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> GRANITE_WARP_PAD = BLOCKS.register(
      "granite_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.POLISHED_GRANITE).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> DIORITE_WARP_PAD = BLOCKS.register(
      "diorite_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.POLISHED_DIORITE).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> ANDESITE_WARP_PAD = BLOCKS.register(
      "andesite_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.POLISHED_ANDESITE).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> CALCITE_WARP_PAD = BLOCKS.register(
      "calcite_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.CALCITE).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> TUFF_WARP_PAD = BLOCKS.register(
      "tuff_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.TUFF_BRICKS).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> DEEPSLATE_WARP_PAD = BLOCKS.register(
      "deepslate_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> BRICK_WARP_PAD = BLOCKS.register(
      "brick_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.BRICKS).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> SANDSTONE_WARP_PAD = BLOCKS.register(
      "sandstone_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.SANDSTONE).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> RED_SANDSTONE_WARP_PAD = BLOCKS.register(
      "red_sandstone_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.RED_SANDSTONE).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> SARASA_SANDSTONE_WARP_PAD = BLOCKS.register(
      "sarasa_sandstone_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy((BlockBehaviour)SARASA_SANDSTONE.get()).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> PACKED_MUD_WARP_PAD = BLOCKS.register(
      "packed_mud_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.PACKED_MUD).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> PRISMARINE_BRICK_WARP_PAD = BLOCKS.register(
      "prismarine_brick_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> NETHER_BRICK_WARP_PAD = BLOCKS.register(
      "nether_brick_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.NETHER_BRICKS).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> RED_NETHER_BRICK_WARP_PAD = BLOCKS.register(
      "red_nether_brick_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> BASALT_WARP_PAD = BLOCKS.register(
      "basalt_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.POLISHED_BASALT).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> BLACKSTONE_WARP_PAD = BLOCKS.register(
      "blackstone_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> QUARTZ_WARP_PAD = BLOCKS.register(
      "quartz_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.QUARTZ_BLOCK).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> END_STONE_WARP_PAD = BLOCKS.register(
      "end_stone_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.END_STONE_BRICKS).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> PURPUR_WARP_PAD = BLOCKS.register(
      "purpur_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy(Blocks.PURPUR_BLOCK).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> ROYAL_DWARVEN_WARP_PAD = BLOCKS.register(
      "royal_dwarven_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> LABYRINTH_BRICK_WARP_PAD = BLOCKS.register(
      "labyrinth_brick_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy((BlockBehaviour)LABYRINTH_BRICKS.get()).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> CREAM_LABYRINTH_BRICK_WARP_PAD = BLOCKS.register(
      "cream_labyrinth_brick_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy((BlockBehaviour)CREAM_LABYRINTH_BRICKS.get()).noOcclusion())
   );
   public static final RegistrySupplier<WarpPadBlock> DARK_LABYRINTH_BRICK_WARP_PAD = BLOCKS.register(
      "dark_labyrinth_brick_warp_pad", () -> new WarpPadBlock(Properties.ofFullCopy((BlockBehaviour)DARK_LABYRINTH_BRICKS.get()).noOcclusion())
   );
   public static final RegistrySupplier<HipokuteGrass> HIPOKUTE_GRASS = BLOCKS.register(
      "hipokute_grass",
      () -> new HipokuteGrass(
         Properties.of()
            .mapColor(MapColor.PLANT)
            .noCollission()
            .noOcclusion()
            .randomTicks()
            .lightLevel(blockState -> 5)
            .instabreak()
            .sound(SoundType.CROP)
            .pushReaction(PushReaction.DESTROY)
      )
   );
   public static final RegistrySupplier<Block> POTTED_HIPOKUTE_FLOWER = BLOCKS.register(
      "potted_hipokute_flower",
      () -> new HipokuteFlowerPotBlock(
         TensuraMaterialItems.HIPOKUTE_FLOWER, Properties.ofFullCopy(Blocks.POTTED_BIRCH_SAPLING).noOcclusion().lightLevel(blockState -> 7)
      )
   );
   public static final RegistrySupplier<BaffledilBlock> BAFFLEDIL = BLOCKS.register(
      "baffledil",
      () -> new BaffledilBlock(
         TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS),
         0.5F,
         Properties.of()
            .mapColor(MapColor.PLANT)
            .noCollission()
            .instabreak()
            .sound(SoundType.GRASS)
            .offsetType(OffsetType.XZ)
            .pushReaction(PushReaction.DESTROY)
      )
   );
   public static final RegistrySupplier<Block> POTTED_BAFFLEDIL = BLOCKS.register(
      "potted_baffledil", () -> new FlowerPotBlock((Block)BAFFLEDIL.get(), Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY))
   );
   public static final RegistrySupplier<Block> LOOSE_DIRT = BLOCKS.register(
      "loose_dirt", () -> new LooseBlock(Properties.ofFullCopy(Blocks.DIRT).noOcclusion().isViewBlocking(TensuraBlocks::always))
   );
   public static final RegistrySupplier<Block> LOOSE_GRAVEL = BLOCKS.register(
      "loose_gravel", () -> new LooseBlock(Properties.ofFullCopy(Blocks.GRAVEL).noOcclusion().isViewBlocking(TensuraBlocks::always))
   );
   public static final RegistrySupplier<Block> QUICKMUD = BLOCKS.register(
      "quickmud", () -> new LooseBlock(Properties.ofFullCopy(Blocks.MUD).noOcclusion().isViewBlocking(TensuraBlocks::always))
   );
   public static final RegistrySupplier<Block> QUICKSAND = BLOCKS.register(
      "quicksand", () -> new LooseBlock(Properties.ofFullCopy(Blocks.SAND).noOcclusion().isViewBlocking(TensuraBlocks::always))
   );
   public static final RegistrySupplier<Block> RED_QUICKSAND = BLOCKS.register(
      "red_quicksand", () -> new LooseBlock(Properties.ofFullCopy(Blocks.RED_SAND).noOcclusion().isViewBlocking(TensuraBlocks::always))
   );
   public static final RegistrySupplier<Block> SARASA_QUICKSAND = BLOCKS.register(
      "sarasa_quicksand", () -> new LooseBlock(Properties.ofFullCopy((BlockBehaviour)SARASA_SAND.get()).noOcclusion().isViewBlocking(TensuraBlocks::always))
   );
   public static final RegistrySupplier<Block> LIGHT_AIR = BLOCKS.register("light_air", LightAirBlock::new);
   public static final RegistrySupplier<Block> SOLID_SPACE = BLOCKS.register("solid_space", SolidSpaceBlock::new);
   public static final RegistrySupplier<OrcDisasterHead> ORC_DISASTER_HEAD = BLOCKS.register(
      "orc_disaster_head", () -> new OrcDisasterHead(Properties.of().noOcclusion().strength(1.0F))
   );

   public static void init() {
      BLOCKS.register();
      TensuraBlocks.Items.ITEMS.register();
   }

   private static boolean always(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return true;
   }

   public static class Items {
      private static final DeferredRegister<Item> ITEMS = DeferredRegister.create("tensura", Registries.ITEM);
      public static final RegistrySupplier<Item> PALM_SAPLING = simpleBlockItem(TensuraBlocks.PALM_SAPLING);
      public static final RegistrySupplier<Item> PALM_LEAVES = simpleBlockItem(TensuraBlocks.PALM_LEAVES);
      public static final RegistrySupplier<Item> PALM_LOG = simpleBlockItem(TensuraBlocks.PALM_LOG);
      public static final RegistrySupplier<Item> PALM_WOOD = simpleBlockItem(TensuraBlocks.PALM_WOOD);
      public static final RegistrySupplier<Item> STRIPPED_PALM_LOG = simpleBlockItem(TensuraBlocks.STRIPPED_PALM_LOG);
      public static final RegistrySupplier<Item> STRIPPED_PALM_WOOD = simpleBlockItem(TensuraBlocks.STRIPPED_PALM_WOOD);
      public static final RegistrySupplier<Item> PALM_PLANKS = simpleBlockItem(TensuraBlocks.PALM_PLANKS);
      public static final RegistrySupplier<Item> PALM_STAIRS = simpleBlockItem(TensuraBlocks.PALM_STAIRS);
      public static final RegistrySupplier<Item> PALM_SLAB = simpleBlockItem(TensuraBlocks.PALM_SLAB);
      public static final RegistrySupplier<Item> PALM_FENCE = simpleBlockItem(TensuraBlocks.PALM_FENCE);
      public static final RegistrySupplier<Item> PALM_FENCE_GATE = simpleBlockItem(TensuraBlocks.PALM_FENCE_GATE);
      public static final RegistrySupplier<Item> PALM_DOOR = simpleBlockItem(TensuraBlocks.PALM_DOOR);
      public static final RegistrySupplier<Item> PALM_TRAPDOOR = simpleBlockItem(TensuraBlocks.PALM_TRAPDOOR);
      public static final RegistrySupplier<Item> PALM_PRESSURE_PLATE = simpleBlockItem(TensuraBlocks.PALM_PRESSURE_PLATE);
      public static final RegistrySupplier<Item> PALM_BUTTON = simpleBlockItem(TensuraBlocks.PALM_BUTTON);
      public static final RegistrySupplier<SignItem> PALM_SIGN = ITEMS.register(
         "palm_sign",
         () -> new SignItem(
            new net.minecraft.world.item.Item.Properties().arch$tab(TensuraCreativeTabs.BLOCKS).stacksTo(16),
            (Block)TensuraBlocks.PALM_STANDING_SIGN.get(),
            (Block)TensuraBlocks.PALM_WALL_SIGN.get()
         )
      );
      public static final RegistrySupplier<SignItem> PALM_HANGING_SIGN = ITEMS.register(
         "palm_hanging_sign",
         () -> new HangingSignItem(
            (Block)TensuraBlocks.PALM_HANGING_SIGN.get(),
            (Block)TensuraBlocks.PALM_WALL_HANGING_SIGN.get(),
            new net.minecraft.world.item.Item.Properties().arch$tab(TensuraCreativeTabs.BLOCKS).stacksTo(16)
         )
      );
      public static final RegistrySupplier<Item> PALM_BOAT = ITEMS.register(
         "palm_boat",
         () -> new TensuraBoatItem(new net.minecraft.world.item.Item.Properties().arch$tab(TensuraCreativeTabs.BLOCKS).stacksTo(1), TensuraBoatEntity.Type.PALM)
      );
      public static final RegistrySupplier<Item> PALM_CHEST_BOAT = ITEMS.register(
         "palm_chest_boat",
         () -> new TensuraChestBoatItem(
            new net.minecraft.world.item.Item.Properties().arch$tab(TensuraCreativeTabs.BLOCKS).stacksTo(1), TensuraBoatEntity.Type.PALM
         )
      );
      public static final RegistrySupplier<Item> THATCH_BLOCK = simpleBlockItem(TensuraBlocks.THATCH_BLOCK);
      public static final RegistrySupplier<Item> THATCH_STAIRS = simpleBlockItem(TensuraBlocks.THATCH_STAIRS);
      public static final RegistrySupplier<Item> THATCH_SLAB = simpleBlockItem(TensuraBlocks.THATCH_SLAB);
      public static final RegistrySupplier<Item> THATCH_WALL = simpleBlockItem(TensuraBlocks.THATCH_WALL);
      public static final RegistrySupplier<Item> THATCH_BED = simpleBlockItem(TensuraBlocks.THATCH_BED);
      public static final RegistrySupplier<Item> TATAMI_BLOCK = simpleBlockItem(TensuraBlocks.TATAMI_BLOCK);
      public static final RegistrySupplier<Item> TATAMI_CARPET = simpleBlockItem(TensuraBlocks.TATAMI_CARPET);
      public static final RegistrySupplier<Item> SINGLE_TATAMI_BLOCK = simpleBlockItem(TensuraBlocks.SINGLE_TATAMI_BLOCK);
      public static final RegistrySupplier<Item> SINGLE_TATAMI_CARPET = simpleBlockItem(TensuraBlocks.SINGLE_TATAMI_CARPET);
      public static final RegistrySupplier<Item> SARASA_SAND = simpleBlockItem(TensuraBlocks.SARASA_SAND);
      public static final RegistrySupplier<Item> SARASA_SANDSTONE = simpleBlockItem(TensuraBlocks.SARASA_SANDSTONE);
      public static final RegistrySupplier<Item> CHISELED_SARASA_SANDSTONE = simpleBlockItem(TensuraBlocks.CHISELED_SARASA_SANDSTONE);
      public static final RegistrySupplier<Item> CUT_SARASA_SANDSTONE = simpleBlockItem(TensuraBlocks.CUT_SARASA_SANDSTONE);
      public static final RegistrySupplier<Item> SMOOTH_SARASA_SANDSTONE = simpleBlockItem(TensuraBlocks.SMOOTH_SARASA_SANDSTONE);
      public static final RegistrySupplier<Item> SARASA_SANDSTONE_STAIRS = simpleBlockItem(TensuraBlocks.SARASA_SANDSTONE_STAIRS);
      public static final RegistrySupplier<Item> SMOOTH_SARASA_SANDSTONE_STAIRS = simpleBlockItem(TensuraBlocks.SMOOTH_SARASA_SANDSTONE_STAIRS);
      public static final RegistrySupplier<Item> SARASA_SANDSTONE_SLAB = simpleBlockItem(TensuraBlocks.SARASA_SANDSTONE_SLAB);
      public static final RegistrySupplier<Item> CUT_SARASA_SANDSTONE_SLAB = simpleBlockItem(TensuraBlocks.CUT_SARASA_SANDSTONE_SLAB);
      public static final RegistrySupplier<Item> SMOOTH_SARASA_SANDSTONE_SLAB = simpleBlockItem(TensuraBlocks.SMOOTH_SARASA_SANDSTONE_SLAB);
      public static final RegistrySupplier<Item> SARASA_SANDSTONE_WALL = simpleBlockItem(TensuraBlocks.SARASA_SANDSTONE_WALL);
      public static final RegistrySupplier<Item> LOW_QUALITY_MAGIC_CRYSTAL_BLOCK = simpleBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK);
      public static final RegistrySupplier<Item> LOW_QUALITY_MAGIC_CRYSTAL_BRICKS = simpleBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS);
      public static final RegistrySupplier<Item> CHISELED_LOW_QUALITY_MAGIC_CRYSTAL_BRICKS = simpleBlockItem(
         TensuraBlocks.CHISELED_LOW_QUALITY_MAGIC_CRYSTAL_BRICKS
      );
      public static final RegistrySupplier<Item> LOW_QUALITY_MAGIC_CRYSTAL_STAIRS = simpleBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_STAIRS);
      public static final RegistrySupplier<Item> LOW_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS = simpleBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS);
      public static final RegistrySupplier<Item> LOW_QUALITY_MAGIC_CRYSTAL_SLAB = simpleBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_SLAB);
      public static final RegistrySupplier<Item> LOW_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB = simpleBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB);
      public static final RegistrySupplier<Item> LOW_QUALITY_MAGIC_CRYSTAL_BRICK_WALL = simpleBlockItem(TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_WALL);
      public static final RegistrySupplier<Item> MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK = simpleBlockItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK);
      public static final RegistrySupplier<Item> MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS = simpleBlockItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS);
      public static final RegistrySupplier<Item> CHISELED_MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS = simpleBlockItem(
         TensuraBlocks.CHISELED_MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS
      );
      public static final RegistrySupplier<Item> MEDIUM_QUALITY_MAGIC_CRYSTAL_STAIRS = simpleBlockItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_STAIRS);
      public static final RegistrySupplier<Item> MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS = simpleBlockItem(
         TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS
      );
      public static final RegistrySupplier<Item> MEDIUM_QUALITY_MAGIC_CRYSTAL_SLAB = simpleBlockItem(TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_SLAB);
      public static final RegistrySupplier<Item> MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB = simpleBlockItem(
         TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB
      );
      public static final RegistrySupplier<Item> MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_WALL = simpleBlockItem(
         TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_WALL
      );
      public static final RegistrySupplier<Item> HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK = simpleBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK);
      public static final RegistrySupplier<Item> HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS = simpleBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS);
      public static final RegistrySupplier<Item> CHISELED_HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS = simpleBlockItem(
         TensuraBlocks.CHISELED_HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS
      );
      public static final RegistrySupplier<Item> HIGH_QUALITY_MAGIC_CRYSTAL_STAIRS = simpleBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_STAIRS);
      public static final RegistrySupplier<Item> HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS = simpleBlockItem(
         TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS
      );
      public static final RegistrySupplier<Item> HIGH_QUALITY_MAGIC_CRYSTAL_SLAB = simpleBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_SLAB);
      public static final RegistrySupplier<Item> HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB = simpleBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB);
      public static final RegistrySupplier<Item> HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_WALL = simpleBlockItem(TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_WALL);
      public static final RegistrySupplier<Item> MAGIC_ORE = simpleBlockItem(TensuraBlocks.MAGIC_ORE);
      public static final RegistrySupplier<Item> SILVER_ORE = simpleBlockItem(TensuraBlocks.SILVER_ORE);
      public static final RegistrySupplier<Item> DEEPSLATE_MAGIC_ORE = simpleBlockItem(TensuraBlocks.DEEPSLATE_MAGIC_ORE);
      public static final RegistrySupplier<Item> DEEPSLATE_SILVER_ORE = simpleBlockItem(TensuraBlocks.DEEPSLATE_SILVER_ORE);
      public static final RegistrySupplier<Item> RAW_SILVER_BLOCK = simpleBlockItem(TensuraBlocks.RAW_SILVER_BLOCK);
      public static final RegistrySupplier<Item> SILVER_BLOCK = simpleBlockItem(TensuraBlocks.SILVER_BLOCK);
      public static final RegistrySupplier<Item> MAGIC_ORE_BLOCK = simpleBlockItem(TensuraBlocks.MAGIC_ORE_BLOCK);
      public static final RegistrySupplier<Item> LOW_MAGISTEEL_BLOCK = simpleBlockItem(TensuraBlocks.LOW_MAGISTEEL_BLOCK);
      public static final RegistrySupplier<Item> HIGH_MAGISTEEL_BLOCK = fireResistedBlockItem(TensuraBlocks.HIGH_MAGISTEEL_BLOCK);
      public static final RegistrySupplier<Item> PURE_MAGISTEEL_BLOCK = fireResistedBlockItem(TensuraBlocks.PURE_MAGISTEEL_BLOCK);
      public static final RegistrySupplier<Item> MITHRIL_BLOCK = fireResistedBlockItem(TensuraBlocks.MITHRIL_BLOCK);
      public static final RegistrySupplier<Item> ORICHALCUM_BLOCK = fireResistedBlockItem(TensuraBlocks.ORICHALCUM_BLOCK);
      public static final RegistrySupplier<Item> ADAMANTITE_BLOCK = fireResistedBlockItem(TensuraBlocks.ADAMANTITE_BLOCK);
      public static final RegistrySupplier<Item> HIHIIROKANE_BLOCK = fireResistedBlockItem(TensuraBlocks.HIHIIROKANE_BLOCK);
      public static final RegistrySupplier<Item> LOOSE_DIRT = dungeonBlockItem(TensuraBlocks.LOOSE_DIRT);
      public static final RegistrySupplier<Item> LOOSE_GRAVEL = dungeonBlockItem(TensuraBlocks.LOOSE_GRAVEL);
      public static final RegistrySupplier<Item> QUICKMUD = dungeonBlockItem(TensuraBlocks.QUICKMUD);
      public static final RegistrySupplier<Item> QUICKSAND = dungeonBlockItem(TensuraBlocks.QUICKSAND);
      public static final RegistrySupplier<Item> RED_QUICKSAND = dungeonBlockItem(TensuraBlocks.RED_QUICKSAND);
      public static final RegistrySupplier<Item> SARASA_QUICKSAND = dungeonBlockItem(TensuraBlocks.SARASA_QUICKSAND);
      public static final RegistrySupplier<Item> STICKY_COBWEB = dungeonBlockItem(TensuraBlocks.STICKY_COBWEB);
      public static final RegistrySupplier<Item> STICKY_STEEL_COBWEB = dungeonBlockItem(TensuraBlocks.STICKY_STEEL_COBWEB);
      public static final RegistrySupplier<Item> SPIDER_EGG = dungeonBlockItem(TensuraBlocks.SPIDER_EGG);
      public static final RegistrySupplier<Item> WEB_BLOCK = dungeonBlockItem(TensuraBlocks.WEB_BLOCK);
      public static final RegistrySupplier<Item> WEB_STAIRS = dungeonBlockItem(TensuraBlocks.WEB_STAIRS);
      public static final RegistrySupplier<Item> WEB_SLAB = dungeonBlockItem(TensuraBlocks.WEB_SLAB);
      public static final RegistrySupplier<Item> WEBBED_COBBLESTONE = dungeonBlockItem(TensuraBlocks.WEBBED_COBBLESTONE);
      public static final RegistrySupplier<Item> WEBBED_COBBLESTONE_STAIRS = dungeonBlockItem(TensuraBlocks.WEBBED_COBBLESTONE_STAIRS);
      public static final RegistrySupplier<Item> WEBBED_COBBLESTONE_SLAB = dungeonBlockItem(TensuraBlocks.WEBBED_COBBLESTONE_SLAB);
      public static final RegistrySupplier<Item> WEBBED_COBBLESTONE_WALL = dungeonBlockItem(TensuraBlocks.WEBBED_COBBLESTONE_WALL);
      public static final RegistrySupplier<Item> WEBBED_STONE_BRICKS = dungeonBlockItem(TensuraBlocks.WEBBED_STONE_BRICKS);
      public static final RegistrySupplier<Item> WEBBED_STONE_BRICK_STAIRS = dungeonBlockItem(TensuraBlocks.WEBBED_STONE_BRICK_STAIRS);
      public static final RegistrySupplier<Item> WEBBED_STONE_BRICK_SLAB = dungeonBlockItem(TensuraBlocks.WEBBED_STONE_BRICK_SLAB);
      public static final RegistrySupplier<Item> WEBBED_STONE_BRICK_WALL = dungeonBlockItem(TensuraBlocks.WEBBED_STONE_BRICK_WALL);
      public static final RegistrySupplier<Item> LABYRINTH_BRICK = labyrinthBlockItem(TensuraBlocks.LABYRINTH_BRICKS);
      public static final RegistrySupplier<Item> LABYRINTH_BRICK_STAIR = labyrinthBlockItem(TensuraBlocks.LABYRINTH_BRICK_STAIR);
      public static final RegistrySupplier<Item> LABYRINTH_BRICK_SLAB = labyrinthBlockItem(TensuraBlocks.LABYRINTH_BRICK_SLAB);
      public static final RegistrySupplier<Item> LABYRINTH_BRICK_TL = labyrinthBlockItem(TensuraBlocks.LABYRINTH_BRICK_TL);
      public static final RegistrySupplier<Item> LABYRINTH_BRICK_TR = labyrinthBlockItem(TensuraBlocks.LABYRINTH_BRICK_TR);
      public static final RegistrySupplier<Item> LABYRINTH_BRICK_BL = labyrinthBlockItem(TensuraBlocks.LABYRINTH_BRICK_BL);
      public static final RegistrySupplier<Item> LABYRINTH_BRICK_BR = labyrinthBlockItem(TensuraBlocks.LABYRINTH_BRICK_BR);
      public static final RegistrySupplier<Item> LABYRINTH_STONE = labyrinthBlockItem(TensuraBlocks.LABYRINTH_STONE);
      public static final RegistrySupplier<Item> LABYRINTH_STONE_STAIR = labyrinthBlockItem(TensuraBlocks.LABYRINTH_STONE_STAIR);
      public static final RegistrySupplier<Item> LABYRINTH_STONE_SLAB = labyrinthBlockItem(TensuraBlocks.LABYRINTH_STONE_SLAB);
      public static final RegistrySupplier<Item> LABYRINTH_STONE_TL = labyrinthBlockItem(TensuraBlocks.LABYRINTH_STONE_TL);
      public static final RegistrySupplier<Item> LABYRINTH_STONE_TR = labyrinthBlockItem(TensuraBlocks.LABYRINTH_STONE_TR);
      public static final RegistrySupplier<Item> LABYRINTH_STONE_BL = labyrinthBlockItem(TensuraBlocks.LABYRINTH_STONE_BL);
      public static final RegistrySupplier<Item> LABYRINTH_STONE_BR = labyrinthBlockItem(TensuraBlocks.LABYRINTH_STONE_BR);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_BRICK = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICKS);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_BRICK_STAIR = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_STAIR);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_BRICK_SLAB = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_SLAB);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_BRICK_TL = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_TL);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_BRICK_TR = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_TR);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_BRICK_BL = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_BL);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_BRICK_BR = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_BRICK_BR);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_STONE = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_STONE_STAIR = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_STAIR);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_STONE_SLAB = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_SLAB);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_STONE_TL = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_TL);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_STONE_TR = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_TR);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_STONE_BL = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_BL);
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_STONE_BR = labyrinthBlockItem(TensuraBlocks.CREAM_LABYRINTH_STONE_BR);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_BRICK = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICKS);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_BRICK_STAIR = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_STAIR);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_BRICK_SLAB = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_SLAB);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_BRICK_TL = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_TL);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_BRICK_TR = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_TR);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_BRICK_BL = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_BL);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_BRICK_BR = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_BRICK_BR);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_STONE = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_STONE_STAIR = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_STAIR);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_STONE_SLAB = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_SLAB);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_STONE_TL = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_TL);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_STONE_TR = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_TR);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_STONE_BL = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_BL);
      public static final RegistrySupplier<Item> DARK_LABYRINTH_STONE_BR = labyrinthBlockItem(TensuraBlocks.DARK_LABYRINTH_STONE_BR);
      public static final RegistrySupplier<Item> LABYRINTH_LAMP = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LAMP);
      public static final RegistrySupplier<Item> LABYRINTH_LAMP_TL = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LAMP_TL);
      public static final RegistrySupplier<Item> LABYRINTH_LAMP_TR = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LAMP_TR);
      public static final RegistrySupplier<Item> LABYRINTH_LAMP_BL = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LAMP_BL);
      public static final RegistrySupplier<Item> LABYRINTH_LAMP_BR = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LAMP_BR);
      public static final RegistrySupplier<Item> LABYRINTH_LIT_LAMP = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LIT_LAMP);
      public static final RegistrySupplier<Item> LABYRINTH_LIT_LAMP_TL = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LIT_LAMP_TL);
      public static final RegistrySupplier<Item> LABYRINTH_LIT_LAMP_TR = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LIT_LAMP_TR);
      public static final RegistrySupplier<Item> LABYRINTH_LIT_LAMP_BL = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LIT_LAMP_BL);
      public static final RegistrySupplier<Item> LABYRINTH_LIT_LAMP_BR = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LIT_LAMP_BR);
      public static final RegistrySupplier<Item> LABYRINTH_CRYSTAL = labyrinthBlockItem(TensuraBlocks.LABYRINTH_CRYSTAL);
      public static final RegistrySupplier<Item> LABYRINTH_PRAYING_PATH = labyrinthBlockItem(TensuraBlocks.LABYRINTH_PRAYING_PATH);
      public static final RegistrySupplier<Item> LABYRINTH_LIGHT_PATH = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LIGHT_PATH);
      public static final RegistrySupplier<Item> LABYRINTH_LIGHT_PATH_STAIRS = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LIGHT_PATH_STAIRS);
      public static final RegistrySupplier<Item> LABYRINTH_LIGHT_PATH_SLAB = labyrinthBlockItem(TensuraBlocks.LABYRINTH_LIGHT_PATH_SLAB);
      public static final RegistrySupplier<Item> LABYRINTH_PORTAL = labyrinthBlockItem(TensuraBlocks.LABYRINTH_PORTAL);
      public static final RegistrySupplier<Item> LABYRINTH_BARRIER_BLOCK = labyrinthBlockItem(TensuraBlocks.LABYRINTH_BARRIER_BLOCK);
      public static final RegistrySupplier<Item> HELL_PORTAL = labyrinthBlockItem(TensuraBlocks.HELL_PORTAL);
      public static final RegistrySupplier<Item> TRAINING_DUMMY = simpleBlockItem(TensuraBlocks.TRAINING_DUMMY, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> WOODCUTTER = simpleBlockItem(TensuraBlocks.WOODCUTTER, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> SMITHING_BENCH = simpleBlockItem(TensuraBlocks.SMITHING_BENCH, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> MINING_STATION = simpleBlockItem(TensuraBlocks.MINING_STATION, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> SPELLBINDING_TABLE = simpleBlockItem(TensuraBlocks.SPELLBINDING_TABLE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> KILN = fireResistedBlockItem(TensuraBlocks.KILN, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> KILN_MITHRIL = fireResistedBlockItem(TensuraBlocks.KILN_MITHRIL, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> KILN_ORICHALCUM = fireResistedBlockItem(TensuraBlocks.KILN_ORICHALCUM, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> OAK_TOOL_RACK = simpleBlockItem(TensuraBlocks.OAK_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> SPRUCE_TOOL_RACK = simpleBlockItem(TensuraBlocks.SPRUCE_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> BIRCH_TOOL_RACK = simpleBlockItem(TensuraBlocks.BIRCH_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> JUNGLE_TOOL_RACK = simpleBlockItem(TensuraBlocks.JUNGLE_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> ACACIA_TOOL_RACK = simpleBlockItem(TensuraBlocks.ACACIA_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> DARK_OAK_TOOL_RACK = simpleBlockItem(TensuraBlocks.DARK_OAK_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> MANGROVE_TOOL_RACK = simpleBlockItem(TensuraBlocks.MANGROVE_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> CHERRY_TOOL_RACK = simpleBlockItem(TensuraBlocks.CHERRY_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> PALM_TOOL_RACK = simpleBlockItem(TensuraBlocks.PALM_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> BAMBOO_TOOL_RACK = simpleBlockItem(TensuraBlocks.BAMBOO_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> CRIMSON_TOOL_RACK = simpleBlockItem(TensuraBlocks.CRIMSON_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> WARPED_TOOL_RACK = simpleBlockItem(TensuraBlocks.WARPED_TOOL_RACK, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> BRICKS_MAGIC_ENGINE = simpleBlockItem(TensuraBlocks.BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> STONE_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.STONE_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> TUFF_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.TUFF_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> DEEPSLATE_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.DEEPSLATE_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> MUD_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.MUD_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> PRISMARINE_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.PRISMARINE_BRICK_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> RED_NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.RED_NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> POLISHED_BLACKSTONE_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.POLISHED_BLACKSTONE_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> QUARTZ_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.QUARTZ_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> END_STONE_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.END_STONE_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> PURPUR_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.PURPUR_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> LOW_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE = simpleBlockItem(
         TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> LABYRINTH_BRICKS_MAGIC_ENGINE = fireResistedBlockItem(
         TensuraBlocks.LABYRINTH_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_BRICKS_MAGIC_ENGINE = fireResistedBlockItem(
         TensuraBlocks.CREAM_LABYRINTH_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> DARK_LABYRINTH_BRICKS_MAGIC_ENGINE = fireResistedBlockItem(
         TensuraBlocks.DARK_LABYRINTH_BRICKS_MAGIC_ENGINE, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> STONE_WARP_PAD = fireResistedBlockItem(TensuraBlocks.STONE_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> GRANITE_WARP_PAD = fireResistedBlockItem(TensuraBlocks.GRANITE_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> DIORITE_WARP_PAD = fireResistedBlockItem(TensuraBlocks.DIORITE_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> ANDESITE_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.ANDESITE_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> CALCITE_WARP_PAD = fireResistedBlockItem(TensuraBlocks.CALCITE_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> TUFF_WARP_PAD = fireResistedBlockItem(TensuraBlocks.TUFF_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> DEEPSLATE_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.DEEPSLATE_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> BRICK_WARP_PAD = fireResistedBlockItem(TensuraBlocks.BRICK_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> SANDSTONE_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.SANDSTONE_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> RED_SANDSTONE_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.RED_SANDSTONE_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> SARASA_SANDSTONE_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.SARASA_SANDSTONE_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> PACKED_MUD_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.PACKED_MUD_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> PRISMARINE_BRICK_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.PRISMARINE_BRICK_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> NETHER_BRICK_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.NETHER_BRICK_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> RED_NETHER_BRICK_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.RED_NETHER_BRICK_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> BASALT_WARP_PAD = fireResistedBlockItem(TensuraBlocks.BASALT_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> BLACKSTONE_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.BLACKSTONE_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> QUARTZ_WARP_PAD = fireResistedBlockItem(TensuraBlocks.QUARTZ_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> END_STONE_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.END_STONE_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> PURPUR_WARP_PAD = fireResistedBlockItem(TensuraBlocks.PURPUR_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS);
      public static final RegistrySupplier<Item> LABYRINTH_BRICKS_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.LABYRINTH_BRICK_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> CREAM_LABYRINTH_BRICKS_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.CREAM_LABYRINTH_BRICK_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> DARK_LABYRINTH_BRICKS_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.DARK_LABYRINTH_BRICK_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> ROYAL_DWARVEN_WARP_PAD = fireResistedBlockItem(
         TensuraBlocks.ROYAL_DWARVEN_WARP_PAD, TensuraCreativeTabs.FUNCTIONAL_BLOCKS
      );
      public static final RegistrySupplier<Item> SLIME_CHUNK_BLOCK = simpleBlockItem(TensuraBlocks.SLIME_CHUNK_BLOCK);
      public static final RegistrySupplier<Item> CHILLED_SLIME_BLOCK = simpleBlockItem(TensuraBlocks.CHILLED_SLIME_BLOCK);

      public static <T extends Block> RegistrySupplier<Item> simpleBlockItem(RegistrySupplier<T> block) {
         return ITEMS.register(block.getId().getPath(), () -> new SimpleBlockItem((Block)block.get()));
      }

      public static <T extends Block> RegistrySupplier<Item> simpleBlockItem(RegistrySupplier<T> block, CreativeModeTab tab) {
         return ITEMS.register(block.getId().getPath(), () -> new SimpleBlockItem((Block)block.get(), tab));
      }

      public static <T extends Block> RegistrySupplier<Item> simpleBlockItem(RegistrySupplier<T> block, DeferredSupplier<CreativeModeTab> tab) {
         return ITEMS.register(block.getId().getPath(), () -> new SimpleBlockItem((Block)block.get(), tab));
      }

      public static <T extends Block> RegistrySupplier<Item> dungeonBlockItem(RegistrySupplier<T> block) {
         return ITEMS.register(
            block.getId().getPath(),
            () -> new SimpleBlockItem((Block)block.get(), new net.minecraft.world.item.Item.Properties().arch$tab(TensuraCreativeTabs.DUNGEON_BLOCKS))
         );
      }

      public static <T extends Block> RegistrySupplier<Item> labyrinthBlockItem(RegistrySupplier<T> block) {
         return fireResistedBlockItem(block, TensuraCreativeTabs.DUNGEON_BLOCKS);
      }

      public static <T extends Block> RegistrySupplier<Item> fireResistedBlockItem(RegistrySupplier<T> block) {
         return fireResistedBlockItem(block, TensuraCreativeTabs.BLOCKS);
      }

      public static <T extends Block> RegistrySupplier<Item> fireResistedBlockItem(RegistrySupplier<T> block, CreativeModeTab tab) {
         return ITEMS.register(
            block.getId().getPath(),
            () -> new SimpleBlockItem((Block)block.get(), new net.minecraft.world.item.Item.Properties().arch$tab(tab).fireResistant())
         );
      }

      public static <T extends Block> RegistrySupplier<Item> fireResistedBlockItem(RegistrySupplier<T> block, DeferredSupplier<CreativeModeTab> tab) {
         return ITEMS.register(
            block.getId().getPath(),
            () -> new SimpleBlockItem((Block)block.get(), new net.minecraft.world.item.Item.Properties().arch$tab(tab).fireResistant())
         );
      }
   }
}
