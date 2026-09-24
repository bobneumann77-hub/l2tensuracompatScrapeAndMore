package io.github.manasmods.tensura.entity.template;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.effect.debuff.SleepEffect;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.MoveOrFlyToWalkTarget;
import io.github.manasmods.tensura.entity.ai.navigator.SwimmableGroundNavigation;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.ILivingPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TensuraTamableEntity extends TamableAnimal implements NeutralMob {
   private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(
      TensuraTamableEntity.class, EntityDataSerializers.INT
   );
   private static final EntityDataAccessor<Integer> CUSTOM_OWNER_COMMAND = SynchedEntityData.defineId(TensuraTamableEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> CUSTOM_BEHAVIOUR = SynchedEntityData.defineId(TensuraTamableEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(TensuraTamableEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<BlockPos> WANDER_POS = SynchedEntityData.defineId(TensuraTamableEntity.class, EntityDataSerializers.BLOCK_POS);
   protected int sleepingTime;
   protected int maxSleepTime;
   @Nullable
   private UUID persistentAngerTarget;

   protected TensuraTamableEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.lookControl = new TensuraTamableEntity.SleepLookControl();
      this.moveControl = new TensuraTamableEntity.SleepMoveControl();
   }

   public static Builder setAttributes() {
      return Mob.createMobAttributes().add(Attributes.FOLLOW_RANGE, 32.0).add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 2.0);
   }

   protected PathNavigation createNavigation(Level level) {
      SwimmableGroundNavigation navigation = new SwimmableGroundNavigation(this, level);
      navigation.setCanFloat(true);
      navigation.setCanPassDoors(true);
      return navigation;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_REMAINING_ANGER_TIME, 0);
      builder.define(CUSTOM_OWNER_COMMAND, 0);
      builder.define(CUSTOM_BEHAVIOUR, 0);
      builder.define(SLEEPING, false);
      builder.define(WANDER_POS, BlockPos.ZERO);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("OwnerCommand", this.getOwnerCommand());
      compound.putInt("Behaviour", this.getBehaviour());
      compound.putBoolean("Sleeping", this.isSleeping());
      compound.putInt("WanderPosX", this.getWanderPos().getX());
      compound.putInt("WanderPosY", this.getWanderPos().getY());
      compound.putInt("WanderPosZ", this.getWanderPos().getZ());
      this.addPersistentAngerSaveData(compound);
      if (this.getOwnerUUID() == null) {
         compound.remove("Owner");
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setOwnerCommand(compound.getInt("OwnerCommand"));
      this.setBehaviour(compound.getInt("Behaviour"));
      this.entityData.set(SLEEPING, compound.getBoolean("Sleeping"));
      this.setWanderPos(new BlockPos(compound.getInt("WanderPosX"), compound.getInt("WanderPosY"), compound.getInt("WanderPosZ")));
      this.readPersistentAngerSaveData(this.level(), compound);
   }

   public int getOwnerCommand() {
      return (Integer)this.entityData.get(CUSTOM_OWNER_COMMAND);
   }

   public void setOwnerCommand(int command) {
      this.entityData.set(CUSTOM_OWNER_COMMAND, command);
   }

   public int getBehaviour() {
      return (Integer)this.entityData.get(CUSTOM_BEHAVIOUR);
   }

   public void setBehaviour(int behaviour) {
      this.entityData.set(CUSTOM_BEHAVIOUR, behaviour);
   }

   public boolean isWandering() {
      return this.getOwnerCommand() == 1;
   }

   public void setWandering(boolean wandering) {
      int mode = wandering ? 1 : 0;
      this.entityData.set(CUSTOM_OWNER_COMMAND, mode);
   }

   public boolean canSleep() {
      return false;
   }

   public boolean isSleeping() {
      if (SleepEffect.isForcedSleeping(this)) {
         return true;
      } else {
         return !this.canSleep() ? super.isSleeping() : (Boolean)this.entityData.get(SLEEPING);
      }
   }

   public void setSleeping(boolean sleeping) {
      if (this.canSleep()) {
         this.entityData.set(SLEEPING, sleeping);
         this.sleepingTime = 0;
         if (sleeping) {
            this.maxSleepTime = 200 + this.random.nextInt(550);
         } else {
            this.maxSleepTime = 100 + this.random.nextInt(50);
         }
      }
   }

   public void setWanderPos(BlockPos pPos) {
      this.entityData.set(WANDER_POS, pPos);
   }

   public BlockPos getWanderPos() {
      return (BlockPos)this.entityData.get(WANDER_POS);
   }

   public int getRemainingPersistentAngerTime() {
      return (Integer)this.entityData.get(DATA_REMAINING_ANGER_TIME);
   }

   public void setRemainingPersistentAngerTime(int pTime) {
      this.entityData.set(DATA_REMAINING_ANGER_TIME, pTime);
   }

   @Nullable
   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public void setPersistentAngerTarget(@Nullable UUID pTarget) {
      this.persistentAngerTarget = pTarget;
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(TimeUtil.rangeOfSeconds(20, 39).sample(this.random));
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (SLEEPING.equals(pKey)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   public boolean canBeLeashed() {
      return this.isTame();
   }

   public boolean canUsePortal(boolean bl) {
      return (bl || !this.isPassenger()) && this.isAlive();
   }

   public boolean requireTamingForBreeding() {
      return true;
   }

   public boolean canBreed() {
      return this.isInLove();
   }

   public boolean canMate(Animal animal) {
      if (animal == this) {
         return false;
      }

      if (animal.getClass() != this.getClass()) {
         return false;
      }

      TensuraTamableEntity partner = (TensuraTamableEntity)animal;
      if (this.requireTamingForBreeding()) {
         if (!this.isTame() || !partner.isTame()) {
            return false;
         } else {
            return !this.isInSittingPose() && !partner.isInSittingPose()
               ? this.canBreed() && animal.canBreed() && SubordinateHelper.isAlly(this, partner)
               : false;
         }
      } else {
         return this.isTame() == partner.isTame() && this.canBreed() && animal.canBreed();
      }
   }

   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      return null;
   }

   public void finalizeSpawnChildFromBreeding(ServerLevel serverLevel, Animal animal, @Nullable AgeableMob ageableMob) {
      super.finalizeSpawnChildFromBreeding(serverLevel, animal, ageableMob);
      if (ageableMob instanceof TensuraTamableEntity tamable) {
         tamable.setWandering(true);
      }
   }

   @NotNull
   public EntityDimensions getDefaultDimensions(Pose pPose) {
      return this.isSleeping() ? this.getSleepingDimensions(pPose) : super.getDefaultDimensions(pPose);
   }

   public EntityDimensions getSleepingDimensions(Pose pPose) {
      return this.canSleep() ? super.getDefaultDimensions(pPose).scale(1.0F, 0.5F) : super.getDefaultDimensions(pPose);
   }

   public void setLastHurtByMob(@Nullable LivingEntity livingEntity) {
      super.setLastHurtByMob(livingEntity);
      BrainUtils.setMemory(this, MemoryModuleType.HURT_BY_ENTITY, livingEntity);
   }

   @Nullable
   public LivingEntity getTarget() {
      return !BrainUtils.hasMemory(this, MemoryModuleType.ATTACK_TARGET) ? super.getTarget() : BrainUtils.getTargetOfEntity(this, super.getTarget());
   }

   public void clearTarget() {
      SubordinateHelper.removeTarget(this);
   }

   public boolean wantsToAttack(LivingEntity pTarget, LivingEntity pOwner) {
      return !ILivingPartEntity.checkForHead(pTarget).isAlliedTo(pOwner);
   }

   @NotNull
   protected AABB getAttackBoundingBox() {
      Entity entity = this.getVehicle();
      AABB box;
      if (entity != null) {
         AABB vehicleBox = entity.getBoundingBox();
         AABB boundingBox = this.getBoundingBox();
         box = new AABB(
            Math.min(boundingBox.minX, vehicleBox.minX),
            boundingBox.minY,
            Math.min(boundingBox.minZ, vehicleBox.minZ),
            Math.max(boundingBox.maxX, vehicleBox.maxX),
            boundingBox.maxY,
            Math.max(boundingBox.maxZ, vehicleBox.maxZ)
         );
      } else {
         box = this.getBoundingBox();
      }

      double range = Math.max(Math.sqrt(2.0) - 0.6, this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE));
      return box.inflate(range, range, range);
   }

   public DamageSource getBaseDamageSource() {
      return this.damageSources().mobAttack(this);
   }

   public boolean doHurtTarget(Entity entity) {
      return this.doHurtTarget(entity, 1.0F);
   }

   public boolean doHurtTarget(Entity entity, float multiplier) {
      float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
      DamageSource damageSource = this.getBaseDamageSource();
      Level level = this.level();
      if (level instanceof ServerLevel serverLevel) {
         f = EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), entity, damageSource, f);
      }

      boolean hurt = entity.hurt(damageSource, f);
      if (hurt) {
         float g = this.getKnockback(entity, damageSource);
         if (g > 0.0F && entity instanceof LivingEntity target) {
            target.knockback(g * 0.5F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
         }

         if (level instanceof ServerLevel serverLevel) {
            if (entity instanceof LivingEntity target) {
               ItemStack stack = this.getWeaponItem();
               stack.getItem().hurtEnemy(stack, target, this);
            }

            EnchantmentHelper.doPostAttackEffects(serverLevel, entity, damageSource);
            TensuraEnchantmentHelper.doAdditionalAfterDamage(serverLevel, entity, this, this.damageSources().mobAttack(this), this.getMainHandItem(), f);
         }

         this.setLastHurtMob(entity);
         this.playAttackSound();
      }

      if (level instanceof ServerLevel serverLevel && !SkillUtils.shouldCancelInteraction(this)) {
         TensuraEnchantmentHelper.doAdditionalAfterAttack(serverLevel, entity, this, this.damageSources().mobAttack(this), this.getMainHandItem(), f);
      }

      return hurt;
   }

   public boolean isTamedByNonPlayer() {
      return !this.isTame() ? false : !(this.getOwner() instanceof Player);
   }

   public void resetOwner(@Nullable UUID ownerUUID) {
      if (ownerUUID == null) {
         this.setTame(false, false);
         this.setOwnerUUID(null);
         this.entityData.set(DATA_OWNERUUID_ID, Optional.empty());
      } else {
         this.setTame(true, false);
         this.setOwnerUUID(ownerUUID);
      }

      this.setOrderedToSit(false);
      this.setInSittingPose(false);
      this.setBehaviour(0);
      this.setWandering(false);
      this.clearTarget();
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.canSleep()) {
            this.sleepHandler();
         }
      }
   }

   protected boolean shouldWakeUp() {
      return SleepEffect.isForcedSleeping(this)
         ? false
         : this.isAngry()
            || this.isInLove()
            || this.isInLiquid()
            || this.isVehicle()
            || this.isPassenger()
            || this.getBehaviour() == 3
            || ++this.sleepingTime > this.maxSleepTime && this.level().isDay()
            || this.shouldFollowOwner();
   }

   protected boolean canGoToSleep() {
      return !this.isAngry()
         && this.level().isNight()
         && !this.shouldFollowOwner()
         && this.getBehaviour() != 3
         && !this.isInLiquid()
         && !this.isVehicle()
         && !this.isPassenger()
         && this.random.nextInt(100) == 0;
   }

   protected void sleepHandler() {
      boolean sleeping = this.isSleeping();
      if (sleeping && this.shouldWakeUp()) {
         this.setSleeping(false);
         sleeping = false;
      }

      if (!sleeping && this.canGoToSleep()) {
         if (this.getRandom().nextBoolean()) {
            this.setSleeping(true);
         } else {
            this.sleepingTime = 0;
            this.maxSleepTime = 100 + this.random.nextInt(550);
         }
      }
   }

   public boolean shouldFollowOwner() {
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         return false;
      } else {
         return owner.distanceToSqr(this) < 144.0 ? false : !this.isOrderedToSit() && !this.isWandering();
      }
   }

   public void aiStep() {
      super.aiStep();
      if (this.level() instanceof ServerLevel server) {
         this.updatePersistentAnger(server, true);
      }

      if (this.isSleeping()) {
         this.jumping = false;
         this.xxa = 0.0F;
         this.zza = 0.0F;
      }
   }

   public boolean isAlliedTo(Entity entity) {
      LivingEntity livingentity = this.getOwner();
      if (!this.isTame() || livingentity == null) {
         return super.isAlliedTo(entity);
      } else if (entity == livingentity) {
         return true;
      } else {
         return entity instanceof ISubordinate tamableAnimal ? tamableAnimal.isOwnedBy(livingentity) : livingentity.isAlliedTo(entity);
      }
   }

   public boolean isFood(ItemStack itemStack) {
      return false;
   }

   public boolean isHealingFood(ItemStack itemStack) {
      return this.isFood(itemStack);
   }

   public boolean isTamingFood(ItemStack pStack) {
      return false;
   }

   public void applyFoodHeal(ItemStack stack, Player player, InteractionHand hand) {
      this.heal(3.0F);
      this.ate();
   }

   public InteractionResult onHipokuteHeal(Player player, InteractionHand hand, ItemStack stack) {
      return stack.interactLivingEntity(player, this, hand);
   }

   public InteractionResult onNormalHeal(Player player, InteractionHand hand, ItemStack stack) {
      this.usePlayerItem(player, hand, stack);
      this.applyFoodHeal(stack, player, hand);
      this.level().playSound(null, this, SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F);
      return InteractionResult.SUCCESS;
   }

   public InteractionResult onTaming(Player player, InteractionHand hand, ItemStack stack) {
      this.usePlayerItem(player, hand, stack);
      this.ate();
      if (this.getRandom().nextInt(9) == 0) {
         this.tame(player);
         this.getNavigation().stop();
         this.setTarget(null);
         this.setOrderedToSit(true);
         this.level().broadcastEntityEvent(this, (byte)7);
      } else {
         this.level().broadcastEntityEvent(this, (byte)6);
      }

      return InteractionResult.sidedSuccess(this.level().isClientSide());
   }

   public InteractionResult handleEating(Player player, InteractionHand hand, ItemStack stack) {
      if (!this.isTame() && this.isTamingFood(stack)) {
         return this.onTaming(player, hand, stack);
      }

      if (this.isHealingFood(stack) && this.getHealth() < this.getMaxHealth()) {
         return this.onNormalHeal(player, hand, stack);
      }

      if (this.isFood(stack)) {
         int i = this.getAge();
         if (!this.level().isClientSide() && i == 0 && this.canFallInLove()) {
            this.usePlayerItem(player, hand, stack);
            this.setInLove(player);
            this.level().playSound(player, this, SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
         }

         if (this.isBaby()) {
            this.usePlayerItem(player, hand, stack);
            this.ageUp(getSpeedUpSecondsWhenFeeding(-i), true);
            this.ate();
            this.level().playSound(player, this, SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
         }

         if (this.level().isClientSide()) {
            return InteractionResult.CONSUME;
         }
      }

      return InteractionResult.PASS;
   }

   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (this.isTame() && this.isOwnedBy(player)) {
         this.cycleCommands(this, player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   @NotNull
   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!stack.is(TensuraItemTags.HIPOKUTE_POTIONS) && !stack.is(TensuraItemTags.ARCANE_POTIONS)) {
         InteractionResult eating = this.handleEating(player, hand, stack);
         return eating.consumesAction() ? eating : this.handleCommanding(player, hand, stack);
      } else {
         return this.onHipokuteHeal(player, hand, stack);
      }
   }

   protected boolean canSpawnSpecialVariant(MobSpawnType pSpawnType) {
      if (pSpawnType == null) {
         return true;
      } else {
         return pSpawnType == MobSpawnType.MOB_SUMMONED ? false : pSpawnType != MobSpawnType.BUCKET;
      }
   }

   protected boolean canRandomizeSpawnData(MobSpawnType pSpawnType) {
      if (pSpawnType == null) {
         return true;
      } else {
         return pSpawnType == MobSpawnType.TRIGGERED ? false : pSpawnType != MobSpawnType.BUCKET;
      }
   }

   public static boolean checkGrassMobSpawnRules(
      EntityType<? extends Mob> mob, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      if (pSpawnType == MobSpawnType.SPAWNER) {
         return true;
      } else {
         return !pLevel.getBlockState(pPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) ? false : Mob.checkMobSpawnRules(mob, pLevel, pSpawnType, pPos, pRandom);
      }
   }

   public static boolean checkTensuraMobSpawnRules(
      EntityType<? extends Mob> mob, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      if (pSpawnType == MobSpawnType.SPAWNER) {
         return true;
      } else {
         return !pLevel.getBlockState(pPos.below()).is(TensuraBlockTags.MOBS_SPAWNABLE_ON)
            ? false
            : Mob.checkMobSpawnRules(mob, pLevel, pSpawnType, pPos, pRandom);
      }
   }

   public static boolean checkHostileGrassMobSpawnRules(
      EntityType<? extends Mob> mob, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      if (pLevel.getDifficulty() == Difficulty.PEACEFUL) {
         return false;
      } else if (pSpawnType == MobSpawnType.SPAWNER) {
         return true;
      } else {
         return !pLevel.getBlockState(pPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) ? false : Mob.checkMobSpawnRules(mob, pLevel, pSpawnType, pPos, pRandom);
      }
   }

   public static boolean checkHostileMobSpawnRules(
      EntityType<? extends Mob> mob, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      if (pLevel.getDifficulty() == Difficulty.PEACEFUL) {
         return false;
      } else if (pSpawnType == MobSpawnType.SPAWNER) {
         return true;
      } else {
         return !pLevel.getBlockState(pPos.below()).is(TensuraBlockTags.MOBS_SPAWNABLE_ON)
            ? false
            : Mob.checkMobSpawnRules(mob, pLevel, pSpawnType, pPos, pRandom);
      }
   }

   public static boolean checkFlyingSpawnRules(
      EntityType<? extends Mob> pType, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      return true;
   }

   protected int getBaseExperienceReward() {
      return this.xpReward > 0
         ? super.getBaseExperienceReward()
         : (int)(EnergyHelper.getEPGain(this, EnergyHelper.getBaseMaxEP(this)) * TensuraBehaviourHelper.CONFIG.xpDropMultiplier);
   }

   protected boolean shouldDespawnInPeaceful() {
      return !this.isTame();
   }

   protected boolean removeWhenNoAction() {
      return this.removeWhenFarAway(256.0);
   }

   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      if (this.isTame()) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(this);
      return existence.getSpawnType() == MobSpawnType.STRUCTURE ? false : existence.getSpawnType() != MobSpawnType.BREEDING;
   }

   public void checkDespawn() {
      if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
         this.discard();
      } else if (!this.isPersistenceRequired() && !this.requiresCustomPersistence()) {
         Entity entity = this.level().getNearestPlayer(this, -1.0);
         if (entity != null) {
            double distance = entity.distanceToSqr(this);
            int removeDistance = this.getType().getCategory().getDespawnDistance();
            if (distance > removeDistance * removeDistance && this.removeWhenFarAway(distance)) {
               this.discard();
            }

            int noRemoveDistance = this.getType().getCategory().getNoDespawnDistance();
            int l = noRemoveDistance * noRemoveDistance;
            if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && distance > l && this.removeWhenNoAction() && this.removeWhenFarAway(distance)) {
               this.discard();
            } else if (distance < l) {
               this.noActionTime = 0;
            }
         }
      } else {
         this.noActionTime = 0;
      }
   }

   public static <E extends TensuraTamableEntity> MoveToWalkTarget<E> getMoveToWalkTarget() {
      return (MoveToWalkTarget<E>)new MoveToWalkTarget()
         .cooldownFor(entity -> 0)
         .startCondition(entity -> !entity.isOrderedToSit() && !entity.isSleeping())
         .stopIf(ISubordinate::isOrderedToSit);
   }

   public static <E extends TensuraTamableEntity & IFlying> MoveOrFlyToWalkTarget<E> getMoveOrFlyToWalkTarget() {
      return (MoveOrFlyToWalkTarget<E>)new MoveOrFlyToWalkTarget()
         .startCondition(entity -> !entity.isOrderedToSit() && !entity.isSleeping())
         .stopIf(rec$ -> ((ISubordinate)rec$).isOrderedToSit());
   }

   public class SleepLookControl extends LookControl {
      public SleepLookControl() {
         super(TensuraTamableEntity.this);
      }

      public void tick() {
         if (!TensuraTamableEntity.this.isSleeping()) {
            super.tick();
         }
      }
   }

   public class SleepMoveControl extends MoveControl {
      public SleepMoveControl() {
         super(TensuraTamableEntity.this);
      }

      public void tick() {
         if (!TensuraTamableEntity.this.isSleeping()) {
            super.tick();
         }
      }
   }
}
