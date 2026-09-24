package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.block.template.SimpleLeaves;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.labyrinth.ILabyrinth;
import io.github.manasmods.tensura.storage.labyrinth.LabyrinthStorage;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.util.client.ClientHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LabyrinthBarrierBlock extends Block {
   public static final BooleanProperty BLOCK_MOTION = BlockStateProperties.ENABLED;

   public LabyrinthBarrierBlock() {
      super(
         Properties.of()
            .mapColor(MapColor.NONE)
            .isSuffocating(SimpleLeaves::never)
            .isViewBlocking((blockState, blockGetter, blockPos) -> false)
            .isValidSpawn((blockState, blockGetter, blockPos, entityType) -> false)
            .noLootTable()
            .dynamicShape()
            .noOcclusion()
            .pushReaction(PushReaction.IGNORE)
            .sound(SoundType.AMETHYST)
            .strength(-1.0F, 3600000.0F)
      );
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(BLOCK_MOTION, Boolean.FALSE));
   }

   public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return Shapes.empty();
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.INVISIBLE;
   }

   public void animateTick(BlockState pState, Level pLevel, BlockPos pos, RandomSource pRandom) {
      ClientHelper.spawnMarkerParticle(pLevel, pState, pos, (Item)TensuraBlocks.Items.LABYRINTH_BARRIER_BLOCK.get());
   }

   public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      Entity entity = pContext instanceof EntityCollisionContext entityCollision ? entityCollision.getEntity() : null;
      if (entity instanceof Projectile) {
         return Shapes.block();
      } else if (entity != null && entity.getType().is(TensuraEntityTags.SPIRIT_PROTECTOR)) {
         return Shapes.block();
      } else {
         return this.canGoThrough(pState, entity) ? Shapes.empty() : Shapes.block();
      }
   }

   public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity target) {
      if (!this.canGoThrough(pState, target)) {
         if (pLevel instanceof ServerLevel serverLevel) {
            ILabyrinth saveData = TensuraStorages.getLabyrinthFrom(serverLevel);
            if (saveData != null) {
               Vec3 throwVec = saveData.getColossusPos().subtract(target.position()).normalize().scale(1.5);
               target.setDeltaMovement(throwVec.x(), 0.0, throwVec.z());
               if (target instanceof LivingEntity protector && protector.getType().is(TensuraEntityTags.SPIRIT_PROTECTOR)) {
                  SubordinateHelper.removeTarget(protector);
               }

               target.hurtMarked = true;
            }
         }
      }
   }

   public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
      if (!pPlayer.isCreative()) {
         return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHit);
      }

      pLevel.setBlockAndUpdate(pPos, (BlockState)pState.setValue(BLOCK_MOTION, !(Boolean)pState.getValue(BLOCK_MOTION)));
      return InteractionResult.SUCCESS;
   }

   public boolean isCollisionShapeFullBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return true;
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

   protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
   }

   public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return true;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{BLOCK_MOTION});
   }

   private boolean canGoThrough(BlockState pState, @Nullable Entity entity) {
      if (entity == null) {
         return true;
      } else if (this.isColossusStartedBlocking(entity)) {
         return false;
      } else if ((Boolean)pState.getValue(BLOCK_MOTION)) {
         return this.isColossusWonButEmpty(entity) ? true : LabyrinthStorage.isEntityPassedColossus(entity);
      } else {
         return true;
      }
   }

   private boolean isColossusStartedBlocking(Entity entity) {
      if (entity instanceof LivingEntity living) {
         MinecraftServer server = entity.level().getServer();
         if (server != null) {
            ServerLevel level = server.getLevel(TensuraDimensions.LABYRINTH);
            if (level != null) {
               ILabyrinth data = TensuraStorages.getLabyrinthFrom(level);
               if (data != null && !data.isColossusSpawned() && TensuraStorages.getSpiritFrom(living).isColossusStarted()) {
                  LabyrinthStorage.removeStartedEntity(living);
                  return false;
               }
            }
         }

         return TensuraStorages.getSpiritFrom(living).isColossusStarted();
      } else {
         return false;
      }
   }

   private boolean isColossusWonButEmpty(Entity entity) {
      if (entity instanceof LivingEntity living) {
         MinecraftServer server = entity.level().getServer();
         if (server != null) {
            ServerLevel level = server.getLevel(TensuraDimensions.LABYRINTH);
            if (level != null) {
               ILabyrinth data = TensuraStorages.getLabyrinthFrom(level);
               if (data != null && !data.isColossusSpawned() && TensuraStorages.getSpiritFrom(living).isColossusWon()) {
                  return true;
               }
            }
         }

         return TensuraStorages.getSpiritFrom(living).isColossusWon();
      } else {
         return false;
      }
   }
}
