package io.github.manasmods.tensura.registry.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.entity.monster.AkashEntity;
import io.github.manasmods.tensura.entity.monster.AquaFrogEntity;
import io.github.manasmods.tensura.entity.monster.ArchDaemonEntity;
import io.github.manasmods.tensura.entity.monster.ArmorsaurusEntity;
import io.github.manasmods.tensura.entity.monster.ArmyWaspEntity;
import io.github.manasmods.tensura.entity.monster.BarghestEntity;
import io.github.manasmods.tensura.entity.monster.BasiliskEntity;
import io.github.manasmods.tensura.entity.monster.BeastGnomeEntity;
import io.github.manasmods.tensura.entity.monster.BlackSpiderEntity;
import io.github.manasmods.tensura.entity.monster.BladeTigerEntity;
import io.github.manasmods.tensura.entity.monster.CattledeerEntity;
import io.github.manasmods.tensura.entity.monster.CharybdisEntity;
import io.github.manasmods.tensura.entity.monster.DirewolfEntity;
import io.github.manasmods.tensura.entity.monster.DragonPeacockEntity;
import io.github.manasmods.tensura.entity.monster.ElementalColossusEntity;
import io.github.manasmods.tensura.entity.monster.FeatheredSerpentEntity;
import io.github.manasmods.tensura.entity.monster.GiantAntEntity;
import io.github.manasmods.tensura.entity.monster.GiantBatEntity;
import io.github.manasmods.tensura.entity.monster.GiantBearEntity;
import io.github.manasmods.tensura.entity.monster.GiantCodEntity;
import io.github.manasmods.tensura.entity.monster.GiantSalmonEntity;
import io.github.manasmods.tensura.entity.monster.GoblinEntity;
import io.github.manasmods.tensura.entity.monster.GreaterDaemonEntity;
import io.github.manasmods.tensura.entity.monster.HellCaterpillarEntity;
import io.github.manasmods.tensura.entity.monster.HellMothEntity;
import io.github.manasmods.tensura.entity.monster.HornedBearEntity;
import io.github.manasmods.tensura.entity.monster.HornedRabbitEntity;
import io.github.manasmods.tensura.entity.monster.HoundDogEntity;
import io.github.manasmods.tensura.entity.monster.HoverLizardEntity;
import io.github.manasmods.tensura.entity.monster.IfritCloneEntity;
import io.github.manasmods.tensura.entity.monster.IfritEntity;
import io.github.manasmods.tensura.entity.monster.KnightSpiderEntity;
import io.github.manasmods.tensura.entity.monster.LandfishEntity;
import io.github.manasmods.tensura.entity.monster.LeechLizardEntity;
import io.github.manasmods.tensura.entity.monster.LesserDaemonEntity;
import io.github.manasmods.tensura.entity.monster.LizardmanEntity;
import io.github.manasmods.tensura.entity.monster.MegalodonEntity;
import io.github.manasmods.tensura.entity.monster.MetalSlimeEntity;
import io.github.manasmods.tensura.entity.monster.OneEyedOwlEntity;
import io.github.manasmods.tensura.entity.monster.OrcDisasterEntity;
import io.github.manasmods.tensura.entity.monster.OrcEntity;
import io.github.manasmods.tensura.entity.monster.OrcLordEntity;
import io.github.manasmods.tensura.entity.monster.PegacornEntity;
import io.github.manasmods.tensura.entity.monster.PegasusEntity;
import io.github.manasmods.tensura.entity.monster.PhantasporeEntity;
import io.github.manasmods.tensura.entity.monster.SalamanderEntity;
import io.github.manasmods.tensura.entity.monster.SissieEntity;
import io.github.manasmods.tensura.entity.monster.SlimeEntity;
import io.github.manasmods.tensura.entity.monster.SpearToroEntity;
import io.github.manasmods.tensura.entity.monster.SupermassiveSlimeEntity;
import io.github.manasmods.tensura.entity.monster.SylphideEntity;
import io.github.manasmods.tensura.entity.monster.UndineEntity;
import io.github.manasmods.tensura.entity.monster.UnicornEntity;
import io.github.manasmods.tensura.entity.monster.WarGnomeEntity;
import io.github.manasmods.tensura.entity.monster.WingedCatEntity;
import io.github.manasmods.tensura.entity.multipart.EvilCentipedeBody;
import io.github.manasmods.tensura.entity.multipart.EvilCentipedeEntity;
import io.github.manasmods.tensura.entity.multipart.TempestSerpentBody;
import io.github.manasmods.tensura.entity.multipart.TempestSerpentEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;

