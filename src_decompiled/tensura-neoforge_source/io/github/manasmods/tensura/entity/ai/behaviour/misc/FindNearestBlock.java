package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;

public class FindNearestBlock<E extends PathfinderMob> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory((MemoryModuleType)SBLMemoryTypes.NEARBY_BLOCKS.get());
   protected BiPredicate<E, Pair<BlockPos, BlockState>> predicate = (entity, pair) -> true;
   protected BiConsumer<E, Pair<BlockPos, BlockState>> action = (entity, pair) -> {};
   protected Pair<BlockPos, BlockState> target = null;

   public FindNearestBlock<E> predicate(BiPredicate<E, Pair<BlockPos, BlockState>> predicate) {
      this.predicate = predicate;
      return this;
   }

   public FindNearestBlock<E> action(BiConsumer<E, Pair<BlockPos, BlockState>> consumer) {
      this.action = consumer;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      double closestDist = Mth.square(entity.getAttributeValue(Attributes.FOLLOW_RANGE));
      BlockPos entityPos = entity.blockPosition();
      Pair<BlockPos, BlockState> closest = null;

      for (Pair<BlockPos, BlockState> position : (List)BrainUtils.getMemory(entity, (MemoryModuleType)SBLMemoryTypes.NEARBY_BLOCKS.get())) {
         if (this.predicate.test(entity, position)) {
            double dist = entityPos.distSqr((Vec3i)position.getFirst());
            if (dist < closestDist) {
               closestDist = dist;
               closest = position;
            }
         }
      }

      this.target = closest;
      return this.target != null;
   }

   protected void start(E entity) {
      this.action.accept(entity, this.target);
   }

   protected void stop(E entity) {
      this.target = null;
   }
}
