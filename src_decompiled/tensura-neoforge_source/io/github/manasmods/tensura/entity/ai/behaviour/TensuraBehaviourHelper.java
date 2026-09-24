package io.github.manasmods.tensura.entity.ai.behaviour;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.config.entity.EntityConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.RetaliateOrTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.SleepOnBed;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.ValidateNearbyPoi;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomWalkTargetAroundCenter;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetWalkTargetFromBlockMemory;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetWalkTargetToSpecificBlock;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.AcquirePoi;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.AllApplicableBehaviours;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class TensuraBehaviourHelper {
   public static final EntityConfig CONFIG = (EntityConfig)ConfigRegistry.getConfig(EntityConfig.class);

   public static void saveGlobalPos(LivingEntity entity, CompoundTag compound, MemoryModuleType<GlobalPos> memory, String name) {
      GlobalPos pos = (GlobalPos)BrainUtils.getMemory(entity, memory);
      if (pos != null) {
         DataResult<Tag> dataresult = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos);
         dataresult.result().ifPresent(tag -> compound.put(name, tag));
      }
   }

   public static void readGlobalPos(LivingEntity entity, CompoundTag compound, MemoryModuleType<GlobalPos> memory, String name) {
      if (compound.contains(name)) {
         Dynamic<?> dynamic = new Dynamic(NbtOps.INSTANCE, compound.get(name));
         DataResult<GlobalPos> dataresult = GlobalPos.CODEC.parse(dynamic);
         dataresult.result().ifPresent(pos -> BrainUtils.setMemory(entity, memory, pos));
      }
   }

   public static <E extends PathfinderMob & ISubordinate> ExtendedBehaviour<E> setWanderAroundHome() {
      return new SetRandomWalkTargetAroundCenter<PathfinderMob>().maxDistance(entity -> (double)CONFIG.tamedWanderRadius).center(entity -> {
         GlobalPos pos = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.HOME);
         return pos == null ? entity.position() : Vec3.atCenterOf(pos.pos());
      }).startCondition(entity -> BrainUtils.hasMemory(entity, MemoryModuleType.HOME));
   }

   public static <E extends PathfinderMob & ISubordinate> ExtendedBehaviour<E> getMoveToWanderPos(Function<E, Integer> distance) {
      return new SetWalkTargetToSpecificBlock<PathfinderMob>()
         .setTargetPos(rec$ -> ((ISubordinate)rec$).getWanderPos())
         .closeEnoughWhen((entity, pos) -> distance.apply((E)entity) / 2)
         .speedMod((entity, pos) -> 1.2F)
         .startCondition(entity -> {
            if (!((ISubordinate)entity).isTame()) {
               return false;
            }

            if (!((ISubordinate)entity).isWandering()) {
               return false;
            }

            BlockPos wp = ((ISubordinate)entity).getWanderPos();
            if (wp.equals(BlockPos.ZERO)) {
               return false;
            }

            double dist = distance.apply((E)entity).intValue();
            double dx = entity.getX() - (wp.getX() + 0.5);
            double dy = entity.getY() - (wp.getY() + 0.5);
            double dz = entity.getZ() - (wp.getZ() + 0.5);
            return dx * dx + dy * dy + dz * dz > dist * dist;
         });
   }

   public static <E extends PathfinderMob & ISubordinate> ExtendedBehaviour<E> getMoveToWanderPos() {
      return getMoveToWanderPos(entity -> CONFIG.tamedWanderRadius);
   }

   public static <E extends PlayerLikeEntity> ExtendedBehaviour<E> getMoveToWanderFarmingPos() {
      return getMoveToWanderPos(entity -> entity.shouldDoFarming() ? 10 : CONFIG.tamedWanderRadius);
   }

   public static <E extends PathfinderMob & ISubordinate> ExtendedBehaviour<E> getPreyTargeting(
      E subordinate, Predicate<LivingEntity> additionalPredicate, Predicate<LivingEntity> preyPredicate
   ) {
      Predicate<LivingEntity> combinedPrey = preyPredicate.and(getSleepingPreyPredicate(subordinate));
      return new RetaliateOrTarget()
         .alertAlliesWhen((mob, entity) -> !((ISubordinate)mob).isTame() && !entity.isInvisible())
         .attackablePredicate(entity -> additionalPredicate.test(entity) && subordinate.shouldTarget(subordinate, entity, combinedPrey));
   }

   public static <E extends PathfinderMob & ISubordinate> ExtendedBehaviour<E> getPreyTargeting(E subordinate, Predicate<LivingEntity> preyPredicate) {
      return getPreyTargeting(subordinate, target -> true, preyPredicate);
   }

   public static <E extends PathfinderMob & ISubordinate> ExtendedBehaviour<E> getPreyTargeting(E subordinate) {
      return getPreyTargeting(subordinate, target -> true, getAnimalPreyPredicate(subordinate));
   }

   public static Predicate<LivingEntity> getAnimalPreyPredicate(LivingEntity subordinate) {
      return target -> {
         if (target.getType().equals(EntityType.PLAYER)) {
            return true;
         } else if (subordinate.getHealth() >= subordinate.getMaxHealth() * CONFIG.hostileHPMultiplier) {
            return false;
         } else {
            return target.getType() == subordinate.getType() ? false : target.getType().is(TensuraEntityTags.ANIMAL_PREY);
         }
      };
   }

   public static Predicate<LivingEntity> getSleepingPreyPredicate(LivingEntity subordinate) {
      return target -> !subordinate.isSleeping() ? true : target.getPose() != Pose.CROUCHING && !SkillUtils.canBlockSoundDetect(target);
   }

   public static <E extends LivingEntity & ISubordinate> boolean canContinueToSleep(E entity) {
      return canContinueToSleep(entity, null);
   }

   public static <E extends LivingEntity & ISubordinate> boolean canContinueToSleep(E entity, @Nullable GlobalPos pos) {
      if (!entity.isTame()) {
         return true;
      }

      if (entity.isOrderedToSit()) {
         return true;
      }

      if (entity.isWandering()) {
         BlockPos wp = entity.getWanderPos();
         if (wp.equals(BlockPos.ZERO)) {
            return true;
         }

         double distance = CONFIG.tamedWanderRadius;
         double wx = entity.getX() - (wp.getX() + 0.5);
         double wy = entity.getY() - (wp.getY() + 0.5);
         double wz = entity.getZ() - (wp.getZ() + 0.5);
         return wx * wx + wy * wy + wz * wz < distance * distance;
      } else {
         LivingEntity owner = entity.getOwner();
         if (owner == null) {
            return true;
         }

         double distance = CONFIG.tamedSleepRadius;
         double distSq = distance * distance;
         if (entity.distanceToSqr(owner) < distSq) {
            return false;
         }

         if (pos == null) {
            return true;
         }

         BlockPos pp = pos.pos();
         double ox = owner.getX() - (pp.getX() + 0.5);
         double oy = owner.getY() - pp.getY();
         double oz = owner.getZ() - (pp.getZ() + 0.5);
         return ox * ox + oy * oy + oz * oz < distSq;
      }
   }

   public static <E extends PathfinderMob & SmartBrainOwner<E>> BrainActivityGroup<E> getSleepActivityGroup() {
      return new BrainActivityGroup(Activity.REST)
         .behaviours(
            new Behavior[]{
               new AcquirePoi<PathfinderMob>()
                  .predicate((entity, holder) -> holder.is(PoiTypes.HOME))
                  .writeTo(MemoryModuleType.HOME)
                  .additionalPredicate((entity, pos) -> SleepOnBed.isValidBedPosition(entity.level().getBlockState(pos))),
               new ValidateNearbyPoi<PathfinderMob>((entity, holder) -> holder.is(PoiTypes.HOME), MemoryModuleType.HOME),
               new SetWalkTargetFromBlockMemory(MemoryModuleType.HOME, PoiTypes.HOME).startCondition(entity -> !entity.isSleeping()),
               new SleepOnBed(),
               new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
                  .startCondition(entity -> !entity.isSleeping() && !BrainUtils.hasMemory(entity, MemoryModuleType.WALK_TARGET))
            }
         )
         .onlyStartWithMemoryStatus(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
         .onlyStartWithMemoryStatus(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT);
   }

   public static <E extends TensuraHumanoidEntity & SmartBrainOwner<E>> BrainActivityGroup<E> getHumanoidSleepActivityGroup(E living) {
      return new BrainActivityGroup(Activity.REST)
         .behaviours(
            new Behavior[]{
               new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     getPreyTargeting(living, entity -> false).startCondition(rec$ -> ((ISubordinate)rec$).isTame()),
                     new SubordinateFollowOwner().startCondition(entity -> !canContinueToSleep((E)entity)),
                     new AllApplicableBehaviours(
                        new ExtendedBehaviour[]{
                           new AcquirePoi()
                              .predicate((entity, holder) -> holder.is(PoiTypes.HOME))
                              .writeTo(MemoryModuleType.HOME)
                              .additionalPredicate((entity, pos) -> SleepOnBed.isValidBedPosition(entity.level().getBlockState(pos))),
                           new ValidateNearbyPoi((entity, holder) -> holder.is(PoiTypes.HOME), MemoryModuleType.HOME),
                           new SetWalkTargetFromBlockMemory(MemoryModuleType.HOME, PoiTypes.HOME)
                              .startCondition(
                                 entity -> !entity.isSleeping()
                                    && canContinueToSleep((E)entity, (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.HOME))
                              ),
                           new SleepOnBed().startCondition(x$0 -> canContinueToSleep((E)((LivingEntity)x$0))),
                           new OneRandomBehaviour(
                                 new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))}
                              )
                              .startCondition(entity -> !entity.isSleeping() && !BrainUtils.hasMemory(entity, MemoryModuleType.WALK_TARGET))
                        }
                     )
                  }
               )
            }
         )
         .onlyStartWithMemoryStatus(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
         .onlyStartWithMemoryStatus(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT);
   }

   public static void setHomeOnPos(LivingEntity mob, BlockPos pos, Level level) {
      MutableBlockPos groundPos = pos.below().mutable();

      while (groundPos.getY() >= level.getMinBuildHeight() && !level.getBlockState(groundPos).entityCanStandOn(level, groundPos, mob)) {
         groundPos.move(Direction.DOWN);
      }

      if (level.getBlockState(groundPos).entityCanStandOn(level, groundPos, mob)) {
         setHome(mob, level, groundPos);
      }
   }

   public static void setHome(LivingEntity mob, Level level, BlockPos pos) {
      GlobalPos groundPos = GlobalPos.of(level.dimension(), pos.above().immutable());
      BrainUtils.setMemory(mob, MemoryModuleType.HOME, groundPos);
   }

   public static void releaseHome(LivingEntity entity) {
      if (entity.level() instanceof ServerLevel level) {
         MinecraftServer var7 = level.getServer();
         GlobalPos globalPos = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.HOME);
         if (globalPos != null) {
            ServerLevel serverLevel = var7.getLevel(globalPos.dimension());
            if (serverLevel != null) {
               PoiManager poiManager = serverLevel.getPoiManager();
               Optional<Holder<PoiType>> optional = poiManager.getType(globalPos.pos());
               if (optional.isPresent() && optional.get().is(PoiTypes.HOME)) {
                  poiManager.release(globalPos.pos());
                  DebugPackets.sendPoiTicketCountPacket(serverLevel, globalPos.pos());
               }
            }
         }
      }
   }
}
