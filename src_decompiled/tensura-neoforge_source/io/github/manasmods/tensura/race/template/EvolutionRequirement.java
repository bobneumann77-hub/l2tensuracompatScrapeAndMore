package io.github.manasmods.tensura.race.template;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class EvolutionRequirement {
   public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
      return 0.0F;
   }

   public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
      return Component.empty();
   }

   public static class AbilityRequirement extends EvolutionRequirement {
      private final boolean mastered;
      private final ManasSkill requirementAbility;

      public AbilityRequirement(ManasSkill requirementAbility, boolean mastered) {
         this.requirementAbility = requirementAbility;
         this.mastered = mastered;
      }

      @Override
      public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
         if (this.isMastered()) {
            return SkillUtils.isSkillMastered(entity, this.getRequirementAbility()) ? 1.0F : 0.0F;
         } else {
            return SkillUtils.hasSkill(entity, this.getRequirementAbility()) ? 1.0F : 0.0F;
         }
      }

      @Override
      public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
         Component name = this.getRequirementAbility().getChatDisplayName(true);
         return this.isMastered()
            ? Component.translatable("tensura.evolution_menu.mastery_requirement", new Object[]{name})
            : Component.translatable("tensura.evolution_menu.acquire_requirement", new Object[]{name});
      }

      @Generated
      public boolean isMastered() {
         return this.mastered;
      }

      @Generated
      public ManasSkill getRequirementAbility() {
         return this.requirementAbility;
      }
   }

   public static class AwakenRequirement extends EvolutionRequirement {
      @Override
      public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         return !existence.isTrueDemonLord() && !existence.isTrueHero() ? 0.0F : 1.0F;
      }

      @Override
      public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
         return Component.translatable(
            "tensura.evolution_menu.awaken_requirement",
            new Object[]{
               Component.translatable("tensura.evolve.demon_lord").withStyle(ChatFormatting.DARK_PURPLE),
               Component.translatable("tensura.evolve.hero").withStyle(ChatFormatting.GOLD)
            }
         );
      }
   }

   public static class BossRequirement extends EvolutionRequirement {
      private final int requirement;

      public BossRequirement(int requirement) {
         this.requirement = requirement;
      }

      @Override
      public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
         return entity instanceof Player player ? (float)TensuraStats.getBossDefeated(player) / this.requirement : 0.0F;
      }

      @Override
      public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
         return Component.translatable("tensura.evolution_menu.boss_kill_requirement", new Object[]{this.getRequirement()});
      }

      @Generated
      public int getRequirement() {
         return this.requirement;
      }
   }

   public static class EPRequirement extends EvolutionRequirement {
      private final double requirement;

      public EPRequirement(double requirement) {
         this.requirement = requirement;
      }

      @Override
      public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
         return (float)(EnergyHelper.getBaseMaxEP(entity) / this.getRequirement());
      }

      @Override
      public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
         return Component.translatable("tensura.evolution_menu.ep_requirement", new Object[]{this.getRequirement()});
      }

      @Generated
      public double getRequirement() {
         return this.requirement;
      }
   }

   public static class ItemCarryingRequirement extends EvolutionRequirement {
      private final int requirement;
      private final Item requirementItem;

      public ItemCarryingRequirement(Item item, int requirement) {
         this.requirementItem = item;
         this.requirement = requirement;
      }

      @Override
      public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
         int essence = 0;
         if (entity instanceof Player player) {
            essence = player.getInventory().countItem(this.requirementItem);
         }

         return (float)essence / this.getRequirement();
      }

      @Override
      public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
         return Component.translatable(
            "tensura.evolution_menu.carrying_requirement", new Object[]{this.getRequirement(), this.getRequirementItem().getDefaultInstance().getDisplayName()}
         );
      }

      @Generated
      public int getRequirement() {
         return this.requirement;
      }

      @Generated
      public Item getRequirementItem() {
         return this.requirementItem;
      }
   }

   public static class ItemConsumeRequirement extends EvolutionRequirement {
      private final int requirement;
      private final Item requirementItem;

      public ItemConsumeRequirement(Item item, int requirement) {
         this.requirementItem = item;
         this.requirement = requirement;
      }

      @Override
      public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
         int essence = 0;
         if (entity instanceof Player player) {
            if (player.isLocalPlayer()) {
               essence = ((LocalPlayer)entity).getStats().getValue(Stats.ITEM_USED.get(this.getRequirementItem()));
            } else {
               essence = ((ServerPlayer)entity).getStats().getValue(Stats.ITEM_USED.get(this.getRequirementItem()));
            }
         }

         return (float)essence / this.getRequirement();
      }

      @Override
      public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
         return Component.translatable(
            "tensura.evolution_menu.consume_requirement", new Object[]{this.getRequirement(), this.getRequirementItem().getDefaultInstance().getDisplayName()}
         );
      }

      @Generated
      public int getRequirement() {
         return this.requirement;
      }

      @Generated
      public Item getRequirementItem() {
         return this.requirementItem;
      }
   }

   public static class NamedRequirement extends EvolutionRequirement {
      @Override
      public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         return existence.getName() != null ? 1.0F : 0.0F;
      }

      @Override
      public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
         return Component.translatable("tensura.evolution_menu.name_requirement");
      }
   }

   public static class PhysicalBodyRequirement extends EvolutionRequirement {
      @Override
      public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         return !existence.isSpiritualForm() ? 1.0F : 0.0F;
      }

      @Override
      public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
         return Component.translatable("tensura.evolution_menu.physical_body_requirement");
      }
   }
}
