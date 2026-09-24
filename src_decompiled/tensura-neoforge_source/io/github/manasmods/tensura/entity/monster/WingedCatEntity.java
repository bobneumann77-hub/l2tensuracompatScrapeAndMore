package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.skill.api.ManasSkill;
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
import io.github.manasmods.tensura.entity.ai.behaviour.movement.TeleportToEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.projectile.magic.SpaceCutProjectile;
import io.github.manasmods.tensura.entity.template.TensuraRideableEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IElementalSpirit;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
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

public class WingedCatEntity extends TensuraRideableEntity implements GeoEntity, SmartBrainOwner<WingedCatEntity>, IFlying, IElementalSpirit, ITensuraMount {
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(WingedCatEntity.class, EntityDataSerializers.BOOLEAN);
   protected int flyingTick;
   protected boolean wasFlying;
   private int mountAbilityCooldown = 0;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public WingedCatEntity(EntityType<? extends WingedCatEntity> type, Level level) {
      super(type, level);
      this.initFlying(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.FLYING_SPEED, 0.6F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
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
      if (flying) {
         this.setSleeping(false);
      }
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
      return !this.isNoAi() && !this.isFlying();
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof WingedCatEntity cat) {
         return cat.isTame() == this.isTame();
      } else {
         return entity instanceof AkashEntity akash ? akash.isTame() == this.isTame() : false;
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
         if (pSource.getEntity() instanceof WingedCatEntity cat && !cat.isTame()) {
            return false;
         }

         if (pSource.getEntity() instanceof AkashEntity akash && !akash.isTame()) {
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
   public boolean canBeNamed(Player player) {
      return !this.isTamedByNonPlayer();
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
      if (!this.level().isClientSide()) {
         if (this.mountAbilityCooldown > 0 && this.mountAbilityCooldown-- == 15) {
            this.spaceCutAttack(0.1, false, null);
            this.spaceCutAttack(0.6, false, null);
            this.spaceCutAttack(0.6, true, null);
         }
      }
   }

   public void spaceCutAttack(double multishot, boolean negativeAngle, @Nullable LivingEntity target) {
      SpaceCutProjectile spaceCut = new SpaceCutProjectile(this.level(), this);
      spaceCut.setVisible(true);
      spaceCut.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.SPATIAL_MANIPULATION.get()));
      float radius = 2.0F;
      float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
      double extraX = radius * Mth.sin((float)(Math.PI + angle));
      double extraZ = radius * Mth.cos(angle);
      spaceCut.setPos(this.getX() + extraX, this.getEyeY() - 0.5, this.getZ() + extraZ);
      if (multishot > 0.0) {
         int rot = negativeAngle ? -145 : 145;
         float yaw = this.getYRot() * (float) (Math.PI / 180.0);
         float f3 = Mth.sin((float)(yaw + Math.toRadians(rot)));
         float f = -Mth.cos((float)(yaw + Math.toRadians(rot)));
         spaceCut.setPos(spaceCut.getX() + f3 * multishot, spaceCut.getY(), spaceCut.getZ() + f * multishot);
      }

      spaceCut.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
      spaceCut.setNoGravity(true);
      Vec3 vec3;
      if (this.getFirstPassenger() != null) {
         vec3 = this.getFirstPassenger().getViewVector(2.0F);
      } else if (target != null) {
         vec3 = new Vec3(target.getX() - this.getX(), target.getEyeY() - this.getY(), target.getZ() - this.getZ()).scale(0.1F);
      } else {
         vec3 = this.getViewVector(2.0F);
      }

      spaceCut.shoot(vec3.x(), vec3.y(), vec3.z(), 1.0F, 0.0F);
      this.level().addFreshEntity(spaceCut);
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), TensuraSoundEvents.CAST_SPACE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }

