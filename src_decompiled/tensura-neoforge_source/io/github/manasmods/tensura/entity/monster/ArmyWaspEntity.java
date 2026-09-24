package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.BreedWithPartner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowTemptation;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.ItemTemptingSensor;
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

public class ArmyWaspEntity extends TensuraTamableEntity implements SmartBrainOwner<ArmyWaspEntity>, GeoEntity, IFlying {
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(ArmyWaspEntity.class, EntityDataSerializers.BOOLEAN);
   protected int flyingTick;
   protected boolean wasFlying;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ArmyWaspEntity(EntityType<? extends TamableAnimal> type, Level worldIn) {
      super(type, worldIn);
      this.initFlying(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.ARMOR, 4.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.FLYING_SPEED, 0.6F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.1)
         .add(Attributes.STEP_HEIGHT, 1.0);
   }

   @Override
   public void switchMoveControl(MoveControl control) {
      this.moveControl = control;
   }

   @Override
   public void switchNavigation(PathNavigation navigation) {
      this.navigation = navigation;
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(compound.getBoolean("Flying"));
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   @Override
   public void setFlying(boolean flying) {
      this.entityData.set(FLYING, flying);
   }

   @Override
   public boolean wasFlying() {
      return this.wasFlying;
   }

   @Override
   public boolean shouldStopFlying(Mob entity) {
      return IFlying.super.shouldStopFlying(entity) || this.isOrderedToSit() || this.isInLove();
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   @Override
   protected boolean canGoToSleep() {
      return super.canGoToSleep() && this.isOrderedToSit();
   }

   @Override
   public EntityDimensions getSleepingDimensions(Pose pPose) {
      return this.getType().getDimensions().scale(this.getAgeScale() * 0.75F);
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public ArmyWaspEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      ArmyWaspEntity wasp = (ArmyWaspEntity)((EntityType)MonsterEntityTypes.ARMY_WASP.get()).create(pLevel);
      if (wasp == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         wasp.setOwnerUUID(uuid);
         wasp.setTame(true, true);
      }

      return wasp;
   }

   public void push(Entity pEntity) {
      if (!pEntity.getType().equals(this.getType())) {
         super.push(pEntity);
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source == this.damageSources().inWall() || super.isInvulnerableTo(source);
   }

   public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
      if (!(pState.getBlock() instanceof WebBlock)) {
         super.makeStuckInBlock(pState, pMotionMultiplier);
      }
   }

   public float getSpeed() {
      float speed = super.getSpeed();
      if (this.onGround()) {
         speed *= 0.5F;
      }

      return speed;
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
   }

   @Override
   public boolean doHurtTarget(Entity target) {
      boolean hurt = target.hurt(this.damageSources().sting(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
      if (hurt) {
         if (target instanceof LivingEntity living && living.getLastHurtByMobTimestamp() == living.tickCount) {
            living.setStingerCount(living.getStingerCount() + 1);

            int i = switch (this.level().getDifficulty()) {
               case EASY -> 5;
               case NORMAL -> 10;
               case HARD -> 16;
               default -> 0;
            };
            if (i > 0) {
               int level = living.getStingerCount() / 3;
               living.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), i * 20, level), this);
               living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, i * 20, level > 0 ? 1 : 0), this);
            }
         }

         this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
      }

      return hurt;
   }

   @Override
   public boolean isFood(ItemStack stack) {
      return stack.is(ItemTags.FLOWERS);
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.armyWasp, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (!this.onGround()) {
         this.setFlying(true);
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   protected SoundEvent getAmbientSound() {
      return !this.isFlying() ? null : SoundEvents.BEE_LOOP;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.BEE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BEE_DEATH;
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<ArmyWaspEntity>> getSensors() {
      return ObjectArrayList.of(
         new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new ItemTemptingSensor().temptedWith(ArmyWaspEntity::isFood)}
      );
   }

   public BrainActivityGroup<ArmyWaspEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<ArmyWaspEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false),
                  new FollowTemptation()
                     .followIf(TamableAnimal::isOwnedBy)
                     .speedMod((entity, player) -> 1.1F)
                     .startCondition(entity -> !entity.isOrderedToSit()),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  new SubordinateFollowOwner().canTeleportOffGroundWhen(entity -> {
                     entity.setFlying(true);
                     return true;
                  }),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomFlyAndWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<ArmyWaspEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new AnimatableMeleeAttack(5).attackInterval(entity -> 10).whenStarting(entity -> entity.triggerAnim("miscController", "sting"))
         }
      );
   }

   protected PlayState loopController(AnimationState<ArmyWaspEntity> state) {
      String name;
      if (!this.isAlive()) {
         name = "animation.army_wasp.dead";
      } else if (this.isSleeping()) {
         name = "animation.army_wasp.sleep";
      } else if (this.onGround()) {
         if (state.isMoving()) {
            name = "animation.army_wasp.walk";
         } else {
            name = "animation.army_wasp.idle";
         }
      } else if (state.isMoving()) {
         name = "animation.army_wasp.fly";
      } else {
         name = "animation.army_wasp.hover";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> {
                  this.swinging = false;
                  return PlayState.STOP;
               })
               .triggerableAnim("eat", RawAnimation.begin().then("animation.army_wasp.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("sting", RawAnimation.begin().then("animation.army_wasp.sting", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   @Override
   public void setFlyingTick(int flyingTick) {
      this.flyingTick = flyingTick;
   }

   @Generated
   @Override
   public int getFlyingTick() {
      return this.flyingTick;
   }

   @Generated
   @Override
   public void setWasFlying(boolean wasFlying) {
      this.wasFlying = wasFlying;
   }
}
