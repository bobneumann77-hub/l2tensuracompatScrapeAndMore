package io.github.manasmods.tensura.registry.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.item.TensuraFoodProperties;
import io.github.manasmods.tensura.item.consumable.SimpleFoodItem;
import io.github.manasmods.tensura.item.misc.OrcDisasterHeadItem;
import io.github.manasmods.tensura.item.misc.SimpleBlockItem;
import io.github.manasmods.tensura.item.weapon.ranged.UnicornHornItem;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.block.Block;

public class TensuraMobDropItems {
   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create("tensura", Registries.ITEM);
   public static final RegistrySupplier<Item> LOW_QUALITY_MAGIC_CRYSTAL = ITEMS.register(
      "low_quality_magic_crystal", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> MEDIUM_QUALITY_MAGIC_CRYSTAL = ITEMS.register(
      "medium_quality_magic_crystal", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS).fireResistant())
   );
   public static final RegistrySupplier<Item> HIGH_QUALITY_MAGIC_CRYSTAL = ITEMS.register(
      "high_quality_magic_crystal", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS).fireResistant())
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_D = ITEMS.register(
      "monster_leather_d", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_C = ITEMS.register(
      "monster_leather_c", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_B = ITEMS.register(
      "monster_leather_b", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_A = ITEMS.register(
      "monster_leather_a", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> MONSTER_LEATHER_SPECIAL_A = ITEMS.register(
      "monster_leather_special_a", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> GIANT_BAT_WING = ITEMS.register(
      "giant_bat_wing", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> INVISIBLE_FEATHER = ITEMS.register(
      "invisible_feather", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> DRAGON_PEACOCK_FEATHER = ITEMS.register(
      "dragon_peacock_feather", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> HELL_MOTH_SILK = ITEMS.register(
      "hell_moth_silk", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS)) {
         public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> pTooltipComponents, TooltipFlag tooltipFlag) {
            pTooltipComponents.add(Component.translatable("tooltip.tensura.coming_soon").withStyle(ChatFormatting.RED));
         }
      }
   );
   public static final RegistrySupplier<Item> GEHENNA_MOTH_SILK = ITEMS.register(
      "gehenna_moth_silk", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS)) {
         public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> pTooltipComponents, TooltipFlag tooltipFlag) {
            pTooltipComponents.add(Component.translatable("tooltip.tensura.coming_soon").withStyle(ChatFormatting.RED));
         }
      }
   );
   public static final RegistrySupplier<Item> GIANT_ANT_CARAPACE = ITEMS.register(
      "giant_ant_carapace", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> KNIGHT_SPIDER_CARAPACE = ITEMS.register(
      "knight_spider_carapace", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> INSECTAR_CARAPACE = ITEMS.register(
      "insectar_carapace", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS)) {
         public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> pTooltipComponents, TooltipFlag tooltipFlag) {
            pTooltipComponents.add(Component.translatable("tooltip.tensura.coming_soon").withStyle(ChatFormatting.RED));
         }
      }
   );
   public static final RegistrySupplier<Item> ARMORSAURUS_SCALE = ITEMS.register(
      "armorsaurus_scale", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> ARMORSAURUS_SHELL = ITEMS.register(
      "armorsaurus_shell", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> SERPENT_SCALE = ITEMS.register(
      "serpent_scale", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> CHARYBDIS_SCALE = ITEMS.register(
      "charybdis_scale", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS).fireResistant())
   );
   public static final RegistrySupplier<Item> CENTIPEDE_STINGER = ITEMS.register(
      "centipede_stinger", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> SPIDER_FANG = ITEMS.register(
      "spider_fang", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> BLADE_TIGER_TAIL = ITEMS.register(
      "blade_tiger_tail", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> SLIME_CHUNK = ITEMS.register(
      "slime_chunk", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> SLIME_CORE = ITEMS.register(
      "slime_core", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> STICKY_THREAD = ITEMS.register(
      "sticky_thread", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> STEEL_THREAD = ITEMS.register(
      "steel_thread", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> SISSIE_TOOTH = ITEMS.register(
      "sissie_tooth", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> BEAST_HORN = ITEMS.register(
      "beast_horn", () -> new Item(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> UNICORN_HORN = ITEMS.register(
      "unicorn_horn", () -> new UnicornHornItem(new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS))
   );
   public static final RegistrySupplier<Item> DAEMON_ESSENCE = ITEMS.register(
      "daemon_essence", () -> new SimpleFoodItem(TensuraCreativeTabs.MOB_DROPS, TensuraFoodProperties.DAEMON_ESSENCE)
   );
   public static final RegistrySupplier<Item> DRAGON_ESSENCE = ITEMS.register(
      "dragon_essence", () -> new SimpleFoodItem(TensuraCreativeTabs.MOB_DROPS, TensuraFoodProperties.DRAGON_ESSENCE)
   );
   public static final RegistrySupplier<Item> ELEMENTAL_ESSENCE = ITEMS.register(
      "elemental_essence", () -> new SimpleFoodItem(TensuraCreativeTabs.MOB_DROPS, TensuraFoodProperties.ELEMENTAL_ESSENCE)
   );
   public static final RegistrySupplier<Item> ROYAL_BLOOD = ITEMS.register(
      "royal_blood", () -> new SimpleFoodItem(TensuraCreativeTabs.MOB_DROPS, TensuraFoodProperties.ROYAL_BLOOD, true)
   );
   public static final RegistrySupplier<Item> ZANE_BLOOD = ITEMS.register(
      "zane_blood", () -> new SimpleFoodItem(TensuraCreativeTabs.MOB_DROPS, TensuraFoodProperties.ZANE_BLOOD, true)
   );
   public static final RegistrySupplier<Item> MOTH_EGG = ITEMS.register(
      "moth_egg", () -> new SimpleBlockItem((Block)TensuraBlocks.MOTH_EGG.get(), TensuraCreativeTabs.MOB_DROPS)
   );
   public static final RegistrySupplier<Item> CHARYBDIS_CORE = ITEMS.register(
      "charybdis_core",
      () -> new SimpleBlockItem((Block)TensuraBlocks.CHARYBDIS_CORE.get(), new Properties().arch$tab(TensuraCreativeTabs.MOB_DROPS).fireResistant())
   );
   public static final RegistrySupplier<Item> ORC_DISASTER_HEAD = ITEMS.register("orc_disaster_head", OrcDisasterHeadItem::new);

   public static void init() {
      ITEMS.register();
   }
}
