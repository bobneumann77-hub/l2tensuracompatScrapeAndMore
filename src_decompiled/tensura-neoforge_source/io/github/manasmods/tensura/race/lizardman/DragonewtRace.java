package io.github.manasmods.tensura.race.lizardman;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.config.race.LizardmanConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Map;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DragonewtRace extends LizardmanRace {
   public DragonewtRace(Difficulty difficulty) {
      super(difficulty);
   }

   public DragonewtRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((LizardmanConfig)ConfigRegistry.getConfig(LizardmanConfig.class)).Dragonewt;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.TRUE_DRAGONEWT.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return null;
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.TRUE_DRAGONEWT.get());
   }

   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.LIZARDMAN.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement.ItemConsumeRequirement((Item)TensuraMobDropItems.DRAGON_ESSENCE.get(), 10), 100.0F);
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)IntrinsicSkills.DRAGON_EYE.get());
      list.add((ManasSkill)IntrinsicSkills.DRAGON_EAR.get());
      list.add((ManasSkill)IntrinsicSkills.DRAGON_MODE.get());
      switch (RandomSource.create().nextIntBetweenInclusive(1, 3)) {
         case 1:
            list.add((ManasSkill)IntrinsicSkills.FLAME_BREATH.get());
            break;
         case 2:
            list.add((ManasSkill)IntrinsicSkills.ICE_BREATH.get());
            break;
         case 3:
            list.add((ManasSkill)IntrinsicSkills.THUNDER_BREATH.get());
      }

      list.add((ManasSkill)ResistanceSkills.MAGIC_RESISTANCE.get());
      return list;
   }

   @Override
   public List<ManasSkill> getRenderingIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)IntrinsicSkills.DRAGON_EYE.get());
      list.add((ManasSkill)IntrinsicSkills.DRAGON_EAR.get());
      list.add((ManasSkill)IntrinsicSkills.DRAGON_MODE.get());
      list.add((ManasSkill)ResistanceSkills.MAGIC_RESISTANCE.get());
      switch (entity.tickCount / 20 % 3) {
         case 0:
            list.add((ManasSkill)IntrinsicSkills.FLAME_BREATH.get());
            break;
         case 1:
            list.add((ManasSkill)IntrinsicSkills.ICE_BREATH.get());
            break;
         case 2:
            list.add((ManasSkill)IntrinsicSkills.THUNDER_BREATH.get());
      }

      return list;
   }

   public boolean isIntrinsicSkill(ManasRaceInstance instance, LivingEntity entity, ManasSkill skill) {
      if (skill == IntrinsicSkills.FLAME_BREATH.get()) {
         return true;
      } else if (skill == IntrinsicSkills.ICE_BREATH.get()) {
         return true;
      } else {
         return skill == IntrinsicSkills.THUNDER_BREATH.get() ? true : super.isIntrinsicSkill(instance, entity, skill);
      }
   }

   protected float getFlightBoost() {
      return ((LizardmanConfig)ConfigRegistry.getConfig(LizardmanConfig.class)).Dragonewt.flightBoost;
   }

   protected int getFlightBoostCooldown() {
      return ((LizardmanConfig)ConfigRegistry.getConfig(LizardmanConfig.class)).Dragonewt.flightCooldown;
   }

   public void onActivateAbility(ManasRaceInstance instance, LivingEntity entity) {
      if (!entity.isPassenger()) {
         if (entity instanceof Player player) {
            if (player.getFoodData().getFoodLevel() > 0 || player.getAbilities().invulnerable) {
               if (!player.getAbilities().invulnerable) {
                  player.getFoodData().addExhaustion(2.0F);
               }

               if (player.onGround()) {
                  Vec3 delta = player.getDeltaMovement();
                  double dy = delta.y <= 0.0 ? 0.5 : delta.y + 0.5;
                  player.setDeltaMovement(new Vec3(delta.x(), dy, delta.z()));
               }

               SkillHelper.riptidePush(player, this.getFlightBoost());
               instance.setCooldown(this.getFlightBoostCooldown());
               player.hurtMarked = true;
               player.hasImpulse = true;
               player.startFallFlying();
            }
         }
      }
   }

   @Override
   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      if (entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE)) {
         if (!entity.hasInfiniteMaterials()) {
            if (!entity.isSpectator()) {
               if (entity.level().getBiome(entity.getOnPos()).is(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
                  entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false, false));
                  entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false, false));
               }
            }
         }
      }
   }
}
