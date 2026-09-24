package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.OrbitAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.projectile.magic.FireBoltProjectile;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IElementalSpirit;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.pathfinder.PathType;
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

public class SalamanderEntity extends TensuraTamableEntity implements GeoEntity, SmartBrainOwner<SalamanderEntity>, IFlying, IElementalSpirit, INameEvolution {
   protected static final EntityDataAccessor<Boolean> CAN_SELF_DESTRUCT = SynchedEntityData.defineId(SalamanderEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(SalamanderEntity.class, EntityDataSerializers.BOOLEAN);
   protected int flyingTick;
   protected boolean wasFlying;
   protected int fireTrailTime;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public SalamanderEntity(EntityType<? extends SalamanderEntity> type, Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.initFlying(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 6.0)
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.2F)
         .add(Attributes.FLYING_SPEED, 0.6F)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 10.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 2.0);
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
      builder.define(FLYING, false);
      builder.define(CAN_SELF_DESTRUCT, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putBoolean("CanSelfDestruct", this.canSelfDestruct());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(compound.getBoolean("Flying"));
      this.setCanSelfDestruct(compound.getBoolean("CanSelfDestruct"));
   }

   public boolean canSelfDestruct() {
      return (Boolean)this.entityData.get(CAN_SELF_DESTRUCT);
   }

