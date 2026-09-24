package io.github.manasmods.tensura.entity.merchant.profession;

import com.google.common.collect.ImmutableMap;
import io.github.manasmods.tensura.config.entity.EntityConfig;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.merchant.trade.OneForBattlewillManualTrade;
import io.github.manasmods.tensura.entity.merchant.trade.OneForEnchantedBookTrade;
import io.github.manasmods.tensura.entity.merchant.trade.OneForEnchantedItemTrade;
import io.github.manasmods.tensura.entity.merchant.trade.OneForMagicTomeTrade;
import io.github.manasmods.tensura.entity.merchant.trade.OneForMapTrade;
import io.github.manasmods.tensura.entity.merchant.trade.OneForOneTrade;
import io.github.manasmods.tensura.entity.merchant.trade.OneForPotionTrade;
import io.github.manasmods.tensura.entity.merchant.trade.OneForRandomListTrade;
import io.github.manasmods.tensura.entity.merchant.trade.OneForRandomMapTrade;
import io.github.manasmods.tensura.entity.merchant.trade.OneForTaggedItemTrade;
import io.github.manasmods.tensura.entity.merchant.trade.TaggedItemForOneTrade;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.entity.ai.TensuraVillagerProfessions;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;

public class DwarfProfession {
   public static Int2ObjectMap<ItemListing[]> getProfessionTrades(VillagerProfession profession) {
      EntityConfig.Dwarf multiplier = TensuraBehaviourHelper.CONFIG.Dwarf;
      if (profession.equals(VillagerProfession.ARMORER)) {
         return getArmorerTrades(multiplier.armorerPriceMultiplier);
      } else if (profession.equals(VillagerProfession.BUTCHER)) {
         return getButcherTrades(multiplier.butcherPriceMultiplier);
      } else if (profession.equals(TensuraVillagerProfessions.BATTLEWILL_TRAINER.get())) {
         return getBattlewillTrainerTrades(multiplier.battlewillTrainerPriceMultiplier);
      } else if (profession.equals(VillagerProfession.CARTOGRAPHER)) {
         return getCartographerTrades(multiplier.cartographerPriceMultiplier);
      } else if (profession.equals(VillagerProfession.CLERIC)) {
         return getAlchemistTrades(multiplier.alchemistPriceMultiplier);
      } else if (profession.equals(VillagerProfession.FARMER)) {
         return getFarmerTrades(multiplier.farmerPriceMultiplier);
      } else if (profession.equals(VillagerProfession.FISHERMAN)) {
         return getFishermanTrades(multiplier.fishermanPriceMultiplier);
      } else if (profession.equals(VillagerProfession.FLETCHER)) {
         return getFletcherTrades(multiplier.fletcherPriceMultiplier);
      } else if (profession.equals(VillagerProfession.LEATHERWORKER)) {
         return getLeatherWorkerTrades(multiplier.leatherWorkerPriceMultiplier);
      } else if (profession.equals(VillagerProfession.LIBRARIAN)) {
         return getLibrarianTrades(multiplier.librarianPriceMultiplier);
      } else if (profession.equals(TensuraVillagerProfessions.LUMBERJACK.get())) {
         return getLumberjackTrades(multiplier.lumberjackPriceMultiplier);
      } else if (profession.equals(TensuraVillagerProfessions.MAGIC_TRAINER.get())) {
         return getMagicTrainerTrades(multiplier.magicTrainerPriceMultiplier);
      } else if (profession.equals(VillagerProfession.MASON)) {
         return getMasonTrades(multiplier.masonPriceMultiplier);
      } else if (profession.equals(TensuraVillagerProfessions.MERCHANT.get())) {
         return getMerchantTrades(multiplier.merchantPriceMultiplier);
      } else if (profession.equals(TensuraVillagerProfessions.MINER.get())) {
         return getMinerTrades(multiplier.minerPriceMultiplier);
      } else if (profession.equals(VillagerProfession.SHEPHERD)) {
         return getShepherdTrades(multiplier.shepherdPriceMultiplier);
      } else if (profession.equals(VillagerProfession.TOOLSMITH)) {
         return getToolSmithTrades(multiplier.toolSmithPriceMultiplier);
      } else {
         return profession.equals(VillagerProfession.WEAPONSMITH) ? getWeaponSmithTrades(multiplier.weaponSmithPriceMultiplier) : Int2ObjectMaps.emptyMap();
      }
   }

