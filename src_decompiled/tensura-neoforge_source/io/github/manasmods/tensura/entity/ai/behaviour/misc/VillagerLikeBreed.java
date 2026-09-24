package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class VillagerLikeBreed extends ExtendedBehaviour<TensuraHumanoidEntity> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .hasMemory(MemoryModuleType.BREED_TARGET)
      .hasMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
   private Function<TensuraHumanoidEntity, Float> moveSpeed = entity -> 0.75F;
   private Function<TensuraHumanoidEntity, Integer> lookPriority = entity -> 2;
   private Function<TensuraHumanoidEntity, Integer> breedDelayMin = entity -> 200;
   private Function<TensuraHumanoidEntity, Integer> breedDelayExtra = entity -> 100;
   private Function<TensuraHumanoidEntity, Double> interactRangeSqr = entity -> 9.0;
   private Function<TensuraHumanoidEntity, Integer> homeSearchRange = entity -> 48;
   private BiPredicate<TensuraHumanoidEntity, TensuraHumanoidEntity> canMatePredicate = (merchant, partner) -> merchant.canBreed()
      && partner.canBreed()
      && merchant.canMate(partner);
   private Function<TensuraHumanoidEntity, Integer> foodPointToConsume = entity -> 15;
   private long birthTimestamp = 0L;

   public VillagerLikeBreed() {
      this.runFor(entity -> 300);
   }

   public VillagerLikeBreed moveSpeed(Function<TensuraHumanoidEntity, Float> f) {
      this.moveSpeed = f;
      return this;
   }

   public VillagerLikeBreed lookPriority(Function<TensuraHumanoidEntity, Integer> f) {
      this.lookPriority = f;
      return this;
   }

   public VillagerLikeBreed breedDelay(Function<TensuraHumanoidEntity, Integer> base, Function<TensuraHumanoidEntity, Integer> extra) {
      this.breedDelayMin = base;
      this.breedDelayExtra = extra;
      this.runFor(entity -> base.apply(entity) + extra.apply(entity));
      return this;
   }

   public VillagerLikeBreed interactRange(Function<TensuraHumanoidEntity, Double> distSqr) {
      this.interactRangeSqr = distSqr;
      return this;
   }

   public VillagerLikeBreed homeSearchRange(Function<TensuraHumanoidEntity, Integer> range) {
      this.homeSearchRange = range;
      return this;
   }

   public VillagerLikeBreed canMateWhen(BiPredicate<TensuraHumanoidEntity, TensuraHumanoidEntity> predicate) {
      this.canMatePredicate = predicate;
      return this;
   }

   public VillagerLikeBreed foodPointToConsume(Function<TensuraHumanoidEntity, Integer> point) {
      this.foodPointToConsume = point;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, TensuraHumanoidEntity self) {
      return this.isBreedingPossible(self);
   }

   protected boolean canStillUse(ServerLevel level, TensuraHumanoidEntity self, long gameTime) {
      return gameTime <= this.birthTimestamp && this.isBreedingPossible(self);
   }

   protected void start(ServerLevel level, TensuraHumanoidEntity self, long gameTime) {
      AgeableMob target = (AgeableMob)BrainUtils.getMemory(self, MemoryModuleType.BREED_TARGET);
      if (target instanceof TensuraHumanoidEntity mate) {
         BehaviorUtils.lockGazeAndWalkToEachOther(self, mate, this.moveSpeed.apply(self), this.lookPriority.apply(self));
         level.broadcastEntityEvent(mate, (byte)18);
         level.broadcastEntityEvent(self, (byte)18);
         int duration = this.breedDelayMin.apply(self) + self.getRandom().nextInt(this.breedDelayExtra.apply(self));
         this.birthTimestamp = gameTime + duration;
      }
   }

   protected void tick(ServerLevel level, TensuraHumanoidEntity entity, long gameTime) {
      AgeableMob target = (AgeableMob)BrainUtils.getMemory(entity, MemoryModuleType.BREED_TARGET);
      if (target instanceof TensuraHumanoidEntity mate) {
         double maxDist = this.interactRangeSqr.apply(entity);
         if (!(entity.distanceToSqr(mate) > maxDist)) {
            BehaviorUtils.lockGazeAndWalkToEachOther(entity, mate, this.moveSpeed.apply(entity), this.lookPriority.apply(entity));
            if (gameTime >= this.birthTimestamp) {
               entity.consumeFoodPoints(this.foodPointToConsume.apply(entity));
               mate.consumeFoodPoints(this.foodPointToConsume.apply(entity));
               this.tryToGiveBirth(level, entity, mate);
            }

            if (entity.tickCount % 20 == 0) {
               level.broadcastEntityEvent(mate, (byte)12);
               level.broadcastEntityEvent(entity, (byte)12);
            }
         }
      }
   }

   protected void stop(ServerLevel level, TensuraHumanoidEntity self, long gameTime) {
      BrainUtils.clearMemory(self, MemoryModuleType.BREED_TARGET);
   }

   private boolean isBreedingPossible(TensuraHumanoidEntity entity) {
      Brain<?> brain = entity.getBrain();
      Optional<AgeableMob> targetOpt = brain.getMemory(MemoryModuleType.BREED_TARGET).filter(t -> t.getType() == entity.getType());
      if (targetOpt.isEmpty()) {
         return false;
      }

      AgeableMob other = targetOpt.get();
      return BehaviorUtils.targetIsValid(brain, MemoryModuleType.BREED_TARGET, entity.getType())
         && this.canMatePredicate.test(entity, (TensuraHumanoidEntity)other);
   }

   private void tryToGiveBirth(ServerLevel level, TensuraHumanoidEntity entity, TensuraHumanoidEntity mate) {
      Optional<BlockPos> vacant = this.takeVacantBed(level, entity);
      if (vacant.isEmpty()) {
         level.broadcastEntityEvent(mate, (byte)13);
         level.broadcastEntityEvent(entity, (byte)13);
      } else {
         Optional<TensuraHumanoidEntity> baby = this.breed(level, entity, mate);
         if (baby.isPresent()) {
            this.giveBedToChild(level, baby.get(), vacant.get());
         } else {
            level.getPoiManager().release(vacant.get());
            DebugPackets.sendPoiTicketCountPacket(level, vacant.get());
         }
      }
   }

   private Optional<BlockPos> takeVacantBed(ServerLevel level, TensuraHumanoidEntity self) {
      PoiManager poi = level.getPoiManager();
      return poi.take(holder -> holder.is(PoiTypes.HOME), (type, pos) -> this.canReach(self, pos, type), self.blockPosition(), this.homeSearchRange.apply(self));
   }

   private boolean canReach(TensuraHumanoidEntity TensuraHumanoidEntity, BlockPos pos, Holder<PoiType> poiType) {
      Path path = TensuraHumanoidEntity.getNavigation().createPath(pos, ((PoiType)poiType.value()).validRange());
      return path != null && path.canReach();
   }

   private Optional<TensuraHumanoidEntity> breed(ServerLevel level, TensuraHumanoidEntity entity, TensuraHumanoidEntity mate) {
      if (entity.getBreedOffspring(level, mate) instanceof TensuraHumanoidEntity child) {
         entity.setAge(6000);
         mate.setAge(6000);
         child.setAge(-24000);
         child.moveTo(entity.getX(), entity.getY(), entity.getZ(), 0.0F, 0.0F);
         level.addFreshEntityWithPassengers(child);
         level.broadcastEntityEvent(child, (byte)12);
         return Optional.of(child);
      } else {
         return Optional.empty();
      }
   }

   private void giveBedToChild(ServerLevel level, TensuraHumanoidEntity child, BlockPos bed) {
      GlobalPos home = GlobalPos.of(level.dimension(), bed);
      child.getBrain().setMemory(MemoryModuleType.HOME, home);
   }
}
