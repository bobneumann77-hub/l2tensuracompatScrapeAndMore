package dev.xkmc.l2hostility.content.item.traits;

import dev.xkmc.l2core.base.effects.EffectBuilder;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import java.util.ArrayList;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class EffectBooster {
   public static void boostCharge(LivingEntity target) {
      double factor = 1.0 + (Double)LHConfig.SERVER.drainDuration.get();
      int maxTime = (Integer)LHConfig.SERVER.drainDurationMax.get();
      int min = (Integer)LHConfig.SERVER.witchChargeMinDuration.get();
      boost(target, e -> ((MobEffect)e.value()).getCategory() == MobEffectCategory.HARMFUL, min, factor, maxTime);
   }

   public static void boostBottle(LivingEntity target) {
      double factor = 1.0 + (Double)LHConfig.SERVER.drainDuration.get();
      int maxTime = (Integer)LHConfig.SERVER.drainDurationMax.get();
      int min = (Integer)LHConfig.SERVER.witchChargeMinDuration.get();
      boost(target, e -> true, min, factor, maxTime);
   }

   public static void boostTrait(LivingEntity target, double factor, int maxTime) {
      boost(target, e -> ((MobEffect)e.value()).getCategory() == MobEffectCategory.HARMFUL && !e.is(LHTagGen.DRAIN_IGNORE), 0, factor, maxTime);
   }

   private static void boost(LivingEntity target, Predicate<Holder<MobEffect>> pred, int min, double factor, int maxTime) {
      for (MobEffectInstance e : new ArrayList(target.getActiveEffects())) {
         if (pred.test(e.getEffect())) {
            int current = e.getDuration();
            if (current >= min) {
               int max = Math.min(maxTime, (int)(current * factor));
               if (max > current) {
                  new EffectBuilder(e).setDuration(max);
               }

               target.forceAddEffect(e, null);
            }
         }
      }
   }

   public static void boostInfinite(LivingEntity target) {
      int min = (Integer)LHConfig.SERVER.witchChargeMinDuration.get();

      for (MobEffectInstance e : new ArrayList(target.getActiveEffects())) {
         if (((MobEffect)e.getEffect().value()).getCategory() == MobEffectCategory.HARMFUL) {
            int current = e.getDuration();
            if (current >= min) {
               new EffectBuilder(e).setDuration(-1);
               target.forceAddEffect(e, null);
            }
         }
      }
   }
}
