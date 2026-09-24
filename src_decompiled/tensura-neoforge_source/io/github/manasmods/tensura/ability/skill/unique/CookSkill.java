package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class CookSkill extends Skill {
   private static final UniqueSkillConfig.Cook CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Cook;
   public static final ResourceLocation COOK = ResourceLocation.fromNamespaceAndPath("tensura", "cook");

   public CookSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public List<Integer> getModeLearningList(ManasSkillInstance instance) {
      return List.of(0);
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return mode == 0 ? "cook.chaotic_fate" : super.getModeId(instance, mode);
   }

   public void onToggleOn(ManasSkillInstance skillInstance, LivingEntity entity) {
      AttributeInstance degrade = entity.getAttribute(TensuraAttributes.RESISTANCE_DEGRADATION);
      if (degrade != null) {
         degrade.addOrReplacePermanentModifier(new AttributeModifier(COOK, 1.0, Operation.ADD_VALUE));
      }

      AttributeInstance critical = entity.getAttribute(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE);
      if (critical != null) {
         critical.addOrReplacePermanentModifier(new AttributeModifier(COOK, CONFIG.critChance, Operation.ADD_VALUE));
      }

      AttributeInstance dodgeNegate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (dodgeNegate != null) {
         dodgeNegate.addOrReplacePermanentModifier(new AttributeModifier(COOK, CONFIG.dodgeNegation, Operation.ADD_VALUE));
      }

      AttributeInstance learning = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
      if (learning != null) {
         learning.addOrReplacePermanentModifier(new AttributeModifier(COOK, CONFIG.learningPoint, Operation.ADD_VALUE));
      }

      AttributeInstance mastery = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (mastery != null) {
         mastery.addOrReplacePermanentModifier(new AttributeModifier(COOK, CONFIG.masteryPoint, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance skillInstance, LivingEntity entity) {
      AttributeInstance degrade = entity.getAttribute(TensuraAttributes.RESISTANCE_DEGRADATION);
      if (degrade != null) {
         degrade.removeModifier(COOK);
      }

      AttributeInstance critical = entity.getAttribute(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE);
      if (critical != null) {
         critical.removeModifier(COOK);
      }

      AttributeInstance negate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (negate != null) {
         negate.removeModifier(COOK);
      }

      AttributeInstance learning = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
      if (learning != null) {
         learning.removeModifier(COOK);
      }

      AttributeInstance mastery = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (mastery != null) {
         mastery.removeModifier(COOK);
      }
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingEntity entity, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(attacker, instance)) {
         return true;
      }

      if (entity.getAttributeValue(TensuraAttributes.LAW_DEGRADATION) > 0.0
         && EnergyHelper.getMaxEP(entity) > EnergyHelper.getMaxEP(attacker) * CONFIG.barrierEP) {
         return true;
      }

      AttributeInstance attribute = entity.getAttribute(TensuraAttributes.MULTILAYER_BARRIER);
      if (attribute != null && !attribute.getModifiers().isEmpty()) {
         attribute.removeModifiers();
         entity.level().playSound(null, entity.blockPosition(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      return true;
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      CompoundTag tag = instance.getOrCreateTag();
      if (!tag.getBoolean("ChaoticFateActivated")) {
         return true;
      }

      if (instance.onCoolDown(0)) {
         return true;
      }

      AttributeInstance health = target.getAttribute(Attributes.MAX_HEALTH);
      if (health == null) {
         return true;
      }

      double damageAmount = ((Float)amount.get()).floatValue();
      AttributeModifier chefModifier = health.getModifier(COOK);
      if (chefModifier != null) {
         damageAmount -= chefModifier.amount();
      }

      AttributeModifier attributemodifier = new AttributeModifier(COOK, Math.min(0.0, damageAmount * CONFIG.hpReducedMultiplier * -1.0), Operation.ADD_VALUE);
      health.removeModifier(attributemodifier);
      health.addOrReplacePermanentModifier(attributemodifier);
      if (!instance.isMastered(entity)) {
         tag.putBoolean("ChaoticFateActivated", false);
      }

      instance.addMasteryPoint(entity);
      instance.setCoolDown(1, 0);
      entity.level().playSound(null, target.blockPosition(), (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      if (!this.learnMode(instance, entity, mode)) {
         if (entity.isShiftKeyDown()) {
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 6.0, false);
            if (target == null || !target.isAlive()) {
               target = entity;
            }

            if (removeCookedHP(target)) {
               instance.setCoolDown(1, 0);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.DEBUFF_DEACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.COMPOSTER);
            }
         } else {
            boolean activated = tag.getBoolean("ChaoticFateActivated");
            tag.putBoolean("ChaoticFateActivated", !activated);
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  activated ? (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get() : (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
         }
      }
   }

   public static boolean removeCookedHP(LivingEntity entity) {
      AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
      if (health == null) {
         return false;
      } else if (health.getModifier(COOK) != null) {
         health.removeModifier(COOK);
         return true;
      } else {
         return false;
      }
   }
}
