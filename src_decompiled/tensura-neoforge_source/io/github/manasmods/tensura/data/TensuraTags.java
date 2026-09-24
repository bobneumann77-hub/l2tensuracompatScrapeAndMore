package io.github.manasmods.tensura.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.levelgen.structure.Structure;

public class TensuraTags {
   public static class BannerPattens {
      public static TagKey<BannerPattern> DWARGON = modTag("dwargon");

      static TagKey<BannerPattern> modTag(String name) {
         return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
      }

      static TagKey<BannerPattern> create(ResourceLocation name) {
         return TagKey.create(Registries.BANNER_PATTERN, name);
      }
   }

   public static class DamageTypes {
      public static TagKey<DamageType> IS_MENTAL = modTag("is_mental");
      public static TagKey<DamageType> IS_SPIRITUAL = modTag("is_spiritual");
      public static TagKey<DamageType> IS_PHYSICAL = modTag("is_physical");
      public static TagKey<DamageType> IS_MAGIC_FIRE = modTag("is_magic_fire");
      public static TagKey<DamageType> BYPASS_ANTI_SKILL = modTag("bypass_anti_skill");
      public static TagKey<DamageType> BYPASS_DODGE = modTag("bypass_dodge");
      public static TagKey<DamageType> BYPASS_PROTECTION_ENCHANTMENT = modTag("bypass_protection_enchantment");
      public static TagKey<DamageType> BYPASS_BARRIER = modTag("bypass_barrier");
      public static TagKey<DamageType> BYPASS_DIMENSION_FAULT = modTag("bypass_dimension_fault");
      public static TagKey<DamageType> BYPASS_DISTORTION_FIELD = modTag("bypass_distortion_field");
      public static TagKey<DamageType> BYPASS_MULTIDIMENSIONAL_BARRIER = modTag("bypass_multidimensional_barrier");
      public static TagKey<DamageType> STOP_DEATH_PENALTY = modTag("stop_death_penalty");

      static TagKey<DamageType> modTag(String name) {
         return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
      }

      static TagKey<DamageType> create(ResourceLocation name) {
         return TagKey.create(Registries.DAMAGE_TYPE, name);
      }
   }

   public static class Enchantments {
      public static TagKey<Enchantment> ENGRAVING = modTag("engraving");
      public static TagKey<Enchantment> INHERITANCE_ENGRAVING = modTag("inheritance_engraving");
      public static TagKey<Enchantment> ENGRAVING_EXCLUSIVE = modTag("engraving_exclusive");
      public static TagKey<Enchantment> SEALING_EXCLUSIVE = modTag("sealing_exclusive");
      public static TagKey<Enchantment> SEALING_CURSE = modTag("sealing_curse");
      public static TagKey<Enchantment> TSUKUMOGAMI = modTag("tsukumogami");

      static TagKey<Enchantment> modTag(String name) {
         return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
      }

      static TagKey<Enchantment> create(ResourceLocation name) {
         return TagKey.create(Registries.ENCHANTMENT, name);
      }
   }

   public static class MobEffects {
      public static TagKey<MobEffect> HIDDEN_ICON = modTag("hidden_icon");
      public static TagKey<MobEffect> SKILL_BUFF = modTag("skill_buff");
      public static TagKey<MobEffect> SKILL_DEBUFF = modTag("skill_debuff");
      public static TagKey<MobEffect> SKILL_EFFECT = modTag("skill_effect");
      public static TagKey<MobEffect> TRANSFORMATION = modTag("transformation");
      public static TagKey<MobEffect> AFFECTED_BY_ANTI_SKILL = modTag("affected_by_anti_skill");
      public static TagKey<MobEffect> AFFECTED_BY_LAW_MANIPULATION = modTag("affected_by_law_manipulation");
      public static TagKey<MobEffect> IGNORE_ANTI_MAGICULE = modTag("ignore_anti_magicule");
      public static TagKey<MobEffect> SPIRITUAL_AFFECTED = modTag("spiritual_affected");
      public static TagKey<MobEffect> INCURABLE_BY_MILK = modTag("incurable_by_milk");

      static TagKey<MobEffect> modTag(String name) {
         return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
      }

      static TagKey<MobEffect> create(ResourceLocation name) {
         return TagKey.create(Registries.MOB_EFFECT, name);
      }
   }

   public static class PoiTypes {
      public static TagKey<PoiType> NPC_JOB_SITE = modTag("npc_job_site");

      static TagKey<PoiType> modTag(String name) {
         return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
      }

      static TagKey<PoiType> create(ResourceLocation name) {
         return TagKey.create(Registries.POINT_OF_INTEREST_TYPE, name);
      }
   }

   public static class Potions {
      public static TagKey<Potion> DWARF_ALCHEMIST = modTag("dwarf_alchemist_trade");
      public static TagKey<Potion> DWARF_FLETCHER = modTag("dwarf_fletcher_trade");

      static TagKey<Potion> modTag(String name) {
         return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
      }

      static TagKey<Potion> create(ResourceLocation name) {
         return TagKey.create(Registries.POTION, name);
      }
   }

   public static class Structures {
      public static TagKey<Structure> NO_WINGED_CAT = modTag("no_winged_cat");
      public static TagKey<Structure> ON_PYRAMID_EXPLORER_MAPS = modTag("on_pyramid_explorer_maps");
      public static TagKey<Structure> ON_CHARYBDIS_EXPLORER_MAPS = modTag("on_charybdis_explorer_maps");
      public static TagKey<Structure> ON_HELL_GATE_EXPLORER_MAPS = modTag("on_hell_gate_explorer_maps");
      public static TagKey<Structure> ON_LABYRINTH_EXPLORER_MAPS = modTag("on_labyrinth_explorer_maps");
      public static TagKey<Structure> ON_DWARF_VILLAGE_MAPS = modTag("on_dwarf_village_maps");
      public static TagKey<Structure> ON_LIZARDMAN_VILLAGE_MAPS = modTag("on_lizardman_village_maps");

      static TagKey<Structure> modTag(String name) {
         return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
      }

      static TagKey<Structure> create(ResourceLocation name) {
         return TagKey.create(Registries.STRUCTURE, name);
      }
   }
}
