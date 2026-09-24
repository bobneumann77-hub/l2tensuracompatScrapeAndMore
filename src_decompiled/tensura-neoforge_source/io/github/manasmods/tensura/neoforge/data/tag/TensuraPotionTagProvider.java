package io.github.manasmods.tensura.neoforge.data.tag;

import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.registry.effect.TensuraPotions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class TensuraPotionTagProvider extends TagsProvider<Potion> {
   public TensuraPotionTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
      super(output, Registries.POTION, lookupProvider, "tensura", existingFileHelper);
   }

   protected void addTags(@NotNull Provider provider) {
      this.tag(TensuraTags.Potions.DWARF_ALCHEMIST)
         .add(
            new ResourceKey[]{
               Potions.MUNDANE.getKey(),
               Potions.THICK.getKey(),
               Potions.AWKWARD.getKey(),
               Potions.NIGHT_VISION.getKey(),
               Potions.LONG_NIGHT_VISION.getKey(),
               Potions.INVISIBILITY.getKey(),
               Potions.LONG_INVISIBILITY.getKey(),
               Potions.LEAPING.getKey(),
               Potions.LONG_LEAPING.getKey(),
               Potions.STRONG_LEAPING.getKey(),
               Potions.FIRE_RESISTANCE.getKey(),
               Potions.LONG_FIRE_RESISTANCE.getKey(),
               Potions.SWIFTNESS.getKey(),
               Potions.LONG_SWIFTNESS.getKey(),
               Potions.STRONG_SWIFTNESS.getKey(),
               Potions.SLOWNESS.getKey(),
               Potions.LONG_SLOWNESS.getKey(),
               Potions.STRONG_SLOWNESS.getKey(),
               Potions.WATER_BREATHING.getKey(),
               Potions.LONG_WATER_BREATHING.getKey(),
               Potions.HEALING.getKey(),
               Potions.STRONG_HEALING.getKey(),
               Potions.HARMING.getKey(),
               Potions.STRONG_HARMING.getKey(),
               Potions.POISON.getKey(),
               Potions.LONG_POISON.getKey(),
               Potions.STRONG_POISON.getKey(),
               Potions.REGENERATION.getKey(),
               Potions.LONG_REGENERATION.getKey(),
               Potions.STRONG_REGENERATION.getKey(),
               Potions.STRENGTH.getKey(),
               Potions.LONG_STRENGTH.getKey(),
               Potions.STRONG_STRENGTH.getKey(),
               Potions.WEAKNESS.getKey(),
               Potions.LONG_WEAKNESS.getKey(),
               Potions.LUCK.getKey(),
               Potions.SLOW_FALLING.getKey(),
               Potions.LONG_SLOW_FALLING.getKey(),
               TensuraPotions.CHILL.getKey(),
               TensuraPotions.LONG_CHILL.getKey(),
               TensuraPotions.LONG_CHILL.getKey(),
               TensuraPotions.GLOWING.getKey(),
               TensuraPotions.LONG_GLOWING.getKey()
            }
         );
      this.tag(TensuraTags.Potions.DWARF_FLETCHER)
         .add(
            new ResourceKey[]{
               Potions.SLOWNESS.getKey(),
               Potions.LONG_SLOWNESS.getKey(),
               Potions.STRONG_SLOWNESS.getKey(),
               Potions.HARMING.getKey(),
               Potions.STRONG_HARMING.getKey(),
               Potions.POISON.getKey(),
               Potions.LONG_POISON.getKey(),
               Potions.STRONG_POISON.getKey(),
               Potions.WEAKNESS.getKey(),
               Potions.LONG_WEAKNESS.getKey(),
               TensuraPotions.CHILL.getKey(),
               TensuraPotions.LONG_CHILL.getKey(),
               TensuraPotions.LONG_CHILL.getKey(),
               TensuraPotions.GLOWING.getKey(),
               TensuraPotions.LONG_GLOWING.getKey()
            }
         );
   }
}
