package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class ConfusionEffect extends TensuraMobEffect {
   public ConfusionEffect() {
      super(MobEffectCategory.HARMFUL, new Color(98, 49, 136).getRGB());
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("tensura", "confusion"), -0.15F, Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      MobEffectInstance confusion = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.CONFUSION));
      if (confusion == null) {
         return true;
      }

      TensuraMobEffect.addEffect(
         entity, MobEffects.CONFUSION, 80, pAmplifier, false, false, false, confusion.tensura$getSource(), confusion.tensura$getSourceAbility()
      );
      TensuraMobEffect.addEffect(
         entity, MobEffects.DARKNESS, 60, pAmplifier, false, false, false, confusion.tensura$getSource(), confusion.tensura$getSourceAbility()
      );
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 20 == 0;
   }
}
