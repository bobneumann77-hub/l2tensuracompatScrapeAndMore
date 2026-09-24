package io.github.manasmods.tensura.entity.ai.behaviour;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;

public abstract class CustomTimeDelayedBehaviour<E extends LivingEntity> extends ExtendedBehaviour<E> {
   protected Function<E, Integer> delayTime = entity -> 10;
   protected long delayFinishedAt = 0L;
   protected Consumer<E> delayedCallback = entity -> {};

   public CustomTimeDelayedBehaviour() {
      this.runFor(entity -> 60);
   }

   public final CustomTimeDelayedBehaviour<E> delayFor(Function<E, Integer> delayTime) {
      this.delayTime = delayTime;
      return this;
   }

   public final CustomTimeDelayedBehaviour<E> whenActivating(Consumer<E> callback) {
      this.delayedCallback = callback;
      return this;
   }

   protected int getDelayTime(E entity) {
      return this.delayTime.apply(entity);
   }

   protected void start(ServerLevel level, E entity, long gameTime) {
      int delay = this.getDelayTime(entity);
      if (delay > 0) {
         this.delayFinishedAt = gameTime + delay;
         super.start(level, entity, gameTime);
      } else {
         super.start(level, entity, gameTime);
         this.doDelayedAction(entity);
      }
   }

   protected void stop(ServerLevel level, E entity, long gameTime) {
      super.stop(level, entity, gameTime);
      this.delayFinishedAt = 0L;
   }

   protected boolean shouldKeepRunning(E entity) {
      return this.delayFinishedAt >= entity.level().getGameTime();
   }

   protected void tick(ServerLevel level, E entity, long gameTime) {
      super.tick(level, entity, gameTime);
      if (this.delayFinishedAt <= gameTime) {
         this.doDelayedAction(entity);
         this.delayedCallback.accept(entity);
      }
   }

   protected void doDelayedAction(E entity) {
   }
}
