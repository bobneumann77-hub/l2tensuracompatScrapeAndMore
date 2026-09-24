package io.github.manasmods.tensura.entity.multipart;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraRideableEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ILivingPartEntity;
import io.github.manasmods.tensura.network.s2c.HurtLivingPartPayload;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LivingMultipartBody extends TensuraMountEntity implements ILivingPartEntity {
   private static final EntityDataAccessor<Integer> BODY_INDEX = SynchedEntityData.defineId(LivingMultipartBody.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> BODY_X_ROT = SynchedEntityData.defineId(LivingMultipartBody.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Optional<UUID>> HEAD_UUID = SynchedEntityData.defineId(
      LivingMultipartBody.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(
      LivingMultipartBody.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(
      LivingMultipartBody.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Boolean> END_SEGMENT = SynchedEntityData.defineId(LivingMultipartBody.class, EntityDataSerializers.BOOLEAN);
   protected float radius;
   protected float angleYaw;
   private double prevHeight = 0.0;
   private int prevParentHurtTime = 0;
   private int prevParentDeathTime = 0;
   public EntityDimensions multipartSize;
   private final MutableBlockPos tensura$fluidPos = new MutableBlockPos();

   public LivingMultipartBody(EntityType<? extends LivingMultipartBody> type, Level worldIn) {
      super(type, worldIn);
      this.multipartSize = type.getDimensions();
   }

   public LivingMultipartBody(EntityType<? extends LivingMultipartBody> type, LivingEntity parent) {
      super(type, parent.level());
      this.setParent(parent);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(HEAD_UUID, Optional.empty());
      builder.define(PARENT_UUID, Optional.empty());
      builder.define(CHILD_UUID, Optional.empty());
      builder.define(BODY_INDEX, 0);
      builder.define(BODY_X_ROT, 0.0F);
      builder.define(END_SEGMENT, Boolean.FALSE);
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getHeadId() != null) {
         compound.putUUID("HeadUUID", this.getHeadId());
      }

      if (this.getParentId() != null) {
         compound.putUUID("ParentUUID", this.getParentId());
      }

      if (this.getChildId() != null) {
         compound.putUUID("ChildUUID", this.getChildId());
      }

      compound.putBoolean("EndSegment", this.isEndSegment());
      compound.putInt("BodyIndex", this.getBodyIndex());
      compound.putFloat("PartAngle", this.angleYaw);
      compound.putFloat("PartRadius", this.radius);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.hasUUID("HeadUUID")) {
         this.setHeadId(compound.getUUID("HeadUUID"));
      }

      if (compound.hasUUID("ParentUUID")) {
         this.setParentId(compound.getUUID("ParentUUID"));
      }

      if (compound.hasUUID("ChildUUID")) {
         this.setChildId(compound.getUUID("ChildUUID"));
      }

      this.setEndSegment(compound.getBoolean("EndSegment"));
      this.setBodyIndex(compound.getInt("BodyIndex"));
      this.angleYaw = compound.getFloat("PartAngle");
      this.radius = compound.getFloat("PartRadius");
   }

   public int getBodyIndex() {
      return (Integer)this.entityData.get(BODY_INDEX);
   }

   public void setBodyIndex(int index) {
      this.entityData.set(BODY_INDEX, index);
   }

   @Nullable
   @Override
   public UUID getHeadId() {
      return (UUID)((Optional)this.entityData.get(HEAD_UUID)).orElse(null);
   }

   public void setHeadId(@Nullable UUID uniqueId) {
      this.entityData.set(HEAD_UUID, Optional.ofNullable(uniqueId));
   }

   @Override
   public Entity getHead() {
      UUID id = this.getHeadId();
      return id != null && !this.level().isClientSide ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public void setHead(Entity entity) {
      this.setHeadId(entity.getUUID());
   }

   @Nullable
   public UUID getParentId() {
      return (UUID)((Optional)this.entityData.get(PARENT_UUID)).orElse(null);
   }

   public void setParentId(@Nullable UUID uniqueId) {
      this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
   }

   @Nullable
   public Entity getParent() {
      UUID id = this.getParentId();
      return id != null && !this.level().isClientSide ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public void setParent(Entity entity) {
      this.setParentId(entity.getUUID());
   }

   @Nullable
   public UUID getChildId() {
      return (UUID)((Optional)this.entityData.get(CHILD_UUID)).orElse(null);
   }

   public Entity getChild() {
      UUID id = this.getChildId();
      return id != null && !this.level().isClientSide ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public void setChildId(@Nullable UUID uniqueId) {
      this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
   }

   public boolean isEndSegment() {
      return (Boolean)this.entityData.get(END_SEGMENT);
   }

   public void setEndSegment(boolean end) {
      this.entityData.set(END_SEGMENT, end);
   }

   public float getXRot() {
      return (Float)this.entityData.get(BODY_X_ROT);
   }

   public float getBackOffset() {
      return this.getBbWidth() / 2.0F;
   }

   public boolean is(@NotNull Entity entity) {
      return this == entity || this.getHead() == entity || this.getParent() == entity;
   }

   @Override
   public boolean isAlliedTo(Entity pEntity) {
      if (this.getHead() != null && pEntity.isAlliedTo(this.getHead())) {
         return true;
      } else {
         return Objects.equals(this.getHeadId(), pEntity.getUUID()) ? true : super.isAlliedTo(pEntity);
      }
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.getParent() != null;
   }

   public boolean isInvulnerableTo(@NotNull DamageSource source) {
      return this.getParent() == null ? false : this.getParent().isInvulnerableTo(source);
   }

   public boolean isNoGravity() {
      return false;
   }

   public boolean isPickable() {
      return true;
   }

   @Override
   public boolean canMate(@NotNull Animal pOtherAnimal) {
      return false;
   }

   @Override
   public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
      return false;
   }

   public boolean startRiding(@NotNull Entity entityIn) {
      return false;
   }

   public boolean hurt(@NotNull DamageSource source, float damage) {
      Entity parent = this.getParent();
      return parent == null ? false : parent.hurt(source, damage);
   }

   public boolean addEffect(MobEffectInstance pEffectInstance, @Nullable Entity pEntity) {
      return this.getHead() instanceof LivingEntity head ? head.addEffect(pEffectInstance, pEntity) : super.addEffect(pEffectInstance, pEntity);
   }

   public boolean removeEffect(Holder<MobEffect> pEffect) {
      return this.getHead() instanceof LivingEntity head ? head.removeEffect(pEffect) : super.removeEffect(pEffect);
   }

   public void heal(float pHealAmount) {
      if (this.getHead() instanceof LivingEntity head) {
         head.heal(pHealAmount);
      } else {
         super.heal(pHealAmount);
      }
   }

   protected void updateInvisibilityStatus() {
   }

   public void setInvisible(boolean pInvisible) {
      super.setInvisible(pInvisible);
      if (this.getChild() != null) {
         this.getChild().setInvisible(pInvisible);
      }
   }

   public void setInSittingPose(boolean pSitting) {
      super.setInSittingPose(pSitting);
      if (this.getChild() instanceof TamableAnimal child) {
         child.setInSittingPose(pSitting);
      }
   }

   protected void miscUpdate() {
      this.portalProcess = null;
      this.setDeltaMovement(Vec3.ZERO);
      if (!this.level().isClientSide() && this.getHead() != null && this.isCurrentlyGlowing() != this.getHead().isCurrentlyGlowing()) {
         this.setGlowingTag(this.getHead().isCurrentlyGlowing());
      }
   }

   @Override
   public void tick() {
      super.tick();
      this.miscUpdate();
      if (this.tickCount > 1) {
         Entity parent = this.getParent();
         if (this.tickCount % 5 == 0) {
            this.refreshDimensions();
         }

         if (parent != null && !this.level().isClientSide()) {
            if (parent instanceof TensuraTamableEntity parentEntity) {
               int parentHurtTime = parentEntity.hurtTime;
               int parentDeathTime = parentEntity.deathTime;
               if (parentHurtTime > 0 || parentDeathTime > 0) {
                  boolean rising = parentHurtTime > 0 && this.prevParentHurtTime == 0 || parentDeathTime > 0 && this.prevParentDeathTime == 0;
                  if (rising && this.level() instanceof ServerLevel serverLevel) {
                     HurtLivingPartPayload payload = new HurtLivingPartPayload(this.getId(), parentEntity.getId());
                     double maxDistSqr = 16384.0;

                     for (ServerPlayer p : serverLevel.players()) {
                        if (p.distanceToSqr(this) <= maxDistSqr) {
                           NetworkManager.sendToPlayer(p, payload);
                        }
                     }
                  }

                  this.hurtTime = parentHurtTime;
                  this.deathTime = parentDeathTime;
               }

               this.prevParentHurtTime = parentHurtTime;
               this.prevParentDeathTime = parentDeathTime;
               if (parentEntity.isAlive()) {
                  this.setHealth(parentEntity.getHealth());
                  this.setOrderedToSit(parentEntity.isOrderedToSit());
               }
            }

            if (parent.isRemoved()) {
               this.dropEquipment();
               this.remove(RemovalReason.DISCARDED);
               return;
            }

            boolean shouldRemove = this.getHead() instanceof LivingMultipartHead head && !head.isRemoved() && this.getBodyIndex() + 1 > head.getSegmentCount();
            shouldRemove = shouldRemove || parent instanceof LivingMultipartBody body && body.getChild() != this;
            shouldRemove = shouldRemove || parent instanceof LivingMultipartHead head && head.getChild() != this;
            if (shouldRemove) {
               if (parent instanceof LivingMultipartBody body) {
                  body.setEndSegment(true);
               }

               this.dropEquipment();
               this.remove(RemovalReason.DISCARDED);
            }
         } else if (!this.level().isClientSide && this.tickCount > 20) {
            this.remove(RemovalReason.DISCARDED);
            this.dropEquipment();
         }
      }
   }

   public void pushEntities() {
      List<Entity> list = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2, 0.0, 0.2));
      Entity parent = this.getParent();
      if (parent != null) {
         for (Entity entity : list) {
            if (entity.isPushable() && entity != parent && !(entity instanceof ILivingPartEntity part && Objects.equals(part.getHeadId(), this.getUUID()))) {
               entity.push(this);
            }
         }
      }
   }

   public Vec3 repositionParts(float parentOffset, Vec3 parentPosition, float parentXRot, float parentYRot, float ourYRot, boolean doHeight) {
      Vec3 parentButt = parentPosition.add(this.getOffsetVec(-parentOffset * this.getScale(), parentXRot, parentYRot));
      Vec3 ourButt = parentButt.add(this.getOffsetVec((-this.getBackOffset() - 0.5F * this.getBbWidth()) * this.getScale(), this.getXRot(), ourYRot));
      Vec3 avg = new Vec3((parentButt.x + ourButt.x) / 2.0, (parentButt.y + ourButt.y) / 2.0, (parentButt.z + ourButt.z) / 2.0);
      double xDist = parentButt.x - ourButt.x;
      double zDist = parentButt.z - ourButt.z;
      double dist = Math.sqrt(xDist * xDist + zDist * zDist);
      double height = doHeight
         ? this.getLowPartHeight(parentButt.x, parentButt.y, parentButt.z) + this.getHighPartHeight(ourButt.x, ourButt.y, ourButt.z)
         : 0.0;
      if (Math.abs(this.prevHeight - height) > 0.2) {
         this.prevHeight = height;
      }

      double partYDest = Mth.clamp(this.prevHeight, -0.4F, 0.4F);
      float f = (float)(Mth.atan2(zDist, xDist) * 180.0F / (float)Math.PI) - 90.0F;
      float rawAngle = Mth.wrapDegrees((float)(-(Mth.atan2(partYDest, dist) * 180.0 / (float) Math.PI)));
      float f2 = this.getLimitAngle(this.getXRot(), rawAngle, 10.0F);
      this.setXRot(f2);
      this.entityData.set(BODY_X_ROT, f2);
      this.setYRot(f);
      this.yHeadRot = f;
      this.moveTo(avg.x, avg.y, avg.z, f, f2);
      return avg;
   }

   public double getLowPartHeight(double x, double yIn, double z) {
      if (this.isFluidAt(x, yIn, z)) {
         return 0.0;
      }

      double checkAt = 0.0;

      while (checkAt > -3.0 && !this.isOpaqueBlockAt(x, yIn + checkAt, z)) {
         checkAt -= 0.2;
      }

      return checkAt;
   }

   public double getHighPartHeight(double x, double yIn, double z) {
      if (this.isFluidAt(x, yIn, z)) {
         return 0.0;
      }

      double checkAt = 0.0;

      while (checkAt <= 3.0 && this.isOpaqueBlockAt(x, yIn + checkAt, z)) {
         checkAt += 0.2;
      }

      return checkAt;
   }

   public boolean isFluidAt(double x, double y, double z) {
      if (this.noPhysics) {
         return false;
      }

      this.tensura$fluidPos.set((int)x, (int)y, (int)z);
      return !this.level().getFluidState(this.tensura$fluidPos).isEmpty();
   }

   public boolean isOpaqueBlockAt(double x, double y, double z) {
      if (this.noPhysics) {
         return false;
      }

      float f = 1.0F;
      Vec3 vec3 = new Vec3(x, y, z);
      AABB aabb = AABB.ofSize(vec3, 1.0, 1.0E-6, 1.0);
      return this.level()
         .getBlockStates(aabb)
         .filter(Predicate.not(BlockStateBase::isAir))
         .anyMatch(
            shape -> {
               BlockPos blockpos = ObjectSelectionHelper.getBlockPos(vec3);
               return shape.isSuffocating(this.level(), blockpos)
                  && Shapes.joinIsNotEmpty(shape.getCollisionShape(this.level(), blockpos).move(vec3.x, vec3.y, vec3.z), Shapes.create(aabb), BooleanOp.AND);
            }
         );
   }

   @Override
   public void onServerHurt(LivingEntity parent) {
      if (parent.deathTime > 0) {
         this.deathTime = parent.deathTime;
      }

      if (parent.hurtTime > 0) {
         this.hurtTime = parent.hurtTime;
      }
   }

   @Override
   public boolean isSaddleable() {
      return this.getParent() instanceof TensuraRideableEntity parent ? parent.isSaddleable() : super.isSaddleable();
   }

   @Nullable
   @Override
   public LivingEntity getControllingPassenger() {
      return null;
   }

   @Override
   public InteractionResult handleEating(Player player, InteractionHand hand, ItemStack stack) {
      return this.getParent() instanceof TensuraTamableEntity parent ? parent.handleEating(player, hand, stack) : InteractionResult.PASS;
   }
}
