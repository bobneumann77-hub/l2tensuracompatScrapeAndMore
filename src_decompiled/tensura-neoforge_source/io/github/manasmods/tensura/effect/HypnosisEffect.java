package io.github.manasmods.tensura.effect;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HypnosisEffect extends TensuraMobEffect {
   public HypnosisEffect() {
      super(MobEffectCategory.HARMFUL, new Color(165, 48, 165).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.getRandom().nextFloat() <= 0.05 * (pAmplifier + 1)) {
         entity.setYRot(entity.getRandom().nextFloat() * 360.0F);
         entity.setYHeadRot(entity.getYRot());
         entity.setYBodyRot(entity.getYRot());
         entity.yRotO = entity.getYRot();
         entity.yHeadRotO = entity.getYRot();
         entity.yBodyRotO = entity.getYRot();
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 5 == 0;
   }
}
