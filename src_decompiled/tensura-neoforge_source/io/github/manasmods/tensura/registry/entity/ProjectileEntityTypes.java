package io.github.manasmods.tensura.registry.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.entity.projectile.InvisibleArrow;
import io.github.manasmods.tensura.entity.projectile.KunaiProjectile;
import io.github.manasmods.tensura.entity.projectile.LightArrowProjectile;
import io.github.manasmods.tensura.entity.projectile.MonsterSpitProjectile;
import io.github.manasmods.tensura.entity.projectile.SevererBladeProjectile;
import io.github.manasmods.tensura.entity.projectile.SpearProjectile;
import io.github.manasmods.tensura.entity.projectile.SpearedFinArrow;
import io.github.manasmods.tensura.entity.projectile.ThrownHealingPotion;
import io.github.manasmods.tensura.entity.projectile.ThrownHolyWater;
import io.github.manasmods.tensura.entity.projectile.ThrownItemProjectile;
import io.github.manasmods.tensura.entity.projectile.UnicornHornProjectile;
import io.github.manasmods.tensura.entity.projectile.WebBulletProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.AcidBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.AuraBulletProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.AuraSlashProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.BlackFlameBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.BogShotProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.BoulderShotProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.BulletProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.ChaosEaterProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.DimensionCutProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FireBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FireBoltProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FireLanceProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FlameOrbProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FlameSphereProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FloatSphereProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FrostBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FusionistProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.GravitySphereProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.HeatSphereProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.HellFlareProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.IceLanceProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.InvisibleFireBoltProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.LightningLanceProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.LightningSphereProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.MagmaShotProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.MudShotProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.ObsidianShotProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.PlasmaBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.PoisonBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.PoisonCutterProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.ReflectorEchoProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.SeveranceCutterProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.SniperGrenadeProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.SolarGrenadeProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.SpaceCutProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.SpatialArrowProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.SteamBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.StoneShotProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.TempestScaleEntity;
import io.github.manasmods.tensura.entity.projectile.magic.ThunderLanceProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.ThunderSphereProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WaterBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WaterBladeProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WindBladeProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WindSphereProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WindTornadoProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;

