package io.github.manasmods.tensura.menu;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.menu.container.DecraftingContainer;
import io.github.manasmods.tensura.menu.container.TensuraCraftingContainer;
import io.github.manasmods.tensura.menu.slot.DecraftingSlot;
import io.github.manasmods.tensura.menu.slot.DegenerateSlot;
import io.github.manasmods.tensura.network.s2c.OpenDegenerateMenuPayload;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class UncraftingMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
   @Generated
   private static final Logger log = LogManager.getLogger(UncraftingMenu.class);
   private final ManasSkill skill;
   private final Player player;
   public final TensuraCraftingContainer craftSlots;
   public final DecraftingContainer decraftSlots;
   public final ResultContainer craftResultSlots;
   private final Slot resultSlot;
   private boolean placingRecipe;

   public UncraftingMenu(int id, Inventory inv, ManasSkill skill) {
      super(null, id);
      this.skill = skill;
      this.player = inv.player;
      this.craftSlots = new TensuraCraftingContainer(this, 3, 3);
      this.decraftSlots = new DecraftingContainer(this, 3, 3);
      this.craftResultSlots = new ResultContainer();
      this.resultSlot = new DegenerateSlot(inv.player, this, this.craftResultSlots, 0, 97, 65);
      this.addSlot(this.resultSlot);
      this.addCraftingSlots();
      this.addPlayerInventory(inv);
   }

   protected static void slotChangedCraftingGrid(
      AbstractContainerMenu abstractContainerMenu,
      Level level,
      Player player,
      CraftingContainer craftingContainer,
      ResultContainer resultContainer,
      @Nullable RecipeHolder<CraftingRecipe> recipeHolder
   ) {
      if (!level.isClientSide()) {
         CraftingInput craftingInput = craftingContainer.asCraftInput();
         ServerPlayer serverPlayer = (ServerPlayer)player;
         ItemStack itemStack = ItemStack.EMPTY;
         Optional<RecipeHolder<CraftingRecipe>> optional = level.getServer()
            .getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, craftingInput, level, recipeHolder);
         if (optional.isPresent()) {
            RecipeHolder<CraftingRecipe> recipe = optional.get();
            CraftingRecipe craftingRecipe = (CraftingRecipe)recipe.value();
            if (resultContainer.setRecipeUsed(level, serverPlayer, recipe)) {
               ItemStack itemStack2 = craftingRecipe.assemble(craftingInput, level.registryAccess());
               if (itemStack2.isItemEnabled(level.enabledFeatures())) {
                  itemStack = itemStack2;
               }
            }
         }

         resultContainer.setItem(0, itemStack);
         abstractContainerMenu.setRemoteSlot(0, itemStack);
         serverPlayer.connection
            .send(new ClientboundContainerSetSlotPacket(abstractContainerMenu.containerId, abstractContainerMenu.incrementStateId(), 0, itemStack));
      }
   }

   public void slotsChanged(Container container) {
      if (this.craftSlots.isCanPlace() && !this.placingRecipe) {
         slotChangedCraftingGrid(this, this.player.level(), this.player, this.craftSlots, this.craftResultSlots, null);
      }
   }

   public void beginPlacingRecipe() {
      if (this.craftSlots.isCanPlace()) {
         this.placingRecipe = true;
      }
   }

   public void finishPlacingRecipe(RecipeHolder<CraftingRecipe> recipeHolder) {
      if (this.craftSlots.isCanPlace()) {
         this.placingRecipe = false;
         slotChangedCraftingGrid(this, this.player.level(), this.player, this.craftSlots, this.craftResultSlots, recipeHolder);
      }
   }

   public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
      if (this.craftSlots.isCanPlace()) {
         this.craftSlots.fillStackedContents(stackedContents);
      }
   }

   public void clearCraftingContent() {
      if (this.craftSlots.isCanPlace()) {
         this.craftSlots.clearContent();
         this.craftResultSlots.clearContent();
      }
   }

   public boolean recipeMatches(RecipeHolder<CraftingRecipe> recipeHolder) {
      return ((CraftingRecipe)recipeHolder.value()).matches(this.craftSlots.asCraftInput(), this.player.level());
   }

   public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
      return slot.container != this.craftResultSlots && super.canTakeItemForPickAll(itemStack, slot);
   }

   public int getResultSlotIndex() {
      return 0;
   }

   public int getGridWidth() {
      return this.craftSlots.getWidth();
   }

   public int getGridHeight() {
      return this.craftSlots.getHeight();
   }

   public int getSize() {
      return 10;
   }

   public RecipeBookType getRecipeBookType() {
      return RecipeBookType.CRAFTING;
   }

   public boolean shouldMoveToInventory(int i) {
      return i != this.getResultSlotIndex();
   }

   public boolean stillValid(Player player) {
      return player.isAlive();
   }

   private void addPlayerInventory(Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 25 + l * 18, 117 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(playerInventory, i, 25 + i * 18, 175));
      }
   }

   private void addCraftingSlots() {
      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            this.addSlot(new Slot(this.craftSlots, y + x * 3, 20 + y * 18, 47 + x * 18) {
               public boolean mayPlace(ItemStack pStack) {
                  if (!UncraftingMenu.this.resultSlot.hasItem() && UncraftingMenu.this.decraftSlots.isEmpty()) {
                     if (!UncraftingMenu.this.craftSlots.isCanPlace()) {
                        UncraftingMenu.this.craftSlots.setCanPlace(true);
                     }

                     return true;
                  } else {
                     return UncraftingMenu.this.craftSlots.isCanPlace();
                  }
               }
            });
         }
      }

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 3; column++) {
            this.addSlot(new DecraftingSlot(this.player, this, this.decraftSlots, column + row * 3, 138 + column * 18, 47 + row * 18));
         }
      }
   }

   public boolean clickMenuButton(Player player, int i) {
      if (i == -1) {
         ManasSkillInstance instance = this.getSkillInstance(player);
         if (instance != null) {
            this.removed(player);
            if (player instanceof ServerPlayer serverPlayer) {
               serverPlayer.nextContainerCounter();
               NetworkManager.sendToPlayer(
                  serverPlayer,
                  new OpenDegenerateMenuPayload(
                     OpenDegenerateMenuPayload.MenuType.ENCHANTING, serverPlayer.containerCounter, player.getId(), instance.getSkill().getRegistryName()
                  )
               );
               player.containerMenu = new SynthesisSeparationMenu(serverPlayer.containerCounter, player.getInventory(), instance.getSkill());
               serverPlayer.initMenu(player.containerMenu);
               ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   private ManasSkillInstance getSkillInstance(Player player) {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(this.skill);
      return optional.orElse(null);
   }

   public void removed(Player pPlayer) {
      this.clearContainer(pPlayer, this.craftSlots);
      if (!this.resultSlot.hasItem()) {
         this.clearContainer(pPlayer, this.decraftSlots);
      } else {
         if (!this.craftSlots.isCanPlace()) {
            this.clearContainer(pPlayer, this.craftResultSlots);
         }

         this.decraftSlots.clearContent();
      }

      super.removed(pPlayer);
   }

   public ItemStack quickMoveStack(Player player, int i) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemStack2 = slot.getItem();
         itemStack = itemStack2.copy();
         if (i == 0) {
            if (!this.moveItemStackTo(itemStack2, 19, 55, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemStack2, itemStack);
         } else if (i >= 10 && i < 19) {
            if (!this.moveItemStackTo(itemStack2, 19, 55, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemStack2, itemStack);
         } else if (i >= 19 && i < 55) {
            if (!this.craftSlots.isCanPlace() || !this.moveItemStackTo(itemStack2, 1, 10, false)) {
               if (i < 46) {
                  if (!this.moveItemStackTo(itemStack2, 46, 55, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (!this.moveItemStackTo(itemStack2, 19, 46, false)) {
                  return ItemStack.EMPTY;
               }
            }
         } else if (!this.moveItemStackTo(itemStack2, 19, 55, false)) {
            return ItemStack.EMPTY;
         }

         if (itemStack2.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemStack2.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, itemStack2);
      }

      return itemStack;
   }

   @Generated
   public ManasSkill getSkill() {
      return this.skill;
   }

   @Generated
   public Player getPlayer() {
      return this.player;
   }
}
