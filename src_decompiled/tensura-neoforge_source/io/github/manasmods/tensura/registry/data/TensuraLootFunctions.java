package io.github.manasmods.tensura.registry.data;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.data.template.function.ApplySkillDataFunction;
import io.github.manasmods.tensura.data.template.function.IncreaseByScaleFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class TensuraLootFunctions {
   public static final DeferredRegister<LootItemFunctionType<?>> FUNCTIONS = DeferredRegister.create("tensura", Registries.LOOT_FUNCTION_TYPE);
   public static final RegistrySupplier<LootItemFunctionType<ApplySkillDataFunction>> APPLY_SKILL_DATA = FUNCTIONS.register(
      "apply_skill_data", () -> new LootItemFunctionType(ApplySkillDataFunction.CODEC)
   );
   public static final RegistrySupplier<LootItemFunctionType<IncreaseByScaleFunction>> INCREASE_BY_SCALE = FUNCTIONS.register(
      "increase_by_scale", () -> new LootItemFunctionType(IncreaseByScaleFunction.CODEC)
   );

   public static void init() {
      FUNCTIONS.register();
   }
}
