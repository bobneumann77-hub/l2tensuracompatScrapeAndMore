package io.github.manasmods.tensura.menu;

import com.google.common.collect.Lists;
import io.github.manasmods.tensura.recipe.SmithingBenchRecipe;
import io.github.manasmods.tensura.recipe.input.SmithingBenchRecipeInput;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.menu.TensuraMenuTypes;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import java.util.List;
import lombok.Generated;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SmithingBenchMenu extends AbstractContainerMenu {
   @Generated
   private static final Logger log = LogManager.getLogger(SmithingBenchMenu.class);
   private final ContainerLevelAccess access;
   private final DataSlot selectedRecipeIndex = DataSlot.standalone();
   private final List<RecipeHolder<SmithingBenchRecipe>> recipes = Lists.newArrayList();
   private final Player player;
   public final Level level;
   private final Inventory inventory;
   private final ResultContainer resultContainer = new ResultContainer();
   private final Slot resultSlot;

   public SmithingBenchMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
      this(pContainerId, inv, ContainerLevelAccess.NULL);
   }

   public SmithingBenchMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess access) {
      super((MenuType)TensuraMenuTypes.SMITHING_BENCH.get(), pContainerId);
      this.access = access;
      this.player = pPlayerInventory.player;
      this.level = this.player.level();
      this.inventory = pPlayerInventory;
      this.addPlayerInventory();
      this.addPlayerHotbar();
      this.addDataSlot(this.selectedRecipeIndex).set(-1);
      this.resultSlot = this.addSlot(
         new Slot(this.resultContainer, 0, 126, 81) {
            public boolean mayPlace(ItemStack pStack) {
               return false;
            }

            public boolean mayPickup(Player pPlayer) {
               return SmithingBenchMenu.this.canCraft();
            }

            public void onTake(Player pPlayer, ItemStack pStack) {
               if (SmithingBenchMenu.this.isValidRecipeIndex(SmithingBenchMenu.this.selectedRecipeIndex.get())) {
                  if (!SmithingBenchMenu.this.player.hasInfiniteMaterials()) {
                     ((SmithingBenchRecipe)SmithingBenchMenu.this.recipes.get(SmithingBenchMenu.this.selectedRecipeIndex.get()).value())
                        .takeItemsFrom(SmithingBenchMenu.this.inventory);
                     SmithingBenchMenu.this.inventory.setChanged();
                  }

                  SmithingBenchMenu.this.setupResultSlot();
               }

               super.onTake(pPlayer, pStack);
            }
         }
      );
      this.setupRecipeList();
   }

   private void addPlayerInventory() {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(this.inventory, l + i * 9 + 9, 17 + l * 18, 112 + i * 18));
         }
      }
   }

   private void addPlayerHotbar() {
      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(this.inventory, i, 17 + i * 18, 170));
      }
   }

   public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
      return pSlot.container != this.inventory && super.canTakeItemForPickAll(pStack, pSlot);
   }

   public boolean stillValid(Player pPlayer) {
      return (Boolean)this.access
         .evaluate(
            (level, blockPos) -> level.getBlockState(blockPos).is((Block)TensuraBlocks.SMITHING_BENCH.get())
               && pPlayer.distanceToSqr(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5) <= 64.0,
            true
         );
   }

   public boolean canCraft() {
      if (!this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
         return false;
      } else {
         return this.player.hasInfiniteMaterials()
            ? true
            : ((SmithingBenchRecipe)this.recipes.get(this.selectedRecipeIndex.get()).value()).matches(this.getInput(), this.level);
      }
   }

   public int getSelectedRecipeIndex() {
      return this.selectedRecipeIndex.get();
   }

   public boolean clickMenuButton(Player pPlayer, int pId) {
      if (this.isValidRecipeIndex(pId)) {
         this.selectedRecipeIndex.set(pId);
         this.setupResultSlot();
         return true;
      } else {
         return super.clickMenuButton(pPlayer, pId);
      }
   }

   private boolean isValidRecipeIndex(int pRecipeIndex) {
      return pRecipeIndex >= 0 && pRecipeIndex < this.recipes.size();
   }

   private void setupRecipeList() {
      this.recipes.clear();
      this.selectedRecipeIndex.set(-1);
      this.resultSlot.set(ItemStack.EMPTY);
      this.recipes
         .addAll(
            this.level
               .getRecipeManager()
               .getAllRecipesFor((RecipeType)TensuraRecipes.SMITHING_BENCH_TYPE.get())
               .stream()
               .filter(recipe -> ((SmithingBenchRecipe)recipe.value()).hasUnlocked(this.getInput()))
               .sorted(SmithingBenchRecipe.getComparator())
               .toList()
         );
   }

   private void setupResultSlot() {
      if (!this.level.isClientSide()) {
         if (this.recipes.isEmpty()) {
            this.resultSlot.set(ItemStack.EMPTY);
         } else if (this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            RecipeHolder<SmithingBenchRecipe> smithingBenchRecipe = this.getRecipes().get(this.selectedRecipeIndex.get());
            this.resultContainer.setRecipeUsed(smithingBenchRecipe);
            this.resultContainer.setItem(0, ((SmithingBenchRecipe)smithingBenchRecipe.value()).assemble(this.getInput(), this.level.registryAccess()));
         }

         this.broadcastChanges();
      }
   }

   public SmithingBenchRecipeInput getInput() {
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(this.player);
      return new SmithingBenchRecipeInput(this.inventory, data.getKnownSchematics(), this.player.hasInfiniteMaterials());
   }

   public void removed(Player pPlayer) {
      this.resultContainer.removeItemNoUpdate(1);
      super.removed(pPlayer);
   }

   public ItemStack quickMoveStack(Player player, int i) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemStack2 = slot.getItem();
         itemStack = itemStack2.copy();
         if (i == 36) {
            if (!this.canCraft()) {
               return ItemStack.EMPTY;
            }

            if (!this.moveItemStackTo(itemStack2, 0, 36, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemStack2, itemStack);
         } else if (i >= 0 && i <= 36) {
            if (i < 27) {
               if (!this.moveItemStackTo(itemStack2, 27, 36, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(itemStack2, 0, 27, false)) {
               return ItemStack.EMPTY;
            }
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
   public ContainerLevelAccess getAccess() {
      return this.access;
   }

   @Generated
   public List<RecipeHolder<SmithingBenchRecipe>> getRecipes() {
      return this.recipes;
   }

   @Generated
   public Player getPlayer() {
      return this.player;
   }
}
