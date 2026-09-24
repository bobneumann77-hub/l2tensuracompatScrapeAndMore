package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.LeapToTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomSwimAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.projectile.magic.PoisonBallProjectile;
import io.github.manasmods.tensura.entity.template.TensuraRideableEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IAmphibian;
import io.github.manasmods.tensura.entity.template.subclass.IElementalSpirit;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class AquaFrogEntity extends TensuraRideableEntity implements GeoEntity, SmartBrainOwner<AquaFrogEntity>, IAmphibian, IElementalSpirit, ITensuraMount {
   private int swimmingTick = 0;
   private boolean landNavigating = false;
   private int mountAbilityCooldown = 0;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public AquaFrogEntity(EntityType<? extends AquaFrogEntity> type, Level level) {
      super(type, level);
      this.initAmphibian(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.ATTACK_DAMAGE, 15.0)
         .add(Attributes.JUMP_STRENGTH, 2.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.5)
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
   public boolean canSleep() {
      return !this.isNoAi();
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof AquaFrogEntity cat) {
         return cat.isTame() == this.isTame();
      } else {
         return entity instanceof UndineEntity undine ? undine.isTame() == this.isTame() : false;
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
         if (pSource.getEntity() instanceof AquaFrogEntity cat && !cat.isTame()) {
            return false;
         }

         if (pSource.getEntity() instanceof UndineEntity undine && !undine.isTame()) {
            return false;
         }
      }

      boolean hurt = super.hurt(pSource, pAmount);
      if (hurt && pSource.getDirectEntity() instanceof LivingEntity attacker && TensuraDamageHelper.isPhysicalAttack(pSource) && !attacker.isAlliedTo(this)) {
         MobEffectInstance poison = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 200, 1, true, false, true);
         TensuraMobEffect.addEffect(attacker, poison, this.getUUID(), (ManasSkill)CommonSkills.POISON.get(), 0);
      }

      return hurt;
   }

   protected void actuallyHurt(DamageSource source, float damage) {
      damage *= this.getPhysicalAttackInput(source);
      super.actuallyHurt(source, damage);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
      return levelReader.getFluidState(blockPos).is(FluidTags.WATER)
         ? 10.0F + levelReader.getPathfindingCostFromLightLevels(blockPos)
         : super.getWalkTargetValue(blockPos, levelReader);
   }

   @Override
   public boolean shouldFindWater(Mob mob) {
      return this.shouldStayInWater(mob) && this.getSwimmingTick() <= -2000;
   }

   public boolean isKermit() {
      return !this.hasCustomName() ? false : "Kermit".equalsIgnoreCase(this.getName().getString());
   }

   @Override
   public void tick() {
      super.tick();
      this.handleSwimming(this);
      if (!this.level().isClientSide()) {
         if (this.mountAbilityCooldown > 0 && this.mountAbilityCooldown-- == 15) {
            this.poisonSpit();
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void poisonSpit() {
      PoisonBallProjectile ball = new PoisonBallProjectile(this.level(), this);
      ball.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.WATER_MANIPULATION.get()));
      ball.setMode(0);
      float radius = 1.0F;
      float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
      double extraX = radius * Mth.sin((float)(Math.PI + angle));
      double extraZ = radius * Mth.cos(angle);
      ball.setPos(this.getX() + extraX, this.getEyeY(), this.getZ() + extraZ);
      ball.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
      ball.setMobEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 200, 1, true, false, true));
      ball.setEffectRange(2.0F);
      Vec3 vector3f = this.getFirstPassenger() != null ? this.getFirstPassenger().getViewVector(2.0F) : this.getViewVector(2.0F);
      ball.shoot(vector3f.x(), vector3f.y(), vector3f.z(), 2.0F, 0.0F);
      this.level().addFreshEntity(ball);
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAbilityCooldown <= 0) {
         this.mountAbilityCooldown = 20;
         this.triggerAnim("miscController", "spit");
      }
   }

   @Override
   public boolean doHurtTarget(Entity pEntity) {
      if (super.doHurtTarget(pEntity) && pEntity instanceof LivingEntity living && living.getLastHurtByMobTimestamp() == living.tickCount) {
         MobEffectInstance poison = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 200, 2, true, false, true);
         TensuraMobEffect.addEffect(living, poison, this.getUUID(), (ManasSkill)CommonSkills.POISON.get(), 0);
         return true;
      } else {
         return false;
      }
   }

   public boolean killedEntity(ServerLevel serverLevel, LivingEntity livingEntity) {
      if (this.isKermit() && (this.getTarget() == null || !this.getTarget().isAlive())) {
         this.triggerAnim("miscController", "laugh");
      }

      return true;
   }

   @Override
   public boolean canBeNamed(Player player) {
      return !this.isTamedByNonPlayer();
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
      return pStack.is((Item)TensuraMaterialItems.WATER_ELEMENTAL_SHARD.get());
   }

   @Override
   public Element getElemental() {
      return Element.WATER;
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

      if (this.convertElementalCore(this, player, hand, (Item)TensuraMaterialItems.ELEMENT_CORE_WATER.get())) {
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

   @Override
   public void handleStartJump(int pJumpPower) {
      if (this.canExecuteRidersJump()) {
         this.triggerAnim("jumpController", "jump");
      } else {
         this.playerJumpPendingScale = 0.0F;
      }
   }

   @Override
   protected float getRiddenSpeed(Player player) {
      float speed = super.getRiddenSpeed(player);
      return this.isInWaterOrBubble() ? speed * 1.5F : (this.onGround() ? speed * 0.5F : speed);
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, -0.3F, f * -0.25).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.aquaFrog, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canSpawnSpecialVariant(pReason)
         && pLevel.getBiome(this.blockPosition()).is(TensuraBiomeTags.ANCIENT_FOREST)
         && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.greaterSpiritChance, pLevel.getRandom())) {
         UndineEntity spirit = new UndineEntity((EntityType<? extends UndineEntity>)MonsterEntityTypes.UNDINE.get(), this.level());
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
      return SoundEvents.FROG_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.FROG_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.FROG_DEATH;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<AquaFrogEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<AquaFrogEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<AquaFrogEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false),
                  new SubordinateFollowOwner(),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{
                     new SetRandomSwimAndWalkTarget().speedModifier((entity, pos) -> entity.isInWaterOrBubble() ? 1.5F : 1.0F).cooldownFor(entity -> 0),
                     new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
                  }
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<AquaFrogEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new LeapToTarget(0)
                     .leapRange((entity, target) -> 32.0F)
                     .moveSpeedContribution((frog, entity) -> 1.0F)
                     .verticalJumpStrength((entity, target) -> entity.getJumpPower(0.25F))
                     .attackInterval(entity -> 20)
                     .whenStarting(entity -> entity.triggerAnim("jumpController", "hop")),
                  new CustomRangeAttack(5).minAttackRadius(5.0F).maxAttackRadius(32.0F).attackInterval(entity -> 100).performAttack((entity, target) -> {
                     entity.lookAt(Anchor.EYES, target.getEyePosition());
                     entity.poisonSpit();
                  }).whenStarting(entity -> entity.triggerAnim("miscController", "spit")).startCondition(cat -> cat.getRandom().nextInt(10) == 1),
                  new CustomRangeAttack(5)
                     .maxAttackRadius(7.0F)
                     .attackInterval(entity -> 20)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "tongue"))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<AquaFrogEntity> state) {
      String name;
      if (this.isNoAi()) {
         name = "animation.aqua_frog.idle";
      } else if (this.isSleeping()) {
         name = "animation.aqua_frog.sleep";
      } else if (this.isInSittingPose()) {
         if (this.isKermit()) {
            name = "animation.aqua_frog.kermit_sit";
         } else {
            name = "animation.aqua_frog.sit";
         }
      } else if (state.isMoving()) {
         if (this.isInWaterOrBubble()) {
            name = "animation.aqua_frog.swim";
         } else if (this.onGround()) {
            name = "animation.aqua_frog.walk";
         } else if (this.getDeltaMovement().y() < 0.0) {
            name = "animation.aqua_frog.fall";
         } else {
            name = "animation.aqua_frog.idle";
         }
      } else if (this.isInWaterOrBubble()) {
         name = "animation.aqua_frog.idle_swim";
      } else if (!this.onGround() && !(this.getDeltaMovement().y() >= 0.0)) {
         name = "animation.aqua_frog.fall";
      } else {
         name = "animation.aqua_frog.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("eat", RawAnimation.begin().then("animation.aqua_frog.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("tongue", RawAnimation.begin().then("animation.aqua_frog.tongue", LoopType.PLAY_ONCE))
               .triggerableAnim("spit", RawAnimation.begin().then("animation.aqua_frog.spit", LoopType.PLAY_ONCE))
               .triggerableAnim("laugh", RawAnimation.begin().then("animation.aqua_frog.kermit_laugh", LoopType.PLAY_ONCE)),
            new AnimationController(this, "jumpController", 5, event -> PlayState.STOP)
               .triggerableAnim("hop", RawAnimation.begin().then("animation.aqua_frog.hop", LoopType.PLAY_ONCE))
               .triggerableAnim("jump", RawAnimation.begin().then("animation.aqua_frog.jump", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   @Override
   public int getSwimmingTick() {
      return this.swimmingTick;
   }

   @Generated
   @Override
   public void setSwimmingTick(int swimmingTick) {
      this.swimmingTick = swimmingTick;
   }

   @Generated
   @Override
   public boolean isLandNavigating() {
      return this.landNavigating;
   }

   @Generated
   @Override
   public void setLandNavigating(boolean landNavigating) {
      this.landNavigating = landNavigating;
   }
}
