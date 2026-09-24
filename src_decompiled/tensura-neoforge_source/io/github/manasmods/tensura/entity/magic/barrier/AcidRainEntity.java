package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AcidRainEntity extends BarrierEntity {
   @Nullable
   private Entity target;

   public AcidRainEntity(EntityType<? extends AcidRainEntity> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.setFollowOwner(true);
      this.noCulling = false;
   }

   public AcidRainEntity(Level level, LivingEntity entity) {
      this((EntityType<? extends AcidRainEntity>)MiscEntityTypes.ACID_RAIN.get(), level);
      this.setOwner(entity);
   }

   @Nullable
   public Entity getTarget() {
      return this.target;
   }

   public void setTarget(@Nullable Entity pTarget) {
      this.target = pTarget;
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
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.WATER_ELEMENTAL;
   }

   @Override
   public boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return !super.canHitEntity(pTarget) ? false : !(this.getOwner() instanceof LivingEntity owner && owner.isAlliedTo(pTarget));
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
      double height = this.getSize() * 2.0F + this.getHeight();
      if (this.getTarget() != null) {
         this.setPos(this.getTarget().getX(), this.getTarget().getY() + height, this.getTarget().getZ());
      } else {
         Entity owner = this.getOwner();
         if (owner != null) {
            this.setPos(owner.getX(), owner.getY() + height, owner.getZ());
            List<AcidRainEntity> rains = this.level()
               .getEntitiesOfClass(AcidRainEntity.class, owner.getBoundingBox(), entityData -> entityData.getOwner() == owner && entityData != this);
            if (!rains.isEmpty() || !owner.isAlive() || owner.level() != this.level()) {
               this.remove();
            }
         }
      }
   }

   @Override
   public void applyEffect(LivingEntity entity) {
      if (this.dealDamage(entity, this.getDamage(), 0.1F)) {
         MobEffectInstance instance = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 40, 0, true, false, true);
         ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
         TensuraMobEffect.addEffect(entity, instance, this.getOwner(), skill, this.getMode());
      }
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
         this.level().addParticle(TensuraParticleUtils.getAcidCloud(), this.getX() + pos.x, yPos + pos.y, this.getZ() + pos.z, motion.x, motion.y, motion.z);
      }

      int i = Mth.ceil((float) Math.PI * this.getVisualSize() * this.getVisualSize());

      for (int j = 0; j < i; j++) {
         if (!(this.random.nextFloat() > 0.25)) {
            float angle = this.random.nextFloat() * (float) (Math.PI * 2);
            double randomRad = Mth.sqrt(this.random.nextFloat()) * this.getVisualSize();
            double x = this.getX() + Mth.cos(angle) * randomRad;
            double y = yPos + Mth.cos(angle) * Mth.sqrt(this.random.nextFloat());
            double z = this.getZ() + Mth.sin(angle) * randomRad;
            this.level().addParticle((ParticleOptions)TensuraParticleTypes.FALLING_ACID.get(), x, y, z, 0.0, 0.0, 0.0);
         }
      }
   }
}
