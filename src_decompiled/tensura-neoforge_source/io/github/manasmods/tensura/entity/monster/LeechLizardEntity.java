package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.LeapToTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.variant.LeechLizardVariant;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
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
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.navigation.SmoothAmphibiousPathNavigation;
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

public class LeechLizardEntity extends TensuraMountEntity implements GeoEntity, SmartBrainOwner<LeechLizardEntity>, VariantHolder<LeechLizardVariant> {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(LeechLizardEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public LeechLizardEntity(EntityType<? extends LeechLizardEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @Override
   protected PathNavigation createNavigation(Level level) {
      return new SmoothAmphibiousPathNavigation(this, level);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.MAX_HEALTH, 35.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.1F)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.JUMP_STRENGTH, 0.5)
         .add(Attributes.SAFE_FALL_DISTANCE, 7.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.05F);
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

   public LeechLizardVariant getVariant() {
      return LeechLizardVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(LeechLizardVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
   }

   protected float getWaterSlowDown() {
      return 0.95F;
   }

   @Override
   public boolean doHurtTarget(Entity pEntity) {
      if (super.doHurtTarget(pEntity)) {
         if (pEntity instanceof LivingEntity living && living.getLastHurtByMobTimestamp() == living.tickCount) {
            if (this.getRandom().nextInt(3) == 0) {
               int poison = living.hasEffect(MobEffects.POISON) ? 1 : 0;
               living.addEffect(new MobEffectInstance(MobEffects.POISON, 200, poison, false, false), this);
            } else {
               living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false), this);
            }

            this.heal(3.0F);
         }

         return true;
      } else {
         return false;
      }
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(0.0, -0.75, 0.0);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.LEECH_LIZARD_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.LEECH_LIZARD_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.LEECH_LIZARD_DEATH.get();
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      LeechLizardEntity lizard = (LeechLizardEntity)((EntityType)MonsterEntityTypes.LEECH_LIZARD.get()).create(pLevel);
      if (lizard == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         lizard.setOwnerUUID(uuid);
         lizard.setTame(true, true);
      }

      int i = this.random.nextInt(9);
      if (i < 4) {
         lizard.setVariant(this.getVariant());
      } else if (i < 8 && pOtherParent instanceof LeechLizardEntity lizardEntity) {
         lizard.setVariant(lizardEntity.getVariant());
      } else {
         this.biomesBasedVariant(pLevel);
      }

      return lizard;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   @Override
   protected void executeRidersJump(float f, Vec3 vec3) {
      double jump = this.getJumpPower(f);
      this.addDeltaMovement(
         this.getLookAngle()
            .multiply(1.0, 0.0, 1.0)
            .normalize()
            .scale(5.0F * f * this.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.getBlockSpeedFactor())
            .add(0.0, 1.4285F * jump, 0.0)
      );
      this.setRiddenJumping(true);
      this.hasImpulse = true;
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.leechLizard, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      this.biomesBasedVariant(serverLevelAccessor);
      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
   }

   public void biomesBasedVariant(ServerLevelAccessor pLevel) {
      Holder<Biome> biomes = pLevel.getBiome(this.getOnPos());
      if (biomes.is(BiomeTags.SPAWNS_WARM_VARIANT_FROGS)) {
         this.setVariant(LeechLizardVariant.TAN);
      } else if (biomes.is(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
         this.setVariant(LeechLizardVariant.WHITE);
      } else if (biomes.is(BiomeTags.IS_FOREST)) {
         this.setVariant(LeechLizardVariant.BROWN);
      } else {
         this.setVariant(LeechLizardVariant.GREEN);
      }
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<LeechLizardEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<LeechLizardEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<LeechLizardEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this),
                  new SubordinateFollowOwner(),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<LeechLizardEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new AnimatableMeleeAttack(3).whenStarting(entity -> entity.triggerAnim("miscController", "bite")),
            new LeapToTarget(3)
               .minRange((entity, target) -> 2.0F)
               .leapRange((entity, target) -> 5.0F)
               .moveSpeedContribution((dog, entity) -> 1.0F)
               .jumpStrength((entity, target) -> 0.8F)
               .attackInterval(entity -> 30)
               .whenStarting(entity -> {
                  entity.triggerAnim("miscController", "leap");
                  entity.playSound((SoundEvent)TensuraSoundEvents.LEECH_LIZARD_ATTACK.get());
               })
               .startCondition(entity -> entity.getTarget() != null && !entity.isWithinMeleeAttackRange(entity.getTarget()))
         }
      );
   }

   protected PlayState loopController(AnimationState<LeechLizardEntity> state) {
      String name;
      if (this.isInSittingPose()) {
         name = "animation.leech_lizard.stay";
      } else if (this.isInLiquid() && this.level().getBlockState(this.blockPosition().below(1)).canBeReplaced()) {
         name = "animation.leech_lizard.swim";
      } else if (state.isMoving()) {
         if (this.isInLiquid() || !this.isAngry() && !this.isSprinting()) {
            name = "animation.leech_lizard.walk";
         } else {
            name = "animation.leech_lizard.run";
         }
      } else {
         name = "animation.leech_lizard.idle";
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
               .triggerableAnim("eat", RawAnimation.begin().then("animation.leech_lizard.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("bite", RawAnimation.begin().then("animation.leech_lizard.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("leap", RawAnimation.begin().then("animation.leech_lizard.leap", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
