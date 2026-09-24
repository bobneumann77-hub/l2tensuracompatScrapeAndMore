package io.github.manasmods.tensura.recipe;

import io.github.manasmods.tensura.registry.effect.TensuraPotions;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;

public class SpecialRecipeRegister {
   public static void registerBrewingRecipe(SpecialRecipeRegister.BrewingRegisterStrategy strategy) {
      strategy.registerStart((Item)TensuraConsumableItems.CHILLED_SLIME.get(), TensuraPotions.getReference(TensuraPotions.CHILL));
      strategy.registerMix(TensuraPotions.getReference(TensuraPotions.CHILL), Items.REDSTONE, TensuraPotions.getReference(TensuraPotions.LONG_CHILL));
      strategy.registerMix(TensuraPotions.getReference(TensuraPotions.CHILL), Items.GLOWSTONE_DUST, TensuraPotions.getReference(TensuraPotions.STRONG_CHILL));
      strategy.registerStart((Item)TensuraConsumableItems.RAW_SERPENT_MEAT.get(), TensuraPotions.getReference(TensuraPotions.CORROSION));
      strategy.registerMix(TensuraPotions.getReference(TensuraPotions.CORROSION), Items.REDSTONE, TensuraPotions.getReference(TensuraPotions.LONG_CORROSION));
      strategy.registerMix(
         TensuraPotions.getReference(TensuraPotions.CORROSION), Items.GLOWSTONE_DUST, TensuraPotions.getReference(TensuraPotions.STRONG_CORROSION)
      );
      strategy.registerStart((Item)TensuraMobDropItems.SPIDER_FANG.get(), TensuraPotions.getReference(TensuraPotions.FATAL_POISON));
      strategy.registerMix(
         TensuraPotions.getReference(TensuraPotions.FATAL_POISON), Items.REDSTONE, TensuraPotions.getReference(TensuraPotions.LONG_FATAL_POISON)
      );
      strategy.registerMix(
         TensuraPotions.getReference(TensuraPotions.FATAL_POISON), Items.GLOWSTONE_DUST, TensuraPotions.getReference(TensuraPotions.STRONG_FATAL_POISON)
      );
      strategy.registerStart((Item)TensuraConsumableItems.GIANT_ANT_LEG.get(), TensuraPotions.getReference(TensuraPotions.FRAGILITY));
      strategy.registerMix(TensuraPotions.getReference(TensuraPotions.FRAGILITY), Items.REDSTONE, TensuraPotions.getReference(TensuraPotions.LONG_FRAGILITY));
      strategy.registerMix(
         TensuraPotions.getReference(TensuraPotions.FRAGILITY), Items.GLOWSTONE_DUST, TensuraPotions.getReference(TensuraPotions.STRONG_FRAGILITY)
      );
      strategy.registerStart((Item)TensuraMaterialItems.BAFFLEDIL.get(), TensuraPotions.getReference(TensuraPotions.HYPNOSIS));
      strategy.registerMix(TensuraPotions.getReference(TensuraPotions.HYPNOSIS), Items.REDSTONE, TensuraPotions.getReference(TensuraPotions.LONG_HYPNOSIS));
      strategy.registerMix(
         TensuraPotions.getReference(TensuraPotions.HYPNOSIS), Items.GLOWSTONE_DUST, TensuraPotions.getReference(TensuraPotions.STRONG_HYPNOSIS)
      );
      strategy.registerMix(
         TensuraPotions.getReference(TensuraPotions.HYPNOSIS), Items.FERMENTED_SPIDER_EYE, TensuraPotions.getReference(TensuraPotions.HYPNOTIC_EFFICIENCY)
      );
      strategy.registerMix(
         TensuraPotions.getReference(TensuraPotions.LONG_HYPNOSIS),
         Items.FERMENTED_SPIDER_EYE,
         TensuraPotions.getReference(TensuraPotions.LONG_HYPNOTIC_EFFICIENCY)
      );
      strategy.registerMix(
         TensuraPotions.getReference(TensuraPotions.STRONG_HYPNOSIS),
         Items.FERMENTED_SPIDER_EYE,
         TensuraPotions.getReference(TensuraPotions.STRONG_HYPNOTIC_EFFICIENCY)
      );
      strategy.registerMix(
         TensuraPotions.getReference(TensuraPotions.HYPNOTIC_EFFICIENCY), Items.REDSTONE, TensuraPotions.getReference(TensuraPotions.LONG_HYPNOTIC_EFFICIENCY)
      );
      strategy.registerMix(
         TensuraPotions.getReference(TensuraPotions.HYPNOTIC_EFFICIENCY),
         Items.GLOWSTONE_DUST,
         TensuraPotions.getReference(TensuraPotions.STRONG_HYPNOTIC_EFFICIENCY)
      );
      strategy.registerStart((Item)TensuraMobDropItems.INVISIBLE_FEATHER.get(), TensuraPotions.getReference(TensuraPotions.NIGHT_OWL));
      strategy.registerMix(TensuraPotions.getReference(TensuraPotions.NIGHT_OWL), Items.REDSTONE, TensuraPotions.getReference(TensuraPotions.LONG_NIGHT_OWL));
      strategy.registerStart((Item)TensuraMobDropItems.DRAGON_PEACOCK_FEATHER.get(), TensuraPotions.getReference(TensuraPotions.GLOWING));
      strategy.registerMix(TensuraPotions.getReference(TensuraPotions.GLOWING), Items.REDSTONE, TensuraPotions.getReference(TensuraPotions.LONG_GLOWING));
      strategy.registerStart((Item)TensuraMobDropItems.CENTIPEDE_STINGER.get(), TensuraPotions.getReference(TensuraPotions.PARALYSIS));
      strategy.registerMix(TensuraPotions.getReference(TensuraPotions.PARALYSIS), Items.REDSTONE, TensuraPotions.getReference(TensuraPotions.LONG_PARALYSIS));
      strategy.registerMix(
         TensuraPotions.getReference(TensuraPotions.PARALYSIS), Items.GLOWSTONE_DUST, TensuraPotions.getReference(TensuraPotions.STRONG_PARALYSIS)
      );
      strategy.registerContainer((Item)TensuraConsumableItems.WATER_MAGIC_BOTTLE.get());
      strategy.registerContainer((Item)TensuraConsumableItems.VACUUMED_WATER_MAGIC_BOTTLE.get());
      strategy.registerContainerMix(
         (Item)TensuraConsumableItems.WATER_MAGIC_BOTTLE.get(), (Item)TensuraMaterialItems.HIPOKUTE_GRASS.get(), (Item)TensuraConsumableItems.LOW_POTION.get()
      );
      strategy.registerContainerMix(
         (Item)TensuraConsumableItems.VACUUMED_WATER_MAGIC_BOTTLE.get(),
         (Item)TensuraMaterialItems.HIPOKUTE_GRASS.get(),
         (Item)TensuraConsumableItems.HIGH_POTION.get()
      );
      strategy.registerContainerMix(
         (Item)TensuraConsumableItems.WATER_MAGIC_BOTTLE.get(),
         (Item)TensuraMaterialItems.HIPOKUTE_FLOWER.get(),
         (Item)TensuraConsumableItems.HIGH_POTION.get()
      );
      strategy.registerContainerMix(
         (Item)TensuraConsumableItems.VACUUMED_WATER_MAGIC_BOTTLE.get(),
         (Item)TensuraMaterialItems.HIPOKUTE_FLOWER.get(),
         (Item)TensuraConsumableItems.FULL_POTION.get()
      );
      strategy.registerContainer((Item)TensuraConsumableItems.LOW_POTION.get());
      strategy.registerContainer((Item)TensuraConsumableItems.HIGH_POTION.get());
      strategy.registerContainer((Item)TensuraConsumableItems.FULL_POTION.get());
      strategy.registerContainerMix(
         (Item)TensuraConsumableItems.LOW_POTION.get(), (Item)TensuraMobDropItems.DAEMON_ESSENCE.get(), (Item)TensuraConsumableItems.LOW_ARCANE_POTION.get()
      );
      strategy.registerContainerMix(
         (Item)TensuraConsumableItems.HIGH_POTION.get(),
         (Item)TensuraMobDropItems.DAEMON_ESSENCE.get(),
         (Item)TensuraConsumableItems.MEDIUM_ARCANE_POTION.get()
      );
      strategy.registerContainerMix(
         (Item)TensuraConsumableItems.FULL_POTION.get(), (Item)TensuraMobDropItems.DAEMON_ESSENCE.get(), (Item)TensuraConsumableItems.HIGH_ARCANE_POTION.get()
      );
   }

   public interface BrewingRegisterStrategy {
      void registerStart(Item var1, Holder<Potion> var2);

      void registerMix(Holder<Potion> var1, Item var2, Holder<Potion> var3);

      void registerContainer(Item var1);

      void registerContainerMix(Item var1, Item var2, Item var3);
   }
}
