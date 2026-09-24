package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.ability.skill.unique.SlothSkill;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class RestEffect extends TensuraMobEffect {
   protected static final ResourceLocation REST = ResourceLocation.fromNamespaceAndPath("tensura", "rest");

   public RestEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(29, 25, 89).getRGB());
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, REST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, REST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, REST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, REST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, REST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, REST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER, REST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.GLIDE_SPEED_MULTIPLIER, REST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE, REST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE, REST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, REST, SlothSkill.CONFIG.restAP - 1.0F, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, REST, SlothSkill.CONFIG.restMP - 1.0F, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity instanceof Player player) {
         if (!player.getAbilities().flying) {
            return true;
         }

         player.getAbilities().flying = false;
         player.onUpdateAbilities();
         return false;
      } else {
         return true;
      }
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 5 == 0;
   }
}
