package io.github.manasmods.tensura.entity.magic.misc;

import io.github.manasmods.tensura.entity.monster.CharybdisEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;

public class PrimedCharybdisCoreEntity extends Entity {
   private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(PrimedCharybdisCoreEntity.class, EntityDataSerializers.INT);

   public PrimedCharybdisCoreEntity(EntityType<? extends PrimedCharybdisCoreEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.blocksBuilding = true;
   }

   public PrimedCharybdisCoreEntity(Level pLevel, double pX, double pY, double pZ) {
      this((EntityType<? extends PrimedCharybdisCoreEntity>)MiscEntityTypes.CHARYBDIS_CORE.get(), pLevel);
      this.setPos(pX, pY, pZ);
      this.unstableJump();
      this.setFuse(200);
      this.xo = pX;
      this.yo = pY;
      this.zo = pZ;
   }

   private void unstableJump() {
      double d0 = this.level().random.nextDouble() * Math.PI * 2.0;
      this.setDeltaMovement(-Math.sin(d0) * 0.1, 0.3F, -Math.cos(d0) * 0.1);
   }

   public void tick() {
      if (!this.isNoGravity()) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
      if (this.onGround()) {
         this.unstableJump();
      }

      int i = this.getFuse() - 1;
      this.setFuse(i);
      if (i <= 0) {
         this.discard();
         if (!this.level().isClientSide) {
            this.explode();
         }
      } else {
         this.updateInWaterStateAndDoFluidPushing();
         if (i % 10 != 0) {
            return;
         }

         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.SCULK_SOUL);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.SOUL.get());
      }
   }

   protected void explode() {
      this.level().explode(this, this.getX(), this.getY(0.0625), this.getZ(), 10.0F, ExplosionInteraction.MOB);
      CharybdisEntity entity = new CharybdisEntity((EntityType<? extends CharybdisEntity>)MonsterEntityTypes.CHARYBDIS.get(), this.level());
      entity.moveTo(this.position().add(0.0, 1.0, 0.0));
      entity.finalizeSpawn((ServerLevel)this.level(), this.level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.EVENT, null);
      this.level().addFreshEntity(entity);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.EXPLOSION_EMITTER, 4.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.CLOUD, 4.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.FLASH, 4.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.FLASH, 3.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get(), 3.0);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_FUSE_ID, 80);
   }

   protected void addAdditionalSaveData(CompoundTag pCompound) {
      pCompound.putShort("Fuse", (short)this.getFuse());
   }

   protected void readAdditionalSaveData(CompoundTag pCompound) {
      this.setFuse(pCompound.getShort("Fuse"));
   }

   public void setFuse(int pLife) {
      this.entityData.set(DATA_FUSE_ID, pLife);
   }

   public int getFuse() {
      return (Integer)this.entityData.get(DATA_FUSE_ID);
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.NONE;
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }
}
