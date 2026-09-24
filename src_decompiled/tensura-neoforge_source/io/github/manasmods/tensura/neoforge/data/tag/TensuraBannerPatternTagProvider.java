package io.github.manasmods.tensura.neoforge.data.tag;

import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.registry.item.misc.TensuraBannerPatterns;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class TensuraBannerPatternTagProvider extends BannerPatternTagsProvider {
   public TensuraBannerPatternTagProvider(PackOutput output, CompletableFuture<Provider> completableFuture, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, completableFuture, "tensura", existingFileHelper);
   }

   protected void addTags(Provider arg) {
      this.tag(TensuraTags.BannerPattens.DWARGON).add(TensuraBannerPatterns.DWARGON);
   }
}
