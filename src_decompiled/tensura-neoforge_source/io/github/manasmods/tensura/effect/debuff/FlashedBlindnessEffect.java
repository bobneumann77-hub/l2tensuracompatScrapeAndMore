package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class FlashedBlindnessEffect extends TensuraMobEffect {
   public FlashedBlindnessEffect() {
      super(MobEffectCategory.HARMFUL, new Color(255, 255, 255).getRGB());
      this.addAttributeModifier(
         TensuraAttributes.DARK_VISION, ResourceLocation.fromNamespaceAndPath("tensura", "reverse_blindness"), -0.1F, Operation.ADD_VALUE
      );
   }
}
