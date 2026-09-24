package dev.xkmc.l2hostility.events;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkCapHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkCapSyncToClient;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkDifficulty;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.capability.player.PlayerDifficulty;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.StartTracking;
import net.neoforged.neoforge.event.level.ChunkWatchEvent.Sent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;

@EventBusSubscriber(modid = "l2hostility", bus = Bus.GAME)
public class CapabilityEvents {
   private static final Set<ChunkCapHolder> PENDING = new LinkedHashSet<>();

   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      if (event.getTarget() instanceof LivingEntity entity && event.getEntity() instanceof ServerPlayer player) {
         ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(entity).ifPresent(e -> e.syncToPlayer(entity, player));
      }
   }

   private static boolean initMob(LivingEntity mob, MobSpawnType type) {
      if (((GeneralCapabilityHolder)LHMiscs.MOB.type()).isProper(mob)) {
         MobTraitCap cap = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(mob);
         if (!mob.level().isClientSide() && !cap.isInitialized()) {
            Optional<ChunkCapHolder> opt = ChunkDifficulty.at(mob.level(), mob.blockPosition());
            if (opt.isPresent()) {
               cap.init(mob.level(), mob, opt.get());
               if (type == MobSpawnType.NATURAL && cap.shouldDiscard(mob)) {
                  return true;
               }

               if (type == MobSpawnType.SPAWNER) {
                  cap.dropRate = (Double)LHConfig.SERVER.dropRateFromSpawner.get();
               }
            }
         }
      }

      return false;
   }

   @SubscribeEvent
   public static void onEntitySpawn(FinalizeSpawnEvent event) {
      LivingEntity mob = event.getEntity();
      if (initMob(mob, event.getSpawnType())) {
         event.setSpawnCancelled(true);
      }
   }

   @SubscribeEvent
   public static void livingTickEvent(Post event) {
      if (event.getEntity() instanceof LivingEntity mob) {
         if (mob.tickCount % 10 == 0) {
            if (Float.isNaN(mob.getHealth())) {
               mob.setHealth(0.0F);
            }

            if (Float.isNaN(mob.getAbsorptionAmount())) {
               mob.setAbsorptionAmount(0.0F);
            }
         }

         Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(mob);
         if (!opt.isEmpty() || ((GeneralCapabilityHolder)LHMiscs.MOB.type()).isProper(mob)) {
            MobTraitCap cap = opt.orElse((MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(mob));
            cap.tick(mob);
         }
      }
   }

   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      LivingEntity mob = event.getEntity();
      if (!mob.level().isClientSide()) {
         LivingEntity killer = event.getEntity().getKillCredit();
         ServerPlayer player = null;
         if (killer instanceof ServerPlayer pl) {
            player = pl;
         } else if (killer instanceof OwnableEntity own && own.getOwner() instanceof ServerPlayer pl) {
            player = pl;
         }

         Optional<MobTraitCap> optCap = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(mob);
         if (optCap.isPresent()) {
            MobTraitCap cap = optCap.get();
            if (killer != null) {
               cap.onKilled(mob, player);
            }

            if (player != null) {
               PlayerDifficulty playerDiff = (PlayerDifficulty)((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).getOrCreate(player);
               playerDiff.addKillCredit(player, cap);
               LevelChunk chunk = mob.level().getChunkAt(mob.blockPosition());
               new ChunkCapHolder(chunk, (ChunkDifficulty)((GeneralCapabilityHolder)LHMiscs.CHUNK.type()).getOrCreate(chunk)).addKillHistory(player, mob, cap);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onServerTick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
      for (ChunkCapHolder e : PENDING) {
         L2Hostility.toTrackingChunk(e.chunk(), ChunkCapSyncToClient.of(e));
      }

      PENDING.clear();
   }

   @SubscribeEvent
   public static void onStartTrackingChunk(Sent event) {
      ChunkDifficulty opt = (ChunkDifficulty)((GeneralCapabilityHolder)LHMiscs.CHUNK.type()).getOrCreate(event.getChunk());
      L2Hostility.HANDLER.toClientPlayer(ChunkCapSyncToClient.of(new ChunkCapHolder(event.getChunk(), opt)), event.getPlayer());
   }

   public static void markDirty(ChunkCapHolder chunk) {
      PENDING.add(chunk);
   }
}
