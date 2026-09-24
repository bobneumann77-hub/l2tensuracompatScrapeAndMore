package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import lombok.Generated;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class HealingRainEntity extends BarrierEntity {
   protected float percentageHeal = 0.0F;

   public HealingRainEntity(EntityType<? extends HealingRainEntity> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.noCulling = false;
   }

   public HealingRainEntity(Level level, LivingEntity entity) {
      this((EntityType<? extends HealingRainEntity>)MiscEntityTypes.HEALING_RAIN.get(), level);
      this.setOwner(entity);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putFloat("Heal", this.getPercentageHeal());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setPercentageHeal(compound.getFloat("Heal"));
   }

   @Override
   public boolean canWalkThrough() {
      return true;
   }

   @Override
   public boolean blockBuilding() {
      return false;
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   public boolean shouldCreateParts() {
      return false;
   }

   @Override
   public boolean canHitEntity(Entity pTarget) {
      if (!pTarget.isAlive()) {
         return false;
      }

      if (pTarget.isSpectator()) {
         return false;
      }

      if (pTarget instanceof LivingEntity target) {
         if (target.getHealth() >= target.getMaxHealth()) {
            return false;
         }

         if (pTarget == this.getOwner()) {
            return true;
         }

         if (this.getOwner() instanceof LivingEntity owner) {
            if (owner.isAlliedTo(target)) {
               return true;
            } else if (owner.getLastHurtMob() == target) {
               return false;
            } else if (owner.getLastAttacker() == target) {
               return false;
            } else {
               return target instanceof Mob mob ? mob.getTarget() != owner : true;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected AABB getAffectedArea() {
      double height = this.getSize() * 3.0F + this.getHeight();
      return new AABB(
         this.getX() - this.getSize(),
         this.getY() - height,
         this.getZ() - this.getSize(),
         this.getX() + this.getSize(),
         this.getY(),
         this.getZ() + this.getSize()
      );
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return EntityDimensions.scalable(this.getSize() * 2.0F, 2.0F);
   }

   @Override
   public void tick() {
      super.tick();
      double height = this.getSize() * 2.0F + this.getHeight();
      this.level().playSound(null, this.getX(), this.getY() - height, this.getZ(), SoundEvents.WEATHER_RAIN, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }

   @Override
   public void applyFollowOwner() {
      Entity owner = this.getOwner();
      if (owner != null) {
         this.setPos(owner.getX(), owner.getY() + this.getSize() * 2.0F + this.getHeight(), owner.getZ());
      }
   }

   @Override
   protected boolean dealDamage(Entity entity) {
      LivingEntity target = (LivingEntity)entity;
      float heal = this.getDamage();
      if (this.getPercentageHeal() > 0.0F) {
         heal = Math.max(heal, target.getMaxHealth() * this.getPercentageHeal());
      }

      target.heal(heal);
      target.level()
         .playSound(
            null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
         );
      TensuraParticleHelper.spawnServerParticles(
         entity.level(),
         TensuraParticleUtils.getLightGreenWave(0.9F, target.getBbWidth() * 3.0F, -0.5F, true),
         target.getX(),
         target.getY() + target.getBbHeight() * 0.33,
         target.getZ()
      );
      TensuraParticleHelper.spawnServerParticles(
         entity.level(),
         TensuraParticleUtils.getLightGreenWave(0.9F, target.getBbWidth() * 3.0F, -0.5F, true),
         target.getX(),
         target.getY() + target.getBbHeight() * 0.66,
         target.getZ()
      );
      return true;
   }

   @Override
   public void spawnParticle() {
      double yPos = this.getY() + 1.0;
      float f = Mth.clamp(0.5F * this.getVisualSize(), 0.125F, 5.0F);

      for (int i = 0; i < f; i++) {
         if (f - i < 1.0F && this.random.nextFloat() > f - i) {
            return;
         }

         float distance = this.getVisualSize() * (1.0F - this.random.nextFloat() * this.random.nextFloat());
         float v = this.random.nextFloat() * 6.28F;
         Vec3 pos = new Vec3(distance * Mth.cos(v), 0.2, distance * Mth.sin(v));
         Vec3 motion = new Vec3((2.0 * Math.random() - 1.0) * 0.03, this.random.nextDouble() * 0.01, (2.0 * Math.random() - 1.0) * 0.03);
         this.level().addParticle(TensuraParticleUtils.getHealingCloud(), this.getX() + pos.x, yPos + pos.y, this.getZ() + pos.z, motion.x, motion.y, motion.z);
      }

      int i = Mth.ceil((float) Math.PI * this.getVisualSize() * this.getVisualSize());

      for (int j = 0; j < i; j++) {
         if (!(this.random.nextFloat() > 0.25)) {
            float angle = this.random.nextFloat() * (float) (Math.PI * 2);
            double randomRad = Mth.sqrt(this.random.nextFloat()) * this.getVisualSize();
            double x = this.getX() + Mth.cos(angle) * randomRad;
            double y = yPos + Mth.cos(angle) * Mth.sqrt(this.random.nextFloat());
            double z = this.getZ() + Mth.sin(angle) * randomRad;
            this.level().addParticle((ParticleOptions)TensuraParticleTypes.FALLING_HEAL_DROP.get(), x, y, z, 0.0, 0.0, 0.0);
         }
      }
   }

   @Generated
   public float getPercentageHeal() {
      return this.percentageHeal;
   }

   @Generated
   public void setPercentageHeal(float percentageHeal) {
      this.percentageHeal = percentageHeal;
   }
}
