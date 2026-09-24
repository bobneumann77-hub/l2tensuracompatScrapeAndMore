package io.github.manasmods.tensura.registry.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.data.template.critereon.AdditionalRacialPredicate;
import io.github.manasmods.tensura.data.template.critereon.EvolutionStatePredicate;
import io.github.manasmods.tensura.data.template.critereon.ExistencePointPredicate;
import io.github.manasmods.tensura.data.template.critereon.HellCaterpillarPredicate;
import io.github.manasmods.tensura.data.template.critereon.SlimePredicate;
import io.github.manasmods.tensura.data.template.critereon.TamedPredicate;
import io.github.manasmods.tensura.data.template.critereon.VariantPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.MobSpawnType;

public class TensuraCritereonPredicates {
   public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> PREDICATES = DeferredRegister.create(
      "tensura", Registries.ENTITY_SUB_PREDICATE_TYPE
   );
   public static final RegistrySupplier<MapCodec<AdditionalRacialPredicate>> ADDITIONAL_RACIAL_PREDICATE = PREDICATES.register(
      "additional_racial_predicate", () -> AdditionalRacialPredicate.CODEC
   );
   public static final RegistrySupplier<MapCodec<EvolutionStatePredicate>> EVOLUTION_STATE_PREDICATE = PREDICATES.register(
      "evolution_state_predicate", () -> EvolutionStatePredicate.CODEC
   );
   public static final RegistrySupplier<MapCodec<ExistencePointPredicate>> EXISTENCE_POINT_PREDICATE = PREDICATES.register(
      "existence_point_predicate", () -> ExistencePointPredicate.CODEC
   );
   public static final RegistrySupplier<MapCodec<HellCaterpillarPredicate>> HELL_CATERPILLAR_PREDICATE = PREDICATES.register(
      "hell_caterpillar_predicate", () -> HellCaterpillarPredicate.CODEC
   );
   public static final RegistrySupplier<MapCodec<SlimePredicate>> SLIME_PREDICATE = PREDICATES.register("slime_predicate", () -> SlimePredicate.CODEC);
   public static final RegistrySupplier<MapCodec<TamedPredicate>> TAMED_PREDICATE = PREDICATES.register("tamed_predicate", () -> TamedPredicate.CODEC);
   public static final RegistrySupplier<MapCodec<VariantPredicate>> VARIANT_PREDICATE = PREDICATES.register("variant_predicate", () -> VariantPredicate.CODEC);
   public static final Codec<MobSpawnType> SPAWN_TYPE_CODEC = Codec.STRING.xmap(name -> MobSpawnType.valueOf(name.toUpperCase()), Enum::name);

   public static void init() {
      PREDICATES.register();
   }
}
