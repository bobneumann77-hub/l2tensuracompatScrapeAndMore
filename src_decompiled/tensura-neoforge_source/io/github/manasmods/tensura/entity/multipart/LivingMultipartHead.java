package io.github.manasmods.tensura.entity.multipart;

import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.subclass.ILivingPartEntity;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class LivingMultipartHead extends TensuraMountEntity {
   private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(
      LivingMultipartHead.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(LivingMultipartHead.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SEGMENT_COUNT = SynchedEntityData.defineId(LivingMultipartHead.class, EntityDataSerializers.INT);
   public final float[] ringBuffer = new float[64];
   public int ringBufferIndex = -1;
   protected LivingMultipartBody[] parts;

   public LivingMultipartHead(EntityType<? extends LivingMultipartHead> type, Level worldIn) {
      super(type, worldIn);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CHILD_UUID, Optional.empty());
      builder.define(CHILD_ID, -1);
      builder.define(SEGMENT_COUNT, 5);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getChildId() != null) {
         compound.putUUID("ChildUUID", this.getChildId());
      }

      compound.putInt("SegCount", this.getSegmentCount());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.hasUUID("ChildUUID")) {
         this.setChildId(compound.getUUID("ChildUUID"));
      }

      this.setSegmentCount(compound.getInt("SegCount"));
   }

   public int getSegmentCount() {
      return Math.max((Integer)this.entityData.get(SEGMENT_COUNT), 1);
   }

   public void setSegmentCount(int segments) {
      this.entityData.set(SEGMENT_COUNT, segments);
   }

   @Nullable
   public UUID getChildId() {
      return (UUID)((Optional)this.entityData.get(CHILD_UUID)).orElse(null);
   }

   public void setChildId(@Nullable UUID uniqueId) {
      this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
   }

   public Entity getChild() {
      UUID id = this.getChildId();
      return id != null && !this.level().isClientSide ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   @Override
   public boolean isAlliedTo(Entity pEntity) {
      if (pEntity instanceof LivingMultipartBody body) {
         if (Objects.equals(body.getHeadId(), this.getUUID())) {
            return true;
         }

         if (body.getHead() != null && pEntity.isAlliedTo(body.getHead())) {
            return true;
         }
      }

      return super.isAlliedTo(pEntity);
   }

   public boolean canAttack(LivingEntity pTarget) {
      return pTarget instanceof LivingMultipartBody body && Objects.equals(body.getHeadId(), this.getUUID()) ? false : super.canAttack(pTarget);
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 1;
   }

   private boolean shouldReplaceParts() {
      if (this.parts != null && this.parts[0] != null && this.parts.length == this.getSegmentCount()) {
         for (int i = 0; i < this.getSegmentCount(); i++) {
            if (this.parts[i] == null) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   abstract LivingMultipartBody createBody(LivingEntity var1, boolean var2);

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

   public void pushEntities() {
      for (Entity entity : this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2, 0.0, 0.2))) {
         if (entity.isPushable() && !(entity instanceof ILivingPartEntity part && Objects.equals(part.getHeadId(), this.getUUID()))) {
            entity.push(this);
         }
      }
   }

   protected void updateRot() {
      this.yBodyRot = Mth.clamp(this.getYRot(), this.yBodyRot - 2.0F, this.yBodyRot + 2.0F);
      this.yHeadRot = this.yBodyRot;
   }

   @Override
   public void tick() {
      super.tick();
      this.portalProcess = null;
      this.updateRot();
      this.updateRingBuffer();
      if (!this.level().isClientSide) {
         Entity child = this.getChild();
         if (child == null) {
            LivingEntity partParent = this;
            this.parts = new LivingMultipartBody[this.getSegmentCount()];
            Vec3 prevPos = this.position();
            float backOffset = this.getBbWidth() / 2.0F;

            for (int i = 0; i < this.getSegmentCount(); i++) {
               float prevReqRot = this.calcPartRotation(i) + this.getPartYaw(i);
               float reqRot = this.calcPartRotation(i + 1) + this.getPartYaw(i);
               LivingMultipartBody part = this.createBody(partParent, i == this.getSegmentCount() - 1);
               part.setHead(this);
               part.setParent(partParent);
               part.setBodyIndex(i);
               if (partParent == this) {
                  this.setChildId(part.getUUID());
                  this.entityData.set(CHILD_ID, part.getId());
               }

               if (partParent instanceof LivingMultipartBody body) {
                  body.setChildId(part.getUUID());
               }

               part.setPos(part.repositionParts(backOffset, prevPos, this.getXRot(), prevReqRot, reqRot, false));
               this.level().addFreshEntity(part);
               this.parts[i] = part;
               partParent = part;
               backOffset = part.getBackOffset();
               prevPos = part.position();
            }
         }

         if (this.tickCount > 1) {
            if (this.shouldReplaceParts() && this.getChild() instanceof LivingMultipartBody firstBody) {
               this.parts = new LivingMultipartBody[this.getSegmentCount()];
               this.parts[0] = firstBody;
               this.entityData.set(CHILD_ID, this.parts[0].getId());
               LivingEntity partParent = this.parts[0];
               Vec3 prevPos = this.position();
               float backOffset = this.getBbWidth() / 2.0F;

               for (int i = 1; i < this.getSegmentCount(); i++) {
                  if (this.parts[i - 1].getChild() instanceof LivingMultipartBody body) {
                     this.parts[i] = body;
                     partParent = body;
                     backOffset = body.getBackOffset();
                     prevPos = body.position();
                  } else {
                     if (this.parts[i - 1].isEndSegment()) {
                        this.parts[i - 1].setEndSegment(false);
                     }

                     float prevReqRot = this.calcPartRotation(i) + this.getPartYaw(i);
                     float reqRot = this.calcPartRotation(i + 1) + this.getPartYaw(i);
                     LivingMultipartBody part = this.createBody(partParent, i == this.getSegmentCount() - 1);
                     part.setHead(this);
                     part.setParent(partParent);
                     part.setBodyIndex(i);
                     if (partParent instanceof LivingMultipartBody body) {
                        body.setChildId(part.getUUID());
                     }

                     part.setPos(part.repositionParts(backOffset, prevPos, this.getXRot(), prevReqRot, reqRot, false));
                     this.level().addFreshEntity(part);
                     this.parts[i] = part;
                     partParent = part;
                     backOffset = part.getBackOffset();
                     prevPos = part.position();
                  }
               }
            }

            this.updatePartPosition();
         }
      }
   }

   protected void updateRingBuffer() {
      if (this.ringBufferIndex < 0) {
         Arrays.fill(this.ringBuffer, this.yBodyRot);
      }

      if (this.shouldUpdateRingBuffer() || this.ringBufferIndex < 0) {
         this.ringBufferIndex++;
      }

      if (this.ringBufferIndex == this.ringBuffer.length) {
         this.ringBufferIndex = 0;
      }

      this.ringBuffer[this.ringBufferIndex] = this.getYRot();
   }

   protected void updatePartPosition() {
      if (this.parts != null) {
         Vec3 prev = this.position();
         float xRot = this.getXRot();
         float backOffset = this.getBbWidth() / 2.0F;

         for (int i = 0; i < this.getSegmentCount(); i++) {
            if (this.parts[i] != null) {
               float prevReqRot = this.calcPartRotation(i) + this.getPartYaw(i);
               float reqRot = this.calcPartRotation(i + 1) + this.getPartYaw(i);
               prev = this.parts[i].repositionParts(backOffset, prev, xRot, prevReqRot, reqRot, true);
               xRot = this.parts[i].getXRot();
               backOffset = this.parts[i].getBackOffset();
            }
         }
      }
   }

   protected boolean shouldUpdateRingBuffer() {
      return this.getControllingPassenger() != null ? true : this.getDeltaMovement().lengthSqr() >= 0.005;
   }

   protected float getPartYaw(int i) {
      return this.getRingBuffer(4 + i, 1.0F);
   }

   protected float calcPartRotation(int i) {
      return 0.0F;
   }

   public float getRingBuffer(int bufferOffset, float partialTicks) {
      if (this.isDeadOrDying()) {
         partialTicks = 0.0F;
      }

      partialTicks = 1.0F - partialTicks;
      int i = this.ringBufferIndex - bufferOffset & 63;
      int j = this.ringBufferIndex - bufferOffset - 1 & 63;
      float d0 = this.ringBuffer[i];
      float d1 = this.ringBuffer[j] - d0;
      return Mth.wrapDegrees(d0 + d1 * partialTicks);
   }
}
