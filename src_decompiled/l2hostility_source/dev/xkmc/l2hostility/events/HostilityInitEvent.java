package dev.xkmc.l2hostility.events;

import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public abstract class HostilityInitEvent extends LivingEvent {
   private final MobTraitCap cap;
   private final HostilityInitEvent.InitPhase phase;

   public HostilityInitEvent(LivingEntity mob, MobTraitCap cap, HostilityInitEvent.InitPhase phase) {
      super(mob);
      this.cap = cap;
      this.phase = phase;
   }

   public MobTraitCap getData() {
      return this.cap;
   }

   public HostilityInitEvent.InitPhase getPhase() {
      return this.phase;
   }

   public enum InitPhase {
      INIT,
      COPY,
      ARMOR,
      WEAPON;
   }

   public static class Post extends HostilityInitEvent {
      public Post(LivingEntity mob, MobTraitCap cap, HostilityInitEvent.InitPhase phase) {
         super(mob, cap, phase);
      }
   }

   public static class Pre extends HostilityInitEvent implements ICancellableEvent {
      public Pre(LivingEntity mob, MobTraitCap cap, HostilityInitEvent.InitPhase phase) {
         super(mob, cap, phase);
      }
   }
}
