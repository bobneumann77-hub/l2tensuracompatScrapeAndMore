package dev.xkmc.l2hostility.content.item.spawner;

import dev.xkmc.l2modularblock.mult.CreateBlockStateBlockMethod;
import dev.xkmc.l2modularblock.mult.DefaultStateBlockMethod;
import dev.xkmc.l2modularblock.one.LightBlockMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;

public class BaseTraitMethod implements CreateBlockStateBlockMethod, DefaultStateBlockMethod, LightBlockMethod {
   public void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{TraitSpawnerBlock.STATE});
   }

   public BlockState getDefaultState(BlockState state) {
      return (BlockState)state.setValue(TraitSpawnerBlock.STATE, TraitSpawnerBlock.State.IDLE);
   }

   public int getLightValue(BlockState state, BlockGetter level, BlockPos pos) {
      return ((TraitSpawnerBlock.State)state.getValue(TraitSpawnerBlock.STATE)).light();
   }
}
