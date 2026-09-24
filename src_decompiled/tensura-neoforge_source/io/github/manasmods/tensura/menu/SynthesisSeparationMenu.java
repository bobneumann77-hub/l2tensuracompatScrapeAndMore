package io.github.manasmods.tensura.menu;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.subclass.ISynthesisSeparation;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.network.s2c.OpenDegenerateMenuPayload;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;
import org.jetbrains.annotations.Nullable;

public class SynthesisSeparationMenu extends AbstractContainerMenu {
   private final Player player;
   private final ManasSkill skill;
   private int xp;
   private int repairItemCountCost;
   public Slot inputSlot;
   private final ResultContainer anvilResultSlot = new ResultContainer();
   public final ResultContainer disenchanterItemOutput = new ResultContainer();
   public final ResultContainer disenchanterBookOutput = new ResultContainer();
   private final Container anvilInputSlots = new SimpleContainer(2);
   public final Container disenchanterInputSlots = new SimpleContainer(2) {
      public void setChanged() {
         super.setChanged();
         SynthesisSeparationMenu.this.slotsChanged(this);
      }
   };

   public SynthesisSeparationMenu(int pContainerId, Inventory inventory, ManasSkill skill) {
      super(null, pContainerId);
      this.skill = skill;
      this.player = inventory.player;
      this.addPlayerInventorySlots(inventory);
      this.addGrindstoneSlots();
      this.addAnvilSlots();
   }

   public boolean stillValid(Player player) {
      return player.isAlive();
   }

   public void createResult() {
      ItemStack first = this.anvilInputSlots.getItem(0);
      ItemStack second = this.anvilInputSlots.getItem(1);
      if (!first.isEmpty() && !second.isEmpty() && EnchantmentHelper.canStoreEnchantments(first)) {
         ItemStack copy = first.copy();
         Mutable mutable = new Mutable(EnchantmentHelper.getEnchantmentsForCrafting(copy));
         this.repairItemCountCost = 0;
         boolean enchantedBook = second.has(DataComponents.STORED_ENCHANTMENTS);
         if (copy.isDamageableItem() && copy.getItem().isValidRepairItem(first, second)) {
            int repair = Math.min(copy.getDamageValue(), copy.getMaxDamage() / 4);
            if (repair <= 0) {
               this.anvilResultSlot.setItem(0, ItemStack.EMPTY);
               return;
            }

            int countCost;
            for (countCost = 0; repair > 0 && countCost < second.getCount(); countCost++) {
               int n = copy.getDamageValue() - repair;
               copy.setDamageValue(n);
               repair = Math.min(copy.getDamageValue(), copy.getMaxDamage() / 4);
            }

            this.repairItemCountCost = countCost;
         } else {
            if (!enchantedBook && (!copy.is(second.getItem()) || !copy.isDamageableItem())) {
               this.anvilResultSlot.setItem(0, ItemStack.EMPTY);
               return;
            }

            if (copy.isDamageableItem() && !enchantedBook) {
               int repair = first.getMaxDamage() - first.getDamageValue();
               int countCost = second.getMaxDamage() - second.getDamageValue();
               int n = countCost + copy.getMaxDamage() * 12 / 100;
               int repairedAmount = repair + n;
               int damage = copy.getMaxDamage() - repairedAmount;
               if (damage < 0) {
                  damage = 0;
               }

               if (damage < copy.getDamageValue()) {
                  copy.setDamageValue(damage);
               }
            }

            ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(second);
            boolean failed = false;
            boolean canEnchant = false;

            for (Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
               Holder<Enchantment> holder = (Holder<Enchantment>)entry.getKey();
               int currentLevel = mutable.getLevel(holder);
               int level = entry.getIntValue();
               if (this.skill instanceof ISynthesisSeparation synthesisSeparation
                  && synthesisSeparation.getSynthesisBlacklistEnchantments().contains(holder.getRegisteredName())) {
                  level = Math.max(currentLevel, level);
               } else {
                  level = currentLevel == level ? level + 1 : Math.max(level, currentLevel);
               }

               if (!((Enchantment)holder.value()).canEnchant(first) && !first.is(Items.ENCHANTED_BOOK)) {
                  canEnchant = true;
               } else {
                  failed = true;
                  int maxLevel = this.getMaxLevel(holder);
                  if (level > maxLevel) {
                     level = maxLevel;
                  }

                  mutable.set(holder, Math.max(level, currentLevel));
               }
            }

            if (canEnchant && !failed) {
               this.anvilResultSlot.setItem(0, ItemStack.EMPTY);
               return;
            }
         }

         if (!copy.isEmpty()) {
            EnchantmentHelper.setEnchantments(copy, mutable.toImmutable());
         }

         this.anvilResultSlot.setItem(0, copy);
         this.broadcastChanges();
      } else {
         this.anvilResultSlot.setItem(0, ItemStack.EMPTY);
      }
   }

