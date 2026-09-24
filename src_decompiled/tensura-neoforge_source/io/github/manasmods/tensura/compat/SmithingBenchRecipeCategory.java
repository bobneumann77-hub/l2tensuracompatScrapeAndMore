package io.github.manasmods.tensura.compat;

import io.github.manasmods.tensura.recipe.SmithingBenchRecipe;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class SmithingBenchRecipeCategory implements IRecipeCategory<SmithingBenchRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("tensura", "smithing_bench");
   public static final RecipeType<SmithingBenchRecipe> SMITHING_BENCH_RECIPE_TYPE = new RecipeType(UID, SmithingBenchRecipe.class);
   private final IDrawable background;
   private final IDrawable icon;

   public SmithingBenchRecipeCategory(IGuiHelper helper) {
      this.background = helper.createBlankDrawable(140, 18);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)TensuraBlocks.Items.SMITHING_BENCH.get()));
   }

   public RecipeType<SmithingBenchRecipe> getRecipeType() {
      return SMITHING_BENCH_RECIPE_TYPE;
   }

   public Component getTitle() {
      return Component.translatable("tensura.jei.smithing.title");
   }

   @Nullable
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, SmithingBenchRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 0).addItemStack(recipe.getOutput());
      List<ItemStack> ingredient1 = new ArrayList<>(
         Arrays.stream(recipe.getIngredient1().getItems()).peek(stack -> stack.setCount(recipe.getAmount1())).toList()
      );
      builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).addItemStacks(ingredient1);
      List<ItemStack> ingredient2 = new ArrayList<>(
         Arrays.stream(recipe.getIngredient2().getItems()).peek(stack -> stack.setCount(recipe.getAmount2())).toList()
      );
      builder.addSlot(RecipeIngredientRole.INPUT, 20, 0).addItemStacks(ingredient2);
      List<ItemStack> ingredient3 = new ArrayList<>(
         Arrays.stream(recipe.getIngredient3().getItems()).peek(stack -> stack.setCount(recipe.getAmount3())).toList()
      );
      builder.addSlot(RecipeIngredientRole.INPUT, 40, 0).addItemStacks(ingredient3);
      List<ItemStack> ingredient4 = new ArrayList<>(
         Arrays.stream(recipe.getIngredient4().getItems()).peek(stack -> stack.setCount(recipe.getAmount4())).toList()
      );
      builder.addSlot(RecipeIngredientRole.INPUT, 60, 0).addItemStacks(ingredient4);
      List<ItemStack> ingredient5 = new ArrayList<>(
         Arrays.stream(recipe.getIngredient5().getItems()).peek(stack -> stack.setCount(recipe.getAmount5())).toList()
      );
      builder.addSlot(RecipeIngredientRole.INPUT, 80, 0).addItemStacks(ingredient5);
   }

   @Generated
   public IDrawable getBackground() {
      return this.background;
   }
}
