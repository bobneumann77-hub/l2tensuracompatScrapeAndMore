package io.github.manasmods.tensura.registry.entity;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.entity.SpawnRateConfig;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.entity.human.DwarfEntity;
import io.github.manasmods.tensura.entity.human.FalmuthKnightEntity;
import io.github.manasmods.tensura.entity.human.FolgenEntity;
import io.github.manasmods.tensura.entity.human.GazelDwargoEntity;
import io.github.manasmods.tensura.entity.human.HinataSakaguchiEntity;
import io.github.manasmods.tensura.entity.human.KiraraMizutaniEntity;
import io.github.manasmods.tensura.entity.human.KyoyaTachinbanaEntity;
import io.github.manasmods.tensura.entity.human.MaiFurukiEntity;
import io.github.manasmods.tensura.entity.human.MarkLaurenEntity;
import io.github.manasmods.tensura.entity.human.ShinRyuseiEntity;
import io.github.manasmods.tensura.entity.human.ShinjiTanimuraEntity;
import io.github.manasmods.tensura.entity.human.ShizuEntity;
import io.github.manasmods.tensura.entity.human.ShogoTaguchiEntity;
import io.github.manasmods.tensura.entity.human.golem.BoneGolemEntity;
import io.github.manasmods.tensura.entity.human.golem.TrainingDummyEntity;
import io.github.manasmods.tensura.entity.human.undead.UndeadHumanoidEntity;
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
import io.github.manasmods.tensura.entity.multipart.EvilCentipedeEntity;
import io.github.manasmods.tensura.entity.multipart.TempestSerpentEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ISwimming;
import io.github.manasmods.tensura.registry.entity.ai.TensuraMemoryModules;
import io.github.manasmods.tensura.registry.entity.ai.TensuraSensors;
import io.github.manasmods.tensura.registry.entity.ai.TensuraVillagerProfessions;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class TensuraEntityTypes {
   public static final SpawnRateConfig CONFIG = (SpawnRateConfig)ConfigRegistry.getConfig(SpawnRateConfig.class);

   public static void init() {
      HumanEntityTypes.init();
      MonsterEntityTypes.init();
      ProjectileEntityTypes.init();
      MiscEntityTypes.init();
      TensuraSensors.init();
      TensuraMemoryModules.init();
      TensuraVillagerProfessions.init();
      createAttributes();
      spawnPlacements();
   }

   public static void createAttributes() {
      EntityAttributeRegistry.register(HumanEntityTypes.BONE_GOLEM, BoneGolemEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.CLONE, CloneEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.TRAINING_DUMMY, TrainingDummyEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.DWARF, DwarfEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.GAZEL_DWARGO, GazelDwargoEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.FALMUTH_KNIGHT, FalmuthKnightEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.FOLGEN, FolgenEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.HINATA_SAKAGUCHI, HinataSakaguchiEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.KIRARA_MIZUTANI, KiraraMizutaniEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.KYOYA_TACHIBANA, KyoyaTachinbanaEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.MAI_FURUKI, MaiFurukiEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.MARK_LAUREN, MarkLaurenEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.SHINJI_TANIMURA, ShinjiTanimuraEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.SHIN_RYUSEI, ShinRyuseiEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.SHIZU, ShizuEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.SHOGO_TAGUCHI, ShogoTaguchiEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.SKELETON, UndeadHumanoidEntity::setAttributes);
      EntityAttributeRegistry.register(HumanEntityTypes.ZOMBIE, UndeadHumanoidEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.AKASH, AkashEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.AQUA_FROG, AquaFrogEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.ARCH_DAEMON, ArchDaemonEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.ARMORSAURUS, ArmorsaurusEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.ARMY_WASP, ArmyWaspEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.BARGHEST, BarghestEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.BASILISK, BasiliskEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.BEAST_GNOME, BeastGnomeEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.BLACK_SPIDER, BlackSpiderEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.BLADE_TIGER, BladeTigerEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.CATTLEDEER, CattledeerEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.CHARYBDIS, CharybdisEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.DIREWOLF, DirewolfEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.DRAGON_PEACOCK, DragonPeacockEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.ELEMENTAL_COLOSSUS, ElementalColossusEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.EVIL_CENTIPEDE, EvilCentipedeEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.EVIL_CENTIPEDE_BODY, EvilCentipedeEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.FEATHERED_SERPENT, FeatheredSerpentEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.GIANT_ANT, GiantAntEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.GIANT_BAT, GiantBatEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.GIANT_BEAR, GiantBearEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.GIANT_COD, GiantCodEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.GIANT_SALMON, GiantSalmonEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.GOBLIN, GoblinEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.GREATER_DAEMON, GreaterDaemonEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.HELL_CATERPILLAR, HellCaterpillarEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.HELL_MOTH, HellMothEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.HORNED_BEAR, HornedBearEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.HORNED_RABBIT, HornedRabbitEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.HOUND_DOG, HoundDogEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.HOVER_LIZARD, HoverLizardEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.IFRIT, IfritEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.IFRIT_CLONE, IfritCloneEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.KNIGHT_SPIDER, KnightSpiderEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.LANDFISH, LandfishEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.LEECH_LIZARD, LeechLizardEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.LESSER_DAEMON, LesserDaemonEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.LIZARDMAN, LizardmanEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.MEGALODON, MegalodonEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.ONE_EYED_OWL, OneEyedOwlEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.ORC, OrcEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.ORC_LORD, OrcLordEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.ORC_DISASTER, OrcDisasterEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.PEGASUS, PegasusEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.PEGACORN, PegacornEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.PHANTASPORE, PhantasporeEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.SALAMANDER, SalamanderEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.SISSIE, SissieEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.SLIME, SlimeEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.METAL_SLIME, MetalSlimeEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.SUPERMASSIVE_SLIME, SupermassiveSlimeEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.SPEAR_TORO, SpearToroEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.SYLPHIDE, SylphideEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.UNDINE, UndineEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.UNICORN, UnicornEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.TEMPEST_SERPENT, TempestSerpentEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.TEMPEST_SERPENT_BODY, TempestSerpentEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.WAR_GNOME, WarGnomeEntity::setAttributes);
      EntityAttributeRegistry.register(MonsterEntityTypes.WINGED_CAT, WingedCatEntity::setAttributes);
   }

   public static void spawnPlacements() {
      SpawnPlacementsRegistry.register(
         HumanEntityTypes.KYOYA_TACHIBANA, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkTensuraMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.AQUA_FROG, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkTensuraMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.ARCH_DAEMON, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.ARMORSAURUS, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.ARMY_WASP, SpawnPlacementTypes.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkFlyingSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.BARGHEST, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.BASILISK, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.BEAST_GNOME, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkTensuraMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.BLACK_SPIDER, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, BlackSpiderEntity::checkSpiderSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.BLADE_TIGER, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkHostileGrassMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.CATTLEDEER, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkGrassMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.DIREWOLF, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.DRAGON_PEACOCK, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING, DragonPeacockEntity::checkPeacockSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.EVIL_CENTIPEDE, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.FEATHERED_SERPENT, SpawnPlacementTypes.NO_RESTRICTIONS, Types.MOTION_BLOCKING, TensuraTamableEntity::checkFlyingSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.GIANT_ANT, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.GIANT_BAT, SpawnPlacementTypes.NO_RESTRICTIONS, Types.MOTION_BLOCKING, GiantBatEntity::checkBatSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.GIANT_BEAR, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING, TensuraTamableEntity::checkGrassMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.GIANT_COD, SpawnPlacementTypes.NO_RESTRICTIONS, Types.MOTION_BLOCKING, ISwimming::checkDefaultSwimmingSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.GIANT_SALMON, SpawnPlacementTypes.NO_RESTRICTIONS, Types.MOTION_BLOCKING, ISwimming::checkDefaultSwimmingSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.HORNED_RABBIT, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, HornedRabbitEntity::checkHornedRabbitSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.GREATER_DAEMON, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.HELL_CATERPILLAR, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING, TensuraTamableEntity::checkGrassMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.HELL_MOTH, SpawnPlacementTypes.NO_RESTRICTIONS, Types.MOTION_BLOCKING, HellMothEntity::checkMothSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.HORNED_BEAR, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkHostileGrassMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.HOUND_DOG, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.HOVER_LIZARD, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkGrassMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.KNIGHT_SPIDER, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkTensuraMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.LANDFISH, SpawnPlacementTypes.NO_RESTRICTIONS, Types.MOTION_BLOCKING, LandfishEntity::checkLandfishSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.LEECH_LIZARD, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.LESSER_DAEMON, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.LIZARDMAN, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, LizardmanEntity::checkLizardSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.MEGALODON, SpawnPlacementTypes.NO_RESTRICTIONS, Types.MOTION_BLOCKING, MegalodonEntity::checkMegalodonSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.ONE_EYED_OWL, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING, OneEyedOwlEntity::checkOwlSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.ORC, SpawnPlacementTypes.ON_GROUND, Types.WORLD_SURFACE, TensuraTamableEntity::checkTensuraMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.PEGASUS, SpawnPlacementTypes.ON_GROUND, Types.WORLD_SURFACE, TensuraTamableEntity::checkGrassMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.PHANTASPORE, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkGrassMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.SALAMANDER, SpawnPlacementTypes.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkFlyingSpawnRules
      );
      SpawnPlacementsRegistry.register(MonsterEntityTypes.SISSIE, SpawnPlacementTypes.IN_WATER, Types.MOTION_BLOCKING, SissieEntity::checkSissieSpawnRules);
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.SLIME, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, TensuraTamableEntity::checkTensuraMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.SPEAR_TORO, SpawnPlacementTypes.IN_WATER, Types.MOTION_BLOCKING, SpearToroEntity::checkSpearToroSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.UNICORN, SpawnPlacementTypes.ON_GROUND, Types.WORLD_SURFACE, TensuraTamableEntity::checkGrassMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.TEMPEST_SERPENT, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING, TensuraTamableEntity::checkHostileMobSpawnRules
      );
      SpawnPlacementsRegistry.register(
         MonsterEntityTypes.WINGED_CAT, SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING, WingedCatEntity::checkWingedCatSpawnRules
      );
   }

   public static boolean rollSpawn(List<Integer> dayNightRoll, LevelAccessor level, MobSpawnType reason) {
      if (reason == MobSpawnType.SPAWNER) {
         return true;
      } else {
         int roll = level.getSkyDarken() >= 4 && !level.dimensionType().hasFixedTime() ? dayNightRoll.getLast() : dayNightRoll.getFirst();
         if (roll == 1) {
            return true;
         } else {
            return roll <= 0 ? false : level.getRandom().nextInt(roll) == 1;
         }
      }
   }

   public static boolean rollChance(int rolls, RandomSource randomSource) {
      return rolls <= 0 ? false : randomSource.nextInt(rolls) == 0;
   }
}
