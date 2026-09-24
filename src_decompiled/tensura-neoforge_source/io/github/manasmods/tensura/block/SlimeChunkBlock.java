package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlimeChunkBlock extends HalfTransparentBlock {
   private static final VoxelShape FALLING_COLLISION_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9F, 1.0);

   public SlimeChunkBlock(Properties pProperties) {
      super(pProperties);
   }

   public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pDirection) {
      return pAdjacentBlockState.is(this) || pAdjacentBlockState.is((Block)TensuraBlocks.CHILLED_SLIME_BLOCK.get());
   }

   public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return Shapes.empty();
   }

   public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
      if (!pEntity.getType().is(TensuraEntityTags.SLIME_WALKABLE_MOBS)) {
         pEntity.makeStuckInBlock(pState, new Vec3(0.7F, 0.7, 0.7F));
      }
   }

   public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
      if (!(pFallDistance < 4.0F)) {
         pEntity.playSound(SoundEvents.SLIME_BLOCK_FALL, 1.0F, 1.0F);
      }
   }

   public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      if (pContext instanceof EntityCollisionContext entitycollisioncontext) {
         Entity entity = entitycollisioncontext.getEntity();
         if (entity == null) {
            return Shapes.empty();
         }

         if (entity.fallDistance > 2.5F) {
            return FALLING_COLLISION_SHAPE;
         }
      }

      return Shapes.empty();
   }

   protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
      return true;
   }

   public boolean isStickyBlock(BlockState state) {
      return state.is(TensuraBlockTags.STICKY_BLOCKS);
   }
}
