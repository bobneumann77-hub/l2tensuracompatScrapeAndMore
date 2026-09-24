package io.github.manasmods.tensura.neoforge.data.loot;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class TensuraLootProvider extends LootTableProvider {
   public TensuraLootProvider(PackOutput packOutput, CompletableFuture<Provider> provider) {
      super(
         packOutput,
         Set.of(),
         List.of(
            new SubProviderEntry(TensuraBlockLootProvider::new, LootContextParamSets.BLOCK),
            new SubProviderEntry(TensuraEntityLootProvider::new, LootContextParamSets.ENTITY),
            new SubProviderEntry(TensuraChestLootProvider::new, LootContextParamSets.CHEST),
            new SubProviderEntry(TensuraArchaeologyLootProvider::new, LootContextParamSets.ARCHAEOLOGY),
            new SubProviderEntry(TensuraAdvancementLootProvider::new, LootContextParamSets.ADVANCEMENT_REWARD)
         ),
         provider
      );
   }
}
