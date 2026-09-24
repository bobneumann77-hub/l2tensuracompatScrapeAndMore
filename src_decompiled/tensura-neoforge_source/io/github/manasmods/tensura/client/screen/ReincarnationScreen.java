package io.github.manasmods.tensura.client.screen;

import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.util.client.RenderHelper;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class ReincarnationScreen extends AbstractContainerScreen<ReincarnationMenu> {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/reincarnation/gui.png");
   private final Component submitButtonText = Component.translatable("tensura.reincarnation.submit");
   private int prevButtonX = 0;
   private int prevButtonY = 0;
   private int nextButtonX = 0;
   private int nextButtonY = 0;
   private int submitButtonX = 0;
   private int submitButtonY = 0;
   private int randomIndex = 0;
   private int scale;
   private int tick = 0;

   public ReincarnationScreen(ReincarnationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
      super(pMenu, pPlayerInventory, pTitle.copy().withStyle(ChatFormatting.WHITE));
   }

   protected void init() {
      super.init();
      this.titleLabelY = -this.topPos - 9 - 1;
      this.inventoryLabelY = -this.topPos - 9 - 1;
      this.prevButtonX = this.leftPos + 7;
      this.prevButtonY = this.topPos + 6;
      this.nextButtonX = this.leftPos + this.imageWidth - 18 - 6;
      this.nextButtonY = this.topPos + 6;
      this.submitButtonX = this.leftPos + 7;
      this.submitButtonY = this.topPos + this.imageHeight - 19;
      this.scale = this.getSelectedScale();
   }

   protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2;
      guiGraphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight);
      this.renderButtons(guiGraphics, pMouseX, pMouseY);
      this.renderRace(guiGraphics, pMouseX, pMouseY);
      if (this.getSelectedIndex() == ((ReincarnationMenu)this.getMenu()).getRacePool().size()) {
         List<Float> size = Arrays.asList(0.5F, 0.75F, 1.0F, 1.25F, 1.5F, 1.75F, 2.0F, 2.25F, 2.5F, 2.75F, 3.0F);
         this.scale = Math.round(30.0F * (size.get(this.randomIndex) / 2.0F));
         this.tick++;
         if (this.tick % 40 == 0) {
            this.randomIndex = this.minecraft.player.getRandom().nextInt(size.size());
         }
      }

      InventoryScreen.renderEntityInInventoryFollowsMouse(
         guiGraphics, this.leftPos + 6, this.topPos + 26, this.leftPos + 71, this.topPos + 147, this.scale, 0.0625F, pMouseX, pMouseY, this.minecraft.player
      );
   }

   protected void renderRace(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
      ManasRace race = this.getSelectedRace();
      if (race != null) {
         guiGraphics.drawString(
            this.font,
            Objects.requireNonNull(race.getName()).withStyle(ChatFormatting.BOLD),
            (int)((this.imageWidth - this.font.width(race.getName())) / 2.0F + this.leftPos),
            this.topPos + 7,
            Color.WHITE.getRGB(),
            false
         );
         if (race instanceof DefaultRace defaultRace) {
            RenderHelper.drawScaledTextInArea(
               guiGraphics,
               this.font,
               Component.translatable(
                  "tensura.reincarnation.infobox.1",
                  new Object[]{
                     race.getDifficulty().asText(),
                     defaultRace.getDefaultConfig().getMinAura(),
                     defaultRace.getDefaultConfig().getMaxAura(),
                     defaultRace.getDefaultConfig().getMinMagicule(),
                     defaultRace.getDefaultConfig().getMaxMagicule()
                  }
               ),
               this.leftPos + this.imageWidth - 95,
               this.topPos + 26,
               91.0F,
               35.0F,
               Color.WHITE,
               4.0F
            );
            MutableComponent intrinsicSkill = Component.translatable("tensura.skill.empty").withStyle(ChatFormatting.GRAY);
            List<ManasSkill> list = defaultRace.getRenderingIntrinsicSkills(race.createDefaultInstance(), ((ReincarnationMenu)this.menu).getPlayer());
            if (!list.isEmpty()) {
               intrinsicSkill = Component.empty();

               for (int i = 0; i < list.size(); i++) {
                  intrinsicSkill.append(list.get(i).getChatDisplayName(true));
                  if (i + 1 != list.size()) {
                     intrinsicSkill.append(", ");
                  }
               }
            }

            RenderHelper.drawScaledTextInArea(
               guiGraphics,
               this.font,
               Component.translatable("tensura.reincarnation.infobox.2", new Object[]{intrinsicSkill}),
               this.leftPos + this.imageWidth - 95,
               this.topPos + 60,
               90.0F,
               35.0F,
               Color.WHITE,
               4.0F
            );
            RenderHelper.drawScaledTextInArea(
               guiGraphics, this.font, race.getRaceDescription(), this.leftPos + this.imageWidth - 95, this.topPos + 96, 90.0F, 70.0F, Color.WHITE, 3.0F
            );
         }
      } else {
         guiGraphics.drawString(
            this.font,
            Component.translatable("tensura.reincarnation.random").withStyle(ChatFormatting.BOLD),
            (int)((this.imageWidth - this.font.width(Component.translatable("tensura.reincarnation.random"))) / 2.0F + this.leftPos),
            this.topPos + 7,
            Color.WHITE.getRGB(),
            false
         );
         RenderHelper.drawScaledTextInArea(
            guiGraphics,
            this.font,
            Component.translatable(
               "tensura.reincarnation.infobox.1",
               new Object[]{Component.translatable("tensura.reincarnation.unknown").withStyle(ChatFormatting.AQUA), "???", "???", "???", "???"}
            ),
            this.leftPos + this.imageWidth - 95,
            this.topPos + 26,
            91.0F,
            35.0F,
            Color.WHITE,
            4.0F
         );
         RenderHelper.drawScaledTextInArea(
            guiGraphics,
            this.font,
            Component.translatable(
               "tensura.reincarnation.infobox.2", new Object[]{Component.translatable("tensura.reincarnation.unknown").withStyle(ChatFormatting.GRAY)}
            ),
            this.leftPos + this.imageWidth - 95,
            this.topPos + 60,
            90.0F,
            35.0F,
            Color.WHITE,
            4.0F
         );
         RenderHelper.drawScaledTextInArea(
            guiGraphics,
            this.font,
            Component.translatable("tensura.reincarnation.random_race"),
            this.leftPos + this.imageWidth - 95,
            this.topPos + 96,
            90.0F,
            70.0F,
            Color.WHITE,
            3.0F
         );
      }
   }

   protected void renderButtons(GuiGraphics graphics, int pMouseX, int pMouseY) {
      int UPrefOffset = this.mouseOverPrevButton(pMouseX, pMouseY) ? 25 : 2;
      graphics.blit(BACKGROUND, this.prevButtonX, this.prevButtonY, UPrefOffset, 181, 18, 10);
      int NextUOffset = this.mouseOverNextButton(pMouseX, pMouseY) ? 25 : 2;
      graphics.blit(BACKGROUND, this.nextButtonX, this.nextButtonY, NextUOffset, 168, 18, 10);
      int submitVOffset = this.mouseOverSubmitButton(pMouseX, pMouseY) ? 183 : 168;
      graphics.blit(BACKGROUND, this.submitButtonX, this.submitButtonY, 45, submitVOffset, 63, 12);
      graphics.drawString(
         this.font,
         this.submitButtonText,
         (int)((63 - this.font.width(this.submitButtonText)) / 2.0F + this.submitButtonX),
         this.submitButtonY + 2,
         Color.WHITE.getRGB(),
         false
      );
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (this.mouseOverNextButton(pMouseX, pMouseY) && pButton == 0) {
         int nextIndex = this.hasNextRace() ? this.getSelectedIndex() + 1 : 0;
         if (((ReincarnationMenu)this.menu).clickMenuButton(this.minecraft.player, nextIndex)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(((ReincarnationMenu)this.menu).containerId, nextIndex);
            this.scale = this.getSelectedScale();
         }

         return true;
      } else if (this.mouseOverPrevButton(pMouseX, pMouseY) && pButton == 0) {
         int nextIndex = this.hasPrevRace() ? this.getSelectedIndex() - 1 : ((ReincarnationMenu)this.getMenu()).getRacePool().size();
         if (((ReincarnationMenu)this.menu).clickMenuButton(this.minecraft.player, nextIndex)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(((ReincarnationMenu)this.menu).containerId, nextIndex);
            this.scale = this.getSelectedScale();
         }

         return true;
      } else {
         int id = ((ReincarnationMenu)this.menu).isChangeRaceOnly() ? -2 : -1;
         if (this.mouseOverSubmitButton(pMouseX, pMouseY) && pButton == 0 && ((ReincarnationMenu)this.menu).clickMenuButton(this.minecraft.player, id)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(((ReincarnationMenu)this.menu).containerId, id);
            this.onClose();
         }

         return super.mouseClicked(pMouseX, pMouseY, pButton);
      }
   }

   protected boolean mouseOverPrevButton(double pMouseX, double pMouseY) {
      if (pMouseX < this.prevButtonX) {
         return false;
      } else if (pMouseX > this.prevButtonX + 18) {
         return false;
      } else {
         return pMouseY < this.prevButtonY ? false : pMouseY <= this.prevButtonY + 10;
      }
   }

   protected boolean mouseOverNextButton(double pMouseX, double pMouseY) {
      if (pMouseX < this.nextButtonX) {
         return false;
      } else if (pMouseX > this.nextButtonX + 18) {
         return false;
      } else {
         return pMouseY < this.nextButtonY ? false : pMouseY <= this.nextButtonY + 10;
      }
   }

   protected boolean mouseOverSubmitButton(double pMouseX, double pMouseY) {
      if (pMouseX < this.submitButtonX) {
         return false;
      } else if (pMouseX > this.submitButtonX + 63) {
         return false;
      } else {
         return pMouseY < this.submitButtonY ? false : pMouseY <= this.submitButtonY + 12;
      }
   }

   protected boolean hasPrevRace() {
      return this.getSelectedIndex() > 0;
   }

   protected boolean hasNextRace() {
      return this.getSelectedIndex() < ((ReincarnationMenu)this.getMenu()).getRacePool().size();
   }

   @Nullable
   protected ManasRace getSelectedRace() {
      int selected = this.getSelectedIndex();
      return selected >= ((ReincarnationMenu)this.getMenu()).getRacePool().size() ? null : ((ReincarnationMenu)this.getMenu()).getRacePool().get(selected);
   }

   private int getSelectedScale() {
      return this.getSelectedRace() instanceof DefaultRace defaultRace ? (int)Math.round(30.0 * (1.0 + defaultRace.getDefaultConfig().getSize())) : 30;
   }

   protected int getSelectedIndex() {
      return ((ReincarnationMenu)this.menu).selectedManasRaceIndex.get();
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      return this.minecraft != null && this.minecraft.options.keyInventory.matches(pKeyCode, pScanCode)
         ? true
         : super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }
}
