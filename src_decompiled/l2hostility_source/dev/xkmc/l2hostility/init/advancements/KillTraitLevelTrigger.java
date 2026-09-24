package dev.xkmc.l2hostility.init.advancements;

import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.server.level.ServerPlayer;

public class KillTraitLevelTrigger extends BaseCriterion<KillTraitLevelTrigger.Ins, KillTraitLevelTrigger> {
   public static KillTraitLevelTrigger.Ins ins(MobTrait traits, int rank) {
      KillTraitLevelTrigger.Ins ans = new KillTraitLevelTrigger.Ins();
      ans.trait = traits;
      ans.rank = rank;
      return ans;
   }

   public KillTraitLevelTrigger() {
      super(KillTraitLevelTrigger.Ins.class);
   }

   public void trigger(ServerPlayer player, MobTraitCap cap) {
      this.trigger(player, e -> e.matchAll(cap));
   }

   @SerialClass
   public static class Ins extends BaseCriterionInstance<KillTraitLevelTrigger.Ins, KillTraitLevelTrigger> {
      @SerialField
      public MobTrait trait;
      @SerialField
      public int rank;

      public Ins() {
         super((KillTraitLevelTrigger)HostilityTriggers.TRAIT_LEVEL.get());
      }

      public boolean matchAll(MobTraitCap cap) {
         return this.trait != null && cap.getTraitLevel(this.trait) >= this.rank;
      }
   }
}
