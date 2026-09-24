package io.github.manasmods.tensura.registry.block;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class TensuraWoodTypes {
   public static final BlockSetType PALM_SET = new BlockSetType("tensura:palm");
   public static WoodType PALM = WoodType.register(new WoodType("tensura:palm", PALM_SET));
}
