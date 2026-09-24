package io.github.manasmods.tensura.neoforge.data.tag;

import io.github.manasmods.tensura.registry.block.TensuraPaintingVariants;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.PaintingVariantTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class TensuraPaintingVariantTagsProvider extends PaintingVariantTagsProvider {
   public TensuraPaintingVariantTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, "tensura", existingFileHelper);
   }

   protected void addTags(Provider provider) {
      this.tag(PaintingVariantTags.PLACEABLE)
         .add(
            new ResourceKey[]{
               TensuraPaintingVariants.BLUMUND,
               TensuraPaintingVariants.FILTWOOD,
               TensuraPaintingVariants.FULBROSIA,
               TensuraPaintingVariants.INGRASSIA,
               TensuraPaintingVariants.LETTER_OF_CHALLENGE,
               TensuraPaintingVariants.RAJA,
               TensuraPaintingVariants.SCARLET_BOND,
               TensuraPaintingVariants.SUNFLOWER,
               TensuraPaintingVariants.WORLD_MAP,
               TensuraPaintingVariants.TEMPEST
            }
         );
   }
}
