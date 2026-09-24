package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomSwimAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IAmphibian;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.entity.variant.LandfishVariant;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
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

public class LandfishEntity extends TensuraTamableEntity implements GeoEntity, SmartBrainOwner<LandfishEntity>, IAmphibian, INameEvolution {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(LandfishEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> EVOLUTION_STATE = SynchedEntityData.defineId(LandfishEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int swimmingTick = 0;
   private boolean landNavigating = false;

   public LandfishEntity(EntityType<? extends LandfishEntity> type, Level level) {
      super(type, level);
      this.initAmphibian(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 6.0)
         .add(Attributes.MAX_HEALTH, 16.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.01F)
         .add(Attributes.STEP_HEIGHT, 1.0)
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
      builder.define(DATA_ID_TYPE_VARIANT, 0);
      builder.define(EVOLUTION_STATE, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
      compound.putInt("EvoState", this.getCurrentEvolutionState());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(DATA_ID_TYPE_VARIANT, compound.getInt("Variant"));
      this.setCurrentEvolutionState(compound.getInt("EvoState"));
   }

   public LandfishVariant getVariant() {
      return LandfishVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(LandfishVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
      if (variant.equals(LandfishVariant.DOLPHIN)) {
         this.gainSwimSpeed(this, 0.5);
      }

      if (this.onGround()) {
         this.triggerAnim("miscController", "jump");
      }

      this.setAirSupply(this.getMaxAirSupply());
   }

   public int getMaxAirSupply() {
      return 3000;
   }

   protected void handleAirSupply(int i) {
      if (this.getVariant().getId() < 5) {
         if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(i - 1);
            if (this.getAirSupply() == -20) {
               this.setAirSupply(0);
               this.hurt(this.damageSources().dryOut(), 2.0F);
               if (this.onGround()) {
                  this.triggerAnim("miscController", "jump");
               }
            }
         } else {
            this.setAirSupply(this.getMaxAirSupply());
         }
      }
   }

   @Override
   public boolean shouldFindWater(Mob mob) {
      if (this.getAirSupply() <= 300 && !this.isOrderedToSit()) {
         if (this.getTarget() != null) {
            this.setTarget(null);
         }

         return true;
      } else {
         return this.shouldStayInWater(this) && this.getSwimmingTick() <= -1000;
      }
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
   public int getCurrentEvolutionState() {
      return (Integer)this.entityData.get(EVOLUTION_STATE);
   }

   @Override
   public void setCurrentEvolutionState(int state) {
      this.entityData.set(EVOLUTION_STATE, state);
   }

   @Override
   public void evolve() {
      int current = this.getCurrentEvolutionState();
      if (current < this.getMaxEvolutionState()) {
         this.setCurrentEvolutionState(current + 1);
         if (this.getVariant().equals(LandfishVariant.GUARDIAN)) {
            this.setVariant(LandfishVariant.ELDER_GUARDIAN);
         }
      }

      if (this.onGround()) {
         this.triggerAnim("miscController", "jump");
      }

      this.setAirSupply(this.getMaxAirSupply());
   }

   public void thunderHit(ServerLevel pLevel, LightningBolt pLightning) {
      if (this.getVariant().equals(LandfishVariant.GUARDIAN)) {
         this.setVariant(LandfishVariant.ELDER_GUARDIAN);
         this.gainMaxHealth(this, 10.0);
         this.playSound(SoundEvents.ELDER_GUARDIAN_CURSE, 2.0F, 1.0F);
      } else {
         super.thunderHit(pLevel, pLightning);
      }
   }

   public void baseTick() {
      int i = this.getAirSupply();
      super.baseTick();
      this.handleAirSupply(i);
   }

   @Override
   public void tick() {
      super.tick();
      this.handleSwimming(this);
      if (this.level().isClientSide() && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.01) {
         Vec3 vec3 = this.getViewVector(0.0F);
         float f = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)) * 0.3F;
         float f1 = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)) * 0.3F;
         float f2 = 1.2F - this.random.nextFloat() * 0.7F;

         for (int i = 0; i < 2; i++) {
            this.level().addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3.x * f2 + f, this.getY() - vec3.y, this.getZ() - vec3.z * f2 + f1, 0.0, 0.0, 0.0);
            this.level().addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3.x * f2 - f, this.getY() - vec3.y, this.getZ() - vec3.z * f2 - f1, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public boolean doHurtTarget(Entity pEntity) {
      boolean flag = super.doHurtTarget(pEntity);
      if (flag && this.getRandom().nextInt(4) == 1 && pEntity instanceof LivingEntity target && target.getLastHurtByMobTimestamp() == target.tickCount) {
         switch (this.getVariant()) {
            case ELDER_GUARDIAN:
               target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 400, 1, true, false, true), this);
               break;
            case PUFFER:
               target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1, true, false, true), this);
         }
      }

      return flag;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.FISHES);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return this.isFood(pStack);
   }

   @Override
   public InteractionResult handleEating(Player player, InteractionHand hand, ItemStack stack) {
      if (stack.is(Items.WATER_BUCKET)) {
         if (!player.hasInfiniteMaterials()) {
            stack.shrink(1);
         }

         if (this.getHealth() < this.getMaxHealth()) {
            this.heal(2.0F);
         }

         if (this.onGround()) {
            this.triggerAnim("miscController", "jump");
         } else {
            this.triggerAnim("miscController", "eat");
         }

         this.setAirSupply(this.getMaxAirSupply());
         if (this.getRandom().nextInt(5) == 0) {
            this.level().playSound(null, this, SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
         } else {
            ItemStack bucket = new ItemStack(Items.BUCKET);
            if (!player.addItem(bucket)) {
               player.drop(bucket, false);
            }

            this.level().playSound(null, this, SoundEvents.GENERIC_DRINK, SoundSource.NEUTRAL, 1.0F, 1.0F);
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return super.handleEating(player, hand, stack);
      }
   }

   public void spawnChildFromBreeding(ServerLevel serverLevel, Animal animal) {
      super.spawnChildFromBreeding(serverLevel, animal);
      if (this.onGround()) {
         this.triggerAnim("miscController", "jump");
      }
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      LandfishEntity fish = (LandfishEntity)((EntityType)MonsterEntityTypes.LANDFISH.get()).create(pLevel);
      if (fish == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         fish.setOwnerUUID(uuid);
         fish.setTame(true, true);
      }

      int i = this.random.nextInt(9);
      LandfishVariant variant;
      if (i < 3) {
         variant = this.getVariant();
      } else if (i < 6 && pOtherParent instanceof LandfishEntity entity) {
         variant = entity.getVariant();
      } else {
         variant = this.setRandomVariant(pLevel);
      }

      fish.setVariant(variant);
      return fish;
   }

   public boolean checkSpawnObstruction(LevelReader levelReader) {
      return levelReader.isUnobstructed(this);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.landfish, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   public static boolean checkLandfishSpawnRules(
      EntityType<LandfishEntity> fish, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      int i = pLevel.getSeaLevel();
      int j = i - 13;
      return pPos.getY() >= j
         && pPos.getY() <= i
         && pLevel.getFluidState(pPos.below()).is(FluidTags.WATER)
         && pLevel.getBlockState(pPos.above()).is(Blocks.WATER);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      this.setVariant(this.setRandomVariant(pLevel));
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   private LandfishVariant setRandomVariant(ServerLevelAccessor pLevel) {
      if (pLevel.getBiome(this.blockPosition()).is(BiomeTags.IS_DEEP_OCEAN)) {
         return LandfishVariant.byId(this.getRandom().nextInt(8));
      } else {
         return pLevel.getBiome(this.blockPosition()).is(BiomeTags.IS_OCEAN)
            ? LandfishVariant.byId(this.getRandom().nextInt(7))
            : LandfishVariant.byId(this.getRandom().nextInt(5));
      }
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.FISH_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.FISH_DEATH.get();
   }

   @NotNull
   protected SoundEvent getSwimSound() {
      return SoundEvents.FISH_SWIM;
   }

   protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockIn) {
      this.playSound((SoundEvent)TensuraSoundEvents.FISH_FLOP.get(), 0.75F, 0.75F);
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

   public List<ExtendedSensor<LandfishEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<LandfishEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<LandfishEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> entity.getType().equals(EntityType.GUARDIAN))
                     .startCondition(entity -> !entity.shouldFindWater(entity)),
                  new SubordinateFollowOwner().speedMod(1.2F),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{
                     new SetRandomSwimAndWalkTarget()
                        .speedModifier((entity, pos) -> !entity.isInWaterOrBubble() && !entity.shouldFindWater(entity) ? 1.0F : 2.0F)
                        .cooldownFor(entity -> 0)
                        .startCondition(entity -> !entity.isOrderedToSit())
                        .stopIf(ISubordinate::isOrderedToSit),
                     new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
                  }
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<LandfishEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf((entity, target) -> entity.shouldStopTarget(entity, target) || entity.shouldFindWater(entity)),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new AnimatableMeleeAttack(1).attackInterval(entity -> 1).whenStarting(entity -> entity.triggerAnim("miscController", "slap"))
         }
      );
   }

   protected PlayState loopController(AnimationState<LandfishEntity> state) {
      String name;
      if (this.isInSittingPose()) {
         if (this.onGround()) {
            name = "animation.landfish.stay";
         } else {
            name = "animation.landfish.idle_swim";
         }
      } else if (this.isInWaterOrBubble()) {
         if (state.isMoving()) {
            name = "animation.landfish.swim";
         } else {
            name = "animation.landfish.idle_swim";
         }
      } else if (state.isMoving()) {
         if (this.getHealth() < this.getMaxHealth() * 0.25) {
            name = "animation.landfish.walk_hurt";
         } else {
            name = "animation.landfish.walk";
         }
      } else if (this.getHealth() < this.getMaxHealth() * 0.25) {
         name = "animation.landfish.idle_hurt";
      } else {
         name = "animation.landfish.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 0, event -> PlayState.STOP)
               .triggerableAnim("eat", RawAnimation.begin().then("animation.landfish.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("slap", RawAnimation.begin().then("animation.landfish.slap", LoopType.PLAY_ONCE))
               .triggerableAnim("jump", RawAnimation.begin().then("animation.landfish.jump", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   @Override
   public int getSwimmingTick() {
      return this.swimmingTick;
   }

   @Generated
   @Override
   public void setSwimmingTick(int swimmingTick) {
      this.swimmingTick = swimmingTick;
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
