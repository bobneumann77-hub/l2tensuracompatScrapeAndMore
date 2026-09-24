package io.github.manasmods.tensura.race.wight;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.config.race.WightConfig;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import java.util.List;
import java.util.Map;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class WightKingRace extends WightRace {
   public WightKingRace(Difficulty difficulty) {
      super(difficulty);
   }

   public WightKingRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((WightConfig)ConfigRegistry.getConfig(WightConfig.class)).WightKing;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.SPIRIT_SKELETON.get();
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.SPIRIT_SKELETON.get());
   }

   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.WIGHT.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement.EPRequirement(((WightConfig)ConfigRegistry.getConfig(WightConfig.class)).WightKing.epRequirement), 100.0F);
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)SpiritualMagics.CREATE_LESSER_UNDEAD.get());
      return list;
   }

   @Override
   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      if (RaceUtils.isUnderSun(entity)) {
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, true, false, true));
         entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, true, false, true));
         entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 1, true, false, true));
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 40, 1, true, false, true));
      }
   }
}
