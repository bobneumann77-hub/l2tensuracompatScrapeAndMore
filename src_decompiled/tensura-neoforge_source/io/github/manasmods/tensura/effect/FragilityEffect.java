package io.github.manasmods.tensura.effect;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.awt.Color;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class FragilityEffect extends TensuraMobEffect implements DamageAction {
   public FragilityEffect() {
      super(MobEffectCategory.HARMFUL, new Color(98, 98, 100).getRGB());
   }

   @Override
   public boolean onBeingDamaged(LivingEntity entity, DamageSource source, Changeable<Float> amount) {
      MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY));
      if (instance == null) {
         return true;
      }

      float effectLevel = instance.getAmplifier() + 1;
      float newAmount = (Float)amount.get() * (1.0F + 0.2F * effectLevel);
      if (newAmount > Float.MAX_VALUE || Float.isNaN(newAmount)) {
         newAmount = Float.MAX_VALUE;
      }

      amount.set(newAmount);
      return true;
   }
}
