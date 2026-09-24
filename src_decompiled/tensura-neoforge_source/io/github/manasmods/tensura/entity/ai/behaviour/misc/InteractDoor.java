package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.object.TriPredicate;
import net.tslat.smartbrainlib.util.BrainUtils;

public class InteractDoor<E extends LivingEntity> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3)
      .hasMemory(MemoryModuleType.PATH)
      .usesMemories(new MemoryModuleType[]{MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_LIVING_ENTITIES});
   protected ToIntFunction<E> doorInteractionDelay = entity -> 30;
   protected ToIntFunction<E> doorHoldDelay = entity -> 20;
   protected TriPredicate<E, LivingEntity, BlockPos> holdDoorsOpenFor = (entity, other, doorPos) -> entity.getType() == other.getType()
      && doorPos.closerToCenterThan(other.position(), 2.0);
   protected int doorHoldTime = 10;
   protected int doorCloseCooldown = 10;
   protected Node lastNode = null;

   public InteractDoor<E> holdDoorsOpenFor(TriPredicate<E, LivingEntity, BlockPos> predicate) {
      this.holdDoorsOpenFor = predicate;
      return this;
   }

   public InteractDoor<E> doorInteractionDelay(ToIntFunction<E> delay) {
      this.doorInteractionDelay = delay;
      return this;
   }

   public InteractDoor<E> doorHoldDelay(ToIntFunction<E> delay) {
      this.doorHoldDelay = delay;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      Path path = (Path)BrainUtils.getMemory(entity, MemoryModuleType.PATH);
      return path != null && !path.notStarted() && !path.isDone();
   }

   protected boolean shouldKeepRunning(E entity) {
      return BrainUtils.hasMemory(entity, MemoryModuleType.PATH) && this.checkExtraStartConditions((ServerLevel)entity.level(), entity);
   }

   protected void tick(E entity) {
      ServerLevel level = (ServerLevel)entity.level();
      Path path = (Path)BrainUtils.getMemory(entity, MemoryModuleType.PATH);
      if (path != null && path.getPreviousNode() != null) {
         BlockPos prevNodePos = path.getPreviousNode().asBlockPos();
         BlockPos nextNodePos = path.getNextNode().asBlockPos();
         BlockState prevNodeBlockState = level.getBlockState(prevNodePos);
         BlockState nextNodeBlockState = level.getBlockState(nextNodePos);
         if (this.doorCloseCooldown < 0) {
            this.doorCloseCooldown = this.doorInteractionDelay.applyAsInt(entity);
            this.lastNode = path.getNextNode();
         }

         if (Objects.equals(this.lastNode, path.getNextNode()) || --this.doorCloseCooldown >= 0) {
            if (--this.doorHoldTime < 0) {
               BrainUtils.withMemory(
                  entity, MemoryModuleType.DOORS_TO_CLOSE, doorsToClose -> this.checkAndCloseDoors(level, entity, doorsToClose, prevNodePos, nextNodePos)
               );
            }

            if (isInteractableDoor(prevNodeBlockState)) {
               this.tryOpenDoor(level, entity, prevNodeBlockState, prevNodePos);
            }

            if (isInteractableDoor(nextNodeBlockState)) {
               this.tryOpenDoor(level, entity, nextNodeBlockState, nextNodePos);
            }
         }
      }
   }

   protected void tryOpenDoor(ServerLevel level, E entity, BlockState blockState, BlockPos pos) {
      DoorBlock door = (DoorBlock)blockState.getBlock();
      if (!door.isOpen(blockState)) {
         door.setOpen(entity, level, blockState, pos, true);
         this.doorHoldTime = this.doorHoldDelay.applyAsInt(entity);
         Set<GlobalPos> doorPositions = (Set<GlobalPos>)BrainUtils.getMemory(entity, MemoryModuleType.DOORS_TO_CLOSE);
         if (doorPositions == null) {
            doorPositions = new ObjectOpenHashSet();
         }

         doorPositions.add(new GlobalPos(level.dimension(), pos));
         BrainUtils.setMemory(entity, MemoryModuleType.DOORS_TO_CLOSE, doorPositions);
      }
   }

   protected void checkAndCloseDoors(ServerLevel level, E entity, Set<GlobalPos> doorsToClose, BlockPos prevNodePos, BlockPos nextNodePos) {
      Iterator<GlobalPos> iterator = doorsToClose.iterator();

      while (iterator.hasNext()) {
         GlobalPos doorLocation = iterator.next();
         BlockPos doorPos = doorLocation.pos();
         if (!doorPos.equals(prevNodePos) && !doorPos.equals(nextNodePos)) {
            if (doorLocation.dimension() == level.dimension() && doorPos.closerToCenterThan(entity.position(), 3.0)) {
               BlockState doorState = level.getBlockState(doorPos);
               if (isInteractableDoor(doorState)) {
                  DoorBlock doorBlock = (DoorBlock)doorState.getBlock();
                  if (doorBlock.isOpen(doorState)
                     && !this.shouldHoldDoorOpenForOthers(
                        entity, doorPos, (List<LivingEntity>)BrainUtils.memoryOrDefault(entity, MemoryModuleType.NEAREST_LIVING_ENTITIES, List::of)
                     )) {
                     doorBlock.setOpen(entity, level, doorState, doorPos, false);
                  }
               }

               iterator.remove();
            } else {
               iterator.remove();
            }
         }
      }
   }

   protected boolean shouldHoldDoorOpenForOthers(E entity, BlockPos doorPos, List<LivingEntity> others) {
      for (LivingEntity other : others) {
         if (this.holdDoorsOpenFor.test(entity, other, doorPos)) {
            Path path = (Path)BrainUtils.getMemory(entity, MemoryModuleType.PATH);
            if (path != null
               && !path.isDone()
               && !path.notStarted()
               && path.getPreviousNode() != null
               && (path.getPreviousNode().asBlockPos().equals(doorPos) || path.getNextNode().asBlockPos().equals(doorPos))) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isInteractableDoor(BlockState state) {
      return state.is(BlockTags.MOB_INTERACTABLE_DOORS) && state.getBlock() instanceof DoorBlock;
   }
}
