package io.github.manasmods.tensura.registry.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.entity.magic.ExplosionCircle;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.AcidRainEntity;
import io.github.manasmods.tensura.entity.magic.barrier.AirJailEntity;
import io.github.manasmods.tensura.entity.magic.barrier.AntiMagicAreaEntity;
import io.github.manasmods.tensura.entity.magic.barrier.AntiShockAreaEntity;
import io.github.manasmods.tensura.entity.magic.barrier.BlizzardEntity;
import io.github.manasmods.tensura.entity.magic.barrier.BossBarrierEntity;
import io.github.manasmods.tensura.entity.magic.barrier.DarkCubeEntity;
import io.github.manasmods.tensura.entity.magic.barrier.DisintegrationEntity;
import io.github.manasmods.tensura.entity.magic.barrier.EarthStormEntity;
import io.github.manasmods.tensura.entity.magic.barrier.FireJailEntity;
import io.github.manasmods.tensura.entity.magic.barrier.FlareCircleEntity;
import io.github.manasmods.tensura.entity.magic.barrier.HealingRainEntity;
import io.github.manasmods.tensura.entity.magic.barrier.HeatStormEntity;
import io.github.manasmods.tensura.entity.magic.barrier.HolyFieldEntity;
import io.github.manasmods.tensura.entity.magic.barrier.MegiddoBubbleEntity;
import io.github.manasmods.tensura.entity.magic.barrier.RangedBarrierEntity;
import io.github.manasmods.tensura.entity.magic.barrier.ThunderRainEntity;
import io.github.manasmods.tensura.entity.magic.barrier.WaterJailEntity;
import io.github.manasmods.tensura.entity.magic.beam.BlackLightningBlastProjectile;
import io.github.manasmods.tensura.entity.magic.beam.BloodRayProjectile;
import io.github.manasmods.tensura.entity.magic.beam.DarknessCannonProjectile;
import io.github.manasmods.tensura.entity.magic.beam.ElectroBlastProjectile;
import io.github.manasmods.tensura.entity.magic.beam.GluttonyMistProjectile;
import io.github.manasmods.tensura.entity.magic.beam.PredatorMistProjectile;
import io.github.manasmods.tensura.entity.magic.beam.SolarBeamProjectile;
import io.github.manasmods.tensura.entity.magic.beam.SpatialRayProjectile;
import io.github.manasmods.tensura.entity.magic.breath.BlackFlameBreathProjectile;
import io.github.manasmods.tensura.entity.magic.breath.FlameBreathProjectile;
import io.github.manasmods.tensura.entity.magic.breath.IceBreathProjectile;
import io.github.manasmods.tensura.entity.magic.breath.ParalysingBreathProjectile;
import io.github.manasmods.tensura.entity.magic.breath.PoisonousBreathProjectile;
import io.github.manasmods.tensura.entity.magic.breath.ThunderBreathProjectile;
import io.github.manasmods.tensura.entity.magic.breath.WaterBreathProjectile;
import io.github.manasmods.tensura.entity.magic.breath.WindBreathProjectile;
import io.github.manasmods.tensura.entity.magic.field.CurseBindHands;
import io.github.manasmods.tensura.entity.magic.field.DeathBlessingField;
import io.github.manasmods.tensura.entity.magic.field.GravityField;
import io.github.manasmods.tensura.entity.magic.field.HellFlare;
import io.github.manasmods.tensura.entity.magic.field.Hellfire;
import io.github.manasmods.tensura.entity.magic.field.MagicExplosion;
import io.github.manasmods.tensura.entity.magic.field.MarionetteLines;
import io.github.manasmods.tensura.entity.magic.field.MudHands;
import io.github.manasmods.tensura.entity.magic.field.ShadowBindHands;
import io.github.manasmods.tensura.entity.magic.field.beam.SummoningBeam;
import io.github.manasmods.tensura.entity.magic.field.cloud.BloodMistCloud;
import io.github.manasmods.tensura.entity.magic.field.cloud.FireStormCloud;
import io.github.manasmods.tensura.entity.magic.field.cloud.MiasmicMistCloud;
import io.github.manasmods.tensura.entity.magic.field.cloud.SleepMistCloud;
import io.github.manasmods.tensura.entity.magic.field.haki.HakiField;
import io.github.manasmods.tensura.entity.magic.field.haki.SacredHakiField;
import io.github.manasmods.tensura.entity.magic.lightning.BlackLightningBolt;
import io.github.manasmods.tensura.entity.magic.lightning.LightningBolt;
import io.github.manasmods.tensura.entity.magic.misc.DeathTornadoEntity;
import io.github.manasmods.tensura.entity.magic.misc.HazyBlossomEntity;
import io.github.manasmods.tensura.entity.magic.misc.MadOrbsEntity;
import io.github.manasmods.tensura.entity.magic.misc.MagicLandmineEntity;
import io.github.manasmods.tensura.entity.magic.misc.NonPlayerFishingHook;
import io.github.manasmods.tensura.entity.magic.misc.PrimedCharybdisCoreEntity;
import io.github.manasmods.tensura.entity.magic.misc.TensuraFallingBlock;
import io.github.manasmods.tensura.entity.magic.misc.WarpPortalEntity;
import io.github.manasmods.tensura.entity.magic.shield.MagicShieldEntity;
import io.github.manasmods.tensura.entity.magic.shield.ShieldEntity;
import io.github.manasmods.tensura.entity.magic.spike.DripstoneSpikeEntity;
import io.github.manasmods.tensura.entity.magic.spike.FirePillarEntity;
import io.github.manasmods.tensura.entity.magic.spike.IcePillarEntity;
import io.github.manasmods.tensura.entity.magic.spike.IcicleSpikeEntity;
import io.github.manasmods.tensura.entity.magic.spike.MudSpikeEntity;
import io.github.manasmods.tensura.entity.magic.spike.PillarEntity;
import io.github.manasmods.tensura.entity.template.TensuraBoatEntity;
import io.github.manasmods.tensura.entity.template.TensuraChestBoatEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;

