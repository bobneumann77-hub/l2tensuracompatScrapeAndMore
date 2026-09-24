package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.entity.variant.PhantasporeVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import lombok.Generated;
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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;
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

public class PhantasporeEntity
   extends TensuraMountEntity
   implements GeoEntity,
   SmartBrainOwner<PhantasporeEntity>,
   ITensuraMount,
   VariantHolder<PhantasporeVariant> {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(PhantasporeEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> BURIED = SynchedEntityData.defineId(PhantasporeEntity.class, EntityDataSerializers.BOOLEAN);
   private int mountAttackCooldown = 0;
   private boolean dancing;
   @Nullable
   private BlockPos jukebox;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public PhantasporeEntity(EntityType<? extends PhantasporeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.ARMOR, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.JUMP_STRENGTH, 1.0)
         .add(Attributes.SAFE_FALL_DISTANCE, 8.0)
         .add(Attributes.STEP_HEIGHT, 2.0)
         .add(Attributes.SCALE, 1.0)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 3.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ID_TYPE_VARIANT, 0);
      builder.define(BURIED, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
      compound.putBoolean("Buried", this.isBuried());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(DATA_ID_TYPE_VARIANT, compound.getInt("Variant"));
      this.setBuried(compound.getBoolean("Buried"));
   }

   @NotNull
   public PhantasporeVariant getVariant() {
      return PhantasporeVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(PhantasporeVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
   }

   public boolean isBuried() {
      return (Boolean)this.entityData.get(BURIED);
   }

   public void setBuried(boolean b) {
      this.entityData.set(BURIED, b);
   }

   public boolean isAmbushing() {
      if (!this.isBuried()) {
         return false;
      } else {
         return BrainUtils.getMemory(this, MemoryModuleType.HURT_BY_ENTITY) != null
            ? false
            : !BrainUtils.hasMemory(this, MemoryModuleType.ATTACK_COOLING_DOWN)
               && !BrainUtils.hasMemory(this, (MemoryModuleType)SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get());
      }
   }

   @NotNull
   @Override
   public EntityDimensions getDefaultDimensions(Pose pPose) {
      return this.isBuried() ? super.getDefaultDimensions(pPose).scale(1.0F, 0.1F) : super.getDefaultDimensions(pPose);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (BURIED.equals(pKey)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   public boolean canBeAffected(MobEffectInstance instance) {
      return instance.getEffect().equals(TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS)) ? false : super.canBeAffected(instance);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      if (this.getVariant().isNether() && source.is(DamageTypeTags.IS_FIRE) && source.tensura$getAbilityInstance() == null) {
         return true;
      } else {
         return source.is(DamageTypes.IN_WALL) ? true : super.isInvulnerableTo(source);
      }
   }

   @Override
   public boolean doHurtTarget(Entity pEntity) {
      if (super.doHurtTarget(pEntity)) {
         if (pEntity instanceof LivingEntity living && living.getLastHurtByMobTimestamp() == living.tickCount) {
            if (this.getRandom().nextInt(3) != 0) {
               int poison = living.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS)) ? 1 : 0;
               living.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS), 200, poison, true, false, true), this);
            } else {
               living.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 100, 0, true, false, true), this);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public int getChestsAllowed() {
      return 0;
   }

   public float getSpeed() {
      float speed = super.getSpeed();
      if (this.isBuried()) {
         speed *= 0.2F;
      }

      return speed;
   }

   @Override
   public void tick() {
      super.tick();
      this.mountAttackCooldown--;
   }

   @Override
   public void aiStep() {
      if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 5.0) || !this.level().getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
         this.dancing = false;
         this.jukebox = null;
      }

      super.aiStep();
   }

   public void setRecordPlayingNearby(BlockPos pPos, boolean pIsPartying) {
      if (this.isTame()) {
         this.jukebox = pPos;
         this.dancing = pIsPartying;
      }
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
               && (!(entity instanceof PhantasporeEntity) || entity == this.getTarget())
         );
      if (!list.isEmpty()) {
         for (LivingEntity target : list) {
            target.hurt(this.damageSources().mobAttack(this), (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier));
            SkillHelper.knockBack(this, target, strength);
         }
      }
   }

   public void releasePollen(float range) {
      float f = Mth.clamp(7.0F * range, 0.75F, 30.0F);

      for (int i = 0; i < f; i++) {
         if (f - i < 1.0F && this.random.nextFloat() > f - i) {
            return;
         }

         float distance = range * 0.5F * (1.0F - this.random.nextFloat() * this.random.nextFloat());
         float v = this.random.nextFloat() * 6.28F;
         Vec3 pos = new Vec3(distance * Mth.cos(v), 0.2 + (this.random.nextFloat() - 0.5F) * 2.0F, distance * Mth.sin(v));
         TensuraParticleHelper.spawnServerParticles(
            this.level(), TensuraParticleUtils.getHypnosisCloud(), this.getX() + pos.x, this.getY() + pos.y, this.getZ() + pos.z, 1, 0.0, 0.0, 0.0, 0.0, false
         );
      }

      AABB aabb = this.getBoundingBox().inflate(range);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(LivingEntity.class, aabb, entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && !entity.equals(this));
      if (!list.isEmpty()) {
         for (LivingEntity target : list) {
            if (Alignment.shouldConsumeAir(target)) {
               MobEffectInstance hypnosis = target.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS));
               int level = hypnosis != null ? hypnosis.getAmplifier() + 1 : 0;
               target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS), 200, level, true, false, true), this);
            }

            target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 100, 0, true, false, true), this);
         }
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SHROOMLIGHT_STEP;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.SHROOMLIGHT_HIT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SHROOMLIGHT_FALL;
   }

   protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockIn) {
      this.playSound(SoundEvents.SHROOMLIGHT_STEP, 0.5F, 1.0F);
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      return null;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.FLOWERS) || pStack.is(ItemTags.FROG_FOOD);
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(0.0, 0.1, 0.0);
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAttackCooldown <= 0) {
         this.releasePollen(8.0F);
         this.triggerAnim("miscController", "poison");
         this.level()
            .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.MIST_RELEASE.get(), TensuraSkill.ABILITY_SOUND, 1.5F, 1.0F);
         this.mountAttackCooldown = 100;
      }
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.phantaspore, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         Holder<Biome> biome = level.getBiome(this.blockPosition());
         if (biome.is(Biomes.WARPED_FOREST) || biome.is(Biomes.SOUL_SAND_VALLEY)) {
            this.setVariant(PhantasporeVariant.WARPED);
         } else if (biome.is(BiomeTags.IS_NETHER)) {
            this.setVariant(PhantasporeVariant.CRIMSON);
         } else {
            this.setVariant(PhantasporeVariant.byId(level.getRandom().nextInt(3)));
         }
      }

      return super.finalizeSpawn(level, pDifficulty, pReason, pSpawnData);
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<PhantasporeEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<PhantasporeEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new FloatToSurfaceOfFluid().startCondition(entity -> !entity.isAmbushing()),
            new LookAtTarget().startCondition(entity -> !entity.isAmbushing()),
            TensuraTamableEntity.getMoveToWalkTarget().startCondition(entity -> !((PhantasporeEntity)entity).isAmbushing())
         }
      );
   }

   public BrainActivityGroup<PhantasporeEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getPreyTargeting(this),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  new SubordinateFollowOwner().whenStarting(entity -> {
                     if (entity.isBuried()) {
                        entity.triggerAnim("loopController", "leave");
                        entity.setBuried(false);
                     }
                  }),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{
                     new SetRandomWalkTarget().startCondition(entity -> !entity.isAmbushing()),
                     new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60)),
                     new Idle()
                        .cooldownFor(entity -> 400)
                        .startCondition(entity -> entity.isBuried() && entity.getRandom().nextInt(20) == 0)
                        .whenStarting(entity -> entity.triggerAnim("loopController", "peek")),
                     new Idle()
                        .cooldownFor(entity -> 100)
                        .startCondition(entity -> !entity.isBuried() && !entity.isVehicle() && entity.getRandom().nextInt(5) == 0)
                        .whenStarting(entity -> {
                           entity.triggerAnim("loopController", "bury");
                           entity.setBuried(true);
                        })
                  }
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<PhantasporeEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 1.2F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(5)
                     .maxAttackRadius(4.0F)
                     .attackInterval(entity -> 0)
                     .performAttack(
                        (entity, target) -> {
                           entity.setBuried(false);
                           entity.releasePollen(4.0F);
                           entity.areaAttack(2.0, 0.25F, 3.0F);
                           TensuraParticleHelper.spawnServerParticles(
                              this.level(), TensuraParticleUtils.getColorlessWave(0.9F, 3.0F), entity.getX(), entity.getY() + 1.2F, entity.getZ()
                           );
                           entity.level()
                              .playSound(
                                 null,
                                 entity.getX(),
                                 entity.getY(),
                                 entity.getZ(),
                                 (SoundEvent)TensuraSoundEvents.MIST_RELEASE.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 1.5F,
                                 1.0F
                              );
                        }
                     )
                     .whenStarting(entity -> {
                        entity.triggerAnim("loopController", "ambush");
                        entity.playSound(SoundEvents.ROOTED_DIRT_BREAK);
                        TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 5, 2.0F);
                        TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 5, 3.0F);
                     })
                     .startCondition(PhantasporeEntity::isBuried),
                  new CustomRangeAttack(15)
                     .minAttackRadius(16.0F)
                     .maxAttackRadius(32.0F)
                     .attackInterval(entity -> 0)
                     .performAttack((entity, target) -> entity.setBuried(false))
                     .whenStarting(entity -> {
                        entity.triggerAnim("loopController", "leave");
                        entity.playSound(SoundEvents.ROOTED_DIRT_BREAK);
                        TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 5, 2.0F);
                        TensuraParticleHelper.spawnServerGroundSlamParticle(entity, 5, 3.0F);
                     })
                     .startCondition(entity -> entity.isBuried() && !entity.isAmbushing()),
                  new CustomRangeAttack(10)
                     .minAttackRadius(6.0F)
                     .maxAttackRadius(32.0F)
                     .attackInterval(entity -> 20)
                     .performAttack((entity, target) -> entity.setBuried(true))
                     .whenStarting(entity -> entity.triggerAnim("loopController", "bury"))
                     .startCondition(entity -> !entity.isBuried() && !entity.isVehicle()),
                  new CustomRangeAttack(3)
                     .maxAttackRadius(7.0F)
                     .attackInterval(entity -> 40)
                     .performAttack(
                        (entity, target) -> {
                           entity.releasePollen(6.0F);
                           entity.level()
                              .playSound(
                                 null,
                                 entity.getX(),
                                 entity.getY(),
                                 entity.getZ(),
                                 (SoundEvent)TensuraSoundEvents.MIST_RELEASE.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 1.5F,
                                 1.0F
                              );
                        }
                     )
                     .whenStarting(entity -> entity.triggerAnim("miscController", "poison"))
                     .startCondition(entity -> !entity.isBuried() && entity.getRandom().nextInt(12) == 1),
                  new CustomRangeAttack(3)
                     .maxAttackRadius(5.0F)
                     .attackInterval(entity -> 40)
                     .performAttack(
                        (entity, target) -> {
                           entity.areaAttack(1.5, 1.0F, 4.0F);
                           TensuraParticleHelper.spawnServerGroundSlamParticle(this, 5, 4.0F);
                           TensuraParticleHelper.spawnServerGroundSlamParticle(this, 5, 2.0F);
                           entity.level()
                              .playSound(
                                 null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, TensuraSkill.ABILITY_SOUND, 1.5F, 1.0F
                              );
                        }
                     )
                     .whenStarting(entity -> entity.triggerAnim("miscController", "whip_spin"))
                     .startCondition(entity -> !entity.isBuried() && entity.getRandom().nextInt(8) == 1),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(5.0F)
                     .performAttack((entity, target) -> entity.doHurtTarget(target, 2.0F))
                     .whenStarting(entity -> entity.triggerAnim("miscController", "whip_main"))
                     .startCondition(entity -> !entity.isBuried() && entity.getRandom().nextFloat() <= 0.5),
                  new AnimatableMeleeAttack(2)
                     .attackInterval(entity -> 10)
                     .whenStarting(entity -> entity.triggerAnim("miscController", entity.getRandom().nextBoolean() ? "whip_right" : "whip_left"))
                     .startCondition(entity -> !entity.isBuried())
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<PhantasporeEntity> state) {
      String name;
      if (this.isBuried() || !this.isAlive()) {
         name = "animation.phantaspore.buried";
      } else if (this.isDancing()) {
         name = "animation.phantaspore.dance";
      } else if (this.isInSittingPose()) {
         name = "animation.phantaspore.stay";
      } else if (state.isMoving()) {
         name = "animation.phantaspore.walk";
      } else {
         name = "animation.phantaspore.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController)
               .triggerableAnim("bury", RawAnimation.begin().then("animation.phantaspore.bury", LoopType.PLAY_ONCE))
               .triggerableAnim("leave", RawAnimation.begin().then("animation.phantaspore.leave_ground", LoopType.PLAY_ONCE))
               .triggerableAnim("ambush", RawAnimation.begin().then("animation.phantaspore.ambush", LoopType.PLAY_ONCE))
               .triggerableAnim("peek", RawAnimation.begin().then("animation.phantaspore.peek", LoopType.PLAY_ONCE)),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("whip_right", RawAnimation.begin().then("animation.phantaspore.whip_right", LoopType.PLAY_ONCE))
               .triggerableAnim("whip_left", RawAnimation.begin().then("animation.phantaspore.whip_left", LoopType.PLAY_ONCE))
               .triggerableAnim("whip_main", RawAnimation.begin().then("animation.phantaspore.whip_main", LoopType.PLAY_ONCE))
               .triggerableAnim("whip_spin", RawAnimation.begin().then("animation.phantaspore.whip_spin", LoopType.PLAY_ONCE))
               .triggerableAnim("poison", RawAnimation.begin().then("animation.phantaspore.poison", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   public boolean isDancing() {
      return this.dancing;
   }
}
