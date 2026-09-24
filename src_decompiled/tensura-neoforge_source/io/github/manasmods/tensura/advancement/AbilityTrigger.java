package io.github.manasmods.tensura.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AbilityTrigger extends SimpleCriterionTrigger<AbilityTrigger.TriggerInstance> {
   public Codec<AbilityTrigger.TriggerInstance> codec() {
      return AbilityTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer serverPlayer, ManasSkill skill) {
      this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(serverPlayer, skill));
   }

   public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ResourceLocation> skill) implements SimpleInstance {
      public static final Codec<AbilityTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(AbilityTrigger.TriggerInstance::player),
               ResourceLocation.CODEC.optionalFieldOf("skill").forGetter(AbilityTrigger.TriggerInstance::skill)
            )
            .apply(instance, AbilityTrigger.TriggerInstance::new)
      );

      public static Criterion<AbilityTrigger.TriggerInstance> activateSpecialAbility(ResourceLocation skill) {
         return ((AbilityTrigger)TensuraCriteriaTriggers.SPECIAL_ACTIVATION.get())
            .createCriterion(new AbilityTrigger.TriggerInstance(Optional.empty(), Optional.of(skill)));
      }

      public static Criterion<AbilityTrigger.TriggerInstance> obtainAbility() {
         return ((AbilityTrigger)TensuraCriteriaTriggers.ABILITY_OBTAINED.get())
            .createCriterion(new AbilityTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<AbilityTrigger.TriggerInstance> learnAbility(ResourceLocation skill) {
         return ((AbilityTrigger)TensuraCriteriaTriggers.SKILL_LEARNT.get())
            .createCriterion(new AbilityTrigger.TriggerInstance(Optional.empty(), Optional.of(skill)));
      }

      public static Criterion<AbilityTrigger.TriggerInstance> learnBattlewill() {
         return ((AbilityTrigger)TensuraCriteriaTriggers.BATTLEWILL_LEARNT.get())
            .createCriterion(new AbilityTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<AbilityTrigger.TriggerInstance> learnMagic() {
         return ((AbilityTrigger)TensuraCriteriaTriggers.MAGIC_LEARNT.get())
            .createCriterion(new AbilityTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<AbilityTrigger.TriggerInstance> learnSkill() {
         return ((AbilityTrigger)TensuraCriteriaTriggers.SKILL_LEARNT.get())
            .createCriterion(new AbilityTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<AbilityTrigger.TriggerInstance> masterAbility(ResourceLocation skill) {
         return ((AbilityTrigger)TensuraCriteriaTriggers.SKILL_MASTERED.get())
            .createCriterion(new AbilityTrigger.TriggerInstance(Optional.empty(), Optional.of(skill)));
      }

      public static Criterion<AbilityTrigger.TriggerInstance> masterBattlewill() {
         return ((AbilityTrigger)TensuraCriteriaTriggers.BATTLEWILL_MASTERED.get())
            .createCriterion(new AbilityTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<AbilityTrigger.TriggerInstance> masterMagic() {
         return ((AbilityTrigger)TensuraCriteriaTriggers.MAGIC_MASTERED.get())
            .createCriterion(new AbilityTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<AbilityTrigger.TriggerInstance> masterSkill() {
         return ((AbilityTrigger)TensuraCriteriaTriggers.SKILL_MASTERED.get())
            .createCriterion(new AbilityTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<AbilityTrigger.TriggerInstance> masterUniqueSkill() {
         return ((AbilityTrigger)TensuraCriteriaTriggers.UNIQUE_SKILL_MASTERED.get())
            .createCriterion(new AbilityTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public boolean matches(ServerPlayer player, ManasSkill skill) {
         return this.skill().isPresent() ? skill.getRegistryName().equals(this.skill().get()) : true;
      }
   }
}
