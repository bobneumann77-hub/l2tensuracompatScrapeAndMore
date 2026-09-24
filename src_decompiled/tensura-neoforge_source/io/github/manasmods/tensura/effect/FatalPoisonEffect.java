package io.github.manasmods.tensura.effect;

import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.awt.Color;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class FatalPoisonEffect extends TensuraMobEffect {
   public FatalPoisonEffect() {
      super(MobEffectCategory.HARMFUL, new Color(82, 0, 143).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.getHealth() > 0.0F) {
         if (entity.level() instanceof ServerLevel level) {
            MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON));
            if (instance == null) {
               return true;
            }

            float damage = 2.0F * (pAmplifier + 1);
            entity.hurt(
               TensuraDamageHelper.getUUIDDamageSource(
                     TensuraDamageTypes.FATAL_POISON, level, instance.tensura$getSource(), instance.tensura$getSourceAbility()
                  )
                  .tensura$setDodgeBypass(),
               damage
            );
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 20 == 0;
   }
}
