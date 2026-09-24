package dev.xkmc.l2hostility.init.advancements;

import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;

public class KillTraitsTrigger extends BaseCriterion<KillTraitsTrigger.Ins, KillTraitsTrigger> {
   public static Criterion<KillTraitsTrigger.Ins> ins(MobTrait... traits) {
      KillTraitsTrigger.Ins ans = new KillTraitsTrigger.Ins();
      ans.traits = traits;
      return ans.build();
   }

   public KillTraitsTrigger() {
      super(KillTraitsTrigger.Ins.class);
   }

   public void trigger(ServerPlayer player, MobTraitCap cap) {
      this.trigger(player, e -> e.matchAll(cap));
   }

   @SerialClass
   public static class Ins extends BaseCriterionInstance<KillTraitsTrigger.Ins, KillTraitsTrigger> {
      @SerialField
      public MobTrait[] traits;

      public Ins() {
         super((KillTraitsTrigger)HostilityTriggers.KILL_TRAITS.get());
      }

      public boolean matchAll(MobTraitCap cap) {
         if (cap.traits.isEmpty()) {
            return false;
         }

         for (MobTrait e : this.traits) {
            if (!cap.hasTrait(e)) {
               return false;
            }
         }

         return true;
      }
   }
}