   public void setCanSelfDestruct(boolean selfDestruct) {
      this.entityData.set(CAN_SELF_DESTRUCT, selfDestruct);
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

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof SalamanderEntity cat) {
         return cat.isTame() == this.isTame();
      } else {
         return entity instanceof IfritEntity ifrit ? ifrit.isTame() == this.isTame() : false;
      }
   }

   public boolean canAttack(LivingEntity pTarget) {
      return this.isAlliedTo(pTarget) ? false : super.canAttack(pTarget);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || super.isInvulnerableTo(source);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (!this.isTame()) {
         if (pSource.getEntity() instanceof SalamanderEntity cat && !cat.isTame()) {
            return false;
         }

         if (pSource.getEntity() instanceof IfritEntity ifrit && !ifrit.isTame()) {
            return false;
         }
      }

      return super.hurt(pSource, pAmount);
   }

   protected void actuallyHurt(DamageSource source, float damage) {
      damage *= this.getPhysicalAttackInput(source);
      super.actuallyHurt(source, damage);
   }

   public boolean isOnFire() {
      return TensuraStorages.getEffectFrom(this).isOnBlackFlame();
   }

   @Override
   public boolean canBeNamed(Player player) {
      return !this.isTamedByNonPlayer();
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
      if (this.level().isClientSide() && !this.isInWater()) {
         Vec3 vec3 = this.getViewVector(0.0F);
         float f = Mth.cos(this.yBodyRot * (float) (Math.PI / 180.0)) * 0.3F;
         float f1 = Mth.sin(this.yBodyRot * (float) (Math.PI / 180.0)) * 0.3F;
         double yPos = this.getEyeY() + 0.2F;
         float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
         float radius = 0.3F;
         double extraX = Mth.sin((float)(Math.PI + angle));
         double extraZ = Mth.cos(angle);
         if (this.isInSittingPose()) {
            radius = 0.0F;
            f *= 0.8F;
            f1 *= 0.8F;
            yPos -= 0.5;
         } else if (this.onGround()) {
            radius = 0.2F;
         } else if (this.getDeltaMovement().lengthSqr() > 0.03) {
            f *= 0.6F;
            f1 *= 0.6F;
            yPos -= 0.1F;
         }

         this.level()
            .addParticle(
               (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(),
               this.getX() - vec3.x * 0.1 + f + extraX * radius,
               yPos,
               this.getZ() - vec3.z * 0.1 + f1 + extraZ * radius,
               0.0,
               0.0,
               0.0
            );
         this.level()
            .addParticle(
               (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(),
               this.getX() - vec3.x * 0.1 - f + extraX * radius,
               yPos,
               this.getZ() - vec3.z * 0.1 - f1 + extraZ * radius,
               0.0,
               0.0,
               0.0
            );
      }
   }

   public void shootFireBolt(@NotNull LivingEntity target, float v) {
      FireBoltProjectile ball = new FireBoltProjectile(this.level(), this);
      ball.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.FLAME_MANIPULATION.get()));
      float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
      double xOffset = Mth.sin((float)(Math.PI + angle));
      double zOffset = Mth.cos(angle);
      ball.moveTo(this.getX() + xOffset, this.getEyeY() - 0.2, this.getZ() + zOffset, this.getYRot(), this.getXRot());
      ball.setDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 1.0));
      ball.setBurnTicks(100);
      ball.shootToward(target, 1.5F, 0.0F);
      this.level().addFreshEntity(ball);
   }

   public void spawnFireTrail() {
      Level level = this.level();
      AABB aabb = new AABB(this.blockPosition().below(2).getCenter(), this.blockPosition().below(8).getCenter());

      for (LivingEntity living : level.getEntitiesOfClass(
         LivingEntity.class, aabb.inflate(2.0), livingx -> !livingx.isAlliedTo(this) && !this.isOwnedBy(livingx)
      )) {
         DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(level, TensuraDamageTypes.FLAME_BREATH, this)
            .tensura$setAbilityInstance(SkillUtils.getSkillOrNull(this, (ManasSkill)IntrinsicSkills.FLAME_BREATH.get()))
            .tensura$setSkillType(Skill.SkillType.INTRINSIC);
         if (living.hurt(damageSource, 4.0F)) {
            living.setRemainingFireTicks(60);
         }
      }

      float radius = this.getBbWidth();
      double x = this.getX() + (level.random.nextDouble() - 0.5) * radius;
      double y = this.getY() - 1.0 + (level.random.nextDouble() - 0.5) * radius * 0.75;
      double z = this.getZ() + (level.random.nextDouble() - 0.5) * radius;

      for (int i = 0; i < 6; i++) {
         for (int j = 0; j < 5; j++) {
            double newX = x + this.getRandom().nextGaussian() / 2.0;
            double newY = y + this.getRandom().nextGaussian() / 2.0;
            double newZ = z + this.getRandom().nextGaussian() / 2.0;
            TensuraParticleHelper.spawnServerParticles(
               level, (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), newX, newY - i, newZ, 0, 0.0, -0.1, 0.0, 0.0, false
            );
         }
      }
   }

   private void explode() {
      if (!this.level().isClientSide) {
         ExplosionInteraction interaction = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
            ? ExplosionInteraction.BLOCK
            : ExplosionInteraction.NONE;
         this.level().explode(this, this.getX(), this.getY(), this.getZ(), 6.0F, interaction);
         DamageSource source = TensuraDamageTypes.getIndirectEntityDamageSource(this.level(), TensuraDamageTypes.SUICIDE, this.getOwner(), this);
         this.hurt(source, this.getMaxHealth());
      }
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      return null;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.SPIRIT_FOOD);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return pStack.is((Item)TensuraMaterialItems.FIRE_ELEMENTAL_SHARD.get());
   }

   @Override
   public Element getElemental() {
      return Element.FLAME;
   }

   @Override
   public SpiritualMagic.SpiritLevel getSpiritLevel() {
      return SpiritualMagic.SpiritLevel.MEDIUM;
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (!this.isTame() || !this.isOwnedBy(player)) {
         return InteractionResult.PASS;
      }

      if (this.convertElementalCore(this, player, hand, (Item)TensuraMaterialItems.ELEMENT_CORE_FIRE.get())) {
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      }

      InteractionResult golemInteraction = this.getGolemInteraction(player, hand, this);
      if (golemInteraction.consumesAction()) {
         return golemInteraction;
      }

      this.cycleCommands(this, player);
      return InteractionResult.sidedSuccess(this.level().isClientSide());
   }

   @Override
   public void applyFoodHeal(ItemStack stack, Player player, InteractionHand hand) {
      this.heal(5.0F);
      this.ate();
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   public void cycleCommands(Mob subordinate, Player player) {
      if (!subordinate.level().isClientSide()) {
         MutableComponent message;
         if (this.canSelfDestruct()) {
            message = Component.translatable("tensura.message.pet.follow", new Object[]{this.getDisplayName()});
            this.setOrderedToSit(false);
            this.setWandering(false);
            this.setCanSelfDestruct(false);
         } else if (!this.isWandering() && !this.isOrderedToSit()) {
            message = Component.translatable("tensura.message.pet.wander", new Object[]{this.getDisplayName()});
            this.setOrderedToSit(false);
            this.setTarget(null);
            this.setCanSelfDestruct(false);
            this.setWandering(true);
            this.setWanderPos(player.getOnPos().above());
         } else if (this.isWandering()) {
            message = Component.translatable("tensura.message.pet.stay", new Object[]{this.getDisplayName()});
            this.getNavigation().stop();
            this.setOrderedToSit(true);
            this.setWandering(false);
            this.setTarget(null);
            this.setCanSelfDestruct(false);
         } else {
            message = Component.translatable("tensura.message.pet.self_destruct", new Object[]{this.getDisplayName()});
            this.setOrderedToSit(false);
            this.setWandering(false);
            this.setTarget(null);
            this.setCanSelfDestruct(true);
         }

         player.displayClientMessage(message.setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true);
      }
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.salamander, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canSpawnSpecialVariant(pReason)
         && pLevel.getBiome(this.blockPosition()).is(TensuraBiomeTags.ANCIENT_FOREST)
         && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.greaterSpiritChance, pLevel.getRandom())) {
         IfritEntity spirit = new IfritEntity((EntityType<? extends IfritEntity>)MonsterEntityTypes.IFRIT.get(), this.level());
         spirit.setPos(this.getX(), this.getY(), this.getZ());
         spirit.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
         this.level().addFreshEntity(spirit);
         this.setRemoved(RemovalReason.DISCARDED);
         return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
      } else {
         return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
      }
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_DEATH.get();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<SalamanderEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<SalamanderEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<SalamanderEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false),
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

   public BrainActivityGroup<SalamanderEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new OrbitAttack()
               .orbitRadius((entity, target) -> 10.0)
               .canDoOrbitalAttack((entity, target) -> entity.shouldSelfDestruct())
               .onStartOrbitAttack((entity, target) -> {
                  if (entity.onGround()) {
                     entity.triggerAnim("miscController", "self_destruct_still");
                  } else {
                     entity.triggerAnim("miscController", "self_destruct");
                  }
               })
               .performOrbitAttack((entity, target) -> entity.explode())
               .onOrbitTick((entity, target, pair) -> {
                  entity.fireTrailTime--;
                  if (entity.fireTrailTime <= -200) {
                     entity.fireTrailTime = 100;
                  }

                  if (entity.fireTrailTime > 0) {
                     entity.spawnFireTrail();
                  }

                  return true;
               })
               .onTick(entity -> {
                  entity.setFlying(true);
                  return true;
               }),
            new CustomRangeAttack(10).maxAttackRadius(40.0F).attackInterval(entity -> 60).performAttack((entity, target) -> {
               entity.lookAt(Anchor.EYES, target.getEyePosition());
               entity.shootFireBolt(target, 1.0F);
            }).whenStarting(entity -> entity.triggerAnim("miscController", "fire_ball"))
         }
      );
   }

   private boolean shouldSelfDestruct() {
      if (!this.isTame()) {
         if (this.getHealth() >= this.getMaxHealth() * 0.1F) {
            return false;
         }

         if (this.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))) {
            return true;
         }
      }

      return this.canSelfDestruct();
   }

   protected PlayState loopController(AnimationState<SalamanderEntity> state) {
      String name;
      if (this.isNoAi()) {
         name = "animation.salamander.idle";
      } else if (this.isInSittingPose()) {
         name = "animation.salamander.stay";
      } else if (state.isMoving()) {
         if (this.onGround()) {
            name = "animation.salamander.walk";
         } else {
            name = "animation.salamander.fly";
         }
      } else if (this.onGround()) {
         name = "animation.salamander.idle";
      } else {
         name = "animation.salamander.idle_fly";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("eat", RawAnimation.begin().then("animation.salamander.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("eat_still", RawAnimation.begin().then("animation.salamander.eat_still", LoopType.PLAY_ONCE))
               .triggerableAnim("fire_ball", RawAnimation.begin().then("animation.salamander.fire_ball", LoopType.PLAY_ONCE))
               .triggerableAnim("self_destruct", RawAnimation.begin().then("animation.salamander.self_destruct", LoopType.PLAY_ONCE))
               .triggerableAnim("self_destruct_still", RawAnimation.begin().then("animation.salamander.self_destruct_still", LoopType.PLAY_ONCE))
         }
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
