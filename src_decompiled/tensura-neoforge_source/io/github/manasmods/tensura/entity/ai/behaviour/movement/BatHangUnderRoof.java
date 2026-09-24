package io.github.manasmods.tensura.entity.ai.behaviour.movement;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

public class BatHangUnderRoof<E extends Mob & IFlying> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).noMemory(MemoryModuleType.ATTACK_TARGET);
   protected int distance = 20;
   protected TriFunction<E, BlockPos, BlockState, Boolean> canHangOn = (entity, pos, state) -> {
      if (!entity.level().isEmptyBlock(pos.below())) {
         return false;
      } else {
         return !entity.level().isEmptyBlock(pos.below(2)) ? false : state.isFaceSturdy(entity.level(), pos, Direction.DOWN);
      }
   };
   protected Function<E, Boolean> checkHanging = rec$ -> ((LivingEntity)rec$).isSleeping();
   protected BiConsumer<E, BlockPos> setHanging = (rec$, x$0) -> ((LivingEntity)rec$).setSleepingPos(x$0);
   protected Vec3 hangPos;
   private long nextScanTick = 0L;

   public BatHangUnderRoof<E> maxAttackRadius(int radius) {
      this.distance = radius;
      return this;
   }

   public BatHangUnderRoof<E> canHangOn(TriFunction<E, BlockPos, BlockState, Boolean> supplier) {
      this.canHangOn = supplier;
      return this;
   }

   public BatHangUnderRoof<E> checkHanging(Function<E, Boolean> supplier) {
      this.checkHanging = supplier;
      return this;
   }

   public BatHangUnderRoof<E> setHanging(BiConsumer<E, BlockPos> callback) {
      this.setHanging = callback;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      long now = level.getGameTime();
      if (now < this.nextScanTick) {
         return false;
      } else if (entity.isVehicle() || entity.isPassenger()) {
         return false;
      } else if (this.checkHanging.apply(entity)) {
         return false;
      } else {
         Vec3 vec3 = this.findHangFromPos(entity);
         if (vec3 != null) {
            this.hangPos = vec3;
            return true;
         } else {
            this.nextScanTick = now + 60L;
            return false;
         }
      }
   }

   protected void start(E entity) {
      Vec3 hangPos = this.hangPos;
      if (hangPos != null) {
         entity.setFlying(true);
         BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(hangPos, 1.0F, 0));
         BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(BlockPos.containing(hangPos)));
      }
   }

   protected boolean shouldKeepRunning(E entity) {
      return !entity.getNavigation().isDone() && !this.checkHanging.apply(entity);
   }

   protected void tick(E entity) {
      if (!this.checkHanging.apply(entity)) {
         Level level = entity.level();
         BlockPos above = new BlockPos(Mth.floor(entity.getX()), Mth.floor(entity.getBoundingBox().maxY + 0.5), Mth.floor(entity.getZ()));
         if ((Boolean)this.canHangOn.apply(entity, above, level.getBlockState(above))) {
            this.setHanging.accept(entity, above);
            entity.setFlying(false);
         }
      }
   }

   protected void stop(E entity) {
      entity.getNavigation().stop();
   }

   @Nullable
   private Vec3 findHangFromPos(E entity) {
      RandomSource random = entity.getRandom();
      int range = this.distance;

      for (int i = 0; i < this.distance; i++) {
         BlockPos pos = entity.blockPosition().offset(random.nextInt(range) - range / 2, 0, random.nextInt(range) - range / 2);
         if (entity.level().isEmptyBlock(pos) && entity.level().isLoaded(pos)) {
            while (entity.level().isEmptyBlock(pos) && pos.getY() < entity.level().getMaxBuildHeight()) {
               pos = pos.above();
            }

            if ((Boolean)this.canHangOn.apply(entity, pos, entity.level().getBlockState(pos))) {
               return Vec3.atCenterOf(pos);
            }
         }
      }

      return null;
   }
}
