package io.github.manasmods.tensura.entity.magic.field;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class AreaField extends TensuraProjectile {
   private static final EntityDataAccessor<Integer> TICK_EACH_HIT = SynchedEntityData.defineId(AreaField.class, EntityDataSerializers.INT);
   @Nullable
   private Entity target;

   public AreaField(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noPhysics = true;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(TICK_EACH_HIT, 20);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag pCompound) {
      this.setTickEachHit(pCompound.getInt("TickEachHit"));
      super.readAdditionalSaveData(pCompound);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag pCompound) {
      pCompound.putInt("TickEachHit", this.getTickEachHit());
      super.addAdditionalSaveData(pCompound);
   }

   public int getTickEachHit() {
      return Math.max(1, (Integer)this.getEntityData().get(TICK_EACH_HIT));
   }

   public void setTickEachHit(int tick) {
      this.getEntityData().set(TICK_EACH_HIT, tick);
   }

   @Override
   public void setSize(float size) {
      this.getEntityData().set(SIZE, Mth.clamp(size, 0.0F, 50.0F));
   }

   @Nullable
   public Entity getTarget() {
      return this.target;
   }

   public void setTarget(@Nullable Entity pTarget) {
      this.target = pTarget;
   }

   public boolean isInstant() {
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

   public void refreshDimensions() {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      super.refreshDimensions();
      this.setPos(d0, d1, d2);
   }

   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return EntityDimensions.scalable(this.getSize() * 2.0F, this.getSize() * 2.0F);
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      return pTarget != this && pTarget.isAlive() && !(pTarget instanceof LivingEntity entity && entity.hasInfiniteMaterials())
         ? this.getTarget() == null || pTarget == this.getTarget()
         : false;
   }

   @Override
   public DamageSource getDamageSource(ResourceKey<DamageType> type, float costMultiplier) {
      return super.getDamageSource(type, costMultiplier).tensura$setDodgeBypass();
   }

   @Override
   public void tick() {
      super.tick();
      this.updateVisualSize();
      this.applyEntityHit();
   }

   public void applyEntityHit() {
      if (this.getAge() == this.getTickEachHit() && this.isInstant() && !this.level().isClientSide()) {
         this.hitTarget(true);
      }

      if (this.level().isClientSide()) {
         this.spawnAmbientParticles();
      } else if (!this.isInstant() && this.getAge() == 1 || this.getTickEachHit() != 0 && this.getAge() % this.getTickEachHit() == 0) {
         this.hitTarget(false);
      }
   }

   protected void updateVisualSize() {
      if (this.getVisualSize() != this.getSize()) {
         this.setVisualSize(Math.min(this.getVisualSize() + 0.25F, this.getSize()));
      }
   }

   protected void hitTarget(boolean instant) {
      if (!this.level().isClientSide()) {
         for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (this.canHitEntity(target)) {
               this.applyEffect(target, instant);
            }
         }
      }
   }

   public void applyEffect(LivingEntity target, boolean instant) {
      this.hitEntity(target, ProjectileHitResult.DEFAULT);
   }

   public void spawnAmbientParticles() {
   }

   @Nullable
   public static AreaField getLastingField(
      EntityType<? extends AreaField> entityType,
      float damage,
      float radius,
      int life,
      int hitInterval,
      @Nullable MobEffectInstance effect,
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
      if (tag.getInt("FieldID") == 0 && !EnergyHelper.isOutOfEnergy(owner, (Double)cost.getFirst(), (Double)cost.getSecond())) {
         AreaField barrier = (AreaField)entityType.create(level);
         if (barrier == null) {
            return null;
         }

         barrier.setOwner(owner);
         barrier.setDamage(damage);
         barrier.setMobEffect(effect);
         barrier.setSize(radius);
         barrier.setLife(life);
         barrier.setTickEachHit(hitInterval);
         barrier.setSkill(instance);
         barrier.setMode(mode);
         barrier.setApCost((Double)cost.getFirst());
         barrier.setMpCost((Double)cost.getSecond());
         barrier.setPos(pos);
         owner.level().addFreshEntity(barrier);
         owner.swing(InteractionHand.MAIN_HAND, true);
         tag.putInt("FieldID", barrier.getId());
         instance.markDirty();
         return barrier;
      } else if (!(owner.level().getEntity(tag.getInt("FieldID")) instanceof AreaField barrier)) {
         tag.putInt("FieldID", 0);
         instance.markDirty();
         return null;
      } else {
         if (heldTicks % 20 != 0 || !EnergyHelper.isOutOfEnergy(owner, (Double)increaseCost.getFirst(), (Double)increaseCost.getSecond())) {
            barrier.increaseLife(1);
         }

         return barrier;
      }
   }
}
