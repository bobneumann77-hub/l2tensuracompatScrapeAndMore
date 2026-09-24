package io.github.manasmods.tensura.race.giant;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.GiantConfig;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import java.util.List;
import java.util.Map;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DivineGiantRace extends AncientGiantRace {
   public DivineGiantRace(Difficulty difficulty) {
      super(difficulty);
   }

   public DivineGiantRace() {
      this(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((GiantConfig)ConfigRegistry.getConfig(GiantConfig.class)).DivineGiant;
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
      return List.of((ManasRace)TensuraRaces.ANCIENT_GIANT.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement.EPRequirement(((GiantConfig)ConfigRegistry.getConfig(GiantConfig.class)).DivineGiant.epRequirement), 100.0F);
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)IntrinsicSkills.DIVINE_KI_RELEASE.get());
      list.add((ManasSkill)IntrinsicSkills.TITANIFICATION.get());
      list.add((ManasSkill)ExtraSkills.ULTRASPEED_REGENERATION.get());
      return list;
   }

   @Override
   public boolean onAttackEntity(ManasRaceInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (source.getDirectEntity() == entity && TensuraDamageHelper.isPhysicalAttack(source)) {
         int durabilityBreak = (int)Math.max(
            1.0, ((Float)amount.get()).floatValue() * ((GiantConfig)ConfigRegistry.getConfig(GiantConfig.class)).DivineGiant.armorDurability
         );

         for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack slotStack = target.getItemBySlot(slot);
            slotStack.hurtAndBreak(durabilityBreak, target, slot);
         }
      }

      return true;
   }
}
