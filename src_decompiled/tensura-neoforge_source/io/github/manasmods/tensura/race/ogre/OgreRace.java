package io.github.manasmods.tensura.race.ogre;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.race.OgreConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class OgreRace extends DefaultRace {
   public OgreRace(Difficulty difficulty) {
      super(difficulty);
   }

   public OgreRace() {
      this(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((OgreConfig)ConfigRegistry.getConfig(OgreConfig.class)).Ogre;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ENLIGHTENED_OGRE.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.SPIRIT_ONI.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.KIJIN.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasRace> list = new ArrayList<>();
      list.add((ManasRace)TensuraRaces.ENLIGHTENED_OGRE.get());
      list.add((ManasRace)TensuraRaces.KIJIN.get());
      return list;
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(
         new EvolutionRequirement() {
            @Override
            public float getProgress(ManasRaceInstance instance, LivingEntity entityx) {
               return TensuraStorages.getSpiritFrom(entityx).isColossusPassed() ? 1.0F : 0.0F;
            }

            @Override
            public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entityx) {
               return Component.translatable(
                  "tensura.evolution_menu.battle_mob_requirement", new Object[]{((EntityType)MonsterEntityTypes.ELEMENTAL_COLOSSUS.get()).getDescription()}
               );
            }
         },
         100.0F
      );
   }

   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = new ArrayList<>();
      list.add((ManasSkill)CommonSkills.STRENGTH.get());
      return list;
   }

   @Override
   public boolean hasGuaranteeElemental() {
      return true;
   }

   @Override
   public double getElementalSpiritsChance(Element elemental, SpiritualMagic.SpiritLevel level) {
      if (elemental.equals(Element.FLAME)) {
         return level.equals(SpiritualMagic.SpiritLevel.LESSER) ? 100.0 : super.getElementalSpiritsChance(elemental, level) * 2.0;
      } else {
         return super.getElementalSpiritsChance(elemental, level);
      }
   }
}
