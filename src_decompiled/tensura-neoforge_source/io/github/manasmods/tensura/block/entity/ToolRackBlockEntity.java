package io.github.manasmods.tensura.block.entity;

import io.github.manasmods.tensura.block.ToolRackBlock;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity.DataComponentInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import org.jetbrains.annotations.NotNull;

public class ToolRackBlockEntity extends BlockEntity implements Container {
   private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
   private int lastInteractedSlot = -1;

   public ToolRackBlockEntity(BlockPos blockPos, BlockState blockState) {
      super((BlockEntityType)TensuraBlockEntities.TOOL_RACK.get(), blockPos, blockState);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      this.items.clear();
      ContainerHelper.loadAllItems(compoundTag, this.items, provider);
      this.lastInteractedSlot = compoundTag.getInt("last_interacted_slot");
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      ContainerHelper.saveAllItems(compoundTag, this.items, true, provider);
      compoundTag.putInt("last_interacted_slot", this.lastInteractedSlot);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = super.getUpdateTag(provider);
      this.saveAdditional(tag, provider);
      return tag;
   }

   protected void applyImplicitComponents(DataComponentInput dataComponentInput) {
      super.applyImplicitComponents(dataComponentInput);
      ((ItemContainerContents)dataComponentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyInto(this.items);
   }

   protected void collectImplicitComponents(Builder builder) {
      super.collectImplicitComponents(builder);
      builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
   }

   public void removeComponentsFromTag(CompoundTag compoundTag) {
      compoundTag.remove("Items");
   }

   public int getContainerSize() {
      return 3;
   }

   public int getMaxStackSize() {
      return 1;
   }

   public int count() {
      return (int)this.items.stream().filter(Predicate.not(ItemStack::isEmpty)).count();
   }

   public boolean isEmpty() {
      return this.items.stream().allMatch(ItemStack::isEmpty);
   }

   @NotNull
   public ItemStack getItem(int i) {
      return (ItemStack)this.items.get(i);
   }

   public void setItem(int i, ItemStack itemStack) {
      if (ToolRackBlock.canInsert(itemStack)) {
         this.items.set(i, itemStack);
         this.updateState(i);
      } else if (itemStack.isEmpty()) {
         this.removeItem(i, 1);
      }
   }

   @NotNull
   public ItemStack removeItem(int i, int j) {
      ItemStack itemStack = Objects.requireNonNullElse((ItemStack)this.items.get(i), ItemStack.EMPTY);
      this.items.set(i, ItemStack.EMPTY);
      if (!itemStack.isEmpty()) {
         this.updateState(i);
      }

      return itemStack;
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int i) {
      return this.removeItem(i, 1);
   }

   public void clearContent() {
      this.items.clear();
   }

   public boolean canTakeItem(Container container, int i, ItemStack itemStack) {
      return container.hasAnyMatching(
         stack -> stack.isEmpty()
            ? true
            : ItemStack.isSameItemSameComponents(itemStack, stack) && stack.getCount() + itemStack.getCount() <= container.getMaxStackSize(stack)
      );
   }

   public boolean canPlaceItem(int i, ItemStack itemStack) {
      return ToolRackBlock.canInsert(itemStack) && this.getItem(i).isEmpty() && itemStack.getCount() == this.getMaxStackSize();
   }

   public boolean stillValid(Player player) {
      return Container.stillValidBlockEntity(this, player);
   }

   private void updateState(int i) {
      if (i >= 0 && i < 3) {
         this.lastInteractedSlot = i;
         BlockState blockState = this.getBlockState();

         for (int j = 0; j < ToolRackBlock.SLOT_OCCUPIED_PROPERTIES.size(); j++) {
            boolean bl = !this.getItem(j).isEmpty();
            BooleanProperty booleanProperty = ToolRackBlock.SLOT_OCCUPIED_PROPERTIES.get(j);
            blockState = (BlockState)blockState.setValue(booleanProperty, bl);
         }

         Objects.requireNonNull(this.level).setBlock(this.worldPosition, blockState, 3);
         this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.worldPosition, Context.of(blockState));
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
   public int getLastInteractedSlot() {
      return this.lastInteractedSlot;
   }
}
