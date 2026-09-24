package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.ability.skill.unique.CommanderSkill;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class InspirationEffect extends TensuraMobEffect {
   protected static final ResourceLocation INSPIRATION = ResourceLocation.fromNamespaceAndPath("tensura", "inspiration");

   public InspirationEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(236, 155, 74).getRGB());
      double amount = CommanderSkill.CONFIG.inspireMultiplier;
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, INSPIRATION, amount, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, INSPIRATION, amount, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(Attributes.ATTACK_KNOCKBACK, INSPIRATION, amount, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, INSPIRATION, amount, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(Attributes.MAX_HEALTH, INSPIRATION, amount, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, INSPIRATION, amount, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, INSPIRATION, amount, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, INSPIRATION, amount, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE, INSPIRATION, CommanderSkill.CONFIG.inspireCritChance, Operation.ADD_VALUE);
   }
}
