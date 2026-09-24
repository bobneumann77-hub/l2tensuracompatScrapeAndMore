package io.github.manasmods.tensura.entity.human;

import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.config.entity.EntityConfig;
import io.github.manasmods.tensura.config.entity.PlayerConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.ProfessionBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.InteractDoor;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.InteractWithEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.VillagerLikeBreed;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.path.MerchantFollowTrader;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.TradeWithMerchants;
import io.github.manasmods.tensura.entity.ai.sensor.NearbyTreeSensor;
import io.github.manasmods.tensura.entity.ai.sensor.NearbyWantedItemSensor;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.merchant.profession.DwarfProfession;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraMerchantEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IGender;
import io.github.manasmods.tensura.entity.variant.DwarfVariant;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.ai.TensuraVillagerProfessions;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.world.TensuraGameRules;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowParent;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
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

public class DwarfEntity extends TensuraMerchantEntity implements SmartBrainOwner<DwarfEntity>, IGender {
   public static PlayerConfig.Reputation REPUTATION_CONFIG = ReincarnationMenu.PLAYER_CONFIG.Reputation;
   private static final EntityDataAccessor<Integer> GENDER = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SKIN = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> FACE = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SCAR = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HAIR = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> FACIAL_HAIR = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HAIR_COLOR = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TOP = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TOP_COLOR = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BOTTOM = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BOTTOM_COLOR = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> FEET = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> FEET_COLOR = SynchedEntityData.defineId(DwarfEntity.class, EntityDataSerializers.INT);
   private int lastReputationUpdate = 0;
   private SmartBrainSchedule schedule;
   private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(2, 1, 2);