   public ItemEnchantments getInputEnchantments() {
      return this.getInputEnchantments(EnchantmentHelper.getEnchantmentsForCrafting(this.disenchanterInputSlots.getItem(0)));
   }

   public ItemEnchantments getInputEnchantments(ItemEnchantments enchantments) {
      if (this.skill instanceof ISynthesisSeparation synthesisSeparation) {
         if (TensuraEnchantmentHelper.hasTag(enchantments, TensuraTags.Enchantments.SEALING_CURSE)) {
            return ItemEnchantments.EMPTY;
         }

         Mutable input = new Mutable(enchantments);
         input.removeIf(holder -> synthesisSeparation.getSeparateBlacklistEnchantments().contains(holder.getRegisteredName()));
         return input.toImmutable();
      } else {
         Mutable input = new Mutable(enchantments);
         input.removeIf(holder -> holder.is(TensuraTags.Enchantments.ENGRAVING));
         return input.toImmutable();
      }
   }

   public List<Holder<Enchantment>> getSortedEnchantmentList(ItemEnchantments enchantments) {
      return enchantments.keySet()
         .stream()
         .sorted(Comparator.comparing(enchantment -> enchantment.unwrapKey().map(resourceKey -> resourceKey.location().getPath()).orElse("[unregistered]")))
         .toList();
   }

   public void removeEnchantment(Holder<Enchantment> enchantment) {
      ItemStack outputStack = this.disenchanterInputSlots.getItem(0).copy();
      Mutable map = new Mutable(EnchantmentHelper.getEnchantmentsForCrafting(outputStack));
      int level = map.getLevel(enchantment);
      map.removeIf(holder -> holder == enchantment);
      EnchantmentHelper.setEnchantments(outputStack, map.toImmutable());
      ItemStack outputBook = this.disenchanterInputSlots.getItem(1).copy();
      if (this.disenchanterBookOutput.getItem(0).isEmpty()) {
         if (outputBook.isEmpty()) {
            int difference = ((Enchantment)enchantment.value()).getMaxCost(level) - ((Enchantment)enchantment.value()).getMinCost(level);
            if (difference >= 1) {
               this.xp = this.xp + this.player.getRandom().nextInt(0, difference);
            }

            this.xp = this.xp + ((Enchantment)enchantment.value()).getMinCost(level);
         } else {
            if (outputBook.is(Items.BOOK)) {
               outputBook = new ItemStack(Items.ENCHANTED_BOOK);
            }

            outputBook.enchant(enchantment, level);
            this.disenchanterBookOutput.setItem(0, outputBook);
         }

         this.disenchanterInputSlots.getItem(1).shrink(1);
      } else {
         outputBook = this.disenchanterBookOutput.getItem(0).copy();
         outputBook.enchant(enchantment, level);
         this.disenchanterBookOutput.setItem(0, outputBook);
      }

      if (outputStack.is(Items.ENCHANTED_BOOK) && map.toImmutable().isEmpty()) {
         outputStack = new ItemStack(Items.BOOK);
      }

      if (this.getInputEnchantments(map.toImmutable()).isEmpty()) {
         this.disenchanterItemOutput.setItem(0, outputStack.copy());
         this.disenchanterInputSlots.getItem(0).shrink(1);
      } else {
         this.disenchanterInputSlots.setItem(0, outputStack.copy());
      }

      if (this.xp > 0) {
         this.player.giveExperiencePoints(this.xp);
         this.player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
         this.xp = 0;
      }

      this.broadcastChanges();
   }

