package io.github.manasmods.tensura.item.misc;

import dev.architectury.registry.registries.DeferredSupplier;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

public class SimpleBlockItem extends BlockItem {
   public SimpleBlockItem(Block pBlock, Properties properties) {
      super(pBlock, properties);
   }

   public SimpleBlockItem(Block pBlock, CreativeModeTab tab) {
      this(pBlock, new Properties().arch$tab(tab));
   }

   public SimpleBlockItem(Block pBlock, DeferredSupplier<CreativeModeTab> tab) {
      this(pBlock, new Properties().arch$tab(tab));
   }

   public SimpleBlockItem(Block pBlock) {
      this(pBlock, TensuraCreativeTabs.BLOCKS);
   }
}
