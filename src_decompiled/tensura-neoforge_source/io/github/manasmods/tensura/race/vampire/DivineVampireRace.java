package io.github.manasmods.tensura.race.vampire;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.config.race.VampireConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import java.util.List;
import java.util.Map;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class DivineVampireRace extends VampireLordRace {
   public DivineVampireRace(Difficulty difficulty) {
      super(difficulty);
   }

   public DivineVampireRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((VampireConfig)ConfigRegistry.getConfig(VampireConfig.class)).DivineVampire;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return null;
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return null;
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of();
   }

   @Override
   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.VAMPIRE_LORD.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement.EPRequirement(((VampireConfig)ConfigRegistry.getConfig(VampireConfig.class)).DivineVampire.epRequirement), 100.0F);
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)IntrinsicSkills.DIVINE_KI_RELEASE.get());
      list.add((ManasSkill)ExtraSkills.INFINITE_REGENERATION.get());
      return list;
   }

   @Override
   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      if (entity.level().isNight() && entity.level().getMoonPhase() == 4) {
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, true, false, true));
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 40, 0, true, false, true));
      }
   }
}
