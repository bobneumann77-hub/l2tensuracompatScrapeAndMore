package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

public class ValidateNearbyPoi<E extends PathfinderMob> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(0);
   private final BiFunction<E, Holder<PoiType>, Boolean> poiPredicate;
   private final MemoryModuleType<GlobalPos> memoryType;
   protected TriFunction<E, ServerLevel, BlockPos, Boolean> targetPredicate = ValidateNearbyPoi::isBedOccupied;
   protected BiFunction<E, BlockPos, Integer> maxDistance = (entity, pos) -> 32;
   @Nullable
   private ServerLevel level;
   private BlockPos target;

   public ValidateNearbyPoi(BiFunction<E, Holder<PoiType>, Boolean> predicate, MemoryModuleType<GlobalPos> memoryType) {
      this.poiPredicate = predicate;
      this.memoryType = memoryType;
   }

   public ValidateNearbyPoi<E> maxDistance(BiFunction<E, BlockPos, Integer> distance) {
      this.maxDistance = distance;
      return this;
   }

   public ValidateNearbyPoi<E> predicate(TriFunction<E, ServerLevel, BlockPos, Boolean> predicate) {
      this.targetPredicate = predicate;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      GlobalPos globalPos = (GlobalPos)BrainUtils.getMemory(entity, this.memoryType);
      if (globalPos == null) {
         return false;
      }

      BlockPos pos = globalPos.pos();
      if (level.dimension() != globalPos.dimension()) {
         return false;
      }

      if (!pos.closerToCenterThan(entity.position(), this.maxDistance.apply(entity, pos).intValue())) {
         return false;
      }

      ServerLevel targetLevel = level.getServer().getLevel(globalPos.dimension());
      if (targetLevel == null) {
         return false;
      }

      this.level = targetLevel;
      this.target = pos;
      return true;
   }

   protected void start(E entity) {
      if (this.level != null) {
         Optional<Holder<PoiType>> optional = this.level.getPoiManager().getType(this.target);
         if (!optional.isPresent() || !this.poiPredicate.apply(entity, optional.get())) {
            BrainUtils.clearMemory(entity, this.memoryType);
         } else if ((Boolean)this.targetPredicate.apply(entity, this.level, this.target)) {
            BrainUtils.clearMemory(entity, this.memoryType);
            this.level.getPoiManager().release(this.target);
         }
      }
   }

   protected void stop(E entity) {
      this.level = null;
      this.target = null;
   }

   public static boolean isBedOccupied(PathfinderMob entity, ServerLevel level, BlockPos pos) {
      BlockState bed = level.getBlockState(pos);
      return bed.is(BlockTags.BEDS) && bed.getValue(BedBlock.PART) == BedPart.HEAD && (Boolean)bed.getValue(BedBlock.OCCUPIED) && !entity.isSleeping();
   }
}
