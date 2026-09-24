package io.github.manasmods.tensura.entity.magic.barrier;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.entity.template.TensuraPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.IMultipart;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import lombok.Generated;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BarrierEntity extends TensuraProjectile implements IMultipart {
   private static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(BarrierEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> ADDITIONAL_HEIGHT = SynchedEntityData.defineId(BarrierEntity.class, EntityDataSerializers.FLOAT);
   public BarrierPart[] parts = new BarrierPart[]{
      new BarrierPart(this, "Up", Direction.UP),
      new BarrierPart(this, "Down", Direction.DOWN),
      new BarrierPart(this, "North", Direction.NORTH),
      new BarrierPart(this, "East", Direction.EAST),
      new BarrierPart(this, "South", Direction.SOUTH),
      new BarrierPart(this, "West", Direction.WEST)
   };
   protected int tickEachHit = 20;
   protected boolean followOwner = false;

   public BarrierEntity(EntityType<? extends BarrierEntity> entityType, Level level, LivingEntity entity) {
      this(entityType, level);
      this.setOwner(entity);
   }

   public BarrierEntity(EntityType<? extends BarrierEntity> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
      this.blocksBuilding = false;
      this.noCulling = true;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(HEALTH, 20.0F);
      builder.define(ADDITIONAL_HEIGHT, 0.0F);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("Health", this.getHealth());
      pCompound.putFloat("AdditionalHeight", this.getHeight());
      pCompound.putInt("TickEachHit", this.getTickEachHit());
      pCompound.putBoolean("FollowOwner", this.isFollowOwner());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setHealth(pCompound.getFloat("Health"));
      this.setHeight(pCompound.getFloat("AdditionalHeight"));
      this.setTickEachHit(pCompound.getInt("TickEachHit"));
      this.setFollowOwner(pCompound.getBoolean("FollowOwner"));
   }

   public void setHealth(float pDamageTaken) {
      this.entityData.set(HEALTH, pDamageTaken);
   }

   public float getHealth() {
      return (Float)this.entityData.get(HEALTH);
   }

   @Override
   public void setSize(float size) {
      this.getEntityData().set(SIZE, size);
      this.setPos(this.position().add(0.0, -size, 0.0));
   }

   public float getHeight() {
      return (Float)this.entityData.get(ADDITIONAL_HEIGHT);
   }

   public void setHeight(float height) {
      this.entityData.set(ADDITIONAL_HEIGHT, height);
   }

   @Override
   public TensuraPartEntity[] getParts() {
      return this.parts;
   }

   public boolean canWalkThrough() {
      return false;
   }

   public boolean canWalkThrough(Entity entity) {
      Entity owner = this.getOwner();
      return owner != null && owner.isShiftKeyDown() ? entity.isAlliedTo(owner) || entity == owner : false;
   }

   public boolean shouldPush() {
      return false;
   }

   public boolean blockBuilding() {
      return true;
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public boolean canHitEntity(Entity pTarget) {
      if (!pTarget.isAlive() || !pTarget.isPickable()) {
         return false;
      } else {
         return pTarget.isSpectator() ? false : !(pTarget instanceof Player player && player.isCreative());
      }
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return pDistance < 1024.0 || super.shouldRenderAtSqrDistance(pDistance);
   }

   protected AABB getAffectedArea() {
      return this.getBoundingBox();
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return EntityDimensions.scalable(this.getSize() * 2.0F, this.getSize() * 2.0F + this.getHeight());
   }

   public List<LivingEntity> getAffectedEntities() {
      return this.level().getEntitiesOfClass(LivingEntity.class, this.getAffectedArea());
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (!this.level().isClientSide() && !this.isRemoved()) {
         this.setHealth(this.getHealth() - pAmount);
         this.markHurt();
         this.gameEvent(GameEvent.ENTITY_DAMAGE, pSource.getEntity());
         if (this.getHealth() <= 0.0F) {
            this.discard();
         }
      }

      return true;
   }

   public int getDelayHitTime() {
      return 0;
   }

   @Override
   public DamageSource getDamageSource(ResourceKey<DamageType> type, float costMultiplier) {
      return super.getDamageSource(type, costMultiplier).tensura$setDodgeBypass();
   }

   protected void updateVisualSize() {
      if (this.getLife() - this.getAge() < 20) {
         this.setVisualSize(Math.max(this.getVisualSize() - this.getSize() / 20.0F, 0.0F));
      } else if (this.getVisualSize() != this.getSize()) {
         this.setVisualSize(Math.min(this.getVisualSize() + 0.5F, this.getSize()));
      }
   }

   @Override
   public void tick() {
      super.tick();
      this.updateVisualSize();
      if (this.shouldCreateParts()) {
         for (BarrierPart part : this.parts) {
            part.setPositionOnDirection();
         }
      }

      if (this.level().isClientSide()) {
         this.spawnParticle();
      } else {
         if (this.isFollowOwner()) {
            this.applyFollowOwner();
         }

         if (this.getAge() >= this.getDelayHitTime() && this.getTickEachHit() != 0 && this.getAge() % this.getTickEachHit() == 0) {
            this.hitTarget();
         }

         if (this.getLife() >= 0 && this.getAge() >= this.getLife()) {
            this.remove();
         }
      }
   }

   public void applyFollowOwner() {
      Entity owner = this.getOwner();
      if (owner != null) {
         this.setPos(owner.getX(), owner.getY() + owner.getBbHeight() / 2.0F - this.getSize(), owner.getZ());
      }
   }

   protected void hitTarget() {
      for (LivingEntity target : this.getAffectedEntities()) {
         if (this.canHitEntity(target)) {
            this.applyEffect(target);
         }
      }
   }

   public void applyEffect(LivingEntity target) {
      this.hitEntity(target, ProjectileHitResult.DEFAULT);
   }

   public void spawnParticle() {
   }

   public static void spawnLastingBarrier(
      EntityType<? extends BarrierEntity> entityType,
      float damage,
      float radius,
      float height,
      int life,
      float health,
      Vec3 pos,
      LivingEntity owner,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost,
      Pair<Double, Double> increaseCost,
      int heldTicks
   ) {
      if (instance != null) {
         CompoundTag tag = instance.getOrCreateTag();
         Level level = owner.level();
         if (tag.getInt("BarrierID") == 0 && !EnergyHelper.isOutOfEnergy(owner, (Double)cost.getFirst(), (Double)cost.getSecond())) {
            BarrierEntity barrier = (BarrierEntity)entityType.create(level);
            if (barrier == null) {
               return;
            }

            barrier.setOwner(owner);
            barrier.setDamage(damage);
            barrier.setSize(radius);
            barrier.setHeight(height);
            barrier.setLife(life);
            barrier.setHealth(health);
            barrier.setPos(pos);
            barrier.setSkill(instance);
            barrier.setMode(mode);
            barrier.setApCost((Double)cost.getFirst());
            barrier.setMpCost((Double)cost.getSecond());
            owner.level().addFreshEntity(barrier);
            owner.swing(InteractionHand.MAIN_HAND, true);
            tag.putInt("BarrierID", barrier.getId());
         } else if (owner.level().getEntity(tag.getInt("BarrierID")) instanceof BarrierEntity barrier) {
            if (heldTicks % 20 != 0 || !EnergyHelper.isOutOfEnergy(owner, (Double)increaseCost.getFirst(), (Double)increaseCost.getSecond())) {
               barrier.increaseLife(1);
            }
         } else {
            tag.putInt("BarrierID", 0);
         }

         instance.markDirty();
      }
   }

   @Nullable
   public static BarrierEntity getLastingBarrier(
      EntityType<? extends BarrierEntity> entityType,
      float damage,
      float radius,
      float height,
      int life,
      float health,
      Vec3 pos,
      LivingEntity owner,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost,
      Pair<Double, Double> increaseCost,
      int heldTicks
   ) {
      if (instance == null) {
         return null;
      }

      CompoundTag tag = instance.getOrCreateTag();
      Level level = owner.level();
      if (tag.getInt("BarrierID") == 0 && !EnergyHelper.isOutOfEnergy(owner, (Double)cost.getFirst(), (Double)cost.getSecond())) {
         BarrierEntity barrier = (BarrierEntity)entityType.create(level);
         if (barrier == null) {
            return null;
         }

         barrier.setOwner(owner);
         barrier.setDamage(damage);
         barrier.setSize(radius);
         barrier.setHeight(height);
         barrier.setLife(life);
         barrier.setHealth(health);
         barrier.setPos(pos);
         barrier.setSkill(instance);
         barrier.setMode(mode);
         barrier.setApCost((Double)cost.getFirst());
         barrier.setMpCost((Double)cost.getSecond());
         owner.level().addFreshEntity(barrier);
         owner.swing(InteractionHand.MAIN_HAND, true);
         tag.putInt("BarrierID", barrier.getId());
         instance.markDirty();
         return barrier;
      } else if (!(owner.level().getEntity(tag.getInt("BarrierID")) instanceof BarrierEntity barrier)) {
         tag.putInt("BarrierID", 0);
         instance.markDirty();
         return null;
      } else {
         if (heldTicks % 20 != 0 || !EnergyHelper.isOutOfEnergy(owner, (Double)increaseCost.getFirst(), (Double)increaseCost.getSecond())) {
            barrier.increaseLife(1);
         }

         return barrier;
      }
   }

   @Generated
   public int getTickEachHit() {
      return this.tickEachHit;
   }

   @Generated
   public void setTickEachHit(int tickEachHit) {
      this.tickEachHit = tickEachHit;
   }

   @Generated
   public boolean isFollowOwner() {
      return this.followOwner;
   }

   @Generated
   public void setFollowOwner(boolean followOwner) {
      this.followOwner = followOwner;
   }
}
