package io.github.manasmods.tensura.entity.template;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.tensura.menu.MountMenu;
import io.github.manasmods.tensura.menu.container.SimpleLimitedContainer;
import io.github.manasmods.tensura.network.s2c.OpenMountMenuPayload;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class TensuraMountEntity extends TensuraRideableEntity implements HasCustomInventoryScreen, ContainerListener {
   private static final EntityDataAccessor<Integer> CHESTS = SynchedEntityData.defineId(TensuraMountEntity.class, EntityDataSerializers.INT);
   public SimpleLimitedContainer inventory;
   private int prevSlots = 0;

   public TensuraMountEntity(EntityType<? extends TensuraMountEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.createInventory();
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CHESTS, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.hasArmorSlot() && !this.inventory.getItem(this.getArmorSlotId()).isEmpty()) {
         compound.put("ArmorItem", this.inventory.getItem(this.getArmorSlotId()).save(this.registryAccess()));
      }

      if (this.hasWeaponSlot() && !this.inventory.getItem(this.getWeaponSlotId()).isEmpty()) {
         compound.put("WeaponItem", this.inventory.getItem(this.getWeaponSlotId()).save(this.registryAccess()));
      }

      compound.putInt("Chests", this.getChests());
      if (this.getChests() > 0) {
         ListTag listTag = new ListTag();

         for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
               CompoundTag tag = new CompoundTag();
               tag.putByte("Slot", (byte)i);
               listTag.add(stack.save(this.registryAccess(), tag));
            }
         }

         compound.put("Items", listTag);
      }
   }

   @Override
   protected void saveSaddle(CompoundTag compound) {
      if (this.hasSaddleSlot() && !this.inventory.getItem(0).isEmpty()) {
         compound.put("SaddleItem", this.inventory.getItem(0).save(this.registryAccess()));
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.contains("ArmorItem", 10)) {
         ItemStack armor = ItemStack.parse(this.registryAccess(), compound.getCompound("ArmorItem")).orElse(ItemStack.EMPTY);
         if (this.isMountArmor(armor)) {
            this.inventory.setItem(this.getArmorSlotId(), armor);
         }
      }

      if (compound.contains("WeaponItem", 10)) {
         ItemStack weapon = ItemStack.parse(this.registryAccess(), compound.getCompound("WeaponItem")).orElse(ItemStack.EMPTY);
         if (this.isMountWeapon(weapon)) {
            this.inventory.setItem(this.getWeaponSlotId(), weapon);
         }
      }

      this.setChests(compound.getInt("Chests"));
      this.createInventory();
      if (this.getChests() > 0) {
         ListTag list = compound.getList("Items", 10);

         for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            int slot = tag.getByte("Slot") & 255;
            if (slot >= 1 && slot < this.inventory.getContainerSize()) {
               this.inventory.setItem(slot, ItemStack.parseOptional(this.registryAccess(), tag));
            }
         }
      }

      this.updateContainerEquipment();
   }

   @Override
   protected void readSaddle(CompoundTag compound) {
      if (compound.contains("SaddleItem", 10)) {
         ItemStack saddle = ItemStack.parse(this.registryAccess(), compound.getCompound("SaddleItem")).orElse(ItemStack.EMPTY);
         if (this.isMountSaddle(saddle)) {
            this.inventory.setItem(0, saddle);
         }
      }
   }

   @Override
   public void equipSaddle(ItemStack itemStack, @Nullable SoundSource soundSource) {
      this.inventory.setItem(0, itemStack);
      this.playSound(this.getSaddleSoundEvent(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public boolean isChested() {
      return this.getChests() > 0;
   }

   public int getChests() {
      return (Integer)this.entityData.get(CHESTS);
   }

   public void setChests(int chests) {
      this.entityData.set(CHESTS, chests);
   }

   protected void equipChest(Player player, ItemStack itemStack) {
      this.setChests(this.getChests() + 1);
      this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
      if (!player.hasInfiniteMaterials()) {
         itemStack.shrink(1);
      }

      this.createInventory();
   }

   protected void applyTamingSideEffects() {
      super.applyTamingSideEffects();
      this.updateContainerEquipment();
   }

   public boolean hasSaddleSlot() {
      return true;
   }

   public boolean hasArmorSlot() {
      return false;
   }

   public int getArmorSlotId() {
      return this.hasSaddleSlot() ? 1 : 0;
   }

   public boolean isMountArmor(ItemStack stack) {
      return stack.getItem() instanceof AnimalArmorItem;
   }

   public EquipmentSlot getMountArmorSlot() {
      return EquipmentSlot.BODY;
   }

   public boolean hasWeaponSlot() {
      return false;
   }

   public int getWeaponSlotId() {
      int slot = 2;
      if (!this.hasSaddleSlot()) {
         slot--;
      }

      if (!this.hasArmorSlot()) {
         slot--;
      }

      return slot;
   }

   public boolean isMountWeapon(ItemStack stack) {
      return true;
   }

   public int getChestsAllowed() {
      return 1;
   }

   public int getChestSlots() {
      return 15;
   }

   public int getTotalChestSlots() {
      return this.getChestSlots() * this.getChests();
   }

   public int getMiscSlots() {
      int slot = 0;
      if (this.hasSaddleSlot()) {
         slot++;
      }

      if (this.hasArmorSlot()) {
         slot++;
      }

      if (this.hasWeaponSlot()) {
         slot++;
      }

      return slot;
   }

   public int getInventorySize() {
      return this.getChests() > 0 ? this.getTotalChestSlots() + this.getMiscSlots() : this.getMiscSlots();
   }

   public int getMenuRenderSize() {
      return 10;
   }

   public boolean canOpenMountInventory(Player owner) {
      return this.isTame();
   }

   @Override
   public void tick() {
      super.tick();
      if (this.prevSlots != this.getInventorySize()) {
         this.createInventory();
      }
   }

   protected void createInventory() {
      SimpleLimitedContainer container = this.inventory;
      this.inventory = new SimpleLimitedContainer(this.getInventorySize(), this.getMiscSlots());
      if (container != null) {
         container.removeListener(this);
         int i = Math.min(container.getContainerSize(), this.inventory.getContainerSize());

         for (int j = 0; j < i; j++) {
            ItemStack itemStack = container.getItem(j);
            if (!itemStack.isEmpty()) {
               this.inventory.setItem(j, itemStack.copy());
            }
         }
      }

      this.inventory.addListener(this);
      this.updateContainerEquipment();
      this.prevSlots = this.getInventorySize();
   }

   public void openCustomInventoryScreen(Player owner) {
      if (this.canOpenMountInventory(owner)) {
         if (!this.level().isClientSide()) {
            ServerPlayer player = (ServerPlayer)owner;
            if (!this.isVehicle() || this.hasPassenger(owner)) {
               if (player.containerMenu != player.inventoryMenu) {
                  player.closeContainer();
               }

               this.openMountInventory(owner, 0);
            }
         }
      }
   }

   public void openMountInventory(Player owner, int page) {
      if (owner instanceof ServerPlayer player) {
         player.nextContainerCounter();
         boolean saddle = this.hasSaddleSlot() && this.isSaddleRequired();
         boolean armor = this.hasArmorSlot();
         boolean weapon = this.hasWeaponSlot();
         int chestSlots = this.isChested() ? Math.max(0, Math.min(15, this.getTotalChestSlots() - 15 * page)) : 0;
         NetworkManager.sendToPlayer(
            player, new OpenMountMenuPayload(player.containerCounter, this.inventory.getContainerSize(), page, this.getId(), saddle, armor, weapon, chestSlots)
         );
         player.containerMenu = new MountMenu(player.containerCounter, player.getInventory(), this.inventory, this, page, saddle, armor, weapon, chestSlots);
         player.initMenu(player.containerMenu);
         ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
      }
   }

   public boolean hasInventoryChanged(Container container) {
      return this.inventory != container;
   }

   protected void updateContainerEquipment() {
      if (!this.level().isClientSide) {
         if (this.hasSaddleSlot()) {
            this.entityData.set(SADDLED, !this.inventory.getItem(0).isEmpty());
         }

         if (this.hasArmorSlot()) {
            ItemStack armor = this.inventory.getItem(this.getArmorSlotId());
            if (this.isTame()) {
               this.setItemSlotAndDropWhenKilled(this.getMountArmorSlot(), armor);
            } else {
               this.setItemSlot(this.getMountArmorSlot(), armor);
            }
         }

         if (this.hasWeaponSlot()) {
            ItemStack weapon = this.inventory.getItem(this.getWeaponSlotId());
            if (this.isTame()) {
               this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, weapon);
            } else {
               this.setItemSlot(EquipmentSlot.MAINHAND, weapon);
            }
         }
      }
   }

   public void containerChanged(Container container) {
      boolean saddled = this.isSaddled();
      this.updateContainerEquipment();
      if (this.tickCount > 20 && !saddled && this.isSaddled()) {
         this.playSound(this.getSaddleSoundEvent(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
      }
   }

   public SlotAccess getSlot(int slot) {
      if (slot == 499) {
         return new SlotAccess() {
            public ItemStack get() {
               return TensuraMountEntity.this.getChests() > 0 ? new ItemStack(Items.CHEST, TensuraMountEntity.this.getChests()) : ItemStack.EMPTY;
            }

            public boolean set(ItemStack stack) {
               if (stack.isEmpty()) {
                  if (TensuraMountEntity.this.getChests() > 0) {
                     TensuraMountEntity.this.setChests(0);
                     TensuraMountEntity.this.createInventory();
                  }

                  return true;
               } else if (stack.is(Items.CHEST)) {
                  if (TensuraMountEntity.this.getChests() > 0) {
                     TensuraMountEntity.this.setChests(stack.getCount());
                     TensuraMountEntity.this.createInventory();
                  }

                  return true;
               } else {
                  return false;
               }
            }
         };
      } else {
         int id = slot - 400;
         if (id < 0 || id >= this.getMiscSlots() || id >= this.inventory.getContainerSize()) {
            int i = slot - 500 + this.getMiscSlots();
            return i >= this.getMiscSlots() && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(slot);
         } else if (id == 0) {
            return this.createEquipmentSlotAccess(id, stack -> stack.isEmpty() || this.isMountSaddle(stack));
         } else if (id == this.getArmorSlotId()) {
            return this.createEquipmentSlotAccess(id, stack -> stack.isEmpty() || this.isMountArmor(stack));
         } else {
            return id == this.getWeaponSlotId() ? this.createEquipmentSlotAccess(id, stack -> stack.isEmpty() || this.isMountWeapon(stack)) : SlotAccess.NULL;
         }
      }
   }

   private SlotAccess createEquipmentSlotAccess(int slot, Predicate<ItemStack> predicate) {
      return new SlotAccess() {
         public ItemStack get() {
            return TensuraMountEntity.this.inventory.getItem(slot);
         }

         public boolean set(ItemStack stack) {
            if (!predicate.test(stack)) {
               return false;
            }

            TensuraMountEntity.this.inventory.setItem(slot, stack);
            TensuraMountEntity.this.updateContainerEquipment();
            return true;
         }
      };
   }

   protected void dropChestEach() {
      if (this.inventory != null) {
         int size = this.inventory.getContainerSize();

         for (int i = size - this.getChestSlots(); i < size; i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
               this.spawnAtLocation(stack);
               this.inventory.setItem(i, ItemStack.EMPTY);
            }
         }
      }

      if (this.getChests() > 0) {
         if (!this.level().isClientSide()) {
            this.spawnAtLocation(new ItemStack(Blocks.CHEST));
         }

         this.setChests(this.getChests() - 1);
      }
   }

   protected void dropEquipment() {
      if (this.inventory != null) {
         for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            if (i >= this.getMiscSlots() || !this.isTamedByNonPlayer() && this.isTame()) {
               ItemStack stack = this.inventory.getItem(i);
               if (!stack.isEmpty()) {
                  this.spawnAtLocation(stack);
                  this.inventory.setItem(i, ItemStack.EMPTY);
               }
            }
         }
      }

      if (this.getChests() > 0) {
         if (!this.level().isClientSide) {
            this.spawnAtLocation(new ItemStack(Blocks.CHEST, this.getChests()));
         }

         this.setChests(0);
      }

      this.updateContainerEquipment();
   }

   protected float getEquipmentDropChance(EquipmentSlot pSlot) {
      return this.isTame() && !this.isTamedByNonPlayer() ? 0.0F : super.getEquipmentDropChance(pSlot);
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (this.isTame() && this.isOwnedBy(player)) {
         InteractionResult riding = this.getRidingInteraction(player, hand);
         if (riding != InteractionResult.PASS) {
            return riding;
         }

         this.cycleCommands(this, player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else if (this.isRideable(player) && this.getControllingPassenger() != null) {
         this.doPlayerRide(player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   public InteractionResult getRidingInteraction(Player player, InteractionHand hand) {
      if (this.isSaddleable()) {
         if (this.getChestsAllowed() > 0) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (itemstack.is(Items.CHEST) && this.getChests() < this.getChestsAllowed()) {
               this.equipChest(player, itemstack);
               return InteractionResult.sidedSuccess(this.level().isClientSide());
            }

            if (this.getChests() > 0 && itemstack.is(Items.SHEARS)) {
               this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F);
               this.dropChestEach();
               return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
         }

         if (this.isRideable(player)) {
            this.doPlayerRide(player);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
         }
      }

      if (!player.isSecondaryUseActive()) {
         this.openCustomInventoryScreen(player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }
}
