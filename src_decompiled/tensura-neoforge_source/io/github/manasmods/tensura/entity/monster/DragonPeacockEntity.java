package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.IGender;
import io.github.manasmods.tensura.entity.variant.PeacockVariant;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
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

public class DragonPeacockEntity
   extends TensuraTamableEntity
   implements SmartBrainOwner<DragonPeacockEntity>,
   GeoEntity,
   IFlying,
   VariantHolder<PeacockVariant>,
   IGender {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(DragonPeacockEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(DragonPeacockEntity.class, EntityDataSerializers.BOOLEAN);
   protected int flyingTick;
   protected boolean wasFlying;
   private boolean partyPeacock;
   @Nullable
   private BlockPos jukebox;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public DragonPeacockEntity(EntityType<? extends TamableAnimal> type, Level worldIn) {
      super(type, worldIn);
      this.initFlying(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 16.0)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.FLYING_SPEED, 0.6F)
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
      builder.define(DATA_ID_TYPE_VARIANT, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putInt("Variant", this.getTypeVariant());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(compound.getBoolean("Flying"));
      this.entityData.set(DATA_ID_TYPE_VARIANT, compound.getInt("Variant"));
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

   public PeacockVariant getVariant() {
      return PeacockVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(PeacockVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
   }

   @Override
   public boolean isMale() {
      return this.getVariant() == PeacockVariant.MALE;
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (DATA_FLAGS_ID.equals(pKey)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   @NotNull
   @Override
   public EntityDimensions getDefaultDimensions(Pose pPose) {
      EntityDimensions entitydimensions = super.getDefaultDimensions(pPose);
      return !(this.getVehicle() instanceof LivingEntity) && !this.isInSittingPose() ? entitydimensions : entitydimensions.scale(1.0F, 0.7F);
   }

   public DragonPeacockEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      DragonPeacockEntity peacock = (DragonPeacockEntity)((EntityType)MonsterEntityTypes.DRAGON_PEACOCK.get()).create(pLevel);
      if (peacock == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         peacock.setOwnerUUID(uuid);
         peacock.setTame(true, true);
      }

      return peacock;
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return !super.canMate(pOtherAnimal) ? false : ((DragonPeacockEntity)pOtherAnimal).getVariant() != this.getVariant();
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
      if (this.isPassenger() && this.getVehicle() instanceof LivingEntity living) {
         Vec3 vec3 = living.getDeltaMovement();
         this.setYRot(living.getYRot());
         if (!living.isInWaterOrBubble() && !living.isInLava() && living.getItemBySlot(EquipmentSlot.HEAD).is(TensuraItemTags.PEACOCK_SITTING_HELMETS)) {
            if (living.isShiftKeyDown()) {
               this.stopRiding();
               this.refreshDimensions();
               this.setPos(Vec3.atCenterOf(living.blockPosition()));
               LivingEntity target = ObjectSelectionHelper.getTargetingEntity(living, 32.0, true, false);
               if (target == null) {
                  return;
               }

               if (target.hasInfiniteMaterials()) {
                  return;
               }

               for (DragonPeacockEntity dragonPeacock : this.level()
                  .getEntitiesOfClass(DragonPeacockEntity.class, this.getBoundingBox().inflate(25.0), entity -> entity.isOwnedBy(living) && !entity.isBaby())) {
                  dragonPeacock.setOrderedToSit(Boolean.FALSE);
                  dragonPeacock.setTarget(target);
               }
            }
         } else {
            this.stopRiding();
            this.refreshDimensions();
            this.setPos(Vec3.atCenterOf(living.blockPosition()));
         }

         if (!living.onGround() && !this.isBaby() && vec3.y() < 0.0) {
            living.setDeltaMovement(vec3.multiply(1.0, 0.65, 1.0));
            living.resetFallDistance();
         }
      }
   }

   @Override
   public void aiStep() {
      if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 3.46) || !this.level().getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
         this.partyPeacock = false;
         this.jukebox = null;
      }

      super.aiStep();
   }

   public void setRecordPlayingNearby(BlockPos pPos, boolean pIsPartying) {
      this.jukebox = pPos;
      this.partyPeacock = pIsPartying;
   }

   public void setTarget(@Nullable LivingEntity entity) {
      super.setTarget(entity);
      if (this.getTarget() != null) {
         this.triggerAnim("miscController", "threaten");
      }
   }

   @Override
   public boolean isFood(ItemStack stack) {
      return stack.is(TensuraItemTags.PEACOCK_FOOD);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.PEACOCK_TAMING_FOOD);
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (this.isTame() && this.isOwnedBy(player)) {
         if (player.getFirstPassenger() == null
            && !player.isSecondaryUseActive()
            && stack.isEmpty()
            && player.getItemBySlot(EquipmentSlot.HEAD).is(TensuraItemTags.PEACOCK_SITTING_HELMETS)) {
            this.startRiding(player, true);
            this.getNavigation().stop();
            this.setTarget(null);
            this.refreshDimensions();
            return InteractionResult.sidedSuccess(this.level().isClientSide());
         } else {
            this.cycleCommands(this, player);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.dragonPeacock, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   public static boolean checkPeacockSpawnRules(
      EntityType<DragonPeacockEntity> pPeacock, ServerLevelAccessor pLevel, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource
   ) {
      return Animal.isBrightEnoughToSpawn(pLevel, blockPos) && blockPos.getY() > 40;
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (!this.onGround()) {
         this.setFlying(true);
      }

      this.setVariant(PeacockVariant.byId(this.random.nextInt(2)));
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.BIRD_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return (SoundEvent)TensuraSoundEvents.BIRD_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.BIRD_DEATH.get();
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

   public List<ExtendedSensor<DragonPeacockEntity>> getSensors() {
      return ObjectArrayList.of(
         new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new ItemTemptingSensor().temptedWith(DragonPeacockEntity::isFood)}
      );
   }

   public BrainActivityGroup<DragonPeacockEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<DragonPeacockEntity> getIdleTasks() {
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

   public BrainActivityGroup<DragonPeacockEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new AnimatableMeleeAttack(0).attackInterval(entity -> 1).whenStarting(entity -> {
               if (!entity.isFlying()) {
                  entity.triggerAnim("miscController", "eat");
               }
            })
         }
      );
   }

   protected PlayState loopController(AnimationState<DragonPeacockEntity> state) {
      String name;
      if (this.isPassenger()
         && this.getVehicle() instanceof LivingEntity living
         && this.getOwnerUUID() != null
         && Objects.equals(living.getUUID(), this.getOwnerUUID())) {
         if ((living.onGround() || living.getDeltaMovement().y() == 0.0) && !living.isSprinting()) {
            name = "animation.dragon_peacock.sit";
         } else {
            name = "animation.dragon_peacock.glide";
         }
      } else if (this.onGround()) {
         if (this.isInSittingPose() && !this.isPartyPeacock()) {
            name = "animation.dragon_peacock.sit";
         } else if (state.isMoving()) {
            name = "animation.dragon_peacock.walk";
         } else if (this.isPartyPeacock()) {
            name = "animation.dragon_peacock.dance";
         } else {
            name = "animation.dragon_peacock.idle";
         }
      } else {
         name = "animation.dragon_peacock.fly";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 5, this::loopController),
            new AnimationController(this, "miscController", 3, event -> {
                  this.swinging = false;
                  return PlayState.STOP;
               })
               .triggerableAnim("eat", RawAnimation.begin().then("animation.dragon_peacock.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("threaten", RawAnimation.begin().then("animation.dragon_peacock.threaten", LoopType.PLAY_ONCE))
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

   @Generated
   public boolean isPartyPeacock() {
      return this.partyPeacock;
   }
}
