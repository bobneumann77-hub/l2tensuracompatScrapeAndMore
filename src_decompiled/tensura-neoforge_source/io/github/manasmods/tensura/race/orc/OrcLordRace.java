package io.github.manasmods.tensura.race.orc;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.OrcConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Map;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class OrcLordRace extends HighOrcRace {
   public OrcLordRace(Difficulty difficulty) {
      super(difficulty);
   }

   public OrcLordRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((OrcConfig)ConfigRegistry.getConfig(OrcConfig.class)).OrcLord;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.MAJIN;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ORC_DISASTER.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ORC_DISASTER.get();
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.ORC_DISASTER.get());
   }

   @Override
   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.HIGH_ORC.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(
         new EvolutionRequirement.ItemConsumeRequirement(
            (Item)TensuraMobDropItems.ROYAL_BLOOD.get(), ((OrcConfig)ConfigRegistry.getConfig(OrcConfig.class)).OrcLord.bloodRequirement
         ),
         100.0F
      );
   }

   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)CommonSkills.SELF_REGENERATION.get());
      list.add((ManasSkill)UniqueSkills.STARVED.get());
      return list;
   }

   public boolean canTick(ManasRaceInstance instance, LivingEntity entity) {
      return !entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE) ? false : !entity.hasInfiniteMaterials() && !entity.isSpectator();
   }

   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 40, 2, false, false, false));
   }
}
