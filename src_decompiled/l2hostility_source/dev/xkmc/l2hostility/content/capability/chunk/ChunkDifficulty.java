package dev.xkmc.l2hostility.content.capability.chunk;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityTemplate;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

@SerialClass
public class ChunkDifficulty extends GeneralCapabilityTemplate<LevelChunk, ChunkDifficulty> {
   private ChunkDifficulty.ChunkStage stage = ChunkDifficulty.ChunkStage.PRE_INIT;
   @SerialField
   protected SectionDifficulty[] sections;

   public static Optional<ChunkCapHolder> at(Level level, BlockPos pos) {
      return at(level, pos.getX() >> 4, pos.getZ() >> 4);
   }

   public static Optional<ChunkCapHolder> at(Level level, int x, int z) {
      ChunkAccess chunk = level.getChunk(x, z, ChunkStatus.CARVERS, false);
      if (chunk instanceof ImposterProtoChunk im) {
         chunk = im.getWrapped();
      }

      return chunk instanceof LevelChunk c
         ? Optional.of(new ChunkCapHolder(c, (ChunkDifficulty)((GeneralCapabilityHolder)LHMiscs.CHUNK.type()).getOrCreate(c)))
         : Optional.empty();
   }

   protected void check(LevelChunk chunk) {
      int size = chunk.getLevel().getSectionsCount();
      if (this.sections == null || this.sections.length != size || this.stage == ChunkDifficulty.ChunkStage.PRE_INIT) {
         this.stage = ChunkDifficulty.ChunkStage.INIT;
         if (this.sections == null || this.sections.length != size) {
            this.sections = new SectionDifficulty[size];

            for (int i = 0; i < size; i++) {
               this.sections[i] = new SectionDifficulty();
               this.sections[i].index = chunk.getMinSection() + i;
            }
         }

         for (int i = 0; i < size; i++) {
            this.sections[i].section = chunk.getSection(i);
         }
      }
   }

   public enum ChunkStage {
      PRE_INIT,
      INIT;
   }
}
