package io.github.manasmods.tensura.entity.template;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import java.util.List;
import lombok.Generated;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TensuraRideableEntity extends TensuraTamableEntity implements Saddleable, PlayerRideableJumping {
   protected static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(TensuraRideableEntity.class, EntityDataSerializers.BOOLEAN);
   protected boolean isRiddenJumping;
   protected float playerJumpPendingScale;
   protected ItemStack saddleItem = ItemStack.EMPTY;

   protected TensuraRideableEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SADDLED, Boolean.FALSE);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      this.saveSaddle(compound);
   }

   protected void saveSaddle(CompoundTag compound) {
      if (!this.saddleItem.isEmpty()) {
         compound.put("SaddleItem", this.saddleItem.save(this.registryAccess()));
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.readSaddle(compound);
   }

   protected void readSaddle(CompoundTag compound) {
      this.saddleItem = ItemStack.EMPTY;
      if (compound.contains("SaddleItem", 10)) {
         ItemStack saddle = ItemStack.parse(this.registryAccess(), compound.getCompound("SaddleItem")).orElse(ItemStack.EMPTY);
         if (this.isMountSaddle(saddle)) {
            this.saddleItem = saddle;
         }
      }

      this.setSaddled(!this.saddleItem.isEmpty());
   }

   public boolean isSaddled() {
      return (Boolean)this.entityData.get(SADDLED);
   }

   public void setSaddled(boolean chested) {
      this.entityData.set(SADDLED, chested);
   }

   public boolean isSaddleable() {
      return this.isAlive() && !this.isBaby() && this.isTame();
   }

   public boolean isSaddleRequired() {
      return true;
   }

   public boolean isMountSaddle(ItemStack stack) {
      return stack.is((Item)TensuraMaterialItems.MONSTER_SADDLE.get());
   }

   public int getRiderSeats() {
      return 1;
   }

   protected boolean canAddPassenger(Entity entity) {
      return this.getPassengers().size() < this.getRiderSeats() ? true : entity instanceof LivingEntity rider && this.isOwnedBy(rider);
   }

   public boolean isRideable(Player rider) {
      if (this.isBaby()) {
         return false;
      } else if (rider.isSecondaryUseActive()) {
         return false;
      } else {
         return !this.isSaddleRequired() ? this.canAddPassenger(rider) : this.isSaddled() && this.canAddPassenger(rider);
      }
   }

   public boolean canSprint() {
      return true;
   }

   protected float getRiddenSpeed(Player player) {
      float speed = (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
      if (this.isInWater()) {
         return speed * (float)this.getAttributeValue(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER);
      } else if (this.isInLava()) {
         return speed * (float)this.getAttributeValue(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER);
      } else {
         return this.isNoGravity() ? speed * 2.0F : speed;
      }
   }

   protected float getRiddenJump(float f) {
      return this.getJumpPower(f);
   }

   protected Vec2 getRiddenRotation(LivingEntity livingEntity) {
      return new Vec2(livingEntity.getXRot() * 0.5F, livingEntity.getYRot());
   }

   protected Vec3 getRiddenInput(Player player, Vec3 vec3) {
      float f = player.xxa * 0.5F;
      float g = player.zza;
      if (g <= 0.0F) {
         g *= 0.25F;
      }

      return new Vec3(f, 0.0, g);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      if ((!this.isSaddleRequired() || this.isSaddled()) && this.getFirstPassenger() instanceof Player rider && this.isOwnedBy(rider)) {
         return rider;
      } else {
         return this.isTame() ? null : super.getControllingPassenger();
      }
   }

   protected void positionRider(Entity entity, MoveFunction moveFunction) {
      super.positionRider(entity, moveFunction);
      if (entity instanceof LivingEntity rider) {
         rider.yBodyRot = this.yBodyRot;
      }
   }

   public void equipSaddle(ItemStack itemStack, @Nullable SoundSource soundSource) {
      this.saddleItem = itemStack;
      this.setSaddled(true);
      this.playSound(this.getSaddleSoundEvent(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public InteractionResult getRidingInteraction(Player player, InteractionHand hand) {
      if (this.isSaddleable()) {
         if (this.isSaddleRequired()) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (!this.isSaddled() && this.isMountSaddle(itemstack)) {
               this.equipSaddle(itemstack, null);
               if (!player.hasInfiniteMaterials()) {
                  itemstack.shrink(1);
               }

               return InteractionResult.sidedSuccess(this.level().isClientSide);
            }

            if (this.isSaddled() && itemstack.is(Items.SHEARS)) {
               this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
               this.spawnAtLocation(this.saddleItem);
               this.setSaddled(false);
               return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
         }

         if (this.isRideable(player)) {
            this.doPlayerRide(player);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
         }
      }

      return InteractionResult.PASS;
   }

   public void setOrderedToSit(boolean sit) {
      super.setOrderedToSit(sit);
      if (sit && this.getControllingPassenger() == null) {
         for (Entity passenger : this.getPassengers()) {
            passenger.stopRiding();
         }
      }
   }

   protected boolean doPlayerRide(Player player) {
      if (this.getPassengers().size() >= this.getRiderSeats() && this.getControllingPassenger() == null && this.isOwnedBy(player)) {
         ((Entity)this.getPassengers().getLast()).stopRiding();
      }

      if (!this.level().isClientSide()) {
         player.setYRot(this.getYRot());
         player.setXRot(this.getXRot());
         this.setOrderedToSit(false);
         this.setInSittingPose(false);
         this.setWandering(false);
         this.getNavigation().stop();
         return player.startRiding(this);
      } else {
         return false;
      }
   }

   protected void addPassenger(Entity entity) {
      if (entity.getVehicle() != this) {
         throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
      }

      if (this.getPassengers().isEmpty()) {
         this.passengers = ImmutableList.of(entity);
      } else {
         List<Entity> list = Lists.newArrayList(this.passengers);
         if (!(!this.level().isClientSide() && entity instanceof Player player) || !this.isOwnedBy(player) && this.getFirstPassenger() instanceof Player) {
            list.add(entity);
         } else {
            list.addFirst(entity);
         }

         this.passengers = ImmutableList.copyOf(list);
      }

      this.gameEvent(GameEvent.ENTITY_MOUNT, entity);
   }

   protected void removePassenger(Entity rider) {
      super.removePassenger(rider);
      if (this.getControllingPassenger() == null) {
         this.setSprinting(false);
      }
   }

   protected void tickRidden(Player player, Vec3 vec3) {
      super.tickRidden(player, vec3);
      Vec2 vec2 = this.getRiddenRotation(player);
      this.setRot(vec2.y, vec2.x);
      this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
      if (this.isControlledByLocalInstance()) {
         this.setSprinting(player.isSprinting());
         if (this.isInLiquid()) {
            this.applyFluidControlledMovement(player, vec3);
         }

         if (this.canExecuteRidersJump()) {
            this.setRiddenJumping(false);
            if (this.playerJumpPendingScale > 0.0F && !this.isRiddenJumping()) {
               this.executeRidersJump(this.playerJumpPendingScale, vec3);
            }

            this.playerJumpPendingScale = 0.0F;
         }

         this.applyExtraRidingMovement(player, vec3);
      }
   }

   protected void applyFluidControlledMovement(Player controller, Vec3 vec3) {
      double threshold = Math.min(this.getFluidJumpThreshold(), 0.5);
      if ((this.isInLava() || this.getFluidHeight(FluidTags.WATER) > threshold) && controller.jumping) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.07, 0.0));
      } else if (TensuraKeybinds.DODGE.isDown()) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.07, 0.0));
      }
   }

   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
   }

   protected boolean canExecuteRidersJump() {
      return this.onGround();
   }

   protected void executeRidersJump(float f, Vec3 vec3) {
      double d = this.getRiddenJump(f);
      Vec3 vec32 = this.getDeltaMovement();
      this.setDeltaMovement(vec32.x, d, vec32.z);
      this.setRiddenJumping(true);
      this.hasImpulse = true;
      if (vec3.z > 0.0) {
         float g = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
         float h = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
         this.setDeltaMovement(this.getDeltaMovement().add(-0.4F * g * f, 0.0, 0.4F * h * f));
      }
   }

   public void onPlayerJump(int i) {
      boolean saddled = !this.isSaddleRequired() || this.isSaddled();
      if (saddled && this.canExecuteRidersJump()) {
         if (i < 0) {
            i = 0;
         }

         if (i >= 90) {
            this.playerJumpPendingScale = 1.0F;
         } else {
            this.playerJumpPendingScale = 0.4F + 0.4F * i / 90.0F;
         }
      }
   }

   public boolean canJump() {
      return !this.isSaddleRequired() || this.isSaddled();
   }

   public void handleStartJump(int i) {
      if (this.canExecuteRidersJump()) {
         this.playJumpSound();
      } else {
         this.playerJumpPendingScale = 0.0F;
      }
   }

   public void handleStopJump() {
   }

   protected void playJumpSound() {
      this.playSound(SoundEvents.HORSE_JUMP, 0.4F, 1.0F);
   }

   @Nullable
   private Vec3 getDismountLocationInDirection(Vec3 vec3, LivingEntity livingEntity) {
      double d = this.getX() + vec3.x;
      double e = this.getBoundingBox().minY;
      double f = this.getZ() + vec3.z;
      MutableBlockPos mutableBlockPos = new MutableBlockPos();
      UnmodifiableIterator var10 = livingEntity.getDismountPoses().iterator();

      while (var10.hasNext()) {
         Pose pose = (Pose)var10.next();
         mutableBlockPos.set(d, e, f);
         double g = this.getBoundingBox().maxY + 0.75;

         do {
            double h = this.level().getBlockFloorHeight(mutableBlockPos);
            if (mutableBlockPos.getY() + h > g) {
               break;
            }

            if (DismountHelper.isBlockFloorValid(h)) {
               AABB aABB = livingEntity.getLocalBoundsForPose(pose);
               Vec3 vec32 = new Vec3(d, mutableBlockPos.getY() + h, f);
               if (DismountHelper.canDismountTo(this.level(), livingEntity, aABB.move(vec32))) {
                  livingEntity.setPose(pose);
                  return vec32;
               }
            }

            mutableBlockPos.move(Direction.UP);
         } while (!(mutableBlockPos.getY() >= g));
      }

      return null;
   }

   @NotNull
   public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
      Vec3 vec3 = getCollisionHorizontalEscapeVector(
         this.getBbWidth(), livingEntity.getBbWidth(), this.getYRot() + (livingEntity.getMainArm() == HumanoidArm.RIGHT ? 90.0F : -90.0F)
      );
      Vec3 vec32 = this.getDismountLocationInDirection(vec3, livingEntity);
      if (vec32 != null) {
         return vec32;
      }

      Vec3 vec33 = getCollisionHorizontalEscapeVector(
         this.getBbWidth(), livingEntity.getBbWidth(), this.getYRot() + (livingEntity.getMainArm() == HumanoidArm.LEFT ? 90.0F : -90.0F)
      );
      Vec3 vec34 = this.getDismountLocationInDirection(vec33, livingEntity);
      return vec34 != null ? vec34 : this.position();
   }

   public double getFluidJumpThreshold() {
      double threshold = super.getFluidJumpThreshold() + this.getBbHeight() / 2.0F;
      return Math.min(threshold, this.getNavigation().isStuck() ? 0.4 : this.getEyeHeight() - 0.1F);
   }

   public void dropSaddle() {
      if (this.isSaddled() && !this.saddleItem.isEmpty()) {
         this.spawnAtLocation(this.saddleItem);
         this.setSaddled(false);
      }
   }

   public boolean causeFallDamage(float f, float g, DamageSource damageSource) {
      if (f > 1.0F) {
         this.playSound(SoundEvents.HORSE_LAND, 0.4F, 1.0F);
      }

      int i = this.calculateFallDamage(f, g);
      if (i <= 0) {
         return false;
      }

      this.hurt(damageSource, i);
      if (this.isVehicle()) {
         for (Entity entity : this.getIndirectPassengers()) {
            entity.hurt(damageSource, i);
         }
      }

      this.playBlockFallSound();
      return true;
   }

   @Generated
   public boolean isRiddenJumping() {
      return this.isRiddenJumping;
   }

   @Generated
   public void setRiddenJumping(boolean isRiddenJumping) {
      this.isRiddenJumping = isRiddenJumping;
   }
}
