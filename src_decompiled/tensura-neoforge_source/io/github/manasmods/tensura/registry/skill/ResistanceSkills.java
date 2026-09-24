package io.github.manasmods.tensura.registry.skill;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.ability.skill.resist.AbnormalConditionNullification;
import io.github.manasmods.tensura.ability.skill.resist.AbnormalConditionResistance;
import io.github.manasmods.tensura.ability.skill.resist.ColdNullification;
import io.github.manasmods.tensura.ability.skill.resist.ColdResistance;
import io.github.manasmods.tensura.ability.skill.resist.CorrosionNullification;
import io.github.manasmods.tensura.ability.skill.resist.CorrosionResistance;
import io.github.manasmods.tensura.ability.skill.resist.DarknessAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.DarknessAttackResistance;
import io.github.manasmods.tensura.ability.skill.resist.EarthAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.EarthAttackResistance;
import io.github.manasmods.tensura.ability.skill.resist.ElectricityNullification;
import io.github.manasmods.tensura.ability.skill.resist.ElectricityResistance;
import io.github.manasmods.tensura.ability.skill.resist.FireAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.FireAttackResistance;
import io.github.manasmods.tensura.ability.skill.resist.GravityAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.GravityAttackResistance;
import io.github.manasmods.tensura.ability.skill.resist.HeatNullification;
import io.github.manasmods.tensura.ability.skill.resist.HeatResistance;
import io.github.manasmods.tensura.ability.skill.resist.HolyAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.HolyAttackResistance;
import io.github.manasmods.tensura.ability.skill.resist.LightAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.LightAttackResistance;
import io.github.manasmods.tensura.ability.skill.resist.MagicNullification;
import io.github.manasmods.tensura.ability.skill.resist.MagicResistance;
import io.github.manasmods.tensura.ability.skill.resist.NullificationSkill;
import io.github.manasmods.tensura.ability.skill.resist.PainResistanceSkill;
import io.github.manasmods.tensura.ability.skill.resist.ParalysisNullification;
import io.github.manasmods.tensura.ability.skill.resist.ParalysisResistanceSkill;
import io.github.manasmods.tensura.ability.skill.resist.PhysicalAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.PhysicalAttackResistance;
import io.github.manasmods.tensura.ability.skill.resist.PierceNullification;
import io.github.manasmods.tensura.ability.skill.resist.PierceResistance;
import io.github.manasmods.tensura.ability.skill.resist.PoisonNullification;
import io.github.manasmods.tensura.ability.skill.resist.PoisonResistance;
import io.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import io.github.manasmods.tensura.ability.skill.resist.SpatialAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.SpatialAttackResistance;
import io.github.manasmods.tensura.ability.skill.resist.SpiritualAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.SpiritualAttackResistance;
import io.github.manasmods.tensura.ability.skill.resist.ThermalFluctuationNullification;
import io.github.manasmods.tensura.ability.skill.resist.ThermalFluctuationResistance;
import io.github.manasmods.tensura.ability.skill.resist.WaterAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.WaterAttackResistance;
import io.github.manasmods.tensura.ability.skill.resist.WindAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.WindAttackResistance;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class ResistanceSkills {
   public static final RegistrySupplier<AbnormalConditionResistance> ABNORMAL_CONDITION_RESISTANCE = register(
      "abnormal_condition_resistance", AbnormalConditionResistance::new
   );
   public static final RegistrySupplier<ColdResistance> COLD_RESISTANCE = register("cold_resistance", ColdResistance::new);
   public static final RegistrySupplier<CorrosionResistance> CORROSION_RESISTANCE = register("corrosion_resistance", CorrosionResistance::new);
   public static final RegistrySupplier<DarknessAttackResistance> DARKNESS_ATTACK_RESISTANCE = register(
      "darkness_attack_resistance", DarknessAttackResistance::new
   );
   public static final RegistrySupplier<EarthAttackResistance> EARTH_ATTACK_RESISTANCE = register("earth_attack_resistance", EarthAttackResistance::new);
   public static final RegistrySupplier<ElectricityResistance> ELECTRICITY_RESISTANCE = register("electricity_resistance", ElectricityResistance::new);
   public static final RegistrySupplier<FireAttackResistance> FLAME_ATTACK_RESISTANCE = register("flame_attack_resistance", FireAttackResistance::new);
   public static final RegistrySupplier<GravityAttackResistance> GRAVITY_ATTACK_RESISTANCE = register("gravity_attack_resistance", GravityAttackResistance::new);
   public static final RegistrySupplier<HeatResistance> HEAT_RESISTANCE = register("heat_resistance", HeatResistance::new);
   public static final RegistrySupplier<HolyAttackResistance> HOLY_ATTACK_RESISTANCE = register("holy_attack_resistance", HolyAttackResistance::new);
   public static final RegistrySupplier<LightAttackResistance> LIGHT_ATTACK_RESISTANCE = register("light_attack_resistance", LightAttackResistance::new);
   public static final RegistrySupplier<MagicResistance> MAGIC_RESISTANCE = register("magic_resistance", MagicResistance::new);
   public static final RegistrySupplier<PhysicalAttackResistance> PHYSICAL_ATTACK_RESISTANCE = register(
      "physical_attack_resistance", PhysicalAttackResistance::new
   );
   public static final RegistrySupplier<ResistSkill> PAIN_RESISTANCE = register("pain_resistance", PainResistanceSkill::new);
   public static final RegistrySupplier<ResistSkill> PARALYSIS_RESISTANCE = register("paralysis_resistance", ParalysisResistanceSkill::new);
   public static final RegistrySupplier<PierceResistance> PIERCE_RESISTANCE = register("pierce_resistance", PierceResistance::new);
   public static final RegistrySupplier<PoisonResistance> POISON_RESISTANCE = register("poison_resistance", PoisonResistance::new);
   public static final RegistrySupplier<SpatialAttackResistance> SPATIAL_ATTACK_RESISTANCE = register("spatial_attack_resistance", SpatialAttackResistance::new);
   public static final RegistrySupplier<SpiritualAttackResistance> SPIRITUAL_ATTACK_RESISTANCE = register(
      "spiritual_attack_resistance", SpiritualAttackResistance::new
   );
   public static final RegistrySupplier<ThermalFluctuationResistance> THERMAL_FLUCTUATION_RESISTANCE = register(
      "thermal_fluctuation_resistance", ThermalFluctuationResistance::new
   );
   public static final RegistrySupplier<WaterAttackResistance> WATER_ATTACK_RESISTANCE = register("water_attack_resistance", WaterAttackResistance::new);
   public static final RegistrySupplier<WindAttackResistance> WIND_ATTACK_RESISTANCE = register("wind_attack_resistance", WindAttackResistance::new);
   public static final RegistrySupplier<AbnormalConditionNullification> ABNORMAL_CONDITION_NULLIFICATION = register(
      "abnormal_condition_nullification", AbnormalConditionNullification::new
   );
   public static final RegistrySupplier<ColdNullification> COLD_NULLIFICATION = register("cold_nullification", ColdNullification::new);
   public static final RegistrySupplier<CorrosionNullification> CORROSION_NULLIFICATION = register("corrosion_nullification", CorrosionNullification::new);
   public static final RegistrySupplier<DarknessAttackNullification> DARKNESS_ATTACK_NULLIFICATION = register(
      "darkness_attack_nullification", DarknessAttackNullification::new
   );
   public static final RegistrySupplier<EarthAttackNullification> EARTH_ATTACK_NULLIFICATION = register(
      "earth_attack_nullification", EarthAttackNullification::new
   );
   public static final RegistrySupplier<ElectricityNullification> ELECTRICITY_NULLIFICATION = register(
      "electricity_nullification", ElectricityNullification::new
   );
   public static final RegistrySupplier<FireAttackNullification> FLAME_ATTACK_NULLIFICATION = register(
      "flame_attack_nullification", FireAttackNullification::new
   );
   public static final RegistrySupplier<GravityAttackNullification> GRAVITY_ATTACK_NULLIFICATION = register(
      "gravity_attack_nullification", GravityAttackNullification::new
   );
   public static final RegistrySupplier<HeatNullification> HEAT_NULLIFICATION = register("heat_nullification", HeatNullification::new);
   public static final RegistrySupplier<HolyAttackNullification> HOLY_ATTACK_NULLIFICATION = register("holy_attack_nullification", HolyAttackNullification::new);
   public static final RegistrySupplier<LightAttackNullification> LIGHT_ATTACK_NULLIFICATION = register(
      "light_attack_nullification", LightAttackNullification::new
   );
   public static final RegistrySupplier<MagicNullification> MAGIC_NULLIFICATION = register("magic_nullification", MagicNullification::new);
   public static final RegistrySupplier<ResistSkill> PAIN_NULLIFICATION = register("pain_nullification", NullificationSkill::new);
   public static final RegistrySupplier<ParalysisNullification> PARALYSIS_NULLIFICATION = register("paralysis_nullification", ParalysisNullification::new);
   public static final RegistrySupplier<PierceNullification> PIERCE_NULLIFICATION = register("pierce_nullification", PierceNullification::new);
   public static final RegistrySupplier<PhysicalAttackNullification> PHYSICAL_ATTACK_NULLIFICATION = register(
      "physical_attack_nullification", PhysicalAttackNullification::new
   );
   public static final RegistrySupplier<PoisonNullification> POISON_NULLIFICATION = register("poison_nullification", PoisonNullification::new);
   public static final RegistrySupplier<SpatialAttackNullification> SPATIAL_ATTACK_NULLIFICATION = register(
      "spatial_attack_nullification", SpatialAttackNullification::new
   );
   public static final RegistrySupplier<SpiritualAttackNullification> SPIRITUAL_ATTACK_NULLIFICATION = register(
      "spiritual_attack_nullification", SpiritualAttackNullification::new
   );
   public static final RegistrySupplier<ThermalFluctuationNullification> THERMAL_FLUCTUATION_NULLIFICATION = register(
      "thermal_fluctuation_nullification", ThermalFluctuationNullification::new
   );
   public static final RegistrySupplier<WaterAttackNullification> WATER_ATTACK_NULLIFICATION = register(
      "water_attack_nullification", WaterAttackNullification::new
   );
   public static final RegistrySupplier<WindAttackNullification> WIND_ATTACK_NULLIFICATION = register("wind_attack_nullification", WindAttackNullification::new);

   private static <E extends ManasSkill> RegistrySupplier<E> register(String name, Supplier<E> supplier) {
      return SkillRegistry.SKILLS.register(ResourceLocation.fromNamespaceAndPath("tensura", name), supplier);
   }

   public static void init() {
   }
}
