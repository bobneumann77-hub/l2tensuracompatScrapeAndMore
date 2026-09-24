package io.github.manasmods.tensura.race.wight;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.config.race.WightConfig;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.race.vampire.VampireRace;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.storage.Alignment;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class WightRace extends DefaultRace {
   public WightRace(Difficulty difficulty) {
      super(difficulty);
   }

   public WightRace() {
      this(Difficulty.HARD);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((WightConfig)ConfigRegistry.getConfig(WightConfig.class)).Wight;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.MAJIN;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.WIGHT_KING.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.SPIRIT_SKELETON.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.WIGHT_KING.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasRace> list = new ArrayList<>();
      list.add((ManasRace)TensuraRaces.HUMAN.get());
      list.add((ManasRace)TensuraRaces.WIGHT_KING.get());
      return list;
   }

   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get());
      list.add((ManasSkill)ResistanceSkills.PAIN_NULLIFICATION.get());
      return list;
   }

   public boolean onBeingTargeted(ManasRaceInstance instance, Changeable<LivingEntity> target, LivingEntity owner) {
      if (RaceUtils.isUndead(owner)) {
         DamageSource source = owner.getLastDamageSource();
         if (source == null) {
            target.set(null);
         } else if (source.getEntity() != target.get()) {
            target.set(null);
         }
      }

      return true;
   }

   public boolean canTick(ManasRaceInstance instance, LivingEntity entity) {
      return !entity.hasInfiniteMaterials() && !entity.isSpectator();
   }

   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      if (RaceUtils.isUnderSun(entity)) {
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 2, true, false, true));
         entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2, true, false, true));
         entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 2, true, false, true));
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 40, 2, true, false, true));
         if (VampireRace.shouldBurn(entity)) {
            entity.setRemainingFireTicks(60);
         }
      }
   }
}
