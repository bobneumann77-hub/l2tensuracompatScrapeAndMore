package dev.xkmc.l2hostility.content.effect;

import dev.xkmc.l2core.base.effects.api.ForceEffect;
import dev.xkmc.l2core.base.effects.api.InherentEffect;
import dev.xkmc.l2hostility.init.L2Hostility;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class MoonwalkEffect extends InherentEffect implements ForceEffect {
   private static final double FACTOR = 0.7;

   public MoonwalkEffect(MobEffectCategory category, int color) {
      super(category, color);
      this.addAttributeModifier(Attributes.GRAVITY, L2Hostility.loc("moonwalk"), Operation.ADD_MULTIPLIED_TOTAL, lv -> Math.pow(0.7, lv + 1) - 1.0);
   }
}
