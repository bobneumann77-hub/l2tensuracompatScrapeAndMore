package io.github.manasmods.tensura.client.screen;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.client.screen.templates.IScrollBar;
import io.github.manasmods.tensura.client.screen.templates.SimpleScreen;
import io.github.manasmods.tensura.network.c2s.RequestIllusionItemPacket;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.util.client.RenderHelper;
import io.github.manasmods.tensura.util.client.ScreenHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class IllusionItemScreen extends SimpleScreen implements IScrollBar {
   protected static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/falsifier/falsifier.png");
   protected final ManasSkill skill;
   protected final List<Item> fullItems = BuiltInRegistries.ITEM.stream().filter(item -> item != Items.AIR).toList();
   protected float scrollOffset;
   protected boolean scrolling;
   protected int listStartIndex;
   protected EditBox searchField;
   protected String nameFilter = "";
   protected List<Item> items = new ArrayList<>();
   protected boolean selectedArmor = false;
   protected EquipmentSlot selectedSlot = null;
   protected int listX = -1;
   protected int listY = -1;
   protected ItemStack headSlot;
   protected ItemStack chestSlot;
   protected ItemStack legsSlot;
   protected ItemStack feetSlot;
   protected ItemStack mainHand;
   protected ItemStack offHand;

   public IllusionItemScreen(ManasSkill skill) {
      super(skill.getName(), 105, 103);
      this.skill = skill;
      this.shouldFade = false;
   }

   @Override
   public void init() {
      super.init();
      this.searchField = new EditBox(this.font, 0, 0, 65, 10, Component.empty());
      this.searchField.setBordered(false);
      this.searchField.setResponder(s -> {
         this.nameFilter = s;
         this.setScrollOffset(0.0F);
         this.setListStartIndex(0);
         this.updateFilteredItems();
      });
      this.addRenderableWidget(this.searchField);
      if (this.nameFilter == null) {
         this.nameFilter = "";
      } else {
         this.searchField.setValue(this.nameFilter);
      }

      this.headSlot = RequestIllusionItemPacket.getFalsifierItemForScreen(this.player, this.skill, EquipmentSlot.HEAD);
      this.chestSlot = RequestIllusionItemPacket.getFalsifierItemForScreen(this.player, this.skill, EquipmentSlot.CHEST);
      this.legsSlot = RequestIllusionItemPacket.getFalsifierItemForScreen(this.player, this.skill, EquipmentSlot.LEGS);
      this.feetSlot = RequestIllusionItemPacket.getFalsifierItemForScreen(this.player, this.skill, EquipmentSlot.FEET);
      this.mainHand = RequestIllusionItemPacket.getFalsifierItemForScreen(this.player, this.skill, EquipmentSlot.MAINHAND);
      this.offHand = RequestIllusionItemPacket.getFalsifierItemForScreen(this.player, this.skill, EquipmentSlot.OFFHAND);
      if (this.selectedSlot != null) {
         this.listX = this.selectedArmor ? guiLeft - 93 : guiLeft + 105;
         this.listY = guiTop + 21 + 18 * (this.selectedArmor ? 4 - this.selectedSlot.getFilterFlag() : 2 + this.selectedSlot.getIndex());
      }
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mX, int mY, float partialTick) {
      graphics.blit(BACKGROUND, guiLeft, guiTop, 0, 0, this.imageWidth, this.imageHeight);
      RenderHelper.drawCenteredText(graphics, this.font, this.title, this.width / 2, guiTop + 7, 16777215, false);
      InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, guiLeft + 28, guiTop + 23, guiLeft + 77, guiTop + 93, 30, 0.0625F, mX, mY, this.player);
      this.selectedArmor = this.selectedArmor();
      if (this.selectedSlot != null) {
         graphics.blit(BACKGROUND, this.listX, this.listY, 106.0F, this.selectedArmor ? 23.0F : 0.0F, 93, 22, 256, 256);
         graphics.blit(BACKGROUND, this.selectedArmor ? this.listX + 12 : this.listX + 2, this.listY + 22, 106.0F, 46.0F, 79, 72, 256, 256);
         this.renderScrollBar(graphics, mX, mY, 87, 104, 93, 104);
      }

      this.displayItems(graphics, mX, mY);
   }

   protected void displayItems(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      this.renderItemTexture(guiGraphics, this.headSlot, guiLeft + 10, guiTop + 23);
      this.renderItemTexture(guiGraphics, this.chestSlot, guiLeft + 10, guiTop + 41);
      this.renderItemTexture(guiGraphics, this.legsSlot, guiLeft + 10, guiTop + 59);
      this.renderItemTexture(guiGraphics, this.feetSlot, guiLeft + 10, guiTop + 77);
      this.renderItemTexture(guiGraphics, this.mainHand, guiLeft + 79, guiTop + 59);
      this.renderItemTexture(guiGraphics, this.offHand, guiLeft + 79, guiTop + 77);
      if (this.selectedSlot != null) {
         boolean hoverClear = RenderHelper.mouseOver(
            mouseX,
            mouseY,
            this.selectedArmor ? this.listX + 4 : this.listX + 76,
            this.selectedArmor ? this.listX + 17 : this.listX + 89,
            this.listY + 7,
            this.listY + 18
         );
         guiGraphics.blit(
            BACKGROUND, this.selectedArmor ? this.listX + 4 : this.listX + 76, this.listY + 7, 0.0F, hoverClear ? 115.0F : 104.0F, 13, 11, 256, 256
         );

         for (int i = this.listStartIndex; i < this.listStartIndex + 5 && i < this.items.size(); i++) {
            int buttonY = this.listY + 24 + 13 * (i - this.listStartIndex);
            boolean isHovered = RenderHelper.mouseOver(
               mouseX,
               mouseY,
               this.selectedArmor ? this.listX + 17 : this.listX + 7,
               this.selectedArmor ? this.listX + 78 : this.listX + 68,
               buttonY,
               buttonY + 13
            );
            guiGraphics.blit(BACKGROUND, this.selectedArmor ? this.listX + 17 : this.listX + 7, buttonY, 26.0F, isHovered ? 117.0F : 104.0F, 61, 13, 256, 256);
            ItemStack item = this.items.get(i).getDefaultInstance();
            Component component = (Component)(item.isEmpty() ? Component.translatable("tensura.skill.falsifier.empty") : item.getHoverName());
            RenderHelper.drawShortenedTextWithTooltip(
               guiGraphics,
               this.font,
               component,
               component,
               this.selectedArmor ? this.listX + 17 : this.listX + 7,
               buttonY,
               3,
               3,
               61,
               13,
               mouseX,
               mouseY,
               item.isEmpty() ? 11184810 : 16777215,
               true,
               this
            );
         }
      }
   }

   @Override
   public void renderTooltip(GuiGraphics graphics, int mX, int mY) {
      EquipmentSlot slot = this.getHoveredSlot(mX, mY);
      if (slot != null) {
         switch (slot) {
            case HEAD:
               this.renderItemTooltip(graphics, this.headSlot, mX, mY, true);
               break;
            case CHEST:
               this.renderItemTooltip(graphics, this.chestSlot, mX, mY, true);
               break;
            case LEGS:
               this.renderItemTooltip(graphics, this.legsSlot, mX, mY, true);
               break;
            case FEET:
               this.renderItemTooltip(graphics, this.feetSlot, mX, mY, true);
               break;
            case MAINHAND:
               this.renderItemTooltip(graphics, this.mainHand, mX, mY, true);
               break;
            case OFFHAND:
               this.renderItemTooltip(graphics, this.offHand, mX, mY, true);
         }
      } else if (this.selectedSlot != null) {
         Type type = this.selectedSlot.getType();
         if (type == Type.HAND && RenderHelper.mouseOver(mX, mY, this.listX + 76, this.listX + 89, this.listY + 7, this.listY + 18)) {
            graphics.renderTooltip(this.font, Component.translatable("tensura.skill.falsifier.clear_slot").withStyle(ChatFormatting.RED), mX, mY);
         } else {
            if (type == Type.HUMANOID_ARMOR && RenderHelper.mouseOver(mX, mY, this.listX + 4, this.listX + 17, this.listY + 7, this.listY + 18)) {
               graphics.renderTooltip(this.font, Component.translatable("tensura.skill.falsifier.clear_slot").withStyle(ChatFormatting.RED), mX, mY);
            }
         }
      }
   }

   @Override
   public boolean mouseClicked(double mX, double mY, int pButton) {
      if (this.clickedScrollBar(mX, mY)) {
         return true;
      }

      EquipmentSlot slot = this.getHoveredSlot(mX, mY);
      if (slot != null) {
         this.updateSelectedSlot(slot);
         return true;
      }

      if (this.selectedSlot != null) {
         Type type = this.selectedSlot.getType();
         if (type == Type.HAND && RenderHelper.mouseOver(mX, mY, this.listX + 76, this.listX + 89, this.listY + 7, this.listY + 18)) {
            ScreenHelper.clicked();
            this.setItem(new ItemStack(Items.AIR), this.selectedSlot);
            return true;
         }

         if (type == Type.HUMANOID_ARMOR && RenderHelper.mouseOver(mX, mY, this.listX + 4, this.listX + 17, this.listY + 7, this.listY + 18)) {
            ScreenHelper.clicked();
            this.setItem(new ItemStack(Items.AIR), this.selectedSlot);
            return true;
         }
      }

      for (int i = this.listStartIndex; i < this.listStartIndex + 5 && i < this.items.size(); i++) {
         int posY = this.listY + 24 + 13 * (i - this.listStartIndex);
         if (RenderHelper.mouseOver(
            mX, mY, this.selectedArmor ? this.listX + 17 : this.listX + 7, this.selectedArmor ? this.listX + 78 : this.listX + 68, posY, posY + 13
         )) {
            ScreenHelper.clicked();
            ItemStack item = this.items.get(i).getDefaultInstance();
            if (item.isEmpty()) {
               item = RequestIllusionItemPacket.getEmptyStack();
            }

            this.setItem(item, this.selectedSlot);
            return true;
         }
      }

      return super.mouseClicked(mX, mY, pButton);
   }

   @Override
   public boolean mouseDragged(double mX, double mY, int button, double dX, double dY) {
      return this.draggedScrollBar(mY) ? true : super.mouseDragged(mX, mY, button, dX, dY);
   }

   @Override
   public boolean mouseScrolled(double mX, double mY, double dX, double dY) {
      return this.scrolledScrollBar(dY) ? true : super.mouseScrolled(mX, mY, dX, dY);
   }

   @Override
   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.searchField.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         return true;
      } else if (this.searchField.isFocused() && this.searchField.isVisible() && pKeyCode != 256) {
         return true;
      } else {
         return this.minecraft != null && this.minecraft.options.keySwapOffhand.matches(pKeyCode, pScanCode)
            ? true
            : super.keyPressed(pKeyCode, pScanCode, pModifiers);
      }
   }

   @Nullable
   protected EquipmentSlot getHoveredSlot(double mX, double mY) {
      if (RenderHelper.mouseOver(mX, mY, guiLeft + 8, guiLeft + 27, guiTop + 21, guiTop + 40)) {
         return EquipmentSlot.HEAD;
      } else if (RenderHelper.mouseOver(mX, mY, guiLeft + 8, guiLeft + 27, guiTop + 39, guiTop + 58)) {
         return EquipmentSlot.CHEST;
      } else if (RenderHelper.mouseOver(mX, mY, guiLeft + 8, guiLeft + 27, guiTop + 57, guiTop + 76)) {
         return EquipmentSlot.LEGS;
      } else if (RenderHelper.mouseOver(mX, mY, guiLeft + 8, guiLeft + 27, guiTop + 75, guiTop + 94)) {
         return EquipmentSlot.FEET;
      } else if (RenderHelper.mouseOver(mX, mY, guiLeft + 77, guiLeft + 96, guiTop + 57, guiTop + 76)) {
         return EquipmentSlot.MAINHAND;
      } else {
         return RenderHelper.mouseOver(mX, mY, guiLeft + 77, guiLeft + 96, guiTop + 75, guiTop + 94) ? EquipmentSlot.OFFHAND : null;
      }
   }

   protected void updateSelectedSlot(EquipmentSlot slot) {
      ScreenHelper.clicked();
      this.selectedSlot = slot;
      this.selectedArmor = this.selectedArmor();
      this.listX = this.selectedArmor ? guiLeft - 93 : guiLeft + 105;
      this.listY = guiTop + 21 + 18 * (this.selectedArmor ? 4 - this.selectedSlot.getFilterFlag() : 2 + this.selectedSlot.getIndex());
      this.searchField.setX(this.selectedArmor ? this.listX + 21 : this.listX + 7);
      this.searchField.setY(this.listY + 7);
      this.searchField.setValue("");
      this.setScrollOffset(0.0F);
      this.setListStartIndex(0);
      this.updateFilteredItems();
   }

   protected void renderItemTooltip(GuiGraphics graphics, ItemStack stack, int pMouseX, int pMouseY, boolean checkEmpty) {
      if (!checkEmpty || !stack.isEmpty()) {
         if (!stack.isEmpty() && !stack.has((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get())) {
            graphics.renderTooltip(this.font, stack, pMouseX, pMouseY);
         } else {
            graphics.renderTooltip(this.font, Component.translatable("tensura.skill.falsifier.empty").withStyle(ChatFormatting.RED), pMouseX, pMouseY);
         }
      }
   }

   protected void renderItemTexture(GuiGraphics graphics, ItemStack stack, int x, int y) {
      if (stack != null && !stack.isEmpty()) {
         graphics.renderItem(stack, x, y);
      }
   }

   protected boolean selectedArmor() {
      return this.selectedSlot == null ? false : this.selectedSlot.getType().equals(Type.HUMANOID_ARMOR);
   }

   protected void updateFilteredItems() {
      this.items.clear();
      this.items.add(Items.AIR);
      if (this.nameFilter != null && !this.nameFilter.isEmpty() && !this.nameFilter.isBlank()) {
         this.items
            .addAll(
               this.fullItems
                  .stream()
                  .filter(
                     item -> item.getDescription().getString().toLowerCase().contains(this.nameFilter.toLowerCase())
                        && this.canItemBeEquippedInSlot(item, this.selectedSlot)
                  )
                  .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).getPath()))
                  .toList()
            );
      } else {
         this.items
            .addAll(
               this.fullItems
                  .stream()
                  .filter(item -> this.canItemBeEquippedInSlot(item, this.selectedSlot))
                  .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).getPath()))
                  .toList()
            );
      }
   }

   protected void setItem(ItemStack item, EquipmentSlot slot) {
      switch (slot) {
         case HEAD:
            this.headSlot = item;
            break;
         case CHEST:
            this.chestSlot = item;
            break;
         case LEGS:
            this.legsSlot = item;
            break;
         case FEET:
            this.feetSlot = item;
            break;
         case MAINHAND:
            this.mainHand = item;
            break;
         case OFFHAND:
            this.offHand = item;
      }

      CompoundTag tag = null;
      if (item.has((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get())) {
         tag = new CompoundTag();
         tag.putBoolean(TensuraDataComponents.DUMMY_ITEM.getRegisteredName(), true);
      } else if (!item.isEmpty()) {
         tag = (CompoundTag)item.save(this.player.level().registryAccess());
      }

      NetworkManager.sendToServer(RequestIllusionItemPacket.getDefault(this.skill, tag, slot));
   }

   protected boolean canItemBeEquippedInSlot(Item item, EquipmentSlot slot) {
      if (slot == null) {
         return false;
      } else if (slot.getType().equals(Type.HAND) || item.equals(Items.AIR)) {
         return true;
      } else {
         return item instanceof Equipable equipable ? equipable.getEquipmentSlot().equals(slot) : slot.equals(EquipmentSlot.HEAD) && item instanceof BlockItem;
      }
   }

   @Override
   public int getScrollBarX() {
      return this.selectedArmor ? this.listX + 80 : this.listX + 70;
   }

   @Override
   public int getScrollBarY() {
      return this.listY + 24;
   }

   @Override
   public int getScrollBarWidth() {
      return 6;
   }

   @Override
   public int getScrollBarHeight() {
      return 23;
   }

   @Override
   public int getScrollBarTotalSpace() {
      return 65;
   }

   @Override
   public int getScrollBarListSize() {
      return this.items.size();
   }

   @Override
   public int getScrollBarRenderCount() {
      return 5;
   }

   @Override
   public int getScrollBarTextureWidth() {
      return 256;
   }

   @Override
   public int getScrollBarTextureHeight() {
      return 256;
   }

   @Override
   public ResourceLocation getScrollBarTexture() {
      return BACKGROUND;
   }

   @Generated
   public ManasSkill getSkill() {
      return this.skill;
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
