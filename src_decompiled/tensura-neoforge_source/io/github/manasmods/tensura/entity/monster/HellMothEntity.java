package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.block.MothEggBlock;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.OrbitAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.SpittingRangedMonsterAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.CustomBreedWithPartner;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.FindNearestBlock;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.LayEggs;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetWalkTargetToSpecificBlock;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.projectile.MonsterSpitProjectile;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.entity.template.subclass.SpittingRangedMonster;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowTemptation;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.ItemTemptingSensor;
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

public class HellMothEntity extends TensuraTamableEntity implements GeoEntity, SmartBrainOwner<HellMothEntity>, IFlying, INameEvolution, SpittingRangedMonster {
   private static final EntityDataAccessor<Boolean> GEHENNA = SynchedEntityData.defineId(HellMothEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> POWDER_ATTACK = SynchedEntityData.defineId(HellMothEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(HellMothEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(HellMothEntity.class, EntityDataSerializers.BOOLEAN);
   protected int flyingTick;
   protected boolean wasFlying;
   public boolean isWet;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public HellMothEntity(EntityType<? extends HellMothEntity> type, Level level) {
      super(type, level);
      this.initFlying(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.FLYING_SPEED, 0.6F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
         .add(Attributes.STEP_HEIGHT, 1.0);
   }

   @Override
   public void switchMoveControl(MoveControl control) {
      this.moveControl = control;
   }

   @Override
   public void switchNavigation(PathNavigation navigation) {
      this.navigation = navigation;
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(GEHENNA, false);
      builder.define(POWDER_ATTACK, false);
      builder.define(HAS_EGG, false);
      builder.define(FLYING, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Gehenna", this.isGehenna());
      compound.putBoolean("PowderAttacking", this.isPowderAttacking());
      compound.putBoolean("HasEgg", this.hasEgg());
      compound.putBoolean("Flying", this.isFlying());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setGehenna(compound.getBoolean("Gehenna"));
      this.setPowderAttack(compound.getBoolean("PowderAttacking"));
      this.setHasEgg(compound.getBoolean("HasEgg"));
      this.setFlying(compound.getBoolean("Flying"));
   }

   public boolean isGehenna() {
      return (Boolean)this.entityData.get(GEHENNA);
   }

   public void setGehenna(boolean gehenna) {
      this.entityData.set(GEHENNA, gehenna);
   }

   public boolean isPowderAttacking() {
      return (Boolean)this.entityData.get(POWDER_ATTACK);
   }

   public void setPowderAttack(boolean gehenna) {
      this.entityData.set(POWDER_ATTACK, gehenna);
   }

   public boolean hasEgg() {
      return (Boolean)this.entityData.get(HAS_EGG);
   }

   public void setHasEgg(boolean egg) {
      this.entityData.set(HAS_EGG, egg);
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   @Override
   public void setFlying(boolean flying) {
      this.entityData.set(FLYING, flying);
   }

   @Override
   public boolean wasFlying() {
      return this.wasFlying;
   }

   @Override
   public boolean shouldStopFlying(Mob entity) {
      return IFlying.super.shouldStopFlying(entity) || this.isOrderedToSit() || this.isInLove();
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   public float getAgeScale() {
      return this.isBaby() ? 0.7F : 1.0F;
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
      if (this.isInWaterRainOrBubble()) {
         this.isWet = true;
      } else if (this.onGround()) {
         if (this.isWet) {
            this.isWet = false;
            this.triggerAnim("loopController", "shake");
            this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.gameEvent(GameEvent.ENTITY_ACTION);
         } else if (this.tickCount % 200 == 0) {
            this.triggerAnim("loopController", "clean");
         }
      }
   }

   @Override
   public void performRangedAttack(@NotNull LivingEntity pTarget, float pDistanceFactor) {
      MonsterSpitProjectile spit = new MonsterSpitProjectile(this.level(), this);
      spit.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
      double d1 = pTarget.getX() - this.getX();
      double d2 = pTarget.getEyeY() - 1.1F - spit.getY();
      double d3 = pTarget.getZ() - this.getZ();
      double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.2F;
      spit.shoot(d1, d2 + d4, d3, 1.6F, 1.0F);
      if (!this.isSilent()) {
         this.level()
            .playSound(
               null,
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.LLAMA_SPIT,
               this.getSoundSource(),
               1.0F,
               1.0F + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F
            );
      }

      this.level().addFreshEntity(spit);
   }

   @Override
   public void spitHit(LivingEntity pTarget) {
      if (!(pTarget instanceof HellMothEntity) && !(pTarget instanceof HellCaterpillarEntity)) {
         if (this.doHurtTarget(pTarget)) {
            int level = this.random.nextInt(10) >= 8 ? 1 : 0;
            pTarget.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 300, level, true, false, true), this);
            pTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 200, level, true, false, true), this);
         }
      }
   }

   @Override
   public void spitParticle(MonsterSpitProjectile projectile) {
      this.particleSpawning(projectile, ParticleTypes.SPIT, 5);
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.WOOL);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return this.isFood(pStack);
   }

   public boolean canFallInLove() {
      return super.canFallInLove() && !this.hasEgg();
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      HellMothEntity moth = (HellMothEntity)((EntityType)MonsterEntityTypes.HELL_MOTH.get()).create(pLevel);
      if (moth == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         moth.setOwnerUUID(uuid);
         moth.setTame(true, true);
      }

      return moth;
   }

   @Override
   public void evolve() {
      if (TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.gehennaMothChance, this.level().getRandom())) {
         this.turnGehenna();
      }
   }

   public void turnGehenna() {
      this.setGehenna(true);
      EnergyHelper.increaseMaxEP(this, 1000.0);
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.hellMoth, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   public static boolean checkMothSpawnRules(
      EntityType<HellMothEntity> moth, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      return pLevel.getBlockState(pPos.below()).is(BlockTags.PARROTS_SPAWNABLE_ON)
         && Monster.isDarkEnoughToSpawn(pLevel, pPos, pRandom)
         && checkMobSpawnRules(moth, pLevel, pSpawnType, pPos, pRandom);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canSpawnSpecialVariant(pReason) && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.gehennaMothChance, pLevel.getRandom())
         )
       {
         this.turnGehenna();
      }

      if (!this.onGround()) {
         this.setFlying(true);
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   public void die(DamageSource pCause) {
      this.isWet = false;
      super.die(pCause);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.INSECT_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return (SoundEvent)TensuraSoundEvents.INSECT_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.INSECT_DEATH.get();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<HellMothEntity>> getSensors() {
      return ObjectArrayList.of(
         new ExtendedSensor[]{
            new NearbyLivingEntitySensor(),
            new HurtBySensor(),
            new NearbyBlocksSensor()
               .setRadius(16.0)
               .setPredicate((state, entity) -> state.is(BlockTags.LEAVES) || state.is(BlockTags.WOOL))
               .setScanRate(entity -> entity.hasEgg() ? 20 : 100),
            new ItemTemptingSensor().temptedWith((entity, stack) -> stack.is(TensuraItemTags.MOTH_TEMPT_ITEMS))
         }
      );
   }

   public BrainActivityGroup<HellMothEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<HellMothEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new LayEggs(15)
                     .laySound(SoundEvents.WOOL_FALL)
                     .shouldLay(
                        (entity, position) -> position.pos().closerToCenterThan(entity.position(), 2.0)
                           && MothEggBlock.canLayEgg(entity.level(), position.pos())
                     )
                     .layEggs(
                        (entity, pos) -> {
                           Level level = entity.level();
                           level.playSound(null, pos, SoundEvents.WOOL_FALL, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
                           level.levelEvent(2001, pos, Block.getId(level.getBlockState(pos.below())));
                           level.setBlock(
                              pos.above(),
                              (BlockState)((MothEggBlock)TensuraBlocks.MOTH_EGG.get())
                                 .defaultBlockState()
                                 .setValue(MothEggBlock.EGGS, entity.random.nextInt(4) + 1),
                              3
                           );
                           entity.setHasEgg(false);
                           entity.setInLoveTime(600);
                           BrainUtils.clearMemories(entity, new MemoryModuleType[]{MemoryModuleType.MEETING_POINT});
                        }
                     )
                     .startCondition(HellMothEntity::hasEgg),
                  new FindNearestBlock()
                     .predicate((mob, pair) -> MothEggBlock.canLayEgg(mob.level(), (BlockPos)pair.getFirst()))
                     .action(
                        (mob, pair) -> BrainUtils.setForgettableMemory(
                           this, MemoryModuleType.MEETING_POINT, new GlobalPos(this.level().dimension(), (BlockPos)pair.getFirst()), 200
                        )
                     )
                     .startCondition(entity -> entity.hasEgg() && !BrainUtils.hasMemory(entity, MemoryModuleType.MEETING_POINT)),
                  new SetWalkTargetToSpecificBlock()
                     .setTargetPos(entity -> ((GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.MEETING_POINT)).pos().above())
                     .speedMod((entity, pos) -> 1.2F)
                     .startCondition(entity -> entity.hasEgg() && BrainUtils.hasMemory(entity, MemoryModuleType.MEETING_POINT)),
                  new CustomBreedWithPartner().performBreed((entity, partner) -> {
                     entity.setHasEgg(true);
                     CustomBreedWithPartner.applyBreedingReward(entity, partner);
                     CustomBreedWithPartner.setBreedCooldown(entity, partner, 6000);
                  }).startCondition(entity -> !entity.hasEgg()),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false),
                  new FollowTemptation().speedMod((entity, player) -> 1.2F).startCondition(entity -> !entity.isOrderedToSit()),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  new SubordinateFollowOwner().canTeleportOffGroundWhen(entity -> {
                     entity.setFlying(true);
                     return true;
                  }),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomFlyAndWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<HellMothEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new OrbitAttack()
               .orbitRadius((entity, target) -> entity.isPowderAttacking() ? 2.0 : 10.0)
               .orbitHeight((entity, target) -> entity.isPowderAttacking() ? 4.0 : 5.0)
               .canDoOrbitalAttack((entity, target) -> {
                  entity.setPowderAttack(!entity.isPowderAttacking());
                  return false;
               })
               .onOrbitTick(
                  (entity, target, pair) -> {
                     if (entity.isPowderAttacking()) {
                        AABB aabb = new AABB(entity.blockPosition().below(2).getCenter(), entity.blockPosition().below(5).getBottomCenter());
                        List<LivingEntity> list = entity.level()
                           .getEntitiesOfClass(
                              LivingEntity.class,
                              aabb.inflate(2.0),
                              living -> !living.isAlliedTo(entity) && !(living instanceof HellCaterpillarEntity) && !(living instanceof HellMothEntity)
                           );
                        if (!list.isEmpty()) {
                           for (LivingEntity living : list) {
                              living.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0), entity);
                              living.addEffect(
                                 new MobEffectInstance(
                                    TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS),
                                    300,
                                    living.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS)) ? 2 : 1,
                                    true,
                                    false,
                                    true
                                 ),
                                 entity
                              );
                           }
                        }

                        float radius = entity.getBbWidth();
                        double x = entity.getX() + (this.level().random.nextDouble() - 0.5) * radius;
                        double y = entity.getY() - 1.0 + (this.level().random.nextDouble() - 0.5) * radius * 0.75;
                        double z = entity.getZ() + (this.level().random.nextDouble() - 0.5) * radius;

                        for (int i = 0; i < 6; i++) {
                           for (int j = 0; j < 10; j++) {
                              double newX = x + entity.getRandom().nextGaussian() / 2.0;
                              double newY = y + entity.getRandom().nextGaussian() / 2.0;
                              double newZ = z + entity.getRandom().nextGaussian() / 2.0;
                              TensuraParticleHelper.spawnServerParticles(
                                 entity.level(),
                                 new DustParticleOptions(Vec3.fromRGB24(14733312).toVector3f(), 1.0F),
                                 newX,
                                 newY - i,
                                 newZ,
                                 1,
                                 0.0,
                                 0.0,
                                 0.0,
                                 -0.1,
                                 false
                              );
                           }
                        }
                     }

