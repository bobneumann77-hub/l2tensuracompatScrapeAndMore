package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.race.RaceUtils;
import java.awt.Color;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MagicInterferenceEffect extends TensuraMobEffect {
   public MagicInterferenceEffect() {
      super(MobEffectCategory.HARMFUL, new Color(97, 55, 117).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity instanceof Player player) {
         if (!player.getAbilities().mayfly) {
            return true;
         }

         if (RaceUtils.canStillFly(player, true, true, true)) {
            return true;
         }

         player.getAbilities().mayfly = false;
         player.getAbilities().flying = false;
         player.onUpdateAbilities();
         return true;
      } else {
         return true;
      }
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 40 == 0;
   }
}