   public void doubleStrike() {
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.REVERSE_PORTAL, 2.0);
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, TensuraSkill.ABILITY_SOUND, 1.5F, 1.0F);
      AABB aabb = this.getBoundingBox().inflate(3.0);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            aabb,
            entity -> !entity.isAlliedTo(this)
               && entity != this.getOwner()
               && entity != this
               && (!(entity instanceof WingedCatEntity) || entity == this.getTarget())
         );
      if (!list.isEmpty()) {
         float damageMultiplier = this.hasSpaceManipulation() ? 1.0F : 0.5F;
         DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.SPACE_ELEMENTAL, this)
            .tensura$setAbilityInstance(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.SPATIAL_MANIPULATION.get()))
            .tensura$setMagiculeCost(20.0)
            .tensura$setElement(Element.SPACE)
            .tensura$setMagicType(Magic.MagicType.SPIRITUAL);

         for (LivingEntity target : list) {
            target.hurt(damageSource, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier);
            SkillHelper.knockBack(this, target, 0.5F);
         }
      }
   }

   public boolean hasSpaceManipulation() {
      return SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)ExtraSkills.SPATIAL_MANIPULATION.get()).isPresent();
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAbilityCooldown <= 0) {
         this.mountAbilityCooldown = 30;
         this.triggerAnim("miscController", "strike");
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
      return pStack.is((Item)TensuraMaterialItems.SPACE_ELEMENTAL_SHARD.get());
   }

   @Override
   public Element getElemental() {
      return Element.SPACE;
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

      if (this.convertElementalCore(this, player, hand, (Item)TensuraMaterialItems.ELEMENT_CORE_SPACE.get())) {
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

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
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

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.wingedCat, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   public static boolean checkWingedCatSpawnRules(
      EntityType<? extends WingedCatEntity> pType, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      return pSpawnType == MobSpawnType.SPAWNER ? true : pPos.getY() >= 70;
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canSpawnSpecialVariant(pReason)
         && pLevel.getBiome(this.blockPosition()).is(TensuraBiomeTags.ANCIENT_FOREST)
         && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.greaterSpiritChance, pLevel.getRandom())) {
         AkashEntity spirit = new AkashEntity((EntityType<? extends AkashEntity>)MonsterEntityTypes.AKASH.get(), this.level());
         spirit.setPos(this.getX(), this.getY(), this.getZ());
         spirit.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
         this.level().addFreshEntity(spirit);
         this.setRemoved(RemovalReason.DISCARDED);
         return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
      } else {
         return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
      }
   }

   protected SoundEvent getAmbientSound() {
      return this.isAngry() ? (SoundEvent)TensuraSoundEvents.CAT_AGGRO.get() : (SoundEvent)TensuraSoundEvents.CAT_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return (SoundEvent)TensuraSoundEvents.CAT_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.CAT_DEATH.get();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<WingedCatEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<WingedCatEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<WingedCatEntity> getIdleTasks() {
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

   public BrainActivityGroup<WingedCatEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new TeleportToEntity()
               .following(Mob::getTarget)
               .canTeleportOffGroundWhen(entity -> true)
               .teleportRadius((entity, target) -> 3)
               .teleportToTargetAfter((entity, target) -> {
                  if (entity.tickCount % 200 == 0) {
                     return 3.0;
                  } else {
                     return entity.tickCount % 20 == 0 ? 15.0 : 32.0;
                  }
               })
               .onSuccessTeleport((entity, target) -> {
                  entity.setFlying(true);
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.REVERSE_PORTAL, 1.0);
                  entity.level()
                     .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               }),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(15).minAttackRadius(5.0F).maxAttackRadius(40.0F).attackInterval(entity -> 100).performAttack((entity, target) -> {
                     entity.lookAt(Anchor.EYES, target.getEyePosition());
                     entity.spaceCutAttack(0.1, false, target);
                     entity.spaceCutAttack(0.6, false, target);
                     entity.spaceCutAttack(0.6, true, target);
                  }).whenStarting(entity -> entity.triggerAnim("miscController", "strike")).startCondition(cat -> cat.getRandom().nextInt(10) == 1),
                  new CustomRangeAttack(12)
                     .maxAttackRadius(5.0F)
                     .attackInterval(entity -> 20)
                     .performAttack((entity, target) -> entity.doubleStrike())
                     .whenStarting(entity -> entity.triggerAnim("miscController", "double_strike"))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<WingedCatEntity> state) {
      String name;
      if (this.isNoAi()) {
         name = "animation.winged_cat.idle";
      } else if (this.isSleeping()) {
         name = "animation.winged_cat.loaf";
      } else if (this.isInSittingPose()) {
         name = "animation.winged_cat.sit";
      } else if (state.isMoving()) {
         if (this.onGround()) {
            name = "animation.winged_cat.walk";
         } else {
            name = "animation.winged_cat.fly";
         }
      } else if (this.onGround()) {
         name = "animation.winged_cat.idle";
      } else {
         name = "animation.winged_cat.idle_fly";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("eat", RawAnimation.begin().then("animation.winged_cat.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("strike", RawAnimation.begin().then("animation.winged_cat.single_strike", LoopType.PLAY_ONCE))
               .triggerableAnim("double_strike", RawAnimation.begin().then("animation.winged_cat.double_strike", LoopType.PLAY_ONCE))
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
