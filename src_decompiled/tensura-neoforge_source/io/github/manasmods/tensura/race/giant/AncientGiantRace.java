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
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import java.util.List;
import java.util.Map;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class AncientGiantRace extends GiantRace {
   public AncientGiantRace(Difficulty difficulty) {
      super(difficulty);
   }

   public AncientGiantRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((GiantConfig)ConfigRegistry.getConfig(GiantConfig.class)).AncientGiant;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DIVINE_GIANT.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DIVINE_GIANT.get();
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.DIVINE_GIANT.get());
   }

   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.GIANT.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      GiantConfig.AncientGiant config = ((GiantConfig)ConfigRegistry.getConfig(GiantConfig.class)).AncientGiant;
      return Map.of(
         new EvolutionRequirement.EPRequirement(config.epRequirement),
         50.0F,
         new EvolutionRequirement.ItemCarryingRequirement(Items.ANCIENT_DEBRIS, config.debrisRequirement),
         50.0F
      );
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)CommonSkills.SELF_REGENERATION.get());
      return list;
   }

   public void onRaceSet(ManasRaceInstance instance, LivingEntity living) {
      if (living instanceof Player player) {
         GiantConfig.AncientGiant config = ((GiantConfig)ConfigRegistry.getConfig(GiantConfig.class)).AncientGiant;
         player.getInventory().clearOrCountMatchingItems(stack -> stack.is(Items.ANCIENT_DEBRIS), config.debrisRequirement, player.getInventory());
      }
   }

   public boolean onAttackEntity(ManasRaceInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (source.getDirectEntity() == entity && TensuraDamageHelper.isPhysicalAttack(source)) {
         GiantConfig.AncientGiant config = ((GiantConfig)ConfigRegistry.getConfig(GiantConfig.class)).AncientGiant;
         int durabilityBreak = (int)Math.max(1.0, ((Float)amount.get()).floatValue() * config.armorDurability);

         for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack slotStack = target.getItemBySlot(slot);
            slotStack.hurtAndBreak(durabilityBreak, target, slot);
         }
      }

      return true;
   }
}
