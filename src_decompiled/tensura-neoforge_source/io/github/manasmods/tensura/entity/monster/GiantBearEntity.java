package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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

public class GiantBearEntity extends TensuraMountEntity implements GeoEntity, SmartBrainOwner<GiantBearEntity>, ITensuraMount {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int mountAbilityCooldown = 0;

   public GiantBearEntity(EntityType<? extends GiantBearEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 18.0)
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.ARMOR, 8.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.4F)
         .add(Attributes.JUMP_STRENGTH, 1.0)
         .add(Attributes.STEP_HEIGHT, 1.5)
         .add(Attributes.SAFE_FALL_DISTANCE, 9.0);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (DATA_FLAGS_ID.equals(pKey) || DATA_SHARED_FLAGS_ID.equals(pKey)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   @NotNull
   @Override
   public EntityDimensions getDefaultDimensions(Pose pPose) {
      EntityDimensions entitydimensions = super.getDefaultDimensions(pPose);
      if (this.isSprinting() && this.onGround()) {
         return entitydimensions.scale(1.0F, 0.6F);
      } else {
         return !this.isInSittingPose() && !this.isOrderedToSit() ? entitydimensions : entitydimensions.scale(1.0F, 0.6666667F);
      }
   }

   @Override
   public boolean hasSaddleSlot() {
      return false;
   }

   @Override
   public boolean isSaddleRequired() {
      return false;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.type().effects().equals(DamageEffects.POKING) || super.isInvulnerableTo(source);
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.giantBear, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.mountAbilityCooldown > 0 && this.mountAbilityCooldown-- == 5) {
            this.areaAttack(2.0F, 4.0F);
         }
      }
   }

   public void areaAttack(float multiplier, float radius) {
      TensuraParticleHelper.spawnGroundSlamParticle(this, (int)radius, radius / 2.0F);
      TensuraParticleHelper.spawnServerParticles(
         this.level(), TensuraParticleUtils.getColorlessWave(0.9F, radius / 2.0F), this.getX(), this.getY() + 0.2F, this.getZ()
      );
      this.level()
         .playSound(
            null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(), TensuraSkill.ABILITY_SOUND, multiplier, 1.0F
         );
      AABB aabb = this.getBoundingBox().inflate(radius);
      List<LivingEntity> targets = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            aabb,
            entity -> !entity.isAlliedTo(this)
               && !entity.equals(this.getOwner())
               && !entity.equals(this)
               && (!(entity instanceof GiantBearEntity) || entity == this.getTarget())
         );
      if (!targets.isEmpty()) {
         for (LivingEntity target : targets) {
            target.hurt(this.damageSources().mobAttack(this), (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier));
            target.getDeltaMovement().add(0.0, 0.5 * multiplier, 0.0);
         }
      }
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAbilityCooldown <= 0) {
         this.mountAbilityCooldown = 20;
         this.triggerAnim("slamController", "slam_both_hand");
      }
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      GiantBearEntity bear = (GiantBearEntity)((EntityType)MonsterEntityTypes.GIANT_BEAR.get()).create(pLevel);
      if (bear == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         bear.setOwnerUUID(uuid);
         bear.setTame(true, true);
      }

      return bear;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.BEAR_FOOD);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return this.isFood(pStack);
   }

   @Override
   protected boolean doPlayerRide(Player player) {
      if (super.doPlayerRide(player)) {
         this.triggerAnim("miscController", "pick_up");
         return true;
      } else {
         return false;
      }
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   @Override
   public int getRiderSeats() {
      return 2;
   }

   @Override
   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
      Vec3 riddenInput = this.getRiddenInput(controller, vec3);
      if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > this.getFluidJumpThreshold() && riddenInput.z() > 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.03, 0.0));
      }
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      int i = Math.max(this.getPassengers().indexOf(entity), 0);
      float forward = this.isSprinting() ? 0.1F : -0.1F;
      Vec3 vec3 = new Vec3(i == 0 ? -0.6 : 0.6, -0.4, forward * f).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.BEAR_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.BEAR_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.BEAR_DEATH.get();
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

   public List<ExtendedSensor<GiantBearEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<GiantBearEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<GiantBearEntity> getIdleTasks() {
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
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(40, 60)).whenStarting(entity -> {
                     if (!entity.isVehicle() && !entity.isSleeping()) {
                        if (!(entity.getDeltaMovement().length() > 0.1) && !entity.getMoveControl().hasWanted()) {
                           if (entity.getRandom().nextFloat() <= 0.01) {
                              entity.triggerAnim("loopController", "flex");
                           }
                        }
                     }
                  })}
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<GiantBearEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(14)
                     .maxAttackRadius(5.0F)
                     .attackInterval(entity -> 60)
                     .performAttack((entity, target) -> entity.areaAttack(2.0F, 4.0F))
                     .whenStarting(
                        entity -> {
                           entity.triggerAnim("loopController", "slam_both_hand");
                           entity.playSound(
                              (SoundEvent)TensuraSoundEvents.BEAR_ATTACK.get(),
                              entity.getSoundVolume(),
                              (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + 1.0F
                           );
                        }
                     )
                     .startCondition(tiger -> tiger.getRandom().nextInt(8) == 1),
                  new AnimatableMeleeAttack(10)
                     .attackInterval(entity -> 1)
                     .whenStarting(entity -> entity.triggerAnim("miscController", entity.getRandom().nextBoolean() ? "right_punch" : "left_punch"))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<GiantBearEntity> state) {
      String name;
      if (this.isInSittingPose()) {
         if (this.getBehaviour() == 1) {
            name = "animation.giant_bear.sit";
         } else {
            name = "animation.giant_bear.stay";
         }
      } else if (state.isMoving()) {
         if (this.isInLiquid() || !this.isAngry() && !this.isSprinting()) {
            name = "animation.giant_bear.walk";
         } else {
            name = "animation.giant_bear.run";
         }
      } else if (this.getHealth() < this.getMaxHealth() / 4.0F) {
         name = "animation.giant_bear.idle_hurt";
      } else {
         name = "animation.giant_bear.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController)
               .triggerableAnim("flex", RawAnimation.begin().then("animation.giant_bear.flex", LoopType.PLAY_ONCE))
               .triggerableAnim("slam_both_hand", RawAnimation.begin().then("animation.giant_bear.slam_both_hand", LoopType.PLAY_ONCE)),
            new AnimationController(this, "miscController", 2, event -> PlayState.STOP)
               .triggerableAnim("eat", RawAnimation.begin().then("animation.giant_bear.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("pick_up", RawAnimation.begin().then("animation.giant_bear.pick_up_item", LoopType.PLAY_ONCE))
               .triggerableAnim("right_punch", RawAnimation.begin().then("animation.giant_bear.right_punch", LoopType.PLAY_ONCE))
               .triggerableAnim("left_punch", RawAnimation.begin().then("animation.giant_bear.left_punch", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
