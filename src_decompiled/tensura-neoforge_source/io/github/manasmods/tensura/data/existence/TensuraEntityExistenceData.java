package io.github.manasmods.tensura.data.existence;

import io.github.manasmods.tensura.registry.battlewill.MeleeArts;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.magic.SummoningMagics;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import java.util.List;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class TensuraEntityExistenceData {
   public static void bootstrap(BootstrapContext<EntityExistenceData> context) {
      applyHuman(context);
      applyTensura(context);
      applyVanillaHostile(context);
      applyVanillaNeutral(context);
      applyVanillaPassive(context);
   }

   public static void register(BootstrapContext<EntityExistenceData> context, EntityExistenceData data) {
      ResourceKey<EntityExistenceData> key = ResourceKey.create(TensuraCustomData.ENTITY_EXISTENCE, data.entity());
      context.register(key, data);
   }

   private static void applyHuman(BootstrapContext<EntityExistenceData> context) {
      register(
         context,
         EntityExistenceData.getDefault(
            HumanEntityTypes.GAZEL_DWARGO.getId(),
            3600,
            300000,
            300000,
            736331,
            736332,
            List.of(
               MeleeArts.FIVE_PETALS_THRUST.getId(),
               MeleeArts.EIGHT_PETALS_SLASH.getId(),
               ExtraSkills.MAGIC_SENSE.getId(),
               ExtraSkills.EARTH_DOMINATION.getId(),
               ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.EARTH_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.FLAME_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.LIGHT_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.SPATIAL_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.WATER_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.WIND_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.ELECTRICITY_RESISTANCE.getId(),
               ResistanceSkills.HEAT_RESISTANCE.getId(),
               ResistanceSkills.COLD_RESISTANCE.getId(),
               ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.getId(),
               ResistanceSkills.POISON_NULLIFICATION.getId(),
               ResistanceSkills.CORROSION_NULLIFICATION.getId(),
               ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.MAGIC_RESISTANCE.getId(),
               ResistanceSkills.HOLY_ATTACK_RESISTANCE.getId(),
               SpiritualMagics.EARTH_SPIKES.getId(),
               SpiritualMagics.EARTH_STORM.getId(),
               SummoningMagics.SUMMON_GREATER_ELEMENTAL.getId()
            )
         )
      );
      register(context, EntityExistenceData.getDefault(HumanEntityTypes.DWARF.getId(), 70, 80, 120, 720, 1080));
      register(context, EntityExistenceData.getDefault(HumanEntityTypes.FALMUTH_KNIGHT.getId(), 100, 100, 100, 2000, 3000));
      register(
         context,
         EntityExistenceData.getDefault(
            HumanEntityTypes.FOLGEN.getId(),
            1000,
            20000,
            30000,
            100000,
            110000,
            List.of(UniqueSkills.SPEARHEAD.getId(), ExtraSkills.STRENGTHEN_BODY.getId(), ExtraSkills.STEEL_STRENGTH.getId())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            HumanEntityTypes.HINATA_SAKAGUCHI.getId(),
            3600,
            536331,
            536332,
            500000,
            500000,
            List.of(
               UniqueSkills.USURPER.getId(),
               UniqueSkills.MATHEMATICIAN.getId(),
               ExtraSkills.MAGIC_SENSE.getId(),
               ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.EARTH_ATTACK_NULLIFICATION.getId(),
               ResistanceSkills.FLAME_ATTACK_NULLIFICATION.getId(),
               ResistanceSkills.LIGHT_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.SPATIAL_ATTACK_NULLIFICATION.getId(),
               ResistanceSkills.WATER_ATTACK_NULLIFICATION.getId(),
               ResistanceSkills.WIND_ATTACK_NULLIFICATION.getId(),
               ResistanceSkills.ELECTRICITY_NULLIFICATION.getId(),
               ResistanceSkills.HEAT_RESISTANCE.getId(),
               ResistanceSkills.COLD_RESISTANCE.getId(),
               ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.getId(),
               ResistanceSkills.POISON_NULLIFICATION.getId(),
               ResistanceSkills.CORROSION_NULLIFICATION.getId(),
               ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.MAGIC_RESISTANCE.getId(),
               ResistanceSkills.HOLY_ATTACK_RESISTANCE.getId(),
               SpiritualMagics.HELLFIRE.getId(),
               SpiritualMagics.BLIZZARD.getId(),
               SpiritualMagics.EARTH_JAIL.getId(),
               SpiritualMagics.AERIAL_BLADE.getId(),
               SpiritualMagics.SWIPE.getId(),
               SummoningMagics.SUMMON_GREATER_ELEMENTAL.getId(),
               SummoningMagics.SUMMON_MEDIUM_ELEMENTAL.getId()
            )
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(HumanEntityTypes.KIRARA_MIZUTANI.getId(), 1000, 20000, 25000, 30000, 45000, List.of(UniqueSkills.BEWILDER.getId()))
      );
      register(
         context,
         EntityExistenceData.getDefault(
            HumanEntityTypes.KYOYA_TACHIBANA.getId(),
            1000,
            30000,
            35000,
            50000,
            55000,
            List.of(UniqueSkills.SEVERER.getId(), ExtraSkills.ALL_SEEING_EYE.getId(), ExtraSkills.THOUGHT_ACCELERATION.getId())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            HumanEntityTypes.MAI_FURUKI.getId(),
            1000,
            80000,
            90000,
            70000,
            80000,
            List.of(UniqueSkills.TRAVELER.getId(), ExtraSkills.SPATIAL_MOTION.getId(), ExtraSkills.MAGIC_SENSE.getId())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            HumanEntityTypes.MARK_LAUREN.getId(),
            1000,
            30000,
            40000,
            60000,
            70000,
            List.of(UniqueSkills.THROWER.getId(), ExtraSkills.STRENGTHEN_BODY.getId(), CommonSkills.STRENGTH.getId())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(HumanEntityTypes.SHINJI_TANIMURA.getId(), 1000, 70000, 80000, 50000, 60000, List.of(UniqueSkills.HEALER.getId()))
      );
      register(
         context,
         EntityExistenceData.getDefault(HumanEntityTypes.SHIN_RYUSEI.getId(), 1000, 30000, 40000, 50000, 60000, List.of(UniqueSkills.OBSERVER.getId()))
      );
      register(
         context,
         EntityExistenceData.getDefault(
            HumanEntityTypes.SHIZU.getId(),
            2000,
            50000,
            50000,
            143543,
            143543,
            List.of(
               UniqueSkills.DEGENERATE.getId(),
               ExtraSkills.HEAT_WAVE.getId(),
               ExtraSkills.FLAME_MANIPULATION.getId(),
               ExtraSkills.MAGIC_SENSE.getId(),
               IntrinsicSkills.FLAME_TRANSFORM.getId(),
               CommonSkills.RANGED_BARRIER.getId(),
               ResistanceSkills.FLAME_ATTACK_NULLIFICATION.getId(),
               ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.getId(),
               SpiritualMagics.FIRE_BOLT.getId(),
               SpiritualMagics.HELLFIRE.getId()
            )
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            HumanEntityTypes.SHOGO_TAGUCHI.getId(), 1000, 30000, 40000, 50000, 60000, List.of(UniqueSkills.BERSERKER.getId(), UniqueSkills.SURVIVOR.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(HumanEntityTypes.SKELETON.getId(), 100, 1000, 3000));
      register(context, EntityExistenceData.getDefault(HumanEntityTypes.ZOMBIE.getId(), 100, 1000, 3000));
   }

   private static void applyTensura(BootstrapContext<EntityExistenceData> context) {
      List<ResourceLocation> fishSkills = List.of(IntrinsicSkills.WATER_BREATHING.getId());
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.AKASH.getId(),
            2000,
            120000,
            150000,
            List.of(
               IntrinsicSkills.SPACE_TRANSFORM.getId(),
               ExtraSkills.SPATIAL_MANIPULATION.getId(),
               SpiritualMagics.TELEPORT.getId(),
               ResistanceSkills.SPATIAL_ATTACK_NULLIFICATION.getId()
            )
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.AQUA_FROG.getId(),
            500,
            6000,
            9000,
            List.of(
               ExtraSkills.WATER_MANIPULATION.getId(),
               CommonSkills.POISON.getId(),
               IntrinsicSkills.WATER_TRANSFORM.getId(),
               ResistanceSkills.WATER_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.POISON_RESISTANCE.getId()
            )
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.ARCH_DAEMON.getId(),
            2000,
            80000,
            140000,
            500,
            1000,
            List.of(
               ExtraSkills.MAGIC_SENSE.getId(),
               IntrinsicSkills.POSSESSION.getId(),
               ResistanceSkills.MAGIC_RESISTANCE.getId(),
               AspectualMagics.MUD_SPEARS.getId(),
               AspectualMagics.STONE_SHOT.getId(),
               AspectualMagics.FIRE_BALL.getId(),
               AspectualMagics.ICE_BREAKER.getId(),
               AspectualMagics.THUNDER_ORB.getId(),
               AspectualMagics.ACID_SHELL.getId(),
               AspectualMagics.WATER_JAIL.getId(),
               AspectualMagics.TORNADO_BLADE.getId(),
               AspectualMagics.REINFORCED_BARRIER.getId()
            )
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(MonsterEntityTypes.ARMORSAURUS.getId(), 200, 2000, 2500, 3000, 3500, List.of(IntrinsicSkills.BODY_ARMOR.getId()))
      );
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.ARMY_WASP.getId(), 160, 10000, 15000, List.of(CommonSkills.POISON.getId())));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.BARGHEST.getId(), 80, 2000, 4000));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.BASILISK.getId(), 200, 5000, 6000, 1000, 2000));
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.BEAST_GNOME.getId(),
            500,
            5000,
            9000,
            List.of(
               ExtraSkills.EARTH_MANIPULATION.getId(),
               ExtraSkills.MAGIC_SENSE.getId(),
               IntrinsicSkills.EARTH_TRANSFORM.getId(),
               ResistanceSkills.EARTH_ATTACK_RESISTANCE.getId()
            )
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(MonsterEntityTypes.BLACK_SPIDER.getId(), 200, 5000, 6000, 1000, 1500, List.of(ExtraSkills.STICKY_STEEL_THREAD.getId()))
      );
      register(
         context,
         EntityExistenceData.getDefault(MonsterEntityTypes.BLADE_TIGER.getId(), 300, 8000, 11000, 1000, 2000, List.of(CommonSkills.VOICE_CANNON.getId()))
      );
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.CATTLEDEER.getId(), 60, 500, 700));
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.CHARYBDIS.getId(),
            3200,
            1200000,
            1200000,
            200000,
            400000,
            List.of(
               CommonSkills.GRAVITY_FLIGHT.getId(),
               ExtraSkills.GRAVITY_MANIPULATION.getId(),
               ExtraSkills.MAGIC_JAMMING.getId(),
               ExtraSkills.MAGIC_SENSE.getId(),
               ExtraSkills.ULTRASPEED_REGENERATION.getId(),
               ResistanceSkills.PAIN_RESISTANCE.getId(),
               ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.getId(),
               ResistanceSkills.PARALYSIS_RESISTANCE.getId()
            )
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.DIREWOLF.getId(), 100, 3600, 4300, List.of(CommonSkills.COERCION.getId(), CommonSkills.THOUGHT_COMMUNICATION.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.DRAGON_PEACOCK.getId(), 60, 1000, 2000));
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.EVIL_CENTIPEDE.getId(), 150, 6000, 7000, 1000, 2000, List.of(IntrinsicSkills.PARALYSING_BREATH.getId())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.ELEMENTAL_COLOSSUS.getId(), 4500, 330000, 350000, List.of(ResistanceSkills.THERMAL_FLUCTUATION_RESISTANCE.getId())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.FEATHERED_SERPENT.getId(),
            500,
            6000,
            9000,
            List.of(ExtraSkills.WIND_MANIPULATION.getId(), IntrinsicSkills.WIND_TRANSFORM.getId(), ResistanceSkills.WIND_ATTACK_RESISTANCE.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.GIANT_ANT.getId(), 100, 2334, 2500, 2000, 2499));
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.GIANT_BAT.getId(), 100, 2500, 3000, 1834, 1999, List.of(IntrinsicSkills.DRAIN.getId(), IntrinsicSkills.ULTRASONIC_WAVES.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.GIANT_BEAR.getId(), 80, 1000, 3000, 3000, 4000));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.GIANT_COD.getId(), 30, 500, 1000, 500, 1000, fishSkills));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.GIANT_SALMON.getId(), 30, 500, 1000, 500, 1000, fishSkills));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.GOBLIN.getId(), 44, 100, 300, 400, 700));
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.GREATER_DAEMON.getId(),
            400,
            9500,
            69000,
            500,
            1000,
            List.of(
               ExtraSkills.MAGIC_SENSE.getId(),
               IntrinsicSkills.POSSESSION.getId(),
               ResistanceSkills.MAGIC_RESISTANCE.getId(),
               AspectualMagics.STONE_SHOT.getId(),
               AspectualMagics.FIRE_LANCE.getId(),
               AspectualMagics.THUNDER_ORB.getId(),
               AspectualMagics.ACID_SHELL.getId(),
               AspectualMagics.TORNADO_BLADE.getId(),
               AspectualMagics.MAGIC_BARRIER.getId(),
               AspectualMagics.ICICLE_LANCE.getId(),
               AspectualMagics.SLEEP_MIST.getId()
            ),
            EntityType.getKey((EntityType)MonsterEntityTypes.ARCH_DAEMON.get())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.HELL_CATERPILLAR.getId(), 40, 2000, 2999, 1000, 2000, EntityType.getKey((EntityType)MonsterEntityTypes.HELL_MOTH.get())
         )
      );
      register(
         context, EntityExistenceData.getDefault(MonsterEntityTypes.HELL_MOTH.getId(), 80, 4000, 4500, 2000, 2499, List.of(CommonSkills.PARALYSIS.getId()))
      );
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.HORNED_BEAR.getId(), 200, 2000, 4000, 1000, 2000));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.HORNED_RABBIT.getId(), 50, 2000, 3000, 1000, 3000));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.HOUND_DOG.getId(), 120, 1000, 1499));
      List<ResourceLocation> lizardSkills = List.of(ExtraSkills.SENSE_HEAT_SOURCE.getId());
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.HOVER_LIZARD.getId(), 100, 3000, 6000, lizardSkills));
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.IFRIT.getId(),
            2000,
            120000,
            150000,
            List.of(
               IntrinsicSkills.FLAME_TRANSFORM.getId(),
               ExtraSkills.FLAME_MANIPULATION.getId(),
               ExtraSkills.BODY_DOUBLE.getId(),
               CommonSkills.RANGED_BARRIER.getId(),
               SpiritualMagics.FIRE_BOLT.getId(),
               SpiritualMagics.FLARE_CIRCLE.getId(),
               SpiritualMagics.HELLFIRE.getId(),
               ResistanceSkills.FLAME_ATTACK_NULLIFICATION.getId()
            )
         )
      );
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.KNIGHT_SPIDER.getId(), 180, 5000, 6500, 4000, 4500));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.LANDFISH.getId(), 30, 400, 600, 200, 200));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.LEECH_LIZARD.getId(), 70, 3000, 6000, lizardSkills));
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.LESSER_DAEMON.getId(),
            200,
            7500,
            8000,
            500,
            1000,
            List.of(
               ExtraSkills.MAGIC_SENSE.getId(),
               IntrinsicSkills.POSSESSION.getId(),
               ResistanceSkills.MAGIC_RESISTANCE.getId(),
               AspectualMagics.EARTH_LOCK.getId(),
               AspectualMagics.STONE_SHOT.getId(),
               AspectualMagics.FIRE.getId(),
               AspectualMagics.FIRE_BALL.getId(),
               AspectualMagics.WATER.getId(),
               AspectualMagics.WATER_CUTTER.getId(),
               AspectualMagics.WIND_GUST.getId(),
               AspectualMagics.WIND_CUTTER.getId()
            ),
            EntityType.getKey((EntityType)MonsterEntityTypes.GREATER_DAEMON.get())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(MonsterEntityTypes.LIZARDMAN.getId(), 68, 2000, 3000, 3000, 5000, List.of(IntrinsicSkills.SCALE_ARMOR.getId()))
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.MEGALODON.getId(),
            200,
            15000,
            30000,
            5000,
            10000,
            List.of(IntrinsicSkills.SCALE_ARMOR.getId(), ExtraSkills.MAGIC_JAMMING.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.ONE_EYED_OWL.getId(), 40, 150, 250, 150, 250));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.ORC.getId(), 76, 500, 1500, 500, 1500));
      List<ResourceLocation> orcLordSkills = List.of(
         UniqueSkills.STARVED.getId(),
         CommonSkills.CORROSION.getId(),
         CommonSkills.SELF_REGENERATION.getId(),
         CommonSkills.STRENGTH.getId(),
         IntrinsicSkills.SCALE_ARMOR.getId()
      );
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.ORC_LORD.getId(), 1000, 80000, 90000, orcLordSkills));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.ORC_DISASTER.getId(), 2700, 224435, 250000, orcLordSkills));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.PEGASUS.getId(), 80, 2000, 4000, 1000, 2000));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.PEGACORN.getId(), 120, 6500, 7000, 2500, 3000));
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.PHANTASPORE.getId(), 80, 2500, 5000, 500, 1000, List.of(CommonSkills.POISON.getId(), ResistanceSkills.POISON_RESISTANCE.getId())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.SLIME.getId(), 100, 180, 980, 10, 10, List.of(CommonSkills.SELF_REGENERATION.getId(), IntrinsicSkills.ABSORB_DISSOLVE.getId())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.METAL_SLIME.getId(),
            500,
            100000,
            300000,
            10,
            10,
            List.of(
               CommonSkills.SELF_REGENERATION.getId(),
               IntrinsicSkills.BODY_ARMOR.getId(),
               IntrinsicSkills.ABSORB_DISSOLVE.getId(),
               ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.getId()
            )
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.SUPERMASSIVE_SLIME.getId(),
            1000,
            50000,
            100000,
            10,
            10,
            List.of(ExtraSkills.ULTRASPEED_REGENERATION.getId(), IntrinsicSkills.ABSORB_DISSOLVE.getId())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.SALAMANDER.getId(),
            500,
            7000,
            8500,
            List.of(
               IntrinsicSkills.FLAME_BREATH.getId(),
               IntrinsicSkills.FLAME_TRANSFORM.getId(),
               ExtraSkills.FLAME_MANIPULATION.getId(),
               ResistanceSkills.FLAME_ATTACK_RESISTANCE.getId()
            )
         )
      );
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.SISSIE.getId(), 300, 20000, 35000, 10000, 15000, fishSkills));
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.SPEAR_TORO.getId(), 160, 6000, 6500, 3000, 3500, fishSkills));
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.SYLPHIDE.getId(),
            2000,
            120000,
            150000,
            List.of(
               IntrinsicSkills.WIND_TRANSFORM.getId(),
               ExtraSkills.WIND_MANIPULATION.getId(),
               SpiritualMagics.WIND_BLADE.getId(),
               SpiritualMagics.AERIAL_BLADE.getId(),
               SpiritualMagics.ELECTRO_BLAST.getId(),
               ResistanceSkills.WIND_ATTACK_NULLIFICATION.getId()
            )
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.UNDINE.getId(),
            2000,
            120000,
            150000,
            List.of(
               IntrinsicSkills.WATER_TRANSFORM.getId(),
               ExtraSkills.WATER_MANIPULATION.getId(),
               SpiritualMagics.WATER_CUTTER.getId(),
               SpiritualMagics.ACID_RAIN.getId(),
               ResistanceSkills.WATER_ATTACK_NULLIFICATION.getId()
            )
         )
      );
      register(context, EntityExistenceData.getDefault(MonsterEntityTypes.UNICORN.getId(), 100, 5000, 6000, 2000, 3000));
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.TEMPEST_SERPENT.getId(),
            160,
            8000,
            8500,
            1000,
            1500,
            List.of(IntrinsicSkills.POISONOUS_BREATH.getId(), ExtraSkills.SENSE_HEAT_SOURCE.getId())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.WAR_GNOME.getId(),
            2000,
            120000,
            150000,
            List.of(
               CommonSkills.GRAVITY_FIELD.getId(),
               IntrinsicSkills.EARTH_TRANSFORM.getId(),
               ExtraSkills.EARTH_MANIPULATION.getId(),
               SpiritualMagics.EARTH_SPIKES.getId(),
               SpiritualMagics.EARTH_JAIL.getId(),
               ResistanceSkills.EARTH_ATTACK_NULLIFICATION.getId()
            )
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            MonsterEntityTypes.WINGED_CAT.getId(),
            500,
            8000,
            9000,
            List.of(ExtraSkills.SPATIAL_MANIPULATION.getId(), IntrinsicSkills.SPACE_TRANSFORM.getId(), ResistanceSkills.SPATIAL_ATTACK_RESISTANCE.getId())
         )
      );
   }

   private static void applyVanillaHostile(BootstrapContext<EntityExistenceData> context) {
      register(
         context,
         EntityExistenceData.getDefault(
            EntityType.getKey(EntityType.BLAZE), 80, 2500, 2500, List.of(CommonSkills.GRAVITY_FLIGHT.getId(), ResistanceSkills.HEAT_NULLIFICATION.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.BOGGED), 2100, 2100));
      register(
         context,
         EntityExistenceData.getDefault(
            EntityType.getKey(EntityType.BREEZE), 80, 2500, 2500, List.of(SpiritualMagics.WIND.getId(), ResistanceSkills.WIND_ATTACK_RESISTANCE.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.CREEPER), 2200, 2200));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.DROWNED), 1600, 1600));
      register(
         context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.ELDER_GUARDIAN), 1000, 10000, 10000, List.of(CommonSkills.COERCION.getId()))
      );
      register(
         context,
         EntityExistenceData.getDefault(EntityType.getKey(EntityType.ENDER_DRAGON), 4000, 25000, 25000, List.of(CommonSkills.SELF_REGENERATION.getId()))
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.ENDERMITE), 20, 1300, 1300));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.EVOKER), 80, 4000, 4000, 4000, 4000, List.of(ExtraSkills.SAGE.getId())));
      register(
         context,
         EntityExistenceData.getDefault(EntityType.getKey(EntityType.GHAST), 100, 3000, 3000, List.of(ResistanceSkills.FLAME_ATTACK_NULLIFICATION.getId()))
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.GUARDIAN), 60, 3670, 3670, EntityType.getKey(EntityType.ELDER_GUARDIAN)));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.HOGLIN), 80, 3500, 3500));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.HUSK), 2100, 2100));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.ILLUSIONER), 80, 4000, 4000, 4000, 4000));
      register(
         context,
         EntityExistenceData.getDefault(EntityType.getKey(EntityType.MAGMA_CUBE), 40, 1000, 1000, List.of(ResistanceSkills.HEAT_NULLIFICATION.getId()))
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.PIGLIN_BRUTE), 100, 50, 50, 6500, 6500));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.PILLAGER), 50, 50, 2750, 2750));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.PHANTOM), 80, 2000, 2000));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.RAVAGER), 200, 2000, 2000, 3300, 3300));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.SHULKER), 80, 1450, 1450));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.SILVERFISH), 20, 1000, 1000));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.SKELETON), 2000, 2000));
      register(
         context,
         EntityExistenceData.getDefault(
            EntityType.getKey(EntityType.SLIME), 60, 1000, 1000, List.of(CommonSkills.SELF_REGENERATION.getId(), IntrinsicSkills.ABSORB_DISSOLVE.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.STRAY), 2100, 2100));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.VEX), 60, 1900, 1900));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.VINDICATOR), 50, 50, 50, 4500, 4500));
      register(
         context,
         EntityExistenceData.getDefault(
            EntityType.getKey(EntityType.WARDEN),
            2000,
            8000,
            8000,
            2000,
            2000,
            List.of(
               ExtraSkills.STEEL_STRENGTH.getId(),
               IntrinsicSkills.ULTRASONIC_WAVES.getId(),
               ExtraSkills.SENSE_SOUNDWAVE.getId(),
               ResistanceSkills.HEAT_RESISTANCE.getId()
            )
         )
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.WITCH), 60, 2750, 2750, 1050, 1050));
      register(
         context,
         EntityExistenceData.getDefault(
            EntityType.getKey(EntityType.WITHER),
            4000,
            15000,
            15000,
            List.of(CommonSkills.GRAVITY_FLIGHT.getId(), CommonSkills.CORROSION.getId(), CommonSkills.SELF_REGENERATION.getId())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(
            EntityType.getKey(EntityType.WITHER_SKELETON), 60, 3100, 3100, List.of(CommonSkills.CORROSION.getId(), ResistanceSkills.HEAT_NULLIFICATION.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.ZOGLIN), 80, 2500, 2500));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.ZOMBIE), 1400, 1400));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.ZOMBIE_VILLAGER), 450, 450, 1000, 1000));
   }

   private static void applyVanillaNeutral(BootstrapContext<EntityExistenceData> context) {
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.BEE), 20, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.CAVE_SPIDER), 30, 250, 250, 800, 800, List.of(CommonSkills.POISON.getId())));
      register(
         context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.DOLPHIN), 20, 10, 10, 20, 20, List.of(CommonSkills.HYDRAULIC_PROPULSION.getId()))
      );
      register(
         context,
         EntityExistenceData.getDefault(
            EntityType.getKey(EntityType.ENDERMAN), 80, 300, 3000, 300, 300, List.of(ExtraSkills.SHADOW_MOTION.getId(), ExtraSkills.DANGER_SENSE.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.GOAT), 20, 120, 120, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.IRON_GOLEM), 200, 5400, 5400, List.of(CommonSkills.STRENGTH.getId())));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.LLAMA), 30, 10, 10, 110, 110));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.PANDA), 40, 10, 10, 20, 20));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.PIGLIN), 40, 500, 500, 1500, 1500));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.POLAR_BEAR), 60, 10, 10, 1440, 1440));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.SPIDER), 40, 850, 850, 200, 200));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.TRADER_LLAMA), 40, 10, 10, 110, 110));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.WOLF), 50, 10, 10, 1090, 1090));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.ZOMBIFIED_PIGLIN), 1550, 1550));
   }

   private static void applyVanillaPassive(BootstrapContext<EntityExistenceData> context) {
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.ALLAY), 80, 334, 500));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.ARMADILLO), 30, 10, 10, 10, 10));
      register(
         context,
         EntityExistenceData.getDefault(
            EntityType.getKey(EntityType.AXOLOTL), 30, 10, 10, 10, 10, List.of(CommonSkills.SELF_REGENERATION.getId(), IntrinsicSkills.WATER_BREATHING.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.BAT), 20, 10, 10, 10, 10, List.of(ExtraSkills.SENSE_SOUNDWAVE.getId())));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.CAMEL), 30, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.CAT), 30, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.CHICKEN), 10, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.COW), 20, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.DONKEY), 30, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.FOX), 30, 10, 10, 20, 20));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.FROG), 20, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.HORSE), 30, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.MOOSHROOM), 30, 10, 10, 20, 20));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.MULE), 30, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.OCELOT), 30, 10, 10, 30, 30));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.PARROT), 20, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.PIG), 20, 10, 10, 10, 10));
      register(
         context,
         EntityExistenceData.getDefault(
            EntityType.getKey(EntityType.PUFFERFISH), 40, 10, 10, 50, 50, List.of(CommonSkills.POISON.getId(), IntrinsicSkills.WATER_BREATHING.getId())
         )
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.RABBIT), 20, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.SHEEP), 20, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.SKELETON_HORSE), 60, 150, 150));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.SNIFFER), 50, 100, 100, 100, 100));
      register(
         context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.SNOW_GOLEM), 80, 150, 150, List.of(ResistanceSkills.COLD_RESISTANCE.getId()))
      );
      register(
         context,
         EntityExistenceData.getDefault(EntityType.getKey(EntityType.STRIDER), 40, 20, 20, 10, 10, List.of(ResistanceSkills.HEAT_NULLIFICATION.getId()))
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.TADPOLE), 10, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.TURTLE), 30, 10, 10, 10, 10));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.VILLAGER), 50, 50, 950, 950));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.WANDERING_TRADER), 50, 50, 50, 1200, 1200));
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.ZOMBIE_HORSE), 60, 150, 150));
      List<ResourceLocation> waterBreathing = List.of(IntrinsicSkills.WATER_BREATHING.getId());
      register(
         context,
         EntityExistenceData.getDefault(
            EntityType.getKey(EntityType.COD), 10, 10, 10, 10, 10, waterBreathing, EntityType.getKey((EntityType)MonsterEntityTypes.GIANT_COD.get())
         )
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.GLOW_SQUID), 10, 10, 10, 20, 20, waterBreathing));
      register(
         context,
         EntityExistenceData.getDefault(
            EntityType.getKey(EntityType.SALMON), 10, 10, 10, 10, 10, waterBreathing, EntityType.getKey((EntityType)MonsterEntityTypes.GIANT_SALMON.get())
         )
      );
      register(
         context,
         EntityExistenceData.getDefault(EntityType.getKey(EntityType.SQUID), 10, 10, 10, 10, 10, waterBreathing, EntityType.getKey(EntityType.GLOW_SQUID))
      );
      register(context, EntityExistenceData.getDefault(EntityType.getKey(EntityType.TROPICAL_FISH), 10, 10, 10, 10, 10, waterBreathing));
   }
}
