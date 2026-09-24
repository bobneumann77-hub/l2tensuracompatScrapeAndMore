package io.github.manasmods.tensura.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.block.entity.KilnBlockEntity;
import io.github.manasmods.tensura.data.recipe.KilnMoltenMaterial;
import io.github.manasmods.tensura.recipe.input.KilnMeltingRecipeInput;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class KilnMeltingRecipe implements Recipe<KilnMeltingRecipeInput> {
   public static final ResourceLocation EMPTY = ResourceLocation.withDefaultNamespace("air");
   private final Ingredient input;
   private final int smeltTick;
   private final ResourceLocation moltenType;
   private final int moltenAmount;
   private final ResourceLocation secondaryType;
   private final int secondaryAmount;

   public boolean matches(KilnMeltingRecipeInput recipeInput, Level level) {
      ItemStack inputStack = recipeInput.getItem(0).copy();
      if (!this.input.test(inputStack)) {
         return false;
      } else {
         boolean primaryPlaceable = this.sameOrEmpty(level, recipeInput, this.moltenType, this.moltenAmount);
         if (this.secondaryType.equals(EMPTY)) {
            return primaryPlaceable;
         } else {
            return !primaryPlaceable ? false : this.sameOrEmpty(level, recipeInput, this.secondaryType, this.secondaryAmount);
         }
      }
   }

   private boolean sameOrEmpty(Level level, KilnMeltingRecipeInput recipeInput, ResourceLocation type, int amount) {
      Registry<KilnMoltenMaterial> registry = level.registryAccess().registryOrThrow(TensuraCustomData.KILN_MOLTEN);

      for (KilnMoltenMaterial material : registry.stream().toList()) {
         if (material.type().equals(type)) {
            Optional<ResourceLocation> containerMaterial = material.magic() ? recipeInput.right() : recipeInput.left();
            if (containerMaterial.isPresent() && !containerMaterial.get().equals(EMPTY) && !containerMaterial.get().equals(type)) {
               return false;
            }

            int existingAmount = material.magic() ? recipeInput.magicAmount() : recipeInput.moltenAmount();
            return existingAmount + amount <= recipeInput.maximumMolten();
         }
      }

      return false;
   }

   public ItemStack assemble(KilnMeltingRecipeInput recipeInput, Provider provider) {
      return ItemStack.EMPTY;
   }

   public void assembleMolten(Level level, KilnBlockEntity container, Provider provider) {
      this.melt(level, container, this.moltenType, this.moltenAmount);
      this.melt(level, container, this.secondaryType, this.secondaryAmount);
      container.removeItem(1, 1);
   }

   private void melt(Level level, KilnBlockEntity container, ResourceLocation type, int amount) {
      if (!type.equals(EMPTY)) {
         Registry<KilnMoltenMaterial> registry = level.registryAccess().registryOrThrow(TensuraCustomData.KILN_MOLTEN);

         for (KilnMoltenMaterial material : registry.stream().toList()) {
            if (material.type().equals(type)) {
               if (material.magic()) {
                  container.setRightBarId(Optional.of(material.type()));
                  container.addMagicMaterialAmount(amount);
               } else {
                  container.setLeftBarId(Optional.of(material.type()));
                  container.addMoltenMaterialAmount(amount);
               }
            }
         }
      }
   }

   public boolean canCraftInDimensions(int i, int j) {
      return true;
   }

   public ItemStack getResultItem(Provider provider) {
      return ItemStack.EMPTY;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)TensuraRecipes.KILN_MELTING_SERIALIZER.get();
   }

   public RecipeType<?> getType() {
      return (RecipeType<?>)TensuraRecipes.KILN_MELTING_TYPE.get();
   }

   @Generated
   @Override
   public String toString() {
      return "KilnMeltingRecipe(input="
         + this.getInput()
         + ", smeltTick="
         + this.getSmeltTick()
         + ", moltenType="
         + this.getMoltenType()
         + ", moltenAmount="
         + this.getMoltenAmount()
         + ", secondaryType="
         + this.getSecondaryType()
         + ", secondaryAmount="
         + this.getSecondaryAmount()
         + ")";
   }

   @Generated
   public KilnMeltingRecipe(Ingredient input, int smeltTick, ResourceLocation moltenType, int moltenAmount, ResourceLocation secondaryType, int secondaryAmount) {
      this.input = input;
      this.smeltTick = smeltTick;
      this.moltenType = moltenType;
      this.moltenAmount = moltenAmount;
      this.secondaryType = secondaryType;
      this.secondaryAmount = secondaryAmount;
   }

   @Generated
   public Ingredient getInput() {
      return this.input;
   }

   @Generated
   public int getSmeltTick() {
      return this.smeltTick;
   }

   @Generated
   public ResourceLocation getMoltenType() {
      return this.moltenType;
   }

   @Generated
   public int getMoltenAmount() {
      return this.moltenAmount;
   }

   @Generated
   public ResourceLocation getSecondaryType() {
      return this.secondaryType;
   }

   @Generated
   public int getSecondaryAmount() {
      return this.secondaryAmount;
   }

   public static class Builder {
      private Ingredient input;
      private int smeltTick;
      private final ResourceLocation moltenType;
      private final int moltenAmount;
      private ResourceLocation secondaryType = KilnMeltingRecipe.EMPTY;
      private int secondaryAmount = 0;

      public KilnMeltingRecipe.Builder requires(Ingredient ingredient) {
         this.input = ingredient;
         return this;
      }

      public KilnMeltingRecipe.Builder smeltTick(int smeltTick) {
         this.smeltTick = smeltTick;
         return this;
      }

      public KilnMeltingRecipe.Builder inputSecondary(ResourceLocation moltenType, int amount) {
         this.secondaryType = moltenType;
         this.secondaryAmount = amount;
         return this;
      }

      public void build(RecipeOutput output, ResourceLocation id) {
         SpecialRecipeBuilder.special(
               category -> new KilnMeltingRecipe(
                  this.input == null ? Ingredient.EMPTY : this.input,
                  this.smeltTick,
                  this.moltenType,
                  this.moltenAmount,
                  this.secondaryType,
                  this.secondaryAmount
               )
            )
            .save(output, id);
      }

      public void build(RecipeOutput output, String fileName) {
         this.build(output, ResourceLocation.fromNamespaceAndPath(this.moltenType.getNamespace(), "melting/" + fileName));
      }

      @Generated
      private Builder(ResourceLocation moltenType, int moltenAmount) {
         this.moltenType = moltenType;
         this.moltenAmount = moltenAmount;
      }

      @Generated
      public static KilnMeltingRecipe.Builder of(ResourceLocation moltenType, int moltenAmount) {
         return new KilnMeltingRecipe.Builder(moltenType, moltenAmount);
      }
   }

   public static class Serializer implements RecipeSerializer<KilnMeltingRecipe> {
      private static final MapCodec<KilnMeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(KilnMeltingRecipe::getInput),
               Codec.INT.optionalFieldOf("smeltTick", 100).forGetter(KilnMeltingRecipe::getSmeltTick),
               ResourceLocation.CODEC.optionalFieldOf("primary", KilnMeltingRecipe.EMPTY).forGetter(KilnMeltingRecipe::getMoltenType),
               Codec.INT.optionalFieldOf("primary_count", 0).forGetter(KilnMeltingRecipe::getMoltenAmount),
               ResourceLocation.CODEC.optionalFieldOf("secondary", KilnMeltingRecipe.EMPTY).forGetter(KilnMeltingRecipe::getSecondaryType),
               Codec.INT.optionalFieldOf("secondary_count", 0).forGetter(KilnMeltingRecipe::getSecondaryAmount)
            )
            .apply(instance, KilnMeltingRecipe::new)
      );

      public MapCodec<KilnMeltingRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, KilnMeltingRecipe> streamCodec() {
         return StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            KilnMeltingRecipe::getInput,
            ByteBufCodecs.INT,
            KilnMeltingRecipe::getSmeltTick,
            ResourceLocation.STREAM_CODEC,
            KilnMeltingRecipe::getMoltenType,
            ByteBufCodecs.INT,
            KilnMeltingRecipe::getMoltenAmount,
            ResourceLocation.STREAM_CODEC,
            KilnMeltingRecipe::getSecondaryType,
            ByteBufCodecs.INT,
            KilnMeltingRecipe::getSecondaryAmount,
            KilnMeltingRecipe::new
         );
      }
   }

   public static class Type implements RecipeType<KilnMeltingRecipe> {
   }
}
