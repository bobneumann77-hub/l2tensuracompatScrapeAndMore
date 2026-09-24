package dev.xkmc.l2hostility.content.effect;

import dev.xkmc.l2core.base.effects.api.ForceEffect;
import dev.xkmc.l2core.base.effects.api.InherentEffect;
import dev.xkmc.l2hostility.init.L2Hostility;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class GravityEffect extends InherentEffect implements ForceEffect {
   private static final double FACTOR = 1.3;

   public GravityEffect(MobEffectCategory category, int color) {
      super(category, color);
      this.addAttributeModifier(Attributes.GRAVITY, L2Hostility.loc("gravity"), Operation.ADD_MULTIPLIED_TOTAL, lv -> Math.pow(1.3, lv + 1) - 1.0);
   }
}
