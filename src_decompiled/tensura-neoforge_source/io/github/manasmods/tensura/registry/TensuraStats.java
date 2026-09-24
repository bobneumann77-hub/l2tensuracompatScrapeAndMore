package io.github.manasmods.tensura.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class TensuraStats {
   private static final DeferredRegister<StatType<?>> STATS = DeferredRegister.create("tensura", Registries.STAT_TYPE);
   private static final DeferredRegister<ResourceLocation> CUSTOM_STATS = DeferredRegister.create("tensura", Registries.CUSTOM_STAT);
   public static final RegistrySupplier<StatType<EntityType<?>>> BOSS_KILLED = registerStatType("boss_killed", BuiltInRegistries.ENTITY_TYPE);
   public static final ResourceLocation BATTLEWILL_LEARNT = makeCustomStat("battlewill_learnt");
   public static final ResourceLocation BATTLEWILL_MASTERED = makeCustomStat("battlewill_mastered");
   public static final ResourceLocation MAGIC_LEARNT = makeCustomStat("magic_learnt");
   public static final ResourceLocation MAGIC_MASTERED = makeCustomStat("magic_mastered");
   public static final ResourceLocation SKILL_LEARNT = makeCustomStat("skill_learnt");
   public static final ResourceLocation SKILL_MASTERED = makeCustomStat("skill_mastered");
   public static final ResourceLocation SPIRIT_PRAY_TIME = makeCustomStat("spirit_pray_time");
   public static final ResourceLocation SPIRIT_PRAY_FAIL_TIME = makeCustomStat("spirit_pray_fail_time");
   public static final ResourceLocation SPIRIT_CONTRACTED_TIME = makeCustomStat("spirit_contracted_time");
   public static final ResourceLocation ENTITY_NAMED = makeCustomStat("entity_named");
   public static final ResourceLocation BOSS_DEFEATED = makeCustomStat("boss_defeated");

   private static <T> RegistrySupplier<StatType<T>> registerStatType(String string, Registry<T> registry) {
      Component component = Component.translatable("stat_type.tensura." + string);
      return STATS.register(string, () -> new StatType(registry, component));
   }

   private static ResourceLocation makeCustomStat(String string) {
      ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("tensura", string);
      CUSTOM_STATS.register(string, () -> resourceLocation);
      return resourceLocation;
   }

   public static void init() {
      STATS.register();
      CUSTOM_STATS.register();
   }

   public static int getBossDefeated(Player player) {
      StatsCounter statsCounter;
      if (player.isLocalPlayer()) {
         statsCounter = ((LocalPlayer)player).getStats();
      } else {
         if (!(player instanceof ServerPlayer serverPlayer)) {
            return 0;
         }

         statsCounter = serverPlayer.getStats();
      }

      return statsCounter.getValue(Stats.CUSTOM.get(BOSS_DEFEATED));
   }
}
