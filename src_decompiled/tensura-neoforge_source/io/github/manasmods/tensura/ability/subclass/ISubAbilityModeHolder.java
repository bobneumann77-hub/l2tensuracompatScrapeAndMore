package io.github.manasmods.tensura.ability.subclass;

import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.SkillEvents.UnlockSkillEvent;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.network.s2c.OpenSubAbilitySelectionMenuPayload;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.AbilityPreset;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import io.github.manasmods.tensura.storage.ability.IAbility;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;

public interface ISubAbilityModeHolder {
   int getSubModeOffset(ManasSkillInstance var1);

   default void openSubAbilitySelectionMenu(ServerPlayer player, ManasSkill skill) {
      NetworkManager.sendToPlayer(player, new OpenSubAbilitySelectionMenuPayload(player.getId(), skill.getRegistryName()));
      player.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F);
   }

   default void onLearnFail(ManasSkillInstance instance, LivingEntity entity, ManasSkill skill) {
   }

   default boolean learnSubSkill(ManasSkillInstance instance, LivingEntity entity, ManasSkill skill, int mode, double chance) {
      CompoundTag tag = instance.getOrCreateTag();
      List<AbilitySlot> subSlots = getSubSlots(instance);

      for (AbilitySlot slot : subSlots) {
         if (slot.getSkill() == skill && slot.getMode() == mode) {
            return false;
         }
      }

      if (entity.getRandom().nextInt(100) >= chance) {
         this.onLearnFail(instance, entity, skill);
         return false;
      }

      Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();
      MutableComponent component = null;
      ManasSkillInstance subInstance;
      if (!subInstances.containsKey(skill)) {
         subInstance = skill.createDefaultInstance();
         subInstance.setParentSkill((ManasSkill)this);
         if (skill instanceof TensuraSkill tensuraSkill) {
            tensuraSkill.learnMode(subInstance, entity, mode, tensuraSkill.getLearningPointRequirement(subInstance, entity, mode), false);
            component = Component.translatable(
                  "tensura.skill.acquire_mode", new Object[]{tensuraSkill.getModeName(subInstance, mode), instance.getChatDisplayName(true)}
               )
               .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
         }

         subInstances.put(skill, subInstance);
      } else {
         subInstance = subInstances.get(skill);
         if (skill instanceof TensuraSkill tensuraSkill) {
            tensuraSkill.learnMode(subInstance, entity, mode, tensuraSkill.getLearningPointRequirement(subInstance, entity, mode), false);
            component = Component.translatable(
                  "tensura.skill.acquire_mode", new Object[]{tensuraSkill.getModeName(subInstance, mode), instance.getChatDisplayName(true)}
               )
               .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
         }
      }

      if (component == null) {
         component = Component.translatable("tensura.skill.acquire_mode.default", new Object[]{instance.getChatDisplayName(true)})
            .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
      }

      Changeable<MutableComponent> unlockMessage = Changeable.of(component);
      EventResult unlockResult = ((UnlockSkillEvent)SkillEvents.UNLOCK_SKILL.invoker()).unlockSkill(subInstance, entity, unlockMessage);
      if (unlockResult.isFalse()) {
         return false;
      }

      AbilitySlot slot = new AbilitySlot(skill, mode);
      subSlots.add(slot);
      subSlots.sort(AbilitySlot.getComparator());
      int index = subSlots.indexOf(slot);
      IAbility ability = TensuraStorages.getAbilityFrom(entity);

      for (AbilityPreset preset : ability.getPresets()) {
         for (AbilitySlot abilitySlot : preset.getAbilities()) {
            if (abilitySlot.getSkill() == instance.getSkill() && abilitySlot.getMode() > index) {
               abilitySlot.setMode(abilitySlot.getMode() + 1);
            }
         }
      }

      ability.markDirty();
      List<Integer> cooldownList = new ArrayList<>();

      for (int i = 0; i < instance.getModes(); i++) {
         cooldownList.addLast(instance.getCoolDown(i));
      }

      cooldownList.addLast(0);
      instance.setCoolDownList(cooldownList);
      ListTag subSlotList = new ListTag();

      for (int i = 0; i < subSlots.size(); i++) {
         CompoundTag subSlot = new CompoundTag();
         subSlot.put("slot" + i, subSlots.get(i).serialize());
         subSlotList.add(subSlot);
      }

      tag.put("subSlots", subSlotList);
      return true;
   }

   default void removeSubSkill(ManasSkillInstance instance, LivingEntity entity, ManasSkill skill, int mode) {
      List<AbilitySlot> subSlots = getSubSlots(instance);

      for (AbilitySlot slot : subSlots) {
         if (slot.getSkill() == skill && slot.getMode() == mode) {
            int index = subSlots.indexOf(slot) + this.getSubModeOffset(instance);
            subSlots.remove(slot);
            List<Integer> cooldownList = new ArrayList<>();

            for (int i = 0; i < instance.getModes(); i++) {
               cooldownList.addLast(instance.getCoolDown(i));
            }

            cooldownList.remove(index);
            instance.setCoolDownList(cooldownList);
            IAbility ability = TensuraStorages.getAbilityFrom(entity);

            for (AbilityPreset preset : ability.getPresets()) {
               for (AbilitySlot abilitySlot : preset.getAbilities()) {
                  if (abilitySlot.getSkill() == instance.getSkill()) {
                     if (abilitySlot.getMode() == index) {
                        abilitySlot.setSkill(null);
                        abilitySlot.setMode(0);
                     } else if (abilitySlot.getMode() >= index) {
                        abilitySlot.setMode(abilitySlot.getMode() - 1);
                     }
                  }
               }
            }

            ability.markDirty();
            CompoundTag tag = instance.getOrCreateTag();
            if (subSlots.isEmpty()) {
               tag.remove("subSlots");
               instance.getSubInstances().clear();
            } else {
               ListTag subSlotList = new ListTag();

               for (int i = 0; i < subSlots.size(); i++) {
                  CompoundTag subSlot = new CompoundTag();
                  subSlot.put("slot" + i, subSlots.get(i).serialize());
                  subSlotList.add(subSlot);
               }

               tag.put("subSlots", subSlotList);
            }

            instance.markDirty();
            if (!entity.level().isClientSide()) {
               entity.manasCore$sync();
            }
            break;
         }
      }
   }

   default int getModeCooldown(ManasSkillInstance instance, ManasSkill skill, int mode) {
      List<AbilitySlot> subSlots = getSubSlots(instance);

      for (AbilitySlot slot : subSlots) {
         if (slot.getSkill() == skill && slot.getMode() == mode) {
            return instance.getCoolDown(subSlots.indexOf(slot) + this.getSubModeOffset(instance));
         }
      }

      return 0;
   }

   default double getModeMastery(ManasSkillInstance instance, ManasSkill skill, int mode) {
      for (AbilitySlot slot : getSubSlots(instance)) {
         if (slot.getSkill() == skill && slot.getMode() == mode) {
            Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();
            ManasSkillInstance subInstance = subInstances.get(slot.getSkill());
            return subInstance.getMastery();
         }
      }

      return 0.0;
   }

   static boolean isModeInSlot(ManasSkillInstance instance, LivingEntity entity, ManasSkill skill, int mode) {
      List<AbilitySlot> slots = getSubSlots(instance);
      if (slots.isEmpty()) {
         return true;
      }

      int offset = instance.getSkill() instanceof ISubAbilityModeHolder holder ? holder.getSubModeOffset(instance) : 1;

      for (AbilitySlot slot : slots) {
         if (slot.getSkill() == skill
            && slot.getMode() == mode
            && TensuraStorages.getAbilityFrom(entity).isAbilityInActivePreset(instance.getSkill(), slots.indexOf(slot) + offset)) {
            return true;
         }
      }

      return false;
   }

   static List<AbilitySlot> getSubSlots(ManasSkillInstance instance) {
      CompoundTag tag = instance.getOrCreateTag();
      List<AbilitySlot> subSlots = new ArrayList<>();
      ListTag slotList = (ListTag)tag.get("subSlots");
      if (slotList != null) {
         for (int i = 0; i < slotList.size(); i++) {
            subSlots.add(AbilitySlot.fromNBT(((CompoundTag)slotList.get(i)).getCompound("slot" + i)));
         }
      }

      return subSlots;
   }
}
