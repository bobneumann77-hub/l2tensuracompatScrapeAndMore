package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.LeapToTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetWalkTargetToSpecificBlock;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.storage.labyrinth.ILabyrinth;
import io.github.manasmods.tensura.storage.labyrinth.LabyrinthStorage;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
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

public class ElementalColossusEntity extends TensuraMountEntity implements GeoEntity, SmartBrainOwner<ElementalColossusEntity>, ITensuraMount {
   private static final EntityDataAccessor<Boolean> SLAMMING_FALL = SynchedEntityData.defineId(ElementalColossusEntity.class, EntityDataSerializers.BOOLEAN);
   private final ServerBossEvent bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossBarColor.PINK, BossBarOverlay.NOTCHED_20)
      .setPlayBossMusic(true);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ElementalColossusEntity(EntityType<? extends ElementalColossusEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public ElementalColossusEntity(ServerLevel level, Vec3 pos, MobSpawnType spawnType) {
      super((EntityType<? extends TensuraMountEntity>)MonsterEntityTypes.ELEMENTAL_COLOSSUS.get(), level);
      this.setPos(pos);
      this.finalizeSpawn(level, level.getCurrentDifficultyAt(this.blockPosition()), spawnType, null);
   }

   @NotNull
   @Override
   protected PathNavigation createNavigation(Level level) {
      return new SmoothGroundNavigation(this, level);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 40.0)
         .add(Attributes.MAX_HEALTH, 600.0)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.ATTACK_DAMAGE, 50.0)
         .add(Attributes.ATTACK_KNOCKBACK, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.JUMP_STRENGTH, 2.5)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.2F)
         .add(Attributes.STEP_HEIGHT, 3.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 5.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 20.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 4.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 4.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SLAMMING_FALL, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("SlammingFall", this.isSlammingFall());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setSlammingFall(compound.getBoolean("SlammingFall"));
   }

   public boolean isSlammingFall() {
      return (Boolean)this.entityData.get(SLAMMING_FALL);
   }

   public void setSlammingFall(boolean falling) {
      this.entityData.set(SLAMMING_FALL, falling);
   }

   @Override
   public EntityDimensions getSleepingDimensions(Pose pPose) {
      return this.getType().getDimensions().scale(this.getAgeScale()).scale(1.0F, 0.75F);
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   @Override
   public void setSleeping(boolean sleeping) {
      this.entityData.set(SLEEPING, sleeping);
      this.refreshDimensions();
      if (sleeping) {
         this.clearTarget();
         this.setYRot(180.0F);
         this.yRotO = this.getYRot();
         this.setXRot(0.0F);
         this.setRot(180.0F, 0.0F);
         this.yBodyRot = this.getYRot();
         this.yHeadRot = this.yBodyRot;
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypeTags.IS_PROJECTILE) && TensuraDamageHelper.isPierce(source)
         ? true
         : source.is(DamageTypes.IN_WALL)
            || source.type().effects().equals(DamageEffects.POKING)
            || source.is(DamageTypes.HOT_FLOOR)
            || super.isInvulnerableTo(source);
   }

   public boolean canBeAffected(MobEffectInstance instance) {
      if (instance.is(MobEffects.POISON)) {
         return false;
      } else if (instance.is(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON))) {
         return false;
      } else if (instance.is(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS))) {
         return false;
      } else if (instance.is(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION))) {
         return false;
      } else {
         return instance.is(TensuraMobEffects.getReference(TensuraMobEffects.BURDEN)) ? false : super.canBeAffected(instance);
      }
   }

   @Override
   public boolean isSaddleRequired() {
      return false;
   }

   public void setTarget(@Nullable LivingEntity entity) {
      super.setTarget(entity);
      if (entity != null && this.isAlive() && !this.isTame()) {
         LabyrinthStorage.removePassedEntity(entity, true);
      }
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (this.isSleeping()) {
         pAmount *= 0.1F;
      }

      if (TensuraDamageHelper.isNaturalEffects(pSource)) {
         pAmount *= 0.2F;
      }

      boolean hurt = super.hurt(pSource, pAmount);
      if (hurt && this.isAlive() && !this.isTame() && pSource.getEntity() instanceof LivingEntity target) {
         LivingEntity owner = SubordinateHelper.getSubordinateOwner(target);
         if (owner != null) {
            target = owner;
         }

         LabyrinthStorage.removePassedEntity(target, true);
      }

      return hurt;
   }

   public boolean killedEntity(ServerLevel pLevel, LivingEntity pEntity) {
      boolean killed = super.killedEntity(pLevel, pEntity);
      if (!this.isTame()) {
         markAsPassed(this, pEntity, true, false);
         BrainUtils.clearMemories(this, new MemoryModuleType[]{MemoryModuleType.WALK_TARGET});
      }

      return killed;
   }

   public static void markAsPassed(LivingEntity protector, LivingEntity target, boolean teleportToEntrance, boolean defeatProtector) {
      if (defeatProtector) {
         LivingEntity owner = SubordinateHelper.getSubordinateOwner(target);
         if (owner != null) {
            if (target instanceof Player) {
               markAsPassed(protector, owner, teleportToEntrance, true);
            } else {
               target = owner;
            }
         }
      }

      SubordinateHelper.removeTarget(protector);
      LabyrinthStorage.addPassedEntity(target, defeatProtector);
      if (teleportToEntrance) {
         if (target instanceof ServerPlayer player) {
            ((PlayerTrigger)TensuraCriteriaTriggers.DIED_TO_COLOSSUS.get()).trigger(player);
         }

         MinecraftServer server = target.level().getServer();
         if (server != null) {
            ServerLevel serverLevel = server.getLevel(TensuraDimensions.LABYRINTH);
            if (serverLevel != null) {
               ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
               Vec3 vec3 = labyrinth.getPassedEntrancePos();
               target.unRide();
               target.teleportTo(vec3.x(), vec3.y(), vec3.z());
               target.hurtMarked = true;
            }
         }
      }
   }

   protected boolean canRide(Entity entity) {
      return false;
   }

   public void setCustomName(@Nullable Component pName) {
      super.setCustomName(pName);
      this.bossEvent.setName(this.getDisplayName());
   }

   public void startSeenByPlayer(ServerPlayer pPlayer) {
      super.startSeenByPlayer(pPlayer);
      if (!this.isTame()) {
         this.bossEvent.addPlayer(pPlayer);
      }
   }

   public void stopSeenByPlayer(ServerPlayer pPlayer) {
      super.stopSeenByPlayer(pPlayer);
      this.bossEvent.removePlayer(pPlayer);
   }

   protected void customServerAiStep() {
      this.tickBrain(this);
      if (!this.isTame()) {
         this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
      }
   }

   @Override
   protected void applyTamingSideEffects() {
      super.applyTamingSideEffects();
      this.bossEvent.removeAllPlayers();
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.isSlammingFall() && this.isInLiquid()) {
            this.setSlammingFall(false);
         }
      }
   }

   @Override
   protected void sleepHandler() {
      if (this.isSleeping()) {
         if (this.isAngry() || this.isVehicle() || this.isPassenger() || this.shouldFollowOwner()) {
            this.setSleeping(false);
         }

         if (this.tickCount % 20 == 0) {
            this.heal(30.0F);
         }
      } else if (this.isOrderedToSit() && this.tickCount % 20 == 0) {
         this.heal(20.0F);
      } else if (this.tickCount % 40 == 0) {
         this.heal(10.0F);
      }

      if (!this.isTame() && !this.isAngry() && !this.isSleeping() && this.isAlive()) {
         MinecraftServer server = this.level().getServer();
         if (server != null) {
            ServerLevel serverLevel = server.getLevel(TensuraDimensions.LABYRINTH);
            if (serverLevel != null) {
               ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
               if (labyrinth != null && this.distanceToSqr(labyrinth.getColossusPos()) < 4.0) {
                  this.setSleeping(true);
                  BrainUtils.clearMemory(this, MemoryModuleType.WALK_TARGET);
               }
            }
         }
      }
   }

   public void spinAttack() {
      this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP);
      AABB aabb = this.getBoundingBox().inflate(4.0);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(LivingEntity.class, aabb, entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && !entity.equals(this));
      if (!list.isEmpty()) {
         double damageMultiplier = 1.5;

         for (LivingEntity target : list) {
            if (target.hurt(this.damageSources().mobAttack(this), (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier))) {
               if (target.getHealth() >= 1.0F) {
                  SkillHelper.knockBack(this, target, 2.0F);
               }
            } else if (target.getHealth() >= 1.0F) {
               SkillHelper.knockBack(this, target, 1.5F);
            }
         }
      }
   }

   @Override
   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      if (this.isSlammingFall() && pFallDistance > 3.0F) {
         this.triggerAnim("bodyController", "slam");
         EffectStorage.setCameraShake(this, 10.0, 0.1F, 15);
         AABB aabb = AABB.ofSize(this.position(), 10.0, 10.0, 10.0);

         for (LivingEntity target : this.level()
            .getEntitiesOfClass(LivingEntity.class, aabb, entity -> !entity.isAlliedTo(this) && entity.isAlive() && entity != this.getOwner() && entity != this)) {
            this.doHurtTarget(target, 2.0F);
            SkillHelper.knockBack(this, target, 2.0F);
         }

         if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            SkillHelper.launchBlock(
               this,
               this.position(),
               5,
               1,
               0.5F,
               0.3F,
               blockState -> this.getRandom().nextInt(3) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE),
               blockPos -> true
            );
         }

         this.level()
            .explode(
               this,
               this.damageSources().mobAttack(this),
               null,
               this.getOnPos().getX(),
               this.getOnPos().getY(),
               this.getOnPos().getZ(),
               3.0F,
               false,
               ExplosionInteraction.MOB
            );
         return false;
      } else {
         this.setSlammingFall(false);
         this.playBlockFallSound();
         return true;
      }
   }

   protected boolean isHoldingTargets() {
      return this.getControllingPassenger() != null ? this.getPassengers().size() >= 2 : this.isVehicle();
   }

   public boolean canFallInLove() {
      return false;
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return false;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.ELEMENTAL_CORES);
   }

   @Override
   public InteractionResult onNormalHeal(Player player, InteractionHand hand, ItemStack stack) {
      this.usePlayerItem(player, hand, stack);
      this.applyFoodHeal(stack, player, hand);
      this.level().playSound(null, this, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.NEUTRAL, 1.0F, 1.0F);
      return InteractionResult.SUCCESS;
   }

   @Override
   public void applyFoodHeal(ItemStack stack, Player player, InteractionHand hand) {
      if (stack.is((Item)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get())) {
         this.heal(100.0F);
      } else if (stack.is((Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get())) {
         this.heal(300.0F);
      } else if (stack.is((Item)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get())) {
         this.heal(500.0F);
      } else if (stack.is((Item)TensuraMaterialItems.ADAMANTITE_INGOT.get())) {
         this.heal(1000.0F);
      } else if (stack.is((Item)TensuraMaterialItems.HIHIIROKANE_INGOT.get())) {
         this.heal(2000.0F);
      }

      this.ate();
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      if (this.getControllingPassenger() != null && this.getControllingPassenger().equals(entity)) {
         Vec3 vec3 = new Vec3(0.0, -0.5, 0.4 * f).yRot(-this.getYRot() * 0.0174F);
         return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
      } else {
         float yaw = this.getYRot() * (float) (Math.PI / 180.0);
         float f1 = Mth.sin((float)(yaw + Math.toRadians(145.0)));
         float f2 = -Mth.cos((float)(yaw + Math.toRadians(145.0)));
         Vec3 vec3 = new Vec3(0.0, -3.0, 0.6 * f).yRot(-this.getYRot() * 0.0174F);
         return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3.add(f1, 0.0, f2));
      }
   }

   @Override
   public void mountAbility(Player rider) {
      if (!this.isSlammingFall()) {
         if (!this.isHoldingTargets()) {
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(rider, 8.0, false);
            if (target == null) {
               return;
            }

            this.doHurtTarget(target, 0.5F);
            if (target.getHealth() >= 1.0F && target.distanceTo(this) < 5.0F) {
               target.startRiding(this, true);
            }

            this.playSound(SoundEvents.IRON_GOLEM_ATTACK);
            this.triggerAnim("miscController", "grab");
         } else {
            for (Entity passenger : this.getPassengers()) {
               if (!Objects.equals(this.getControllingPassenger(), passenger)) {
                  passenger.unRide();
                  Vec3 throwVec = this.getLookAngle().normalize().scale(5.0);
                  if (this.getOwner() instanceof Player owner && this.getControllingPassenger() instanceof Player controller && controller.equals(owner)) {
                     throwVec = owner.getLookAngle().normalize().scale(5.0);
                  }

                  passenger.setDeltaMovement(passenger.getDeltaMovement().add(throwVec.x(), 1.0 + throwVec.y(), throwVec.z()));
                  this.doHurtTarget(passenger, 2.0F);
                  passenger.hurtMarked = true;
               }
            }

            this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP);
            this.triggerAnim("miscController", "throw");
         }
      }
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      this.setSleeping(true);
      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   @Override
   protected boolean removeWhenNoAction() {
      return false;
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return super.shouldRenderAtSqrDistance(pDistance) || pDistance < 1024.0;
   }

   public void remove(RemovalReason pReason) {
      super.remove(pReason);
      if (this.level().dimension().equals(TensuraDimensions.LABYRINTH)) {
         ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(this.level());
         if (labyrinth.isColossusSpawned()) {
            labyrinth.setColossusSpawned(false);
         }
      }
   }

   public void die(DamageSource pDamageSource) {
      super.die(pDamageSource);
      if (this.getPose() == Pose.DYING) {
         this.triggerAnim("miscController", "death");
      }
   }

   protected void tickDeath() {
      if (++this.deathTime >= 40) {
         this.remove(RemovalReason.KILLED);
         this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 1.0F);
         if (this.level().dimension().equals(TensuraDimensions.LABYRINTH)) {
            ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(this.level());
            if (labyrinth.isColossusSpawned()) {
               labyrinth.setColossusSpawned(false);
            }
         }

         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.POOF, 3.0);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.CLOUD, 3.0);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.POOF, 2.0);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.CLOUD, 1.0);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.POOF);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.CLOUD);
      }
   }

   public boolean shouldDropExperience() {
      return TensuraStorages.getExistenceFrom(this).getSpawnType() == MobSpawnType.TRIGGERED ? false : super.shouldDropExperience();
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.GOLEM_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.GOLEM_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.GOLEM_DEATH.get();
   }

   protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockIn) {
      this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   public List<ExtendedSensor<ElementalColossusEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<ElementalColossusEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<ElementalColossusEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldAttack, this::shouldAttack),
                  new SetWalkTargetToSpecificBlock()
                     .closeEnoughWhen((entity, pos) -> 1)
                     .setTargetPos(entity -> BlockPos.containing(TensuraStorages.getLabyrinthFrom(entity.level()).getColossusPos()))
                     .startCondition(entity -> {
                        if (entity.isTame()) {
                           return false;
                        } else {
                           return entity.isSleeping() ? false : entity.level().dimension().equals(TensuraDimensions.LABYRINTH) && !entity.isAngry();
                        }
                     }),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  new SubordinateFollowOwner().speedMod(1.5F),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{
                     new SetRandomWalkTarget().startCondition(TamableAnimal::isTame), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
                  }
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<ElementalColossusEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 1.5F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomHeldAttack()
                     .minAttackRadius(0.0F)
                     .maxAttackRadius(6.0F)
                     .onTick((entity, target, tick) -> {
                        if (tick >= 10 && tick <= 20) {
                           entity.spinAttack();
                        }

                        return tick < 25;
                     })
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.3 && !entity.isHoldingTargets())
                     .whenStarting(entity -> entity.triggerAnim("bodyController", "spin")),
                  new CustomRangeAttack(10).maxAttackRadius(5.0F).attackInterval(entity -> 10).performAttack((entity, target) -> {
                     if (!entity.isHoldingTargets()) {
                        entity.doHurtTarget(target, 0.5F);
                        if (target.getHealth() >= 1.0F && target.distanceTo(this) < 5.0F) {
                           target.startRiding(entity, true);
                        }

                        entity.playSound(SoundEvents.IRON_GOLEM_ATTACK);
                     } else {
                        for (Entity passenger : entity.getPassengers()) {
                           if (passenger != entity.getControllingPassenger()) {
                              passenger.unRide();
                              Vec3 throwVec = entity.getLookAngle().scale(5.0);
                              passenger.setDeltaMovement(passenger.getDeltaMovement().add(throwVec.x(), 1.0 + throwVec.y(), throwVec.z()));
                              entity.doHurtTarget(passenger, 2.0F);
                              passenger.hurtMarked = true;
                           }
                        }

                        entity.playSound(SoundEvents.PLAYER_ATTACK_SWEEP);
                     }
                  }).whenStarting(entity -> {
                     if (entity.isHoldingTargets()) {
                        entity.triggerAnim("miscController", "throw");
                     } else {
                        entity.triggerAnim("miscController", "grab");
                     }
                  }).startCondition(entity -> entity.getControllingPassenger() == null && (entity.isHoldingTargets() || entity.getRandom().nextFloat() <= 0.05)),
                  new AnimatableMeleeAttack(11)
                     .attackInterval(entity -> 1)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "punch"))
                     .startCondition(entity -> !entity.isHoldingTargets()),
                  new LeapToTarget(15)
                     .minRange((entity, target) -> 10.0F)
                     .leapRange((entity, target) -> 64.0F)
                     .jumpStrength((entity, target) -> entity.getJumpPower(1.0F))
                     .verticalJumpStrength((entity, target) -> entity.getJumpPower(0.5F))
                     .attackInterval(entity -> 30)
                     .whenStarting(entity -> {
                        entity.setSlammingFall(true);
                        entity.triggerAnim("bodyController", "jump");
                        BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(BrainUtils.getTargetOfEntity(entity), true));
                     })
                     .startCondition(entity -> entity.getControllingPassenger() == null && !entity.isHoldingTargets() && entity.getRandom().nextFloat() <= 0.2)
               }
            )
         }
      );
   }

   public boolean shouldAttack(LivingEntity entity) {
      if (RaceUtils.isNonLiving(entity)) {
         return false;
      }

      if (entity.hasInfiniteMaterials() || entity.isSpectator()) {
         return false;
      }

      if (LabyrinthStorage.isEntityPassedColossus(entity)) {
         return false;
      }

      MinecraftServer server = entity.level().getServer();
      if (server == null) {
         return true;
      }

      ServerLevel serverLevel = server.getLevel(TensuraDimensions.LABYRINTH);
      if (serverLevel == null) {
         return true;
      }

      ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
      double range = labyrinth.getAreaRadius();
      return !(labyrinth.getColossusPos().distanceToSqr(entity.position()) > range * range);
   }

   public boolean shouldStopTarget(Mob subordinate, LivingEntity target) {
      if (LabyrinthStorage.isEntityPassedColossus(target)) {
         return true;
      }

      MinecraftServer server = subordinate.level().getServer();
      if (server != null) {
         ServerLevel serverLevel = server.getLevel(TensuraDimensions.LABYRINTH);
         if (serverLevel != null) {
            ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(serverLevel);
            double range = labyrinth.getAreaRadius();
            if (labyrinth.getColossusPos().distanceToSqr(target.position()) > range * range) {
               return true;
            }
         }
      }

      return super.shouldStopTarget(subordinate, target);
   }

   protected PlayState loopController(AnimationState<ElementalColossusEntity> state) {
      String name;
      if (!this.isSleeping() && !this.isInSittingPose()) {
         if (this.onGround() || !(this.fallDistance > 2.0F) && !(this.getDeltaMovement().y() > 0.0)) {
            if (state.isMoving()) {
               name = "animation.elemental_colossus.walk";
            } else {
               name = "animation.elemental_colossus.idle";
            }
         } else {
            name = "animation.elemental_colossus.fall";
         }
      } else {
         name = "animation.elemental_colossus.idle_deactivated";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("punch", RawAnimation.begin().then("animation.elemental_colossus.punch", LoopType.PLAY_ONCE))
               .triggerableAnim("grab", RawAnimation.begin().then("animation.elemental_colossus.grab", LoopType.PLAY_ONCE))
               .triggerableAnim("throw", RawAnimation.begin().then("animation.elemental_colossus.throw", LoopType.PLAY_ONCE))
               .triggerableAnim("death", RawAnimation.begin().then("animation.elemental_colossus.death", LoopType.PLAY_ONCE)),
            new AnimationController(this, "bodyController", 0, event -> PlayState.STOP)
               .triggerableAnim("spin", RawAnimation.begin().then("animation.elemental_colossus.spin", LoopType.PLAY_ONCE))
               .triggerableAnim("jump", RawAnimation.begin().then("animation.elemental_colossus.jump", LoopType.PLAY_ONCE))
               .triggerableAnim("slam", RawAnimation.begin().then("animation.elemental_colossus.slam_fall", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
