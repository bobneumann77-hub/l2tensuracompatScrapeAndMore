package io.github.manasmods.tensura.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import net.minecraft.world.entity.LivingEntity;

public class TensuraSpiritEvents {
   public static Event<TensuraSpiritEvents.SpiritLevelUpdate> SPIRIT_UPDATE = EventFactory.createEventResult(new TensuraSpiritEvents.SpiritLevelUpdate[0]);

   @FunctionalInterface
   public interface SpiritLevelUpdate {
      EventResult update(LivingEntity var1, Changeable<Element> var2, Changeable<SpiritualMagic.SpiritLevel> var3);
   }
}
