package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.WaterJumpAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.JumpOutOfWater;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.SwimToWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetSwimTargetToAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.entity.template.subclass.ISwimmingJumper;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomSwimTarget;
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

public class SpearToroEntity extends TensuraMountEntity implements GeoEntity, SmartBrainOwner<SpearToroEntity>, ITensuraMount, ISwimmingJumper {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int flopTick = 0;

   public SpearToroEntity(EntityType<? extends SpearToroEntity> type, Level level) {
      super(type, level);
      this.initSwimming(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 18.0)
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.ARMOR, 4.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
         .add(Attributes.JUMP_STRENGTH, 2.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.5);
   }

   @Override
   public void switchLookControl(LookControl control) {
      this.lookControl = control;
   }

   @Override
   public void switchMoveControl(MoveControl control) {
      this.moveControl = control;
   }

   @Override
   public void switchNavigation(PathNavigation navigation) {
      this.navigation = navigation;
   }

   public int getMaxAirSupply() {
      return 1000;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
      return levelReader.getFluidState(blockPos).is(FluidTags.WATER)
         ? 10.0F + levelReader.getPathfindingCostFromLightLevels(blockPos)
         : super.getWalkTargetValue(blockPos, levelReader);
   }

   public void baseTick() {
      int i = this.getAirSupply();
      super.baseTick();
      this.handleAirSupply(this, i);
   }

   @Override
   public void tick() {
      super.tick();
      this.spawnSwimmingParticle(this);
   }

   @Override
   public void aiStep() {
      this.handleFlopping(this);
      super.aiStep();
   }

