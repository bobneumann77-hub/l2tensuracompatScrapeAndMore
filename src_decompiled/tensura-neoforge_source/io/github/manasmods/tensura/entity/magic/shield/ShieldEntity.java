package io.github.manasmods.tensura.entity.magic.shield;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import lombok.Generated;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ShieldEntity extends TensuraProjectile implements GeoEntity {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(ShieldEntity.class);
   private static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(ShieldEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DISTANCE_FROM_OWNER = SynchedEntityData.defineId(ShieldEntity.class, EntityDataSerializers.FLOAT);
   protected int contactInterval = 20;
   protected float contactRange = 0.1F;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private AABB cachedBoundingBox;
   private double cachedBboxX = Double.NaN;
   private double cachedBboxY = Double.NaN;
   private double cachedBboxZ = Double.NaN;
   private Direction cachedBboxDirection;
   private float cachedBboxSize = -1.0F;

   public ShieldEntity(EntityType<? extends ShieldEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(HEALTH, 20.0F);
      builder.define(DISTANCE_FROM_OWNER, 3.0F);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putFloat("Health", this.getHealth());
      compound.putFloat("DistanceFromOwner", this.getDistanceFromOwner());
      compound.putFloat("ContactRange", this.getContactRange());
      compound.putInt("ContactInterval", this.getContactInterval());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setHealth(compound.getFloat("Health"));
      this.setDistanceFromOwner(compound.getFloat("DistanceFromOwner"));
      this.setContactRange(compound.getFloat("ContactRange"));
      this.setContactInterval(compound.getInt("ContactInterval"));
   }

   public void setHealth(float pDamageTaken) {
      this.entityData.set(HEALTH, pDamageTaken);
   }

   public float getHealth() {
      return (Float)this.entityData.get(HEALTH);
   }

   public void setDistanceFromOwner(float pDamageTaken) {
      this.entityData.set(DISTANCE_FROM_OWNER, pDamageTaken);
   }

   public float getDistanceFromOwner() {
      return (Float)this.entityData.get(DISTANCE_FROM_OWNER);
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return this.getType().getDimensions();
   }

   @NotNull
   protected AABB makeBoundingBox() {
      Direction direction = this.getNearestViewDirection();
      float size = this.getSize() / 2.0F;
      if (this.cachedBoundingBox != null
         && direction == this.cachedBboxDirection
         && size == this.cachedBboxSize
         && this.cachedBboxX == this.getX()
         && this.cachedBboxY == this.getY()
         && this.cachedBboxZ == this.getZ()) {
         return this.cachedBoundingBox;
      }

      float xOff = 0.02F;
      float yOff = 0.02F;
      float zOff = 0.02F;
      switch (direction) {
         case NORTH:
         case SOUTH:
            xOff = size;
            yOff = size;
            break;
         case EAST:
         case WEST:
            zOff = size;
            yOff = size;
            break;
         case UP:
         case DOWN:
            xOff = size;
            zOff = size;
      }

      AABB result = new AABB(this.getX() - xOff, this.getY() - yOff, this.getZ() - zOff, this.getX() + xOff, this.getY() + yOff, this.getZ() + zOff);
      this.cachedBoundingBox = result;
      this.cachedBboxX = this.getX();
      this.cachedBboxY = this.getY();
      this.cachedBboxZ = this.getZ();
      this.cachedBboxDirection = direction;
      this.cachedBboxSize = size;
      return result;
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      return false;
   }

   public boolean shouldPush() {
      return true;
   }

   public boolean canBeCollidedWith() {
      return !this.canWalkThrough();
   }

   public boolean canCollideWith(@NotNull Entity entity) {
      return this.canWalkThrough(entity) ? false : (entity.canBeCollidedWith() || entity.isPushable()) && !this.isPassengerOfSameVehicle(entity);
   }

   public boolean canWalkThrough() {
      return false;
   }

   public boolean canWalkThrough(Entity entity) {
      Entity owner = this.getOwner();
      return owner != null && owner.isShiftKeyDown() ? entity.isAlliedTo(owner) || entity == owner : false;
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (!this.level().isClientSide() && !this.isRemoved()) {
         if (this.getHealth() <= 0.0F) {
            return false;
         }

         this.setHealth(this.getHealth() - pAmount);
         this.markHurt(pSource);
         this.gameEvent(GameEvent.ENTITY_DAMAGE, pSource.getEntity());
         if (this.getHealth() <= 0.0F) {
            this.setRemoveIn(20);
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), this.getSoundSource(), 2.0F, 1.0F);
         } else {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SHIELD_BLOCK, this.getSoundSource(), 2.0F, 1.0F);
         }
      }

      return true;
   }

   protected void markHurt(DamageSource pSource) {
      this.hurtMarked = true;
      if (this.getLife() - this.getAge() >= 20) {
         Vec3 position = pSource.getSourcePosition();
         if (position == null) {
            this.triggerAnim("controller", "hit");
         } else {
            Vec3 toSource = position.subtract(this.position()).normalize();
            if (this.getLookAngle().normalize().dot(toSource) < 0.0) {
               this.triggerAnim("controller", "hit");
            } else {
               this.triggerAnim("controller", "hit_behind");
            }
         }
      }
   }

   public void setRot(float f, float g) {
      super.setRot(f, g);
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.getLife() - this.getAge() >= 20) {
            float contactDamage = this.getDamage();
            int contactInterval = this.getContactInterval();
            if (contactInterval == 0 || contactDamage > 0.0F && this.getAge() % contactInterval == 0) {
               AABB box = this.getBoundingBox().inflate(this.getContactRange());

               for (Entity target : this.level().getEntities(this, box, this::canHitEntity)) {
                  this.applyEffect(target);
               }
            }

            Entity owner = this.getOwner();
            if (owner != null) {
               if (this.shouldPush()) {
                  AABB box = this.getBoundingBox().move(owner.getLookAngle().scale(0.25));

                  for (Entity target : this.level().getEntities(this, box, this::canCollideWith)) {
                     target.move(MoverType.SHULKER, owner.getLookAngle().scale(0.5));
                  }
               }

               float distance = this.getDistanceFromOwner();
               if (distance != 0.0F) {
                  Vec2 view = ObjectSelectionHelper.getRotFromVector(owner.getLookAngle());
                  this.setRot(view.y, view.x);
                  this.setPos(owner.getEyePosition().add(owner.getLookAngle().normalize().scale(distance)));
               }
            }
         }
      }
   }

   public boolean applyEffect(Entity target) {
      return this.hitEntity(target, ProjectileHitResult.DEFAULT);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(
               this,
               "loopController",
               0,
               event -> {
                  if (this.getAge() < 20) {
                     return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.magic_shield.start"));
                  } else if (this.getLife() - this.getAge() < 20) {
                     return this.getHealth() <= 0.0F
                        ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.magic_shield.destroy"))
                        : event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.magic_shield.end"));
                  } else {
                     return event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_shield.loop"));
                  }
               }
            ),
            new AnimationController(this, "controller", 0, event -> PlayState.STOP)
               .triggerableAnim("hit", RawAnimation.begin().then("animation.magic_shield.hit", LoopType.PLAY_ONCE))
               .triggerableAnim("hit_behind", RawAnimation.begin().then("animation.magic_shield.hit_behind", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static ShieldEntity createShield(
      EntityType<? extends ShieldEntity> entityType,
      LivingEntity owner,
      ManasSkillInstance instance,
      TensuraSkill skill,
      int mode,
      int duration,
      float distance,
      float health,
      float size
   ) {
      Pair<Double, Double> cost = new Pair(skill.getAuraCost(owner, instance, mode), skill.getMagiculeCost(owner, instance, mode));
      return createShield(entityType, owner, instance, mode, duration, distance, health, size, cost);
   }

   public static ShieldEntity createShield(
      EntityType<? extends ShieldEntity> entityType,
      LivingEntity owner,
      ManasSkillInstance instance,
      int mode,
      int duration,
      float distance,
      float health,
      float size,
      Pair<Double, Double> cost
   ) {
      CompoundTag tag = instance.getOrCreateTag();
      Level level = owner.level();
      if (tag.getInt("ShieldEntity") == 0 && duration > 0) {
         ShieldEntity shield = (ShieldEntity)entityType.create(level);
         if (shield == null) {
            return null;
         }

         shield.setLife(duration);
         shield.setDistanceFromOwner(distance);
         shield.setSize(size);
         shield.setHealth(health);
         shield.setOwner(owner);
         shield.setPos(owner.getEyePosition().add(owner.getLookAngle().normalize().scale(distance)));
         shield.setApCost((Double)cost.getFirst());
         shield.setMpCost((Double)cost.getSecond());
         shield.setSkill(instance);
         shield.setMode(mode);
         owner.level().addFreshEntity(shield);
         shield.reapplyPosition();
         tag.putInt("ShieldEntity", shield.getId());
         instance.markDirty();
         return shield;
      } else if (owner.level().getEntity(tag.getInt("ShieldEntity")) instanceof ShieldEntity shield) {
         if (shield.getLife() - shield.getAge() >= 20) {
            shield.increaseLife(1);
         }

         return shield;
      } else {
         tag.putInt("ShieldEntity", 0);
         instance.markDirty();
         return null;
      }
   }

   @Generated
   public int getContactInterval() {
      return this.contactInterval;
   }

   @Generated
   public void setContactInterval(int contactInterval) {
      this.contactInterval = contactInterval;
   }

   @Generated
   public float getContactRange() {
      return this.contactRange;
   }

   @Generated
   public void setContactRange(float contactRange) {
      this.contactRange = contactRange;
   }
}
