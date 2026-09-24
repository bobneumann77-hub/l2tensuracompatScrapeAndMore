package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.ability.skill.unique.TunerSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class FateChangeEffect extends TensuraMobEffect {
   protected static final ResourceLocation TUNER = ResourceLocation.fromNamespaceAndPath("tensura", "tuner");

   public FateChangeEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(44, 148, 99, 255).getRGB());
      UniqueSkillConfig.Tuner CONFIG = TunerSkill.CONFIG;
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, TUNER, CONFIG.bonusAttack, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, TUNER, CONFIG.bonusAttackSpeed, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, TUNER, CONFIG.bonusSpeed, Operation.ADD_VALUE);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, TUNER, CONFIG.bonusSwim, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE, TUNER, CONFIG.bonusMeleeDodge, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE, TUNER, CONFIG.bonusProjectileDodge, Operation.ADD_VALUE);
   }
}
