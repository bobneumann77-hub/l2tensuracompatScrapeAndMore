package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ChaosEaterProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private static final EntityDataAccessor<Boolean> REACHED = SynchedEntityData.defineId(ChaosEaterProjectile.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> START_OFFSET_X = SynchedEntityData.defineId(ChaosEaterProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> START_OFFSET_Y = SynchedEntityData.defineId(ChaosEaterProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> START_OFFSET_Z = SynchedEntityData.defineId(ChaosEaterProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> START_DISTANCE = SynchedEntityData.defineId(ChaosEaterProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> MAX_COUNT = SynchedEntityData.defineId(ChaosEaterProjectile.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> NUMBER = SynchedEntityData.defineId(ChaosEaterProjectile.class, EntityDataSerializers.INT);
   private int capturedTarget = 0;
   @Nullable
   private LivingEntity target;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ChaosEaterProjectile(EntityType<? extends ChaosEaterProjectile> entityType, Level level) {
      super(entityType, level);
      this.setPiercingEntity(true);
      this.setPiercingBlock(true);
      this.setSize(1.5F);
   }

   public ChaosEaterProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends ChaosEaterProjectile>)ProjectileEntityTypes.CHAOS_EATER.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(REACHED, false);
      builder.define(START_OFFSET_X, 0.0F);
      builder.define(START_OFFSET_Y, 0.0F);
      builder.define(START_OFFSET_Z, 0.0F);
      builder.define(START_DISTANCE, 2.0F);
      builder.define(MAX_COUNT, 4);
      builder.define(NUMBER, 0);
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Reached", this.isReached());
      compound.putFloat("xStart", (float)this.getStartOffset().x());
      compound.putFloat("yStart", (float)this.getStartOffset().y());
      compound.putFloat("zStart", (float)this.getStartOffset().z());
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setReached(compound.getBoolean("Reached"));
      this.setStartOffset(compound.getFloat("xStart"), compound.getFloat("yStart"), compound.getFloat("zStart"));
   }

   public boolean isReached() {
      return (Boolean)this.entityData.get(REACHED);
   }

   public void setReached(boolean reached) {
      this.entityData.set(REACHED, reached);
   }

   public void setStartOffset(float x, float y, float z) {
      this.getEntityData().set(START_OFFSET_X, x);
      this.getEntityData().set(START_OFFSET_Y, y);
      this.getEntityData().set(START_OFFSET_Z, z);
   }

   public Vec3 getStartOffset() {
      return new Vec3(
         ((Float)this.getEntityData().get(START_OFFSET_X)).floatValue(),
         ((Float)this.getEntityData().get(START_OFFSET_Y)).floatValue(),
         ((Float)this.getEntityData().get(START_OFFSET_Z)).floatValue()
      );
   }

   public float getStartDistance() {
      return (Float)this.entityData.get(START_DISTANCE);
   }

   public void setStartDistance(float distance) {
      this.entityData.set(START_DISTANCE, distance);
   }

   public int getMaxCount() {
      return (Integer)this.entityData.get(MAX_COUNT);
   }

   public void setMaxCount(int count) {
      this.entityData.set(MAX_COUNT, count);
   }

   public int getCount() {
      return (Integer)this.entityData.get(NUMBER);
   }

   public void setCount(int count) {
      this.entityData.set(NUMBER, count);
   }

   public void setUpStartPos(int maxCount, int count, float startDistance) {
      this.setMaxCount(maxCount);
      this.setCount(count);
      this.setStartDistance(startDistance);
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.target == null && this.getOwner() instanceof Mob mob ? mob.getTarget() : this.target;
   }

   public void setTarget(@Nullable LivingEntity pTarget) {
      this.target = pTarget;
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   public void positionRider(@NotNull Entity entity, MoveFunction moveFunction) {
      if (this.hasPassenger(entity)) {
         entity.resetFallDistance();
         entity.setPos(this.getX(), this.getY() - entity.getBbHeight() / 2.0F, this.getZ());
      }
   }

   @Override
   public ResourceLocation[] getTextureLocation() {
      return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("tensura", "textures/blank_texture.png")};
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         TensuraParticleHelper.addServerParticlesAroundSelf(this, TensuraParticleUtils.getChaosEaterAura(1.0F, 4.0F, -0.3F));
         Entity owner = this.getOwner();
         if (owner == null) {
            if (this.getTarget() == null) {
               this.discard();
            }
         } else {
            double distance = Math.sqrt(this.distanceToSqr(owner.getEyePosition()));
            if (!this.getPassengers().isEmpty()) {
               if (this.capturedTarget > 0) {
                  this.capturedTarget--;
                  if (this.capturedTarget <= 0) {
                     this.ejectPassengers();
                  }
               } else if (distance <= 2.0) {
                  this.ejectPassengers();
               }
            }

            LivingEntity target = this.getTarget();
            if (target != null && !target.isAlive()) {
               this.setTarget(null);
               target = null;
            }

            this.updateStartPos(owner);
            if (target != null && !this.isReached()) {
               double f = this.distanceTo(target);
               double d0 = (target.getX() - this.getX()) / f;
               double d1 = (target.getY() + target.getBbHeight() / 2.0F - this.getY()) / f;
               double d2 = (target.getZ() - this.getZ()) / f;
               this.setDeltaMovement(new Vec3(d0, d1, d2).scale(this.getSpeed()));
               this.lookAt(Anchor.EYES, target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0));
               if (distance >= 50.0) {
                  this.setReached(true);
               }
            } else if (!this.isReached() && owner instanceof Player player) {
               Vec3 result = ObjectSelectionHelper.getPlayerPOVHitResult(this.level(), player, Fluid.NONE, 20.0).getLocation();
               double f = Math.sqrt(this.distanceToSqr(result));
               double d0 = (result.x - this.getX()) / f;
               double d1 = (result.y - this.getY()) / f;
               double d2 = (result.z - this.getZ()) / f;
               this.setDeltaMovement(new Vec3(d0, d1, d2).scale(this.getSpeed()));
               this.lookAt(Anchor.EYES, result);
               if (distance >= 20.0 || f <= 0.5) {
                  this.setReached(true);
               }
            } else {
               float multiplier = this.getPassengers().isEmpty() ? 1.0F : 0.4F;
               double d0 = (owner.getX() - this.getX()) / distance * multiplier;
               double d1 = (owner.getEyeY() - this.getY()) / distance * multiplier;
               double d2 = (owner.getZ() - this.getZ()) / distance * multiplier;
               Vec3 vec3 = new Vec3(d0, d1, d2);
               this.setDeltaMovement(vec3.scale(this.getSpeed()));
               if (distance <= 4.0) {
                  this.discard();
               }

               for (ItemEntity item : owner.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(2.0))) {
                  item.setPos(this.position());
               }
            }
         }
      }
   }

   protected void updateStartPos(Entity owner) {
      int rot = 360 / this.getMaxCount();

      for (int i = 0; i < this.getMaxCount(); i++) {
         Vec3 offset = new Vec3(0.0, this.getStartDistance(), 0.0)
            .zRot((rot * i - rot / 2.0F) * (float) (Math.PI / 180.0))
            .xRot(-owner.getXRot() * (float) (Math.PI / 180.0))
            .yRot(-owner.getYRot() * (float) (Math.PI / 180.0));
         if (this.getCount() == i) {
            this.setStartOffset((float)offset.x, (float)offset.y, (float)offset.z);
         }
      }

      TensuraParticleHelper.addServerParticlesAroundPos(
         this.random, this.level(), owner.getEyePosition().add(this.getStartOffset()), TensuraParticleUtils.getChaosEaterAura(1.0F, 4.0F, -0.3F), 0.5
      );
   }

   @Override
   protected boolean hitEntity(Entity entity, ProjectileHitResult result) {
      entity.setRemainingFireTicks(Math.max(this.getBurnTicks(), 0));
      this.knockBack(entity);
      if (this.getMobEffect() != null && entity instanceof LivingEntity living && !this.isAlly(living)) {
         ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
         TensuraMobEffect.addEffect(living, new MobEffectInstance(this.getMobEffect()), this.getOwner(), skill, this.getMode());
      }

      return this.dealDamage(entity);
   }

   @Override
   protected boolean dealDamage(Entity target) {
      if (target.level().isClientSide()) {
         return false;
      }

      if (this.isReached() && target == this.getOwner()) {
         this.discard();
      }

      if (this.getTarget() == null || this.getTarget() == target) {
         this.setReached(true);
      }

      if (this.damage <= 0.0F) {
         return false;
      } else if (target instanceof LivingEntity living && this.isAlly(living)) {
         return false;
      } else {
         if (target.getVehicle() == this) {
            return false;
         }

         if (target.getType().is(TensuraEntityTags.FULL_GRAVITY_CONTROL)) {
            return false;
         }

         DamageSource damagesource = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.CORROSION, this.getOwner())
            .tensura$setAbilityInstance(this.getSkill())
            .tensura$setAbilityMode(this.getMode())
            .tensura$setMagiculeCost(this.getMpCost());
         if (target.hurt(damagesource, this.getDamage())) {
            if (this.capturedTarget <= 0 && !(target.getVehicle() instanceof ChaosEaterProjectile)) {
               target.startRiding(this, true);
               this.capturedTarget = 40;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public void applyEffectAround(double inflateRadius) {
      if (this.getMobEffect() != null) {
         List<LivingEntity> livingEntityList = this.level()
            .getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(inflateRadius), entityx -> !this.isAlly(entityx));
         if (!livingEntityList.isEmpty()) {
            ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;

            for (LivingEntity entity : livingEntityList) {
               if (this.getOwner() instanceof LivingEntity player) {
                  entity.setLastHurtByMob(player);
               }

               TensuraMobEffect.addEffect(entity, new MobEffectInstance(this.getMobEffect()), this.getOwner(), skill, this.getMode());
            }
         }
      }
   }

   public boolean isAlly(LivingEntity entity) {
      return entity == this.getOwner() ? true : this.getOwner() != null && this.getOwner().isAlliedTo(entity);
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)SoundEvents.GENERIC_EXPLODE.value());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), TensuraParticleUtils.getChaosEaterAura(1.0F, 4.0F, -0.3F), x, y, z, 10, 0.5, 0.5, 0.5, 0.1, false
      );
   }

   @Override
   public void flyingParticles() {
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
