package io.github.manasmods.tensura.registry.menu;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.menu.KilnMenu;
import io.github.manasmods.tensura.menu.MiningStationMenu;
import io.github.manasmods.tensura.menu.NamingMenu;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.menu.SkillCreationMenu;
import io.github.manasmods.tensura.menu.SmithingBenchMenu;
import io.github.manasmods.tensura.menu.SpellbindingMenu;
import io.github.manasmods.tensura.menu.WoodcutterMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

public class TensuraMenuTypes {
   private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create("tensura", Registries.MENU);
   public static final RegistrySupplier<MenuType<ReincarnationMenu>> REINCARNATION = MENUS.register(
      "reincarnation_menu", () -> MenuRegistry.ofExtended(ReincarnationMenu::new)
   );
   public static final RegistrySupplier<MenuType<NamingMenu>> NAMING_MENU = MENUS.register("naming_menu", () -> MenuRegistry.ofExtended(NamingMenu::new));
   public static final RegistrySupplier<MenuType<KilnMenu>> KILN = MENUS.register("kiln_menu", () -> MenuRegistry.ofExtended(KilnMenu::new));
   public static final RegistrySupplier<MenuType<SkillCreationMenu>> SKILL_CREATION = MENUS.register(
      "skill_creation_menu", () -> MenuRegistry.ofExtended(SkillCreationMenu::new)
   );
   public static final RegistrySupplier<MenuType<SmithingBenchMenu>> SMITHING_BENCH = MENUS.register(
      "smithing_bench_menu", () -> MenuRegistry.ofExtended(SmithingBenchMenu::new)
   );
   public static final RegistrySupplier<MenuType<SpellbindingMenu>> SPELLBINDING = MENUS.register(
      "spellbinding_menu", () -> MenuRegistry.ofExtended(SpellbindingMenu::new)
   );
   public static final RegistrySupplier<MenuType<MiningStationMenu>> MINING_STATION_MENU = MENUS.register(
      "mining_station_menu", () -> MenuRegistry.ofExtended(MiningStationMenu::new)
   );
   public static final RegistrySupplier<MenuType<WoodcutterMenu>> WOOD_CUTTER_MENU = MENUS.register(
      "wood_cutter_menu", () -> MenuRegistry.ofExtended(WoodcutterMenu::new)
   );

   public static void init() {
      MENUS.register();
   }
}
