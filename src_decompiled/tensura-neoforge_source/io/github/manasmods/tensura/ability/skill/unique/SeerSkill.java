package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class SeerSkill extends Skill {
   private static final UniqueSkillConfig.Seer CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Seer;
   protected static final ResourceLocation SEER = ResourceLocation.fromNamespaceAndPath("tensura", "seer");

   public SeerSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FUTURE_VISION));
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      double meleeAmount = instance.isMastered(entity) ? CONFIG.meleeDodgeMastered : CONFIG.meleeDodge;
      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.addOrReplacePermanentModifier(new AttributeModifier(SEER, meleeAmount, Operation.ADD_VALUE));
      }

      double projectileAmount = instance.isMastered(entity) ? CONFIG.projectileDodgeMastered : CONFIG.projectileDodge;
      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.addOrReplacePermanentModifier(new AttributeModifier(SEER, projectileAmount, Operation.ADD_VALUE));
      }

      double chance = instance.isMastered(entity) ? CONFIG.criticalChanceMastered : CONFIG.criticalChance;
      AttributeInstance critical = entity.getAttribute(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE);
      if (critical != null) {
         critical.addOrReplacePermanentModifier(new AttributeModifier(SEER, chance, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.removeModifier(SEER);
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.removeModifier(SEER);
      }

      AttributeInstance critical = entity.getAttribute(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE);
      if (critical != null) {
         critical.removeModifier(SEER);
      }
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity entity, DamageSource damageSource, Changeable<Float> amount) {
      if (!instance.isToggled()) {
         return true;
      } else if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      } else if (damageSource.getDirectEntity() != null && damageSource.getDirectEntity() == damageSource.getEntity()) {
         float multiplier = instance.isMastered(entity) ? CONFIG.inputMultiplierMastered : CONFIG.inputMultiplier;
         amount.set((Float)amount.get() * multiplier);
         entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SHIELD_BLOCK, TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
         return true;
      } else {
         return true;
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FUTURE_VISION))) {
         instance.setCoolDown(CONFIG.visionCooldown, mode);
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.FUTURE_VISION));
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      } else {
         int duration = instance.isMastered(entity) ? CONFIG.visionDurationMastered : CONFIG.visionDuration;
         instance.setCoolDown(CONFIG.visionCooldown + duration / 20, mode);
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FUTURE_VISION), duration, 0, false, false, false));
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FUTURE_VISION));
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      instance.addMasteryPoint(entity);
   }
}
