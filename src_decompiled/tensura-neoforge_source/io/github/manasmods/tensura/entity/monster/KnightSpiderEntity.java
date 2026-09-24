package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.LeapToTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.navigator.ClimbingNavigator;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IClimbing;
import io.github.manasmods.tensura.entity.template.subclass.IGiantMob;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class KnightSpiderEntity extends TensuraMountEntity implements GeoEntity, SmartBrainOwner<KnightSpiderEntity>, IClimbing, IGiantMob, ITensuraMount {
   private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(KnightSpiderEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> SLAMMING_FALL = SynchedEntityData.defineId(KnightSpiderEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public KnightSpiderEntity(EntityType<? extends KnightSpiderEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @NotNull
   @Override
   protected PathNavigation createNavigation(Level level) {
      return new ClimbingNavigator(this, level);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 8.0)
         .add(Attributes.MAX_HEALTH, 90.0)
         .add(Attributes.ATTACK_DAMAGE, 22.0)
         .add(Attributes.MOVEMENT_SPEED, 0.35F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.9F)
         .add(Attributes.JUMP_STRENGTH, 2.0)
         .add(Attributes.STEP_HEIGHT, 5.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CLIMBING, Boolean.FALSE);
      builder.define(SLAMMING_FALL, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Climbing", this.isClimbing());
      compound.putBoolean("SlammingFall", this.isSlammingFall());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setClimbing(compound.getBoolean("Climbing"));
      this.setSlammingFall(compound.getBoolean("SlammingFall"));
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

   public boolean isSlammingFall() {
      return (Boolean)this.entityData.get(SLAMMING_FALL);
   }

   public void setSlammingFall(boolean falling) {
      this.entityData.set(SLAMMING_FALL, falling);
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.CACTUS) || source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
      if (!pState.is(TensuraBlockTags.WEB_BLOCKS)) {
         super.makeStuckInBlock(pState, pMotionMultiplier);
      }
   }

   protected float getBlockSpeedFactor() {
      BlockState blockstate = this.level().getBlockState(this.blockPosition());
      return blockstate.is(TensuraBlockTags.WEB_BLOCKS) ? 1.0F : super.getBlockSpeedFactor();
   }

   protected float getBlockJumpFactor() {
      BlockState blockstate = this.level().getBlockState(this.blockPosition());
      return blockstate.is(TensuraBlockTags.WEB_BLOCKS) ? 1.0F : super.getBlockJumpFactor();
   }

   @Override
   public float getClimbSpeedMultiplier() {
      return this.isBaby() ? 0.75F : 1.5F;
   }

   protected float getJumpPower() {
      return 0.0F;
   }

   @Override
   public int getChestsAllowed() {
      return 4;
   }

   @Override
   public int getMenuRenderSize() {
      return 4;
   }

   @Override
   public void tick() {
      super.tick();
      this.handleClimbing(this);
      if (this.isSlammingFall() && (this.isInLiquid() || this.onGround())) {
         this.setSlammingFall(false);
      }

      if (!this.level().isClientSide()) {
         LivingEntity controller = this.getControllingPassenger();
         if (!this.isTame() || controller != null && this.isOwnedBy(controller)) {
            this.breakBlocks(this, 1.0F, false);
         }
      }
   }

   @Override
   public void handleClimbing(LivingEntity entity) {
      if (!entity.level().isClientSide()) {
         this.setClimbing(this.collidingWall(entity));
      }

      if (entity.onClimbable()) {
         this.setSlammingFall(false);
         if (this.collidingWall(entity)) {
            entity.setDeltaMovement(entity.getDeltaMovement().x, entity.getDeltaMovement().y * this.getClimbSpeedMultiplier(), entity.getDeltaMovement().z);
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
         if (!this.onGround() && !this.isInLiquid() && !this.isSlammingFall()) {
            this.setSlammingFall(Boolean.TRUE);
            this.triggerAnim("jumpController", "jump");
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
               );
         } else {
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(rider, 8.0, false);
            if (target != null) {
               this.triggerAnim("miscController", "bite");
               this.doHurtTarget(target);
            }
         }
      }
   }

   @Override
   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      if (pFallDistance >= 5.0F) {
         this.triggerAnim("jumpController", "land");
         if (this.isSlammingFall()) {
            this.setSlammingFall(Boolean.FALSE);
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            TensuraParticleHelper.spawnServerParticles(
               this.level(), TensuraParticleUtils.getColorlessWave(0.9F, 7.0F), this.getX(), this.getY() + 0.2F, this.getZ()
            );
            EffectStorage.setCameraShake(this, 16.0, 0.02F, 15);
            AABB aabb = AABB.ofSize(this.position(), 16.0, 16.0, 16.0);
            List<LivingEntity> list = this.level()
               .getEntitiesOfClass(
                  LivingEntity.class, aabb, entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && !(entity instanceof KnightSpiderEntity)
               );
            float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);

            for (LivingEntity target : list) {
               target.hurt(this.damageSources().mobAttack(this), Math.min(pFallDistance * 2.0F, damage * 3.0F));
               SkillHelper.knockBack(this, target, 1.0F);
            }

            if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
               SkillHelper.launchBlock(
                  this,
                  this.position(),
                  7,
                  1,
                  0.5F,
                  0.3F,
                  blockState -> this.getRandom().nextInt(3) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE),
                  blockPos -> true
               );
            }

            return false;
         }
      }

      return false;
   }

   public boolean isDamageSourceBlocked(DamageSource damageSource) {
      if (!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
         && damageSource.is(TensuraTags.DamageTypes.IS_PHYSICAL)
         && !damageSource.isCreativePlayer()
         && damageSource.getDirectEntity() != null
         && this.level().getRandom().nextInt(5) == 1) {
         this.triggerAnim("miscController", this.level().getRandom().nextBoolean() ? "right_dash" : "left_dash");
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, this.getJumpPower(0.5F), 0.0));
         this.level()
            .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(), TensuraSkill.ABILITY_SOUND, 0.25F, 3.0F);
         return true;
      } else {
         return super.isDamageSourceBlocked(damageSource);
      }
   }

   @Override
   public int getRiderSeats() {
      return 4;
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      int i = Math.max(this.getPassengers().indexOf(entity), 0);

      Vec3 vec3 = switch (i) {
         case 1 -> new Vec3(0.0, -0.4F, 0.25 * f);
         case 2 -> new Vec3(0.0, 0.1, -2.0F * f);
         case 3 -> new Vec3(0.0, 0.1, -4.0F * f);
         default -> new Vec3(0.0, -0.75, 1.5 * f);
      };
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3.yRot(-this.getYRot() * 0.0174F));
   }

   @Override
   public void handleStartJump(int i) {
      super.handleStartJump(i);
      if (this.canExecuteRidersJump()) {
         this.triggerAnim("jumpController", "jump");
      }
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.has(DataComponents.FOOD);
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "bite");
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      KnightSpiderEntity spider = (KnightSpiderEntity)((EntityType)MonsterEntityTypes.KNIGHT_SPIDER.get()).create(pLevel);
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

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.knightSpider, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
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

   public List<ExtendedSensor<KnightSpiderEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<KnightSpiderEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<KnightSpiderEntity> getIdleTasks() {
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

   public BrainActivityGroup<KnightSpiderEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(3).maxAttackRadius(10.0F).minAttackRadius(2.0F).attackInterval(entity -> 100).performAttack((entity, target) -> {
                     entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, entity.getJumpPower(1.0F), 0.0));
                     entity.setSlammingFall(true);
                  }).whenStarting(entity -> entity.triggerAnim("jumpController", "jump")).startCondition(entity -> {
                     if (entity.isSlammingFall()) {
                        return false;
                     }

                     if (entity.isInLiquid()) {
                        return false;
                     }

                     LivingEntity target = entity.getTarget();
                     return target != null && target.getHealth() < entity.getAttributeValue(Attributes.ATTACK_DAMAGE)
                        ? false
                        : entity.getRandom().nextInt(20) == 1;
                  }),
                  new AnimatableMeleeAttack(5).whenStarting(entity -> entity.triggerAnim("miscController", "bite")),
                  new LeapToTarget(5)
                     .leapRange((entity, target) -> 32.0F)
                     .moveSpeedContribution((dog, entity) -> 2.0F)
                     .jumpStrength((entity, target) -> entity.getJumpPower(1.0F))
                     .attackInterval(entity -> 30)
                     .whenStarting(entity -> entity.triggerAnim("jumpController", "leap"))
                     .startCondition(entity -> entity.getTarget() != null && !entity.isWithinMeleeAttackRange(entity.getTarget()))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<KnightSpiderEntity> state) {
      String name;
      if (this.isDeadOrDying()) {
         name = "animation.knight_spider.dead";
      } else if (this.isSleeping()) {
         name = "animation.knight_spider.sleep";
      } else if (this.isInSittingPose()) {
         name = "animation.knight_spider.stay";
      } else if (!this.onGround()) {
         name = "animation.knight_spider.falling";
      } else if (state.isMoving()) {
         name = "animation.knight_spider.walk";
      } else {
         name = "animation.knight_spider.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 8, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.knight_spider.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("right_dash", RawAnimation.begin().then("animation.knight_spider.right_dash", LoopType.PLAY_ONCE))
               .triggerableAnim("left_dash", RawAnimation.begin().then("animation.knight_spider.left_dash", LoopType.PLAY_ONCE)),
            new AnimationController(this, "jumpController", 3, event -> PlayState.STOP)
               .triggerableAnim("leap", RawAnimation.begin().then("animation.knight_spider.leap", LoopType.PLAY_ONCE))
               .triggerableAnim("jump", RawAnimation.begin().then("animation.knight_spider.jump", LoopType.PLAY_ONCE))
               .triggerableAnim("land", RawAnimation.begin().then("animation.knight_spider.land", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
