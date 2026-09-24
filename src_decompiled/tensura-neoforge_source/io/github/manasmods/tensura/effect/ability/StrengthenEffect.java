package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class StrengthenEffect extends TensuraMobEffect {
   public StrengthenEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(185, 162, 123).getRGB());
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("tensura", "strengthen"), 6.0, Operation.ADD_VALUE);
   }
}
