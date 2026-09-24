package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.LeapToTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.variant.HornedRabbitVariant;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HornedRabbitEntity extends TensuraTamableEntity implements GeoEntity, SmartBrainOwner<HornedRabbitEntity>, VariantHolder<HornedRabbitVariant> {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(HornedRabbitEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean wasOnGround;
   private int jumpTicks;
   private int jumpDuration;
   private int jumpChargeTicks;
   private int jumpDelayTicks;
   public static final RawAnimation SCRATCH = RawAnimation.begin().then("animation.horned_rabbit.idle_scratch", LoopType.PLAY_ONCE);
   public static final RawAnimation JUMP = RawAnimation.begin().then("animation.horned_rabbit.jump", LoopType.PLAY_ONCE);
   public static final RawAnimation JUMP_ATTACK = RawAnimation.begin().then("animation.horned_rabbit.jump_attack", LoopType.PLAY_ONCE);

   public HornedRabbitEntity(EntityType<? extends HornedRabbitEntity> type, Level level) {
      super(type, level);
      this.jumpControl = new HornedRabbitEntity.RabbitJumpControl(this);
      this.moveControl = new HornedRabbitEntity.RabbitMoveControl(this);
      this.setSpeedModifier(0.0);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.JUMP_STRENGTH, 0.6F);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ID_TYPE_VARIANT, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(DATA_ID_TYPE_VARIANT, compound.getInt("Variant"));
   }

   public HornedRabbitVariant getVariant() {
      return HornedRabbitVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(HornedRabbitVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
   }

   public boolean canSpawnSprintParticle() {
      return false;
   }

   @NotNull
   public Vec3 getLeashOffset() {
      return new Vec3(0.0, 0.6F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      double d = this.moveControl.getSpeedModifier();
      if (d > 0.0) {
         double e = this.getDeltaMovement().horizontalDistanceSqr();
         if (e < 0.01) {
            this.moveRelative(0.1F, new Vec3(0.0, 0.0, 1.0));
         }
      }

      if (!this.level().isClientSide) {
         this.level().broadcastEntityEvent(this, (byte)1);
      }
   }

   public void setSpeedModifier(double d) {
      this.getNavigation().setSpeedModifier(d);
      this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), d);
   }

   public void setJumping(boolean bl) {
      super.setJumping(bl);
      if (bl) {
         this.playSound(SoundEvents.RABBIT_JUMP, this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }
   }

   public void startJumping() {
      this.setJumping(true);
      this.jumpChargeTicks = 0;
      this.jumpDuration = 10;
      this.jumpTicks = 0;
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   public void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
      if (this.jumpDelayTicks > 0) {
         this.jumpDelayTicks--;
      }

      if (this.jumpChargeTicks > 0 && --this.jumpChargeTicks == 0 && this.onGround()) {
         Path path = this.navigation.getPath();
         Vec3 vec3 = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
         if (path != null && !path.isDone()) {
            vec3 = path.getNextEntityPos(this);
         }

         this.facePoint(vec3.x, vec3.z);
         this.setSpeedModifier(this.isAngry() ? 1.5 : 1.0);
         this.startJumping();
      }

      if (this.onGround()) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         HornedRabbitEntity.RabbitJumpControl control = (HornedRabbitEntity.RabbitJumpControl)this.jumpControl;
         if (!control.wantJump()) {
            if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0 && this.jumpChargeTicks <= 0) {
               this.jumpChargeTicks = 5;
               this.triggerAnim("miscController", "jump");
            }
         } else if (!control.canJump()) {
            this.enableJumpControl();
         }
      }

      this.wasOnGround = this.onGround();
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (this.jumpTicks != this.jumpDuration) {
         this.jumpTicks++;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }
   }

   private void facePoint(double d, double e) {
      this.setYRot((float)(Mth.atan2(e - this.getZ(), d - this.getX()) * (180.0 / Math.PI)) - 90.0F);
   }

   private void enableJumpControl() {
      ((HornedRabbitEntity.RabbitJumpControl)this.jumpControl).setCanJump(true);
   }

   private void disableJumpControl() {
      ((HornedRabbitEntity.RabbitJumpControl)this.jumpControl).setCanJump(false);
   }

   private void setLandingDelay() {
      if (this.moveControl.getSpeedModifier() < 2.2) {
         this.jumpDelayTicks = 5;
      } else {
         this.jumpDelayTicks = 1;
      }
   }

   private void checkLandingDelay() {
      this.setLandingDelay();
      this.disableJumpControl();
   }

   public void handleEntityEvent(byte b) {
      if (b == 1) {
         this.spawnSprintParticle();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleEntityEvent(b);
      }
   }

   public void die(DamageSource source) {
      super.die(source);
      if (!this.isAlive()) {
         this.triggerAnim("miscController", "death");
      }
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.RABBIT_FOOD);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.RABBIT_TAMING_FOOD);
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      HornedRabbitEntity entity = (HornedRabbitEntity)((EntityType)MonsterEntityTypes.HORNED_RABBIT.get()).create(pLevel);
      if (entity == null) {
         return null;
      }

      if (pOtherParent instanceof HornedRabbitEntity rabbit) {
         entity.setVariant(pLevel.getRandom().nextBoolean() ? this.getVariant() : rabbit.getVariant());
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         entity.setOwnerUUID(uuid);
         entity.setTame(true, true);
      }

      return entity;
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.hornedRabbit, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   public static boolean checkHornedRabbitSpawnRules(
      EntityType<HornedRabbitEntity> pRabbit, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      return pLevel.getBlockState(pPos.below()).is(BlockTags.RABBITS_SPAWNABLE_ON) && isBrightEnoughToSpawn(pLevel, pPos);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      if (this.canRandomizeSpawnData(mobSpawnType)) {
         this.applyBiomeVariant(serverLevelAccessor);
      }

      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
   }

   public void applyBiomeVariant(LevelAccessor level) {
      Holder<Biome> biome = level.getBiome(this.blockPosition());
      if (biome.is(BiomeTags.SPAWNS_GOLD_RABBITS)) {
         this.setVariant(level.getRandom().nextBoolean() ? HornedRabbitVariant.GOLD : HornedRabbitVariant.SAND);
      } else if (biome.is(BiomeTags.SPAWNS_WHITE_RABBITS)) {
         this.setVariant(level.getRandom().nextBoolean() ? HornedRabbitVariant.WHITE : HornedRabbitVariant.SALT);
      } else {
         this.setVariant(HornedRabbitVariant.byId(level.getRandom().nextInt(5)));
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.RABBIT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.RABBIT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.RABBIT_DEATH;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   public List<ExtendedSensor<HornedRabbitEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<HornedRabbitEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<HornedRabbitEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false),
                  new SubordinateFollowOwner(),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{
                     new SetRandomWalkTarget().cooldownFor(entity -> 0),
                     new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60)).whenStarting(entity -> {
                        if (entity.getRandom().nextFloat() <= 0.05) {
                           entity.triggerAnim("miscController", "scratch");
                        }
                     })
                  }
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<HornedRabbitEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new LeapToTarget(0)
               .minRange((entity, target) -> 2.0F)
               .leapRange((entity, target) -> 6.0F)
               .moveSpeedContribution((frog, entity) -> 1.5F)
               .verticalJumpStrength((entity, target) -> entity.getJumpPower(0.5F))
               .attackInterval(entity -> 20)
               .whenStarting(entity -> entity.triggerAnim("miscController", "jump_attack")),
            new CustomRangeAttack(0).maxAttackRadius(3.0F).attackInterval(entity -> 20)
         }
      );
   }

   protected PlayState loopController(AnimationState<HornedRabbitEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.horned_rabbit.sleep";
      } else if (this.isInSittingPose()) {
         name = "animation.horned_rabbit.stay";
      } else if (!state.isMoving() || !this.isInLiquid() && !this.onGround()) {
         name = "animation.horned_rabbit.idle";
      } else {
         name = "animation.horned_rabbit.walk";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "miscController", 10, this::loopController)
            .triggerableAnim("scratch", SCRATCH)
            .triggerableAnim("jump", JUMP)
            .triggerableAnim("jump_attack", JUMP_ATTACK)
            .triggerableAnim("death", RawAnimation.begin().then("animation.horned_rabbit.death", LoopType.HOLD_ON_LAST_FRAME))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static class RabbitJumpControl extends JumpControl {
      private final HornedRabbitEntity rabbit;
      private boolean canJump;

      public RabbitJumpControl(HornedRabbitEntity rabbit) {
         super(rabbit);
         this.rabbit = rabbit;
      }

      public boolean wantJump() {
         return this.jump;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void tick() {
         if (!this.rabbit.isSleeping()) {
            if (this.jump) {
               this.rabbit.startJumping();
               this.jump = false;
            }
         }
      }

      @Generated
      public void setCanJump(boolean canJump) {
         this.canJump = canJump;
      }
   }

   static class RabbitMoveControl extends MoveControl {
      private final HornedRabbitEntity rabbit;
      private double nextJumpSpeed;

      public RabbitMoveControl(HornedRabbitEntity rabbit) {
         super(rabbit);
         this.rabbit = rabbit;
      }

      public void tick() {
         if (this.rabbit.onGround() && !this.rabbit.jumping && !((HornedRabbitEntity.RabbitJumpControl)this.rabbit.jumpControl).wantJump()) {
            this.rabbit.setSpeedModifier(0.0);
         } else if (this.hasWanted()) {
            this.rabbit.setSpeedModifier(this.nextJumpSpeed);
         }

         super.tick();
      }

      public void setWantedPosition(double d, double e, double f, double g) {
         if (this.rabbit.isInWater()) {
            g = 1.5;
         }

         super.setWantedPosition(d, e, f, g);
         if (g > 0.0) {
            this.nextJumpSpeed = g;
         }
      }
   }
}
