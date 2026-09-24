package io.github.manasmods.tensura.entity.monster;

import com.google.common.collect.ImmutableMap;
import io.github.manasmods.tensura.entity.ai.behaviour.ProfessionBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.InteractWithEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.VillagerLikeBreed;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.path.MerchantFollowTrader;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.NearbyTreeSensor;
import io.github.manasmods.tensura.entity.ai.sensor.NearbyWantedItemSensor;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.merchant.trade.OneForOneTrade;
import io.github.manasmods.tensura.entity.merchant.trade.TensuraTradeHelper;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraMerchantEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IGender;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.entity.variant.GoblinVariant;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.world.TensuraGameRules;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
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

public class GoblinEntity extends TensuraMerchantEntity implements SmartBrainOwner<GoblinEntity>, INameEvolution, IGender {
   private static final EntityDataAccessor<Integer> EVOLUTION_STATE = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> GENDER = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SKIN = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> FACE = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HAIR = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HAIR_COLOR = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> BANDAGES = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<Integer> HEAD = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> HEAD_COLOR = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TOP = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TOP_COLOR = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BOTTOM = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BOTTOM_COLOR = SynchedEntityData.defineId(GoblinEntity.class, EntityDataSerializers.INT);
   private SmartBrainSchedule schedule;

   public GoblinEntity(EntityType<? extends GoblinEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      this.setMerchantLevel(1);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.MAX_HEALTH, 12.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.STEP_HEIGHT, 1.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(EVOLUTION_STATE, 0);
      builder.define(GENDER, 0);
      builder.define(SKIN, 0);
      builder.define(FACE, 0);
      builder.define(HAIR, 0);
      builder.define(HAIR_COLOR, 0);
      builder.define(BANDAGES, false);
      builder.define(HEAD, 0);
      builder.define(HEAD_COLOR, -1);
      builder.define(TOP, 0);
      builder.define(TOP_COLOR, -1);
      builder.define(BOTTOM, 0);
      builder.define(BOTTOM_COLOR, -1);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("EvoState", this.getCurrentEvolutionState());
      super.addAdditionalSaveData(compound);
      TensuraBehaviourHelper.saveGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
      compound.putInt("Gender", (Integer)this.entityData.get(GENDER));
      compound.putInt("Skin", (Integer)this.entityData.get(SKIN));
      compound.putInt("Face", (Integer)this.entityData.get(FACE));
      compound.putInt("Hair", (Integer)this.entityData.get(HAIR));
      compound.putInt("HairColor", (Integer)this.entityData.get(HAIR_COLOR));
      compound.putBoolean("Bandages", (Boolean)this.entityData.get(BANDAGES));
      compound.putInt("Head", (Integer)this.entityData.get(HEAD));
      compound.putInt("HeadColor", (Integer)this.entityData.get(HEAD_COLOR));
      compound.putInt("Top", (Integer)this.entityData.get(TOP));
      compound.putInt("TopColor", (Integer)this.entityData.get(TOP_COLOR));
      compound.putInt("Bottom", (Integer)this.entityData.get(BOTTOM));
      compound.putInt("BottomColor", (Integer)this.entityData.get(BOTTOM_COLOR));
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      this.setCurrentEvolutionState(compound.getInt("EvoState"));
      super.readAdditionalSaveData(compound);
      TensuraBehaviourHelper.readGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
      this.entityData.set(GENDER, compound.getInt("Gender"));
      this.entityData.set(SKIN, compound.getInt("Skin"));
      this.entityData.set(FACE, compound.getInt("Face"));
      this.entityData.set(HAIR, compound.getInt("Hair"));
      this.entityData.set(HAIR_COLOR, compound.getInt("HairColor"));
      this.entityData.set(BANDAGES, compound.getBoolean("Bandages"));
      this.entityData.set(HEAD, compound.getInt("Head"));
      this.entityData.set(HEAD_COLOR, compound.getInt("HeadColor"));
      this.entityData.set(TOP, compound.getInt("Top"));
      this.entityData.set(TOP_COLOR, compound.getInt("TopColor"));
      this.entityData.set(BOTTOM, compound.getInt("Bottom"));
      this.entityData.set(BOTTOM_COLOR, compound.getInt("BottomColor"));
   }

   public GoblinVariant.Gender getGender() {
      return GoblinVariant.Gender.byId((Integer)this.entityData.get(GENDER));
   }

   public void setGender(int gender) {
      this.entityData.set(GENDER, gender);
   }

   @Override
   public boolean isMale() {
      return this.getGender() == GoblinVariant.Gender.MALE;
   }

   @Override
   public boolean isFemale() {
      return this.getGender() == GoblinVariant.Gender.FEMALE;
   }

   public GoblinVariant.Skin getSkin() {
      return GoblinVariant.Skin.byId((Integer)this.entityData.get(SKIN));
   }

   public void setSkin(int skin) {
      this.entityData.set(SKIN, skin);
   }

