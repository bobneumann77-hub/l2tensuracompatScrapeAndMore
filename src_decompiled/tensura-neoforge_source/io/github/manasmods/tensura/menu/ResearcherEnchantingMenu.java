package io.github.manasmods.tensura.menu;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.subclass.IResearcherEnchanter;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.enchantment.EngravingHelper;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import java.util.Objects;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;
import org.jetbrains.annotations.Nullable;

public class ResearcherEnchantingMenu extends AbstractContainerMenu {
   private final Player player;
   private final ManasSkill skill;
   private final LivingEntity storageOwner;
   private final SimpleContainer bookInput;
   private final SimpleContainer itemInput;
   private final SimpleContainer itemOutput;
   private ItemEnchantments oldEnchantments = ItemEnchantments.EMPTY;
   public boolean mayPickup = false;

   public ResearcherEnchantingMenu(int pContainerId, Inventory inventory, LivingEntity storageOwner, @Nullable ManasSkill skill) {
      super(null, pContainerId);
      this.player = inventory.player;
      this.storageOwner = storageOwner;
      this.skill = skill;
      this.addPlayerInventorySlots(inventory);
      this.bookInput = new SimpleContainer(1);
      this.itemInput = new SimpleContainer(1);
      this.itemOutput = new SimpleContainer(1);
      this.addSlot(
         new Slot(this.bookInput, 0, 123, 45) {
            public int getMaxStackSize() {
               return 1;
            }

            public void set(ItemStack pStack) {
               super.set(pStack);
               if (!ResearcherEnchantingMenu.this.player.level().isClientSide()) {
                  if (skill instanceof IResearcherEnchanter enchanter && enchanter.isAllowedToCopyEnchantments(ResearcherEnchantingMenu.this.player, pStack)) {
                     if (pStack.is(Items.ENCHANTED_BOOK)) {
                        ItemEnchantments old = (ItemEnchantments)pStack.get(DataComponents.STORED_ENCHANTMENTS);
                        if (old != null) {
                           Mutable mutable = new Mutable(old);
                           if (IResearcherEnchanter.addEnchantments(ResearcherEnchantingMenu.this.player, mutable, skill)) {
                              ItemStack copy = pStack.copy();
                              EnchantmentHelper.setEnchantments(copy, mutable.toImmutable());
                              if (mutable.keySet().isEmpty()) {
                                 copy = Items.BOOK.getDefaultInstance();
                              }

                              ResearcherEnchantingMenu.this.bookInput.setItem(0, copy);
                              ResearcherEnchantingMenu.this.player.playNotifySound(SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);
                              ResearcherEnchantingMenu.this.addSkillMastery();
                           }
                        }
                     } else {
                        ItemEnchantments old = (ItemEnchantments)pStack.get(DataComponents.ENCHANTMENTS);
                        if (old != null) {
                           Mutable mutable = new Mutable(old);
                           if (IResearcherEnchanter.addEnchantments(ResearcherEnchantingMenu.this.player, mutable, skill)) {
                              ItemStack copy = pStack.copy();
                              EnchantmentHelper.setEnchantments(copy, mutable.toImmutable());
                              ResearcherEnchantingMenu.this.bookInput.setItem(0, copy);
                              ResearcherEnchantingMenu.this.player.playNotifySound(SoundEvents.GRINDSTONE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                              ResearcherEnchantingMenu.this.addSkillMastery();
                           }
                        }
                     }

                     ResearcherEnchantingMenu.this.slotsChanged(this.container);
                  }
               }
            }

            public boolean mayPlace(ItemStack pStack) {
               return super.mayPlace(pStack)
                  && skill instanceof IResearcherEnchanter enchanter
                  && enchanter.isAllowedToCopyEnchantments(ResearcherEnchantingMenu.this.player, pStack);
            }
         }
      );
      this.addSlot(
         new Slot(this.itemInput, 0, 147, 67) {
            public int getMaxStackSize() {
               return 1;
            }

            public void setChanged() {
               super.setChanged();
               if (!ResearcherEnchantingMenu.this.mayPickup) {
                  if (this.shouldUpdateEnchantedItem(this.getItem(), ResearcherEnchantingMenu.this.itemOutput.getItem(0))) {
                     this.getItem().set((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get(), ResearcherEnchantingMenu.this.player.tickCount);
                     IResearcherEnchanter.addSelectedEnchantments(
                        ResearcherEnchantingMenu.this.player, EnchantmentHelper.getEnchantmentsForCrafting(this.getItem()), skill, true
                     );
                     ResearcherEnchantingMenu.this.itemOutput.setItem(0, ResearcherEnchantingMenu.this.getOutputItem(this.getItem()));
                     ResearcherEnchantingMenu.this.oldEnchantments = IResearcherEnchanter.getSelectedEnchantments(ResearcherEnchantingMenu.this.player, skill);
                     IResearcherEnchanter.clearNewEnchantment(ResearcherEnchantingMenu.this.player, skill);
                  }
               }
            }

            public void onTake(Player pPlayer, ItemStack pStack) {
               super.onTake(pPlayer, pStack);
               pStack.remove((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get());
            }

            protected void onQuickCraft(ItemStack pStack, int pAmount) {
               super.onQuickCraft(pStack, pAmount);
               pStack.remove((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get());
            }

            public void onQuickCraft(ItemStack pOldStack, ItemStack pNewStack) {
               super.onQuickCraft(pOldStack, pNewStack);
               pOldStack.remove((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get());
            }

            private boolean shouldUpdateEnchantedItem(ItemStack input, ItemStack output) {
               if (input.isEmpty() || output.isEmpty()) {
                  return true;
               } else if (!ItemStack.isSameItem(input, output)) {
                  return true;
               } else {
                  return input.has((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get())
                        && output.has((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get())
                     ? !Objects.equals(
                        input.get((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get()),
                        output.get((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get())
                     )
                     : true;
               }
            }

            public boolean mayPlace(ItemStack pStack) {
               return EnchantmentHelper.hasTag(pStack, TensuraTags.Enchantments.SEALING_CURSE)
                  ? false
                  : super.mayPlace(pStack) && !ResearcherEnchantingMenu.this.mayPickup;
            }

            public boolean mayPickup(Player pPlayer) {
               return super.mayPickup(pPlayer) && !ResearcherEnchantingMenu.this.mayPickup;
            }
         }
      );
      this.addSlot(new Slot(this.itemOutput, 0, 147, 114) {
         public int getMaxStackSize() {
            return 1;
         }

         public void setChanged() {
            super.setChanged();
         }

         public boolean mayPlace(ItemStack pStack) {
            return false;
         }

         public boolean mayPickup(Player pPlayer) {
            return super.mayPickup(pPlayer) && ResearcherEnchantingMenu.this.mayPickup;
         }

         public void onTake(Player pPlayer, ItemStack pStack) {
            super.onTake(pPlayer, pStack);
            pStack.remove((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get());
            ResearcherEnchantingMenu.this.mayPickup = false;
         }
      });
   }

   public boolean clickMenuButton(Player player, int i) {
      if (i == -1) {
         ManasSkillInstance instance = this.getSkillInstance(player);
         if (instance != null && this.getSkill() instanceof ISpatialStorage storage) {
            if (player instanceof ServerPlayer serverPlayer) {
               this.removed(player);
               storage.openSpatialStoragePage(serverPlayer, this.getStorageOwner(), instance, 0);
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Nullable
   private ManasSkillInstance getSkillInstance(Player player) {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(this.skill);
      return optional.orElse(null);
   }

   private void addPlayerInventorySlots(Inventory inv) {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(inv, l + i * 9 + 9, 8 + l * 18, 145 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(inv, i, 8 + i * 18, 203));
      }
   }

   private void previewEnchantmentsOnItem(boolean clear) {
      if (clear) {
         this.itemOutput.clearContent();
         this.itemOutput.setItem(0, this.getOutputItem(this.itemInput.getItem(0)));
         EnchantmentHelper.setEnchantments(this.itemOutput.getItem(0), ItemEnchantments.EMPTY);
      }

      ItemStack stack = this.itemOutput.getItem(0);
      if (!stack.isEmpty()) {
         ItemEnchantments map = IResearcherEnchanter.getSelectedEnchantments(this.player, this.skill);
         if (!map.isEmpty()) {
            EnchantmentHelper.setEnchantments(stack, map);
            this.broadcastFullState();
         }
      }
   }

   public void editSelection(int id, int level) {
      if (!this.mayPickup) {
         ItemEnchantments mapAll = this.getAllEnchantments();
         if (!mapAll.isEmpty() && id < mapAll.entrySet().size()) {
            Holder<Enchantment> enchantment = IResearcherEnchanter.getSortedEnchantmentList(mapAll).get(id);
            if (!enchantment.is(TensuraTags.Enchantments.ENGRAVING) || !(this.itemOutput.getItem(0).getItem() instanceof EnchantedBookItem)) {
               int maxEnchantLevel = Math.min(this.getMaxStoredLevel(enchantment), this.getMaxEnchantLevel(enchantment, this.player));
               if (level > maxEnchantLevel) {
                  level = maxEnchantLevel;
               }

               int maxLevel = mapAll.getLevel(enchantment);
               ItemEnchantments map = IResearcherEnchanter.getSelectedEnchantments(this.player, this.skill);
               ItemEnchantments originalMap = EnchantmentHelper.getEnchantmentsForCrafting(this.itemInput.getItem(0));
               if (level == 0 && map.getLevel(enchantment) > 0) {
                  IResearcherEnchanter.setSelectedEnchantment(this.player, originalMap, enchantment, 0, this.skill);
                  this.previewEnchantmentsOnItem(true);
               } else if (map.getLevel(enchantment) > 0) {
                  IResearcherEnchanter.setSelectedEnchantment(this.player, originalMap, enchantment, level, this.skill);
                  this.previewEnchantmentsOnItem(true);
               } else {
                  IResearcherEnchanter.setSelectedEnchantment(this.player, originalMap, enchantment, maxLevel, this.skill);
                  this.previewEnchantmentsOnItem(false);
               }

               this.broadcastFullState();
            }
         }
      }
   }

   public int getMaxStoredLevel(Holder<Enchantment> enchantment) {
      return this.getAllEnchantments().getLevel(enchantment);
   }

   public ItemEnchantments getAllEnchantments() {
      return IResearcherEnchanter.getAllEnchantments(this.player, this.skill);
   }

   public void handleFinalizeEnchantment() {
      ItemStack stack = this.getOutputItem(this.itemInput.getItem(0));
      if (stack.isEmpty()) {
         IResearcherEnchanter.addSelectedEnchantments(this.player, ItemEnchantments.EMPTY, this.skill, true);
      } else {
         int cost = this.getExperienceCost();
         if (getTotalXp(this.player) >= cost) {
            if (cost > 0) {
               this.player.giveExperiencePoints(-cost);
            }

            this.mayPickup = true;
            ItemEnchantments selected = IResearcherEnchanter.getSelectedEnchantments(this.player, this.skill);
            EnchantmentHelper.setEnchantments(stack, selected);
            if (cost > 0 && this.getSkill() instanceof IResearcherEnchanter enchanter) {
               int engraving = 0;

               for (Holder<Enchantment> holder : selected.keySet()) {
                  if (holder.is(TensuraTags.Enchantments.ENGRAVING)) {
                     engraving++;
                  }
               }

               if (engraving > 0 && this.player.getRandom().nextFloat() < enchanter.getCurseChancePerEngraving() * engraving) {
                  EngravingHelper.applyCurseEngraving(this.player, stack, 1);
               }
            }

            if (stack.is(Items.ENCHANTED_BOOK) && selected.isEmpty()) {
               stack = this.itemInput.getItem(0).copy();
            }

            this.itemInput.clearContent();
            this.itemOutput.setItem(0, stack);
            this.broadcastFullState();
            IResearcherEnchanter.addSelectedEnchantments(this.player, ItemEnchantments.EMPTY, this.skill, true);
            if (this.oldEnchantments.size() < selected.size()) {
               this.addSkillMastery();
            }
         }
      }
   }

   public ItemStack getOutputItem(ItemStack stack) {
      return stack.is(Items.BOOK) ? Items.ENCHANTED_BOOK.getDefaultInstance() : stack.copy();
   }

   public int getExperienceCost() {
      int cost = 0;
      if (this.player.isCreative()) {
         return cost;
      }

      ItemEnchantments newEnchantments = IResearcherEnchanter.getNewEnchantments(this.player, this.skill);

      for (Holder<Enchantment> holder : newEnchantments.keySet()) {
         cost += ((Enchantment)holder.value()).getMinCost(newEnchantments.getLevel(holder));
      }

      return cost;
   }

   public static long getTotalXp(Player player) {
      int level = player.experienceLevel;
      float progress = player.experienceProgress;
      long xp = 0L;

      for (int i = 0; i < level; i++) {
         if (i >= 30) {
            xp += 112L + (i - 30) * 9L;
         } else if (i >= 15) {
            xp += 37L + (i - 15) * 5L;
         } else {
            xp += 7L + i * 2L;
         }
      }

      long xpForNextLevel;
      if (level >= 30) {
         xpForNextLevel = 112L + (level - 30) * 9L;
      } else if (level >= 15) {
         xpForNextLevel = 37L + (level - 15) * 5L;
      } else {
         xpForNextLevel = 7L + level * 2L;
      }

      return xp + (long)(progress * (float)xpForNextLevel);
   }

   public int getMaxEnchantLevel(Holder<Enchantment> enchantment, Player player) {
      if (player.isCreative()) {
         return 255;
      } else {
         return this.getSkill() instanceof IResearcherEnchanter enchanter
            ? enchanter.getMaxLevel(enchantment)
            : ((Enchantment)enchantment.value()).getMaxLevel();
      }
   }

   private void addSkillMastery() {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(this.player).getSkill(this.getSkill());
      if (optional.isPresent()) {
         optional.get().addMasteryPoint(this.player);
         optional.get().markDirty();
         SkillAPI.getSkillsFrom(this.player).markDirty();
      }
   }

   public void removed(Player pPlayer) {
      IResearcherEnchanter.addSelectedEnchantments(this.player, ItemEnchantments.EMPTY, this.skill, true);
      ItemStack input = this.itemInput.getItem(0);
      ItemStack output = this.itemOutput.getItem(0);
      if (!input.isEmpty() && input.has((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get())) {
         input.remove((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get());
      }

      if (!output.isEmpty() && output.has((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get())) {
         output.remove((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get());
      }

      if (this.itemInput.isEmpty() && !this.itemOutput.isEmpty()) {
         this.clearContainer(pPlayer, this.itemOutput);
      } else {
         this.clearContainer(pPlayer, this.itemInput);
      }

      this.clearContainer(pPlayer, this.bookInput);
      super.removed(pPlayer);
   }

   public boolean stillValid(Player player) {
      return player.isAlive();
   }

   public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
      ItemStack copy = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(pIndex);
      if (slot != null && slot.hasItem()) {
         ItemStack stack = slot.getItem();
         if (stack.has((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get())) {
            stack.remove((DataComponentType)TensuraDataComponents.ENCHANT_COUNTER.get());
         }

         copy = stack.copy();
         if (pIndex >= 0 && pIndex < 36) {
            if (!this.moveItemStackTo(stack, 36, 39, false)) {
               if (pIndex < 27) {
                  if (!this.moveItemStackTo(stack, 27, 36, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (!this.moveItemStackTo(stack, 0, 27, false)) {
                  return ItemStack.EMPTY;
               }
            }
         } else if (!this.moveItemStackTo(stack, 0, 36, false)) {
            return ItemStack.EMPTY;
         }

         if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(pPlayer, stack);
      }

      return copy;
   }

   @Generated
   public Player getPlayer() {
      return this.player;
   }

   @Generated
   public ManasSkill getSkill() {
      return this.skill;
   }

   @Generated
   public LivingEntity getStorageOwner() {
      return this.storageOwner;
   }

   @Generated
   public SimpleContainer getBookInput() {
      return this.bookInput;
   }

   @Generated
   public SimpleContainer getItemInput() {
      return this.itemInput;
   }

   @Generated
   public SimpleContainer getItemOutput() {
      return this.itemOutput;
   }
}
