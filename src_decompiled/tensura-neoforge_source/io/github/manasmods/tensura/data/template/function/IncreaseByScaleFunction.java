package io.github.manasmods.tensura.data.template.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.registry.data.TensuraLootFunctions;
import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

public class IncreaseByScaleFunction extends LootItemConditionalFunction {
   public static final MapCodec<IncreaseByScaleFunction> CODEC = RecordCodecBuilder.mapCodec(
      instance -> commonFields(instance)
         .and(
            instance.group(
               NumberProviders.CODEC.fieldOf("count").forGetter(enchantedCountIncreaseFunction -> enchantedCountIncreaseFunction.value),
               Codec.INT.optionalFieldOf("limit", 0).forGetter(enchantedCountIncreaseFunction -> enchantedCountIncreaseFunction.limit)
            )
         )
         .apply(instance, IncreaseByScaleFunction::new)
   );
   private final NumberProvider value;
   private final int limit;

   IncreaseByScaleFunction(List<LootItemCondition> list, NumberProvider numberProvider, int i) {
      super(list);
      this.value = numberProvider;
      this.limit = i;
   }

   public LootItemFunctionType<IncreaseByScaleFunction> getType() {
      return (LootItemFunctionType<IncreaseByScaleFunction>)TensuraLootFunctions.INCREASE_BY_SCALE.get();
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(ImmutableSet.of(LootContextParams.THIS_ENTITY), this.value.getReferencedContextParams());
   }

   private boolean hasLimit() {
      return this.limit > 0;
   }

   public ItemStack run(ItemStack itemStack, LootContext lootContext) {
      Entity entity = (Entity)lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
      if (entity instanceof LivingEntity livingEntity) {
         float f = (float)livingEntity.getAttributeValue(Attributes.SCALE) * this.value.getFloat(lootContext);
         itemStack.grow(Math.round(f));
         if (this.hasLimit()) {
            itemStack.limitSize(this.limit);
         }
      }

      return itemStack;
   }

   public static IncreaseByScaleFunction.Builder scaleMultiplier(NumberProvider numberProvider) {
      return new IncreaseByScaleFunction.Builder(numberProvider);
   }

   public static class Builder extends net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.Builder<IncreaseByScaleFunction.Builder> {
      private final NumberProvider count;
      private int limit = 0;

      public Builder(NumberProvider numberProvider) {
         this.count = numberProvider;
      }

      @NotNull
      protected IncreaseByScaleFunction.Builder getThis() {
         return this;
      }

      public IncreaseByScaleFunction.Builder setLimit(int i) {
         this.limit = i;
         return this;
      }

      public LootItemFunction build() {
         return new IncreaseByScaleFunction(this.getConditions(), this.count, this.limit);
      }
   }
}
