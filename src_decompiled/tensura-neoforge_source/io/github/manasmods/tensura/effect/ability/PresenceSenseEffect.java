package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class PresenceSenseEffect extends TensuraMobEffect {
   public PresenceSenseEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(88, 176, 204).getRGB());
      this.addAttributeModifier(TensuraAttributes.PRESENCE_SENSE, ResourceLocation.fromNamespaceAndPath("tensura", "presence_sense"), 1.0, Operation.ADD_VALUE);
   }
}
