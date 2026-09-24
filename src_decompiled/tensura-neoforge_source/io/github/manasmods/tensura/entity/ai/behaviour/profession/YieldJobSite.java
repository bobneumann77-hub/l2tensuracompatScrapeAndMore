package io.github.manasmods.tensura.entity.ai.behaviour.profession;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.TensuraMerchantEntity;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.pathfinder.Path;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class YieldJobSite extends ExtendedBehaviour<TensuraMerchantEntity> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(5)
      .hasMemory(MemoryModuleType.POTENTIAL_JOB_SITE)
      .noMemory(MemoryModuleType.JOB_SITE)
      .usesMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES)
      .usesMemory(MemoryModuleType.WALK_TARGET)
      .usesMemory(MemoryModuleType.LOOK_TARGET);
   private Function<TensuraMerchantEntity, Float> speedMod = v -> 1.0F;
   private BiFunction<TensuraMerchantEntity, BlockPos, Integer> closeEnoughDist = (v, pos) -> 1;

   public YieldJobSite speedMod(Function<TensuraMerchantEntity, Float> speed) {
      this.speedMod = speed;
      return this;
   }

   public YieldJobSite closeEnoughWhen(BiFunction<TensuraMerchantEntity, BlockPos, Integer> fn) {
      this.closeEnoughDist = fn;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, TensuraMerchantEntity merchant) {
      if (merchant.isBaby()) {
         return false;
      }

      if (merchant.getProfession() != VillagerProfession.NONE) {
         return false;
      }

      GlobalPos site = (GlobalPos)BrainUtils.getMemory(merchant, MemoryModuleType.POTENTIAL_JOB_SITE);
      return site != null;
   }

   protected void start(TensuraMerchantEntity merchant) {
      ServerLevel level = (ServerLevel)merchant.level();
      GlobalPos site = (GlobalPos)BrainUtils.getMemory(merchant, MemoryModuleType.POTENTIAL_JOB_SITE);
      if (site != null) {
         BlockPos pos = site.pos();
         Optional<Holder<PoiType>> poiType = level.getPoiManager().getType(pos);
         if (!poiType.isEmpty()) {
            List<LivingEntity> nearby = (List<LivingEntity>)BrainUtils.getMemory(merchant, MemoryModuleType.NEAREST_LIVING_ENTITIES);
            if (nearby != null) {
               Optional<TensuraMerchantEntity> receiver = nearby.stream()
                  .filter(le -> le instanceof TensuraMerchantEntity && le != merchant)
                  .map(le -> (TensuraMerchantEntity)le)
                  .filter(LivingEntity::isAlive)
                  .filter(v -> nearbyWantsJobsite(poiType.get(), v, pos))
                  .findFirst();
               if (!receiver.isEmpty()) {
                  TensuraMerchantEntity other = receiver.get();
                  BrainUtils.clearMemory(merchant, MemoryModuleType.WALK_TARGET);
                  BrainUtils.clearMemory(merchant, MemoryModuleType.LOOK_TARGET);
                  BrainUtils.clearMemory(merchant, MemoryModuleType.POTENTIAL_JOB_SITE);
                  if (other.getBrain().getMemory(MemoryModuleType.JOB_SITE).isEmpty()) {
                     BrainUtils.setMemory(
                        other, MemoryModuleType.WALK_TARGET, new WalkTarget(pos, this.speedMod.apply(other), this.closeEnoughDist.apply(other, pos))
                     );
                     BrainUtils.setMemory(other, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));
                     BrainUtils.setMemory(other, MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.of(level.dimension(), pos));
                     DebugPackets.sendPoiTicketCountPacket(level, pos);
                  }
               }
            }
         }
      }
   }

   protected boolean canStillUse(ServerLevel level, TensuraMerchantEntity entity, long gameTime) {
      return false;
   }

   private static boolean nearbyWantsJobsite(Holder<PoiType> poiHolder, TensuraMerchantEntity merchant, BlockPos pos) {
      if (merchant.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent()) {
         return false;
      }

      Optional<GlobalPos> jobSite = merchant.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      VillagerProfession prof = merchant.getProfession();
      return !prof.heldJobSite().test(poiHolder)
         ? false
         : jobSite.<Boolean>map(globalPos -> globalPos.pos().equals(pos)).orElseGet(() -> canReachPos(merchant, pos, (PoiType)poiHolder.value()));
   }

   private static boolean canReachPos(PathfinderMob mob, BlockPos pos, PoiType poiType) {
      Path path = mob.getNavigation().createPath(pos, poiType.validRange());
      return path != null && path.canReach();
   }
}
