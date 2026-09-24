package io.github.manasmods.tensura.neoforge.data.advancement;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.advancement.AbilityTrigger;
import io.github.manasmods.tensura.advancement.ExistenceGainTrigger;
import io.github.manasmods.tensura.advancement.ItemUsedOnEntityTrigger;
import io.github.manasmods.tensura.advancement.TensuraAdvancements;
import io.github.manasmods.tensura.advancement.TradeTrigger;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.loot.TensuraAdvancementLoot;
import io.github.manasmods.tensura.data.template.critereon.ExistencePointPredicate;
import io.github.manasmods.tensura.data.template.critereon.SlimePredicate;
import io.github.manasmods.tensura.data.template.critereon.TamedPredicate;
import io.github.manasmods.tensura.data.template.critereon.VariantPredicate;
import io.github.manasmods.tensura.entity.variant.DaemonVariant;
import io.github.manasmods.tensura.entity.variant.DirewolfVariant;
import io.github.manasmods.tensura.item.misc.MagicTomeItem;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import io.github.manasmods.tensura.registry.item.TensuraSpawnEggs;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.world.TensuraBiomes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import lombok.Generated;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.ImpossibleTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TensuraAdvancementGenerator implements AdvancementGenerator {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(TensuraAdvancementGenerator.class);
   public static Criterion<?> IMPOSSIBLE = CriteriaTriggers.IMPOSSIBLE.createCriterion(new TriggerInstance());
   private static final Item[] EDIBLE_ITEMS = new Item[]{
      (Item)TensuraConsumableItems.DUBIOUS_FOOD.get(),
      (Item)TensuraConsumableItems.RAW_BLADE_TIGER_MEAT.get(),
      (Item)TensuraConsumableItems.BLADE_TIGER_STEAK.get(),
      (Item)TensuraConsumableItems.RAW_ARMORSAURUS_MEAT.get(),
      (Item)TensuraConsumableItems.COOKED_ARMORSAURUS_MEAT.get(),
      (Item)TensuraConsumableItems.CHILLED_SLIME.get(),
      (Item)TensuraConsumableItems.BUCKET_OF_CATTLEDEER_MILK.get(),
      (Item)TensuraConsumableItems.CATTLEDEER_BEEF.get(),
      (Item)TensuraConsumableItems.CATTLEDEER_STEAK.get(),
      (Item)TensuraConsumableItems.RAW_CHARYBDIS_MEAT.get(),
      (Item)TensuraConsumableItems.COOKED_CHARYBDIS_MEAT.get(),
      (Item)TensuraConsumableItems.GIANT_ANT_LEG.get(),
      (Item)TensuraConsumableItems.COOKED_GIANT_ANT_LEG.get(),
      (Item)TensuraConsumableItems.RAW_GIANT_BAT_MEAT.get(),
      (Item)TensuraConsumableItems.COOKED_GIANT_BAT_MEAT.get(),
      (Item)TensuraConsumableItems.KNIGHT_SPIDER_LEG.get(),
      (Item)TensuraConsumableItems.COOKED_KNIGHT_SPIDER_LEG.get(),
      (Item)TensuraConsumableItems.RAW_MEGALODON_MEAT.get(),
      (Item)TensuraConsumableItems.COOKED_MEGALODON_MEAT.get(),
      (Item)TensuraConsumableItems.RAW_SERPENT_MEAT.get(),
      (Item)TensuraConsumableItems.COOKED_SERPENT_MEAT.get(),
      (Item)TensuraConsumableItems.RAW_SPEAR_TORO_MEAT.get(),
      (Item)TensuraConsumableItems.COOKED_SPEAR_TORO_MEAT.get(),
      (Item)TensuraConsumableItems.SPEAR_TORO_FIN.get(),
      (Item)TensuraConsumableItems.COOKED_SPEAR_TORO_FIN.get(),
      (Item)TensuraConsumableItems.RAW_SISSIE_MEAT.get(),
      (Item)TensuraConsumableItems.COOKED_SISSIE_MEAT.get(),
      (Item)TensuraConsumableItems.SISSIE_FIN.get(),
      (Item)TensuraConsumableItems.COOKED_SISSIE_FIN.get(),
      (Item)TensuraConsumableItems.SILVER_APPLE.get(),
      (Item)TensuraConsumableItems.ENCHANTED_SILVER_APPLE.get(),
      (Item)TensuraMobDropItems.DAEMON_ESSENCE.get(),
      (Item)TensuraMobDropItems.DRAGON_ESSENCE.get(),
      (Item)TensuraMobDropItems.ELEMENTAL_ESSENCE.get(),
      (Item)TensuraMobDropItems.ROYAL_BLOOD.get(),
      (Item)TensuraMobDropItems.ZANE_BLOOD.get()
   };
   private static final ResourceKey<Biome>[] TENSURA_BIOMES = new ResourceKey[]{
      TensuraBiomes.ANCIENT_FOREST, TensuraBiomes.BARREN_LAND, TensuraBiomes.DESERT_OF_DEATH, TensuraBiomes.MIASMIC_PLAINS
   };
   private static final ResourceKey<Biome>[] HELL_BIOMES = new ResourceKey[]{
      TensuraBiomes.UNDERWORLD_BARRENS, TensuraBiomes.UNDERWORLD_SPIKES, TensuraBiomes.UNDERWORLD_SANDS, TensuraBiomes.UNDERWORLD_RED_SANDS
   };

   public void generateBasic(@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper helper) {
      AdvancementHolder REINCARNATED = this.addRoot(
         consumer,
         helper,
         TensuraAdvancements.Basic.REINCARNATED,
         (ItemLike)TensuraMaterialItems.RACE_RESET_SCROLL.get(),
         ResourceLocation.fromNamespaceAndPath("tensura", "textures/block/dark_labyrinth_stone.png"),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of(
            "reincarnation",
            ((PlayerTrigger)TensuraCriteriaTriggers.REINCARNATED.get())
               .createCriterion(new net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance(Optional.empty()))
         )
      );
      AdvancementHolder EMPOWERMENT = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.EMPOWERMENT,
         REINCARNATED,
         (ItemLike)TensuraMobDropItems.SLIME_CHUNK.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         Strategy.OR,
         Pair.of("ep", this.playerKilledEntityTrigger(ExistencePointPredicate.getDefault(1.0)))
      );
      AdvancementHolder D_RANK = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.D_RANK,
         EMPOWERMENT,
         (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_NUGGET.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         null,
         Pair.of("ep", ExistenceGainTrigger.TriggerInstance.gainExistencePoint(1000.0))
      );
      AdvancementHolder C_RANK = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.C_RANK,
         D_RANK,
         (ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_NUGGET.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         null,
         Pair.of("ep", ExistenceGainTrigger.TriggerInstance.gainExistencePoint(3000.0))
      );
      AdvancementHolder B_RANK = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.B_RANK,
         C_RANK,
         (ItemLike)TensuraMaterialItems.MITHRIL_NUGGET.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         null,
         Pair.of("ep", ExistenceGainTrigger.TriggerInstance.gainExistencePoint(6000.0))
      );
      AdvancementHolder A_RANK = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.A_RANK,
         B_RANK,
         (ItemLike)TensuraMaterialItems.ORICHALCUM_NUGGET.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("ep", ExistenceGainTrigger.TriggerInstance.gainExistencePoint(10000.0))
      );
      AdvancementHolder SA_RANK = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.SA_RANK,
         A_RANK,
         (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_NUGGET.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("ep", ExistenceGainTrigger.TriggerInstance.gainExistencePoint(100000.0))
      );
      AdvancementHolder S_RANK = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.S_RANK,
         SA_RANK,
         (ItemLike)TensuraMaterialItems.ADAMANTITE_NUGGET.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         null,
         Pair.of("ep", ExistenceGainTrigger.TriggerInstance.gainExistencePoint(400000.0))
      );
      AdvancementHolder SS_RANK = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.SS_RANK,
         S_RANK,
         (ItemLike)TensuraMaterialItems.HIHIIROKANE_NUGGET.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(30),
         null,
         Pair.of("ep", ExistenceGainTrigger.TriggerInstance.gainExistencePoint(800000.0))
      );
      AdvancementHolder GROWTH_SPURT = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.GROWTH_SPURT,
         D_RANK,
         (ItemLike)TensuraMobDropItems.ROYAL_BLOOD.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of(
            "evolve",
            ((PlayerTrigger)TensuraCriteriaTriggers.EVOLVE_RACE.get())
               .createCriterion(new net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance(Optional.empty()))
         )
      );
      AdvancementHolder INFAMY_FAMOUS = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.INFAMY_FAMOUS,
         GROWTH_SPURT,
         (ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(20),
         null
      );
      AdvancementHolder HIGHER_FORM = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.HIGHER_FORM,
         S_RANK,
         (ItemLike)TensuraMobDropItems.DRAGON_ESSENCE.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(30),
         null,
         Pair.of(
            "awaken",
            ((PlayerTrigger)TensuraCriteriaTriggers.AWAKEN_RACE.get())
               .createCriterion(new net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance(Optional.empty()))
         )
      );
      AdvancementHolder ACQUIRE_SILVERWARE = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.ACQUIRE_SILVERWARE,
         REINCARNATED,
         (ItemLike)TensuraMaterialItems.SILVER_INGOT.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         this.createReward(TensuraAdvancementLoot.ACQUIRE_SILVERWARE),
         null,
         Pair.of("silver", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.SILVER_INGOT.get()))
      );
      AdvancementHolder MAGIC_ORE = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.MAGIC_ORE,
         ACQUIRE_SILVERWARE,
         (ItemLike)TensuraMaterialItems.MAGIC_ORE.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("magic_ore", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.MAGIC_ORE.get()))
      );
      AdvancementHolder LOW_MAGISTEEL = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.LOW_MAGISTEEL,
         MAGIC_ORE,
         (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         this.createReward(TensuraAdvancementLoot.LOW_MAGISTEEL),
         null,
         Pair.of("low_magisteel", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get()))
      );
      AdvancementHolder HIGH_MAGISTEEL = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.HIGH_MAGISTEEL,
         LOW_MAGISTEEL,
         (ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         this.createReward(TensuraAdvancementLoot.HIGH_MAGISTEEL),
         null,
         Pair.of("high_magisteel", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get()))
      );
      AdvancementHolder MITHRIL = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.MITHRIL,
         HIGH_MAGISTEEL,
         (ItemLike)TensuraMaterialItems.MITHRIL_INGOT.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         this.createReward(TensuraAdvancementLoot.MITHRIL),
         null,
         Pair.of("mithril", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.MITHRIL_INGOT.get()))
      );
      AdvancementHolder ORICHALCUM = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.ORICHALCUM,
         MITHRIL,
         (ItemLike)TensuraMaterialItems.ORICHALCUM_INGOT.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         this.createReward(TensuraAdvancementLoot.ORICHALCUM),
         null,
         Pair.of("orichalcum", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.ORICHALCUM_INGOT.get()))
      );
      AdvancementHolder PURE_MAGISTEEL = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.PURE_MAGISTEEL,
         ORICHALCUM,
         (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         false,
         this.createReward(TensuraAdvancementLoot.PURE_MAGISTEEL),
         null,
         Pair.of("pure_magisteel", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()))
      );
      AdvancementHolder ADAMANTITE = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.ADAMANTITE,
         PURE_MAGISTEEL,
         (ItemLike)TensuraMaterialItems.ADAMANTITE_INGOT.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(TensuraAdvancementLoot.ADAMANTITE),
         Strategy.OR,
         Pair.of("items", this.obtainItemTrigger(TensuraItemTags.ADAMANTITE_ITEMS))
      );
      AdvancementHolder HIHIIROKANE = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.HIHIIROKANE,
         ADAMANTITE,
         (ItemLike)TensuraMaterialItems.HIHIIROKANE_INGOT.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(TensuraAdvancementLoot.HIHIIROKANE),
         Strategy.OR,
         Pair.of("items", this.obtainItemTrigger(TensuraItemTags.HIHIIROKANE_ITEMS))
      );
      AdvancementHolder LABYRINTH = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.LABYRINTH,
         REINCARNATED,
         (ItemLike)TensuraBlocks.LABYRINTH_BRICKS.get(),
         AdvancementType.GOAL,
         true,
         true,
         true,
         null,
         Pair.of("labyrinth", this.changeDimensionTrigger(TensuraDimensions.LABYRINTH))
      );
      AdvancementHolder JUST_A_TEST = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.JUST_A_TEST,
         LABYRINTH,
         (ItemLike)TensuraBlocks.LABYRINTH_STONE.get(),
         AdvancementType.TASK,
         true,
         true,
         true,
         null,
         Pair.of(
            "colossus",
            ((PlayerTrigger)TensuraCriteriaTriggers.DIED_TO_COLOSSUS.get())
               .createCriterion(new net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance(Optional.empty()))
         )
      );
      AdvancementHolder SPIRIT_PROTECTOR = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.SPIRIT_PROTECTOR,
         JUST_A_TEST,
         (ItemLike)TensuraBlocks.PURE_MAGISTEEL_BLOCK.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(10),
         null,
         Pair.of("colossus", this.playerKilledEntityTrigger((EntityType<?>)MonsterEntityTypes.ELEMENTAL_COLOSSUS.get()))
      );
      AdvancementHolder ELEMENTALIST = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.ELEMENTALIST,
         JUST_A_TEST,
         (ItemLike)TensuraBlocks.LABYRINTH_PRAYING_PATH.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of(
            "spirit",
            ((PlayerTrigger)TensuraCriteriaTriggers.SPIRIT_CONTRACTED.get())
               .createCriterion(new net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance(Optional.empty()))
         )
      );
      AdvancementHolder BLESSED_ONE = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.BLESSED_ONE,
         ELEMENTALIST,
         (ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(50),
         null,
         Pair.of(
            "bless",
            ((PlayerTrigger)TensuraCriteriaTriggers.SPIRIT_BLESSED.get())
               .createCriterion(new net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance(Optional.empty()))
         )
      );
      AdvancementHolder INFINITY_CORES = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.INFINITY_CORES,
         ELEMENTALIST,
         (ItemLike)TensuraMaterialItems.ELEMENT_CORE_EMPTY.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(20),
         null,
         Pair.of(
            "slotting",
            ((PlayerTrigger)TensuraCriteriaTriggers.MAX_SLOTTING_USED.get())
               .createCriterion(new net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance(Optional.empty()))
         )
      );
      AdvancementHolder MAGIC_SEEDY_PLACE = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.MAGIC_SEEDY_PLACE,
         REINCARNATED,
         (ItemLike)TensuraMaterialItems.HIPOKUTE_SEEDS.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         null,
         Pair.of("seed", net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.placedBlock((Block)TensuraBlocks.HIPOKUTE_GRASS.get()))
      );
      AdvancementHolder HIPOKUTE_FLOWER = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.HIPOKUTE_FLOWER,
         MAGIC_SEEDY_PLACE,
         (ItemLike)TensuraMaterialItems.HIPOKUTE_FLOWER.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("flower", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.HIPOKUTE_FLOWER.get()))
      );
      AdvancementHolder GOOD_AS_NEW = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.GOOD_AS_NEW,
         HIPOKUTE_FLOWER,
         (ItemLike)TensuraConsumableItems.FULL_POTION.get(),
         AdvancementType.GOAL,
         true,
         true,
         true,
         null,
         Pair.of("potion", this.consumeTrigger((ItemLike)TensuraConsumableItems.FULL_POTION.get()))
      );
      AdvancementHolder DELIGHTFUL_TRADE = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.DELIGHTFUL_TRADE,
         REINCARNATED,
         (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         null,
         Pair.of("trade", TradeTrigger.TriggerInstance.traded(Builder.item().of(TensuraItemTags.COINS).build()))
      );
      AdvancementHolder MY_PRECIOUS = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.MY_PRECIOUS,
         DELIGHTFUL_TRADE,
         (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("coin", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.GOLD_COIN.get()))
      );
      AdvancementHolder MILLION_DOLLAR = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.MILLION_DOLLAR,
         MY_PRECIOUS,
         (ItemLike)TensuraMaterialItems.STELLAR_GOLD_COIN.get(),
         AdvancementType.GOAL,
         true,
         true,
         true,
         null,
         Pair.of("coin", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.STELLAR_GOLD_COIN.get()))
      );
      AdvancementHolder REWIND_TIME = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.REWIND_TIME,
         REINCARNATED,
         (ItemLike)TensuraMaterialItems.CHARACTER_RESET_SCROLL.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         Strategy.OR,
         Pair.of("race", this.consumeTrigger((ItemLike)TensuraMaterialItems.RACE_RESET_SCROLL.get())),
         Pair.of("skill", this.consumeTrigger((ItemLike)TensuraMaterialItems.SKILL_RESET_SCROLL.get())),
         Pair.of("character", this.consumeTrigger((ItemLike)TensuraMaterialItems.CHARACTER_RESET_SCROLL.get()))
      );
   }

   public void generateAdventure(@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper helper) {
      AdvancementHolder REAL_ADVENTURE = this.addRoot(
         consumer,
         helper,
         TensuraAdvancements.Adventure.REAL_ADVENTURE,
         (ItemLike)TensuraMaterialItems.SPATIAL_BAG.get(),
         ResourceLocation.fromNamespaceAndPath("tensura", "textures/block/labyrinth_stone.png"),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("ability", AbilityTrigger.TriggerInstance.obtainAbility())
      );
      AdvancementHolder FAST_LEARNER = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.FAST_LEARNER,
         REAL_ADVENTURE,
         (ItemLike)TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         Strategy.OR,
         Pair.of("battlewill", AbilityTrigger.TriggerInstance.learnBattlewill()),
         Pair.of("magic", AbilityTrigger.TriggerInstance.learnMagic()),
         Pair.of("skill", AbilityTrigger.TriggerInstance.learnSkill())
      );
      AdvancementHolder MASTER_SKILL = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.MASTER_SKILL,
         FAST_LEARNER,
         (ItemLike)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         Strategy.OR,
         Pair.of("battlewill", AbilityTrigger.TriggerInstance.masterBattlewill()),
         Pair.of("magic", AbilityTrigger.TriggerInstance.masterMagic()),
         Pair.of("skill", AbilityTrigger.TriggerInstance.masterSkill())
      );
      AdvancementHolder MASTER_UNIQUE = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.MASTER_UNIQUE_SKILL,
         MASTER_SKILL,
         (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         false,
         this.createReward(20),
         null,
         Pair.of("skill", AbilityTrigger.TriggerInstance.masterUniqueSkill())
      );
      AdvancementHolder FORBIDDEN_MANUAL = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.FORBIDDEN_MANUAL,
         FAST_LEARNER,
         (ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("manual", this.consumeTrigger((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get()))
      );
      AdvancementHolder YOU_A_WIZARD = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.YOU_A_WIZARD,
         FAST_LEARNER,
         (ItemLike)TensuraMaterialItems.MAGIC_TOME.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         null,
         Pair.of("tome", this.consumeTrigger((ItemLike)TensuraMaterialItems.MAGIC_TOME.get()))
      );
      AdvancementHolder WANDERFUL = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.WANDERFUL,
         YOU_A_WIZARD,
         (ItemLike)TensuraToolItems.HIGH_MAGIC_STAFF.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         Strategy.OR,
         Pair.of("staves", this.obtainItemTrigger(TensuraItemTags.MAGIC_STAVES))
      );
      AdvancementHolder BOOKED_ON_MAGIC = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.BOOKED_ON_MAGIC,
         WANDERFUL,
         (ItemLike)TensuraToolItems.GRIMOIRE_SPECIAL_A.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         Strategy.OR,
         Pair.of("grimoires", this.obtainItemTrigger(TensuraItemTags.MAGIC_GRIMOIRES))
      );
      AdvancementHolder EXPLOSION = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.EXPLOSION,
         YOU_A_WIZARD,
         MagicTomeItem.createForMagic((ManasSkill)AspectualMagics.EXPLOSION.get()),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         null,
         Pair.of("magic", AbilityTrigger.TriggerInstance.activateSpecialAbility(AspectualMagics.EXPLOSION.getId()))
      );
      AdvancementHolder MONSTER_TAMER = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.MONSTER_TAMER,
         REAL_ADVENTURE,
         (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_B.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         null,
         Pair.of("monster", this.tameAnimalTrigger(TensuraEntityTags.MONSTER))
      );
      AdvancementHolder HEAR_ME_DIREWOLVES = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.HEAR_ME_DIREWOLVES,
         MONSTER_TAMER,
         (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_A.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("direwolf", this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.DIREWOLF.get()))
      );
      AdvancementHolder GOOD_BOY = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.GOOD_BOY,
         HEAR_ME_DIREWOLVES,
         Items.BONE,
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(50),
         null,
         Pair.of(
            "wolf",
            this.rideAnimalTrigger(
               net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
                  .of((EntityType)MonsterEntityTypes.DIREWOLF.get())
                  .subPredicate(VariantPredicate.getDefault(DirewolfVariant.TEMPEST_STAR_WOLF.toString()))
            )
         )
      );
      AdvancementHolder NAME_A_MOB = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.NAME_A_MOB,
         MONSTER_TAMER,
         Items.NAME_TAG,
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of(
            "name",
            ((PlayerTrigger)TensuraCriteriaTriggers.NAME_ENTITY.get())
               .createCriterion(new net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance(Optional.empty()))
         )
      );
      AdvancementHolder RULER_OF_MONSTER = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.RULER_OF_MONSTERS,
         NAME_A_MOB,
         (ItemLike)TensuraArmorItems.DARK_JACKET.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(TensuraAdvancementLoot.RULER_OF_MONSTERS),
         Strategy.AND,
         Pair.of("direwolf", this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.DIREWOLF.get())),
         Pair.of("goblin", this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.GOBLIN.get())),
         Pair.of("lizardman", this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.LIZARDMAN.get())),
         Pair.of("orc", this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.ORC.get())),
         Pair.of("slime", this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.SLIME.get()))
      );
      AdvancementHolder TAME_A_SLIME = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.TAMED_A_SLIME,
         MONSTER_TAMER,
         (ItemLike)TensuraMobDropItems.SLIME_CHUNK.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         Strategy.OR,
         Pair.of("slime", this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.SLIME.get())),
         Pair.of("metal_slime", this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.METAL_SLIME.get()))
      );
      AdvancementHolder GET_BUCKETED = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.GET_BUCKETED,
         TAME_A_SLIME,
         (ItemLike)TensuraMaterialItems.SLIME_IN_A_BUCKET.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         null,
         Pair.of(
            "bucket",
            net.minecraft.advancements.critereon.FilledBucketTrigger.TriggerInstance.filledBucket(
               Builder.item().of(new ItemLike[]{(ItemLike)TensuraMaterialItems.SLIME_IN_A_BUCKET.get()})
            )
         )
      );
      AdvancementHolder SLIME_TRAITOR = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.TRAITOR,
         GET_BUCKETED,
         (ItemLike)TensuraConsumableItems.CHILLED_SLIME.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         null,
         Pair.of("slime", this.consumeTrigger((ItemLike)TensuraMaterialItems.SLIME_IN_A_BUCKET.get()))
      );
      AdvancementHolder GROW_A_SLIME = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.GROW_A_SLIME,
         GET_BUCKETED,
         (ItemLike)TensuraMobDropItems.SLIME_CORE.get(),
         AdvancementType.GOAL,
         true,
         true,
         true,
         null,
         Pair.of(
            "slime_core",
            ItemUsedOnEntityTrigger.TriggerInstance.itemUsedOnEntity(
               net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(TensuraEntityTags.SLIMES),
               Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.SLIME_CORE.get()})
            )
         )
      );
      AdvancementHolder KING_SLIME = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.KING_SLIME,
         GROW_A_SLIME,
         (ItemLike)TensuraBlocks.SLIME_CHUNK_BLOCK.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(20),
         null,
         Pair.of(
            "slime_core",
            ItemUsedOnEntityTrigger.TriggerInstance.itemUsedOnEntity(
               net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
                  .of(TensuraEntityTags.SLIMES)
                  .subPredicate(new SlimePredicate(Optional.empty(), Optional.of(true))),
               Builder.item().of(new ItemLike[]{(ItemLike)TensuraMobDropItems.SLIME_CORE.get()})
            )
         )
      );
      AdvancementHolder SLIME_ARMY = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.SLIME_ARMY,
         GROW_A_SLIME,
         (ItemLike)TensuraToolItems.SLIME_STAFF.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         null,
         Pair.of(
            "staff",
            net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
               net.minecraft.advancements.critereon.LocationPredicate.Builder.location(),
               Builder.item().of(new ItemLike[]{(ItemLike)TensuraToolItems.SLIME_STAFF.get()})
            )
         )
      );
      AdvancementHolder MONSTER_RIDER = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.MONSTER_RIDER,
         MONSTER_TAMER,
         (ItemLike)TensuraMaterialItems.MONSTER_SADDLE.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         null,
         Pair.of("saddle", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.MONSTER_SADDLE.get()))
      );
      AdvancementHolder KILLER_FISH = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.KILLER_FISH,
         MONSTER_RIDER,
         (ItemLike)TensuraMobDropItems.SISSIE_TOOTH.get(),
         AdvancementType.GOAL,
         true,
         true,
         true,
         Strategy.OR,
         Pair.of("sissie", this.rideAnimalTrigger((EntityType<?>)MonsterEntityTypes.SISSIE.get()))
      );
      AdvancementHolder CHOO_CHOO = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.CHOO_CHOO,
         MONSTER_RIDER,
         (ItemLike)TensuraMobDropItems.CENTIPEDE_STINGER.get(),
         AdvancementType.GOAL,
         true,
         true,
         true,
         Strategy.OR,
         Pair.of("centipede", this.rideAnimalTrigger((EntityType<?>)MonsterEntityTypes.EVIL_CENTIPEDE.get())),
         Pair.of("centipede_body", this.rideAnimalTrigger((EntityType<?>)MonsterEntityTypes.EVIL_CENTIPEDE_BODY.get())),
         Pair.of("serpent", this.rideAnimalTrigger((EntityType<?>)MonsterEntityTypes.TEMPEST_SERPENT.get())),
         Pair.of("serpent_body", this.rideAnimalTrigger((EntityType<?>)MonsterEntityTypes.TEMPEST_SERPENT_BODY.get()))
      );
      AdvancementHolder GETCHA_BETTER_LEATHERS = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.GETCHA_BETTER_LEATHERS,
         REAL_ADVENTURE,
         (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         this.createReward(TensuraAdvancementLoot.GETCHA_BETTER_LEATHERS),
         Strategy.OR,
         Pair.of("monster_leathers", this.obtainItemTrigger(TensuraItemTags.MONSTER_LEATHERS))
      );
      AdvancementHolder BELIEVE_TO_FLY = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.BELIEVE_T0_FLY,
         GETCHA_BETTER_LEATHERS,
         (ItemLike)TensuraMobDropItems.DRAGON_PEACOCK_FEATHER.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("shoes", this.obtainItemTrigger((ItemLike)TensuraArmorItems.WINGED_SHOES.get()))
      );
      AdvancementHolder RIPOFF_ELYTRA = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.RIPOFF_ELYTRA,
         BELIEVE_TO_FLY,
         (ItemLike)TensuraArmorItems.BAT_GLIDER.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("bat_glider", this.obtainItemTrigger((ItemLike)TensuraArmorItems.BAT_GLIDER.get()))
      );
      AdvancementHolder VIGILANT = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.VIGILANT,
         REAL_ADVENTURE,
         (ItemLike)TensuraMobDropItems.GIANT_ANT_CARAPACE.get(),
         AdvancementType.TASK,
         true,
         true,
         true,
         this.createReward(TensuraAdvancementLoot.VIGILANT),
         null,
         Pair.of("giant_ant_carapace", this.obtainItemTrigger((ItemLike)TensuraMobDropItems.GIANT_ANT_CARAPACE.get()))
      );
      AdvancementHolder GOODNIGHT_SPIDER = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.GOODNIGHT_SPIDER,
         VIGILANT,
         (ItemLike)TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE.get(),
         AdvancementType.TASK,
         true,
         true,
         true,
         this.createReward(TensuraAdvancementLoot.GOODNIGHT_SPIDER),
         null,
         Pair.of("knight_spider_carapace", this.obtainItemTrigger((ItemLike)TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE.get()))
      );
      AdvancementHolder ARACHNOPHOBIC = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.ARACHNOPHOBIC,
         GOODNIGHT_SPIDER,
         (ItemLike)TensuraMobDropItems.SPIDER_FANG.get(),
         AdvancementType.TASK,
         true,
         true,
         true,
         this.createReward(TensuraAdvancementLoot.ARACHNOPHOBIC),
         null,
         Pair.of("black_spider", this.playerKilledEntityTrigger((EntityType<?>)MonsterEntityTypes.BLACK_SPIDER.get()))
      );
      AdvancementHolder SHELL_LIZARD = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.SHELL_LIZARD,
         ARACHNOPHOBIC,
         (ItemLike)TensuraMobDropItems.ARMORSAURUS_SHELL.get(),
         AdvancementType.TASK,
         true,
         true,
         true,
         this.createReward(TensuraAdvancementLoot.SHELL_LIZARD),
         Strategy.OR,
         Pair.of("armorsaurus_scale", this.obtainItemTrigger((ItemLike)TensuraMobDropItems.ARMORSAURUS_SCALE.get())),
         Pair.of("armorsaurus_shell", this.obtainItemTrigger((ItemLike)TensuraMobDropItems.ARMORSAURUS_SHELL.get()))
      );
      AdvancementHolder HISS_TORY = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.HISS_TORY,
         SHELL_LIZARD,
         (ItemLike)TensuraMobDropItems.SERPENT_SCALE.get(),
         AdvancementType.TASK,
         true,
         true,
         true,
         this.createReward(TensuraAdvancementLoot.HISS_TORY),
         null,
         Pair.of("serpent_scale", this.obtainItemTrigger((ItemLike)TensuraMobDropItems.SERPENT_SCALE.get()))
      );
      AdvancementHolder EAT_OR_BE_EATEN = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.EAT_OR_BE_EATEN,
         HISS_TORY,
         (ItemLike)TensuraMobDropItems.ORC_DISASTER_HEAD.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         null,
         Pair.of("orc_disaster", this.playerKilledEntityTrigger((EntityType<?>)MonsterEntityTypes.ORC_DISASTER.get()))
      );
      AdvancementHolder CONQUEROR_OF_FLAMES = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.CONQUEROR_OF_FLAMES,
         HISS_TORY,
         (ItemLike)TensuraArmorItems.ANTI_MAGIC_MASK.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         null,
         Pair.of("ifrit", this.playerKilledEntityTrigger((EntityType<?>)MonsterEntityTypes.IFRIT.get(), new TamedPredicate(false)))
      );
      AdvancementHolder RULER_OF_THE_SKIES = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.RULER_OF_THE_SKIES,
         HISS_TORY,
         (ItemLike)TensuraBlocks.CHARYBDIS_CORE.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(TensuraAdvancementLoot.RULER_OF_THE_SKIES),
         null,
         Pair.of("charybdis", this.playerKilledEntityTrigger((EntityType<?>)MonsterEntityTypes.CHARYBDIS.get(), new TamedPredicate(false)))
      );
      AdvancementHolder NANODA = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.NANODA,
         RULER_OF_THE_SKIES,
         (ItemLike)TensuraMaterialItems.MUSIC_DISC_NANODA.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(50),
         null,
         Pair.of("nanoda", this.obtainItemTrigger((ItemLike)TensuraMaterialItems.MUSIC_DISC_NANODA.get()))
      );
      AdvancementHolder GREAT_SAINT = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.GREAT_SAINT_OF_THE_WEST,
         HISS_TORY,
         (ItemLike)TensuraToolItems.MOONLIGHT.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(20),
         null,
         Pair.of("hinata", this.playerKilledEntityTrigger((EntityType<?>)HumanEntityTypes.HINATA_SAKAGUCHI.get(), new TamedPredicate(false)))
      );
      AdvancementHolder HERO_KING = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.HERO_KING,
         HISS_TORY,
         (ItemLike)TensuraToolItems.RUHK.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(20),
         null,
         Pair.of("gazel", this.playerKilledEntityTrigger((EntityType<?>)HumanEntityTypes.GAZEL_DWARGO.get(), new TamedPredicate(false)))
      );
      AdvancementHolder START_SMITHING = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.START_SMITHING,
         REAL_ADVENTURE,
         (ItemLike)TensuraSmithingSchematicItems.SPIDER_BOWS.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         null,
         Pair.of("schematic", net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance.usedItem(Builder.item().of(TensuraItemTags.SCHEMATICS)))
      );
      AdvancementHolder BECOME_NINJA = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.BECOME_NINJA,
         START_SMITHING,
         (ItemLike)TensuraToolItems.KUNAI.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("kunai", this.obtainItemTrigger((ItemLike)TensuraToolItems.KUNAI.get()))
      );
      AdvancementHolder UNHEALABLE_WOUND = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.UNHEALABLE_WOUND,
         BECOME_NINJA,
         (ItemLike)TensuraToolItems.SPATIAL_BLADE.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("blade", this.obtainItemTrigger((ItemLike)TensuraToolItems.SPATIAL_BLADE.get()))
      );
      AdvancementHolder A_BIT_COLD = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.A_BIT_COLD,
         UNHEALABLE_WOUND,
         (ItemLike)TensuraToolItems.ICE_BLADE.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         false,
         null,
         Pair.of("blade", this.obtainItemTrigger((ItemLike)TensuraToolItems.ICE_BLADE.get()))
      );
      AdvancementHolder MASTER_SMITH = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.MASTER_SMITH,
         A_BIT_COLD,
         (ItemLike)TensuraSmithingSchematicItems.DARK_SET.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(20),
         null,
         Pair.of(
            "schematics",
            ((PlayerTrigger)TensuraCriteriaTriggers.LEARN_ALL_SCHEMATICS.get())
               .createCriterion(new net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance(Optional.empty()))
         )
      );
      AdvancementHolder NO_NO_SQUARE = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.NO_NO_SQUARE,
         START_SMITHING,
         (ItemLike)TensuraBlocks.STONE_BRICKS_MAGIC_ENGINE.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of(
            "block",
            net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
               net.minecraft.advancements.critereon.LocationPredicate.Builder.location()
                  .setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(TensuraBlockTags.MAGIC_ENGINES)),
               Builder.item()
            )
         )
      );
      AdvancementHolder WAY_STONE = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.WAY_STONE,
         NO_NO_SQUARE,
         (ItemLike)TensuraBlocks.STONE_WARP_PAD.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("block", this.obtainItemTrigger(TensuraItemTags.WARP_PADS))
      );
      AdvancementHolder BETTER_SMELTER = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.BETTER_SMELTER,
         START_SMITHING,
         (ItemLike)TensuraBlocks.Items.KILN.get(),
         AdvancementType.TASK,
         true,
         true,
         false,
         null,
         Pair.of("kiln", this.obtainItemTrigger((ItemLike)TensuraBlocks.Items.KILN.get()))
      );
      AdvancementHolder EVEN_BETTER_SMELTER = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.EVEN_BETTER_SMELTER,
         BETTER_SMELTER,
         (ItemLike)TensuraBlocks.Items.KILN_MITHRIL.get(),
         AdvancementType.GOAL,
         true,
         true,
         true,
         null,
         Pair.of("kiln", this.obtainItemTrigger((ItemLike)TensuraBlocks.Items.KILN_MITHRIL.get()))
      );
      AdvancementHolder BEST_SMELTER = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.BEST_SMELTER,
         EVEN_BETTER_SMELTER,
         (ItemLike)TensuraBlocks.Items.KILN_ORICHALCUM.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         null,
         Pair.of("kiln", this.obtainItemTrigger((ItemLike)TensuraBlocks.Items.KILN_ORICHALCUM.get()))
      );
      AdvancementHolder PIERROT_MASK = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.PIERROT_MASK,
         BETTER_SMELTER,
         (ItemLike)TensuraArmorItems.CRAZY_PIERROT_MASK.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         Strategy.OR,
         Pair.of("mask", this.obtainItemTrigger(TensuraItemTags.PIERROT_MASKS))
      );
      AdvancementHolder TOO_STRONG = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.TOO_STRONG,
         PIERROT_MASK,
         (ItemLike)TensuraToolItems.DRAGON_KNUCKLE.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         null,
         Pair.of("knuckle", this.obtainItemTrigger((ItemLike)TensuraToolItems.DRAGON_KNUCKLE.get()))
      );
      AdvancementHolder HELL = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.HELL,
         REAL_ADVENTURE,
         (ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("hell", this.changeDimensionTrigger(TensuraDimensions.HELL))
      );
      AdvancementHolder HELLA_COOL = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.HELLA_COOL,
         HELL,
         (ItemLike)TensuraSpawnEggs.LESSER_DAEMON.get(),
         AdvancementType.GOAL,
         true,
         true,
         false,
         null,
         Pair.of("daemons", this.tameAnimalTrigger(TensuraEntityTags.DAEMONS))
      );
      AdvancementHolder BUILD_BODY = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.BUILD_BODY,
         HELLA_COOL,
         (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_BONE_GOLEM.get(),
         AdvancementType.GOAL,
         true,
         true,
         true,
         null,
         Pair.of("golems", this.obtainItemTrigger(TensuraItemTags.BONE_GOLEMS))
      );
      AdvancementHolder RAINBOW_IN_HELL = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.RAINBOW_IN_HELL,
         BUILD_BODY,
         (ItemLike)TensuraMaterialItems.DAEMON_CORE.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         Strategy.AND,
         Pair.of(
            "red",
            this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.ARCH_DAEMON.get(), VariantPredicate.getDefault(DaemonVariant.Linage.RED.toString()))
         ),
         Pair.of(
            "white",
            this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.ARCH_DAEMON.get(), VariantPredicate.getDefault(DaemonVariant.Linage.WHITE.toString()))
         ),
         Pair.of(
            "black",
            this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.ARCH_DAEMON.get(), VariantPredicate.getDefault(DaemonVariant.Linage.BLACK.toString()))
         ),
         Pair.of(
            "green",
            this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.ARCH_DAEMON.get(), VariantPredicate.getDefault(DaemonVariant.Linage.GREEN.toString()))
         ),
         Pair.of(
            "yellow",
            this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.ARCH_DAEMON.get(), VariantPredicate.getDefault(DaemonVariant.Linage.YELLOW.toString()))
         ),
         Pair.of(
            "purple",
            this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.ARCH_DAEMON.get(), VariantPredicate.getDefault(DaemonVariant.Linage.PURPLE.toString()))
         ),
         Pair.of(
            "blue",
            this.tameAnimalTrigger((EntityType<?>)MonsterEntityTypes.ARCH_DAEMON.get(), VariantPredicate.getDefault(DaemonVariant.Linage.BLUE.toString()))
         )
      );
      List<Pair<String, Criterion<?>>> hellList = new ArrayList<>();
      HolderGetter<Biome> holderGetter = registries.lookupOrThrow(Registries.BIOME);

      for (ResourceKey<Biome> resourceKey : HELL_BIOMES) {
         hellList.add(
            Pair.of(
               resourceKey.location().getPath(),
               net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.located(
                  net.minecraft.advancements.critereon.LocationPredicate.Builder.inBiome(holderGetter.getOrThrow(resourceKey))
               )
            )
         );
      }

      AdvancementHolder UNHOLY_TOURISM = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.UNHOLY_TOURISM,
         HELL,
         (ItemLike)TensuraArmorItems.ADAMANTITE_BOOTS.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(500),
         Strategy.AND,
         hellList
      );
      List<Pair<String, Criterion<?>>> biomeList = new ArrayList<>();

      for (ResourceKey<Biome> resourceKey : TENSURA_BIOMES) {
         biomeList.add(
            Pair.of(
               resourceKey.location().getPath(),
               net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.located(
                  net.minecraft.advancements.critereon.LocationPredicate.Builder.inBiome(holderGetter.getOrThrow(resourceKey))
               )
            )
         );
      }

      AdvancementHolder OTHERWORLDLY_BIOMES = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Adventure.OTHERWORLDLY_BIOMES,
         REAL_ADVENTURE,
         (ItemLike)TensuraArmorItems.HIHIIROKANE_BOOTS.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(300),
         Strategy.AND,
         biomeList
      );
   }

   public void generateVanillaRooted(@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper helper) {
      AdvancementHolder advancementHolder4 = net.minecraft.advancements.Advancement.Builder.advancement()
         .parent(ResourceLocation.withDefaultNamespace("story/upgrade_tools"))
         .display(
            Items.IRON_INGOT,
            Component.translatable("advancements.story.smelt_iron.title"),
            Component.translatable("advancements.story.smelt_iron.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("iron", net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{Items.IRON_INGOT}))
         .rewards(this.createReward(TensuraAdvancementLoot.SMELT_IRON))
         .save(consumer, "story/smelt_iron");
      AdvancementHolder advancementHolder6 = net.minecraft.advancements.Advancement.Builder.advancement()
         .parent(ResourceLocation.withDefaultNamespace("story/iron_tools"))
         .display(
            Items.DIAMOND,
            Component.translatable("advancements.story.mine_diamond.title"),
            Component.translatable("advancements.story.mine_diamond.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("diamond", net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{Items.DIAMOND}))
         .rewards(this.createReward(TensuraAdvancementLoot.MINE_DIAMOND))
         .save(consumer, "story/mine_diamond");
      AdvancementHolder GETCHA_LEATHERS = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.GETCHA_LEATHERS,
         ResourceLocation.withDefaultNamespace("story/root"),
         Items.LEATHER,
         AdvancementType.TASK,
         true,
         true,
         false,
         this.createReward(TensuraAdvancementLoot.GETCHA_LEATHERS),
         null,
         Pair.of("leather", this.obtainItemTrigger(Items.LEATHER))
      );
      AdvancementHolder GOLD_RUSH = this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.GOLD_RUSH,
         ResourceLocation.withDefaultNamespace("story/iron_tools"),
         Items.GOLD_INGOT,
         AdvancementType.TASK,
         true,
         true,
         false,
         this.createReward(TensuraAdvancementLoot.GOLD_RUSH),
         null,
         Pair.of("gold", this.obtainItemTrigger(Items.GOLD_INGOT))
      );
      this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.OBTAIN_HIHIIROKANE_HOE,
         ResourceLocation.withDefaultNamespace("husbandry/obtain_netherite_hoe"),
         (ItemLike)TensuraToolItems.HIHIIROKANE_HOE.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(100),
         null,
         Pair.of(
            "hoe",
            net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
               net.minecraft.advancements.critereon.LocationPredicate.Builder.location(),
               Builder.item()
                  .of(new ItemLike[]{(ItemLike)TensuraToolItems.HIHIIROKANE_HOE.get()})
                  .hasComponents(DataComponentPredicate.builder().expect((DataComponentType)TensuraDataComponents.TSUKUMOGAMI_INACTIVE.get(), 0.0F).build())
            )
         )
      );
      this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.UNICORN_HORN,
         ResourceLocation.withDefaultNamespace("adventure/whos_the_pillager_now"),
         (ItemLike)TensuraMobDropItems.UNICORN_HORN.get(),
         AdvancementType.GOAL,
         true,
         true,
         true,
         null,
         Pair.of("horn", this.playerKilledEntityProjectileTrigger((EntityType<?>)ProjectileEntityTypes.UNICORN_HORN.get()))
      );
      this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.LIGHT_AS_HORNED_RABBIT,
         ResourceLocation.withDefaultNamespace("adventure/walk_on_powder_snow_with_leather_boots"),
         (ItemLike)TensuraArmorItems.MONSTER_LEATHER_SPECIAL_A_BOOTS.get(),
         AdvancementType.GOAL,
         true,
         true,
         true,
         null,
         Pair.of("boots", this.walkOnBlockWithEquipment(Blocks.POWDER_SNOW, TensuraItemTags.CAN_WALK_ON_POWDER_SNOW))
      );
      List<Pair<String, Criterion<?>>> criterionList = new ArrayList<>();

      for (Item item : EDIBLE_ITEMS) {
         criterionList.add(
            Pair.of(BuiltInRegistries.ITEM.getKey(item).getPath(), net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance.usedItem(item))
         );
      }

      this.addChild(
         consumer,
         helper,
         TensuraAdvancements.Basic.MONSTROUS_DIET,
         ResourceLocation.withDefaultNamespace("husbandry/balanced_diet"),
         (ItemLike)TensuraConsumableItems.DUBIOUS_FOOD.get(),
         AdvancementType.CHALLENGE,
         true,
         true,
         true,
         this.createReward(200),
         Strategy.AND,
         criterionList
      );
   }

   public void generate(@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper helper) {
      this.generateBasic(registries, consumer, helper);
      this.generateAdventure(registries, consumer, helper);
      this.generateVanillaRooted(registries, consumer, helper);
   }

   @SafeVarargs
   private AdvancementHolder addRoot(
      Consumer<AdvancementHolder> consumer,
      ExistingFileHelper helper,
      ResourceLocation location,
      ItemLike displayItem,
      ResourceLocation background,
      AdvancementType advancementType,
      boolean showToast,
      boolean announceInChat,
      boolean hidden,
      @Nullable Strategy requirements,
      Pair<String, Criterion<?>>... criterion
   ) {
      Component title = Component.translatable("tensura.advancements." + location.getPath() + ".title");
      Component description = Component.translatable("tensura.advancements." + location.getPath() + ".description");
      net.minecraft.advancements.Advancement.Builder builder = net.minecraft.advancements.Advancement.Builder.advancement()
         .display(displayItem, title, description, background, advancementType, showToast, announceInChat, hidden);
      if (requirements != null) {
         builder.requirements(requirements);
      }

      if (criterion.length > 0) {
         for (Pair<String, Criterion<?>> map : criterion) {
            builder.addCriterion((String)map.getFirst(), (Criterion)map.getSecond());
         }
      } else {
         builder.addCriterion("custom", IMPOSSIBLE);
      }

      builder.save(consumer, ResourceLocation.parse(String.format("%s:%s", location.getNamespace(), location.getPath())), helper);
      return builder.build(location);
   }

   @SafeVarargs
   private AdvancementHolder addChild(
      Consumer<AdvancementHolder> consumer,
      ExistingFileHelper helper,
      ResourceLocation location,
      AdvancementHolder parent,
      ItemLike displayItem,
      AdvancementType advancementType,
      boolean showToast,
      boolean announceInChat,
      boolean hidden,
      @Nullable Strategy requirements,
      Pair<String, Criterion<?>>... criterion
   ) {
      return this.addChild(consumer, helper, location, parent, displayItem, advancementType, showToast, announceInChat, hidden, null, requirements, criterion);
   }

   @SafeVarargs
   private AdvancementHolder addChild(
      Consumer<AdvancementHolder> consumer,
      ExistingFileHelper helper,
      ResourceLocation location,
      AdvancementHolder parent,
      ItemLike displayItem,
      AdvancementType advancementType,
      boolean showToast,
      boolean announceInChat,
      boolean hidden,
      @Nullable AdvancementRewards rewards,
      @Nullable Strategy requirements,
      Pair<String, Criterion<?>>... criterion
   ) {
      Component title = Component.translatable("tensura.advancements." + location.getPath() + ".title");
      Component description = Component.translatable("tensura.advancements." + location.getPath() + ".description");
      net.minecraft.advancements.Advancement.Builder builder = net.minecraft.advancements.Advancement.Builder.advancement()
         .parent(parent)
         .display(displayItem, title, description, null, advancementType, showToast, announceInChat, hidden);
      if (requirements != null) {
         builder.requirements(requirements);
      }

      if (rewards != null) {
         builder.rewards(rewards);
      }

      if (criterion.length > 0) {
         for (Pair<String, Criterion<?>> map : criterion) {
            builder.addCriterion((String)map.getFirst(), (Criterion)map.getSecond());
         }
      } else {
         builder.addCriterion("custom", IMPOSSIBLE);
      }

      builder.save(consumer, ResourceLocation.parse(String.format("%s:%s", location.getNamespace(), location.getPath())), helper);
      return builder.build(location);
   }

   private AdvancementHolder addChild(
      Consumer<AdvancementHolder> consumer,
      ExistingFileHelper helper,
      ResourceLocation location,
      AdvancementHolder parent,
      ItemLike displayItem,
      AdvancementType advancementType,
      boolean showToast,
      boolean announceInChat,
      boolean hidden,
      @Nullable AdvancementRewards rewards,
      @Nullable Strategy requirements,
      List<Pair<String, Criterion<?>>> criterion
   ) {
      Component title = Component.translatable("tensura.advancements." + location.getPath() + ".title");
      Component description = Component.translatable("tensura.advancements." + location.getPath() + ".description");
      net.minecraft.advancements.Advancement.Builder builder = net.minecraft.advancements.Advancement.Builder.advancement()
         .parent(parent)
         .display(displayItem, title, description, null, advancementType, showToast, announceInChat, hidden);
      if (requirements != null) {
         builder.requirements(requirements);
      }

      if (rewards != null) {
         builder.rewards(rewards);
      }

      if (criterion.size() > 0) {
         for (Pair<String, Criterion<?>> map : criterion) {
            builder.addCriterion((String)map.getFirst(), (Criterion)map.getSecond());
         }
      } else {
         builder.addCriterion("custom", IMPOSSIBLE);
      }

      builder.save(consumer, ResourceLocation.parse(String.format("%s:%s", location.getNamespace(), location.getPath())), helper);
      return builder.build(location);
   }

   @SafeVarargs
   private AdvancementHolder addChild(
      Consumer<AdvancementHolder> consumer,
      ExistingFileHelper helper,
      ResourceLocation location,
      AdvancementHolder parent,
      ItemStack displayItem,
      AdvancementType advancementType,
      boolean showToast,
      boolean announceInChat,
      boolean hidden,
      @Nullable Strategy requirements,
      Pair<String, Criterion<?>>... criterion
   ) {
      return this.addChild(consumer, helper, location, parent, displayItem, advancementType, showToast, announceInChat, hidden, null, requirements, criterion);
   }

   @SafeVarargs
   private AdvancementHolder addChild(
      Consumer<AdvancementHolder> consumer,
      ExistingFileHelper helper,
      ResourceLocation location,
      AdvancementHolder parent,
      ItemStack displayItem,
      AdvancementType advancementType,
      boolean showToast,
      boolean announceInChat,
      boolean hidden,
      @Nullable AdvancementRewards rewards,
      @Nullable Strategy requirements,
      Pair<String, Criterion<?>>... criterion
   ) {
      Component title = Component.translatable("tensura.advancements." + location.getPath() + ".title");
      Component description = Component.translatable("tensura.advancements." + location.getPath() + ".description");
      net.minecraft.advancements.Advancement.Builder builder = net.minecraft.advancements.Advancement.Builder.advancement()
         .parent(parent)
         .display(displayItem, title, description, null, advancementType, showToast, announceInChat, hidden);
      if (requirements != null) {
         builder.requirements(requirements);
      }

      if (rewards != null) {
         builder.rewards(rewards);
      }

      if (criterion.length > 0) {
         for (Pair<String, Criterion<?>> map : criterion) {
            builder.addCriterion((String)map.getFirst(), (Criterion)map.getSecond());
         }
      } else {
         builder.addCriterion("custom", IMPOSSIBLE);
      }

      builder.save(consumer, ResourceLocation.parse(String.format("%s:%s", location.getNamespace(), location.getPath())), helper);
      return builder.build(location);
   }

   @SafeVarargs
   private AdvancementHolder addChild(
      Consumer<AdvancementHolder> consumer,
      ExistingFileHelper helper,
      ResourceLocation location,
      ResourceLocation parent,
      ItemLike displayItem,
      AdvancementType advancementType,
      boolean showToast,
      boolean announceInChat,
      boolean hidden,
      @Nullable Strategy requirements,
      Pair<String, Criterion<?>>... criterion
   ) {
      return this.addChild(consumer, helper, location, parent, displayItem, advancementType, showToast, announceInChat, hidden, null, requirements, criterion);
   }

   @SafeVarargs
   private AdvancementHolder addChild(
      Consumer<AdvancementHolder> consumer,
      ExistingFileHelper helper,
      ResourceLocation location,
      ResourceLocation parent,
      ItemLike displayItem,
      AdvancementType advancementType,
      boolean showToast,
      boolean announceInChat,
      boolean hidden,
      @Nullable AdvancementRewards rewards,
      @Nullable Strategy requirements,
      Pair<String, Criterion<?>>... criterion
   ) {
      Component title = Component.translatable("tensura.advancements." + location.getPath() + ".title");
      Component description = Component.translatable("tensura.advancements." + location.getPath() + ".description");
      net.minecraft.advancements.Advancement.Builder builder = net.minecraft.advancements.Advancement.Builder.advancement()
         .parent(parent)
         .display(displayItem, title, description, null, advancementType, showToast, announceInChat, hidden);
      if (requirements != null) {
         builder.requirements(requirements);
      }

      if (rewards != null) {
         builder.rewards(rewards);
      }

      if (criterion.length > 0) {
         for (Pair<String, Criterion<?>> map : criterion) {
            builder.addCriterion((String)map.getFirst(), (Criterion)map.getSecond());
         }
      } else {
         builder.addCriterion("custom", IMPOSSIBLE);
      }

      builder.save(consumer, ResourceLocation.parse(String.format("%s:%s", location.getNamespace(), location.getPath())), helper);
      return builder.build(location);
   }

   private AdvancementHolder addChild(
      Consumer<AdvancementHolder> consumer,
      ExistingFileHelper helper,
      ResourceLocation location,
      ResourceLocation parent,
      ItemLike displayItem,
      AdvancementType advancementType,
      boolean showToast,
      boolean announceInChat,
      boolean hidden,
      @Nullable AdvancementRewards rewards,
      @Nullable Strategy requirements,
      List<Pair<String, Criterion<?>>> criterion
   ) {
      Component title = Component.translatable("tensura.advancements." + location.getPath() + ".title");
      Component description = Component.translatable("tensura.advancements." + location.getPath() + ".description");
      net.minecraft.advancements.Advancement.Builder builder = net.minecraft.advancements.Advancement.Builder.advancement()
         .parent(parent)
         .display(displayItem, title, description, null, advancementType, showToast, announceInChat, hidden);
      if (requirements != null) {
         builder.requirements(requirements);
      }

      if (rewards != null) {
         builder.rewards(rewards);
      }

      if (!criterion.isEmpty()) {
         for (Pair<String, Criterion<?>> map : criterion) {
            builder.addCriterion((String)map.getFirst(), (Criterion)map.getSecond());
         }
      } else {
         builder.addCriterion("custom", IMPOSSIBLE);
      }

      builder.save(consumer, ResourceLocation.parse(String.format("%s:%s", location.getNamespace(), location.getPath())), helper);
      return builder.build(location);
   }

   private Criterion<net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance> consumeTrigger(String modId, String location) {
      return net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance.usedItem(
         (ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(modId, location))
      );
   }

   private Criterion<net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance> consumeTrigger(String location) {
      return net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance.usedItem(
         (ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.parse(location))
      );
   }

   private Criterion<net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance> consumeTrigger(ItemLike item) {
      return net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance.usedItem(item);
   }

   private Criterion<net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance> obtainItemTrigger(ItemLike item) {
      return net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[]{item});
   }

   private Criterion<net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance> obtainItemTrigger(TagKey<Item> item) {
      return net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(new ItemPredicate[]{Builder.item().of(item).build()});
   }

   private Criterion<net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance> playerKilledEntityProjectileTrigger(EntityType<?> entity) {
      net.minecraft.advancements.critereon.EntityPredicate.Builder projectile = new net.minecraft.advancements.critereon.EntityPredicate.Builder().of(entity);
      net.minecraft.advancements.critereon.DamageSourcePredicate.Builder builder = new net.minecraft.advancements.critereon.DamageSourcePredicate.Builder()
         .direct(projectile);
      return net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(Optional.empty(), Optional.of(builder.build()));
   }

   private Criterion<net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance> playerKilledEntityTrigger(EntityType<?> entity) {
      net.minecraft.advancements.critereon.EntityPredicate.Builder predicate = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(entity);
      return net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(predicate);
   }

   private Criterion<net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance> playerKilledEntityTrigger(EntitySubPredicate subPredicate) {
      net.minecraft.advancements.critereon.EntityPredicate.Builder predicate = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(subPredicate);
      return net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(predicate);
   }

   private Criterion<net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance> playerKilledEntityTrigger(
      EntityType<?> entity, EntitySubPredicate subPredicate
   ) {
      net.minecraft.advancements.critereon.EntityPredicate.Builder predicate = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .of(entity)
         .subPredicate(subPredicate);
      return net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.playerKilledEntity(predicate);
   }

   private Criterion<net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance> entityKilledPlayerTrigger(EntityType<?> entity) {
      net.minecraft.advancements.critereon.EntityPredicate.Builder predicate = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(entity);
      return net.minecraft.advancements.critereon.KilledTrigger.TriggerInstance.entityKilledPlayer(predicate);
   }

   private Criterion<net.minecraft.advancements.critereon.TameAnimalTrigger.TriggerInstance> tameAnimalTrigger(TagKey<EntityType<?>> typeTagKey) {
      return net.minecraft.advancements.critereon.TameAnimalTrigger.TriggerInstance.tamedAnimal(
         net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(typeTagKey)
      );
   }

   private Criterion<net.minecraft.advancements.critereon.TameAnimalTrigger.TriggerInstance> tameAnimalTrigger(EntityType<?> type) {
      return net.minecraft.advancements.critereon.TameAnimalTrigger.TriggerInstance.tamedAnimal(
         net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(type))
      );
   }

   private Criterion<net.minecraft.advancements.critereon.TameAnimalTrigger.TriggerInstance> tameAnimalTrigger(
      EntityType<?> type, EntitySubPredicate subPredicate
   ) {
      return net.minecraft.advancements.critereon.TameAnimalTrigger.TriggerInstance.tamedAnimal(
         net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().entityType(EntityTypePredicate.of(type)).subPredicate(subPredicate)
      );
   }

   private Criterion<net.minecraft.advancements.critereon.StartRidingTrigger.TriggerInstance> rideAnimalTrigger(EntityType<?> type) {
      return this.rideAnimalTrigger(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(type));
   }

   private Criterion<net.minecraft.advancements.critereon.StartRidingTrigger.TriggerInstance> rideAnimalTrigger(
      net.minecraft.advancements.critereon.EntityPredicate.Builder pVehicle
   ) {
      return net.minecraft.advancements.critereon.StartRidingTrigger.TriggerInstance.playerStartsRiding(
         net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().vehicle(pVehicle)
      );
   }

   private Criterion<net.minecraft.advancements.critereon.ChangeDimensionTrigger.TriggerInstance> changeDimensionTrigger(ResourceKey<Level> level) {
      return net.minecraft.advancements.critereon.ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(level);
   }

   private Criterion<net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance> walkOnBlockWithEquipment(Block block, TagKey<Item> tag) {
      return net.minecraft.advancements.critereon.PlayerTrigger.TriggerInstance.located(
         net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
            .equipment(net.minecraft.advancements.critereon.EntityEquipmentPredicate.Builder.equipment().feet(Builder.item().of(tag)))
            .steppingOn(
               net.minecraft.advancements.critereon.LocationPredicate.Builder.location()
                  .setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(new Block[]{block}))
            )
      );
   }

   private AdvancementRewards createReward(int experience) {
      return net.minecraft.advancements.AdvancementRewards.Builder.experience(experience).build();
   }

   private AdvancementRewards createReward(ResourceKey<LootTable> loot) {
      return net.minecraft.advancements.AdvancementRewards.Builder.loot(loot).build();
   }
}
