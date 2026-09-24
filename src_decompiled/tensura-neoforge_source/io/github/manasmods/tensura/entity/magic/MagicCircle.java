package io.github.manasmods.tensura.entity.magic;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import lombok.Generated;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MagicCircle extends TensuraProjectile implements GeoEntity {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(MagicCircle.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> FOLLOW_VIEW = SynchedEntityData.defineId(MagicCircle.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> FOLLOW_POSITION = SynchedEntityData.defineId(MagicCircle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> LOOK_DISTANCE = SynchedEntityData.defineId(MagicCircle.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> SPINNING = SynchedEntityData.defineId(MagicCircle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> CHARGED_ENERGY = SynchedEntityData.defineId(MagicCircle.class, EntityDataSerializers.FLOAT);
   private Vec3 castOffset = Vec3.ZERO;
   private Vec3 targetedPos = Vec3.ZERO;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private AABB cachedBoundingBox;
   private double cachedBboxX = Double.NaN;
   private double cachedBboxY = Double.NaN;
   private double cachedBboxZ = Double.NaN;
   private Direction cachedBboxDirection;
   private float cachedBboxSize = -1.0F;

   public MagicCircle(EntityType<? extends MagicCircle> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setRot(0.0F, -90.0F);
      this.noPhysics = true;
   }

   public MagicCircle(Level level, LivingEntity entity) {
      this((EntityType<? extends MagicCircle>)MiscEntityTypes.MAGIC_CIRCLE.get(), level);
      this.setOwner(entity);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ID_TYPE_VARIANT, 0);
      builder.define(FOLLOW_VIEW, -1.0F);
      builder.define(FOLLOW_POSITION, false);
      builder.define(LOOK_DISTANCE, 3.0F);
      builder.define(SPINNING, false);
      builder.define(CHARGED_ENERGY, -1.0F);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("Variant", this.getTypeVariant());
      pCompound.putFloat("FollowViewDistance", this.getFollowingViewDistance());
      pCompound.putBoolean("FollowPosition", this.isFollowingPosition());
      pCompound.putFloat("LookDistance", this.getLookDistance());
      pCompound.putBoolean("Spinning", this.isSpinning());
      pCompound.putFloat("ChargedEnergy", this.getChargedEnergy());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.entityData.set(DATA_ID_TYPE_VARIANT, pCompound.getInt("Variant"));
      this.setFollowViewDistance(pCompound.getFloat("FollowViewDistance"));
      this.setFollowPosition(pCompound.getBoolean("FollowPosition"));
      this.setLookDistance(pCompound.getFloat("LookDistance"));
      this.setSpinning(pCompound.getBoolean("Spinning"));
      this.setChargedEnergy(pCompound.getFloat("ChargedEnergy"));
   }

   public MagicCircleVariant getVariant() {
      return MagicCircleVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(MagicCircleVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
   }

   public float getFollowingViewDistance() {
      return (Float)this.getEntityData().get(FOLLOW_VIEW);
   }

   public void setFollowViewDistance(float follow) {
      this.getEntityData().set(FOLLOW_VIEW, follow);
   }

   public boolean isFollowingPosition() {
      return (Boolean)this.getEntityData().get(FOLLOW_POSITION);
   }

   public void setFollowPosition(boolean follow) {
      this.getEntityData().set(FOLLOW_POSITION, follow);
   }

   public float getLookDistance() {
      return (Float)this.entityData.get(LOOK_DISTANCE);
   }

   public void setLookDistance(float i) {
      this.entityData.set(LOOK_DISTANCE, i);
   }

   public boolean isSpinning() {
      return (Boolean)this.getEntityData().get(SPINNING);
   }

   public void setSpinning(boolean spinning) {
      this.getEntityData().set(SPINNING, spinning);
   }

   public float getChargedEnergy() {
      return (Float)this.entityData.get(CHARGED_ENERGY);
   }

   public void setChargedEnergy(float i) {
      this.entityData.set(CHARGED_ENERGY, i);
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return this.getType().getDimensions().scale(this.getSize(), 1.0F);
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

      float xOff = 0.05F;
      float yOff = 0.05F;
      float zOff = 0.05F;
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

   public void refreshDimensions() {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      super.refreshDimensions();
      this.setPos(d0, d1, d2);
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      return false;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         Entity owner = this.getOwner();
         if (owner != null) {
            float distance = this.getFollowingViewDistance();
            if (distance != -1.0F) {
               if (distance == 0.0F) {
                  this.setRot(owner.getYHeadRot(), owner.getXRot());
               }

               if (this.isFollowingPosition() || this.getLookDistance() != 0.0F) {
                  Vec3 ownerEye = owner.getEyePosition();
                  Vec3 ownerLookNorm = owner.getLookAngle().normalize();
                  if (distance != 0.0F) {
                     Vec3 vec3 = this.position().subtract(ownerEye.add(ownerLookNorm.scale(distance)));
                     Vec2 view = ObjectSelectionHelper.getRotFromVector(vec3);
                     this.setRot(view.y, view.x);
                  }

                  if (this.isFollowingPosition()) {
                     this.setPos(ownerEye.add(this.getCastOffset()).add(ownerLookNorm.scale(this.getLookDistance())));
                  }
               } else if (distance != 0.0F && owner instanceof LivingEntity entity) {
                  if (this.targetedPos.equals(Vec3.ZERO)) {
                     Entity target = ObjectSelectionHelper.getTargetingEntity(entity, distance, false, true);
                     if (target != null) {
                        this.targetedPos = this.position().subtract(target.position());
                     } else {
                        BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, Block.COLLIDER, distance);
                        this.targetedPos = this.position().subtract(result.getLocation());
                     }
                  }

                  Vec2 view = ObjectSelectionHelper.getRotFromVector(this.targetedPos);
                  this.setRot(view.y, view.x);
               }
            } else if (this.isFollowingPosition()) {
               this.setPos(owner.position().add(this.getCastOffset()));
            }
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(
               this,
               "loopController",
               10,
               event -> {
                  if (this.getLife() != -1 && this.getLife() - this.getAge() < 20) {
                     return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.magic_circle.end"));
                  } else {
                     return this.isSpinning() && this.getAge() > 10
                        ? event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_circle.spin"))
                        : event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_circle.loop"));
                  }
               }
            ),
            new AnimationController(this, "controller", 0, event -> PlayState.STOP)
               .triggerableAnim("start", RawAnimation.begin().then("animation.magic_circle.start", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static void castMagicCircle(
      String circleID,
      float radius,
      int life,
      Vec3 pos,
      MagicCircleVariant variant,
      boolean spinning,
      LivingEntity owner,
      CompoundTag tag,
      @Nullable ManasSkill skill,
      int mode,
      Pair<Double, Double> cost
   ) {
      Level level = owner.level();
      if (tag.getInt(circleID) == 0) {
         MagicCircle circle = new MagicCircle(level, owner);
         circle.setPos(pos);
         circle.setLife(life);
         circle.setSize(radius);
         circle.setVariant(variant);
         circle.setSpinning(spinning);
         circle.setSkill(SkillUtils.getSkillOrNull(owner, skill));
         circle.setMode(mode);
         circle.setApCost((Double)cost.getFirst());
         circle.setMpCost((Double)cost.getSecond());
         owner.level().addFreshEntity(circle);
         circle.reapplyPosition();
         circle.triggerAnim("controller", "start");
         owner.swing(InteractionHand.MAIN_HAND, true);
         tag.putInt(circleID, circle.getId());
      } else if (owner.level().getEntity(tag.getInt(circleID)) instanceof MagicCircle circle) {
         circle.increaseLife(1);
      } else {
         tag.putInt(circleID, 0);
      }
   }

   public static void castMagicCircle(
      float radius,
      int life,
      MagicCircleVariant variant,
      LivingEntity owner,
      CompoundTag tag,
      float distanceFromCaster,
      Vec3 offset,
      @Nullable ManasSkill skill,
      int mode,
      Pair<Double, Double> cost
   ) {
      castMagicCircle("MagicCircleID", radius, life, variant, false, owner, tag, distanceFromCaster, 0.0F, offset, skill, mode, cost);
   }

   public static void castMagicCircle(
      String circleID,
      float radius,
      int life,
      MagicCircleVariant variant,
      boolean spinning,
      LivingEntity owner,
      CompoundTag tag,
      float distanceFromCaster,
      float targetDistance,
      Vec3 offset,
      @Nullable ManasSkill skill,
      int mode,
      Pair<Double, Double> cost
   ) {
      Level level = owner.level();
      if (tag.getInt(circleID) == 0) {
         MagicCircle circle = new MagicCircle(level, owner);
         circle.setSize(radius);
         circle.setLife(life);
         circle.setVariant(variant);
         if (distanceFromCaster != 0.0F) {
            circle.setFollowViewDistance(targetDistance);
            circle.setLookDistance(distanceFromCaster);
         }

         circle.setSpinning(spinning);
         circle.setFollowPosition(true);
         circle.setCastOffset(offset);
         circle.setPos(owner.getEyePosition().add(offset).add(owner.getLookAngle().normalize().scale(distanceFromCaster)));
         circle.setSkill(SkillUtils.getSkillOrNull(owner, skill));
         circle.setMode(mode);
         circle.setApCost((Double)cost.getFirst());
         circle.setMpCost((Double)cost.getSecond());
         owner.level().addFreshEntity(circle);
         circle.reapplyPosition();
         circle.triggerAnim("controller", "start");
         owner.swing(InteractionHand.MAIN_HAND, true);
         tag.putInt(circleID, circle.getId());
      } else if (owner.level().getEntity(tag.getInt(circleID)) instanceof MagicCircle circle) {
         circle.increaseLife(1);
      } else {
         tag.putInt(circleID, 0);
      }
   }

   public static void castMagicCircle(
      float radius,
      int life,
      Vec3 pos,
      MagicCircleVariant variant,
      LivingEntity owner,
      CompoundTag tag,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      castMagicCircle(radius, life, pos, variant, false, owner, tag, instance, mode, cost);
   }

   public static void castMagicCircle(
      float radius,
      int life,
      Vec3 pos,
      MagicCircleVariant variant,
      boolean spinning,
      LivingEntity owner,
      CompoundTag tag,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      castMagicCircle("MagicCircleID", radius, life, pos, variant, spinning, owner, tag, instance, mode, cost);
   }

   public static void castMagicCircle(
      String circleID,
      float radius,
      int life,
      Vec3 pos,
      MagicCircleVariant variant,
      boolean spinning,
      LivingEntity owner,
      CompoundTag tag,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      Level level = owner.level();
      if (tag.getInt(circleID) == 0) {
         MagicCircle circle = new MagicCircle(level, owner);
         circle.setLife(life);
         circle.setPos(pos);
         circle.setSize(radius);
         circle.setVariant(variant);
         circle.setSpinning(spinning);
         circle.setSkill(instance);
         circle.setMode(mode);
         circle.setApCost((Double)cost.getFirst());
         circle.setMpCost((Double)cost.getSecond());
         level.addFreshEntity(circle);
         circle.reapplyPosition();
         circle.triggerAnim("controller", "start");
         owner.swing(InteractionHand.MAIN_HAND, true);
         tag.putInt(circleID, circle.getId());
      } else if (level.getEntity(tag.getInt(circleID)) instanceof MagicCircle circle) {
         circle.increaseLife(1);
      } else {
         tag.putInt(circleID, 0);
      }
   }

   public static MagicCircle castMagicCircle(
      float radius,
      int life,
      MagicCircleVariant variant,
      LivingEntity owner,
      CompoundTag tag,
      float distanceFromCaster,
      Vec3 offset,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      return castMagicCircle(radius, life, variant, false, owner, tag, distanceFromCaster, 0.0F, offset, instance, mode, cost);
   }

   public static MagicCircle castMagicCircle(
      float radius,
      int life,
      MagicCircleVariant variant,
      boolean spinning,
      LivingEntity owner,
      CompoundTag tag,
      float distanceFromCaster,
      Vec3 offset,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      return castMagicCircle(radius, life, variant, spinning, owner, tag, distanceFromCaster, 0.0F, offset, instance, mode, cost);
   }

   public static MagicCircle castMagicCircle(
      float radius,
      int life,
      MagicCircleVariant variant,
      LivingEntity owner,
      CompoundTag tag,
      float distanceFromCaster,
      float targetDistance,
      Vec3 offset,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      return castMagicCircle(radius, life, variant, false, owner, tag, distanceFromCaster, targetDistance, offset, instance, mode, cost);
   }

   public static MagicCircle castMagicCircle(
      float radius,
      int life,
      MagicCircleVariant variant,
      boolean spinning,
      LivingEntity owner,
      CompoundTag tag,
      float distanceFromCaster,
      float targetDistance,
      Vec3 offset,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      return castMagicCircle("MagicCircleID", radius, life, variant, spinning, owner, tag, distanceFromCaster, targetDistance, offset, instance, mode, cost);
   }

   public static MagicCircle castMagicCircle(
      String circleID,
      float radius,
      int life,
      MagicCircleVariant variant,
      boolean spinning,
      LivingEntity owner,
      CompoundTag tag,
      float distanceFromCaster,
      float targetDistance,
      Vec3 offset,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      Level level = owner.level();
      if (tag.getInt(circleID) == 0) {
         MagicCircle circle = new MagicCircle(level, owner);
         circle.setSize(radius);
         circle.setLife(life);
         circle.setVariant(variant);
         if (distanceFromCaster != 0.0F) {
            circle.setFollowViewDistance(targetDistance);
            circle.setLookDistance(distanceFromCaster);
         }

         circle.setSpinning(spinning);
         circle.setFollowPosition(true);
         circle.setCastOffset(offset);
         circle.setPos(owner.getEyePosition().add(offset).add(owner.getLookAngle().normalize().scale(distanceFromCaster)));
         circle.setSkill(instance);
         circle.setMode(mode);
         circle.setApCost((Double)cost.getFirst());
         circle.setMpCost((Double)cost.getSecond());
         owner.level().addFreshEntity(circle);
         circle.reapplyPosition();
         circle.triggerAnim("controller", "start");
         owner.swing(InteractionHand.MAIN_HAND, true);
         tag.putInt(circleID, circle.getId());
         return circle;
      } else if (owner.level().getEntity(tag.getInt(circleID)) instanceof MagicCircle circle) {
         circle.increaseLife(1);
         return circle;
      } else {
         tag.putInt(circleID, 0);
         return null;
      }
   }

   public static void castMagicCircle(
      float radius, int life, Vec3 pos, MagicCircleVariant variant, LivingEntity owner, @Nullable ManasSkill skill, int mode, Pair<Double, Double> cost
   ) {
      MagicCircle circle = new MagicCircle(owner.level(), owner);
      circle.setFollowViewDistance(-1.0F);
      circle.setSize(radius);
      circle.setLife(life);
      circle.setPos(pos);
      circle.setVariant(variant);
      circle.setSkill(SkillUtils.getSkillOrNull(owner, skill));
      circle.setMode(mode);
      circle.setApCost((Double)cost.getFirst());
      circle.setMpCost((Double)cost.getSecond());
      owner.level().addFreshEntity(circle);
      circle.reapplyPosition();
      circle.triggerAnim("controller", "start");
      owner.swing(InteractionHand.MAIN_HAND, true);
   }

   public static void castMagicCircle(
      float radius,
      int life,
      MagicCircleVariant variant,
      LivingEntity owner,
      float distanceFromCaster,
      Vec3 offset,
      @Nullable ManasSkill skill,
      int mode,
      Pair<Double, Double> cost
   ) {
      castMagicCircle(radius, life, variant, owner, distanceFromCaster, 0.0F, offset, skill, mode, cost);
   }

   public static void castMagicCircle(
      float radius,
      int life,
      MagicCircleVariant variant,
      LivingEntity owner,
      float distanceFromCaster,
      float targetDistance,
      Vec3 offset,
      @Nullable ManasSkill skill,
      int mode,
      Pair<Double, Double> cost
   ) {
      MagicCircle circle = new MagicCircle(owner.level(), owner);
      circle.setSize(radius);
      circle.setLife(life);
      circle.setVariant(variant);
      if (distanceFromCaster != 0.0F) {
         circle.setFollowViewDistance(targetDistance);
         circle.setLookDistance(distanceFromCaster);
      }

      circle.setFollowPosition(true);
      circle.setCastOffset(offset);
      circle.setPos(owner.getEyePosition().add(offset).add(owner.getLookAngle().normalize().scale(distanceFromCaster)));
      circle.setSkill(SkillUtils.getSkillOrNull(owner, skill));
      circle.setMode(mode);
      circle.setApCost((Double)cost.getFirst());
      circle.setMpCost((Double)cost.getSecond());
      owner.level().addFreshEntity(circle);
      circle.reapplyPosition();
      circle.triggerAnim("controller", "start");
      owner.swing(InteractionHand.MAIN_HAND, true);
   }

   public static void castMagicCircle(
      float radius,
      int life,
      MagicCircleVariant variant,
      boolean spinning,
      LivingEntity owner,
      float distanceFromCaster,
      float targetDistance,
      Vec3 offset,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      MagicCircle circle = new MagicCircle(owner.level(), owner);
      circle.setSize(radius);
      circle.setLife(life);
      circle.setVariant(variant);
      if (distanceFromCaster != 0.0F) {
         circle.setFollowViewDistance(targetDistance);
         circle.setLookDistance(distanceFromCaster);
      }

      circle.setSpinning(spinning);
      circle.setFollowPosition(true);
      circle.setCastOffset(offset);
      circle.setPos(owner.getEyePosition().add(offset).add(owner.getLookAngle().normalize().scale(distanceFromCaster)));
      circle.setSkill(instance);
      circle.setApCost((Double)cost.getFirst());
      circle.setMpCost((Double)cost.getSecond());
      circle.setMode(mode);
      owner.level().addFreshEntity(circle);
      circle.reapplyPosition();
      circle.triggerAnim("controller", "start");
      owner.swing(InteractionHand.MAIN_HAND, true);
   }

   public static void castTargetedMagicCircle(
      float radius,
      int life,
      Vec3 target,
      MagicCircleVariant variant,
      boolean spinning,
      LivingEntity owner,
      CompoundTag tag,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      castTargetedMagicCircle("MagicCircleID", radius, life, target, variant, spinning, owner, -1.0F, true, tag, instance, mode, cost);
   }

   public static void castTargetedMagicCircle(
      String circleID,
      float radius,
      int life,
      Vec3 target,
      MagicCircleVariant variant,
      boolean spinning,
      LivingEntity owner,
      float targetDistance,
      boolean followView,
      CompoundTag tag,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      Level level = owner.level();
      if (tag.getInt(circleID) == 0) {
         MagicCircle circle = new MagicCircle(level, owner);
         circle.setPos(target);
         circle.setFollowViewDistance(targetDistance);
         if (!followView) {
            circle.setLookDistance(0.0F);
         }

         circle.setLife(life);
         circle.setSize(radius);
         circle.setVariant(variant);
         circle.setSpinning(spinning);
         circle.setSkill(instance);
         circle.setMode(mode);
         circle.setApCost((Double)cost.getFirst());
         circle.setMpCost((Double)cost.getSecond());
         owner.level().addFreshEntity(circle);
         circle.reapplyPosition();
         circle.triggerAnim("controller", "start");
         owner.swing(InteractionHand.MAIN_HAND, true);
         tag.putInt(circleID, circle.getId());
      } else if (owner.level().getEntity(tag.getInt(circleID)) instanceof MagicCircle circle) {
         circle.increaseLife(1);
         circle.setPos(target);
         circle.hurtMarked = true;
      } else {
         tag.putInt(circleID, 0);
      }
   }

   @Generated
   public Vec3 getCastOffset() {
      return this.castOffset;
   }

   @Generated
   public void setCastOffset(Vec3 castOffset) {
      this.castOffset = castOffset;
   }

   @Generated
   public Vec3 getTargetedPos() {
      return this.targetedPos;
   }

   @Generated
   public void setTargetedPos(Vec3 targetedPos) {
      this.targetedPos = targetedPos;
   }
}
