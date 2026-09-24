package io.github.manasmods.tensura.entity.magic.spike;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MagicSpikeEntity extends SpikeEntity implements GeoEntity {
   private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(MagicSpikeEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(MagicSpikeEntity.class, EntityDataSerializers.FLOAT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public MagicSpikeEntity(EntityType<? extends MagicSpikeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setExtendingTick(4);
   }

   public MagicSpikeEntity(EntityType<? extends MagicSpikeEntity> pEntityType, Level pLevel, LivingEntity pOwner) {
      this(pEntityType, pLevel);
      this.setOwner(pOwner);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(YAW, 0.0F);
      builder.define(PITCH, 0.0F);
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putFloat("Yaw", this.getYaw());
      compound.putFloat("Pitch", this.getPitch());
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setYaw(compound.getFloat("Yaw"));
      this.setPitch(compound.getFloat("Pitch"));
   }

   public float getYaw() {
      return (Float)this.entityData.get(YAW);
   }

   public void setYaw(float yaw) {
      this.entityData.set(YAW, yaw);
   }

   public float getPitch() {
      return (Float)this.entityData.get(PITCH);
   }

   public void setPitch(float pitch) {
      this.entityData.set(PITCH, pitch);
   }

   @Override
   public void doExtendingEffect() {
      if (this.getExtendingTick() - this.getAge() == 19) {
         this.triggerAnim("controller", "start");
      }

      Vec3 vec3 = this.calculateViewVector(this.getYaw(), this.getPitch()).normalize();

      for (Entity target : this.level().getEntities(this, this.getBoundingBox().inflate(0.0, 0.5, 0.0).expandTowards(vec3), this::canHitEntity)) {
         if (this.shouldPushUp()) {
            target.move(MoverType.SHULKER, new Vec3(0.0, Direction.UP.getStepY(), 0.0));
         }

         if (target instanceof LivingEntity living) {
            this.applyEffect(living);
         }
      }
   }

   public ResourceLocation getTexture() {
      return null;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(
               this,
               "loopController",
               5,
               event -> this.getLife() - this.getAge() < 30
                  ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.spike.down"))
                  : event.setAndContinue(RawAnimation.begin().thenLoop("animation.spike.still"))
            ),
            new AnimationController(this, "controller", 0, event -> PlayState.STOP)
               .triggerableAnim("start", RawAnimation.begin().then("animation.spike.up", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
