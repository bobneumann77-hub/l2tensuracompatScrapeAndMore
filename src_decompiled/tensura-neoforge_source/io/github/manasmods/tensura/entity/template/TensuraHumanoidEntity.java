package io.github.manasmods.tensura.entity.template;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.tensura.menu.HumanoidInventoryMenu;
import io.github.manasmods.tensura.menu.HumanoidMainMenu;
import io.github.manasmods.tensura.menu.container.SimpleLimitedContainer;
import io.github.manasmods.tensura.network.s2c.OpenHumanoidMenuPayload;
import io.github.manasmods.tensura.storage.TensuraStorages;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TensuraHumanoidEntity extends TensuraTamableEntity implements HasCustomInventoryScreen, ContainerListener {
   private int prevSlots = 0;
   public TensuraHumanoidEntity.HumanoidContainer inventory;
   protected List<Integer> fakeItemSlots = new ArrayList<>();

   public TensuraHumanoidEntity(EntityType<? extends TensuraHumanoidEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.createInventory();
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (!this.fakeItemSlots.isEmpty()) {
         CompoundTag fakeSlots = new CompoundTag();

         for (int i = 0; i < this.fakeItemSlots.size(); i++) {
            fakeSlots.putInt("slot" + i, this.fakeItemSlots.get(i));
         }

         compound.put("FakeSlots", fakeSlots);
      }

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

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      if (compound.contains("FakeSlots")) {
         this.fakeItemSlots.clear();
         CompoundTag fakeSlots = compound.getCompound("FakeSlots");
         fakeSlots.getAllKeys().forEach(s -> this.fakeItemSlots.add(fakeSlots.getInt(s)));
      }

      super.readAdditionalSaveData(compound);
      this.createInventory();
      ListTag list = compound.getList("Items", 10);

      for (int i = 0; i < list.size(); i++) {
         CompoundTag tag = list.getCompound(i);
         int slot = tag.getByte("Slot") & 255;
         if (slot < this.inventory.getContainerSize()) {
            ItemStack stack = ItemStack.parseOptional(this.registryAccess(), tag);
            if (!stack.isEmpty()) {
               this.inventory.setItem(slot, stack);
            }
         }
      }
   }

   protected void applyTamingSideEffects() {
      super.applyTamingSideEffects();
      this.updateContainerEquipment();
   }

   public List<EquipmentSlot> getAvailableSlots() {
      return List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
   }

   public int getSlotId(EquipmentSlot slot) {
      int id = 0;

      for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
         if (equipmentSlot == slot) {
            break;
         }

         if (this.getAvailableSlots().contains(equipmentSlot)) {
            id++;
         }
      }

      return this.getAvailableSlots().contains(slot) ? id : -1;
   }

   @Nullable
   public EquipmentSlot getSlotFromId(int id) {
      List<EquipmentSlot> available = this.getAvailableSlots();
      return id >= 0 && id < available.size() ? available.get(id) : null;
   }

   public void addFakeItem(EquipmentSlot slot, ItemStack stack) {
      int id = this.getSlotId(slot);
      if (!this.fakeItemSlots.contains(id)) {
         this.fakeItemSlots.add(id);
      }

      this.setItemSlot(slot, stack);
   }

   public int getChestSlots() {
      return 36;
   }

   public int getMiscSlots() {
      return this.getAvailableSlots().size();
   }

   public int getInventorySize() {
      return this.getChestSlots() + this.getMiscSlots();
   }

   @Override
   public void tick() {
      super.tick();
      if (this.prevSlots != this.getInventorySize()) {
         this.createInventory();
      }
   }

   public int getMenuRenderSize() {
      return 30;
   }

   public boolean shouldShowEP() {
      return true;
   }

   public boolean shouldShowHP() {
      return true;
   }

   public boolean shouldShowSHP() {
      return true;
   }

   public boolean shouldShowArmor() {
      return !this.shouldShowEP() || !this.shouldShowHP() || !this.shouldShowSHP();
   }

   public boolean canOpenMountInventory(Player owner) {
      return this.isOwnedBy(owner);
   }

   protected void createInventory() {
      TensuraHumanoidEntity.HumanoidContainer container = this.inventory;
      this.inventory = new TensuraHumanoidEntity.HumanoidContainer(this.getInventorySize(), this.getMiscSlots());
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
         if (!this.level().isClientSide() && (!this.isVehicle() || this.hasPassenger(owner))) {
            ServerPlayer player = (ServerPlayer)owner;
            if (player.containerMenu != player.inventoryMenu) {
               player.closeContainer();
            }

            this.openMainInventory(owner);
         }
      }
   }

   public void openMainInventory(Player owner) {
      if (owner instanceof ServerPlayer player) {
         player.nextContainerCounter();
         NetworkManager.sendToPlayer(player, new OpenHumanoidMenuPayload(player.containerCounter, this.inventory.getContainerSize(), -1, this.getId(), 0));
         player.containerMenu = new HumanoidMainMenu(player.containerCounter, player.getInventory(), this.inventory, this);
         player.initMenu(player.containerMenu);
         ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
      }
   }

   public void openSideInventory(Player owner, int page) {
      if (owner instanceof ServerPlayer player) {
         player.nextContainerCounter();
         int chestSlots = Math.max(0, Math.min(36, this.getChestSlots() - 36 * page));
         NetworkManager.sendToPlayer(
            player, new OpenHumanoidMenuPayload(player.containerCounter, this.inventory.getContainerSize(), page, this.getId(), chestSlots)
         );
         player.containerMenu = new HumanoidInventoryMenu(player.containerCounter, player.getInventory(), this.inventory, this, page, chestSlots);
         player.initMenu(player.containerMenu);
         ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
      }
   }

   public boolean hasInventoryChanged(Container container) {
      return this.inventory != container;
   }

   public void updateContainerEquipment() {
      if (!this.level().isClientSide()) {
         for (EquipmentSlot slot : this.getAvailableSlots()) {
            int id = this.getSlotId(slot);
            if (id != -1 && !this.fakeItemSlots.contains(id)) {
               this.setItemSlot(slot, this.inventory.getItem(id));
            }
         }
      }
   }

   public void containerChanged(Container container) {
      this.updateContainerEquipment();
   }

   public SlotAccess getSlot(int slot) {
      int id = slot - 400;
      if (id >= 0 && id < this.getMiscSlots() && id < this.inventory.getContainerSize()) {
         for (EquipmentSlot equipmentSlot : this.getAvailableSlots()) {
            int slotId = this.getSlotId(equipmentSlot);
            if (slotId != -1 && id == slotId) {
               return this.createEquipmentSlotAccess(id, ItemStack::isEmpty);
            }
         }

         return SlotAccess.NULL;
      } else {
         int i = slot - 500 + this.getMiscSlots();
         return i >= this.getMiscSlots() && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(slot);
      }
   }

   private SlotAccess createEquipmentSlotAccess(int slot, Predicate<ItemStack> predicate) {
      return new SlotAccess() {
         public ItemStack get() {
            return TensuraHumanoidEntity.this.inventory.getItem(slot);
         }

         public boolean set(ItemStack stack) {
            if (!predicate.test(stack)) {
               return false;
            }

            TensuraHumanoidEntity.this.inventory.setItem(slot, stack);
            TensuraHumanoidEntity.this.updateContainerEquipment();
            return true;
         }
      };
   }

   protected boolean shouldDropLoot() {
      return TensuraStorages.getExistenceFrom(this).getSummoner() == null;
   }

   public boolean shouldDropExperience() {
      return TensuraStorages.getExistenceFrom(this).getSummoner() == null;
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

      this.updateContainerEquipment();
   }

   protected float getEquipmentDropChance(EquipmentSlot pSlot) {
      return this.isTame() && !this.isTamedByNonPlayer() ? 0.0F : super.getEquipmentDropChance(pSlot);
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (this.isTame() && this.isOwnedBy(player)) {
         InteractionResult interaction = this.getInventoryInteraction(player, hand);
         if (interaction.consumesAction()) {
            return interaction;
         }

         this.cycleCommands(this, player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   public InteractionResult getInventoryInteraction(Player player, InteractionHand hand) {
      if (!player.isSecondaryUseActive()) {
         this.openCustomInventoryScreen(player);
         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   public boolean canBreed() {
      return this.countFoodPointsInInventory() >= 15 && !this.isSleeping() && this.getAge() == 0;
   }

   public boolean hasExcessFood() {
      return this.countFoodPointsInInventory() >= 30;
   }

   public boolean wantsMoreFood() {
      return this.countFoodPointsInInventory() < 15;
   }

   private int countFoodPointsInInventory() {
      int points = 0;
      SimpleContainer container = this.inventory;

      for (ItemStack itemStack : container.getItems()) {
         if (!itemStack.isEmpty()) {
            FoodProperties properties = (FoodProperties)itemStack.get(DataComponents.FOOD);
            if (properties != null) {
               points += properties.nutrition() * itemStack.getCount();
            }
         }
      }

      return points;
   }

   public void consumeFoodPoints(int points) {
      if (this.countFoodPointsInInventory() > 0) {
         for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack itemStack = this.inventory.getItem(i);
            if (!itemStack.isEmpty()) {
               FoodProperties properties = (FoodProperties)itemStack.get(DataComponents.FOOD);
               if (properties != null) {
                  int count = itemStack.getCount();

                  for (int j = count; j > 0; j--) {
                     points -= properties.nutrition();
                     this.inventory.removeItem(i, 1);
                     if (points <= 0) {
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   public class HumanoidContainer extends SimpleLimitedContainer {
      public HumanoidContainer(int size, int limited) {
         super(size, limited);
      }

      public HumanoidContainer(int limited, ItemStack... stacks) {
         super(limited, stacks);
      }

      public void setItem(int i, ItemStack itemStack) {
         super.setItem(i, itemStack);
         if (TensuraHumanoidEntity.this.fakeItemSlots.contains(i)) {
            TensuraHumanoidEntity.this.fakeItemSlots.remove(Integer.valueOf(i));
         }
      }
   }
}
