package io.github.manasmods.tensura.registry.item.misc;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.enchantment.effect.AdditionalDamageEntity;
import io.github.manasmods.tensura.enchantment.effect.ApplySaturationEntity;
import io.github.manasmods.tensura.enchantment.effect.BarrierPiercingEntity;
import io.github.manasmods.tensura.enchantment.effect.DeadEndRainbowEntity;
import io.github.manasmods.tensura.enchantment.effect.ElementalCoreDamageEntity;
import io.github.manasmods.tensura.enchantment.effect.EnergyStealEntity;
import io.github.manasmods.tensura.enchantment.effect.SeveranceDamageEntity;
import io.github.manasmods.tensura.enchantment.effect.SpiritualDamageEntity;
import io.github.manasmods.tensura.enchantment.effect.WeaponBaseAttributeMultiplier;
import java.util.List;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class TensuraEnchantmentEffectComponents {
   public static final DeferredRegister<DataComponentType<?>> TYPES = DeferredRegister.create("tensura", Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE);
   public static final RegistrySupplier<DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> AFTER_ATTACK = TYPES.register(
      "after_attack",
      () -> DataComponentType.builder()
         .persistent(TargetedConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf())
         .build()
   );
   public static final RegistrySupplier<DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> AFTER_DAMAGE = TYPES.register(
      "after_damage",
      () -> DataComponentType.builder()
         .persistent(TargetedConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf())
         .build()
   );
   public static final RegistrySupplier<DataComponentType<List<WeaponBaseAttributeMultiplier>>> WEAPON_MULTIPLIER = TYPES.register(
      "weapon_multiplier", () -> DataComponentType.builder().persistent(WeaponBaseAttributeMultiplier.CODEC.codec().listOf()).build()
   );
   public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_EFFECTS = DeferredRegister.create(
      "tensura", Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE
   );
   public static final RegistrySupplier<MapCodec<ApplySaturationEntity>> APPLY_SATURATION = ENTITY_EFFECTS.register(
      "apply_saturation", () -> ApplySaturationEntity.CODEC
   );
   public static final RegistrySupplier<MapCodec<AdditionalDamageEntity>> ADDITIONAL_DAMAGE = ENTITY_EFFECTS.register(
      "additional_damage", () -> AdditionalDamageEntity.CODEC
   );
   public static final RegistrySupplier<MapCodec<BarrierPiercingEntity>> BARRIER_PIERCING = ENTITY_EFFECTS.register(
      "barrier_piercing", () -> BarrierPiercingEntity.CODEC
   );
   public static final RegistrySupplier<MapCodec<DeadEndRainbowEntity>> DEAD_END_RAINBOW = ENTITY_EFFECTS.register(
      "dead_end_rainbow", () -> DeadEndRainbowEntity.CODEC
   );
   public static final RegistrySupplier<MapCodec<ElementalCoreDamageEntity>> ELEMENTAL_CORE_DAMAGE = ENTITY_EFFECTS.register(
      "elemental_core_damage", () -> ElementalCoreDamageEntity.CODEC
   );
   public static final RegistrySupplier<MapCodec<EnergyStealEntity>> ENERGY_STEAL = ENTITY_EFFECTS.register("energy_steal", () -> EnergyStealEntity.CODEC);
   public static final RegistrySupplier<MapCodec<SeveranceDamageEntity>> SEVERANCE = ENTITY_EFFECTS.register("severance", () -> SeveranceDamageEntity.CODEC);
   public static final RegistrySupplier<MapCodec<SpiritualDamageEntity>> SPIRITUAL_DAMAGE = ENTITY_EFFECTS.register(
      "spiritual_damage", () -> SpiritualDamageEntity.CODEC
   );

   public static void init() {
      TYPES.register();
      ENTITY_EFFECTS.register();
   }
}
