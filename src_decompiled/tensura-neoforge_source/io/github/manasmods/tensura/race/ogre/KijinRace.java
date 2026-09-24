package io.github.manasmods.tensura.race.ogre;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.ElementalTransformSkill;
import io.github.manasmods.tensura.config.race.OgreConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.storage.TensuraStorages;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class KijinRace extends OgreRace {
   public KijinRace(Difficulty difficulty) {
      super(difficulty);
   }

   public KijinRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((OgreConfig)ConfigRegistry.getConfig(OgreConfig.class)).Kijin;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.MYSTIC_ONI.get();
   }

   @Nullable
   @Override
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.MYSTIC_ONI.get();
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.MYSTIC_ONI.get(), (ManasRace)TensuraRaces.WICKED_ONI.get());
   }

   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.OGRE.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement() {
         @Override
         public float getProgress(ManasRaceInstance instance, LivingEntity entityx) {
            int spirit = 0;
            if (entityx instanceof Player player) {
               if (player.isLocalPlayer()) {
                  spirit = ((LocalPlayer)entityx).getStats().getValue(Stats.CUSTOM.get(TensuraStats.SPIRIT_CONTRACTED_TIME));
               } else if (entityx instanceof ServerPlayer serverPlayer) {
                  spirit = serverPlayer.getStats().getValue(Stats.CUSTOM.get(TensuraStats.SPIRIT_CONTRACTED_TIME));
               }
            }

            return (float)spirit / ((OgreConfig)ConfigRegistry.getConfig(OgreConfig.class)).Kijin.spiritRequirement;
         }

         @Override
         public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entityx) {
            return Component.translatable("tensura.evolution_menu.spirit_requirement");
         }
      }, 100.0F);
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      List<Element> obtainedElements = Arrays.stream(Element.values())
         .filter(element -> TensuraStorages.getSpiritFrom(entity).getSpiritLevelId(element) > 0 && ElementalTransformSkill.getTransformSkill(element) != null)
         .toList();
      if (!obtainedElements.isEmpty()) {
         list.add(ElementalTransformSkill.getTransformSkill(obtainedElements.get(entity.getRandom().nextInt(0, obtainedElements.size()))));
      } else {
         list.add(
            ElementalTransformSkill.getTransformSkill(
               Element.getCommandSuggestElemental().get(entity.getRandom().nextInt(0, Element.getCommandSuggestElemental().size()))
            )
         );
      }

      return list;
   }
}
