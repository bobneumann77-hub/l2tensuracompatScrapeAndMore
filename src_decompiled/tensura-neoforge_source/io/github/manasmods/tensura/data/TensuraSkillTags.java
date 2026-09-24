package io.github.manasmods.tensura.data;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class TensuraSkillTags {
   public static final TagKey<ManasSkill> BATTLEWILL = modTag("battlewill");
   public static final TagKey<ManasSkill> SKILLS = modTag("skills");
   public static final TagKey<ManasSkill> COMMON_SKILLS = modTag("common_skills");
   public static final TagKey<ManasSkill> EXTRA_SKILLS = modTag("extra_skills");
   public static final TagKey<ManasSkill> INTRINSIC_SKILLS = modTag("intrinsic_skills");
   public static final TagKey<ManasSkill> RESISTANCE_SKILLS = modTag("resistance_skills");
   public static final TagKey<ManasSkill> UNIQUE_SKILLS = modTag("unique_skills");
   public static final TagKey<ManasSkill> ULTIMATE_SKILLS = modTag("ultimate_skills");
   public static final TagKey<ManasSkill> MAGIC = modTag("magic");
   public static final TagKey<ManasSkill> UNBINDABLE_MAGIC = modTag("unbindable_magic");
   public static final TagKey<ManasSkill> COPIABLE_MAGIC = modTag("copiable_magic");
   public static final TagKey<ManasSkill> RESET_WITH_RACE = modTag("reset_with_race");
   public static final TagKey<ManasSkill> FOUND_IN_TOME = modTag("found_in_tome");
   public static final TagKey<ManasSkill> TOME_COPY_EXCLUDED = modTag("tome_copy_excluded");
   public static final TagKey<ManasSkill> UNLEARNT_CAST_EXCLUDED = modTag("unlearnt_cast_excluded");
   public static final TagKey<ManasSkill> ASPECTUAL_MAGIC = modTag("aspectual_magic");
   public static final TagKey<ManasSkill> SPIRITUAL_MAGIC = modTag("spiritual_magic");
   public static final TagKey<ManasSkill> SUMMONING_MAGIC = modTag("summoning_magic");
   public static final TagKey<ManasSkill> NECROMANCY_MAGIC = modTag("necromancy_magic");
   public static final TagKey<ManasSkill> LOW_MANUAL_DWARF_TRADE = modTag("low_manual_dwarf_trade");
   public static final TagKey<ManasSkill> MEDIUM_MANUAL_DWARF_TRADE = modTag("medium_manual_dwarf_trade");
   public static final TagKey<ManasSkill> HIGH_MANUAL_DWARF_TRADE = modTag("high_manual_dwarf_trade");
   public static final TagKey<ManasSkill> RARE_MANUAL_DWARF_TRADE = modTag("rare_manual_dwarf_trade");
   public static final TagKey<ManasSkill> LOW_BASIC_TOME_DWARF_TRADE = modTag("low_basic_tome_dwarf_trade");
   public static final TagKey<ManasSkill> MEDIUM_BASIC_TOME_DWARF_TRADE = modTag("medium_basic_tome_dwarf_trade");
   public static final TagKey<ManasSkill> HIGH_BASIC_TOME_DWARF_TRADE = modTag("high_basic_tome_dwarf_trade");
   public static final TagKey<ManasSkill> GREAT_BASIC_TOME_DWARF_TRADE = modTag("great_basic_tome_dwarf_trade");
   public static final TagKey<ManasSkill> LOW_UPGRADED_TOME_DWARF_TRADE = modTag("low_upgraded_tome_dwarf_trade");
   public static final TagKey<ManasSkill> MEDIUM_UPGRADED_TOME_DWARF_TRADE = modTag("medium_upgraded_tome_dwarf_trade");
   public static final TagKey<ManasSkill> HIGH_UPGRADED_TOME_DWARF_TRADE = modTag("high_upgraded_tome_dwarf_trade");
   public static final TagKey<ManasSkill> GREAT_UPGRADED_TOME_DWARF_TRADE = modTag("great_upgraded_tome_dwarf_trade");
   public static final TagKey<ManasSkill> LOW_RARE_TOME_TRADE = modTag("low_rare_tome_dwarf_trade");
   public static final TagKey<ManasSkill> MEDIUM_RARE_TOME_TRADE = modTag("medium_rare_tome_dwarf_trade");
   public static final TagKey<ManasSkill> HIGH_RARE_TOME_TRADE = modTag("high_rare_tome_dwarf_trade");
   public static final TagKey<ManasSkill> GREAT_RARE_TOME_DWARF_TRADE = modTag("great_rare_tome_dwarf_trade");
   public static final TagKey<ManasSkill> COMMON_TOME_BURIED = modTag("common_tome_buried_wizard_tower");
   public static final TagKey<ManasSkill> UNCOMMON_TOME_BURIED = modTag("uncommon_tome_buried_wizard_tower");
   public static final TagKey<ManasSkill> RARE_TOME_BURIED = modTag("rare_tome_buried_wizard_tower");
   public static final TagKey<ManasSkill> COMMON_TOME_BURNT = modTag("common_tome_burnt_wizard_tower");
   public static final TagKey<ManasSkill> UNCOMMON_TOME_BURNT = modTag("uncommon_tome_burnt_wizard_tower");
   public static final TagKey<ManasSkill> RARE_TOME_BURNT = modTag("rare_tome_burnt_wizard_tower");
   public static final TagKey<ManasSkill> COMMON_TOME_FROZEN = modTag("common_tome_frozen_wizard_tower");
   public static final TagKey<ManasSkill> UNCOMMON_TOME_FROZEN = modTag("uncommon_tome_frozen_wizard_tower");
   public static final TagKey<ManasSkill> RARE_TOME_FROZEN = modTag("rare_tome_frozen_wizard_tower");
   public static final TagKey<ManasSkill> COMMON_TOME_ROTTED = modTag("common_tome_rotted_wizard_tower");
   public static final TagKey<ManasSkill> UNCOMMON_TOME_ROTTED = modTag("uncommon_tome_rotted_wizard_tower");
   public static final TagKey<ManasSkill> RARE_TOME_ROTTED = modTag("rare_tome_rotted_wizard_tower");
   public static final TagKey<ManasSkill> COMMON_TOME_RUINED = modTag("common_tome_ruined_wizard_tower");
   public static final TagKey<ManasSkill> UNCOMMON_TOME_RUINED = modTag("uncommon_tome_ruined_wizard_tower");
   public static final TagKey<ManasSkill> RARE_TOME_RUINED = modTag("rare_tome_ruined_wizard_tower");
   public static final TagKey<ManasSkill> EPIC_TOME_TOWER = modTag("epic_tome_wizard_tower");
   public static final TagKey<ManasSkill> HELL_TREASURE_TOME = modTag("hell_treasure_tome");
   public static final TagKey<ManasSkill> NO_PLUNDERING = modTag("no_plundering");
   public static final TagKey<ManasSkill> HAS_MAGICULE_RICH_HAKI = modTag("has_magicule_rich_haki");
   public static final TagKey<ManasSkill> ELEMENTAL_MANIPULATION = modTag("elemental_manipulation");
   public static final TagKey<ManasSkill> ELEMENTAL_DOMINATION = modTag("elemental_domination");
   public static final TagKey<ManasSkill> EARTH_SKILLS = modTag("earth_skills");
   public static final TagKey<ManasSkill> FLAME_SKILLS = modTag("flame_skills");
   public static final TagKey<ManasSkill> GRAVITY_SKILLS = modTag("gravity_skills");
   public static final TagKey<ManasSkill> LIGHTNING_SKILLS = modTag("lightning_skills");
   public static final TagKey<ManasSkill> SOUND_SKILLS = modTag("sound_skills");
   public static final TagKey<ManasSkill> SPACE_SKILLS = modTag("space_skills");
   public static final TagKey<ManasSkill> WATER_SKILLS = modTag("water_skills");
   public static final TagKey<ManasSkill> WIND_SKILLS = modTag("wind_skills");

   static TagKey<ManasSkill> modTag(String name) {
      return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
   }

   static TagKey<ManasSkill> create(ResourceLocation name) {
      return TagKey.create(SkillAPI.getSkillRegistryKey(), name);
   }
}
