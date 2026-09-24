package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class FutureSightEffect extends TensuraMobEffect {
   public static ResourceLocation FUTURE_SIGHT = ResourceLocation.fromNamespaceAndPath("tensura", "future_sight");

   public FutureSightEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(20, 224, 126, 255).getRGB());
      this.addAttributeModifier(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE, FUTURE_SIGHT, 100.0, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE, FUTURE_SIGHT, 100.0, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.DODGE_NEGATE_CHANCE, FUTURE_SIGHT, 100.0, Operation.ADD_VALUE);
      this.addAttributeModifier(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE, FUTURE_SIGHT, 100.0, Operation.ADD_VALUE);
   }
}
