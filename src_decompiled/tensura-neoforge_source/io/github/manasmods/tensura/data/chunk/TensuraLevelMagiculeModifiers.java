package io.github.manasmods.tensura.data.chunk;

import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import java.util.List;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class TensuraLevelMagiculeModifiers {
   public static void bootstrap(BootstrapContext<LevelMagiculeModifier> context) {
      register(context, new LevelMagiculeModifier(Level.OVERWORLD.location(), 0, List.of(), List.of()));
      register(
         context,
         new LevelMagiculeModifier(
            Level.NETHER.location(),
            0,
            List.of(new DataPackMagiculeModifier(DataPackMagiculeModifier.Mode.MULTIPLY, 1.2)),
            List.of(new DataPackMagiculeModifier(DataPackMagiculeModifier.Mode.MULTIPLY, 1.25))
         )
      );
      register(
         context,
         new LevelMagiculeModifier(
            Level.END.location(),
            0,
            List.of(new DataPackMagiculeModifier(DataPackMagiculeModifier.Mode.MULTIPLY, 1.5)),
            List.of(new DataPackMagiculeModifier(DataPackMagiculeModifier.Mode.MULTIPLY, 1.75))
         )
      );
      register(
         context,
         new LevelMagiculeModifier(
            TensuraDimensions.LABYRINTH.location(),
            0,
            List.of(new DataPackMagiculeModifier(DataPackMagiculeModifier.Mode.ADD, 29500.0)),
            List.of(new DataPackMagiculeModifier(DataPackMagiculeModifier.Mode.MULTIPLY, 2.0))
         )
      );
      register(
         context,
         new LevelMagiculeModifier(
            TensuraDimensions.HELL.location(),
            0,
            List.of(new DataPackMagiculeModifier(DataPackMagiculeModifier.Mode.ADD, 100000.0)),
            List.of(new DataPackMagiculeModifier(DataPackMagiculeModifier.Mode.MULTIPLY, 2.0))
         )
      );
   }

   public static void register(BootstrapContext<LevelMagiculeModifier> context, LevelMagiculeModifier data) {
      ResourceKey<LevelMagiculeModifier> key = ResourceKey.create(TensuraCustomData.LEVEL_MAGICULE, data.worldId());
      context.register(key, data);
   }
}
