package io.github.manasmods.tensura.entity.multipart;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.LeapToTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.magic.breath.ParalysingBreathProjectile;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ILivingPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
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

public class EvilCentipedeEntity extends LivingMultipartHead implements GeoEntity, SmartBrainOwner<EvilCentipedeEntity>, ITensuraMount, INameEvolution {
   protected static final EntityDataAccessor<Integer> BREATHING = SynchedEntityData.defineId(EvilCentipedeEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int mountAbilityCooldown = 0;
   private UUID breathUUID = null;

   public EvilCentipedeEntity(EntityType<? extends EvilCentipedeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.JUMP_STRENGTH, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.STEP_HEIGHT, 3.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.1F);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(BREATHING, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Breathing", this.getBreathing());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setBreathing(compound.getInt("Breathing"));
   }

   public int getBreathing() {
      return (Integer)this.entityData.get(BREATHING);
   }

   public void setBreathing(int breathing) {
      this.entityData.set(BREATHING, breathing);
   }

   @Override
   public int getMaxHeadYRot() {
      return 3;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL)
         || source.type().effects().equals(DamageEffects.POKING)
         || source.is(DamageTypes.FALLING_BLOCK)
         || super.isInvulnerableTo(source);
   }

   public boolean canBeAffected(MobEffectInstance pEffectInstance) {
      return pEffectInstance.getEffect().equals(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS)) ? false : super.canBeAffected(pEffectInstance);
   }

   @Override
   public LivingMultipartBody createBody(LivingEntity parent, boolean tail) {
      EvilCentipedeBody body = new EvilCentipedeBody((EntityType<? extends EvilCentipedeBody>)MonsterEntityTypes.EVIL_CENTIPEDE_BODY.get(), parent);
      if (tail) {
         body.setEndSegment(true);
      }

      return body;
   }

   @Override
   protected boolean shouldUpdateRingBuffer() {
      return true;
   }

   @Override
   protected float calcPartRotation(int i) {
      float rot = this.isInLiquid() ? 40.0F : 20.0F;
      return (float)(rot * -Math.sin(this.walkDist * 3.0F - i));
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.mountAbilityCooldown > 0) {
            this.mountAbilityCooldown--;
         }

         if (this.getBreathing() > 0 && this.isAlive() && (this.getControllingPassenger() != null || this.getTarget() != null)) {
            this.setBreathing(this.getBreathing() - 1);
            if (this.breathUUID == null) {
               this.spawnParalyzingBreath();
            } else {
               this.level()
                  .playSound(
                     null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.BREATH_POISON.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F
                  );
            }
         } else if (this.breathUUID != null) {
            Entity breath = ((ServerLevel)this.level()).getEntity(this.breathUUID);
            if (breath instanceof ParalysingBreathProjectile) {
               breath.discard();
            }

            this.setBreathing(0);
            this.breathUUID = null;
         }
      }
   }

   private void spawnParalyzingBreath() {
      ParalysingBreathProjectile breath = new ParalysingBreathProjectile(this.level(), this);
      breath.setLife(120);
      breath.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0F);
      breath.setPos(this.position().add(0.0, this.getBbHeight() / 2.0F, 0.0));
      breath.setMpCost(50.0);
      breath.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)IntrinsicSkills.PARALYSING_BREATH.get()));
      this.level().addFreshEntity(breath);
      this.breathUUID = breath.getUUID();
   }

   @Override
   public boolean canActivateMountAbility(LivingEntity rider) {
      return !this.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_SKILL));
   }

   @Override
   public void mountAbility(Player rider) {
      if (!this.isBaby()) {
         if (this.getBreathing() > 0) {
            this.setBreathing(0);
            this.mountAbilityCooldown = 40;
         } else {
            this.setBreathing(80);
            this.mountAbilityCooldown = 120;
         }
      }
   }

   @Override
   public boolean doHurtTarget(@NotNull Entity entity) {
      if (super.doHurtTarget(entity)) {
         if (entity instanceof LivingEntity target && target.getLastHurtByMobTimestamp() == target.tickCount) {
            int para = target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS)) ? 1 : 0;
            target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 100, para, true, false, true), this);
         }

         this.gameEvent(GameEvent.ENTITY_INTERACT);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void evolve() {
      this.setSegmentCount(this.getSegmentCount() + this.random.nextInt(2, 6));
   }

   @Override
   public int getChestsAllowed() {
      return 0;
   }

   @Override
   public boolean isSaddleRequired() {
      return false;
   }

   @Override
   protected float getRiddenSpeed(Player player) {
      return super.getRiddenSpeed(player) * 0.3F;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, 0.0, -0.75 * f).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   @Override
   public InteractionResult getRidingInteraction(Player player, InteractionHand hand) {
      if (this.isSaddleable() && this.isRideable(player)) {
         this.doPlayerRide(player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "bite");
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      EvilCentipedeEntity centipede = (EvilCentipedeEntity)((EntityType)MonsterEntityTypes.EVIL_CENTIPEDE.get()).create(pLevel);
      if (centipede == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         centipede.setOwnerUUID(uuid);
         centipede.setTame(true, true);
      }

      return centipede;
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType type, @Nullable SpawnGroupData spawnGroupData
   ) {
      if (this.canRandomizeSpawnData(type)) {
         this.setSegmentCount(this.getRandom().nextInt(10, 13));
      }

      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, type, spawnGroupData);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.evilCentipede, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.CENTIPEDE_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return (SoundEvent)TensuraSoundEvents.CENTIPEDE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.CENTIPEDE_DEATH.get();
   }

   protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockIn) {
      this.playSound(SoundEvents.SPIDER_STEP, 1.0F, 2.0F);
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

   public List<ExtendedSensor<EvilCentipedeEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<EvilCentipedeEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new FloatToSurfaceOfFluid(), new LookAtTarget().startCondition(entity -> !entity.isOrderedToSit()), TensuraTamableEntity.getMoveToWalkTarget()
         }
      );
   }

   public BrainActivityGroup<EvilCentipedeEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this),
                  new SubordinateFollowOwner()
                     .stopFollowingWithin((entity, owner) -> owner.getVehicle() instanceof ILivingPartEntity part && part.getHead() == entity ? 32.0 : 8.0),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<EvilCentipedeEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new CustomRangeAttack(0)
               .maxAttackRadius(12.0F)
               .attackInterval(entity -> 160)
               .performAttack((entity, target) -> entity.setBreathing(80))
               .startCondition(entity -> !entity.isBaby() && entity.getRandom().nextInt(10) == 1),
            new AnimatableMeleeAttack(1).attackInterval(entity -> 0).whenStarting(entity -> entity.triggerAnim("miscController", "bite")),
            new LeapToTarget(10)
               .moveSpeedContribution((dog, entity) -> 2.0F)
               .jumpStrength((entity, target) -> entity.getJumpPower(1.0F))
               .attackInterval(entity -> 100)
               .startCondition(entity -> entity.getTarget() != null && !entity.isWithinMeleeAttackRange(entity.getTarget()))
         }
      );
   }

   protected PlayState loopController(AnimationState<EvilCentipedeEntity> state) {
      String name;
      if (this.getBreathing() > 0) {
         name = "animation.evil_centipede.breath";
      } else if (state.isMoving()) {
         name = "animation.evil_centipede.walk";
      } else {
         name = "animation.evil_centipede.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 5, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.evil_centipede.bite", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
