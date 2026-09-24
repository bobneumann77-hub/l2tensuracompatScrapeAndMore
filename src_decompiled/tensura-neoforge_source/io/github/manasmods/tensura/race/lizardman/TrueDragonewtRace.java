package io.github.manasmods.tensura.race.lizardman;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.LizardmanConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Map;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class TrueDragonewtRace extends DragonewtRace {
   public TrueDragonewtRace(Difficulty difficulty) {
      super(difficulty);
   }

   public TrueDragonewtRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((LizardmanConfig)ConfigRegistry.getConfig(LizardmanConfig.class)).TrueDragonewt;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.HOLY;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DIVINE_DRAGON.get();
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.DIVINE_DRAGON.get());
   }

   @Override
   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.DRAGONEWT.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(
         new EvolutionRequirement.EPRequirement(((LizardmanConfig)ConfigRegistry.getConfig(LizardmanConfig.class)).TrueDragonewt.epRequirement), 100.0F
      );
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)IntrinsicSkills.DRAGON_SKIN.get());
      list.add((ManasSkill)IntrinsicSkills.FLAME_BREATH.get());
      list.add((ManasSkill)IntrinsicSkills.ICE_BREATH.get());
      list.add((ManasSkill)IntrinsicSkills.THUNDER_BREATH.get());
      return list;
   }

   @Override
   protected float getFlightBoost() {
      return ((LizardmanConfig)ConfigRegistry.getConfig(LizardmanConfig.class)).TrueDragonewt.flightBoost;
   }

   @Override
   protected int getFlightBoostCooldown() {
      return ((LizardmanConfig)ConfigRegistry.getConfig(LizardmanConfig.class)).TrueDragonewt.flightCooldown;
   }

   @Override
   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      if (entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE)) {
         if (!entity.hasInfiniteMaterials()) {
            if (!entity.isSpectator()) {
               if (entity.level().getBiome(entity.getOnPos()).is(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
                  entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false, false));
               }
            }
         }
      }
   }
}
