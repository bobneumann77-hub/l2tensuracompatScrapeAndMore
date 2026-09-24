package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import io.github.manasmods.tensura.storage.ep.IExistence;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class MindControlEffect extends TensuraMobEffect {
   public MindControlEffect() {
      super(MobEffectCategory.HARMFUL, new Color(222, 13, 104).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.level() instanceof ServerLevel level) {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL));
         if (instance == null) {
            return true;
         } else {
            AbilitySlot slot = instance.tensura$hasAbility() ? instance.tensura$getSourceAbility() : null;
            if (slot == null) {
               return true;
            } else {
               return (instance.tensura$hasSource() ? level.getEntity(instance.tensura$getSource()) : null) instanceof LivingEntity living
                  ? SkillUtils.hasSkill(living, slot.getSkill())
                  : true;
            }
         }
      } else {
         return true;
      }
   }

   @Override
   public void onAttributeRemoved(LivingEntity target, MobEffectInstance instance) {
      IExistence existence = TensuraStorages.getExistenceFrom(target);
      if (instance.tensura$getSource() == null || Objects.equals(existence.getTemporaryOwner(), instance.tensura$getSource())) {
         existence.setTemporaryOwner(null);
         if (target instanceof ISubordinate subordinate) {
            subordinate.resetOwner(existence.getPermanentOwner());
         }
      }

      if (instance.tensura$getSource() != null) {
         existence.removeNeutralTarget(instance.tensura$getSource());
      }
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 60 == 0;
   }
}
