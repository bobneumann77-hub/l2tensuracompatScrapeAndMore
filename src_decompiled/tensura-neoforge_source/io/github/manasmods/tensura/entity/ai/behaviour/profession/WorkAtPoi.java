package io.github.manasmods.tensura.entity.ai.behaviour.profession;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.template.TensuraMerchantEntity;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.apache.commons.lang3.function.TriFunction;

public class WorkAtPoi<E extends TensuraMerchantEntity> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2).usesMemory(MemoryModuleType.LOOK_TARGET).hasMemory(MemoryModuleType.JOB_SITE);
   private Function<E, Double> distanceRequired = e -> 2.0;
   private Function<E, Integer> checkCooldown = e -> 300;
   private Function<E, Integer> randomGate = e -> 2;
   private TriFunction<BlockPos, ServerLevel, E, Boolean> useWorkstation = (pos, level, entity) -> {
      if (entity.getProfession() == VillagerProfession.FARMER) {
         makeBread(entity, 32);
         useComposter(pos, level, entity);
      }

      placePickedItems(pos, level, entity);
      return true;
   };
   private Consumer<E> playWorkSound = entity -> entity.makeSound(entity.getProfession().workSound());
   private Predicate<E> shouldRestock = TensuraMerchantEntity::shouldRestock;
   private Consumer<E> doRestock = TensuraMerchantEntity::restock;
   private long lastCheck = 0L;

   public WorkAtPoi<E> distance(Function<E, Double> dist) {
      this.distanceRequired = dist;
      return this;
   }

   public WorkAtPoi<E> cooldown(Function<E, Integer> ticks) {
      this.checkCooldown = ticks;
      return this;
   }

   public WorkAtPoi<E> randomGate(Function<E, Integer> oneInN) {
      this.randomGate = oneInN;
      return this;
   }

   public WorkAtPoi<E> onWorkSound(Consumer<E> sound) {
      this.playWorkSound = sound;
      return this;
   }

   public WorkAtPoi<E> onUseWorkstation(TriFunction<BlockPos, ServerLevel, E, Boolean> function) {
      this.useWorkstation = function;
      return this;
   }

   public WorkAtPoi<E> shouldRestock(Predicate<E> pred) {
      this.shouldRestock = pred;
      return this;
   }

   public WorkAtPoi<E> restock(Consumer<E> action) {
      this.doRestock = action;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      long now = level.getGameTime();
      if (now - this.lastCheck < this.checkCooldown.apply(entity).intValue()) {
         return false;
      } else {
         int n = Math.max(1, this.randomGate.apply(entity));
         if (level.random.nextInt(n) != 0) {
            return false;
         } else {
            this.lastCheck = now;
            GlobalPos site = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.JOB_SITE);
            if (site == null) {
               return false;
            } else {
               return site.dimension() != level.dimension() ? false : site.pos().closerToCenterThan(entity.position(), this.distanceRequired.apply(entity));
            }
         }
      }
   }

   protected void start(E entity) {
      ServerLevel level = (ServerLevel)entity.level();
      BrainUtils.setMemory(entity, MemoryModuleType.LAST_WORKED_AT_POI, level.getGameTime());
      GlobalPos site = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.JOB_SITE);
      if (site != null) {
         BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(site.pos()));
         if ((Boolean)this.useWorkstation.apply(site.pos(), level, entity)) {
            this.playWorkSound.accept(entity);
            if (this.shouldRestock.test(entity)) {
               this.doRestock.accept(entity);
            }
         }
      }
   }

   protected boolean canStillUse(ServerLevel level, E entity, long gameTime) {
      GlobalPos job = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.JOB_SITE);
      if (job == null) {
         return false;
      } else {
         return job.dimension() != level.dimension() ? false : job.pos().closerToCenterThan(entity.position(), this.distanceRequired.apply(entity));
      }
   }

   public static void placePickedItems(BlockPos pos, ServerLevel level, TensuraMerchantEntity merchant) {
      if (merchant.shouldDepositPickedItems()) {
         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               for (int k = -1; k <= 1; k++) {
                  BlockPos targetPos = pos.offset(i, j, k);
                  if (level.getBlockState(targetPos).is(TensuraBlockTags.OPENABLE_BY_NPC)) {
                     DepositPickedItems.depositItem(level, merchant, targetPos);
                  }
               }
            }
         }
      }
   }

   public static void useComposter(BlockPos pos, ServerLevel level, TensuraMerchantEntity merchant) {
      List<Item> COMPOSTABLE_ITEMS = ImmutableList.of(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, (Item)TensuraMaterialItems.HIPOKUTE_SEEDS.get());
      BlockState state = level.getBlockState(pos);
      if ((Integer)state.getValue(ComposterBlock.LEVEL) == 8) {
         state = ComposterBlock.extractProduce(merchant, state, level, pos);
      }

      int i = 32;
      int[] array = new int[COMPOSTABLE_ITEMS.size()];
      SimpleContainer container = merchant.inventory;
      int k = container.getContainerSize();
      BlockState composter = state;

      for (int l = k - 1; l >= 0 && i > 0; l--) {
         ItemStack itemStack = container.getItem(l);
         int m = COMPOSTABLE_ITEMS.indexOf(itemStack.getItem());
         if (m != -1) {
            int n = itemStack.getCount();
            int o = array[m] + n;
            array[m] = o;
            int p = Math.min(Math.min(o - 10, i), n);
            if (p > 0) {
               i -= p;

               for (int q = 0; q < p; q++) {
                  composter = ComposterBlock.insertItem(merchant, composter, level, itemStack, pos);
                  if ((Integer)composter.getValue(ComposterBlock.LEVEL) == 7) {
                     level.levelEvent(1500, pos, composter != state ? 1 : 0);
                  } else if ((Integer)state.getValue(ComposterBlock.LEVEL) == 8) {
                     state = ComposterBlock.extractProduce(merchant, state, level, pos);
                  }
               }
            }
         }
      }

      level.levelEvent(1500, pos, composter != state ? 1 : 0);
   }

   public static void makeBread(TensuraMerchantEntity merchant, int maxBreadAmount) {
      SimpleContainer container = merchant.inventory;
      if (container.countItem(Items.BREAD) <= maxBreadAmount) {
         int wheat = container.countItem(Items.WHEAT);
         int bread = Math.min(maxBreadAmount, wheat / 3);
         if (bread != 0) {
            int m = bread * 3;
            container.removeItemType(Items.WHEAT, m);
            ItemStack itemStack = container.addItem(new ItemStack(Items.BREAD, bread));
            if (!itemStack.isEmpty()) {
               merchant.spawnAtLocation(itemStack, 0.5F);
            }
         }
      }
   }
}
