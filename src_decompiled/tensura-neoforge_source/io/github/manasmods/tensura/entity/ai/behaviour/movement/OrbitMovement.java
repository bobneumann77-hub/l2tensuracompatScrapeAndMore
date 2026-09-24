package io.github.manasmods.tensura.entity.ai.behaviour.movement;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.HeldBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class OrbitMovement<E extends Mob> extends HeldBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(0);
   protected Function<E, Float> speedMod = owner -> 1.0F;
   protected Function<E, Boolean> requireNotHavingTarget = owner -> true;
   protected Function<E, Double> orbitRadiusSupplier = entity -> 20.0;
   protected Function<E, Double> orbitHeightSupplier = entity -> 20.0;
   protected BiFunction<E, Pair<Vec3, Integer>, Boolean> orbitTickConsumer = (entity, pair) -> true;
   protected Function<E, Integer> orbitCenterRefreshIntervalSupplier = entity -> 200 + entity.getRandom().nextInt(100);
   protected BiConsumer<E, GlobalPos> refreshOrbitCenterSupplier = (entity, currentCenter) -> {
      LivingEntity target = BrainUtils.getTargetOfEntity(entity);
      setDefaultMeetingPoint(entity, target != null ? target.blockPosition() : entity.blockPosition());
   };
   private Vec3 startOrbitFrom;
   private int orbitTime;
   private int refreshOrbitCenterCooldown;
   protected Vec3 lastOrbitPos;
   private int positionUpdateCooldown = 0;

   public OrbitMovement<E> requireNotHavingTarget(Function<E, Boolean> function) {
      this.requireNotHavingTarget = function;
      return this;
   }

   public OrbitMovement<E> speedMod(Function<E, Float> speedModifier) {
      this.speedMod = speedModifier;
      return this;
   }

   public OrbitMovement<E> orbitRadius(Function<E, Double> supplier) {
      this.orbitRadiusSupplier = supplier;
      return this;
   }

   public OrbitMovement<E> orbitHeight(Function<E, Double> supplier) {
      this.orbitHeightSupplier = supplier;
      return this;
   }

   public OrbitMovement<E> onOrbitTick(BiFunction<E, Pair<Vec3, Integer>, Boolean> tickConsumer) {
      this.orbitTickConsumer = tickConsumer;
      return this;
   }

   public OrbitMovement<E> orbitCenterRefreshInterval(Function<E, Integer> supplier) {
      this.orbitCenterRefreshIntervalSupplier = supplier;
      return this;
   }

   public OrbitMovement<E> refreshOrbitCenter(BiConsumer<E, GlobalPos> callback) {
      this.refreshOrbitCenterSupplier = callback;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      LivingEntity target = BrainUtils.getTargetOfEntity(entity);
      if (this.requireNotHavingTarget.apply(entity) && target != null) {
         return false;
      }

      if (!BrainUtils.hasMemory(entity, MemoryModuleType.MEETING_POINT)) {
         this.refreshOrbitCenterSupplier.accept(entity, null);
      }

      return true;
   }

   protected boolean shouldKeepRunning(E entity) {
      return !this.requireNotHavingTarget.apply(entity) || BrainUtils.getTargetOfEntity(entity) == null;
   }

   protected void start(E entity) {
      this.orbitTime = 0;
      this.refreshOrbitCenterCooldown = this.orbitCenterRefreshIntervalSupplier.apply(entity);
      this.startOrbitFrom = ((GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.MEETING_POINT)).pos().getCenter();
   }

   protected void stop(E entity) {
      this.startOrbitFrom = null;
      this.orbitTime = 0;
      this.refreshOrbitCenterCooldown = 0;
      this.lastOrbitPos = null;
      this.positionUpdateCooldown = 0;
   }

   protected void tick(E entity) {
      if (this.orbitTickConsumer.apply(entity, Pair.of(this.startOrbitFrom, this.orbitTime))) {
         if (this.startOrbitFrom == null) {
            this.refreshOrbitCenterCooldown = this.orbitCenterRefreshIntervalSupplier.apply(entity);
            GlobalPos center = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.MEETING_POINT);
            this.refreshOrbitCenterSupplier.accept(entity, center);
            this.startOrbitFrom = center.pos().getCenter();
         } else if (this.orbitTime < this.refreshOrbitCenterCooldown) {
            this.orbitTime++;
            if (this.positionUpdateCooldown > 0) {
               this.positionUpdateCooldown--;
            }

            if (this.positionUpdateCooldown <= 0) {
               Vec3 orbitPos = this.getOrbitPosition(entity, this.orbitRadiusSupplier.apply(entity));
               if (this.shouldStartPath(orbitPos, entity)) {
                  BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(orbitPos, this.speedMod.apply(entity), 0));
                  BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosTracker(BlockPos.containing(orbitPos)));
                  this.lastOrbitPos = orbitPos;
               }

               float width = entity.getBbWidth();
               this.positionUpdateCooldown = width < 2.0F ? 10 : (int)(width * 10.0F);
            }
         } else {
            this.orbitTime = 0;
            this.startOrbitFrom = null;
         }
      }
   }

   protected boolean shouldStartPath(Vec3 orbitPos, E entity) {
      return this.lastOrbitPos == null || this.lastOrbitPos.distanceToSqr(orbitPos) > Math.max(4.0, entity.getBbWidth() * 3.0);
   }

   protected Vec3 getOrbitPosition(E entity, double radius) {
      float zoomIn = 1.0F - (float)this.orbitTime / this.refreshOrbitCenterCooldown;
      float angle = (float)((Math.PI * 2) * ((float)this.orbitTime / this.refreshOrbitCenterCooldown));
      double extraX = radius * Mth.sin(angle);
      double extraZ = radius * Mth.cos(angle);
      double extraY = this.orbitHeightSupplier.apply(entity) + zoomIn * 3.0F;
      return this.startOrbitFrom.add(extraX, extraY, extraZ);
   }

   public static void setDefaultMeetingPoint(LivingEntity entity, BlockPos pos) {
      BlockPos center = entity.level().getHeightmapPos(Types.WORLD_SURFACE, pos);
      BrainUtils.setMemory(entity, MemoryModuleType.MEETING_POINT, new GlobalPos(entity.level().dimension(), center));
   }
}
