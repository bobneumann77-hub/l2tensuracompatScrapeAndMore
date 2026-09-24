package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.variant.CattledeerVariant;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.util.SubordinateHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.BreakBlock;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.BreedWithPartner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Panic;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowParent;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowTemptation;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToBlock;
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

public class CattledeerEntity extends TensuraTamableEntity implements GeoEntity, SmartBrainOwner<CattledeerEntity>, VariantHolder<CattledeerVariant> {
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(CattledeerEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public CattledeerEntity(EntityType<? extends CattledeerEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.01F);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(VARIANT, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(VARIANT, compound.getInt("Variant"));
   }

   @NotNull
   public CattledeerVariant getVariant() {
      return CattledeerVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(CattledeerVariant variant) {
      this.entityData.set(VARIANT, variant.getId() & 0xFF);
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   @Override
   public boolean canBeLeashed() {
      return true;
   }

   @Override
   protected boolean shouldWakeUp() {
      return super.shouldWakeUp() || (Boolean)BrainUtils.memoryOrDefault(this, MemoryModuleType.IS_PANICKING, () -> false);
   }

   @Override
   protected boolean canGoToSleep() {
      return super.canGoToSleep() && !(Boolean)BrainUtils.memoryOrDefault(this, MemoryModuleType.IS_PANICKING, () -> false);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return this.isFood(pStack);
   }

   @NotNull
   @Override
   public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      if (itemstack.is(Items.BUCKET)) {
         pPlayer.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
         pPlayer.setItemInHand(pHand, ItemUtils.createFilledResult(itemstack, pPlayer, Items.MILK_BUCKET.getDefaultInstance()));
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return super.mobInteract(pPlayer, pHand);
      }
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      CattledeerEntity entity = (CattledeerEntity)((EntityType)MonsterEntityTypes.CATTLEDEER.get()).create(pLevel);
      if (entity == null) {
         return null;
      }

      if (pOtherParent instanceof CattledeerEntity cattledeer) {
         entity.setVariant(pLevel.getRandom().nextBoolean() ? this.getVariant() : cattledeer.getVariant());
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         entity.setOwnerUUID(uuid);
         entity.setTame(true, true);
      }

      return entity;
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.CATTLE_FOOD);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      if (this.canRandomizeSpawnData(mobSpawnType)) {
         Holder<Biome> biome = serverLevelAccessor.getBiome(this.blockPosition());
         if (biome.is(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
            this.setVariant(CattledeerVariant.COLD);
         } else if (biome.is(BiomeTags.SPAWNS_WARM_VARIANT_FROGS)) {
            this.setVariant(CattledeerVariant.HOT);
         } else if (serverLevelAccessor.getRandom().nextFloat() <= 0.25) {
            this.setVariant(CattledeerVariant.SPOTTED);
         } else {
            this.setVariant(CattledeerVariant.DEFAULT);
         }
      }

      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.cattledeer, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.COW_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.COW_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COW_DEATH;
   }

   protected void playStepSound(BlockPos pPos, BlockState pBlock) {
      this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   private boolean shouldPanic() {
      return this.getLastHurtByMob() != null || this.isFreezing() || this.isOnFire() || BrainUtils.getMemory(this, MemoryModuleType.HURT_BY) != null;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<CattledeerEntity>> getSensors() {
      return ObjectArrayList.of(
         new ExtendedSensor[]{
            new NearbyLivingEntitySensor(),
            new HurtBySensor(),
            new ItemTemptingSensor().temptedWith((entity, stack) -> stack.is(Items.WHEAT)),
            new NearbyBlocksSensor().setRadius(2.0)
         }
      );
   }

   public BrainActivityGroup<CattledeerEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<CattledeerEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new Panic()
                     .panicIf(
                        (entity, damageSource) -> entity.getOwner() != null && damageSource.getEntity() == entity.getOwner()
                           ? false
                           : entity.isFreezing() || entity.isOnFire() || damageSource.getEntity() instanceof LivingEntity
                     )
                     .speedMod(entity -> 2.0F),
                  new BreedWithPartner(),
                  new FollowTemptation().speedMod((entity, player) -> 1.2F).startCondition(entity -> !entity.isOrderedToSit()),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  new FollowParent()
                     .parentPredicate(
                        (entity, parent) -> entity.getOwner() != null
                           && Objects.equals(entity.getOwner().getUUID(), SubordinateHelper.getSubordinateOwnerUUID(parent))
                     )
                     .speedMod((entity, player) -> 1.2F),
                  new BreakBlock()
                     .timeToBreak((entity, pos, state) -> 25)
                     .forBlocks((entity, pos, state) -> state.is(Blocks.SHORT_GRASS) && pos.distToCenterSqr(entity.position()) <= 1.0)
                     .whenStarting(entity -> {
                        entity.triggerAnim("miscController", "eat");
                        entity.getNavigation().stop();
                     })
                     .whenStopping(entity -> {
                        if (entity.getHealth() < entity.getMaxHealth()) {
                           entity.heal(3.0F);
                        } else if (entity.isBaby()) {
                           entity.ageUp(getSpeedUpSecondsWhenFeeding(-entity.getAge()), true);
                        }

                        entity.playSound(SoundEvents.GRASS_BREAK);
                     })
                     .startCondition(
                        entity -> !entity.isOrderedToSit()
                           && !entity.isSleeping()
                           && !entity.shouldPanic()
                           && (entity.getHealth() < entity.getMaxHealth() || entity.isBaby())
                     )
                     .cooldownFor(entity -> 200),
                  new SubordinateFollowOwner(),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(
               new ExtendedBehaviour[]{
                  new SetWalkTargetToBlock()
                     .predicate((entity, pair) -> ((BlockState)pair.getSecond()).is(Blocks.SHORT_GRASS))
                     .closeEnoughWhen((entity, pos) -> 1)
                     .startCondition(entity -> !entity.isOrderedToSit() && !entity.isSleeping() && !entity.shouldPanic())
                     .cooldownFor(entity -> 200),
                  new SetRandomWalkTarget(),
                  new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60)),
                  new Idle()
                     .runFor(entity -> 25)
                     .cooldownFor(entity -> 200)
                     .startCondition(entity -> !entity.isOrderedToSit() && !entity.isSleeping() && !entity.shouldPanic())
                     .whenStarting(entity -> {
                        if (entity.getRandom().nextBoolean()) {
                           this.triggerAnim("loopController", "look_around");
                        }
                     })
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<CattledeerEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.cattledeer.sleep";
      } else if (this.isInSittingPose()) {
         name = "animation.cattledeer.sit";
      } else if (state.isMoving()) {
         if (this.shouldPanic()) {
            name = "animation.cattledeer.run";
         } else {
            name = "animation.cattledeer.walk";
         }
      } else {
         name = "animation.cattledeer.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController)
               .triggerableAnim("look_around", RawAnimation.begin().then("animation.cattledeer.look_around", LoopType.PLAY_ONCE)),
            new AnimationController(this, "miscController", 3, event -> {
               this.swinging = false;
               return PlayState.STOP;
            }).triggerableAnim("eat", RawAnimation.begin().then("animation.cattledeer.eat", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
