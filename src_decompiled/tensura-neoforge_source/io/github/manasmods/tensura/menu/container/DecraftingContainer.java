package io.github.manasmods.tensura.menu.container;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.menu.UncraftingMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DecraftingContainer implements CraftingContainer {
   private final NonNullList<ItemStack> itemStacks;
   private final int width;
   private final int height;
   private final UncraftingMenu menu;

   public DecraftingContainer(UncraftingMenu pMenu, int pWidth, int pHeight) {
      this.itemStacks = NonNullList.withSize(pWidth * pHeight, ItemStack.EMPTY);
      this.menu = pMenu;
      this.width = pWidth;
      this.height = pHeight;
   }

   public int getContainerSize() {
      return this.itemStacks.size();
   }

   public boolean isEmpty() {
      for (ItemStack itemStack : this.itemStacks) {
         if (!itemStack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @NotNull
   public ItemStack getItem(int pIndex) {
      return pIndex >= this.getContainerSize() ? ItemStack.EMPTY : (ItemStack)this.itemStacks.get(pIndex);
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int pIndex) {
      return ContainerHelper.takeItem(this.itemStacks, pIndex);
   }

   @NotNull
   public ItemStack removeItem(int pIndex, int pCount) {
      ItemStack itemStack = ContainerHelper.removeItem(this.itemStacks, pIndex, pCount);
      if (!itemStack.isEmpty()) {
         this.menu.slotsChanged(this);
      }

      return itemStack;
   }

   public void setItem(int pIndex, @NotNull ItemStack pStack) {
      this.itemStacks.set(pIndex, pStack);
      this.menu.slotsChanged(this);
   }

   public void setChanged() {
   }

   public void clearContent() {
      this.itemStacks.clear();
   }

   public boolean stillValid(@NotNull Player pPlayer) {
      return true;
   }

   public boolean canPlaceItem(int pIndex, @NotNull ItemStack pStack) {
      return false;
   }

   public List<ItemStack> getItems() {
      return List.copyOf(this.itemStacks);
   }

   public void fillStackedContents(StackedContents stackedContents) {
      for (ItemStack itemStack : this.itemStacks) {
         stackedContents.accountSimpleStack(itemStack);
      }
   }

   public void updateSlots(ItemStack itemStack, Level level) {
      this.clearContent();
      this.fillOutputSlots(itemStack, level);
      this.menu.slotsChanged(this);
   }

   private void fillOutputSlots(ItemStack inputStack, Level level) {
      if (!level.isClientSide()) {
         MinecraftServer server = level.getServer();
         if (server != null) {
            RecipeHolder<?> recipe = this.searchRecipe(level, inputStack, server.getRecipeManager());
            if (recipe != null) {
               List<ItemStack> list = this.convertTo3x3(recipe)
                  .stream()
                  .collect(
                     ArrayList::new,
                     (accumulator, ingredientx) -> accumulator.add(ingredientx.isEmpty() ? ItemStack.EMPTY : ingredientx.getItems()[0]),
                     ArrayList::addAll
                  );
               int index = 0;
               if (!list.isEmpty()) {
                  for (ItemStack ingredient : list) {
                     this.setItem(index, ingredient.copy());
                     index++;
                  }
               }
            }
         }
      }
   }

   private List<Ingredient> convertTo3x3(RecipeHolder<?> recipe) {
      List<Ingredient> ingredients = new ArrayList<>(recipe.value().getIngredients());
      if (recipe.value() instanceof ShapedRecipe shapedRecipe) {
         int width = shapedRecipe.getWidth();
         if (width == 2) {
            ingredients.add(Ingredient.EMPTY);
            ingredients.add(Ingredient.EMPTY);
            ingredients.add(Ingredient.EMPTY);
            ingredients.add(4, Ingredient.EMPTY);
            ingredients.add(2, Ingredient.EMPTY);
         } else if (width == 1) {
            ingredients.add(Ingredient.EMPTY);
            ingredients.add(Ingredient.EMPTY);
            ingredients.add(2, Ingredient.EMPTY);
            ingredients.add(2, Ingredient.EMPTY);
            ingredients.add(1, Ingredient.EMPTY);
            ingredients.add(1, Ingredient.EMPTY);
            ingredients.add(0, Ingredient.EMPTY);
         }
      }

      while (ingredients.size() > 9) {
         ingredients.removeLast();
      }

      return ingredients;
   }

   @Nullable
   private RecipeHolder<?> searchRecipe(Level level, ItemStack input, RecipeManager recipeManager) {
      Item inputItem = input.getItem();
      if (input.is(TensuraItemTags.NO_DECRAFT)) {
         return null;
      }

      if (!input.isDamaged() && !input.isEnchanted()) {
         if (inputItem instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock) {
            return null;
         } else {
            Optional<RecipeHolder<?>> optionalRecipe = recipeManager.getRecipes().stream().filter(recipeHolder -> {
               Recipe<?> recipe = recipeHolder.value();
               if (!recipe.getType().equals(RecipeType.CRAFTING)) {
                  return false;
               } else if (recipe.getResultItem(level.registryAccess()).getCount() > 1) {
                  return false;
               } else {
                  return recipe.getResultItem(level.registryAccess()).getItem() != inputItem ? false : !recipe.getIngredients().isEmpty();
               }
            }).findFirst();
            return optionalRecipe.orElse(null);
         }
      } else {
         return null;
      }
   }

   @Generated
   public int getWidth() {
      return this.width;
   }

   @Generated
   public int getHeight() {
      return this.height;
   }
}
