package io.github.manasmods.tensura.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.manasmods.tensura.data.recipe.KilnMoltenMaterial;
import io.github.manasmods.tensura.menu.KilnMenu;
import io.github.manasmods.tensura.recipe.KilnMeltingRecipe;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.util.client.RenderHelper;
import java.awt.Color;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class KilnScreen extends AbstractContainerScreen<KilnMenu> {
   public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/kiln/kiln_gui.png");
   public static final ResourceLocation BACKGROUND_MITHRIL = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/kiln/kiln_mithril_gui.png");
   public static final ResourceLocation BACKGROUND_ORICHALCUM = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/kiln/kiln_orichalcum_gui.png");
   public static final ResourceLocation BOOSTED_FLAME = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/kiln/boosted_flame.png");
   private Optional<KilnMoltenMaterial> leftBarMaterial;
   private Optional<KilnMoltenMaterial> rightBarMaterial;

   public KilnScreen(KilnMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageWidth = 256;
      this.imageHeight = 168;
      this.leftBarMaterial = menu.kiln
         .getLeftBarId()
         .flatMap(
            location -> menu.level
               .registryAccess()
               .registryOrThrow(TensuraCustomData.KILN_MOLTEN)
               .stream()
               .filter(moltenMaterial -> moltenMaterial.type().equals(location))
               .findFirst()
         );
      this.rightBarMaterial = menu.kiln
         .getRightBarId()
         .flatMap(
            location -> menu.level
               .registryAccess()
               .registryOrThrow(TensuraCustomData.KILN_MOLTEN)
               .stream()
               .filter(moltenMaterial -> moltenMaterial.type().equals(location))
               .findFirst()
         );
   }

   protected void containerTick() {
      super.containerTick();
      if (hasChanged(((KilnMenu)this.menu).kiln.getLeftBarId(), this.leftBarMaterial)) {
         this.leftBarMaterial = this.materialOf(((KilnMenu)this.menu).kiln.getLeftBarId());
      }

      if (hasChanged(((KilnMenu)this.menu).kiln.getRightBarId(), this.rightBarMaterial)) {
         this.rightBarMaterial = this.materialOf(((KilnMenu)this.menu).kiln.getRightBarId());
      }
   }

   private Optional<KilnMoltenMaterial> materialOf(Optional<ResourceLocation> id) {
      return id.flatMap(
         location -> ((KilnMenu)this.menu)
            .level
            .registryAccess()
            .registryOrThrow(TensuraCustomData.KILN_MOLTEN)
            .stream()
            .filter(moltenMaterial -> moltenMaterial.type().equals(location))
            .findFirst()
      );
   }

   private static boolean hasChanged(Optional<ResourceLocation> barId, Optional<KilnMoltenMaterial> material) {
      if (barId.isEmpty()) {
         return material.isPresent();
      } else {
         return material.isEmpty() ? true : !material.get().type().equals(barId.get());
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      super.render(graphics, mouseX, mouseY, delta);
      this.renderTooltip(graphics, mouseX, mouseY);
   }

   protected void renderLabels(GuiGraphics graphics, int pMouseX, int pMouseY) {
      RenderHelper.drawCenteredText(graphics, this.font, this.title, 88, this.titleLabelY + 1, Color.WHITE.getRGB(), false);
      RenderHelper.drawCenteredText(
         graphics, this.font, Component.translatable("tensura.kiln.smeltery_label"), 210, this.titleLabelY + 9, Color.WHITE.getRGB(), false
      );
      RenderHelper.drawCenteredText(graphics, this.font, this.playerInventoryTitle, 89, this.inventoryLabelY + 2, 4210752, false);
   }

   private ResourceLocation getBackground() {
      return switch (((KilnMenu)this.menu).kiln.getKilnType()) {
         case NORMAL -> BACKGROUND;
         case MITHRIL -> BACKGROUND_MITHRIL;
         case ORICHALCUM -> BACKGROUND_ORICHALCUM;
      };
   }

   protected void renderBg(GuiGraphics graphics, float pPartialTick, int mX, int mY) {
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      graphics.blit(this.getBackground(), x, y, 0, 0, 256, 168);
      this.renderProgress(graphics, x, y);
      this.renderFire(graphics, x, y);
      this.renderMolten(graphics, x, y);
      if (((KilnMenu)this.menu).kiln.hasPrevMixingRecipe()) {
         boolean hovering = mX >= x + 69 && mX <= x + 73 && mY >= y + 40 && mY <= y + 48;
         graphics.blit(this.getBackground(), x + 69, y + 40, 14, this.imageHeight + (hovering ? 8 : 0), 4, 8);
      }

      if (((KilnMenu)this.menu).kiln.hasNextMixingRecipe()) {
         boolean hovering = mX >= x + 103 && mX <= x + 107 && mY >= y + 40 && mY <= y + 48;
         graphics.blit(this.getBackground(), x + 103, y + 40, 19, this.imageHeight + (hovering ? 8 : 0), 4, 8);
      }
   }

   private void renderProgress(GuiGraphics graphics, int x, int y) {
      if (((KilnMenu)this.menu).isSmelting()) {
         int height = ((KilnMenu)this.menu).getScaledProgress();
         graphics.blit(this.getBackground(), x + 199, y + 72 - height, 24, this.imageHeight + 24 - height, 24, height);
      }
   }

   private void renderFire(GuiGraphics graphics, int x, int y) {
      if (((KilnMenu)this.menu).hasFuel()) {
         if (((KilnMenu)this.menu).kiln.getBoostedTime() > 0) {
            graphics.blit(
               BOOSTED_FLAME,
               x + 204,
               y + 90 - ((KilnMenu)this.menu).getScaledFuelProgress(),
               0.0F,
               13 - ((KilnMenu)this.menu).getScaledFuelProgress(),
               13,
               ((KilnMenu)this.menu).getScaledFuelProgress(),
               13,
               13
            );
         } else {
            graphics.blit(
               this.getBackground(),
               x + 204,
               y + 90 - ((KilnMenu)this.menu).getScaledFuelProgress(),
               0,
               181 - ((KilnMenu)this.menu).getScaledFuelProgress(),
               13,
               ((KilnMenu)this.menu).getScaledFuelProgress()
            );
         }
      }
   }

   private void renderMolten(GuiGraphics graphics, int x, int y) {
      this.leftBarMaterial.ifPresent(molten -> {
         int height = ((KilnMenu)this.menu).getMoltenProgress();
         RenderSystem.setShaderColor(molten.red() / 255.0F, molten.green() / 255.0F, molten.blue() / 255.0F, molten.alpha() / 255.0F);
         graphics.blit(this.getBackground(), x + 18, y + 80 - height, 0, this.imageHeight + 14, 13, height);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      });
      this.rightBarMaterial.ifPresent(molten -> {
         int height = ((KilnMenu)this.menu).getMagisteelProgress();
         RenderSystem.setShaderColor(molten.red() / 255.0F, molten.green() / 255.0F, molten.blue() / 255.0F, molten.alpha() / 255.0F);
         graphics.blit(this.getBackground(), x + 145, y + 80 - height, 0, this.imageHeight + 14, 13, height);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      });
   }

   protected void renderTooltip(GuiGraphics graphics, int mX, int mY) {
      super.renderTooltip(graphics, mX, mY);
      if (((KilnMenu)this.menu).kiln.hasNextMixingRecipe()
         && mX >= this.leftPos + 103
         && mX <= this.leftPos + 107
         && mY >= this.topPos + 40
         && mY <= this.topPos + 48) {
         graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_right"), mX, mY);
      } else if (((KilnMenu)this.menu).kiln.hasPrevMixingRecipe()
         && mX >= this.leftPos + 69
         && mX <= this.leftPos + 73
         && mY >= this.topPos + 40
         && mY <= this.topPos + 48) {
         graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.mixing_left"), mX, mY);
      }

      if (this.isHovering(18, 5, 15, 76, mX, mY)) {
         if (!((KilnMenu)this.menu).kiln.getLeftBarId().equals(Optional.of(KilnMeltingRecipe.EMPTY)) && ((KilnMenu)this.menu).kiln.getMoltenAmount() > 0) {
            String valueText = ((KilnMenu)this.menu).kiln.getMoltenAmount() + "/" + ((KilnMenu)this.menu).kiln.getMaxMoltenAmount();
            this.leftBarMaterial.ifPresent(moltenMaterial -> this.renderMaterialTooltip(graphics, mX, mY, moltenMaterial, valueText));
         } else {
            graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.empty"), mX, mY);
         }
      }

      if (this.isHovering(143, 5, 15, 76, mX, mY)) {
         if (!((KilnMenu)this.menu).kiln.getRightBarId().equals(Optional.of(KilnMeltingRecipe.EMPTY))
            && ((KilnMenu)this.menu).kiln.getMagicMaterialAmount() > 0) {
            String valueText = ((KilnMenu)this.menu).kiln.getMagicMaterialAmount() / 4.0F + "/" + ((KilnMenu)this.menu).kiln.getMaxMoltenAmount() / 4;
            this.rightBarMaterial.ifPresent(moltenMaterial -> this.renderMaterialTooltip(graphics, mX, mY, moltenMaterial, valueText));
         } else {
            graphics.renderTooltip(this.font, Component.translatable("tooltip.tensura.kiln.empty"), mX, mY);
         }
      }
   }

   private void renderMaterialTooltip(GuiGraphics graphics, int mouseX, int mouseY, KilnMoltenMaterial material, String valueText) {
      MutableComponent materialNameComponent = Component.translatable(
         String.format("%s.molten.%s.material", material.type().getNamespace(), material.type().getPath())
      );
      MutableComponent component = Component.translatable("tooltip.tensura.kiln.molten_item", new Object[]{valueText, materialNameComponent})
         .withStyle(emtpyStyleWithMaterialColor(material));
      graphics.renderTooltip(this.font, component, mouseX, mouseY);
   }

   private static Style emtpyStyleWithMaterialColor(KilnMoltenMaterial material) {
      return Style.EMPTY.withColor(new Color(material.red(), material.green(), material.blue()).getRGB());
   }

   public boolean mouseClicked(double x, double y, int pButton) {
      int width = (this.width - this.imageWidth) / 2;
      int height = (this.height - this.imageHeight) / 2;
      if (x >= width + 103 && x < width + 107 && y >= height + 40 && y < height + 48 && ((KilnMenu)this.menu).clickMenuButton(this.minecraft.player, 0)) {
         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((KilnMenu)this.menu).containerId, 0);
         return true;
      } else {
         if (!(x >= width + 69)
            || !(x < width + 73)
            || !(y >= height + 40)
            || !(y < height + 48)
            || !((KilnMenu)this.menu).clickMenuButton(this.minecraft.player, 1)) {
            return super.mouseClicked(x, y, pButton);
         }

         if (this.minecraft.gameMode == null) {
            return false;
         }

         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         this.minecraft.gameMode.handleInventoryButtonClick(((KilnMenu)this.menu).containerId, 1);
         return true;
      }
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
         ? true
         : super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }
}
