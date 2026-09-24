package io.github.manasmods.tensura.neoforge.data.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TensuraAttributeTagProvider extends TagsProvider<Attribute> {
   public TensuraAttributeTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
      super(output, Registries.ATTRIBUTE, lookupProvider, "tensura", existingFileHelper);
   }

   protected void addTags(Provider arg) {
   }
}
