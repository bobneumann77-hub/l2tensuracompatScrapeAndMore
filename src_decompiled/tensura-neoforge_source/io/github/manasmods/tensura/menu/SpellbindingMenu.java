package io.github.manasmods.tensura.menu;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.block.SpellbindingBlock;
import io.github.manasmods.tensura.block.entity.SpellbindingBlockEntity;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.menu.TensuraMenuTypes;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpellbindingMenu extends AbstractContainerMenu {
   @Generated
   private static final Logger log = LogManager.getLogger(SpellbindingMenu.class);
   private final Player player;
   public final SpellbindingBlockEntity blockEntity;
   private final List<ManasSkillInstance> abilities;
   private final List<ResourceLocation> unlearntAbilities;
   private final Inventory inventory;

   public SpellbindingMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
      this(pContainerId, inv, (SpellbindingBlockEntity)inv.player.level().getBlockEntity(extraData.readBlockPos()));
   }

   public SpellbindingMenu(int pContainerId, Inventory pPlayerInventory, SpellbindingBlockEntity blockEntity) {
      super((MenuType)TensuraMenuTypes.SPELLBINDING.get(), pContainerId);
      this.blockEntity = blockEntity;
      this.inventory = pPlayerInventory;
      this.player = pPlayerInventory.player;
      this.abilities = new ArrayList<>();
      this.unlearntAbilities = new ArrayList<>();
      this.addPlayerInventory();
      this.addPlayerHotbar();
      this.addSlot(new Slot(this.blockEntity, 0, 25, 21) {
         public boolean mayPlace(ItemStack pStack) {
            return pStack.is(TensuraItemTags.SPELL_BINDABLE);
         }

         public void set(ItemStack itemStack) {
            super.set(itemStack);
            SpellbindingMenu.this.updateMagics();
         }
      });
      this.updateMagics();
   }

   private void addPlayerInventory() {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(this.inventory, l + i * 9 + 9, 8 + l * 18, 109 + i * 18));
         }
      }
   }

   private void addPlayerHotbar() {
      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(this.inventory, i, 8 + i * 18, 167));
      }
   }

   protected void updateMagics() {
      this.abilities.clear();
      this.unlearntAbilities.clear();
      ItemStack stack = this.getBlockEntity().getItem(0);
      if (!stack.isEmpty()) {
         Skills skills = SkillAPI.getSkillsFrom(this.player);
         this.abilities
            .addAll(
               skills.getLearnedSkills()
                  .stream()
                  .filter(instance -> instance.is(TensuraSkillTags.MAGIC) && !instance.is(TensuraSkillTags.UNBINDABLE_MAGIC) && instance.getMastery() >= 0.0)
                  .toList()
            );
         List<ResourceLocation> staffSkills = this.getExistingSkills(stack);
         if (!staffSkills.isEmpty()) {
            this.unlearntAbilities.addAll(staffSkills.stream().filter(location -> skills.getSkill(location).isEmpty()).toList());
            if (!this.unlearntAbilities.isEmpty()) {
               this.abilities
                  .addAll(
                     this.unlearntAbilities
                        .stream()
                        .map(location -> {
                           ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(location);
                           return skill == null ? null : skill.createDefaultInstance();
                        })
                        .filter(
                           instance -> instance != null
                              && instance.is(TensuraSkillTags.MAGIC)
                              && !instance.is(TensuraSkillTags.UNBINDABLE_MAGIC)
                              && instance.getMastery() >= 0.0
                        )
                        .toList()
                  );
            }
         }
      }
   }

   public List<ResourceLocation> getExistingSkills(ItemStack stack) {
      List<ResourceLocation> skills = (List<ResourceLocation>)stack.get((DataComponentType)TensuraDataComponents.SKILL_LIST.get());
      return skills == null ? List.of() : skills;
   }

   public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
      return pSlot.container != this.inventory && super.canTakeItemForPickAll(pStack, pSlot);
   }

   public boolean stillValid(Player player) {
      return (Boolean)ContainerLevelAccess.create(this.player.level(), this.blockEntity.getBlockPos())
         .evaluate(
            (level, blockPos) -> level.getBlockState(blockPos).getBlock() instanceof SpellbindingBlock && player.canInteractWithBlock(blockPos, 4.0), true
         );
   }

   public ItemStack quickMoveStack(Player player, int i) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemStack2 = slot.getItem();
         itemStack = itemStack2.copy();
         if (i == 36) {
            if (!this.moveItemStackTo(itemStack2, 0, 36, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemStack2, itemStack);
         } else if (i >= 0 && i <= 36 && !this.moveItemStackTo(itemStack2, 36, 37, false)) {
            if (i < 27) {
               if (!this.moveItemStackTo(itemStack2, 27, 36, true)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(itemStack2, 0, 27, false)) {
               return ItemStack.EMPTY;
            }
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

   @Generated
   public Player getPlayer() {
      return this.player;
   }

   @Generated
   public SpellbindingBlockEntity getBlockEntity() {
      return this.blockEntity;
   }

   @Generated
   public List<ManasSkillInstance> getAbilities() {
      return this.abilities;
   }

   @Generated
   public List<ResourceLocation> getUnlearntAbilities() {
      return this.unlearntAbilities;
   }
}
