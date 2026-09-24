package io.github.manasmods.tensura.effect;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class ParalysisEffect extends TensuraMobEffect {
   public ParalysisEffect() {
      super(MobEffectCategory.HARMFUL, new Color(224, 208, 9).getRGB());
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("tensura", "paralysis"), -0.2F, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(
         ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, ResourceLocation.fromNamespaceAndPath("tensura", "paralysis"), -0.2F, Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath("tensura", "paralysis"), -0.33F, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("tensura", "paralysis"), -0.1F, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(
         Attributes.BLOCK_BREAK_SPEED, ResourceLocation.fromNamespaceAndPath("tensura", "paralysis"), -0.3F, Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get())) {
         pAmplifier -= 2;
         if (pAmplifier < 0) {
            return false;
         }
      }

      if (entity.isSprinting() && pAmplifier >= 1) {
         entity.setSprinting(false);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 20 == 0 && pAmplifier >= 1;
   }
}
