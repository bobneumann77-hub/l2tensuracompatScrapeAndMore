package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.variant.HoverLizardVariant;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AnimalArmorItem.BodyType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
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
import net.tslat.smartbrainlib.api.core.navigation.SmoothAmphibiousPathNavigation;
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

public class HoverLizardEntity extends TensuraMountEntity implements GeoEntity, SmartBrainOwner<HoverLizardEntity>, VariantHolder<HoverLizardVariant> {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(HoverLizardEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public int itchingCountDown = 0;

   public HoverLizardEntity(EntityType<? extends HoverLizardEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @Override
   protected PathNavigation createNavigation(Level level) {
      return new SmoothAmphibiousPathNavigation(this, level);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.JUMP_STRENGTH, 1.0)
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.ARMOR, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.01F)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 3.0)
         .add(Attributes.STEP_HEIGHT, 1.5)
         .add(Attributes.JUMP_STRENGTH, 0.8F)
         .add(Attributes.SAFE_FALL_DISTANCE, 7.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.05F)
         .add(Attributes.OXYGEN_BONUS, 30.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ID_TYPE_VARIANT, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(DATA_ID_TYPE_VARIANT, compound.getInt("Variant"));
   }

   public HoverLizardVariant getVariant() {
      return HoverLizardVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(HoverLizardVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   protected float getWaterSlowDown() {
      return 0.95F;
   }

   public boolean canStandOnFluid(FluidState fluidState) {
      return !fluidState.is(FluidTags.WATER) ? false : !this.level().getBlockState(this.blockPosition().below()).is(Blocks.WATER);
   }

   @Override
   public boolean hasArmorSlot() {
      return true;
   }

   @Override
   public boolean isMountArmor(ItemStack stack) {
      return stack.getItem() instanceof AnimalArmorItem armorItem && armorItem.getBodyType().equals(BodyType.EQUESTRIAN);
   }

   @Override
   public int getChestSlots() {
      return 30;
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, -0.75, -0.5 * f).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.HOVER_LIZARD_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.HOVER_LIZARD_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.HOVER_LIZARD_DEATH.get();
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.itchingCountDown++ > 400 && !this.isSleeping() && this.getRandom().nextInt(10) <= 3) {
         this.itchingCountDown = 0;
         if (this.isInLiquid() && !this.level().getBlockState(this.blockPosition().below(2)).canBeReplaced() && this.getControllingPassenger() == null) {
            this.triggerAnim("miscController", "drinking");
         } else {
            this.triggerAnim("miscController2", "itching");
         }
      }
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      HoverLizardEntity lizard = (HoverLizardEntity)((EntityType)MonsterEntityTypes.HOVER_LIZARD.get()).create(pLevel);
      if (lizard == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         lizard.setOwnerUUID(uuid);
         lizard.setTame(true, true);
      }

      int i = this.random.nextInt(9);
      if (i < 4) {
         lizard.setVariant(this.getVariant());
      } else if (i < 8 && pOtherParent instanceof HoverLizardEntity lizardEntity) {
         lizard.setVariant(lizardEntity.getVariant());
      } else {
         lizard.setVariant((HoverLizardVariant)Util.getRandom(HoverLizardVariant.values(), this.random));
      }

      return lizard;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.FISHES);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return this.isFood(pStack);
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", this.getRandom().nextBoolean() ? "eating_one_hand" : "eating_both_hand");
   }

   @Override
   public void handleStartJump(int i) {
      super.handleStartJump(i);
      this.triggerAnim("miscController2", "hop");
   }

   @Override
   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
      Vec3 riddenInput = this.getRiddenInput(controller, vec3);
      if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > this.getFluidJumpThreshold() && riddenInput.z() > 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.03, 0.0));
      }
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.hoverLizard, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      this.setVariant((HoverLizardVariant)Util.getRandom(HoverLizardVariant.values(), this.random));
      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<HoverLizardEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<HoverLizardEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<HoverLizardEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false),
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

   public BrainActivityGroup<HoverLizardEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 1.2F),
            new AnimatableMeleeAttack(1).whenStarting(entity -> entity.triggerAnim("miscController", "biting"))
         }
      );
   }

   protected PlayState loopController(AnimationState<HoverLizardEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.hover_lizard.sleeping";
      } else if (this.isInSittingPose()) {
         name = "animation.hover_lizard.idle_tail_swing";
      } else if (this.isInLiquid() && this.level().getBlockState(this.blockPosition().below(2)).canBeReplaced()) {
         name = "animation.hover_lizard.deep_swim";
      } else if (state.isMoving()) {
         if (!this.isInLiquid() && (this.isAngry() || this.isSprinting())) {
            if (this.getHealth() < this.getMaxHealth() / 4.0F) {
               name = "animation.hover_lizard.run_hurt";
            } else {
               name = "animation.hover_lizard.run";
            }
         } else if (this.getHealth() < this.getMaxHealth() / 4.0F) {
            name = "animation.hover_lizard.walk_hurt";
         } else {
            name = "animation.hover_lizard.walk";
         }
      } else {
         name = "animation.hover_lizard.idle";
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
               .triggerableAnim("eating_one_hand", RawAnimation.begin().then("animation.hover_lizard.eating_one_hand", LoopType.PLAY_ONCE))
               .triggerableAnim("eating_both_hand", RawAnimation.begin().then("animation.hover_lizard.eating_both_hand", LoopType.PLAY_ONCE))
               .triggerableAnim("biting", RawAnimation.begin().then("animation.hover_lizard.biting", LoopType.PLAY_ONCE))
               .triggerableAnim("drinking", RawAnimation.begin().then("animation.hover_lizard.drinking", LoopType.PLAY_ONCE)),
            new AnimationController(this, "miscController2", 3, event -> {
                  this.swinging = false;
                  return PlayState.STOP;
               })
               .triggerableAnim("itching", RawAnimation.begin().then("animation.hover_lizard.itching", LoopType.PLAY_ONCE))
               .triggerableAnim("hop", RawAnimation.begin().then("animation.hover_lizard.hop", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
