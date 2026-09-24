package io.github.manasmods.tensura.storage.ability;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.storage.api.Storage;
import io.github.manasmods.manascore.storage.api.StorageEvents;
import io.github.manasmods.manascore.storage.api.StorageKey;
import io.github.manasmods.manascore.storage.api.StorageEvents.RegisterStorage;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AbilityStorage extends Storage implements IAbility {
   @Generated
   private static final Logger log = LogManager.getLogger(AbilityStorage.class);
   private static StorageKey<AbilityStorage> key = null;
   private int activePreset = 0;
   private double waterPoint = 0.0;
   private double lavaPoint = 0.0;
   private List<AbilityPreset> presets;

   public static void init() {
      StorageEvents.REGISTER_ENTITY_STORAGE
         .register(
            (RegisterStorage)registry -> key = registry.register(
               ResourceLocation.fromNamespaceAndPath("tensura", "ability_storage"),
               AbilityStorage.class,
               LivingEntity.class::isInstance,
               target -> new AbilityStorage((LivingEntity)target)
            )
         );
   }

   protected AbilityStorage(LivingEntity holder) {
      super(holder);
   }

   @Override
   public List<AbilityPreset> getPresets() {
      if (this.presets == null) {
         this.presets = AbilityPreset.getEmptyList(9);
      }

      return this.presets;
   }

   public void save(CompoundTag data) {
      data.putInt("active", this.activePreset);
      if (this.presets != null) {
         CompoundTag ability = new CompoundTag();

         for (int i = 0; i < this.presets.size(); i++) {
            ability.put("preset_" + i, this.presets.get(i).serialize());
         }

         data.put("presets", ability);
      }

      data.putDouble("waterPoint", this.waterPoint);
      data.putDouble("lavaPoint", this.lavaPoint);
   }

   public void load(CompoundTag data) {
      this.activePreset = data.getInt("active");
      if (data.contains("presets")) {
         CompoundTag presetList = data.getCompound("presets");
         List<AbilityPreset> p = this.getPresets();

         for (int i = 0; i < p.size(); i++) {
            p.get(i).deserialize(presetList.getCompound("preset_" + i));
         }
      }

      this.waterPoint = data.getDouble("waterPoint");
      this.lavaPoint = data.getDouble("lavaPoint");
   }

   protected LivingEntity getOwner() {
      return (LivingEntity)this.holder;
   }

   @Override
   public AbilityPreset getPreset(int number) {
      return this.getPresets().get(number);
   }

   @Override
   public String getPresetName(int number) {
      return this.getPresets().get(number).getName();
   }

   @Override
   public List<String> getAllPresetNames() {
      List<String> names = new ArrayList<>();
      this.getPresets().forEach(preset -> names.add(preset.getName()));
      return names;
   }

   @Override
   public void setPresetName(int number, String name) {
      this.getPresets().get(number).setName(name);
   }

   @Override
   public List<AbilitySlot> getAbilitySlots() {
      return this.getPresets().get(this.activePreset).getAbilities();
   }

   @Override
   public List<AbilitySlot> getAbilitySlots(int preset) {
      return this.getPresets().get(preset).getAbilities();
   }

   @Override
   public AbilitySlot getAbilitySlot(int slot) {
      return this.getPresets().get(this.activePreset).getAbilities().get(slot);
   }

   @Override
   public AbilitySlot getAbilitySlot(int preset, int slot) {
      return this.getPresets().get(preset).getAbilities().get(slot);
   }

   @Override
   public void setAbilitySlot(int slot, ManasSkill skill, int mode) {
      this.getPresets().get(this.activePreset).getAbilities().set(slot, new AbilitySlot(skill, mode));
   }

   @Override
   public void setAbilitySlot(int preset, int slot, ManasSkill skill, int mode) {
      this.getPresets().get(preset).getAbilities().set(slot, new AbilitySlot(skill, mode));
   }

   @Override
   public void setAbilitySlot(int preset, int slot, AbilitySlot abilitySlot) {
      this.getPresets().get(preset).getAbilities().set(slot, abilitySlot);
   }

   @Override
   public boolean isAbilityInActivePreset(int slot, ManasSkill skill) {
      if (this.presets == null) {
         return false;
      }

      AbilitySlot abilitySlot = this.presets.get(this.activePreset).getAbilities().get(slot);
      return abilitySlot.getSkill() == skill;
   }

   @Override
   public boolean isAbilityInActivePreset(int slot, ManasSkill skill, int mode) {
      if (slot >= 0 && this.presets != null) {
         List<AbilitySlot> slots = this.presets.get(this.activePreset).getAbilities();
         if (slot >= slots.size()) {
            return false;
         }

         AbilitySlot abilitySlot = slots.get(slot);
         return abilitySlot.getSkill() == skill && abilitySlot.getMode() == mode;
      } else {
         return false;
      }
   }

   @Override
   public boolean isAbilityInActivePreset(ManasSkill skill) {
      if (this.presets == null) {
         return false;
      }

      for (AbilitySlot slot : this.presets.get(this.activePreset).getAbilities()) {
         if (slot.getSkill() == skill) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isAbilityInActivePreset(ManasSkill skill, int mode) {
      if (this.presets == null) {
         return false;
      }

      for (AbilitySlot slot : this.presets.get(this.activePreset).getAbilities()) {
         if (slot.getSkill() == skill && slot.getMode() == mode) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isAbilityInPresets(ManasSkill skill) {
      if (this.presets == null) {
         return false;
      }

      for (AbilityPreset preset : this.presets) {
         for (AbilitySlot slot : preset.getAbilities()) {
            if (slot.getSkill() == skill) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean isAbilityInPresets(ManasSkill skill, int mode) {
      if (this.presets == null) {
         return false;
      }

      for (AbilityPreset preset : this.presets) {
         for (AbilitySlot slot : preset.getAbilities()) {
            if (slot.getSkill() == skill && slot.getMode() == mode) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean removeSkillFromPresets(ManasSkill skill) {
      return this.removeSkillFromPresets(slot -> slot.getSkill() == skill);
   }

   @Override
   public boolean removeSkillFromPresets(ManasSkill skill, int mode) {
      return this.removeSkillFromPresets(slot -> slot.getSkill() == skill && slot.getMode() == mode);
   }

   @Override
   public boolean removeSkillFromPresets(Predicate<AbilitySlot> predicate) {
      if (this.presets == null) {
         return false;
      }

      boolean success = false;

      for (AbilityPreset preset : this.presets) {
         for (AbilitySlot slot : preset.getAbilities()) {
            if (predicate.test(slot)) {
               slot.setSkill(null);
               slot.setMode(0);
               success = true;
            }
         }
      }

      return success;
   }

   @Override
   public void setWaterPoint(double point) {
      this.waterPoint = Mth.clamp(point, 0.0, this.getOwner().getAttributeValue(TensuraAttributes.WATER_CAPACITY));
   }

   @Override
   public void setLavaPoint(double point) {
      this.lavaPoint = Mth.clamp(point, 0.0, this.getOwner().getAttributeValue(TensuraAttributes.LAVA_CAPACITY));
   }

   @Generated
   public static StorageKey<AbilityStorage> getKey() {
      return key;
   }

   @Generated
   @Override
   public int getActivePreset() {
      return this.activePreset;
   }

   @Generated
   @Override
   public void setActivePreset(int activePreset) {
      this.activePreset = activePreset;
   }

   @Generated
   @Override
   public double getWaterPoint() {
      return this.waterPoint;
   }

   @Generated
   @Override
   public double getLavaPoint() {
      return this.lavaPoint;
   }
}
