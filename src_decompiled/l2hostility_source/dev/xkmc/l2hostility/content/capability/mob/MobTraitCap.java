package dev.xkmc.l2hostility.content.capability.mob;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityTemplate;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkCapHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkDifficulty;
import dev.xkmc.l2hostility.content.capability.chunk.RegionalDifficultyModifier;
import dev.xkmc.l2hostility.content.capability.player.PlayerDifficulty;
import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2hostility.content.config.WorldDifficultyConfig;
import dev.xkmc.l2hostility.content.item.spawner.TraitSpawnerBlockEntity;
import dev.xkmc.l2hostility.content.logic.InheritContext;
import dev.xkmc.l2hostility.content.logic.ItemPopulator;
import dev.xkmc.l2hostility.content.logic.MobDifficultyCollector;
import dev.xkmc.l2hostility.content.logic.PlayerFinder;
import dev.xkmc.l2hostility.content.logic.TraitManager;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.events.ClientEvents;
import dev.xkmc.l2hostility.events.HostilityInitEvent;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.advancements.HostilityTriggers;
import dev.xkmc.l2hostility.init.advancements.KillTraitCountTrigger;
import dev.xkmc.l2hostility.init.advancements.KillTraitEffectTrigger;
import dev.xkmc.l2hostility.init.advancements.KillTraitFlameTrigger;
import dev.xkmc.l2hostility.init.advancements.KillTraitLevelTrigger;
import dev.xkmc.l2hostility.init.advancements.KillTraitsTrigger;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

@SerialClass
public class MobTraitCap extends GeneralCapabilityTemplate<LivingEntity, MobTraitCap> {
   @SerialField
   public final LinkedHashMap<MobTrait, Integer> traits = new LinkedHashMap<>();
   @SerialField
   private MobTraitCap.Stage stage = MobTraitCap.Stage.PRE_INIT;
   @SerialField
   public int lv;
   @SerialField(toClient = false)
   private final HashMap<ResourceLocation, CapStorageData> data = new HashMap<>();
   @SerialField
   public boolean summoned = false;
   @SerialField
   public boolean minion = false;
   @SerialField
   public boolean noDrop = false;
   @SerialField
   public boolean fullDrop = false;
   @SerialField
   public boolean copied = false;
   @SerialField
   public double dropRate = 1.0;
   @Nullable
   @SerialField
   public BlockPos pos = null;
   @Nullable
   @SerialField
   public MinionData asMinion = null;
   @Nullable
   @SerialField
   public MasterData asMaster = null;
   @Nullable
   private TraitSpawnerBlockEntity summoner = null;
   private boolean inherited = false;
   private boolean ticking = false;
   private EntityConfig.Config configCache = null;
   private final ArrayList<Pair<MobTrait, Integer>> pending = new ArrayList<>();

   public void syncToClient(LivingEntity entity) {
      L2Hostility.HANDLER.toTrackingPlayers(MobCapSyncToClient.of(entity, this), entity);
   }

   public void syncToPlayer(LivingEntity entity, ServerPlayer player) {
      L2Hostility.HANDLER.toClientPlayer(MobCapSyncToClient.of(entity, this), player);
   }

   public void deinit() {
      this.copied = false;
      this.traits.clear();
      this.lv = 0;
      this.stage = MobTraitCap.Stage.PRE_INIT;
   }

   public boolean reinit(LivingEntity mob, int level, boolean max) {
      this.deinit();
      this.init(mob.level(), mob, (pos, ins) -> {
         ins.base = level;
         if (max) {
            ins.setFullChance();
         }
      });
      return true;
   }

   @Nullable
   public EntityConfig.Config getConfigCache(LivingEntity le) {
      if (this.configCache == null) {
         this.configCache = ((EntityConfig)L2Hostility.ENTITY.getMerged()).get(le.getType());
      }

      if (this.configCache == null && le.level() instanceof ServerLevel sl) {
         this.configCache = ((WorldDifficultyConfig)L2Hostility.DIFFICULTY.getMerged()).get(sl, le.blockPosition(), le.getType());
      }

      if (this.configCache == null) {
         this.configCache = ((WorldDifficultyConfig)L2Hostility.DIFFICULTY.getMerged()).get(le.level().dimension().location(), le.getType());
      }

      return this.configCache;
   }

