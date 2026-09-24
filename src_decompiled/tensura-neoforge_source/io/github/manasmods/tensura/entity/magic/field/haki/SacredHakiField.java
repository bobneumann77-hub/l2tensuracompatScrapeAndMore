package io.github.manasmods.tensura.entity.magic.field.haki;

import io.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class SacredHakiField extends HakiField {
   public SacredHakiField(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      if (pTarget == this || pTarget == null || !pTarget.isAlive() || pTarget instanceof LivingEntity entity && entity.hasInfiniteMaterials()) {
         return false;
      } else {
         return pTarget == this.getOwner() ? false : this.getTarget() == null || pTarget == this.getTarget();
      }
   }

   @Override
   public void applyEffect(LivingEntity target, boolean instant) {
      if (this.getOwner() != null && target.isAlliedTo(this.getOwner())) {
         boolean success = target.getHealth() < target.getMaxHealth();
         if (this.getAge() % 100 == 0 && this.getAge() > 0) {
            target.heal(this.getDamage());
         }

         Predicate<Holder<MobEffect>> predicate = effect -> ((MobEffect)effect.value()).getCategory() == MobEffectCategory.HARMFUL;
         success = TensuraMobEffect.removePredicateEffect(target, predicate) || success;
         if (success) {
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.HEART, 1.0);
         }
      } else {
         double targetEP = EnergyHelper.getMaxEP(target);
         double difference = this.getEp() / targetEP;
         if (!(difference <= 2.0)) {
            int fearLevel = (int)(this.getEpDifferenceMultiplier() * (difference - 2.0));
            fearLevel = Math.min(fearLevel, TensuraMobEffect.CONFIG.maxFear);
            MobEffectInstance fear = new MobEffectInstance(
               TensuraMobEffects.getReference(TensuraMobEffects.FEAR), this.getFearDuration(), fearLevel, true, false, true
            );
            if (TensuraMobEffect.addEffect(target, fear, this.getOwner(), this.getSkill().getSkill(), this.getMode())) {
               HakiSkill.hakiPush(target, this.getOwner(), this.getSkill(), fearLevel);
            }
         }
      }
   }
}