                     return true;
                  }
               )
               .onTick(entity -> {
                  entity.setFlying(true);
                  return true;
               })
               .whenStopping(entity -> entity.setPowderAttack(false)),
            new SpittingRangedMonsterAttack(5).attackRadius(15.0F).attackInterval(entity -> 40).performAttack((entity, target) -> {
               entity.performRangedAttack(target, 1.0F);
               entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LLAMA_SPIT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }).whenStarting(entity -> entity.triggerAnim("loopController", "silk"))
         }
      );
   }

   protected PlayState loopController(AnimationState<HellMothEntity> state) {
      String name;
      if (this.isPowderAttacking()) {
         name = "animation.hell_moth.idle_agro";
      } else if (state.isMoving()) {
         if (this.onGround()) {
            name = "animation.hell_moth.walking";
         } else if (this.isAngry()) {
            name = "animation.hell_moth.fly_agro";
         } else {
            name = "animation.hell_moth.fly_passive";
         }
      } else if (this.onGround()) {
         name = "animation.hell_moth.idle";
      } else {
         name = "animation.hell_moth.idle_fly";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "loopController", 5, this::loopController)
            .triggerableAnim("shake", RawAnimation.begin().then("animation.hell_moth.water_shaking", LoopType.PLAY_ONCE))
            .triggerableAnim("clean", RawAnimation.begin().then("animation.hell_moth.cleaning", LoopType.PLAY_ONCE))
            .triggerableAnim("hatch", RawAnimation.begin().then("animation.hell_moth.hatch", LoopType.PLAY_ONCE))
            .triggerableAnim("silk", RawAnimation.begin().then("animation.hell_moth.silk_attack", LoopType.PLAY_ONCE))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   @Override
   public void setFlyingTick(int flyingTick) {
      this.flyingTick = flyingTick;
   }

   @Generated
   @Override
   public int getFlyingTick() {
      return this.flyingTick;
   }

   @Generated
   @Override
   public void setWasFlying(boolean wasFlying) {
      this.wasFlying = wasFlying;
   }
}
