package io.github.manasmods.tensura.entity.magic.misc;

import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MadOrbsEntity extends TensuraProjectile implements GeoEntity {
   private static final EntityDataAccessor<Integer> SPHERES_NUMBERS = SynchedEntityData.defineId(MadOrbsEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SHOOT_COOLDOWN = SynchedEntityData.defineId(MadOrbsEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public MadOrbsEntity(EntityType<? extends MadOrbsEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
      this.noPhysics = true;
   }

   public MadOrbsEntity(Level pLevel, @Nullable LivingEntity pOwner) {
      this((EntityType<? extends MadOrbsEntity>)MiscEntityTypes.MAD_ORBS.get(), pLevel);
      this.setOwner(pOwner);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SPHERES_NUMBERS, 6);
      builder.define(SHOOT_COOLDOWN, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putShort("Cooldown", (short)this.getShootCooldown());
      pCompound.putShort("Spheres", (short)this.getSpheres());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setShootCooldown(pCompound.getShort("Cooldown"));
      this.setSpheres(pCompound.getShort("Spheres"));
   }

   public void setSpheres(int pLife) {
      this.entityData.set(SPHERES_NUMBERS, pLife);
   }

   public int getSpheres() {
      return (Integer)this.entityData.get(SPHERES_NUMBERS);
   }

   public void setShootCooldown(int i) {
      this.entityData.set(SHOOT_COOLDOWN, i);
   }

   public int getShootCooldown() {
      return (Integer)this.entityData.get(SHOOT_COOLDOWN);
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      return false;
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return pDistance < 1024.0 || super.shouldRenderAtSqrDistance(pDistance);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return this.isRemoved() || source.is(DamageTypes.FELL_OUT_OF_WORLD);
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.tickCount % 100 == 0 && this.getSpheres() < 6) {
            this.setSpheres(this.getSpheres() + 1);
         }

         if (this.getOwner() instanceof LivingEntity owner) {
            if (owner.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAD_OGRE)) && owner.isAlive()) {
               if (this.tickCount % 20 == 0) {
                  List<MadOrbsEntity> storms = this.level()
                     .getEntitiesOfClass(MadOrbsEntity.class, owner.getBoundingBox(), entityData -> entityData.getOwner() == owner && entityData != this);
                  if (!storms.isEmpty() || owner.level() != this.level()) {
                     this.remove();
                     return;
                  }
               } else if (owner.level() != this.level()) {
                  this.remove();
                  return;
               }

               this.updateAngle(owner);
            } else {
               this.remove();
            }
         }
      }
   }

   public void updateAngle(LivingEntity owner) {
      this.setPos(owner.getX(), owner.getEyePosition().y(), owner.getZ());
      this.setRot(owner.getYRot(), owner.getXRot());
      if (this.isShiftKeyDown() != owner.isShiftKeyDown()) {
         this.setShiftKeyDown(owner.isShiftKeyDown());
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(
            this,
            "loopController",
            10,
            event -> event.setAndContinue(RawAnimation.begin().thenLoop(this.isShiftKeyDown() ? "animation.mad_orbs.circle" : "animation.mad_orbs.back_circle"))
         )
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
