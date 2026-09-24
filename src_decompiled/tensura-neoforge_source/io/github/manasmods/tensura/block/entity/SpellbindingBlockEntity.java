package io.github.manasmods.tensura.block.entity;

import io.github.manasmods.tensura.menu.SpellbindingMenu;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SpellbindingBlockEntity extends BaseContainerBlockEntity {
   public NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
   public int spin;

   public SpellbindingBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   public SpellbindingBlockEntity(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)TensuraBlockEntities.SPELLBINDING.get(), blockPos, blockState);
   }

   @NotNull
   protected Component getDefaultName() {
      return Component.translatable("tensura.spellbinding.label");
   }

   public int getMaxStackSize() {
      return 1;
   }

   @NotNull
   protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
      return new SpellbindingMenu(i, inventory, this);
   }

   public boolean stillValid(Player pPlayer) {
      if (this.level == null) {
         return false;
      } else {
         return this.level.getBlockEntity(this.worldPosition) != this
            ? false
            : pPlayer.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
      }
   }

   protected void saveAdditional(@NotNull CompoundTag nbt, Provider provider) {
      super.saveAdditional(nbt, provider);
      ContainerHelper.saveAllItems(nbt, this.items, provider);
   }

   public void loadAdditional(CompoundTag nbt, Provider provider) {
      super.loadAdditional(nbt, provider);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(nbt, this.items, provider);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = super.getUpdateTag(provider);
      this.saveAdditional(tag, provider);
      return tag;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      for (ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @NotNull
   public ItemStack getItem(int pIndex) {
      return (ItemStack)this.items.get(pIndex);
   }

   @NotNull
   public ItemStack removeItem(int pIndex, int pCount) {
      return ContainerHelper.removeItem(this.items, pIndex, pCount);
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int pIndex) {
      return ContainerHelper.takeItem(this.items, pIndex);
   }

   public void setItem(int pIndex, ItemStack pStack) {
      this.items.set(pIndex, pStack);
      if (pStack.getCount() > this.getMaxStackSize()) {
         pStack.setCount(this.getMaxStackSize());
      }

      this.markChangedAndSync();
   }

   public void clearContent() {
      this.items.clear();
      this.markChangedAndSync();
   }

   private void markChangedAndSync() {
      if (this.level != null && !this.level.isClientSide()) {
         this.setChanged();
         BlockState state = this.getBlockState();
         this.level.sendBlockUpdated(this.worldPosition, state, state, 2);
      }
   }

   public void drops() {
      if (this.level != null) {
         SimpleContainer inventory = new SimpleContainer(this.items.size());

         for (int i = 0; i < this.items.size(); i++) {
            inventory.setItem(i, (ItemStack)this.items.get(i));
         }

         Containers.dropContents(this.level, this.worldPosition, inventory);
      }
   }

   @Generated
   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   @Generated
   public void setItems(NonNullList<ItemStack> items) {
      this.items = items;
   }
}
