package dev.xkmc.l2hostility.content.item.beacon;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.registrate.LHBlocks;
import dev.xkmc.l2modularblock.core.DelegateEntityBlockImpl;
import dev.xkmc.l2modularblock.impl.BlockEntityBlockMethodImpl;
import dev.xkmc.l2modularblock.type.BlockMethod;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;

public class HostilityBeaconBlock extends DelegateEntityBlockImpl implements BeaconBeamBlock {
   public static final BlockEntityBlockMethodImpl<HostilityBeaconBlockEntity> BE = new BlockEntityBlockMethodImpl(
      LHBlocks.BE_BEACON, HostilityBeaconBlockEntity.class
   );

   public HostilityBeaconBlock(Properties p) {
      super(p, new BlockMethod[]{BE});
   }

   public DyeColor getColor() {
      return DyeColor.RED;
   }

   public static void buildModel(DataGenContext<Block, HostilityBeaconBlock> ctx, RegistrateBlockstateProvider pvd) {
      ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)pvd.models()
                        .withExistingParent(ctx.getName(), "block/beacon"))
                     .texture("particle", pvd.modLoc("block/beacon_glass")))
                  .texture("glass", pvd.modLoc("block/beacon_glass")))
               .texture("obsidian", pvd.mcLoc("block/crying_obsidian")))
            .texture("beacon", pvd.modLoc("block/beacon")))
         .renderType("translucent");
      ((PartBuilder)((PartBuilder)pvd.getMultipartBuilder((Block)ctx.get())
               .part()
               .modelFile(
                  ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)pvd.models()
                                    .getBuilder(ctx.getName() + "_base"))
                                 .parent(new UncheckedModelFile(L2Hostility.loc("block/beacon"))))
                              .texture("particle", pvd.modLoc("block/beacon_glass")))
                           .texture("obsidian", pvd.mcLoc("block/crying_obsidian")))
                        .texture("beacon", pvd.modLoc("block/beacon")))
                     .renderType("translucent")
               )
               .addModel())
            .end()
            .part()
            .modelFile(((BlockModelBuilder)pvd.models().cubeAll(ctx.getName() + "_glass", pvd.modLoc("block/beacon_glass"))).renderType("translucent"))
            .addModel())
         .end();
   }
}
