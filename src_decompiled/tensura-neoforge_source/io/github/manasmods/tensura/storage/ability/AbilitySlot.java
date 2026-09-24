package io.github.manasmods.tensura.storage.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class AbilitySlot {
   public static final Codec<AbilitySlot> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC
               .optionalFieldOf("skill")
               .forGetter(slot -> slot.getSkill() != null ? Optional.ofNullable(slot.getSkill().getRegistryName()) : Optional.empty()),
            Codec.INT.fieldOf("mode").forGetter(AbilitySlot::getMode)
         )
         .apply(instance, (location, integer) -> {
            ManasSkill skill = location.<ManasSkill>map(loc -> (ManasSkill)SkillAPI.getSkillRegistry().get(loc)).orElse(null);
            return new AbilitySlot(skill, integer);
         })
   );
   private int mode;
   @Nullable
   private ManasSkill skill;

   public AbilitySlot(@Nullable ManasSkill skill, int mode) {
      this.skill = skill;
      this.mode = mode;
   }

   public final CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      if (this.skill != null) {
         nbt.putString("ability", this.skill.getRegistryName().toString());
      } else if (nbt.contains("ability")) {
         nbt.remove("ability");
      }

      nbt.putInt("mode", this.mode);
      return nbt;
   }

   public void deserialize(CompoundTag tag) {
      if (tag.contains("ability")) {
         this.skill = (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.tryParse(tag.getString("ability")));
      }

      this.mode = tag.getInt("mode");
   }

   public static AbilitySlot fromNBT(CompoundTag tag) {
      ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.tryParse(tag.getString("ability")));
      return new AbilitySlot(skill, tag.getInt("mode"));
   }

   public void setSkillAndMode(ManasSkill skill, int mode) {
      this.skill = skill;
      this.mode = mode;
   }

   public boolean isEmpty() {
      return this.skill == null || this.getMode() < 0;
   }

   public static AbilitySlot getEmpty() {
      return new AbilitySlot(null, 0);
   }

   public static List<AbilitySlot> getEmptyList(int i) {
      List<AbilitySlot> list = new ArrayList<>();

      for (int count = 0; count < i; count++) {
         list.add(getEmpty());
      }

      return list;
   }

   public static Comparator<AbilitySlot> getComparator() {
      return Comparator.comparing(slot -> slot.getSkill().getRegistryName()).thenComparingInt(AbilitySlot::getMode);
   }

   @Generated
   public int getMode() {
      return this.mode;
   }

   @Nullable
   @Generated
   public ManasSkill getSkill() {
      return this.skill;
   }

   @Generated
   public void setMode(int mode) {
      this.mode = mode;
   }

   @Generated
   public void setSkill(@Nullable ManasSkill skill) {
      this.skill = skill;
   }
}
