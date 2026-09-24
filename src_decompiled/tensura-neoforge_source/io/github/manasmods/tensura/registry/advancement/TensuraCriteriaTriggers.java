package io.github.manasmods.tensura.registry.advancement;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.advancement.AbilityTrigger;
import io.github.manasmods.tensura.advancement.ExistenceGainTrigger;
import io.github.manasmods.tensura.advancement.ItemUsedOnEntityTrigger;
import io.github.manasmods.tensura.advancement.TradeTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;

public class TensuraCriteriaTriggers {
   public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create("tensura", Registries.TRIGGER_TYPE);
   public static final RegistrySupplier<PlayerTrigger> REINCARNATED = TRIGGERS.register("reincarnated", PlayerTrigger::new);
   public static final RegistrySupplier<PlayerTrigger> NAME_ENTITY = TRIGGERS.register("name", PlayerTrigger::new);
   public static final RegistrySupplier<PlayerTrigger> LEARN_ALL_SCHEMATICS = TRIGGERS.register("all_schematics", PlayerTrigger::new);
   public static final RegistrySupplier<ExistenceGainTrigger> EXISTENCE_GAIN = TRIGGERS.register("existence_gain", ExistenceGainTrigger::new);
   public static final RegistrySupplier<ItemUsedOnEntityTrigger> ITEM_USED_ON_ENTITY = TRIGGERS.register("item_used_on_entity", ItemUsedOnEntityTrigger::new);
   public static final RegistrySupplier<TradeTrigger> TRADE = TRIGGERS.register("trade", TradeTrigger::new);
   public static final RegistrySupplier<PlayerTrigger> EVOLVE_RACE = TRIGGERS.register("evolve_race", PlayerTrigger::new);
   public static final RegistrySupplier<PlayerTrigger> AWAKEN_RACE = TRIGGERS.register("awaken_race", PlayerTrigger::new);
   public static final RegistrySupplier<PlayerTrigger> DIED_TO_COLOSSUS = TRIGGERS.register("died_to_colossus", PlayerTrigger::new);
   public static final RegistrySupplier<PlayerTrigger> SPIRIT_CONTRACTED = TRIGGERS.register("spirit_contracted", PlayerTrigger::new);
   public static final RegistrySupplier<PlayerTrigger> SPIRIT_BLESSED = TRIGGERS.register("spirit_blessed", PlayerTrigger::new);
   public static final RegistrySupplier<PlayerTrigger> MAX_SLOTTING_USED = TRIGGERS.register("max_slotting_used", PlayerTrigger::new);
   public static final RegistrySupplier<AbilityTrigger> ABILITY_OBTAINED = TRIGGERS.register("ability_obtained", AbilityTrigger::new);
   public static final RegistrySupplier<AbilityTrigger> SPECIAL_ACTIVATION = TRIGGERS.register("special_activation", AbilityTrigger::new);
   public static final RegistrySupplier<AbilityTrigger> BATTLEWILL_LEARNT = TRIGGERS.register("battlewill_learnt", AbilityTrigger::new);
   public static final RegistrySupplier<AbilityTrigger> MAGIC_LEARNT = TRIGGERS.register("magic_learnt", AbilityTrigger::new);
   public static final RegistrySupplier<AbilityTrigger> SKILL_LEARNT = TRIGGERS.register("skill_learnt", AbilityTrigger::new);
   public static final RegistrySupplier<AbilityTrigger> BATTLEWILL_MASTERED = TRIGGERS.register("battlewill_mastered", AbilityTrigger::new);
   public static final RegistrySupplier<AbilityTrigger> MAGIC_MASTERED = TRIGGERS.register("magic_mastered", AbilityTrigger::new);
   public static final RegistrySupplier<AbilityTrigger> SKILL_MASTERED = TRIGGERS.register("skill_mastered", AbilityTrigger::new);
   public static final RegistrySupplier<AbilityTrigger> UNIQUE_SKILL_MASTERED = TRIGGERS.register("unique_skill_mastered", AbilityTrigger::new);

   public static void init() {
      TRIGGERS.register();
   }
}
