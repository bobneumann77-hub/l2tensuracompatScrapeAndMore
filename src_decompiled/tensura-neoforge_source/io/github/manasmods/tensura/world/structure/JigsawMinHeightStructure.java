package io.github.manasmods.tensura.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.registry.world.TensuraStructureTypes;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public class JigsawMinHeightStructure extends Structure {
   public static final MapCodec<JigsawMinHeightStructure> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               settingsCodec(instance),
               StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
               ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
               Codec.intRange(0, 7).fieldOf("size").forGetter(structure -> structure.maxDepth),
               HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
               Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
               Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
               Codec.intRange(1, 256).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
               Codec.INT.fieldOf("minY").orElse(0).forGetter(structure -> structure.minY),
               Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(jigsawStructure -> jigsawStructure.poolAliases),
               DimensionPadding.CODEC
                  .optionalFieldOf("dimension_padding", DimensionPadding.ZERO)
                  .forGetter(jigsawStructure -> jigsawStructure.dimensionPadding),
               LiquidSettings.CODEC
                  .optionalFieldOf("liquid_settings", LiquidSettings.APPLY_WATERLOGGING)
                  .forGetter(jigsawStructure -> jigsawStructure.liquidSettings)
            )
            .apply(instance, JigsawMinHeightStructure::new)
      )
      .flatXmap(verifyRange(), verifyRange());
   private final Holder<StructureTemplatePool> startPool;
   private final Optional<ResourceLocation> startJigsawName;
   private final int maxDepth;
   private final HeightProvider startHeight;
   private final boolean useExpansionHack;
   private final Optional<Types> projectStartToHeightmap;
   private final int maxDistanceFromCenter;
   private final int minY;
   private final List<PoolAliasBinding> poolAliases;
   private final DimensionPadding dimensionPadding;
   private final LiquidSettings liquidSettings;

   private static Function<JigsawMinHeightStructure, DataResult<JigsawMinHeightStructure>> verifyRange() {
      return structure -> {
         int i = switch (structure.terrainAdaptation()) {
            case NONE -> 0;
            case BURY, BEARD_THIN, BEARD_BOX, ENCAPSULATE -> 12;
            default -> throw new MatchException(null, null);
         };
         return structure.maxDistanceFromCenter + i > 256
            ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 256")
            : DataResult.success(structure);
      };
   }

   public JigsawMinHeightStructure(
      StructureSettings settings,
      Holder<StructureTemplatePool> holder,
      Optional<ResourceLocation> location,
      int depth,
      HeightProvider height,
      boolean hack,
      Optional<Types> project,
      int distance,
      int minY,
      List<PoolAliasBinding> list,
      DimensionPadding dimensionPadding,
      LiquidSettings liquidSettings
   ) {
      super(settings);
      this.startPool = holder;
      this.startJigsawName = location;
      this.maxDepth = depth;
      this.startHeight = height;
      this.useExpansionHack = hack;
      this.projectStartToHeightmap = project;
      this.maxDistanceFromCenter = distance;
      this.minY = minY;
      this.poolAliases = list;
      this.dimensionPadding = dimensionPadding;
      this.liquidSettings = liquidSettings;
   }

   public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
      ChunkPos chunkpos = context.chunkPos();
      int i = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
      BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), i, chunkpos.getMinBlockZ());
      if (this.projectStartToHeightmap.isPresent()) {
         int y = context.chunkGenerator().getFirstFreeHeight(blockpos.getX(), blockpos.getZ(), switch ((Types)this.projectStartToHeightmap.get()) {
            case WORLD_SURFACE_WG -> Types.WORLD_SURFACE;
            case OCEAN_FLOOR_WG -> Types.OCEAN_FLOOR;
            default -> (Types)this.projectStartToHeightmap.get();
         }, context.heightAccessor(), context.randomState());
         if (y < this.minY) {
            return Optional.empty();
         }
      }

      return JigsawPlacement.addPieces(
         context,
         this.startPool,
         this.startJigsawName,
         this.maxDepth,
         blockpos,
         this.useExpansionHack,
         this.projectStartToHeightmap,
         this.maxDistanceFromCenter,
         PoolAliasLookup.create(this.poolAliases, blockpos, context.seed()),
         this.dimensionPadding,
         this.liquidSettings
      );
   }

   public StructureType<?> type() {
      return (StructureType<?>)TensuraStructureTypes.JIGSAW_MIN_HEIGHT.get();
   }
}
