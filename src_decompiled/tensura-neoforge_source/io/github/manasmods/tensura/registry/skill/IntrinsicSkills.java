package io.github.manasmods.tensura.registry.skill;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.ability.skill.intrinsic.AbsorbDissolveSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.BeastTransformationSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.BloodMistSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.BodyArmorSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.DarknessTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.DivineKiReleaseSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.DragonEarSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.DragonEyeSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.DragonModeSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.DragonSkinSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.DrainSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.EarthTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.EyeOfTruthSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.FlameBreathSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.FlameTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.GiantificationSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.IceBreathSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.LightTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.OgreBerserkerSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.ParalysingBreathSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.PoisonousBreathSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.PossessionSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.ScaleArmorSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.SpaceTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.ThunderBreathSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.TitanificationSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.UltrasonicWavesSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.UnpredictabilitySkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.WaterBreathingSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.WaterTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.WindTransformSkill;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class IntrinsicSkills {
   public static final RegistrySupplier<AbsorbDissolveSkill> ABSORB_DISSOLVE = register("absorb_and_dissolve", AbsorbDissolveSkill::new);
   public static final RegistrySupplier<BeastTransformationSkill> BEAST_TRANSFORMATION = register("beast_transformation", BeastTransformationSkill::new);
   public static final RegistrySupplier<BodyArmorSkill> BODY_ARMOR = register("body_armor", BodyArmorSkill::new);
   public static final RegistrySupplier<CharmSkill> CHARM = register("charm", CharmSkill::new);
   public static final RegistrySupplier<DarknessTransformSkill> DARKNESS_TRANSFORM = register("darkness_transform", DarknessTransformSkill::new);
   public static final RegistrySupplier<DivineKiReleaseSkill> DIVINE_KI_RELEASE = register("divine_ki_release", DivineKiReleaseSkill::new);
   public static final RegistrySupplier<BloodMistSkill> BLOOD_MIST = register("blood_mist", BloodMistSkill::new);
   public static final RegistrySupplier<DragonEarSkill> DRAGON_EAR = register("dragon_ear", DragonEarSkill::new);
   public static final RegistrySupplier<DragonEyeSkill> DRAGON_EYE = register("dragon_eye", DragonEyeSkill::new);
   public static final RegistrySupplier<DragonModeSkill> DRAGON_MODE = register("dragon_mode", DragonModeSkill::new);
   public static final RegistrySupplier<DragonSkinSkill> DRAGON_SKIN = register("dragon_skin", DragonSkinSkill::new);
   public static final RegistrySupplier<DrainSkill> DRAIN = register("drain", DrainSkill::new);
   public static final RegistrySupplier<EarthTransformSkill> EARTH_TRANSFORM = register("earth_transform", EarthTransformSkill::new);
   public static final RegistrySupplier<EyeOfTruthSkill> EYE_OF_TRUTH = register("eye_of_truth", EyeOfTruthSkill::new);
   public static final RegistrySupplier<FlameBreathSkill> FLAME_BREATH = register("flame_breath", FlameBreathSkill::new);
   public static final RegistrySupplier<FlameTransformSkill> FLAME_TRANSFORM = register("flame_transform", FlameTransformSkill::new);
   public static final RegistrySupplier<GiantificationSkill> GIANTIFICATION = register("giantification", GiantificationSkill::new);
   public static final RegistrySupplier<IceBreathSkill> ICE_BREATH = register("ice_breath", IceBreathSkill::new);
   public static final RegistrySupplier<LightTransformSkill> LIGHT_TRANSFORM = register("light_transform", LightTransformSkill::new);
   public static final RegistrySupplier<OgreBerserkerSkill> OGRE_BERSERKER = register("ogre_berserker", OgreBerserkerSkill::new);
   public static final RegistrySupplier<ParalysingBreathSkill> PARALYSING_BREATH = register("paralysing_breath", ParalysingBreathSkill::new);
   public static final RegistrySupplier<PoisonousBreathSkill> POISONOUS_BREATH = register("poisonous_breath", PoisonousBreathSkill::new);
   public static final RegistrySupplier<PossessionSkill> POSSESSION = register("possession", PossessionSkill::new);
   public static final RegistrySupplier<ScaleArmorSkill> SCALE_ARMOR = register("scale_armor", ScaleArmorSkill::new);
   public static final RegistrySupplier<SpaceTransformSkill> SPACE_TRANSFORM = register("space_transform", SpaceTransformSkill::new);
   public static final RegistrySupplier<ThunderBreathSkill> THUNDER_BREATH = register("thunder_breath", ThunderBreathSkill::new);
   public static final RegistrySupplier<TitanificationSkill> TITANIFICATION = register("titanification", TitanificationSkill::new);
   public static final RegistrySupplier<UltrasonicWavesSkill> ULTRASONIC_WAVES = register("ultrasonic_waves", UltrasonicWavesSkill::new);
   public static final RegistrySupplier<UnpredictabilitySkill> UNPREDICTABILITY = register("unpredictability", UnpredictabilitySkill::new);
   public static final RegistrySupplier<WaterBreathingSkill> WATER_BREATHING = register("water_breathing", WaterBreathingSkill::new);
   public static final RegistrySupplier<WaterTransformSkill> WATER_TRANSFORM = register("water_transform", WaterTransformSkill::new);
   public static final RegistrySupplier<WindTransformSkill> WIND_TRANSFORM = register("wind_transform", WindTransformSkill::new);

   private static <E extends ManasSkill> RegistrySupplier<E> register(String name, Supplier<E> supplier) {
      return SkillRegistry.SKILLS.register(ResourceLocation.fromNamespaceAndPath("tensura", name), supplier);
   }

   public static void init() {
   }
}
