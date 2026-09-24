package io.github.manasmods.tensura.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.recipe.input.SmithingBenchRecipeInput;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SmithingBenchRecipe implements Recipe<SmithingBenchRecipeInput> {
   private final Ingredient ingredient1;
   private final Ingredient ingredient2;
   private final Ingredient ingredient3;
   private final Ingredient ingredient4;
   private final Ingredient ingredient5;
   private final int amount1;
   private final int amount2;
   private final int amount3;
   private final int amount4;
   private final int amount5;
   private final List<ResourceLocation> requiredSchematics;
   private final ItemStack output;

   public boolean matches(SmithingBenchRecipeInput recipeInput, Level level) {
      Container container = recipeInput.container();
      return this.hasEnough(container, this.ingredient1, this.amount1)
         && this.hasEnough(container, this.ingredient2, this.amount2)
         && this.hasEnough(container, this.ingredient3, this.amount3)
         && this.hasEnough(container, this.ingredient4, this.amount4)
         && this.hasEnough(container, this.ingredient5, this.amount5);
   }

   public boolean hasUnlocked(SmithingBenchRecipeInput recipeInput) {
      return recipeInput.creative() ? true : new HashSet<>(recipeInput.learntSchematics()).containsAll(this.requiredSchematics);
   }

   private boolean hasEnough(Container container, Ingredient ingredient, int amount) {
      if (ingredient.isEmpty()) {
         return true;
      }

      int remainingAmount = amount;
      if (remainingAmount <= 0) {
         return true;
      }

      for (int i = 0; i < container.getContainerSize(); i++) {
         ItemStack item = container.getItem(i);
         if (ingredient.test(item)) {
            remainingAmount -= item.getCount();
         }
      }

      return remainingAmount <= 0;
   }

   public ItemStack assemble(SmithingBenchRecipeInput recipeInput, Provider provider) {
      return this.getResultItem(provider);
   }

   public ItemStack getResultItem(Provider provider) {
      return this.getOutput().copy();
   }

   public void takeItemsFrom(Container container) {
      for (int i = 0; i < 5; i++) {
         Ingredient ingredient = (Ingredient)this.getIngredients().get(i);
         if (!ingredient.isEmpty()) {
            int amount = (Integer)this.getIngredientAmount().get(i);
            if (amount == 0) {
               break;
            }

            for (int j = 0; j < container.getContainerSize(); j++) {
               ItemStack inventoryStack = container.getItem(j);
               if (!inventoryStack.isEmpty() && ingredient.test(inventoryStack)) {
                  if (amount - inventoryStack.getCount() < 0) {
                     inventoryStack.shrink(amount);
                     amount = 0;
                  } else {
                     amount -= inventoryStack.getCount();
                     inventoryStack.setCount(0);
                  }
               }
            }
         }
      }
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return NonNullList.of(Ingredient.EMPTY, new Ingredient[]{this.ingredient1, this.ingredient2, this.ingredient3, this.ingredient4, this.ingredient5});
   }

   public NonNullList<Integer> getIngredientAmount() {
      return NonNullList.of(0, new Integer[]{this.amount1, this.amount2, this.amount3, this.amount4, this.amount5});
   }

   public static Comparator<RecipeHolder<SmithingBenchRecipe>> getComparator() {
      return Comparator.comparingInt(SmithingBenchRecipe::getOrder);
   }

   private static int getOrder(RecipeHolder<SmithingBenchRecipe> recipe) {
      return BuiltInRegistries.ITEM.getId(((SmithingBenchRecipe)recipe.value()).getOutput().getItem());
   }

   public boolean canCraftInDimensions(int i, int j) {
      return true;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)TensuraRecipes.SMITHING_BENCH_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)TensuraRecipes.SMITHING_BENCH_TYPE.get();
   }

   @Generated
   public Ingredient getIngredient1() {
      return this.ingredient1;
   }

   @Generated
   public Ingredient getIngredient2() {
      return this.ingredient2;
   }

   @Generated
   public Ingredient getIngredient3() {
      return this.ingredient3;
   }

   @Generated
   public Ingredient getIngredient4() {
      return this.ingredient4;
   }

   @Generated
   public Ingredient getIngredient5() {
      return this.ingredient5;
   }

   @Generated
   public int getAmount1() {
      return this.amount1;
   }

   @Generated
   public int getAmount2() {
      return this.amount2;
   }

   @Generated
   public int getAmount3() {
      return this.amount3;
   }

   @Generated
   public int getAmount4() {
      return this.amount4;
   }

   @Generated
   public int getAmount5() {
      return this.amount5;
   }

   @Generated
   public List<ResourceLocation> getRequiredSchematics() {
      return this.requiredSchematics;
   }

   @Generated
   public ItemStack getOutput() {
      return this.output;
   }

   @Generated
   @Override
   public String toString() {
      return "SmithingBenchRecipe(ingredient1="
         + this.getIngredient1()
         + ", ingredient2="
         + this.getIngredient2()
         + ", ingredient3="
         + this.getIngredient3()
         + ", ingredient4="
         + this.getIngredient4()
         + ", ingredient5="
         + this.getIngredient5()
         + ", amount1="
         + this.getAmount1()
         + ", amount2="
         + this.getAmount2()
         + ", amount3="
         + this.getAmount3()
         + ", amount4="
         + this.getAmount4()
         + ", amount5="
         + this.getAmount5()
         + ", requiredSchematics="
         + this.getRequiredSchematics()
         + ", output="
         + this.getOutput()
         + ")";
   }

   @Generated
   public SmithingBenchRecipe(
      Ingredient ingredient1,
      Ingredient ingredient2,
      Ingredient ingredient3,
      Ingredient ingredient4,
      Ingredient ingredient5,
      int amount1,
      int amount2,
      int amount3,
      int amount4,
      int amount5,
      List<ResourceLocation> requiredSchematics,
      ItemStack output
   ) {
      this.ingredient1 = ingredient1;
      this.ingredient2 = ingredient2;
      this.ingredient3 = ingredient3;
      this.ingredient4 = ingredient4;
      this.ingredient5 = ingredient5;
      this.amount1 = amount1;
      this.amount2 = amount2;
      this.amount3 = amount3;
      this.amount4 = amount4;
      this.amount5 = amount5;
      this.requiredSchematics = requiredSchematics;
      this.output = output;
   }

   public static class Builder {
      private final ItemStack result;
      private final List<ResourceLocation> schematics = new ArrayList<>();
      private final NonNullList<Pair<Ingredient, Integer>> ingredientMap = NonNullList.withSize(5, Pair.of(Ingredient.EMPTY, 0));
      private int ingredientAmount = 0;

      public static SmithingBenchRecipe.Builder of(Item result, int amount) {
         return of(new ItemStack(result, amount));
      }

      public static SmithingBenchRecipe.Builder of(Item result) {
         return of(result.getDefaultInstance());
      }

      public static SmithingBenchRecipe.Builder of(Supplier<? extends Item> result) {
         return of(result.get());
      }

      public SmithingBenchRecipe.Builder addIngredient(Ingredient ingredient, int amount) {
         if (this.ingredientAmount + 1 > 5) {
            throw new IllegalStateException("SmithingBenchRecipes only allow up to 5 ingredient");
         }

         this.ingredientMap.set(this.ingredientAmount++, Pair.of(ingredient, amount));
         return this;
      }

      public SmithingBenchRecipe.Builder addIngredient(ItemStack stack) {
         return this.addIngredient(Ingredient.of(new ItemStack[]{stack}), stack.getCount());
      }

      public SmithingBenchRecipe.Builder addIngredient(TagKey<Item> itemTagKey, int amount) {
         return this.addIngredient(Ingredient.of(itemTagKey), amount);
      }

      public SmithingBenchRecipe.Builder addIngredient(Supplier<? extends Item> item, int amount) {
         return this.addIngredient(item.get(), amount);
      }

      public SmithingBenchRecipe.Builder addIngredient(Item item, int amount) {
         return this.addIngredient(new ItemStack(item, amount));
      }

      public SmithingBenchRecipe.Builder addIngredient(Item item) {
         return this.addIngredient(item, 1);
      }

      public SmithingBenchRecipe.Builder requiresSchematic(ResourceLocation schematic) {
         this.schematics.add(schematic);
         return this;
      }

      public SmithingBenchRecipe.Builder requiresSchematic(Supplier<? extends Item> schematic) {
         return this.requiresSchematic(schematic.get());
      }

      public SmithingBenchRecipe.Builder requiresSchematic(Item schematic) {
         return this.requiresSchematic(BuiltInRegistries.ITEM.getKey(schematic));
      }

      public void build(RecipeOutput recipeOutput, ResourceLocation id) {
         if (this.ingredientAmount == 0) {
            throw new IllegalStateException(String.format("Recipe %s need at least 1 ingredient", id));
         }

         SpecialRecipeBuilder.special(
               category -> new SmithingBenchRecipe(
                  (Ingredient)((Pair)this.ingredientMap.get(0)).getFirst(),
                  (Ingredient)((Pair)this.ingredientMap.get(1)).getFirst(),
                  (Ingredient)((Pair)this.ingredientMap.get(2)).getFirst(),
                  (Ingredient)((Pair)this.ingredientMap.get(3)).getFirst(),
                  (Ingredient)((Pair)this.ingredientMap.get(4)).getFirst(),
                  (Integer)((Pair)this.ingredientMap.get(0)).getSecond(),
                  (Integer)((Pair)this.ingredientMap.get(1)).getSecond(),
                  (Integer)((Pair)this.ingredientMap.get(2)).getSecond(),
                  (Integer)((Pair)this.ingredientMap.get(3)).getSecond(),
                  (Integer)((Pair)this.ingredientMap.get(4)).getSecond(),
                  this.schematics.stream().distinct().toList(),
                  this.result
               )
            )
            .save(recipeOutput, id);
      }

      public void build(RecipeOutput recipeOutput, String suffix) {
         ResourceLocation location = BuiltInRegistries.ITEM.getKey(this.result.getItem());
         this.build(recipeOutput, ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "smithing/" + location.getPath() + "_" + suffix));
      }

      public void build(RecipeOutput recipeOutput) {
         ResourceLocation location = BuiltInRegistries.ITEM.getKey(this.result.getItem());
         this.build(recipeOutput, ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "smithing/" + location.getPath()));
      }

      @Generated
      private Builder(ItemStack result) {
         this.result = result;
      }

      @Generated
      public static SmithingBenchRecipe.Builder of(ItemStack result) {
         return new SmithingBenchRecipe.Builder(result);
      }
   }

   public static class Serializer implements RecipeSerializer<SmithingBenchRecipe> {
      private static final MapCodec<SmithingBenchRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC_NONEMPTY.fieldOf("input1").forGetter(SmithingBenchRecipe::getIngredient1),
               Ingredient.CODEC_NONEMPTY.optionalFieldOf("input2", Ingredient.EMPTY).forGetter(SmithingBenchRecipe::getIngredient2),
               Ingredient.CODEC_NONEMPTY.optionalFieldOf("input3", Ingredient.EMPTY).forGetter(SmithingBenchRecipe::getIngredient3),
               Ingredient.CODEC_NONEMPTY.optionalFieldOf("input4", Ingredient.EMPTY).forGetter(SmithingBenchRecipe::getIngredient4),
               Ingredient.CODEC_NONEMPTY.optionalFieldOf("input5", Ingredient.EMPTY).forGetter(SmithingBenchRecipe::getIngredient5),
               Codec.INT.optionalFieldOf("inputAmount1", 1).forGetter(SmithingBenchRecipe::getAmount1),
               Codec.INT.optionalFieldOf("inputAmount2", 0).forGetter(SmithingBenchRecipe::getAmount2),
               Codec.INT.optionalFieldOf("inputAmount3", 0).forGetter(SmithingBenchRecipe::getAmount3),
               Codec.INT.optionalFieldOf("inputAmount4", 0).forGetter(SmithingBenchRecipe::getAmount4),
               Codec.INT.optionalFieldOf("inputAmount5", 0).forGetter(SmithingBenchRecipe::getAmount5),
               ResourceLocation.CODEC.listOf().fieldOf("schematics").forGetter(SmithingBenchRecipe::getRequiredSchematics),
               ItemStack.CODEC.fieldOf("output").forGetter(SmithingBenchRecipe::getOutput)
            )
            .apply(instance, SmithingBenchRecipe::new)
      );

      public MapCodec<SmithingBenchRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, SmithingBenchRecipe> streamCodec() {
         return new StreamCodec<RegistryFriendlyByteBuf, SmithingBenchRecipe>() {
            public SmithingBenchRecipe decode(RegistryFriendlyByteBuf buf) {
               Ingredient ingredient1 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
               Ingredient ingredient2 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
               Ingredient ingredient3 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
               Ingredient ingredient4 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
               Ingredient ingredient5 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
               int amount1 = (Integer)ByteBufCodecs.VAR_INT.decode(buf);
               int amount2 = (Integer)ByteBufCodecs.VAR_INT.decode(buf);
               int amount3 = (Integer)ByteBufCodecs.VAR_INT.decode(buf);
               int amount4 = (Integer)ByteBufCodecs.VAR_INT.decode(buf);
               int amount5 = (Integer)ByteBufCodecs.VAR_INT.decode(buf);
               List<ResourceLocation> schematics = (List<ResourceLocation>)ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
               ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
               return new SmithingBenchRecipe(
                  ingredient1, ingredient2, ingredient3, ingredient4, ingredient5, amount1, amount2, amount3, amount4, amount5, schematics, output
               );
            }

            public void encode(RegistryFriendlyByteBuf buf, SmithingBenchRecipe recipe) {
               Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient1());
               Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient2());
               Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient3());
               Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient4());
               Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient5());
               ByteBufCodecs.VAR_INT.encode(buf, recipe.getAmount1());
               ByteBufCodecs.VAR_INT.encode(buf, recipe.getAmount2());
               ByteBufCodecs.VAR_INT.encode(buf, recipe.getAmount3());
               ByteBufCodecs.VAR_INT.encode(buf, recipe.getAmount4());
               ByteBufCodecs.VAR_INT.encode(buf, recipe.getAmount5());
               ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.getRequiredSchematics());
               ItemStack.STREAM_CODEC.encode(buf, recipe.getOutput());
            }
         };
      }
   }

   public static class Type implements RecipeType<SmithingBenchRecipe> {
   }
}
