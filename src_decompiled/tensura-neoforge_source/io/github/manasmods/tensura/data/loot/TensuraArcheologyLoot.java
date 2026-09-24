package io.github.manasmods.tensura.data.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class TensuraArcheologyLoot {
   public static ResourceKey<LootTable> ANT_NEST = register("ant_nest");
   public static ResourceKey<LootTable> HELL_RUINS = register("hell_ruins");

   private static ResourceKey<LootTable> register(String name) {
      return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("tensura", String.format("archaeology/%s", name)));
   }
}
