package io.github.manasmods.tensura.storage.ability;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;

public class AbilityPreset {
   private String name;
   private final List<AbilitySlot> abilities = AbilitySlot.getEmptyList(3);

   public AbilityPreset(String name) {
      this.name = name;
   }

   public final CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      CompoundTag ability = new CompoundTag();

      for (int i = 0; i < this.abilities.size(); i++) {
         ability.put("ability_" + i, this.abilities.get(i).serialize());
      }

      nbt.put("abilities", ability);
      nbt.putString("name", this.name);
      return nbt;
   }

   public void deserialize(CompoundTag nbt) {
      CompoundTag presetList = nbt.getCompound("abilities");

      for (int i = 0; i < this.abilities.size(); i++) {
         this.abilities.get(i).deserialize(presetList.getCompound("ability_" + i));
      }

      this.name = nbt.getString("name");
   }

   public static AbilityPreset getEmpty() {
      return new AbilityPreset("Preset");
   }

   public static List<AbilityPreset> getEmptyList(int i) {
      List<AbilityPreset> list = new ArrayList<>();

      for (int count = 0; count < i; count++) {
         list.add(new AbilityPreset("Preset " + (count + 1)));
      }

      return list;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public List<AbilitySlot> getAbilities() {
      return this.abilities;
   }

   @Generated
   public void setName(String name) {
      this.name = name;
   }
}
