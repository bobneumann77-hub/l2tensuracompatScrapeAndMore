package io.github.manasmods.tensura.registry.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TensuraPoiTypes {
   public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create("tensura", Registries.POINT_OF_INTEREST_TYPE);
   public static RegistrySupplier<PoiType> BATTLEWILL_TRAINER = register("battlewill_trainer", TensuraBlocks.TRAINING_DUMMY, 1, 1);
   public static RegistrySupplier<PoiType> GUARD = register(
      "guard",
      1,
      1,
      TensuraBlocks.OAK_TOOL_RACK,
      TensuraBlocks.SPRUCE_TOOL_RACK,
      TensuraBlocks.BIRCH_TOOL_RACK,
      TensuraBlocks.JUNGLE_TOOL_RACK,
      TensuraBlocks.ACACIA_TOOL_RACK,
      TensuraBlocks.DARK_OAK_TOOL_RACK,
      TensuraBlocks.MANGROVE_TOOL_RACK,
      TensuraBlocks.CHERRY_TOOL_RACK,
      TensuraBlocks.BAMBOO_TOOL_RACK,
      TensuraBlocks.CRIMSON_TOOL_RACK,
      TensuraBlocks.WARPED_TOOL_RACK,
      TensuraBlocks.PALM_TOOL_RACK
   );
   public static RegistrySupplier<PoiType> LUMBERJACK = register("lumberjack", TensuraBlocks.WOODCUTTER, 1, 1);
   public static RegistrySupplier<PoiType> MAGIC_TRAINER = register("magic_trainer", TensuraBlocks.SPELLBINDING_TABLE, 1, 1);
   public static RegistrySupplier<PoiType> MINER = register("miner", TensuraBlocks.MINING_STATION, 1, 1);

   public static void init() {
      POI_TYPES.register();
   }

   private static RegistrySupplier<PoiType> register(String name, Set<BlockState> set, int i, int j) {
      return POI_TYPES.register(name, () -> new PoiType(set, i, j));
   }

   private static RegistrySupplier<PoiType> register(String name, Supplier<? extends Block> blockSupplier, int ticketCount, int searchDistance) {
      return POI_TYPES.register(name, () -> {
         Set<BlockState> set = ImmutableSet.copyOf(blockSupplier.get().getStateDefinition().getPossibleStates());
         return new PoiType(set, ticketCount, searchDistance);
      });
   }

   @SafeVarargs
   private static RegistrySupplier<PoiType> register(String name, int ticketCount, int searchDistance, Supplier<? extends Block>... blockSuppliers) {
      return POI_TYPES.register(name, () -> {
         Builder<BlockState> builder = ImmutableSet.builder();

         for (Supplier<? extends Block> supplier : blockSuppliers) {
            builder.addAll(supplier.get().getStateDefinition().getPossibleStates());
         }

         return new PoiType(builder.build(), ticketCount, searchDistance);
      });
   }
}
