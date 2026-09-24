package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.Nullable;

public class AntiSkill extends Skill {
   private static final UniqueSkillConfig.AntiSkill CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).AntiSkill;

   public AntiSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   public boolean onBeingDamaged(ManasSkillInstance instance, LivingEntity entity, DamageSource damageSource, float amount) {
      if (!instance.isToggled() && !this.isInSlot(entity, instance)) {
         return true;
      }

      if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      }

      if (damageSource.is(TensuraTags.DamageTypes.BYPASS_ANTI_SKILL)) {
         return true;
      }

      if (damageSource.tensura$getBarrierBypassLevel() >= 3.0F) {
         return true;
      }

      if (damageSource.tensura$getAbilityInstance() == null && damageSource.tensura$getMagicType() == null && damageSource.tensura$getSkillType() == null) {
         return true;
      }

      entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
      return false;
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled() && !this.isInSlot(entity, instance)) {
         return true;
      }

      if (source.getDirectEntity() != entity) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return true;
      }

      if (entity.getMainHandItem().isEmpty() && entity.getOffhandItem().isEmpty()) {
         AttributeInstance barrier = target.getAttribute(TensuraAttributes.MULTILAYER_BARRIER);
         if (barrier != null && !(barrier.getValue() <= 0.0)) {
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            barrier.removeModifiers();
            return true;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled() && !this.isInSlot(entity, instance)) {
         return true;
      }

      if (source.getDirectEntity() != entity) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return true;
      }

      if (instance.isMastered(entity)) {
         if (!entity.getMainHandItem().isEmpty()) {
            return true;
         }
      } else if (!entity.getMainHandItem().isEmpty() || !entity.getOffhandItem().isEmpty()) {
         return true;
      }

      target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_SKILL), CONFIG.antiDuration, 0, false, false, false));
      TensuraMobEffect.removePredicateEffect(target, effect -> effect.is(TensuraTags.MobEffects.AFFECTED_BY_ANTI_SKILL));
      TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ENCHANTED_HIT, 1.0);
      entity.level()
         .playSound(
            null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
         );
      return true;
   }

   public boolean onEffectAdded(ManasSkillInstance instance, LivingEntity entity, @Nullable Entity source, Changeable<MobEffectInstance> effect) {
      return !instance.isToggled() && !this.isInSlot(entity, instance)
         ? true
         : !((MobEffectInstance)effect.get()).getEffect().is(TensuraTags.MobEffects.SKILL_DEBUFF);
   }
}
