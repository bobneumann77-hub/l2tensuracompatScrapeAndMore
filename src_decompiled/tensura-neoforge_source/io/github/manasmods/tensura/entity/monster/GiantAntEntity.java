package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.SpittingRangedMonsterAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.navigator.ClimbingNavigator;
import io.github.manasmods.tensura.entity.projectile.MonsterSpitProjectile;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IClimbing;
import io.github.manasmods.tensura.entity.template.subclass.IGiantMob;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.entity.template.subclass.SpittingRangedMonster;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
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
import org.jetbrains.annotations.NotNull;
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

public class GiantAntEntity
   extends TensuraMountEntity
   implements GeoEntity,
   SmartBrainOwner<GiantAntEntity>,
   IClimbing,
   IGiantMob,
   ITensuraMount,
   SpittingRangedMonster {
   private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(GiantAntEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int mountAbilityCooldown = 0;

   public GiantAntEntity(EntityType<? extends GiantAntEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @NotNull
   @Override
   protected PathNavigation createNavigation(Level level) {
      return new ClimbingNavigator(this, level);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 4.0)
         .add(Attributes.MAX_HEALTH, 45.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.MOVEMENT_SPEED, 0.32F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.8F)
         .add(Attributes.JUMP_STRENGTH, 1.25)
         .add(Attributes.STEP_HEIGHT, 3.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CLIMBING, Boolean.FALSE);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Climbing", this.isClimbing());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setClimbing(compound.getBoolean("Climbing"));
   }

   @Override
   public boolean isClimbing() {
      return (Boolean)this.entityData.get(CLIMBING);
   }

   @Override
   public void setClimbing(boolean climbing) {
      this.entityData.set(CLIMBING, climbing);
   }

   public boolean onClimbable() {
      return this.isClimbing();
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   public void push(Entity pEntity) {
      if (!pEntity.getType().equals(this.getType())) {
         super.push(pEntity);
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.SWEET_BERRY_BUSH) || super.isInvulnerableTo(source);
   }

   @Override
   public float getClimbSpeedMultiplier() {
      return this.isBaby() ? 1.0F : 1.5F;
   }

   protected float getJumpPower() {
      return 0.0F;
   }

   @Override
   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   @Override
   public int getChestsAllowed() {
      return 6;
   }

   @Override
   public int getMenuRenderSize() {
      return 8;
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.giantAnt, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @Override
   public void tick() {
      super.tick();
      this.handleClimbing(this);
      if (this.mountAbilityCooldown > 0) {
         this.getNavigation().stop();
         if (this.mountAbilityCooldown-- == 5) {
            LivingEntity owner = this.getControllingPassenger();
            if (owner != null) {
               BlockHitResult hitResult = ObjectSelectionHelper.getPlayerPOVHitResult(this.level(), owner, Fluid.NONE, 30.0);
               this.performRangedAttack(hitResult.getBlockPos(), this.isBaby() ? 2.0 : 4.0, this.isBaby() ? 1.0 : 2.0);
               this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }
         }
      }

      if (!this.level().isClientSide()) {
         LivingEntity controller = this.getControllingPassenger();
         if (!this.isTame() || controller != null && this.isOwnedBy(controller)) {
            this.breakBlocks(this, 1.0F, false);
         }
      }
   }

   @Override
   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
      this.handleRideableClimbing(this, vec3);
   }

   @Override
   public void mountAbility(Player rider) {
      if (!this.isBaby()) {
         if (this.mountAbilityCooldown <= 0) {
            this.mountAbilityCooldown = 15;
            this.triggerAnim("miscController", "spit");
         }
      }
   }

   @Override
   public void spitParticle(MonsterSpitProjectile projectile) {
      this.particleSpawning(projectile, new DustParticleOptions(new Vector3f(0.0F, 1.0F, 0.0F), 1.0F), 30);
      TensuraParticleHelper.spawnServerParticles(
         this.level(), TensuraParticleUtils.getAcidEffect(), projectile.getX(), projectile.getY(), projectile.getZ(), 15, 0.08, 0.08, 0.08, 0.15, true
      );
   }

   @Override
   public void spitHit(LivingEntity pTarget) {
      if (!(pTarget instanceof GiantAntEntity)) {
         pTarget.hurt(this.damageSources().mobAttack(this), 12.0F);
      }
   }

   @Override
   public void impactEffect(MonsterSpitProjectile spit, double x, double y, double z) {
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            spit.getBoundingBox().inflate(3.0),
            entityData -> !entityData.isAlliedTo(this) && !entityData.is(this) && entityData != this.getOwner()
         );
      if (!list.isEmpty()) {
         for (LivingEntity pTarget : list) {
            pTarget.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 300, 0, true, false, true), this);
            if (this.getRandom().nextInt(10) >= 8) {
               pTarget.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 300, 0, true, false, true), this);
            } else {
               pTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 1, true, false, true), this);
            }
         }
      }
   }

   public void areaAttack() {
      TensuraParticleHelper.spawnGroundSlamParticle(this, 5, 5.0F);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getColorlessWave(0.9F, 5.0F), this.getX(), this.getY() + 0.2F, this.getZ());
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      AABB aabb = this.getBoundingBox().inflate(5.0);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            aabb,
            entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && !entity.equals(this) && !(entity instanceof GiantAntEntity)
         );
      if (!list.isEmpty()) {
         double damageMultiplier = 2.0;

         for (LivingEntity target : list) {
            target.hurt(
               this.damageSources().mobAttack(this).tensura$setMagiculeCost(20.0), (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier)
            );
            target.getDeltaMovement().add(0.0, 0.5 * damageMultiplier, 0.0);
         }
      }
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, -0.5, 1.0F * f).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.ANT_FOOD) || pStack.has(DataComponents.FOOD);
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      GiantAntEntity spider = (GiantAntEntity)((EntityType)MonsterEntityTypes.GIANT_ANT.get()).create(pLevel);
      if (spider == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         spider.setOwnerUUID(uuid);
         spider.setTame(true, true);
      }

      return spider;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SPIDER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.SPIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SPIDER_DEATH;
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

   public List<ExtendedSensor<GiantAntEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<GiantAntEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<GiantAntEntity> getIdleTasks() {
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
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60)).whenStarting(entity -> {
                     if (!entity.isSleeping()) {
                        if (entity.getRandom().nextFloat() <= 0.01) {
                           entity.triggerAnim("miscController", "clean");
                        }
                     }
                  })}
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<GiantAntEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 1.2F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new SpittingRangedMonsterAttack(13)
                     .requireInSight(false)
                     .attackRadius(25.0F)
                     .attackInterval(entity -> 100)
                     .performAttack((entity, target) -> {
                        entity.performRangedAttack(target, entity.isBaby() ? 2.0 : 4.0, entity.isBaby() ? 1.0 : 2.0);
                        entity.level()
                           .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LLAMA_SPIT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                     })
                     .whenStarting(entity -> entity.triggerAnim("miscController", "spit"))
                     .startCondition(entity -> !entity.isBaby() && entity.getRandom().nextInt(10) == 1),
                  new CustomRangeAttack(17)
                     .maxAttackRadius(7.0F)
                     .attackInterval(entity -> 60)
                     .performAttack((entity, target) -> entity.areaAttack())
                     .whenStarting(entity -> entity.triggerAnim("miscController", "slam"))
                     .startCondition(entity -> !entity.isBaby() && entity.getRandom().nextInt(10) == 1 && entity.getControllingPassenger() == null),
                  new AnimatableMeleeAttack(3).whenStarting(entity -> entity.triggerAnim("miscController", "bite"))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<GiantAntEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.giant_ant.sleep";
      } else if (this.isInSittingPose()) {
         name = "animation.giant_ant.stay";
      } else if (state.isMoving()) {
         if (this.isAngry()) {
            name = "animation.giant_ant.search";
         } else {
            name = "animation.giant_ant.walk";
         }
      } else {
         name = "animation.giant_ant.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("eat", RawAnimation.begin().then("animation.giant_ant.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("bite", RawAnimation.begin().then("animation.giant_ant.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("spit", RawAnimation.begin().then("animation.giant_ant.spit", LoopType.PLAY_ONCE))
               .triggerableAnim("clean", RawAnimation.begin().then("animation.giant_ant.clean", LoopType.PLAY_ONCE))
               .triggerableAnim("slam", RawAnimation.begin().then("animation.giant_ant.slam", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
