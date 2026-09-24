package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.spiritual.water.MegiddoMagic;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MegiddoBubbleEntity extends BarrierEntity implements GeoEntity {
   private static final EntityDataAccessor<Integer> CHARGE = SynchedEntityData.defineId(MegiddoBubbleEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> CHAIN_CHARGE = SynchedEntityData.defineId(MegiddoBubbleEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> START_POS_X = SynchedEntityData.defineId(MegiddoBubbleEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> START_POS_Y = SynchedEntityData.defineId(MegiddoBubbleEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> START_POS_Z = SynchedEntityData.defineId(MegiddoBubbleEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> TARGET_POS_X = SynchedEntityData.defineId(MegiddoBubbleEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> TARGET_POS_Y = SynchedEntityData.defineId(MegiddoBubbleEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> TARGET_POS_Z = SynchedEntityData.defineId(MegiddoBubbleEntity.class, EntityDataSerializers.FLOAT);
   private int beamTick = 0;
   private int chargeChain = 0;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public MegiddoBubbleEntity(Level level, LivingEntity entity) {
      this((EntityType<? extends MegiddoBubbleEntity>)MiscEntityTypes.MEGIDDO_BUBBLE.get(), level);
      this.setElementalAttack(true);
      this.setOwner(entity);
   }

   public MegiddoBubbleEntity(EntityType<? extends MegiddoBubbleEntity> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   public boolean shouldCreateParts() {
      return false;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CHARGE, 10);
      builder.define(CHAIN_CHARGE, 0);
      builder.define(START_POS_X, 0.0F);
      builder.define(START_POS_Y, 0.0F);
      builder.define(START_POS_Z, 0.0F);
      builder.define(TARGET_POS_X, 0.0F);
      builder.define(TARGET_POS_Y, 0.0F);
      builder.define(TARGET_POS_Z, 0.0F);
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("Charge", this.getCharge());
      pCompound.putInt("ChainCharge", this.getChainCharge());
      pCompound.putFloat("xStart", (float)this.getStartBeamOffset().x());
      pCompound.putFloat("yStart", (float)this.getStartBeamOffset().y());
      pCompound.putFloat("zStart", (float)this.getStartBeamOffset().z());
      pCompound.putFloat("xTarget", (float)this.getTargetPos().x());
      pCompound.putFloat("yTarget", (float)this.getTargetPos().y());
      pCompound.putFloat("zTarget", (float)this.getTargetPos().z());
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setCharge(pCompound.getInt("Charge"));
      this.setChainCharge(pCompound.getInt("ChainCharge"));
      this.setStartBeamOffset(pCompound.getFloat("xStart"), pCompound.getFloat("yStart"), pCompound.getFloat("zStart"));
      this.setTargetPos(pCompound.getFloat("xTarget"), pCompound.getFloat("yTarget"), pCompound.getFloat("zTarget"));
   }

   public int getCharge() {
      return (Integer)this.getEntityData().get(CHARGE);
   }

   public void setCharge(int charge) {
      this.getEntityData().set(CHARGE, charge);
   }

   public int getChainCharge() {
      return (Integer)this.getEntityData().get(CHAIN_CHARGE);
   }

   public void setChainCharge(int charge) {
      this.getEntityData().set(CHAIN_CHARGE, charge);
      this.chargeChain = charge;
   }

   public void setStartBeamOffset(float x, float y, float z) {
      this.getEntityData().set(START_POS_X, x);
      this.getEntityData().set(START_POS_Y, y);
      this.getEntityData().set(START_POS_Z, z);
   }

   public Vec3 getStartBeamOffset() {
      return new Vec3(
         ((Float)this.getEntityData().get(START_POS_X)).floatValue(),
         ((Float)this.getEntityData().get(START_POS_Y)).floatValue(),
         ((Float)this.getEntityData().get(START_POS_Z)).floatValue()
      );
   }

   public void setTargetPos(float x, float y, float z) {
      this.getEntityData().set(TARGET_POS_X, x);
      this.getEntityData().set(TARGET_POS_Y, y);
      this.getEntityData().set(TARGET_POS_Z, z);
   }

   public Vec3 getTargetPos() {
      return new Vec3(
         ((Float)this.getEntityData().get(TARGET_POS_X)).floatValue(),
         ((Float)this.getEntityData().get(TARGET_POS_Y)).floatValue(),
         ((Float)this.getEntityData().get(TARGET_POS_Z)).floatValue()
      );
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return this.getType().getDimensions().scale(this.getSize());
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return pDistance < 16384.0 || super.shouldRenderAtSqrDistance(pDistance);
   }

   @Override
   protected void updateVisualSize() {
      this.setVisualSize(this.getSize());
   }

   @Override
   public void applyFollowOwner() {
      Entity owner = this.getOwner();
      if (owner != null && this.getChainCharge() <= 0) {
         this.setPos(owner.getX(), owner.getY() + 20.0, owner.getZ());
         List<MegiddoBubbleEntity> bubbles = this.level()
            .getEntitiesOfClass(MegiddoBubbleEntity.class, this.getBoundingBox(), entityData -> entityData.getOwner() == owner && entityData != this);
         if (!bubbles.isEmpty() || !owner.isAlive() || owner.level() != this.level()) {
            this.remove();
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.getTargetPos().equals(Vec3.ZERO)) {
         this.beamTick++;
         if (this.beamTick == 1) {
            Vec3 source = this.getStartBeamOffset();
            Vec3 targetPos = this.getTargetPos();
            Vec3 offSetToTarget = targetPos.subtract(source);
            Vec3 normalizes = offSetToTarget.normalize();

            for (int i = 0; i < Mth.floor(offSetToTarget.length()) + 1; i++) {
               Vec3 particlePos = source.add(normalizes.scale(i));
               AABB aabb = new AABB(
                  particlePos.x + 0.5, particlePos.y + 0.5, particlePos.z + 0.5, particlePos.x - 0.5, particlePos.y - 0.5, particlePos.z - 0.5
               );

               for (LivingEntity living : this.level()
                  .getEntitiesOfClass(LivingEntity.class, aabb, entityData -> entityData != this.getOwner() && entityData.isAlive())) {
                  DamageSource damagesource = TensuraDamageTypes.getIndirectEntityDamageSource(this.level(), TensuraDamageTypes.MEGIDDO, this.getOwner(), this)
                     .tensura$setMagiculeCost(this.getMpCost())
                     .tensura$setAbilityInstance(this.getSkill())
                     .tensura$setAbilityMode(this.getMode());
                  if (living.hurt(damagesource, MegiddoMagic.CONFIG.beamDamage)) {
                     TensuraParticleHelper.addServerParticlesAroundSelf(living, ParticleTypes.CLOUD);
                  }
               }
            }
         } else if (this.beamTick >= 10) {
            if (this.getChainCharge() > 0) {
               List<LivingEntity> list = this.getTargetList(this.getTargetPos(), MegiddoMagic.CONFIG.autoRange / 2.0F);
               if (!list.isEmpty()) {
                  if (this.chargeChain <= 0) {
                     this.chargeChain = this.getChainCharge();
                     LivingEntity target = list.get(this.random.nextInt(list.size()));
                     if (this.startNewBeam(target)) {
                        this.level()
                           .playSound(
                              null,
                              target.getX(),
                              target.getY(),
                              target.getZ(),
                              (SoundEvent)TensuraSoundEvents.MEGIDDO_SHOOT.get(),
                              TensuraSkill.ABILITY_SOUND,
                              0.5F,
                              1.5F
                           );
                     }
                  } else {
                     LivingEntity target = list.get(this.random.nextInt(list.size()));
                     this.setStartBeamOffset((float)this.getTargetPos().x(), (float)this.getTargetPos().y(), (float)this.getTargetPos().z());
                     this.setTargetPos((float)target.getX(), (float)(target.getY() + target.getBbHeight() / 2.0F), (float)target.getZ());
                  }

                  this.chargeChain--;
               } else {
                  this.setTargetPos(0.0F, 0.0F, 0.0F);
               }
            } else {
               this.setTargetPos(0.0F, 0.0F, 0.0F);
            }

            this.beamTick = 0;
         }
      } else if (this.getChainCharge() > 0
         && this.getAge() >= 50
         && this.getLife() - this.getAge() >= 50
         && (this.getAge() - 50) % MegiddoMagic.CONFIG.autoBeamTime == 0) {
         List<LivingEntity> list = this.getTargetList(this.position().add(0.0, -40.0, 0.0), MegiddoMagic.CONFIG.autoRange);
         if (!list.isEmpty()) {
            LivingEntity target = list.get(this.random.nextInt(list.size()));
            if (this.startNewBeam(target)) {
               this.level()
                  .playSound(
                     null,
                     target.getX(),
                     target.getY(),
                     target.getZ(),
                     (SoundEvent)TensuraSoundEvents.MEGIDDO_SHOOT.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.5F
                  );
            }
         }
      }

      if (this.level().dimensionType().ultraWarm()) {
         this.discard();
      }
   }

   public boolean startNewBeam(LivingEntity target) {
      if (this.getCharge() <= 0 || this.level().isClientSide()) {
         return false;
      }

      if (!this.hasSunlight()) {
         return false;
      }

      this.setCharge(this.getCharge() - 1);
      if (this.getCharge() <= 0) {
         this.setAge(this.getLife() - 20);
      }

      this.setTargetPos((float)target.getX(), (float)(target.getY() + target.getBbHeight() / 2.0F), (float)target.getZ());
      this.setStartBeamOffset((float)this.getX(), (float)(this.getY() - this.getSize() * 0.25), (float)this.getZ());
      return true;
   }

   private boolean hasSunlight() {
      Level level = this.level();
      if (!level.isRaining() && !level.isThundering() && !level.isNight()) {
         return level.getBrightness(LightLayer.SKY, this.blockPosition()) < 14 ? false : level.isDay() && !level.dimensionType().hasFixedTime();
      } else {
         return false;
      }
   }

   public List<LivingEntity> getTargetList(Vec3 pos, float radius) {
      AABB box = new AABB(ObjectSelectionHelper.getBlockPos(pos)).inflate(radius);
      return this.level().getEntitiesOfClass(LivingEntity.class, box, this::shouldTarget);
   }

   protected boolean shouldTarget(LivingEntity entity) {
      if (entity == this.getOwner()) {
         return false;
      } else if (!entity.level().isLoaded(entity.blockPosition())) {
         return false;
      } else if (entity.hasInfiniteMaterials()) {
         return false;
      } else {
         return this.getOwner() != null && entity.isAlliedTo(this.getOwner()) ? false : this.hasLineOfSight(entity);
      }
   }

   public boolean hasLineOfSight(Entity pEntity) {
      if (pEntity.level() != this.level()) {
         return false;
      }

      Vec3 vec3 = new Vec3(this.getX(), this.getY(), this.getZ());
      Vec3 vec31 = new Vec3(pEntity.getX(), pEntity.getEyeY(), pEntity.getZ());
      return this.level().clip(new ClipContext(vec3, vec31, Block.COLLIDER, Fluid.NONE, this)).getType() == Type.MISS;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(
            this,
            "controller",
            0,
            event -> {
               if (this.getAge() < 50) {
                  return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.megiddo.start"));
               } else {
                  return this.getLife() - this.getAge() < 50
                     ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.megiddo.end"))
                     : event.setAndContinue(RawAnimation.begin().thenLoop("animation.megiddo.loop"));
               }
            }
         )
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
