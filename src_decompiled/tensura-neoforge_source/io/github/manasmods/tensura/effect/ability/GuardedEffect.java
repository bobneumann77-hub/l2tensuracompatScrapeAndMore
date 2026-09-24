package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.ability.skill.unique.GuardianSkill;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class GuardedEffect extends TensuraMobEffect {
   protected static final ResourceLocation GUARDIAN = ResourceLocation.fromNamespaceAndPath("tensura", "guardian_protection");

   public GuardedEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(105, 103, 103).getRGB());
      this.addAttributeModifier(Attributes.ARMOR, GUARDIAN, GuardianSkill.CONFIG.protectionArmor, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.MULTILAYER_BARRIER, GUARDIAN, GuardianSkill.CONFIG.protectionBarrier, Operation.ADD_VALUE);
   }
}
