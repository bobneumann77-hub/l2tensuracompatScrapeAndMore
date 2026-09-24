package io.github.manasmods.tensura.item.misc;

import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

public class HipokuteFlowerItem extends Item {
   public final Supplier<Block> pottedFlower;

   public HipokuteFlowerItem(Supplier<Block> pot, Properties pProperties) {
      super(pProperties);
      this.pottedFlower = pot;
   }
}
