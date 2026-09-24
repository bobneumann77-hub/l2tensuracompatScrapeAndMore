package io.github.manasmods.tensura.entity.magic.breath;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.skill.intrinsic.ParalysingBreathSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParalysingBreathProjectile extends BreathEntity {
   public ParalysingBreathProjectile(EntityType<? extends ParalysingBreathProjectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public ParalysingBreathProjectile(Level level, LivingEntity entity) {
      this((EntityType<? extends ParalysingBreathProjectile>)MiscEntityTypes.PARALYSING_BREATH.get(), level);
      this.setOwner(entity);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.PARALYZING;
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return !super.canHitEntity(pTarget) ? false : !(this.getOwner() instanceof LivingEntity owner && owner.isAlliedTo(pTarget));
      }
   }

   @Override
   protected boolean applyMobEffects(LivingEntity entity) {
      ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
      if (this.getMobEffect() != null) {
         TensuraMobEffect.addEffect(entity, new MobEffectInstance(this.getMobEffect()), this.getOwner(), skill, this.getMode());
      }

      MobEffectInstance instance = new MobEffectInstance(
         TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS),
         ParalysingBreathSkill.CONFIG.paralysisDuration,
         ParalysingBreathSkill.CONFIG.paralysisLevel - 1,
         true,
         false,
         true
      );
      TensuraMobEffect.addEffect(entity, instance, this.getOwner(), skill, this.getMode());
      return true;
   }

   @Override
   protected boolean dealDamage(Entity target, float damage, float costMultiplier) {
      return !(target instanceof LivingEntity) ? false : super.dealDamage(target, damage, costMultiplier);
   }

   @Override
   protected boolean hitEntity(Entity entity, ProjectileHitResult customResult) {
      if (this.getOwner() != null) {
         SkillHelper.pushBackFromPos(ObjectSelectionHelper.getBlockPos(this.getOwner().getEyePosition()), entity, this.getOwner(), this.getSkill(), 0.1F);
      }

      return super.hitEntity(entity, customResult);
   }

   @Override
   public void spawnParticle() {
      if (this.getOwner() instanceof LivingEntity owner) {
         Vec3 var22 = owner.getLookAngle().normalize();
         Vec3 pos = owner.position().add(var22.scale(1.6));
         double x = pos.x;
         double y = pos.y + owner.getEyeHeight() * 0.9F;
         double z = pos.z;
         RandomSource rand = owner.getRandom();
         double speed = rand.nextDouble() * 0.35 + 0.55;

         for (int i = 0; i < 20; i++) {
            double ox = rand.nextDouble() * 0.3 - 0.15;
            double oy = rand.nextDouble() * 0.3 - 0.15;
            double oz = rand.nextDouble() * 0.3 - 0.15;
            Vec3 randomVec = new Vec3(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5).normalize();
            Vec3 result = var22.scale(3.0).add(randomVec).normalize().scale(speed);
            owner.level()
               .addParticle(
                  rand.nextDouble() < 0.75 ? TensuraParticleUtils.getParalyzingEffect(20) : TensuraParticleUtils.getParalyzingAura(0.75F, 3.0F, 0.01F, 20),
                  x + ox,
                  y + oy,
                  z + oz,
                  result.x,
                  result.y,
                  result.z
               );
         }
      }
   }
}
