package io.github.manasmods.tensura.registry.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.recipe.KilnMeltingRecipe;
import io.github.manasmods.tensura.recipe.KilnMixingRecipe;
import io.github.manasmods.tensura.recipe.MiningStationRecipe;
import io.github.manasmods.tensura.recipe.RefiningRecipe;
import io.github.manasmods.tensura.recipe.SmithingBenchRecipe;
import io.github.manasmods.tensura.recipe.WoodcutterRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe.Serializer;

public class TensuraRecipes {
   private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create("tensura", Registries.RECIPE_TYPE);
   public static final RegistrySupplier<RecipeType<KilnMeltingRecipe>> KILN_MELTING_TYPE = RECIPE_TYPES.register("kiln_melting", KilnMeltingRecipe.Type::new);
   public static final RegistrySupplier<RecipeType<KilnMixingRecipe>> KILN_MIXING_TYPE = RECIPE_TYPES.register("kiln_mixing", KilnMixingRecipe.Type::new);
   public static final RegistrySupplier<RecipeType<RefiningRecipe>> REFINING_TYPE = RECIPE_TYPES.register("refining", RefiningRecipe.Type::new);
   public static final RegistrySupplier<RecipeType<SmithingBenchRecipe>> SMITHING_BENCH_TYPE = RECIPE_TYPES.register(
      "smithing_bench", SmithingBenchRecipe.Type::new
   );
   public static final RegistrySupplier<RecipeType<MiningStationRecipe>> MINING_STATION_TYPE = RECIPE_TYPES.register(
      "mining_station", MiningStationRecipe.Type::new
   );
   public static final RegistrySupplier<RecipeType<WoodcutterRecipe>> WOOD_CUTTER_TYPE = RECIPE_TYPES.register("woodcutting", WoodcutterRecipe.Type::new);
   private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create("tensura", Registries.RECIPE_SERIALIZER);
   public static final RegistrySupplier<RecipeSerializer<KilnMeltingRecipe>> KILN_MELTING_SERIALIZER = RECIPE_SERIALIZERS.register(
      "kiln_melting", KilnMeltingRecipe.Serializer::new
   );
   public static final RegistrySupplier<RecipeSerializer<KilnMixingRecipe>> KILN_MIXING_SERIALIZER = RECIPE_SERIALIZERS.register(
      "kiln_mixing", KilnMixingRecipe.Serializer::new
   );
   public static final RegistrySupplier<RecipeSerializer<RefiningRecipe>> REFINING_SERIALIZER = RECIPE_SERIALIZERS.register(
      "refining", RefiningRecipe.Serializer::new
   );
   public static final RegistrySupplier<RecipeSerializer<SmithingBenchRecipe>> SMITHING_BENCH_SERIALIZER = RECIPE_SERIALIZERS.register(
      "smithing_bench", SmithingBenchRecipe.Serializer::new
   );
   public static final RegistrySupplier<RecipeSerializer<MiningStationRecipe>> MINING_STATION_SERIALIZER = RECIPE_SERIALIZERS.register(
      "mining_station", MiningStationRecipe.Serializer::new
   );
   public static final RegistrySupplier<RecipeSerializer<WoodcutterRecipe>> WOOD_CUTTER_SERIALIZER = RECIPE_SERIALIZERS.register(
      "woodcutting", () -> new Serializer(WoodcutterRecipe::new)
   );

   public static void init() {
      RECIPE_TYPES.register();
      RECIPE_SERIALIZERS.register();
   }
}
