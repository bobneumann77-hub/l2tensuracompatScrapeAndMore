package dev.xkmc.l2hostility.content.capability.chunk;

import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.config.WorldDifficultyConfig;
import dev.xkmc.l2hostility.content.logic.DifficultyLevel;
import dev.xkmc.l2hostility.content.logic.LevelEditor;
import dev.xkmc.l2hostility.content.logic.MobDifficultyCollector;
import dev.xkmc.l2hostility.events.CapabilityEvents;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;

@SerialClass
public class SectionDifficulty {
   @SerialField
   int index;
   @SerialField
   public BlockPos activePos = null;
   @SerialField
   private final DifficultyLevel difficulty = new DifficultyLevel();
   @SerialField
   private SectionDifficulty.SectionStage stage = SectionDifficulty.SectionStage.INIT;
   LevelChunkSection section;

   public static Optional<SectionDifficulty> sectionAt(Level level, BlockPos pos) {
      return ChunkDifficulty.at(level, pos).map(e -> e.getSection(pos.getY()));
   }

   public void modifyInstance(Level level, BlockPos pos, MobDifficultyCollector instance) {
      this.modifyInstanceInternal(level, pos, instance);
      if ((Boolean)LHConfig.SERVER.allowSectionDifficulty.get()) {
         instance.acceptBonusLevel(this.difficulty.getLevel());
      }

      if (this.stage == SectionDifficulty.SectionStage.CLEARED) {
         instance.setCap(0);
      }
   }

   private void modifyInstanceInternal(Level level, BlockPos pos, MobDifficultyCollector instance) {
      WorldDifficultyConfig.DifficultyConfig levelDiff = ((WorldDifficultyConfig)L2Hostility.DIFFICULTY.getMerged()).levelMap.get(level.dimension().location());
      if (levelDiff == null) {
         levelDiff = WorldDifficultyConfig.defaultLevel();
      }

      instance.acceptConfig(levelDiff);
      Holder<Biome> biome = level.getBiome(pos);
      biome.unwrapKey().map(e -> ((WorldDifficultyConfig)L2Hostility.DIFFICULTY.getMerged()).biomeMap.get(e.location())).ifPresent(instance::acceptConfig);
      instance.acceptBonusLevel(
         (int)Math.round((Double)LHConfig.SERVER.distanceFactor.get() * Math.sqrt(1.0 * pos.getX() * pos.getX() + 1.0 * pos.getZ() * pos.getZ()))
      );
   }

   public List<Component> getSectionDifficultyDetail(Player player) {
      if (this.isCleared()) {
         return List.of();
      }

      WorldDifficultyConfig.DifficultyConfig levelDiff = ((WorldDifficultyConfig)L2Hostility.DIFFICULTY.getMerged())
         .levelMap
         .get(player.level().dimension().location());
      int dim = levelDiff == null ? WorldDifficultyConfig.defaultLevel().base() : levelDiff.base();
      BlockPos pos = player.blockPosition();
      Holder<Biome> biome = player.level().getBiome(pos);
      int bio = biome.unwrapKey()
         .map(e -> ((WorldDifficultyConfig)L2Hostility.DIFFICULTY.getMerged()).biomeMap.get(e.location()))
         .map(WorldDifficultyConfig.DifficultyConfig::base)
         .orElse(0);
      int dist = (int)Math.round((Double)LHConfig.SERVER.distanceFactor.get() * Math.sqrt(pos.getX() * pos.getX() + pos.getZ() * pos.getZ()));
      int adaptive = this.difficulty.getLevel();
      return List.of(
         LangData.INFO_SECTION_DIM_LEVEL.get(dim).withStyle(ChatFormatting.GRAY),
         LangData.INFO_SECTION_BIOME_LEVEL.get(bio).withStyle(ChatFormatting.GRAY),
         LangData.INFO_SECTION_DISTANCE_LEVEL.get(dist).withStyle(ChatFormatting.GRAY),
         LangData.INFO_SECTION_ADAPTIVE_LEVEL.get(adaptive).withStyle(ChatFormatting.GRAY)
      );
   }

   public boolean isCleared() {
      return this.stage == SectionDifficulty.SectionStage.CLEARED;
   }

   public boolean setClear(ChunkCapHolder chunk, BlockPos pos) {
      if (this.stage == SectionDifficulty.SectionStage.CLEARED) {
         return false;
      }

      this.stage = SectionDifficulty.SectionStage.CLEARED;
      CapabilityEvents.markDirty(chunk);
      chunk.chunk().setUnsaved(true);
      return true;
   }

   public boolean setUnclear(ChunkCapHolder chunk, BlockPos pos) {
      if (this.stage == SectionDifficulty.SectionStage.INIT) {
         return false;
      }

      this.stage = SectionDifficulty.SectionStage.INIT;
      CapabilityEvents.markDirty(chunk);
      chunk.chunk().setUnsaved(true);
      return true;
   }

   public void addKillHistory(ChunkCapHolder chunk, Player player, LivingEntity mob, MobTraitCap cap) {
      this.difficulty.grow(1.0, cap);
      CapabilityEvents.markDirty(chunk);
      chunk.chunk().setUnsaved(true);
   }

   public LevelEditor getLevelEditor(Level level, BlockPos pos) {
      MobDifficultyCollector col = new MobDifficultyCollector();
      this.modifyInstanceInternal(level, pos, col);
      DifficultyLevel diff = LHConfig.SERVER.allowSectionDifficulty.get() ? this.difficulty : new DifficultyLevel();
      return new LevelEditor(diff, col.getBase());
   }

   public double getScale(Level level, BlockPos pos) {
      MobDifficultyCollector col = new MobDifficultyCollector();
      this.modifyInstanceInternal(level, pos, col);
      return col.scale;
   }

   public enum SectionStage {
      INIT,
      CLEARED;
   }
}
