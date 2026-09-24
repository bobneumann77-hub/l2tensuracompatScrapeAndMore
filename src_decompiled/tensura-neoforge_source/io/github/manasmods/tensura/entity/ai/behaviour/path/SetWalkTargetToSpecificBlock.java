package io.github.manasmods.tensura.entity.ai.behaviour.path;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class SetWalkTargetToSpecificBlock<E extends PathfinderMob> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .noMemory(MemoryModuleType.WALK_TARGET)
      .usesMemories(new MemoryModuleType[]{MemoryModuleType.LOOK_TARGET});
   protected Function<E, BlockPos> targetFunction = owner -> BlockPos.ZERO;
   protected BiFunction<E, BlockPos, Float> speedMod = (owner, pos) -> 1.0F;
   protected BiFunction<E, BlockPos, Integer> closeEnoughDist = (entity, pos) -> 2;

   public SetWalkTargetToSpecificBlock<E> setTargetPos(Function<E, BlockPos> function) {
      this.targetFunction = function;
      return this;
   }

   public SetWalkTargetToSpecificBlock<E> speedMod(BiFunction<E, BlockPos, Float> speedModifier) {
      this.speedMod = speedModifier;
      return this;
   }

   public SetWalkTargetToSpecificBlock<E> closeEnoughWhen(BiFunction<E, BlockPos, Integer> function) {
      this.closeEnoughDist = function;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected void start(E entity) {
      BlockPos pos = this.targetFunction.apply(entity);
      BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(pos, this.speedMod.apply(entity, pos), this.closeEnoughDist.apply(entity, pos)));
      BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));
   }
}
