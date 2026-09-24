package io.github.manasmods.tensura.registry.attribute;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.TensuraAttributeRegister;
import io.github.manasmods.tensura.config.EnergyConfig;
import io.github.manasmods.tensura.config.ability.AbilityConfig;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attribute.Sentiment;

public class TensuraAttributes {
   public static final Holder<Attribute> MAX_SPIRITUAL_HEALTH = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "max_spiritual_health", "tensura.attribute.max_spiritual_health", 60.0, 0.0, 2000000.0, true, Sentiment.NEUTRAL
   );
   public static final Holder<Attribute> SPIRITUAL_HEALTH_REGENERATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "spiritual_health_regeneration", "tensura.attribute.spiritual_health_regeneration", 2.0, 0.0, 2000000.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> MAX_AURA = TensuraAttributeRegister.registerGenericAttribute(
      "tensura",
      "max_aura",
      "tensura.attribute.max_aura",
      50.0,
      ((EnergyConfig)ConfigRegistry.getConfig(EnergyConfig.class)).minAura,
      ((EnergyConfig)ConfigRegistry.getConfig(EnergyConfig.class)).maxAura,
      true,
      Sentiment.NEUTRAL
   );
   public static final Holder<Attribute> LIMITED_SPIRITUAL_MAX_AURA = TensuraAttributeRegister.registerGenericAttribute(
      "tensura",
      "limited_spiritual_max_aura",
      "tensura.attribute.limited_spiritual_max_aura",
      0.0,
      0.0,
      ((EnergyConfig)ConfigRegistry.getConfig(EnergyConfig.class)).maxAura,
      true,
      Sentiment.NEGATIVE
   );
   public static final Holder<Attribute> AURA_REGENERATION_MULTIPLIER = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "aura_regeneration_multiplier", "tensura.attribute.aura_regeneration_multiplier", 1.0, 0.0, 2000000.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> AURA_GAIN = TensuraAttributeRegister.registerGenericAttribute(
      "tensura",
      "aura_gain",
      "tensura.attribute.aura_gain",
      ((EnergyConfig)ConfigRegistry.getConfig(EnergyConfig.class)).baseAuraGain,
      0.0,
      ((EnergyConfig)ConfigRegistry.getConfig(EnergyConfig.class)).maxAuraGain,
      false,
      Sentiment.POSITIVE
   );
   public static final Holder<Attribute> MAX_MAGICULE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura",
      "max_magicule",
      "tensura.attribute.max_magicule",
      50.0,
      ((EnergyConfig)ConfigRegistry.getConfig(EnergyConfig.class)).minMagicule,
      ((EnergyConfig)ConfigRegistry.getConfig(EnergyConfig.class)).maxMagicule,
      true,
      Sentiment.NEUTRAL
   );
   public static final Holder<Attribute> LIMITED_SPIRITUAL_MAX_MAGICULE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura",
      "limited_spiritual_max_magicule",
      "tensura.attribute.limited_spiritual_max_magicule",
      0.0,
      0.0,
      ((EnergyConfig)ConfigRegistry.getConfig(EnergyConfig.class)).maxMagicule,
      true,
      Sentiment.NEGATIVE
   );
   public static final Holder<Attribute> MAGICULE_REGENERATION_MULTIPLIER = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "magicule_regeneration_multiplier", "tensura.attribute.magicule_regeneration_multiplier", 1.0, 0.0, 2000000.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> MAGICULE_GAIN = TensuraAttributeRegister.registerGenericAttribute(
      "tensura",
      "magicule_gain",
      "tensura.attribute.magicule_gain",
      ((EnergyConfig)ConfigRegistry.getConfig(EnergyConfig.class)).baseMagiculeGain,
      0.0,
      ((EnergyConfig)ConfigRegistry.getConfig(EnergyConfig.class)).maxMagiculeGain,
      false,
      Sentiment.POSITIVE
   );
   public static final Holder<Attribute> ABILITY_MASTERY_GAIN = TensuraAttributeRegister.registerGenericAttribute(
      "tensura",
      "ability_mastery_gain",
      "tensura.attribute.ability_mastery_gain",
      ((AbilityConfig)ConfigRegistry.getConfig(AbilityConfig.class)).Mastery.masteryPoint,
      0.0,
      1024.0,
      false,
      Sentiment.POSITIVE
   );
   public static final Holder<Attribute> ABILITY_LEARNING_GAIN = TensuraAttributeRegister.registerGenericAttribute(
      "tensura",
      "ability_learning_gain",
      "tensura.attribute.ability_learning_gain",
      ((AbilityConfig)ConfigRegistry.getConfig(AbilityConfig.class)).Learning.learningPoint,
      0.0,
      1024.0,
      false,
      Sentiment.POSITIVE
   );
   public static final Holder<Attribute> PRESENCE_CONCEALMENT = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "presence_concealment", "tensura.attribute.presence_concealment", 0.0, 0.0, 1024.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> PRESENCE_SENSE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "presence_sense", "tensura.attribute.presence_sense", 0.0, 0.0, 1024.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> PRESENCE_SENSE_RADIUS = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "presence_sense_radius", "tensura.attribute.presence_sense_radius", 30.0, 0.0, 1024.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> HEAT_SENSE_RADIUS = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "heat_sense_radius", "tensura.attribute.heat_sense_radius", 0.0, 0.0, 1024.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> ANALYSIS_LEVEL = TensuraAttributeRegister.registerPlayerAttribute(
      "tensura", "analysis_level", "tensura.attribute.analysis_level", 0.0, 0.0, 1024.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> ANALYSIS_DISTANCE = TensuraAttributeRegister.registerPlayerAttribute(
      "tensura", "analysis_distance", "tensura.attribute.analysis_distance", 5.0, 0.0, 1024.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> VIEW_ZOOM = TensuraAttributeRegister.registerPlayerAttribute(
      "tensura", "view_zoom", "tensura.attribute.view_zoom", 0.0, 0.0, 1024.0, true, Sentiment.NEUTRAL
   );
   public static final Holder<Attribute> DARK_VISION = TensuraAttributeRegister.registerPlayerAttribute(
      "tensura", "dark_vision", "tensura.attribute.dark_vision", 0.0, -1.0, 1.0, true, Sentiment.NEGATIVE
   );
   public static final Holder<Attribute> DODGE_STRENGTH = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "dodge_strength", "tensura.attribute.dodge_strength", 0.2, 0.0, 100.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> DODGE_INVULNERABILITY = TensuraAttributeRegister.registerGenericAttribute(
      "tensura",
      "dodge_invulnerability",
      "tensura.attribute.dodge_invulnerability",
      ((AbilityConfig)ConfigRegistry.getConfig(AbilityConfig.class)).Dodge.baseDodgeInvulnerableTick,
      0.0,
      ((AbilityConfig)ConfigRegistry.getConfig(AbilityConfig.class)).Dodge.maxDodgeInvulnerableTick,
      false,
      Sentiment.POSITIVE
   );
   public static final Holder<Attribute> AUTO_MELEE_DODGE_CHANCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "melee_dodge_chance", "tensura.attribute.melee_dodge_chance", 0.0, 0.0, 100.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> AUTO_PROJECTILE_DODGE_CHANCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "projectile_dodge_chance", "tensura.attribute.projectile_dodge_chance", 0.0, 0.0, 100.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> DODGE_NEGATE_CHANCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "dodge_negate_chance", "tensura.attribute.dodge_negate_chance", 0.0, 0.0, 100.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> CHANT_SPEED = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "chant_speed", "tensura.attribute.chant_speed", 1.0, 0.0, 1024.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> MAGIC_COST_MULTIPLIER = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "magic_cost_multiplier", "tensura.attribute.magic_cost_multiplier", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> MULTILAYER_BARRIER = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "multilayer_barrier", "tensura.attribute.multilayer_barrier", 0.0, 0.0, 1000000.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> WARP_SHOT = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "warp_shot", "tensura.attribute.warp_shot", 0.0, 0.0, 1024.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> HEIGHT_MULTIPLIER = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "height_multiplier", "tensura.attribute.height_multiplier", 1.0, 0.0, 1024.0, true, Sentiment.NEUTRAL
   );
   public static final Holder<Attribute> WIDTH_MULTIPLIER = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "width_multiplier", "tensura.attribute.width_multiplier", 1.0, 0.0, 1024.0, true, Sentiment.NEUTRAL
   );
   public static final Holder<Attribute> WATER_CAPACITY = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "water_capacity", "tensura.attribute.water_capacity", 0.0, 0.0, 1.0E8, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> LAVA_CAPACITY = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "lava_capacity", "tensura.attribute.lava_capacity", 0.0, 0.0, 1.0E9, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> LAW_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "law_degradation", "tensura.attribute.law_degradation", 0.0, 0.0, 1.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> RESISTANCE_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "resistance_degradation", "tensura.attribute.resistance_degradation", 0.0, 0.0, 1.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> PHYSICAL_RESIST_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "physical_resist_degradation", "tensura.attribute.physical_resist_degradation", 0.0, 0.0, 1.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> PHYSICAL_BARRIER = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "physical_barrier", "tensura.attribute.physical_barrier", 0.0, -1024.0, 1024.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> MAGIC_INTERFERENCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "magic_interference", "tensura.attribute.magic_interference", 0.0, -1024.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> MAGIC_RESISTANCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "magic_resistance", "tensura.attribute.magic_resistance", 0.0, -1024.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> MAGIC_BARRIER = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "magic_barrier", "tensura.attribute.magic_barrier", 0.0, -1024.0, 1024.0, true, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> EARTH_BOOST = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "earth_boost", "tensura.attribute.earth_boost", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> EARTH_RESISTANCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "earth_resistance", "tensura.attribute.earth_resistance", 0.0, -1024.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> EARTH_RESIST_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "earth_resist_degradation", "tensura.attribute.earth_resist_degradation", 0.0, 0.0, 1.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> DARKNESS_BOOST = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "darkness_boost", "tensura.attribute.darkness_boost", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> DARKNESS_RESISTANCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "darkness_resistance", "tensura.attribute.darkness_resistance", 0.0, -1024.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> DARKNESS_RESIST_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "darkness_resist_degradation", "tensura.attribute.darkness_resist_degradation", 0.0, 0.0, 1.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> FLAME_BOOST = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "flame_boost", "tensura.attribute.flame_boost", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> FLAME_RESISTANCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "flame_resistance", "tensura.attribute.flame_resistance", 0.0, -1024.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> FLAME_RESIST_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "flame_resist_degradation", "tensura.attribute.flame_resist_degradation", 0.0, 0.0, 1.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> LIGHT_BOOST = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "light_boost", "tensura.attribute.light_boost", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> LIGHT_RESISTANCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "light_resistance", "tensura.attribute.light_resistance", 0.0, -1024.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> LIGHT_RESIST_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "light_resist_degradation", "tensura.attribute.light_resist_degradation", 0.0, 0.0, 1.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> SPACE_BOOST = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "space_boost", "tensura.attribute.space_boost", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> SPACE_RESISTANCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "space_resistance", "tensura.attribute.space_resistance", 0.0, -1024.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> SPACE_RESIST_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "space_resist_degradation", "tensura.attribute.space_resist_degradation", 0.0, 0.0, 1.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> WATER_BOOST = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "water_boost", "tensura.attribute.water_boost", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> WATER_RESISTANCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "water_resistance", "tensura.attribute.water_resistance", 0.0, -1024.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> WATER_RESIST_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "water_resist_degradation", "tensura.attribute.water_resist_degradation", 0.0, 0.0, 1.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> WIND_BOOST = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "wind_boost", "tensura.attribute.wind_boost", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> WIND_RESISTANCE = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "wind_resistance", "tensura.attribute.wind_resistance", 0.0, -1024.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> WIND_RESIST_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "wind_resist_degradation", "tensura.attribute.wind_resist_degradation", 0.0, 0.0, 1.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> GRAVITY_BOOST = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "gravity_boost", "tensura.attribute.gravity_boost", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> GRAVITY_RESIST_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "gravity_resist_degradation", "tensura.attribute.gravity_resist_degradation", 0.0, 0.0, 1.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> LIGHTNING_BOOST = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "lightning_boost", "tensura.attribute.lightning_boost", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> LIGHTNING_RESIST_DEGRADATION = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "lightning_resist_degradation", "tensura.attribute.lightning_resist_degradation", 0.0, 0.0, 1.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> ILLUSION_BOOST = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "illusion_boost", "tensura.attribute.illusion_boost", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );
   public static final Holder<Attribute> SOUND_BOOST = TensuraAttributeRegister.registerGenericAttribute(
      "tensura", "sound_boost", "tensura.attribute.sound_boost", 1.0, 0.0, 1024.0, false, Sentiment.POSITIVE
   );

   public static void init() {
   }
}
