package io.github.manasmods.tensura.data.disolving;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TensuraItemDissolving {
   public static void bootstrap(BootstrapContext<ItemDissolving> context) {
      register(context, ItemDissolving.getDefault(TensuraMaterialItems.ADAMANTITE_INGOT.getId(), 100000.0));
      register(context, ItemDissolving.getDefault(TensuraMaterialItems.HIHIIROKANE_INGOT.getId(), 400000.0));
      register(context, ItemDissolving.getDefault(TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.getId(), 5000.0));
      register(context, ItemDissolving.getDefault(TensuraMaterialItems.LOW_MAGISTEEL_INGOT.getId(), 1000.0));
      register(context, ItemDissolving.getDefault(TensuraMaterialItems.MAGIC_ORE.getId(), 5000.0));
      register(context, ItemDissolving.getDefault(TensuraMaterialItems.MAGIC_STONE.getId(), 1000.0));
      register(context, ItemDissolving.getDefault(TensuraMaterialItems.MITHRIL_INGOT.getId(), 100000.0));
      register(context, ItemDissolving.getDefault(TensuraMaterialItems.ORICHALCUM_INGOT.getId(), 7500.0));
      register(context, ItemDissolving.getDefault(TensuraMaterialItems.PURE_MAGISTEEL_INGOT.getId(), 10000.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.ARMORSAURUS_SCALE.getId(), 50.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.ARMORSAURUS_SHELL.getId(), 150.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.CHARYBDIS_SCALE.getId(), 300.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.GEHENNA_MOTH_SILK.getId(), 50.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.GIANT_ANT_CARAPACE.getId(), 120.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.GIANT_BAT_WING.getId(), 400.0, 2.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.BEAST_HORN.getId(), 200.0, 2.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.BLADE_TIGER_TAIL.getId(), 500.0, 2.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.HELL_MOTH_SILK.getId(), 10.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.getId(), 5000.0, 0.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.INSECTAR_CARAPACE.getId(), 1200.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.INVISIBLE_FEATHER.getId(), 10.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE.getId(), 300.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.LOW_QUALITY_MAGIC_CRYSTAL.getId(), 1000.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.MONSTER_LEATHER_SPECIAL_A.getId(), 10000.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.MONSTER_LEATHER_A.getId(), 1000.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.MONSTER_LEATHER_B.getId(), 600.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.MONSTER_LEATHER_C.getId(), 300.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.MONSTER_LEATHER_D.getId(), 100.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.MEDIUM_QUALITY_MAGIC_CRYSTAL.getId(), 2500.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.DRAGON_PEACOCK_FEATHER.getId(), 10.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.SERPENT_SCALE.getId(), 100.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.SPIDER_FANG.getId(), 600.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.STEEL_THREAD.getId(), 10.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.STICKY_THREAD.getId(), 10.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.SLIME_CORE.getId(), 50000.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.DAEMON_ESSENCE.getId(), 3000.0, 5.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.DRAGON_ESSENCE.getId(), 5000.0, 6.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.ELEMENTAL_ESSENCE.getId(), 2000.0, 4.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.ROYAL_BLOOD.getId(), 1000.0, 3.0));
      register(context, ItemDissolving.getDefault(TensuraMobDropItems.ZANE_BLOOD.getId(), 5000.0, 6.0));
      register(context, ItemDissolving.getDefault(TensuraMaterialItems.SLIME_IN_A_BUCKET.getId(), 1000.0));
      register(context, ItemDissolving.getDefault(Items.POTION.arch$registryName(), 10.0));
      register(context, ItemDissolving.getDefault(Items.SPLASH_POTION.arch$registryName(), 10.0));
      register(context, ItemDissolving.getDefault(Items.LINGERING_POTION.arch$registryName(), 10.0));

      for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
         FoodProperties food = (FoodProperties)item.components().get(DataComponents.FOOD);
         if (food != null && food.nutrition() > 0) {
            ItemStack stack = item.getDefaultInstance();
            double multiplier = stack.is(TensuraItemTags.MONSTER_CONSUMABLES) ? (stack.is(TensuraItemTags.COOKED_MONSTER_CONSUMABLES) ? 50.0 : 100.0) : 3.0;
            double healMultiplier = stack.is(TensuraItemTags.MONSTER_CONSUMABLES) ? (stack.is(TensuraItemTags.COOKED_MONSTER_CONSUMABLES) ? 5.0 : 10.0) : 1.0;
            register(
               context,
               ItemDissolving.getDefault(item.arch$registryName(), food.nutrition() * multiplier, Math.round(food.saturation() * healMultiplier * 10.0) / 10.0)
            );
         }
      }
   }

   public static void register(BootstrapContext<ItemDissolving> context, ItemDissolving data) {
      ResourceKey<ItemDissolving> key = ResourceKey.create(TensuraCustomData.ITEM_DISSOLVING, data.item());
      context.register(key, data);
   }
}
