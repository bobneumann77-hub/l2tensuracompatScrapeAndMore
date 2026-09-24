package io.github.manasmods.tensura.block;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class HipokuteFlowerPotBlock extends FlowerPotBlock {
   public final RegistrySupplier<? extends Item> flower;

   public HipokuteFlowerPotBlock(RegistrySupplier<? extends Item> flower, Properties properties) {
      super((Block)TensuraBlocks.HIPOKUTE_GRASS.get(), properties);
      this.flower = flower;
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
      ItemStack itemStack = new ItemStack((ItemLike)this.flower.get());
      if (!player.addItem(itemStack)) {
         player.drop(itemStack, false);
      }

      level.setBlock(blockPos, Blocks.FLOWER_POT.defaultBlockState(), 3);
      level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
      return InteractionResult.sidedSuccess(level.isClientSide);
   }
}
