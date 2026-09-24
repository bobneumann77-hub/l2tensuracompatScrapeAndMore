package io.github.manasmods.tensura.registry.item.misc;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraSpawnEggs;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class TensuraCreativeTabs {
   private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create("tensura", Registries.CREATIVE_MODE_TAB);
   public static final RegistrySupplier<CreativeModeTab> BLOCKS = TABS.register(
      "00-blocks",
      () -> CreativeTabRegistry.create(Component.translatable("item_tab.tensura.blocks"), () -> new ItemStack(TensuraBlocks.Items.MAGIC_ORE_BLOCK))
   );
   public static final RegistrySupplier<CreativeModeTab> DUNGEON_BLOCKS = TABS.register(
      "01-dungeon_blocks",
      () -> CreativeTabRegistry.create(Component.translatable("item_tab.tensura.dungeon"), () -> new ItemStack(TensuraBlocks.Items.LABYRINTH_BRICK))
   );
   public static final RegistrySupplier<CreativeModeTab> FUNCTIONAL_BLOCKS = TABS.register(
      "02-functional_blocks",
      () -> CreativeTabRegistry.create(Component.translatable("item_tab.tensura.functional"), () -> new ItemStack(TensuraBlocks.Items.BRICKS_MAGIC_ENGINE))
   );
   public static final RegistrySupplier<CreativeModeTab> GEARS = TABS.register(
      "03-gears", () -> CreativeTabRegistry.create(Component.translatable("item_tab.tensura.gears"), () -> new ItemStack(TensuraToolItems.ORB_OF_DOMINATION))
   );
   public static final RegistrySupplier<CreativeModeTab> ARMOR = TABS.register(
      "04-armor",
      () -> CreativeTabRegistry.create(Component.translatable("item_tab.tensura.armors"), () -> new ItemStack(TensuraArmorItems.ARMORSAURUS_SCALEMAIL_HELMET))
   );
   public static final RegistrySupplier<CreativeModeTab> CONSUMABLES = TABS.register(
      "05-consumables",
      () -> CreativeTabRegistry.create(Component.translatable("item_tab.tensura.food"), () -> new ItemStack(TensuraConsumableItems.REVIVAL_ELIXIR))
   );
   public static final RegistrySupplier<CreativeModeTab> LEARNABLE = TABS.register(
      "06-learnable",
      () -> CreativeTabRegistry.create(Component.translatable("item_tab.tensura.learnable"), () -> new ItemStack(TensuraMaterialItems.BATTLEWILL_MANUAL))
   );
   public static final RegistrySupplier<CreativeModeTab> MOB_DROPS = TABS.register(
      "07-mob_drops",
      () -> CreativeTabRegistry.create(Component.translatable("item_tab.tensura.drops"), () -> new ItemStack(TensuraMobDropItems.ARMORSAURUS_SCALE))
   );
   public static final RegistrySupplier<CreativeModeTab> MISCELLANEOUS = TABS.register(
      "08-miscellaneous",
      () -> CreativeTabRegistry.create(Component.translatable("item_tab.tensura.misc"), () -> new ItemStack(TensuraMaterialItems.HIPOKUTE_FLOWER))
   );
   public static final RegistrySupplier<CreativeModeTab> SPAWN_EGG = TABS.register(
      "09-spawn_eggs", () -> CreativeTabRegistry.create(Component.translatable("item_tab.tensura.eggs"), () -> new ItemStack(TensuraSpawnEggs.SLIME))
   );

   public static void init() {
      TABS.register();
   }
}
