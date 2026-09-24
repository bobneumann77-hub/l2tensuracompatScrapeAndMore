package io.github.manasmods.tensura.neoforge.data.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class TensuraFluidTagProvider extends FluidTagsProvider {
   public TensuraFluidTagProvider(PackOutput output, CompletableFuture<Provider> completableFuture, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, completableFuture, "tensura", existingFileHelper);
   }

   protected void addTags(Provider arg) {
   }
}
