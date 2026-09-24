package io.github.manasmods.tensura.entity.magic.field.cloud;

import io.github.manasmods.tensura.entity.magic.field.AreaField;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class AreaCloud extends AreaField {
   private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(AreaCloud.class, EntityDataSerializers.FLOAT);

   public AreaCloud(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(HEIGHT, 0.5F);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag pCompound) {
      this.setHeight(pCompound.getFloat("Height"));
      super.readAdditionalSaveData(pCompound);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag pCompound) {
      pCompound.putFloat("Height", this.getHeight());
      super.addAdditionalSaveData(pCompound);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (HEIGHT.equals(pKey)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   public float getHeight() {
      return (Float)this.getEntityData().get(HEIGHT);
   }

   public void setHeight(float pRadius) {
      this.getEntityData().set(HEIGHT, Mth.clamp(pRadius, 0.0F, 50.0F));
      this.refreshDimensions();
   }

   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return EntityDimensions.scalable(this.getSize() * 2.0F, this.getHeight());
   }

   @Override
   protected void hitTarget(boolean instant) {
      if (!this.level().isClientSide()) {
         for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (this.canHitEntity(target) && target.distanceToSqr(this.getX(), this.getY(), this.getZ()) < this.getSize() * this.getSize()) {
               this.applyEffect(target, instant);
            }
         }
      }
   }
}
