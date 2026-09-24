package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.object.SquareRadius;

public class FindNearestPosition<E extends PathfinderMob> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(0);
   protected BiPredicate<E, BlockPos> predicate = (entity, pos) -> true;
   protected BiConsumer<E, BlockPos> action = (entity, pos) -> {};
   protected SquareRadius radius = new SquareRadius(16.0, 8.0);
   protected BlockPos target = null;
   private long nextScanTick = 0L;

   public FindNearestPosition() {
      this.cooldownFor(entity -> 200);
   }

   public FindNearestPosition<E> predicate(BiPredicate<E, BlockPos> predicate) {
      this.predicate = predicate;
      return this;
   }

   public FindNearestPosition<E> setRadius(double radius) {
      return this.setRadius(radius, radius);
   }

   public FindNearestPosition<E> setRadius(double xz, double y) {
      this.radius = new SquareRadius(xz, y);
      return this;
   }

   public FindNearestPosition<E> action(BiConsumer<E, BlockPos> consumer) {
      this.action = consumer;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      long now = level.getGameTime();
      if (now < this.nextScanTick) {
         return false;
      }

      double closestDist = Mth.square(entity.getAttributeValue(Attributes.FOLLOW_RANGE));
      BlockPos entityPos = entity.blockPosition();
      BlockPos closest = null;

      for (BlockPos position : BlockPos.betweenClosed(
         entity.blockPosition().subtract(this.radius.toVec3i()), entity.blockPosition().offset(this.radius.toVec3i())
      )) {
         if (this.predicate.test(entity, position)) {
            double dist = entityPos.distSqr(position);
            if (dist < closestDist) {
               closestDist = dist;
               closest = position;
            }
         }
      }

      this.target = closest;
      if (this.target == null) {
         this.nextScanTick = now + 40L;
      }

      return this.target != null;
   }

   protected void start(E entity) {
      this.action.accept(entity, this.target);
   }

   protected void stop(E entity) {
      this.target = null;
   }
}
