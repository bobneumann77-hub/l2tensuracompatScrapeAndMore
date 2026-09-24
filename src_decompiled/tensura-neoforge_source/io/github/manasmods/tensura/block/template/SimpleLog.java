package io.github.manasmods.tensura.block.template;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class SimpleLog extends RotatedPillarBlock {
   public SimpleLog(MapColor topColor, MapColor sideColor) {
      super(
         Properties.of()
            .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Axis.Y ? topColor : sideColor)
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.0F)
            .sound(SoundType.WOOD)
            .ignitedByLava()
      );
   }

   public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return 5;
   }

   public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return 5;
   }
}
