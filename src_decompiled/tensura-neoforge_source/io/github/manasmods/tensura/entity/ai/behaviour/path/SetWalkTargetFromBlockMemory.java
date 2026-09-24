package io.github.manasmods.tensura.entity.ai.behaviour.path;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;

public class SetWalkTargetFromBlockMemory<E extends PathfinderMob> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .usesMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
      .noMemory(MemoryModuleType.WALK_TARGET);
   private final MemoryModuleType<GlobalPos> sourceMemory;
   private final ResourceKey<PoiType> poiType;
   protected BiFunction<E, BlockPos, Float> speedMod = (owner, pos) -> 1.2F;
   protected BiFunction<E, BlockPos, Integer> closeEnoughDist = (entity, pos) -> 2;
   protected BiFunction<E, BlockPos, Integer> maxDistance = (entity, pos) -> 32;
   protected SquareRadius radius = new SquareRadius(16.0, 7.0);
   private int timeoutTick = 200;
   private int triesToGiveUp = 1000;

   public SetWalkTargetFromBlockMemory(MemoryModuleType<GlobalPos> sourceMemory, ResourceKey<PoiType> poiType) {
      this.sourceMemory = sourceMemory;
      this.poiType = poiType;
   }

   public SetWalkTargetFromBlockMemory<E> closeEnoughWhen(BiFunction<E, BlockPos, Integer> function) {
      this.closeEnoughDist = function;
      return this;
   }

   public SetWalkTargetFromBlockMemory<E> speedMod(BiFunction<E, BlockPos, Float> speedModifier) {
      this.speedMod = speedModifier;
      return this;
   }

   public SetWalkTargetFromBlockMemory<E> maxDistance(BiFunction<E, BlockPos, Integer> distance) {
      this.maxDistance = distance;
      return this;
   }

   public SetWalkTargetFromBlockMemory<E> setRandomRadius(double xz, double y) {
      this.radius = new SquareRadius(xz, y);
      return this;
   }

   public SetWalkTargetFromBlockMemory<E> timeout(int ticks) {
      this.timeoutTick = ticks;
      return this;
   }

   public SetWalkTargetFromBlockMemory<E> numberOfTries(int ticks) {
      this.triesToGiveUp = ticks;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      return BrainUtils.hasMemory(entity, this.sourceMemory);
   }

   protected void start(E entity) {
      GlobalPos targetPos = (GlobalPos)BrainUtils.getMemory(entity, this.sourceMemory);
      if (targetPos != null) {
         if (this.shouldWalk(targetPos, entity)) {
            BlockPos target = targetPos.pos();
            int distManhattan = target.distManhattan(entity.blockPosition());
            int maxDistance = this.maxDistance.apply(entity, target);
            if (distManhattan > maxDistance) {
               Vec3 pos = null;
               int tries = 0;

               while (pos == null || BlockPos.containing(pos).distManhattan(entity.blockPosition()) > maxDistance) {
                  pos = DefaultRandomPos.getPosTowards(
                     entity, (int)this.radius.xzRadius(), (int)this.radius.yRadius(), Vec3.atBottomCenterOf(target), Math.PI / 2
                  );
                  if (++tries > this.triesToGiveUp) {
                     this.releasePoi(entity, this.sourceMemory, this.poiType);
                     entity.getBrain().eraseMemory(this.sourceMemory);
                     entity.getBrain().setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, entity.level().getGameTime());
                     return;
                  }
               }

               entity.getBrain()
                  .setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, this.speedMod.apply(entity, target), this.closeEnoughDist.apply(entity, target)));
               BrainUtils.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            } else if (distManhattan > this.closeEnoughDist.apply(entity, target)) {
               entity.getBrain()
                  .setMemory(
                     MemoryModuleType.WALK_TARGET, new WalkTarget(target, this.speedMod.apply(entity, target), this.closeEnoughDist.apply(entity, target))
                  );
               BrainUtils.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            }
         } else {
            this.releasePoi(entity, this.sourceMemory, this.poiType);
            BrainUtils.clearMemory(entity, this.sourceMemory);
            BrainUtils.setMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, entity.level().getGameTime());
         }
      }
   }

   protected boolean shouldWalk(GlobalPos targetPos, E entity) {
      if (targetPos.dimension() != entity.level().dimension()) {
         return false;
      }

      Long cantReachSince = (Long)BrainUtils.getMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      if (cantReachSince == null) {
         return true;
      }

      long gameTime = entity.level().getGameTime();
      return gameTime - cantReachSince <= this.timeoutTick;
   }

   public void releasePoi(E entity, MemoryModuleType<GlobalPos> memoryModuleType, ResourceKey<PoiType> poiType) {
      if (entity.level() instanceof ServerLevel level) {
         MinecraftServer minecraftServer = level.getServer();
         GlobalPos globalPos = (GlobalPos)BrainUtils.getMemory(entity, memoryModuleType);
         if (globalPos == null) {
            return;
         }

         ServerLevel serverLevel = minecraftServer.getLevel(globalPos.dimension());
         if (serverLevel != null) {
            PoiManager poiManager = serverLevel.getPoiManager();
            Optional<Holder<PoiType>> optional = poiManager.getType(globalPos.pos());
            if (optional.isPresent() && optional.get().is(poiType)) {
               poiManager.release(globalPos.pos());
            }
         }
      }
   }
}
