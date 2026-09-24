package io.github.manasmods.tensura.neoforge.data.tag;

import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.registry.block.TensuraPoiTypes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.PoiTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class TensuraPoiTagProvider extends PoiTypeTagsProvider {
   public TensuraPoiTagProvider(PackOutput output, CompletableFuture<Provider> completableFuture, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, completableFuture, "tensura", existingFileHelper);
   }

   protected void addTags(Provider arg) {
      this.tag(TensuraTags.PoiTypes.NPC_JOB_SITE)
         .addTag(PoiTypeTags.ACQUIRABLE_JOB_SITE)
         .add(
            new ResourceKey[]{
               TensuraPoiTypes.BATTLEWILL_TRAINER.getKey(),
               TensuraPoiTypes.GUARD.getKey(),
               TensuraPoiTypes.LUMBERJACK.getKey(),
               TensuraPoiTypes.MAGIC_TRAINER.getKey(),
               TensuraPoiTypes.MINER.getKey()
            }
         );
   }
}
