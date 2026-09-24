package io.github.manasmods.tensura.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.manasmods.tensura.client.screen.KilnScreen;
import io.github.manasmods.tensura.data.recipe.KilnMoltenMaterial;
import io.github.manasmods.tensura.recipe.KilnMeltingRecipe;
import io.github.manasmods.tensura.recipe.KilnMixingRecipe;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import java.awt.Color;
import lombok.Generated;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class KilnMixingRecipeCategory implements IRecipeCategory<KilnMixingRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("tensura", "kiln/mixing");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/kiln/jei_mixing.png");
   public static final RecipeType<KilnMixingRecipe> KILN_MIXING_RECIPE_TYPE = new RecipeType(UID, KilnMixingRecipe.class);
   private final IDrawable background;
   private final IDrawable icon;

   public KilnMixingRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 177, 86);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()));
   }

   public RecipeType<KilnMixingRecipe> getRecipeType() {
      return KILN_MIXING_RECIPE_TYPE;
   }

   public Component getTitle() {
      return Component.translatable("tensura.jei.mixing.title");
   }

   @Nullable
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, KilnMixingRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 36).addItemStack(recipe.getOutput());
   }

   public void draw(KilnMixingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Registry<KilnMoltenMaterial> registry = level.registryAccess().registryOrThrow(TensuraCustomData.KILN_MOLTEN);

         for (KilnMoltenMaterial molten : registry.stream().toList()) {
            if (!recipe.getLeftInput().equals(KilnMeltingRecipe.EMPTY) && molten.type().equals(recipe.getLeftInput())) {
               int height = this.getMoltenProgress(recipe.getLeftAmount());
               RenderSystem.setShaderColor(molten.red() / 255.0F, molten.green() / 255.0F, molten.blue() / 255.0F, molten.alpha() / 255.0F);
               graphics.blit(KilnScreen.BACKGROUND, 18, 80 - height, 0, 182, 13, height);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (!recipe.getRightInput().equals(KilnMeltingRecipe.EMPTY) && molten.type().equals(recipe.getRightInput())) {
               int height = this.getMoltenProgress(recipe.getRightAmount());
               RenderSystem.setShaderColor(molten.red() / 255.0F, molten.green() / 255.0F, molten.blue() / 255.0F, molten.alpha() / 255.0F);
               graphics.blit(KilnScreen.BACKGROUND, 145, 80 - height, 0, 182, 13, height);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }
   }

   public int getMoltenProgress(int amount) {
      if (amount > 144) {
         amount = 144;
      }

      return amount != 0 ? amount * 74 / 144 : 0;
   }

   public void getTooltip(ITooltipBuilder tooltip, KilnMixingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Registry<KilnMoltenMaterial> registry = level.registryAccess().registryOrThrow(TensuraCustomData.KILN_MOLTEN);

         for (KilnMoltenMaterial moltenMaterial : registry.stream().toList()) {
            if (moltenMaterial.magic()) {
               if (this.isHovering(145, 6, 13, 74, mouseX, mouseY)
                  && !recipe.getRightInput().equals(KilnMeltingRecipe.EMPTY)
                  && moltenMaterial.type().equals(recipe.getRightInput())) {
                  tooltip.add(toolTipFromMoltenMaterial(moltenMaterial, recipe.getRightAmount() / 4.0F));
               }
            } else if (!recipe.getLeftInput().equals(KilnMeltingRecipe.EMPTY)
               && this.isHovering(18, 6, 13, 74, mouseX, mouseY)
               && moltenMaterial.type().equals(recipe.getLeftInput())) {
               tooltip.add(toolTipFromMoltenMaterial(moltenMaterial, recipe.getLeftAmount()));
            }
         }
      }
   }

   protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
      return pMouseX >= pX - 1 && pMouseX < pX + pWidth + 1 && pMouseY >= pY - 1 && pMouseY < pY + pHeight + 1;
   }

   public static MutableComponent toolTipFromMoltenMaterial(KilnMoltenMaterial moltenMaterial, float amount) {
      MutableComponent moltenMaterialName = Component.translatable(
         String.format("%s.molten.%s.material", moltenMaterial.type().getNamespace(), moltenMaterial.type().getPath())
      );
      int textColor = new Color(moltenMaterial.red(), moltenMaterial.green(), moltenMaterial.blue()).getRGB();
      return Component.translatable("tooltip.tensura.kiln.molten_item", new Object[]{amount, moltenMaterialName}).withStyle(Style.EMPTY.withColor(textColor));
   }

   @Generated
   public IDrawable getBackground() {
      return this.background;
   }
}
