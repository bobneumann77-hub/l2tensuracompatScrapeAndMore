package dev.xkmc.l2hostility.content.traits.base;

import dev.xkmc.l2core.base.effects.EffectUtil;
import dev.xkmc.l2hostility.content.item.curio.ring.RingOfReflection;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class TargetEffectTrait extends MobTrait {
   public final Function<Integer, MobEffectInstance> func;

   public TargetEffectTrait(Function<Integer, MobEffectInstance> func) {
      super(() -> ((MobEffect)func.apply(1).getEffect().value()).getColor());
      this.func = func;
   }

   public TargetEffectTrait(IntSupplier color, Function<Integer, MobEffectInstance> func) {
      super(color);
      this.func = func;
   }

   @Override
   public void postHurtImpl(int level, LivingEntity attacker, LivingEntity target) {
      if (((RingOfReflection)LHItems.RING_REFLECTION.get()).isOn(target)) {
         int radius = (Integer)LHConfig.SERVER.ringOfReflectionRadius.get();

         for (Entity e : target.level().getEntities(target, target.getBoundingBox().inflate(radius))) {
            if (e instanceof Mob mob && !(mob.distanceTo(target) > radius)) {
               EffectUtil.addEffect(mob, this.func.apply(level), attacker);
            }
         }
      } else {
         EffectUtil.addEffect(target, this.func.apply(level), attacker);
      }
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(LangData.TOOLTIP_TARGET_EFFECT.get());
      list.add(this.mapLevel(access, e -> {
         MobEffectInstance ins = this.func.apply(e);
         MutableComponent ans = Component.translatable(ins.getDescriptionId());
         MobEffect mobeffect = (MobEffect)ins.getEffect().value();
         if (ins.getAmplifier() > 0) {
            ans = Component.translatable("potion.withAmplifier", new Object[]{ans, Component.translatable("potion.potency." + ins.getAmplifier())});
         }

         if (!ins.endsWithin(20)) {
            ans = Component.translatable("potion.withDuration", new Object[]{ans, MobEffectUtil.formatDuration(ins, 1.0F, 20.0F)});
         }

         return ans.withStyle(mobeffect.getCategory().getTooltipFormatting());
      }));
   }
}