public class MonsterEntityTypes {
   private static final DeferredRegister<EntityType<?>> MONSTERS = DeferredRegister.create("tensura", Registries.ENTITY_TYPE);
   public static final RegistrySupplier<EntityType<AkashEntity>> AKASH = MONSTERS.register(
      "akash",
      () -> Builder.of(AkashEntity::new, MobCategory.MONSTER)
         .sized(0.8F, 1.7F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "akash").toString())
   );
   public static final RegistrySupplier<EntityType<AquaFrogEntity>> AQUA_FROG = MONSTERS.register(
      "aqua_frog",
      () -> Builder.of(AquaFrogEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.2F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "aqua_frog").toString())
   );
   public static final RegistrySupplier<EntityType<ArchDaemonEntity>> ARCH_DAEMON = MONSTERS.register(
      "arch_daemon",
      () -> Builder.of(ArchDaemonEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "arch_daemon").toString())
   );
   public static final RegistrySupplier<EntityType<ArmorsaurusEntity>> ARMORSAURUS = MONSTERS.register(
      "armorsaurus",
      () -> Builder.of(ArmorsaurusEntity::new, MobCategory.MONSTER)
         .sized(1.5F, 2.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "armorsaurus").toString())
   );
   public static final RegistrySupplier<EntityType<ArmyWaspEntity>> ARMY_WASP = MONSTERS.register(
      "army_wasp",
      () -> Builder.of(ArmyWaspEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "army_wasp").toString())
   );
   public static final RegistrySupplier<EntityType<BarghestEntity>> BARGHEST = MONSTERS.register(
      "barghest",
      () -> Builder.of(BarghestEntity::new, MobCategory.MONSTER)
         .sized(0.9F, 1.4F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "barghest").toString())
   );
   public static final RegistrySupplier<EntityType<BasiliskEntity>> BASILISK = MONSTERS.register(
      "basilisk",
      () -> Builder.of(BasiliskEntity::new, MobCategory.MONSTER)
         .sized(1.2F, 2.25F)
         .eyeHeight(2.2F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "basilisk").toString())
   );
   public static final RegistrySupplier<EntityType<BeastGnomeEntity>> BEAST_GNOME = MONSTERS.register(
      "beast_gnome",
      () -> Builder.of(BeastGnomeEntity::new, MobCategory.MONSTER)
         .sized(3.5F, 3.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "beast_gnome").toString())
   );
   public static final RegistrySupplier<EntityType<BlackSpiderEntity>> BLACK_SPIDER = MONSTERS.register(
      "black_spider",
      () -> Builder.of(BlackSpiderEntity::new, MobCategory.MONSTER)
         .sized(3.0F, 2.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "black_spider").toString())
   );
   public static final RegistrySupplier<EntityType<BladeTigerEntity>> BLADE_TIGER = MONSTERS.register(
      "blade_tiger",
      () -> Builder.of(BladeTigerEntity::new, MobCategory.MONSTER)
         .sized(1.5F, 2.6F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "blade_tiger").toString())
   );
   public static final RegistrySupplier<EntityType<CattledeerEntity>> CATTLEDEER = MONSTERS.register(
      "cattledeer",
      () -> Builder.of(CattledeerEntity::new, MobCategory.MONSTER)
         .sized(0.9F, 1.8F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "cattledeer").toString())
   );
   public static final RegistrySupplier<EntityType<CharybdisEntity>> CHARYBDIS = MONSTERS.register(
      "charybdis",
      () -> Builder.of(CharybdisEntity::new, MobCategory.MONSTER)
         .sized(14.0F, 13.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis").toString())
   );
   public static final RegistrySupplier<EntityType<DirewolfEntity>> DIREWOLF = MONSTERS.register(
      "direwolf",
      () -> Builder.of(DirewolfEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.3F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "direwolf").toString())
   );
   public static final RegistrySupplier<EntityType<DragonPeacockEntity>> DRAGON_PEACOCK = MONSTERS.register(
      "dragon_peacock",
      () -> Builder.of(DragonPeacockEntity::new, MobCategory.MONSTER)
         .sized(0.5F, 1.3F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "dragon_peacock").toString())
   );
   public static final RegistrySupplier<EntityType<ElementalColossusEntity>> ELEMENTAL_COLOSSUS = MONSTERS.register(
      "elemental_colossus",
      () -> Builder.of(ElementalColossusEntity::new, MobCategory.CREATURE)
         .sized(2.0F, 4.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "elemental_colossus").toString())
   );
   public static final RegistrySupplier<EntityType<FeatheredSerpentEntity>> FEATHERED_SERPENT = MONSTERS.register(
      "feathered_serpent",
      () -> Builder.of(FeatheredSerpentEntity::new, MobCategory.MONSTER)
         .sized(0.9F, 1.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "feathered_serpent").toString())
   );
   public static final RegistrySupplier<EntityType<GiantAntEntity>> GIANT_ANT = MONSTERS.register(
      "giant_ant",
      () -> Builder.of(GiantAntEntity::new, MobCategory.MONSTER)
         .sized(2.5F, 3.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "giant_ant").toString())
   );
   public static final RegistrySupplier<EntityType<GiantBatEntity>> GIANT_BAT = MONSTERS.register(
      "giant_bat",
      () -> Builder.of(GiantBatEntity::new, MobCategory.MONSTER)
         .sized(1.2F, 1.65F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "giant_bat").toString())
   );
   public static final RegistrySupplier<EntityType<GiantBearEntity>> GIANT_BEAR = MONSTERS.register(
      "giant_bear",
      () -> Builder.of(GiantBearEntity::new, MobCategory.MONSTER)
         .sized(2.0F, 3.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "giant_bear").toString())
   );
   public static final RegistrySupplier<EntityType<GiantCodEntity>> GIANT_COD = MONSTERS.register(
      "giant_cod",
      () -> Builder.of(GiantCodEntity::new, MobCategory.MONSTER)
         .sized(0.7F, 0.7F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "giant_cod").toString())
   );
   public static final RegistrySupplier<EntityType<GiantSalmonEntity>> GIANT_SALMON = MONSTERS.register(
      "giant_salmon",
      () -> Builder.of(GiantSalmonEntity::new, MobCategory.MONSTER)
         .sized(0.7F, 0.7F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "giant_salmon").toString())
   );
   public static final RegistrySupplier<EntityType<GoblinEntity>> GOBLIN = MONSTERS.register(
      "goblin",
      () -> Builder.of(GoblinEntity::new, MobCategory.MONSTER)
         .sized(0.45F, 1.35F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "goblin").toString())
   );
   public static final RegistrySupplier<EntityType<GreaterDaemonEntity>> GREATER_DAEMON = MONSTERS.register(
      "greater_daemon",
      () -> Builder.of(GreaterDaemonEntity::new, MobCategory.MONSTER)
         .sized(1.2F, 4.8F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "greater_daemon").toString())
   );
   public static final RegistrySupplier<EntityType<HellCaterpillarEntity>> HELL_CATERPILLAR = MONSTERS.register(
      "hell_caterpillar",
      () -> Builder.of(HellCaterpillarEntity::new, MobCategory.MONSTER)
         .sized(0.9F, 0.9F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "hell_caterpillar").toString())
   );
   public static final RegistrySupplier<EntityType<HellMothEntity>> HELL_MOTH = MONSTERS.register(
      "hell_moth",
      () -> Builder.of(HellMothEntity::new, MobCategory.MONSTER)
         .sized(1.2F, 1.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "hell_moth").toString())
   );
   public static final RegistrySupplier<EntityType<HornedBearEntity>> HORNED_BEAR = MONSTERS.register(
      "horned_bear",
      () -> Builder.of(HornedBearEntity::new, MobCategory.MONSTER)
         .sized(1.5F, 2.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "horned_bear").toString())
   );
   public static final RegistrySupplier<EntityType<HornedRabbitEntity>> HORNED_RABBIT = MONSTERS.register(
      "horned_rabbit",
      () -> Builder.of(HornedRabbitEntity::new, MobCategory.MONSTER)
         .sized(0.5F, 0.8F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "horned_rabbit").toString())
   );
   public static final RegistrySupplier<EntityType<HoundDogEntity>> HOUND_DOG = MONSTERS.register(
      "hound_dog",
      () -> Builder.of(HoundDogEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 1.3F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "hound_dog").toString())
   );
   public static final RegistrySupplier<EntityType<HoverLizardEntity>> HOVER_LIZARD = MONSTERS.register(
      "hover_lizard",
      () -> Builder.of(HoverLizardEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 2.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "hover_lizard").toString())
   );
   public static final RegistrySupplier<EntityType<IfritEntity>> IFRIT = MONSTERS.register(
      "ifrit",
      () -> Builder.of(IfritEntity::new, MobCategory.MONSTER)
         .sized(0.8F, 3.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "ifrit").toString())
   );
   public static final RegistrySupplier<EntityType<IfritCloneEntity>> IFRIT_CLONE = MONSTERS.register(
      "ifrit_clone",
      () -> Builder.of(IfritCloneEntity::new, MobCategory.MONSTER)
         .sized(0.8F, 3.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "ifrit_clone").toString())
   );
   public static final RegistrySupplier<EntityType<KnightSpiderEntity>> KNIGHT_SPIDER = MONSTERS.register(
      "knight_spider",
      () -> Builder.of(KnightSpiderEntity::new, MobCategory.MONSTER)
         .sized(5.0F, 3.75F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "knight_spider").toString())
   );
   public static final RegistrySupplier<EntityType<LandfishEntity>> LANDFISH = MONSTERS.register(
      "landfish",
      () -> Builder.of(LandfishEntity::new, MobCategory.MONSTER)
         .sized(0.8F, 1.3F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "landfish").toString())
   );
   public static final RegistrySupplier<EntityType<LeechLizardEntity>> LEECH_LIZARD = MONSTERS.register(
      "leech_lizard",
      () -> Builder.of(LeechLizardEntity::new, MobCategory.MONSTER)
         .sized(1.0F, 2.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "leech_lizard").toString())
   );
   public static final RegistrySupplier<EntityType<LesserDaemonEntity>> LESSER_DAEMON = MONSTERS.register(
      "lesser_daemon",
      () -> Builder.of(LesserDaemonEntity::new, MobCategory.MONSTER)
         .sized(1.2F, 4.8F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "lesser_daemon").toString())
   );
   public static final RegistrySupplier<EntityType<LizardmanEntity>> LIZARDMAN = MONSTERS.register(
      "lizardman",
      () -> Builder.of(LizardmanEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman").toString())
   );
   public static final RegistrySupplier<EntityType<MegalodonEntity>> MEGALODON = MONSTERS.register(
      "megalodon",
      () -> Builder.of(MegalodonEntity::new, MobCategory.MONSTER)
         .sized(3.5F, 2.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "megalodon").toString())
   );
   public static final RegistrySupplier<EntityType<OneEyedOwlEntity>> ONE_EYED_OWL = MONSTERS.register(
      "one_eyed_owl",
      () -> Builder.of(OneEyedOwlEntity::new, MobCategory.MONSTER)
         .sized(0.3F, 0.7F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "one_eyed_owl").toString())
   );
   public static final RegistrySupplier<EntityType<OrcEntity>> ORC = MONSTERS.register(
      "orc",
      () -> Builder.of(OrcEntity::new, MobCategory.MONSTER)
         .sized(0.8F, 2.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "orc").toString())
   );
   public static final RegistrySupplier<EntityType<OrcLordEntity>> ORC_LORD = MONSTERS.register(
      "orc_lord",
      () -> Builder.of(OrcLordEntity::new, MobCategory.MONSTER)
         .sized(1.5F, 4.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "orc_lord").toString())
   );
   public static final RegistrySupplier<EntityType<OrcDisasterEntity>> ORC_DISASTER = MONSTERS.register(
      "orc_disaster",
      () -> Builder.of(OrcDisasterEntity::new, MobCategory.MONSTER)
         .sized(1.5F, 5.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "orc_disaster").toString())
   );
   public static final RegistrySupplier<EntityType<PegasusEntity>> PEGASUS = MONSTERS.register(
      "pegasus",
      () -> Builder.of(PegasusEntity::new, MobCategory.MONSTER)
         .sized(1.3964844F, 1.6F)
         .eyeHeight(1.52F)
         .passengerAttachments(new float[]{1.25F})
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "pegasus").toString())
   );
   public static final RegistrySupplier<EntityType<PegacornEntity>> PEGACORN = MONSTERS.register(
      "pegacorn",
      () -> Builder.of(PegacornEntity::new, MobCategory.MONSTER)
         .sized(1.3964844F, 1.6F)
         .eyeHeight(1.52F)
         .passengerAttachments(new float[]{1.25F})
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "pegacorn").toString())
   );
   public static final RegistrySupplier<EntityType<PhantasporeEntity>> PHANTASPORE = MONSTERS.register(
      "phantaspore",
      () -> Builder.of(PhantasporeEntity::new, MobCategory.MONSTER)
         .sized(1.8F, 3.3F)
         .eyeHeight(2.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "phantaspore").toString())
   );
   public static final RegistrySupplier<EntityType<SalamanderEntity>> SALAMANDER = MONSTERS.register(
      "salamander",
      () -> Builder.of(SalamanderEntity::new, MobCategory.MONSTER)
         .sized(0.5F, 1.6F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "salamander").toString())
   );
   public static final RegistrySupplier<EntityType<SissieEntity>> SISSIE = MONSTERS.register(
      "sissie",
      () -> Builder.of(SissieEntity::new, MobCategory.WATER_CREATURE)
         .sized(6.5F, 6.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "sissie").toString())
   );
   public static final RegistrySupplier<EntityType<SlimeEntity>> SLIME = MONSTERS.register(
      "slime",
      () -> Builder.of(SlimeEntity::new, MobCategory.MONSTER)
         .sized(0.25F, 0.21F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "slime").toString())
   );
   public static final RegistrySupplier<EntityType<MetalSlimeEntity>> METAL_SLIME = MONSTERS.register(
      "metal_slime",
      () -> Builder.of(MetalSlimeEntity::new, MobCategory.MONSTER)
         .sized(0.25F, 0.21F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "metal_slime").toString())
   );
   public static final RegistrySupplier<EntityType<SupermassiveSlimeEntity>> SUPERMASSIVE_SLIME = MONSTERS.register(
      "supermassive_slime",
      () -> Builder.of(SupermassiveSlimeEntity::new, MobCategory.MONSTER)
         .sized(0.25F, 0.21F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "supermassive_slime").toString())
   );
   public static final RegistrySupplier<EntityType<SpearToroEntity>> SPEAR_TORO = MONSTERS.register(
      "spear_toro",
      () -> Builder.of(SpearToroEntity::new, MobCategory.WATER_CREATURE)
         .sized(2.5F, 2.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "spear_toro").toString())
   );
   public static final RegistrySupplier<EntityType<SylphideEntity>> SYLPHIDE = MONSTERS.register(
      "sylphide",
      () -> Builder.of(SylphideEntity::new, MobCategory.MONSTER)
         .sized(0.8F, 2.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "sylphide").toString())
   );
   public static final RegistrySupplier<EntityType<UndineEntity>> UNDINE = MONSTERS.register(
      "undine",
      () -> Builder.of(UndineEntity::new, MobCategory.MONSTER)
         .sized(0.8F, 2.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "undine").toString())
   );
   public static final RegistrySupplier<EntityType<UnicornEntity>> UNICORN = MONSTERS.register(
      "unicorn",
      () -> Builder.of(UnicornEntity::new, MobCategory.MONSTER)
         .sized(1.3964844F, 1.6F)
         .eyeHeight(1.52F)
         .passengerAttachments(new float[]{1.25F})
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "unicorn").toString())
   );
   public static final RegistrySupplier<EntityType<WarGnomeEntity>> WAR_GNOME = MONSTERS.register(
      "war_gnome",
      () -> Builder.of(WarGnomeEntity::new, MobCategory.MONSTER)
         .sized(1.2F, 4.5F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "war_gnome").toString())
   );
   public static final RegistrySupplier<EntityType<WingedCatEntity>> WINGED_CAT = MONSTERS.register(
      "winged_cat",
      () -> Builder.of(WingedCatEntity::new, MobCategory.MONSTER)
         .sized(0.9F, 1.1F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "winged_cat").toString())
   );
   public static final RegistrySupplier<EntityType<EvilCentipedeEntity>> EVIL_CENTIPEDE = MONSTERS.register(
      "evil_centipede",
      () -> Builder.of(EvilCentipedeEntity::new, MobCategory.MONSTER)
         .sized(0.75F, 0.9F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "evil_centipede").toString())
   );
   public static final RegistrySupplier<EntityType<EvilCentipedeBody>> EVIL_CENTIPEDE_BODY = MONSTERS.register(
      "evil_centipede_body",
      () -> Builder.of(EvilCentipedeBody::new, MobCategory.MISC)
         .sized(0.65F, 0.9F)
         .fireImmune()
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "evil_centipede_body").toString())
   );
   public static final RegistrySupplier<EntityType<TempestSerpentEntity>> TEMPEST_SERPENT = MONSTERS.register(
      "tempest_serpent",
      () -> Builder.of(TempestSerpentEntity::new, MobCategory.MONSTER)
         .sized(1.1F, 1.0F)
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "tempest_serpent").toString())
   );
   public static final RegistrySupplier<EntityType<TempestSerpentBody>> TEMPEST_SERPENT_BODY = MONSTERS.register(
      "tempest_serpent_body",
      () -> Builder.of(TempestSerpentBody::new, MobCategory.MISC)
         .sized(1.1F, 1.0F)
         .fireImmune()
         .clientTrackingRange(10)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "tempest_serpent_body").toString())
   );

   public static void init() {
      MONSTERS.register();
   }
}
