package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class OppressionEffect extends TensuraMobEffect {
   public static final ResourceLocation OPPRESSION = ResourceLocation.fromNamespaceAndPath("tensura", "oppression");

   public OppressionEffect() {
      super(MobEffectCategory.HARMFUL, new Color(64, 58, 70).getRGB());
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, OPPRESSION, -0.95F, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, OPPRESSION, -0.95F, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      MobEffectInstance oppression = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.OPPRESSION));
      if (oppression == null) {
         return true;
      }

      int level = pAmplifier;
      MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY));
      if (instance != null) {
         level = instance.getAmplifier() + 1;
      }

      TensuraMobEffect.addEffect(
         entity,
         TensuraMobEffects.getReference(TensuraMobEffects.INSANITY),
         220,
         level,
         true,
         false,
         true,
         oppression.tensura$getSource(),
         oppression.tensura$getSourceAbility()
      );
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 200 == 0;
   }
}
