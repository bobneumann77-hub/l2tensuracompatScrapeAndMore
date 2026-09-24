package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.awt.Color;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DisintegratingEffect extends TensuraMobEffect {
   public DisintegratingEffect() {
      super(MobEffectCategory.HARMFUL, new Color(246, 196, 16).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.level() instanceof ServerLevel level) {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.DISINTEGRATING));
         if (instance == null) {
            return true;
         }

         Entity source = instance.tensura$hasSource() ? level.getEntity(instance.tensura$getSource()) : null;
         DamageSource damageSource = TensuraDamageHelper.getUUIDDamageSource(
               TensuraDamageTypes.HOLY_DAMAGE, level, instance.tensura$getSource(), instance.tensura$getSourceAbility()
            )
            .tensura$setElement(Element.HOLY)
            .tensura$setBarrierBypassLevel(3.0F)
            .tensura$setDodgeBypass();
         float damage = 100.0F * (pAmplifier + 1);
         entity.hurt(damageSource, damage);
         TensuraDamageHelper.directSpiritualHurt(entity, source, damageSource, damage);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 10 == 0;
   }
}
