package io.github.manasmods.tensura.neoforge.data.loot;

import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.data.loot.TensuraArcheologyLoot;
import io.github.manasmods.tensura.data.template.function.ApplySkillDataFunction;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction.Target;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class TensuraArchaeologyLootProvider implements LootTableSubProvider {
   public TensuraArchaeologyLootProvider(Provider lookup) {
   }

   public void generate(BiConsumer<ResourceKey<LootTable>, Builder> consumer) {
      consumer.accept(
         TensuraArcheologyLoot.ANT_NEST,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Items.ARMS_UP_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.BREWER_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.ARCHER_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.MINER_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.PRIZE_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.SKULL_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.BURN_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.DANGER_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.FRIEND_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.HEART_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.HEARTBREAK_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.HOWL_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.SHEAF_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.ANGLER_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.SHELTER_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.SNORT_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.BLADE_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.EXPLORER_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.MOURNER_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.PLENTY_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.BONE).setWeight(5))
                  .add(LootItem.lootTableItem(Items.BONE_BLOCK).setWeight(3))
                  .add(LootItem.lootTableItem(Items.SKELETON_SKULL).setWeight(2))
                  .add(
                     LootItem.lootTableItem(Items.SUSPICIOUS_STEW)
                        .setWeight(3)
                        .apply(
                           SetStewEffectFunction.stewEffect()
                              .withEffect(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), UniformGenerator.between(7.0F, 10.0F))
                        )
                  )
                  .add(LootItem.lootTableItem((ItemLike)TensuraConsumableItems.GIANT_ANT_LEG.get()).setWeight(4))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMobDropItems.GIANT_ANT_CARAPACE.get()).setWeight(4))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get()))
            )
      );
      consumer.accept(
         TensuraArcheologyLoot.HELL_RUINS,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Items.SKULL_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.DANGER_POTTERY_SHERD))
                  .add(LootItem.lootTableItem(Items.BONE).setWeight(5))
                  .add(LootItem.lootTableItem(Items.BONE_BLOCK).setWeight(3))
                  .add(LootItem.lootTableItem(Items.SKELETON_SKULL).setWeight(2))
                  .add(
                     LootItem.lootTableItem(Items.SUSPICIOUS_STEW)
                        .setWeight(3)
                        .apply(
                           SetStewEffectFunction.stewEffect()
                              .withEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY), UniformGenerator.between(7.0F, 10.0F))
                        )
                  )
                  .add(LootItem.lootTableItem(Items.NETHERITE_SCRAP).setWeight(2))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_ORE.get()))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get()))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_TOME.get())
                        .apply(ApplySkillDataFunction.applyTag(TensuraSkillTags.HELL_TREASURE_TOME))
                  )
                  .add(
                     LootItem.lootTableItem(Items.MAP)
                        .apply(
                           ExplorationMapFunction.makeExplorationMap()
                              .setDestination(TensuraTags.Structures.ON_HELL_GATE_EXPLORER_MAPS)
                              .setMapDecoration(MapDecorationTypes.RED_X)
                              .setZoom((byte)2)
                              .setSkipKnownStructures(false)
                        )
                        .apply(SetNameFunction.setName(Component.translatable("tensura.map.hell_gate"), Target.ITEM_NAME))
                  )
            )
      );
   }
}
