package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.projectile.magic.WindSphereProjectile;
import io.github.manasmods.tensura.entity.template.TensuraRideableEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IElementalSpirit;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class FeatheredSerpentEntity
   extends TensuraRideableEntity
   implements GeoEntity,
   SmartBrainOwner<FeatheredSerpentEntity>,
   IFlying,
   IElementalSpirit,
   ITensuraMount {
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(FeatheredSerpentEntity.class, EntityDataSerializers.BOOLEAN);
   protected int flyingTick;
   protected boolean wasFlying;
   private int mountAbilityCooldown = 0;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public FeatheredSerpentEntity(EntityType<? extends FeatheredSerpentEntity> type, Level level) {
      super(type, level);
      this.initFlying(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.FLYING_SPEED, 0.6F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
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
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(compound.getBoolean("Flying"));
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
   public boolean canSleep() {
      return !this.isNoAi();
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof FeatheredSerpentEntity serpent) {
         return serpent.isTame() == this.isTame();
      } else {
         return entity instanceof SylphideEntity sylphide ? sylphide.isTame() == this.isTame() : false;
      }
   }

   public boolean canAttack(LivingEntity pTarget) {
      return this.isAlliedTo(pTarget) ? false : super.canAttack(pTarget);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (!this.isTame()) {
         if (pSource.getEntity() instanceof FeatheredSerpentEntity serpent && !serpent.isTame()) {
            return false;
         }

         if (pSource.getEntity() instanceof SylphideEntity sylphide && !sylphide.isTame()) {
            return false;
         }
      }

      return super.hurt(pSource, pAmount);
   }

   protected void actuallyHurt(DamageSource source, float damage) {
      damage *= this.getPhysicalAttackInput(source);
      super.actuallyHurt(source, damage);
   }

   @Override
   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   @Override
   public boolean canBeNamed(Player player) {
      return !this.isTamedByNonPlayer();
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
      if (!this.level().isClientSide()) {
         if (this.mountAbilityCooldown > 0 && this.mountAbilityCooldown-- == 10) {
            this.wingAttack(this.getEyePosition().add(this.getLookAngle().scale(15.0)));
         }
      }
   }

   public void wingAttack(Vec3 targetPos) {
      Level level = this.level();
      if (!level.isClientSide()) {
         Vec3 source = this.getEyePosition();
         Vec3 sourceToTarget = targetPos.subtract(source);
         Vec3 normalizes = sourceToTarget.normalize();
         level.playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);

         for (int particleIndex = 1; particleIndex < Mth.floor(sourceToTarget.length()) + 1; particleIndex++) {
            Vec3 particlePos = source.add(normalizes.scale(particleIndex));
            TensuraParticleHelper.addServerParticlesAroundPos(this.random, level, particlePos, ParticleTypes.GUST, 5.0);
            TensuraParticleHelper.addServerParticlesAroundPos(this.random, level, particlePos, TensuraParticleUtils.getGust(), 4.0);
            AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos)).inflate(4.0);
            List<Entity> list = level.getEntitiesOfClass(Entity.class, aabb, living -> !living.is(this));
            if (!list.isEmpty()) {
               float multiplier = this.hasWindManipulation() ? 1.0F : 0.5F;
               ManasSkillInstance instance = SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.WIND_MANIPULATION.get());

               for (Entity target : list) {
                  DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.WIND_ELEMENTAL, this)
                     .tensura$setAbilityInstance(instance)
                     .tensura$setAbilityMode(0)
                     .tensura$setMagiculeCost(100.0)
                     .tensura$setElement(Element.WIND)
                     .tensura$setMagicType(Magic.MagicType.SPIRITUAL);
                  if (target instanceof LivingEntity) {
                     target.hurt(damageSource, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier);
                  }

                  level.playSound(
                     null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
                  SkillHelper.pushBackFromPos(this.blockPosition(), target, this, instance, 3.0F, 0.5);
               }
            }
         }
      }
   }

   public void windSphereAttack() {
      WindSphereProjectile windSphere = new WindSphereProjectile(this.level(), this);
      windSphere.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.WIND_MANIPULATION.get()));
      windSphere.setMode(0);
      float radius = 2.0F;
      float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
      double extraX = radius * Mth.sin((float)(Math.PI + angle));
      double extraZ = radius * Mth.cos(angle);
      windSphere.moveTo(this.getX() + extraX, this.getEyeY() - 0.5, this.getZ() + extraZ, this.getYRot(), this.getXRot());
      windSphere.setHitRadius(4.0F);
      windSphere.setDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.75));
      windSphere.setNoGravity(true);
      windSphere.setKnockForce(3.0F);
      windSphere.setBurnTicks(-1);
      windSphere.shootFromRot(this.getLookAngle());
      this.level().addFreshEntity(windSphere);
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }

   public void tailSwing() {
      TensuraParticleHelper.addServerParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), 2.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), 3.0);
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      AABB aabb = this.getBoundingBox().inflate(5.0);
      List<LivingEntity> livingEntityList = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            aabb,
            entity -> !entity.isAlliedTo(this)
               && entity != this.getOwner()
               && entity != this
               && (!(entity instanceof FeatheredSerpentEntity) || entity == this.getTarget())
         );
      if (!livingEntityList.isEmpty()) {
         double damageMultiplier = this.hasWindManipulation() ? 1.5 : 1.0;
         int level = this.hasWindManipulation() ? 1 : 0;
         DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.WIND_ELEMENTAL, this)
            .tensura$setAbilityInstance(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.WIND_MANIPULATION.get()))
            .tensura$setAbilityMode(0)
            .tensura$setMagiculeCost(20.0)
            .tensura$setElement(Element.WIND)
            .tensura$setMagicType(Magic.MagicType.SPIRITUAL);

         for (LivingEntity target : livingEntityList) {
            if (target.hurt(damageSource, (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier))) {
               target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 100, level, true, false, true), this);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), 1.0);
            }

            SkillHelper.knockBack(this, target, 2.0F);
         }
      }
   }

   public boolean hasWindManipulation() {
      return SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)ExtraSkills.WIND_MANIPULATION.get()).isPresent();
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAbilityCooldown <= 0) {
         this.mountAbilityCooldown = 32;
         this.triggerAnim("miscController", "wing_attack");
      }
   }

   @Override
   public boolean isSaddleRequired() {
      return false;
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
      return pStack.is((Item)TensuraMaterialItems.WIND_ELEMENTAL_SHARD.get());
   }

   @Override
   public Element getElemental() {
      return Element.WIND;
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

      if (this.convertElementalCore(this, player, hand, (Item)TensuraMaterialItems.ELEMENT_CORE_WIND.get())) {
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      }

      InteractionResult golemInteraction = this.getGolemInteraction(player, hand, this);
      if (golemInteraction.consumesAction()) {
         return golemInteraction;
      }

      InteractionResult riding = this.getRidingInteraction(player, hand);
      if (riding != InteractionResult.PASS) {
         return riding;
      }

      this.cycleCommands(this, player);
      return InteractionResult.sidedSuccess(this.level().isClientSide());
   }

   @Override
   public void applyFoodHeal(ItemStack stack, Player player, InteractionHand hand) {
      this.heal(5.0F);
      this.ate();
   }

   public boolean isNoGravity() {
      return super.isNoGravity() ? true : this.getControllingPassenger() != null && !this.onGround();
   }

   @Override
   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
      if (controller.jumping) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.1, 0.0));
      } else if (TensuraKeybinds.DODGE.isDown()) {
         this.descending(this, controller);
      }
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.featheredSerpent, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canSpawnSpecialVariant(pReason)
         && pLevel.getBiome(this.blockPosition()).is(TensuraBiomeTags.ANCIENT_FOREST)
         && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.greaterSpiritChance, pLevel.getRandom())) {
         SylphideEntity spirit = new SylphideEntity((EntityType<? extends SylphideEntity>)MonsterEntityTypes.SYLPHIDE.get(), this.level());
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
      return (SoundEvent)TensuraSoundEvents.SPIRIT_WIND_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_WIND_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_WIND_DEATH.get();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<FeatheredSerpentEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<FeatheredSerpentEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<FeatheredSerpentEntity> getIdleTasks() {
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

   public BrainActivityGroup<FeatheredSerpentEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(22).minAttackRadius(5.0F).maxAttackRadius(15.0F).attackInterval(entity -> 100).performAttack((entity, target) -> {
                     entity.lookAt(Anchor.EYES, target.getEyePosition());
                     entity.wingAttack(target.getEyePosition());
                  }).whenStarting(entity -> entity.triggerAnim("miscController", "wing_attack")).startCondition(entity -> entity.getRandom().nextInt(10) == 1),
                  new CustomRangeAttack(25).minAttackRadius(5.0F).maxAttackRadius(32.0F).attackInterval(entity -> 100).performAttack((entity, target) -> {
                     entity.lookAt(Anchor.EYES, target.getEyePosition());
                     entity.windSphereAttack();
                  }).whenStarting(entity -> entity.triggerAnim("miscController", "projectile")).startCondition(entity -> entity.getRandom().nextInt(10) == 1),
                  new CustomRangeAttack(25)
                     .maxAttackRadius(7.0F)
                     .attackInterval(entity -> 40)
                     .performAttack((entity, target) -> entity.tailSwing())
                     .whenStarting(entity -> entity.triggerAnim("miscController", "tail_whip"))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<FeatheredSerpentEntity> state) {
      String name;
      if (this.isNoAi()) {
         name = "animation.feathered_serpent.idle";
      } else if (this.isSleeping()) {
         name = "animation.feathered_serpent.sleep";
      } else if (this.isInSittingPose()) {
         name = "animation.feathered_serpent.idle";
      } else if (state.isMoving()) {
         if (!this.onGround() || this.isAngry()) {
            name = "animation.feathered_serpent.fly";
         } else if (this.isSprinting()) {
            name = "animation.feathered_serpent.slither_fast";
         } else {
            name = "animation.feathered_serpent.slither";
         }
      } else if (this.onGround() && !this.isAngry()) {
         name = "animation.feathered_serpent.idle";
      } else if (this.isSprinting()) {
         name = "animation.feathered_serpent.idle_fly_fast";
      } else {
         name = "animation.feathered_serpent.idle_fly";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("wing_attack", RawAnimation.begin().then("animation.feathered_serpent.wing_attack", LoopType.PLAY_ONCE))
               .triggerableAnim("projectile", RawAnimation.begin().then("animation.feathered_serpent.projectile", LoopType.PLAY_ONCE))
               .triggerableAnim("tail_whip", RawAnimation.begin().then("animation.feathered_serpent.tail_whip", LoopType.PLAY_ONCE))
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
