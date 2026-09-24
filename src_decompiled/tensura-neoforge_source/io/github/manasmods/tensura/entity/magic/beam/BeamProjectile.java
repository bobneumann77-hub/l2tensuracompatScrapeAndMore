package io.github.manasmods.tensura.entity.magic.beam;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeamProjectile extends TensuraProjectile {
   private static final EntityDataAccessor<Boolean> FOLLOW_OWNER = SynchedEntityData.defineId(BeamProjectile.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(BeamProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(BeamProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> RANGE = SynchedEntityData.defineId(BeamProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<BlockPos> TARGET_POS = SynchedEntityData.defineId(BeamProjectile.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<Float> COLLIDE_X = SynchedEntityData.defineId(BeamProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> COLLIDE_Y = SynchedEntityData.defineId(BeamProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> COLLIDE_Z = SynchedEntityData.defineId(BeamProjectile.class, EntityDataSerializers.FLOAT);
   protected float explosionRadius = 0.0F;
   public double endPosX;
   public double endPosY;
   public double endPosZ;
   public double prevCollidePosX;
   public double prevCollidePosY;
   public double prevCollidePosZ;
   @Nullable
   public Direction side = null;
   public final Map<Color, Float> beamColorAndSize = Maps.newHashMap();

   protected BeamProjectile(EntityType<? extends BeamProjectile> pType, Level pLevel) {
      super(pType, pLevel);
      this.noCulling = true;
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FOLLOW_OWNER, false);
      builder.define(YAW, 0.0F);
      builder.define(PITCH, 0.0F);
      builder.define(RANGE, 30.0F);
      builder.define(TARGET_POS, BlockPos.ZERO);
      builder.define(COLLIDE_X, 0.0F);
      builder.define(COLLIDE_Y, 0.0F);
      builder.define(COLLIDE_Z, 0.0F);
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("FollowOwner", this.isFollowingOwner());
      compound.putFloat("Range", this.getRange());
      compound.putFloat("Yaw", this.getYaw());
      compound.putFloat("Pitch", this.getPitch());
      compound.putDouble("TargetX", this.getTargetPos().getX());
      compound.putDouble("TargetY", this.getTargetPos().getY());
      compound.putDouble("TargetZ", this.getTargetPos().getZ());
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFollowingOwner(compound.getBoolean("FollowOwner"));
      this.setRange(compound.getFloat("Range"));
      this.setYaw(compound.getFloat("Yaw"));
      this.setPitch(compound.getFloat("Pitch"));
      this.setTargetPos(compound.getDouble("TargetX"), compound.getDouble("TargetY"), compound.getDouble("TargetZ"));
      this.setPitch(compound.getFloat("Pitch"));
   }

   public boolean isFollowingOwner() {
      return (Boolean)this.entityData.get(FOLLOW_OWNER);
   }

   public void setFollowingOwner(boolean follow) {
      this.entityData.set(FOLLOW_OWNER, follow);
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

   public float getRange() {
      return (Float)this.entityData.get(RANGE);
   }

   public void setRange(float range) {
      this.entityData.set(RANGE, range);
   }

   protected BlockPos getTargetPos() {
      return (BlockPos)this.entityData.get(TARGET_POS);
   }

   public void setTargetPos(double x, double y, double z) {
      this.entityData.set(TARGET_POS, new BlockPos((int)x, (int)y, (int)z));
   }

   public float getCollideX() {
      return (Float)this.entityData.get(COLLIDE_X);
   }

   public float getCollideY() {
      return (Float)this.entityData.get(COLLIDE_Y);
   }

   public float getCollideZ() {
      return (Float)this.entityData.get(COLLIDE_Z);
   }

   public void setCollidePos(float x, float y, float z) {
      this.entityData.set(COLLIDE_X, x);
      this.entityData.set(COLLIDE_Y, y);
      this.entityData.set(COLLIDE_Z, z);
   }

   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return this.getType().getDimensions();
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 1024.0;
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   public boolean piercingBlock() {
      return false;
   }

   protected ExplosionInteraction getExplosionInteraction() {
      return this.shouldGrief() ? ExplosionInteraction.BLOCK : ExplosionInteraction.NONE;
   }

   public ResourceLocation[] getTextureLocation() {
      return null;
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      if (pTarget == this) {
         return false;
      } else {
         return pTarget == this.getOwner()
            ? false
            : this.getOwner() == null || !this.getOwner().isPassengerOfSameVehicle(pTarget) && !this.getOwner().isAlliedTo(pTarget);
      }
   }

   protected boolean shouldStopFollowOwner() {
      return this.getLife() - this.getAge() < 20;
   }

   public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
      super.recreateFromPacket(clientboundAddEntityPacket);
      this.updateAngle();
      this.setEndPos();
      this.updateCollisionPos(new Vec3(this.getX(), this.getY(), this.getZ()), new Vec3(this.endPosX, this.endPosY, this.endPosZ));
   }

   @Override
   public void tick() {
      super.tick();
      this.tickHandler();
   }

   @Override
   protected boolean shouldRemove() {
      if (this.shouldStopFollowOwner()) {
         this.setFollowingOwner(false);
         this.setVisualSize(this.getVisualSize() - this.getSize() / 20.0F);
      } else {
         this.setVisualSize(this.getSize());
      }

      return super.shouldRemove();
   }

   public void tickHandler() {
      this.prevCollidePosX = this.getCollideX();
      this.prevCollidePosY = this.getCollideY();
      this.prevCollidePosZ = this.getCollideZ();
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
      this.updateAngle();
      if (!this.level().isClientSide()) {
         this.setEndPos();
         if (this.isFollowingOwner() && (this.getOwner() == null || !this.getOwner().isAlive())) {
            this.discard();
         }

         if (this.getOwner() instanceof LivingEntity owner) {
            if (this.tickCount % 3 == 0) {
               this.handleBlockInteraction();
            }

            for (Entity entity : this.updateCollisionPos(new Vec3(this.getX(), this.getY(), this.getZ()), new Vec3(this.endPosX, this.endPosY, this.endPosZ))) {
               if (this.canHitEntity(entity) && !TensuraGameRules.isLabyrinthPvpOff(this.level(), entity, owner)) {
                  if (this.tickCount % 5 == 0) {
                     this.explosion(entity.getX(), entity.getY() + entity.getBbHeight() / 2.0F, entity.getZ());
                  }

                  this.hitEntity(entity, ProjectileHitResult.DEFAULT);
               }
            }
         }
      }
   }

   protected double getBlockInteractionRangeMultiplier() {
      return 2.0;
   }

   protected void handleBlockInteraction() {
      boolean shouldExplode = false;
      double radius = this.getSize() * this.getBlockInteractionRangeMultiplier();
      double radiusSqr = radius * radius;
      double centerX = this.getCollideX();
      double centerY = this.getCollideY();
      double centerZ = this.getCollideZ();
      int minX = (int)(centerX - radius);
      int minY = (int)(centerY - radius);
      int minZ = (int)(centerZ - radius);
      int maxX = (int)(centerX + radius);
      int maxY = (int)(centerY + radius);
      int maxZ = (int)(centerZ + radius);
      boolean grief = this.shouldGrief();
      Level level = this.level();
      MutableBlockPos pos = new MutableBlockPos();

      for (int x = minX; x <= maxX; x++) {
         double dx = x - centerX;
         double dxSqr = dx * dx;

         for (int y = minY; y <= maxY; y++) {
            double dy = y - centerY;
            double dyDxSqr = dxSqr + dy * dy;

            for (int z = minZ; z <= maxZ; z++) {
               double dz = z - centerZ;
               if (!(dyDxSqr + dz * dz > radiusSqr)) {
                  pos.set(x, y, z);
                  if (grief) {
                     this.interactBlocks(pos.immutable());
                  }

                  if (!level.getBlockState(pos).isAir() && !level.getFluidState(pos).isSource()) {
                     shouldExplode = true;
                  }
               }
            }
         }
      }

      shouldExplode = shouldExplode && !TensuraGameRules.isLabyrinthPvpOff(level);
      if (this.tickCount % 3 == 0 && shouldExplode) {
         this.explosion(this.getCollideX(), this.getCollideY(), this.getCollideZ());
      }
   }

   public void explosion(double x, double y, double z) {
      if (!(this.getExplosionRadius() <= 0.0F)) {
         boolean mobGrief = this.shouldGrief();
         boolean fire = mobGrief && this.getBurnTicks() > 0;
         if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), x, y, z)
            .isFalse()) {
            this.level().explode(this.getOwner(), x, y, z, this.getExplosionRadius(), fire, this.getExplosionInteraction());
            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker()).grief(this.getSkill(), this.level(), this.getOwner(), x, y, z);
         }
      }
   }

   protected boolean canDestroyBlock(BlockPos pos) {
      BlockState state = this.level().getBlockState(pos);
      if (!state.canBeReplaced()) {
         return false;
      } else {
         return state.is(Blocks.FIRE) ? false : !this.level().getFluidState(pos).isSource();
      }
   }

   protected void interactBlocks(BlockPos pos) {
      if (this.canDestroyBlock(pos)) {
         if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
            .isFalse()) {
            this.level().destroyBlock(pos, false);
            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
         }
      }
   }

   protected void placeFires(BlockPos pos) {
      if (this.random.nextInt(3) == 0
         && this.level().getBlockState(pos).isAir()
         && this.level().getBlockState(pos.below()).isSolidRender(this.level(), pos.below())) {
         this.level().setBlockAndUpdate(pos, BaseFireBlock.getState(this.level(), pos));
      }
   }

   protected void setEndPos() {
      this.endPosX = this.getX() + this.getRange() * Math.cos(this.getYaw()) * Math.cos(this.getPitch());
      this.endPosZ = this.getZ() + this.getRange() * Math.sin(this.getYaw()) * Math.cos(this.getPitch());
      this.endPosY = this.getY() + this.getRange() * Math.sin(this.getPitch());
   }

   public List<Entity> updateCollisionPos(Vec3 from, Vec3 to) {
      if (!(this.getOwner() instanceof LivingEntity)) {
         return List.of();
      }

      BlockHitResult result = this.level().clip(new ClipContext(from, to, Block.COLLIDER, Fluid.NONE, this));
      if (!this.piercingBlock() && result.getType() != Type.MISS) {
         Vec3 pos = result.getLocation();
         this.setCollidePos((float)pos.x(), (float)pos.y(), (float)pos.z());
         this.side = result.getDirection();
      } else {
         this.setCollidePos((float)this.endPosX, (float)this.endPosY, (float)this.endPosZ);
         this.side = null;
      }

      return this.collectEntityCollision(from, to);
   }

   public List<Entity> collectEntityCollision(Vec3 from, Vec3 to) {
      List<Entity> entities = new ArrayList<>();
      AABB bounds = new AABB(
            Math.min(this.getX(), this.getCollideX()),
            Math.min(this.getY(), this.getCollideY()),
            Math.min(this.getZ(), this.getCollideZ()),
            Math.max(this.getX(), this.getCollideX()),
            Math.max(this.getY(), this.getCollideY()),
            Math.max(this.getZ(), this.getCollideZ())
         )
         .inflate(this.getSize());
      Vec3 forward = this.getLookAngle().normalize();
      double fwdX = forward.x;
      double fwdY = forward.y;
      double fwdZ = forward.z;
      double thisX = this.getX();
      double thisY = this.getY();
      double thisZ = this.getZ();

      for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, bounds)) {
         double dx = entity.getX() - thisX;
         double dy = entity.getY() - thisY;
         double dz = entity.getZ() - thisZ;
         if (!(fwdX * dx + fwdY * dy + fwdZ * dz <= 0.0)) {
            float pad = entity.getPickRadius() + 0.5F;
            AABB padded = entity.getBoundingBox().inflate(pad, pad, pad);
            if (padded.contains(from)) {
               entities.add(entity);
            } else if (padded.clip(from, to).isPresent()) {
               entities.add(entity);
            }
         }
      }

      return entities;
   }

   public void updateAngle() {
      if (!this.getTargetPos().equals(BlockPos.ZERO)) {
         BlockPos Target = this.getTargetPos();
         double d0 = Target.getX() - this.getX();
         double d1 = Target.getY() - this.getY();
         double d2 = Target.getZ() - this.getZ();
         double d3 = Math.sqrt(d0 * d0 + d2 * d2);
         float yRot = Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * 180.0F / (float)Math.PI) - 90.0F);
         float xRot = Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * 180.0F / (float)Math.PI)));
         this.setYaw((float)((yRot + 90.0F) * Math.PI / 180.0));
         this.setPitch((float)(-xRot * Math.PI / 180.0));
         this.setRot(this.getPitch(), this.getYaw());
      } else if (this.isFollowingOwner() && this.getOwner() != null) {
         this.updateAngle(this.getOwner());
      }
   }

   public void updateAngle(Entity owner) {
      this.setRot(owner.getYHeadRot(), owner.getXRot());
      this.setYaw((float)((owner.getYRot() + 90.0F) * Math.PI / 180.0));
      this.setPitch((float)(-owner.getXRot() * Math.PI / 180.0));
      Vec3 vec3 = this.getFollowPos(owner).add(owner.getViewVector(1.0F).normalize().scale(0.5));
      this.setPos(vec3.x, vec3.y, vec3.z);
   }

   protected Vec3 getFollowPos(Entity owner) {
      return new Vec3(owner.getX(), owner.getY() + owner.getBbHeight() * 3.0F / 4.0F - this.getBbHeight() / 2.0F, owner.getZ());
   }

   public void startParticles(Vec3 start) {
   }

   public void rayParticles(Vec3 pos, int i) {
   }

   public void hitParticles(double x, double y, double z) {
   }

   public static void spawnLastingBeam(
      EntityType<? extends BeamProjectile> entityType,
      float damage,
      float size,
      float range,
      LivingEntity owner,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost,
      Pair<Double, Double> increaseCost,
      int heldTicks
   ) {
      spawnLastingBeam(entityType, damage, 0.0F, size, 21, range, 0.0F, owner.getEyePosition(), owner, instance, mode, cost, increaseCost, heldTicks);
   }

   public static void spawnLastingBeam(
      EntityType<? extends BeamProjectile> entityType,
      float damage,
      float secondaryDamage,
      float size,
      float range,
      LivingEntity owner,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost,
      Pair<Double, Double> increaseCost,
      int heldTicks
   ) {
      spawnLastingBeam(entityType, damage, secondaryDamage, size, 21, range, 0.0F, owner.getEyePosition(), owner, instance, mode, cost, increaseCost, heldTicks);
   }

   public static void spawnLastingBeam(
      EntityType<? extends BeamProjectile> entityType,
      float damage,
      float size,
      int life,
      float range,
      float explosionRange,
      Vec3 pos,
      LivingEntity owner,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost,
      Pair<Double, Double> increaseCost,
      int heldTicks
   ) {
      spawnLastingBeam(entityType, damage, 0.0F, size, life, range, explosionRange, pos, owner, instance, mode, cost, increaseCost, heldTicks);
   }

   public static void spawnLastingBeam(
      EntityType<? extends BeamProjectile> entityType,
      float damage,
      float secondaryDamage,
      float size,
      int life,
      float range,
      float explosionRange,
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
         if (tag.getInt("BeamID") == 0 && !EnergyHelper.isOutOfEnergy(owner, (Double)cost.getFirst(), (Double)cost.getSecond())) {
            BeamProjectile beam = (BeamProjectile)entityType.create(level);
            if (beam == null) {
               return;
            }

            beam.setOwner(owner);
            beam.setFollowingOwner(true);
            beam.setLife(life);
            beam.setDamage(damage);
            beam.setSecondaryDamage(secondaryDamage);
            beam.setExplosionRadius(explosionRange);
            beam.setSize(size);
            beam.setRange(range);
            beam.setPos(pos);
            beam.setSkill(instance);
            beam.setMode(mode);
            beam.setApCost((Double)cost.getFirst());
            beam.setMpCost((Double)cost.getSecond());
            beam.updateAngle(owner);
            owner.level().addFreshEntity(beam);
            owner.swing(InteractionHand.MAIN_HAND, true);
            tag.putInt("BeamID", beam.getId());
         } else if (!(owner.level().getEntity(tag.getInt("BeamID")) instanceof BeamProjectile beam && beam.isFollowingOwner())) {
            tag.putInt("BeamID", 0);
         } else if (heldTicks % 20 != 0 || !EnergyHelper.isOutOfEnergy(owner, (Double)increaseCost.getFirst(), (Double)increaseCost.getSecond())) {
            beam.setAge(0);
         }

         instance.markDirty();
      }
   }

   @Generated
   public float getExplosionRadius() {
      return this.explosionRadius;
   }

   @Generated
   public void setExplosionRadius(float explosionRadius) {
      this.explosionRadius = explosionRadius;
   }
}
