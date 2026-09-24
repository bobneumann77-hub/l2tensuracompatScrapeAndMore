package io.github.manasmods.tensura.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;

public class WebbedStoneBlock extends Block {
   public WebbedStoneBlock() {
      super(
         Properties.of()
            .mapColor(MapColor.STONE)
            .strength(2.0F, 6.0F)
            .speedFactor(0.8F)
            .jumpFactor(0.9F)
            .sound(SoundType.STONE)
            .isValidSpawn(WebBlock::always)
            .isRedstoneConductor(WebBlock::always)
            .isViewBlocking(WebBlock::always)
            .isSuffocating(WebBlock::always)
      );
   }
}
