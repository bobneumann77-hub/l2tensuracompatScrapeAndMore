package io.github.manasmods.tensura.neoforge.data.tag;

import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.registry.world.TensuraStructures;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class TensuraStructureTagProvider extends StructureTagsProvider {
   public TensuraStructureTagProvider(PackOutput output, CompletableFuture<Provider> completableFuture, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, completableFuture, "tensura", existingFileHelper);
   }

   protected void addTags(Provider arg) {
      this.tag(TensuraTags.Structures.NO_WINGED_CAT).add(BuiltinStructures.END_CITY);
      this.tag(TensuraTags.Structures.ON_PYRAMID_EXPLORER_MAPS).add(BuiltinStructures.DESERT_PYRAMID);
      this.tag(TensuraTags.Structures.ON_CHARYBDIS_EXPLORER_MAPS)
         .add(
            new ResourceKey[]{
               TensuraStructures.CHARYBDIS_CAVE,
               TensuraStructures.CHARYBDIS_CAVE_DESERT,
               TensuraStructures.CHARYBDIS_CAVE_ICE,
               TensuraStructures.CHARYBDIS_CAVE_MESA
            }
         );
      this.tag(TensuraTags.Structures.ON_LABYRINTH_EXPLORER_MAPS).add(TensuraStructures.LABYRINTH_TREE);
      this.tag(TensuraTags.Structures.ON_HELL_GATE_EXPLORER_MAPS).add(TensuraStructures.HELL_GATE);
      this.tag(TensuraTags.Structures.ON_LABYRINTH_EXPLORER_MAPS).add(TensuraStructures.LABYRINTH_TREE);
      this.tag(TensuraTags.Structures.ON_DWARF_VILLAGE_MAPS).add(TensuraStructures.DWARF_VILLAGE);
      this.tag(TensuraTags.Structures.ON_LIZARDMAN_VILLAGE_MAPS)
         .add(new ResourceKey[]{TensuraStructures.LIZARDMAN_VILLAGE_UNDERGROUND, TensuraStructures.LIZARDMAN_VILLAGE_WATER});
   }
}