public class ProjectileEntityTypes {
   private static final DeferredRegister<EntityType<?>> PROJECTILES = DeferredRegister.create("tensura", Registries.ENTITY_TYPE);
   public static final RegistrySupplier<EntityType<AcidBallProjectile>> ACID_BALL = PROJECTILES.register(
      "acid_ball",
      () -> Builder.of(AcidBallProjectile::new, MobCategory.MISC)
         .sized(0.4F, 0.4F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "acid_ball").toString())
   );
   public static final RegistrySupplier<EntityType<AuraSlashProjectile>> AURA_SLASH = PROJECTILES.register(
      "aura_slash",
      () -> Builder.of(AuraSlashProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.8F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "aura_slash").toString())
   );
   public static final RegistrySupplier<EntityType<AuraBulletProjectile>> AURA_BULLET = PROJECTILES.register(
      "aura_bullet",
      () -> Builder.of(AuraBulletProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "aura_bullet").toString())
   );
   public static final RegistrySupplier<EntityType<BlackFlameBallProjectile>> BLACK_FLAME_BALL = PROJECTILES.register(
      "black_flame_ball",
      () -> Builder.of(BlackFlameBallProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "black_flame_ball").toString())
   );
   public static final RegistrySupplier<EntityType<BogShotProjectile>> BOG_SHOT = PROJECTILES.register(
      "bog_shot",
      () -> Builder.of(BogShotProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "bog_shot").toString())
   );
   public static final RegistrySupplier<EntityType<BoulderShotProjectile>> BOULDER_SHOT = PROJECTILES.register(
      "boulder_shot",
      () -> Builder.of(BoulderShotProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "boulder_shot").toString())
   );
   public static final RegistrySupplier<EntityType<BulletProjectile>> BULLET = PROJECTILES.register(
      "bullet",
      () -> Builder.of(BulletProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "bullet").toString())
   );
   public static final RegistrySupplier<EntityType<ChaosEaterProjectile>> CHAOS_EATER = PROJECTILES.register(
      "chaos_eater",
      () -> Builder.of(ChaosEaterProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "chaos_eater").toString())
   );
   public static final RegistrySupplier<EntityType<DimensionCutProjectile>> DIMENSION_CUT = PROJECTILES.register(
      "dimension_cut_projectile",
      () -> Builder.of(DimensionCutProjectile::new, MobCategory.MISC)
         .sized(0.8F, 0.8F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "dimension_cut_projectile").toString())
   );
   public static final RegistrySupplier<EntityType<FireBallProjectile>> FIRE_BALL = PROJECTILES.register(
      "fire_ball",
      () -> Builder.of(FireBallProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "fire_ball").toString())
   );
   public static final RegistrySupplier<EntityType<FireBoltProjectile>> FIRE_BOLT = PROJECTILES.register(
      "fire_bolt",
      () -> Builder.of(FireBoltProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "fire_bolt").toString())
   );
   public static final RegistrySupplier<EntityType<FireLanceProjectile>> FIRE_LANCE = PROJECTILES.register(
      "fire_lance",
      () -> Builder.of(FireLanceProjectile::new, MobCategory.MISC)
         .sized(0.3F, 0.3F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "fire_lance").toString())
   );
   public static final RegistrySupplier<EntityType<FlameOrbProjectile>> FLAME_ORB = PROJECTILES.register(
      "flame_orb",
      () -> Builder.of(FlameOrbProjectile::new, MobCategory.MISC)
         .sized(0.3F, 0.3F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "flame_orb").toString())
   );
   public static final RegistrySupplier<EntityType<FlameSphereProjectile>> FLAME_SPHERE = PROJECTILES.register(
      "flame_sphere",
      () -> Builder.of(FlameSphereProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "flame_sphere").toString())
   );
   public static final RegistrySupplier<EntityType<FloatSphereProjectile>> FLOAT_SPHERE = PROJECTILES.register(
      "float_sphere",
      () -> Builder.of(FloatSphereProjectile::new, MobCategory.MISC)
         .sized(0.25F, 0.25F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "float_sphere").toString())
   );
   public static final RegistrySupplier<EntityType<FrostBallProjectile>> FROST_BALL = PROJECTILES.register(
      "frost_ball",
      () -> Builder.of(FrostBallProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "frost_ball").toString())
   );
   public static final RegistrySupplier<EntityType<FusionistProjectile>> FUSIONIST_PROJECTILE = PROJECTILES.register(
      "fusionist_projectile",
      () -> Builder.of(FusionistProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "fusionist_projectile").toString())
   );
   public static final RegistrySupplier<EntityType<GravitySphereProjectile>> GRAVITY_SPHERE = PROJECTILES.register(
      "gravity_sphere",
      () -> Builder.of(GravitySphereProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "gravity_sphere").toString())
   );
   public static final RegistrySupplier<EntityType<HeatSphereProjectile>> HEAT_SPHERE = PROJECTILES.register(
      "heat_sphere",
      () -> Builder.of(HeatSphereProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "heat_sphere").toString())
   );
   public static final RegistrySupplier<EntityType<HellFlareProjectile>> HELL_FLARE_PROJECTILE = PROJECTILES.register(
      "hell_flare_projectile",
      () -> Builder.of(HellFlareProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "hell_flare_projectile").toString())
   );
   public static final RegistrySupplier<EntityType<IceLanceProjectile>> ICE_LANCE = PROJECTILES.register(
      "ice_lance",
      () -> Builder.of(IceLanceProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "ice_lance").toString())
   );
   public static final RegistrySupplier<EntityType<InvisibleFireBoltProjectile>> INVISIBLE_FIRE_BOLT = PROJECTILES.register(
      "invisible_fire_bolt",
      () -> Builder.of(InvisibleFireBoltProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "invisible_fire_bolt").toString())
   );
   public static final RegistrySupplier<EntityType<LightArrowProjectile>> LIGHT_ARROW = PROJECTILES.register(
      "light_arrow",
      () -> Builder.of(LightArrowProjectile::new, MobCategory.MISC)
         .updateInterval(20)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(4)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "light_arrow").toString())
   );
   public static final RegistrySupplier<EntityType<LightningLanceProjectile>> LIGHTNING_LANCE = PROJECTILES.register(
      "lightning_lance",
      () -> Builder.of(LightningLanceProjectile::new, MobCategory.MISC)
         .sized(0.3F, 0.3F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "lightning_lance").toString())
   );
   public static final RegistrySupplier<EntityType<LightningSphereProjectile>> LIGHTNING_SPHERE = PROJECTILES.register(
      "lightning_sphere",
      () -> Builder.of(LightningSphereProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "lightning_sphere").toString())
   );
   public static final RegistrySupplier<EntityType<MagmaShotProjectile>> MAGMA_SHOT = PROJECTILES.register(
      "magma_shot",
      () -> Builder.of(MagmaShotProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "magma_shot").toString())
   );
   public static final RegistrySupplier<EntityType<MudShotProjectile>> MUD_SHOT = PROJECTILES.register(
      "mud_shot",
      () -> Builder.of(MudShotProjectile::new, MobCategory.MISC)
         .sized(0.4F, 0.4F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "mud_shot").toString())
   );
   public static final RegistrySupplier<EntityType<ObsidianShotProjectile>> OBSIDIAN_SHOT = PROJECTILES.register(
      "obsidian_shot",
      () -> Builder.of(ObsidianShotProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "obsidian_shot").toString())
   );
   public static final RegistrySupplier<EntityType<PlasmaBallProjectile>> PLASMA_BALL = PROJECTILES.register(
      "plasmas_ball",
      () -> Builder.of(PlasmaBallProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "plasmas_ball").toString())
   );
   public static final RegistrySupplier<EntityType<PoisonBallProjectile>> POISON_BALL = PROJECTILES.register(
      "poison_ball",
      () -> Builder.of(PoisonBallProjectile::new, MobCategory.MISC)
         .sized(0.4F, 0.4F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "poison_ball").toString())
   );
   public static final RegistrySupplier<EntityType<PoisonCutterProjectile>> POISON_CUTTER = PROJECTILES.register(
      "poison_cutter",
      () -> Builder.of(PoisonCutterProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.8F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "poison_cutter").toString())
   );
   public static final RegistrySupplier<EntityType<ReflectorEchoProjectile>> REFLECTOR_ECHO = PROJECTILES.register(
      "reflector_echo",
      () -> Builder.of(ReflectorEchoProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "reflector_echo").toString())
   );
   public static final RegistrySupplier<EntityType<SeveranceCutterProjectile>> SEVERANCE_CUTTER = PROJECTILES.register(
      "severance_cutter",
      () -> Builder.of(SeveranceCutterProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "severance_cutter").toString())
   );
   public static final RegistrySupplier<EntityType<SolarGrenadeProjectile>> SOLAR_GRENADE = PROJECTILES.register(
      "solar_grenade",
      () -> Builder.of(SolarGrenadeProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "solar_grenade").toString())
   );
   public static final RegistrySupplier<EntityType<SniperGrenadeProjectile>> SNIPER_GRENADE = PROJECTILES.register(
      "sniper_grenade",
      () -> Builder.of(SniperGrenadeProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "sniper_grenade").toString())
   );
   public static final RegistrySupplier<EntityType<SpaceCutProjectile>> SPACE_CUT = PROJECTILES.register(
      "space_cut_projectile",
      () -> Builder.of(SpaceCutProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.8F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "space_cut_projectile").toString())
   );
   public static final RegistrySupplier<EntityType<SpatialArrowProjectile>> SPATIAL_ARROW = PROJECTILES.register(
      "spatial_arrow",
      () -> Builder.of(SpatialArrowProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "spatial_arrow").toString())
   );
   public static final RegistrySupplier<EntityType<SteamBallProjectile>> STEAM_BALL = PROJECTILES.register(
      "steam_ball",
      () -> Builder.of(SteamBallProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "steam_ball").toString())
   );
   public static final RegistrySupplier<EntityType<StoneShotProjectile>> STONE_SHOT = PROJECTILES.register(
      "stone_shot",
      () -> Builder.of(StoneShotProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "stone_shot").toString())
   );
   public static final RegistrySupplier<EntityType<TempestScaleEntity>> TEMPEST_SCALE = PROJECTILES.register(
      "tempest_scale",
      () -> Builder.of(TempestScaleEntity::new, MobCategory.MISC)
         .sized(0.8F, 0.8F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "tempest_scale").toString())
   );
   public static final RegistrySupplier<EntityType<ThunderLanceProjectile>> THUNDER_LANCE = PROJECTILES.register(
      "thunder_lance",
      () -> Builder.of(ThunderLanceProjectile::new, MobCategory.MISC)
         .sized(0.3F, 0.3F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "thunder_lance").toString())
   );
   public static final RegistrySupplier<EntityType<ThunderSphereProjectile>> THUNDER_SPHERE = PROJECTILES.register(
      "thunder_sphere",
      () -> Builder.of(ThunderSphereProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "thunder_sphere").toString())
   );
   public static final RegistrySupplier<EntityType<WaterBallProjectile>> WATER_BALL = PROJECTILES.register(
      "water_ball",
      () -> Builder.of(WaterBallProjectile::new, MobCategory.MISC)
         .sized(0.4F, 0.4F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "water_ball").toString())
   );
   public static final RegistrySupplier<EntityType<WaterBladeProjectile>> WATER_BLADE = PROJECTILES.register(
      "water_blade",
      () -> Builder.of(WaterBladeProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.8F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "water_blade").toString())
   );
   public static final RegistrySupplier<EntityType<WindBladeProjectile>> WIND_BLADE = PROJECTILES.register(
      "wind_blade",
      () -> Builder.of(WindBladeProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.8F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "wind_blade").toString())
   );
   public static final RegistrySupplier<EntityType<WindSphereProjectile>> WIND_SPHERE = PROJECTILES.register(
      "wind_sphere",
      () -> Builder.of(WindSphereProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "wind_sphere").toString())
   );
   public static final RegistrySupplier<EntityType<WindTornadoProjectile>> WIND_TORNADO = PROJECTILES.register(
      "wind_tornado",
      () -> Builder.of(WindTornadoProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .clientTrackingRange(32)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "wind_tornado").toString())
   );
   public static final RegistrySupplier<EntityType<ThrownHealingPotion>> HEALING_POTION = PROJECTILES.register(
      "healing_potion",
      () -> Builder.of(ThrownHealingPotion::new, MobCategory.MISC)
         .sized(0.3F, 0.3F)
         .fireImmune()
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "healing_potion").toString())
   );
   public static final RegistrySupplier<EntityType<ThrownHolyWater>> HOLY_WATER = PROJECTILES.register(
      "holy_water",
      () -> Builder.of(ThrownHolyWater::new, MobCategory.MISC)
         .sized(0.3F, 0.3F)
         .fireImmune()
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "holy_water").toString())
   );
   public static final RegistrySupplier<EntityType<KunaiProjectile>> KUNAI = PROJECTILES.register(
      "kunai",
      () -> Builder.of(KunaiProjectile::new, MobCategory.MISC).sized(0.3F, 0.3F).build(ResourceLocation.fromNamespaceAndPath("tensura", "kunai").toString())
   );
   public static final RegistrySupplier<EntityType<MonsterSpitProjectile>> MONSTER_SPIT = PROJECTILES.register(
      "monster_spit",
      () -> Builder.of(MonsterSpitProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "monster_spit").toString())
   );
   public static final RegistrySupplier<EntityType<SevererBladeProjectile>> SEVERER_BLADE = PROJECTILES.register(
      "severer_blade_projectile",
      () -> Builder.of(SevererBladeProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "severer_blade_projectile").toString())
   );
   public static final RegistrySupplier<EntityType<SpearProjectile>> SPEAR = PROJECTILES.register(
      "spear",
      () -> Builder.of(SpearProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).build(ResourceLocation.fromNamespaceAndPath("tensura", "spear").toString())
   );
   public static final RegistrySupplier<EntityType<InvisibleArrow>> INVISIBLE_ARROW = PROJECTILES.register(
      "invisible_arrow",
      () -> Builder.of(InvisibleArrow::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(4)
         .updateInterval(20)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "invisible_arrow").toString())
   );
   public static final RegistrySupplier<EntityType<SpearedFinArrow>> SPEARED_FIN_ARROW = PROJECTILES.register(
      "speared_fin_arrow",
      () -> Builder.of(SpearedFinArrow::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(4)
         .updateInterval(20)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "speared_fin_arrow").toString())
   );
   public static final RegistrySupplier<EntityType<UnicornHornProjectile>> UNICORN_HORN = PROJECTILES.register(
      "unicorn_horn",
      () -> Builder.of(UnicornHornProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .clientTrackingRange(4)
         .updateInterval(20)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "unicorn_horn").toString())
   );
   public static final RegistrySupplier<EntityType<ThrownItemProjectile>> THROWN_ITEM = PROJECTILES.register(
      "thrown_item",
      () -> Builder.of(ThrownItemProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "thrown_item").toString())
   );
   public static final RegistrySupplier<EntityType<WebBulletProjectile>> WEB_BULLET = PROJECTILES.register(
      "web_bullet",
      () -> Builder.of(WebBulletProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "web_bullet").toString())
   );

   public static void init() {
      PROJECTILES.register();
   }
}
