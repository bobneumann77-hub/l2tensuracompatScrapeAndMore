package io.github.manasmods.tensura.entity.ai.behaviour.profession;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.TensuraMerchantEntity;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;

public class TradeWithMerchants extends ExtendedBehaviour<TensuraMerchantEntity> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .hasMemory(MemoryModuleType.INTERACTION_TARGET)
      .hasMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
   private Function<TensuraMerchantEntity, Float> speed = entity -> 0.75F;
   private Function<TensuraMerchantEntity, Double> engageRangeSqr = entity -> 9.0;
   private Function<TensuraMerchantEntity, Integer> approachPriority = entity -> 2;
   private BiFunction<TensuraMerchantEntity, TensuraMerchantEntity, Set<Item>> computeWillingTrades = TradeWithMerchants::figureOutWhatIAmWillingToTrade;
   private BiFunction<TensuraMerchantEntity, TensuraMerchantEntity, Boolean> shareFood = (entity, merchant) -> entity.hasExcessFood()
      && (entity.getProfession() == VillagerProfession.FARMER || merchant.wantsMoreFood());
   private BiFunction<TensuraMerchantEntity, TensuraMerchantEntity, Boolean> shareWheat = (entity, merchant) -> merchant.getProfession()
         == VillagerProfession.FARMER
      && entity.inventory.countItem(Items.WHEAT) > Items.WHEAT.getDefaultMaxStackSize() / 2;
   private Function<TensuraMerchantEntity, Boolean> shareProfessionRequests = entity -> true;
   private Set<Item> trades = Set.of();

   public TradeWithMerchants speed(Function<TensuraMerchantEntity, Float> f) {
      this.speed = f;
      return this;
   }

   public TradeWithMerchants engageRangeSqr(Function<TensuraMerchantEntity, Double> f) {
      this.engageRangeSqr = f;
      return this;
   }

   public TradeWithMerchants approachPriority(Function<TensuraMerchantEntity, Integer> f) {
      this.approachPriority = f;
      return this;
   }

   public TradeWithMerchants computeWillingTrades(BiFunction<TensuraMerchantEntity, TensuraMerchantEntity, Set<Item>> fn) {
      this.computeWillingTrades = fn;
      return this;
   }

   public TradeWithMerchants shareFood(BiFunction<TensuraMerchantEntity, TensuraMerchantEntity, Boolean> enabled) {
      this.shareFood = enabled;
      return this;
   }

   public TradeWithMerchants shareWheat(BiFunction<TensuraMerchantEntity, TensuraMerchantEntity, Boolean> enabled) {
      this.shareWheat = enabled;
      return this;
   }

   public TradeWithMerchants shareProfessionRequests(Function<TensuraMerchantEntity, Boolean> enabled) {
      this.shareProfessionRequests = enabled;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, TensuraMerchantEntity entity) {
      return BehaviorUtils.targetIsValid(entity.getBrain(), MemoryModuleType.INTERACTION_TARGET, entity.getType());
   }

   protected boolean canStillUse(ServerLevel level, TensuraMerchantEntity self, long gameTime) {
      return this.checkExtraStartConditions(level, self);
   }

   protected void start(ServerLevel level, TensuraMerchantEntity entity, long gameTime) {
      LivingEntity other = (LivingEntity)entity.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).orElse(null);
      if (other != null) {
         BehaviorUtils.lockGazeAndWalkToEachOther(entity, other, this.speed.apply(entity), this.approachPriority.apply(entity));
         this.trades = this.computeWillingTrades.apply(entity, (TensuraMerchantEntity)other);
      }
   }

   protected void tick(ServerLevel level, TensuraMerchantEntity entity, long gameTime) {
      LivingEntity other = (LivingEntity)entity.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).orElse(null);
      if (other instanceof TensuraMerchantEntity merchant) {
         if (entity.distanceToSqr(other) <= this.engageRangeSqr.apply(entity)) {
            BehaviorUtils.lockGazeAndWalkToEachOther(entity, other, this.speed.apply(entity), this.approachPriority.apply(entity));
            entity.gossip(merchant, gameTime);
            if (this.shareFood.apply(entity, merchant)) {
               throwHalfStack(entity, stack -> stack.has(DataComponents.FOOD), other);
            }

            if (this.shareWheat.apply(entity, merchant)) {
               throwHalfStack(entity, stack -> stack.is(Items.WHEAT), merchant);
            }

            if (this.shareProfessionRequests.apply(entity) && !this.trades.isEmpty() && entity.inventory.hasAnyOf(this.trades)) {
               throwHalfStack(entity, stack -> this.trades.contains(stack.getItem()), merchant);
            }
         }
      }
   }

   protected void stop(ServerLevel level, TensuraMerchantEntity entity, long gameTime) {
      entity.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
      this.trades = Set.of();
   }

   private static Set<Item> figureOutWhatIAmWillingToTrade(TensuraMerchantEntity self, TensuraMerchantEntity other) {
      ImmutableSet<Item> wantsOther = other.getProfession().requestedItems();
      ImmutableSet<Item> wantsSelf = self.getProfession().requestedItems();
      return wantsOther.stream().filter(it -> !wantsSelf.contains(it)).collect(Collectors.toUnmodifiableSet());
   }

   private static void throwHalfStack(TensuraMerchantEntity thrower, Predicate<ItemStack> allowed, LivingEntity receiver) {
      SimpleContainer inventory = thrower.inventory;
      ItemStack toThrow = ItemStack.EMPTY;
      int slot = 0;

      while (slot < inventory.getContainerSize()) {
         ItemStack stack = inventory.getItem(slot);
         if (!stack.isEmpty() && allowed.test(stack)) {
            int count = stack.getCount();
            int amount;
            if (count > stack.getMaxStackSize() / 2) {
               amount = count / 2;
            } else {
               if (count <= 30) {
                  slot++;
                  continue;
               }

               amount = count - 30;
            }

            stack.shrink(amount);
            toThrow = new ItemStack(stack.getItem(), amount);
            break;
         } else {
            slot++;
         }
      }

      if (!toThrow.isEmpty()) {
         BehaviorUtils.throwItem(thrower, toThrow, receiver.position());
      }
   }
}
