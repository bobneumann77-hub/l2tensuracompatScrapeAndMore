package io.github.manasmods.tensura.entity.template;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import io.github.manasmods.tensura.advancement.TradeTrigger;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.entity.ai.TensuraVillagerProfessions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import lombok.Generated;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TensuraMerchantEntity extends PlayerLikeEntity implements Merchant, ReputationEventHandler {
   private static final EntityDataAccessor<Integer> MERCHANT_LEVEL = SynchedEntityData.defineId(TensuraMerchantEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<String> PROFESSION = SynchedEntityData.defineId(TensuraMerchantEntity.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<Boolean> WORKING = SynchedEntityData.defineId(TensuraMerchantEntity.class, EntityDataSerializers.BOOLEAN);
   private static final long DAY_TICKS = 24000L;
   private static final long MIN_RESTOCK_GAP = 2400L;
   @Nullable
   private Player tradingPlayer;
   @Nullable
   protected MerchantOffers offers;
   @Nullable
   protected Player lastTradedPlayer;
   @Nullable
   protected Player lastLeveledPlayer;
   protected int updateMerchantTimer;
   protected boolean increaseProfessionLevelOnUpdate;
   private int villagerXp;
   private long lastRestockGameTime;
   private long lastRestockDay = -1L;
   private int numberOfRestocksToday;
   private final GossipContainer gossips = new GossipContainer();
   private long lastGossipTime;
   private long lastGossipDecayTime;
   public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<TensuraMerchantEntity, Holder<PoiType>>> POI_MEMORIES = ImmutableMap.of(
      MemoryModuleType.JOB_SITE,
      (BiPredicate<TensuraMerchantEntity, Holder>)(entity, holder) -> entity.getProfession().acquirableJobSite().test(holder),
      MemoryModuleType.POTENTIAL_JOB_SITE,
      (BiPredicate<TensuraMerchantEntity, Holder>)(entity, holder) -> VillagerProfession.ALL_ACQUIRABLE_JOBS.test(holder)
   );

   public TensuraMerchantEntity(EntityType<? extends PlayerLikeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public boolean isClientSide() {
      return this.level().isClientSide();
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(MERCHANT_LEVEL, 0);
      builder.define(PROFESSION, "minecraft:none");
      builder.define(WORKING, Boolean.TRUE);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("MerchantLevel", this.getMerchantLevel());
      if (this.getProfession() != null) {
         compound.putString("Profession", (String)this.entityData.get(PROFESSION));
      } else {
         compound.putString("Profession", "minecraft:none");
      }

      compound.putBoolean("Working", this.isWorking());
      if (!this.level().isClientSide) {
         MerchantOffers merchantOffers = this.getOffers();
         if (!merchantOffers.isEmpty()) {
            compound.put(
               "Offers", (Tag)MerchantOffers.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), merchantOffers).getOrThrow()
            );
         }
      }

      compound.putInt("Xp", this.villagerXp);
      compound.putLong("LastRestock", this.lastRestockGameTime);
      compound.putLong("LastRestockDay", this.lastRestockDay);
      compound.putInt("RestocksToday", this.numberOfRestocksToday);
      compound.put("Gossips", (Tag)this.gossips.store(NbtOps.INSTANCE));
      compound.putLong("LastGossipDecay", this.lastGossipDecayTime);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      this.setMerchantLevel(compound.getInt("MerchantLevel"));
      if (compound.contains("Profession")) {
         this.setProfession(compound.getString("Profession"));
      } else {
         this.setProfession(VillagerProfession.NONE);
      }

      super.readAdditionalSaveData(compound);
      this.setWorking(compound.getBoolean("Working"));
      if (compound.contains("Offers")) {
         DataResult<MerchantOffers> resulted = MerchantOffers.CODEC
            .parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), compound.get("Offers"));
         resulted.resultOrPartial().ifPresent(merchantOffers -> this.offers = merchantOffers);
      }

      if (compound.contains("Xp", 3)) {
         this.villagerXp = compound.getInt("Xp");
      }

      this.lastRestockGameTime = compound.getLong("LastRestock");
      this.lastRestockDay = compound.getLong("LastRestockDay");
      this.numberOfRestocksToday = compound.getInt("RestocksToday");
      ListTag listTag = compound.getList("Gossips", 10);
      this.gossips.update(new Dynamic(NbtOps.INSTANCE, listTag));
      this.lastGossipDecayTime = compound.getLong("LastGossipDecay");
   }

   public int getMerchantLevel() {
      return (Integer)this.entityData.get(MERCHANT_LEVEL);
   }

   public void setMerchantLevel(int level) {
      this.entityData.set(MERCHANT_LEVEL, level);
   }

   public VillagerProfession getProfession() {
      return (VillagerProfession)BuiltInRegistries.VILLAGER_PROFESSION.get(ResourceLocation.parse((String)this.entityData.get(PROFESSION)));
   }

   public void setProfession(VillagerProfession profession) {
      this.setProfession(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString());
   }

   public void setProfession(String profession) {
      this.entityData.set(PROFESSION, profession);
   }

   public void onSetProfession(VillagerProfession profession) {
      if (profession.workSound() != null) {
         this.makeSound(profession.workSound());
      }
   }

   public boolean isWorking() {
      return (Boolean)this.entityData.get(WORKING);
   }

   public void setWorking(boolean working) {
      this.entityData.set(WORKING, working);
   }

   @Nullable
   public Player getTradingPlayer() {
      return this.tradingPlayer;
   }

   public void setTradingPlayer(@Nullable Player player) {
      boolean nonTrader = this.getTradingPlayer() != null && player == null;
      this.tradingPlayer = player;
      if (nonTrader) {
         this.stopTrading();
      }
   }

   public boolean isTrading() {
      return this.tradingPlayer != null;
   }

   public void overrideXp(int i) {
      this.setVillagerXp(i);
   }

   public void setGossips(Tag tag) {
      this.gossips.update(new Dynamic(NbtOps.INSTANCE, tag));
   }

   @NotNull
   public MerchantOffers getOffers() {
      if (this.level().isClientSide) {
         throw new IllegalStateException("Cannot load Villager offers on the client");
      }

      if (this.offers == null) {
         this.offers = new MerchantOffers();
         this.updateTrades();
      }

      return this.offers;
   }

   public void overrideOffers(MerchantOffers merchantOffers) {
   }

   public boolean showProgressBar() {
      return true;
   }

   public boolean showTopClothes() {
      return this.getProfession() != TensuraVillagerProfessions.ROYAL_GUARD.get();
   }

   public boolean showBottomClothes() {
      return this.getProfession() != TensuraVillagerProfessions.ROYAL_GUARD.get();
   }

   public boolean showBoots() {
      return this.isSleeping() ? false : this.getProfession() != TensuraVillagerProfessions.ROYAL_GUARD.get();
   }

   public Int2ObjectMap<ItemListing[]> getPossibleTrades() {
      return null;
   }

   protected boolean shouldIncreaseLevel() {
      return false;
   }

   protected int getTradesPerLevel(int level) {
      return level == 1 ? 1 : 2;
   }

   protected void increaseMerchantCareer() {
      this.setMerchantLevel(this.getMerchantLevel() + 1);
      this.updateTrades();
   }

   public boolean wantsToTrade() {
      return !this.isSleeping();
   }

   protected boolean shouldCancelTrading(Player player) {
      return this.isBaby() || !this.isAlive() || !this.wantsToTrade();
   }

   protected boolean startTrading(Player player) {
      if (this.shouldCancelTrading(player)) {
         return false;
      }

      if (this.getOffers().isEmpty()) {
         return false;
      }

      this.updateSpecialPrices(player);
      this.setTradingPlayer(player);
      this.openTradingScreen(player, this.getDisplayName(), this.getMerchantLevel());
      return true;
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (this.isTame()) {
         if (this.isOwnedBy(player)) {
            InteractionResult interaction = this.getInventoryInteraction(player, hand);
            if (interaction.consumesAction()) {
               return interaction;
            }

            this.cycleCommands(this, player);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
         } else {
            return InteractionResult.PASS;
         }
      } else {
         return !player.level().isClientSide() && this.startTrading(player) ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
      }
   }

   protected void customServerAiStep() {
      if (!this.isTrading() && this.updateMerchantTimer > 0) {
         this.updateMerchantTimer--;
         if (this.updateMerchantTimer <= 0) {
            if (this.increaseProfessionLevelOnUpdate) {
               this.increaseMerchantCareer();
               this.increaseProfessionLevelOnUpdate = false;
               this.lastLeveledPlayer = null;
            }

            this.onMerchantUpdate();
         }
      }

      if (this.lastTradedPlayer != null && this.level() instanceof ServerLevel level) {
         level.onReputationEvent(ReputationEventType.TRADE, this.lastTradedPlayer, this);
         this.level().broadcastEntityEvent(this, (byte)14);
         this.lastTradedPlayer = null;
      }

      super.customServerAiStep();
   }

   protected void onMerchantUpdate() {
   }

   protected void updateTrades() {
      Int2ObjectMap<ItemListing[]> map = this.getPossibleTrades();
      if (map != null && !map.isEmpty()) {
         int level = this.getMerchantLevel();
         if (level <= map.size()) {
            ItemListing[] trades = (ItemListing[])map.get(level);
            if (trades != null) {
               this.addOffersFromItemListings(this.getOffers(), trades, this.getTradesPerLevel(level));
            }
         }
      }
   }

   protected void addOffersFromItemListings(MerchantOffers merchantOffers, ItemListing[] itemListings, int i) {
      ArrayList<ItemListing> list = Lists.newArrayList(itemListings);
      int j = 0;

      while (j < i && !list.isEmpty()) {
         MerchantOffer merchantOffer = list.remove(this.random.nextInt(list.size())).getOffer(this, this.random);
         if (merchantOffer != null) {
            merchantOffers.add(merchantOffer);
            j++;
         }
      }
   }

   protected void updateSpecialPrices(Player player) {
      int i = this.getPlayerReputation(player);
      if (i != 0) {
         for (MerchantOffer offer : this.getOffers()) {
            offer.addToSpecialPriceDiff(-Mth.floor(i * offer.getPriceMultiplier()));
         }
      }

      MobEffectInstance hero = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
      if (hero != null) {
         int j = hero.getAmplifier();

         for (MerchantOffer offer : this.getOffers()) {
            if (!(offer.getPriceMultiplier() <= 0.0F)) {
               double multiplier = 0.05 + 0.05 * j;
               int k = (int)Math.floor(multiplier * offer.getBaseCostA().getCount());
               offer.addToSpecialPriceDiff(-Math.max(k, 1));
            }
         }
      }
   }

   public void notifyTrade(MerchantOffer merchantOffer) {
      if (merchantOffer.getXp() != 0 || merchantOffer.getPriceMultiplier() != 0.0F || merchantOffer.getMaxUses() != 1024) {
         merchantOffer.increaseUses();
      }

      this.ambientSoundTime = -this.getAmbientSoundInterval();
      this.rewardTradeXp(merchantOffer);
      if (this.tradingPlayer instanceof ServerPlayer player) {
         ((TradeTrigger)TensuraCriteriaTriggers.TRADE.get()).trigger(player, this, merchantOffer.getResult());
      }
   }

   public void notifyTradeUpdated(ItemStack itemStack) {
      if (!this.level().isClientSide() && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
         this.ambientSoundTime = -this.getAmbientSoundInterval();
         this.makeSound(this.getTradeUpdatedSound(!itemStack.isEmpty()));
      }
   }

   protected void rewardTradeXp(MerchantOffer merchantOffer) {
      int i = 3 + this.random.nextInt(4);
      this.villagerXp = this.villagerXp + merchantOffer.getXp();
      this.lastTradedPlayer = this.getTradingPlayer();
      if (this.shouldIncreaseLevel()) {
         this.updateMerchantTimer = 40;
         this.increaseProfessionLevelOnUpdate = true;
         this.lastLeveledPlayer = this.getTradingPlayer();
         i += 5;
      }

      if (merchantOffer.shouldRewardExp() && merchantOffer.getXp() > 0) {
         this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), i));
      }
   }

   public boolean canRestock() {
      return true;
   }

   public int getStartWorkTime() {
      return 2000;
   }

   public int getEndWorkTime() {
      return 12000;
   }

   public int getFallbackRestockTime() {
      return 11000;
   }

   public boolean isInWorkHours() {
      long time = this.level().getDayTime() % 24000L;
      return time >= this.getStartWorkTime() && time <= this.getEndWorkTime();
   }

   private void maybeResetForNewDay() {
      long currentDay = this.level().getDayTime() / 24000L;
      if (currentDay != this.lastRestockDay) {
         this.lastRestockDay = currentDay;
         this.resetNumberOfRestocks();
         this.lastRestockGameTime = 0L;
      }
   }

   protected boolean allowedToRestock() {
      if (!this.isInWorkHours()) {
         return false;
      } else if (this.numberOfRestocksToday == 0) {
         return true;
      } else {
         return this.numberOfRestocksToday < 2 ? this.level().getGameTime() - this.lastRestockGameTime >= 2400L : false;
      }
   }

   public boolean shouldRestock() {
      this.maybeResetForNewDay();
      long dayTime = this.level().getDayTime();
      long time = dayTime % 24000L;
      if (time >= this.getFallbackRestockTime() && time <= this.getEndWorkTime() && this.numberOfRestocksToday == 0) {
         this.lastRestockGameTime = this.level().getGameTime();
         return true;
      } else {
         return this.allowedToRestock() && this.needsToRestock();
      }
   }

   protected boolean needsToRestock() {
      for (MerchantOffer merchantOffer : this.getOffers()) {
         if (merchantOffer.needsRestock()) {
            return true;
         }
      }

      return false;
   }

   public void restock() {
      this.updateDemand();

      for (MerchantOffer merchantOffer : this.getOffers()) {
         merchantOffer.resetUses();
      }

      this.resendOffersToTradingPlayer();
      this.lastRestockGameTime = this.level().getGameTime();
      this.numberOfRestocksToday++;
   }

   public void restockIfPossible() {
      if (this.shouldRestock()) {
         this.restock();
      }
   }

   protected void resendOffersToTradingPlayer() {
      MerchantOffers merchantOffers = this.getOffers();
      Player player = this.getTradingPlayer();
      if (player != null && !merchantOffers.isEmpty()) {
         player.sendMerchantOffers(player.containerMenu.containerId, merchantOffers, 1, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
      }
   }

   protected void resetNumberOfRestocks() {
      this.catchUpDemand();
      this.numberOfRestocksToday = 0;
   }

   protected void catchUpDemand() {
      int i = 2 - this.numberOfRestocksToday;
      if (i > 0) {
         for (MerchantOffer merchantOffer : this.getOffers()) {
            merchantOffer.resetUses();
         }
      }

      for (int j = 0; j < i; j++) {
         this.updateDemand();
      }

      this.resendOffersToTradingPlayer();
   }

   protected void updateDemand() {
      for (MerchantOffer merchantOffer : this.getOffers()) {
         merchantOffer.updateDemand();
      }
   }

   protected void stopTrading() {
      this.setTradingPlayer(null);
      this.resetSpecialPrices();
   }

   protected void resetSpecialPrices() {
      if (!this.level().isClientSide()) {
         for (MerchantOffer merchantOffer : this.getOffers()) {
            merchantOffer.resetSpecialPriceDiff();
         }
      }
   }

   @Nullable
   public Entity changeDimension(DimensionTransition dimensionTransition) {
      this.stopTrading();
      return super.changeDimension(dimensionTransition);
   }

   public int getPlayerReputation(Player player) {
      return this.gossips.getReputation(player.getUUID(), gossipType -> true);
   }

   public void gossip(TensuraMerchantEntity villager, long l) {
      if ((l < this.lastGossipTime || l >= this.lastGossipTime + 1200L) && (l < villager.lastGossipTime || l >= villager.lastGossipTime + 1200L)) {
         this.gossips.transferFrom(villager.gossips, this.random, 10);
         this.lastGossipTime = l;
         villager.lastGossipTime = l;
      }
   }

   @Override
   public void tick() {
      super.tick();
      this.maybeDecayGossip();
   }

   protected void maybeDecayGossip() {
      long l = this.level().getGameTime();
      if (this.lastGossipDecayTime == 0L) {
         this.lastGossipDecayTime = l;
      } else if (l >= this.lastGossipDecayTime + 24000L) {
         this.gossips.decay();
         this.lastGossipDecayTime = l;
      }
   }

   public void onReputationEventFrom(ReputationEventType reputationEventType, Entity entity) {
      if (reputationEventType == ReputationEventType.TRADE) {
         this.gossips.add(entity.getUUID(), GossipType.TRADING, 2);
      } else if (reputationEventType == ReputationEventType.VILLAGER_HURT) {
         this.gossips.add(entity.getUUID(), GossipType.MINOR_NEGATIVE, 25);
      } else if (reputationEventType == ReputationEventType.VILLAGER_KILLED) {
         this.gossips.add(entity.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
      }
   }

   protected void onReputationHurt(ServerLevel level, LivingEntity attacker) {
      if (!attacker.hasInfiniteMaterials()) {
         level.onReputationEvent(ReputationEventType.VILLAGER_HURT, attacker, this);
         if (this.isAlive() && attacker instanceof Player) {
            this.level().broadcastEntityEvent(this, (byte)13);
         }
      }
   }

   protected void onReputationKill(ServerLevel level, Player attacker) {
      if (!attacker.hasInfiniteMaterials()) {
         NearestVisibleLivingEntities entities = (NearestVisibleLivingEntities)BrainUtils.getMemory(this, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
         if (entities != null) {
            entities.findAll(ReputationEventHandler.class::isInstance)
               .forEach(living -> level.onReputationEvent(ReputationEventType.VILLAGER_KILLED, attacker, (ReputationEventHandler)living));
         }
      }
   }

   @Override
   public void setLastHurtByMob(@Nullable LivingEntity attacker) {
      if (attacker != null && this.level() instanceof ServerLevel level) {
         this.onReputationHurt(level, attacker);
      }

      super.setLastHurtByMob(attacker);
   }

   public void die(DamageSource damageSource) {
      this.releaseAllPois();
      if (damageSource.getEntity() instanceof Player attacker && this.level() instanceof ServerLevel level) {
         this.onReputationKill(level, attacker);
      }

      super.die(damageSource);
      this.stopTrading();
   }

   private void releaseAllPois() {
      TensuraBehaviourHelper.releaseHome(this);
      this.releasePoi(MemoryModuleType.JOB_SITE);
      this.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
   }

   public void releasePoi(MemoryModuleType<GlobalPos> memoryModuleType) {
      if (this.level() instanceof ServerLevel level) {
         MinecraftServer var9 = level.getServer();
         GlobalPos globalPos = (GlobalPos)BrainUtils.getMemory(this, memoryModuleType);
         if (globalPos != null) {
            ServerLevel serverLevel = var9.getLevel(globalPos.dimension());
            if (serverLevel != null) {
               PoiManager poiManager = serverLevel.getPoiManager();
               Optional<Holder<PoiType>> optional = poiManager.getType(globalPos.pos());
               BiPredicate<TensuraMerchantEntity, Holder<PoiType>> biPredicate = POI_MEMORIES.get(memoryModuleType);
               if (optional.isPresent() && biPredicate.test(this, optional.get())) {
                  poiManager.release(globalPos.pos());
                  DebugPackets.sendPoiTicketCountPacket(serverLevel, globalPos.pos());
               }
            }
         }
      }
   }

   @NotNull
   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }

   protected SoundEvent getTradeUpdatedSound(boolean success) {
      return success ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
   }

   @Generated
   public void setOffers(@Nullable MerchantOffers offers) {
      this.offers = offers;
   }

   @Generated
   public void setVillagerXp(int villagerXp) {
      this.villagerXp = villagerXp;
   }

   @Generated
   public int getVillagerXp() {
      return this.villagerXp;
   }

   @Generated
   public GossipContainer getGossips() {
      return this.gossips;
   }
}
