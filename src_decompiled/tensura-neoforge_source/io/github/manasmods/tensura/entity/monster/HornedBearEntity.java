package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HornedBearEntity extends TensuraMountEntity implements GeoEntity, SmartBrainOwner<HornedBearEntity>, ITensuraMount {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int mountAbilityCooldown = 0;

   public HornedBearEntity(EntityType<? extends HornedBearEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.MAX_HEALTH, 100.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
         .add(Attributes.JUMP_STRENGTH, 1.5)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 3.0)
         .add(Attributes.STEP_HEIGHT, 1.5)
         .add(Attributes.SAFE_FALL_DISTANCE, 4.0);
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   @Override
   public int getChestSlots() {
      return 40;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.type().effects().equals(DamageEffects.POKING) || super.isInvulnerableTo(source);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.hornedBear, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.mountAbilityCooldown > 0) {
            this.mountAbilityCooldown--;
         }
      }
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      HornedBearEntity bear = (HornedBearEntity)((EntityType)MonsterEntityTypes.HORNED_BEAR.get()).create(pLevel);
      if (bear == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         bear.setOwnerUUID(uuid);
         bear.setTame(true, true);
      }

      return bear;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.BEAR_FOOD);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return this.isFood(pStack);
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   @Override
   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
      Vec3 riddenInput = this.getRiddenInput(controller, vec3);
      if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > this.getFluidJumpThreshold() && riddenInput.z() > 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.03, 0.0));
      }
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAbilityCooldown <= 0) {
         this.mountAbilityCooldown = 10;
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(rider, 6.0, false);
         if (target != null) {
            this.triggerAnim("strikeController", "strike");
            target.hurt(this.damageSources().mobAttack(this), (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5));
            this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP);
         }
      }
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.BEAR_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.BEAR_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.BEAR_DEATH.get();
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

   public List<ExtendedSensor<HornedBearEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<HornedBearEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<HornedBearEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this),
                  new SubordinateFollowOwner().speedMod(1.5F),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<HornedBearEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 1.5F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(10)
                     .maxAttackRadius(4.0F)
                     .attackInterval(entity -> 40)
                     .performAttack(
                        (entity, target) -> target.hurt(this.damageSources().mobAttack(this), (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5))
                     )
                     .whenStarting(
                        entity -> {
                           entity.triggerAnim("strikeController", "strike");
                           entity.playSound(
                              (SoundEvent)TensuraSoundEvents.BEAR_ATTACK.get(),
                              entity.getSoundVolume(),
                              (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + 1.0F
                           );
                        }
                     )
                     .startCondition(tiger -> tiger.getRandom().nextInt(3) == 1),
                  new AnimatableMeleeAttack(1).attackInterval(entity -> 1).whenStarting(entity -> entity.triggerAnim("miscController", "bite"))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<HornedBearEntity> state) {
      String name;
      if (this.isInSittingPose()) {
         name = "animation.horned_bear.sit";
      } else if (state.isMoving()) {
         if (this.isInLiquid() || !this.isAngry() && !this.isSprinting()) {
            if (this.getHealth() < this.getMaxHealth() / 4.0F) {
               name = "animation.horned_bear.walk_hurt";
            } else {
               name = "animation.horned_bear.walk";
            }
         } else {
            name = "animation.horned_bear.run";
         }
      } else {
         name = "animation.horned_bear.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 2, event -> PlayState.STOP)
               .triggerableAnim("eat", RawAnimation.begin().then("animation.horned_bear.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("bite", RawAnimation.begin().then("animation.horned_bear.bite", LoopType.PLAY_ONCE)),
            new AnimationController(this, "strikeController", 0, event -> PlayState.STOP)
               .triggerableAnim("strike", RawAnimation.begin().then("animation.horned_bear.standing_strike", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
