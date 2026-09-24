package io.github.manasmods.tensura.effect;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.animal.Panda;

public class ChillEffect extends TensuraMobEffect {
   public ChillEffect() {
      super(MobEffectCategory.HARMFUL, new Color(41, 166, 182).getRGB());
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("tensura", "chill"), -0.2F, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("tensura", "chill"), -0.2F, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, ResourceLocation.fromNamespaceAndPath("tensura", "chill"), -0.2F, Operation.ADD_VALUE);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.canFreeze()) {
         return false;
      }

      int minFreeze = 20 << amplifier;
      if (amplifier > 3) {
         minFreeze = 180;
      }

      if (entity.getTicksFrozen() < minFreeze) {
         entity.setTicksFrozen(minFreeze);
         if (entity instanceof Panda panda && panda.getRandom().nextInt(100) == 69) {
            panda.sneeze(true);
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration > 0;
   }
}
