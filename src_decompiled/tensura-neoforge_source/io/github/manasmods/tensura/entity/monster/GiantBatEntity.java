package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.OrbitAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.BatHangUnderRoof;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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

public class GiantBatEntity extends TensuraMountEntity implements GeoEntity, SmartBrainOwner<GiantBatEntity>, IFlying {
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(GiantBatEntity.class, EntityDataSerializers.BOOLEAN);
   protected int flyingTick;
   protected boolean wasFlying;
   private boolean validHangingPos = false;
   private int checkHangingTime;
   private BlockPos prevHangPos;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public GiantBatEntity(EntityType<? extends GiantBatEntity> type, Level level) {
      super(type, level);
      this.initFlying(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 45.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.FLYING_SPEED, 0.7F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
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
   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (DATA_FLAGS_ID.equals(pKey)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   @NotNull
   public AABB getBoundingBoxForCulling() {
      double scale = this.isBaby() ? 1.5 : 3.0;
      return this.getBoundingBox().inflate(scale, scale, scale);
   }

   @NotNull
   @Override
   public EntityDimensions getDefaultDimensions(Pose pPose) {
      EntityDimensions entitydimensions = super.getDefaultDimensions(pPose);
      return this.isInSittingPose() && !this.isSleeping() ? entitydimensions.scale(1.0F, 1.5F) : entitydimensions;
   }

   @Override
   public EntityDimensions getSleepingDimensions(Pose pPose) {
      return this.getType().getDimensions().scale(this.getAgeScale());
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
   }

   @Override
   protected void sleepHandler() {
      if (this.isSleeping()) {
         BlockPos above = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getBoundingBox().maxY + 0.1F), Mth.floor(this.getZ()));
         if (this.checkHangingTime-- < 0 || this.random.nextFloat() < 0.1F || !Objects.equals(this.prevHangPos, above)) {
            this.validHangingPos = this.canHangFrom(above, this.level().getBlockState(above));
            this.checkHangingTime = 5 + this.random.nextInt(5);
            this.prevHangPos = above;
         }

         if (this.validHangingPos) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.1F, 0.3F, 0.1F).add(0.0, 0.08, 0.0));
         } else {
            this.setSleeping(false);
            this.setFlying(true);
         }
      } else {
         this.validHangingPos = false;
         this.prevHangPos = null;
      }
   }

   public boolean canHangFrom(BlockPos pos, BlockState state) {
      if (!this.level().isEmptyBlock(pos.below())) {
         return false;
      } else if (!this.level().isEmptyBlock(pos.below(2))) {
         return false;
      } else {
         return !state.isFaceSturdy(this.level(), pos, Direction.DOWN) ? false : !this.level().isEmptyBlock(pos.above());
      }
   }

   @Override
   public boolean doHurtTarget(Entity entity) {
      float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
      DamageSource damageSource = entity.getType().is(TensuraEntityTags.NO_BLOOD)
         ? this.damageSources().mobAttack(this)
         : TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.BLOOD_DRAIN, this);
      if (this.level() instanceof ServerLevel serverLevel) {
         f = EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), entity, damageSource, f);
      }

      if (entity.hurt(damageSource, f)) {
         if (!entity.getType().is(TensuraEntityTags.NO_BLOOD)) {
            this.heal(f);
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, DustParticleOptions.REDSTONE);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }

         if (this.level() instanceof ServerLevel serverLevel2) {
            EnchantmentHelper.doPostAttackEffects(serverLevel2, entity, damageSource);
         }

         this.setLastHurtMob(entity);
         this.playAttackSound();
         return true;
      } else {
         return false;
      }
   }

   public void ultrasonicWave(LivingEntity target) {
      Level level = this.level();
      Vec3 targetPos = target.getEyePosition();
      Vec3 source = this.position().add(0.0, this.getEyeHeight(), 0.0);
      Vec3 sourceToTarget = targetPos.subtract(source);
      Vec3 normalizes = sourceToTarget.normalize();
      level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_SONIC_BOOM, TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);

      for (int particleIndex = 1; particleIndex < Mth.floor(sourceToTarget.length()); particleIndex++) {
         Vec3 particlePos = source.add(normalizes.scale(particleIndex));
         ((ServerLevel)level).sendParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
         AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos)).inflate(2.0);
         List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, livingx -> !livingx.is(this));
         if (!list.isEmpty()) {
            for (LivingEntity living : list) {
               DamageSource damagesource = this.damageSources()
                  .sonicBoom(this)
                  .tensura$setMagiculeCost(30.0)
                  .tensura$setAbilityMode(0)
                  .tensura$setAbilityInstance(SkillUtils.getSkillOrNull(this, (ManasSkill)IntrinsicSkills.ULTRASONIC_WAVES.get()));
               living.hurt(damagesource, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F);
               double d0 = Math.max(0.0, 1.0 - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
               Vec3 vec3 = this.getViewVector(1.0F).normalize().scale(2.0 * d0 * 0.1F);
               if (vec3.lengthSqr() > 0.0) {
                  living.push(vec3.x, vec3.y, vec3.z);
               }
            }
         }
      }
   }

   @Override
   public boolean isSaddleable() {
      return false;
   }

   @Override
   public boolean hasSaddleSlot() {
      return false;
   }

   public GiantBatEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      GiantBatEntity bat = (GiantBatEntity)((EntityType)MonsterEntityTypes.GIANT_BAT.get()).create(pLevel);
      if (bat == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         bat.setOwnerUUID(uuid);
         bat.setTame(true, true);
      }

      return bat;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   @Override
   public InteractionResult getRidingInteraction(Player player, InteractionHand hand) {
      if (this.getChestsAllowed() > 0) {
         ItemStack itemstack = player.getItemInHand(hand);
         if (itemstack.is(Items.CHEST) && this.getChests() < this.getChestsAllowed()) {
            this.equipChest(player, itemstack);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
         }

         if (this.getChests() > 0 && itemstack.is(Items.SHEARS)) {
            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F);
            this.dropChestEach();
            return InteractionResult.sidedSuccess(this.level().isClientSide());
         }
      }

      if (!player.isSecondaryUseActive() && this.isChested()) {
         this.openCustomInventoryScreen(player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.giantBat, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      if (reason == MobSpawnType.NATURAL && this.getY() <= 50.0) {
         this.doSpawnPose(worldIn);
      }

      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   public static boolean checkBatSpawnRules(
      EntityType<GiantBatEntity> pType, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      if (pLevel.getDifficulty() == Difficulty.PEACEFUL) {
         return false;
      }

      if (!checkMobSpawnRules(pType, pLevel, pSpawnType, pPos, pRandom)) {
         return false;
      }

      if (pPos.getY() > 50) {
         return true;
      }

      MutableBlockPos above = new MutableBlockPos();
      above.set(pPos);
      int k = 0;

      while (pLevel.isEmptyBlock(above) && above.getY() < pLevel.getMaxBuildHeight()) {
         above.move(0, 1, 0);
         if (++k > 5) {
            return true;
         }
      }

      return false;
   }

   private void doSpawnPose(LevelAccessor level) {
      BlockPos above = this.blockPosition();
      int upBy = 100;

      for (int k = 0; level.isEmptyBlock(above) && above.getY() < this.level().getMaxBuildHeight() && k < upBy; k++) {
         above = above.above();
      }

      if (level.isEmptyBlock(above)) {
         this.setFlying(true);
      } else {
         this.setSleeping(true);
      }

      this.setPos(above.getX() + 0.5F, above.getY() - this.getBoundingBox().getYsize(), above.getZ() + 0.5F);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.BAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.BAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BAT_DEATH;
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

   public List<ExtendedSensor<GiantBatEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<GiantBatEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<GiantBatEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false),
                  new SubordinateFollowOwner().canTeleportOffGroundWhen(entity -> {
                     entity.setSleeping(false);
                     entity.setFlying(true);
                     return true;
                  }),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
               new ExtendedBehaviour[]{
                  new BatHangUnderRoof()
                     .canHangOn(GiantBatEntity::canHangFrom)
                     .setHanging((entity, pos) -> entity.setSleeping(true))
                     .startCondition(entity -> entity.level().isDay() || entity.getFlyingTick() > 150),
                  new SetRandomFlyAndWalkTarget(),
                  new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
               }
            )
         }
      );
   }

   public BrainActivityGroup<GiantBatEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new OrbitAttack()
               .speedMod((entity, target) -> 1.5F)
               .orbitRadius((entity, target) -> 10.0)
               .orbitHeight((entity, target) -> 7.0)
               .performOrbitAttack((entity, target) -> {
                  entity.doHurtTarget(target);
                  entity.triggerAnim("miscController", "bite");
               })
               .onTick(entity -> {
                  entity.setSleeping(false);
                  entity.setFlying(true);
                  return true;
               }),
            new CustomRangeAttack(10)
               .maxAttackRadius(20.0F)
               .attackInterval(entity -> 100)
               .performAttack(GiantBatEntity::ultrasonicWave)
               .whenStarting(entity -> entity.triggerAnim("loopController", "sonic_attack"))
         }
      );
   }

   protected PlayState loopController(AnimationState<GiantBatEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.giant_bat.hang";
      } else if (this.isInSittingPose()) {
         name = "animation.giant_bat.stay";
      } else if (state.isMoving()) {
         if (this.onGround()) {
            name = "animation.giant_bat.crawl";
         } else if (this.isAngry()) {
            name = "animation.giant_bat.idle_fly";
         } else {
            name = "animation.giant_bat.fly";
         }
      } else if (this.onGround()) {
         name = "animation.giant_bat.stand";
      } else {
         name = "animation.giant_bat.idle_fly";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController)
               .triggerableAnim("sonic_attack", RawAnimation.begin().then("animation.giant_bat.sonic_attack", LoopType.PLAY_ONCE)),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.giant_bat.bite", LoopType.PLAY_ONCE))
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
