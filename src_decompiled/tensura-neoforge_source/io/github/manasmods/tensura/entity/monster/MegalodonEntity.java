package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributeUtils;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.OrbitAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.OrbitMovement;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.IGiantMob;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
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

public class MegalodonEntity extends TensuraMountEntity implements SmartBrainOwner<MegalodonEntity>, GeoEntity, IFlying, ITensuraMount, IGiantMob {
   protected static final EntityDataAccessor<Integer> ROLLING_TICK = SynchedEntityData.defineId(MegalodonEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(MegalodonEntity.class, EntityDataSerializers.BOOLEAN);
   protected int flyingTick;
   protected boolean wasFlying;
   private int chosenAttack = 0;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public MegalodonEntity(EntityType<? extends MegalodonEntity> type, Level worldIn) {
      super(type, worldIn);
      this.initFlying(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 70.0)
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.FOLLOW_RANGE, 48.0)
         .add(Attributes.MOVEMENT_SPEED, 0.4F)
         .add(Attributes.FLYING_SPEED, 0.7F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.STEP_HEIGHT, 2.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.5);
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
      builder.define(ROLLING_TICK, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putInt("RollingTick", this.getRollingTick());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(compound.getBoolean("Flying"));
      this.setRollingTick(compound.getInt("RollingTick"));
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
   public boolean canIgnoreCollisionFlight() {
      return true;
   }

   public int getRollingTick() {
      return (Integer)this.entityData.get(ROLLING_TICK);
   }

   public void setRollingTick(int tick) {
      this.entityData.set(ROLLING_TICK, tick);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || source.type().effects().equals(DamageEffects.POKING) || super.isInvulnerableTo(source);
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof MegalodonEntity megalodon) {
         return megalodon.isTame() == this.isTame();
      } else {
         return entity instanceof CharybdisEntity charybdis ? charybdis.isTame() == this.isTame() : false;
      }
   }

   public boolean canAttack(LivingEntity pTarget) {
      return this.isAlliedTo(pTarget) ? false : super.canAttack(pTarget);
   }

   @Override
   public boolean breakableBlocks(LivingEntity entity, BlockPos pos, BlockState state) {
      return entity.level().dimension().equals(TensuraDimensions.HELL)
         ? state.is(TensuraBlockTags.BREAKABLE_BY_MONSTER)
         : !state.is(TensuraBlockTags.BOSS_IMMUNE);
   }

   @Override
   public boolean dropBlockLoot(LivingEntity entity, BlockState state) {
      return this.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE)) ? false : !state.is(TensuraBlockTags.SKILL_BREAK_EASY);
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
      if (this.getRollingTick() > 0) {
         this.setRollingTick(this.getRollingTick() - 1);
         this.hasImpulse = true;
         List<LivingEntity> list = this.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               this.getBoundingBox().inflate(2.5),
               entity -> !this.isAlliedTo(entity) && entity != this.getOwner() && entity != this && this.hasLineOfSight(entity)
            );
         if (!list.isEmpty()) {
            for (LivingEntity target : list) {
               this.doHurtTarget(target, 2.0F);
               target.setDeltaMovement(target.getDeltaMovement().add(this.getDeltaMovement()));
               target.hurtMarked = true;
            }
         }
      }
   }

   @Override
   public void handleFlying(Mob entity) {
      IFlying.super.handleFlying(entity);
      if (!this.level().isClientSide()) {
         LivingEntity controller = this.getControllingPassenger();
         SimpleContainer container = this.isChested() ? this.inventory : null;
         if (!this.isTame()) {
            this.breakBlocks(this, 1.0F, true, 0, container, true);
         } else if (controller != null && this.isOwnedBy(controller)) {
            this.breakBlocks(this, 1.0F, false, 0, container);
         }

         if (controller != null) {
            if (controller.getXRot() <= 20.0F) {
               this.digBlocks(this, 1.0F, 2, 1.0F, false, container);
            } else {
               this.digBlocks(this, 1.0F, 0, 1.0F, controller.getXRot() >= 40.0F, container);
            }
         }
      }
   }

   public void areaAttack(float multiplier, float strength, float range) {
      AABB aabb = this.getBoundingBox().inflate(range);
      List<LivingEntity> livingEntityList = this.level()
         .getEntitiesOfClass(
            LivingEntity.class, aabb, entity -> !this.isAlliedTo(entity) && entity != this.getOwner() && !entity.equals(this) && this.hasLineOfSight(entity)
         );
      if (!livingEntityList.isEmpty()) {
         for (LivingEntity target : livingEntityList) {
            this.doHurtTarget(target, multiplier);
            SkillHelper.knockBack(this, target, strength);
         }
      }
   }

   @Override
   public boolean isFood(ItemStack stack) {
      return stack.is(TensuraItemTags.SPIRIT_FOOD) || stack.has(DataComponents.FOOD);
   }

   public MegalodonEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      return null;
   }

   @Override
   public int getChestSlots() {
      return 30;
   }

   public boolean isNoGravity() {
      return super.isNoGravity() ? true : this.getControllingPassenger() != null && !this.onGround();
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, -0.25, 0.4 * f).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   @Override
   protected float getRiddenSpeed(Player player) {
      float speed = super.getRiddenSpeed(player);
      return this.getRollingTick() > 0 ? speed * 4.0F : speed;
   }

   @Override
   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
      if (this.isNoGravity()) {
         if (controller.jumping) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.1, 0.0));
         } else if (TensuraKeybinds.DODGE.isDown()) {
            this.descending(this, controller);
         }
      }
   }

   @Override
   public void mountAbility(Player rider) {
      this.triggerAnim("rollController", "roll");
      SkillHelper.riptidePush(this, 4.0F);
      this.setRollingTick(16);
      this.markHurt();
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.SWEEP_ATTACK, 2.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.SWEEP_ATTACK, 3.0);
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TRIDENT_RIPTIDE_3, TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return TensuraStorages.getExistenceFrom(this).getSpawnType() == MobSpawnType.MOB_SUMMONED ? false : super.removeWhenFarAway(pDistanceToClosestPlayer);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.megalodon, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   public static boolean checkMegalodonSpawnRules(
      EntityType<MegalodonEntity> fish, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      return pLevel.getDifficulty() != Difficulty.PEACEFUL && pPos.getY() > 90 && checkFlyingSpawnRules(fish, pLevel, pSpawnType, pPos, pRandom);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (!this.onGround()) {
         this.setFlying(true);
      }

      if (!this.isTame()) {
         OrbitMovement.setDefaultMeetingPoint(this, this.blockPosition());
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.MEGALODON_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return (SoundEvent)TensuraSoundEvents.MEGALODON_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.MEGALODON_DEATH.get();
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<MegalodonEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<MegalodonEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<MegalodonEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldAttack),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  new SubordinateFollowOwner().canTeleportOffGroundWhen(entity -> {
                     entity.setFlying(true);
                     return true;
                  }),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OrbitMovement().startCondition(entity -> !entity.isTame()),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{
                     new SetRandomFlyAndWalkTarget().startCondition(TamableAnimal::isTame), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
                  }
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<MegalodonEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf((entity, target) -> {
               boolean stop = entity.shouldStopTarget(entity, target);
               if (stop && !entity.isTame()) {
                  OrbitMovement.setDefaultMeetingPoint(entity, target.blockPosition());
               }

               return stop;
            }),
            new OrbitAttack()
               .speedMod((entity, target) -> 2.0F)
               .attackSpeedMod((entity, target) -> 3.0F)
               .orbitAttackInterval(entity -> 100 + entity.getRandom().nextInt(80))
               .orbitRadius((entity, target) -> 20.0)
               .orbitHeight((entity, target) -> 20.0)
               .shouldDoMeleeAttack((entity, target) -> {
                  if (entity.getRandom().nextFloat() <= 0.1F) {
                     entity.chosenAttack = 3;
                     return target.distanceTo(entity) < 10.0F;
                  } else if (entity.getRandom().nextBoolean()) {
                     entity.chosenAttack = 2;
                     return target.distanceTo(entity) < 6.0F;
                  } else {
                     entity.chosenAttack = 1;
                     return BehaviorUtils.isWithinAttackRange(entity, target, 1);
                  }
               })
               .performOrbitAttack(
                  (entity, target) -> {
                     switch (this.chosenAttack) {
                        case 2:
                           entity.triggerAnim("miscController", "fling");
                           entity.doHurtTarget(target, 1.5F);
                           SkillHelper.knockBack(this, target, 3.0F);
                           ManasCoreAttributeUtils.triggerCriticalAttackEffect(target, entity);
                           break;
                        case 3:
                           entity.triggerAnim("rollController", "roll");
                           entity.lookAt(Anchor.EYES, target.getEyePosition());
                           SkillHelper.riptidePush(entity, 4.0F);
                           entity.setRollingTick(16);
                           entity.markHurt();
                           TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 2.0);
                           TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 3.0);
                           entity.level()
                              .playSound(
                                 null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TRIDENT_RIPTIDE_3, TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F
                              );
                           break;
                        default:
                           entity.doHurtTarget(target);
                           entity.triggerAnim("miscController", "bite");
                     }
                  }
               )
               .onOrbitTick((entity, target, pair) -> {
                  if (pair.getFirst() != null && (Integer)pair.getSecond() > 40 && target.distanceTo(entity) < 10.0F && entity.getRandom().nextFloat() <= 0.1F) {
                     entity.triggerAnim("rollController", "take_off");
                     entity.areaAttack(1.5F, 2.5F, 4.0F);
                     TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.CLOUD, 3.0);
                     TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 3.0);
                     TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 4.0);
                     entity.level()
                        .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
                     return false;
                  } else {
                     return true;
                  }
               })
               .onTick(entity -> {
                  entity.setFlying(true);
                  return true;
               })
         }
      );
   }

   public boolean shouldAttack(LivingEntity entity) {
      if (entity == this) {
         return false;
      } else if (!entity.isAlive()) {
         return false;
      } else if (entity.hasInfiniteMaterials()) {
         return false;
      } else if (this.isAlliedTo(entity)) {
         return false;
      } else if (this.getOwner() == null) {
         return entity.getType().is(TensuraEntityTags.HELL_NEUTRAL) ? false : EnergyHelper.getMaxEP(entity) > 2000.0;
      } else if (entity.isAlliedTo(this.getOwner())) {
         return false;
      } else {
         return entity instanceof Mob mob
            ? mob.getTarget() == this.getOwner()
            : this.getOwner().getLastHurtMob() == entity || this.getOwner().getLastHurtByMob() == entity;
      }
   }

   protected PlayState loopController(AnimationState<MegalodonEntity> state) {
      String name;
      if (this.isInWaterRainOrBubble()) {
         if (state.isMoving()) {
            name = "animation.megalodon.swim";
         } else {
            name = "animation.megalodon.swim_idle";
         }
      } else if (state.isMoving()) {
         name = "animation.megalodon.fly";
      } else {
         name = "animation.megalodon.fly_idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 3, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.megalodon.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("fling", RawAnimation.begin().then("animation.megalodon.fling", LoopType.PLAY_ONCE)),
            new AnimationController(this, "rollController", 3, event -> PlayState.STOP)
               .triggerableAnim("roll", RawAnimation.begin().then("animation.megalodon.roll", LoopType.PLAY_ONCE))
               .triggerableAnim("take_off", RawAnimation.begin().then("animation.megalodon.take_off", LoopType.PLAY_ONCE))
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
