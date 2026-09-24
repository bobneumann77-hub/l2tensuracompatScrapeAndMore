package io.github.manasmods.tensura.effect;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class BurdenEffect extends TensuraMobEffect {
   public BurdenEffect() {
      super(MobEffectCategory.HARMFUL, new Color(63, 65, 65).getRGB());
      this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath("tensura", "burden"), 0.1F, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.GRAVITY, ResourceLocation.fromNamespaceAndPath("tensura", "burden"), 0.5, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(
         Attributes.FALL_DAMAGE_MULTIPLIER, ResourceLocation.fromNamespaceAndPath("tensura", "burden"), 0.5, Operation.ADD_MULTIPLIED_TOTAL
      );
   }
}
