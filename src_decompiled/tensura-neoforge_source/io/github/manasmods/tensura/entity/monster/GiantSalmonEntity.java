package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.SwimToWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.entity.template.subclass.ISwimming;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class GiantSalmonEntity extends TensuraTamableEntity implements GeoEntity, SmartBrainOwner<GiantSalmonEntity>, ISwimming {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int flopTick = 0;

   public GiantSalmonEntity(EntityType<? extends GiantSalmonEntity> type, Level level) {
      super(type, level);
      this.initSwimming(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 15.0)
         .add(Attributes.ATTACK_DAMAGE, 4.0)
         .add(Attributes.ARMOR, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.FOLLOW_RANGE, 16.0)
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

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return this.isFood(pStack);
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      GiantSalmonEntity fish = (GiantSalmonEntity)((EntityType)MonsterEntityTypes.GIANT_SALMON.get()).create(pLevel);
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

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public boolean checkSpawnObstruction(LevelReader levelReader) {
      return levelReader.isUnobstructed(this);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.giantSalmon, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (pReason == MobSpawnType.NATURAL || pReason == MobSpawnType.CHUNK_GENERATION) {
         for (int i = 0; i <= 3; i++) {
            Salmon salmon = new Salmon(EntityType.SALMON, this.level());
            salmon.setPos(this.getX(), this.getY() - 1.0, this.getZ());
            salmon.finalizeSpawn(pLevel, this.level().getCurrentDifficultyAt(salmon.blockPosition()), MobSpawnType.NATURAL, null);
            this.level().addFreshEntity(salmon);
         }
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SALMON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.SALMON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SALMON_DEATH;
   }

   @NotNull
   protected SoundEvent getSwimSound() {
      return SoundEvents.FISH_SWIM;
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

   public List<ExtendedSensor<GiantSalmonEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<GiantSalmonEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new LookAtTarget(),
            new SwimToWalkTarget().cooldownFor(entity -> 0).startCondition(entity -> !entity.isOrderedToSit()).stopIf(ISubordinate::isOrderedToSit)
         }
      );
   }

   public BrainActivityGroup<GiantSalmonEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, Entity::isInWaterOrBubble, entity -> entity.getLastHurtMob() instanceof Salmon),
                  new SubordinateFollowOwner().speedMod(1.5F),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
               new ExtendedBehaviour[]{
                  new SetRandomSwimTarget().setRadius(20.0).speedModifier(1.5F).cooldownFor(entity -> 0),
                  new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
               }
            )
         }
      );
   }

   public BrainActivityGroup<GiantSalmonEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf((entity, target) -> entity.shouldStopTarget(entity, target) || !target.isInWaterOrBubble()),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new AnimatableMeleeAttack(0).attackInterval(entity -> 5).whenStarting(entity -> entity.triggerAnim("miscController", "bite"))
         }
      );
   }

   protected PlayState loopController(AnimationState<GiantSalmonEntity> state) {
      String name = "animation.giant_fish.idle";
      if (this.isInWaterOrBubble()) {
         if (state.isMoving()) {
            if (!this.isAngry() && !this.isSprinting()) {
               name = "animation.giant_fish.swim";
            } else {
               name = "animation.giant_fish.swim_fast";
            }
         }
      } else if (this.shouldFlop()) {
         name = "animation.giant_fish.flop";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 0, event -> PlayState.STOP)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.giant_fish.bite", LoopType.PLAY_ONCE))
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
