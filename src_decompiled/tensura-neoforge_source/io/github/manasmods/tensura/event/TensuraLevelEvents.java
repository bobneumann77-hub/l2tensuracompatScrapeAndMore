package io.github.manasmods.tensura.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;

public class TensuraLevelEvents {
   public static Event<Runnable> POST_INIT = EventFactory.createLoop(new Runnable[0]);
   public static Event<TensuraLevelEvents.ChunkTickEvent> CHUNK_TICK_PRE = EventFactory.createLoop(new TensuraLevelEvents.ChunkTickEvent[0]);
   public static Event<TensuraLevelEvents.ChunkTickEvent> CHUNK_TICK_POST = EventFactory.createLoop(new TensuraLevelEvents.ChunkTickEvent[0]);
   public static Event<TensuraLevelEvents.LevelPreparedEvent> LEVEL_PREPARED = EventFactory.createEventResult(new TensuraLevelEvents.LevelPreparedEvent[0]);

   @FunctionalInterface
   public interface ChunkTickEvent {
      void tick(ServerLevel var1, LevelChunk var2);
   }

   @FunctionalInterface
   public interface LevelPreparedEvent {
      void load(Level var1, Holder<DimensionType> var2);
   }
}
