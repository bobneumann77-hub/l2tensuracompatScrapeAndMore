package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.LeapToTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.SpittingRangedMonsterAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.projectile.MonsterSpitProjectile;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.SpittingRangedMonster;
import io.github.manasmods.tensura.entity.variant.HoundDogVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HoundDogEntity
   extends TensuraTamableEntity
   implements GeoEntity,
   SmartBrainOwner<HoundDogEntity>,
   SpittingRangedMonster,
   VariantHolder<HoundDogVariant> {
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(HoundDogEntity.class, EntityDataSerializers.INT);
   protected boolean prevSnakeControlled = false;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public HoundDogEntity(EntityType<? extends HoundDogEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 6.0)
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.1F)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 1.5);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(VARIANT, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(VARIANT, compound.getInt("Variant"));
   }

   public HoundDogVariant getVariant() {
      return HoundDogVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(HoundDogVariant variant) {
      this.entityData.set(VARIANT, variant.getId() & 0xFF);
      this.updateStatsByVariant();
      this.setHealth(this.getMaxHealth());
   }

   public void updateStatsByVariant() {
      if (this.getVariant().equals(HoundDogVariant.EVOLVED)) {
         if (this.isSnakeControlled()) {
            Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(60.0);
            Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(4.0);
         } else {
            Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(30.0);
            Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(6.0);
         }
      }
   }

   public boolean isSnakeControlled() {
      return this.getVariant().equals(HoundDogVariant.EVOLVED) && this.getHealth() <= this.getMaxHealth() * 2.0F / 3.0F;
   }

   @Override
   public boolean canSleep() {
      return !this.isNoAi();
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return false;
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      return null;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.type().effects().equals(DamageEffects.POKING) || TensuraDamageHelper.isPoison(source) || super.isInvulnerableTo(source);
   }

   public boolean canBeAffected(MobEffectInstance instance) {
      return instance.getEffect() == MobEffects.POISON ? false : super.canBeAffected(instance);
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      if (pFallDistance < 5.0F) {
         return false;
      }

      int i = this.calculateFallDamage(pFallDistance - 5.0F, pMultiplier);
      if (i <= 0) {
         return false;
      }

      this.hurt(pSource, i);
      this.playBlockFallSound();
      return true;
   }

   @Override
   public boolean doHurtTarget(Entity pEntity) {
      if (super.doHurtTarget(pEntity)) {
         if (pEntity instanceof LivingEntity living
            && this.getVariant().equals(HoundDogVariant.EVOLVED)
            && living.getLastHurtByMobTimestamp() == living.tickCount) {
            if (this.isSnakeControlled()) {
               living.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 200, 0, true, false, true), this);
               this.heal(2.0F);
            } else {
               int poison = living.hasEffect(MobEffects.POISON) ? 1 : 0;
               living.addEffect(new MobEffectInstance(MobEffects.POISON, 200, poison), this);
               this.heal(1.0F);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void spitParticle(MonsterSpitProjectile projectile) {
      this.particleSpawning(projectile, new DustParticleOptions(new Vector3f(0.0F, 255.0F, 0.0F), 1.0F), 5);
   }

   @Override
   public void spitHit(LivingEntity pTarget) {
      if (!(pTarget instanceof HoundDogEntity)) {
         pTarget.hurt(TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.POISONOUS_BREATH, this), 3.0F);
      }
   }

   @Override
   public void impactEffect(MonsterSpitProjectile spit, double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getAcidEffect(), x, y, z, 55, 0.04, 0.04, 0.04, 0.05, true);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getAcidBubble(), x, y, z, 25, 0.04, 0.04, 0.04, 0.05, false);
      List<LivingEntity> livingEntityList = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            spit.getBoundingBox().inflate(3.0),
            entityData -> !entityData.isAlliedTo(this) && !entityData.is(this) && entityData != this.getOwner()
         );
      if (!livingEntityList.isEmpty()) {
         for (LivingEntity pTarget : livingEntityList) {
            pTarget.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 100, 0, true, false, true), this);
         }
      }
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      if (TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.houndDogSnakeChance, serverLevelAccessor.getRandom())) {
         this.setVariant(HoundDogVariant.EVOLVED);
      }

      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.houndDog, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.prevSnakeControlled != this.isSnakeControlled()) {
         this.prevSnakeControlled = this.isSnakeControlled();
         this.updateStatsByVariant();
      }
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   protected SoundEvent getAmbientSound() {
      return this.isAngry() ? (SoundEvent)TensuraSoundEvents.DOG_AGGRO.get() : (SoundEvent)TensuraSoundEvents.DOG_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.DOG_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.DOG_DEATH.get();
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

   public List<ExtendedSensor<HoundDogEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<HoundDogEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<HoundDogEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
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

   public BrainActivityGroup<HoundDogEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> owner.isSnakeControlled() ? 1.5F : 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new SpittingRangedMonsterAttack(8)
                     .requireInSight(false)
                     .attackRadius(20.0F)
                     .attackInterval(entity -> 100)
                     .performAttack((entity, target) -> entity.performRangedAttack(target, 2.0, 2.0))
                     .whenStarting(entity -> entity.triggerAnim("miscController", "spit"))
                     .startCondition(HoundDogEntity::isSnakeControlled),
                  new AnimatableMeleeAttack(10)
                     .attackInterval(entity -> 5)
                     .startCondition(HoundDogEntity::isSnakeControlled)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "snake_bite")),
                  new LeapToTarget(2)
                     .leapRange((entity, target) -> 5.0F)
                     .moveSpeedContribution((dog, entity) -> 1.0F)
                     .jumpStrength((entity, target) -> 0.8F)
                     .attackInterval(entity -> 60)
                     .startCondition(
                        entity -> !entity.isSnakeControlled() && entity.getTarget() != null && !entity.isWithinMeleeAttackRange(entity.getTarget())
                     )
                     .whenStarting(entity -> entity.triggerAnim("miscController", "leap")),
                  new AnimatableMeleeAttack(2)
                     .attackInterval(entity -> 5)
                     .startCondition(entity -> !entity.isSnakeControlled())
                     .whenStarting(entity -> entity.triggerAnim("miscController", "bite"))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<HoundDogEntity> state) {
      String name;
      if (this.getHealth() <= 0.0F) {
         name = "animation.hound_dog.lay";
      } else if (this.isSnakeControlled()) {
         if (state.isMoving()) {
            name = "animation.hound_dog.walk_snake";
         } else {
            name = "animation.hound_dog.idle_snake";
         }
      } else if (this.isSleeping()) {
         name = "animation.hound_dog.sleep";
      } else if (this.isInSittingPose()) {
         name = "animation.hound_dog.sit";
      } else if (state.isMoving()) {
         if (this.isInWater() || this.isInLava()) {
            name = "animation.hound_dog.swim";
         } else if (this.isAngry()) {
            name = "animation.hound_dog.run";
         } else {
            name = "animation.hound_dog.walk";
         }
      } else {
         name = "animation.hound_dog.idle";
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
               .triggerableAnim("eat", RawAnimation.begin().then("animation.hound_dog.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("bite", RawAnimation.begin().then("animation.hound_dog.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("snake_bite", RawAnimation.begin().then("animation.hound_dog.bite_snake", LoopType.PLAY_ONCE))
               .triggerableAnim("leap", RawAnimation.begin().then("animation.hound_dog.leap_attack", LoopType.PLAY_ONCE))
               .triggerableAnim("spit", RawAnimation.begin().then("animation.hound_dog.snake_spit", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
