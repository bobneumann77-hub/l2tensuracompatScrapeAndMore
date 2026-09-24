package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class MovementInterferenceEffect extends TensuraMobEffect {
   protected static final ResourceLocation INTERFERENCE = ResourceLocation.fromNamespaceAndPath("tensura", "interference");

   public MovementInterferenceEffect() {
      super(MobEffectCategory.HARMFUL, new Color(66, 64, 64).getRGB());
      this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, INTERFERENCE, 0.1, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, INTERFERENCE, -0.1, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, INTERFERENCE, -0.1, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, INTERFERENCE, -0.1, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, INTERFERENCE, -0.1, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER, INTERFERENCE, -0.1, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.GLIDE_SPEED_MULTIPLIER, INTERFERENCE, -0.1, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return false;
   }
}
