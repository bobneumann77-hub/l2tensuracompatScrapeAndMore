package io.github.manasmods.tensura.data.loot;

import io.github.manasmods.tensura.advancement.TensuraAdvancements;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class TensuraAdvancementLoot {
   public static ResourceKey<LootTable> GETCHA_LEATHERS = register(TensuraAdvancements.Basic.GETCHA_LEATHERS);
   public static ResourceKey<LootTable> GETCHA_BETTER_LEATHERS = register(TensuraAdvancements.Adventure.GETCHA_BETTER_LEATHERS);
   public static ResourceKey<LootTable> SMELT_IRON = register(ResourceLocation.withDefaultNamespace("smelt_iron"));
   public static ResourceKey<LootTable> MINE_DIAMOND = register(ResourceLocation.withDefaultNamespace("mine_diamond"));
   public static ResourceKey<LootTable> GOLD_RUSH = register(TensuraAdvancements.Basic.GOLD_RUSH);
   public static ResourceKey<LootTable> ACQUIRE_SILVERWARE = register(TensuraAdvancements.Basic.ACQUIRE_SILVERWARE);
   public static ResourceKey<LootTable> LOW_MAGISTEEL = register(TensuraAdvancements.Basic.LOW_MAGISTEEL);
   public static ResourceKey<LootTable> HIGH_MAGISTEEL = register(TensuraAdvancements.Basic.HIGH_MAGISTEEL);
   public static ResourceKey<LootTable> MITHRIL = register(TensuraAdvancements.Basic.MITHRIL);
   public static ResourceKey<LootTable> ORICHALCUM = register(TensuraAdvancements.Basic.ORICHALCUM);
   public static ResourceKey<LootTable> PURE_MAGISTEEL = register(TensuraAdvancements.Basic.PURE_MAGISTEEL);
   public static ResourceKey<LootTable> ADAMANTITE = register(TensuraAdvancements.Basic.ADAMANTITE);
   public static ResourceKey<LootTable> HIHIIROKANE = register(TensuraAdvancements.Basic.HIHIIROKANE);
   public static ResourceKey<LootTable> VIGILANT = register(TensuraAdvancements.Adventure.VIGILANT);
   public static ResourceKey<LootTable> SHELL_LIZARD = register(TensuraAdvancements.Adventure.SHELL_LIZARD);
   public static ResourceKey<LootTable> HISS_TORY = register(TensuraAdvancements.Adventure.HISS_TORY);
   public static ResourceKey<LootTable> ARACHNOPHOBIC = register(TensuraAdvancements.Adventure.ARACHNOPHOBIC);
   public static ResourceKey<LootTable> GOODNIGHT_SPIDER = register(TensuraAdvancements.Adventure.GOODNIGHT_SPIDER);
   public static ResourceKey<LootTable> RULER_OF_THE_SKIES = register(TensuraAdvancements.Adventure.RULER_OF_THE_SKIES);
   public static ResourceKey<LootTable> RULER_OF_MONSTERS = register(TensuraAdvancements.Adventure.RULER_OF_MONSTERS);

   private static ResourceKey<LootTable> register(ResourceLocation location) {
      return ResourceKey.create(
         Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(location.getNamespace(), String.format("advancement_reward/%s", location.getPath()))
      );
   }

   private static ResourceKey<LootTable> register(String name) {
      return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("tensura", String.format("advancement_reward/%s", name)));
   }
}
