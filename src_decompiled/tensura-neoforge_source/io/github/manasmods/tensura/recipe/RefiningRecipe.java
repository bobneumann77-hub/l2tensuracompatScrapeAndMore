package io.github.manasmods.tensura.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.item.consumable.HealingPotionItem;
import io.github.manasmods.tensura.recipe.input.RefiningRecipeInput;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.Generated;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RefiningRecipe implements Recipe<RefiningRecipeInput> {
   private final ItemStack input;
   private final Ingredient ingredient1;
   private final Ingredient ingredient2;
   private final Ingredient ingredient3;
   private final Ingredient ingredient4;
   private final Ingredient ingredient5;
   private final ItemStack output;

   public boolean matches(RefiningRecipeInput recipeInput, Level level) {
      if (this.getInputCount(recipeInput.inputContainer(), this.input) <= 0) {
         return false;
      }

      boolean matched = true;
      List<Ingredient> ingredients = new ArrayList<>(List.of(this.ingredient1, this.ingredient2, this.ingredient3, this.ingredient4, this.ingredient5));

      for (int i = 3; i < recipeInput.inputContainer().getContainerSize() && matched && !ingredients.isEmpty(); i++) {
         ItemStack item = recipeInput.getItem(i);
         boolean contain = false;
         Iterator var8 = ingredients.iterator();

         while (true) {
            if (var8.hasNext()) {
               Ingredient ingredient = (Ingredient)var8.next();
               if (!ingredient.test(item)) {
                  continue;
               }

               ingredients.remove(ingredient);
               contain = true;
            }

            matched = contain;
            break;
         }
      }

      return matched;
   }

   private int getInputCount(Container container, ItemStack stack) {
      int count = 0;
      if (isSameIngredient(container.getItem(0), stack)) {
         count++;
      }

      if (isSameIngredient(container.getItem(1), stack)) {
         count++;
      }

      if (isSameIngredient(container.getItem(2), stack)) {
         count++;
      }

      return count;
   }

   public static boolean isSameIngredient(ItemStack itemStack, ItemStack itemStack2) {
      if (itemStack.isEmpty() || itemStack2.isEmpty()) {
         return false;
      } else {
         return !itemStack.is(itemStack2.getItem())
            ? false
            : Objects.equals(itemStack.get(DataComponents.POTION_CONTENTS), itemStack2.get(DataComponents.POTION_CONTENTS));
      }
   }

   public ItemStack assemble(RefiningRecipeInput recipeInput, Provider provider) {
      int inputCount = this.getInputCount(recipeInput.inputContainer(), this.input);
      ItemStack result = this.getResultItem(provider);
      return result.copyWithCount(result.getCount() * inputCount);
   }

   public ItemStack getResultItem(Provider provider) {
      return this.getOutput().copy();
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return NonNullList.of(Ingredient.EMPTY, new Ingredient[]{this.ingredient1, this.ingredient2, this.ingredient3, this.ingredient4, this.ingredient5});
   }

   public void takeItemsFrom(Container container) {
      for (int i = 0; i < 5; i++) {
         Ingredient ingredient = (Ingredient)this.getIngredients().get(i);
         if (!ingredient.isEmpty()) {
            for (int j = 3; j < container.getContainerSize(); j++) {
               ItemStack stack = container.getItem(j);
               if (!stack.isEmpty() && ingredient.test(stack)) {
                  stack.shrink(1);
                  break;
               }
            }
         }
      }

      for (int j = 0; j < 3; j++) {
         ItemStack stack = container.getItem(j);
         if (!stack.isEmpty() && isSameIngredient(this.input, stack)) {
            stack.shrink(1);
         }
      }
   }

   public boolean canCraftInDimensions(int i, int j) {
      return true;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)TensuraRecipes.REFINING_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)TensuraRecipes.REFINING_TYPE.get();
   }

   @Generated
   @Override
   public String toString() {
      return "RefiningRecipe(input="
         + this.getInput()
         + ", ingredient1="
         + this.getIngredient1()
         + ", ingredient2="
         + this.getIngredient2()
         + ", ingredient3="
         + this.getIngredient3()
         + ", ingredient4="
         + this.getIngredient4()
         + ", ingredient5="
         + this.getIngredient5()
         + ", output="
         + this.getOutput()
         + ")";
   }

   @Generated
   public RefiningRecipe(
      ItemStack input, Ingredient ingredient1, Ingredient ingredient2, Ingredient ingredient3, Ingredient ingredient4, Ingredient ingredient5, ItemStack output
   ) {
      this.input = input;
      this.ingredient1 = ingredient1;
      this.ingredient2 = ingredient2;
      this.ingredient3 = ingredient3;
      this.ingredient4 = ingredient4;
      this.ingredient5 = ingredient5;
      this.output = output;
   }

   @Generated
   public ItemStack getInput() {
      return this.input;
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
   public ItemStack getOutput() {
      return this.output;
   }

   public static class Builder {
      private final ItemStack result;
      private ItemStack input;
      private final NonNullList<Ingredient> ingredientMap = NonNullList.withSize(5, Ingredient.EMPTY);
      private int ingredientAmount = 0;

      public static RefiningRecipe.Builder of(Item result, int amount) {
         return of(new ItemStack(result, amount));
      }

      public static RefiningRecipe.Builder of(Item result) {
         return of(result.getDefaultInstance());
      }

      public static RefiningRecipe.Builder of(Supplier<? extends Item> result) {
         return of(result.get());
      }

      public static RefiningRecipe.Builder of(Holder<Potion> pPotion) {
         return of(pPotion, Items.POTION);
      }

      public static RefiningRecipe.Builder of(Holder<Potion> pPotion, Item bottle) {
         ItemStack itemStack = bottle.getDefaultInstance();
         itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(pPotion));
         return of(itemStack);
      }

      public RefiningRecipe.Builder addInput(ItemStack input) {
         this.input = input;
         return this;
      }

      public RefiningRecipe.Builder addInput(Item result) {
         return this.addInput(result.getDefaultInstance());
      }

      public RefiningRecipe.Builder addInput(Holder<Potion> pPotion) {
         return this.addInput(pPotion, Items.POTION);
      }

      public RefiningRecipe.Builder addInput(Holder<Potion> pPotion, Item bottle) {
         ItemStack itemStack = bottle.getDefaultInstance();
         itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(pPotion));
         return this.addInput(itemStack);
      }

      public RefiningRecipe.Builder addIngredient(Ingredient ingredient) {
         this.ingredientMap.set(this.ingredientAmount++, ingredient);
         return this;
      }

      public RefiningRecipe.Builder addIngredient(ItemStack stack) {
         return this.addIngredient(Ingredient.of(new ItemStack[]{stack}));
      }

      public RefiningRecipe.Builder addIngredient(TagKey<Item> itemTagKey) {
         return this.addIngredient(Ingredient.of(itemTagKey));
      }

      public RefiningRecipe.Builder addIngredient(Item item) {
         return this.addIngredient(new ItemStack(item));
      }

      public RefiningRecipe.Builder addIngredient(Item... items) {
         Stream.of(items).forEach(item -> this.ingredientMap.set(this.ingredientAmount++, Ingredient.of(new ItemLike[]{item})));
         return this;
      }

      public void build(RecipeOutput consumer, ResourceLocation id) {
         String path = "refining/" + id.getPath();
         if (this.result.getItem() instanceof PotionItem potionItem && !(potionItem instanceof HealingPotionItem)) {
            path = path
               + "/"
               + BuiltInRegistries.POTION
                  .getKey((Potion)((Holder)((PotionContents)this.result.get(DataComponents.POTION_CONTENTS)).potion().get()).value())
                  .getPath();
         }

         if (!this.input.isEmpty()) {
            ResourceLocation location = BuiltInRegistries.ITEM.getKey(this.input.getItem());
            path = path + "_from_" + location.getPath();
            if (this.input.getItem() instanceof PotionItem) {
               path = path
                  + "_"
                  + Objects.requireNonNull(
                        BuiltInRegistries.POTION
                           .getKey((Potion)((Holder)((PotionContents)this.input.get(DataComponents.POTION_CONTENTS)).potion().get()).value())
                     )
                     .getPath();
            }
         }

         Ingredient ingredient = (Ingredient)this.ingredientMap.get(0);
         Optional<ItemStack> item = Arrays.stream(ingredient.getItems()).findFirst();
         if (item.isPresent()) {
            path = path + "_using_" + Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item.get().getItem())).getPath();
         }

         SpecialRecipeBuilder.special(
               category -> new RefiningRecipe(
                  this.input,
                  ingredient,
                  (Ingredient)this.ingredientMap.get(1),
                  (Ingredient)this.ingredientMap.get(2),
                  (Ingredient)this.ingredientMap.get(3),
                  (Ingredient)this.ingredientMap.get(4),
                  this.result
               )
            )
            .save(consumer, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path));
      }

      public void build(RecipeOutput consumer) {
         this.build(consumer, Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(this.result.getItem())));
      }

      @Generated
      private Builder(ItemStack result) {
         this.result = result;
      }

      @Generated
      public static RefiningRecipe.Builder of(ItemStack result) {
         return new RefiningRecipe.Builder(result);
      }
   }

   public static class Serializer implements RecipeSerializer<RefiningRecipe> {
      private static final MapCodec<RefiningRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               ItemStack.CODEC.fieldOf("input").forGetter(RefiningRecipe::getInput),
               Ingredient.CODEC_NONEMPTY.optionalFieldOf("input1", Ingredient.EMPTY).forGetter(RefiningRecipe::getIngredient1),
               Ingredient.CODEC_NONEMPTY.optionalFieldOf("input2", Ingredient.EMPTY).forGetter(RefiningRecipe::getIngredient2),
               Ingredient.CODEC_NONEMPTY.optionalFieldOf("input3", Ingredient.EMPTY).forGetter(RefiningRecipe::getIngredient3),
               Ingredient.CODEC_NONEMPTY.optionalFieldOf("input4", Ingredient.EMPTY).forGetter(RefiningRecipe::getIngredient4),
               Ingredient.CODEC_NONEMPTY.optionalFieldOf("input5", Ingredient.EMPTY).forGetter(RefiningRecipe::getIngredient5),
               ItemStack.CODEC.fieldOf("output").forGetter(RefiningRecipe::getOutput)
            )
            .apply(instance, RefiningRecipe::new)
      );

      public MapCodec<RefiningRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, RefiningRecipe> streamCodec() {
         return new StreamCodec<RegistryFriendlyByteBuf, RefiningRecipe>() {
            public RefiningRecipe decode(RegistryFriendlyByteBuf buf) {
               ItemStack input = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
               Ingredient ingredient1 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
               Ingredient ingredient2 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
               Ingredient ingredient3 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
               Ingredient ingredient4 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
               Ingredient ingredient5 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
               ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
               return new RefiningRecipe(input, ingredient1, ingredient2, ingredient3, ingredient4, ingredient5, output);
            }

            public void encode(RegistryFriendlyByteBuf buf, RefiningRecipe recipe) {
               ItemStack.STREAM_CODEC.encode(buf, recipe.getInput());
               Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient1());
               Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient2());
               Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient3());
               Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient4());
               Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient5());
               ItemStack.STREAM_CODEC.encode(buf, recipe.getOutput());
            }
         };
      }
   }

   public static class Type implements RecipeType<RefiningRecipe> {
   }
}
