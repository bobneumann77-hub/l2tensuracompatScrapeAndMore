package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.WaterJumpAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.JumpOutOfWater;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.SwimToWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetSwimTargetToAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IGiantMob;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.entity.template.subclass.ISwimmingJumper;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomSwimTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
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

public class SissieEntity extends TensuraMountEntity implements GeoEntity, SmartBrainOwner<SissieEntity>, ITensuraMount, ISwimmingJumper, IGiantMob {
   private static final EntityDataAccessor<Integer> CHASE_MODE = SynchedEntityData.defineId(SissieEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int flopTick = 0;
   private int mountAbilityTick = 0;

   public SissieEntity(EntityType<? extends SissieEntity> type, Level level) {
      super(type, level);
      this.initSwimming(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 24.0)
         .add(Attributes.MAX_HEALTH, 100.0)
         .add(Attributes.MOVEMENT_SPEED, 0.35F)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.4F)
         .add(Attributes.JUMP_STRENGTH, 2.5)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.5)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 8.0);
   }

   @Override
   public void switchLookControl(LookControl control) {
      this.lookControl = control;
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
      builder.define(CHASE_MODE, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("ChaseMode", this.getChaseMode());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setChaseMode(compound.getInt("ChaseMode"));
   }

   public int getChaseMode() {
      return (Integer)this.entityData.get(CHASE_MODE);
   }

   public void setChaseMode(int mode) {
      this.entityData.set(CHASE_MODE, mode);
   }

   @Override
   public int getMenuRenderSize() {
      return 4;
   }

   @Override
   public int getChestSlots() {
      return 45;
   }

   public int getMaxAirSupply() {
      return 600;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
      return levelReader.getFluidState(blockPos).is(FluidTags.WATER)
         ? 10.0F + levelReader.getPathfindingCostFromLightLevels(blockPos)
         : super.getWalkTargetValue(blockPos, levelReader);
   }

   @Override
   public boolean breakableBlocks(LivingEntity entity, BlockPos pos, BlockState state) {
      return state.is(TensuraBlockTags.BREAKABLE_BY_MONSTER) ? true : state.is(BlockTags.SAND) || state.is(Blocks.GRAVEL);
   }

   public void baseTick() {
      int i = this.getAirSupply();
      super.baseTick();
      this.handleAirSupply(this, i);
   }

   @Override
   public void tick() {
      super.tick();
      this.spawnSwimmingParticle(this);
      if (!this.level().isClientSide()) {
         if (this.getChaseMode() != 0) {
            if (!this.isInWaterOrBubble()) {
               this.setChaseMode(0);
               this.mountAbilityTick = 0;
               return;
            }

            if (this.mountAbilityTick-- > 0 && this.mountAbilityTick != -1) {
               if (this.mountAbilityTick == 0) {
                  this.setChaseMode(0);
                  this.mountAbilityTick = -1;
                  return;
               }

               int internal = this.getChaseMode() == 2 ? 10 : 20;
               if (this.mountAbilityTick % internal == 5) {
                  this.playSound(this.getChaseMode() == 2 ? SoundEvents.PLAYER_ATTACK_SWEEP : (SoundEvent)TensuraSoundEvents.BIG_BITE.get(), 3.0F, 1.0F);
                  double radius = this.getChaseMode() == 2 ? 3.0 : 0.0;
                  Vec3 vec3 = new Vec3(this.getLookAngle().x(), 0.0, this.getLookAngle().z());
                  AABB aabb = this.getBoundingBox().move(vec3.scale(radius + (this.isBaby() ? 4 : 8)));

                  for (LivingEntity living : this.level()
                     .getEntitiesOfClass(
                        LivingEntity.class, aabb.inflate(radius), livingx -> !livingx.is(this) && livingx.getVehicle() != this && livingx.isAlive()
                     )) {
                     this.doHurtTarget(living, this.getChaseMode() == 2 ? 1.0F : 1.5F);
                  }
               }
            }

            LivingEntity controller = this.getControllingPassenger();
            if (!this.isTame() || controller != null && this.isOwnedBy(controller)) {
               this.breakBlocks(this, 1.0F, true);
            }
         }
      }
   }

   @Override
   public void aiStep() {
      this.handleFlopping(this);
      super.aiStep();
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.getChaseMode() == 0) {
         this.mountAbilityTick = 200;
         this.setChaseMode(1);
      }
   }

   @Override
   public void mountAbilityRelease(LivingEntity rider) {
      if (this.getChaseMode() != 0) {
         this.mountAbilityTick = -1;
         this.setChaseMode(0);
      }
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      SissieEntity fish = (SissieEntity)((EntityType)MonsterEntityTypes.SISSIE.get()).create(pLevel);
      if (fish == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         fish.setOwnerUUID(uuid);
         fish.setTame(true, true);
      }

      return fish;
   }

   @Override
   protected float getRiddenSpeed(Player player) {
      float speed = super.getRiddenSpeed(player);
      return this.getChaseMode() == 0 ? speed : speed * 1.5F;
   }

   @Override
   protected boolean canExecuteRidersJump() {
      return this.isInWaterOrBubble() && this.canJumpOutOfWater(this, 8, 1, false);
   }

   @Override
   public void onPlayerJump(int i) {
      boolean saddled = !this.isSaddleRequired() || this.isSaddled();
      if (saddled && this.canExecuteRidersJump()) {
         if (i < 0) {
            i = 0;
         }

         if (i >= 90) {
            this.playerJumpPendingScale = 1.0F;
            this.triggerAnim("jumpController", "jump");
         } else {
            this.playerJumpPendingScale = 0.4F + 0.4F * i / 90.0F;
         }
      }
   }

   @Override
   public int getRiderSeats() {
      return 6;
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      int i = Math.max(this.getPassengers().indexOf(entity), 0);

      Vec3 vec3 = switch (i) {
         case 1 -> new Vec3(1.5, 0.0, -2.0);
         case 2 -> new Vec3(-1.5, 0.0, -2.0);
         case 3 -> new Vec3(1.5, 0.0, 0.0);
         case 4 -> new Vec3(-1.5, 0.0, 0.0);
         case 5 -> new Vec3(0.0, 0.0, -1.0);
         default -> new Vec3(0.0, 0.0, 1.0);
      };
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3.yRot(-this.getYRot() * 0.0174F));
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public boolean checkSpawnObstruction(LevelReader levelReader) {
      return levelReader.isUnobstructed(this);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.sissie, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   public static boolean checkSissieSpawnRules(EntityType<SissieEntity> type, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource source) {
      return pos.getY() > 30 && pos.getY() < level.getSeaLevel() ? level.getFluidState(pos).is(FluidTags.WATER) : false;
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.SISSIE_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.SISSIE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.SISSIE_DEATH.get();
   }

   @NotNull
   protected SoundEvent getSwimSound() {
      return SoundEvents.FISH_SWIM;
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @Override
   protected void playJumpSound() {
      this.playSound((SoundEvent)TensuraSoundEvents.GENERIC_WATER_JUMP.get(), 0.4F, 1.0F);
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<SissieEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<SissieEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new LookAtTarget(),
            new SwimToWalkTarget().cooldownFor(entity -> 0).startCondition(entity -> !entity.isOrderedToSit()).stopIf(ISubordinate::isOrderedToSit)
         }
      );
   }

   public BrainActivityGroup<SissieEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(
                     this,
                     target -> this.shouldAttack().test(target) || this.distanceToSqr(target) <= 36.0,
                     TensuraBehaviourHelper.getAnimalPreyPredicate(this)
                  ),
                  new SubordinateFollowOwner().speedMod(1.5F).canTeleportOffGroundWhen(entity -> true),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
               new ExtendedBehaviour[]{
                  new JumpOutOfWater()
                     .jumpInterval(entity -> 200)
                     .jumpStrength(entity -> entity.getJumpPower(0.8F))
                     .onJump(entity -> entity.triggerAnim("jumpController", "jump"))
                     .startCondition(entity -> !entity.isSleeping() && !entity.isOrderedToSit()),
                  new SetRandomSwimTarget().setRadius(20.0).speedModifier(1.5F).cooldownFor(entity -> 0),
                  new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
               }
            )
         }
      );
   }

   public BrainActivityGroup<SissieEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget()
               .invalidateIf((entity, target) -> entity.shouldStopTarget(entity, target) || !entity.shouldAttack().test(target)),
            new SetSwimTargetToAttackTarget().speedMod((owner, target) -> owner.getChaseMode() != 0 ? 3.0F : 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new WaterJumpAttack(1)
                     .jumpStrength(entity -> entity.getJumpPower(1.0F))
                     .onJump(entity -> entity.triggerAnim("jumpController", "jump"))
                     .minAttackRadius(3.0F)
                     .maxAttackRadius(10.0F)
                     .attackInterval(entity -> 60)
                     .performAttack((entity, target) -> entity.doHurtTarget(target, 2.0F))
                     .startCondition(sissie -> sissie.getControllingPassenger() == null && sissie.getChaseMode() == 0 && sissie.getRandom().nextInt(10) == 1),
                  new CustomHeldAttack()
                     .minAttackRadius(5.0F)
                     .maxAttackRadius(64.0F)
                     .attackInterval(entity -> 200)
                     .onTick(
                        (entity, target, time) -> {
                           if (!entity.isInWaterOrBubble()) {
                              return false;
                           }

                           if (target != null) {
                              BehaviorUtils.lookAtEntity(entity, target);
                              if (time % 20 == 0) {
                                 entity.playSound(
                                    entity.getChaseMode() == 2 ? SoundEvents.PLAYER_ATTACK_SWEEP : (SoundEvent)TensuraSoundEvents.BIG_BITE.get(), 3.0F, 1.0F
                                 );
                                 double radius = entity.getChaseMode() == 2 ? 1.0 : 0.0;
                                 AABB aabb = entity.getBoundingBox().move(this.getViewVector(1.0F).scale(entity.isBaby() ? 2.0 : 4.0)).inflate(radius);

                                 for (LivingEntity living : entity.level()
                                    .getEntitiesOfClass(
                                       LivingEntity.class, aabb, livingx -> !livingx.is(entity) && livingx.getVehicle() != entity && livingx.isAlive()
                                    )) {
                                    entity.doHurtTarget(living, entity.getChaseMode() == 2 ? 1.5F : 1.0F);
                                    if (living == target) {
                                       return false;
                                    }
                                 }
                              }
                           }

                           return time <= 200;
                        }
                     )
                     .whenStarting(entity -> {
                        entity.setChaseMode(entity.getRandom().nextInt(5) == 1 ? 2 : 1);
                        entity.mountAbilityTick = -1;
                     })
                     .whenStopping(entity -> entity.setChaseMode(0))
                     .startCondition(sissie -> sissie.getControllingPassenger() == null && sissie.getChaseMode() == 0),
                  new AnimatableMeleeAttack(10).attackInterval(entity -> 1).whenStarting(entity -> {
                     entity.triggerAnim("miscController", "bite");
                     entity.playSound((SoundEvent)TensuraSoundEvents.BIG_BITE.get(), 3.0F, 1.0F);
                  }).startCondition(sissie -> sissie.getChaseMode() == 0)
               }
            )
         }
      );
   }

   private Predicate<LivingEntity> shouldAttack() {
      return target -> target.isInWaterOrBubble() || !target.onGround();
   }

   protected PlayState loopController(AnimationState<SissieEntity> state) {
      String name;
      if (this.isInWaterOrBubble()) {
         if (this.getChaseMode() == 2) {
            name = "animation.sissie.chase_thrash";
         } else if (this.getChaseMode() == 1) {
            name = "animation.sissie.chase_bite";
         } else if (state.isMoving()) {
            if (!this.isAngry() && !this.isSprinting()) {
               name = "animation.sissie.swim";
            } else {
               name = "animation.sissie.swim_fast";
            }
         } else {
            name = "animation.sissie.idle";
         }
      } else if (this.shouldFlop()) {
         name = "animation.sissie.flop";
      } else {
         name = "animation.sissie.fall";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 0, event -> PlayState.STOP)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.sissie.bite", LoopType.PLAY_ONCE)),
            new AnimationController(this, "jumpController", 0, event -> PlayState.STOP)
               .triggerableAnim("jump", RawAnimation.begin().then("animation.sissie.jump_attack", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   @Override
   public int getFlopTick() {
      return this.flopTick;
   }

   @Generated
   @Override
   public void setFlopTick(int flopTick) {
      this.flopTick = flopTick;
   }
}