   public GoblinVariant.Face getFace() {
      return GoblinVariant.Face.byId((Integer)this.entityData.get(FACE));
   }

   public void setFace(int face) {
      this.entityData.set(FACE, face);
   }

   public GoblinVariant.Hair getHair() {
      return GoblinVariant.Hair.byId((Integer)this.entityData.get(HAIR));
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

   public GoblinVariant.Head getHead() {
      return GoblinVariant.Head.byId((Integer)this.entityData.get(HEAD));
   }

   public void setHead(int head) {
      this.entityData.set(HEAD, head);
   }

   public int getHeadColor() {
      return (Integer)this.entityData.get(HEAD_COLOR);
   }

   public void setHeadColor(int i) {
      this.entityData.set(HEAD_COLOR, i);
   }

   public GoblinVariant.Top getTop() {
      return GoblinVariant.Top.byId((Integer)this.entityData.get(TOP));
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

   public GoblinVariant.Bottom getBottom() {
      return GoblinVariant.Bottom.byId((Integer)this.entityData.get(BOTTOM));
   }

   public void setBottom(int bottom) {
      this.entityData.set(BOTTOM, bottom);
   }

   public int getBottomColor() {
      return (Integer)this.entityData.get(BOTTOM_COLOR);
   }

   public void setBottomColor(int i) {
      this.entityData.set(BOTTOM_COLOR, i);
   }

   public boolean hasBandages() {
      return (Boolean)this.entityData.get(BANDAGES);
   }

   public void setBandages(boolean bandages) {
      this.entityData.set(BANDAGES, bandages);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (EVOLUTION_STATE.equals(pKey)) {
         this.reapplyPosition();
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   @Override
   public int getChestSlots() {
      return 9 + 9 * this.getCurrentEvolutionState();
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return !super.canMate(pOtherAnimal) ? false : ((GoblinEntity)pOtherAnimal).getGender() != this.getGender();
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      GoblinEntity baby = (GoblinEntity)((EntityType)MonsterEntityTypes.GOBLIN.get()).create(pLevel);
      if (baby == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         baby.setOwnerUUID(uuid);
         baby.setTame(true, true);
      }

      baby.randomTexture();
      if (this.isHobgoblin() && ((GoblinEntity)pOtherParent).isHobgoblin()) {
         baby.evolve();
      }

      return baby;
   }

   public float getAgeScale() {
      float multiplier = this.isHobgoblin() ? 1.3333334F : 1.0F;
      return multiplier * (this.isBaby() ? 0.75F : 1.0F);
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   public boolean isHobgoblin() {
      return this.getCurrentEvolutionState() >= 1;
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
         this.gainMovementSpeed(this, 0.05);
         this.gainSwimSpeed(this, 1.0);
         this.gainMaxHealth(this, 10.0);
      }
   }

   @Override
   public boolean showProgressBar() {
      return this.getMerchantLevel() < 4;
   }

   @Override
   protected int getTradesPerLevel(int level) {
      return 1;
   }

   @Override
   protected boolean shouldIncreaseLevel() {
      int i = this.getMerchantLevel();
      return i >= 1 && i < 4 && this.getVillagerXp() >= VillagerData.getMaxXpPerLevel(i);
   }

   @Override
   public Int2ObjectMap<ItemListing[]> getPossibleTrades() {
      List<ItemLike> list = List.of(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);
      ItemStack leatherGear = TensuraTradeHelper.getDamagedItem(list.get(this.getRandom().nextInt(0, list.size())), 10, this.getRandom());
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, Items.STICK, 32, 45, 16, 1),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, Items.WHEAT_SEEDS, 12, 20, 16, 1)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 10, Items.LEATHER, 5, 12, 16, 5),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 5, (ItemLike)TensuraMaterialItems.THATCH.get(), 8, 16, 16, 5),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 3, Items.WHEAT, 12, 20, 16, 5)
            },
            3,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 24, 36, leatherGear, 1, 3, 30),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  20,
                  30,
                  TensuraTradeHelper.getDamagedItem((ItemLike)TensuraToolItems.WOODEN_SHORT_SWORD.get(), 10, this.getRandom()),
                  1,
                  4,
                  25
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  32,
                  45,
                  TensuraTradeHelper.getDamagedItem((ItemLike)TensuraToolItems.STONE_SHORT_SWORD.get(), 10, this.getRandom()),
                  1,
                  3,
                  30
               )
            },
            4,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  45,
                  64,
                  TensuraTradeHelper.getDamagedItem((ItemLike)TensuraToolItems.GOBLIN_CLUB.get(), 10, this.getRandom()),
                  1,
                  1,
                  50
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  1,
                  3,
                  TensuraTradeHelper.getDamagedItem((ItemLike)TensuraToolItems.IRON_SHORT_SWORD.get(), 20, this.getRandom()),
                  1,
                  2,
                  50
               ),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 27, 36, (ItemLike)TensuraSmithingSchematicItems.SHORT_SWORD.get(), 1, 1, 50)
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
      this.playSound((SoundEvent)TensuraSoundEvents.GOBLIN_AMBIENT.get(), 1.5F, 2.0F);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         this.populateDefaultEquipmentSlots(this.random, pDifficulty);
         this.randomTexture();
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   private void randomTexture() {
      this.setGender(this.random.nextBoolean() ? 0 : 1);
      this.setSkin(GoblinVariant.Skin.getRandom(this));
      this.setFace(GoblinVariant.Face.getRandom(this.getGender(), this));
      this.setBandages(this.random.nextBoolean());
      List<Integer> hairColors = TensuraBehaviourHelper.CONFIG.Goblin.goblinHairColors;
      this.setHair(GoblinVariant.Hair.getRandom(this));
      this.setHairColor(hairColors.get(this.random.nextInt(hairColors.size())));
      List<Integer> headColors = TensuraBehaviourHelper.CONFIG.Goblin.goblinHeadColors;
      this.setHead(this.random.nextBoolean() ? -1 : GoblinVariant.Head.getRandom(this));
      this.setHeadColor(headColors.get(this.random.nextInt(headColors.size())));
      List<Integer> colors = TensuraBehaviourHelper.CONFIG.Goblin.goblinClothingColors;
      this.setTop(GoblinVariant.Top.getRandom(this));
      this.setTopColor(colors.get(this.random.nextInt(colors.size())));
      List<Integer> bottomColors = TensuraBehaviourHelper.CONFIG.Goblin.goblinBottomClothesColors;
      this.setBottom(GoblinVariant.Bottom.getRandom(this));
      this.setBottomColor(bottomColors.get(this.random.nextInt(bottomColors.size())));
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      super.populateDefaultEquipmentSlots(pRandom, pDifficulty);
      if (!(pRandom.nextFloat() >= 0.5F)) {
         int i = pRandom.nextInt(3);
         ItemStack stack = new ItemStack((ItemLike)TensuraToolItems.GOBLIN_CLUB.get());
         if (i == 0) {
            stack = new ItemStack((ItemLike)TensuraToolItems.STONE_SHORT_SWORD.get());
         }

         this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
         this.updateContainerEquipment();
      }
   }

   @Nullable
   @Override
   public Item getEquipmentForArmor(EquipmentSlot pSlot, int pChance) {
      return switch (pSlot) {
         case HEAD -> pChance == 2 ? Items.LEATHER_HELMET : null;
         case CHEST -> pChance == 2 ? Items.LEATHER_CHESTPLATE : (pChance == 4 ? Items.CHAINMAIL_CHESTPLATE : null);
         case LEGS -> pChance == 2 ? Items.LEATHER_LEGGINGS : (pChance == 4 ? Items.CHAINMAIL_LEGGINGS : null);
         case FEET -> pChance == 2 ? Items.LEATHER_BOOTS : (pChance == 4 ? Items.CHAINMAIL_BOOTS : null);
         default -> null;
      };
   }

   @Override
   public void die(DamageSource damageSource) {
      TensuraBehaviourHelper.releaseHome(this);
      super.die(damageSource);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.GOBLIN_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.GOBLIN_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.GOBLIN_DEATH.get();
   }

   @NotNull
   @Override
   public SoundEvent getNotifyTradeSound() {
      return (SoundEvent)TensuraSoundEvents.GOBLIN_AGGRO.get();
   }

   @Override
   protected SoundEvent getTradeUpdatedSound(boolean success) {
      return success ? (SoundEvent)TensuraSoundEvents.GOBLIN_AMBIENT.get() : (SoundEvent)TensuraSoundEvents.GOBLIN_AGGRO.get();
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

   public List<ExtendedSensor<GoblinEntity>> getSensors() {
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

   public BrainActivityGroup<GoblinEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new WakeUp().stopIf(entity -> {
         if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
            entity.stopSleeping();
            return true;
         } else {
            return false;
         }
      }), new LookAtTarget(), new FloatToSurfaceOfFluid(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<GoblinEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
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

   public BrainActivityGroup<GoblinEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new StrafeTarget().stopStrafingWhen(entity -> !entity.usingRangedWeapon()).startCondition(PlayerLikeEntity::usingRangedWeapon),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F).startCondition(entity -> !entity.usingRangedWeapon()),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  PlayerLikeEntity.getSpearAttack(20).attackInterval(entity -> 5).attackRadius(25.0F),
                  PlayerLikeEntity.getCrossbowAttack().attackInterval(entity -> 10).attackRadius(30.0F),
                  PlayerLikeEntity.getBowAttack().attackInterval(entity -> 10).attackRadius(25.0F),
                  new AnimatableMeleeAttack(1)
                     .attackInterval(entity -> 5)
                     .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
                     .startCondition(entity -> !entity.usingRangedWeapon())
               }
            )
         }
      );
   }

   public Map<Activity, BrainActivityGroup<? extends GoblinEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends GoblinEntity>>)Util.make(
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
}
