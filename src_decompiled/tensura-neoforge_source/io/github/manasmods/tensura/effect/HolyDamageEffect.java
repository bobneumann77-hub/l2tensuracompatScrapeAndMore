package io.github.manasmods.tensura.effect;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.awt.Color;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class HolyDamageEffect extends TensuraMobEffect {
   public HolyDamageEffect() {
      super(MobEffectCategory.NEUTRAL, new Color(255, 166, 4).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (RaceUtils.isAffectedByHolyExposure(entity) && entity.level() instanceof ServerLevel level) {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.HOLY_DAMAGE));
         if (instance == null) {
            return true;
         }

         int damage = 2 * (pAmplifier + 1);
         DamageSource damageSource = TensuraDamageHelper.getUUIDDamageSource(
               TensuraDamageTypes.HOLY_DAMAGE, level, instance.tensura$getSource(), instance.tensura$getSourceAbility()
            )
            .tensura$setDodgeBypass();
         entity.hurt(damageSource.tensura$setElement(Element.HOLY), damage);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 5 == 0;
   }
}
