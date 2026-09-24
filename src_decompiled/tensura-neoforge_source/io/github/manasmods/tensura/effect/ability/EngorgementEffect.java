package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.ability.skill.unique.EngorgerSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class EngorgementEffect extends TensuraMobEffect {
   protected static final ResourceLocation ENGORGEMENT = ResourceLocation.fromNamespaceAndPath("tensura", "engorgement");

   public EngorgementEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(246, 168, 0, 255).getRGB());
      UniqueSkillConfig.Engorger CONFIG = EngorgerSkill.CONFIG;
      this.addAttributeModifier(Attributes.ARMOR, ENGORGEMENT, CONFIG.armor, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ENGORGEMENT, CONFIG.attack, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ATTACK_KNOCKBACK, ENGORGEMENT, CONFIG.attackKnock, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, ENGORGEMENT, CONFIG.jumpBoost, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ENGORGEMENT, CONFIG.knockResistance, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ENGORGEMENT, CONFIG.speed, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.STEP_HEIGHT, ENGORGEMENT, 1.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, ENGORGEMENT, CONFIG.range, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, ENGORGEMENT, CONFIG.range, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.SCALE, ENGORGEMENT, CONFIG.size - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, ENGORGEMENT, CONFIG.speed, Operation.ADD_VALUE);
   }

   public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
      if (pLivingEntity instanceof Player player) {
         if (!pLivingEntity.level().isClientSide()) {
            player.getFoodData().eat(pAmplifier + 1, 0.5F);
         }
      } else if (pLivingEntity.getHealth() < pLivingEntity.getMaxHealth()) {
         pLivingEntity.heal(EngorgerSkill.CONFIG.heal + pAmplifier);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 20 == 0;
   }
}
