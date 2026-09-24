package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.magic.aspectual.misc.HealthcareMagic;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HealthcareEffect extends TensuraMobEffect {
   public HealthcareEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(174, 248, 113).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.getHealth() >= entity.getMaxHealth() || !entity.isAlive()) {
         return true;
      }

      if (SkillUtils.shouldCancelHealing(entity)) {
         return true;
      }

      entity.heal(HealthcareMagic.CONFIG.healthRegeneration * (pAmplifier + 1));
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 40 == 0;
   }
}
