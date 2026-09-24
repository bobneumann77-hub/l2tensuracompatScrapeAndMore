package io.github.manasmods.tensura.entity.monster;

import com.google.common.collect.ImmutableMap;
import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.skill.resist.MagicResistance;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.config.entity.EntityConfig;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.ai.behaviour.ProfessionBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.InteractWithEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.SleepOnBed;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.ValidateNearbyPoi;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.VillagerLikeBreed;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.path.MerchantFollowTrader;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomSwimAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetWalkTargetFromBlockMemory;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.AcquirePoi;
import io.github.manasmods.tensura.entity.ai.sensor.NearbyTreeSensor;
import io.github.manasmods.tensura.entity.ai.sensor.NearbyWantedItemSensor;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.merchant.trade.OneForOneTrade;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraMerchantEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.IFlyingAmphibian;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.entity.variant.LizardmanVariant;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.world.TensuraGameRules;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.AllApplicableBehaviours;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowParent;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.InteractWithDoor;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StayWithinDistanceOfAttackTarget;
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

public class LizardmanEntity
   extends TensuraMerchantEntity
   implements SmartBrainOwner<LizardmanEntity>,
   GeoEntity,
   INameEvolution,
   VariantHolder<LizardmanVariant>,
   IFlyingAmphibian {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(LizardmanEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HAIR = SynchedEntityData.defineId(LizardmanEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HAIR_COLOR = SynchedEntityData.defineId(LizardmanEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TOP = SynchedEntityData.defineId(LizardmanEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TOP_COLOR = SynchedEntityData.defineId(LizardmanEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BOTTOM_COLOR = SynchedEntityData.defineId(LizardmanEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Boolean> BANDAGE = SynchedEntityData.defineId(LizardmanEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> EVOLUTION_STATE = SynchedEntityData.defineId(LizardmanEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> EVOLVING = SynchedEntityData.defineId(LizardmanEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(LizardmanEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private SmartBrainSchedule schedule;
   private boolean landNavigating = false;
   private int swimmingTick = 0;
   protected int flyingTick;
   protected boolean wasFlying;

   public LizardmanEntity(EntityType<? extends LizardmanEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.initFlying(this);
      this.setMerchantLevel(1);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MAX_HEALTH, 24.0)
         .add(Attributes.MOVEMENT_SPEED, 0.22F)
         .add(Attributes.FLYING_SPEED, 0.6F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.2F)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 4.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.2F);
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
      builder.define(DATA_ID_TYPE_VARIANT, 0);
      builder.define(HAIR, 0);
      builder.define(HAIR_COLOR, -1);
      builder.define(TOP, 0);
      builder.define(TOP_COLOR, -1);
      builder.define(BOTTOM_COLOR, -1);
      builder.define(BANDAGE, false);
      builder.define(EVOLUTION_STATE, 0);
      builder.define(EVOLVING, 0);
      builder.define(FLYING, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("EvoState", this.getCurrentEvolutionState());
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
      compound.putInt("Hair", (Integer)this.entityData.get(HAIR));
      compound.putInt("HairColor", (Integer)this.entityData.get(HAIR_COLOR));
      compound.putInt("Top", (Integer)this.entityData.get(TOP));
      compound.putInt("TopColor", (Integer)this.entityData.get(TOP_COLOR));
      compound.putInt("BottomColor", (Integer)this.entityData.get(BOTTOM_COLOR));
      compound.putBoolean("Bandage", (Boolean)this.entityData.get(BANDAGE));
      compound.putInt("Evolving", this.getEvolving());
      compound.putBoolean("Flying", this.isFlying());
      TensuraBehaviourHelper.saveGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      this.setCurrentEvolutionState(compound.getInt("EvoState"));
      super.readAdditionalSaveData(compound);
      this.entityData.set(DATA_ID_TYPE_VARIANT, compound.getInt("Variant"));
      this.entityData.set(HAIR, compound.getInt("Hair"));
      this.entityData.set(HAIR_COLOR, compound.getInt("HairColor"));
      this.entityData.set(TOP, compound.getInt("Top"));
      this.entityData.set(TOP_COLOR, compound.getInt("TopColor"));
      this.entityData.set(BOTTOM_COLOR, compound.getInt("BottomColor"));
      this.entityData.set(BANDAGE, compound.getBoolean("Bandage"));
      this.setEvolving(compound.getInt("Evolving"));
      this.setFlying(compound.getBoolean("Flying"));
      TensuraBehaviourHelper.readGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
   }

   public LizardmanVariant getVariant() {
      return LizardmanVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(LizardmanVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
   }

   public LizardmanVariant.Hair getHair() {
      return LizardmanVariant.Hair.byId((Integer)this.entityData.get(HAIR));
   }

   public void setHair(int hair) {
      this.entityData.set(HAIR, hair);
   }

   public int getHairColor() {
      return (Integer)this.entityData.get(HAIR_COLOR);
   }

   public void setHairColor(int i) {
      this.entityData.set(HAIR_COLOR, i);
   }

   public LizardmanVariant.Top getTop() {
      return LizardmanVariant.Top.byId((Integer)this.entityData.get(TOP));
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

   public boolean hasBandage() {
      return (Boolean)this.entityData.get(BANDAGE);
   }

   public void setBandage(boolean bandage) {
      this.entityData.set(BANDAGE, bandage);
   }

   public int getEvolving() {
      return (Integer)this.entityData.get(EVOLVING);
   }

   public void setEvolving(int tick) {
      this.entityData.set(EVOLVING, tick);
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
      return IFlyingAmphibian.super.shouldStopFlying(entity) || this.isOrderedToSit() || this.isInLove();
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
      return 25;
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      LizardmanEntity baby = (LizardmanEntity)((EntityType)MonsterEntityTypes.LIZARDMAN.get()).create(pLevel);
      if (baby == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         baby.setOwnerUUID(uuid);
         baby.setTame(true, true);
      }

      int i = this.random.nextInt(9);
      LizardmanVariant variant;
      if (i < 4) {
         variant = this.getVariant();
      } else if (i < 8 && pOtherParent instanceof LizardmanEntity lizardman) {
         variant = lizardman.getVariant();
      } else {
         variant = (LizardmanVariant)Util.getRandom(LizardmanVariant.values(), this.random);
      }

      baby.setVariant(variant);
      baby.applyRandomVariant(true);
      baby.setHairColor(TensuraColors.getMixedColor(this.getHairColor(), ((LizardmanEntity)pOtherParent).getHairColor()));
      if (this.isDragonewt() && ((LizardmanEntity)pOtherParent).isDragonewt()) {
         baby.evolve();
      }

      return baby;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.SWEET_BERRY_BUSH) || super.isInvulnerableTo(source);
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return this.isDragonewt() ? false : super.causeFallDamage(pFallDistance, pMultiplier, pSource);
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
   public boolean prefersOnLand() {
      return true;
   }

   @Override
   public boolean canOpenDoor() {
      return true;
   }

   public boolean isDragonewt() {
      return this.getCurrentEvolutionState() >= 1;
   }

   @Override
   public boolean canFly() {
      return this.isDragonewt();
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
      int current = this.getCurrentEvolutionState();
      if (current < this.getMaxEvolutionState()) {
         this.setCurrentEvolutionState(current + 1);
         this.gainSwimSpeed(this, 1.0);
         Skills storage = SkillAPI.getSkillsFrom(this);
         storage.learnSkill((ManasSkill)IntrinsicSkills.DRAGON_EYE.get());
         storage.learnSkill((ManasSkill)IntrinsicSkills.DRAGON_EAR.get());
         storage.learnSkill((ManasSkill)IntrinsicSkills.DRAGON_MODE.get());
         storage.learnSkill(this.random.nextBoolean() ? (ManasSkill)IntrinsicSkills.FLAME_BREATH.get() : (ManasSkill)IntrinsicSkills.THUNDER_BREATH.get());
         ManasSkillInstance instance = ((MagicResistance)ResistanceSkills.MAGIC_RESISTANCE.get()).createDefaultInstance();
         instance.setToggled(true);
         SkillHelper.learnSkill(this, instance);
         if (this.isDragonewt()) {
            this.switchNavigator(this, this.isInWaterOrBubble(), false);
         }
      }
   }

   @Override
   protected int getTradesPerLevel(int level) {
      return 1;
   }

   @Override
   protected boolean shouldIncreaseLevel() {
      int i = this.getMerchantLevel();
      return VillagerData.canLevelUp(i) && this.getVillagerXp() >= VillagerData.getMaxXpPerLevel(i);
   }

   @Override
   public Int2ObjectMap<ItemListing[]> getPossibleTrades() {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 8, 12, Items.COD, 4, 16, 1),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 8, 12, Items.SALMON, 4, 16, 1),
               new OneForOneTrade(Items.COD, 4, (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 3, 5, 5, 1),
               new OneForOneTrade(Items.SALMON, 4, (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 3, 5, 5, 1)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 3, 7, Items.PUFFERFISH, 4, 16, 5),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 18, 22, Items.TROPICAL_FISH, 4, 16, 5),
               new OneForOneTrade(Items.PUFFERFISH, 4, (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 2, 5, 5),
               new OneForOneTrade(Items.TROPICAL_FISH, 4, (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 8, 10, 5, 5),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, (ItemLike)TensuraToolItems.WOODEN_SPEAR.get(), 1, 1, 50)
            },
            3,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraConsumableItems.RAW_SPEAR_TORO_MEAT.get(), 1, 3, (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 3, 20),
               new OneForOneTrade((ItemLike)TensuraConsumableItems.SPEAR_TORO_FIN.get(), 1, (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 2, 2, 20),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 8, 15, (ItemLike)TensuraToolItems.IRON_SPEAR.get(), 1, 1, 50)
            },
            4,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraConsumableItems.RAW_SISSIE_MEAT.get(), 2, (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 2, 3, 3, 30),
               new OneForOneTrade((ItemLike)TensuraConsumableItems.SISSIE_FIN.get(), 1, (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 2, 3, 2, 30),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 15, 25, (ItemLike)TensuraToolItems.SILVER_SPEAR.get(), 1, 1, 50)
            },
            5,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraConsumableItems.RAW_MEGALODON_MEAT.get(), 2, (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 7, 10, 3, 30),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 28, 38, Items.TRIDENT, 1, 1, 50),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (ItemLike)TensuraSmithingSchematicItems.SPEAR.get(), 1, 50)
            }
         )
      );
   }

   @Override
   protected boolean shouldCancelTrading(Player player) {
      if (super.shouldCancelTrading(player)) {
         return true;
      }

      if (this.getMerchantLevel() < 1) {
         this.increaseMerchantCareer();
      }

      return false;
   }

   @Override
   protected void onMerchantUpdate() {
      this.playSound((SoundEvent)TensuraSoundEvents.LIZARDMAN_AMBIENT.get(), 1.5F, 2.0F);
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
      if (!this.isDragonewt() && this.isFlying()) {
         this.setFlying(false);
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

   @Override
   public void die(DamageSource damageSource) {
      TensuraBehaviourHelper.releaseHome(this);
      super.die(damageSource);
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   public static boolean checkLizardSpawnRules(
      EntityType<LizardmanEntity> lizardman, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
   ) {
      return !pLevel.getBlockState(pPos.below()).is(TensuraBlockTags.MOBS_SPAWNABLE_ON)
         ? false
         : checkMobSpawnRules(lizardman, pLevel, pSpawnType, pPos, pRandom) && pPos.getY() < 70 && !pLevel.canSeeSky(pPos);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         this.applyRandomVariant(false);
         this.populateDefaultEquipmentSlots(this.random, pDifficulty);
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   private void applyRandomVariant(boolean breeding) {
      RandomSource source = this.getRandom();
      EntityConfig.Lizardman config = TensuraBehaviourHelper.CONFIG.Lizardman;
      List<Integer> misc = config.lizardmanMiscClothesColors;
      int hair = LizardmanVariant.Hair.getRandom(this);
      this.setHair(hair);
      if (!breeding) {
         this.setVariant((LizardmanVariant)Util.getRandom(LizardmanVariant.values(), this.random));
         List<Integer> hairs = hair >= 5 ? misc : config.lizardmanHairColors;
         this.setHairColor(hairs.get(source.nextInt(hairs.size())));
      }

      int top = LizardmanVariant.Top.getRandom(this);
      this.setTop(top);
      List<Integer> tops = top == LizardmanVariant.Top.CAPE.getId() ? misc : config.lizardmanTopClothesColors;
      this.setTopColor(tops.get(source.nextInt(tops.size())));
      List<Integer> bottoms = config.lizardmanBottomClothesColors;
      this.setBottomColor(bottoms.get(source.nextInt(bottoms.size())));
      this.setBandage(source.nextInt(3) == 1);
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      super.populateDefaultEquipmentSlots(pRandom, pDifficulty);
      int i = pRandom.nextInt(100);
      ItemStack stack = new ItemStack((ItemLike)TensuraToolItems.IRON_SPEAR.get());
      if (i < 25) {
         if (i < 3) {
            stack = new ItemStack((ItemLike)TensuraToolItems.VORTEX_SPEAR.get());
         } else {
            stack = new ItemStack(Items.TRIDENT);
         }
      }

      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
      this.updateContainerEquipment();
   }

   @Nullable
   @Override
   public Item getEquipmentForArmor(EquipmentSlot pSlot, int pChance) {
      switch (pSlot) {
         case HEAD:
            if (pChance == 0) {
               return Items.LEATHER_HELMET;
            } else {
               if (pChance == 1) {
                  return (Item)TensuraArmorItems.MONSTER_LEATHER_D_HELMET.get();
               }

               return null;
            }
         case CHEST:
            if (pChance == 0) {
               return Items.LEATHER_CHESTPLATE;
            } else if (pChance == 1) {
               return Items.CHAINMAIL_CHESTPLATE;
            } else if (pChance == 2) {
               return Items.IRON_CHESTPLATE;
            } else {
               if (pChance == 3) {
                  return (Item)TensuraArmorItems.MONSTER_LEATHER_D_CHESTPLATE.get();
               }

               return null;
            }
         case LEGS:
            if (pChance == 0) {
               return Items.LEATHER_LEGGINGS;
            } else if (pChance == 1) {
               return Items.CHAINMAIL_LEGGINGS;
            } else if (pChance == 2) {
               return Items.IRON_LEGGINGS;
            } else {
               if (pChance == 3) {
                  return (Item)TensuraArmorItems.MONSTER_LEATHER_D_LEGGINGS.get();
               }

               return null;
            }
         case FEET:
            if (pChance == 0) {
               return Items.LEATHER_BOOTS;
            } else if (pChance == 1) {
               return Items.CHAINMAIL_BOOTS;
            } else if (pChance == 2) {
               return Items.IRON_BOOTS;
            } else {
               if (pChance == 3) {
                  return (Item)TensuraArmorItems.MONSTER_LEATHER_D_LEGGINGS.get();
               }

               return null;
            }
         default:
            return null;
      }
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.LIZARDMAN_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.LIZARDMAN_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.LIZARDMAN_DEATH.get();
   }

   @NotNull
   @Override
   public SoundEvent getNotifyTradeSound() {
      return (SoundEvent)TensuraSoundEvents.LIZARDMAN_AMBIENT.get();
   }

   @Override
   protected SoundEvent getTradeUpdatedSound(boolean success) {
      return success ? (SoundEvent)TensuraSoundEvents.LIZARDMAN_AMBIENT.get() : (SoundEvent)TensuraSoundEvents.LIZARDMAN_HURT.get();
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<LizardmanEntity>> getSensors() {
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

   public BrainActivityGroup<LizardmanEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new WakeUp().onWakeUp(TensuraMerchantEntity::restockIfPossible).stopIf(entity -> {
               if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
                  entity.stopSleeping();
                  return true;
               } else {
                  return false;
               }
            }),
            new LookAtTarget(),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraTamableEntity.getMoveOrFlyToWalkTarget().startCondition(entity -> !entity.isOrderedToSit() && ((IFlying)entity).canFly()),
                  TensuraTamableEntity.getMoveToWalkTarget()
               }
            )
         }
      );
   }

   public BrainActivityGroup<LizardmanEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new InteractWithDoor(),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new InteractWithEntity(this.getType(), MemoryModuleType.BREED_TARGET)
                     .selfPredicate(AgeableMob::canBreed)
                     .targetPredicate(AgeableMob::canBreed)
                     .bothPredicate((entity, target) -> ((Animal)entity).canMate((Animal)target))
                     .interactTime(entity -> 300),
                  new VillagerLikeBreed(),
                  new FollowParent().startCondition(entity -> !entity.isTame() || entity.isWandering()),
                  new MerchantFollowTrader(),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false),
                  new SubordinateFollowOwner().canTeleportOffGroundWhen(entity -> {
                     if (entity.isDragonewt()) {
                        entity.setFlying(true);
                        return true;
                     } else {
                        return false;
                     }
                  }),
                  ProfessionBehaviourHelper.getBasicJobBehaviours(this),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new HumanoidConsumeItem().startCondition(entity -> entity.shouldHeal()).stopIf(entity -> !entity.shouldHeal()),
            new OneRandomBehaviour(
               new ExtendedBehaviour[]{
                  new FirstApplicableBehaviour(
                        new ExtendedBehaviour[]{
                           new SetRandomFlyAndWalkTarget().verticalWeight(entity -> 1).startCondition(LizardmanEntity::isDragonewt),
                           new SetRandomSwimAndWalkTarget()
                              .speedModifier((entity, pos) -> entity.isInWaterOrBubble() ? 1.5F : 1.0F)
                              .cooldownFor(entity -> 0)
                              .startCondition(entity -> !entity.isOrderedToSit())
                              .stopIf(ISubordinate::isOrderedToSit)
                        }
                     )
                     .startCondition(entity -> !entity.isOrderedToSit()),
                  new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
               }
            )
         }
      );
   }

   public BrainActivityGroup<LizardmanEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new StrafeTarget().stopStrafingWhen(entity -> !entity.usingRangedWeapon()).startCondition(PlayerLikeEntity::usingRangedWeapon),
            new StayWithinDistanceOfAttackTarget().stopIf(entity -> !entity.usingRangedWeapon()).startCondition(PlayerLikeEntity::usingRangedWeapon),
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

   public Map<Activity, BrainActivityGroup<? extends LizardmanEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends LizardmanEntity>>)Util.make(
         new Object2ObjectOpenHashMap(),
         map -> map.put(
            Activity.REST,
            new BrainActivityGroup(Activity.REST)
               .behaviours(
                  new Behavior[]{
                     new FirstApplicableBehaviour(
                        new ExtendedBehaviour[]{
                           TensuraBehaviourHelper.getPreyTargeting(this, entity -> false).startCondition(ISubordinate::isTame),
                           new SubordinateFollowOwner().canTeleportOffGroundWhen(entity -> {
                              if (entity.isDragonewt()) {
                                 entity.setFlying(true);
                                 return true;
                              } else {
                                 return false;
                              }
                           }).startCondition(entity -> !TensuraBehaviourHelper.canContinueToSleep(entity)),
                           new AllApplicableBehaviours(
                              new ExtendedBehaviour[]{
                                 new AcquirePoi()
                                    .predicate((entity, holder) -> holder.is(PoiTypes.HOME))
                                    .writeTo(MemoryModuleType.HOME)
                                    .additionalPredicate((entity, pos) -> SleepOnBed.isValidBedPosition(entity.level().getBlockState(pos))),
                                 new ValidateNearbyPoi((entity, holder) -> holder.is(PoiTypes.HOME), MemoryModuleType.HOME),
                                 new SetWalkTargetFromBlockMemory(MemoryModuleType.HOME, PoiTypes.HOME)
                                    .startCondition(
                                       entity -> !entity.isSleeping()
                                          && TensuraBehaviourHelper.canContinueToSleep(entity, (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.HOME))
                                    ),
                                 new SleepOnBed().startCondition(TensuraBehaviourHelper::canContinueToSleep),
                                 new OneRandomBehaviour(
                                       new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))}
                                    )
                                    .startCondition(entity -> !entity.isSleeping() && !BrainUtils.hasMemory(entity, MemoryModuleType.WALK_TARGET))
                              }
                           )
                        }
                     )
                  }
               )
               .onlyStartWithMemoryStatus(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
               .onlyStartWithMemoryStatus(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT)
         )
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

   protected PlayState loopController(AnimationState<LizardmanEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.lizardman.idle";
      } else if (this.isInSittingPose()) {
         name = "animation.lizardman.sit";
      } else if (this.shouldSwim()) {
         name = "animation.lizardman.swim";
      } else if (state.isMoving()) {
         if (!this.onGround() && this.isDragonewt()) {
            name = "animation.lizardman.fly";
         } else if (!this.isAngry() && !this.isSprinting() && (this.getControllingPassenger() == null || !this.getControllingPassenger().isSprinting())) {
            name = "animation.lizardman.walk";
         } else {
            name = "animation.lizardman.run";
         }
      } else if (this.onGround()) {
         name = "animation.lizardman.idle";
      } else {
         name = "animation.lizardman.idle_fly";
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
               .triggerableAnim("attack", RawAnimation.begin().then("animation.lizardman.attack", LoopType.PLAY_ONCE))
               .triggerableAnim("shield", RawAnimation.begin().then("animation.lizardman.shield", LoopType.PLAY_ONCE))
               .triggerableAnim("crossbow", RawAnimation.begin().then("animation.lizardman.crossbow", LoopType.PLAY_ONCE))
               .triggerableAnim("spear", RawAnimation.begin().then("animation.lizardman.spear", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
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
