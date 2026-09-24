package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BodyArmorSkill extends Skill {
   private static final IntrinsicSkillConfig.BodyArmor CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).BodyArmor;

   public BodyArmorSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return true;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (entity.getItemBySlot(slot).is(TensuraItemTags.BODY_ARMOR_ITEMS)) {
               instance.addMasteryPoint(entity);
            }
         }

         tag.putInt("activatedTimes", 0);
      } else {
         tag.putInt("activatedTimes", time + 1);
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.isShiftKeyDown()) {
         if (entity.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && !EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            entity.setItemSlot(EquipmentSlot.MAINHAND, this.getDummyItem((Item)TensuraToolItems.ARMORSAURUS_GAUNTLET.get()));
         } else if (entity.getItemBySlot(EquipmentSlot.MAINHAND).is((Item)TensuraToolItems.ARMORSAURUS_GAUNTLET.get())) {
            entity.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
         }
      } else if (!entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
         && !entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
         && !entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
         && !entity.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {
         if (entity.getItemBySlot(EquipmentSlot.HEAD).is((Item)TensuraArmorItems.ARMORSAURUS_HELMET.get())) {
            entity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
         }

         if (entity.getItemBySlot(EquipmentSlot.CHEST).is((Item)TensuraArmorItems.ARMORSAURUS_CHESTPLATE.get())) {
            entity.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
         }

         if (entity.getItemBySlot(EquipmentSlot.LEGS).is((Item)TensuraArmorItems.ARMORSAURUS_LEGGINGS.get())) {
            entity.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
         }

         if (entity.getItemBySlot(EquipmentSlot.FEET).is((Item)TensuraArmorItems.ARMORSAURUS_BOOTS.get())) {
            entity.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
         }

         entity.level()
            .playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.DEFENCE_DEACTIVATE.get(),
               TensuraSkill.ABILITY_SOUND,
               1.0F,
               1.0F
            );
      } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         if (entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            entity.setItemSlot(EquipmentSlot.HEAD, this.getDummyItem((Item)TensuraArmorItems.ARMORSAURUS_HELMET.get()));
         }

         if (entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            entity.setItemSlot(EquipmentSlot.CHEST, this.getDummyItem((Item)TensuraArmorItems.ARMORSAURUS_CHESTPLATE.get()));
         }

         if (entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty()) {
            entity.setItemSlot(EquipmentSlot.LEGS, this.getDummyItem((Item)TensuraArmorItems.ARMORSAURUS_LEGGINGS.get()));
         }

         if (entity.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {
            entity.setItemSlot(EquipmentSlot.FEET, this.getDummyItem((Item)TensuraArmorItems.ARMORSAURUS_BOOTS.get()));
         }

         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }

   private ItemStack getDummyItem(Item item) {
      ItemStack stack = new ItemStack(item);
      stack.set((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), true);
      return stack;
   }
}
