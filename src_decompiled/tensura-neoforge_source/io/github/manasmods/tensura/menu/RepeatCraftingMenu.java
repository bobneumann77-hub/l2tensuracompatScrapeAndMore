package io.github.manasmods.tensura.menu;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.subclass.IRefining;
import io.github.manasmods.tensura.ability.subclass.IRepeatCrafting;
import io.github.manasmods.tensura.menu.container.TensuraCraftingContainer;
import io.github.manasmods.tensura.recipe.SmithingBenchRecipe;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
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
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class RepeatCraftingMenu<S extends ManasSkill & IRepeatCrafting<S>> extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
   @Generated
   private static final Logger log = LogManager.getLogger(RepeatCraftingMenu.class);
   private final S skill;
   private final Player player;
   private final LivingEntity storageOwner;
   public final SimpleContainer copySlots;
   public final TensuraCraftingContainer craftSlots;
   public final ResultContainer craftResultSlots;
   private boolean placingRecipe;

   public RepeatCraftingMenu(int id, Inventory inv, LivingEntity storageOwner, SimpleContainer container, S skill) {
      super(null, id);
      this.skill = skill;
      this.player = inv.player;
      this.storageOwner = storageOwner;
      this.craftResultSlots = new ResultContainer();
      this.craftSlots = new TensuraCraftingContainer(this, 3, 3);
      this.copySlots = new SimpleContainer(1);
      this.addCraftingSlots();
      this.addPlayerInventory(inv);

      for (int i = 0; i < 9; i++) {
         this.craftSlots.setItem(i, container.getItem(i));
      }

      this.copySlots.setItem(0, container.getItem(10));
      if (this.isRepeatingCrafter(this.player)) {
         this.craftResultSlots.setItem(0, container.getItem(9));
      }
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
      if (!this.isRepeatingCrafter(this.player) && !this.placingRecipe) {
         slotChangedCraftingGrid(this, this.player.level(), this.player, this.craftSlots, this.craftResultSlots, null);
      }
   }

   public void beginPlacingRecipe() {
      if (!this.isRepeatingCrafter(this.player)) {
         this.placingRecipe = true;
      }
   }

   public void finishPlacingRecipe(RecipeHolder<CraftingRecipe> recipeHolder) {
      if (!this.isRepeatingCrafter(this.player)) {
         this.placingRecipe = false;
         slotChangedCraftingGrid(this, this.player.level(), this.player, this.craftSlots, this.craftResultSlots, recipeHolder);
      }
   }

   public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
      if (!this.isRepeatingCrafter(this.player)) {
         this.craftSlots.fillStackedContents(stackedContents);
      }
   }

   public void clearCraftingContent() {
      if (!this.isRepeatingCrafter(this.player)) {
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

   @Nullable
   public ManasSkillInstance getSkillInstance(LivingEntity owner) {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(owner).getSkill(this.skill);
      return optional.orElse(null);
   }

   private boolean isRepeatingCrafter(LivingEntity owner) {
      ManasSkillInstance instance = this.getSkillInstance(owner);
      return instance == null ? false : instance.getOrCreateTag().getBoolean("Repeating");
   }

   private void addPlayerInventory(Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 45 + l * 18, 110 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(playerInventory, i, 45 + i * 18, 168));
      }
   }

   private void addCraftingSlots() {
      this.addSlot(
         new ResultSlot(this.player, this.craftSlots, this.craftResultSlots, 0, 173, 53) {
            public void onTake(Player pPlayer, ItemStack pStack) {
               if (!RepeatCraftingMenu.this.isRepeatingCrafter(pPlayer)) {
                  RecipeHolder<SmithingBenchRecipe> smithingRecipe = RepeatCraftingMenu.this.skill
                     .getCopySmithingRecipe(pPlayer.level(), RepeatCraftingMenu.this.copySlots.getItem(0), RepeatCraftingMenu.this.craftSlots);
                  RepeatCraftingMenu.this.skill.onTakeResultItems(pPlayer, RepeatCraftingMenu.this.craftSlots, smithingRecipe);
               }
            }

            protected void onQuickCraft(ItemStack pStack, int pAmount) {
               if (!RepeatCraftingMenu.this.isRepeatingCrafter(RepeatCraftingMenu.this.getPlayer())) {
                  super.onQuickCraft(pStack, pAmount);
               }
            }
         }
      );

      for (int x = 0; x < 3; x++) {
         for (int y = 0; y < 3; y++) {
            this.addSlot(new Slot(this.craftSlots, y + x * 3, 79 + y * 18, 35 + x * 18));
         }
      }

      this.addSlot(
         new Slot(this.copySlots, 0, 50, 53) {
            public void onTake(Player pPlayer, ItemStack pStack) {
               if (!RepeatCraftingMenu.this.isRepeatingCrafter(pPlayer)) {
                  RepeatCraftingMenu.slotChangedCraftingGrid(
                     RepeatCraftingMenu.this, pPlayer.level(), pPlayer, RepeatCraftingMenu.this.craftSlots, RepeatCraftingMenu.this.craftResultSlots, null
                  );
               } else {
                  super.onTake(pPlayer, pStack);
               }
            }
         }
      );
   }

   public boolean clickMenuButton(Player player, int i) {
      if (i == -1) {
         ManasSkillInstance instance = this.getSkillInstance(player);
         if (instance != null) {
            CompoundTag tag = instance.getOrCreateTag();
            boolean repeating = tag.getBoolean("Repeating");
            if (!this.craftResultSlots.isEmpty()) {
               if (repeating) {
                  player.playNotifySound(SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.PLAYERS, 1.0F, 1.0F);
                  return false;
               }

               this.craftResultSlots.clearContent();
               this.craftResultSlots.setChanged();
            }

            tag.putBoolean("Repeating", !repeating);
            instance.markDirty();
            SkillAPI.getSkillsFrom(player).markDirty();
         }

         return true;
      } else {
         if (i == 3 && this.skill instanceof IRefining<?> refining) {
            ManasSkillInstance instance = this.getSkillInstance(this.getStorageOwner());
            if (instance != null) {
               this.removed(player);
               if (player instanceof ServerPlayer serverPlayer) {
                  refining.openRefiningMenu(serverPlayer, this.getStorageOwner(), instance);
               }

               return true;
            }
         }

         return false;
      }
   }

   public void removed(Player pPlayer) {
      ManasSkillInstance instance = this.getSkillInstance(this.getStorageOwner());
      if (instance != null) {
         for (int i = 0; i < 9; i++) {
            this.skill.setItemInSpatialStorage(instance, this.getStorageOwner(), this.craftSlots.getItem(i), i);
         }

         this.skill.setItemInSpatialStorage(instance, this.getStorageOwner(), this.copySlots.getItem(0), 10);
         if (this.isRepeatingCrafter(this.getStorageOwner())) {
            this.skill.setItemInSpatialStorage(instance, this.getStorageOwner(), this.craftResultSlots.getItem(0), 9);
         }
      }

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
      if (i == 0) {
         stack.getItem().onCraftedBy(stack, this.player.level(), this.player);
         if (!this.moveItemStackTo(stack, 12, 47, false)) {
            return ItemStack.EMPTY;
         }

         slot.onQuickCraft(stack, copy);
      } else if (i >= 12 && i < 48) {
         if (!this.moveItemStackTo(stack, 1, 11, false)) {
            if (i < 39) {
               if (!this.moveItemStackTo(stack, 39, 47, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(stack, 12, 39, false)) {
               return ItemStack.EMPTY;
            }
         }
      } else if (!this.moveItemStackTo(stack, 12, 47, false)) {
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

   @Generated
   public S getSkill() {
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
