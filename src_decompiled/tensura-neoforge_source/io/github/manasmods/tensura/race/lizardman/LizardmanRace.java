package io.github.manasmods.tensura.race.lizardman;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.LizardmanConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class LizardmanRace extends DefaultRace {
   public LizardmanRace(Difficulty difficulty) {
      super(difficulty);
   }

   public LizardmanRace() {
      this(Difficulty.HARD);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((LizardmanConfig)ConfigRegistry.getConfig(LizardmanConfig.class)).Lizardman;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DRAGONEWT.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.TRUE_DRAGONEWT.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DRAGONEWT.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.DRAGONEWT.get());
   }

   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)IntrinsicSkills.SCALE_ARMOR.get());
      return list;
   }

   public boolean canTick(ManasRaceInstance instance, LivingEntity entity) {
      return !entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE) ? false : !entity.hasInfiniteMaterials() && !entity.isSpectator();
   }

   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      if (entity.level().getBiome(entity.getOnPos()).is(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
         entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 0, false, false, false));
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, false, false, false));
         entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, false, false));
      }
   }
}
