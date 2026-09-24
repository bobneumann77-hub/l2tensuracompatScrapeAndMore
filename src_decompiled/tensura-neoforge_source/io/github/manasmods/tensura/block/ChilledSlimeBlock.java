package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.Vec3;

public class ChilledSlimeBlock extends SlimeChunkBlock {
   public ChilledSlimeBlock(Properties pProperties) {
      super(pProperties);
   }

   @Override
   public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pDirection) {
      return pAdjacentBlockState.is(this) || pAdjacentBlockState.is((Block)TensuraBlocks.SLIME_CHUNK_BLOCK.get());
   }

   @Override
   public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
      if (!pEntity.getType().is(TensuraEntityTags.SLIME_WALKABLE_MOBS)) {
         pEntity.makeStuckInBlock(pState, new Vec3(0.5, 0.7, 0.5));
         pEntity.setIsInPowderSnow(true);
         if (!pLevel.isClientSide && pEntity.isOnFire()) {
            pEntity.setSharedFlagOnFire(false);
         }
      }
   }
}
