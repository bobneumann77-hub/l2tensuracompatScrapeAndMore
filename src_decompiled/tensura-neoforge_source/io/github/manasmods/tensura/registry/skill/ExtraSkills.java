package io.github.manasmods.tensura.registry.skill;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.ability.skill.extra.AllSeeingEyeSkill;
import io.github.manasmods.tensura.ability.skill.extra.AnalyticalAppraisalSkill;
import io.github.manasmods.tensura.ability.skill.extra.BlackFlameSkill;
import io.github.manasmods.tensura.ability.skill.extra.BlackLightningSkill;
import io.github.manasmods.tensura.ability.skill.extra.BodyDoubleSkill;
import io.github.manasmods.tensura.ability.skill.extra.ChantAnnulmentSkill;
import io.github.manasmods.tensura.ability.skill.extra.DangerSenseSkill;
import io.github.manasmods.tensura.ability.skill.extra.DemonLordHakiSkill;
import io.github.manasmods.tensura.ability.skill.extra.EarthDominationSkill;
import io.github.manasmods.tensura.ability.skill.extra.EarthManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.FlameDominationSkill;
import io.github.manasmods.tensura.ability.skill.extra.FlameManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.GodwolfSenseSkill;
import io.github.manasmods.tensura.ability.skill.extra.GravityDominationSkill;
import io.github.manasmods.tensura.ability.skill.extra.GravityManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import io.github.manasmods.tensura.ability.skill.extra.HeatWaveSkill;
import io.github.manasmods.tensura.ability.skill.extra.HeavenlyEyeSkill;
import io.github.manasmods.tensura.ability.skill.extra.HeroHakiSkill;
import io.github.manasmods.tensura.ability.skill.extra.InfiniteRegenerationSkill;
import io.github.manasmods.tensura.ability.skill.extra.LawManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.LightningDominationSkill;
import io.github.manasmods.tensura.ability.skill.extra.LightningManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.MagicAuraSkill;
import io.github.manasmods.tensura.ability.skill.extra.MagicDarknessTransformSkill;
import io.github.manasmods.tensura.ability.skill.extra.MagicEarthTransformSkill;
import io.github.manasmods.tensura.ability.skill.extra.MagicFlameTransformSkill;
import io.github.manasmods.tensura.ability.skill.extra.MagicJammingSkill;
import io.github.manasmods.tensura.ability.skill.extra.MagicLightTransformSkill;
import io.github.manasmods.tensura.ability.skill.extra.MagicSenseSkill;
import io.github.manasmods.tensura.ability.skill.extra.MagicSpaceTransformSkill;
import io.github.manasmods.tensura.ability.skill.extra.MagicWaterTransformSkill;
import io.github.manasmods.tensura.ability.skill.extra.MagicWindTransformSkill;
import io.github.manasmods.tensura.ability.skill.extra.MajestySkill;
import io.github.manasmods.tensura.ability.skill.extra.ManaManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.MolecularManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.MortalFearSkill;
import io.github.manasmods.tensura.ability.skill.extra.MultilayerBarrierSkill;
import io.github.manasmods.tensura.ability.skill.extra.SacredHakiSkill;
import io.github.manasmods.tensura.ability.skill.extra.SageSkill;
import io.github.manasmods.tensura.ability.skill.extra.SenseHeatSourceSkill;
import io.github.manasmods.tensura.ability.skill.extra.SenseSoundwaveSkill;
import io.github.manasmods.tensura.ability.skill.extra.ShadowMotionSkill;
import io.github.manasmods.tensura.ability.skill.extra.SnakeEyeSkill;
import io.github.manasmods.tensura.ability.skill.extra.SoundDominationSkill;
import io.github.manasmods.tensura.ability.skill.extra.SoundManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.SpatialDominationSkill;
import io.github.manasmods.tensura.ability.skill.extra.SpatialManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.SpatialMotionSkill;
import io.github.manasmods.tensura.ability.skill.extra.SteelStrengthSkill;
import io.github.manasmods.tensura.ability.skill.extra.StickySteelThreadSkill;
import io.github.manasmods.tensura.ability.skill.extra.StrengthenBodySkill;
import io.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import io.github.manasmods.tensura.ability.skill.extra.UltraInstinctSkill;
import io.github.manasmods.tensura.ability.skill.extra.UltraspeedRegenerationSkill;
import io.github.manasmods.tensura.ability.skill.extra.UniversalPerceptionSkill;
import io.github.manasmods.tensura.ability.skill.extra.WaterDominationSkill;
import io.github.manasmods.tensura.ability.skill.extra.WaterManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.WeatherDominationSkill;
import io.github.manasmods.tensura.ability.skill.extra.WeatherManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.WindDominationSkill;
import io.github.manasmods.tensura.ability.skill.extra.WindManipulationSkill;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class ExtraSkills {
   public static final RegistrySupplier<AllSeeingEyeSkill> ALL_SEEING_EYE = register("all_seeing_eye", AllSeeingEyeSkill::new);
   public static final RegistrySupplier<AnalyticalAppraisalSkill> ANALYTICAL_APPRAISAL = register("analytical_appraisal", AnalyticalAppraisalSkill::new);
   public static final RegistrySupplier<BlackFlameSkill> BLACK_FLAME = register("black_flame", BlackFlameSkill::new);
   public static final RegistrySupplier<BlackLightningSkill> BLACK_LIGHTNING = register("black_lightning", BlackLightningSkill::new);
   public static final RegistrySupplier<BodyDoubleSkill> BODY_DOUBLE = register("body_double", BodyDoubleSkill::new);
   public static final RegistrySupplier<ChantAnnulmentSkill> CHANT_ANNULMENT = register("chant_annulment", ChantAnnulmentSkill::new);
   public static final RegistrySupplier<DangerSenseSkill> DANGER_SENSE = register("danger_sense", DangerSenseSkill::new);
   public static final RegistrySupplier<DemonLordHakiSkill> DEMON_LORD_HAKI = register("demon_lord_haki", DemonLordHakiSkill::new);
   public static final RegistrySupplier<EarthDominationSkill> EARTH_DOMINATION = register("earth_domination", EarthDominationSkill::new);
   public static final RegistrySupplier<EarthManipulationSkill> EARTH_MANIPULATION = register("earth_manipulation", EarthManipulationSkill::new);
   public static final RegistrySupplier<FlameDominationSkill> FLAME_DOMINATION = register("flame_domination", FlameDominationSkill::new);
   public static final RegistrySupplier<FlameManipulationSkill> FLAME_MANIPULATION = register("flame_manipulation", FlameManipulationSkill::new);
   public static final RegistrySupplier<GodwolfSenseSkill> GODWOLF_SENSE = register("godwolf_sense", GodwolfSenseSkill::new);
   public static final RegistrySupplier<GravityDominationSkill> GRAVITY_DOMINATION = register("gravity_domination", GravityDominationSkill::new);
   public static final RegistrySupplier<GravityManipulationSkill> GRAVITY_MANIPULATION = register("gravity_manipulation", GravityManipulationSkill::new);
   public static final RegistrySupplier<HakiSkill> HAKI = register("haki", HakiSkill::new);
   public static final RegistrySupplier<HeatWaveSkill> HEAT_WAVE = register("heat_wave", HeatWaveSkill::new);
   public static final RegistrySupplier<HeavenlyEyeSkill> HEAVENLY_EYE = register("heavenly_eye", HeavenlyEyeSkill::new);
   public static final RegistrySupplier<HeroHakiSkill> HERO_HAKI = register("hero_haki", HeroHakiSkill::new);
   public static final RegistrySupplier<InfiniteRegenerationSkill> INFINITE_REGENERATION = register("infinite_regeneration", InfiniteRegenerationSkill::new);
   public static final RegistrySupplier<LawManipulationSkill> LAW_MANIPULATION = register("law_manipulation", LawManipulationSkill::new);
   public static final RegistrySupplier<LightningDominationSkill> LIGHTNING_DOMINATION = register("lightning_domination", LightningDominationSkill::new);
   public static final RegistrySupplier<LightningManipulationSkill> LIGHTNING_MANIPULATION = register("lightning_manipulation", LightningManipulationSkill::new);
   public static final RegistrySupplier<MagicAuraSkill> MAGIC_AURA = register("magic_aura", MagicAuraSkill::new);
   public static final RegistrySupplier<MagicDarknessTransformSkill> MAGIC_DARKNESS_TRANSFORM = register(
      "magic_darkness_transform", MagicDarknessTransformSkill::new
   );
   public static final RegistrySupplier<MagicEarthTransformSkill> MAGIC_EARTH_TRANSFORM = register("magic_earth_transform", MagicEarthTransformSkill::new);
   public static final RegistrySupplier<MagicFlameTransformSkill> MAGIC_FLAME_TRANSFORM = register("magic_flame_transform", MagicFlameTransformSkill::new);
   public static final RegistrySupplier<MagicJammingSkill> MAGIC_JAMMING = register("magic_jamming", MagicJammingSkill::new);
   public static final RegistrySupplier<MagicLightTransformSkill> MAGIC_LIGHT_TRANSFORM = register("magic_light_transform", MagicLightTransformSkill::new);
   public static final RegistrySupplier<MagicSenseSkill> MAGIC_SENSE = register("magic_sense", MagicSenseSkill::new);
   public static final RegistrySupplier<MagicSpaceTransformSkill> MAGIC_SPACE_TRANSFORM = register("magic_space_transform", MagicSpaceTransformSkill::new);
   public static final RegistrySupplier<MagicWaterTransformSkill> MAGIC_WATER_TRANSFORM = register("magic_water_transform", MagicWaterTransformSkill::new);
   public static final RegistrySupplier<MagicWindTransformSkill> MAGIC_WIND_TRANSFORM = register("magic_wind_transform", MagicWindTransformSkill::new);
   public static final RegistrySupplier<MajestySkill> MAJESTY = register("majesty", MajestySkill::new);
   public static final RegistrySupplier<ManaManipulationSkill> MANA_MANIPULATION = register("mana_manipulation", ManaManipulationSkill::new);
   public static final RegistrySupplier<MolecularManipulationSkill> MOLECULAR_MANIPULATION = register("molecular_manipulation", MolecularManipulationSkill::new);
   public static final RegistrySupplier<MortalFearSkill> MORTAL_FEAR = register("mortal_fear", MortalFearSkill::new);
   public static final RegistrySupplier<MultilayerBarrierSkill> MULTILAYER_BARRIER = register("multilayer_barrier", MultilayerBarrierSkill::new);
   public static final RegistrySupplier<SacredHakiSkill> SACRED_HAKI = register("sacred_haki", SacredHakiSkill::new);
   public static final RegistrySupplier<SageSkill> SAGE = register("sage", SageSkill::new);
   public static final RegistrySupplier<SenseHeatSourceSkill> SENSE_HEAT_SOURCE = register("sense_heat_source", SenseHeatSourceSkill::new);
   public static final RegistrySupplier<SenseSoundwaveSkill> SENSE_SOUNDWAVE = register("sense_soundwave", SenseSoundwaveSkill::new);
   public static final RegistrySupplier<ShadowMotionSkill> SHADOW_MOTION = register("shadow_motion", ShadowMotionSkill::new);
   public static final RegistrySupplier<SnakeEyeSkill> SNAKE_EYE = register("snake_eye", SnakeEyeSkill::new);
   public static final RegistrySupplier<SoundDominationSkill> SOUND_DOMINATION = register("sound_domination", SoundDominationSkill::new);
   public static final RegistrySupplier<SoundManipulationSkill> SOUND_MANIPULATION = register("sound_manipulation", SoundManipulationSkill::new);
   public static final RegistrySupplier<SpatialDominationSkill> SPATIAL_DOMINATION = register("spatial_domination", SpatialDominationSkill::new);
   public static final RegistrySupplier<SpatialManipulationSkill> SPATIAL_MANIPULATION = register("spatial_manipulation", SpatialManipulationSkill::new);
   public static final RegistrySupplier<SpatialMotionSkill> SPATIAL_MOTION = register("spatial_motion", SpatialMotionSkill::new);
   public static final RegistrySupplier<StickySteelThreadSkill> STICKY_STEEL_THREAD = register("sticky_steel_thread", StickySteelThreadSkill::new);
   public static final RegistrySupplier<SteelStrengthSkill> STEEL_STRENGTH = register("steel_strength", SteelStrengthSkill::new);
   public static final RegistrySupplier<StrengthenBodySkill> STRENGTHEN_BODY = register("strengthen_body", StrengthenBodySkill::new);
   public static final RegistrySupplier<ThoughtAccelerationSkill> THOUGHT_ACCELERATION = register("thought_acceleration", ThoughtAccelerationSkill::new);
   public static final RegistrySupplier<UltraInstinctSkill> ULTRA_INSTINCT = register("ultra_instinct", UltraInstinctSkill::new);
   public static final RegistrySupplier<UltraspeedRegenerationSkill> ULTRASPEED_REGENERATION = register(
      "ultraspeed_regeneration", UltraspeedRegenerationSkill::new
   );
   public static final RegistrySupplier<UniversalPerceptionSkill> UNIVERSAL_PERCEPTION = register("universal_perception", UniversalPerceptionSkill::new);
   public static final RegistrySupplier<WaterDominationSkill> WATER_DOMINATION = register("water_domination", WaterDominationSkill::new);
   public static final RegistrySupplier<WaterManipulationSkill> WATER_MANIPULATION = register("water_manipulation", WaterManipulationSkill::new);
   public static final RegistrySupplier<WindDominationSkill> WIND_DOMINATION = register("wind_domination", WindDominationSkill::new);
   public static final RegistrySupplier<WindManipulationSkill> WIND_MANIPULATION = register("wind_manipulation", WindManipulationSkill::new);
   public static final RegistrySupplier<WeatherDominationSkill> WEATHER_DOMINATION = register("weather_domination", WeatherDominationSkill::new);
   public static final RegistrySupplier<WeatherManipulationSkill> WEATHER_MANIPULATION = register("weather_manipulation", WeatherManipulationSkill::new);

   private static <E extends ManasSkill> RegistrySupplier<E> register(String name, Supplier<E> supplier) {
      return SkillRegistry.SKILLS.register(ResourceLocation.fromNamespaceAndPath("tensura", name), supplier);
   }

   public static void init() {
   }
}
