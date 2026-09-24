package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.extra.BlackFlameSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import java.awt.Color;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class BlackBurnEffect extends TensuraMobEffect {
   public BlackBurnEffect() {
      super(MobEffectCategory.HARMFUL, new Color(33, 32, 32).getRGB());
   }

   public void onEffectStarted(LivingEntity entity, int i) {
      super.onEffectStarted(entity, i);
      IEffect effect = TensuraStorages.getEffectFrom(entity);
      effect.setOnBlackFlame(true);
      effect.markDirty();
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.level() instanceof ServerLevel level) {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.BLACK_BURN));
         if (instance == null) {
            return true;
         }

         float damage = (pAmplifier + 1) * BlackFlameSkill.CONFIG.blackBurnDamage;
         DamageSource damageSource = TensuraDamageHelper.getUUIDDamageSource(
               TensuraDamageTypes.BLACK_FLAME, level, instance.tensura$getSource(), instance.tensura$getSourceAbility()
            )
            .tensura$setElement(Element.FLAME)
            .tensura$setDodgeBypass();
         TensuraDamageHelper.hurtSplitElemental(entity, damageSource, 0.9F, damage);
      }

      return true;
   }

   @Override
   public void onAttributeRemoved(LivingEntity entity, MobEffectInstance instance) {
      IEffect effect = TensuraStorages.getEffectFrom(entity);
      effect.setOnBlackFlame(false);
      effect.markDirty();
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 20 == 0;
   }
}
