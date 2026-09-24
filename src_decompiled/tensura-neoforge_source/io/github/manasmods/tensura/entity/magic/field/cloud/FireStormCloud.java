package io.github.manasmods.tensura.entity.magic.field.cloud;

import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FireStormCloud extends AreaCloud {
   private static final EntityDataAccessor<Float> BURN_HEIGHT = SynchedEntityData.defineId(FireStormCloud.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> TICK_EACH_SURGE = SynchedEntityData.defineId(FireStormCloud.class, EntityDataSerializers.INT);
   protected float surgeDamage = 0.0F;
   protected float secondarySurgeDamage = 0.0F;

   public FireStormCloud(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public FireStormCloud(Level level, LivingEntity entity) {
      this((EntityType<? extends Projectile>)MiscEntityTypes.FIRE_STORM.get(), level);
      this.setOwner(entity);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(BURN_HEIGHT, 2.0F);
      builder.define(TICK_EACH_SURGE, 20);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setBurntHeight(compound.getFloat("BurnHeight"));
      this.setTickEachSurge(compound.getInt("TickEachSurge"));
      this.setSurgeDamage(compound.getFloat("SurgeDamage"));
      this.setSecondarySurgeDamage(compound.getFloat("SecondarySurgeDamage"));
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putFloat("BurnHeight", this.getBurnHeight());
      compound.putInt("TickEachSurge", this.getTickEachSurge());
      compound.putFloat("SurgeDamage", this.getSurgeDamage());
      compound.putFloat("SecondarySurgeDamage", this.getSecondarySurgeDamage());
   }

   public float getBurnHeight() {
      return (Float)this.getEntityData().get(BURN_HEIGHT);
   }

   public void setBurntHeight(float pRadius) {
      this.getEntityData().set(BURN_HEIGHT, Mth.clamp(pRadius, 0.0F, 50.0F));
   }

   public int getTickEachSurge() {
      return Math.max(1, (Integer)this.getEntityData().get(TICK_EACH_SURGE));
   }

   public void setTickEachSurge(int tick) {
      this.getEntityData().set(TICK_EACH_SURGE, tick);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.FIRE_ELEMENTAL;
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
   public void tick() {
      super.tick();
      if (!this.level().isClientSide() && this.getAge() % this.getTickEachSurge() == 0) {
         this.hitTargetOnSurge();
      }

      if (this.getAge() % (this.getTickEachSurge() + 10) < 10) {
         this.playSound((SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), 1.0F, 1.0F);
      }

      if (this.tickCount % 10 == 0) {
         this.playSound(SoundEvents.FIRE_AMBIENT, 2.0F, 1.0F);
      }
   }

   @Override
   protected void hitTarget(boolean instant) {
      if (!this.level().isClientSide()) {
         if (this.getAge() % this.getTickEachSurge() != 0) {
            double radius = this.getVisualSize();
            AABB aabb = new AABB(this.getBoundingBox().getMinPosition().add(0.0, this.getHeight() * -1.0F, 0.0), this.getBoundingBox().getMaxPosition());

            for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
               if (this.canHitEntity(target)) {
                  Vec3 ground = ObjectSelectionHelper.getNearestGround(target.position(), this.level(), 10.0, target);
                  if (!(this.blockPosition().distToCenterSqr(ground) > radius * radius) && !(target.getBlockY() > ground.y + this.getBurnHeight())) {
                     this.applyEffect(target, instant);
                  }
               }
            }
         }
      }
   }

   protected void hitTargetOnSurge() {
      if (!this.level().isClientSide()) {
         double radius = this.getVisualSize();
         AABB aabb = new AABB(this.getBoundingBox().getMinPosition().add(0.0, this.getHeight() * -1.0F, 0.0), this.getBoundingBox().getMaxPosition());

         for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (this.canHitEntity(target)) {
               Vec3 ground = ObjectSelectionHelper.getNearestGround(target.position(), this.level(), 10.0, target);
               if (!(this.blockPosition().distToCenterSqr(ground) > radius * radius)) {
                  Magic.MagicType magicType = this.isElementalAttack() ? Magic.MagicType.SPIRITUAL : null;
                  DamageSource source = this.getDamageSource(1.0F).tensura$setMagicType(magicType);
                  if (this.getSecondarySurgeDamage() > 0.0F) {
                     Magic.MagicType secondMagicType = this.isElementalAttack() ? Magic.MagicType.ASPECTUAL : null;
                     DamageSource secondSource = this.getDamageSource(this.getSecondaryDamageType(), 1.0F).tensura$setMagicType(secondMagicType);
                     TensuraDamageHelper.hurtDouble(target, source, this.getSurgeDamage(), secondSource, this.getSecondarySurgeDamage());
                  } else if (this.getSurgeDamage() > 0.0F) {
                     target.hurt(source, this.getSurgeDamage());
                  }
               }
            }
         }
      }
   }

   @Override
   public void spawnAmbientParticles() {
      BlockPos center = this.blockPosition();
      int radius = (int)this.getVisualSize();

      for (int dx = -radius; dx <= radius; dx++) {
         for (int dz = -radius; dz <= radius; dz++) {
            int x = center.getX() + dx;
            int z = center.getZ() + dz;
            Vec3 ground = this.getOwner() != null
               ? ObjectSelectionHelper.getNearestGround(new Vec3(x, center.getY(), z), this.level(), 10.0, this.getOwner())
               : new Vec3(x, center.getY(), z);
            if (!(center.distToCenterSqr(ground) > radius * radius)) {
               Vec3 pos = new Vec3(x + 0.5, ground.y + 1.0, z + 0.5);
               if (this.getAge() % (this.getTickEachSurge() + 10) < 10) {
                  this.spawnFirePillar(pos, this.getHeight());
               } else if (this.getRandom().nextFloat() <= 0.2F) {
                  this.spawnFirePillar(pos, Math.max(0.0F, this.getBurnHeight() - 2.0F));
               }

               this.spawnHeatEffects(pos, 0.0F);
            }
         }
      }
   }

   private void spawnFirePillar(Vec3 position, float height) {
      RandomSource rand = this.getRandom();
      double dX = rand.nextGaussian() * 0.5;
      double dY = rand.nextGaussian() * 0.25;
      double dZ = rand.nextGaussian() * 0.5;
      this.level()
         .addParticle(
            (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(),
            position.x + dX,
            position.y + dY,
            position.z + dZ,
            0.0,
            rand.nextDouble() * 0.1 * height + 0.05,
            0.0
         );
   }

   private void spawnHeatEffects(Vec3 position, float height) {
      RandomSource rand = this.getRandom();
      if (!rand.nextBoolean()) {
         double dX = rand.nextGaussian() * 0.5;
         double dY = rand.nextGaussian() * 0.25;
         double dZ = rand.nextGaussian() * 0.5;
         this.level()
            .addParticle(
               (ParticleOptions)TensuraParticleTypes.HEAT_EFFECT.get(),
               position.x + dX,
               position.y + dY,
               position.z + dZ,
               0.0,
               rand.nextDouble() * 0.1 * height + 0.05,
               0.0
            );
      }
   }

   @Generated
   public float getSurgeDamage() {
      return this.surgeDamage;
   }

   @Generated
   public void setSurgeDamage(float surgeDamage) {
      this.surgeDamage = surgeDamage;
   }

   @Generated
   public float getSecondarySurgeDamage() {
      return this.secondarySurgeDamage;
   }

   @Generated
   public void setSecondarySurgeDamage(float secondarySurgeDamage) {
      this.secondarySurgeDamage = secondarySurgeDamage;
   }
}
