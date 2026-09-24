package io.github.manasmods.tensura.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.recipe.input.MiningStationRecipeInput;
import io.github.manasmods.tensura.recipe.misc.RecipeIngredient;
import io.github.manasmods.tensura.recipe.misc.RecipeResult;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record MiningStationRecipe(RecipeIngredient recipeIngredient, List<RecipeResult> recipeResults) implements Recipe<MiningStationRecipeInput> {
   public boolean canCraftInDimensions(int i, int j) {
      return true;
   }

   @Deprecated
   @NotNull
   public ItemStack getResultItem(Provider provider) {
      return ItemStack.EMPTY;
   }

   @Deprecated
   @NotNull
   public ItemStack assemble(MiningStationRecipeInput recipeInput, Provider provider) {
      return ItemStack.EMPTY;
   }

   public boolean matches(MiningStationRecipeInput recipeInput, Level level) {
      return this.recipeIngredient.test(recipeInput.item());
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)TensuraRecipes.MINING_STATION_TYPE.get();
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)TensuraRecipes.MINING_STATION_SERIALIZER.get();
   }

   public NonNullList<ItemStack> getResultItems() {
      NonNullList<ItemStack> list = NonNullList.createWithCapacity(this.recipeResults.size());

      for (RecipeResult recipeResult : this.recipeResults) {
         list.add(recipeResult.getItem().copy());
      }

      return list;
   }

   public NonNullList<ItemStack> getMinMaxResultItems(boolean min) {
      List<RecipeResult> results = this.recipeResults();
      NonNullList<ItemStack> itemList = NonNullList.createWithCapacity(results.size());

      for (RecipeResult result : results) {
         ItemStack itemStack = result.getItem().copy();
         itemStack.setCount(min ? result.getMin() : result.getMax());
         itemList.add(itemStack);
      }

      return itemList;
   }

   public NonNullList<ItemStack> getRandomResultItems() {
      return this.getRandomResultItems(null);
   }

   public NonNullList<ItemStack> getRandomResultItems(RandomSource random) {
      List<RecipeResult> results = this.recipeResults();
      NonNullList<ItemStack> itemList = NonNullList.createWithCapacity(results.size());

      for (RecipeResult result : results) {
         ItemStack itemStack = result.getItem().copy();
         itemStack.setCount(result.getAmount(random));
         itemList.add(itemStack);
      }

      return itemList;
   }

   public static class Builder {
      private final List<RecipeIngredient> recipeIngredients;
      private final List<RecipeResult> recipeResults = new ArrayList<>();

      public static MiningStationRecipe.Builder of(ItemLike ingredient) {
         return of(ingredient, 1);
      }

      public static MiningStationRecipe.Builder of(ItemLike ingredient, int ingredientAmount) {
         return of(Ingredient.of(new ItemLike[]{ingredient}), ingredientAmount);
      }

      public static MiningStationRecipe.Builder of(ItemStack ingredient) {
         return of(ingredient, 1);
      }

      public static MiningStationRecipe.Builder of(ItemStack ingredient, int ingredientAmount) {
         return of(Ingredient.of(new ItemStack[]{ingredient}), ingredientAmount);
      }

      public static MiningStationRecipe.Builder of(Ingredient ingredient) {
         return of(ingredient, 1);
      }

      public static MiningStationRecipe.Builder of(Ingredient ingredient, int ingredientAmount) {
         return of(new RecipeIngredient[]{RecipeIngredient.create(ingredient, ingredientAmount)});
      }

      public static MiningStationRecipe.Builder of(TagKey<Item> ingredient) {
         return of(ingredient, 1);
      }

      public static MiningStationRecipe.Builder of(TagKey<Item> ingredient, int ingredientAmount) {
         return of(new RecipeIngredient[]{RecipeIngredient.create(ingredient, ingredientAmount)});
      }

      public static MiningStationRecipe.Builder of(ItemLike... ingredients) {
         return of(Arrays.stream(ingredients).map(RecipeIngredient::create).toList());
      }

      public static MiningStationRecipe.Builder of(ItemStack... ingredients) {
         return of(Arrays.stream(ingredients).map(RecipeIngredient::create).toList());
      }

      public static MiningStationRecipe.Builder of(RecipeIngredient... ingredients) {
         return of(Arrays.stream(ingredients).toList());
      }

      public MiningStationRecipe.Builder addResult(Item result) {
         return this.addResult(result, 1);
      }

      public MiningStationRecipe.Builder addResult(Item result, int amount) {
         return this.addResult(result, amount, 1.0F);
      }

      public MiningStationRecipe.Builder addResult(Item result, float chance) {
         return this.addResult(result, 1, chance);
      }

      public MiningStationRecipe.Builder addResult(Item result, int amount, float chance) {
         return this.addResult(result, amount, amount, chance);
      }

      public MiningStationRecipe.Builder addResult(Item result, int minInclusive, int maxInclusive) {
         return this.addResult(result, minInclusive, maxInclusive, 1.0F);
      }

      public MiningStationRecipe.Builder addResult(Item result, int minInclusive, int maxInclusive, float chance) {
         return this.addResult(RecipeResult.inclusiveRange(result, minInclusive, maxInclusive, chance));
      }

      public MiningStationRecipe.Builder addResult(ItemStack result) {
         return this.addResult(result, 1);
      }

      public MiningStationRecipe.Builder addResult(ItemStack result, int amount) {
         return this.addResult(result, amount, 1.0F);
      }

      public MiningStationRecipe.Builder addResult(ItemStack result, float chance) {
         return this.addResult(result, 1, chance);
      }

      public MiningStationRecipe.Builder addResult(ItemStack result, int amount, float chance) {
         return this.addResult(result, amount, amount, chance);
      }

      public MiningStationRecipe.Builder addResult(ItemStack result, int minInclusive, int maxInclusive) {
         return this.addResult(result, minInclusive, maxInclusive, 1.0F);
      }

      public MiningStationRecipe.Builder addResult(ItemStack result, int minInclusive, int maxInclusive, float chance) {
         return this.addResult(RecipeResult.inclusiveRange(result, minInclusive, maxInclusive, chance));
      }

      public MiningStationRecipe.Builder addResult(RegistrySupplier<Item> result) {
         return this.addResult((Item)result.get());
      }

      public MiningStationRecipe.Builder addResult(RegistrySupplier<Item> result, int amount) {
         return this.addResult((Item)result.get(), amount);
      }

      public MiningStationRecipe.Builder addResult(RegistrySupplier<Item> result, float chance) {
         return this.addResult((Item)result.get(), chance);
      }

      public MiningStationRecipe.Builder addResult(RegistrySupplier<Item> result, int amount, float chance) {
         return this.addResult((Item)result.get(), amount, chance);
      }

      public MiningStationRecipe.Builder addResult(RegistrySupplier<Item> result, int minInclusive, int maxInclusive) {
         return this.addResult((Item)result.get(), minInclusive, maxInclusive);
      }

      public MiningStationRecipe.Builder addResult(RegistrySupplier<Item> result, int minInclusive, int maxInclusive, float chance) {
         return this.addResult((Item)result.get(), minInclusive, maxInclusive, chance);
      }

      public MiningStationRecipe.Builder addResult(RecipeResult... results) {
         if (results.length <= 9 && this.recipeResults.size() <= 9) {
            for (RecipeResult recipeResult : results) {
               if (!this.recipeResults.contains(recipeResult)) {
                  this.recipeResults.add(recipeResult);
               }
            }

            return this;
         } else {
            throw new IllegalStateException("MiningStationRecipes only allow up to 9 results");
         }
      }

      public void build(RecipeOutput recipeOutput) {
         if (this.recipeIngredients.isEmpty()) {
            throw new IllegalStateException("MiningStationRecipe requires at least one ingredient");
         }

         for (RecipeIngredient recipeIngredient : this.recipeIngredients) {
            this.build(recipeOutput, recipeIngredient);
         }
      }

      public void build(RecipeOutput recipeOutput, ResourceLocation id) {
         if (this.recipeIngredients.isEmpty()) {
            throw new IllegalStateException(String.format("Recipe %s requires at least one ingredient", id));
         }

         for (int index = 0; index < this.recipeIngredients.size(); index++) {
            int j = index;
            ResourceLocation location = ResourceLocation.parse(id.toString() + (index == 0 ? "" : String.format("_%d", index)));
            SpecialRecipeBuilder.special(category -> new MiningStationRecipe(this.recipeIngredients.get(j), this.recipeResults)).save(recipeOutput, location);
         }
      }

      private void build(RecipeOutput recipeOutput, RecipeIngredient recipeIngredient) {
         ResourceLocation location = ResourceLocation.fromNamespaceAndPath("tensura", "mining_station/" + recipeIngredient.getId());
         if (recipeIngredient.isEmpty()) {
            throw new IllegalStateException(String.format("Recipe %s requires a valid ingredient", location));
         }

         SpecialRecipeBuilder.special(category -> new MiningStationRecipe(recipeIngredient, this.recipeResults)).save(recipeOutput, location);
      }

      @Generated
      private Builder(List<RecipeIngredient> recipeIngredients) {
         this.recipeIngredients = recipeIngredients;
      }

      @Generated
      public static MiningStationRecipe.Builder of(List<RecipeIngredient> recipeIngredients) {
         return new MiningStationRecipe.Builder(recipeIngredients);
      }
   }

   public static class Serializer implements RecipeSerializer<MiningStationRecipe> {
      private static final MapCodec<MiningStationRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               RecipeIngredient.CODEC.fieldOf("ingredient").forGetter(MiningStationRecipe::recipeIngredient),
               RecipeResult.CODEC.listOf().fieldOf("results").forGetter(MiningStationRecipe::recipeResults)
            )
            .apply(instance, MiningStationRecipe::new)
      );

      @NotNull
      public MapCodec<MiningStationRecipe> codec() {
         return CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, MiningStationRecipe> streamCodec() {
         return new StreamCodec<RegistryFriendlyByteBuf, MiningStationRecipe>() {
            @NotNull
            public MiningStationRecipe decode(RegistryFriendlyByteBuf buf) {
               RecipeIngredient ingredient = (RecipeIngredient)RecipeIngredient.STREAM_CODEC.decode(buf);
               List<RecipeResult> results = (List<RecipeResult>)RecipeResult.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
               return new MiningStationRecipe(ingredient, results);
            }

            public void encode(RegistryFriendlyByteBuf buf, MiningStationRecipe recipe) {
               RecipeIngredient.STREAM_CODEC.encode(buf, recipe.recipeIngredient());
               RecipeResult.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.recipeResults());
            }
         };
      }
   }

   public static class Type implements RecipeType<MiningStationRecipe> {
   }
}
