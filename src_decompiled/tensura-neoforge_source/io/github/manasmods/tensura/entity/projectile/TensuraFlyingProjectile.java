package io.github.manasmods.tensura.entity.projectile;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitEvent;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.extra.SpatialDominationSkill;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierPart;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.TensuraExplosionDamageCalculator;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class TensuraFlyingProjectile extends TensuraProjectile {
   private static final EntityDataAccessor<Float> LOOK_DISTANCE = SynchedEntityData.defineId(TensuraFlyingProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DELAY_TICK = SynchedEntityData.defineId(TensuraFlyingProjectile.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> PIERCING_BLOCK = SynchedEntityData.defineId(TensuraFlyingProjectile.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> PIERCING_ENTITY = SynchedEntityData.defineId(TensuraFlyingProjectile.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Vector3f> DELAY_VEC = SynchedEntityData.defineId(TensuraFlyingProjectile.class, EntityDataSerializers.VECTOR3);
   protected float speed = 1.0F;
   protected float explosionRadius = 0.0F;
   protected float hitRadius = 0.0F;
   protected int armorHurt = 0;
   protected boolean ignoreInvulnerabilityOnHit = false;
   private Vec3 ownerOffset = Vec3.ZERO;
   private Vec3 targetOffset = Vec3.ZERO;
   private float delaySizeChange = 0.0F;
   private Entity homingTarget = null;
   private transient Vec3 cachedDelayVec;
   private transient Vector3f cachedDelayVecKey;

   public TensuraFlyingProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setLife(TensuraProjectile.CONFIG.projectileDespawnTick);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DELAY_TICK, 0);
      builder.define(LOOK_DISTANCE, 0.0F);
      builder.define(PIERCING_BLOCK, Boolean.FALSE);
      builder.define(PIERCING_ENTITY, Boolean.FALSE);
      builder.define(DELAY_VEC, new Vector3f());
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("DelayTick", this.getDelayTick());
      compound.putFloat("LookDistance", this.getLookDistance());
      compound.putFloat("DelaySize", this.getDelaySizeChange());
      compound.putDouble("delayX", this.getDelayVec().x);
      compound.putDouble("delayY", this.getDelayVec().y);
      compound.putDouble("delayZ", this.getDelayVec().z);
      compound.putFloat("Speed", this.getSpeed());
      compound.putFloat("ExplosionRadius", this.getExplosionRadius());
      compound.putFloat("HitRadius", this.getHitRadius());
      compound.putInt("ArmorHurt", this.getArmorHurt());
      compound.putBoolean("IgnoreInvulnerabilityOnHit", this.isIgnoreInvulnerabilityOnHit());
      compound.putBoolean("PiercingEntity", this.isPiercingEntity());
      compound.putBoolean("PiercingBlock", this.isPiercingBlock());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setDelayTick(compound.getInt("DelayTick"));
      this.setLookDistance(compound.getFloat("LookDistance"));
      this.setDelaySizeChange(compound.getFloat("DelaySize"));
      this.setDelayVec(new Vec3(compound.getDouble("delayX"), compound.getDouble("delayY"), compound.getDouble("delayZ")));
      this.setSpeed(compound.getFloat("Speed"));
      this.setExplosionRadius(compound.getFloat("ExplosionRadius"));
      this.setHitRadius(compound.getFloat("HitRadius"));
      this.setArmorHurt(compound.getInt("ArmorHurt"));
      this.setIgnoreInvulnerabilityOnHit(compound.getBoolean("IgnoreInvulnerabilityOnHit"));
      this.setPiercingEntity(compound.getBoolean("PiercingEntity"));
      this.setPiercingBlock(compound.getBoolean("PiercingBlock"));
   }

   public int getDelayTick() {
      return (Integer)this.entityData.get(DELAY_TICK);
   }

   public void setDelayTick(int i) {
      this.entityData.set(DELAY_TICK, i);
   }

   public Vec3 getDelayVec() {
      Vector3f current = (Vector3f)this.entityData.get(DELAY_VEC);
      if (this.cachedDelayVec != null && current.equals(this.cachedDelayVecKey)) {
         return this.cachedDelayVec;
      }

      Vec3 vec = new Vec3(current);
      this.cachedDelayVec = vec;
      this.cachedDelayVecKey = current;
      return vec;
   }

   public void setDelayVec(Vec3 vec3) {
      Vector3f f = new Vector3f((float)vec3.x, (float)vec3.y, (float)vec3.z);
      this.entityData.set(DELAY_VEC, f);
      this.cachedDelayVec = vec3;
      this.cachedDelayVecKey = f;
   }

   public float getLookDistance() {
      return (Float)this.entityData.get(LOOK_DISTANCE);
   }

   public void setLookDistance(float i) {
      this.entityData.set(LOOK_DISTANCE, i);
   }

   public boolean isPiercingEntity() {
      return (Boolean)this.entityData.get(PIERCING_ENTITY);
   }

   public void setPiercingEntity(boolean b) {
      this.entityData.set(PIERCING_ENTITY, b);
   }

   public boolean isPiercingBlock() {
      return (Boolean)this.entityData.get(PIERCING_BLOCK);
   }

   public void setPiercingBlock(boolean b) {
      this.entityData.set(PIERCING_BLOCK, b);
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else if (pTarget instanceof BarrierPart part && !part.blocksBuilding) {
         return false;
      } else {
         return this.getOwner() instanceof Mob mob && pTarget instanceof LivingEntity target && !mob.canAttack(target) ? false : super.canHitEntity(pTarget);
      }
   }

   public boolean ignoreExplosion(Explosion explosion) {
      return this.getExplosionRadius() > 0.0F;
   }

   public ResourceLocation[] getTextureLocation() {
      return null;
   }

   public ResourceLocation getTexture() {
      return null;
   }

   @Override
   public void tick() {
      super.tick();
      this.tickHandler();
      if (this.level().isClientSide()) {
         this.flyingParticles();
      }
   }

   @Override
   protected boolean shouldRemove() {
      double range = TensuraProjectile.CONFIG.projectileDespawnRange;
      return range > 0.0 && this.getOwner() != null && this.distanceTo(this.getOwner()) > range ? true : super.shouldRemove();
   }

   public void updateDelayPosition() {
      MagicCircle circle = this.getMagicCircle();
      if (circle != null) {
         if (circle.isAlive()) {
            this.setPos(
               circle.getEyePosition()
                  .add(0.0, this.getBbHeight() / -2.0F, 0.0)
                  .add(this.getOwnerOffset().xRot(-circle.getXRot() * (float) (Math.PI / 180.0)).yRot(-circle.getYRot() * (float) (Math.PI / 180.0)))
            );
         }
      } else if (!this.noPhysics && this.getOwnerOffset() != Vec3.ZERO) {
         Entity owner = this.getOwner();
         if (owner != null) {
            this.setPos(
               owner.getEyePosition()
                  .add(0.0, this.getBbHeight() / -2.0F, 0.0)
                  .add(owner.getLookAngle().normalize())
                  .add(this.getOwnerOffset().xRot(-owner.getXRot() * (float) (Math.PI / 180.0)).yRot(-owner.getYRot() * (float) (Math.PI / 180.0)))
            );
         }
      }

      this.updateShootRotation();
   }

   public void tickHandler() {
      if (this.getDelayTick() > 0) {
         this.setDelayTick(this.getDelayTick() - 1);
         if (this.delaySizeChange != 0.0F) {
            this.setSize(this.getSize() + this.delaySizeChange);
         }

         this.updateShootVector();
         if (this.getDelayTick() == 0) {
            this.setDeltaMovement(this.getDelayVec());
            this.delayShootSound().ifPresent(this::playSound);
            this.hurtMarked = true;
         } else {
            this.updateDelayPosition();
         }

         this.updateRotation(this.getDelayVec(), false);
      } else {
         this.updateRotation();
      }

      Vec3 position = this.position();
      Vec3 targetPos = position.add(this.getDeltaMovement());
      AABB box = this.getBoundingBox().move(this.getDeltaMovement().normalize()).expandTowards(this.getDeltaMovement());

      for (Entity entity : this.level().getEntities(this, box, this::canHitEntity)) {
         EntityHitResult result = new EntityHitResult(entity);
         Changeable<ProjectileHitResult> resultChangeable = Changeable.of(ProjectileHitResult.DEFAULT);
         Changeable<ProjectileDeflection> deflectionChangeable = Changeable.of(ProjectileDeflection.NONE);
         ((ProjectileHitEvent)EntityEvents.PROJECTILE_HIT.invoker()).hit(result, this, deflectionChangeable, resultChangeable);
         if (resultChangeable.get() != ProjectileHitResult.PASS) {
            this.onHitEntity(result, (ProjectileHitResult)resultChangeable.get());
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, result.getLocation(), Context.of(this, null));
         }
      }

      HitResult hitResult = this.level().clip(new ClipContext(position, targetPos, Block.COLLIDER, Fluid.NONE, this));
      if (hitResult.getType() == Type.BLOCK) {
         Changeable<ProjectileHitResult> resultChangeable = Changeable.of(ProjectileHitResult.DEFAULT);
         Changeable<ProjectileDeflection> deflectionChangeable = Changeable.of(ProjectileDeflection.NONE);
         ((ProjectileHitEvent)EntityEvents.PROJECTILE_HIT.invoker()).hit(hitResult, this, deflectionChangeable, resultChangeable);
         if (resultChangeable.get() != ProjectileHitResult.PASS) {
            this.onHit(hitResult);
         }
      }

      if (this.getEffectRange() > 0.0F && this.getMobEffect() != null) {
         this.applyEffectAround(this.getEffectRange());
      }

      this.updateMovement();
      this.checkInsideBlocks();
   }

   public void updateRotation() {
      this.updateRotation(this.getDeltaMovement(), true);
   }

   public void updateRotation(Vec3 vec3, boolean oppositePhysics) {
      this.updateRotation(vec3, this.isNoGravity(), oppositePhysics);
   }

   public void updateRotation(Vec3 vec3, boolean ignoreGravity, boolean oppositePhysics) {
      if (oppositePhysics && this.noPhysics) {
         this.setYRot((float)(Mth.atan2(-vec3.x, -vec3.z) * 180.0F / (float)Math.PI));
      } else {
         this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI));
      }

      this.setXRot((float)(Mth.atan2(vec3.y, vec3.horizontalDistance()) * 180.0F / (float)Math.PI));
      this.setXRot(ignoreGravity ? this.getXRot() : lerpRotation(this.xRotO, this.getXRot()));
      this.setYRot(ignoreGravity ? this.getYRot() : lerpRotation(this.yRotO, this.getYRot()));
   }

   public void updateMovement() {
      Vec3 movement = this.getDeltaMovement();
      this.setPos(this.position().add(movement));
      if (this.getDelayTick() <= 0) {
         Entity target = this.getHomingTarget();
         if (target != null) {
            double distance = this.distanceTo(target);
            double x = (target.getX() - this.getX()) / distance;
            double y = (target.getY() + target.getBbHeight() / 2.0F - this.getY()) / distance;
            double z = (target.getZ() - this.getZ()) / distance;
            double speed = Math.max(this.getSpeed(), this.getDeltaMovement().length());
            this.setDeltaMovement(new Vec3(x, y, z).scale(speed));
            if (distance < 1.0) {
               this.setHomingTarget(null);
            }
         } else if (this.getLookDistance() <= 0.0F && !this.isNoGravity()) {
            this.setDeltaMovement(movement.x, movement.y - 0.05F, movement.z);
         }
      }
   }

   public void setPosAndShoot(LivingEntity entity) {
      this.setPosDirection(entity, TensuraFlyingProjectile.PositionDirection.MIDDLE);
      this.shootFromRot(entity.getLookAngle());
   }

   public void shootFromRot(Vec3 rotation) {
      if (!applyWarpShot(this, this.getOwner(), this.getSpeed())) {
         this.setDeltaMovement(rotation.scale(this.getSpeed()));
         this.updateRotation(this.getDeltaMovement(), true, true);
      }
   }

   public void shootToward(Entity pEntity, float pVelocity, float pInaccuracy) {
      this.shootToward(pEntity, new Vec3(0.0, pEntity.getEyeHeight(), 0.0), pVelocity, pInaccuracy);
   }

   public void shootToward(Entity pEntity, Vec3 offset, float pVelocity, float pInaccuracy) {
      Vec3 towardEntity = new Vec3(pEntity.getX() - this.getX(), pEntity.getY() - this.getY(), pEntity.getZ() - this.getZ()).add(offset).scale(0.1F);
      this.shoot(towardEntity.x(), towardEntity.y(), towardEntity.z(), pVelocity, pInaccuracy);
      this.updateRotation(this.getDeltaMovement(), true, true);
   }

   public void shootFromBehind(Entity pEntity, float pVelocity, float pInaccuracy) {
      this.setPos(pEntity.getEyePosition().subtract(pEntity.getViewVector(1.0F).scale(2.0)));
      Vec3 towardEntity = new Vec3(pEntity.getX() - this.getX(), pEntity.getEyeY() - this.getY(), pEntity.getZ() - this.getZ()).scale(0.1F);
      this.shoot(towardEntity.x(), towardEntity.y(), towardEntity.z(), pVelocity, pInaccuracy);
      this.updateRotation(this.getDeltaMovement(), true, true);
   }

   public void setPosDirection(LivingEntity entity, TensuraFlyingProjectile.PositionDirection direction) {
      if (direction == TensuraFlyingProjectile.PositionDirection.MIDDLE) {
         this.setPos(entity.position().add(entity.getLookAngle()).add(0.0, entity.getEyeHeight() - this.getBoundingBox().getYsize() * 0.5, 0.0));
      } else {
         float rot = entity.yHeadRot + (direction == TensuraFlyingProjectile.PositionDirection.LEFT ? -60 : 60);
         this.setPos(
            entity.getX() - entity.getBbWidth() * 0.5 * Mth.sin(rot * (float) (Math.PI / 180.0)),
            entity.getEyeY() - 0.2F,
            entity.getZ() + entity.getBbWidth() * 0.5 * Mth.cos(rot * (float) (Math.PI / 180.0))
         );
      }
   }

   public void updateShootRotation() {
      this.updateShootVector();
      this.updateRotation(this.getDelayVec(), true, false);
   }

   private void updateShootVector() {
      Entity entity = this.getOwner();
      if (this.getLookDistance() != 0.0F) {
         if (entity instanceof LivingEntity owner) {
            Entity target = entity instanceof Mob mob && mob.getTarget() != null
               ? mob.getTarget()
               : ObjectSelectionHelper.getTargetingEntity(owner, this.getLookDistance(), false, true);
            Vec3 pos;
            if (target != null) {
               pos = target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0);
            } else {
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(
                  entity.level(), owner, Fluid.NONE, this.level().isClientSide() ? Block.VISUAL : Block.COLLIDER, this.getLookDistance()
               );
               pos = result.getLocation();
            }

            pos = pos.add(this.getTargetOffset().xRot(-owner.getXRot() * (float) (Math.PI / 180.0)).yRot(-owner.getYRot() * (float) (Math.PI / 180.0)));
            this.setDelayVec(pos.subtract(this.position()).normalize().scale(this.getSpeed()));
         }
      }
   }

   public static boolean applyWarpShot(Projectile projectile, Entity shooter, float speed) {
      if (!projectile.getType().is(TensuraEntityTags.CAN_WARP_SHOT)) {
         return false;
      }

      if (shooter instanceof LivingEntity owner) {
         if (owner.isShiftKeyDown()) {
            return false;
         }

         AttributeInstance warpShot = owner.getAttribute(TensuraAttributes.WARP_SHOT);
         if (warpShot == null || warpShot.getValue() <= 0.0) {
            return false;
         }

         if (SkillUtils.shouldCancelTeleportation(owner)) {
            return false;
         }

         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(
            LivingEntity.class, owner, SpatialDominationSkill.CONFIG.warpShotDistance, 0.5, false, false, false
         );
         if (target == null || !target.isAlive()) {
            return false;
         }

         if (EnergyHelper.isOutOfEnergy(owner, 0.0, SpatialDominationSkill.CONFIG.magiculeCostWarpShot)) {
            return false;
         }

         Vec3 targetPos = target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0);
         Changeable<Vec3> position = Changeable.of(
            targetPos.subtract(target.calculateViewVector(0.0F, target.getViewYRot(1.0F)).scale(1.5F + target.getBbWidth() / 2.0F))
         );
         if (!((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
            .transmission(projectile, owner, position, WarpPoint.TransmissionType.ABILITY)
            .isFalse()) {
            projectile.setPos((Vec3)position.get());
            Vec3 towardEntity = new Vec3(targetPos.x() - projectile.getX(), targetPos.y() - projectile.getY(), targetPos.z() - projectile.getZ()).scale(0.1F);
            Vec3 vec3 = towardEntity.normalize().scale(speed * warpShot.getValue());
            projectile.setDeltaMovement(vec3);
            double d0 = vec3.horizontalDistance();
            projectile.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI));
            projectile.setXRot((float)(Mth.atan2(vec3.y, d0) * 180.0F / (float)Math.PI));
            projectile.yRotO = projectile.getYRot();
            projectile.xRotO = projectile.getXRot();
            Skills skills = SkillAPI.getSkillsFrom(owner);

            for (AttributeModifier modifier : warpShot.getModifiers()) {
               ResourceLocation location = modifier.id();
               ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(location);
               if (skill == null) {
                  warpShot.removeModifier(location);
               } else {
                  Optional<ManasSkillInstance> optional = skills.getSkill(skill);
                  if (optional.isEmpty()) {
                     warpShot.removeModifier(location);
                  } else {
                     optional.get().addMasteryPoint(owner);
                     skills.checkAndMarkDirty(optional.get());
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected ExplosionInteraction getExplosionInteraction() {
      return this.getOwner() instanceof Player ? ExplosionInteraction.BLOCK : ExplosionInteraction.MOB;
   }

   public DamageSource getExplosionDamageSource() {
      return Explosion.getDefaultDamageSource(this.level(), this.getOwner())
         .tensura$setElement(this.getElement())
         .tensura$setAbilityInstance(this.getSkill())
         .tensura$setAbilityMode(this.getMode())
         .tensura$setMagiculeCost(this.getMpCost())
         .tensura$setAuraCost(this.getApCost());
   }

   public void onExplosion(double x, double y, double z) {
      if (!(this.getExplosionRadius() <= 0.0F)) {
         this.shakeScreenOnExplosion();
         boolean mobGrief = this.shouldGrief();
         if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), x, y, z)
            .isFalse()) {
            this.explode(x, y, z, mobGrief && this.getBurnTicks() > 0);
            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker()).grief(this.getSkill(), this.level(), this.getOwner(), x, y, z);
         }
      }
   }

   protected void shakeScreenOnExplosion() {
      if (this.getExplosionRadius() >= 5.0F) {
         EffectStorage.setCameraShake(this, this.getExplosionRadius() / 2.0F, this.getExplosionRadius() / 200.0F, 15);
      }
   }

   protected void explode(double x, double y, double z, boolean fire) {
      TensuraExplosionDamageCalculator calculator = new TensuraExplosionDamageCalculator(
         this.shouldGrief(), true, Optional.empty(), Optional.of(this.getKnockForce())
      );
      this.level()
         .explode(
            this.getOwner(),
            this.getExplosionDamageSource(),
            calculator,
            x,
            y,
            z,
            this.getExplosionRadius(),
            fire,
            this.getExplosionInteraction(),
            ParticleTypes.EXPLOSION,
            ParticleTypes.EXPLOSION_EMITTER,
            SoundEvents.GENERIC_EXPLODE
         );
   }

   protected void onHitBlock(BlockHitResult pResult) {
      this.applyBlockHitPre(pResult);
      super.onHitBlock(pResult);
      this.applyBlockHitPost(pResult);
   }

   protected void applyBlockHitPre(BlockHitResult pResult) {
      if (this.getHitRadius() > 0.0F) {
         for (Entity target : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(this.getHitRadius()), this::canHitEntity)) {
            this.hitEntity(target, ProjectileHitResult.HIT);
         }
      }

      if (this.shouldGrief() && this.getBurnTicks() > 0) {
         BlockPos pos = pResult.getBlockPos();
         if (this.level().getBlockState(pos).getBlock() instanceof TntBlock
            && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
               .isFalse()) {
            TntBlock.explode(this.level(), pos);
            this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
         }
      }

      if (!this.isPiercingBlock()) {
         this.onExplosion(this.getX(), this.getY(), this.getZ());
      }
   }

   protected void applyBlockHitPost(BlockHitResult pResult) {
      if (!this.isPiercingBlock()) {
         this.hitParticles(this.xOld, this.yOld, this.zOld);
         if (this.hitSound().isPresent()) {
            this.playHitSound(this.hitSound().get(), pResult);
         }

         this.discard();
      }
   }

   protected void onHitEntity(@NotNull EntityHitResult result, ProjectileHitResult customResult) {
      Entity entity = result.getEntity();
      if (this.canHitEntity(entity)) {
         super.onHitEntity(result);
         this.applyHitEntity(entity, result, customResult);
      }
   }

   protected void applyHitEntity(Entity entity, EntityHitResult result, ProjectileHitResult customResult) {
      if (!this.level().isClientSide()) {
         if (this.isIgnoreInvulnerabilityOnHit() && (this.getDamage() > 0.0F || this.getSecondaryDamage() > 0.0F)) {
            entity.invulnerableTime = 0;
         }

         this.hitEntity(entity, customResult);
         if (this.getHitRadius() > 0.0F) {
            for (Entity target : this.level()
               .getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(this.getHitRadius()), targetx -> targetx != entity && this.canHitEntity(targetx))) {
               this.hitEntity(target, customResult);
            }
         }

         this.onExplosion(this.getX(), this.getY(), this.getZ());
      }

      if (!this.isPiercingEntity()) {
         this.hitParticles(this.xOld, this.yOld, this.zOld);
         if (this.hitSound().isPresent()) {
            this.playHitSound(this.hitSound().get(), result);
         }

         this.discard();
      }
   }

   @Override
   protected boolean hitEntity(Entity entity, ProjectileHitResult customResult) {
      if (!super.hitEntity(entity, customResult)) {
         return false;
      }

      int armorHurt = this.getArmorHurt();
      if (armorHurt > 0 && entity instanceof LivingEntity target) {
         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.getType().equals(net.minecraft.world.entity.EquipmentSlot.Type.HAND)) {
               target.getItemBySlot(slot).hurtAndBreak(armorHurt, target, slot);
            }
         }
      }

      return true;
   }

   @Override
   protected void applyBurn(Entity entity) {
      if (entity instanceof MinecartTNT tnt) {
         tnt.primeFuse();
      } else {
         entity.setRemainingFireTicks(Math.max(this.getBurnTicks(), 0));
      }
   }

   @Override
   public void knockBack(Entity entity) {
      if (!(this.getExplosionRadius() > 0.0F)) {
         SkillHelper.knockBack(
            entity, this.getOwner(), this.getSkill(), this.getDeltaMovement(), this.getKnockForce(), this.getKnockResistNegate(), this.getKnockForce() / 3.0
         );
      }
   }

   public Vec3 getRandomVec3() {
      RandomSource rand = this.getRandom();
      return new Vec3(rand.nextDouble() * 2.0 - 1.0, rand.nextDouble() * 2.0 - 1.0, rand.nextDouble() * 2.0 - 1.0);
   }

   public void flyingParticles() {
      if (!this.onGround()) {
         this.level().addParticle(ParticleTypes.INSTANT_EFFECT, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
      }
   }

   public void hitParticles(double x, double y, double z) {
      this.level().addParticle(ParticleTypes.EXPLOSION, x, y, z, 0.0, 0.0, 0.0);
   }

   public Optional<SoundEvent> delayShootSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> hitSound() {
      return Optional.of(SoundEvents.ARROW_HIT);
   }

   protected void playHitSound(SoundEvent sound, HitResult hitresult) {
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), sound, TensuraSkill.ABILITY_SOUND, 2.0F, 0.9F + this.level().random.nextFloat() * 0.2F);
   }

   @Generated
   public float getSpeed() {
      return this.speed;
   }

   @Generated
   public void setSpeed(float speed) {
      this.speed = speed;
   }

   @Generated
   public float getExplosionRadius() {
      return this.explosionRadius;
   }

   @Generated
   public void setExplosionRadius(float explosionRadius) {
      this.explosionRadius = explosionRadius;
   }

   @Generated
   public float getHitRadius() {
      return this.hitRadius;
   }

   @Generated
   public void setHitRadius(float hitRadius) {
      this.hitRadius = hitRadius;
   }

   @Generated
   public int getArmorHurt() {
      return this.armorHurt;
   }

   @Generated
   public void setArmorHurt(int armorHurt) {
      this.armorHurt = armorHurt;
   }

   @Generated
   public boolean isIgnoreInvulnerabilityOnHit() {
      return this.ignoreInvulnerabilityOnHit;
   }

   @Generated
   public void setIgnoreInvulnerabilityOnHit(boolean ignoreInvulnerabilityOnHit) {
      this.ignoreInvulnerabilityOnHit = ignoreInvulnerabilityOnHit;
   }

   @Generated
   public void setOwnerOffset(Vec3 ownerOffset) {
      this.ownerOffset = ownerOffset;
   }

   @Generated
   public Vec3 getOwnerOffset() {
      return this.ownerOffset;
   }

   @Generated
   public void setTargetOffset(Vec3 targetOffset) {
      this.targetOffset = targetOffset;
   }

   @Generated
   public Vec3 getTargetOffset() {
      return this.targetOffset;
   }

   @Generated
   public void setDelaySizeChange(float delaySizeChange) {
      this.delaySizeChange = delaySizeChange;
   }

   @Generated
   public float getDelaySizeChange() {
      return this.delaySizeChange;
   }

   @Generated
   public void setHomingTarget(Entity homingTarget) {
      this.homingTarget = homingTarget;
   }

   @Generated
   public Entity getHomingTarget() {
      return this.homingTarget;
   }

   public enum PositionDirection {
      MIDDLE,
      RIGHT,
      LEFT;
   }
}
