package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.LeapToTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.SpittingRangedMonsterAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.projectile.MonsterSpitProjectile;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.entity.template.subclass.SpittingRangedMonster;
import io.github.manasmods.tensura.entity.variant.BasiliskVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageEffects;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BasiliskEntity
   extends TensuraMountEntity
   implements GeoEntity,
   SmartBrainOwner<BasiliskEntity>,
   SpittingRangedMonster,
   ITensuraMount,
   VariantHolder<BasiliskVariant> {
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(BasiliskEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int mountAbilityCooldown = 0;

   public BasiliskEntity(EntityType<? extends BasiliskEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 16.0)
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.25)
         .add(Attributes.STEP_HEIGHT, 1.5)
         .add(Attributes.JUMP_STRENGTH, 1.5)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.5);
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

   public BasiliskVariant getVariant() {
      return BasiliskVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(BasiliskVariant variant) {
      this.entityData.set(VARIANT, variant.getId() & 0xFF);
   }

   @Override
   public boolean canSleep() {
      return !this.isNoAi();
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      BasiliskEntity entity = (BasiliskEntity)((EntityType)MonsterEntityTypes.BASILISK.get()).create(pLevel);
      if (entity == null) {
         return null;
      }

      if (pOtherParent instanceof BasiliskEntity basilisk) {
         entity.setVariant(pLevel.getRandom().nextBoolean() ? this.getVariant() : basilisk.getVariant());
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
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.type().effects().equals(DamageEffects.POKING) || TensuraDamageHelper.isPoison(source) || super.isInvulnerableTo(source);
   }

   public boolean canBeAffected(MobEffectInstance instance) {
      if (instance.getEffect() == MobEffects.POISON) {
         return false;
      } else {
         return instance.getEffect() == TensuraMobEffects.getReference(TensuraMobEffects.PETRIFICATION) ? false : super.canBeAffected(instance);
      }
   }

   @Override
   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      this.playBlockFallSound();
      return false;
   }

   @Override
   public void spitParticle(MonsterSpitProjectile projectile) {
      this.particleSpawning(projectile, TensuraParticleUtils.getWhiteEffect(), 5);
   }

   @Override
   public void spitHit(LivingEntity target) {
      if (!(target instanceof BasiliskEntity)) {
         target.hurt(
            TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.PETRIFICATION, this),
            (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.75F
         );
         MobEffectInstance petrification = target.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.PETRIFICATION));
         int level = petrification != null ? petrification.getAmplifier() + 1 : 0;
         target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PETRIFICATION), 100, level, true, false, true), this);
      }
   }

   @Override
   public void impactEffect(MonsterSpitProjectile spit, double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getWhiteEffect(), x, y, z, 55, 0.04, 0.04, 0.04, 0.05, true);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getWhiteBubble(), x, y, z, 25, 0.04, 0.04, 0.04, 0.05, false);
   }

   public void setTarget(@Nullable LivingEntity entity) {
      super.setTarget(entity);
      if (this.getTarget() != null && entity != null) {
         this.triggerAnim("miscController", "roar");
      }
   }

   @Override
   public void tick() {
      super.tick();
      Vec3 vec3 = this.getDeltaMovement();
      if (!this.onGround() && vec3.y < 0.0) {
         this.setDeltaMovement(vec3.multiply(1.0, 0.8, 1.0));
      }

      if (!this.level().isClientSide()) {
         if (this.mountAbilityCooldown > 0) {
            this.getNavigation().stop();
            if (this.mountAbilityCooldown-- == 15) {
               LivingEntity owner = this.getControllingPassenger();
               if (owner != null) {
                  LivingEntity target = ObjectSelectionHelper.getTargetingEntity(owner, 20.0, true);
                  BlockPos pos;
                  if (target != null) {
                     pos = ObjectSelectionHelper.getBlockPos(target.position().add(0.0, target.getBbHeight() / 2.0, 0.0));
                  } else {
                     pos = ObjectSelectionHelper.getPlayerPOVHitResult(this.level(), owner, Fluid.NONE, 30.0).getBlockPos();
                  }

                  this.performRangedAttack(pos, this.isBaby() ? 1.0 : 2.0, this.isBaby() ? 1.0 : 2.0, new Vec3(1.0, -0.5, 0.0).yRot(-this.getYRot() * 0.0174F));
                  this.level()
                     .playSound(
                        null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.BREATH_POISON.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                     );
               }
            }
         }
      }
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   @Override
   protected float getRiddenSpeed(Player player) {
      return super.getRiddenSpeed(player) * 1.5F;
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, -0.25, -0.5 * f).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   @Override
   public boolean canActivateMountAbility(LivingEntity rider) {
      return !this.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_SKILL));
   }

   @Override
   public void mountAbility(Player rider) {
      if (!this.isBaby()) {
         if (this.mountAbilityCooldown <= 0) {
            this.mountAbilityCooldown = 30;
            this.triggerAnim("miscController", "spit");
         }
      }
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.basilisk, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
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
      if (biome.is(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
         this.setVariant(BasiliskVariant.FALCON);
      } else if (biome.is(BiomeTags.SPAWNS_WARM_VARIANT_FROGS)) {
         this.setVariant(BasiliskVariant.ROOSTER);
      } else if (!biome.is(BiomeTags.IS_MOUNTAIN) && level.getRandom().nextFloat() <= 0.25) {
         this.setVariant(BasiliskVariant.CHICKEN);
      } else {
         this.setVariant(BasiliskVariant.EAGLE);
      }
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.BASILISK_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.BASILISK_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.BASILISK_DEATH.get();
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

   public List<ExtendedSensor<BasiliskEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<BasiliskEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<BasiliskEntity> getIdleTasks() {
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
            new OneRandomBehaviour(
               new ExtendedBehaviour[]{
                  new SetRandomWalkTarget(),
                  new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60)),
                  new Idle()
                     .runFor(entity -> 35)
                     .cooldownFor(entity -> 200)
                     .startCondition(
                        entity -> entity.onGround() && !entity.isOrderedToSit() && !entity.isSleeping() && entity.getControllingPassenger() == null
                     )
                     .whenStarting(entity -> {
                        if (entity.getRandom().nextBoolean()) {
                           entity.triggerAnim("miscController", "alert");
                        }
                     })
               }
            )
         }
      );
   }

   public BrainActivityGroup<BasiliskEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new SpittingRangedMonsterAttack(15)
                     .requireInSight(false)
                     .attackRadius(20.0F)
                     .attackInterval(entity -> 100)
                     .performAttack(
                        (entity, target) -> {
                           entity.performRangedAttack(target, 1.0, 2.0, new Vec3(1.0, -0.5, 0.0).yRot(-this.getYRot() * 0.0174F));
                           entity.level()
                              .playSound(
                                 null,
                                 entity.getX(),
                                 entity.getY(),
                                 entity.getZ(),
                                 (SoundEvent)TensuraSoundEvents.BREATH_POISON.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 1.0F,
                                 1.0F
                              );
                        }
                     )
                     .whenStarting(entity -> entity.triggerAnim("miscController", "spit"))
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.2F),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(5.0F)
                     .attackInterval(entity -> 40)
                     .performAttack(
                        (entity, target) -> {
                           entity.doHurtTarget(target, 2.0F);
                           entity.playSound(
                              SoundEvents.PLAYER_ATTACK_SWEEP,
                              entity.getSoundVolume(),
                              (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + 1.0F
                           );
                        }
                     )
                     .whenStarting(entity -> entity.triggerAnim("miscController", "kick"))
                     .startCondition(basilisk -> basilisk.getRandom().nextInt(5) == 1),
                  new AnimatableMeleeAttack(2).attackInterval(entity -> 5).whenStarting(entity -> entity.triggerAnim("miscController", "bite")),
                  new LeapToTarget(10)
                     .leapRange((entity, target) -> 5.0F)
                     .moveSpeedContribution((dog, entity) -> 1.0F)
                     .jumpStrength((entity, target) -> 0.8F)
                     .attackInterval(entity -> 60)
                     .startCondition(entity -> entity.getTarget() != null && !entity.isWithinMeleeAttackRange(entity.getTarget()))
                     .whenStarting(entity -> entity.triggerAnim("miscController", "kick"))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<BasiliskEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.basilisk.sleep";
      } else if (this.isInSittingPose()) {
         name = "animation.basilisk.sit";
      } else if (!this.onGround() && !this.isInLiquid()) {
         name = "animation.basilisk.flap";
      } else if (state.isMoving()) {
         if ((this.isAngry() || this.isSprinting()) && !this.isInLiquid()) {
            name = "animation.basilisk.charge";
         } else {
            name = "animation.basilisk.walk";
         }
      } else {
         name = "animation.basilisk.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("eat", RawAnimation.begin().then("animation.basilisk.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("bite", RawAnimation.begin().then("animation.basilisk.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("kick", RawAnimation.begin().then("animation.basilisk.kick", LoopType.PLAY_ONCE))
               .triggerableAnim("spit", RawAnimation.begin().then("animation.basilisk.snake_spit", LoopType.PLAY_ONCE))
               .triggerableAnim("alert", RawAnimation.begin().then("animation.basilisk.alert", LoopType.PLAY_ONCE))
               .triggerableAnim("roar", RawAnimation.begin().then("animation.basilisk.roar", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
