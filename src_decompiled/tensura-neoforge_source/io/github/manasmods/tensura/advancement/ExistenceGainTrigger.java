package io.github.manasmods.tensura.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;

public class ExistenceGainTrigger extends SimpleCriterionTrigger<ExistenceGainTrigger.TriggerInstance> {
   public Codec<ExistenceGainTrigger.TriggerInstance> codec() {
      return ExistenceGainTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer serverPlayer) {
      this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(serverPlayer));
   }

   public record TriggerInstance(Optional<ContextAwarePredicate> player, double existence, Optional<Double> maxEP) implements SimpleInstance {
      public static final Codec<ExistenceGainTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ExistenceGainTrigger.TriggerInstance::player),
               Codec.DOUBLE.fieldOf("ep").forGetter(ExistenceGainTrigger.TriggerInstance::existence),
               Codec.DOUBLE.optionalFieldOf("maxEP").forGetter(ExistenceGainTrigger.TriggerInstance::maxEP)
            )
            .apply(instance, ExistenceGainTrigger.TriggerInstance::new)
      );

      public static Criterion<ExistenceGainTrigger.TriggerInstance> gainExistencePoint(double ep) {
         return ((ExistenceGainTrigger)TensuraCriteriaTriggers.EXISTENCE_GAIN.get())
            .createCriterion(new ExistenceGainTrigger.TriggerInstance(Optional.empty(), ep, Optional.empty()));
      }

      public static Criterion<ExistenceGainTrigger.TriggerInstance> gainExistencePoint(double ep, double maxEP) {
         return ((ExistenceGainTrigger)TensuraCriteriaTriggers.EXISTENCE_GAIN.get())
            .createCriterion(new ExistenceGainTrigger.TriggerInstance(Optional.empty(), ep, Optional.of(maxEP)));
      }

      public boolean matches(ServerPlayer player) {
         double ep = EnergyHelper.getBaseMaxEP(player);
         return ep < this.existence ? false : this.maxEP.isEmpty() || this.maxEP.get() >= ep;
      }
   }
}
