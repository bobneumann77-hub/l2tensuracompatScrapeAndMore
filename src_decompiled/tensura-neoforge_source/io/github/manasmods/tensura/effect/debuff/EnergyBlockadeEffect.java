package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class EnergyBlockadeEffect extends TensuraMobEffect {
   public EnergyBlockadeEffect() {
      super(MobEffectCategory.HARMFUL, new Color(204, 7, 83).getRGB());
      this.addAttributeModifier(
         TensuraAttributes.AURA_REGENERATION_MULTIPLIER,
         ResourceLocation.fromNamespaceAndPath("tensura", "energy_blockade"),
         -0.2F,
         Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(
         TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER,
         ResourceLocation.fromNamespaceAndPath("tensura", "energy_blockade"),
         -0.2F,
         Operation.ADD_MULTIPLIED_TOTAL
      );
   }
}
