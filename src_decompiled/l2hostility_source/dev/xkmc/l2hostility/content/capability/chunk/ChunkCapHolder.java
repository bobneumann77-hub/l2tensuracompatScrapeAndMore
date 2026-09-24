package dev.xkmc.l2hostility.content.capability.chunk;

import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.logic.MobDifficultyCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;

public record ChunkCapHolder(LevelChunk chunk, ChunkDifficulty cap) implements RegionalDifficultyModifier {
   public SectionDifficulty getSection(int y) {
      this.cap.check(this.chunk);
      int index = (y >> 4) - this.chunk.getMinSection();
      index = Mth.clamp(index, 0, this.cap.sections.length - 1);
      return this.cap.sections[index];
   }

   @Override
   public void modifyInstance(BlockPos pos, MobDifficultyCollector instance) {
      this.cap.check(this.chunk);
      this.getSection(pos.getY()).modifyInstance(this.chunk.getLevel(), pos, instance);
   }

   public void addKillHistory(Player player, LivingEntity mob, MobTraitCap cap) {
      this.cap().check(this.chunk);
      BlockPos pos = mob.blockPosition();
      int index = -this.chunk.getMinSection() + (pos.getY() >> 4);
      if (index >= 0 && index < this.cap().sections.length) {
         this.cap().sections[index].addKillHistory(this, player, mob, cap);
      }
   }
}
