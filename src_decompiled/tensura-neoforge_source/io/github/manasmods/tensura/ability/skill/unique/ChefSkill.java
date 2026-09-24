package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ChefSkill extends Skill {
   private static final UniqueSkillConfig.Chef CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Chef;

   public ChefSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCostEffect;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      LivingEntity target = entity.isShiftKeyDown() ? ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false) : null;
      boolean success;
      if (target != null) {
         IEffect effect = TensuraStorages.getEffectFrom(target);
         success = effect.getSeveranceAmount() > 0.0F;
         effect.setSeveranceAmount(0.0F);
         effect.markDirty();
         Predicate<Holder<MobEffect>> predicate = mobEffect -> ((MobEffect)mobEffect.value()).getCategory() == MobEffectCategory.HARMFUL
            && !mobEffect.is(TensuraTags.MobEffects.SKILL_EFFECT);
         success = TensuraMobEffect.removePredicateEffect(target, predicate, this.getMagiculeCost(entity, instance, mode)) || success;
         double cost = instance.isMastered(entity) ? CONFIG.magiculeCostHPMastered : CONFIG.magiculeCostHP;
         float lackedHealth = target.getMaxHealth() - target.getHealth();
         double lackedMagicule = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)(lackedHealth * cost));
         if (lackedMagicule > 0.0) {
            lackedHealth = (float)(lackedHealth - lackedMagicule / cost);
         }

         target.heal(lackedHealth);
         success = success || lackedHealth > 0.0F;
         if (success) {
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.HEART, 2.0);
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getRedWave(0.9F, target.getBbWidth() * 3.0F, -0.5F, true),
               target.getX(),
               target.getY() + target.getBbHeight() * 0.33,
               target.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getRedWave(0.9F, target.getBbWidth() * 3.0F, -0.5F, true),
               target.getX(),
               target.getY() + target.getBbHeight() * 0.66,
               target.getZ()
            );
         }
      } else {
         IEffect effect = TensuraStorages.getEffectFrom(entity);
         success = effect.getSeveranceAmount() > 0.0F;
         effect.setSeveranceAmount(0.0F);
         effect.markDirty();
         Predicate<Holder<MobEffect>> predicate = mobEffect -> ((MobEffect)mobEffect.value()).getCategory() == MobEffectCategory.HARMFUL
            && !mobEffect.is(TensuraTags.MobEffects.SKILL_EFFECT);
         success = TensuraMobEffect.removePredicateEffect(entity, predicate, this.getMagiculeCost(entity, instance, mode)) || success;
         double cost = instance.isMastered(entity) ? CONFIG.magiculeCostHPMastered : CONFIG.magiculeCostHP;
         float lackedHealth = entity.getMaxHealth() - entity.getHealth();
         double lackedMagicule = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)(lackedHealth * cost));
         if (lackedMagicule > 0.0) {
            lackedHealth = (float)(lackedHealth - lackedMagicule / cost);
         }

         entity.heal(lackedHealth);
         success = success || lackedHealth > 0.0F;
         if (success) {
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.HEART, 2.0);
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getRedWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.33,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getRedWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.66,
               entity.getZ()
            );
         }
      }

      if (success) {
         instance.addMasteryPoint(entity);
         instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         entity.swing(InteractionHand.MAIN_HAND, true);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.ACID_SIZZLE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }
}
