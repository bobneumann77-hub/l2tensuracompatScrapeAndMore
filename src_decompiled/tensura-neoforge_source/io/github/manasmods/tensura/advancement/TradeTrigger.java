package io.github.manasmods.tensura.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.EntityPredicate.Builder;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;

public class TradeTrigger extends SimpleCriterionTrigger<TradeTrigger.TriggerInstance> {
   @NotNull
   public Codec<TradeTrigger.TriggerInstance> codec() {
      return TradeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer serverPlayer, LivingEntity npc, ItemStack stack) {
      LootContext lootContext = EntityPredicate.createContext(serverPlayer, npc);
      this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(lootContext, stack));
   }

   public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> npc, Optional<ItemPredicate> item)
      implements SimpleInstance {
      public static final Codec<TradeTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TradeTrigger.TriggerInstance::player),
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("npc").forGetter(TradeTrigger.TriggerInstance::npc),
               ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TradeTrigger.TriggerInstance::item)
            )
            .apply(instance, TradeTrigger.TriggerInstance::new)
      );

      public static Criterion<TradeTrigger.TriggerInstance> traded() {
         return ((TradeTrigger)TensuraCriteriaTriggers.TRADE.get())
            .createCriterion(new TradeTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TradeTrigger.TriggerInstance> traded(Builder builder) {
         return ((TradeTrigger)TensuraCriteriaTriggers.TRADE.get())
            .createCriterion(new TradeTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(builder)), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TradeTrigger.TriggerInstance> traded(ItemPredicate item) {
         return ((TradeTrigger)TensuraCriteriaTriggers.TRADE.get())
            .createCriterion(new TradeTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(item)));
      }

      public boolean matches(LootContext lootContext, ItemStack itemStack) {
         return this.npc.isPresent() && !this.npc.get().matches(lootContext) ? false : this.item.isEmpty() || this.item.get().test(itemStack);
      }
   }
}