   public DwarfEntity(EntityType<? extends DwarfEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 1.5)
         .add(Attributes.MAX_HEALTH, 24.0)
         .add(Attributes.ATTACK_SPEED, 4.5)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.02F)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.SCALE, 1.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(GENDER, 0);
      builder.define(SKIN, 0);
      builder.define(FACE, 0);
      builder.define(SCAR, 0);
      builder.define(HAIR, 0);
      builder.define(FACIAL_HAIR, -1);
      builder.define(HAIR_COLOR, -1);
      builder.define(TOP, 0);
      builder.define(TOP_COLOR, -1);
      builder.define(BOTTOM, 0);
      builder.define(BOTTOM_COLOR, -1);
      builder.define(FEET, 0);
      builder.define(FEET_COLOR, -1);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      TensuraBehaviourHelper.saveGlobalPos(this, compound, MemoryModuleType.JOB_SITE, "JobSite");
      TensuraBehaviourHelper.saveGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
      compound.putInt("Gender", (Integer)this.entityData.get(GENDER));
      compound.putInt("Skin", (Integer)this.entityData.get(SKIN));
      compound.putInt("Face", (Integer)this.entityData.get(FACE));
      compound.putInt("Scar", (Integer)this.entityData.get(SCAR));
      compound.putInt("Hair", (Integer)this.entityData.get(HAIR));
      compound.putInt("FacialHair", (Integer)this.entityData.get(FACIAL_HAIR));
      compound.putInt("HairColor", (Integer)this.entityData.get(HAIR_COLOR));
      compound.putInt("Top", (Integer)this.entityData.get(TOP));
      compound.putInt("TopColor", (Integer)this.entityData.get(TOP_COLOR));
      compound.putInt("Bottom", (Integer)this.entityData.get(BOTTOM));
      compound.putInt("BottomColor", (Integer)this.entityData.get(BOTTOM_COLOR));
      compound.putInt("Feet", (Integer)this.entityData.get(FEET));
      compound.putInt("FeetColor", (Integer)this.entityData.get(FEET_COLOR));
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      TensuraBehaviourHelper.readGlobalPos(this, compound, MemoryModuleType.JOB_SITE, "JobSite");
      TensuraBehaviourHelper.readGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
      this.entityData.set(GENDER, compound.getInt("Gender"));
      this.entityData.set(SKIN, compound.getInt("Skin"));
      this.entityData.set(FACE, compound.getInt("Face"));
      this.entityData.set(SCAR, compound.getInt("Scar"));
      this.entityData.set(HAIR, compound.getInt("Hair"));
      this.entityData.set(FACIAL_HAIR, compound.getInt("FacialHair"));
      this.entityData.set(HAIR_COLOR, compound.getInt("HairColor"));
      this.entityData.set(TOP, compound.getInt("Top"));
      this.entityData.set(TOP_COLOR, compound.getInt("TopColor"));
      this.entityData.set(BOTTOM, compound.getInt("Bottom"));
      this.entityData.set(BOTTOM_COLOR, compound.getInt("BottomColor"));
      this.entityData.set(FEET, compound.getInt("Feet"));
      this.entityData.set(FEET_COLOR, compound.getInt("FeetColor"));
   }

   public DwarfVariant.Gender getGender() {
      return DwarfVariant.Gender.byId((Integer)this.entityData.get(GENDER));
   }

   public void setGender(int gender) {
      this.entityData.set(GENDER, gender);
   }

   @Override
   public boolean isMale() {
      return this.getGender() == DwarfVariant.Gender.MALE;
   }

   @Override
   public boolean isFemale() {
      return this.getGender() == DwarfVariant.Gender.FEMALE;
   }

   public DwarfVariant.Skin getSkin() {
      return DwarfVariant.Skin.byId((Integer)this.entityData.get(SKIN));
   }

   public void setSkin(int skin) {
      this.entityData.set(SKIN, skin);
   }

   public DwarfVariant.Face getFace() {
      return DwarfVariant.Face.byId((Integer)this.entityData.get(FACE));
   }

   public void setFace(int face) {
      this.entityData.set(FACE, face);
   }

   public int getScar() {
      return (Integer)this.entityData.get(SCAR);
   }

   public void setScar(int i) {
      this.entityData.set(SCAR, i);
   }

   public DwarfVariant.Hair getHair() {
      return DwarfVariant.Hair.byId((Integer)this.entityData.get(HAIR));
   }

   public void setHair(int hair) {
      this.entityData.set(HAIR, hair);
   }

   public DwarfVariant.FacialHair getFacialHair() {
      return DwarfVariant.FacialHair.byId((Integer)this.entityData.get(FACIAL_HAIR));
   }

   public void setFacialHair(int head) {
      this.entityData.set(FACIAL_HAIR, head);
   }

   public int getHairColor() {
      return (Integer)this.entityData.get(HAIR_COLOR);
   }

   public void setHairColor(int i) {
      this.entityData.set(HAIR_COLOR, i);
   }

   public DwarfVariant.Top getTop() {
      return DwarfVariant.Top.byId((Integer)this.entityData.get(TOP));
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

   public DwarfVariant.Bottom getBottom() {
      return DwarfVariant.Bottom.byId((Integer)this.entityData.get(BOTTOM));
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

   public DwarfVariant.Feet getFeet() {
      return DwarfVariant.Feet.byId((Integer)this.entityData.get(FEET));
   }

   public void setFeet(int feet) {
      this.entityData.set(FEET, feet);
   }

   public int getFeetColor() {
      return (Integer)this.entityData.get(FEET_COLOR);
   }

   public void setFeetColor(int i) {
      this.entityData.set(FEET_COLOR, i);
   }

   @Override
   public boolean canMate(Animal animal) {
      if (animal == this) {
         return false;
      } else {
         return animal.getClass() != this.getClass() ? false : ((DwarfEntity)animal).getGender() != this.getGender();
      }
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      DwarfEntity baby = (DwarfEntity)((EntityType)HumanEntityTypes.DWARF.get()).create(pLevel);
      if (baby == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         baby.setOwnerUUID(uuid);
         baby.setTame(true, true);
      }

      baby.applyRandomName();
      baby.applyRandomVariant(true);
      baby.setSkin(DwarfVariant.Skin.getBreed(this.getSkin(), ((DwarfEntity)pOtherParent).getSkin(), this.getRandom()));
      baby.setHairColor(TensuraColors.getMixedColor(this.getHairColor(), ((DwarfEntity)pOtherParent).getHairColor()));
      return baby;
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   @Override
   public boolean wantsToTrade() {
      return this.isSleeping() ? false : this.isWorking();
   }

   @Override
   protected int getTradesPerLevel(int level) {
      return this.getProfession().equals(TensuraVillagerProfessions.MERCHANT.get()) ? 6 : 2;
   }

   @Override
   protected boolean shouldIncreaseLevel() {
      int i = this.getMerchantLevel();
      return VillagerData.canLevelUp(i) && this.getVillagerXp() >= VillagerData.getMaxXpPerLevel(i);
   }

   @Override
   public Int2ObjectMap<ItemListing[]> getPossibleTrades() {
      return DwarfProfession.getProfessionTrades(this.getProfession());
   }

   @Override
   protected boolean shouldCancelTrading(Player player) {
      if (super.shouldCancelTrading(player)) {
         this.makeSound(this.getTradeUpdatedSound(false));
         return true;
      }

      if (!this.getProfession().equals(VillagerProfession.NONE) && !this.getProfession().equals(TensuraVillagerProfessions.ROYAL_GUARD.get())) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
         double rep = data.getReputation(this.getType());
         if (rep <= REPUTATION_CONFIG.hostileReputation) {
            player.displayClientMessage(
               Component.translatable("tensura.message.npc.refuse_trading", new Object[]{this.getName()}).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
               true
            );
            this.makeSound(this.getTradeUpdatedSound(false));
            return true;
         }

         if (this.getMerchantLevel() < 1) {
            this.increaseMerchantCareer();
         }

         return false;
      } else {
         this.makeSound(this.getTradeUpdatedSound(false));
         return true;
      }
   }

   @Override
   protected void onMerchantUpdate() {
      this.playSound(SoundEvents.VILLAGER_CELEBRATE, 1.0F, 1.0F);
   }

   @Override
   public void onSetProfession(VillagerProfession profession) {
      super.onSetProfession(profession);
      if (this.shouldDoButchering()) {
         if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), ((Item)TensuraToolItems.IRON_SHORT_SWORD.get()).getDefaultInstance());
         } else {
            this.inventory.addItem(((Item)TensuraToolItems.IRON_SHORT_SWORD.get()).getDefaultInstance());
         }

         this.inventory.addItem(new ItemStack(Items.WHEAT, 16));
         this.inventory.addItem(new ItemStack(Items.CARROT, 16));
         this.inventory.addItem(new ItemStack(Items.WHEAT_SEEDS, 16));
         this.updateContainerEquipment();
      } else if (this.shouldDoFarming()) {
         if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), Items.IRON_HOE.getDefaultInstance());
         } else {
            this.inventory.addItem(Items.IRON_HOE.getDefaultInstance());
         }

         this.inventory.addItem(new ItemStack(Items.WHEAT_SEEDS, 8));
         this.inventory.addItem(new ItemStack(Items.CARROT, 4));
         this.inventory.addItem(new ItemStack(Items.POTATO, 4));
         this.updateContainerEquipment();
      } else if (this.shouldDoFishing()) {
         if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), Items.FISHING_ROD.getDefaultInstance());
         } else {
            this.inventory.addItem(Items.FISHING_ROD.getDefaultInstance());
         }

         this.updateContainerEquipment();
      } else if (this.shouldDoLumberjack()) {
         if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), Items.IRON_AXE.getDefaultInstance());
         } else {
            this.inventory.addItem(Items.IRON_AXE.getDefaultInstance());
         }

         this.inventory.addItem(new ItemStack(Items.OAK_SAPLING, 4));
         this.inventory.addItem(new ItemStack(Items.BIRCH_SAPLING, 4));
         this.updateContainerEquipment();
      } else if (this.shouldDoShepherd()) {
         if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), Items.SHEARS.getDefaultInstance());
         } else {
            this.inventory.addItem(Items.SHEARS.getDefaultInstance());
         }

         this.inventory.addItem(new ItemStack(Items.WHEAT, 32));
         this.updateContainerEquipment();
      } else if (this.getProfession() == TensuraVillagerProfessions.GUARD.get()) {
         ItemStack stack = this.getRandom().nextBoolean()
            ? Items.IRON_SWORD.getDefaultInstance()
            : ((Item)TensuraToolItems.IRON_SPEAR.get()).getDefaultInstance();
         if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
         } else {
            this.inventory.addItem(stack);
         }

         AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
         if (armor != null) {
            armor.setBaseValue(armor.getBaseValue() + 10.0);
         }

         this.updateContainerEquipment();
      } else if (this.getProfession() == VillagerProfession.FLETCHER) {
         if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), Items.BOW.getDefaultInstance());
         } else {
            this.inventory.addItem(Items.BOW.getDefaultInstance());
         }

         this.inventory.addItem(new ItemStack(Items.ARROW, 32));
         this.updateContainerEquipment();
      }
   }

   @Override
   public boolean shouldDoButchering() {
      return this.getProfession() == VillagerProfession.BUTCHER;
   }

   @Override
   public boolean shouldDoFarming() {
      return this.getProfession() == VillagerProfession.FARMER;
   }

   @Override
   public boolean shouldDoFishing() {
      return this.getProfession() == VillagerProfession.FISHERMAN;
   }

   @Override
   public boolean shouldDoGuarding() {
      return this.getProfession() == TensuraVillagerProfessions.GUARD.get() || this.getProfession() == TensuraVillagerProfessions.ROYAL_GUARD.get();
   }

   @Override
   public boolean shouldDoLumberjack() {
      return this.getProfession() == TensuraVillagerProfessions.LUMBERJACK.get();
   }

   @Override
   public boolean shouldDoShepherd() {
      return this.getProfession() == VillagerProfession.SHEPHERD;
   }

   @NotNull
   protected Vec3i getPickupReach() {
      return ITEM_PICKUP_REACH;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         boolean working = this.getBrain().isActive(Activity.WORK) || this.getBrain().isActive(Activity.FIGHT) && this.isInWorkHours();
         if (working != this.isWorking()) {
            this.setWorking(working);
         }
      }
   }

   @Override
   public InteractionResult onHipokuteHeal(Player player, InteractionHand hand, ItemStack stack) {
      InteractionResult result = stack.interactLivingEntity(player, this, hand);
      if (this.getScar() != 0 && result.consumesAction()) {
         this.setScar(0);
      }

      return result;
   }

   @Override
   protected void rewardTradeXp(MerchantOffer merchantOffer) {
      super.rewardTradeXp(merchantOffer);
      if (this.lastTradedPlayer != null && merchantOffer.shouldRewardExp() && merchantOffer.getXp() > 0) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(this.lastTradedPlayer);
         double reputation = data.getReputation(this.getType());
         if (reputation <= REPUTATION_CONFIG.minReputation) {
            return;
         }

         double point = !merchantOffer.getCostA().is(TensuraItemTags.COINS) && !merchantOffer.getCostB().is(TensuraItemTags.COINS)
            ? REPUTATION_CONFIG.sellingTradePoint
            : REPUTATION_CONFIG.buyingTradePoint;
         data.setReputation(this.getType(), Math.min(reputation + point, REPUTATION_CONFIG.maxReputation));
         data.markDirty();
      }
   }

   @Override
   protected void updateSpecialPrices(Player player) {
      super.updateSpecialPrices(player);
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
      double rep = data.getReputation(this.getType());
      if (rep != 0.0) {
         double point = rep < 0.0 ? REPUTATION_CONFIG.chargePercentage * rep : REPUTATION_CONFIG.discountPercentage * rep;

         for (MerchantOffer offer : this.getOffers()) {
            if (offer.getPriceMultiplier() != 0.0F) {
               int price = (int)Math.floor(point * offer.getBaseCostA().getCount());
               offer.addToSpecialPriceDiff(-price);
            }
         }
      }
   }

   @Override
   protected void increaseMerchantCareer() {
      super.increaseMerchantCareer();
      if (this.lastLeveledPlayer != null) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(this.lastLeveledPlayer);
         double reputation = data.getReputation(this.getType());
         if (reputation <= REPUTATION_CONFIG.minReputation) {
            return;
         }

         data.setReputation(this.getType(), Math.min(reputation + REPUTATION_CONFIG.levelTradePoint, REPUTATION_CONFIG.maxReputation));
         data.markDirty();
      }
   }

   @Override
   protected void onReputationHurt(ServerLevel level, LivingEntity attacker) {
      if (!attacker.hasInfiniteMaterials()) {
         level.onReputationEvent(ReputationEventType.VILLAGER_HURT, attacker, this);
         if (this.isAlive() && attacker instanceof Player) {
            this.level().broadcastEntityEvent(this, (byte)13);
            if (this.lastReputationUpdate >= this.tickCount) {
               return;
            }

            if (this.isOwnedBy(attacker)) {
               return;
            }

            NearestVisibleLivingEntities entities = (NearestVisibleLivingEntities)BrainUtils.getMemory(this, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
            if (entities == null) {
               return;
            }

            if (entities.findClosest(entity -> entity instanceof DwarfEntity dwarf && !dwarf.isTame()).isEmpty()) {
               return;
            }

            this.lastReputationUpdate = this.tickCount;
            ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(attacker);
            double reputation = data.getReputation(this.getType()) - REPUTATION_CONFIG.hurtLostPoint;
            data.setReputation(this.getType(), Math.max(reputation, REPUTATION_CONFIG.minReputation));
            data.markDirty();
         }
      }
   }

   @Override
   protected void onReputationKill(ServerLevel level, Player attacker) {
      if (!attacker.hasInfiniteMaterials()) {
         NearestVisibleLivingEntities entities = (NearestVisibleLivingEntities)BrainUtils.getMemory(this, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
         if (entities != null) {
            entities.findAll(ReputationEventHandler.class::isInstance)
               .forEach(living -> level.onReputationEvent(ReputationEventType.VILLAGER_KILLED, attacker, (ReputationEventHandler)living));
            if (this.lastReputationUpdate < this.tickCount) {
               if (!entities.findClosest(entity -> entity instanceof DwarfEntity dwarf && !dwarf.isTame()).isEmpty()) {
                  this.lastReputationUpdate = this.tickCount;
                  ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(attacker);
                  double reputation = data.getReputation(this.getType()) - REPUTATION_CONFIG.killLostPoint;
                  data.setReputation(this.getType(), Math.max(reputation, REPUTATION_CONFIG.minReputation));
                  data.markDirty();
               }
            }
         }
      }
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         this.applyRandomName();
         this.applyRandomVariant(false);
         if (this.getProfession().equals(TensuraVillagerProfessions.ROYAL_GUARD.get())) {
            AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
            if (armor != null) {
               armor.setBaseValue(armor.getBaseValue() + 15.0);
            }

            ItemStack stack = this.getRandom().nextBoolean() ? new ItemStack(Items.IRON_SWORD) : new ItemStack((ItemLike)TensuraToolItems.IRON_SPEAR.get());
            this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
            if (this.getRandom().nextBoolean()) {
               this.inventory.setItem(this.getSlotId(EquipmentSlot.OFFHAND), Items.SHIELD.getDefaultInstance());
            }

            this.updateContainerEquipment();
         }
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   private void applyRandomName() {
      List<String> names = TensuraBehaviourHelper.CONFIG.Dwarf.dwarfNames;
      this.setCustomName(Component.literal(names.get(this.getRandom().nextInt(names.size()))));
   }

   private void applyRandomVariant(boolean breeding) {
      RandomSource source = this.getRandom();
      EntityConfig.Dwarf config = TensuraBehaviourHelper.CONFIG.Dwarf;
      this.setGender(source.nextBoolean() ? 0 : 1);
      this.setFace(DwarfVariant.Face.getRandom(this.getGender(), this));
      if (!breeding) {
         this.setSkin(DwarfVariant.Skin.getRandom(this));
         this.setScar(source.nextFloat() <= config.scarChance ? source.nextInt(2) : 0);
      }

      this.setHair(DwarfVariant.Hair.getRandom(this.getGender(), this));
      if (this.getGender().equals(DwarfVariant.Gender.MALE)) {
         this.setFacialHair(DwarfVariant.FacialHair.getRandom(this));
      }

      if (!breeding) {
         List<Integer> hairs = config.dwarfHairColors;
         this.setHairColor(hairs.get(source.nextInt(hairs.size())));
      }

      List<Integer> tops = config.dwarfTopClothesColors;
      this.setTop(DwarfVariant.Top.getRandom(this.getGender(), this));
      this.setTopColor(tops.get(source.nextInt(tops.size())));
      List<Integer> bottoms = config.dwarfBottomClothesColors;
      this.setBottom(DwarfVariant.Bottom.getRandom(this));
      this.setBottomColor(bottoms.get(source.nextInt(bottoms.size())));
      List<Integer> boots = config.dwarfBootsColors;
      this.setFeet(DwarfVariant.Feet.getRandom(this));
      this.setFeetColor(boots.get(source.nextInt(boots.size())));
      AttributeInstance scale = this.getAttribute(Attributes.SCALE);
      if (scale != null) {
         if (this.getProfession().equals(TensuraVillagerProfessions.ROYAL_GUARD.get())) {
            scale.setBaseValue(1.0);
         } else {
            scale.setBaseValue(0.7 + Math.pow(source.nextDouble(), 3.0) * 0.3);
         }
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.VILLAGER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.VILLAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VILLAGER_DEATH;
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

   public List<ExtendedSensor<DwarfEntity>> getSensors() {
      return ObjectArrayList.of(
         new ExtendedSensor[]{
            new NearbyLivingEntitySensor(),
            new HurtBySensor(),
            new SleepSensor(),
            new NearbyTreeSensor().shouldScan(dwarf -> dwarf.shouldDoLumberjack() && dwarf.level().getGameRules().getBoolean(TensuraGameRules.NPC_WORKING)),
            new NearbyWantedItemSensor()
               .shouldScan(
                  entity -> entity.shouldAlwaysPickUpItem() && entity.isWorking() && entity.level().getGameRules().getBoolean(TensuraGameRules.NPC_WORKING)
               )
               .setPredicate(NearbyWantedItemSensor.getProfessionItemPredicate())
         }
      );
   }

   public BrainActivityGroup<DwarfEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new WakeUp().onWakeUp(TensuraMerchantEntity::restockIfPossible).stopIf(entity -> {
         if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
            entity.stopSleeping();
            return true;
         } else {
            return false;
         }
      }), new LookAtTarget(), new FloatToSurfaceOfFluid(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<DwarfEntity> getIdleTasks() {
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
                  new TradeWithMerchants(),
                  new FollowParent().startCondition(entity -> !entity.isTame() || entity.isWandering()),
                  new MerchantFollowTrader(),
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldTarget),
                  new SubordinateFollowOwner(),
                  ProfessionBehaviourHelper.getDepositItemsBehaviours(),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new InteractDoor(),
            new HumanoidConsumeItem().startCondition(PlayerLikeEntity::shouldHeal).stopIf(entity -> !entity.shouldHeal()),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{
                     new FirstApplicableBehaviour(
                        new ExtendedBehaviour[]{
                           new InteractWithEntity(this.getType(), MemoryModuleType.INTERACTION_TARGET)
                              .startCondition(entity -> entity.getRandom().nextInt(7) == 1),
                           TensuraBehaviourHelper.setWanderAroundHome()
                        }
                     ),
                     new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
                  }
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<DwarfEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new AllApplicableBehaviours(
                  new ExtendedBehaviour[]{
                     new StrafeTarget().stopStrafingWhen(entity -> !entity.usingRangedWeapon()).startCondition(PlayerLikeEntity::usingRangedWeapon),
                     new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 1.5F).startCondition(entity -> !entity.usingRangedWeapon()),
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
               )
               .startCondition(entity -> {
                  if (entity.getProfession() == VillagerProfession.NONE || entity.shouldDoGuarding()) {
                     return true;
                  } else {
                     return !entity.shouldDoButchering() && !entity.shouldDoShepherd() ? false : entity.getTarget() != null;
                  }
               })
         }
      );
   }

   public boolean shouldTarget(LivingEntity target) {
      if (!this.shouldDoGuarding()) {
         return false;
      } else if (target instanceof Mob mob && mob.getTarget() instanceof DwarfEntity) {
         return true;
      } else {
         return target instanceof Player player
               && TensuraStorages.getPlayerDataFrom(player).getReputation(this.getType()) <= REPUTATION_CONFIG.hostileReputation
            ? true
            : target.getType().is(TensuraEntityTags.GUARD_PREY);
      }
   }

   public Map<Activity, BrainActivityGroup<? extends DwarfEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends DwarfEntity>>)Util.make(new Object2ObjectOpenHashMap(), map -> {
         map.put(Activity.REST, TensuraBehaviourHelper.getHumanoidSleepActivityGroup(this));
         map.put(Activity.WORK, ProfessionBehaviourHelper.getWorkingActivityGroup(this));
      });
   }

   public SmartBrainSchedule getSchedule() {
      if (this.schedule == null) {
         this.schedule = new SmartBrainSchedule()
            .activityAt(10, Activity.IDLE)
            .activityAt(this.getStartWorkTime(), Activity.WORK)
            .activityAt(this.getEndWorkTime(), Activity.PLAY)
            .activityAt(13000, Activity.REST);
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
