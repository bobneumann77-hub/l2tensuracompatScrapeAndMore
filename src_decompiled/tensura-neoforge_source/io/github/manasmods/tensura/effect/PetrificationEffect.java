package io.github.manasmods.tensura.effect;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class PetrificationEffect extends TensuraMobEffect {
   private static final ResourceLocation PETRIFICATION = ResourceLocation.fromNamespaceAndPath("tensura", "petrification");

   public PetrificationEffect() {
      super(MobEffectCategory.HARMFUL, new Color(94, 96, 94).getRGB());
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, PETRIFICATION, -0.25, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, PETRIFICATION, -0.25, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, PETRIFICATION, -0.25, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER, PETRIFICATION, -0.25, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.GLIDE_SPEED_MULTIPLIER, PETRIFICATION, -0.25, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public void onEffectStarted(LivingEntity entity, int i) {
      super.onEffectStarted(entity, i);
      entity.level().playSound(null, entity.blockPosition(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get())) {
         pAmplifier -= 2;
         if (pAmplifier < 3) {
            return false;
         }
      }

      if (entity.level() instanceof ServerLevel level) {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.PETRIFICATION));
         if (instance == null) {
            return true;
         }

         entity.hurt(
            TensuraDamageHelper.getUUIDDamageSource(TensuraDamageTypes.PETRIFICATION, level, instance.tensura$getSource(), instance.tensura$getSourceAbility())
               .tensura$setDodgeBypass(),
            entity.getMaxHealth()
         );
      }

      return false;
   }

   @Override
   public void onAttributeRemoved(LivingEntity entity, MobEffectInstance instance) {
      if (entity.isAlive()) {
         entity.level().playSound(null, entity.blockPosition(), SoundEvents.DRIPSTONE_BLOCK_BREAK, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pAmplifier >= 3 && pDuration % 5 == 0;
   }

   public static int getPetrificationLevel(LivingEntity entity) {
      AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      if (speed == null) {
         return 0;
      }

      AttributeModifier modifier = speed.getModifier(PETRIFICATION);
      return modifier == null ? 0 : (int)(modifier.amount() / -0.25);
   }
}
