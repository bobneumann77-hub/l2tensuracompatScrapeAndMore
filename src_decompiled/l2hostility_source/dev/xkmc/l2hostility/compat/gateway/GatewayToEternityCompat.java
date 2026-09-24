package dev.xkmc.l2hostility.compat.gateway;

import com.mojang.datafixers.util.Pair;
import dev.shadowsoffire.gateways.entity.GatewayEntity;
import dev.shadowsoffire.gateways.event.GateEvent.WaveEntitySpawned;
import dev.shadowsoffire.gateways.gate.GatewayRegistry;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkCapHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkDifficulty;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;

public class GatewayToEternityCompat {
   private static final ThreadLocal<Pair<GatewayEntity, WaveData>> CURRENT = new ThreadLocal<>();

   @SubscribeEvent
   public static void onSpawn(WaveEntitySpawned event) {
      Pair<GatewayEntity, WaveData> prev = CURRENT.get();
      GatewayEntity gate = event.getEntity();
      int wave = event.getEntity().getWave();
      ResourceLocation rl = GatewayRegistry.INSTANCE.getKey(event.getEntity().getGateway());
      if (rl != null) {
         WaveId id = new WaveId(rl, wave);
         WaveData data;
         if (prev != null && gate == prev.getFirst() && ((WaveData)prev.getSecond()).id.equals(id)) {
            data = (WaveData)prev.getSecond();
         } else {
            CURRENT.set(Pair.of(gate, data = new WaveData(id)));
         }

         EntityConfig.Config config = ((EntityConfig)L2Hostility.ENTITY.getMerged()).get(event.getWaveEntity().getType(), rl, WaveData.class, data);
         if (config != null) {
            initMob(event.getWaveEntity(), config);
         }
      }
   }

   private static void initMob(LivingEntity mob, EntityConfig.Config config) {
      if (((GeneralCapabilityHolder)LHMiscs.MOB.type()).isProper(mob)) {
         MobTraitCap cap = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(mob);
         if (!mob.level().isClientSide() && !cap.isInitialized()) {
            Optional<ChunkCapHolder> opt = ChunkDifficulty.at(mob.level(), mob.blockPosition());
            if (opt.isPresent()) {
               cap.setConfigCache(config);
               cap.init(mob.level(), mob, opt.get());
               cap.dropRate = (Double)LHConfig.SERVER.dropRateFromSpawner.get();
            }
         }
      }
   }
}
