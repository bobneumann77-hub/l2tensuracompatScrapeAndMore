package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.config.entity.EntityConfig;
import io.github.manasmods.tensura.entity.ai.behaviour.ProfessionBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.InteractWithEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.VillagerLikeBreed;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.NearbyTreeSensor;
import io.github.manasmods.tensura.entity.ai.sensor.NearbyWantedItemSensor;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.entity.variant.OrcVariant;
import io.github.manasmods.tensura.handler.AttributeHandler;
import io.github.manasmods.tensura.handler.DeathHandler;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.RaceHelper;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowParent;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.InteractWithDoor;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.schedule.SmartBrainSchedule;
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

public class OrcEntity extends PlayerLikeEntity implements SmartBrainOwner<OrcEntity>, GeoEntity, INameEvolution, VariantHolder<OrcVariant> {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> NECK = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> NECK_COLOR = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TOP = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TOP_COLOR = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BOTTOM_COLOR = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BELT_COLOR = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BOOTS_COLOR = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Boolean> BANDAGE = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> NECKLACE = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> EVOLUTION_STATE = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> EVOLVING = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private SmartBrainSchedule schedule;

   public OrcEntity(EntityType<? extends OrcEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 1.5)
         .add(Attributes.MAX_HEALTH, 28.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.STEP_HEIGHT, 1.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(EVOLUTION_STATE, 0);
      builder.define(DATA_ID_TYPE_VARIANT, 0);
      builder.define(EVOLVING, 0);
      builder.define(NECK, 0);
      builder.define(NECK_COLOR, -1);
      builder.define(TOP, 0);
      builder.define(TOP_COLOR, -1);
      builder.define(BOTTOM_COLOR, -1);
      builder.define(BELT_COLOR, 0);
      builder.define(BOOTS_COLOR, -1);
      builder.define(BANDAGE, false);
      builder.define(NECKLACE, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("EvoState", this.getCurrentEvolutionState());
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
      compound.putInt("Evolving", this.getEvolving());
      TensuraBehaviourHelper.saveGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
      compound.putInt("Neck", (Integer)this.entityData.get(NECK));
      compound.putInt("NeckColor", (Integer)this.entityData.get(NECK_COLOR));
      compound.putInt("Top", (Integer)this.entityData.get(TOP));
      compound.putInt("TopColor", (Integer)this.entityData.get(TOP_COLOR));
      compound.putInt("BottomColor", (Integer)this.entityData.get(BOTTOM_COLOR));
      compound.putInt("BeltColor", (Integer)this.entityData.get(BELT_COLOR));
      compound.putInt("BootsColor", (Integer)this.entityData.get(BOOTS_COLOR));
      compound.putBoolean("Bandage", (Boolean)this.entityData.get(BANDAGE));
      compound.putBoolean("Necklace", (Boolean)this.entityData.get(NECKLACE));
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      this.setCurrentEvolutionState(compound.getInt("EvoState"));
      super.readAdditionalSaveData(compound);
      this.entityData.set(DATA_ID_TYPE_VARIANT, compound.getInt("Variant"));
      this.setEvolving(compound.getInt("Evolving"));
      TensuraBehaviourHelper.readGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
      this.entityData.set(NECK, compound.getInt("Neck"));
      this.entityData.set(NECK_COLOR, compound.getInt("NeckColor"));
      this.entityData.set(TOP, compound.getInt("Top"));
      this.entityData.set(TOP_COLOR, compound.getInt("TopColor"));
      this.entityData.set(BOTTOM_COLOR, compound.getInt("BottomColor"));
      this.entityData.set(BELT_COLOR, compound.getInt("BeltColor"));
      this.entityData.set(BOOTS_COLOR, compound.getInt("BootsColor"));
      this.entityData.set(BANDAGE, compound.getBoolean("Bandage"));
      this.entityData.set(NECKLACE, compound.getBoolean("Necklace"));
   }

   public OrcVariant getVariant() {
      return OrcVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(OrcVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
   }

   public int getEvolving() {
      return (Integer)this.entityData.get(EVOLVING);
   }

   public void setEvolving(int tick) {
      this.entityData.set(EVOLVING, tick);
   }

   public OrcVariant.Neck getNeck() {
      return OrcVariant.Neck.byId((Integer)this.entityData.get(NECK));
   }

   public void setNeck(int neck) {
      this.entityData.set(NECK, neck);
   }

   public int getNeckColor() {
      return (Integer)this.entityData.get(NECK_COLOR);
   }

   public void setNeckColor(int i) {
      this.entityData.set(NECK_COLOR, i);
   }

   public OrcVariant.Top getTop() {
      return OrcVariant.Top.byId((Integer)this.entityData.get(TOP));
   }

   public void setTop(int top) {
      this.entityData.set(TOP, top);
   }

   public int getTopColor() {
      return (Integer)this.entityData.get(TOP_COLOR);
   }

   public void setTopColor(int i) {
      this.entityData.set(TOP_COLOR, i);
   }

   public int getBottomColor() {
      return (Integer)this.entityData.get(BOTTOM_COLOR);
   }

   public void setBottomColor(int i) {
      this.entityData.set(BOTTOM_COLOR, i);
   }

   public int getBeltColor() {
      return (Integer)this.entityData.get(BELT_COLOR);
   }

   public void setBeltColor(int i) {
      this.entityData.set(BELT_COLOR, i);
   }

   public int getBootsColor() {
      return (Integer)this.entityData.get(BOOTS_COLOR);
   }

   public void setBootsColor(int i) {
      this.entityData.set(BOOTS_COLOR, i);
   }

   public boolean hasBandage() {
      return (Boolean)this.entityData.get(BANDAGE);
   }

   public void setBandage(boolean bandage) {
      this.entityData.set(BANDAGE, bandage);
   }

   public boolean hasNecklace() {
      return (Boolean)this.entityData.get(NECKLACE);
   }

   public void setNecklace(boolean bandage) {
      this.entityData.set(NECKLACE, bandage);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (EVOLVING.equals(pKey)) {
         this.reapplyPosition();
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   @Override
   public int getMenuRenderSize() {
      return 20;
   }

   @NotNull
   @Override
   public EntityDimensions getDefaultDimensions(Pose pPose) {
      EntityDimensions entitydimensions = super.getDefaultDimensions(pPose);
      if (this.getClass() == OrcDisasterEntity.class) {
         return entitydimensions;
      } else {
         int tick = 40 - this.getEvolving();
         if (this.getEvolving() > 0 && tick > 0) {
            float scale = 1.0F + 0.5F * (tick / 40.0F);
            return entitydimensions.scale(scale);
         } else {
            return entitydimensions;
         }
      }
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      OrcEntity baby = (OrcEntity)((EntityType)MonsterEntityTypes.ORC.get()).create(pLevel);
      if (baby == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         baby.setOwnerUUID(uuid);
         baby.setTame(true, true);
      }

      float chance = 0.0F;
      if (this.getVariant().equals(OrcVariant.ROYAL) || this.getVariant().equals(OrcVariant.ROYAL_LORD)) {
         chance += 0.5F;
      }

      if (pOtherParent instanceof OrcEntity orc && (orc.getVariant().equals(OrcVariant.ROYAL) || orc.getVariant().equals(OrcVariant.ROYAL_LORD))) {
         chance += 0.5F;
      }

      if (this.random.nextFloat() <= chance) {
         baby.setVariant(OrcVariant.ROYAL);
      }

      baby.applyRandomVariant(baby.getVariant());
      return baby;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.CACTUS) || super.isInvulnerableTo(source);
   }

   @Override
   public int getChestSlots() {
      return 18 + 9 * this.getCurrentEvolutionState();
   }

   @Override
   public int getCurrentEvolutionState() {
      return (Integer)this.entityData.get(EVOLUTION_STATE);
   }

   @Override
   public void setCurrentEvolutionState(int state) {
      this.entityData.set(EVOLUTION_STATE, state);
   }

   @Override
   public void evolve() {
      this.gainMovementSpeed(this, 0.03);
      this.gainSwimSpeed(this, 1.0);
   }

   @Override
   public void tick() {
      super.tick();
      this.evolvingTick();
   }

   protected void evolvingTick() {
      int evolving = this.getEvolving();
      if (evolving > 0) {
         int next = evolving - 1;
         this.setEvolving(next);
         this.playSound((SoundEvent)TensuraSoundEvents.ORC_LAUGH.get(), 1.0F, 1.0F);
         TensuraParticleHelper.addServerAuraParticles(this, TensuraParticleUtils.getChaosEaterAura(1.0F, 8.0F, -0.3F), 10, 0.01);
         if (next == 0) {
            if (this.canEvolveToLord()) {
               this.royalLordEvolves();
            } else {
               this.playSound((SoundEvent)TensuraSoundEvents.ORC_TRANSFORM.get(), 1.0F, 1.0F);
            }
         }
      }
   }

   public void push(Entity pEntity) {
      if (!(pEntity instanceof OrcLordEntity)) {
         super.push(pEntity);
      }
   }

   public void swing(InteractionHand interactionHand, boolean bl) {
      super.swing(interactionHand, bl);
      if (interactionHand.equals(InteractionHand.MAIN_HAND)) {
         this.triggerAnim("miscController", "attack");
      } else {
         this.triggerAnim("miscController", "shield");
      }
   }

   public boolean killedEntity(ServerLevel pLevel, LivingEntity target) {
      boolean wasKilled = super.killedEntity(pLevel, target);
      if (wasKilled && this.getClass() == OrcEntity.class) {
         IExistence existence = TensuraStorages.getExistenceFrom(this);
         if (existence.getSummoner() == null) {
            return true;
         }

         if (this.level() instanceof ServerLevel level && level.getEntity(existence.getSummoner()) instanceof OrcLordEntity lord) {
            lord.killedEntity(pLevel, target);
            double epGain = EnergyHelper.getEPGain(target, lord, true);
            DeathHandler.gainEPEntity(lord, target, epGain * target.level().getGameRules().getInt(TensuraGameRules.EP_GAIN_MULTIPLIER));
            TensuraStorages.getExistenceFrom(target).setSkippingEPDrop(true);
            return true;
         }
      }

      return wasKilled;
   }

   protected boolean canEvolveToLord() {
      if (this.isTame()) {
         return false;
      } else {
         return this.isBaby() ? false : this.getVariant().equals(OrcVariant.ROYAL_LORD);
      }
   }

   @Override
   public InteractionResult handleEating(Player player, InteractionHand hand, ItemStack stack) {
      if (this.canEvolveToLord() && stack.is((Item)TensuraMobDropItems.ROYAL_BLOOD.get()) && this.getEvolving() > -10 && this.getEvolving() <= 0) {
         if (!player.isCreative()) {
            stack.shrink(1);
         }

         this.setEvolving(this.getEvolving() - 1);
         if (this.getEvolving() <= -10 && EnergyHelper.getMaxEP(this) >= 80000.0) {
            this.setEvolving(40);
            IExistence existence = TensuraStorages.getExistenceFrom(this);
            existence.addNeutralTarget(player.getUUID());
            existence.markDirty();
         }

         this.heal(this.getMaxHealth());
         this.level().playSound(null, this, SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return super.handleEating(player, hand, stack);
      }
   }

   private void royalLordEvolves() {
      Level level = this.level();
      CompoundTag tag = this.saveWithoutId(new CompoundTag());
      this.discard();
      OrcLordEntity orc = new OrcLordEntity((EntityType<? extends OrcLordEntity>)MonsterEntityTypes.ORC_LORD.get(), level);
      orc.load(tag);
      if (level instanceof ServerLevel serverLevel) {
         orc.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(orc.blockPosition()), MobSpawnType.CONVERSION, null);
      }

      orc.setVariant(OrcVariant.byId(orc.getRandom().nextInt(4)));
      RaceHelper.applyBaseAttribute(setAttributes().build(), orc, true);
      orc.setHealth(orc.getMaxHealth());
      AttributeHandler.updateEntityExistence(orc, level, TensuraStorages.getExistenceFrom(orc));
      level.addFreshEntity(orc);
      level.playSound(null, orc.blockPosition(), (SoundEvent)TensuraSoundEvents.ORC_TRANSFORM.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      TensuraParticleHelper.addServerAuraParticles(this, TensuraParticleUtils.getChaosEaterAura(1.0F, 8.0F, -0.3F), 10, 0.01);
   }

   public void die(DamageSource damageSource) {
      TensuraBehaviourHelper.releaseHome(this);
      super.die(damageSource);
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      if (this.isTame()) {
         return false;
      }

      List<? extends OrcLordEntity> list = this.level().getEntitiesOfClass(OrcDisasterEntity.class, this.getBoundingBox().inflate(30.0, 8.0, 30.0));
      return list.isEmpty();
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.orc, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.getClass() != OrcEntity.class) {
         return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
      }

      if (this.canRandomizeSpawnData(pReason)) {
         OrcVariant variant = null;
         if (this.canSpawnSpecialVariant(pReason) && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.orcLordChance, pLevel.getRandom())) {
            OrcLordEntity orcLord = new OrcLordEntity((EntityType<? extends OrcLordEntity>)MonsterEntityTypes.ORC_LORD.get(), this.level());
            orcLord.setPos(this.getX(), this.getY(), this.getZ());
            orcLord.populateDefaultEquipmentSlots(this.random, pDifficulty);
            orcLord.finalizeSpawn(pLevel, this.level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.NATURAL, null);
            this.level().addFreshEntity(orcLord);
            this.setRemoved(RemovalReason.DISCARDED);
         } else if (TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.orcRoyalChance, pLevel.getRandom())) {
            if (this.canSpawnSpecialVariant(pReason)
               && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.orcRoyalLordChance, pLevel.getRandom())) {
               variant = OrcVariant.ROYAL_LORD;
               ManasSkillInstance instance = new TensuraSkillInstance((ManasSkill)CommonSkills.SELF_REGENERATION.get());
               instance.setToggled(true);
               SkillHelper.learnSkill(this, instance);
            } else {
               variant = OrcVariant.ROYAL;
            }
         }

         if (variant == null) {
            variant = OrcVariant.byId(this.getRandom().nextInt(4));
         }

         this.applyRandomVariant(variant);
         this.populateDefaultEquipmentSlots(this.getRandom(), pDifficulty);
      }

      TensuraBehaviourHelper.setHome(this, pLevel.getLevel(), this.blockPosition());
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   private void applyRandomVariant(@Nullable OrcVariant variant) {
      RandomSource source = this.getRandom();
      EntityConfig.Orc config = TensuraBehaviourHelper.CONFIG.Orc;
      if (variant != null) {
         this.setVariant(variant);
         if (variant == OrcVariant.ROYAL_LORD || variant == OrcVariant.ROYAL) {
            this.setNecklace(true);
         }
      } else {
         this.setNecklace(source.nextInt(5) == 1);
      }

      this.setTop(OrcVariant.Top.getRandom(this));
      List<Integer> tops = config.orcTopClothesColors;
      this.setTopColor(tops.get(source.nextInt(tops.size())));
      this.setBandage(source.nextInt(3) == 1);
      List<Integer> bottoms = config.orcBottomClothesColors;
      this.setBottomColor(bottoms.get(source.nextInt(bottoms.size())));
      List<Integer> boots = config.orcLeatherColors;
      this.setBootsColor(boots.get(source.nextInt(boots.size())));
      if (source.nextInt(3) == 1) {
         this.setBeltColor(boots.get(source.nextInt(boots.size())));
      }

      int neck = source.nextInt(3) == 1 ? 0 : OrcVariant.Neck.getRandom(this);
      this.setNeck(neck);
      if (neck == OrcVariant.Neck.NECKWRAP.getId()) {
         this.setNeckColor(tops.get(source.nextInt(tops.size())));
      } else if (neck == OrcVariant.Neck.SIDECAPE.getId()) {
         this.setNeckColor(boots.get(source.nextInt(boots.size())));
      }
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      super.populateDefaultEquipmentSlots(pRandom, pDifficulty);
      if (!(pRandom.nextFloat() >= 0.2F)) {
         int i = pRandom.nextInt(3);
         ItemStack stack = new ItemStack(Items.IRON_AXE);
         if (i == 0) {
            stack = new ItemStack((ItemLike)TensuraToolItems.IRON_SPEAR.get());
         }

         this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
         this.updateContainerEquipment();
      }
   }

   @Nullable
   @Override
   public Item getEquipmentForArmor(EquipmentSlot pSlot, int pChance) {
      switch (pSlot) {
         case HEAD:
            if (pChance == 0) {
               return Items.LEATHER_HELMET;
            } else if (pChance == 1) {
               return Items.CHAINMAIL_HELMET;
            } else {
               if (pChance == 2) {
                  return Items.IRON_HELMET;
               }

               return null;
            }
         case CHEST:
            if (pChance == 0) {
               return Items.LEATHER_CHESTPLATE;
            } else if (pChance == 1) {
               return Items.CHAINMAIL_CHESTPLATE;
            } else {
               if (pChance == 2) {
                  return Items.IRON_CHESTPLATE;
               }

               return null;
            }
         case LEGS:
            if (pChance == 0) {
               return Items.LEATHER_LEGGINGS;
            } else if (pChance == 1) {
               return Items.CHAINMAIL_LEGGINGS;
            } else {
               if (pChance == 2) {
                  return Items.IRON_LEGGINGS;
               }

               return null;
            }
         case FEET:
            if (pChance == 0) {
               return Items.LEATHER_BOOTS;
            } else if (pChance == 1) {
               return Items.CHAINMAIL_BOOTS;
            } else {
               if (pChance == 2) {
                  return Items.IRON_BOOTS;
               }

               return null;
            }
         default:
            return null;
      }
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.ORC_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.ORC_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.ORC_DEATH.get();
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<OrcEntity>> getSensors() {
      return ObjectArrayList.of(
         new ExtendedSensor[]{
            new NearbyLivingEntitySensor(),
            new HurtBySensor(),
            new SleepSensor(),
            new NearbyTreeSensor()
               .setRadius(10.0, 2.0)
               .shouldScan(entity -> entity.shouldDoLumberjack() && entity.level().getGameRules().getBoolean(TensuraGameRules.NPC_WORKING)),
            new NearbyWantedItemSensor()
               .shouldScan(entity -> entity.shouldAlwaysPickUpItem() && entity.level().getGameRules().getBoolean(TensuraGameRules.NPC_WORKING))
               .setPredicate(NearbyWantedItemSensor.getProfessionItemPredicate())
         }
      );
   }

   public BrainActivityGroup<OrcEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new WakeUp().stopIf(entity -> {
         if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
            entity.stopSleeping();
            return true;
         } else {
            return false;
         }
      }), new LookAtTarget(), new FloatToSurfaceOfFluid(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<OrcEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new FirstApplicableBehaviour(
                        new ExtendedBehaviour[]{
                           new InteractWithEntity(this.getType(), MemoryModuleType.BREED_TARGET)
                              .selfPredicate(AgeableMob::canBreed)
                              .targetPredicate(AgeableMob::canBreed)
                              .bothPredicate((entity, target) -> ((Animal)entity).canMate((Animal)target))
                              .interactTime(entity -> 300),
                           new VillagerLikeBreed()
                        }
                     )
                     .startCondition(entity -> !entity.isTamedByNonPlayer()),
                  new FollowParent().startCondition(entity -> !entity.isTame() || entity.isWandering()),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false),
                  new SubordinateFollowOwner(),
                  ProfessionBehaviourHelper.getBasicJobBehaviours(this),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new InteractWithDoor(),
            new HumanoidConsumeItem().startCondition(entity -> entity.shouldHeal()).stopIf(entity -> !entity.shouldHeal()),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<OrcEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new StrafeTarget().stopStrafingWhen(entity -> !entity.usingRangedWeapon()).startCondition(PlayerLikeEntity::usingRangedWeapon),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F).startCondition(entity -> !entity.usingRangedWeapon()),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  PlayerLikeEntity.getSpearAttack(20)
                     .attackInterval(entity -> 5)
                     .attackRadius(25.0F)
                     .whenStarting(entity -> this.triggerAnim("miscController", "spear")),
                  PlayerLikeEntity.getCrossbowAttack()
                     .attackInterval(entity -> 10)
                     .attackRadius(30.0F)
                     .whenStarting(entity -> this.triggerAnim("miscController", "crossbow")),
                  PlayerLikeEntity.getBowAttack()
                     .attackInterval(entity -> 10)
                     .attackRadius(25.0F)
                     .whenStarting(entity -> this.triggerAnim("miscController", "crossbow")),
                  new AnimatableMeleeAttack(1)
                     .attackInterval(entity -> 5)
                     .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
                     .startCondition(entity -> !entity.usingRangedWeapon())
               }
            )
         }
      );
   }

   public Map<Activity, BrainActivityGroup<? extends OrcEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends OrcEntity>>)Util.make(
         new Object2ObjectOpenHashMap(), map -> map.put(Activity.REST, TensuraBehaviourHelper.getHumanoidSleepActivityGroup(this))
      );
   }

   public SmartBrainSchedule getSchedule() {
      if (this.schedule == null) {
         this.schedule = new SmartBrainSchedule().activityAt(10, Activity.IDLE).activityAt(13000, Activity.REST);
      }

      return this.schedule;
   }

   public void startSleeping(BlockPos blockPos) {
      super.startSleeping(blockPos);
      BrainUtils.setMemory(this, MemoryModuleType.LAST_SLEPT, this.level().getGameTime());
      BrainUtils.clearMemory(this, MemoryModuleType.WALK_TARGET);
      BrainUtils.clearMemory(this, MemoryModuleType.LOOK_TARGET);
      BrainUtils.clearMemory(this, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
   }

   public void stopSleeping() {
      super.stopSleeping();
      BrainUtils.setMemory(this, MemoryModuleType.LAST_WOKEN, this.level().getGameTime());
   }

   protected PlayState loopController(AnimationState<OrcEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.orc.idle";
      } else if (this.isInSittingPose()) {
         name = "animation.orc.sit";
      } else if (this.shouldSwim()) {
         name = "animation.orc.swim";
      } else if (state.isMoving()) {
         if (!this.isAngry() && !this.isSprinting() && (this.getControllingPassenger() == null || !this.getControllingPassenger().isSprinting())) {
            name = "animation.orc.walk";
         } else {
            name = "animation.orc.run";
         }
      } else {
         name = "animation.orc.idle";
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
               .triggerableAnim("attack", RawAnimation.begin().then("animation.orc.attack", LoopType.PLAY_ONCE))
               .triggerableAnim("shield", RawAnimation.begin().then("animation.orc.shield", LoopType.PLAY_ONCE))
               .triggerableAnim("crossbow", RawAnimation.begin().then("animation.orc.crossbow", LoopType.PLAY_ONCE))
               .triggerableAnim("spear", RawAnimation.begin().then("animation.orc.spear", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
