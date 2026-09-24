package io.github.manasmods.tensura.entity.magic.misc;

import io.github.manasmods.tensura.entity.TensuraProjectile;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HazyBlossomEntity extends TensuraProjectile implements GeoEntity {
   private static final EntityDataAccessor<Integer> PETAL_NUMBERS = SynchedEntityData.defineId(HazyBlossomEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> FOLLOW_OWNER = SynchedEntityData.defineId(HazyBlossomEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public HazyBlossomEntity(EntityType<? extends HazyBlossomEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
      this.noPhysics = true;
   }

   public HazyBlossomEntity(Level pLevel, @Nullable LivingEntity pOwner) {
      this((EntityType<? extends HazyBlossomEntity>)MiscEntityTypes.HAZY_BLOSSOM.get(), pLevel);
      this.setOwner(pOwner);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(PETAL_NUMBERS, 6);
      builder.define(FOLLOW_OWNER, true);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("Petals", this.getPetals());
      pCompound.putBoolean("FollowOwner", this.isFollowOwner());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setPetals(pCompound.getInt("Petals"));
      this.setFollowOwner(pCompound.getBoolean("FollowOwner"));
   }

   public void setPetals(int pLife) {
      this.entityData.set(PETAL_NUMBERS, pLife);
   }

   public int getPetals() {
      return (Integer)this.entityData.get(PETAL_NUMBERS);
   }

   public void setFollowOwner(boolean followOwner) {
      this.entityData.set(FOLLOW_OWNER, followOwner);
   }

   public boolean isFollowOwner() {
      return (Boolean)this.entityData.get(FOLLOW_OWNER);
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
      if (!this.level().isClientSide() && this.isFollowOwner()) {
         Entity owner = this.getOwner();
         if (owner != null) {
            boolean dedupCheck = this.tickCount % 20 == 0;
            if (dedupCheck) {
               List<HazyBlossomEntity> entities = this.level()
                  .getEntitiesOfClass(HazyBlossomEntity.class, owner.getBoundingBox(), entityData -> entityData.getOwner() == owner && entityData != this);
               if (!entities.isEmpty() && entities.getFirst().getAge() < this.getAge() || !owner.isAlive() || owner.level() != this.level()) {
                  this.setFollowOwner(false);
                  this.setRemoveIn(100);
                  return;
               }
            } else if (!owner.isAlive() || owner.level() != this.level()) {
               this.setFollowOwner(false);
               this.setRemoveIn(100);
               return;
            }

            double bbHeightHalf = owner.getBbHeight() / 2.0F;
            double ox = owner.getX();
            double oy = owner.getY() + bbHeightHalf;
            double oz = owner.getZ();
            if (owner instanceof Player) {
               this.setPos(ox, oy, oz);
            } else {
               Vec3 view = owner.getViewVector(1.0F);
               this.setPos(ox - view.x, oy - view.y, oz - view.z);
            }

            this.setRot(owner.getYRot(), owner.getXRot());
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(
            this,
            "loopController",
            10,
            event -> {
               if (this.tickCount < 70 && this.isFollowOwner()) {
                  return event.setAndContinue(RawAnimation.begin().thenLoop("animation.blossom.start"));
               }

               int time = this.getLife() - this.getAge();
               return time < 100
                  ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.blossom.end"))
                  : event.setAndContinue(RawAnimation.begin().thenLoop("animation.blossom.loop"));
            }
         )
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
