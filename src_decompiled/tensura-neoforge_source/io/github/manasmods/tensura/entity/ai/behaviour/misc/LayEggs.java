package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class LayEggs<E extends PathfinderMob> extends DelayedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .hasMemory(MemoryModuleType.MEETING_POINT)
      .noMemory(MemoryModuleType.GAZE_COOLDOWN_TICKS);
   protected Function<E, Integer> layCooldown = entity -> 60;
   protected BiPredicate<E, GlobalPos> shouldLay = (entity, pos) -> pos.pos().closerToCenterThan(entity.position(), 3.0);
   protected BiConsumer<E, BlockPos> laySupplier = (entity, pos) -> {
      entity.level().setBlock(pos.above(), Blocks.TURTLE_EGG.defaultBlockState(), 3);
      BrainUtils.clearMemories(entity, new MemoryModuleType[]{MemoryModuleType.MEETING_POINT});
   };
   protected SoundEvent laySound = SoundEvents.TURTLE_LAY_EGG;

   public LayEggs(int delayTicks) {
      super(delayTicks);
      this.noTimeout();
   }

   public LayEggs<E> shouldLay(BiPredicate<E, GlobalPos> canStart) {
      this.shouldLay = canStart;
      return this;
   }

   public LayEggs<E> layCooldown(Function<E, Integer> supplier) {
      this.layCooldown = supplier;
      return this;
   }

   public LayEggs<E> layEggs(BiConsumer<E, BlockPos> callback) {
      this.laySupplier = callback;
      return this;
   }

   public LayEggs<E> laySound(SoundEvent soundEvent) {
      this.laySound = soundEvent;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (entity.isPassenger()) {
         return false;
      }

      GlobalPos home = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.MEETING_POINT);
      return home == null ? false : this.shouldLay.test(entity, home);
   }

   protected boolean shouldKeepRunning(E entity) {
      return this.checkExtraStartConditions((ServerLevel)entity.level(), entity);
   }

   protected void tick(E entity) {
      GlobalPos home = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.MEETING_POINT);
      if (home != null) {
         entity.getNavigation().stop();
         entity.level().playSound(null, home.pos(), this.laySound, SoundSource.BLOCKS, 0.3F, 0.9F + entity.level().random.nextFloat() * 0.2F);
         entity.level().levelEvent(2001, home.pos(), Block.getId(entity.level().getBlockState(home.pos().below())));
      }
   }

   protected void doDelayedAction(E entity) {
      int cooldownTicks = this.layCooldown.apply(entity);
      BrainUtils.setForgettableMemory(entity, MemoryModuleType.GAZE_COOLDOWN_TICKS, cooldownTicks, cooldownTicks);
      GlobalPos home = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.MEETING_POINT);
      if (home != null) {
         this.laySupplier.accept(entity, home.pos());
      }
   }
}
