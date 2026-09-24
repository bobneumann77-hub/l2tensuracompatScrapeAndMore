package io.github.manasmods.tensura.mixin.accessor;

import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerGameMode.class)
public interface AccessorServerPlayerGameMode {
   @Accessor("previousGameModeForPlayer")
   @Mutable
   void setPreviousGameModeForPlayer(GameType var1);
}
