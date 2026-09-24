package io.github.manasmods.tensura.storage.ability;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import java.util.List;
import java.util.function.Predicate;

public interface IAbility {
   int getActivePreset();

   void setActivePreset(int var1);

   List<AbilityPreset> getPresets();

   AbilityPreset getPreset(int var1);

   String getPresetName(int var1);

   List<String> getAllPresetNames();

   void setPresetName(int var1, String var2);

   List<AbilitySlot> getAbilitySlots();

   List<AbilitySlot> getAbilitySlots(int var1);

   AbilitySlot getAbilitySlot(int var1);

   AbilitySlot getAbilitySlot(int var1, int var2);

   void setAbilitySlot(int var1, ManasSkill var2, int var3);

   void setAbilitySlot(int var1, int var2, ManasSkill var3, int var4);

   void setAbilitySlot(int var1, int var2, AbilitySlot var3);

   boolean isAbilityInActivePreset(int var1, ManasSkill var2);

   boolean isAbilityInActivePreset(int var1, ManasSkill var2, int var3);

   boolean isAbilityInActivePreset(ManasSkill var1);

   boolean isAbilityInActivePreset(ManasSkill var1, int var2);

   boolean isAbilityInPresets(ManasSkill var1);

   boolean isAbilityInPresets(ManasSkill var1, int var2);

   boolean removeSkillFromPresets(ManasSkill var1);

   boolean removeSkillFromPresets(ManasSkill var1, int var2);

   boolean removeSkillFromPresets(Predicate<AbilitySlot> var1);

   double getWaterPoint();

   void setWaterPoint(double var1);

   double getLavaPoint();

   void setLavaPoint(double var1);

   void markDirty();
}
