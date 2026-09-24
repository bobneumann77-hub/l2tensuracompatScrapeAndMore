package dev.xkmc.l2hostility.content.item.spawner;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkCapHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkDifficulty;
import dev.xkmc.l2hostility.content.capability.chunk.SectionDifficulty;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

@SerialClass
public class BurstSpawnerBlockEntity extends TraitSpawnerBlockEntity {
   private static WeightedRandomList<SpawnerData> mobsAt(ServerLevel level, BlockPos pos) {
      StructureManager structure = level.structureManager();
      ChunkGenerator chunkGen = level.getChunkSource().getGenerator();
      Holder<Biome> biome = level.getBiome(pos);
      return EventHooks.getPotentialSpawns(level, MobCategory.MONSTER, pos, chunkGen.getMobsAt(biome, structure, MobCategory.MONSTER, pos));
   }

   public static int getSpawnGroup() {
      return (Integer)LHConfig.SERVER.hostilitySpawnCount.get();
   }

   public static double getBonusFactor() {
      return ((Integer)LHConfig.SERVER.hostilitySpawnLevelFactor.get()).intValue();
   }

   public static int getMaxTrials() {
      return 4;
   }

   public BurstSpawnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   @Override
   protected void generate(TraitSpawnerData data) {
      if (this.level instanceof ServerLevel sl) {
         Optional<ChunkCapHolder> cdcap = ChunkDifficulty.at(this.level, this.getBlockPos());
         if (!cdcap.isEmpty()) {
            SectionDifficulty sec = cdcap.get().getSection(this.getBlockPos().getY());
            if (sec.activePos != null
               && this.level.isLoaded(sec.activePos)
               && this.level.getBlockEntity(sec.activePos) instanceof BurstSpawnerBlockEntity other
               && other != this) {
               other.stop();
            }

            sec.activePos = this.getBlockPos();
            int count = 0;

            for (int i = 0; i < getSpawnGroup() * getMaxTrials(); i++) {
               int x = this.level.getRandom().nextInt(16);
               int y = this.level.getRandom().nextInt(16);
               int z = this.level.getRandom().nextInt(16);
               BlockPos pos = new BlockPos(this.getBlockPos().getX() & -8 | x, this.getBlockPos().getY() & -8 | y, this.getBlockPos().getZ() & -8 | z);
               Optional<SpawnerData> e = mobsAt(sl, pos).getRandom(this.level.getRandom());
               if (e.isPresent() && !e.get().type.is(LHTagGen.HOSTILITY_SPAWNER_BLACKLIST)) {
                  Entity entity = e.get().type.create(sl);
                  if (entity != null && !(entity instanceof Creeper)) {
                     entity.setPos(Vec3.atCenterOf(this.getBlockPos()));
                     if (entity instanceof LivingEntity le) {
                        MobTraitCap cap = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(le);
                        cap.summoned = true;
                        cap.noDrop = true;
                        cap.pos = this.getBlockPos();
                        cap.init(this.level, le, (a, b) -> {
                           cdcap.get().modifyInstance(a, b);
                           b.acceptBonusFactor(getBonusFactor());
                           b.setFullChance();
                        });
                        entity.setPos(Vec3.atCenterOf(this.getBlockPos().above()));
                        data.add(le);
                        this.level.addFreshEntity(entity);
                        if (++count >= getSpawnGroup()) {
                           break;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   protected void clearStage() {
      assert this.level != null;
      Optional<ChunkCapHolder> cdcap = ChunkDifficulty.at(this.level, this.getBlockPos());
      if (cdcap.isPresent()) {
         SectionDifficulty section = cdcap.get().getSection(this.getBlockPos().getY());
         section.setClear(cdcap.get(), this.getBlockPos());
         section.activePos = null;
      }
   }

   @Override
   protected CustomBossEvent createBossEvent() {
      CustomBossEvent ans = new CustomBossEvent(
         L2Hostility.loc("hostility_spawner"), LangData.BOSS_EVENT.get(0, getSpawnGroup()).withStyle(ChatFormatting.GOLD)
      );
      ans.setColor(BossBarColor.PURPLE);
      ans.setOverlay(BossBarOverlay.NOTCHED_10);
      return ans;
   }
}
