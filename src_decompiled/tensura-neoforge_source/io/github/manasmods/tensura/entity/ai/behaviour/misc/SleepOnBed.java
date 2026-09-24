package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class SleepOnBed<E extends LivingEntity> extends ExtendedBehaviour<E> {
   private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
      new Pair[]{
         Pair.of(MemoryModuleType.HOME, MemoryStatus.REGISTERED),
         Pair.of(MemoryModuleType.LAST_WOKEN, MemoryStatus.REGISTERED),
         Pair.of(MemoryModuleType.LAST_SLEPT, MemoryStatus.REGISTERED)
      }
   );
   public static final int COOLDOWN_AFTER_BEING_WOKEN = 40;
   private long nextSleepTime;
   protected BiPredicate<E, GlobalPos> shouldSleep = (entity, pos) -> pos.pos().closerToCenterThan(entity.position(), 3.0)
      && isValidBedPosition(entity.level().getBlockState(pos.pos()));
   protected BiPredicate<E, GlobalPos> shouldContinueSleeping = (entity, pos) -> {
      BlockPos bed = pos.pos();
      return entity.getBrain().isActive(Activity.REST) && entity.getY() > bed.getY() + 0.1 && bed.closerToCenterThan(entity.position(), 1.5);
   };

   public SleepOnBed() {
      this.noTimeout();
   }

   public SleepOnBed<E> shouldSleep(BiPredicate<E, GlobalPos> canStart) {
      this.shouldSleep = canStart;
      return this;
   }

   public SleepOnBed<E> shouldContinueSleeping(BiPredicate<E, GlobalPos> canContinue) {
      this.shouldContinueSleeping = canContinue;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (entity.isPassenger()) {
         return false;
      }

      GlobalPos home = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.HOME);
      if (home == null) {
         return false;
      }

      if (level.dimension() != home.dimension()) {
         return false;
      }

      Long lastWoken = (Long)BrainUtils.getMemory(entity, MemoryModuleType.LAST_WOKEN);
      if (lastWoken != null) {
         long sinceLastWoken = level.getGameTime() - lastWoken;
         if (sinceLastWoken > 0L && sinceLastWoken < 40L) {
            return false;
         }
      }

      return this.shouldSleep.test(entity, home);
   }

   protected boolean shouldKeepRunning(E entity) {
      GlobalPos bed = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.HOME);
      return bed == null ? false : this.shouldContinueSleeping.test(entity, bed);
   }

   protected void start(E entity) {
      if (entity.level().getGameTime() > this.nextSleepTime) {
         BrainUtils.withMemory(entity, MemoryModuleType.DOORS_TO_CLOSE, doorsToClose -> this.checkAndCloseDoors(entity.level(), entity, doorsToClose));
         entity.startSleeping(((GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.HOME)).pos());
      }
   }

   protected void checkAndCloseDoors(Level level, E entity, Set<GlobalPos> doorsToClose) {
      Iterator<GlobalPos> iterator = doorsToClose.iterator();

      while (iterator.hasNext()) {
         GlobalPos doorLocation = iterator.next();
         BlockPos doorPos = doorLocation.pos();
         if (doorLocation.dimension() == level.dimension() && doorPos.closerToCenterThan(entity.position(), 3.0)) {
            BlockState doorState = level.getBlockState(doorPos);
            if (InteractDoor.isInteractableDoor(doorState)) {
               DoorBlock doorBlock = (DoorBlock)doorState.getBlock();
               if (doorBlock.isOpen(doorState)) {
                  doorBlock.setOpen(entity, level, doorState, doorPos, false);
               }
            }

            iterator.remove();
         } else {
            iterator.remove();
         }
      }
   }

   protected void stop(E entity) {
      if (entity.isSleeping()) {
         entity.stopSleeping();
         this.nextSleepTime = entity.level().getGameTime() + 40L;
      }
   }

   public static boolean isValidBedPosition(BlockState bed) {
      return bed.is(BlockTags.BEDS) && bed.getValue(BedBlock.PART) == BedPart.HEAD && !(Boolean)bed.getValue(BedBlock.OCCUPIED);
   }
}
