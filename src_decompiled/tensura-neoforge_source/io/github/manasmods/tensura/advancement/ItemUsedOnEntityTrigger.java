package io.github.manasmods.tensura.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public class ItemUsedOnEntityTrigger extends SimpleCriterionTrigger<ItemUsedOnEntityTrigger.TriggerInstance> {
   public Codec<ItemUsedOnEntityTrigger.TriggerInstance> codec() {
      return ItemUsedOnEntityTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer serverPlayer, ItemStack itemStack, Entity entity) {
      ServerLevel serverLevel = serverPlayer.serverLevel();
      LootParams lootParams = new Builder(serverLevel)
         .withParameter(LootContextParams.ORIGIN, entity.position())
         .withParameter(LootContextParams.THIS_ENTITY, entity)
         .withParameter(LootContextParams.TOOL, itemStack)
         .create(LootContextParamSets.FISHING);
      LootContext lootContext = new net.minecraft.world.level.storage.loot.LootContext.Builder(lootParams).create(Optional.empty());
      this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(lootContext));
   }

   public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> location) implements SimpleInstance {
      public static final Codec<ItemUsedOnEntityTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ItemUsedOnEntityTrigger.TriggerInstance::player),
               ContextAwarePredicate.CODEC.optionalFieldOf("entity").forGetter(ItemUsedOnEntityTrigger.TriggerInstance::location)
            )
            .apply(instance, ItemUsedOnEntityTrigger.TriggerInstance::new)
      );

      private static ItemUsedOnEntityTrigger.TriggerInstance getInstance(
         net.minecraft.advancements.critereon.EntityPredicate.Builder entityBuilder, net.minecraft.advancements.critereon.ItemPredicate.Builder itemBuild
      ) {
         ContextAwarePredicate contextAwarePredicate = ContextAwarePredicate.create(
            new LootItemCondition[]{
               LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, entityBuilder).build(), MatchTool.toolMatches(itemBuild).build()
            }
         );
         return new ItemUsedOnEntityTrigger.TriggerInstance(Optional.empty(), Optional.of(contextAwarePredicate));
      }

      public static Criterion<ItemUsedOnEntityTrigger.TriggerInstance> itemUsedOnEntity(
         net.minecraft.advancements.critereon.EntityPredicate.Builder entityBuilder, net.minecraft.advancements.critereon.ItemPredicate.Builder builder2
      ) {
         return ((ItemUsedOnEntityTrigger)TensuraCriteriaTriggers.ITEM_USED_ON_ENTITY.get()).createCriterion(getInstance(entityBuilder, builder2));
      }

      public boolean matches(LootContext lootContext) {
         return this.location.isEmpty() || this.location.get().matches(lootContext);
      }

      public void validate(CriterionValidator criterionValidator) {
         super.validate(criterionValidator);
         this.location.ifPresent(contextAwarePredicate -> criterionValidator.validate(contextAwarePredicate, LootContextParamSets.FISHING, ".location"));
      }
   }
}
