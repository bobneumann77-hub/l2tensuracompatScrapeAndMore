package io.github.manasmods.tensura.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class TensuraInputEvents {
   public static Event<TensuraInputEvents.MovementInputUpdateEvent> MOVEMENT_INPUT_UPDATE_EVENT = EventFactory.createLoop(
      new TensuraInputEvents.MovementInputUpdateEvent[0]
   );
   public static Event<TensuraInputEvents.NumberKeyPressClientEvent> SKILL_NUMBER_KEY_CLIENT = EventFactory.createEventResult(
      new TensuraInputEvents.NumberKeyPressClientEvent[0]
   );
   public static Event<TensuraInputEvents.NumberKeyPressEvent> SKILL_NUMBER_KEY = EventFactory.createEventResult(new TensuraInputEvents.NumberKeyPressEvent[0]);

   @FunctionalInterface
   public interface MovementInputUpdateEvent {
      void input(Player var1, Input var2);
   }

   @FunctionalInterface
   public interface NumberKeyPressClientEvent {
      EventResult press(ManasSkillInstance var1, LivingEntity var2, int var3);
   }

   @FunctionalInterface
   public interface NumberKeyPressEvent {
      EventResult press(Changeable<ManasSkillInstance> var1, LivingEntity var2, Changeable<Integer> var3);
   }
}
