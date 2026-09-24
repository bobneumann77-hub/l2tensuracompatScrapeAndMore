package io.github.manasmods.tensura.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.manasmods.tensura.client.screen.KilnScreen;
import io.github.manasmods.tensura.data.recipe.KilnMoltenMaterial;
import io.github.manasmods.tensura.recipe.KilnMeltingRecipe;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
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

public class KilnMeltingRecipeCategory implements IRecipeCategory<KilnMeltingRecipe> {
   public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("tensura", "kiln/melting");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/kiln/jei_melting.png");
   public static final RecipeType<KilnMeltingRecipe> KILN_MELTING_RECIPE_TYPE = new RecipeType(UID, KilnMeltingRecipe.class);
   private final IDrawable background;
   private final IDrawable icon;

   public KilnMeltingRecipeCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 177, 121);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)TensuraBlocks.Items.KILN.get()));
   }

   public RecipeType<KilnMeltingRecipe> getRecipeType() {
      return KILN_MELTING_RECIPE_TYPE;
   }

   public Component getTitle() {
      return Component.translatable("tensura.jei.melting.title");
   }

   @Nullable
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, KilnMeltingRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 80, 32).addIngredients(recipe.getInput());
   }

   public void draw(KilnMeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Registry<KilnMoltenMaterial> registry = level.registryAccess().registryOrThrow(TensuraCustomData.KILN_MOLTEN);

         for (KilnMoltenMaterial molten : registry.stream().toList()) {
            if (molten.magic()) {
               if (molten.type().equals(recipe.getMoltenType())) {
                  int height = this.getMoltenProgress(recipe.getMoltenAmount());
                  RenderSystem.setShaderColor(molten.red() / 255.0F, molten.green() / 255.0F, molten.blue() / 255.0F, molten.alpha() / 255.0F);
                  graphics.blit(KilnScreen.BACKGROUND, 145, 80 - height, 0, 182, 13, height);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               } else if (molten.type().equals(recipe.getSecondaryType())) {
                  int height = this.getMoltenProgress(recipe.getSecondaryAmount());
                  RenderSystem.setShaderColor(molten.red() / 255.0F, molten.green() / 255.0F, molten.blue() / 255.0F, molten.alpha() / 255.0F);
                  graphics.blit(KilnScreen.BACKGROUND, 145, 80 - height, 0, 182, 13, height);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               }
            } else if (molten.type().equals(recipe.getMoltenType())) {
               int height = this.getMoltenProgress(recipe.getMoltenAmount());
               RenderSystem.setShaderColor(molten.red() / 255.0F, molten.green() / 255.0F, molten.blue() / 255.0F, molten.alpha() / 255.0F);
               graphics.blit(KilnScreen.BACKGROUND, 18, 80 - height, 0, 182, 13, height);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else if (molten.type().equals(recipe.getSecondaryType())) {
               int height = this.getMoltenProgress(recipe.getSecondaryAmount());
               RenderSystem.setShaderColor(molten.red() / 255.0F, molten.green() / 255.0F, molten.blue() / 255.0F, molten.alpha() / 255.0F);
               graphics.blit(KilnScreen.BACKGROUND, 18, 80 - height, 0, 182, 13, height);
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

   public void getTooltip(ITooltipBuilder tooltip, KilnMeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         Registry<KilnMoltenMaterial> registry = level.registryAccess().registryOrThrow(TensuraCustomData.KILN_MOLTEN);

         for (KilnMoltenMaterial material : registry.stream().toList()) {
            if (material.magic()) {
               if (this.isHovering(145, 6, 13, 74, mouseX, mouseY)) {
                  if (material.type().equals(recipe.getMoltenType())) {
                     tooltip.add(toolTipFromMoltenMaterial(material, recipe.getMoltenAmount() / 4.0F));
                  }

                  if (material.type().equals(recipe.getSecondaryType())) {
                     tooltip.add(toolTipFromMoltenMaterial(material, recipe.getSecondaryAmount() / 4.0F));
                  }
               }
            } else if (this.isHovering(18, 6, 13, 74, mouseX, mouseY)) {
               if (material.type().equals(recipe.getMoltenType())) {
                  tooltip.add(toolTipFromMoltenMaterial(material, recipe.getMoltenAmount()));
               }

               if (material.type().equals(recipe.getSecondaryType())) {
                  tooltip.add(toolTipFromMoltenMaterial(material, recipe.getSecondaryAmount()));
               }
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
