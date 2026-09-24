package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class PresenceConcealmentEffect extends TensuraMobEffect {
   public PresenceConcealmentEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(0, 0, 206).getRGB());
      this.addAttributeModifier(
         TensuraAttributes.PRESENCE_CONCEALMENT, ResourceLocation.fromNamespaceAndPath("tensura", "concealment"), 1.0, Operation.ADD_VALUE
      );
      this.addAttributeModifier(Attributes.STEP_HEIGHT, ResourceLocation.fromNamespaceAndPath("tensura", "concealment"), 1.0, Operation.ADD_VALUE);
   }

   public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
      SubordinateHelper.presenceConcealing(pLivingEntity, 40.0);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 5 == 0;
   }
}
