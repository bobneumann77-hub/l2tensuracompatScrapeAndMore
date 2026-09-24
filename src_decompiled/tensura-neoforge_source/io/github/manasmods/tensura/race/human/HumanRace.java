package io.github.manasmods.tensura.race.human;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.tensura.config.race.HumanConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class HumanRace extends DefaultRace {
   public HumanRace(Difficulty difficulty) {
      super(difficulty);
   }

   public HumanRace() {
      this(Difficulty.HARD);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((HumanConfig)ConfigRegistry.getConfig(HumanConfig.class)).Human;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ENLIGHTENED_HUMAN.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.HUMAN_SAINT.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ENLIGHTENED_HUMAN.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasRace> list = new ArrayList<>();
      list.add((ManasRace)TensuraRaces.ENLIGHTENED_HUMAN.get());
      list.add((ManasRace)TensuraRaces.VAMPIRE.get());
      return list;
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement() {
         @Override
         public float getProgress(ManasRaceInstance instance, LivingEntity entityx) {
            float chance = 0.0F;
            if (entityx.hasEffect(MobEffects.WEAKNESS)) {
               chance += 0.2F;
            }

            if (entityx.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
               chance += 0.2F;
            }

            if (entityx.hasEffect(MobEffects.FIRE_RESISTANCE)) {
               chance += 0.2F;
            }

            MobEffectInstance regeneration = entityx.getEffect(MobEffects.REGENERATION);
            if (regeneration != null && regeneration.getAmplifier() >= 1) {
               chance += 0.2F;
            }

            MobEffectInstance absorption = entityx.getEffect(MobEffects.ABSORPTION);
            if (absorption != null && absorption.getAmplifier() >= 3) {
               chance += 0.2F;
            }

            return chance;
         }

         @Override
         public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entityx) {
            return Component.translatable("tensura.evolution_menu.cure_requirement");
         }
      }, 100.0F);
   }
}
