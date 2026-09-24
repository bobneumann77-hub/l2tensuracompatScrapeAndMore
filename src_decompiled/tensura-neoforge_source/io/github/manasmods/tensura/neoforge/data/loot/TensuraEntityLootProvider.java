package io.github.manasmods.tensura.neoforge.data.loot;

import io.github.manasmods.tensura.data.template.critereon.EvolutionStatePredicate;
import io.github.manasmods.tensura.data.template.critereon.ExistencePointPredicate;
import io.github.manasmods.tensura.data.template.critereon.HellCaterpillarPredicate;
import io.github.manasmods.tensura.data.template.critereon.SlimePredicate;
import io.github.manasmods.tensura.data.template.critereon.VariantPredicate;
import io.github.manasmods.tensura.data.template.function.IncreaseByScaleFunction;
import io.github.manasmods.tensura.entity.variant.OrcVariant;
import io.github.manasmods.tensura.entity.variant.PhantasporeVariant;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

public class TensuraEntityLootProvider extends EntityLootSubProvider {
   public TensuraEntityLootProvider(Provider lookup) {
      super(FeatureFlags.REGISTRY.allFlags(), lookup);
   }

   public void generate() {
      this.add((EntityType)HumanEntityTypes.BONE_GOLEM.get(), LootTable.lootTable());
      this.add((EntityType)HumanEntityTypes.CLONE.get(), LootTable.lootTable());
      this.add((EntityType)HumanEntityTypes.TRAINING_DUMMY.get(), LootTable.lootTable());
      this.add((EntityType)HumanEntityTypes.DWARF.get(), LootTable.lootTable());
      this.add((EntityType)HumanEntityTypes.GAZEL_DWARGO.get(), LootTable.lootTable());
      this.add((EntityType)HumanEntityTypes.FALMUTH_KNIGHT.get(), LootTable.lootTable());
      this.add((EntityType)HumanEntityTypes.FOLGEN.get(), LootTable.lootTable());
      this.add((EntityType)HumanEntityTypes.HINATA_SAKAGUCHI.get(), LootTable.lootTable());
      this.add(
         (EntityType)HumanEntityTypes.KIRARA_MIZUTANI.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_STONE.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)HumanEntityTypes.KYOYA_TACHIBANA.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.SPATIAL_BLADE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add((EntityType)HumanEntityTypes.MAI_FURUKI.get(), LootTable.lootTable());
      this.add((EntityType)HumanEntityTypes.MARK_LAUREN.get(), LootTable.lootTable());
      this.add(
         (EntityType)HumanEntityTypes.SHINJI_TANIMURA.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.HIGH_POTION.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
      );
      this.add((EntityType)HumanEntityTypes.SHIN_RYUSEI.get(), LootTable.lootTable());
      this.add(
         (EntityType)HumanEntityTypes.SHIZU.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraSmithingSchematicItems.ANTI_MAGIC_MASK.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add((EntityType)HumanEntityTypes.SHOGO_TAGUCHI.get(), LootTable.lootTable());
      this.add(
         (EntityType)HumanEntityTypes.SKELETON.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.BONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
      );
      this.add(
         (EntityType)HumanEntityTypes.ZOMBIE.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.ROTTEN_FLESH)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.AKASH.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.SPACE_ELEMENTAL_SHARD.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.AQUA_FROG.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMaterialItems.WATER_ELEMENTAL_SHARD.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.25F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.1F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.ARCH_DAEMON.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.5F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.ARMORSAURUS.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.RAW_ARMORSAURUS_MEAT.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ARMORSAURUS_SCALE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 8.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ARMORSAURUS_SHELL.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                  )
                  .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.ARMY_WASP.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.INSECTAR_CARAPACE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.HONEYCOMB)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.BARGHEST.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.BASILISK.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.FEATHER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_B.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DRAGON_ESSENCE.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.01F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.BEAST_GNOME.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMaterialItems.EARTH_ELEMENTAL_SHARD.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.25F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.1F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.BLACK_SPIDER.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.STICKY_THREAD.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(10.0F, 20.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 5.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.STEEL_THREAD.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(10.0F, 20.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 5.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.SPIDER_FANG.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Items.SPIDER_EYE).apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0F, 8.0F))))
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.BLADE_TIGER.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_A.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.RAW_BLADE_TIGER_MEAT.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.BLADE_TIGER_TAIL.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.CATTLEDEER.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.CATTLEDEER_BEEF.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.CHARYBDIS.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DRAGON_ESSENCE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.CHARYBDIS_SCALE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0F, 12.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.RAW_CHARYBDIS_MEAT.get())
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(12.0F, 24.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.DIREWOLF.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.DRAGON_PEACOCK.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DRAGON_PEACOCK_FEATHER.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      net.minecraft.advancements.critereon.EntityPredicate.Builder summonedSpawnType = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(ExistencePointPredicate.getSpawnBlackList(MobSpawnType.MOB_SUMMONED));
      net.minecraft.advancements.critereon.EntityPredicate.Builder triggeredSpawnType = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(ExistencePointPredicate.getSpawnBlackList(MobSpawnType.TRIGGERED));
      this.add(
         (EntityType)MonsterEntityTypes.ELEMENTAL_COLOSSUS.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraBlocks.Items.PURE_MAGISTEEL_BLOCK.get())
                           .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, triggeredSpawnType)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get())
                              .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, summonedSpawnType)))
                           .when(LootItemRandomChanceCondition.randomChance(0.1F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.EVIL_CENTIPEDE.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.CENTIPEDE_STINGER.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.FEATHERED_SERPENT.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMaterialItems.WIND_ELEMENTAL_SHARD.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.25F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.1F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.GIANT_ANT.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.GIANT_ANT_CARAPACE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.GIANT_ANT_LEG.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 6.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.GIANT_BAT.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.GIANT_BAT_WING.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.RAW_GIANT_BAT_MEAT.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.GIANT_BEAR.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.GIANT_COD.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.COD)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.BONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.GIANT_SALMON.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.SALMON)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.BONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.GOBLIN.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.LEATHER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Items.BONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.GREATER_DAEMON.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.1F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      net.minecraft.advancements.critereon.EntityPredicate.Builder hellCocoon = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(new HellCaterpillarPredicate(Optional.of(false), Optional.of(true)));
      net.minecraft.advancements.critereon.EntityPredicate.Builder hellCaterpillar = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(new HellCaterpillarPredicate(Optional.of(false), Optional.of(false)));
      net.minecraft.advancements.critereon.EntityPredicate.Builder gehennaCaterpillar = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(new HellCaterpillarPredicate(Optional.of(true), Optional.of(false)));
      this.add(
         (EntityType)MonsterEntityTypes.HELL_CATERPILLAR.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.HELL_MOTH_SILK.get())
                           .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, hellCocoon)))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0F, 12.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                        .otherwise(
                           ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.HELL_MOTH_SILK.get())
                                 .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, hellCaterpillar)))
                              .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                              .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                              .otherwise(
                                 ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.GEHENNA_MOTH_SILK.get())
                                       .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, gehennaCaterpillar)))
                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                                    .otherwise(
                                       LootItem.lootTableItem((ItemLike)TensuraMobDropItems.GEHENNA_MOTH_SILK.get())
                                          .apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0F, 12.0F)))
                                          .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                                    )
                              )
                        )
                  )
            )
      );
      net.minecraft.advancements.critereon.EntityPredicate.Builder gehennaMoth = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(new HellCaterpillarPredicate(Optional.of(true), Optional.empty()));
      this.add(
         (EntityType)MonsterEntityTypes.HELL_MOTH.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.GEHENNA_MOTH_SILK.get())
                           .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, gehennaMoth)))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                        .otherwise(
                           LootItem.lootTableItem((ItemLike)TensuraMobDropItems.HELL_MOTH_SILK.get())
                              .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F)))
                              .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                        )
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.HORNED_BEAR.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.BEAST_HORN.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.HORNED_RABBIT.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.BEAST_HORN.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Items.RABBIT_FOOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F))))
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.HOUND_DOG.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_D.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.HOVER_LIZARD.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.IFRIT.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.FIRE_ELEMENTAL_SHARD.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  )
            )
      );
      this.add((EntityType)MonsterEntityTypes.IFRIT_CLONE.get(), LootTable.lootTable());
      this.add(
         (EntityType)MonsterEntityTypes.KNIGHT_SPIDER.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.KNIGHT_SPIDER_CARAPACE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.KNIGHT_SPIDER_LEG.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.LANDFISH.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.PRISMARINE_SHARD)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.LEECH_LIZARD.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.LESSER_DAEMON.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DAEMON_ESSENCE.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.01F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      net.minecraft.advancements.critereon.EntityPredicate.Builder dragonewt = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(EvolutionStatePredicate.getDefault(1));
      this.add(
         (EntityType)MonsterEntityTypes.LIZARDMAN.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(2.0F))
                  .add(
                     LootItem.lootTableItem(Items.COD)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
                  .add(
                     LootItem.lootTableItem(Items.SALMON)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Items.BONE_MEAL).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F))))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.DRAGON_ESSENCE.get())
                              .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, dragonewt)))
                           .when(LootItemRandomChanceCondition.randomChance(0.25F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.MEGALODON.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.BONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.RAW_MEGALODON_MEAT.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0F, 10.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.ONE_EYED_OWL.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(Items.ENDER_EYE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F))))
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.INVISIBLE_FEATHER.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
      );
      net.minecraft.advancements.critereon.EntityPredicate.Builder royalOrc = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(VariantPredicate.getDefault(OrcVariant.ROYAL.toString()));
      this.add(
         (EntityType)MonsterEntityTypes.ORC.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.BONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.PORKCHOP)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ROYAL_BLOOD.get())
                              .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, royalOrc)))
                           .when(LootItemRandomChanceCondition.randomChance(0.25F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.ORC_LORD.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.BONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 5.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.PORKCHOP)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 5.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ROYAL_BLOOD.get()).when(LootItemRandomChanceCondition.randomChance(0.5F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.ORC_DISASTER.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.BONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 5.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.PORKCHOP)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 7.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ORC_DISASTER_HEAD.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ROYAL_BLOOD.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.PEGASUS.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.FEATHER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_C.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.PEGACORN.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.UNICORN_HORN.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.FEATHER)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_A.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      net.minecraft.advancements.critereon.EntityPredicate.Builder normal = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(VariantPredicate.getDefault(PhantasporeVariant.DEFAULT.toString()));
      net.minecraft.advancements.critereon.EntityPredicate.Builder red = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(VariantPredicate.getDefault(PhantasporeVariant.RED.toString()));
      net.minecraft.advancements.critereon.EntityPredicate.Builder brown = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(VariantPredicate.getDefault(PhantasporeVariant.BROWN.toString()));
      net.minecraft.advancements.critereon.EntityPredicate.Builder crimson = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(VariantPredicate.getDefault(PhantasporeVariant.CRIMSON.toString()));
      net.minecraft.advancements.critereon.EntityPredicate.Builder warped = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(VariantPredicate.getDefault(PhantasporeVariant.WARPED.toString()));
      this.add(
         (EntityType)MonsterEntityTypes.PHANTASPORE.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .when(
                     LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, normal)
                        .or(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, red))
                  )
                  .add(
                     LootItem.lootTableItem(Items.RED_MUSHROOM_BLOCK)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .when(
                     LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, normal)
                        .or(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, red))
                  )
                  .add(
                     LootItem.lootTableItem(Items.RED_MUSHROOM)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, brown))
                  .add(
                     LootItem.lootTableItem(Items.BROWN_MUSHROOM_BLOCK)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, brown))
                  .add(
                     LootItem.lootTableItem(Items.BROWN_MUSHROOM)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, crimson))
                  .add(
                     LootItem.lootTableItem(Items.CRIMSON_HYPHAE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, crimson))
                  .add(
                     LootItem.lootTableItem(Items.CRIMSON_FUNGUS)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, warped))
                  .add(
                     LootItem.lootTableItem(Items.WARPED_HYPHAE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, warped))
                  .add(
                     LootItem.lootTableItem(Items.WARPED_FUNGUS)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.SALAMANDER.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMaterialItems.FIRE_ELEMENTAL_SHARD.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.25F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.1F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.SISSIE.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(UniformGenerator.between(0.0F, 1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.SISSIE_TOOTH.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.SISSIE_FIN.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.RAW_SISSIE_MEAT.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.BONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 3.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      net.minecraft.advancements.critereon.EntityPredicate.Builder normalSlime = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(new SlimePredicate(Optional.of(false), Optional.empty()));
      net.minecraft.advancements.critereon.EntityPredicate.Builder massiveSlime = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(new SlimePredicate(Optional.empty(), Optional.of(true)));
      this.add(
         (EntityType)MonsterEntityTypes.SLIME.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.SLIME_CHUNK.get())
                           .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, normalSlime)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                        .apply(IncreaseByScaleFunction.scaleMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                        .otherwise(
                           LootItem.lootTableItem((ItemLike)TensuraConsumableItems.CHILLED_SLIME.get())
                              .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                              .apply(IncreaseByScaleFunction.scaleMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                        )
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.SLIME_CORE.get())
                           .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, massiveSlime)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.METAL_SLIME.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.MAGIC_ORE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0F, 8.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.SLIME_CORE.get())
                           .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, massiveSlime)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.SUPERMASSIVE_SLIME.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.SLIME_CHUNK.get())
                           .when(LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, normalSlime)))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(30.0F, 50.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 5.0F)))
                        .otherwise(
                           LootItem.lootTableItem((ItemLike)TensuraConsumableItems.CHILLED_SLIME.get())
                              .apply(SetItemCountFunction.setCount(UniformGenerator.between(30.0F, 50.0F)))
                              .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 5.0F)))
                        )
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem((ItemLike)TensuraMobDropItems.SLIME_CORE.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.SPEAR_TORO.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.SPEAR_TORO_FIN.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 2.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.RAW_SPEAR_TORO_MEAT.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(Items.BONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.SYLPHIDE.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.WIND_ELEMENTAL_SHARD.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.UNDINE.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.WATER_ELEMENTAL_SHARD.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.UNICORN.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.UNICORN_HORN.get()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.MONSTER_LEATHER_B.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.TEMPEST_SERPENT.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.SERPENT_SCALE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 6.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraConsumableItems.RAW_SERPENT_MEAT.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 3.0F)))
                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.WAR_GNOME.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem((ItemLike)TensuraMaterialItems.EARTH_ELEMENTAL_SHARD.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                  )
            )
      );
      this.add(
         (EntityType)MonsterEntityTypes.WINGED_CAT.get(),
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMaterialItems.SPACE_ELEMENTAL_SHARD.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.25F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
            .withPool(
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     ((Builder)LootItem.lootTableItem((ItemLike)TensuraMobDropItems.ELEMENTAL_ESSENCE.get())
                           .when(LootItemRandomChanceCondition.randomChance(0.1F)))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                  )
            )
      );
   }

   @NotNull
   protected Stream<EntityType<?>> getKnownEntityTypes() {
      return BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace().equals("tensura"));
   }
}
