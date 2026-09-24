package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class EnemySearchEffect extends TensuraMobEffect {
   public static final ResourceLocation ENEMY_SEARCH = ResourceLocation.fromNamespaceAndPath("tensura", "enemy_search");

   public EnemySearchEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(236, 105, 105).getRGB());
      this.addAttributeModifier(TensuraAttributes.PRESENCE_SENSE_RADIUS, ENEMY_SEARCH, 0.1F, Operation.ADD_VALUE);
   }
}
