package io.github.manasmods.tensura.item.misc;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.SkillEvents.RemoveSkillEvent;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.intrinsic.AbsorbDissolveSkill;
import io.github.manasmods.tensura.ability.skill.unique.CookSkill;
import io.github.manasmods.tensura.advancement.TensuraAdvancements;
import io.github.manasmods.tensura.config.entity.PlayerConfig;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.entity.variant.BoneGolemVariant;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.network.s2c.DisplayTotemEffectPayload;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ResetScrollItem extends Item {
   public static final List<ResourceLocation> NO_RESET = List.of(Stats.PLAY_TIME, Stats.TOTAL_WORLD_TIME, Stats.DEATHS);
   public static final List<ResourceLocation> RESET_WITH_SKILL = List.of(
      TensuraStats.BATTLEWILL_LEARNT, TensuraStats.BATTLEWILL_MASTERED, TensuraStats.SKILL_LEARNT, TensuraStats.SKILL_MASTERED
   );
   private static final MutableComponent TOOLTIP_SKILL_WARNING = Component.translatable("tooltip.tensura.reset_scroll.skill_warning")
      .withStyle(ChatFormatting.RED);
   private Component cachedTypeTooltip;
   private final ResetScrollItem.ResetType resetType;

   public ResetScrollItem(ResetScrollItem.ResetType resetType) {
      super(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).stacksTo(1));
      this.resetType = resetType;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      if (this.cachedTypeTooltip == null) {
         this.cachedTypeTooltip = Component.translatable(this.getResetType().tooltip).withStyle(ChatFormatting.DARK_AQUA);
      }

      list.add(this.cachedTypeTooltip);
      if (this.getResetType().equals(ResetScrollItem.ResetType.RESET_SKILL)) {
         list.add(Component.literal(""));
         list.add(TOOLTIP_SKILL_WARNING);
      }
   }

   public int getUseDuration(ItemStack pStack, LivingEntity entity) {
      return 10000;
   }

   @NotNull
   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.BOW;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pHand) {
      ItemStack stack = player.getItemInHand(pHand);
      if (player.getCooldowns().isOnCooldown(stack.getItem()) && !player.hasInfiniteMaterials()) {
         return InteractionResultHolder.fail(stack);
      }

      player.startUsingItem(pHand);
      return InteractionResultHolder.consume(stack);
   }

   public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
      if (pRemainingUseDuration % 4 == 0) {
         TensuraParticleHelper.spawnEnchantingTableParticle(pLevel, pLivingEntity.getEyePosition().add(0.0, 0.5, 0.0), ParticleTypes.ENCHANT, 8);
      }
   }

   public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level level, @NotNull LivingEntity entity, int pTimeLeft) {
      if (!level.isClientSide()) {
         int useTicks = this.getUseDuration(pStack, entity) - pTimeLeft;
         if (useTicks >= 10) {
            if (entity instanceof ServerPlayer player) {
               if (entity.isSpectator()) {
                  player.displayClientMessage(Component.translatable("tooltip.tensura.reset_scroll.disable").withStyle(ChatFormatting.RED), false);
                  return;
               }

               if (SkillUtils.inSpiritualWorld(level.dimension()) || level.getBiome(entity.blockPosition()).is(TensuraBiomeTags.IS_UNSAFE_FOR_SPAWN)) {
                  player.displayClientMessage(
                     Component.translatable("tooltip.tensura.reset_scroll.not_safe", new Object[]{pStack.getHoverName()}).withStyle(ChatFormatting.RED), false
                  );
                  return;
               }

               ItemStack copy = pStack.copy();
               if (!player.hasInfiniteMaterials()) {
                  if (this.isBanned()) {
                     player.displayClientMessage(
                        Component.translatable("tensura.item.scroll_not_allowed", new Object[]{pStack.getHoverName()}).withStyle(ChatFormatting.RED), false
                     );
                     return;
                  }

                  player.getCooldowns().addCooldown(pStack.getItem(), 100);
                  pStack.shrink(1);
               }

               ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
               if (isFullReset(player)) {
                  if (this.getResetType().equals(ResetScrollItem.ResetType.RESET_ALL)) {
                     playerData.setResetCounter(playerData.getResetCounter() + 1);
                  } else if (ReincarnationMenu.PLAYER_CONFIG.ResetScroll.counterPenaltyNonCharScroll) {
                     int penalty = level.getGameRules().getInt(TensuraGameRules.RESET_INCOMPLETE_PENALTY);
                     if (penalty > 0) {
                        playerData.setResetCounter(Math.max(0, playerData.getResetCounter() + penalty * -1));
                     }
                  }
               } else {
                  int penalty = level.getGameRules().getInt(TensuraGameRules.RESET_INCOMPLETE_PENALTY);
                  if (penalty > 0) {
                     playerData.setResetCounter(Math.max(0, playerData.getResetCounter() + penalty * -1));
                  }
               }

               playerData.limitLockedSkills(level);
               playerData.markDirty();
               this.getResetType().contextConsumer.accept(player);
               NetworkManager.sendToPlayer(player, new DisplayTotemEffectPayload(copy.getItem().arch$registryName()));
               CriteriaTriggers.CONSUME_ITEM.trigger(player, copy);
               entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 1.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.FLASH, 1.0);
            }
         }
      }
   }

   private boolean isBanned() {
      List<Item> list = ReincarnationMenu.PLAYER_CONFIG
         .ResetScroll
         .resetScrolls
         .stream()
         .map(ResourceLocation::parse)
         .<Item>map(BuiltInRegistries.ITEM::get)
         .filter(Objects::nonNull)
         .toList();
      return !list.contains(this);
   }

   public static void resetLiquidCapacity(Player player) {
      IAbility ability = TensuraStorages.getAbilityFrom(player);
      AttributeInstance water = player.getAttribute(TensuraAttributes.WATER_CAPACITY);
      if (water != null) {
         ability.setWaterPoint(Math.min(water.getValue(), ability.getWaterPoint()));
      }

      AttributeInstance lava = player.getAttribute(TensuraAttributes.LAVA_CAPACITY);
      if (lava != null) {
         ability.setLavaPoint(Math.min(lava.getValue(), ability.getLavaPoint()));
      }

      ability.markDirty();
   }

   public static void resetWarpPoints(Player player) {
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
      if (!data.getWarpPoints().isEmpty()) {
         data.removeWarpPointOverMax(data.getWarpPoints().size() - data.getMaxWarpPoints());
      }

      if (data.getMaxWarpPoints() <= 1) {
         data.clearWarpPads();
      }

      data.markDirty();
   }

   public static void resetFlight(Player player) {
      if ((player.getAbilities().flying || player.getAbilities().mayfly) && !SkillUtils.canFlyLegit(player)) {
         player.getAbilities().flying = false;
         player.getAbilities().mayfly = false;
         player.onUpdateAbilities();
      }
   }

   public static void resetRaceFailsafe(Player player) {
      Races races = RaceAPI.getRaceFrom(player);
      Optional<ManasRaceInstance> optional = races.getRace();
      if (!optional.isEmpty()) {
         races.setRace((ManasRace)TensuraRaces.HUMAN.get(), true);
         if (races instanceof TensuraRace tensuraRace) {
            tensuraRace.resetExistenceData(player);
         }

         races.markDirty();
         IExistence existence = TensuraStorages.getExistenceFrom(player);
         existence.setSpiritualForm(false);
         existence.markDirty();
      }
   }

   public static void resetEverything(ServerPlayer player) {
      MinecraftServer server = player.getServer();
      if (server != null) {
         TensuraStorages.getUniqueStorageFrom(server.overworld()).removeOwner(player.getUUID());
         Skills skills = SkillAPI.getSkillsFrom(player);
         Iterator<ManasSkillInstance> iterator = skills.getLearnedSkills().iterator();

         while (iterator.hasNext()) {
            if (iterator.next() instanceof TensuraSkillInstance instance
               && !((RemoveSkillEvent)SkillEvents.REMOVE_SKILL.invoker()).removeSkill(instance, player, Changeable.of(null)).isFalse()) {
               instance.onForgetSkill(player);
               instance.markDirty();
               iterator.remove();
            }
         }

         skills.markDirty();
         CookSkill.removeCookedHP(player);
         TensuraStorages.resetPlayerData(player);
         TensuraStorages.resetExistence(player);
         TensuraStorages.resetEffect(player);
         TensuraStorages.resetSpirit(player);
         ServerStatsCounter stats = player.getStats();
         stats.markAllDirty();

         for (Stat<?> stat : stats.getDirty()) {
            if (!stat.getType().equals(Stats.CUSTOM) || !NO_RESET.contains((ResourceLocation)stat.getValue())) {
               stats.setValue(player, stat, 0);
            }
         }

         AbsorbDissolveSkill.resetSlimeCoreBoost(player);
         ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
         playerData.clearSchematics();
         playerData.clearReputation();
         playerData.markDirty();
         TensuraAdvancements.revokeAllTensuraAdvancements(player);
         player.setRespawnPosition(Level.OVERWORLD, null, 0.0F, false, false);
         EnergyHelper.removeSpiritualEPLimit(player);
         RaceAPI.getRaceFrom(player).getRace().ifPresent(raceInstance -> BoneGolemVariant.removeBoneGolemFromRace(player, raceInstance));

         for (AttributeInstance attribute : player.getAttributes().attributes.values()) {
            attribute.removeModifier(TensuraRace.DEFAULT_RACE_ID);
         }

         resetRaceFailsafe(player);
         if (player.level().getGameRules().getBoolean(TensuraGameRules.RIMURU_MODE)) {
            ReincarnationMenu.reincarnateAsRimuru(player);
         } else {
            player.setInvulnerable(true);
            playerData.setInResetProgress(1);
            MenuRegistry.openExtendedMenu(player, new SimpleMenuProvider(ReincarnationMenu::new, Component.translatable("tensura.reincarnation")), buf -> {
               buf.writeBoolean(false);
               buf.writeInt(0);
               buf.writeFloat(0.0F);
               buf.writeFloat(0.0F);
            });
            if (player.level().getGameRules().getBoolean(TensuraGameRules.SKILL_BEFORE_RACE)) {
               ReincarnationMenu.grantUniqueSkill(player);
            }
         }

         ReincarnationMenu.grantLearningResistance(player);
         resetFlight(player);
         resetWarpPoints(player);
         resetLiquidCapacity(player);
         player.manasCore$sync(player);
      }
   }

   public static void resetRace(ServerPlayer player) {
      MinecraftServer server = player.getServer();
      if (server != null) {
         ServerStatsCounter stats = player.getStats();
         stats.markAllDirty();

         for (Stat<?> stat : stats.getDirty()) {
            if (!stat.getType().equals(Stats.CUSTOM) || !RESET_WITH_SKILL.contains((ResourceLocation)stat.getValue())) {
               stats.setValue(player, stat, 0);
            }
         }
      }

      Races races = RaceAPI.getRaceFrom(player);
      Optional<ManasRaceInstance> optional = races.getRace();
      if (optional.isPresent()) {
         BoneGolemVariant.removeBoneGolemFromRace(player, optional.get());
         Skills storage = SkillAPI.getSkillsFrom(player);
         Iterator<ManasSkillInstance> iterator = storage.getLearnedSkills().iterator();

         while (iterator.hasNext()) {
            if (iterator.next() instanceof TensuraSkillInstance instance
               && shouldRaceResetRemove(player, instance)
               && !((RemoveSkillEvent)SkillEvents.REMOVE_SKILL.invoker()).removeSkill(instance, player, Changeable.of(null)).isFalse()) {
               instance.onForgetSkill(player);
               instance.markDirty();
               iterator.remove();
            }
         }

         storage.markDirty();
      }

      resetRaceFailsafe(player);
      CookSkill.removeCookedHP(player);
      TensuraStorages.resetPlayerData(player);
      TensuraStorages.resetExistence(player);
      TensuraStorages.resetEffect(player);
      TensuraStorages.resetSpirit(player);
      if (SkillUtils.hasSkill(player, (ManasSkill)UniqueSkills.CHOSEN_ONE.get())) {
         IExistence existence = TensuraStorages.getExistenceFrom(player);
         existence.setBlessed(true);
         existence.markDirty();
      }

      EnergyHelper.removeSpiritualEPLimit(player);
      AbsorbDissolveSkill.resetSlimeCoreBoost(player);
      player.setRespawnPosition(Level.OVERWORLD, null, 0.0F, false, false);

      for (AttributeInstance attribute : player.getAttributes().attributes.values()) {
         attribute.removeModifier(TensuraRace.DEFAULT_RACE_ID);
      }

      TensuraStorages.getPlayerDataFrom(player).setInResetProgress(2);
      MenuRegistry.openExtendedMenu(
         player,
         new SimpleMenuProvider((i, inventory, entity) -> new ReincarnationMenu(i, inventory, player, true), Component.translatable("tensura.reincarnation")),
         buf -> {
            buf.writeBoolean(true);
            buf.writeInt(0);
            buf.writeFloat(0.0F);
            buf.writeFloat(0.0F);
         }
      );
      ReincarnationMenu.grantLearningResistance(player);
      resetFlight(player);
      resetWarpPoints(player);
      resetLiquidCapacity(player);
      player.manasCore$sync(player);
   }

   public static void resetSkill(ServerPlayer player) {
      resetSkill(player, false);
   }

   public static void resetSkill(ServerPlayer player, boolean coverEP) {
      MinecraftServer server = player.getServer();
      if (server != null) {
         TensuraStorages.getUniqueStorageFrom(server.overworld()).removeOwner(player.getUUID());
         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
         if (optional.isPresent()) {
            Skills skills = SkillAPI.getSkillsFrom(player);
            Iterator<ManasSkillInstance> iterator = skills.getLearnedSkills().iterator();

            while (iterator.hasNext()) {
               if (iterator.next() instanceof TensuraSkillInstance instance) {
                  ManasSkill skill = instance.getSkill();
                  if (!optional.get().getObtainedIntrinsicSkills().contains(skill)
                     && !(instance.getSkill() instanceof Magic)
                     && !((RemoveSkillEvent)SkillEvents.REMOVE_SKILL.invoker()).removeSkill(instance, player, Changeable.of(null)).isFalse()) {
                     instance.onForgetSkill(player);
                     instance.markDirty();
                     iterator.remove();
                  }
               }
            }

            skills.markDirty();
            player.manasCore$sync(player);
         }

         for (ResourceLocation toKeep : RESET_WITH_SKILL) {
            player.resetStat(Stats.CUSTOM.get(toKeep));
         }

         TensuraStorages.resetPlayerData(player);
         ReincarnationMenu.randomUniqueSkill(player, coverEP);
         ReincarnationMenu.grantLearningResistance(player);
         resetFlight(player);
         resetWarpPoints(player);
         resetLiquidCapacity(player);
         player.manasCore$sync(player);
      }
   }

   public static boolean isFullReset(ServerPlayer player) {
      PlayerConfig.ResetScroll CONFIG = ReincarnationMenu.PLAYER_CONFIG.ResetScroll;
      if (CONFIG.raceCounter) {
         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
         if (optional.isEmpty()) {
            return false;
         }

         if (!optional.get().getNextEvolutions(player).isEmpty()) {
            return false;
         }
      }

      if (CONFIG.awakenCounter) {
         IExistence existence = TensuraStorages.getExistenceFrom(player);
         if (!existence.isTrueDemonLord() && !existence.isTrueHero()) {
            return false;
         }
      }

      for (String string : CONFIG.bossesCounter) {
         EntityType<?> entityType = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(string));
         if (entityType != null && player.getStats().getValue(((StatType)TensuraStats.BOSS_KILLED.get()).get(entityType)) <= 0) {
            return false;
         }
      }

      return true;
   }

   public static boolean shouldRaceResetRemove(LivingEntity entity, ManasSkillInstance instance) {
      if (instance.isTemporarySkill()) {
         return true;
      }

      if (instance.is(TensuraSkillTags.RESET_WITH_RACE)) {
         return true;
      }

      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
      return optional.isPresent() && optional.get().getObtainedIntrinsicSkills().contains(instance.getSkill())
         ? true
         : instance.is(TensuraSkillTags.INTRINSIC_SKILLS);
   }

   @Generated
   public ResetScrollItem.ResetType getResetType() {
      return this.resetType;
   }

   public enum ResetCounterType {
      STONE(0, Color.WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/reset_medals/stone_reset_medal.png")),
      COPPER(1, Color.WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/reset_medals/copper_reset_medal.png")),
      IRON(2, Color.WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/reset_medals/iron_reset_medal.png")),
      GOLD(3, Color.WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/reset_medals/gold_reset_medal.png")),
      DIAMOND(4, Color.BLACK, ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/reset_medals/diamond_reset_medal.png")),
      MITHRIL(5, Color.WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/reset_medals/mithril_reset_medal.png")),
      ORICHALCUM(6, Color.WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/reset_medals/orichalcum_reset_medal.png")),
      MAGISTEEL(7, Color.WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/reset_medals/magisteel_reset_medal.png")),
      ADAMANTITE(8, Color.WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/reset_medals/adamantite_reset_medal.png")),
      HIHIIROKANE(9, Color.WHITE, ResourceLocation.fromNamespaceAndPath("tensura", "textures/gui/reset_medals/hihiirokane_reset_medal.png"));

      private final int level;
      private final Color textColor;
      private final ResourceLocation textureLocation;

      public static ResetScrollItem.ResetCounterType get(int point) {
         if (point >= 64) {
            return HIHIIROKANE;
         } else if (point >= 50) {
            return ADAMANTITE;
         } else if (point >= 40) {
            return MAGISTEEL;
         } else if (point >= 30) {
            return ORICHALCUM;
         } else if (point >= 20) {
            return MITHRIL;
         } else if (point >= 10) {
            return DIAMOND;
         } else if (point >= 5) {
            return GOLD;
         } else if (point >= 3) {
            return IRON;
         } else {
            return point >= 1 ? COPPER : STONE;
         }
      }

      @Generated
      public int getLevel() {
         return this.level;
      }

      @Generated
      public Color getTextColor() {
         return this.textColor;
      }

      @Generated
      public ResourceLocation getTextureLocation() {
         return this.textureLocation;
      }

      @Generated
      ResetCounterType(final int level, final Color textColor, final ResourceLocation textureLocation) {
         this.level = level;
         this.textColor = textColor;
         this.textureLocation = textureLocation;
      }
   }

   public enum ResetType {
      RESET_RACE(ResetScrollItem::resetRace, "tooltip.tensura.reset_scroll.race"),
      RESET_SKILL(ResetScrollItem::resetSkill, "tooltip.tensura.reset_scroll.skill"),
      RESET_ALL(ResetScrollItem::resetEverything, "tooltip.tensura.reset_scroll.character");

      private final Consumer<ServerPlayer> contextConsumer;
      private final String tooltip;

      @Generated
      ResetType(final Consumer<ServerPlayer> contextConsumer, final String tooltip) {
         this.contextConsumer = contextConsumer;
         this.tooltip = tooltip;
      }
   }
}
