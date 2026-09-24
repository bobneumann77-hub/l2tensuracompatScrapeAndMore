package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.labyrinth.ILabyrinth;
import io.github.manasmods.tensura.storage.labyrinth.LabyrinthStorage;
import io.github.manasmods.tensura.util.client.ClientHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class LabyrinthPortal extends Block implements Portal, SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public LabyrinthPortal() {
      super(
         Properties.of()
            .isViewBlocking((blockState, blockGetter, blockPos) -> false)
            .noLootTable()
            .noOcclusion()
            .noCollission()
            .sound(SoundType.AMETHYST)
            .strength(-1.0F, 3600000.0F)
      );
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, Boolean.FALSE));
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.INVISIBLE;
   }

   public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
      return stateFrom.is(this) ? true : super.skipRendering(state, stateFrom, direction);
   }

   public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return true;
   }

   public void animateTick(BlockState pState, Level pLevel, BlockPos pos, RandomSource pRandom) {
      ClientHelper.spawnMarkerParticle(pLevel, pState, pos, (Item)TensuraBlocks.Items.LABYRINTH_PORTAL.get());
   }

   public boolean isCollisionShapeFullBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return false;
   }

   public boolean canBeReplaced(BlockState pState, Fluid pFluid) {
      return false;
   }

   public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
      return false;
   }

   public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      return true;
   }

   protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if (entity.canUsePortal(false)
         && Shapes.joinIsNotEmpty(
            Shapes.create(entity.getBoundingBox().move(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ())),
            blockState.getShape(level, blockPos),
            BooleanOp.AND
         )) {
         entity.setAsInsidePortal(this, blockPos);
      }
   }

   public DimensionTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos) {
      ResourceKey<Level> resourceKey = serverLevel.dimension() == TensuraDimensions.LABYRINTH ? Level.OVERWORLD : TensuraDimensions.LABYRINTH;
      ServerLevel destination = serverLevel.getServer().getLevel(resourceKey);
      if (destination == null) {
         return null;
      }

      if (resourceKey == TensuraDimensions.LABYRINTH) {
         ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(destination);
         if (!labyrinth.isLoaded()) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.command.labyrinth.generating").withStyle(ChatFormatting.GOLD), true);
            }

            return null;
         } else {
            Vec3 spawnPos = LabyrinthStorage.isEntityPassedColossus(entity) ? labyrinth.getPassedEntrancePos() : labyrinth.getEntrancePos();
            if (entity instanceof ServerPlayer player && TensuraStorages.getSpiritFrom(player).isColossusWon()) {
               spawnPos = labyrinth.getPassedEntrancePos();
            }

            return new DimensionTransition(
               destination,
               spawnPos,
               entity.getDeltaMovement(),
               0.0F,
               0.0F,
               DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)
            );
         }
      } else if (entity instanceof ServerPlayer player && player.getRespawnDimension() == Level.OVERWORLD) {
         return player.findRespawnPositionAndUseSpawnBlock(false, DimensionTransition.DO_NOTHING);
      } else {
         Vec3 vec3 = entity.adjustSpawnLocation(destination, destination.getSharedSpawnPos()).getBottomCenter();
         return new DimensionTransition(
            destination, vec3, Vec3.ZERO, 0.0F, 0.0F, DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)
         );
      }
   }

   public static DimensionTransition getOverworldTransition(Entity entity, ServerLevel destination) {
      if (entity instanceof ServerPlayer player && player.getRespawnDimension() == Level.OVERWORLD) {
         return player.findRespawnPositionAndUseSpawnBlock(false, DimensionTransition.DO_NOTHING);
      } else {
         Vec3 vec3 = entity.adjustSpawnLocation(destination, destination.getSharedSpawnPos()).getBottomCenter();
         return new DimensionTransition(
            destination,
            vec3,
            Vec3.ZERO,
            entity.getYRot(),
            entity.getXRot(),
            DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)
         );
      }
   }

   public static void switchGameMode(LivingEntity entity, boolean leavingLabyrinth) {
      LabyrinthStorage.removeStartedEntity(entity);
      if (entity instanceof ServerPlayer player) {
         GameType current = player.gameMode.getGameModeForPlayer();
         if (leavingLabyrinth) {
            if (current != GameType.ADVENTURE) {
               return;
            }

            MinecraftServer server = player.level().getServer();
            if (server == null) {
               player.setGameMode(GameType.DEFAULT_MODE);
            } else {
               GameType type = server.getDefaultGameType();
               if (type.isSurvival() && type != current) {
                  player.setGameMode(type);
               } else {
                  player.setGameMode(GameType.DEFAULT_MODE);
               }
            }
         } else if (current == GameType.SURVIVAL) {
            player.setGameMode(GameType.ADVENTURE);
         }

         if (!player.isCreative() && !player.isSpectator() && SkillUtils.canFlyLegit(player)) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
         }
      }
   }

   public BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos
   ) {
      if ((Boolean)pState.getValue(WATERLOGGED)) {
         pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
   }

   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{WATERLOGGED});
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }
}
