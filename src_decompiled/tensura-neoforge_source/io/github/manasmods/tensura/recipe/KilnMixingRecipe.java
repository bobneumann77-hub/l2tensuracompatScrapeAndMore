package io.github.manasmods.tensura.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.recipe.input.KilnMeltingRecipeInput;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class KilnMixingRecipe implements Recipe<KilnMeltingRecipeInput> {
   private final ResourceLocation leftInput;
   private final int leftAmount;
   private final ResourceLocation rightInput;
   private final int rightAmount;
   private final ItemStack output;

   public KilnMixingRecipe(ResourceLocation leftInput, int leftAmount, ResourceLocation rightInput, int rightAmount, ItemStack output) {
      this.leftInput = leftInput;
      this.leftAmount = leftAmount;
      this.rightInput = rightInput;
      this.rightAmount = rightAmount;
      this.output = output;
   }

   public boolean matches(KilnMeltingRecipeInput recipeInput, Level level) {
      if (!this.leftInput.equals(KilnMeltingRecipe.EMPTY)) {
         if (recipeInput.left().isEmpty() || recipeInput.left().get().equals(KilnMeltingRecipe.EMPTY)) {
            return false;
         }

         if (!recipeInput.left().get().equals(this.leftInput)) {
            return false;
         }

         if (recipeInput.moltenAmount() < this.leftAmount) {
            return false;
         }
      }

      if (this.rightInput.equals(KilnMeltingRecipe.EMPTY)) {
         return true;
      } else if (recipeInput.right().isEmpty() || recipeInput.right().get().equals(KilnMeltingRecipe.EMPTY)) {
         return false;
      } else {
         return !recipeInput.right().get().equals(this.rightInput) ? false : recipeInput.magicAmount() >= this.rightAmount;
      }
   }

   @NotNull
   public ItemStack assemble(KilnMeltingRecipeInput recipeInput, Provider provider) {
      return this.getResultItem(provider);
   }

   @NotNull
   public ItemStack getResultItem(Provider provider) {
      return this.getOutput().copy();
   }

   public boolean canCraftInDimensions(int i, int j) {
      return true;
   }

   public int compareTo(@NotNull KilnMixingRecipe recipe) {
      int inputAmountLeft = Integer.compare(this.rightAmount, recipe.rightAmount);
      return inputAmountLeft != 0 ? inputAmountLeft : Integer.compare(this.leftAmount, recipe.leftAmount);
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)TensuraRecipes.KILN_MIXING_SERIALIZER.get();
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)TensuraRecipes.KILN_MIXING_TYPE.get();
   }

   @Generated
   public ResourceLocation getLeftInput() {
      return this.leftInput;
   }

   @Generated
   public int getLeftAmount() {
      return this.leftAmount;
   }

   @Generated
   public ResourceLocation getRightInput() {
      return this.rightInput;
   }

   @Generated
   public int getRightAmount() {
      return this.rightAmount;
   }

   @Generated
   public ItemStack getOutput() {
      return this.output;
   }

   @Generated
   @Override
   public String toString() {
      return "KilnMixingRecipe(leftInput="
         + this.getLeftInput()
         + ", leftAmount="
         + this.getLeftAmount()
         + ", rightInput="
         + this.getRightInput()
         + ", rightAmount="
         + this.getRightAmount()
         + ", output="
         + this.getOutput()
         + ")";
   }

   public static class Builder {
      private final ItemStack output;
      private ResourceLocation leftInput = KilnMeltingRecipe.EMPTY;
      private int leftAmount = 0;
      private ResourceLocation rightInput = KilnMeltingRecipe.EMPTY;
      private int rightAmount = 0;

      public KilnMixingRecipe.Builder leftInput(ResourceLocation moltenType, int amount) {
         this.leftInput = moltenType;
         this.leftAmount = amount;
         return this;
      }

      public KilnMixingRecipe.Builder rightInput(ResourceLocation moltenType, int amount) {
         this.rightInput = moltenType;
         this.rightAmount = amount;
         return this;
      }

      public static KilnMixingRecipe.Builder of(Item item) {
         return of(item.getDefaultInstance());
      }

      public static KilnMixingRecipe.Builder of(Supplier<? extends Item> item) {
         return of(item.get());
      }

      public void build(RecipeOutput output, ResourceLocation id) {
         SpecialRecipeBuilder.special(category -> new KilnMixingRecipe(this.leftInput, this.leftAmount, this.rightInput, this.rightAmount, this.output))
            .save(output, id);
      }

      public void build(RecipeOutput output) {
         ResourceLocation location = BuiltInRegistries.ITEM.getKey(this.output.getItem());
         this.build(output, ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "mixing/" + location.getPath()));
      }

      @Generated
      private Builder(ItemStack output) {
         this.output = output;
      }

      @Generated
      public static KilnMixingRecipe.Builder of(ItemStack output) {
         return new KilnMixingRecipe.Builder(output);
      }
   }

   public static class Serializer implements RecipeSerializer<KilnMixingRecipe> {
      private static final MapCodec<KilnMixingRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               ResourceLocation.CODEC.optionalFieldOf("left", KilnMeltingRecipe.EMPTY).forGetter(KilnMixingRecipe::getLeftInput),
               Codec.INT.optionalFieldOf("left_count", 0).forGetter(KilnMixingRecipe::getLeftAmount),
               ResourceLocation.CODEC.optionalFieldOf("right", KilnMeltingRecipe.EMPTY).forGetter(KilnMixingRecipe::getRightInput),
               Codec.INT.optionalFieldOf("right_count", 0).forGetter(KilnMixingRecipe::getRightAmount),
               ItemStack.CODEC.fieldOf("output").forGetter(KilnMixingRecipe::getOutput)
            )
            .apply(instance, KilnMixingRecipe::new)
      );

      @NotNull
      public MapCodec<KilnMixingRecipe> codec() {
         return CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, KilnMixingRecipe> streamCodec() {
         return StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            KilnMixingRecipe::getLeftInput,
            ByteBufCodecs.INT,
            KilnMixingRecipe::getLeftAmount,
            ResourceLocation.STREAM_CODEC,
            KilnMixingRecipe::getRightInput,
            ByteBufCodecs.INT,
            KilnMixingRecipe::getRightAmount,
            ItemStack.STREAM_CODEC,
            KilnMixingRecipe::getOutput,
            KilnMixingRecipe::new
         );
      }
   }

   public static class Type implements RecipeType<KilnMixingRecipe> {
   }
}
