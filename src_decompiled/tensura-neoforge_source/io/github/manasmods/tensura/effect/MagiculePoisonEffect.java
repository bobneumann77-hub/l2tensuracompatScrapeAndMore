package io.github.manasmods.tensura.effect;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class MagiculePoisonEffect extends TensuraMobEffect {
   public MagiculePoisonEffect() {
      super(MobEffectCategory.NEUTRAL, new Color(239, 5, 77).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      int level = pAmplifier + 1;
      DamageSource damageSource = TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.MAGICULE_POISON);
      entity.hurt(damageSource, level * 2);
      entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, level / 2, false, false, false));
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 20 == 0;
   }
}
