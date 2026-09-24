package io.github.manasmods.tensura.registry.world;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.gameevent.GameEvent;

public class TensuraGameEvents {
   private static final DeferredRegister<GameEvent> EVENTS = DeferredRegister.create("tensura", Registries.GAME_EVENT);
   public static RegistrySupplier<GameEvent> AFTER_CHEAT_DEATH = registerGameEvent("after_cheat_death");

   private static RegistrySupplier<GameEvent> registerGameEvent(String name) {
      return EVENTS.register(name, () -> new GameEvent(16));
   }

   public static void init() {
      EVENTS.register();
   }
}
