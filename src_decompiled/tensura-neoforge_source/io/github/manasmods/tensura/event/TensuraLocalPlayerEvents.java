package io.github.manasmods.tensura.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public class TensuraLocalPlayerEvents {
   public static Event<TensuraLocalPlayerEvents.TensuraFovModifierEvent> FOV_MODIFIER_EVENT = EventFactory.createEventResult(
      new TensuraLocalPlayerEvents.TensuraFovModifierEvent[0]
   );

   public interface SETTINGS_EVENT {
      Event<TensuraLocalPlayerEvents.SETTINGS_EVENT.Load> LOAD = EventFactory.createEventResult(new TensuraLocalPlayerEvents.SETTINGS_EVENT.Load[0]);
      Event<TensuraLocalPlayerEvents.SETTINGS_EVENT.Save> SAVE = EventFactory.createEventResult(new TensuraLocalPlayerEvents.SETTINGS_EVENT.Save[0]);

      @FunctionalInterface
      interface Load {
         void loadSettings();
      }

      @FunctionalInterface
      interface Save {
         void saveSettings();
      }
   }

   @FunctionalInterface
   public interface TensuraFovModifierEvent {
      float fovModifier(Player var1, float var2);
   }
}
