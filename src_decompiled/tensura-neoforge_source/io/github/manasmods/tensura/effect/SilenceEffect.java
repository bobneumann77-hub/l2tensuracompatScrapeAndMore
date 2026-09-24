package io.github.manasmods.tensura.effect;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class SilenceEffect extends TensuraMobEffect {
   public static final ResourceLocation SILENCE = ResourceLocation.fromNamespaceAndPath("tensura", "silence");

   public SilenceEffect() {
      super(MobEffectCategory.HARMFUL, new Color(49, 49, 49).getRGB());
      this.addAttributeModifier(TensuraAttributes.CHANT_SPEED, SILENCE, -0.1F, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return false;
   }
}
