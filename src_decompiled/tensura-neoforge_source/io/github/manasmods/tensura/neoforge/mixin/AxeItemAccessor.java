package io.github.manasmods.tensura.neoforge.mixin;

import java.util.Map;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {
   @Accessor("STRIPPABLES")
   static Map<Block, Block> getStrippedBlocks() {
      throw new AssertionError("Untransformed @Accessor");
   }

   @Accessor("STRIPPABLES")
   @Mutable
   static void setStrippedBlocks(Map<Block, Block> strippedBlocks) {
      throw new AssertionError("Untransformed @Accessor");
   }
}
