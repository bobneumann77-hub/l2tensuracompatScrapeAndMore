package io.github.manasmods.tensura.race.slime;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.config.race.SlimeConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.Nullable;

public class GodSlimeRace extends DemonSlimeRace {
   public GodSlimeRace(Difficulty difficulty) {
      super(difficulty);
   }

   public GodSlimeRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
      SlimeConfig.GodSlime config = ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).GodSlime;
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, DEFAULT_RACE_ID, config.jumpStrength, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, DEFAULT_RACE_ID, 2.0 * config.jumpStrength, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(Attributes.FALL_DAMAGE_MULTIPLIER, DEFAULT_RACE_ID, config.fallDamage, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.WIDTH_MULTIPLIER, DEFAULT_RACE_ID, this.getCustomWidth() - 1.0, Operation.ADD_VALUE);
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).GodSlime;
   }

   @Override
   protected int getMaxJumpCharge() {
      return ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).GodSlime.maxChargeTick;
   }

   @Override
   public double getCustomWidth() {
      return ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).GodSlime.width;
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
      return List.of((ManasRace)TensuraRaces.DEMON_SLIME.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement.EPRequirement(((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).GodSlime.epRequirement), 100.0F);
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)IntrinsicSkills.DIVINE_KI_RELEASE.get());
      return list;
   }
}
