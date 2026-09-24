package io.github.manasmods.tensura.entity.magic.field.cloud;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BloodMistCloud extends AreaCloud {
   public BloodMistCloud(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public BloodMistCloud(Level level, LivingEntity entity) {
      this((EntityType<? extends Projectile>)MiscEntityTypes.BLOOD_MIST.get(), level);
      this.setOwner(entity);
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      return !this.canExplodeEntity(pTarget) ? false : !RaceUtils.isBloodless(pTarget) && (this.getTarget() == null || pTarget == this.getTarget());
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.BLOOD_DRAIN;
   }

   @Override
   public void applyEffect(LivingEntity target, boolean instant) {
      if (this.dealDamage(target, this.getDamage(), 0.033333335F) && this.getOwner() instanceof LivingEntity living) {
         living.heal(this.getDamage());
      }
   }

   private boolean canExplodeEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return !super.canHitEntity(pTarget) ? false : !(this.getOwner() instanceof LivingEntity owner && owner.isAlliedTo(pTarget));
      }
   }

   public void bloodExplosion() {
      if (!this.level().isClientSide()) {
         for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (this.canExplodeEntity(target)
               && target.distanceToSqr(this.getX(), this.getY(), this.getZ()) < this.getSize() * this.getSize()
               && !(this.getOwner() instanceof LivingEntity living && (target.isAlliedTo(living) || target.is(living)))) {
               DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.BLOOD_DRAIN, this.getOwner())
                  .tensura$setMagiculeCost(this.getMpCost())
                  .tensura$setAbilityInstance(this.getSkill())
                  .tensura$setAbilityMode(this.getMode());
               target.hurt(source, this.getDamage() * 10.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.FLASH, 1.0);
            }
         }

         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         ((ServerLevel)this.level()).sendParticles(ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 3, 0.08, 0.08, 0.08, 0.15);
         TensuraParticleHelper.serverParticleCloud(
            this.level(), this.random, ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.5, this.getZ(), 0.01, 0.5, 4.0
         );
         this.discard();
      }
   }

   @Override
   public void spawnAmbientParticles() {
      float size = this.getVisualSize() - 1.0F;
      float f = Mth.clamp(1.5F * size, 0.375F, 15.0F);

      for (int i = 0; i < f; i++) {
         if (f - i < 1.0F && this.random.nextFloat() > f - i) {
            return;
         }

         float distance = size * (1.0F - this.random.nextFloat() * this.random.nextFloat());
         float v = this.random.nextFloat() * 6.28F;
         Vec3 pos = new Vec3(distance * Mth.cos(v), 0.2, distance * Mth.sin(v));
         Vec3 motion = new Vec3((2.0 * Math.random() - 1.0) * 0.03, this.random.nextDouble() * 0.01, (2.0 * Math.random() - 1.0) * 0.03);
         this.level()
            .addParticle(TensuraParticleUtils.getBloodMist(40), this.getX() + pos.x, this.getY() + pos.y, this.getZ() + pos.z, motion.x, motion.y, motion.z);
      }
   }
}
