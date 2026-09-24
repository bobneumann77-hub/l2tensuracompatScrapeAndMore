package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetDigTargetToAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomDigTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.NearbySeeThroughEntitySensor;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IDigging;
import io.github.manasmods.tensura.entity.template.subclass.IGiantMob;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Generated;
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
import net.minecraft.tags.FluidTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ArmorsaurusEntity
   extends TensuraMountEntity
   implements GeoEntity,
   SmartBrainOwner<ArmorsaurusEntity>,
   IDigging<ArmorsaurusEntity>,
   IGiantMob,
   ITensuraMount {
   private static final EntityDataAccessor<Boolean> DIGGING = SynchedEntityData.defineId(ArmorsaurusEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int diggingTick = 0;
   private boolean landNavigating = false;
   private int mountAbilityTick = 0;

   public ArmorsaurusEntity(EntityType<? extends ArmorsaurusEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.switchNavigator(this, true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 20.0)
         .add(Attributes.MAX_HEALTH, 80.0)
         .add(Attributes.ATTACK_DAMAGE, 16.0)
         .add(Attributes.MOVEMENT_SPEED, 0.175F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.7F)
         .add(Attributes.JUMP_STRENGTH, 1.0)
         .add(Attributes.SAFE_FALL_DISTANCE, 8.0)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.1F);
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
      builder.define(DIGGING, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Digging", this.isDigging());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setDigging(compound.getBoolean("Digging"));
   }

   @Override
   public boolean isDigging() {
      return (Boolean)this.entityData.get(DIGGING);
   }

   @Override
   public void setDigging(boolean b) {
      this.entityData.set(DIGGING, b);
   }

   @Override
   public int getChestSlots() {
      return 45;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.CACTUS) || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.HOT_FLOOR) || super.isInvulnerableTo(source);
   }

   @NotNull
   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(1.0, 1.0, 1.0);
   }

   @Override
   public EntityDimensions getSleepingDimensions(Pose pPose) {
      return this.getType().getDimensions().scale(this.getAgeScale() * 0.75F);
   }

   @NotNull
   public Vec3 collide(Vec3 vec3) {
      return !this.isDigging() ? super.collide(vec3) : this.getAllowedMovementForEntity(this, vec3);
   }

   public boolean isColliding(BlockPos pos, BlockState blockstate) {
      return (!this.isDigging() || this.canDigBlock(blockstate)) && super.isColliding(pos, blockstate);
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   @Override
   protected boolean canGoToSleep() {
      return super.canGoToSleep() && !this.isDigging();
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.armorsaurus, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @Override
   public void tick() {
      super.tick();
      this.handleDigging(this);
      if (!this.level().isClientSide()) {
         if (this.mountAbilityTick > 0 && --this.mountAbilityTick >= 20) {
            this.playSound(SoundEvents.ROOTED_DIRT_BREAK);
            TensuraParticleHelper.spawnServerGroundSlamParticle(this, 5, 4.0F);
            TensuraParticleHelper.spawnServerGroundSlamParticle(this, 5, 2.0F);
            this.setPos(this.position().add(0.0, -0.1F, 0.0));
         }

         LivingEntity controller = this.getControllingPassenger();
         if (!this.isTame() || controller != null && this.isOwnedBy(controller)) {
            SimpleContainer container = this.isChested() ? this.inventory : null;
            this.breakBlocks(this, 1.0F, false, container);
         }
      }
   }

   public void onDigUp(ArmorsaurusEntity entity) {
      entity.setPos(entity.position().add(0.0, 2.0, 0.0));
      if (this.getControllingPassenger() != null) {
         entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.5, 0.0));
         entity.triggerAnim("digController", "dig_up");
         entity.areaAttack(2.0, 1.5F, 5.0F);
         TensuraParticleHelper.spawnServerParticles(
            this.level(), TensuraParticleUtils.getColorlessWave(0.9F, 3.0F), entity.getX(), entity.getY() + entity.getBbHeight() + 0.2F, entity.getZ()
         );
         entity.level()
            .playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(),
               TensuraSkill.ABILITY_SOUND,
               0.75F,
               1.0F
            );
      } else {
         entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.4, 0.0));
         LivingEntity target = entity.getTarget();
         if (target != null && entity.isWithinMeleeAttackRange(target)) {
            entity.triggerAnim("digController", "dig_up");
            entity.areaAttack(2.0, 1.5F, 4.0F);
            TensuraParticleHelper.spawnServerParticles(
               this.level(), TensuraParticleUtils.getColorlessWave(0.9F, 3.0F), entity.getX(), entity.getY() + entity.getBbHeight() + 0.2F, entity.getZ()
            );
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(),
                  TensuraSkill.ABILITY_SOUND,
                  0.75F,
                  1.0F
               );
         } else {
            entity.triggerAnim("digController", "dig_up_flip");
         }
      }

      entity.playSound(SoundEvents.ROOTED_DIRT_BREAK);
      TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 5, 4.0F);
      TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 5, 2.0F);
   }

   public void areaAttack(double multiplier, float strength, float range) {
      AABB aabb = this.getBoundingBox().inflate(range);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            aabb,
            entity -> !entity.isAlliedTo(this)
               && entity != this.getOwner()
               && !entity.equals(this)
               && (!(entity instanceof ArmorsaurusEntity) || entity == this.getTarget())
         );
      if (!list.isEmpty()) {
         for (LivingEntity target : list) {
            target.hurt(this.damageSources().mobAttack(this), (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier));
            SkillHelper.knockBack(this, target, strength);
         }
      }
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAbilityTick <= 0 && !this.isDigging()) {
         if (this.isSafeDig(this, this.level(), this.blockPosition())) {
            this.mountAbilityTick = 50;
            this.triggerAnim("digController", "dig_down");
            this.setDigging(true);
            this.hurtMarked = true;
         }
      }
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      ArmorsaurusEntity entity = (ArmorsaurusEntity)((EntityType)MonsterEntityTypes.ARMORSAURUS.get()).create(pLevel);
      if (entity == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         entity.setOwnerUUID(uuid);
         entity.setTame(true, true);
      }

      return entity;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.has(DataComponents.FOOD);
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "bite");
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, 0.0, 0.25 * f).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   @Override
   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
      Vec3 riddenInput = this.getRiddenInput(controller, vec3);
      if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > this.getFluidJumpThreshold() && riddenInput.z() > 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.03, 0.0));
      }

      if (this.isDigging() && this.mountAbilityTick <= 0) {
         if (controller.jumping) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.07, 0.0));
         } else if (TensuraKeybinds.DODGE.isDown()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.07, 0.0));
         }
      }
   }

   public boolean shouldSurface(ArmorsaurusEntity entity) {
      if (entity.isDigging() && entity.getDiggingTick() > 300) {
         return true;
      }

      LivingEntity target = entity.getTarget();
      if (target != null && !target.isInWall()) {
         return true;
      }

      LivingEntity owner = entity.getOwner();
      return owner != null && owner.distanceTo(entity) > 10.0F;
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.ARMORSAURUS_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.ARMORSAURUS_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.ARMORSAURUS_DEATH.get();
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

   public List<ExtendedSensor<ArmorsaurusEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbySeeThroughEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<ArmorsaurusEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<ArmorsaurusEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this),
                  new SubordinateFollowOwner().speedMod(1.5F),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{
                     new SetRandomDigTarget().digInterval(entity -> 100).delayDigging((entity, pos) -> 45).whenStarting(entity -> {
                        if (!entity.isDigging()) {
                           entity.triggerAnim("digController", "dig_down");
                        }
                     }).startCondition(entity -> !entity.isTame() || entity.isWandering()),
                     new SetRandomWalkTarget().startCondition(entity -> !entity.isDigging()),
                     new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
                  }
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<ArmorsaurusEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetDigTargetToAttackTarget(45)
               .digInterval(entity -> 80)
               .onStartDigging((entity, target) -> entity.triggerAnim("digController", "dig_down"))
               .shouldDigAttack(ArmorsaurusEntity::shouldDigAttack)
               .onDiggingDelay((entity, target, tick) -> {
                  if (tick <= 20) {
                     TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 10, 4.0F);
                     TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 10, 2.0F);
                     entity.playSound(SoundEvents.ROOTED_DIRT_BREAK);
                  }
               })
               .speedMod((owner, target) -> owner.isDigging() ? 3.0F : 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(15)
                     .maxAttackRadius(8.0F)
                     .attackInterval(entity -> 80)
                     .performAttack(
                        (entity, target) -> {
                           entity.areaAttack(2.0, 2.5F, 4.0F);
                           TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 2.0);
                           TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 3.0);
                           entity.level()
                              .playSound(
                                 null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, TensuraSkill.ABILITY_SOUND, 1.5F, 1.0F
                              );
                        }
                     )
                     .whenStarting(entity -> entity.triggerAnim("miscController", "tail_swing"))
                     .startCondition(entity -> !entity.isDigging() && entity.getRandom().nextInt(10) == 1),
                  new CustomRangeAttack(15)
                     .maxAttackRadius(4.0F)
                     .attackInterval(entity -> 40)
                     .performAttack(
                        (entity, target) -> {
                           entity.areaAttack(1.5, 1.0F, 3.0F);
                           TensuraParticleHelper.spawnServerGroundSlamParticle(this, 5, 4.0F);
                           TensuraParticleHelper.spawnServerGroundSlamParticle(this, 5, 2.0F);
                           TensuraParticleHelper.spawnServerParticles(
                              this.level(), TensuraParticleUtils.getColorlessWave(0.9F, 3.0F), this.getX(), this.getY() + 0.2F, this.getZ()
                           );
                           entity.level()
                              .playSound(
                                 null,
                                 entity.getX(),
                                 entity.getY(),
                                 entity.getZ(),
                                 (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 1.5F,
                                 1.0F
                              );
                        }
                     )
                     .whenStarting(entity -> entity.triggerAnim("miscController", "tail_slam"))
                     .startCondition(entity -> !entity.isDigging() && entity.getRandom().nextInt(10) == 1),
                  new CustomRangeAttack(0)
                     .maxAttackRadius(4.0F)
                     .attackInterval(entity -> 0)
                     .requireInSight(false)
                     .performAttack(
                        (entity, target) -> TensuraParticleHelper.spawnServerParticles(
                           this.level(), TensuraParticleUtils.getColorlessWave(0.9F, 3.0F), this.getX(), this.getY() + 1.2F, this.getZ()
                        )
                     )
                     .whenStarting(entity -> entity.onDigUp(entity))
                     .startCondition(IDigging::isDigging),
                  new AnimatableMeleeAttack(5)
                     .attackInterval(entity -> 1)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "bite"))
                     .startCondition(entity -> !entity.isDigging())
               }
            )
         }
      );
   }

   private boolean shouldDigAttack(LivingEntity target) {
      if (!this.isDigging() && !this.isVehicle()) {
         return target instanceof IDigging<?> digging && digging.isDigging() ? true : this.distanceTo(target) > 10.0F && this.getRandom().nextFloat() < 0.2F;
      } else {
         return false;
      }
   }

   protected PlayState loopController(AnimationState<ArmorsaurusEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.armorsaurus.sleep";
      } else if (this.isInSittingPose()) {
         name = "animation.armorsaurus.stay";
      } else if (this.isInLiquid() || this.isDigging()) {
         name = "animation.armorsaurus.swim";
      } else if (state.isMoving()) {
         name = "animation.armorsaurus.walk";
      } else {
         name = "animation.armorsaurus.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.armorsaurus.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("eat", RawAnimation.begin().then("animation.armorsaurus.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("tail_slam", RawAnimation.begin().then("animation.armorsaurus.tail_slam", LoopType.PLAY_ONCE))
               .triggerableAnim("tail_swing", RawAnimation.begin().then("animation.armorsaurus.tail_swing", LoopType.PLAY_ONCE)),
            new AnimationController(this, "digController", 3, event -> PlayState.STOP)
               .triggerableAnim("dig_down", RawAnimation.begin().then("animation.armorsaurus.dig_down", LoopType.PLAY_ONCE))
               .triggerableAnim("dig_up", RawAnimation.begin().then("animation.armorsaurus.dig_up", LoopType.PLAY_ONCE))
               .triggerableAnim("dig_up_flip", RawAnimation.begin().then("animation.armorsaurus.dig_up_flip", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   @Override
   public int getDiggingTick() {
      return this.diggingTick;
   }

   @Generated
   @Override
   public void setDiggingTick(int diggingTick) {
      this.diggingTick = diggingTick;
   }

   @Generated
   @Override
   public boolean isLandNavigating() {
      return this.landNavigating;
   }

   @Generated
   @Override
   public void setLandNavigating(boolean landNavigating) {
      this.landNavigating = landNavigating;
   }
}
