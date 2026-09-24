package dev.xkmc.l2hostility.content.capability.player;

import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkCapHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkCapSyncToClient;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkDifficulty;
import dev.xkmc.l2hostility.content.capability.chunk.SectionDifficulty;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.item.curio.core.CurseCurioItem;
import dev.xkmc.l2hostility.content.item.spawner.TraitSpawnerBlockEntity;
import dev.xkmc.l2hostility.content.logic.DifficultyLevel;
import dev.xkmc.l2hostility.content.logic.LevelEditor;
import dev.xkmc.l2hostility.content.logic.MobDifficultyCollector;
import dev.xkmc.l2hostility.content.logic.TraitManager;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2library.util.GenericItemStack;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

@SerialClass
public class PlayerDifficulty extends PlayerCapabilityTemplate<PlayerDifficulty> {
   @SerialField
   private final DifficultyLevel difficulty = new DifficultyLevel();
   @SerialField
   public int maxRankKilled = 0;
   @SerialField
   public int rewardCount = 0;
   @SerialField
   public final TreeSet<ResourceLocation> dimensions = new TreeSet<>();
   @Nullable
   public ChunkCapHolder prevChunk;

   public void onClone(Player player, boolean isWasDeath) {
      if (isWasDeath) {
         if (!(Boolean)LHConfig.SERVER.keepInventoryRuleKeepDifficulty.get() || !player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            if ((Boolean)LHConfig.SERVER.deathDecayDimension.get()) {
               this.dimensions.clear();
            }

            if ((Boolean)LHConfig.SERVER.deathDecayTraitCap.get() && this.maxRankKilled > 0) {
               this.maxRankKilled--;
            }

            this.difficulty.decay();
         }
      }
   }

   public void tick(Player player) {
      if (player instanceof ServerPlayer sp) {
         Optional<ChunkCapHolder> opt = ChunkDifficulty.at(player.level(), player.blockPosition());
         if (opt.isPresent()) {
            ChunkCapHolder currentChunk = opt.get();
            if (this.prevChunk != currentChunk) {
               L2Hostility.HANDLER.toClientPlayer(ChunkCapSyncToClient.of(currentChunk), sp);
               this.prevChunk = currentChunk;
            }

            SectionDifficulty sec = opt.get().getSection(player.blockPosition().getY());
            if (sec.activePos != null
               && player.level().isLoaded(sec.activePos)
               && player.level().getBlockEntity(sec.activePos) instanceof TraitSpawnerBlockEntity spawner) {
               spawner.track(player);
            }
         }

         if (this.dimensions.add(player.level().dimension().location())) {
            this.sync(sp);
         }
      }
   }

   public void sync(ServerPlayer sp) {
      ((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).network.toClient(sp);
   }

   public void apply(Player player, MobDifficultyCollector instance) {
      instance.setPlayer(player);
      instance.acceptBonus(this.getLevel(player));
      instance.setTraitCap(this.getRankCap());
      if (CurioCompat.hasItemInCurio(player, (Item)LHItems.CURSE_PRIDE.get())) {
         instance.traitCostFactor((Double)LHConfig.SERVER.prideTraitFactor.get());
         instance.setFullChance();
      }

      if (CurioCompat.hasItemInCurio(player, (Item)LHItems.ABYSSAL_THORN.get())) {
         instance.traitCostFactor(0.0);
         instance.setFullChance();
         instance.setFullDrop();
      }
   }

   public int getRankCap() {
      return TraitManager.getTraitCap(this.maxRankKilled, this.difficulty);
   }

   public void addKillCredit(ServerPlayer player, MobTraitCap cap) {
      if (!(player instanceof FakePlayer)) {
         if ((Boolean)LHConfig.SERVER.enableAdaptiveLeveling.get()) {
            double growFactor = 1.0;

            for (GenericItemStack<CurseCurioItem> stack : CurseCurioItem.getFromPlayer(player)) {
               growFactor *= ((CurseCurioItem)stack.item()).getGrowFactor(stack.stack(), this, cap);
            }

            this.difficulty.grow(growFactor, cap);
         }

         cap.traits.values().stream().max(Comparator.naturalOrder()).ifPresent(integer -> this.maxRankKilled = Math.max(this.maxRankKilled, integer));
         if (this.getLevel(player).getLevel() > this.rewardCount * 10 && (Boolean)LHConfig.SERVER.enableHostilityOrbDrop.get()) {
            this.rewardCount++;
            player.getInventory().add(LHItems.HOSTILITY_ORB.asStack());
         }

         this.sync(player);
      }
   }

   public int getRewardCount() {
      return this.rewardCount;
   }

   public DifficultyLevel getLevel(Player player) {
      return DifficultyLevel.merge(this.difficulty, this.getExtraLevel(player));
   }

   private int getDimCount() {
      return Math.max(0, this.dimensions.size() - 1);
   }

   private int getExtraLevel(Player player) {
      int ans = 0;
      ans += this.getDimCount() * LHConfig.SERVER.dimensionFactor.get();
      return ans + (int)player.getAttributeValue(LHMiscs.ADD_LEVEL);
   }

   public List<Component> getPlayerDifficultyDetail(Player player) {
      int item = (int)player.getAttributeValue(LHMiscs.ADD_LEVEL);
      int dim = this.getDimCount() * (Integer)LHConfig.SERVER.dimensionFactor.get();
      return List.of(
         LangData.INFO_PLAYER_ADAPTIVE_LEVEL.get(this.difficulty.level).withStyle(ChatFormatting.GRAY),
         LangData.INFO_PLAYER_ITEM_LEVEL.get(item).withStyle(ChatFormatting.GRAY),
         LangData.INFO_PLAYER_DIM_LEVEL.get(dim).withStyle(ChatFormatting.GRAY),
         LangData.INFO_PLAYER_EXT_LEVEL.get(this.difficulty.extraLevel).withStyle(ChatFormatting.GRAY)
      );
   }

   public LevelEditor getLevelEditor(Player player) {
      return new LevelEditor(this.difficulty, this.getExtraLevel(player));
   }
}
