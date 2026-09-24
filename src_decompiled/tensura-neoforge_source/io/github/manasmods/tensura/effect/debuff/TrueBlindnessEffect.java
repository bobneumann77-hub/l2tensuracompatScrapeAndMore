package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class TrueBlindnessEffect extends TensuraMobEffect {
   public TrueBlindnessEffect() {
      super(MobEffectCategory.HARMFUL, new Color(0, 0, 0).getRGB());
      this.addAttributeModifier(TensuraAttributes.DARK_VISION, ResourceLocation.fromNamespaceAndPath("tensura", "true_blindness"), 0.6F, Operation.ADD_VALUE);
   }
}
