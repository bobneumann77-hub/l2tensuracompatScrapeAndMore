package io.github.manasmods.tensura.block.entity;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.block.MiningStationBlock;
import io.github.manasmods.tensura.menu.MiningStationMenu;
import io.github.manasmods.tensura.menu.container.MultiResultContainer;
import io.github.manasmods.tensura.recipe.MiningStationRecipe;
import io.github.manasmods.tensura.recipe.input.MiningStationRecipeInput;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiningStationBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
   public static final int INPUT_SLOT_INDEX = 0;
   public static final int WORK_INTERVAL = 10;
   private final SimpleContainer inputContainer = new SimpleContainer(1) {
      public int getMaxStackSize() {
         return MiningStationBlockEntity.this.getMaxStackSize();
      }

      public int getMaxStackSize(ItemStack itemStack) {
         return itemStack.getMaxStackSize() == 1 ? 1 : this.getMaxStackSize();
      }
   };
   private final MultiResultContainer resultContainer = new MultiResultContainer(9, this.getMaxStackSize());
   private final ContainerOpenersCounter openersCounter;
   private boolean needsUpdate = false;
   private int progress = 0;

   public MiningStationBlockEntity(BlockPos blockPos, BlockState blockState) {
      super((BlockEntityType)TensuraBlockEntities.MINING_STATION.get(), blockPos, blockState);
      this.openersCounter = new ContainerOpenersCounter() {
         protected void onOpen(Level level, BlockPos blockPosx, BlockState blockStatex) {
            MiningStationBlockEntity.this.playSound(blockStatex, SoundEvents.BARREL_OPEN);
         }

         protected void onClose(Level level, BlockPos blockPosx, BlockState blockStatex) {
            MiningStationBlockEntity.this.playSound(blockStatex, SoundEvents.BARREL_CLOSE);
         }

         protected void openerCountChanged(Level level, BlockPos blockPosx, BlockState blockStatex, int i, int j) {
         }

         protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof MiningStationMenu
               ? ((MiningStationMenu)player.containerMenu).getMiningStation() == MiningStationBlockEntity.this
               : false;
         }
      };
   }

   @NotNull
   protected Component getDefaultName() {
      return Component.translatable("block.tensura.mining_station");
   }

   public static void tick(Level level, BlockPos pos, BlockState state, MiningStationBlockEntity entity) {
      if (!level.isClientSide()) {
         if (!entity.getItem(0).isEmpty()) {
            if (entity.progress++ >= 10) {
               entity.processInput();
               entity.progress = 0;
               if (!entity.needsUpdate) {
                  return;
               }

               entity.setChanged();
               level.sendBlockUpdated(pos, state, state, 2);
               entity.needsUpdate = false;
            }
         }
      }
   }

   public int getContainerSize() {
      return this.inputContainer.getContainerSize() + this.resultContainer.getContainerSize();
   }

   public int getMaxStackSize() {
      return 64;
   }

   @NotNull
   public ItemStack getItem(int index) {
      return index == 0 ? this.inputContainer.getItem(0) : this.resultContainer.getItem(index - 1);
   }

   public void setItem(int index, ItemStack stack) {
      int maxStackSize = this.getMaxStackSize();
      if (stack.getCount() > maxStackSize) {
         ItemStack copy = stack.copyWithCount(this.getMaxStackSize());
         stack.shrink(maxStackSize);
         stack = copy;
      }

      if (index > 0) {
         this.resultContainer.setItem(index - 1, stack);
      } else {
         this.inputContainer.setItem(0, stack);
      }
   }

   @NotNull
   public NonNullList<ItemStack> getItems() {
      NonNullList<ItemStack> input = this.inputContainer.getItems();
      NonNullList<ItemStack> results = this.resultContainer.getItemStacks();
      NonNullList<ItemStack> merged = NonNullList.createWithCapacity(this.getContainerSize());
      merged.addAll(input);
      merged.addAll(results);
      return merged;
   }

   public void setItems(NonNullList<ItemStack> items) {
      this.inputContainer.setItem(0, (ItemStack)items.removeFirst());
      this.resultContainer.setItems(items);
   }

   @NotNull
   public ItemStack removeItem(int index, int pCount) {
      return index == 0
         ? ContainerHelper.removeItem(this.inputContainer.getItems(), index, pCount)
         : ContainerHelper.removeItem(this.resultContainer.getItemStacks(), index - 1, pCount);
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int index) {
      return index == 0
         ? ContainerHelper.takeItem(this.inputContainer.getItems(), index)
         : ContainerHelper.takeItem(this.resultContainer.getItemStacks(), index - 1);
   }

   public void processInput() {
      if (this.getLevel() != null && !this.getLevel().isClientSide()) {
         ItemStack input = this.getItem(0);
         if (!input.isEmpty()) {
            Pair<RecipeHolder<MiningStationRecipe>, MiningStationRecipeInput> pair = this.getRecipe((ServerLevel)this.getLevel(), input);
            if (pair != null) {
               RecipeHolder<MiningStationRecipe> recipeHolder = (RecipeHolder<MiningStationRecipe>)pair.getFirst();
               MiningStationRecipeInput recipeInput = (MiningStationRecipeInput)pair.getSecond();
               MiningStationRecipe recipe = (MiningStationRecipe)recipeHolder.value();
               if (recipe.matches(recipeInput, this.getLevel())) {
                  int inputCount = recipeInput.item().getCount();
                  int requiredQuantity = recipe.recipeIngredient().getQuantity();
                  if (inputCount >= requiredQuantity) {
                     List<ItemStack> maxResultItems = recipe.getMinMaxResultItems(false);
                     if (this.resultContainer.hasEnoughSpaceFor(maxResultItems)) {
                        this.resultContainer.addOrSetItems(recipe.getRandomResultItems(this.getLevel().getRandom()), false);
                        input.shrink(requiredQuantity);
                        this.needsUpdate = true;
                        this.resultContainer.setRecipeUsed(recipeHolder);
                        Vec3i vec3i = ((Direction)this.getBlockState().getValue(MiningStationBlock.FACING)).getNormal();
                        double d = this.getBlockPos().getX() + 0.5F + vec3i.getX() / 2.0F;
                        double e = this.getBlockPos().getY() + 0.5F + vec3i.getY() / 2.0F;
                        double f = this.getBlockPos().getZ() + 0.5F + vec3i.getZ() / 2.0F;
                        this.getLevel()
                           .playSound(null, d, e, f, SoundEvents.BRUSH_GRAVEL, SoundSource.BLOCKS, 0.75F, this.getLevel().getRandom().nextFloat() * 0.1F + 0.9F);
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   public Pair<RecipeHolder<MiningStationRecipe>, MiningStationRecipeInput> getRecipe(ServerLevel serverLevel, ItemStack input) {
      if (input.isEmpty()) {
         return null;
      }

      MiningStationRecipeInput recipeInput = new MiningStationRecipeInput(input);
      Optional<RecipeHolder<MiningStationRecipe>> optional = serverLevel.getRecipeManager()
         .getRecipeFor((RecipeType)TensuraRecipes.MINING_STATION_TYPE.get(), recipeInput, serverLevel);
      return optional.<Pair<RecipeHolder<MiningStationRecipe>, MiningStationRecipeInput>>map(holder -> Pair.of(holder, recipeInput)).orElse(null);
   }

   public boolean canProcess(ItemStack input) {
      return this.level != null && !this.level.isClientSide() && !input.isEmpty() && this.getRecipe((ServerLevel)this.level, input) != null;
   }

   public boolean canPlaceItem(int index, ItemStack itemStack) {
      return index == 0 && super.canPlaceItem(index, itemStack);
   }

   public boolean isEmpty() {
      return this.inputContainer.isEmpty() && this.resultContainer.isEmpty();
   }

   public void clearContent() {
      this.inputContainer.clearContent();
      this.resultContainer.clearContent();
   }

   public void dropContent() {
      if (this.level != null) {
         Containers.dropContents(this.level, this.getBlockPos(), this.inputContainer);
         Containers.dropContents(this.level, this.getBlockPos(), this.resultContainer);
      }
   }

   public void fillStackedContents(StackedContents stackedContents) {
   }

   @NotNull
   protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
      return this.createMenu(pContainerId, pInventory, pInventory.player);
   }

   @NotNull
   public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
      return new MiningStationMenu(id, inventory, this);
   }

   public boolean stillValid(Player player) {
      if (this.level == null) {
         return false;
      } else {
         return this.level.getBlockEntity(this.worldPosition) != this
            ? false
            : player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
      }
   }

   public int @NotNull [] getSlotsForFace(Direction direction) {
      return switch (direction) {
         case UP -> new int[]{0};
         case DOWN -> new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
         default -> new int[0];
      };
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
      return index == 0;
   }

   public boolean canTakeItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
      return index != 0 || !this.canProcess(itemStack);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = super.getUpdateTag(provider);
      this.saveAdditional(tag, provider);
      return tag;
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      ContainerHelper.saveAllItems(compoundTag, this.getItems(), provider);
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      NonNullList<ItemStack> items = this.getItems();
      ContainerHelper.loadAllItems(compoundTag, items, provider);
      this.setItems(items);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void startOpen(Player player) {
      if (!this.remove && !player.isSpectator()) {
         this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   public void stopOpen(Player player) {
      if (!this.remove && !player.isSpectator()) {
         this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   private void playSound(BlockState blockState, SoundEvent soundEvent) {
      if (this.getLevel() != null) {
         Vec3i vec3i = ((Direction)blockState.getValue(MiningStationBlock.FACING)).getNormal();
         double d = this.getBlockPos().getX() + 0.5F + vec3i.getX() / 2.0F;
         double e = this.getBlockPos().getY() + 0.5F + vec3i.getY() / 2.0F;
         double f = this.getBlockPos().getZ() + 0.5F + vec3i.getZ() / 2.0F;
         this.getLevel().playSound(null, d, e, f, soundEvent, SoundSource.BLOCKS, 0.5F, this.getLevel().random.nextFloat() * 0.1F + 0.9F);
      }
   }
}
