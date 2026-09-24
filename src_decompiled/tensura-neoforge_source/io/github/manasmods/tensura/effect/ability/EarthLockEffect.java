package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.ability.magic.aspectual.earth.EarthLockMagic;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class EarthLockEffect extends TensuraMobEffect {
   public static final ResourceLocation EARTH_LOCK = ResourceLocation.fromNamespaceAndPath("tensura", "earth_lock");

   public EarthLockEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(101, 72, 50).getRGB());
      this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, EARTH_LOCK, EarthLockMagic.CONFIG.knockbackResistance, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, EARTH_LOCK, EarthLockMagic.CONFIG.knockbackResistance, Operation.ADD_VALUE);
   }
}