   private void addPlayerInventorySlots(Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 25 + l * 18, 117 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(playerInventory, i, 25 + i * 18, 175));
      }
   }

   private void addAnvilSlots() {
      this.addSlot(new Slot(this.anvilResultSlot, 0, 175, 30) {
         public boolean mayPlace(ItemStack pStack) {
            return false;
         }

         public void onTake(Player pPlayer, ItemStack pStack) {
            super.onTake(pPlayer, pStack);
            pPlayer.playSound(SoundEvents.ENCHANTMENT_TABLE_USE);
            SynthesisSeparationMenu.this.anvilInputSlots.getItem(0).shrink(1);
            SynthesisSeparationMenu.this.anvilInputSlots.getItem(1).shrink(Math.max(SynthesisSeparationMenu.this.repairItemCountCost, 1));
         }
      });
      this.addSlot(new Slot(this.anvilInputSlots, 0, 69, 30) {
         public int getMaxStackSize() {
            return 1;
         }

         public void set(ItemStack pStack) {
            super.set(pStack);
            SynthesisSeparationMenu.this.createResult();
         }
      });
      this.addSlot(new Slot(this.anvilInputSlots, 1, 118, 30) {
         public void set(ItemStack pStack) {
            super.set(pStack);
            SynthesisSeparationMenu.this.createResult();
         }
      });
   }

   private void addGrindstoneSlots() {
      this.addSlot(new Slot(this.disenchanterItemOutput, 0, 175, 65) {
         public boolean mayPlace(ItemStack pStack) {
            return false;
         }
      });
      this.addSlot(new Slot(this.disenchanterBookOutput, 0, 175, 83) {
         public boolean mayPlace(ItemStack pStack) {
            return false;
         }
      });
      this.inputSlot = new Slot(this.disenchanterInputSlots, 0, 19, 65) {
         public int getMaxStackSize() {
            return 1;
         }

         public boolean mayPlace(ItemStack pStack) {
            return !SynthesisSeparationMenu.this.disenchanterItemOutput.isEmpty()
               ? false
               : !EnchantmentHelper.hasTag(pStack, TensuraTags.Enchantments.SEALING_CURSE);
         }
      };
      this.addSlot(this.inputSlot);
      this.addSlot(new Slot(this.disenchanterInputSlots, 1, 19, 83) {
         public boolean mayPlace(ItemStack pStack) {
            return pStack.is(Items.BOOK) || pStack.is(Items.ENCHANTED_BOOK);
         }
      });
   }

   protected int getMaxLevel(Holder<Enchantment> enchantment) {
      if (this.skill instanceof ISynthesisSeparation synthesisSeparation) {
         return synthesisSeparation.getBonusLevelBlackListEnchantments().contains(enchantment.getRegisteredName())
            ? ((Enchantment)enchantment.value()).getMaxLevel()
            : ((Enchantment)enchantment.value()).getMaxLevel() + synthesisSeparation.getMaximumBonusLevel();
      } else {
         return ((Enchantment)enchantment.value()).getMaxLevel();
      }
   }

   @Nullable
   private ManasSkillInstance getSkillInstance(Player player) {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(this.skill);
      return optional.orElse(null);
   }

   public boolean clickMenuButton(Player player, int i) {
      if (i == -1) {
         ManasSkillInstance instance = this.getSkillInstance(player);
         if (instance != null) {
            this.removed(player);
            if (player instanceof ServerPlayer serverPlayer) {
               serverPlayer.nextContainerCounter();
               NetworkManager.sendToPlayer(
                  serverPlayer,
                  new OpenDegenerateMenuPayload(
                     OpenDegenerateMenuPayload.MenuType.CRAFTING, serverPlayer.containerCounter, player.getId(), instance.getSkill().getRegistryName()
                  )
               );
               player.containerMenu = new UncraftingMenu(serverPlayer.containerCounter, player.getInventory(), instance.getSkill());
               serverPlayer.initMenu(player.containerMenu);
               ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
            }
         }

         return true;
      } else {
         List<Holder<Enchantment>> enchantment = this.getSortedEnchantmentList(this.getInputEnchantments());
         if (i >= 0 && i < enchantment.size()) {
            this.removeEnchantment(enchantment.get(i));
            return true;
         } else {
            return false;
         }
      }
   }

   public void removed(Player pPlayer) {
      this.clearContainer(pPlayer, this.anvilInputSlots);
      this.clearContainer(pPlayer, this.disenchanterInputSlots);
      this.clearContainer(pPlayer, this.disenchanterItemOutput);
      this.clearContainer(pPlayer, this.disenchanterBookOutput);
      super.removed(pPlayer);
   }

   public ItemStack quickMoveStack(Player player, int i) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemStack2 = slot.getItem();
         itemStack = itemStack2.copy();
         if (i != 36 && i != 37 && i != 40) {
            if (i >= 0 && i < 36) {
               if (!this.moveItemStackTo(itemStack2, 38, 40, false) && !this.moveItemStackTo(itemStack2, 41, 43, false)) {
                  if (i < 27) {
                     if (!this.moveItemStackTo(itemStack2, 27, 36, false)) {
                        return ItemStack.EMPTY;
                     }
                  } else if (!this.moveItemStackTo(itemStack2, 0, 27, false)) {
                     return ItemStack.EMPTY;
                  }
               }
            } else if (!this.moveItemStackTo(itemStack2, 0, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!this.moveItemStackTo(itemStack2, 0, 36, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemStack2, itemStack);
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
}
