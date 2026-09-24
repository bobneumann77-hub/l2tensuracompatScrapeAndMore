package io.github.manasmods.tensura.neoforge.data.advancement;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TensuraAdvancementProvider extends AdvancementProvider {
   public TensuraAdvancementProvider(PackOutput output, CompletableFuture<Provider> registries, ExistingFileHelper existingFileHelper) {
      super(output, registries, existingFileHelper, List.of(new TensuraAdvancementGenerator()));
   }
}
