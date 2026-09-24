package io.github.manasmods.tensura.neoforge.data.model;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.block.HipokuteGrass;
import io.github.manasmods.tensura.block.KilnBlock;
import io.github.manasmods.tensura.block.MagicEngineBlock;
import io.github.manasmods.tensura.block.WarpPadBlock;
import io.github.manasmods.tensura.block.part.KilnPart;
import io.github.manasmods.tensura.block.part.WarpPadPart;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel.Builder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TensuraBlockStateProvider extends BlockStateProvider {
   public TensuraBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
      super(output, "tensura", exFileHelper);
   }

   protected void registerStatesAndModels() {
      this.cross((Block)TensuraBlocks.PALM_SAPLING.get());
      this.simpleBlockWithRenderType((Block)TensuraBlocks.PALM_LEAVES.get(), RenderType.TRANSLUCENT.name);
      this.logBlock((RotatedPillarBlock)TensuraBlocks.PALM_LOG.get());
      this.logBlock((RotatedPillarBlock)TensuraBlocks.STRIPPED_PALM_LOG.get());
      this.logBlock((RotatedPillarBlock)TensuraBlocks.PALM_WOOD.get());
      this.logBlock((RotatedPillarBlock)TensuraBlocks.STRIPPED_PALM_WOOD.get());
      this.simpleBlock((Block)TensuraBlocks.PALM_PLANKS.get());
      this.stairs((StairBlock)TensuraBlocks.PALM_STAIRS.get(), TensuraBlocks.PALM_PLANKS);
      this.slab((SlabBlock)TensuraBlocks.PALM_SLAB.get(), TensuraBlocks.PALM_PLANKS);
      this.buttonBlock((ButtonBlock)TensuraBlocks.PALM_BUTTON.get(), this.blockTexture((Block)TensuraBlocks.PALM_PLANKS.get()));
      this.pressurePlateBlock((PressurePlateBlock)TensuraBlocks.PALM_PRESSURE_PLATE.get(), this.blockTexture((Block)TensuraBlocks.PALM_PLANKS.get()));
      this.fenceBlock((FenceBlock)TensuraBlocks.PALM_FENCE.get(), this.blockTexture((Block)TensuraBlocks.PALM_PLANKS.get()));
      this.fenceGateBlock((FenceGateBlock)TensuraBlocks.PALM_FENCE_GATE.get(), this.blockTexture((Block)TensuraBlocks.PALM_PLANKS.get()));
      this.doorBlockWithRenderType(
         (DoorBlock)TensuraBlocks.PALM_DOOR.get(), this.modLoc("block/palm_door_bottom"), this.modLoc("block/palm_door_top"), RenderType.TRANSLUCENT.name
      );
      this.trapdoorBlockWithRenderType((TrapDoorBlock)TensuraBlocks.PALM_TRAPDOOR.get(), this.modLoc("block/palm_trapdoor"), true, RenderType.TRANSLUCENT.name);
      this.signBlock(
         (StandingSignBlock)TensuraBlocks.PALM_STANDING_SIGN.get(), (WallSignBlock)TensuraBlocks.PALM_WALL_SIGN.get(), this.modLoc("block/palm_planks")
      );
      this.hangingSignBlock(
         (CeilingHangingSignBlock)TensuraBlocks.PALM_HANGING_SIGN.get(),
         (WallHangingSignBlock)TensuraBlocks.PALM_WALL_HANGING_SIGN.get(),
         this.modLoc("block/palm_planks")
      );
      this.logBlock((RotatedPillarBlock)TensuraBlocks.THATCH_BLOCK.get());
      this.stairsPillar((StairBlock)TensuraBlocks.THATCH_STAIRS.get(), TensuraBlocks.THATCH_BLOCK);
      this.slabPillar((SlabBlock)TensuraBlocks.THATCH_SLAB.get(), TensuraBlocks.THATCH_BLOCK);
      this.wallBlock((WallBlock)TensuraBlocks.THATCH_WALL.get(), this.modLoc("block/thatch_block"));
      this.directionalBlock((Block)TensuraBlocks.TATAMI_BLOCK.get(), this.cubeAll((Block)TensuraBlocks.TATAMI_BLOCK.get()));
      Function<BlockState, ModelFile> modelFunc = state -> this.models()
         .carpet(this.name((Block)TensuraBlocks.TATAMI_CARPET.get()), this.blockTexture((Block)TensuraBlocks.TATAMI_BLOCK.get()));
      this.getVariantBuilder((Block)TensuraBlocks.TATAMI_CARPET.get())
         .forAllStates(
            state -> ConfiguredModel.builder()
               .modelFile(modelFunc.apply(state))
               .rotationY(((int)((Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING)).toYRot() + 180) % 360)
               .build()
         );
      this.simpleBlock((Block)TensuraBlocks.SINGLE_TATAMI_BLOCK.get());
      this.simpleBlock(
         (Block)TensuraBlocks.SINGLE_TATAMI_CARPET.get(),
         this.models().carpet(this.name((Block)TensuraBlocks.SINGLE_TATAMI_CARPET.get()), this.blockTexture((Block)TensuraBlocks.SINGLE_TATAMI_BLOCK.get()))
      );
      this.simpleBlock((Block)TensuraBlocks.MAGIC_ORE.get());
      this.simpleBlock((Block)TensuraBlocks.DEEPSLATE_MAGIC_ORE.get());
      this.simpleBlock((Block)TensuraBlocks.SILVER_ORE.get());
      this.simpleBlock((Block)TensuraBlocks.DEEPSLATE_SILVER_ORE.get());
      this.simpleBlock((Block)TensuraBlocks.RAW_SILVER_BLOCK.get());
      this.simpleBlock((Block)TensuraBlocks.SILVER_BLOCK.get());
      this.simpleBlock((Block)TensuraBlocks.MAGIC_ORE_BLOCK.get());
      this.simpleBlock((Block)TensuraBlocks.LOW_MAGISTEEL_BLOCK.get());
      this.simpleBlock((Block)TensuraBlocks.HIGH_MAGISTEEL_BLOCK.get());
      this.simpleBlock((Block)TensuraBlocks.PURE_MAGISTEEL_BLOCK.get());
      this.simpleBlock((Block)TensuraBlocks.MITHRIL_BLOCK.get());
      this.simpleBlock((Block)TensuraBlocks.ORICHALCUM_BLOCK.get());
      this.simpleBlock((Block)TensuraBlocks.ADAMANTITE_BLOCK.get());
      this.simpleBlock((Block)TensuraBlocks.HIHIIROKANE_BLOCK.get());
      this.simpleBlock((Block)TensuraBlocks.LABYRINTH_BRICKS.get());
      this.stairs((StairBlock)TensuraBlocks.LABYRINTH_BRICK_STAIR.get(), TensuraBlocks.LABYRINTH_BRICKS);
      this.slab((SlabBlock)TensuraBlocks.LABYRINTH_BRICK_SLAB.get(), TensuraBlocks.LABYRINTH_BRICKS);
      this.simpleBlock((Block)TensuraBlocks.LABYRINTH_BRICK_TL.get());
      this.simpleBlock((Block)TensuraBlocks.LABYRINTH_BRICK_TR.get());
      this.simpleBlock((Block)TensuraBlocks.LABYRINTH_BRICK_BL.get());
      this.simpleBlock((Block)TensuraBlocks.LABYRINTH_BRICK_BR.get());
      this.simpleBlock((Block)TensuraBlocks.LABYRINTH_STONE.get());
      this.stairs((StairBlock)TensuraBlocks.LABYRINTH_STONE_STAIR.get(), TensuraBlocks.LABYRINTH_STONE);
      this.slab((SlabBlock)TensuraBlocks.LABYRINTH_STONE_SLAB.get(), TensuraBlocks.LABYRINTH_STONE);
      this.simpleBlock((Block)TensuraBlocks.LABYRINTH_STONE_TL.get());
      this.simpleBlock((Block)TensuraBlocks.LABYRINTH_STONE_TR.get());
      this.simpleBlock((Block)TensuraBlocks.LABYRINTH_STONE_BL.get());
      this.simpleBlock((Block)TensuraBlocks.LABYRINTH_STONE_BR.get());
      this.simpleBlock((Block)TensuraBlocks.CREAM_LABYRINTH_BRICKS.get());
      this.stairs((StairBlock)TensuraBlocks.CREAM_LABYRINTH_BRICK_STAIR.get(), TensuraBlocks.CREAM_LABYRINTH_BRICKS);
      this.slab((SlabBlock)TensuraBlocks.CREAM_LABYRINTH_BRICK_SLAB.get(), TensuraBlocks.CREAM_LABYRINTH_BRICKS);
      this.simpleBlock((Block)TensuraBlocks.CREAM_LABYRINTH_BRICK_TL.get());
      this.simpleBlock((Block)TensuraBlocks.CREAM_LABYRINTH_BRICK_TR.get());
      this.simpleBlock((Block)TensuraBlocks.CREAM_LABYRINTH_BRICK_BL.get());
      this.simpleBlock((Block)TensuraBlocks.CREAM_LABYRINTH_BRICK_BR.get());
      this.simpleBlock((Block)TensuraBlocks.CREAM_LABYRINTH_STONE.get());
      this.stairs((StairBlock)TensuraBlocks.CREAM_LABYRINTH_STONE_STAIR.get(), TensuraBlocks.CREAM_LABYRINTH_STONE);
      this.slab((SlabBlock)TensuraBlocks.CREAM_LABYRINTH_STONE_SLAB.get(), TensuraBlocks.CREAM_LABYRINTH_STONE);
      this.simpleBlock((Block)TensuraBlocks.CREAM_LABYRINTH_STONE_TL.get());
      this.simpleBlock((Block)TensuraBlocks.CREAM_LABYRINTH_STONE_TR.get());
      this.simpleBlock((Block)TensuraBlocks.CREAM_LABYRINTH_STONE_BL.get());
      this.simpleBlock((Block)TensuraBlocks.CREAM_LABYRINTH_STONE_BR.get());
      this.simpleBlock((Block)TensuraBlocks.DARK_LABYRINTH_BRICKS.get());
      this.stairs((StairBlock)TensuraBlocks.DARK_LABYRINTH_BRICK_STAIR.get(), TensuraBlocks.DARK_LABYRINTH_BRICKS);
      this.slab((SlabBlock)TensuraBlocks.DARK_LABYRINTH_BRICK_SLAB.get(), TensuraBlocks.DARK_LABYRINTH_BRICKS);
      this.simpleBlock((Block)TensuraBlocks.DARK_LABYRINTH_BRICK_TL.get());
      this.simpleBlock((Block)TensuraBlocks.DARK_LABYRINTH_BRICK_TR.get());
      this.simpleBlock((Block)TensuraBlocks.DARK_LABYRINTH_BRICK_BL.get());
      this.simpleBlock((Block)TensuraBlocks.DARK_LABYRINTH_BRICK_BR.get());
      this.simpleBlock((Block)TensuraBlocks.DARK_LABYRINTH_STONE.get());
      this.stairs((StairBlock)TensuraBlocks.DARK_LABYRINTH_STONE_STAIR.get(), TensuraBlocks.DARK_LABYRINTH_STONE);
      this.slab((SlabBlock)TensuraBlocks.DARK_LABYRINTH_STONE_SLAB.get(), TensuraBlocks.DARK_LABYRINTH_STONE);
      this.simpleBlock((Block)TensuraBlocks.DARK_LABYRINTH_STONE_TL.get());
      this.simpleBlock((Block)TensuraBlocks.DARK_LABYRINTH_STONE_TR.get());
      this.simpleBlock((Block)TensuraBlocks.DARK_LABYRINTH_STONE_BL.get());
      this.simpleBlock((Block)TensuraBlocks.DARK_LABYRINTH_STONE_BR.get());
      this.directionalBlock(
         (Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get(), this.cubeAll((Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get())
      );
      this.stairs((StairBlock)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_STAIRS.get(), TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.slab((SlabBlock)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_SLAB.get(), TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.simpleBlock((Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.stairs((StairBlock)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS.get(), TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.slab((SlabBlock)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(), TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.simpleBlock((Block)TensuraBlocks.CHISELED_LOW_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.wallBlock((WallBlock)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get(), this.modLoc("block/low_quality_magic_crystal_bricks"));
      this.directionalBlock(
         (Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get(), this.cubeAll((Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get())
      );
      this.stairs((StairBlock)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_STAIRS.get(), TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.slab((SlabBlock)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_SLAB.get(), TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.simpleBlock((Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.stairs((StairBlock)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS.get(), TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.slab((SlabBlock)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(), TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.simpleBlock((Block)TensuraBlocks.CHISELED_MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.wallBlock((WallBlock)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get(), this.modLoc("block/medium_quality_magic_crystal_bricks"));
      this.directionalBlock(
         (Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get(), this.cubeAll((Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get())
      );
      this.stairs((StairBlock)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_STAIRS.get(), TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.slab((SlabBlock)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_SLAB.get(), TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK);
      this.simpleBlock((Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.stairs((StairBlock)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_STAIRS.get(), TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.slab((SlabBlock)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_SLAB.get(), TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS);
      this.simpleBlock((Block)TensuraBlocks.CHISELED_HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS.get());
      this.wallBlock((WallBlock)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICK_WALL.get(), this.modLoc("block/high_quality_magic_crystal_bricks"));
      this.simpleBlock((Block)TensuraBlocks.SARASA_SAND.get());
      this.stairsBlock(
         (StairBlock)TensuraBlocks.SARASA_SANDSTONE_STAIRS.get(),
         this.modLoc("block/sarasa_sandstone"),
         this.modLoc("block/sarasa_sandstone_bottom"),
         this.modLoc("block/smooth_sarasa_sandstone")
      );
      this.slabBlock(
         (SlabBlock)TensuraBlocks.SARASA_SANDSTONE_SLAB.get(),
         this.modLoc("block/sarasa_sandstone"),
         this.modLoc("block/sarasa_sandstone"),
         this.modLoc("block/sarasa_sandstone_bottom"),
         this.modLoc("block/smooth_sarasa_sandstone")
      );
      this.wallBlock((WallBlock)TensuraBlocks.SARASA_SANDSTONE_WALL.get(), this.modLoc("block/sarasa_sandstone"));
      this.slabBlock(
         (SlabBlock)TensuraBlocks.CUT_SARASA_SANDSTONE_SLAB.get(),
         this.modLoc("block/cut_sarasa_sandstone"),
         this.modLoc("block/cut_sarasa_sandstone"),
         this.modLoc("block/sarasa_sandstone_bottom"),
         this.modLoc("block/smooth_sarasa_sandstone")
      );
      this.simpleBlock((Block)TensuraBlocks.SMOOTH_SARASA_SANDSTONE.get());
      this.stairs((StairBlock)TensuraBlocks.SMOOTH_SARASA_SANDSTONE_STAIRS.get(), TensuraBlocks.SMOOTH_SARASA_SANDSTONE);
      this.slab((SlabBlock)TensuraBlocks.SMOOTH_SARASA_SANDSTONE_SLAB.get(), TensuraBlocks.SMOOTH_SARASA_SANDSTONE);
      this.cross((Block)TensuraBlocks.STICKY_COBWEB.get());
      this.cross((Block)TensuraBlocks.STICKY_STEEL_COBWEB.get());
      this.cross((Block)TensuraBlocks.BAFFLEDIL.get());
      this.cropWithAge((Block)TensuraBlocks.HIPOKUTE_GRASS.get(), HipokuteGrass.AGE);
      this.simpleBlock((Block)TensuraBlocks.LOOSE_DIRT.get());
      this.simpleBlock((Block)TensuraBlocks.LOOSE_GRAVEL.get());
      this.simpleBlock((Block)TensuraBlocks.QUICKMUD.get());
      this.simpleBlock((Block)TensuraBlocks.QUICKSAND.get());
      this.simpleBlock((Block)TensuraBlocks.RED_QUICKSAND.get());
      this.simpleBlock((Block)TensuraBlocks.SARASA_QUICKSAND.get());
      this.kiln((Block)TensuraBlocks.KILN_MITHRIL.get(), ResourceLocation.withDefaultNamespace("block/obsidian"));
      this.kiln((Block)TensuraBlocks.KILN_ORICHALCUM.get(), ResourceLocation.withDefaultNamespace("block/obsidian"));
      this.toolRack((Block)TensuraBlocks.OAK_TOOL_RACK.get(), ResourceLocation.withDefaultNamespace("block/stripped_oak_log"));
      this.toolRack((Block)TensuraBlocks.SPRUCE_TOOL_RACK.get(), ResourceLocation.withDefaultNamespace("block/stripped_spruce_log"));
      this.toolRack((Block)TensuraBlocks.BIRCH_TOOL_RACK.get(), ResourceLocation.withDefaultNamespace("block/stripped_birch_log"));
      this.toolRack((Block)TensuraBlocks.JUNGLE_TOOL_RACK.get(), ResourceLocation.withDefaultNamespace("block/stripped_jungle_log"));
      this.toolRack((Block)TensuraBlocks.ACACIA_TOOL_RACK.get(), ResourceLocation.withDefaultNamespace("block/stripped_acacia_log"));
      this.toolRack((Block)TensuraBlocks.DARK_OAK_TOOL_RACK.get(), ResourceLocation.withDefaultNamespace("block/stripped_dark_oak_log"));
      this.toolRack((Block)TensuraBlocks.MANGROVE_TOOL_RACK.get(), ResourceLocation.withDefaultNamespace("block/stripped_mangrove_log"));
      this.toolRack((Block)TensuraBlocks.CHERRY_TOOL_RACK.get(), ResourceLocation.withDefaultNamespace("block/stripped_cherry_log"));
      this.toolRack((Block)TensuraBlocks.PALM_TOOL_RACK.get(), ResourceLocation.fromNamespaceAndPath("tensura", "block/stripped_palm_log"));
      this.toolRack((Block)TensuraBlocks.BAMBOO_TOOL_RACK.get(), ResourceLocation.withDefaultNamespace("block/stripped_bamboo_block"));
      this.toolRack((Block)TensuraBlocks.CRIMSON_TOOL_RACK.get(), ResourceLocation.withDefaultNamespace("block/stripped_crimson_stem"));
      this.toolRack((Block)TensuraBlocks.WARPED_TOOL_RACK.get(), ResourceLocation.withDefaultNamespace("block/stripped_warped_stem"));
      this.warpPad((Block)TensuraBlocks.STONE_WARP_PAD.get(), "stone_warp_pad");
      this.warpPad((Block)TensuraBlocks.GRANITE_WARP_PAD.get(), "granite_warp_pad");
      this.warpPad((Block)TensuraBlocks.DIORITE_WARP_PAD.get(), "diorite_warp_pad");
      this.warpPad((Block)TensuraBlocks.ANDESITE_WARP_PAD.get(), "andesite_warp_pad");
      this.warpPad((Block)TensuraBlocks.CALCITE_WARP_PAD.get(), "calcite_warp_pad");
      this.warpPad((Block)TensuraBlocks.TUFF_WARP_PAD.get(), "tuff_warp_pad");
      this.warpPad((Block)TensuraBlocks.DEEPSLATE_WARP_PAD.get(), "deepslate_warp_pad");
      this.warpPad((Block)TensuraBlocks.BRICK_WARP_PAD.get(), "brick_warp_pad");
      this.warpPad((Block)TensuraBlocks.SANDSTONE_WARP_PAD.get(), "sandstone_warp_pad");
      this.warpPad((Block)TensuraBlocks.RED_SANDSTONE_WARP_PAD.get(), "red_sandstone_warp_pad");
      this.warpPad((Block)TensuraBlocks.SARASA_SANDSTONE_WARP_PAD.get(), "sarasa_sandstone_warp_pad");
      this.warpPad((Block)TensuraBlocks.PACKED_MUD_WARP_PAD.get(), "packed_mud_warp_pad");
      this.warpPad((Block)TensuraBlocks.PRISMARINE_BRICK_WARP_PAD.get(), "prismarine_brick_warp_pad");
      this.warpPad((Block)TensuraBlocks.NETHER_BRICK_WARP_PAD.get(), "nether_brick_warp_pad");
      this.warpPad((Block)TensuraBlocks.RED_NETHER_BRICK_WARP_PAD.get(), "red_nether_brick_warp_pad");
      this.warpPad((Block)TensuraBlocks.BLACKSTONE_WARP_PAD.get(), "blackstone_warp_pad");
      this.warpPad((Block)TensuraBlocks.BASALT_WARP_PAD.get(), "basalt_warp_pad");
      this.warpPad((Block)TensuraBlocks.QUARTZ_WARP_PAD.get(), "quartz_warp_pad");
      this.warpPad((Block)TensuraBlocks.END_STONE_WARP_PAD.get(), "end_stone_warp_pad");
      this.warpPad((Block)TensuraBlocks.PURPUR_WARP_PAD.get(), "purpur_warp_pad");
      this.warpPad((Block)TensuraBlocks.ROYAL_DWARVEN_WARP_PAD.get(), "royal_dwarven_warp_pad");
      this.warpPad((Block)TensuraBlocks.LABYRINTH_BRICK_WARP_PAD.get(), "labyrinth_brick_warp_pad");
      this.warpPad((Block)TensuraBlocks.CREAM_LABYRINTH_BRICK_WARP_PAD.get(), "cream_labyrinth_brick_warp_pad");
      this.warpPad((Block)TensuraBlocks.DARK_LABYRINTH_BRICK_WARP_PAD.get(), "dark_labyrinth_brick_warp_pad");
      this.magicEngine((Block)TensuraBlocks.BRICKS_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/bricks"));
      this.magicEngine((Block)TensuraBlocks.STONE_BRICKS_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/stone_bricks"));
      this.magicEngine((Block)TensuraBlocks.TUFF_BRICKS_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/tuff_bricks"));
      this.magicEngine((Block)TensuraBlocks.DEEPSLATE_BRICKS_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/deepslate_bricks"));
      this.magicEngine((Block)TensuraBlocks.MUD_BRICKS_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/mud_bricks"));
      this.magicEngine((Block)TensuraBlocks.PRISMARINE_BRICK_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/prismarine_bricks"));
      this.magicEngine((Block)TensuraBlocks.NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/nether_bricks"));
      this.magicEngine((Block)TensuraBlocks.RED_NETHER_BRICKS_STONE_BRICKS_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/red_nether_bricks"));
      this.magicEngine(
         (Block)TensuraBlocks.POLISHED_BLACKSTONE_BRICKS_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/polished_blackstone_bricks")
      );
      this.magicEngine((Block)TensuraBlocks.QUARTZ_BRICKS_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/quartz_bricks"));
      this.magicEngine((Block)TensuraBlocks.END_STONE_BRICKS_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/end_stone_bricks"));
      this.magicEngine((Block)TensuraBlocks.PURPUR_BRICKS_MAGIC_ENGINE.get(), ResourceLocation.withDefaultNamespace("block/purpur_block"));
      this.magicEngine((Block)TensuraBlocks.LOW_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get(), this.modLoc("block/low_quality_magic_crystal_bricks"));
      this.magicEngine((Block)TensuraBlocks.MEDIUM_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get(), this.modLoc("block/medium_quality_magic_crystal_bricks"));
      this.magicEngine((Block)TensuraBlocks.HIGH_QUALITY_MAGIC_CRYSTAL_BRICKS_MAGIC_ENGINE.get(), this.modLoc("block/high_quality_magic_crystal_bricks"));
      this.magicEngine((Block)TensuraBlocks.LABYRINTH_BRICKS_MAGIC_ENGINE.get(), this.modLoc("block/labyrinth_bricks"));
      this.magicEngine((Block)TensuraBlocks.CREAM_LABYRINTH_BRICKS_MAGIC_ENGINE.get(), this.modLoc("block/cream_labyrinth_bricks"));
      this.magicEngine((Block)TensuraBlocks.DARK_LABYRINTH_BRICKS_MAGIC_ENGINE.get(), this.modLoc("block/dark_labyrinth_bricks"));
   }

   public void simpleBlockWithRenderType(Block block, String string) {
      this.getVariantBuilder(block)
         .partialState()
         .setModels(
            new ConfiguredModel[]{
               new ConfiguredModel(
                  ((BlockModelBuilder)this.models().cubeAll(BuiltInRegistries.BLOCK.getKey(block).getPath(), this.blockTexture(block))).renderType(string)
               )
            }
         );
   }

   public void stairs(StairBlock stairs, RegistrySupplier<? extends Block> block) {
      String path = "block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath();
      this.stairsBlock(stairs, this.modLoc(path));
   }

   public void slab(SlabBlock slab, RegistrySupplier<? extends Block> block) {
      String path = "block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath();
      this.slabBlock(slab, this.modLoc(path), this.modLoc(path));
   }

   public void stairsPillar(StairBlock stairs, RegistrySupplier<? extends Block> block) {
      String path = "block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath();
      this.stairsBlock(stairs, this.modLoc(path), this.modLoc(path + "_top"), this.modLoc(path + "_top"));
   }

   public void slabPillar(SlabBlock slab, RegistrySupplier<? extends Block> block) {
      String path = "block/" + BuiltInRegistries.BLOCK.getKey((Block)block.get()).getPath();
      this.slabBlock(slab, this.modLoc(path), this.modLoc(path), this.modLoc(path + "_top"), this.modLoc(path + "_top"));
   }

   private void cross(Block block) {
      this.cross(block, this.blockTexture(block), RenderType.CUTOUT);
   }

   private void cross(Block block, ResourceLocation texture, RenderType renderType) {
      ModelFile modelFile = ((BlockModelBuilder)this.models().cross(BuiltInRegistries.BLOCK.getKey(block).getPath(), texture)).renderType(renderType.name);
      this.simpleBlock(block, modelFile);
   }

   private void cropWithAge(Block block, IntegerProperty age) {
      this.cropWithAge(block, BuiltInRegistries.BLOCK.getKey(block).getPath() + "_stage", BuiltInRegistries.BLOCK.getKey(block).getPath() + "_stage", age);
   }

   public void cropWithAge(Block block, String modelName, String textureName, IntegerProperty age) {
      Function<BlockState, ConfiguredModel[]> function = state -> new ConfiguredModel[]{
         new ConfiguredModel(
            ((BlockModelBuilder)this.models()
                  .cross(modelName + state.getValue(age), ResourceLocation.fromNamespaceAndPath("tensura", "block/" + textureName + state.getValue(age))))
               .renderType(RenderType.CUTOUT.name)
         )
      };
      this.getVariantBuilder(block).forAllStates(function);
   }

   protected void toolRack(Block block, ResourceLocation particle) {
      String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
      ModelFile base = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name, this.modLoc("block/tool_rack")))
            .texture("0", this.modLoc("block/" + name)))
         .texture("particle", particle);
      VariantBlockStateBuilder builder = this.getVariantBuilder(block);
      builder.partialState().with(HorizontalDirectionalBlock.FACING, Direction.EAST).modelForState().modelFile(base).rotationY(90).addModel();
      builder.partialState().with(HorizontalDirectionalBlock.FACING, Direction.NORTH).modelForState().modelFile(base).rotationY(0).addModel();
      builder.partialState().with(HorizontalDirectionalBlock.FACING, Direction.SOUTH).modelForState().modelFile(base).rotationY(180).addModel();
      builder.partialState().with(HorizontalDirectionalBlock.FACING, Direction.WEST).modelForState().modelFile(base).rotationY(270).addModel();
   }

   protected void warpPad(Block block, String texture) {
      String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
      ResourceLocation location = this.modLoc("block/" + texture);
      ModelFile base = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name, this.modLoc("block/warp_pad"))).texture("1", location))
         .texture("particle", location);
      ModelFile center = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_center", this.modLoc("block/warp_pad_center")))
            .texture("1", location))
         .texture("particle", location);
      ModelFile corner = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_corner", this.modLoc("block/warp_pad_corner")))
            .texture("1", location))
         .texture("particle", location);
      ModelFile side = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_side", this.modLoc("block/warp_pad_side")))
            .texture("1", location))
         .texture("particle", location);
      VariantBlockStateBuilder builder = this.getVariantBuilder(block);
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.SMALL_NW).modelForState().modelFile(base).rotationY(180).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.SMALL_NE).modelForState().modelFile(base).rotationY(270).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.SMALL_SE).modelForState().modelFile(base).rotationY(0).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.SMALL_SW).modelForState().modelFile(base).rotationY(90).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.BIG_C).modelForState().modelFile(center).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.BIG_N).modelForState().modelFile(side).rotationY(180).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.BIG_E).modelForState().modelFile(side).rotationY(270).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.BIG_S).modelForState().modelFile(side).rotationY(0).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.BIG_W).modelForState().modelFile(side).rotationY(90).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.BIG_NW).modelForState().modelFile(corner).rotationY(180).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.BIG_NE).modelForState().modelFile(corner).rotationY(270).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.BIG_SE).modelForState().modelFile(corner).rotationY(0).addModel();
      builder.partialState().with(WarpPadBlock.PART, WarpPadPart.BIG_SW).modelForState().modelFile(corner).rotationY(90).addModel();
   }

   protected void magicEngine(Block block, ResourceLocation baseTexture) {
      String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
      ModelFile inactive = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name, this.modLoc("block/magic_engine")))
            .texture("1", baseTexture))
         .texture("particle", baseTexture);
      ModelFile active = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models()
                  .withExistingParent(name + "_active", this.modLoc("block/magic_engine")))
               .texture("1", baseTexture))
            .texture("2", this.modLoc("block/magic_engine_active")))
         .texture("particle", baseTexture);
      VariantBlockStateBuilder stateBuilder = this.getVariantBuilder(block);

      for (boolean enabled : new boolean[]{false, true}) {
         ModelFile file = enabled ? active : inactive;

         for (Direction facing : Direction.values()) {
            int x = rotXForFacing(facing);
            int y = rotYForFacing(facing);
            Builder<VariantBlockStateBuilder> builder = stateBuilder.partialState()
               .with(MagicEngineBlock.FACING, facing)
               .with(MagicEngineBlock.ENABLED, enabled)
               .modelForState()
               .modelFile(file);
            if (x != 0) {
               builder = builder.rotationX(x);
            }

            if (y != 0) {
               builder = builder.rotationY(y);
            }

            builder.addModel();
         }
      }

      this.simpleBlockItem(block, inactive);
   }

   protected void kiln(Block block, ResourceLocation particleTexture) {
      String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
      ModelFile bottom = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_bottom", this.modLoc("block/kiln_bottom")))
            .texture("0", this.modLoc("block/" + name + "_bottom")))
         .texture("particle", particleTexture);
      ModelFile bottomLit = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_bottom_lit", this.modLoc("block/kiln_bottom")))
            .texture("0", this.modLoc("block/" + name + "_bottom_lit")))
         .texture("particle", particleTexture);
      ModelFile bottomBoosted = ((BlockModelBuilder)((BlockModelBuilder)this.models()
               .withExistingParent(name + "_bottom_boosted", this.modLoc("block/kiln_bottom")))
            .texture("0", this.modLoc("block/" + name + "_bottom_boosted")))
         .texture("particle", particleTexture);
      ModelFile top = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_top", this.modLoc("block/kiln_top")))
            .texture("0", this.modLoc("block/" + name + "_top")))
         .texture("particle", particleTexture);
      ModelFile topLit = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_top_lit", this.modLoc("block/kiln_top")))
            .texture("0", this.modLoc("block/" + name + "_top_lit")))
         .texture("particle", particleTexture);
      ModelFile topBoosted = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_top_boosted", this.modLoc("block/kiln_top")))
            .texture("0", this.modLoc("block/" + name + "_top_boosted")))
         .texture("particle", particleTexture);
      VariantBlockStateBuilder builder = this.getVariantBuilder(block);

      for (Direction facing : Plane.HORIZONTAL) {
         int rotY = switch (facing) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
         };
         this.addKilnState(builder, facing, false, false, KilnPart.BASE, bottom, rotY);
         this.addKilnState(builder, facing, true, false, KilnPart.BASE, bottomLit, rotY);
         this.addKilnState(builder, facing, true, true, KilnPart.BASE, bottomBoosted, rotY);
         this.addKilnState(builder, facing, false, true, KilnPart.BASE, bottom, rotY);
         this.addKilnState(builder, facing, false, false, KilnPart.TOP, top, rotY);
         this.addKilnState(builder, facing, true, false, KilnPart.TOP, topLit, rotY);
         this.addKilnState(builder, facing, true, true, KilnPart.TOP, topBoosted, rotY);
         this.addKilnState(builder, facing, false, true, KilnPart.TOP, top, rotY);
      }
   }

   private void addKilnState(VariantBlockStateBuilder builder, Direction facing, boolean lit, boolean powered, KilnPart part, ModelFile model, int rotY) {
      builder.partialState()
         .with(KilnBlock.FACING, facing)
         .with(KilnBlock.LIT, lit)
         .with(KilnBlock.BOOSTED, powered)
         .with(KilnBlock.PART, part)
         .modelForState()
         .modelFile(model)
         .rotationY(rotY)
         .addModel();
   }

   private static int rotXForFacing(Direction direction) {
      return switch (direction) {
         case UP -> 0;
         case DOWN -> 180;
         default -> 90;
      };
   }

   private static int rotYForFacing(Direction direction) {
      return switch (direction) {
         case EAST -> 90;
         case SOUTH -> 180;
         case WEST -> 270;
         default -> 0;
      };
   }

   private String name(Block block) {
      return this.key(block).getPath();
   }

   public ResourceLocation blockTexture(Block block) {
      ResourceLocation name = this.key(block);
      return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), "block/" + name.getPath());
   }

   private ResourceLocation key(Block block) {
      return BuiltInRegistries.BLOCK.getKey(block);
   }
}
