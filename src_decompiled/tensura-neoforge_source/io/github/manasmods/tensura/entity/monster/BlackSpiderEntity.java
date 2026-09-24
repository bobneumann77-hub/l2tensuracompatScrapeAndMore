package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.block.SpiderEggBlock;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.LeapToTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.SpittingRangedMonsterAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.CustomBreedWithPartner;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.FindNearestBlock;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.LayEggs;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetWalkTargetToSpecificBlock;
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
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
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

public class BlackSpiderEntity
   extends TensuraMountEntity
   implements GeoEntity,
   SmartBrainOwner<BlackSpiderEntity>,
   IClimbing,
   IGiantMob,
   ITensuraMount,
   SpittingRangedMonster {
   private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(BlackSpiderEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> STRIPED = SynchedEntityData.defineId(BlackSpiderEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> EGGS = SynchedEntityData.defineId(BlackSpiderEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int mountAbilityCooldown = 0;

   public BlackSpiderEntity(EntityType<? extends BlackSpiderEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @NotNull
   @Override
   protected PathNavigation createNavigation(Level level) {
      return new ClimbingNavigator(this, level);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 6.0)
         .add(Attributes.MAX_HEALTH, 65.0)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
         .add(Attributes.JUMP_STRENGTH, 1.5)
         .add(Attributes.STEP_HEIGHT, 4.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CLIMBING, Boolean.FALSE);
      builder.define(STRIPED, Boolean.FALSE);
      builder.define(EGGS, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Climbing", this.isClimbing());
      compound.putBoolean("Striped", this.isStriped());
      compound.putInt("Eggs", this.getEggs());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setClimbing(compound.getBoolean("Climbing"));
      this.setStriped(compound.getBoolean("Striped"));
      this.setEggs(compound.getInt("Eggs"));
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

   public boolean isStriped() {
      return (Boolean)this.entityData.get(STRIPED);
   }

   public void setStriped(boolean striped) {
      this.entityData.set(STRIPED, striped);
   }

   public int getEggs() {
      return (Integer)this.entityData.get(EGGS);
   }

   public void setEggs(int egg) {
      this.entityData.set(EGGS, egg);
   }

   public float getAgeScale() {
      return this.isBaby() ? 0.2F : 1.0F;
   }

   public void push(Entity pEntity) {
      if (!pEntity.getType().equals(this.getType())) {
         super.push(pEntity);
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
      if (pState.is(TensuraBlockTags.WEB_BLOCKS)) {
         this.resetFallDistance();
      } else {
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
      return this.isBaby() ? 1.0F : 2.0F;
   }

   protected float getJumpPower() {
      return 0.0F;
   }

   @Override
   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   @Override
   public void tick() {
      super.tick();
      this.handleClimbing(this);
      if (this.mountAbilityCooldown > 0) {
         this.getNavigation().stop();
         if (this.mountAbilityCooldown-- == 10) {
            LivingEntity owner = this.getControllingPassenger();
            if (owner != null) {
               BlockHitResult hitResult = ObjectSelectionHelper.getPlayerPOVHitResult(this.level(), owner, Fluid.NONE, 30.0);
               this.performRangedAttack(hitResult.getBlockPos(), this.isBaby() ? -0.3 : -1.5, this.isBaby() ? 1.0 : 5.0);
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
            this.mountAbilityCooldown = 25;
            this.triggerAnim("miscController", "silk");
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
            entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && !entity.equals(this) && !(entity instanceof BlackSpiderEntity)
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

   @Override
   public void spitHit(LivingEntity pTarget) {
      if (!(pTarget instanceof BlackSpiderEntity)) {
         if (pTarget.hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
            int duration = this.isBaby() ? 50 : 200;
            if (!(pTarget.getBbHeight() <= 3.0F) && !(pTarget.getBbWidth() <= 3.0F)) {
               pTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 0, true, false, true), this);
            } else {
               pTarget.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.SILENCE), duration, 0, true, false, true), this);
               MobEffectInstance webbed = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.WEBBED), duration, 0, true, false, true);
               pTarget.addEffect(webbed, this);
            }
         }
      }
   }

   @Override
   public void spitParticle(MonsterSpitProjectile projectile) {
      if (this.isBaby()) {
         this.particleSpawning(projectile, ParticleTypes.SPIT, 2);
      } else {
         this.particleSpawning(projectile, ParticleTypes.SPIT, 5);
      }
   }

   @Override
   public int getMenuRenderSize() {
      return 10;
   }

   @Override
   public int getChestsAllowed() {
      return 4;
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, -0.25, -0.5 * f).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
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
   public boolean canMate(Animal pOtherAnimal) {
      if (!super.canMate(pOtherAnimal)) {
         return false;
      } else {
         return this.getEggs() > 0 ? false : ((BlackSpiderEntity)pOtherAnimal).getEggs() <= 0;
      }
   }

   public void layEggs(BlockPos pos) {
      this.level().setBlock(pos.above(), ((Block)TensuraBlocks.SPIDER_EGG.get()).defaultBlockState(), 3);
      this.setEggs(this.getEggs() - 1);
      if (this.getEggs() <= 0) {
         BrainUtils.clearMemories(this, new MemoryModuleType[]{MemoryModuleType.MEETING_POINT});
      } else {
         int tries = 0;

         while (tries < 4) {
            Direction direction = Direction.getRandom(this.getRandom());
            if (!direction.getAxis().isVertical()) {
               if (SpiderEggBlock.canLayEgg(this.level(), pos.relative(direction))) {
                  BrainUtils.setForgettableMemory(this, MemoryModuleType.MEETING_POINT, new GlobalPos(this.level().dimension(), pos.relative(direction)), 200);
                  break;
               }

               tries++;
            }
         }

         if (tries >= 4) {
            BrainUtils.clearMemories(this, new MemoryModuleType[]{MemoryModuleType.MEETING_POINT});
         }
      }
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      BlackSpiderEntity spider = (BlackSpiderEntity)((EntityType)MonsterEntityTypes.BLACK_SPIDER.get()).create(pLevel);
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

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.blackSpider, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType type, @Nullable SpawnGroupData spawnGroupData
   ) {
      if (this.canRandomizeSpawnData(type)) {
         this.setStriped(serverLevelAccessor.getRandom().nextBoolean());
      }

      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, type, spawnGroupData);
   }

   public static boolean checkSpiderSpawnRules(
      EntityType<BlackSpiderEntity> spider, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      return !pLevel.getBlockState(pPos.below()).is(TensuraBlockTags.MOBS_SPAWNABLE_ON) && !pLevel.getBlockState(pPos.below()).is(TensuraBlockTags.WEB_BLOCKS)
         ? false
         : pLevel.getDifficulty() != Difficulty.PEACEFUL && pLevel.getBrightness(LightLayer.BLOCK, pPos) <= 5;
   }

   public void die(DamageSource pDamageSource) {
      super.die(pDamageSource);
      if (!this.level().isClientSide()) {
         if (!this.isBaby()) {
            int additionalAmount = this.getRandom().nextInt(10) == 1 ? this.getRandom().nextInt(4, 8) : 0;
            int eggAmount = this.getEggs() + additionalAmount;
            if (eggAmount > 0) {
               for (int i = 0; i < eggAmount; i++) {
                  BlackSpiderEntity spider = (BlackSpiderEntity)((EntityType)MonsterEntityTypes.BLACK_SPIDER.get()).create(this.level());
                  if (spider != null) {
                     spider.setAge(-24000);
                     spider.moveTo(this.getX() + 0.3, this.getY(), this.getZ() + 0.3);
                     spider.setDeltaMovement(this.random.nextFloat() - 0.5F, this.random.nextFloat(), this.random.nextFloat() - 0.5F);
                     spider.finalizeSpawn((ServerLevel)this.level(), this.level().getCurrentDifficultyAt(spider.blockPosition()), MobSpawnType.BREEDING, null);
                     spider.markHurt();
                     this.level().addFreshEntity(spider);
                  }
               }
            }
         }
      }
   }

   @Override
   protected void dropEquipment() {
      this.dropSaddle();
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

   public List<ExtendedSensor<BlackSpiderEntity>> getSensors() {
      return ObjectArrayList.of(
         new ExtendedSensor[]{
            new NearbyLivingEntitySensor(),
            new HurtBySensor(),
            new NearbyBlocksSensor().setRadius(2.0, 1.0).setScanRate(entity -> entity.getEggs() > 0 ? 20 : 100)
         }
      );
   }

   public BrainActivityGroup<BlackSpiderEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<BlackSpiderEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new LayEggs(20)
                     .shouldLay(
                        (entity, position) -> position.pos().closerToCenterThan(entity.position(), 3.0)
                           && SpiderEggBlock.canLayEgg(entity.level(), position.pos())
                     )
                     .layEggs(BlackSpiderEntity::layEggs)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "lay_eggs"))
                     .startCondition(entity -> entity.getEggs() > 0),
                  new FindNearestBlock()
                     .predicate((mob, pair) -> SpiderEggBlock.canLayEgg(mob.level(), (BlockPos)pair.getFirst()))
                     .action(
                        (mob, pair) -> BrainUtils.setForgettableMemory(
                           this, MemoryModuleType.MEETING_POINT, new GlobalPos(this.level().dimension(), (BlockPos)pair.getFirst()), 200
                        )
                     )
                     .startCondition(entity -> entity.getEggs() > 0 && !BrainUtils.hasMemory(entity, MemoryModuleType.MEETING_POINT)),
                  new SetWalkTargetToSpecificBlock()
                     .setTargetPos(entity -> ((GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.MEETING_POINT)).pos())
                     .speedMod((entity, pos) -> 1.2F)
                     .startCondition(entity -> entity.getEggs() > 0 && BrainUtils.hasMemory(entity, MemoryModuleType.MEETING_POINT)),
                  new CustomBreedWithPartner().performBreed((entity, partner) -> {
                     entity.setEggs(entity.getRandom().nextInt(5));
                     CustomBreedWithPartner.applyBreedingReward(entity, partner);
                     CustomBreedWithPartner.setBreedCooldown(entity, partner, 6000);
                  }).startCondition(entity -> entity.getEggs() <= 0),
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

   public BrainActivityGroup<BlackSpiderEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(27)
                     .maxAttackRadius(7.0F)
                     .attackInterval(entity -> 60)
                     .performAttack((entity, target) -> entity.areaAttack())
                     .whenStarting(entity -> entity.triggerAnim("miscController", "slam"))
                     .startCondition(entity -> !entity.isBaby() && entity.getRandom().nextInt(10) == 1 && entity.getControllingPassenger() == null),
                  new SpittingRangedMonsterAttack(12)
                     .requireInSight(false)
                     .attackRadius(25.0F)
                     .attackInterval(entity -> 100)
                     .performAttack((entity, target) -> {
                        entity.performRangedAttack(target, entity.isBaby() ? -0.3 : -1.5, entity.isBaby() ? 1.0 : 5.0);
                        entity.level()
                           .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LLAMA_SPIT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                     })
                     .whenStarting(entity -> entity.triggerAnim("miscController", "silk"))
                     .startCondition(entity -> !entity.isBaby() && entity.getRandom().nextInt(10) == 1),
                  new AnimatableMeleeAttack(0).whenStarting(entity -> entity.triggerAnim("miscController", "bite")),
                  new LeapToTarget(12)
                     .leapRange((entity, target) -> 32.0F)
                     .moveSpeedContribution((dog, entity) -> 2.0F)
                     .jumpStrength((entity, target) -> entity.getJumpPower(1.0F))
                     .attackInterval(entity -> 30)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "leap"))
                     .startCondition(entity -> entity.getTarget() != null && !entity.isWithinMeleeAttackRange(entity.getTarget()))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<BlackSpiderEntity> state) {
      String name;
      if (this.isInSittingPose()) {
         name = "animation.black_spider.stay";
      } else if (state.isMoving()) {
         name = "animation.black_spider.walk";
      } else {
         name = "animation.black_spider.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.black_spider.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("leap", RawAnimation.begin().then("animation.black_spider.leap", LoopType.PLAY_ONCE))
               .triggerableAnim("slam", RawAnimation.begin().then("animation.black_spider.slam", LoopType.PLAY_ONCE))
               .triggerableAnim("silk", RawAnimation.begin().then("animation.black_spider.silk", LoopType.PLAY_ONCE))
               .triggerableAnim("lay_eggs", RawAnimation.begin().then("animation.black_spider.lay", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
