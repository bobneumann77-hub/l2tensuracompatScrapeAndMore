package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class HakiCoatEffect extends TensuraMobEffect {
   public static final ResourceLocation HAKI_COAT = ResourceLocation.fromNamespaceAndPath("tensura", "haki_coat");

   public HakiCoatEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(58, 1, 1).getRGB());
      this.addAttributeModifier(TensuraAttributes.PHYSICAL_RESIST_DEGRADATION, HAKI_COAT, 1.0, Operation.ADD_VALUE);
   }
}
