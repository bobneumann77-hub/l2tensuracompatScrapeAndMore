package io.github.manasmods.tensura.race.harpy;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.config.race.HarpyConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.storage.Alignment;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HarpyRace extends DefaultRace {
   public HarpyRace(Difficulty difficulty) {
      super(difficulty);
      this.addAttributeModifier(
         Attributes.SAFE_FALL_DISTANCE, DEFAULT_RACE_ID, ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).Harpy.safeFalling, Operation.ADD_VALUE
      );
   }

   public HarpyRace() {
      this(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).Harpy;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.MAJIN;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.HARPY_QUEEN.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.SPIRIT_BIRD.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.HARPY_QUEEN.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.HARPY_QUEEN.get());
   }

   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = new ArrayList<>();
      list.add((ManasSkill)ExtraSkills.MAGIC_JAMMING.get());
      return list;
   }

   protected float getUpwardBoost() {
      return ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).Harpy.flightBoost;
   }

   protected int getFlightBoostCooldown() {
      return ((HarpyConfig)ConfigRegistry.getConfig(HarpyConfig.class)).Harpy.flightCooldown;
   }

   public void onActivateAbility(ManasRaceInstance instance, LivingEntity entity) {
      if (!entity.isPassenger()) {
         entity.resetFallDistance();
         instance.setCooldown(this.getFlightBoostCooldown());
         if (entity.onGround()) {
            SkillHelper.riptidePush(entity, -0.6F);
         }

         Vec3 delta = entity.getDeltaMovement();
         double dy = delta.y <= 0.0 ? this.getUpwardBoost() : delta.y + this.getUpwardBoost();
         entity.setDeltaMovement(new Vec3(delta.x() * (0.3F + this.getUpwardBoost()), dy, delta.z() * (0.3F + this.getUpwardBoost())));
         entity.hurtMarked = true;
         if (entity instanceof Player player) {
            player.startFallFlying();
         }
      }
   }
}
