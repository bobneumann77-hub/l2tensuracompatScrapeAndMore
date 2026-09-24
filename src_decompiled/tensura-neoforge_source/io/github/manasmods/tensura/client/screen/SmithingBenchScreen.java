package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.tensura.client.screen.templates.IScrollBar;
import io.github.manasmods.tensura.menu.SmithingBenchMenu;
import io.github.manasmods.tensura.recipe.SmithingBenchRecipe;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class SmithingBenchScreen extends AbstractContainerScreen<SmithingBenchMenu> implements IScrollBar {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/smithing/background.png");
   private static final ResourceLocation SLOT = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/slot.png");
   private static final ResourceLocation CHECK = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/smithing/check.png");
   private List<RecipeHolder<SmithingBenchRecipe>> filteredRecipes = new ArrayList<>();
   private EditBox searchBar;
   private String nameFilter = "";
   private float scrollOffset;
   protected boolean scrolling;
   private int listStartIndex;

   public SmithingBenchScreen(SmithingBenchMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
      super(pMenu, pPlayerInventory, pTitle);
      this.imageWidth = 194;
      this.imageHeight = 194;
   }

   protected void init() {
      super.init();
      this.scrollOffset = 0.0F;
      this.listStartIndex = 0;
      this.searchBar = new EditBox(this.font, this.leftPos + 82, this.topPos + 6, 80, 20, Component.empty());
      this.searchBar.setBordered(false);
      this.searchBar.setTextColor(16777215);
      this.searchBar.setResponder(string -> {
         this.nameFilter = string;
         this.setScrollOffset(0.0F);
         this.setListStartIndex(0);
         this.updateFilteredRecipes();
      });
      this.addRenderableWidget(this.searchBar);
      if (this.nameFilter == null) {
         this.nameFilter = "";
      } else {
         this.searchBar.setValue(this.nameFilter);
      }

      this.updateFilteredRecipes();
   }

   public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
      super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
      this.renderTooltip(guiGraphics, pMouseX, pMouseY);
   }

   public void renderLabels(GuiGraphics graphics, int pMouseX, int pMouseY) {
      graphics.drawString(this.font, this.title, this.titleLabelX - 2, this.titleLabelY + 1, 4210752, false);
      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX + 9, this.inventoryLabelY + 29, 4210752, false);
   }

   protected void renderBg(GuiGraphics graphics, float pPartialTick, int mX, int mY) {
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      graphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);
      this.renderScrollBar(graphics, mX, mY);
      int recipeXPos = this.leftPos + 8;
      int recipeYPos = this.topPos + 18;
      int lastVisibleElementIndex = this.listStartIndex + 27;
      this.renderButtons(graphics, mX, mY, recipeXPos, recipeYPos, lastVisibleElementIndex);
      this.renderRecipes(graphics, recipeXPos, recipeYPos, lastVisibleElementIndex, mX, mY);
   }

   private void renderButtons(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int pX, int pY, int pLastVisibleElementIndex) {
      List<RecipeHolder<SmithingBenchRecipe>> list = this.filteredRecipes;

      for (int i = this.listStartIndex; i < pLastVisibleElementIndex && i < list.size(); i++) {
         int j = i - this.listStartIndex;
         int x = pX + j % 9 * 18;
         int l = j / 9;
         int y = pY + l * 18 + 3;
         int stonecutterImageHeight = 0;
         int filteredIndex = ((SmithingBenchMenu)this.menu).getSelectedRecipeIndex() == -1
            ? -1
            : list.indexOf(((SmithingBenchMenu)this.menu).getRecipes().get(((SmithingBenchMenu)this.menu).getSelectedRecipeIndex()));
         if (i == filteredIndex) {
            stonecutterImageHeight += 18;
         } else if (pMouseX >= x && pMouseY >= y - 1 && pMouseX < x + 18 && pMouseY < y + 17) {
            stonecutterImageHeight += 36;
         }

         guiGraphics.blit(SLOT, x, y - 1, 0.0F, stonecutterImageHeight, 18, 18, 18, 54);
      }
   }

   private void renderRecipes(GuiGraphics guiGraphics, int pLeft, int pTop, int pRecipeIndexOffsetMax, int pMouseX, int pMouseY) {
      List<RecipeHolder<SmithingBenchRecipe>> list = this.filteredRecipes;

      for (int i = this.listStartIndex; i < pRecipeIndexOffsetMax && i < list.size(); i++) {
         int j = i - this.listStartIndex;
         int x = pLeft + j % 9 * 18 + 1;
         int l = j / 9;
         int y = pTop + l * 18 + 3;
         SmithingBenchRecipe recipe = (SmithingBenchRecipe)list.get(i).value();
         ItemStack stack = recipe.getResultItem(((SmithingBenchMenu)this.menu).level.registryAccess());
         guiGraphics.renderItem(stack, x, y);
         if (pMouseX >= x - 1 && pMouseX <= x + 16 && pMouseY >= y - 1 && pMouseY <= y + 16) {
            guiGraphics.renderTooltip(this.font, stack, pMouseX, pMouseY);
         }
      }

      if (((SmithingBenchMenu)this.menu).getSelectedRecipeIndex() != -1) {
         SmithingBenchRecipe recipe = (SmithingBenchRecipe)((SmithingBenchMenu)this.menu)
            .getRecipes()
            .get(((SmithingBenchMenu)this.menu).getSelectedRecipeIndex())
            .value();
         boolean recipeMatches = ((SmithingBenchMenu)this.menu).getPlayer().hasInfiniteMaterials()
            || recipe.matches(((SmithingBenchMenu)this.menu).getInput(), ((SmithingBenchMenu)this.menu).level);
         guiGraphics.blit(CHECK, this.leftPos + 112, this.topPos + 85, 8, 8, 0.0F, recipeMatches ? 8.0F : 0.0F, 8, 8, 8, 16);

         for (int index = 0; index < 5; index++) {
            Ingredient ingredient = (Ingredient)recipe.getIngredients().get(index);
            if (!ingredient.isEmpty()) {
               ItemStack stack = ingredient.getItems()[0];
               stack.setCount((Integer)recipe.getIngredientAmount().get(index));
               guiGraphics.renderItemDecorations(this.font, stack, this.leftPos + 18 + 18 * index, this.topPos + 81);
               guiGraphics.renderItem(stack, this.leftPos + 18 + 18 * index, this.topPos + 81);
               int x = this.leftPos + 16 + 18 * index;
               int y = this.topPos + 79;
               if (pMouseX >= x && pMouseX <= x + 19 && pMouseY >= y && pMouseY <= y + 19) {
                  guiGraphics.renderTooltip(this.font, stack, pMouseX, pMouseY);
               }
            }
         }
      }
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.searchBar.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         return true;
      } else if (this.searchBar.isFocused() && this.searchBar.isVisible() && pKeyCode != 256) {
         return true;
      } else {
         return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
            ? true
            : super.keyPressed(pKeyCode, pScanCode, pModifiers);
      }
   }

   public boolean mouseClicked(double x, double y, int pButton) {
      if (!this.searchBar.isHovered()) {
         this.searchBar.setFocused(false);
      }

      if (this.clickedScrollBar(x, y)) {
         return true;
      }

      List<RecipeHolder<SmithingBenchRecipe>> list = this.filteredRecipes;
      int recipeAreaLeft = this.leftPos + 8;
      int recipeAreaTop = this.topPos + 20;
      int lastDisplayedRecipeIndex = this.listStartIndex + 27;

      for (int l = this.listStartIndex; l < lastDisplayedRecipeIndex; l++) {
         int i1 = l - this.listStartIndex;
         double d0 = x - (recipeAreaLeft + i1 % 9 * 18);
         double d1 = y - (recipeAreaTop + i1 / 9 * 18);
         if (list.size() <= l) {
            break;
         }

         int unfilteredIndex = ((SmithingBenchMenu)this.menu).getRecipes().indexOf(list.get(l));
         if (d0 >= 0.0 && d1 >= 0.0 && d0 < 18.0 && d1 < 18.0 && ((SmithingBenchMenu)this.menu).clickMenuButton(this.minecraft.player, unfilteredIndex)) {
            if (this.minecraft.gameMode == null) {
               return false;
            }

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(((SmithingBenchMenu)this.menu).containerId, unfilteredIndex);
            return true;
         }
      }

      return super.mouseClicked(x, y, pButton);
   }

   protected void updateFilteredRecipes() {
      if (this.nameFilter != null && !this.nameFilter.isEmpty() && !this.nameFilter.isBlank()) {
         this.filteredRecipes = ((SmithingBenchMenu)this.menu).getRecipes().stream().filter(instance -> {
            Component name = ((SmithingBenchRecipe)instance.value()).getOutput().getDisplayName();
            return name.getString().toLowerCase().contains(this.nameFilter.toLowerCase());
         }).toList();
      } else {
         this.filteredRecipes = ((SmithingBenchMenu)this.menu).getRecipes();
      }

      this.scrolledScrollBar(0.0);
   }

   public boolean mouseScrolled(double pMouseX, double pMouseY, double pDeltaX, double pDeltaY) {
      this.scrolledScrollBar(pDeltaY);
      return super.mouseScrolled(pMouseX, pMouseY, pDeltaX, pDeltaY);
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      return this.draggedScrollBar(pMouseY) ? true : super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
   }

   @Override
   public int getScrollBarX() {
      return this.leftPos + 175;
   }

   @Override
   public int getScrollBarY() {
      return this.topPos + 21;
   }

   @Override
   public int getScrollBarTotalSpace() {
      return 52;
   }

   @Override
   public int getScrollBarListSize() {
      return this.filteredRecipes.size();
   }

   @Override
   public int getScrollBarRenderCount() {
      return 27;
   }

   @Override
   public int getCustomScrollAmount() {
      return 9;
   }

   @Override
   public boolean hasCustomScrollLogic() {
      return true;
   }

   @Generated
   @Override
   public float getScrollOffset() {
      return this.scrollOffset;
   }

   @Generated
   @Override
   public void setScrollOffset(float scrollOffset) {
      this.scrollOffset = scrollOffset;
   }

   @Generated
   @Override
   public boolean isScrolling() {
      return this.scrolling;
   }

   @Generated
   @Override
   public void setScrolling(boolean scrolling) {
      this.scrolling = scrolling;
   }

   @Generated
   public int getListStartIndex() {
      return this.listStartIndex;
   }

   @Generated
   @Override
   public void setListStartIndex(int listStartIndex) {
      this.listStartIndex = listStartIndex;
   }
}
