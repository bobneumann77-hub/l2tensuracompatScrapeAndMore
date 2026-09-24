package io.github.manasmods.tensura.entity.ai.behaviour.path;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.TensuraMerchantEntity;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.pathfinder.Path;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class SetWalkTargetToPotentialJobSite extends ExtendedBehaviour<TensuraMerchantEntity> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).usesMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
   private static final int TICKS_UNTIL_TIMEOUT = 1200;
   private Function<TensuraMerchantEntity, Float> speedMod = v -> 1.0F;
   private BiFunction<TensuraMerchantEntity, BlockPos, Integer> closeEnoughDist = (v, pos) -> 1;
   private long expiryTime = 0L;

   public SetWalkTargetToPotentialJobSite speedMod(Function<TensuraMerchantEntity, Float> speed) {
      this.speedMod = speed;
      return this;
   }

   public SetWalkTargetToPotentialJobSite closeEnoughWhen(BiFunction<TensuraMerchantEntity, BlockPos, Integer> fn) {
      this.closeEnoughDist = fn;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, TensuraMerchantEntity merchant) {
      boolean okActivity = merchant.getBrain().getActiveNonCoreActivity().map(a -> a == Activity.IDLE || a == Activity.WORK || a == Activity.PLAY).orElse(true);
      if (!okActivity) {
         return false;
      }

      Optional<GlobalPos> job = merchant.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
      if (job.isEmpty()) {
         return false;
      }

      BlockPos pos = job.get().pos();
      Path path = merchant.getNavigation().createPath(pos, 2);
      return path != null;
   }

   protected void start(TensuraMerchantEntity TensuraMerchantEntity) {
      if (TensuraMerchantEntity.level() instanceof ServerLevel level) {
         this.expiryTime = level.getGameTime() + 1200L;
         Optional<GlobalPos> job = TensuraMerchantEntity.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
         if (!job.isEmpty()) {
            BlockPos pos = job.get().pos();
            BrainUtils.setMemory(
               TensuraMerchantEntity,
               MemoryModuleType.WALK_TARGET,
               new WalkTarget(pos, this.speedMod.apply(TensuraMerchantEntity), this.closeEnoughDist.apply(TensuraMerchantEntity, pos))
            );
            BrainUtils.setMemory(TensuraMerchantEntity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));
         }
      }
   }

   protected void tick(TensuraMerchantEntity TensuraMerchantEntity) {
      Optional<GlobalPos> job = TensuraMerchantEntity.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
      if (!job.isEmpty()) {
         BlockPos pos = job.get().pos();
         BrainUtils.setMemory(
            TensuraMerchantEntity,
            MemoryModuleType.WALK_TARGET,
            new WalkTarget(pos, this.speedMod.apply(TensuraMerchantEntity), this.closeEnoughDist.apply(TensuraMerchantEntity, pos))
         );
         BrainUtils.setMemory(TensuraMerchantEntity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));
      }
   }

   protected boolean canStillUse(ServerLevel level, TensuraMerchantEntity TensuraMerchantEntity, long gameTime) {
      return TensuraMerchantEntity.getBrain().hasMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE) && gameTime < this.expiryTime;
   }

   protected void stop(ServerLevel level, TensuraMerchantEntity TensuraMerchantEntity, long gameTime) {
      Optional<GlobalPos> maybe = TensuraMerchantEntity.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
      maybe.ifPresent(globalPos -> {
         BlockPos pos = globalPos.pos();
         ServerLevel poiLevel = level.getServer().getLevel(globalPos.dimension());
         if (poiLevel != null) {
            PoiManager poiManager = poiLevel.getPoiManager();
            if (poiManager.exists(pos, holder -> true)) {
               poiManager.release(pos);
            }

            DebugPackets.sendPoiTicketCountPacket(level, pos);
         }
      });
      BrainUtils.clearMemory(TensuraMerchantEntity, MemoryModuleType.POTENTIAL_JOB_SITE);
      BrainUtils.clearMemory(TensuraMerchantEntity, MemoryModuleType.LOOK_TARGET);
   }
}
