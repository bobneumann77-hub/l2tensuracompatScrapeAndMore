package io.github.manasmods.tensura.registry.entity.ai;

import com.google.common.collect.ImmutableSet;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.registry.block.TensuraPoiTypes;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class TensuraVillagerProfessions {
   private static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create("tensura", Registries.VILLAGER_PROFESSION);
   public static RegistrySupplier<VillagerProfession> BATTLEWILL_TRAINER = register(
      "battlewill_trainer", TensuraPoiTypes.BATTLEWILL_TRAINER.getKey(), SoundEvents.ARMOR_STAND_HIT
   );
   public static RegistrySupplier<VillagerProfession> GUARD = register("guard", TensuraPoiTypes.GUARD.getKey(), SoundEvents.VILLAGER_WORK_WEAPONSMITH);
   public static RegistrySupplier<VillagerProfession> LUMBERJACK = register(
      "lumberjack", TensuraPoiTypes.LUMBERJACK.getKey(), SoundEvents.UI_STONECUTTER_TAKE_RESULT
   );
   public static RegistrySupplier<VillagerProfession> MAGIC_TRAINER = register(
      "magic_trainer", TensuraPoiTypes.MAGIC_TRAINER.getKey(), SoundEvents.ENCHANTMENT_TABLE_USE
   );
   public static RegistrySupplier<VillagerProfession> MINER = register("miner", TensuraPoiTypes.MINER.getKey(), SoundEvents.VILLAGER_WORK_MASON);
   public static RegistrySupplier<VillagerProfession> MERCHANT = register("merchant", SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
   public static RegistrySupplier<VillagerProfession> ROYAL_GUARD = register("royal_guard", SoundEvents.VILLAGER_WORK_ARMORER);

   public static void init() {
      PROFESSIONS.register();
   }

   private static RegistrySupplier<VillagerProfession> register(String name, ResourceKey<PoiType> poi, @Nullable SoundEvent workSound) {
      return register(name, poi, ImmutableSet.of(), ImmutableSet.of(), workSound);
   }

   private static RegistrySupplier<VillagerProfession> register(
      String name, Predicate<Holder<PoiType>> predicate, Predicate<Holder<PoiType>> acquirableJobSite, @Nullable SoundEvent workSound
   ) {
      return register(name, predicate, acquirableJobSite, ImmutableSet.of(), ImmutableSet.of(), workSound);
   }

   private static RegistrySupplier<VillagerProfession> register(
      String name, ResourceKey<PoiType> poi, ImmutableSet<Item> requestedItems, ImmutableSet<Block> secondaryPoi, @Nullable SoundEvent workSound
   ) {
      return register(name, holder -> holder.is(poi), holder -> holder.is(poi), requestedItems, secondaryPoi, workSound);
   }

   private static RegistrySupplier<VillagerProfession> register(String name, @Nullable SoundEvent workSound) {
      return register(name, holder -> false, holder -> false, ImmutableSet.of(), ImmutableSet.of(), workSound);
   }

   private static RegistrySupplier<VillagerProfession> register(
      String name,
      Predicate<Holder<PoiType>> predicate,
      Predicate<Holder<PoiType>> acquirableJobSite,
      ImmutableSet<Item> requestedItems,
      ImmutableSet<Block> secondaryPoi,
      @Nullable SoundEvent workSound
   ) {
      return PROFESSIONS.register(name, () -> new VillagerProfession(name, predicate, acquirableJobSite, requestedItems, secondaryPoi, workSound));
   }
}