public class MiscEntityTypes {
   private static final DeferredRegister<EntityType<?>> MISC = DeferredRegister.create("tensura", Registries.ENTITY_TYPE);
   public static final RegistrySupplier<EntityType<TensuraBoatEntity>> BOAT_ENTITY = MISC.register(
      "tensura_boat",
      () -> Builder.of(TensuraBoatEntity::new, MobCategory.MISC).sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10).build("tensura_boat")
   );
   public static final RegistrySupplier<EntityType<TensuraChestBoatEntity>> CHEST_BOAT_ENTITY = MISC.register(
      "tensura_chest_boat",
      () -> Builder.of(TensuraChestBoatEntity::new, MobCategory.MISC)
         .sized(1.375F, 0.5625F)
         .eyeHeight(0.5625F)
         .clientTrackingRange(10)
         .build("tensura_chest_boat")
   );
   public static final RegistrySupplier<EntityType<NonPlayerFishingHook>> FISHING_HOOK = MISC.register(
      "fishing_hook",
      () -> Builder.of(NonPlayerFishingHook::new, MobCategory.MISC)
         .noSave()
         .noSummon()
         .sized(0.25F, 0.25F)
         .clientTrackingRange(4)
         .updateInterval(5)
         .build("fishing_hook")
   );
   public static final RegistrySupplier<EntityType<MagicCircle>> MAGIC_CIRCLE = MISC.register(
      "magic_circle",
      () -> Builder.of(MagicCircle::new, MobCategory.MISC)
         .sized(1.0F, 0.1F)
         .clientTrackingRange(64)
         .noSummon()
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "magic_circle").toString())
   );
   public static final RegistrySupplier<EntityType<ExplosionCircle>> EXPLOSION_CIRCLE = MISC.register(
      "explosion_circle",
      () -> Builder.of(ExplosionCircle::new, MobCategory.MISC)
         .sized(1.0F, 0.1F)
         .clientTrackingRange(128)
         .noSave()
         .noSummon()
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "explosion_circle").toString())
   );
   public static final RegistrySupplier<EntityType<AcidRainEntity>> ACID_RAIN = MISC.register(
      "acid_rain",
      () -> Builder.of(AcidRainEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "acid_rain").toString())
   );
   public static final RegistrySupplier<EntityType<AntiMagicAreaEntity>> ANTI_MAGIC_AREA = MISC.register(
      "anti_magic_area",
      () -> Builder.of(AntiMagicAreaEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "anti_magic_area").toString())
   );
   public static final RegistrySupplier<EntityType<AntiShockAreaEntity>> ANTI_SHOCK_AREA = MISC.register(
      "anti_shock_area",
      () -> Builder.of(AntiShockAreaEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "anti_shock_area").toString())
   );
   public static final RegistrySupplier<EntityType<AirJailEntity>> AIR_JAIL = MISC.register(
      "air_jail",
      () -> Builder.of(AirJailEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "air_jail").toString())
   );
   public static final RegistrySupplier<EntityType<BlizzardEntity>> BLIZZARD = MISC.register(
      "blizzard",
      () -> Builder.of(BlizzardEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "blizzard").toString())
   );
   public static final RegistrySupplier<EntityType<BossBarrierEntity>> BOSS_BARRIER = MISC.register(
      "boss_barrier",
      () -> Builder.of(BossBarrierEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(32)
         .noSave()
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "boss_barrier").toString())
   );
   public static final RegistrySupplier<EntityType<DarkCubeEntity>> DARK_CUBE = MISC.register(
      "dark_cube",
      () -> Builder.of(DarkCubeEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "dark_cube").toString())
   );
   public static final RegistrySupplier<EntityType<DisintegrationEntity>> DISINTEGRATION = MISC.register(
      "disintegration",
      () -> Builder.of(DisintegrationEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "disintegration").toString())
   );
   public static final RegistrySupplier<EntityType<EarthStormEntity>> EARTH_STORM = MISC.register(
      "earth_storm",
      () -> Builder.of(EarthStormEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "earth_storm").toString())
   );
   public static final RegistrySupplier<EntityType<FireJailEntity>> FIRE_JAIL = MISC.register(
      "fire_jail",
      () -> Builder.of(FireJailEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "fire_jail").toString())
   );
   public static final RegistrySupplier<EntityType<FlareCircleEntity>> FLARE_CIRCLE = MISC.register(
      "flare_circle",
      () -> Builder.of(FlareCircleEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "flare_circle").toString())
   );
   public static final RegistrySupplier<EntityType<HealingRainEntity>> HEALING_RAIN = MISC.register(
      "healing_rain",
      () -> Builder.of(HealingRainEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "healing_rain").toString())
   );
   public static final RegistrySupplier<EntityType<HeatStormEntity>> HEAT_STORM = MISC.register(
      "heat_storm",
      () -> Builder.of(HeatStormEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "heat_storm").toString())
   );
   public static final RegistrySupplier<EntityType<HolyFieldEntity>> HOLY_FIELD = MISC.register(
      "holy_field",
      () -> Builder.of(HolyFieldEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "holy_field").toString())
   );
   public static final RegistrySupplier<EntityType<MegiddoBubbleEntity>> MEGIDDO_BUBBLE = MISC.register(
      "megiddo_bubble",
      () -> Builder.of(MegiddoBubbleEntity::new, MobCategory.MISC)
         .sized(1.8F, 0.9F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "megiddo_bubble").toString())
   );
   public static final RegistrySupplier<EntityType<RangedBarrierEntity>> RANGED_BARRIER = MISC.register(
      "ranged_barrier",
      () -> Builder.of(RangedBarrierEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "ranged_barrier").toString())
   );
   public static final RegistrySupplier<EntityType<ThunderRainEntity>> THUNDER_RAIN = MISC.register(
      "thunder_rain",
      () -> Builder.of(ThunderRainEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "thunder_rain").toString())
   );
   public static final RegistrySupplier<EntityType<WaterJailEntity>> WATER_JAIL = MISC.register(
      "water_jail",
      () -> Builder.of(WaterJailEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "water_jail").toString())
   );
   public static final RegistrySupplier<EntityType<BlackLightningBlastProjectile>> BLACK_LIGHTNING_BLAST = MISC.register(
      "black_lightning_blast",
      () -> Builder.of(BlackLightningBlastProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "black_lightning_blast").toString())
   );
   public static final RegistrySupplier<EntityType<BloodRayProjectile>> BLOOD_RAY = MISC.register(
      "blood_ray",
      () -> Builder.of(BloodRayProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "blood_ray").toString())
   );
   public static final RegistrySupplier<EntityType<DarknessCannonProjectile>> DARKNESS_CANNON = MISC.register(
      "darkness_cannon",
      () -> Builder.of(DarknessCannonProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "darkness_cannon").toString())
   );
   public static final RegistrySupplier<EntityType<ElectroBlastProjectile>> ELECTRO_BLAST = MISC.register(
      "electro_blast",
      () -> Builder.of(ElectroBlastProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "electro_blast").toString())
   );
   public static final RegistrySupplier<EntityType<SolarBeamProjectile>> SOLAR_BEAM = MISC.register(
      "solar_beam",
      () -> Builder.of(SolarBeamProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "solar_beam").toString())
   );
   public static final RegistrySupplier<EntityType<SpatialRayProjectile>> SPATIAL_RAY = MISC.register(
      "spatial_ray",
      () -> Builder.of(SpatialRayProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "spatial_ray").toString())
   );
   public static final RegistrySupplier<EntityType<PredatorMistProjectile>> PREDATOR_MIST = MISC.register(
      "predator_mist",
      () -> Builder.of(PredatorMistProjectile::new, MobCategory.MISC)
         .sized(15.0F, 15.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "predator_mist").toString())
   );
   public static final RegistrySupplier<EntityType<PredatorMistProjectile>> GOURMET_MIST = MISC.register(
      "gourmet_mist",
      () -> Builder.of(PredatorMistProjectile::new, MobCategory.MISC)
         .sized(15.0F, 15.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "gourmet_mist").toString())
   );
   public static final RegistrySupplier<EntityType<GluttonyMistProjectile>> GLUTTONY_MIST = MISC.register(
      "gluttony_mist",
      () -> Builder.of(GluttonyMistProjectile::new, MobCategory.MISC)
         .sized(15.0F, 15.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "gluttony_mist").toString())
   );
   public static final RegistrySupplier<EntityType<BlackFlameBreathProjectile>> BLACK_FLAME_BREATH = MISC.register(
      "black_flame_breath",
      () -> Builder.of(BlackFlameBreathProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "black_flame_breath").toString())
   );
   public static final RegistrySupplier<EntityType<FlameBreathProjectile>> FLAME_BREATH = MISC.register(
      "flame_breath",
      () -> Builder.of(FlameBreathProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "flame_breath").toString())
   );
   public static final RegistrySupplier<EntityType<IceBreathProjectile>> ICE_BREATH = MISC.register(
      "ice_breath",
      () -> Builder.of(IceBreathProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "ice_breath").toString())
   );
   public static final RegistrySupplier<EntityType<ParalysingBreathProjectile>> PARALYSING_BREATH = MISC.register(
      "paralysing_breath",
      () -> Builder.of(ParalysingBreathProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "paralysing_breath").toString())
   );
   public static final RegistrySupplier<EntityType<PoisonousBreathProjectile>> POISONOUS_BREATH = MISC.register(
      "poisonous_breath",
      () -> Builder.of(PoisonousBreathProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "poisonous_breath").toString())
   );
   public static final RegistrySupplier<EntityType<ThunderBreathProjectile>> THUNDER_BREATH = MISC.register(
      "thunder_breath",
      () -> Builder.of(ThunderBreathProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "thunder_breath").toString())
   );
   public static final RegistrySupplier<EntityType<WaterBreathProjectile>> WATER_BREATH = MISC.register(
      "water_breath",
      () -> Builder.of(WaterBreathProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "water_breath").toString())
   );
   public static final RegistrySupplier<EntityType<WindBreathProjectile>> WIND_BREATH = MISC.register(
      "wind_breath",
      () -> Builder.of(WindBreathProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "wind_breath").toString())
   );
   public static final RegistrySupplier<EntityType<CurseBindHands>> CURSE_BIND_HANDS = MISC.register(
      "curse_bind_hands",
      () -> Builder.of(CurseBindHands::new, MobCategory.MISC)
         .sized(1.5F, 2.0F)
         .clientTrackingRange(128)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "curse_bind_hands").toString())
   );
   public static final RegistrySupplier<EntityType<DeathBlessingField>> DEATH_BLESSING = MISC.register(
      "death_blessing",
      () -> Builder.of(DeathBlessingField::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "death_blessing").toString())
   );
   public static final RegistrySupplier<EntityType<BloodMistCloud>> BLOOD_MIST = MISC.register(
      "blood_mist",
      () -> Builder.of(BloodMistCloud::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "blood_mist").toString())
   );
   public static final RegistrySupplier<EntityType<GravityField>> GRAVITY_FIELD = MISC.register(
      "gravity_field",
      () -> Builder.of(GravityField::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "gravity_field").toString())
   );
   public static final RegistrySupplier<EntityType<FireStormCloud>> FIRE_STORM = MISC.register(
      "fire_storm",
      () -> Builder.of(FireStormCloud::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "fire_storm").toString())
   );
   public static final RegistrySupplier<EntityType<HakiField>> HAKI_FIELD = MISC.register(
      "haki_field",
      () -> Builder.of(HakiField::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "haki_field").toString())
   );
   public static final RegistrySupplier<EntityType<SacredHakiField>> SACRED_HAKI_FIELD = MISC.register(
      "sacred_haki_field",
      () -> Builder.of(SacredHakiField::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "sacred_haki_field").toString())
   );
   public static final RegistrySupplier<EntityType<Hellfire>> HELLFIRE = MISC.register(
      "hellfire",
      () -> Builder.of(Hellfire::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "hellfire").toString())
   );
   public static final RegistrySupplier<EntityType<HellFlare>> HELL_FLARE = MISC.register(
      "hell_flare",
      () -> Builder.of(HellFlare::new, MobCategory.MISC)
         .sized(20.0F, 20.0F)
         .clientTrackingRange(128)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "hell_flare").toString())
   );
   public static final RegistrySupplier<EntityType<HellFlare>> HELL_FLARE_LIMITED = MISC.register(
      "hell_flare_limited",
      () -> Builder.of(HellFlare::new, MobCategory.MISC)
         .sized(3.0F, 3.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "hell_flare_limited").toString())
   );
   public static final RegistrySupplier<EntityType<MagicExplosion>> MAGIC_EXPLOSION = MISC.register(
      "magic_explosion",
      () -> Builder.of(MagicExplosion::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "magic_explosion").toString())
   );
   public static final RegistrySupplier<EntityType<MiasmicMistCloud>> MIASMIC_MIST = MISC.register(
      "miasmic_mist",
      () -> Builder.of(MiasmicMistCloud::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "miasmic_mist").toString())
   );
   public static final RegistrySupplier<EntityType<MarionetteLines>> MARIONETTE_LINES = MISC.register(
      "marionette_lines",
      () -> Builder.of(MarionetteLines::new, MobCategory.MISC)
         .sized(1.0F, 2.0F)
         .clientTrackingRange(128)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "marionette_lines").toString())
   );
   public static final RegistrySupplier<EntityType<MudHands>> MUD_HANDS = MISC.register(
      "mud_hands",
      () -> Builder.of(MudHands::new, MobCategory.MISC)
         .sized(1.5F, 2.0F)
         .clientTrackingRange(128)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "mud_hands").toString())
   );
   public static final RegistrySupplier<EntityType<ShadowBindHands>> SHADOW_BIND_HANDS = MISC.register(
      "shadow_bind_hands",
      () -> Builder.of(ShadowBindHands::new, MobCategory.MISC)
         .sized(1.5F, 2.0F)
         .clientTrackingRange(128)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "shadow_bind_hands").toString())
   );
   public static final RegistrySupplier<EntityType<SleepMistCloud>> SLEEP_MIST = MISC.register(
      "sleep_mist",
      () -> Builder.of(SleepMistCloud::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "sleep_mist").toString())
   );
   public static final RegistrySupplier<EntityType<LightningBolt>> LIGHTNING_BOLT = MISC.register(
      "lightning_bolt",
      () -> Builder.of(LightningBolt::new, MobCategory.MISC)
         .noSave()
         .sized(0.0F, 0.0F)
         .clientTrackingRange(64)
         .updateInterval(Integer.MAX_VALUE)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "lightning_bolt").toString())
   );
   public static final RegistrySupplier<EntityType<BlackLightningBolt>> BLACK_LIGHTNING_BOLT = MISC.register(
      "black_lightning_bolt",
      () -> Builder.of(BlackLightningBolt::new, MobCategory.MISC)
         .noSave()
         .sized(0.0F, 0.0F)
         .clientTrackingRange(64)
         .updateInterval(Integer.MAX_VALUE)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "black_lightning_bolt").toString())
   );
   public static final RegistrySupplier<EntityType<ShieldEntity>> AURA_SHIELD = MISC.register(
      "aura_shield",
      () -> Builder.of(ShieldEntity::new, MobCategory.MISC)
         .sized(1.0F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "aura_shield").toString())
   );
   public static final RegistrySupplier<EntityType<MagicShieldEntity>> MAGIC_SHIELD = MISC.register(
      "magic_shield",
      () -> Builder.of(MagicShieldEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(16)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "magic_barrier_pillar").toString())
   );
   public static final RegistrySupplier<EntityType<DripstoneSpikeEntity>> EARTH_SPIKE = MISC.register(
      "earth_spike",
      () -> Builder.of(DripstoneSpikeEntity::new, MobCategory.MISC)
         .sized(0.8F, 0.8F)
         .clientTrackingRange(16)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "earth_spike").toString())
   );
   public static final RegistrySupplier<EntityType<IcicleSpikeEntity>> ICICLE_SPIKE = MISC.register(
      "icicle_spike",
      () -> Builder.of(IcicleSpikeEntity::new, MobCategory.MISC)
         .sized(0.99F, 1.0F)
         .clientTrackingRange(16)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "icicle_spike").toString())
   );
   public static final RegistrySupplier<EntityType<MudSpikeEntity>> MUD_SPIKE = MISC.register(
      "mud_spike",
      () -> Builder.of(MudSpikeEntity::new, MobCategory.MISC)
         .sized(0.8F, 0.8F)
         .clientTrackingRange(16)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "mud_spike").toString())
   );
   public static final RegistrySupplier<EntityType<PillarEntity>> EARTH_PILLAR = MISC.register(
      "earth_pillar",
      () -> Builder.of(PillarEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(16)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "earth_pillar").toString())
   );
   public static final RegistrySupplier<EntityType<FirePillarEntity>> FIRE_PILLAR = MISC.register(
      "fire_pillar",
      () -> Builder.of(FirePillarEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(16)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "fire_pillar").toString())
   );
   public static final RegistrySupplier<EntityType<IcePillarEntity>> ICE_PILLAR = MISC.register(
      "ice_pillar",
      () -> Builder.of(IcePillarEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(16)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "ice_pillar").toString())
   );
   public static final RegistrySupplier<EntityType<PrimedCharybdisCoreEntity>> CHARYBDIS_CORE = MISC.register(
      "charybdis_core",
      () -> Builder.of(PrimedCharybdisCoreEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(16)
         .fireImmune()
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis_core").toString())
   );
   public static final RegistrySupplier<EntityType<DeathTornadoEntity>> DEATH_TORNADO = MISC.register(
      "death_tornado",
      () -> Builder.of((pEntityType, pLevel) -> new DeathTornadoEntity(pLevel), MobCategory.MISC)
         .noSave()
         .sized(0.0F, 0.0F)
         .clientTrackingRange(64)
         .updateInterval(Integer.MAX_VALUE)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "death_tornado").toString())
   );
   public static final RegistrySupplier<EntityType<TensuraFallingBlock>> FALLING_BLOCK = MISC.register(
      "falling_block",
      () -> Builder.of(TensuraFallingBlock::new, MobCategory.MISC)
         .sized(0.98F, 0.98F)
         .clientTrackingRange(16)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "falling_block").toString())
   );
   public static final RegistrySupplier<EntityType<HazyBlossomEntity>> HAZY_BLOSSOM = MISC.register(
      "hazy_blossom",
      () -> Builder.of(HazyBlossomEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "hazy_blossom").toString())
   );
   public static final RegistrySupplier<EntityType<MagicLandmineEntity>> LANDMINE = MISC.register(
      "fusionist_landmine",
      () -> Builder.of(MagicLandmineEntity::new, MobCategory.MISC)
         .sized(1.01F, 1.01F)
         .clientTrackingRange(16)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "fusionist_landmine").toString())
   );
   public static final RegistrySupplier<EntityType<MadOrbsEntity>> MAD_ORBS = MISC.register(
      "mad_orbs",
      () -> Builder.of(MadOrbsEntity::new, MobCategory.MISC)
         .sized(0.2F, 0.2F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "mad_orbs").toString())
   );
   public static final RegistrySupplier<EntityType<SummoningBeam>> SUMMONING_BEAM = MISC.register(
      "summoning_beam",
      () -> Builder.of(SummoningBeam::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "summoning_beam").toString())
   );
   public static final RegistrySupplier<EntityType<WarpPortalEntity>> WARP_PORTAL = MISC.register(
      "warp_portal",
      () -> Builder.of(WarpPortalEntity::new, MobCategory.MISC)
         .sized(0.15F, 0.15F)
         .clientTrackingRange(64)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "warp_portal").toString())
   );

   public static void init() {
      MISC.register();
   }
}
