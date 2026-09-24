package io.github.manasmods.tensura.race.demon;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.config.race.DaemonConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LesserDaemonRace extends DefaultRace {
   public LesserDaemonRace(Difficulty difficulty) {
      super(difficulty);
   }

   public LesserDaemonRace() {
      this(Difficulty.INTERMEDIATE);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((DaemonConfig)ConfigRegistry.getConfig(DaemonConfig.class)).LesserDaemon;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.MAJIN;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.GREATER_DAEMON.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.ARCH_DAEMON.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.GREATER_DAEMON.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.GREATER_DAEMON.get());
   }

   public Pair<ResourceKey<Level>, BlockState> getRespawnDimension(ManasRaceInstance instance, LivingEntity owner) {
      return Pair.of(TensuraDimensions.HELL, Blocks.STONE.defaultBlockState());
   }

   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = new ArrayList<>();
      list.add((ManasSkill)ResistanceSkills.MAGIC_RESISTANCE.get());
      list.add((ManasSkill)IntrinsicSkills.POSSESSION.get());
      return list;
   }

   @Override
   public List<TensuraSkill> getIntrinsicLearnable(ManasRaceInstance instance, LivingEntity entity) {
      return ((DaemonConfig)ConfigRegistry.getConfig(DaemonConfig.class))
         .LesserDaemon
         .learnableMagics
         .stream()
         .map(id -> (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.parse(id)))
         .filter(skill -> skill instanceof TensuraSkill)
         .map(skill -> (TensuraSkill)skill)
         .toList();
   }

   @Override
   public void gainIntrinsicLearnable(ManasRaceInstance instance, LivingEntity entity) {
      if (!entity.level().getGameRules().getBoolean(TensuraGameRules.DISABLE_DAEMON_AUTO_MAGIC)) {
         super.gainIntrinsicLearnable(instance, entity);
      }
   }

   public void onActivateAbility(ManasRaceInstance instance, LivingEntity entity) {
      if (entity instanceof Player player) {
         if (!player.isSpectator() && !player.isCreative()) {
            Level level = player.level();
            if (player.getAbilities().mayfly) {
               if (RaceUtils.canStillFly(player, true, true, false)) {
                  return;
               }

               player.getAbilities().mayfly = false;
               player.getAbilities().flying = false;
            } else {
               player.getAbilities().mayfly = true;
               player.getAbilities().flying = true;
            }

            player.onUpdateAbilities();
            level.playSound(
               null, player.getX(), player.getY(), player.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         }
      }
   }
}
