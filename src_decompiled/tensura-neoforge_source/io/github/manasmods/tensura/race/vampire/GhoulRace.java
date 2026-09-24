package io.github.manasmods.tensura.race.vampire;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.config.race.VampireConfig;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.storage.Alignment;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class GhoulRace extends DefaultRace {
   public GhoulRace(Difficulty difficulty) {
      super(difficulty);
   }

   public GhoulRace() {
      this(Difficulty.HARD);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((VampireConfig)ConfigRegistry.getConfig(VampireConfig.class)).Ghoul;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.MAJIN;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.VAMPIRE.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.VAMPIRE_LORD.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.VAMPIRE.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.VAMPIRE.get());
   }

   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = new ArrayList<>();
      list.add((ManasSkill)CommonSkills.PARALYSIS.get());
      list.add((ManasSkill)CommonSkills.STRENGTH.get());
      list.add((ManasSkill)CommonSkills.SELF_REGENERATION.get());
      return list;
   }

   public boolean canTick(ManasRaceInstance instance, LivingEntity entity) {
      return !entity.hasInfiniteMaterials() && !entity.isSpectator();
   }

   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      if (RaceUtils.isUnderSun(entity)) {
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 4, false, false, false));
         entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 4, false, false, false));
         entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 4, false, false, false));
         if (VampireRace.shouldBurn(entity)) {
            entity.setRemainingFireTicks(60);
         }
      }
   }
}