   public void slashAttack(LivingEntity controller) {
      this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
      AABB aabb = this.getBoundingBox().move(controller.getViewVector(1.0F).scale(this.isBaby() ? 2.0 : 4.0)).inflate(2.0);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            aabb,
            entity -> !entity.isAlliedTo(this)
               && entity != this.getOwner()
               && !entity.equals(this)
               && (!(entity instanceof SpearToroEntity) || entity == this.getTarget())
         );
      if (!list.isEmpty()) {
         for (LivingEntity target : list) {
            target.hurt(this.damageSources().mobAttack(this), (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5));
            SkillHelper.knockBack(this, target, 1.0F);
         }
      }
   }

   @Override
   public void mountAbility(Player rider) {
      LivingEntity controller = this.getControllingPassenger();
      if (controller != null) {
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(controller, 10.0, true);
         if (target != null) {
            this.setTarget(target);
         }

         this.triggerAnim("miscController", "slash");
         this.slashAttack(controller);
         double d0 = -Mth.sin(rider.getYRot() * (float) (Math.PI / 180.0));
         double d1 = Mth.cos(rider.getYRot() * (float) (Math.PI / 180.0));
         if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, rider.getX() + d0 * 3.0, this.getY(0.5), rider.getZ() + d1 * 3.0, 0, d0, 0.0, d1, 0.0);
         }
      }
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      SpearToroEntity fish = (SpearToroEntity)((EntityType)MonsterEntityTypes.SPEAR_TORO.get()).create(pLevel);
      if (fish == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         fish.setOwnerUUID(uuid);
         fish.setTame(true, true);
      }

      return fish;
   }

   @Override
   protected boolean canExecuteRidersJump() {
      return this.isInWaterOrBubble() && this.canJumpOutOfWater(this, 6, 1, false);
   }

   @Override
   public void onPlayerJump(int i) {
      boolean saddled = !this.isSaddleRequired() || this.isSaddled();
      if (saddled && this.canExecuteRidersJump()) {
         if (i < 0) {
            i = 0;
         }

         if (i >= 90) {
            this.playerJumpPendingScale = 1.0F;
            this.triggerAnim("jumpController", "jump");
         } else {
            this.playerJumpPendingScale = 0.4F + 0.4F * i / 90.0F;
         }
      }
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, -0.5, f * 0.75).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public boolean checkSpawnObstruction(LevelReader levelReader) {
      return levelReader.isUnobstructed(this);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.spearToro, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   public static boolean checkSpearToroSpawnRules(EntityType<SpearToroEntity> type, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource source) {
      return pos.getY() > 30 && pos.getY() < level.getSeaLevel() ? level.getFluidState(pos).is(FluidTags.WATER) : false;
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.SPEAR_TORO_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.SPEAR_TORO_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.SPEAR_TORO_DEATH.get();
   }

   @NotNull
   protected SoundEvent getSwimSound() {
      return SoundEvents.FISH_SWIM;
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @Override
   protected void playJumpSound() {
      this.playSound((SoundEvent)TensuraSoundEvents.GENERIC_WATER_JUMP.get(), 0.4F, 1.0F);
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<SpearToroEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<SpearToroEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new LookAtTarget(),
            new SwimToWalkTarget().cooldownFor(entity -> 0).startCondition(entity -> !entity.isOrderedToSit()).stopIf(ISubordinate::isOrderedToSit)
         }
      );
   }

   public BrainActivityGroup<SpearToroEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(
                     this,
                     target -> this.shouldAttack().test(target) || this.distanceToSqr(target) <= 36.0,
                     TensuraBehaviourHelper.getAnimalPreyPredicate(this)
                  ),
                  new SubordinateFollowOwner().speedMod(1.5F).canTeleportOffGroundWhen(entity -> true),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
               new ExtendedBehaviour[]{
                  new JumpOutOfWater()
                     .jumpInterval(entity -> 100)
                     .jumpStrength(entity -> entity.getJumpPower(0.8F))
                     .onJump(entity -> entity.triggerAnim("jumpController", "jump"))
                     .startCondition(entity -> !entity.isSleeping() && !entity.isOrderedToSit()),
                  new SetRandomSwimTarget().setRadius(20.0).speedModifier(1.5F).cooldownFor(entity -> 0),
                  new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
               }
            )
         }
      );
   }

   public BrainActivityGroup<SpearToroEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget()
               .invalidateIf((entity, target) -> entity.shouldStopTarget(entity, target) || !entity.shouldAttack().test(target)),
            new SetSwimTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new WaterJumpAttack(1)
                     .jumpStrength(entity -> entity.getJumpPower(1.0F))
                     .onJump(entity -> entity.triggerAnim("jumpController", "jump"))
                     .minAttackRadius(3.0F)
                     .maxAttackRadius(10.0F)
                     .attackInterval(entity -> 60)
                     .performAttack((entity, target) -> entity.doHurtTarget(target, 2.0F))
                     .startCondition(tiger -> tiger.getRandom().nextInt(10) == 1),
                  new CustomRangeAttack(5)
                     .maxAttackRadius(6.0F)
                     .attackInterval(entity -> 40)
                     .performAttack((entity, target) -> entity.slashAttack(entity))
                     .whenStarting(entity -> entity.triggerAnim("miscController", "slash"))
                     .startCondition(tiger -> tiger.getRandom().nextInt(10) == 1),
                  new AnimatableMeleeAttack(1).attackInterval(entity -> 1).whenStarting(entity -> entity.triggerAnim("miscController", "bite"))
               }
            )
         }
      );
   }

   private Predicate<LivingEntity> shouldAttack() {
      return target -> target.isInWaterOrBubble() || !target.onGround();
   }

   protected PlayState loopController(AnimationState<SpearToroEntity> state) {
      String name;
      if (this.isInWaterOrBubble()) {
         if (state.isMoving()) {
            if (!this.isAngry() && !this.isSprinting()) {
               name = "animation.spear_toro.swim";
            } else {
               name = "animation.spear_toro.swim_fast";
            }
         } else {
            name = "animation.spear_toro.idle";
         }
      } else if (this.shouldFlop()) {
         name = "animation.spear_toro.flop";
      } else {
         name = "animation.spear_toro.fall";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 0, event -> PlayState.STOP)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.spear_toro.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("slash", RawAnimation.begin().then("animation.spear_toro.slash", LoopType.PLAY_ONCE)),
            new AnimationController(this, "jumpController", 0, event -> PlayState.STOP)
               .triggerableAnim("jump", RawAnimation.begin().then("animation.spear_toro.jump_attack", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   @Override
   public int getFlopTick() {
      return this.flopTick;
   }

   @Generated
   @Override
   public void setFlopTick(int flopTick) {
      this.flopTick = flopTick;
   }
}
