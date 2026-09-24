package io.github.manasmods.tensura.menu;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.subclass.IResearcherEnchanter;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.menu.container.TensuraCraftingContainer;
import io.github.manasmods.tensura.menu.slot.SpatialSlot;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ResearcherStorageMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
   @Generated
   private static final Logger log = LogManager.getLogger(ResearcherStorageMenu.class);
   private final ManasSkill skill;
   private final Player player;
   private final LivingEntity storageOwner;
   private final int page;
   private final SpatialStorageContainer container;
   private final TensuraCraftingContainer craftSlots;
   private final ResultContainer craftResultSlots;
   private boolean placingRecipe;
   private final SpatialStorageContainer furnaceInputSlots;
   private final Slot furnaceInputSlot;
   private final Slot furnaceResultSlot;
   private final ResultContainer furnaceResultSlots;

   public ResearcherStorageMenu(int id, Inventory inv, LivingEntity storageOwner, SpatialStorageContainer container, ManasSkill skill, int page) {
      super(null, id);
      this.skill = skill;
      this.player = inv.player;
      this.storageOwner = storageOwner;
      this.page = page;
      this.container = container;
      container.startOpen(this.player);
      this.craftSlots = new TensuraCraftingContainer(this, 3, 3);
      this.craftResultSlots = new ResultContainer();
      this.furnaceInputSlots = new SpatialStorageContainer(1, 128);
      this.furnaceInputSlot = new Slot(this.furnaceInputSlots, 0, 183, 89) {
         public void set(ItemStack pStack) {
            super.set(pStack);
            ResearcherStorageMenu.this.slotChangedFurnaceInput();
         }
      };
      this.furnaceResultSlots = new ResultContainer() {
         public int getMaxStackSize() {
            return ResearcherStorageMenu.this.getMaxStack();
         }
      };
      this.furnaceResultSlot = new Slot(this.furnaceResultSlots, 0, 224, 89) {
         public boolean mayPlace(ItemStack pStack) {
            return false;
         }

         public void onTake(Player pPlayer, ItemStack pStack) {
            super.onTake(pPlayer, pStack);
            if (!ResearcherStorageMenu.this.furnaceInputSlots.isEmpty()) {
               ResearcherStorageMenu.this.slotChangedFurnaceInput();
            }
         }
      };
      this.addPlayerInventory(inv);
      this.addCraftingSlots();
      this.addFurnaceSlots();
      this.addSpatialSlots();
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
      if (!this.placingRecipe) {
         slotChangedCraftingGrid(this, this.player.level(), this.player, this.craftSlots, this.craftResultSlots, null);
      }
   }

   public void beginPlacingRecipe() {
      this.placingRecipe = true;
   }

   public void finishPlacingRecipe(RecipeHolder<CraftingRecipe> recipeHolder) {
      this.placingRecipe = false;
      slotChangedCraftingGrid(this, this.player.level(), this.player, this.craftSlots, this.craftResultSlots, recipeHolder);
   }

   public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
      this.craftSlots.fillStackedContents(stackedContents);
   }

   public void clearCraftingContent() {
      this.craftSlots.clearContent();
      this.craftResultSlots.clearContent();
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

   private void slotChangedFurnaceInput() {
      if (this.player instanceof ServerPlayer serverPlayer) {
         ServerLevel var15 = serverPlayer.serverLevel();
         MinecraftServer server = serverPlayer.getServer();
         if (server != null) {
            SingleRecipeInput input = new SingleRecipeInput(this.furnaceInputSlots.getItem(0));
            Optional<RecipeHolder<SmeltingRecipe>> optional = server.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, var15);
            if (!optional.isEmpty()) {
               RecipeHolder<SmeltingRecipe> recipe = optional.get();
               SmeltingRecipe craftingRecipe = (SmeltingRecipe)recipe.value();
               if (this.furnaceResultSlots.setRecipeUsed(var15, serverPlayer, recipe)) {
                  ItemStack result = craftingRecipe.assemble(input, var15.registryAccess());
                  if (result.isItemEnabled(var15.enabledFeatures())) {
                     ItemStack oldResult = this.furnaceResultSlots.getItem(0);
                     if (oldResult.isEmpty() || oldResult.is(result.getItem())) {
                        if (this.furnaceResultSlots.setRecipeUsed(var15, serverPlayer, optional.get())) {
                           int countToSet = 0;
                           int countToRemove = 0;
                           int recipeItemCount = result.getCount();
                           int resultContainerItemCount = oldResult.getCount();
                           int size = this.furnaceInputSlots.getItem(0).getCount();
                           if (resultContainerItemCount != this.getMaxStack()) {
                              while (resultContainerItemCount + recipeItemCount + countToSet <= this.getMaxStack() && size - countToRemove > 0) {
                                 countToSet += recipeItemCount;
                                 countToRemove++;
                              }

                              result.setCount(countToSet + resultContainerItemCount);
                              this.furnaceInputSlots.getItem(0).shrink(countToRemove);
                              this.furnaceResultSlots.setItem(0, result);
                              this.setRemoteSlot(0, result);
                              serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 0, result));
                              this.furnaceResultSlots.awardUsedRecipes(serverPlayer, List.of(result));
                              serverPlayer.playNotifySound(SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean stillValid(Player player) {
      return player.isAlive();
   }

   public int getMaxStack() {
      return this.container.getMaxStackSize();
   }

   public int getSpatialSize() {
      return Math.min(this.container.getContainerSize() - this.page * 27, 27);
   }

   private void addPlayerInventory(Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 119 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 177));
      }
   }

   private void addSpatialSlots() {
      int slotIndex = 27 * this.page;
      int size = this.container.getContainerSize() - this.page * 27;

      for (int i = 0; i < 3 && size > 0; i++) {
         for (int j = 0; j < 9 && size > 0; j++) {
            this.addSlot(new SpatialSlot(this.container, slotIndex, 8 + j * 18, 44 + i * 18));
            slotIndex++;
            size--;
         }
      }
   }

   private void addCraftingSlots() {
      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            this.addSlot(new Slot(this.craftSlots, y + x * 3, 175 + y * 18, 32 + x * 18));
         }
      }

      this.addSlot(new ResultSlot(this.player, this.craftSlots, this.craftResultSlots, 0, 232, 50));
   }

   private void addFurnaceSlots() {
      this.addSlot(this.furnaceInputSlot);
      this.addSlot(this.furnaceResultSlot);
   }

   public boolean clickMenuButton(Player player, int i) {
      if (i == -1) {
         ManasSkillInstance instance = this.getSkillInstance(player);
         if (instance != null && instance.getSkill() instanceof IResearcherEnchanter enchanter) {
            this.removed(player);
            if (player instanceof ServerPlayer serverPlayer) {
               enchanter.openEnchantingMenu(serverPlayer, this.getStorageOwner(), instance);
            }
         }

         return true;
      } else if (i == 0 && this.page > 0) {
         ManasSkillInstance instance = this.getSkillInstance(this.getStorageOwner());
         if (instance != null && instance.getSkill() instanceof ISpatialStorage spatialStorage) {
            this.removed(player);
            if (player instanceof ServerPlayer serverPlayer) {
               spatialStorage.openSpatialStoragePage(serverPlayer, this.getStorageOwner(), instance, this.page - 1);
            }
         }

         return true;
      } else if (i == 1 && this.page < (this.container.getContainerSize() - 1) / 27) {
         ManasSkillInstance instance = this.getSkillInstance(this.getStorageOwner());
         if (instance != null && instance.getSkill() instanceof ISpatialStorage spatialStorage) {
            this.removed(player);
            if (player instanceof ServerPlayer serverPlayer) {
               spatialStorage.openSpatialStoragePage(serverPlayer, this.getStorageOwner(), instance, this.page + 1);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   private ManasSkillInstance getSkillInstance(LivingEntity owner) {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(owner).getSkill(this.skill);
      return optional.orElse(null);
   }

   public void removed(Player pPlayer) {
      ManasSkillInstance instance = this.getSkillInstance(this.getStorageOwner());
      if (instance != null && instance.getSkill() instanceof ISpatialStorage spatialStorage) {
         spatialStorage.saveContainer(instance, this.getStorageOwner(), this.container);
      }

      this.container.stopOpen(pPlayer);
      this.clearContainer(pPlayer, this.craftSlots);
      this.clearContainer(pPlayer, this.furnaceInputSlots);
      this.clearContainer(pPlayer, this.furnaceResultSlots);
      super.removed(pPlayer);
   }

   public ItemStack quickMoveStack(Player pPlayer, int i) {
      ItemStack copy = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(i);
      if (!slot.hasItem()) {
         return copy;
      }

      ItemStack stack = slot.getItem();
      copy = stack.copy();
      if (i == 45 || i == 47) {
         stack.getItem().onCraftedBy(stack, this.player.level(), this.player);
         if (!this.moveItemStackTo(stack, 0, 36, false) && !this.moveItemStackTo(stack, 48, 48 + this.getSpatialSize(), false)) {
            return ItemStack.EMPTY;
         }

         slot.onQuickCraft(stack, copy);
      } else if (i >= 0 && i < 36) {
         if (i < 27) {
            if (!this.moveItemStackTo(stack, 48, 48 + this.getSpatialSize(), false) && !this.moveItemStackTo(stack, 27, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(stack, 48, 48 + this.getSpatialSize(), false) && !this.moveItemStackTo(stack, 0, 27, false)) {
            return ItemStack.EMPTY;
         }
      } else if (i >= 48 && i <= 48 + this.getSpatialSize()) {
         if (!this.moveItemStackTo(stack, 0, 36, false)) {
            return ItemStack.EMPTY;
         }
      } else if (!this.moveItemStackTo(stack, 0, 27, false) && !this.moveItemStackTo(stack, 48, 48 + this.getSpatialSize(), false)) {
         return ItemStack.EMPTY;
      }

      if (stack.isEmpty()) {
         slot.setByPlayer(ItemStack.EMPTY);
      } else {
         slot.setChanged();
      }

      if (stack.getCount() == copy.getCount()) {
         return ItemStack.EMPTY;
      }

      slot.onTake(this.player, stack);
      return copy;
   }

   protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
      boolean flag = false;
      int i = pStartIndex;
      if (pReverseDirection) {
         i = pEndIndex - 1;
      }

      if (pStack.isStackable()) {
         while (!pStack.isEmpty() && (pReverseDirection ? i >= pStartIndex : i < pEndIndex)) {
            Slot slot = (Slot)this.slots.get(i);
            ItemStack itemstack = slot.getItem();
            if (!itemstack.isEmpty() && slot.mayPlace(pStack) && ItemStack.isSameItemSameComponents(pStack, itemstack)) {
               int j = itemstack.getCount() + pStack.getCount();
               int maxSize = i >= 48 ? this.container.getMaxStackSize() : Math.min(itemstack.getMaxStackSize(), slot.getMaxStackSize());
               if (j <= maxSize) {
                  pStack.setCount(0);
                  itemstack.setCount(j);
                  slot.setChanged();
                  flag = true;
               } else if (itemstack.getCount() < maxSize) {
                  pStack.shrink(maxSize - itemstack.getCount());
                  itemstack.setCount(maxSize);
                  slot.setChanged();
                  flag = true;
               }
            }

            if (pReverseDirection) {
               i--;
            } else {
               i++;
            }
         }
      }

      if (!pStack.isEmpty()) {
         if (pReverseDirection) {
            i = pEndIndex - 1;
         } else {
            i = pStartIndex;
         }

         while (pReverseDirection ? i >= pStartIndex : i < pEndIndex) {
            Slot pSlot = (Slot)this.slots.get(i);
            ItemStack stack = pSlot.getItem();
            if (stack.isEmpty() && pSlot.mayPlace(pStack)) {
               if (pStack.getCount() > pSlot.getMaxStackSize()) {
                  pSlot.set(pStack.split(pSlot.getMaxStackSize()));
               } else {
                  pSlot.set(pStack.split(pStack.getCount()));
               }

               pSlot.setChanged();
               flag = true;
               break;
            }

            if (pReverseDirection) {
               i--;
            } else {
               i++;
            }
         }
      }

      return flag;
   }

   @Generated
   public ManasSkill getSkill() {
      return this.skill;
   }

   @Generated
   public Player getPlayer() {
      return this.player;
   }

   @Generated
   public LivingEntity getStorageOwner() {
      return this.storageOwner;
   }
}
