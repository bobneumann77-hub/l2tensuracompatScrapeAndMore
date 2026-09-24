package io.github.manasmods.tensura.menu;

import com.google.common.collect.Lists;
import io.github.manasmods.tensura.recipe.WoodcutterRecipe;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.menu.TensuraMenuTypes;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import java.util.List;
import lombok.Generated;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class WoodcutterMenu extends AbstractContainerMenu {
   private final Level level;
   private final ContainerLevelAccess access;
   private final DataSlot selectedRecipeIndex;
   private List<RecipeHolder<WoodcutterRecipe>> recipes;
   private ItemStack input;
   private final Slot inputSlot;
   private final Slot resultSlot;
   private final Container container;
   private final ResultContainer resultContainer;
   private Runnable slotUpdateListener;
   private long lastSoundTime;

   public WoodcutterMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
      this(id, inventory, ContainerLevelAccess.NULL);
   }

   public WoodcutterMenu(int id, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
      super((MenuType)TensuraMenuTypes.WOOD_CUTTER_MENU.get(), id);
      this.level = inventory.player.level();
      this.access = containerLevelAccess;
      this.selectedRecipeIndex = DataSlot.standalone();
      this.recipes = Lists.newArrayList();
      this.container = new SimpleContainer(1) {
         public void setChanged() {
            super.setChanged();
            WoodcutterMenu.this.slotsChanged(this);
            WoodcutterMenu.this.slotUpdateListener.run();
         }
      };
      this.resultContainer = new ResultContainer();
      this.input = ItemStack.EMPTY;
      this.inputSlot = this.addSlot(new Slot(this.container, 0, 20, 33));
      this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
         public boolean mayPlace(ItemStack itemStack) {
            return false;
         }

         public void onTake(Player player, ItemStack itemStack) {
            itemStack.onCraftedBy(player.level(), player, itemStack.getCount());
            WoodcutterMenu.this.resultContainer.awardUsedRecipes(player, List.of(WoodcutterMenu.this.inputSlot.getItem()));
            ItemStack itemStack2 = WoodcutterMenu.this.inputSlot.remove(1);
            if (!itemStack2.isEmpty()) {
               WoodcutterMenu.this.setupResultSlot();
            }

            containerLevelAccess.execute((level, blockPos) -> {
               long l = level.getGameTime();
               if (WoodcutterMenu.this.lastSoundTime != l) {
                  level.playSound(null, blockPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  WoodcutterMenu.this.lastSoundTime = l;
               }
            });
            super.onTake(player, itemStack);
         }
      });
      this.slotUpdateListener = () -> {};
      this.addPlayerHotbar(inventory);
      this.addPlayerInventory(inventory);
      this.addDataSlot(this.selectedRecipeIndex);
   }

   private void addPlayerInventory(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
         }
      }
   }

   private void addPlayerHotbar(Inventory playerInventory) {
      for (int column = 0; column < 9; column++) {
         this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
      }
   }

   public boolean stillValid(Player player) {
      return stillValid(this.access, player, (Block)TensuraBlocks.WOODCUTTER.get());
   }

   public boolean clickMenuButton(Player player, int button) {
      if (this.isValidRecipeIndex(button)) {
         this.selectedRecipeIndex.set(button);
         this.setupResultSlot();
      }

      return true;
   }

   public void slotsChanged(Container container) {
      ItemStack itemStack = this.inputSlot.getItem();
      if (!itemStack.is(this.input.getItem())) {
         this.input = itemStack.copy();
         this.setupRecipeList(container, itemStack);
      }
   }

   protected SingleRecipeInput createRecipeInput(Container container) {
      return new SingleRecipeInput(container.getItem(0));
   }

   protected void setupRecipeList(Container container, ItemStack itemStack) {
      this.recipes.clear();
      this.selectedRecipeIndex.set(-1);
      this.resultSlot.set(ItemStack.EMPTY);
      if (!itemStack.isEmpty()) {
         this.recipes = this.level
            .getRecipeManager()
            .getRecipesFor((RecipeType)TensuraRecipes.WOOD_CUTTER_TYPE.get(), this.createRecipeInput(container), this.level);
      }
   }

   protected void setupResultSlot() {
      if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
         RecipeHolder<WoodcutterRecipe> recipeHolder = this.recipes.get(this.selectedRecipeIndex.get());
         ItemStack itemStack = ((WoodcutterRecipe)recipeHolder.value()).assemble(this.createRecipeInput(this.container), this.level.registryAccess());
         if (itemStack.isItemEnabled(this.level.enabledFeatures())) {
            this.resultContainer.setRecipeUsed(recipeHolder);
            this.resultSlot.set(itemStack);
         } else {
            this.resultSlot.set(ItemStack.EMPTY);
         }
      } else {
         this.resultSlot.set(ItemStack.EMPTY);
      }

      this.broadcastChanges();
   }

   @NotNull
   public MenuType<?> getType() {
      return (MenuType<?>)TensuraMenuTypes.WOOD_CUTTER_MENU.get();
   }

   public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
      return slot.container != this.resultContainer && super.canTakeItemForPickAll(itemStack, slot);
   }

   @NotNull
   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack itemStack2 = slot.getItem();
         Item item = itemStack2.getItem();
         itemStack = itemStack2.copy();
         if (index == 1) {
            item.onCraftedBy(itemStack2, player.level(), player);
            if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemStack2, itemStack);
         } else if (index == 0) {
            if (!this.moveItemStackTo(itemStack2, 2, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.level
            .getRecipeManager()
            .getRecipeFor((RecipeType)TensuraRecipes.WOOD_CUTTER_TYPE.get(), new SingleRecipeInput(itemStack2), this.level)
            .isPresent()) {
            if (!this.moveItemStackTo(itemStack2, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 2 && index < 29) {
            if (!this.moveItemStackTo(itemStack2, 29, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 29 && index < 38 && !this.moveItemStackTo(itemStack2, 2, 29, false)) {
            return ItemStack.EMPTY;
         }

         if (itemStack2.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
         }

         slot.setChanged();
         if (itemStack2.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, itemStack2);
         this.broadcastChanges();
      }

      return itemStack;
   }

   public void removed(Player player) {
      super.removed(player);
      this.resultContainer.removeItemNoUpdate(1);
      this.access.execute((level, blockPos) -> this.clearContainer(player, this.container));
   }

   public int getSelectedRecipeIndex() {
      return this.selectedRecipeIndex.get();
   }

   public int getNumRecipes() {
      return this.recipes.size();
   }

   public boolean hasInputItem() {
      return this.inputSlot.hasItem() && !this.recipes.isEmpty();
   }

   public boolean isValidRecipeIndex(int i) {
      return i >= 0 && i < this.recipes.size();
   }

   @Generated
   public List<RecipeHolder<WoodcutterRecipe>> getRecipes() {
      return this.recipes;
   }

   @Generated
   public void setSlotUpdateListener(Runnable slotUpdateListener) {
      this.slotUpdateListener = slotUpdateListener;
   }
}
