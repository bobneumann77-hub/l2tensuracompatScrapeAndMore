package dev.xkmc.l2hostility.init.advancements;

import dev.xkmc.l2core.serial.advancements.BaseCriterion;
import dev.xkmc.l2core.serial.advancements.BaseCriterionInstance;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.function.Predicate;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class KillTraitFlameTrigger extends BaseCriterion<KillTraitFlameTrigger.Ins, KillTraitFlameTrigger> {
   public static Criterion<KillTraitFlameTrigger.Ins> ins(MobTrait traits, KillTraitFlameTrigger.Type effect) {
      KillTraitFlameTrigger.Ins ans = new KillTraitFlameTrigger.Ins();
      ans.trait = traits;
      ans.effect = effect;
      return ans.build();
   }

   public KillTraitFlameTrigger(ResourceLocation id) {
      super(KillTraitFlameTrigger.Ins.class);
   }

   public void trigger(ServerPlayer player, LivingEntity le, MobTraitCap cap) {
      this.trigger(player, e -> e.matchAll(le, cap));
   }

   @SerialClass
   public static class Ins extends BaseCriterionInstance<KillTraitFlameTrigger.Ins, KillTraitFlameTrigger> {
      @SerialField
      public MobTrait trait;
      @SerialField
      public KillTraitFlameTrigger.Type effect;

      public Ins() {
         super((KillTraitFlameTrigger)HostilityTriggers.TRAIT_FLAME.get());
      }

      public boolean matchAll(LivingEntity le, MobTraitCap cap) {
         return cap.hasTrait(this.trait) && this.effect.match(le);
      }
   }

   public enum Type {
      FLAME(Entity::isOnFire);

      private final Predicate<LivingEntity> pred;

      Type(Predicate<LivingEntity> pred) {
         this.pred = pred;
      }

      public boolean match(LivingEntity le) {
         return this.pred.test(le);
      }
   }
}
