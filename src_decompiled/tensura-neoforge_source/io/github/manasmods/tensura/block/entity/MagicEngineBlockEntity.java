package io.github.manasmods.tensura.block.entity;

import io.github.manasmods.tensura.block.MagicEngineBlock;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.util.MagicEngineHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MagicEngineBlockEntity extends BlockEntity {
   public int spin;
   private boolean tracked = false;

   public MagicEngineBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   public MagicEngineBlockEntity(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)TensuraBlockEntities.MAGIC_ENGINE.get(), blockPos, blockState);
   }

   public void setLevel(Level level) {
      super.setLevel(level);
      if (!level.isClientSide() && (Boolean)this.getBlockState().getValue(MagicEngineBlock.ENABLED)) {
         this.setTracked(true);
      }
   }

   public void setRemoved() {
      if (this.tracked) {
         this.setTracked(false);
      }

      super.setRemoved();
   }

   public void setTracked(boolean shouldTrack) {
      if (this.tracked != shouldTrack) {
         if (shouldTrack) {
            MagicEngineHelper.increment();
         } else {
            MagicEngineHelper.decrement();
         }

         this.tracked = shouldTrack;
      }
   }
}
