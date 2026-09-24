package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.world.effect.MobEffectCategory;

public class SpatialBlockadeEffect extends TensuraMobEffect {
   public SpatialBlockadeEffect() {
      super(MobEffectCategory.HARMFUL, new Color(106, 7, 204).getRGB());
   }
}