   public void setConfigCache(EntityConfig.Config config) {
      this.configCache = config;
   }

   public void init(Level level, LivingEntity le, RegionalDifficultyModifier difficulty) {
      boolean skip = !(Boolean)LHConfig.SERVER.allowNoAI.get() && le instanceof Mob mob && mob.isNoAi();
      MobDifficultyCollector instance = new MobDifficultyCollector();
      EntityConfig.Config diff = this.getConfigCache(le);
      if (diff != null) {
         instance.acceptConfig(diff.difficulty());
      }

      difficulty.modifyInstance(le.blockPosition(), instance);
      Player player = PlayerFinder.getNearestPlayer(level, le);
      if (player != null && ((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).isProper(player)) {
         PlayerDifficulty playerDiff = (PlayerDifficulty)((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).getOrCreate(player);
         playerDiff.apply(player, instance);
         if (!(Boolean)LHConfig.SERVER.allowPlayerAllies.get() && le.isAlliedTo(player)) {
            skip = true;
         }
      }

      if (!skip) {
         skip = ((HostilityInitEvent.Pre)NeoForge.EVENT_BUS.post(new HostilityInitEvent.Pre(le, this, HostilityInitEvent.InitPhase.INIT))).isCanceled();
      }

      this.lv = skip ? 0 : TraitManager.fill(this, le, this.traits, instance);
      this.fullDrop = instance.isFullDrop();
      NeoForge.EVENT_BUS.post(new HostilityInitEvent.Post(le, this, HostilityInitEvent.InitPhase.INIT));
      this.stage = MobTraitCap.Stage.INIT;
      this.syncToClient(le);
   }

   public void copyFrom(LivingEntity par, LivingEntity child, MobTraitCap parent) {
      this.deinit();
      InheritContext ctx = new InheritContext(par, parent, child, this, !parent.inherited);
      parent.inherited = true;
      this.lv = parent.lv;
      this.summoned = parent.summoned;
      this.minion = parent.minion;
      this.noDrop = parent.noDrop;
      this.dropRate = parent.dropRate * (Double)LHConfig.SERVER.splitDropRateFactor.get();
      if (!((HostilityInitEvent.Pre)NeoForge.EVENT_BUS.post(new HostilityInitEvent.Pre(child, this, HostilityInitEvent.InitPhase.COPY))).isCanceled()) {
         for (Entry<MobTrait, Integer> ent : parent.traits.entrySet()) {
            int rank = ent.getKey().inherited(this, ent.getValue(), ctx);
            if (rank > 0) {
               this.traits.put(ent.getKey(), rank);
            }
         }
      }

      TraitManager.fill(this, child, this.traits, MobDifficultyCollector.noTrait(this.lv));
      NeoForge.EVENT_BUS.post(new HostilityInitEvent.Post(child, this, HostilityInitEvent.InitPhase.COPY));
      this.copied = true;
      this.stage = MobTraitCap.Stage.INIT;
   }

   public int getLevel() {
      return this.lv;
   }

   public void setLevel(LivingEntity le, int level) {
      this.lv = this.clampLevel(le, level);
      TraitManager.scale(this, le, this.lv);
   }

   public int clampLevel(LivingEntity le, int lv) {
      int cap = (Integer)LHConfig.SERVER.maxMobLevel.get();
      EntityConfig.Config config = this.getConfigCache(le);
      if (config != null && config.maxLevel > 0) {
         cap = Math.min(config.maxLevel, cap);
      }

      return Math.min(cap, lv);
   }

   public boolean isInitialized() {
      return this.stage != MobTraitCap.Stage.PRE_INIT;
   }

   public int getTraitLevel(MobTrait trait) {
      return this.traits.getOrDefault(trait, 0);
   }

   public boolean hasTrait(MobTrait trait) {
      return this.getTraitLevel(trait) > 0;
   }

   public void traitEvent(BiConsumer<MobTrait, Integer> cons) {
      this.traits.forEach(cons);
   }

   public void setTrait(MobTrait trait, int lv) {
      this.pending.add(Pair.of(trait, lv));
   }

   public void removeTrait(MobTrait trait) {
      if (this.traits.containsKey(trait)) {
         if (this.ticking) {
            this.setTrait(trait, 0);
         } else {
            this.traits.remove(trait);
         }
      }
   }

   private boolean clearPending(LivingEntity mob) {
      if (this.pending.isEmpty()) {
         return false;
      }

      while (!this.pending.isEmpty()) {
         ArrayList<Pair<MobTrait, Integer>> temp = new ArrayList<>(this.pending);

         for (Pair<MobTrait, Integer> pair : this.pending) {
            this.traits.put((MobTrait)pair.getFirst(), (Integer)pair.getSecond());
         }

         this.pending.clear();

         for (Pair<MobTrait, Integer> pair : temp) {
            ((MobTrait)pair.getFirst()).initialize(mob, (Integer)pair.getSecond());
            ((MobTrait)pair.getFirst()).postInit(mob, (Integer)pair.getSecond());
         }

         for (Pair<MobTrait, Integer> pair : temp) {
            if ((Integer)pair.getSecond() == 0) {
               this.traits.remove(pair.getFirst());
            }
         }
      }

      return true;
   }

   public void tick(LivingEntity mob) {
      boolean sync = false;
      this.ticking = true;
      if (!mob.level().isClientSide()) {
         if (!this.isInitialized()) {
            Optional<ChunkCapHolder> opt = ChunkDifficulty.at(mob.level(), mob.blockPosition());
            opt.ifPresent(h -> this.init(mob.level(), mob, h));
         }

         if (this.stage == MobTraitCap.Stage.INIT) {
            this.stage = MobTraitCap.Stage.POST_INIT;
            if (!((HostilityInitEvent.Pre)NeoForge.EVENT_BUS.post(new HostilityInitEvent.Pre(mob, this, HostilityInitEvent.InitPhase.WEAPON))).isCanceled()) {
               ItemPopulator.postFill(this, mob);
               NeoForge.EVENT_BUS.post(new HostilityInitEvent.Post(mob, this, HostilityInitEvent.InitPhase.WEAPON));
            }

            this.traits.forEach((k, v) -> k.postInit(mob, v));
            this.clearPending(mob);
            mob.setHealth(mob.getMaxHealth());
            sync = true;
         }

         if (!this.traits.isEmpty()) {
            boolean owned = mob instanceof OwnableEntity own && own.getOwner() instanceof Player;
            boolean dumb = mob instanceof Mob m && m.isNoAi();
            if (!(Boolean)LHConfig.SERVER.allowTraitOnOwnable.get() && owned || !(Boolean)LHConfig.SERVER.allowNoAI.get() && dumb) {
               this.traits.clear();
               sync = true;
            }
         }
      }

      if (this.isInitialized()) {
         if (!this.traits.isEmpty()) {
            if (mob.tickCount % PerformanceConstants.removeTraitInterval() == 0) {
               sync |= this.traits.keySet().removeIf(Objects::isNull);
               sync |= this.traits.keySet().removeIf(MobTrait::isBanned);
            }

            this.traits.forEach((k, v) -> k.tick(mob, v));
         }

         sync |= this.clearPending(mob);
      }

      if (!mob.level().isClientSide() && this.pos != null) {
         if (this.summoner == null && mob.level().getBlockEntity(this.pos) instanceof TraitSpawnerBlockEntity be) {
            this.summoner = be;
         }

         if (this.summoner == null || this.summoner.isRemoved()) {
            mob.discard();
         }
      }

      if (this.asMinion != null) {
         sync |= this.asMinion.tick(mob);
      }

      if (this.hasTrait((MobTrait)LHTraits.MASTER.get())) {
         if (!mob.level().isClientSide() && this.asMaster == null) {
            this.asMaster = new MasterData();
            sync = true;
         }

         if (mob instanceof Mob master && this.asMaster != null) {
            sync |= this.asMaster.tick(this, master);
         }

         if (mob.level().isClientSide()) {
            if (mob instanceof Mob m) {
               ClientEvents.MASTERS.add(m);
            }

            mob.addTag("HostilityGlowing");
         }
      }

      if (this.summoned && mob.level().isClientSide()) {
         mob.addTag("HostilityGlowing");
      }

      if (!mob.level().isClientSide() && sync && !mob.isRemoved()) {
         this.syncToClient(mob);
      }

      this.ticking = false;
   }

   public boolean shouldDiscard(LivingEntity mob) {
      EntityConfig.Config config = this.getConfigCache(mob);
      return config != null && config.minSpawnLevel > 0 ? this.lv < config.minSpawnLevel : false;
   }

   public void onKilled(LivingEntity mob, @Nullable Player player) {
      if (this.summoner != null && !this.summoner.isRemoved()) {
         this.summoner.data.onDeath(mob);
      }

      if (player instanceof ServerPlayer sp) {
         ((KillTraitLevelTrigger)HostilityTriggers.TRAIT_LEVEL.get()).trigger(sp, this);
         ((KillTraitCountTrigger)HostilityTriggers.TRAIT_COUNT.get()).trigger(sp, this);
         ((KillTraitsTrigger)HostilityTriggers.KILL_TRAITS.get()).trigger(sp, this);
         ((KillTraitFlameTrigger)HostilityTriggers.TRAIT_FLAME.get()).trigger(sp, mob, this);
         ((KillTraitEffectTrigger)HostilityTriggers.TRAIT_EFFECT.get()).trigger(sp, mob, this);
      }
   }

   public boolean isSummoned() {
      return this.summoned || this.minion;
   }

   public boolean isMasterProtected() {
      if (this.asMaster != null) {
         for (MasterData.Minion e : this.asMaster.data) {
            if (e.minion != null) {
               Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(e.minion);
               if (opt.isPresent()) {
                  MobTraitCap scap = opt.get();
                  if (scap.asMinion != null && scap.asMinion.protectMaster) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   @Nullable
   public <T extends CapStorageData> T getData(ResourceLocation id) {
      return (T)Wrappers.cast(this.data.get(id));
   }

   public <T extends CapStorageData> T getOrCreateData(ResourceLocation id, Supplier<T> sup) {
      return (T)Wrappers.cast(this.data.computeIfAbsent(id, e -> sup.get()));
   }

   public List<Component> getTitle(boolean showLevel, boolean showTrait) {
      List<Component> ans = new ArrayList<>();
      if (showLevel && this.lv > 0) {
         ans.add(
            LangData.LV
               .get(this.lv)
               .withStyle(
                  Style.EMPTY
                     .withColor(this.fullDrop ? (Integer)LHConfig.CLIENT.overHeadLevelColorAbyss.get() : (Integer)LHConfig.CLIENT.overHeadLevelColor.get())
               )
         );
      }

      if (!showTrait) {
         return ans;
      }

      MutableComponent temp = null;
      int count = 0;

      for (Entry<MobTrait, Integer> e : this.traits.entrySet()) {
         MutableComponent comp = e.getKey().getFullDesc(e.getValue());
         if (temp == null) {
            temp = comp;
            count = 1;
         } else {
            temp.append(Component.literal(" / ").withStyle(ChatFormatting.WHITE)).append(comp);
            if (++count >= 3) {
               ans.add(temp);
               count = 0;
               temp = null;
            }
         }
      }

      if (count > 0) {
         ans.add(temp);
      }

      return ans;
   }

   public enum Stage {
      PRE_INIT,
      INIT,
      POST_INIT;
   }
}
