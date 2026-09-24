package io.github.manasmods.tensura.entity.template;

import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.human.ShizuEntity;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierPart;
import io.github.manasmods.tensura.entity.template.subclass.IElementalSpirit;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.IGiantMob;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import java.util.List;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GreaterSpiritEntity extends TensuraHumanoidEntity implements IElementalSpirit, IFlying, IGiantMob {
   protected static final EntityDataAccessor<Boolean> BOSS = SynchedEntityData.defineId(GreaterSpiritEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(GreaterSpiritEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> MAGIC_ID = SynchedEntityData.defineId(GreaterSpiritEntity.class, EntityDataSerializers.INT);
   protected ServerBossEvent bossEvent;
   private CompoundTag hostNBT = null;
   protected int flyingTick;
   protected boolean wasFlying;

   public GreaterSpiritEntity(EntityType<? extends GreaterSpiritEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.initFlying(this);
   }

   @Override
   public void switchMoveControl(MoveControl control) {
      this.moveControl = control;
   }

   @Override
   public void switchNavigation(PathNavigation navigation) {
      this.navigation = navigation;
   }

   public boolean shouldAttack(LivingEntity entity) {
      if (entity == this) {
         return false;
      }

      if (!entity.isAlive()) {
         return false;
      }

      if (this.isAlliedTo(entity)) {
         return false;
      }

      if (this.getOwner() != null) {
         if (entity.isAlliedTo(this.getOwner())) {
            return false;
         } else if (this.getTarget() == entity) {
            return true;
         } else {
            return entity instanceof Mob mob
               ? mob.getTarget() == this.getOwner()
               : this.getOwner().getLastHurtMob() == entity || this.getOwner().getLastHurtByMob() == entity;
         }
      } else {
         return this.getTarget() == entity ? true : !entity.hasInfiniteMaterials();
      }
   }

   public boolean shouldTarget(LivingEntity entity) {
      if (entity == this || RaceUtils.isNonLiving(entity)) {
         return false;
      } else {
         return !this.isAlliedTo(entity) && !this.isNeutral() ? !entity.hasInfiniteMaterials() : false;
      }
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(BOSS, false);
      builder.define(FLYING, false);
      builder.define(MAGIC_ID, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.hostNBT != null) {
         compound.put("ShizuNBT", this.hostNBT);
      }

      compound.putBoolean("Boss", this.isBoss());
      compound.putBoolean("Flying", this.isFlying());
      compound.putInt("MagicID", this.getMagicID());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.contains("ShizuNBT", 10)) {
         this.setHostNBT((CompoundTag)compound.get("ShizuNBT"));
      }

      this.setBoss(compound.getBoolean("Boss"));
      this.setFlying(compound.getBoolean("Flying"));
      this.setMagicID(compound.getInt("MagicID"));
   }

   public boolean isBoss() {
      return (Boolean)this.entityData.get(BOSS);
   }

   public void setBoss(boolean boss) {
      this.entityData.set(BOSS, boss);
      if (!boss) {
         this.bossEvent.removeAllPlayers();
      }
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

   public int getMagicID() {
      return (Integer)this.entityData.get(MAGIC_ID);
   }

   public void setMagicID(int id) {
      this.entityData.set(MAGIC_ID, id);
   }

   @Override
   public boolean canSleep() {
      return !this.isNoAi() && !this.isFlying();
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   @Override
   public EntityDimensions getSleepingDimensions(Pose pPose) {
      return this.getType().getDimensions().scale(this.getAgeScale());
   }

   public boolean canBeAffected(MobEffectInstance instance) {
      if (instance.is(MobEffects.POISON)) {
         return false;
      } else if (instance.is(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON))) {
         return false;
      } else if (instance.is(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS))) {
         return false;
      } else {
         return instance.is(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION)) ? false : super.canBeAffected(instance);
      }
   }

   protected void actuallyHurt(DamageSource source, float damage) {
      super.actuallyHurt(source, damage * this.getDamageReductionMultiplier(source));
   }

   protected float getDamageReductionMultiplier(DamageSource source) {
      return TensuraDamageHelper.isNaturalEffects(source) ? 0.2F : this.getPhysicalAttackInput(source);
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   @Override
   protected boolean removeWhenNoAction() {
      return false;
   }

   public boolean canAttack(LivingEntity pTarget) {
      return this.isAlliedTo(pTarget) ? false : super.canAttack(pTarget);
   }

   protected boolean canRide(Entity entity) {
      return false;
   }

   private boolean isNeutral() {
      return this.isBoss() ? false : !this.isTamedByNonPlayer();
   }

   @Override
   public boolean canBeNamed(Player player) {
      return this.isNeutral();
   }

   public void setCustomName(@Nullable Component pName) {
      super.setCustomName(pName);
      this.bossEvent.setName(this.getDisplayName());
   }

   public void startSeenByPlayer(ServerPlayer pPlayer) {
      super.startSeenByPlayer(pPlayer);
      if (!this.isTame()) {
         if (this.isBoss()) {
            this.bossEvent.addPlayer(pPlayer);
         }
      }
   }

   public void stopSeenByPlayer(ServerPlayer pPlayer) {
      super.stopSeenByPlayer(pPlayer);
      this.bossEvent.removePlayer(pPlayer);
   }

   @Override
   protected void applyTamingSideEffects() {
      super.applyTamingSideEffects();
      this.bossEvent.removeAllPlayers();
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.isBoss() && !this.isTame()) {
         this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
      }

      if (this.isColliding(this, false)) {
         this.breakBlocks();
         if (this.tickCount % 20 == 0) {
            List<BarrierPart> list = this.level().getEntitiesOfClass(BarrierPart.class, this.getBoundingBox().inflate(1.0));
            if (!list.isEmpty()) {
               for (BarrierPart barrier : list) {
                  this.doHurtTarget(barrier);
               }
            }
         }
      }
   }

   protected void breakBlocks() {
      if (!this.isTame() && this.isFlying() && this.getTarget() != null) {
         this.breakBlocks(this, 2.0F, true, 0, null, true);
      }
   }

   @Override
   public boolean breakableBlocks(LivingEntity entity, BlockPos pos, BlockState state) {
      return !state.is(TensuraBlockTags.BOSS_IMMUNE);
   }

   @Override
   public boolean dropBlockLoot(LivingEntity entity, BlockState state) {
      return !state.is(TensuraBlockTags.SKILL_BREAK_EASY);
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
      if (!this.isTame() && this.tickCount % 20 == 0) {
         this.heal(2.0F);
      }
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.SPIRIT_FOOD);
   }

   @Override
   public SpiritualMagic.SpiritLevel getSpiritLevel() {
      return SpiritualMagic.SpiritLevel.GREATER;
   }

   public abstract Item getElementalCore();

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (!this.isTame() || !this.isOwnedBy(player)) {
         return InteractionResult.PASS;
      }

      if (this.convertElementalCore(this, player, hand, this.getElementalCore())) {
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      }

      InteractionResult golemInteraction = this.getGolemInteraction(player, hand, this);
      if (golemInteraction.consumesAction()) {
         return golemInteraction;
      }

      InteractionResult interaction = this.getInventoryInteraction(player, hand);
      if (interaction.consumesAction()) {
         return interaction;
      }

      this.cycleCommands(this, player);
      return InteractionResult.sidedSuccess(this.level().isClientSide());
   }

   @Override
   public void applyFoodHeal(ItemStack stack, Player player, InteractionHand hand) {
      this.heal(5.0F);
      this.ate();
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (!this.onGround()) {
         this.setFlying(true);
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   public void die(DamageSource source) {
      super.die(source);
      if (!this.isAlive()) {
         if (!this.level().isClientSide()) {
            if (this.hostNBT != null) {
               ShizuEntity entity = new ShizuEntity((EntityType<? extends ShizuEntity>)HumanEntityTypes.SHIZU.get(), this.level());
               entity.load(this.hostNBT);
               entity.moveTo(this.position());
               entity.setTransformTick(200);
               entity.setSleeping(true);
               entity.setDying(true);
               entity.setHealth(entity.getMaxHealth());
               if (this.level() instanceof ServerLevel serverLevel) {
                  entity.finalizeSpawn(serverLevel, this.level().getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.EVENT, null);
               }

               this.level().addFreshEntity(entity);
               this.spawnDeathParticles();
            }
         }
      }
   }

   protected abstract void spawnDeathParticles();

   @Generated
   public void setHostNBT(CompoundTag hostNBT) {
      this.hostNBT = hostNBT;
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
