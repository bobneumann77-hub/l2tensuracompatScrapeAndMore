package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class BladeTigerEntity extends TensuraMountEntity implements GeoEntity, SmartBrainOwner<BladeTigerEntity>, ITensuraMount {
   private static final EntityDataAccessor<Boolean> WHITE = SynchedEntityData.defineId(BladeTigerEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int mountAbilityCooldown = 0;

   public BladeTigerEntity(EntityType<? extends BladeTigerEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.MAX_HEALTH, 100.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
         .add(Attributes.JUMP_STRENGTH, 1.5)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 3.0)
         .add(Attributes.STEP_HEIGHT, 1.5)
         .add(Attributes.SAFE_FALL_DISTANCE, 7.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(WHITE, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("White", this.isWhite());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setWhite(compound.getBoolean("White"));
   }

   public boolean isWhite() {
      return (Boolean)this.entityData.get(WHITE);
   }

   public void setWhite(boolean white) {
      this.entityData.set(WHITE, white);
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   @Override
   public int getChestSlots() {
      return 20;
   }

   @Override
   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      this.playBlockFallSound();
      return false;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.type().effects().equals(DamageEffects.POKING) || super.isInvulnerableTo(source);
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.mountAbilityCooldown > 0) {
            this.mountAbilityCooldown--;
         }
      }
   }

   public void areaAttack(double multiplier, float strength, float range) {
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.SWEEP_ATTACK, 2.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.SWEEP_ATTACK, 3.0);
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, TensuraSkill.ABILITY_SOUND, 1.5F, 1.0F);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            this.getBoundingBox().inflate(range),
            entity -> !entity.isAlliedTo(this)
               && entity != this.getOwner()
               && !entity.equals(this)
               && (entity.getType() != this.getType() || entity == this.getTarget())
         );
      if (!list.isEmpty()) {
         for (LivingEntity target : list) {
            target.hurt(this.damageSources().mobAttack(this), (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier));
            SkillHelper.knockBack(this, target, strength);
         }
      }
   }

   public void doTailPierce(Entity target) {
      float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F;
      if (target.hurt(this.damageSources().mobAttack(this), f)) {
         if (target instanceof Player player && player.isBlocking()) {
            player.getCooldowns().addCooldown(player.getUseItem().getItem(), 100);
            this.level().broadcastEntityEvent(player, (byte)30);
         }

         this.setLastHurtMob(target);
         TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.CRIT, 1.0);
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, TensuraSkill.ABILITY_SOUND, 1.5F, 1.0F);
      }
   }

   public void voiceCannon(Vec3 targetPos) {
      Level level = this.level();
      if (!level.isClientSide()) {
         Vec3 source = this.getEyePosition().add(this.getLookAngle().scale(2.0));
         Vec3 sourceToTarget = targetPos.subtract(source);
         Vec3 normalizes = sourceToTarget.normalize();
         level.playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.COERCION.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);

         for (int particleIndex = 1; particleIndex < Mth.floor(sourceToTarget.length()); particleIndex++) {
            Vec3 particlePos = source.add(normalizes.scale(particleIndex));
            TensuraParticleHelper.spawnServerParticles(level, TensuraParticleUtils.getColorlessSonic(0.875F, 2.0F), particlePos.x, particlePos.y, particlePos.z);
            AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos)).inflate(2.0);
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, livingx -> !livingx.is(this) && !livingx.isAlliedTo(this));
            if (!list.isEmpty()) {
               for (LivingEntity living : list) {
                  DamageSource damagesource = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.SOUND_BLAST, this)
                     .tensura$setMagiculeCost(500.0)
                     .tensura$setAbilityInstance(SkillUtils.getSkillOrNull(this, (ManasSkill)CommonSkills.VOICE_CANNON.get()));
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
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      BladeTigerEntity tiger = (BladeTigerEntity)((EntityType)MonsterEntityTypes.BLADE_TIGER.get()).create(pLevel);
      if (tiger == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         tiger.setOwnerUUID(uuid);
         tiger.setTame(true, true);
      }

      if (this.isWhite() && ((BladeTigerEntity)pOtherParent).isWhite()) {
         tiger.setWhite(true);
      } else if (this.isWhite() || ((BladeTigerEntity)pOtherParent).isWhite()) {
         tiger.setWhite(pLevel.getRandom().nextBoolean());
      }

      return tiger;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, 0.0, -0.5 * f).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   @Override
   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
      Vec3 riddenInput = this.getRiddenInput(controller, vec3);
      if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > this.getFluidJumpThreshold() && riddenInput.z() > 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.03, 0.0));
      }
   }

   @Override
   public boolean canActivateMountAbility(LivingEntity rider) {
      return !this.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_SKILL));
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAbilityCooldown <= 0) {
         this.mountAbilityCooldown = 20;
         this.triggerAnim("miscController", "roar");
         this.voiceCannon(this.getEyePosition().add(rider.getLookAngle().scale(20.0)));
      }
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.bladeTiger, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      if (this.canRandomizeSpawnData(mobSpawnType) && serverLevelAccessor.getRandom().nextInt(100) == 69) {
         this.setWhite(true);
      }

      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.TIGER_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.TIGER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.TIGER_DEATH.get();
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

   public List<ExtendedSensor<BladeTigerEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<BladeTigerEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<BladeTigerEntity> getIdleTasks() {
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
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<BladeTigerEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(5)
                     .requireInSight(false)
                     .maxAttackRadius(20.0F)
                     .minAttackRadius(5.0F)
                     .attackInterval(entity -> 100)
                     .performAttack((entity, target) -> entity.voiceCannon(this.getEyePosition().add(this.getLookAngle().scale(20.0))))
                     .whenStarting(entity -> entity.triggerAnim("miscController", "roar"))
                     .startCondition(tiger -> tiger.getRandom().nextInt(10) == 1 && SkillUtils.hasSkill(tiger, (ManasSkill)CommonSkills.VOICE_CANNON.get())),
                  new CustomRangeAttack(12)
                     .maxAttackRadius(4.0F)
                     .attackInterval(entity -> 60)
                     .performAttack((entity, target) -> entity.areaAttack(1.5, 1.0F, 4.0F))
                     .whenStarting(entity -> entity.triggerAnim("swingController", "tail_swing"))
                     .startCondition(tiger -> tiger.getRandom().nextInt(10) == 1),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(4.0F)
                     .attackInterval(entity -> 80)
                     .performAttack(BladeTigerEntity::doTailPierce)
                     .whenStarting(entity -> entity.triggerAnim("attackController", "tail_pierce"))
                     .startCondition(tiger -> tiger.getRandom().nextInt(10) == 1),
                  new AnimatableMeleeAttack(10)
                     .attackInterval(entity -> 40)
                     .whenStarting(
                        entity -> {
                           entity.triggerAnim("attackController", "strike");
                           entity.playSound(
                              (SoundEvent)TensuraSoundEvents.TIGER_ATTACK.get(),
                              entity.getSoundVolume(),
                              (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + 1.0F
                           );
                        }
                     )
                     .startCondition(tiger -> tiger.getRandom().nextInt(5) == 1),
                  new AnimatableMeleeAttack(1).attackInterval(entity -> 1).whenStarting(entity -> entity.triggerAnim("miscController", "bite"))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<BladeTigerEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.blade_tiger.sleep";
      } else if (this.isInSittingPose()) {
         name = "animation.blade_tiger.sit";
      } else if (state.isMoving()) {
         if (this.isInLiquid()
            || !this.isAngry() && !this.isSprinting() && (this.getControllingPassenger() == null || !this.getControllingPassenger().isSprinting())) {
            name = "animation.blade_tiger.walk";
         } else {
            name = "animation.blade_tiger.run";
         }
      } else {
         name = "animation.blade_tiger.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.blade_tiger.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("roar", RawAnimation.begin().then("animation.blade_tiger.roar", LoopType.PLAY_ONCE))
               .triggerableAnim("eat", RawAnimation.begin().then("animation.blade_tiger.eat", LoopType.PLAY_ONCE)),
            new AnimationController(this, "attackController", 0, event -> PlayState.STOP)
               .triggerableAnim("strike", RawAnimation.begin().then("animation.blade_tiger.strike", LoopType.PLAY_ONCE))
               .triggerableAnim("tail_pierce", RawAnimation.begin().then("animation.blade_tiger.tail_pierce", LoopType.PLAY_ONCE)),
            new AnimationController(this, "swingController", 0, event -> PlayState.STOP)
               .triggerableAnim("tail_swing", RawAnimation.begin().then("animation.blade_tiger.tail_swing", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
