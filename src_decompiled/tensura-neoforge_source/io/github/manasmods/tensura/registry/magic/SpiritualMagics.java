package io.github.manasmods.tensura.registry.magic;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.ability.magic.spiritual.darkness.DarkCubeMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.darkness.DarknessCannonMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.darkness.DarknessMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.darkness.ShadowBindMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.darkness.TrueDarknessMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.earth.EarthJailMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.earth.EarthMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.earth.EarthSpikesMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.earth.EarthStormMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.earth.MagmaSurgeMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.fire.FireBoltMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.fire.FireBreathMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.fire.FireMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.fire.FlareCircleMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.fire.HellfireMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.light.LightMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.light.SolarBeamMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.light.SolarFlareMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.light.SolarRainMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.light.SolarWaveMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.necromancy.CreateGreaterUndeadMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.necromancy.CreateLesserUndeadMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.necromancy.CurseBindMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.necromancy.CurseMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.space.GateMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.space.ShrinkMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.space.SpaceMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.space.SwipeMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.space.TeleportMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.water.AcidRainMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.water.BlizzardMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.water.MegiddoMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.water.WaterCutterMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.water.WaterMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.wind.AerialBladeMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.wind.ElectroBlastMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.wind.LightningLanceMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.wind.WindBladeMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.wind.WindMagic;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class SpiritualMagics {
   public static final RegistrySupplier<DarknessMagic> DARKNESS = register("darkness", DarknessMagic::new);
   public static final RegistrySupplier<ShadowBindMagic> SHADOW_BIND = register("shadow_bind", ShadowBindMagic::new);
   public static final RegistrySupplier<DarkCubeMagic> DARK_CUBE = register("dark_cube", DarkCubeMagic::new);
   public static final RegistrySupplier<DarknessCannonMagic> DARKNESS_CANNON = register("darkness_cannon", DarknessCannonMagic::new);
   public static final RegistrySupplier<TrueDarknessMagic> TRUE_DARKNESS = register("true_darkness", TrueDarknessMagic::new);
   public static final RegistrySupplier<EarthMagic> EARTH = register("earth", EarthMagic::new);
   public static final RegistrySupplier<EarthSpikesMagic> EARTH_SPIKES = register("earth_spikes", EarthSpikesMagic::new);
   public static final RegistrySupplier<EarthStormMagic> EARTH_STORM = register("earth_storm", EarthStormMagic::new);
   public static final RegistrySupplier<MagmaSurgeMagic> MAGMA_SURGE = register("magma_surge", MagmaSurgeMagic::new);
   public static final RegistrySupplier<EarthJailMagic> EARTH_JAIL = register("earth_jail", EarthJailMagic::new);
   public static final RegistrySupplier<FireMagic> FIRE = register("fire", FireMagic::new);
   public static final RegistrySupplier<FireBreathMagic> FIRE_BREATH = register("fire_breath", FireBreathMagic::new);
   public static final RegistrySupplier<FireBoltMagic> FIRE_BOLT = register("fire_bolt", FireBoltMagic::new);
   public static final RegistrySupplier<FlareCircleMagic> FLARE_CIRCLE = register("flare_circle", FlareCircleMagic::new);
   public static final RegistrySupplier<HellfireMagic> HELLFIRE = register("hellfire", HellfireMagic::new);
   public static final RegistrySupplier<LightMagic> LIGHT = register("light", LightMagic::new);
   public static final RegistrySupplier<SolarBeamMagic> SOLAR_BEAM = register("solar_beam", SolarBeamMagic::new);
   public static final RegistrySupplier<SolarWaveMagic> SOLAR_WAVE = register("solar_wave", SolarWaveMagic::new);
   public static final RegistrySupplier<SolarRainMagic> SOLAR_RAIN = register("solar_rain", SolarRainMagic::new);
   public static final RegistrySupplier<SolarFlareMagic> SOLAR_FLARE = register("solar_flare", SolarFlareMagic::new);
   public static final RegistrySupplier<SpaceMagic> SPACE = register("space", SpaceMagic::new);
   public static final RegistrySupplier<GateMagic> GATE = register("gate", GateMagic::new);
   public static final RegistrySupplier<ShrinkMagic> SHRINK = register("shrink", ShrinkMagic::new);
   public static final RegistrySupplier<TeleportMagic> TELEPORT = register("teleport", TeleportMagic::new);
   public static final RegistrySupplier<SwipeMagic> SWIPE = register("swipe", SwipeMagic::new);
   public static final RegistrySupplier<WaterMagic> WATER = register("water", WaterMagic::new);
   public static final RegistrySupplier<WaterCutterMagic> WATER_CUTTER = register("water_cutter", WaterCutterMagic::new);
   public static final RegistrySupplier<AcidRainMagic> ACID_RAIN = register("acid_rain", AcidRainMagic::new);
   public static final RegistrySupplier<MegiddoMagic> MEGIDDO = register("megiddo", MegiddoMagic::new);
   public static final RegistrySupplier<BlizzardMagic> BLIZZARD = register("blizzard", BlizzardMagic::new);
   public static final RegistrySupplier<WindMagic> WIND = register("wind", WindMagic::new);
   public static final RegistrySupplier<LightningLanceMagic> LIGHTNING_LANCE = register("lightning_lance", LightningLanceMagic::new);
   public static final RegistrySupplier<WindBladeMagic> WIND_BLADE = register("wind_blade", WindBladeMagic::new);
   public static final RegistrySupplier<ElectroBlastMagic> ELECTRO_BLAST = register("electro_blast", ElectroBlastMagic::new);
   public static final RegistrySupplier<AerialBladeMagic> AERIAL_BLADE = register("aerial_blade", AerialBladeMagic::new);
   public static final RegistrySupplier<CreateLesserUndeadMagic> CREATE_LESSER_UNDEAD = register("create_lesser_undead", CreateLesserUndeadMagic::new);
   public static final RegistrySupplier<CreateGreaterUndeadMagic> CREATE_GREATER_UNDEAD = register("create_greater_undead", CreateGreaterUndeadMagic::new);
   public static final RegistrySupplier<CurseMagic> CURSE = register("curse", CurseMagic::new);
   public static final RegistrySupplier<CurseBindMagic> CURSE_BIND = register("curse_bind", CurseBindMagic::new);

   private static <E extends ManasSkill> RegistrySupplier<E> register(String name, Supplier<E> supplier) {
      return SkillRegistry.SKILLS.register(ResourceLocation.fromNamespaceAndPath("tensura", name), supplier);
   }

   public static void init() {
   }
}