   public static Int2ObjectMap<ItemListing[]> getAlchemistTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(50.0 * multiplier),
                  (int)(100.0 * multiplier),
                  Items.POTION,
                  1,
                  OneForPotionTrade.PotionType.NORMAL,
                  16,
                  1
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(5.0 * multiplier),
                  Items.POTION,
                  1,
                  OneForPotionTrade.PotionType.LONG,
                  16,
                  1
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(5.0 * multiplier),
                  Items.POTION,
                  1,
                  OneForPotionTrade.PotionType.STRONG,
                  16,
                  1
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(2.0 * multiplier),
                  (int)(7.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.LOW_POTION.get(),
                  1,
                  5,
                  2
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.LOW_ARCANE_POTION.get(),
                  1,
                  5,
                  2
               )
            },
            2,
            new ItemListing[]{
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(50.0 * multiplier),
                  (int)(100.0 * multiplier),
                  Items.POTION,
                  1,
                  OneForPotionTrade.PotionType.NORMAL,
                  16,
                  5
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(5.0 * multiplier),
                  Items.POTION,
                  1,
                  OneForPotionTrade.PotionType.LONG,
                  16,
                  5
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(5.0 * multiplier),
                  Items.POTION,
                  1,
                  OneForPotionTrade.PotionType.STRONG,
                  16,
                  5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(30.0 * multiplier),
                  (int)(40.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.HIGH_POTION.get(),
                  1,
                  5,
                  10
               ),
               new OneForOneTrade(Items.NETHER_WART, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 5, 10),
               new OneForOneTrade(Items.REDSTONE, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 5, 10),
               new OneForOneTrade(Items.GLOWSTONE, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 5, 10),
               new OneForOneTrade(Items.FERMENTED_SPIDER_EYE, (int)(4.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 5, 10, 5, 10)
            },
            3,
            new ItemListing[]{
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(50.0 * multiplier),
                  (int)(100.0 * multiplier),
                  Items.POTION,
                  1,
                  OneForPotionTrade.PotionType.NORMAL,
                  16,
                  10
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(5.0 * multiplier),
                  Items.POTION,
                  1,
                  OneForPotionTrade.PotionType.LONG,
                  16,
                  10
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(5.0 * multiplier),
                  Items.POTION,
                  1,
                  OneForPotionTrade.PotionType.STRONG,
                  16,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(40.0 * multiplier),
                  (int)(60.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.MEDIUM_ARCANE_POTION.get(),
                  1,
                  5,
                  10
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.HIPOKUTE_GRASS.get(),
                     (int)(4.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     50,
                     100,
                     5,
                     20
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.HIPOKUTE_FLOWER.get(),
                     (int)(4.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                     20,
                     50,
                     5,
                     20
                  )
                  .setPriceMultiplier(0.0F)
            },
            4,
            new ItemListing[]{
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(3.0 * multiplier),
                  Items.SPLASH_POTION,
                  1,
                  OneForPotionTrade.PotionType.NORMAL,
                  16,
                  20
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(3.0 * multiplier),
                  (int)(8.0 * multiplier),
                  Items.SPLASH_POTION,
                  1,
                  OneForPotionTrade.PotionType.LONG,
                  16,
                  20
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(3.0 * multiplier),
                  (int)(8.0 * multiplier),
                  Items.SPLASH_POTION,
                  1,
                  OneForPotionTrade.PotionType.STRONG,
                  16,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(3.0 * multiplier),
                  (int)(5.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.HIGH_ARCANE_POTION.get(),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(30.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.HIPOKUTE_GRASS.get(),
                  4,
                  5,
                  30
               )
            },
            5,
            new ItemListing[]{
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(3.0 * multiplier),
                  Items.SPLASH_POTION,
                  1,
                  OneForPotionTrade.PotionType.NORMAL,
                  16,
                  30
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(3.0 * multiplier),
                  (int)(8.0 * multiplier),
                  Items.SPLASH_POTION,
                  1,
                  OneForPotionTrade.PotionType.LONG,
                  16,
                  30
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(3.0 * multiplier),
                  (int)(8.0 * multiplier),
                  Items.SPLASH_POTION,
                  1,
                  OneForPotionTrade.PotionType.STRONG,
                  16,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(3.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.HIPOKUTE_FLOWER.get(),
                  4,
                  5,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(5.0 * multiplier), (ItemLike)TensuraConsumableItems.FULL_POTION.get(), 1, 5, 40
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getArmorerTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(15.0 * multiplier), (int)(25.0 * multiplier), Items.IRON_HELMET, 1, 5, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(15.0 * multiplier), (int)(20.0 * multiplier), Items.IRON_BOOTS, 1, 5, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(25.0 * multiplier),
                  (int)(30.0 * multiplier),
                  (ItemLike)TensuraArmorItems.SILVER_HELMET.get(),
                  1,
                  5,
                  5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(25.0 * multiplier),
                  (ItemLike)TensuraArmorItems.SILVER_BOOTS.get(),
                  1,
                  5,
                  5
               ),
               new OneForOneTrade(Items.RAW_IRON, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 16, 32, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.RAW_COPPER, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 32, 40, 5, 5)
                  .setPriceMultiplier(0.0F)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(20.0 * multiplier), (int)(40.0 * multiplier), Items.IRON_CHESTPLATE, 1, 5, 10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(20.0 * multiplier), (int)(30.0 * multiplier), Items.IRON_LEGGINGS, 1, 5, 10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(25.0 * multiplier),
                  (int)(45.0 * multiplier),
                  (ItemLike)TensuraArmorItems.SILVER_CHESTPLATE.get(),
                  1,
                  5,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(25.0 * multiplier),
                  (int)(35.0 * multiplier),
                  (ItemLike)TensuraArmorItems.SILVER_LEGGINGS.get(),
                  1,
                  5,
                  10
               ),
               new OneForOneTrade(Items.RAW_GOLD, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 4, 5, 20)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.RAW_SILVER.get(), (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 2, 5, 20
                  )
                  .setPriceMultiplier(0.0F)
            },
            3,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), (ItemLike)TensuraSmithingSchematicItems.IRON_GEAR.get(), 1, 5, 20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), (ItemLike)TensuraSmithingSchematicItems.SILVER_GEAR.get(), 1, 5, 20
               ),
               new OneForRandomListTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(30.0 * multiplier),
                  List.of(
                     (ItemLike)TensuraArmorItems.LOW_MAGISTEEL_HELMET.get(),
                     (ItemLike)TensuraArmorItems.ANT_CARAPACE_HELMET.get(),
                     (ItemLike)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_HELMET.get()
                  ),
                  1,
                  5,
                  20
               ),
               new OneForRandomListTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(25.0 * multiplier),
                  List.of(
                     (ItemLike)TensuraArmorItems.LOW_MAGISTEEL_BOOTS.get(),
                     (ItemLike)TensuraArmorItems.ANT_CARAPACE_BOOTS.get(),
                     (ItemLike)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_BOOTS.get()
                  ),
                  1,
                  5,
                  20
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.MAGIC_ORE.get(),
                     (int)(8.0 * multiplier),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                     1,
                     2,
                     3,
                     20
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.GIANT_ANT_CARAPACE.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     32,
                     64,
                     5,
                     20
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.ARMORSAURUS_SCALE.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     48,
                     72,
                     5,
                     20
                  )
                  .setPriceMultiplier(0.0F)
            },
            4,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(1.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.ANT_CARAPACE_GEAR.get(),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(1.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.ARMORSAURUS_SCALEMAIL_GEAR.get(),
                  1,
                  5,
                  30
               ),
               new OneForRandomListTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(25.0 * multiplier),
                  (int)(45.0 * multiplier),
                  List.of(
                     (ItemLike)TensuraArmorItems.LOW_MAGISTEEL_CHESTPLATE.get(),
                     (ItemLike)TensuraArmorItems.ANT_CARAPACE_CHESTPLATE.get(),
                     (ItemLike)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_CHESTPLATE.get()
                  ),
                  1,
                  5,
                  30
               ),
               new OneForRandomListTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(25.0 * multiplier),
                  (int)(35.0 * multiplier),
                  List.of(
                     (ItemLike)TensuraArmorItems.LOW_MAGISTEEL_LEGGINGS.get(),
                     (ItemLike)TensuraArmorItems.ANT_CARAPACE_LEGGINGS.get(),
                     (ItemLike)TensuraArmorItems.ARMORSAURUS_SCALEMAIL_LEGGINGS.get()
                  ),
                  1,
                  5,
                  30
               ),
               new OneForRandomListTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(35.0 * multiplier),
                  (int)(55.0 * multiplier),
                  List.of(
                     (ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_HELMET.get(),
                     (ItemLike)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_HELMET.get(),
                     (ItemLike)TensuraArmorItems.SERPENT_SCALEMAIL_HELMET.get()
                  ),
                  1,
                  5,
                  30
               ),
               new OneForRandomListTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(35.0 * multiplier),
                  (int)(45.0 * multiplier),
                  List.of(
                     (ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_BOOTS.get(),
                     (ItemLike)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_BOOTS.get(),
                     (ItemLike)TensuraArmorItems.SERPENT_SCALEMAIL_BOOTS.get()
                  ),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                     1,
                     5,
                     5,
                     30
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.SERPENT_SCALE.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                     5,
                     10,
                     5,
                     30
                  )
                  .setPriceMultiplier(0.0F)
            },
            5,
            new ItemListing[]{
               new OneForRandomListTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(40.0 * multiplier),
                  (int)(65.0 * multiplier),
                  List.of(
                     (ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_CHESTPLATE.get(),
                     (ItemLike)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_CHESTPLATE.get(),
                     (ItemLike)TensuraArmorItems.SERPENT_SCALEMAIL_CHESTPLATE.get()
                  ),
                  1,
                  5,
                  30
               ),
               new OneForRandomListTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(40.0 * multiplier),
                  (int)(55.0 * multiplier),
                  List.of(
                     (ItemLike)TensuraArmorItems.HIGH_MAGISTEEL_LEGGINGS.get(),
                     (ItemLike)TensuraArmorItems.KNIGHT_SPIDER_CARAPACE_LEGGINGS.get(),
                     (ItemLike)TensuraArmorItems.SERPENT_SCALEMAIL_LEGGINGS.get()
                  ),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR.get(),
                  1,
                  5,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR.get(),
                  1,
                  5,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(1.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.KNIGHT_SPIDER_CARAPACE_GEAR.get(),
                  1,
                  5,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(1.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.SERPENT_SCALEMAIL_GEAR.get(),
                  1,
                  5,
                  40
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getBattlewillTrainerTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(2.0 * multiplier),
                  (ItemLike)TensuraBlocks.Items.TRAINING_DUMMY.get(),
                  1,
                  5,
                  2
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get(), 1, (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(10.0 / multiplier), 5, 2
               )
            },
            2,
            new ItemListing[]{
               new OneForBattlewillManualTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(50.0 * multiplier),
                  TensuraSkillTags.LOW_MANUAL_DWARF_TRADE,
                  5,
                  5
               ),
               new OneForBattlewillManualTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(50.0 * multiplier),
                  TensuraSkillTags.LOW_MANUAL_DWARF_TRADE,
                  5,
                  5
               )
            },
            3,
            new ItemListing[]{
               new OneForBattlewillManualTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(60.0 * multiplier),
                  (int)(100.0 * multiplier),
                  TensuraSkillTags.MEDIUM_MANUAL_DWARF_TRADE,
                  5,
                  10
               ),
               new OneForBattlewillManualTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(60.0 * multiplier),
                  (int)(100.0 * multiplier),
                  TensuraSkillTags.MEDIUM_MANUAL_DWARF_TRADE,
                  5,
                  10
               )
            },
            4,
            new ItemListing[]{
               new OneForBattlewillManualTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(5.0 * multiplier),
                  TensuraSkillTags.HIGH_MANUAL_DWARF_TRADE,
                  5,
                  20
               ),
               new OneForBattlewillManualTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(5.0 * multiplier),
                  TensuraSkillTags.HIGH_MANUAL_DWARF_TRADE,
                  5,
                  20
               )
            },
            5,
            new ItemListing[]{
               new OneForBattlewillManualTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(10.0 * multiplier),
                  TensuraSkillTags.RARE_MANUAL_DWARF_TRADE,
                  5,
                  30
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getButcherTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(5.0 * multiplier), (int)(12.0 * multiplier), Items.COOKED_BEEF, 4, 16, 1
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(5.0 * multiplier), (int)(8.0 * multiplier), Items.COOKED_CHICKEN, 4, 16, 1
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(5.0 * multiplier), (int)(8.0 * multiplier), Items.COOKED_PORKCHOP, 4, 16, 1
               ),
               new OneForOneTrade(Items.BEEF, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 5, 5),
               new OneForOneTrade(Items.CHICKEN, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 5, 5),
               new OneForOneTrade(Items.PORKCHOP, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 5, 5)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(5.0 * multiplier), (int)(12.0 * multiplier), Items.COOKED_MUTTON, 4, 16, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(5.0 * multiplier), (int)(8.0 * multiplier), Items.COOKED_RABBIT, 4, 16, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(40.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.CATTLEDEER_STEAK.get(),
                  4,
                  16,
                  5
               ),
               new OneForOneTrade(Items.MUTTON, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 5, 10),
               new OneForOneTrade(Items.RABBIT, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 5, 10),
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.CATTLEDEER_BEEF.get(),
                  (int)(8.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  5,
                  10,
                  5,
                  10
               )
            },
            3,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(80.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.COOKED_GIANT_ANT_LEG.get(),
                  4,
                  16,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(80.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.COOKED_GIANT_BAT_MEAT.get(),
                  4,
                  16,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.GIANT_ANT_LEG.get(), (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 20, 5, 20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.RAW_GIANT_BAT_MEAT.get(),
                  (int)(8.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  20,
                  5,
                  20
               )
            },
            4,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(2.0 * multiplier),
                  (int)(8.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.COOKED_KNIGHT_SPIDER_LEG.get(),
                  4,
                  16,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(2.0 * multiplier),
                  (int)(8.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.COOKED_ARMORSAURUS_MEAT.get(),
                  4,
                  16,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(2.0 * multiplier),
                  (int)(8.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.COOKED_SERPENT_MEAT.get(),
                  4,
                  16,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.KNIGHT_SPIDER_LEG.get(),
                  (int)(8.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  1,
                  2,
                  5,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.RAW_ARMORSAURUS_MEAT.get(),
                  (int)(8.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  1,
                  2,
                  5,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.RAW_SERPENT_MEAT.get(),
                  (int)(9.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  1,
                  2,
                  5,
                  40
               )
            },
            5,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(8.0 * multiplier),
                  (int)(12.0 * multiplier),
                  (ItemLike)TensuraConsumableItems.BLADE_TIGER_STEAK.get(),
                  4,
                  16,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.RAW_BLADE_TIGER_MEAT.get(),
                  (int)(8.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  2,
                  4,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.HUNTING_KNIFE.get(),
                  1,
                  5,
                  40
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getCartographerTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade(Items.PAPER, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 4, 5, 5),
               new OneForOneTrade(Items.MAP, 1, (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, (int)(4.0 / multiplier), 5, 5),
               new OneForOneTrade(Items.FILLED_MAP, 1, (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, (int)(4.0 / multiplier), 5, 5),
               new OneForRandomMapTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(30.0 * multiplier),
                  List.of(
                     new OneForRandomMapTrade.MapEntry(
                        TensuraTags.Structures.ON_PYRAMID_EXPLORER_MAPS, MapDecorationTypes.DESERT_VILLAGE, "tensura.map.pyramid"
                     ),
                     new OneForRandomMapTrade.MapEntry(StructureTags.ON_JUNGLE_EXPLORER_MAPS, MapDecorationTypes.JUNGLE_TEMPLE, "filled_map.explorer_jungle")
                  ),
                  5,
                  5
               )
            },
            2,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(20.0 * multiplier), (int)(50.0 * multiplier), Items.MAP, 1, 16, 2),
               new OneForMapTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(15.0 * multiplier),
                  (int)(40.0 * multiplier),
                  TensuraTags.Structures.ON_DWARF_VILLAGE_MAPS,
                  MapDecorationTypes.TAIGA_VILLAGE,
                  "tensura.map.dwarf_village",
                  10,
                  10
               )
            },
            3,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(5.0 * multiplier), Items.COMPASS, 16, 16, 3),
               new OneForMapTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(15.0 * multiplier),
                  (int)(40.0 * multiplier),
                  StructureTags.VILLAGE,
                  MapDecorationTypes.PLAINS_VILLAGE,
                  "tensura.map.village",
                  10,
                  20
               )
            },
            4,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(10.0 * multiplier), Items.RECOVERY_COMPASS, 16, 16, 5),
               new OneForRandomMapTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(50.0 * multiplier),
                  List.of(
                     new OneForRandomMapTrade.MapEntry(StructureTags.ON_WOODLAND_EXPLORER_MAPS, MapDecorationTypes.WOODLAND_MANSION, "filled_map.mansion"),
                     new OneForRandomMapTrade.MapEntry(StructureTags.ON_OCEAN_EXPLORER_MAPS, MapDecorationTypes.OCEAN_MONUMENT, "filled_map.monument")
                  ),
                  5,
                  30
               )
            },
            5,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), Items.LODESTONE, 1, 16, 10),
               new OneForRandomMapTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(15.0 * multiplier),
                  List.of(
                     new OneForRandomMapTrade.MapEntry(TensuraTags.Structures.ON_HELL_GATE_EXPLORER_MAPS, MapDecorationTypes.RED_X, "tensura.map.hell_gate"),
                     new OneForRandomMapTrade.MapEntry(TensuraTags.Structures.ON_LABYRINTH_EXPLORER_MAPS, MapDecorationTypes.RED_X, "tensura.map.labyrinth"),
                     new OneForRandomMapTrade.MapEntry(TensuraTags.Structures.ON_CHARYBDIS_EXPLORER_MAPS, MapDecorationTypes.RED_X, "tensura.map.charybdis")
                  ),
                  5,
                  40
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getFarmerTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(1.0 * multiplier), Items.WHEAT_SEEDS, 16, 16, 1),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(1.0 * multiplier), Items.PUMPKIN_SEEDS, 8, 16, 1),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(1.0 * multiplier), Items.MELON_SEEDS, 8, 16, 1),
               new OneForOneTrade(Items.WHEAT, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 2, 5, 5).setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.PUMPKIN, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 2, 4, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.MELON, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 2, 4, 5, 5).setPriceMultiplier(0.0F)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(3.0 * multiplier), (int)(4.0 * multiplier), Items.CARROT, 2, 16, 5),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(3.0 * multiplier), (int)(4.0 * multiplier), Items.POTATO, 2, 16, 5),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(5.0 * multiplier), (int)(7.0 * multiplier), Items.BREAD, 1, 16, 5),
               new OneForOneTrade(Items.CARROT, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 2, 5, 10)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.POTATO, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 2, 5, 10)
                  .setPriceMultiplier(0.0F)
            },
            3,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(1.0 * multiplier), (int)(3.0 * multiplier), Items.IRON_HOE, 1, 16, 10),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(3.0 * multiplier),
                  (ItemLike)TensuraToolItems.IRON_SICKLE.get(),
                  1,
                  16,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(2.0 * multiplier),
                  (int)(3.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.THATCH.get(),
                  1,
                  16,
                  10
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.THATCH.get(), (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 2, 5, 20
                  )
                  .setPriceMultiplier(0.0F)
            },
            4,
            new ItemListing[]{
               new OneForEnchantedItemTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(5.0 * multiplier), (int)(10.0 * multiplier), Items.IRON_HOE, 5, 30, 16, 20
               ),
               new OneForEnchantedItemTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(10.0 * multiplier),
                  (ItemLike)TensuraToolItems.IRON_SICKLE.get(),
                  5,
                  30,
                  16,
                  20
               )
            },
            5,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_HOE.get(),
                  1,
                  16,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_SICKLE.get(),
                  1,
                  16,
                  40
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getFishermanTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(8.0 * multiplier), (int)(12.0 * multiplier), Items.COD, 4, 16, 1),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(8.0 * multiplier), (int)(12.0 * multiplier), Items.SALMON, 4, 16, 1),
               new OneForOneTrade(Items.COD, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 3, 5, 5, 1).setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.SALMON, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 3, 5, 5, 1)
                  .setPriceMultiplier(0.0F)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(3.0 * multiplier), (int)(7.0 * multiplier), Items.PUFFERFISH, 4, 16, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(18.0 * multiplier), (int)(22.0 * multiplier), Items.TROPICAL_FISH, 4, 16, 5
               ),
               new OneForOneTrade(Items.PUFFERFISH, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 2, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.TROPICAL_FISH, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 8, 10, 5, 5)
                  .setPriceMultiplier(0.0F)
            },
            3,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.RAW_SPEAR_TORO_MEAT.get(),
                  (int)(8.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  1,
                  5,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.SPEAR_TORO_FIN.get(), (int)(4.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 2, 5, 20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(40.0 * multiplier), (int)(60.0 * multiplier), Items.FISHING_ROD, 1, 16, 10
               )
            },
            4,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.RAW_SISSIE_MEAT.get(),
                  (int)(8.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  2,
                  4,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.SISSIE_FIN.get(), (int)(4.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 2, 4, 5, 30
               ),
               new OneForEnchantedItemTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(1.0 * multiplier), (int)(5.0 * multiplier), Items.FISHING_ROD, 5, 20, 5, 30
               )
            },
            5,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraConsumableItems.RAW_MEGALODON_MEAT.get(),
                  (int)(8.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  6,
                  12,
                  5,
                  40
               ),
               new OneForEnchantedItemTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(3.0 * multiplier), (int)(8.0 * multiplier), Items.FISHING_ROD, 10, 40, 5, 40
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getFletcherTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(1.0 * multiplier), (int)(3.0 * multiplier), Items.BOW, 1, 5, 5),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(3.0 * multiplier),
                  (ItemLike)TensuraToolItems.SHORT_BOW.get(),
                  1,
                  5,
                  5
               ),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), Items.ARROW, 8, 16, 1),
               new OneForOneTrade(Items.FEATHER, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 8, 16, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.FLINT, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 16, 32, 5, 5)
                  .setPriceMultiplier(0.0F)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(1.0 * multiplier), (int)(2.0 * multiplier), Items.CROSSBOW, 1, 5, 10),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(2.0 * multiplier),
                  (int)(6.0 * multiplier),
                  (ItemLike)TensuraToolItems.LONG_BOW.get(),
                  1,
                  5,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(2.0 * multiplier),
                  (int)(6.0 * multiplier),
                  (ItemLike)TensuraToolItems.WAR_BOW.get(),
                  1,
                  5,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(1.0 * multiplier), (ItemLike)TensuraSmithingSchematicItems.BASIC_BOWS.get(), 1, 5, 10
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(4.0 * multiplier),
                  Items.TIPPED_ARROW,
                  8,
                  OneForPotionTrade.PotionType.NORMAL,
                  TensuraTags.Potions.DWARF_FLETCHER,
                  5,
                  10
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(4.0 * multiplier),
                  (int)(12.0 * multiplier),
                  Items.TIPPED_ARROW,
                  8,
                  OneForPotionTrade.PotionType.LONG,
                  TensuraTags.Potions.DWARF_FLETCHER,
                  5,
                  10
               ),
               new OneForPotionTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(4.0 * multiplier),
                  (int)(12.0 * multiplier),
                  Items.TIPPED_ARROW,
                  8,
                  OneForPotionTrade.PotionType.STRONG,
                  TensuraTags.Potions.DWARF_FLETCHER,
                  5,
                  10
               )
            },
            3,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(50.0 * multiplier), (int)(100.0 * multiplier), Items.SPECTRAL_ARROW, 8, 16, 10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(50.0 * multiplier),
                  (int)(100.0 * multiplier),
                  (ItemLike)TensuraToolItems.INVISIBLE_ARROW.get(),
                  8,
                  16,
                  10
               ),
               new OneForEnchantedItemTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(1.0 * multiplier), (int)(10.0 * multiplier), Items.BOW, 5, 15, 5, 20
               ),
               new OneForEnchantedItemTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(1.0 * multiplier), (int)(10.0 * multiplier), Items.BOW, 5, 20, 5, 20
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.INVISIBLE_FEATHER.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     32,
                     64,
                     5,
                     20
                  )
                  .setPriceMultiplier(0.0F)
            },
            4,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(10.0 * multiplier),
                  (ItemLike)TensuraToolItems.ANT_CROSSBOW.get(),
                  1,
                  5,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(10.0 * multiplier),
                  (ItemLike)TensuraToolItems.SPIDER_BOW.get(),
                  1,
                  5,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(10.0 * multiplier),
                  (ItemLike)TensuraToolItems.SHORT_SPIDER_BOW.get(),
                  1,
                  5,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(12.0 * multiplier),
                  (int)(18.0 * multiplier),
                  (ItemLike)TensuraToolItems.SPEARED_FIN_ARROW.get(),
                  8,
                  16,
                  20
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraConsumableItems.SPEAR_TORO_FIN.get(),
                     (int)(4.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                     2,
                     4,
                     5,
                     40
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraConsumableItems.KNIGHT_SPIDER_LEG.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                     2,
                     4,
                     5,
                     40
                  )
                  .setPriceMultiplier(0.0F)
            },
            5,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(32.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.SPIDER_BOWS.get(),
                  1,
                  16,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(2.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.LONG_SPIDER_BOW.get(),
                  1,
                  16,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(2.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.WAR_SPIDER_BOW.get(),
                  1,
                  16,
                  40
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getLeatherWorkerTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.LEATHER_GEAR.get(),
                  1,
                  5,
                  5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(30.0 * multiplier), (int)(60.0 * multiplier), Items.LEATHER_HORSE_ARMOR, 1, 16, 1
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(16.0 * multiplier),
                  (int)(32.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_D_HELMET.get(),
                  1,
                  16,
                  1
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(12.0 * multiplier),
                  (int)(14.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_D_BOOTS.get(),
                  1,
                  16,
                  1
               ),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(5.0 * multiplier), Items.BUNDLE, 1, 16, 1),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.POUCH_D.get(), 1, 16, 1
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get(),
                     (int)(8.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     4,
                     16,
                     5,
                     5
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.LEATHER, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 8, 5, 5)
                  .setPriceMultiplier(0.0F)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(32.0 * multiplier),
                  (int)(100.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.MONSTER_SADDLE.get(),
                  1,
                  5,
                  8
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(4.0 * multiplier),
                  (ItemLike)TensuraArmorItems.BAT_GLIDER.get(),
                  1,
                  5,
                  8
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(25.0 * multiplier),
                  (int)(40.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_D_CHESTPLATE.get(),
                  1,
                  16,
                  4
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(24.0 * multiplier),
                  (int)(36.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_D_LEGGINGS.get(),
                  1,
                  16,
                  4
               ),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(64.0 * multiplier), Items.SADDLE, 1, 16, 5),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(12.0 * multiplier), (ItemLike)TensuraMaterialItems.POUCH_C.get(), 1, 16, 5
               ),
               new OneForOneTrade(Items.SADDLE, 1, (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(4.0 / multiplier), 16, 5, 10),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.MONSTER_SADDLE.get(), 1, (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(8.0 / multiplier), 32, 5, 10
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get(),
                     (int)(8.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     8,
                     32,
                     5,
                     10
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.GIANT_BAT_WING.get(),
                     (int)(4.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     8,
                     32,
                     5,
                     10
                  )
                  .setPriceMultiplier(0.0F)
            },
            3,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(24.0 * multiplier),
                  (int)(48.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_C_HELMET.get(),
                  1,
                  16,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(16.0 * multiplier),
                  (int)(32.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_C_BOOTS.get(),
                  1,
                  16,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(30.0 * multiplier),
                  (int)(60.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_C_CHESTPLATE.get(),
                  1,
                  16,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(25.0 * multiplier),
                  (int)(40.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_C_LEGGINGS.get(),
                  1,
                  16,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(32.0 * multiplier), (ItemLike)TensuraMaterialItems.POUCH_B.get(), 1, 16, 10
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_B.get(),
                     (int)(8.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     16,
                     64,
                     5,
                     20
                  )
                  .setPriceMultiplier(0.0F)
            },
            4,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(24.0 * multiplier),
                  (int)(48.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_B_HELMET.get(),
                  1,
                  16,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(16.0 * multiplier),
                  (int)(32.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_B_BOOTS.get(),
                  1,
                  16,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(30.0 * multiplier),
                  (int)(60.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_B_CHESTPLATE.get(),
                  1,
                  16,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(25.0 * multiplier),
                  (int)(40.0 * multiplier),
                  (ItemLike)TensuraArmorItems.MONSTER_LEATHER_B_LEGGINGS.get(),
                  1,
                  16,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(12.0 * multiplier), (ItemLike)TensuraMaterialItems.POUCH_A.get(), 1, 16, 20
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_A.get(),
                     (int)(8.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     64,
                     100,
                     5,
                     40
                  )
                  .setPriceMultiplier(0.0F)
            },
            5,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(1.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.MONSTER_LEATHER_GEAR.get(),
                  1,
                  16,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(32.0 * multiplier), (ItemLike)TensuraMaterialItems.POUCH_SPECIAL_A.get(), 1, 16, 30
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A.get(),
                     (int)(8.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                     1,
                     8,
                     5,
                     40
                  )
                  .setPriceMultiplier(0.0F)
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getLibrarianTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(10.0 * multiplier), (int)(25.0 * multiplier), Items.BOOK, 4, 16, 1),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(20.0 * multiplier), (int)(30.0 * multiplier), Items.WRITABLE_BOOK, 1, 16, 1
               ),
               new OneForEnchantedBookTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(25.0 * multiplier), (int)(50.0 * multiplier), EnchantmentTags.TRADEABLE, 1, 1, 8, 4
               ),
               new OneForEnchantedBookTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(25.0 * multiplier), (int)(50.0 * multiplier), EnchantmentTags.TRADEABLE, 1, 1, 8, 4
               )
            },
            2,
            new ItemListing[]{
               new OneForEnchantedBookTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(50.0 * multiplier),
                  (int)(100.0 * multiplier),
                  EnchantmentTags.TRADEABLE,
                  1,
                  2,
                  1,
                  8,
                  5
               ),
               new OneForEnchantedBookTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(50.0 * multiplier),
                  (int)(100.0 * multiplier),
                  EnchantmentTags.TRADEABLE,
                  1,
                  2,
                  1,
                  8,
                  5
               ),
               new OneForOneTrade(Items.BOOK, (int)(1.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 2, 4, 5, 10),
               new OneForOneTrade(Items.ENCHANTED_BOOK, 1, (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(10.0 / multiplier), 25, 5, 10)
            },
            3,
            new ItemListing[]{
               new OneForEnchantedBookTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(1.0 * multiplier), (int)(5.0 * multiplier), EnchantmentTags.TRADEABLE, 2, 3, 1, 4, 20
               ),
               new OneForEnchantedBookTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(1.0 * multiplier), (int)(5.0 * multiplier), EnchantmentTags.TRADEABLE, 2, 3, 1, 4, 20
               ),
               new OneForOneTrade(Items.LECTERN, (int)(1.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 4, 20)
            },
            4,
            new ItemListing[]{
               new OneForEnchantedBookTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(5.0 * multiplier), (int)(10.0 * multiplier), EnchantmentTags.TRADEABLE, 1, 2, 3, 30
               ),
               new OneForEnchantedBookTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(10.0 * multiplier),
                  EnchantmentTags.TRADEABLE,
                  1,
                  2,
                  2,
                  3,
                  30
               )
            },
            5,
            new ItemListing[]{
               new OneForEnchantedBookTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  EnchantmentTags.TRADEABLE,
                  2,
                  3,
                  2,
                  3,
                  50
               ),
               new OneForEnchantedBookTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  EnchantmentTags.TRADEABLE,
                  1,
                  10,
                  2,
                  3,
                  50
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getLumberjackTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(10.0 * multiplier), (int)(30.0 * multiplier), Items.IRON_AXE, 1, 16, 1
               ),
               new OneForTaggedItemTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(10.0 * multiplier), (int)(20.0 * multiplier), ItemTags.LOGS_THAT_BURN, 4, 16, 1
               ),
               new OneForTaggedItemTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(10.0 * multiplier), (int)(20.0 * multiplier), ItemTags.LOGS_THAT_BURN, 4, 16, 1
               )
            },
            2,
            new ItemListing[]{
               new TaggedItemForOneTrade(ItemTags.LOGS_THAT_BURN, (int)(4.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 5, 10, 5, 10)
                  .setPriceMultiplier(0.0F),
               new TaggedItemForOneTrade(ItemTags.SAPLINGS, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 10)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.APPLE, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 2, 4, 5, 10).setPriceMultiplier(0.0F)
            },
            3,
            new ItemListing[]{
               new OneForEnchantedItemTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(60.0 * multiplier), (int)(100.0 * multiplier), Items.IRON_AXE, 5, 30, 16, 10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(30.0 * multiplier),
                  (ItemLike)TensuraToolItems.SILVER_AXE.get(),
                  1,
                  16,
                  10
               )
            },
            4,
            new ItemListing[]{
               new OneForEnchantedItemTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(60.0 * multiplier),
                  (int)(100.0 * multiplier),
                  (ItemLike)TensuraToolItems.SILVER_AXE.get(),
                  5,
                  30,
                  16,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_AXE.get(),
                  1,
                  16,
                  20
               )
            },
            5,
            new ItemListing[]{
               new OneForEnchantedItemTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(30.0 * multiplier),
                  (int)(60.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_AXE.get(),
                  5,
                  30,
                  16,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(50.0 * multiplier),
                  (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_AXE.get(),
                  1,
                  16,
                  30
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getMagicTrainerTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(80.0 * multiplier),
                  TensuraSkillTags.LOW_BASIC_TOME_DWARF_TRADE,
                  16,
                  1
               ),
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(80.0 * multiplier),
                  (int)(100.0 * multiplier),
                  TensuraSkillTags.LOW_UPGRADED_TOME_DWARF_TRADE,
                  16,
                  1
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.MAGIC_TOME.get(), 1, (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(10.0 / multiplier), 5, 5
               )
            },
            2,
            new ItemListing[]{
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(80.0 * multiplier),
                  (int)(100.0 * multiplier),
                  TensuraSkillTags.MEDIUM_BASIC_TOME_DWARF_TRADE,
                  16,
                  5
               ),
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(10.0 * multiplier),
                  TensuraSkillTags.MEDIUM_UPGRADED_TOME_DWARF_TRADE,
                  16,
                  5
               ),
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), (int)(5.0 * multiplier), TensuraSkillTags.LOW_RARE_TOME_TRADE, 16, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(5.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGIC_STAFF.get(),
                  1,
                  5,
                  10
               )
            },
            3,
            new ItemListing[]{
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(15.0 * multiplier),
                  TensuraSkillTags.HIGH_BASIC_TOME_DWARF_TRADE,
                  16,
                  10
               ),
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(15.0 * multiplier),
                  TensuraSkillTags.HIGH_UPGRADED_TOME_DWARF_TRADE,
                  16,
                  10
               ),
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(15.0 * multiplier),
                  TensuraSkillTags.MEDIUM_RARE_TOME_TRADE,
                  16,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(10.0 * multiplier),
                  (ItemLike)TensuraToolItems.MEDIUM_MAGIC_STAFF.get(),
                  1,
                  5,
                  20
               )
            },
            4,
            new ItemListing[]{
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(15.0 * multiplier),
                  TensuraSkillTags.GREAT_BASIC_TOME_DWARF_TRADE,
                  16,
                  15
               ),
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(15.0 * multiplier),
                  (int)(20.0 * multiplier),
                  TensuraSkillTags.GREAT_UPGRADED_TOME_DWARF_TRADE,
                  16,
                  15
               ),
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(15.0 * multiplier),
                  (int)(20.0 * multiplier),
                  TensuraSkillTags.HIGH_RARE_TOME_TRADE,
                  16,
                  15
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.HIGH_MAGIC_STAFF.get(),
                  1,
                  5,
                  30
               )
            },
            5,
            new ItemListing[]{
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(15.0 * multiplier),
                  TensuraSkillTags.GREAT_BASIC_TOME_DWARF_TRADE,
                  16,
                  20
               ),
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(15.0 * multiplier),
                  (int)(20.0 * multiplier),
                  TensuraSkillTags.GREAT_UPGRADED_TOME_DWARF_TRADE,
                  16,
                  20
               ),
               new OneForMagicTomeTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(30.0 * multiplier),
                  TensuraSkillTags.GREAT_RARE_TOME_DWARF_TRADE,
                  16,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(10.0 * multiplier), (ItemLike)TensuraSmithingSchematicItems.MAGIC_STAFF.get(), 1, 5, 40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(5.0 * multiplier), (ItemLike)TensuraMaterialItems.UNBOUND_TOME.get(), 1, 1, 40
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getMasonTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForTaggedItemTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), ItemTags.TERRACOTTA, 16, 16, 1
               ),
               new OneForTaggedItemTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(32.0 * multiplier),
                  (int)(64.0 * multiplier),
                  TensuraItemTags.GLAZED_TERRACOTTA,
                  16,
                  16,
                  1
               ),
               new OneForOneTrade(Items.CLAY, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 4, 8, 5, 5).setPriceMultiplier(0.0F),
               new TaggedItemForOneTrade(
                     TensuraItemTags.STONE_FOR_TRADES, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 4, 5, 5
                  )
                  .setPriceMultiplier(0.0F),
               new TaggedItemForOneTrade(TensuraItemTags.DYE, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 4, 5, 5)
                  .setPriceMultiplier(0.0F)
            },
            2,
            new ItemListing[]{
               new OneForTaggedItemTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), ItemTags.TERRACOTTA, 16, 16, 5
               ),
               new OneForTaggedItemTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(32.0 * multiplier),
                  (int)(64.0 * multiplier),
                  TensuraItemTags.GLAZED_TERRACOTTA,
                  16,
                  16,
                  5
               ),
               new OneForOneTrade(Items.QUARTZ, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 2, 6, 5, 10)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     2,
                     6,
                     5,
                     10
                  )
                  .setPriceMultiplier(0.0F)
            },
            3,
            new ItemListing[]{
               new OneForTaggedItemTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(16.0 * multiplier),
                  (int)(32.0 * multiplier),
                  TensuraItemTags.CONCRETE_POWDER,
                  16,
                  16,
                  10
               ),
               new OneForTaggedItemTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), TensuraItemTags.CONCRETE, 16, 16, 10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(64.0 * multiplier),
                  (int)(96.0 * multiplier),
                  (ItemLike)TensuraBlocks.Items.LOW_QUALITY_MAGIC_CRYSTAL_BLOCK.get(),
                  16,
                  16,
                  10
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     4,
                     12,
                     5,
                     20
                  )
                  .setPriceMultiplier(0.0F)
            },
            4,
            new ItemListing[]{
               new OneForTaggedItemTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(16.0 * multiplier),
                  (int)(32.0 * multiplier),
                  TensuraItemTags.CONCRETE_POWDER,
                  16,
                  16,
                  20
               ),
               new OneForTaggedItemTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), TensuraItemTags.CONCRETE, 16, 16, 20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(1.0 * multiplier),
                  (int)(4.0 * multiplier),
                  (ItemLike)TensuraBlocks.Items.MEDIUM_QUALITY_MAGIC_CRYSTAL_BLOCK.get(),
                  16,
                  16,
                  20
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     8,
                     24,
                     5,
                     30
                  )
                  .setPriceMultiplier(0.0F)
            },
            5,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(12.0 * multiplier),
                  (int)(32.0 * multiplier),
                  (ItemLike)TensuraBlocks.Items.HIGH_QUALITY_MAGIC_CRYSTAL_BLOCK.get(),
                  16,
                  16,
                  30
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getMerchantTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 100, (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 1024, 0)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 100, 1024, 0)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 100, (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), 1, 1024, 0)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.GOLD_COIN.get(), 1, (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 100, 1024, 0)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(4.0 * multiplier), (ItemLike)TensuraConsumableItems.LOW_POTION.get(), 1, 5, 2
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     4,
                     5,
                     2
                  )
                  .setPriceMultiplier(0.0F)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade(Items.BLAZE_ROD, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 50, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.BONE, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 5).setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.GHAST_TEAR, (int)(4.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 2, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.GUNPOWDER, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 10, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.MAGMA_CREAM, (int)(4.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.PHANTOM_MEMBRANE, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 10, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.ROTTEN_FLESH, (int)(32.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.SPIDER_EYE, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(35.0 * multiplier), (ItemLike)TensuraConsumableItems.HIGH_POTION.get(), 1, 16, 2
                  )
                  .setPriceMultiplier(0.0F)
            },
            3,
            new ItemListing[]{
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.GIANT_BAT_WING.get(), (int)(1.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 10, 5, 10
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.HELL_MOTH_SILK.get(), (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 20, 5, 10
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.GEHENNA_MOTH_SILK.get(),
                     (int)(8.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                     70,
                     5,
                     10
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.SPIDER_FANG.get(), (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 10, 5, 10
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     8,
                     5,
                     10
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.SLIME_CHUNK.get(), (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 5, 10
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.GIANT_ANT_CARAPACE.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     40,
                     5,
                     10
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.ARMORSAURUS_SCALE.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     60,
                     5,
                     10
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.ARMORSAURUS_SHELL.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     60,
                     5,
                     10
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraConsumableItems.GIANT_ANT_LEG.get(), (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 16, 5, 10
                  )
                  .setPriceMultiplier(0.0F)
            },
            4,
            new ItemListing[]{
               new OneForOneTrade(
                     (ItemLike)TensuraConsumableItems.KNIGHT_SPIDER_LEG.get(),
                     (int)(8.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                     1,
                     5,
                     15
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                     3,
                     5,
                     15
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.SERPENT_SCALE.get(), (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 8, 5, 15
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.INSECTAR_CARAPACE.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                     16,
                     5,
                     15
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.BLADE_TIGER_TAIL.get(), (int)(4.0 * multiplier), (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), 1, 5, 15
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.BEAST_HORN.get(), (int)(4.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 4, 5, 15
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.UNICORN_HORN.get(), (int)(1.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 4, 5, 15
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.STICKY_THREAD.get(), (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 5, 15
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.STEEL_THREAD.get(), (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 5, 15
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get(),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                     16,
                     5,
                     15
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(5.0 * multiplier), (ItemLike)TensuraConsumableItems.FULL_POTION.get(), 1, 16, 5
                  )
                  .setPriceMultiplier(0.0F)
            },
            5,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.GOLD_COIN.get(), 100, (ItemLike)TensuraMaterialItems.STELLAR_GOLD_COIN.get(), 1, 1024, 0)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.STELLAR_GOLD_COIN.get(), 1, (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), 100, 1024, 0)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.CHARYBDIS_SCALE.get(), (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), 4, 5, 20
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.SISSIE_TOOTH.get(), (int)(1.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 10, 5, 20
                  )
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMobDropItems.SLIME_CORE.get(), (int)(1.0 * multiplier), (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), 8, 5, 20
                  )
                  .setPriceMultiplier(0.0F),
               new OneForTaggedItemTrade((ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), TensuraItemTags.EVOLUTION_ESSENCES, 1, 5, 20)
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getMinerTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(24.0 * multiplier), (int)(48.0 * multiplier), Items.RAIL, 8, 16, 1),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(24.0 * multiplier), (int)(48.0 * multiplier), Items.COPPER_ORE, 8, 16, 1
               ),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(4.0 * multiplier), (int)(12.0 * multiplier), Items.GOLD_ORE, 8, 16, 1),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(4.0 * multiplier),
                  (int)(12.0 * multiplier),
                  (ItemLike)TensuraBlocks.Items.SILVER_ORE.get(),
                  8,
                  16,
                  1
               ),
               new OneForOneTrade(
                  Items.IRON_PICKAXE, 1, (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(8.0 / multiplier), (int)(16.0 / multiplier), 2, 10
               ),
               new TaggedItemForOneTrade(
                     TensuraItemTags.STONE_FOR_TRADES, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 2, 5, 5
                  )
                  .setPriceMultiplier(0.0F)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(36.0 * multiplier), (int)(72.0 * multiplier), Items.POWERED_RAIL, 8, 16, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(48.0 * multiplier), (int)(96.0 * multiplier), Items.IRON_ORE, 8, 16, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), Items.COAL_ORE, 8, 16, 5
               ),
               new OneForOneTrade(Items.RAIL, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 12, 24, 5, 10)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.POWERED_RAIL, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 18, 36, 5, 10)
                  .setPriceMultiplier(0.0F)
            },
            3,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(36.0 * multiplier), (int)(72.0 * multiplier), Items.DETECTOR_RAIL, 8, 16, 10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), Items.LAPIS_ORE, 8, 16, 10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), Items.REDSTONE_ORE, 8, 16, 10
               ),
               new OneForOneTrade(Items.DETECTOR_RAIL, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 18, 36, 5, 20)
                  .setPriceMultiplier(0.0F)
            },
            4,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), (int)(2.0 * multiplier), Items.DIAMOND, 8, 16, 20),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), (int)(2.0 * multiplier), Items.EMERALD, 8, 16, 20)
            },
            5,
            new ItemListing[]{
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                     (int)(2.0 * multiplier),
                     (int)(5.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.MAGIC_ORE.get(),
                     8,
                     8,
                     16,
                     30
                  )
                  .setPriceMultiplier(0.0F)
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getShepherdTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), Items.WHITE_WOOL, 16, 16, 1
               ),
               new OneForOneTrade(Items.WHEAT, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 1, 2, 5, 5).setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.HAY_BLOCK, (int)(4.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 2, 4, 5, 5)
                  .setPriceMultiplier(0.0F)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), Items.GRAY_WOOL, 16, 16, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), Items.BROWN_WOOL, 16, 16, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(16.0 * multiplier), (int)(32.0 * multiplier), Items.BLACK_WOOL, 16, 16, 5
               )
            },
            3,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(8.0 * multiplier), (int)(16.0 * multiplier), Items.WHEAT, 4, 16, 10),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(18.0 * multiplier), (int)(36.0 * multiplier), Items.HAY_BLOCK, 1, 16, 10
               )
            },
            4,
            new ItemListing[]{
               new OneForTaggedItemTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(8.0 * multiplier), (int)(16.0 * multiplier), TensuraItemTags.DYE, 4, 16, 20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(8.0 * multiplier),
                  (int)(16.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.THATCH.get(),
                  4,
                  16,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(),
                  (int)(18.0 * multiplier),
                  (int)(36.0 * multiplier),
                  (ItemLike)TensuraBlocks.Items.THATCH_BLOCK.get(),
                  1,
                  16,
                  20
               )
            },
            5,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), (int)(18.0 * multiplier), (int)(36.0 * multiplier), Items.SHEARS, 1, 16, 30),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(1.0 * multiplier),
                  (ItemLike)TensuraMaterialItems.DWARGON_BANNER_PATTERN.get(),
                  1,
                  3,
                  40
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getToolSmithTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(5.0 * multiplier), (int)(20.0 * multiplier), Items.IRON_AXE, 1, 5, 5),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(5.0 * multiplier), (int)(20.0 * multiplier), Items.IRON_PICKAXE, 1, 5, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.SILVER_AXE.get(),
                  1,
                  5,
                  5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.SILVER_PICKAXE.get(),
                  1,
                  5,
                  5
               ),
               new OneForOneTrade(Items.RAW_IRON, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 16, 32, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.RAW_COPPER, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 32, 40, 5, 5)
                  .setPriceMultiplier(0.0F)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(5.0 * multiplier), (int)(10.0 * multiplier), Items.SHIELD, 1, 5, 10),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(5.0 * multiplier), (int)(20.0 * multiplier), Items.IRON_SHOVEL, 1, 5, 10
               ),
               new OneForOneTrade((ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(5.0 * multiplier), (int)(20.0 * multiplier), Items.IRON_HOE, 1, 5, 10),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.IRON_SICKLE.get(),
                  1,
                  5,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.SILVER_SHOVEL.get(),
                  1,
                  5,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(5.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.SILVER_HOE.get(),
                  1,
                  5,
                  10
               ),
               new OneForOneTrade(Items.RAW_GOLD, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 4, 5, 20)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.RAW_SILVER.get(), (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 2, 5, 20
                  )
                  .setPriceMultiplier(0.0F)
            },
            3,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_AXE.get(),
                  1,
                  5,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_PICKAXE.get(),
                  1,
                  5,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(50.0 * multiplier),
                  (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_AXE.get(),
                  1,
                  5,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(50.0 * multiplier),
                  (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_PICKAXE.get(),
                  1,
                  5,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), (ItemLike)TensuraSmithingSchematicItems.SHIELD.get(), 1, 5, 20
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.MAGIC_ORE.get(),
                     (int)(8.0 * multiplier),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                     1,
                     2,
                     3,
                     20
                  )
                  .setPriceMultiplier(0.0F)
            },
            4,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), (ItemLike)TensuraSmithingSchematicItems.IRON_GEAR.get(), 1, 5, 30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), (ItemLike)TensuraSmithingSchematicItems.SILVER_GEAR.get(), 1, 5, 30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_SHOVEL.get(),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_HOE.get(),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (int)(20.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_SICKLE.get(),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(50.0 * multiplier),
                  (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_SHOVEL.get(),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(50.0 * multiplier),
                  (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_HOE.get(),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(50.0 * multiplier),
                  (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_SICKLE.get(),
                  1,
                  5,
                  30
               )
            },
            5,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.LOW_MAGISTEEL_GEAR.get(),
                  1,
                  5,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(10.0 * multiplier),
                  (ItemLike)TensuraSmithingSchematicItems.HIGH_MAGISTEEL_GEAR.get(),
                  1,
                  5,
                  40
               )
            }
         )
      );
   }

   public static Int2ObjectMap<ItemListing[]> getWeaponSmithTrades(double multiplier) {
      return new Int2ObjectOpenHashMap(
         ImmutableMap.of(
            1,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(8.0 * multiplier),
                  (int)(12.0 * multiplier),
                  (ItemLike)TensuraToolItems.IRON_SHORT_SWORD.get(),
                  1,
                  5,
                  5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), (int)(10.0 * multiplier), (int)(14.0 * multiplier), Items.IRON_SWORD, 1, 5, 5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(13.0 * multiplier),
                  (int)(17.0 * multiplier),
                  (ItemLike)TensuraToolItems.SILVER_SHORT_SWORD.get(),
                  1,
                  5,
                  5
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(16.0 * multiplier),
                  (int)(22.0 * multiplier),
                  (ItemLike)TensuraToolItems.SILVER_SWORD.get(),
                  1,
                  5,
                  5
               ),
               new OneForOneTrade(Items.RAW_IRON, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 16, 32, 5, 5)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(Items.RAW_COPPER, (int)(16.0 * multiplier), (ItemLike)TensuraMaterialItems.BRONZE_COIN.get(), 32, 40, 5, 5)
                  .setPriceMultiplier(0.0F)
            },
            2,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(16.0 * multiplier),
                  (int)(22.0 * multiplier),
                  (ItemLike)TensuraToolItems.IRON_LONG_SWORD.get(),
                  1,
                  5,
                  10
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(23.0 * multiplier),
                  (int)(27.0 * multiplier),
                  (ItemLike)TensuraToolItems.SILVER_LONG_SWORD.get(),
                  1,
                  5,
                  10
               ),
               new OneForOneTrade(Items.RAW_GOLD, (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 4, 5, 20)
                  .setPriceMultiplier(0.0F),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.RAW_SILVER.get(), (int)(8.0 * multiplier), (ItemLike)TensuraMaterialItems.SILVER_COIN.get(), 1, 2, 5, 20
                  )
                  .setPriceMultiplier(0.0F)
            },
            3,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(25.0 * multiplier),
                  (ItemLike)TensuraToolItems.IRON_GREAT_SWORD.get(),
                  1,
                  5,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.SILVER_COIN.get(),
                  (int)(25.0 * multiplier),
                  (int)(30.0 * multiplier),
                  (ItemLike)TensuraToolItems.SILVER_GREAT_SWORD.get(),
                  1,
                  5,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(12.0 * multiplier),
                  (int)(16.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_SHORT_SWORD.get(),
                  1,
                  5,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(15.0 * multiplier),
                  (int)(19.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_SWORD.get(),
                  1,
                  5,
                  20
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), (ItemLike)TensuraSmithingSchematicItems.SHORT_SWORD.get(), 1, 5, 20
               ),
               new OneForOneTrade(
                     (ItemLike)TensuraMaterialItems.MAGIC_ORE.get(),
                     (int)(8.0 * multiplier),
                     (int)(16.0 * multiplier),
                     (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                     1,
                     2,
                     3,
                     20
                  )
                  .setPriceMultiplier(0.0F)
            },
            4,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), (ItemLike)TensuraSmithingSchematicItems.LONG_SWORD.get(), 1, 5, 30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(), (int)(1.0 * multiplier), (ItemLike)TensuraSmithingSchematicItems.GREAT_SWORD.get(), 1, 5, 30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(18.0 * multiplier),
                  (int)(22.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_LONG_SWORD.get(),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(20.0 * multiplier),
                  (int)(25.0 * multiplier),
                  (ItemLike)TensuraToolItems.LOW_MAGISTEEL_GREAT_SWORD.get(),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(25.0 * multiplier),
                  (int)(35.0 * multiplier),
                  (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_SHORT_SWORD.get(),
                  1,
                  5,
                  30
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(35.0 * multiplier),
                  (int)(45.0 * multiplier),
                  (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_SWORD.get(),
                  1,
                  5,
                  30
               )
            },
            5,
            new ItemListing[]{
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(45.0 * multiplier),
                  (int)(55.0 * multiplier),
                  (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_LONG_SWORD.get(),
                  1,
                  5,
                  40
               ),
               new OneForOneTrade(
                  (ItemLike)TensuraMaterialItems.GOLD_COIN.get(),
                  (int)(55.0 * multiplier),
                  (int)(60.0 * multiplier),
                  (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_GREAT_SWORD.get(),
                  1,
                  5,
                  40
               )
            }
         )
      );
   }
}
