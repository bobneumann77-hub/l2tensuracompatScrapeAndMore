package io.github.manasmods.tensura.entity.human;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.magic.field.Hellfire;
import io.github.manasmods.tensura.entity.monster.IfritEntity;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FireBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.HeatSphereProjectile;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.world.TensuraGameRules;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.InteractWithDoor;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.schedule.SmartBrainSchedule;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShizuEntity extends OtherworlderEntity implements SmartBrainOwner<ShizuEntity> {
   private static final EntityDataAccessor<Integer> TRANSFORM_TICK = SynchedEntityData.defineId(ShizuEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DYING = SynchedEntityData.defineId(ShizuEntity.class, EntityDataSerializers.BOOLEAN);
   private SmartBrainSchedule schedule;

   public ShizuEntity(EntityType<? extends ShizuEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.2F)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 1.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 20.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 3.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 3.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(TRANSFORM_TICK, 0);
      builder.define(DYING, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("TransformTick", this.getTransformTick());
      compound.putBoolean("Dying", this.isDying());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setTransformTick(compound.getInt("TransformTick"));
      this.setDying(compound.getBoolean("Dying"));
   }

   public int getTransformTick() {
      return (Integer)this.entityData.get(TRANSFORM_TICK);
   }

   public void setTransformTick(int tick) {
      this.entityData.set(TRANSFORM_TICK, tick);
   }

   public boolean isDying() {
      return (Boolean)this.entityData.get(DYING);
   }

   public void setDying(boolean dying) {
      this.entityData.set(DYING, dying);
   }

   @Override
   public ResourceLocation getTextureLocation() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/otherworlder/shizu.png");
   }

   @Override
   public List<ManasSkill> getUniqueSkills() {
      return List.of((ManasSkill)UniqueSkills.DEGENERATE.get());
   }

   public boolean isOnFire() {
      return TensuraStorages.getEffectFrom(this).isOnBlackFlame();
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.isDying()) {
            if (this.tickCount % 100 == 0) {
               this.hurt(TensuraDamageTypes.getDamageSource(this.level(), TensuraDamageTypes.ENERGY_DRAIN), 5.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.CLOUD);
               TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.DAMAGE_INDICATOR);
            }
         } else if (this.getTransformTick() < 100) {
            if (!(this.getHealth() > this.getMaxHealth() / 3.0F)) {
               this.setSleeping(false);
               this.setTransformTick(this.getTransformTick() + 1);
               this.getNavigation().stop();
               if (this.tickCount % 5 == 0) {
                  this.combust(false);
               }

               this.level()
                  .playSound(
                     null,
                     this.getX(),
                     this.getY(),
                     this.getZ(),
                     (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.5F,
                     0.4F + (this.random.nextFloat() * 0.4F + 0.8F)
                  );
               if (this.getTransformTick() >= 100) {
                  this.summonIfrit(this.getTarget());
               }
            }
         }
      }
   }

   @Override
   protected void updatePoses() {
      if (this.isDying()) {
         this.setPose(Pose.SLEEPING);
      } else if (this.getTransformTick() > 1) {
         this.setPose(Pose.STANDING);
      } else {
         super.updatePoses();
      }
   }

   @Override
   protected void sleepHandler() {
      if (!this.isDying()) {
         if (this.getTransformTick() <= 1) {
            super.sleepHandler();
         }
      }
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      boolean hurt = super.hurt(pSource, pAmount);
      if (hurt && pSource.getEntity() instanceof LivingEntity source) {
         if (!source.isAlive()) {
            return true;
         }

         if (source instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return true;
         }

         source.setRemainingFireTicks(Math.max(200, source.getRemainingFireTicks()));
      }

      return hurt;
   }

   @Override
   public boolean doHurtTarget(Entity pEntity) {
      if (super.doHurtTarget(pEntity) && pEntity instanceof LivingEntity target && target.getLastHurtByMobTimestamp() == target.tickCount) {
         target.setRemainingFireTicks(Math.max(200, target.getRemainingFireTicks()));
         return true;
      } else {
         return false;
      }
   }

   private void fireBallShoot(LivingEntity target, boolean heatWave) {
      if (this.getTarget() != null) {
         this.lookAt(Anchor.EYES, this.getTarget().getEyePosition());
      }

      this.swing(InteractionHand.OFF_HAND, true);
      TensuraFlyingProjectile fireBall;
      if (heatWave) {
         fireBall = new HeatSphereProjectile(this.level(), this);
         fireBall.setEffectRange(2.5F);
         fireBall.setPiercingEntity(true);
         fireBall.setSkill((ManasSkillInstance)SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)ExtraSkills.HEAT_WAVE.get()).orElse(null));
      } else {
         fireBall = new FireBallProjectile(this.level(), this);
         fireBall.setSkill((ManasSkillInstance)SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)SpiritualMagics.FIRE_BOLT.get()).orElse(null));
      }

      fireBall.setMpCost(100.0);
      fireBall.setSpeed(1.2F);
      fireBall.setDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0));
      fireBall.setBurnTicks(20);
      fireBall.setNoGravity(true);
      fireBall.shootToward(target, 1.5F, 0.0F);
      this.level().addFreshEntity(fireBall);
      this.level()
         .playSound(
            null,
            this.getX(),
            this.getY(),
            this.getZ(),
            (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(),
            TensuraSkill.ABILITY_SOUND,
            0.5F,
            0.4F + (this.random.nextFloat() * 0.4F + 0.8F)
         );
   }

   private void hellFire(LivingEntity target) {
      Vec3 pos = target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0);
      Hellfire sphere = new Hellfire(this.level(), this);
      sphere.setDamage(250.0F);
      sphere.setMpCost(10000.0);
      sphere.setSkill((ManasSkillInstance)SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)SpiritualMagics.HELLFIRE.get()).orElse(null));
      sphere.setLife(60);
      sphere.setSize(2.5F);
      sphere.setPos(pos.x, pos.y - sphere.getSize(), pos.z);
      this.level().addFreshEntity(sphere);
      this.swing(InteractionHand.OFF_HAND, true);
      this.level().playSound(null, sphere.getX(), sphere.getY(), sphere.getZ(), SoundEvents.GENERIC_EXPLODE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }

   private void combust(boolean constant) {
      if (constant) {
         this.level()
            .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), this.getX(), this.getEyeY(), this.getZ(), 55, 0.08, 0.08, 0.08, 0.2, true
      );
      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.HEAT_EFFECT.get(), this.getX(), this.getEyeY(), this.getZ(), 55, 0.08, 0.08, 0.08, 0.2, true
      );
      AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 5.0);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(LivingEntity.class, aabb, entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && entity != this);
      if (!list.isEmpty()) {
         DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.HEAT_WAVE, this);

         for (LivingEntity target : list) {
            target.hurt(source, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            target.setDeltaMovement(0.0, 0.1, 0.0);
            SkillHelper.knockBack(this, target, constant ? 1.0F : 2.0F);
         }
      }
   }

   private void summonIfrit(@Nullable LivingEntity target) {
      IExistence existence = TensuraStorages.getExistenceFrom(this);
      if (!existence.isSkippingEPDrop()) {
         existence.setSkippingEPDrop(true);
         existence.markDirty();
         Mob entity = this.getSpiritSummoning();
         entity.setPos(this.position());
         entity.setTarget(target);
         this.level().addFreshEntity(entity);
         this.discard();
         TensuraParticleHelper.spawnServerParticles(
            this.level(), ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ(), 55, 0.08, 0.08, 0.08, 0.5, true
         );
         this.level()
            .playSound(
               null,
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.GENERIC_EXPLODE,
               TensuraSkill.ABILITY_SOUND,
               0.5F,
               0.4F + (this.random.nextFloat() * 0.4F + 0.8F)
            );
      }
   }

   private Mob getSpiritSummoning() {
      IfritEntity entity = new IfritEntity((EntityType<? extends IfritEntity>)MonsterEntityTypes.IFRIT.get(), this.level());
      entity.setHostNBT(this.saveWithoutId(new CompoundTag()));
      entity.setBoss(true);
      entity.triggerAnim("miscController", "burst");
      return entity;
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         this.populateDefaultEquipmentSlots(this.random, pDifficulty);
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      ItemStack mask = new ItemStack((ItemLike)TensuraArmorItems.ANTI_MAGIC_MASK.get());
      this.setItemSlot(EquipmentSlot.HEAD, mask);
      this.inventory.setItem(this.getSlotId(EquipmentSlot.HEAD), mask);
      this.updateContainerEquipment();
      ItemStack stack = new ItemStack((ItemLike)TensuraToolItems.PURE_MAGISTEEL_LONG_SWORD.get());
      this.setItemSlot(EquipmentSlot.MAINHAND, stack);
      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
      this.updateContainerEquipment();
   }

   @Override
   public void die(DamageSource source) {
      if (this.getTransformTick() < 100) {
         LivingEntity target = this.getTarget();
         if (target == null && source.getEntity() instanceof LivingEntity living) {
            target = living;
         }

         this.summonIfrit(target);
      } else {
         super.die(source);
      }
   }

   @Override
   protected float getEquipmentDropChance(EquipmentSlot pSlot) {
      if (this.isTame()) {
         return 0.0F;
      } else {
         return pSlot.equals(EquipmentSlot.HEAD) && this.level().getGameRules().getBoolean(TensuraGameRules.RIMURU_MODE)
            ? 1.0F
            : super.getEquipmentDropChance(pSlot);
      }
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<ShizuEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new SleepSensor()});
   }

   public BrainActivityGroup<ShizuEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new WakeUp().stopIf(entity -> {
               if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
                  entity.stopSleeping();
                  return true;
               } else {
                  return false;
               }
            }).startCondition(entity -> !entity.isDying()),
            new LookAtTarget().startCondition(entity -> !entity.isDying()),
            new FloatToSurfaceOfFluid().startCondition(entity -> !entity.isDying()),
            new MoveToWalkTarget()
               .cooldownFor(entity -> 0)
               .startCondition(entity -> !entity.isOrderedToSit() && !entity.isDying())
               .stopIf(entity -> entity.isOrderedToSit() || entity.isDying())
         }
      );
   }

   public BrainActivityGroup<ShizuEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldTarget),
                  new SubordinateFollowOwner(),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new InteractWithDoor(),
            new HumanoidConsumeItem().startCondition(entity -> entity.shouldHeal()).stopIf(entity -> !entity.shouldHeal()),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<ShizuEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf((shizu, target) -> shizu.isDying() || shizu.shouldStopTarget(shizu, target)),
            new StrafeTarget().stopStrafingWhen(entity -> !entity.usingRangedWeapon()).startCondition(PlayerLikeEntity::usingRangedWeapon),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F).startCondition(entity -> !entity.usingRangedWeapon()),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  PlayerLikeEntity.getSpearAttack(20).attackInterval(entity -> 5).attackRadius(25.0F),
                  PlayerLikeEntity.getCrossbowAttack().attackInterval(entity -> 10).attackRadius(30.0F),
                  PlayerLikeEntity.getBowAttack().attackInterval(entity -> 10).attackRadius(25.0F),
                  new CustomRangeAttack(15).maxAttackRadius(7.0F).performAttack((entity, target) -> {
                     entity.combust(false);
                     entity.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 0.95F + entity.getRandom().nextFloat() * 0.1F);
                  }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.1),
                  new CustomRangeAttack(5).maxAttackRadius(25.0F).attackInterval(entity -> 60).performAttack((entity, target) -> {
                     entity.hellFire(target);
                     entity.playSound((SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), 10.0F, 0.95F + entity.getRandom().nextFloat() * 0.1F);
                  }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.1),
                  new CustomRangeAttack(2)
                     .minAttackRadius(5.0F)
                     .maxAttackRadius(32.0F)
                     .attackInterval(entity -> 20)
                     .performAttack((entity, target) -> entity.fireBallShoot(target, entity.getRandom().nextFloat() <= 0.2F))
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.2),
                  new AnimatableMeleeAttack(1)
                     .attackInterval(entity -> 5)
                     .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
                     .startCondition(entity -> !entity.usingRangedWeapon())
               }
            )
         }
      );
   }

   public Map<Activity, BrainActivityGroup<? extends ShizuEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends ShizuEntity>>)Util.make(
         new Object2ObjectOpenHashMap(), map -> map.put(Activity.REST, TensuraBehaviourHelper.getHumanoidSleepActivityGroup(this))
      );
   }

   public SmartBrainSchedule getSchedule() {
      if (this.schedule == null) {
         this.schedule = new SmartBrainSchedule().activityAt(10, Activity.IDLE).activityAt(13000, Activity.REST);
      }

      return this.schedule;
   }

   public void startSleeping(BlockPos blockPos) {
      super.startSleeping(blockPos);
      BrainUtils.setMemory(this, MemoryModuleType.LAST_SLEPT, this.level().getGameTime());
      BrainUtils.clearMemory(this, MemoryModuleType.WALK_TARGET);
      BrainUtils.clearMemory(this, MemoryModuleType.LOOK_TARGET);
      BrainUtils.clearMemory(this, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
   }

   public void stopSleeping() {
      super.stopSleeping();
      BrainUtils.setMemory(this, MemoryModuleType.LAST_WOKEN, this.level().getGameTime());
   }
}
