package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.common.SelfRegenerationSkill;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import java.awt.Color;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SelfRegenerationEffect extends TensuraMobEffect {
   public SelfRegenerationEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(248, 113, 113).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.getHealth() >= entity.getMaxHealth() || !entity.isAlive()) {
         return true;
      }

      if (SkillUtils.shouldCancelHealing(entity)) {
         return true;
      }

      entity.heal(SelfRegenerationSkill.CONFIG.regenHP * (pAmplifier + 1));
      if (pAmplifier > 0) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         existence.setSpiritualHealth(
            Math.min(
               existence.getSpiritualHealth() + SelfRegenerationSkill.CONFIG.regenSHP * (pAmplifier + 1),
               entity.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH)
            )
         );
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration > 0 && pDuration % 20 == 0;
   }
}
