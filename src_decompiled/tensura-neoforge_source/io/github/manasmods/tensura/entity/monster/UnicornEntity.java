package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.CustomBreedWithPartner;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.util.SubordinateHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AnimalArmorItem.BodyType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.AvoidEntity;
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

public class UnicornEntity extends TensuraMountEntity implements GeoEntity, SmartBrainOwner<UnicornEntity>, ITensuraMount {
   private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(UnicornEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> BLACK = SynchedEntityData.defineId(UnicornEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int mountAbilityCooldown = 0;
   protected int gallopSoundCounter;

   public UnicornEntity(EntityType<? extends UnicornEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.JUMP_STRENGTH, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.01F)
         .add(Attributes.SAFE_FALL_DISTANCE, 7.0)
         .add(Attributes.STEP_HEIGHT, 1.5)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 2.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CHARGING, false);
      builder.define(BLACK, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Charging", this.isCharging());
      compound.putBoolean("Black", this.isBlack());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setCharging(compound.getBoolean("Charging"));
      this.setBlack(compound.getBoolean("Black"));
   }

   public boolean isCharging() {
      return (Boolean)this.entityData.get(CHARGING);
   }

   public void setCharging(boolean charging) {
      this.entityData.set(CHARGING, charging);
   }

   public boolean isBlack() {
      return (Boolean)this.entityData.get(BLACK);
   }

   public void setBlack(boolean black) {
      this.entityData.set(BLACK, black);
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
   public boolean canMate(Animal animal) {
      if (animal == this) {
         return false;
      }

      if (animal.getClass() != this.getClass() && !(animal instanceof PegasusEntity)) {
         return false;
      }

      TensuraTamableEntity partner = (TensuraTamableEntity)animal;
      if (this.requireTamingForBreeding()) {
         if (!this.isTame() || !partner.isTame()) {
            return false;
         } else {
            return !this.isInSittingPose() && !partner.isInSittingPose()
               ? this.canBreed() && animal.canBreed() && SubordinateHelper.isAlly(this, partner)
               : false;
         }
      } else {
         return this.isTame() == partner.isTame() && this.canBreed() && animal.canBreed();
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         if (this.random.nextFloat() >= 0.3F || this.isBaby()) {
            return;
         }

         float radius = this.getBbWidth();
         this.level()
            .addParticle(
               ParticleTypes.INSTANT_EFFECT,
               this.getX() + (this.level().random.nextDouble() - 0.5) * radius,
               this.getY() + (this.level().random.nextDouble() - 0.5) * radius * 0.75,
               this.getZ() + (this.level().random.nextDouble() - 0.5) * radius,
               0.0,
               0.0,
               0.0
            );
      } else {
         if (this.isCharging()) {
            AABB aabb = this.getBoundingBox().move(this.getViewVector(1.0F));

            for (LivingEntity living : this.level()
               .getEntitiesOfClass(LivingEntity.class, aabb, livingx -> !livingx.is(this) && livingx.getVehicle() != this && livingx.isAlive())) {
               this.doHurtTarget(living, 1.0F);
               this.playSound(SoundEvents.HORSE_ANGRY);
            }
         }

         if (this.mountAbilityCooldown > 0 && --this.mountAbilityCooldown <= 0) {
            this.setCharging(false);
         }
      }
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      if (pOtherParent instanceof PegasusEntity pegasus) {
         PegasusEntity pegacorn = (PegasusEntity)((EntityType)MonsterEntityTypes.PEGACORN.get()).create(pLevel);
         if (pegacorn == null) {
            return null;
         }

         UUID uuid = this.getOwnerUUID();
         if (uuid != null) {
            pegacorn.setOwnerUUID(uuid);
            pegacorn.setTame(true, true);
         }

         if (this.isBlack() && pegasus.isBlack()) {
            pegacorn.setBlack(true);
         } else if (this.isBlack() || pegasus.isBlack()) {
            pegacorn.setBlack(pLevel.getRandom().nextBoolean());
         }

         return pegacorn;
      } else {
         UnicornEntity unicorn = (UnicornEntity)((EntityType)MonsterEntityTypes.UNICORN.get()).create(pLevel);
         if (unicorn == null) {
            return null;
         }

         UUID uuid = this.getOwnerUUID();
         if (uuid != null) {
            unicorn.setOwnerUUID(uuid);
            unicorn.setTame(true, true);
         }

         if (this.isBlack() && ((UnicornEntity)pOtherParent).isBlack()) {
            unicorn.setBlack(true);
         } else if (this.isBlack() || ((UnicornEntity)pOtherParent).isBlack()) {
            unicorn.setBlack(pLevel.getRandom().nextBoolean());
         }

         return unicorn;
      }
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.CATTLE_FOOD);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return this.isFood(pStack);
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
      this.playSound(SoundEvents.HORSE_EAT, 1.0F, 1.0F);
   }

   @Override
   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
      Vec3 riddenInput = this.getRiddenInput(controller, vec3);
      if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > this.getFluidJumpThreshold() && riddenInput.z() > 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.03, 0.0));
      }
   }

   @Override
   protected float getRiddenSpeed(Player player) {
      float speed = super.getRiddenSpeed(player);
      return this.isCharging() ? speed * 2.0F : speed * 1.5F;
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAbilityCooldown <= 0) {
         this.mountAbilityCooldown = 60;
         this.setCharging(true);
         this.playSound(SoundEvents.HORSE_ANGRY);
      }
   }

   @Override
   public void handleStartJump(int i) {
      super.handleStartJump(i);
      this.triggerAnim("miscController", "stand");
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.unicorn, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      if (this.canRandomizeSpawnData(mobSpawnType) && serverLevelAccessor.getRandom().nextInt(100) == 69) {
         this.setBlack(true);
      }

      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.HORSE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.HORSE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HORSE_DEATH;
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      if (!blockState.liquid()) {
         BlockState state = this.level().getBlockState(blockPos.above());
         SoundType soundType = blockState.getSoundType();
         if (state.is(Blocks.SNOW)) {
            soundType = state.getSoundType();
         }

         if (this.isVehicle() && this.onGround()) {
            this.gallopSoundCounter++;
            if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
               this.playSound(SoundEvents.HORSE_GALLOP, soundType.getVolume() * 0.15F, soundType.getPitch());
            } else if (this.gallopSoundCounter <= 5) {
               this.playSound(SoundEvents.HORSE_STEP_WOOD, soundType.getVolume() * 0.15F, soundType.getPitch());
            }
         } else if (this.isWoodSoundType(soundType)) {
            this.playSound(SoundEvents.HORSE_STEP_WOOD, soundType.getVolume() * 0.15F, soundType.getPitch());
         } else {
            this.playSound(SoundEvents.HORSE_STEP, soundType.getVolume() * 0.15F, soundType.getPitch());
         }
      }
   }

   private boolean isWoodSoundType(SoundType soundType) {
      return soundType == SoundType.WOOD
         || soundType == SoundType.NETHER_WOOD
         || soundType == SoundType.STEM
         || soundType == SoundType.CHERRY_WOOD
         || soundType == SoundType.BAMBOO_WOOD;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<UnicornEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<UnicornEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<UnicornEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new AvoidEntity()
                     .noCloserThan(10.0F)
                     .stopCaringAfter(20.0F)
                     .speedModifier(1.5F)
                     .avoiding(entity -> !entity.isSpectator() && entity instanceof Player player && !player.isCreative())
                     .startCondition(entity -> !entity.isTame()),
                  new CustomBreedWithPartner(),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false),
                  new SubordinateFollowOwner().speedMod(1.2F),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60)).whenStarting(entity -> {
                     if (!entity.isVehicle() && !entity.isSleeping() && entity.onGround()) {
                        if (!(entity.getDeltaMovement().length() > 0.1) && !entity.getMoveControl().hasWanted()) {
                           if (entity.getRandom().nextFloat() <= 0.01) {
                              entity.triggerAnim("miscController", "eat");
                           }
                        }
                     }
                  })}
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<UnicornEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 1.2F),
            new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     new CustomRangeAttack(10)
                        .maxAttackRadius(10.0F)
                        .minAttackRadius(2.0F)
                        .attackInterval(entity -> 40)
                        .performAttack((entity, target) -> entity.setSprinting(true))
                        .whenStarting(entity -> entity.setCharging(true)),
                     new AnimatableMeleeAttack(0).whenStarting(entity -> {
                        if (!entity.isCharging()) {
                           entity.triggerAnim("miscController", "stand");
                        }

                        entity.setSprinting(false);
                     }).whenStopping(entity -> entity.setCharging(false))
                  }
               )
               .startCondition(entity -> !entity.isVehicle())
         }
      );
   }

   public boolean shouldStopTarget(Mob subordinate, LivingEntity target) {
      return !this.isTame() ? false : super.shouldStopTarget(subordinate, target);
   }

   protected PlayState loopController(AnimationState<UnicornEntity> state) {
      String name;
      if (!this.isAlive()) {
         name = "animation.horse.death";
      } else if (state.isMoving()) {
         if (this.isCharging()) {
            name = "animation.unicorn.charge";
         } else if (this.isInLiquid() || !this.isSprinting() && !this.isAngry()) {
            name = "animation.horse.walk";
         } else {
            name = "animation.horse.run";
         }
      } else {
         name = "animation.horse.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 2, event -> PlayState.STOP)
               .triggerableAnim("eat", RawAnimation.begin().then("animation.horse.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("stand", RawAnimation.begin().then("animation.horse.stand", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
