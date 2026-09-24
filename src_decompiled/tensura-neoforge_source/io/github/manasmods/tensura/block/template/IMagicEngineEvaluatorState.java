package io.github.manasmods.tensura.block.template;

import net.minecraft.world.entity.Mob;

public interface IMagicEngineEvaluatorState {
   boolean tensura$shouldSkipBlocking();

   Mob tensura$getMob();
}
