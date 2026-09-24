package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.variant.BarghestFlameVariant;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.pathfinder.PathType;
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

public class BarghestEntity extends TensuraTamableEntity implements GeoEntity, SmartBrainOwner<BarghestEntity> {
   private static final EntityDataAccessor<Integer> FLAME_TYPE = SynchedEntityData.defineId(BarghestEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> NETHER = SynchedEntityData.defineId(BarghestEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public BarghestEntity(EntityType<? extends BarghestEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 6.0)
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.2F)
         .add(Attributes.STEP_HEIGHT, 1.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLAME_TYPE, 0);
      builder.define(NETHER, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("FlameType", this.getFlameTypeId());
      compound.putBoolean("Nether", this.isNether());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(FLAME_TYPE, compound.getInt("FlameType"));
      this.setNether(compound.getBoolean("Nether"));
   }

   public BarghestFlameVariant getFlameType() {
      return BarghestFlameVariant.byId(this.getFlameTypeId() & 0xFF);
   }

   private int getFlameTypeId() {
      return (Integer)this.entityData.get(FLAME_TYPE);
   }

   public void setFlameType(BarghestFlameVariant variant) {
      this.entityData.set(FLAME_TYPE, variant.getId() & 0xFF);
   }

   public boolean isNether() {
      return (Boolean)this.entityData.get(NETHER);
   }

   public void setNether(boolean nether) {
      this.entityData.set(NETHER, nether);
      AttributeInstance lavaSpeed = this.getAttribute(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER);
      if (lavaSpeed != null) {
         lavaSpeed.setBaseValue(lavaSpeed.getBaseValue() + 4.0);
      }

      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      BarghestEntity barghest = (BarghestEntity)((EntityType)MonsterEntityTypes.BARGHEST.get()).create(pLevel);
      if (barghest == null) {
         return null;
      }

      if (pOtherParent instanceof BarghestEntity other) {
         barghest.setNether(pLevel.getRandom().nextBoolean() ? other.isNether() : this.isNether());
         barghest.setFlameType(pLevel.getRandom().nextBoolean() ? other.getFlameType() : this.getFlameType());
      } else {
         barghest.setNether(this.isNether());
         barghest.setFlameType(this.getFlameType());
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         barghest.setOwnerUUID(uuid);
         barghest.setTame(true, true);
      }

      return barghest;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return this.isNether() && source.is(DamageTypeTags.IS_FIRE) && source.tensura$getAbilityInstance() == null
         ? true
         : source.is(DamageTypes.HOT_FLOOR) || super.isInvulnerableTo(source);
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      if (pFallDistance < 5.0F) {
         return false;
      }

      int i = this.calculateFallDamage(pFallDistance - 5.0F, pMultiplier);
      if (i <= 0) {
         return false;
      }

      this.hurt(pSource, i);
      this.playBlockFallSound();
      return true;
   }

   @Override
   public boolean doHurtTarget(Entity pEntity) {
      if (super.doHurtTarget(pEntity)) {
         if (pEntity instanceof LivingEntity living && living.getLastHurtByMobTimestamp() == living.tickCount && this.random.nextInt(5) == 1) {
            living.setRemainingFireTicks(60);
         }

         return true;
      } else {
         return false;
      }
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      if (this.canRandomizeSpawnData(mobSpawnType)) {
         if (serverLevelAccessor.getLevel().dimension().equals(Level.NETHER)) {
            this.setNether(true);
            Holder<Biome> biome = serverLevelAccessor.getBiome(this.blockPosition());
            if (!biome.is(BiomeTags.HAS_NETHER_FOSSIL) && !biome.is(Biomes.WARPED_FOREST)) {
               this.setFlameType(BarghestFlameVariant.ORANGE);
            } else {
               this.setFlameType(BarghestFlameVariant.TEAL);
            }
         } else if (serverLevelAccessor.getLevel().getBiome(this.blockPosition()).is(TensuraBiomeTags.IS_MIASMIC)) {
            this.setFlameType(BarghestFlameVariant.GREEN);
         }
      }

      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.barghest, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @Override
   public InteractionResult handleEating(Player player, InteractionHand hand, ItemStack stack) {
      if (stack.is(TensuraItemTags.RESET_BARGHEST_FLAME) && this.getFlameTypeId() != 0) {
         this.setFlameType(BarghestFlameVariant.BLUE);
         this.usePlayerItem(player, hand, stack);
         this.level().playSound(null, this, SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      }

      if (stack.is(TensuraItemTags.BARGHEST_FLAME_ORANGE)) {
         this.setFlameType(BarghestFlameVariant.ORANGE);
         if (SkillUtils.hasSkill(this, (ManasSkill)ExtraSkills.BLACK_FLAME.get())) {
            this.setFlameType(BarghestFlameVariant.BLACK);
         }

         this.usePlayerItem(player, hand, stack);
         this.level().playSound(null, this, SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else if (stack.is(TensuraItemTags.BARGHEST_FLAME_TEAL)) {
         this.setFlameType(BarghestFlameVariant.TEAL);
         this.usePlayerItem(player, hand, stack);
         this.level().playSound(null, this, SoundEvents.SOUL_SAND_PLACE, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else if (stack.is(TensuraItemTags.BARGHEST_FLAME_YELLOW)) {
         this.setFlameType(BarghestFlameVariant.YELLOW);
         this.usePlayerItem(player, hand, stack);
         this.level().playSound(null, this, (SoundEvent)SoundEvents.ARMOR_EQUIP_IRON.value(), SoundSource.NEUTRAL, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else if (stack.is(TensuraItemTags.BARGHEST_FLAME_RED)) {
         this.setFlameType(BarghestFlameVariant.RED);
         this.usePlayerItem(player, hand, stack);
         this.level().playSound(null, this, SoundEvents.CALCITE_PLACE, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else if (stack.is(TensuraItemTags.BARGHEST_FLAME_GREEN)) {
         this.setFlameType(BarghestFlameVariant.GREEN);
         this.usePlayerItem(player, hand, stack);
         this.level().playSound(null, this, SoundEvents.COPPER_PLACE, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else if (stack.is(TensuraItemTags.BARGHEST_FLAME_PURPLE)) {
         this.setFlameType(BarghestFlameVariant.PURPLE);
         this.usePlayerItem(player, hand, stack);
         this.level().playSound(null, this, SoundEvents.HOE_TILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else if (stack.is(TensuraItemTags.BARGHEST_FLAME_WHITE)) {
         this.setFlameType(BarghestFlameVariant.WHITE);
         this.usePlayerItem(player, hand, stack);
         this.level().playSound(null, this, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return super.handleEating(player, hand, stack);
      }
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   protected void usePlayerItem(Player player, InteractionHand interactionHand, ItemStack itemStack) {
      if (itemStack.getItem() instanceof BucketItem) {
         player.setItemInHand(interactionHand, BucketItem.getEmptySuccessItem(itemStack, player));
      } else {
         itemStack.consume(1, player);
      }

      player.swing(interactionHand, true);
   }

   protected SoundEvent getAmbientSound() {
      return this.isAngry() ? (SoundEvent)TensuraSoundEvents.BARGHEST_AGGRO.get() : (SoundEvent)TensuraSoundEvents.BARGHEST_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.BARGHEST_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.BARGHEST_DEATH.get();
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

   public List<ExtendedSensor<BarghestEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<BarghestEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<BarghestEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
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

   public BrainActivityGroup<BarghestEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new AnimatableMeleeAttack(2).attackInterval(entity -> 5).whenStarting(entity -> {
               LivingEntity target = entity.getTarget();
               if (target != null && target.getEyeHeight() <= entity.getEyeHeight() * 0.5F) {
                  entity.triggerAnim("miscController", "eat");
               } else {
                  entity.triggerAnim("miscController", "bite");
               }
            })
         }
      );
   }

   protected boolean isInjured() {
      return this.getHealth() < this.getMaxHealth() / 4.0;
   }

   protected PlayState loopController(AnimationState<BarghestEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.barghest.sleep";
      } else if (this.isInSittingPose()) {
         name = this.isInjured() ? "animation.barghest.sit_injured" : "animation.barghest.sit";
      } else if (this.isInLiquid()) {
         name = "animation.barghest.swim";
      } else if (state.isMoving()) {
         if (this.isAngry()) {
            name = "animation.barghest.run";
         } else {
            name = this.isInjured() ? "animation.barghest.walk_injured" : "animation.barghest.walk";
         }
      } else {
         name = this.isInjured() ? "animation.barghest.idle_injured" : "animation.barghest.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> {
                  this.swinging = false;
                  return PlayState.STOP;
               })
               .triggerableAnim("eat", RawAnimation.begin().then("animation.barghest.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("bite", RawAnimation.begin().then("animation.barghest.bite", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
