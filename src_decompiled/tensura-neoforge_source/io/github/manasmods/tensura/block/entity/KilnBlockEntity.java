package io.github.manasmods.tensura.block.entity;

import io.github.manasmods.tensura.block.KilnBlock;
import io.github.manasmods.tensura.block.part.KilnPart;
import io.github.manasmods.tensura.menu.KilnMenu;
import io.github.manasmods.tensura.recipe.KilnMeltingRecipe;
import io.github.manasmods.tensura.recipe.KilnMixingRecipe;
import io.github.manasmods.tensura.recipe.input.KilnMeltingRecipeInput;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KilnBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
   public NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
   public static final int INPUT_FUEL_SLOT_INDEX = 0;
   public static final int INPUT_SLOT_INDEX = 1;
   public static final int OUTPUT_MIXING_SLOT_INDEX = 2;
   public static final int DEFAULT_MAX_MOLTEN = 144;
   private Optional<ResourceLocation> leftBarId = Optional.of(KilnMeltingRecipe.EMPTY);
   private Optional<ResourceLocation> rightBarId = Optional.of(KilnMeltingRecipe.EMPTY);
   private int moltenAmount = 0;
   private int magicMaterialAmount = 0;
   private int fuelTime = 0;
   private int maxFuelTime = 0;
   private int meltingProgress = 0;
   private int maxMeltingProgress = 100;
   private int boostedTime = 0;
   private int lastMagicAmount;
   private int lastMoltenAmount;
   private ItemStack lastMixingStack = ItemStack.EMPTY;
   private ItemStack lastInputStack = ItemStack.EMPTY;
   private ItemStack lastFuelStack = ItemStack.EMPTY;
   public boolean needUpdate = false;
   private List<RecipeHolder<KilnMixingRecipe>> possibleMixingRecipes = new ArrayList<>();
   private int selectedRecipeIndex = 0;
   private int lastSelectedRecipeIndex = -1;
   private RecipeHolder<KilnMeltingRecipe> lastMeltingRecipe = null;
   private int totalPossibleRecipes = 0;
   private final KilnBlock.KilnType kilnType;

   public KilnBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)TensuraBlockEntities.KILN.get(), pos, state);
      this.kilnType = ((KilnBlock)state.getBlock()).getType();
   }

   @NotNull
   protected Component getDefaultName() {
      return Component.translatable("tensura.kiln.label");
   }

   @NotNull
   public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
      return new KilnMenu(id, inventory, this);
   }

   @NotNull
   protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
      return this.createMenu(pContainerId, pInventory, pInventory.player);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
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
      this.leftBarId.ifPresent(location -> nbt.putString("kiln.molten.itemId", location.toString()));
      this.rightBarId.ifPresent(location -> nbt.putString("kiln.magic.itemId", location.toString()));
      nbt.putInt("kiln.molten", this.moltenAmount);
      nbt.putInt("kiln.magic", this.magicMaterialAmount);
      nbt.putInt("kiln.workProgress", this.meltingProgress);
      nbt.putInt("kiln.maxWorkProgress", this.maxMeltingProgress);
      nbt.putInt("kiln.fuel", this.fuelTime);
      nbt.putInt("kiln.maxFuel", this.maxFuelTime);
      nbt.putInt("kiln.boostedTick", this.boostedTime);
      nbt.putInt("kiln.possibleRecipes", this.possibleMixingRecipes.size());
      nbt.putInt("kiln.currentRecipe", this.selectedRecipeIndex);
   }

   public void loadAdditional(CompoundTag nbt, Provider provider) {
      super.loadAdditional(nbt, provider);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(nbt, this.items, provider);
      this.leftBarId = nbt.contains("kiln.molten.itemId")
         ? Optional.ofNullable(ResourceLocation.tryParse(nbt.getString("kiln.molten.itemId")))
         : Optional.of(KilnMeltingRecipe.EMPTY);
      this.rightBarId = nbt.contains("kiln.magic.itemId")
         ? Optional.ofNullable(ResourceLocation.tryParse(nbt.getString("kiln.magic.itemId")))
         : Optional.of(KilnMeltingRecipe.EMPTY);
      this.moltenAmount = nbt.getInt("kiln.molten");
      this.magicMaterialAmount = nbt.getInt("kiln.magic");
      this.meltingProgress = nbt.getInt("kiln.workProgress");
      this.maxMeltingProgress = nbt.getInt("kiln.maxWorkProgress");
      this.fuelTime = nbt.getInt("kiln.fuel");
      this.maxFuelTime = nbt.getInt("kiln.maxFuel");
      this.boostedTime = nbt.getInt("kiln.boostedTick");
      this.totalPossibleRecipes = nbt.getInt("kiln.possibleRecipes");
      this.selectedRecipeIndex = nbt.getInt("kiln.currentRecipe");
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
      this.needUpdate = true;
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

      this.needUpdate = true;
   }

   public void clearContent() {
      this.items.clear();
      this.needUpdate = true;
   }

   public void fillStackedContents(StackedContents pHelper) {
   }

   public int getMaxMoltenAmount() {
      return switch (this.getKilnType()) {
         case NORMAL -> ObjectSelectionHelper.CONFIG.Kiln.moltenDefault;
         case MITHRIL -> ObjectSelectionHelper.CONFIG.Kiln.moltenMithril;
         case ORICHALCUM -> ObjectSelectionHelper.CONFIG.Kiln.moltenOrichalcum;
      };
   }

   public void addMagicMaterialAmount(int moltenAmount) {
      this.magicMaterialAmount += moltenAmount;
      this.needUpdate = true;
   }

   public void addMoltenMaterialAmount(int moltenAmount) {
      this.moltenAmount += moltenAmount;
      this.needUpdate = true;
   }

   public boolean canPlaceItem(int pIndex, ItemStack pStack) {
      return switch (pIndex) {
         case 0 -> AbstractFurnaceBlockEntity.isFuel(pStack);
         case 1 -> super.canPlaceItem(pIndex, pStack);
         default -> false;
      };
   }

   public void drops() {
      if (this.level != null) {
         SimpleContainer inventory = new SimpleContainer(this.items.size());

         for (int i = 0; i < this.items.size(); i++) {
            if (i != 2) {
               inventory.setItem(i, (ItemStack)this.items.get(i));
            }
         }

         Containers.dropContents(this.level, this.worldPosition, inventory);
         if (this.getKilnType().equals(KilnBlock.KilnType.ORICHALCUM)) {
            KilnMeltingRecipeInput recipeInput = new KilnMeltingRecipeInput(
               (ItemStack)this.items.get(1), this.leftBarId, this.rightBarId, this.moltenAmount, this.magicMaterialAmount, this.getMaxMoltenAmount()
            );
            if (this.moltenAmount > 0 && this.leftBarId.isPresent()) {
               Optional<RecipeHolder<KilnMixingRecipe>> recipe = this.level
                  .getRecipeManager()
                  .getRecipesFor((RecipeType)TensuraRecipes.KILN_MIXING_TYPE.get(), recipeInput, this.level)
                  .stream()
                  .filter(holder -> ((KilnMixingRecipe)holder.value()).getLeftInput().equals(this.leftBarId.get()))
                  .min((first, second) -> ((KilnMixingRecipe)first.value()).compareTo((KilnMixingRecipe)second.value()));
               if (recipe.isPresent()) {
                  int count = this.moltenAmount / ((KilnMixingRecipe)recipe.get().value()).getLeftAmount();
                  ItemStack stack = ((KilnMixingRecipe)recipe.get().value()).getOutput().copyWithCount(count);
                  Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), stack);
               }
            }

            if (this.magicMaterialAmount > 0 && this.rightBarId.isPresent()) {
               Optional<RecipeHolder<KilnMixingRecipe>> recipe = this.level
                  .getRecipeManager()
                  .getRecipesFor((RecipeType)TensuraRecipes.KILN_MIXING_TYPE.get(), recipeInput, this.level)
                  .stream()
                  .filter(holder -> ((KilnMixingRecipe)holder.value()).getRightInput().equals(this.rightBarId.get()))
                  .min((first, second) -> ((KilnMixingRecipe)first.value()).compareTo((KilnMixingRecipe)second.value()));
               if (recipe.isPresent()) {
                  int count = this.magicMaterialAmount / ((KilnMixingRecipe)recipe.get().value()).getRightAmount();
                  ItemStack stack = ((KilnMixingRecipe)recipe.get().value()).getOutput().copyWithCount(count);
                  Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), stack);
               }
            }
         }
      }
   }

   protected int getBurnDuration(ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return 0;
      }

      if (!AbstractFurnaceBlockEntity.isFuel(itemStack)) {
         return 0;
      }

      Map<Item, Integer> fuelMap = FurnaceBlockEntity.getFuel();
      return fuelMap != null ? fuelMap.getOrDefault(itemStack.getItem(), 0) : 0;
   }

   public static void tick(Level level, BlockPos pos, BlockState state, KilnBlockEntity pEntity) {
      if (!level.isClientSide()) {
         boolean mixingNeedsUpdate = pEntity.checkMixingCache(level);
         boolean meltingNeedsUpdate = pEntity.checkMeltingCache();
         if (mixingNeedsUpdate) {
            pEntity.updatePossibleMixingRecipes(level.registryAccess());
            pEntity.checkSelectedRecipeIndex();
            if (pEntity.lastSelectedRecipeIndex != pEntity.selectedRecipeIndex && pEntity.possibleMixingRecipes.size() > pEntity.selectedRecipeIndex) {
               pEntity.items
                  .set(2, ((KilnMixingRecipe)pEntity.possibleMixingRecipes.get(pEntity.selectedRecipeIndex).value()).getResultItem(level.registryAccess()));
               pEntity.lastSelectedRecipeIndex = pEntity.selectedRecipeIndex;
               pEntity.needUpdate = true;
            }
         }

         if (mixingNeedsUpdate || meltingNeedsUpdate || pEntity.meltingProgress > 0) {
            pEntity.checkMeltingRecipe(level.registryAccess());
         }

         pEntity.updateFuelTime();
         if (pEntity.needUpdate) {
            pEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, 2);
            pEntity.needUpdate = false;
         }
      } else if ((Boolean)state.getValue(KilnBlock.LIT)) {
         RandomSource random = level.random;
         if (random.nextFloat() < 0.11F) {
            for (int i = 0; i < random.nextInt(2) + 2; i++) {
               CampfireBlock.makeParticles(level, pos.above(2), (Boolean)state.getValue(KilnBlock.BOOSTED), false);
            }
         }
      }
   }

   private void updateFuelTime() {
      if (this.fuelTime > 0) {
         this.fuelTime--;
         this.needUpdate = true;
         this.updateLitState(true);
      } else {
         this.updateLitState(false);
      }

      if (this.boostedTime > 0) {
         this.boostedTime--;
         this.needUpdate = true;
         this.updateBoostedState(true);
      } else {
         this.updateBoostedState(false);
      }
   }

   private void updateLitState(boolean lit) {
      if (this.level != null) {
         if (lit) {
            if (!(Boolean)this.getBlockState().getValue(KilnBlock.LIT)) {
               BlockState newState = (BlockState)this.getBlockState().setValue(KilnBlock.LIT, true);
               this.level.setBlock(this.getBlockPos(), newState, 3);
               setChanged(this.level, this.getBlockPos(), newState);
            }

            BlockPos above = this.worldPosition.above();
            BlockState aboveState = this.level.getBlockState(above);
            if (!aboveState.hasProperty(KilnBlock.LIT)) {
               return;
            }

            if (!(Boolean)aboveState.getValue(KilnBlock.LIT)) {
               BlockState newState = (BlockState)aboveState.setValue(KilnBlock.LIT, true);
               this.level.setBlock(above, newState, 3);
               setChanged(this.level, above, newState);
            }
         } else {
            if ((Boolean)this.getBlockState().getValue(KilnBlock.LIT)) {
               BlockState newState = (BlockState)this.getBlockState().setValue(KilnBlock.LIT, false);
               this.level.setBlock(this.getBlockPos(), newState, 3);
               setChanged(this.level, this.getBlockPos(), newState);
            }

            BlockPos above = this.worldPosition.above();
            BlockState aboveState = this.level.getBlockState(above);
            if (!aboveState.hasProperty(KilnBlock.LIT)) {
               return;
            }

            if ((Boolean)aboveState.getValue(KilnBlock.LIT)) {
               BlockState newState = (BlockState)aboveState.setValue(KilnBlock.LIT, false);
               this.level.setBlock(above, newState, 3);
               setChanged(this.level, above, newState);
            }
         }
      }
   }

   private void updateBoostedState(boolean lit) {
      if (this.level != null) {
         if (lit) {
            if (!(Boolean)this.getBlockState().getValue(KilnBlock.BOOSTED)) {
               BlockState newState = (BlockState)this.getBlockState().setValue(KilnBlock.BOOSTED, true);
               this.level.setBlock(this.getBlockPos(), newState, 3);
               setChanged(this.level, this.getBlockPos(), newState);
            }

            BlockPos above = this.worldPosition.above();
            BlockState aboveState = this.level.getBlockState(above);
            if (!aboveState.hasProperty(KilnBlock.BOOSTED)) {
               return;
            }

            if (!(Boolean)aboveState.getValue(KilnBlock.BOOSTED)) {
               BlockState newState = (BlockState)aboveState.setValue(KilnBlock.BOOSTED, true);
               this.level.setBlock(above, newState, 3);
               setChanged(this.level, above, newState);
            }
         } else {
            if ((Boolean)this.getBlockState().getValue(KilnBlock.BOOSTED)) {
               BlockState newState = (BlockState)this.getBlockState().setValue(KilnBlock.BOOSTED, false);
               this.level.setBlock(this.getBlockPos(), newState, 3);
               setChanged(this.level, this.getBlockPos(), newState);
            }

            BlockPos above = this.worldPosition.above();
            BlockState aboveState = this.level.getBlockState(above);
            if (!aboveState.hasProperty(KilnBlock.BOOSTED)) {
               return;
            }

            if ((Boolean)aboveState.getValue(KilnBlock.BOOSTED)) {
               BlockState newState = (BlockState)aboveState.setValue(KilnBlock.BOOSTED, false);
               this.level.setBlock(above, newState, 3);
               setChanged(this.level, above, newState);
            }
         }
      }
   }

   private boolean checkFuel() {
      if (this.fuelTime > 0) {
         return true;
      }

      ItemStack fuelSlotStack = ((ItemStack)this.items.get(0)).copy();
      if (fuelSlotStack.isEmpty()) {
         return false;
      }

      int fuelTime = this.getBurnDuration(fuelSlotStack);
      if (fuelTime <= 0) {
         return false;
      }

      if (fuelSlotStack.getItem() instanceof BucketItem) {
         fuelSlotStack = Items.BUCKET.getDefaultInstance();
      } else {
         fuelSlotStack.shrink(1);
      }

      this.items.set(0, fuelSlotStack);
      this.fuelTime = fuelTime;
      this.maxFuelTime = fuelTime;
      this.needUpdate = true;
      return true;
   }

   private boolean checkMeltingCache() {
      if (!ItemStack.isSameItemSameComponents(this.lastInputStack, (ItemStack)this.items.get(1))) {
         this.lastInputStack = ((ItemStack)this.items.get(1)).copy();
         return true;
      } else if (!ItemStack.isSameItemSameComponents(this.lastFuelStack, (ItemStack)this.items.get(0))) {
         this.lastFuelStack = ((ItemStack)this.items.get(0)).copy();
         return true;
      } else {
         return false;
      }
   }

   private void checkMeltingRecipe(Provider provider) {
      if (((ItemStack)this.items.get(1)).isEmpty()) {
         if (this.meltingProgress > 0) {
            this.resetMeltingProgress();
         }
      } else if (this.level != null) {
         KilnMeltingRecipeInput recipeInput = new KilnMeltingRecipeInput(
            (ItemStack)this.items.get(1), this.leftBarId, this.rightBarId, this.moltenAmount, this.magicMaterialAmount, this.getMaxMoltenAmount()
         );
         this.level.getRecipeManager().getRecipeFor((RecipeType)TensuraRecipes.KILN_MELTING_TYPE.get(), recipeInput, this.level).ifPresentOrElse(recipe -> {
            if (this.checkFuel()) {
               if (this.lastMeltingRecipe != null && !this.lastMeltingRecipe.id().equals(recipe.id())) {
                  this.lastMeltingRecipe = (RecipeHolder<KilnMeltingRecipe>)recipe;
                  this.resetMeltingProgress();
               }

               int smeltTick = ((KilnMeltingRecipe)recipe.value()).getSmeltTick();
               if (this.maxMeltingProgress != smeltTick) {
                  this.maxMeltingProgress = smeltTick;
               }

               if (this.meltingProgress >= smeltTick) {
                  ((KilnMeltingRecipe)recipe.value()).assembleMolten(this.level, this, provider);
                  this.resetMeltingProgress();
               } else if ((Boolean)this.getBlockState().getValue(KilnBlock.BOOSTED)) {
                  this.meltingProgress += 2;
               } else {
                  this.meltingProgress++;
               }
            } else {
               this.resetMeltingProgress();
            }

            this.needUpdate = true;
         }, this::resetMeltingProgress);
      }
   }

   private void resetMeltingProgress() {
      if (this.meltingProgress > 0) {
         this.meltingProgress = 0;
         this.maxMeltingProgress = 100;
         this.lastInputStack = ItemStack.EMPTY;
         this.needUpdate = true;
      }
   }

   public boolean hasPrevMixingRecipe() {
      return this.totalPossibleRecipes > this.selectedRecipeIndex - 1 && this.selectedRecipeIndex - 1 >= 0;
   }

   public boolean hasNextMixingRecipe() {
      return this.totalPossibleRecipes > this.selectedRecipeIndex + 1 && this.selectedRecipeIndex + 1 >= 0;
   }

   public void mixingNextRecipe() {
      if (this.selectedRecipeIndex + 1 >= this.possibleMixingRecipes.size()) {
         this.selectedRecipeIndex = 0;
      } else {
         this.selectedRecipeIndex++;
      }

      this.needUpdate = true;
   }

   public void mixingPrevRecipe() {
      if (this.selectedRecipeIndex - 1 < 0) {
         this.selectedRecipeIndex = this.possibleMixingRecipes.size() - 1;
      } else {
         this.selectedRecipeIndex--;
      }

      this.needUpdate = true;
   }

   private boolean checkMixingCache(Level level) {
      if (this.lastSelectedRecipeIndex != this.selectedRecipeIndex && !this.possibleMixingRecipes.isEmpty()) {
         this.items.set(2, ((KilnMixingRecipe)this.possibleMixingRecipes.get(this.selectedRecipeIndex).value()).getResultItem(level.registryAccess()));
         this.needUpdate = true;
         this.lastSelectedRecipeIndex = this.selectedRecipeIndex;
      }

      if (this.lastMagicAmount != this.magicMaterialAmount) {
         this.lastMagicAmount = this.magicMaterialAmount;
         return true;
      } else if (this.lastMoltenAmount != this.moltenAmount) {
         this.lastMoltenAmount = this.moltenAmount;
         return true;
      } else if (!ItemStack.isSameItem(this.lastMixingStack, (ItemStack)this.items.get(2))) {
         this.lastMixingStack = ((ItemStack)this.items.get(2)).copy();
         return true;
      } else {
         return false;
      }
   }

   private void updatePossibleMixingRecipes(Provider provider) {
      if (this.level != null) {
         Optional<RecipeHolder<KilnMixingRecipe>> selectedRecipe = this.possibleMixingRecipes.size() > this.selectedRecipeIndex
               && this.selectedRecipeIndex >= 0
            ? Optional.of(this.possibleMixingRecipes.get(this.selectedRecipeIndex))
            : Optional.empty();
         KilnMeltingRecipeInput recipeInput = new KilnMeltingRecipeInput(
            (ItemStack)this.items.get(1), this.leftBarId, this.rightBarId, this.moltenAmount, this.magicMaterialAmount, this.getMaxMoltenAmount()
         );
         this.possibleMixingRecipes = this.level
            .getRecipeManager()
            .getRecipesFor((RecipeType)TensuraRecipes.KILN_MIXING_TYPE.get(), recipeInput, this.level)
            .stream()
            .sorted((first, second) -> ((KilnMixingRecipe)first.value()).compareTo((KilnMixingRecipe)second.value()))
            .toList();
         this.totalPossibleRecipes = this.possibleMixingRecipes.size();
         selectedRecipe.ifPresentOrElse(kilnMixingRecipe -> {
            if (this.possibleMixingRecipes.contains(kilnMixingRecipe)) {
               this.selectedRecipeIndex = this.possibleMixingRecipes.indexOf(kilnMixingRecipe);
            } else {
               this.selectedRecipeIndex = 0;
            }
         }, () -> this.selectedRecipeIndex = 0);
         if (!this.possibleMixingRecipes.isEmpty()) {
            this.items.set(2, ((KilnMixingRecipe)this.possibleMixingRecipes.get(this.selectedRecipeIndex).value()).getResultItem(provider));
         }

         this.needUpdate = true;
      }
   }

   private void checkSelectedRecipeIndex() {
      if (this.totalPossibleRecipes == 0) {
         this.selectedRecipeIndex = 0;
         this.needUpdate = true;
      } else if (this.selectedRecipeIndex >= this.totalPossibleRecipes) {
         this.selectedRecipeIndex--;
         this.needUpdate = true;
      } else if (this.selectedRecipeIndex < 0) {
         this.selectedRecipeIndex = 0;
         this.needUpdate = true;
      }
   }

   public void performMixing(Provider provider) {
      if (this.level instanceof ServerLevel) {
         if (!this.possibleMixingRecipes.isEmpty()) {
            RecipeHolder<KilnMixingRecipe> recipe = this.possibleMixingRecipes.get(this.selectedRecipeIndex);
            this.moltenAmount = this.moltenAmount - ((KilnMixingRecipe)recipe.value()).getLeftAmount();
            if (this.moltenAmount <= 0) {
               this.leftBarId = Optional.of(KilnMeltingRecipe.EMPTY);
            }

            this.magicMaterialAmount = this.magicMaterialAmount - ((KilnMixingRecipe)recipe.value()).getRightAmount();
            if (this.magicMaterialAmount <= 0) {
               this.rightBarId = Optional.of(KilnMeltingRecipe.EMPTY);
            }

            KilnMeltingRecipeInput recipeInput = new KilnMeltingRecipeInput(
               (ItemStack)this.items.get(1), this.leftBarId, this.rightBarId, this.moltenAmount, this.magicMaterialAmount, this.getMaxMoltenAmount()
            );
            if (((KilnMixingRecipe)recipe.value()).matches(recipeInput, this.level)) {
               this.items.set(2, ((KilnMixingRecipe)recipe.value()).assemble(recipeInput, provider));
            } else {
               this.updatePossibleMixingRecipes(provider);
            }

            this.needUpdate = true;
         }
      }
   }

   public int @NotNull [] getSlotsForFace(Direction pSide) {
      if (((KilnPart)this.getBlockState().getValue(KilnBlock.PART)).equals(KilnPart.TOP)) {
         return new int[0];
      } else {
         Direction backSide = ((Direction)this.getBlockState().getValue(KilnBlock.FACING)).getOpposite();
         if (pSide.equals(backSide)) {
            return new int[]{0};
         } else {
            return pSide.equals(Direction.DOWN) ? new int[]{2} : new int[]{1};
         }
      }
   }

   public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
      return switch (pIndex) {
         case 0 -> AbstractFurnaceBlockEntity.isFuel(pItemStack);
         case 1 -> true;
         default -> false;
      };
   }

   public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
      return pIndex != 0 ? false : pStack.is(Items.WATER_BUCKET) || pStack.is(Items.BUCKET);
   }

   @Generated
   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   @Generated
   public void setItems(NonNullList<ItemStack> items) {
      this.items = items;
   }

   @Generated
   public Optional<ResourceLocation> getLeftBarId() {
      return this.leftBarId;
   }

   @Generated
   public void setLeftBarId(Optional<ResourceLocation> leftBarId) {
      this.leftBarId = leftBarId;
   }

   @Generated
   public Optional<ResourceLocation> getRightBarId() {
      return this.rightBarId;
   }

   @Generated
   public void setRightBarId(Optional<ResourceLocation> rightBarId) {
      this.rightBarId = rightBarId;
   }

   @Generated
   public int getMoltenAmount() {
      return this.moltenAmount;
   }

   @Generated
   public void setMoltenAmount(int moltenAmount) {
      this.moltenAmount = moltenAmount;
   }

   @Generated
   public int getMagicMaterialAmount() {
      return this.magicMaterialAmount;
   }

   @Generated
   public void setMagicMaterialAmount(int magicMaterialAmount) {
      this.magicMaterialAmount = magicMaterialAmount;
   }

   @Generated
   public int getFuelTime() {
      return this.fuelTime;
   }

   @Generated
   public void setFuelTime(int fuelTime) {
      this.fuelTime = fuelTime;
   }

   @Generated
   public int getMaxFuelTime() {
      return this.maxFuelTime;
   }

   @Generated
   public void setMaxFuelTime(int maxFuelTime) {
      this.maxFuelTime = maxFuelTime;
   }

   @Generated
   public int getMeltingProgress() {
      return this.meltingProgress;
   }

   @Generated
   public void setMeltingProgress(int meltingProgress) {
      this.meltingProgress = meltingProgress;
   }

   @Generated
   public int getMaxMeltingProgress() {
      return this.maxMeltingProgress;
   }

   @Generated
   public void setMaxMeltingProgress(int maxMeltingProgress) {
      this.maxMeltingProgress = maxMeltingProgress;
   }

   @Generated
   public int getBoostedTime() {
      return this.boostedTime;
   }

   @Generated
   public void setBoostedTime(int boostedTime) {
      this.boostedTime = boostedTime;
   }

   @Generated
   public List<RecipeHolder<KilnMixingRecipe>> getPossibleMixingRecipes() {
      return this.possibleMixingRecipes;
   }

   @Generated
   public int getSelectedRecipeIndex() {
      return this.selectedRecipeIndex;
   }

   @Generated
   public KilnBlock.KilnType getKilnType() {
      return this.kilnType;
   }
}
