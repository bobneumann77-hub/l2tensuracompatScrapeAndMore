package io.github.manasmods.tensura.effect;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class IllusionBoostEffect extends TensuraMobEffect {
   private static final ResourceLocation BOOST = ResourceLocation.fromNamespaceAndPath("tensura", "illusion_boost");

   public IllusionBoostEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(189, 10, 248).getRGB());
      this.addAttributeModifier(TensuraAttributes.ILLUSION_BOOST, BOOST, 0.3F, Operation.ADD_VALUE);
   }
}
