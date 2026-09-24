package io.github.manasmods.tensura.data.otherworlder;

import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class TensuraOtherworlderSpawnDistribution {
   public static void bootstrap(BootstrapContext<OtherworlderSpawnDistribution> context) {
      register(context, OtherworlderSpawnDistribution.getDefault(HumanEntityTypes.FOLGEN.getId(), 0.125));
      register(context, OtherworlderSpawnDistribution.getDefault(HumanEntityTypes.KYOYA_TACHIBANA.getId(), 0.125));
      register(context, OtherworlderSpawnDistribution.getDefault(HumanEntityTypes.KIRARA_MIZUTANI.getId(), 0.125));
      register(context, OtherworlderSpawnDistribution.getDefault(HumanEntityTypes.MAI_FURUKI.getId(), 0.125));
      register(context, OtherworlderSpawnDistribution.getDefault(HumanEntityTypes.MARK_LAUREN.getId(), 0.125));
      register(context, OtherworlderSpawnDistribution.getDefault(HumanEntityTypes.SHIN_RYUSEI.getId(), 0.125));
      register(context, OtherworlderSpawnDistribution.getDefault(HumanEntityTypes.SHINJI_TANIMURA.getId(), 0.125));
      register(context, OtherworlderSpawnDistribution.getDefault(HumanEntityTypes.SHOGO_TAGUCHI.getId(), 0.125));
   }

   public static void register(BootstrapContext<OtherworlderSpawnDistribution> context, OtherworlderSpawnDistribution data) {
      ResourceKey<OtherworlderSpawnDistribution> key = ResourceKey.create(TensuraCustomData.OTHERWORLDER_SPAWN_DISTRIBUTION, data.entity());
      context.register(key, data);
   }
}
