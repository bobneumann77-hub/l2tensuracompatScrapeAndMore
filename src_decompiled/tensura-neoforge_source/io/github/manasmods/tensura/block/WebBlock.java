package io.github.manasmods.tensura.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;

public class WebBlock extends Block {
   public WebBlock() {
      super(
         Properties.of()
            .mapColor(MapColor.WOOL)
            .strength(8.0F, 12.0F)
            .speedFactor(0.4F)
            .jumpFactor(0.8F)
            .sound(SoundType.WOOL)
            .isValidSpawn(WebBlock::always)
            .isViewBlocking(WebBlock::always)
            .isSuffocating(WebBlock::always)
      );
   }

   public static Boolean always(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType<?> entityType) {
      return true;
   }

   public static boolean always(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return true;
   }
}
