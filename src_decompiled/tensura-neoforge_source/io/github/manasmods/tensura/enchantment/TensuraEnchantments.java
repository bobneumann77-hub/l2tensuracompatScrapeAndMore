package io.github.manasmods.tensura.enchantment;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.data.template.critereon.AdditionalRacialPredicate;
import io.github.manasmods.tensura.enchantment.effect.AdditionalDamageEntity;
import io.github.manasmods.tensura.enchantment.effect.ApplySaturationEntity;
import io.github.manasmods.tensura.enchantment.effect.BarrierPiercingEntity;
import io.github.manasmods.tensura.enchantment.effect.DeadEndRainbowEntity;
import io.github.manasmods.tensura.enchantment.effect.ElementalCoreDamageEntity;
import io.github.manasmods.tensura.enchantment.effect.EnergyStealEntity;
import io.github.manasmods.tensura.enchantment.effect.SeveranceDamageEntity;
import io.github.manasmods.tensura.enchantment.effect.SpiritualDamageEntity;
import io.github.manasmods.tensura.enchantment.effect.WeaponBaseAttributeMultiplier;
import io.github.manasmods.tensura.enchantment.template.EnchantmentPostDamageWithTypeEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate.Builder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CrossbowItem.ChargingSounds;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.LevelBasedValue.Fraction;
import net.minecraft.world.item.enchantment.LevelBasedValue.LevelsSquared;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.DamageItem;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.MultiplyValue;
import net.minecraft.world.item.enchantment.effects.RemoveBinomial;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public class TensuraEnchantments {
   public static final ResourceKey<Enchantment> BARRIER_PIERCING = create("barrier_piercing");
   public static final ResourceKey<Enchantment> BREATHING_SUPPORT = create("breathing_support");
   public static final ResourceKey<Enchantment> CRUSHING = create("crushing");
   public static final ResourceKey<Enchantment> ENERGY_STEAL = create("energy_steal");
   public static final ResourceKey<Enchantment> ELEMENTAL_BOOST = create("elemental_boost");
   public static final ResourceKey<Enchantment> ELEMENTAL_RESISTANCE = create("elemental_resistance");
   public static final ResourceKey<Enchantment> ENERGY_PROTECTION = create("energy_protection");
   public static final ResourceKey<Enchantment> HOLY_WEAPON = create("holy_weapon");
   public static final ResourceKey<Enchantment> INTANGIBILITY = create("intangibility");
   public static final ResourceKey<Enchantment> MAGIC_WEAPON = create("magic_weapon");
   public static final ResourceKey<Enchantment> MAGIC_CAPACITY = create("magic_capacity");
   public static final ResourceKey<Enchantment> MAGIC_PROTECTION = create("magic_protection");
   public static final ResourceKey<Enchantment> MAGICULE_ABSORPTION = create("magicule_absorption");
   public static final ResourceKey<Enchantment> SEVERANCE = create("severance");
   public static final ResourceKey<Enchantment> SLOTTING = create("slotting");
   public static final ResourceKey<Enchantment> SOUL_EATER = create("soul_eater");
   public static final ResourceKey<Enchantment> SPIRITUAL_PROTECTION = create("spiritual_protection");
   public static final ResourceKey<Enchantment> SEVERANCE_PROTECTION = create("severance_protection");
   public static final ResourceKey<Enchantment> STURDY = create("sturdy");
   public static final ResourceKey<Enchantment> SWIFT = create("swift");
   public static final ResourceKey<Enchantment> DEAD_END_RAINBOW = create("dead_end_rainbow");
   public static final ResourceKey<Enchantment> HOLY_COAT = create("holy_coat");
   public static final ResourceKey<Enchantment> MAGIC_INTERFERENCE = create("magic_interference");
   public static final ResourceKey<Enchantment> TSUKUMOGAMI = create("tsukumogami");
   public static final ResourceKey<Enchantment> ENERVATION = create("enervation");
   public static final ResourceKey<Enchantment> LETHARGY = create("lethargy");
   public static final ResourceKey<Enchantment> SEALING = create("sealing");
   public static final ResourceKey<Enchantment> STAGNATION = create("stagnation");
   public static final ResourceKey<Enchantment> RUINATION = create("ruination");
   public static final ResourceKey<Enchantment> VITALITY = create("vitality");
   public static final ResourceKey<Enchantment> VIGOR = create("vigor");
   public static final ResourceKey<Enchantment> TRANSCENDENCE = create("transcendence");
   public static final ResourceKey<Enchantment> GROWTH = create("growth");
   public static final ResourceKey<Enchantment> RESTORATION = create("restoration");

   public static void bootstrap(BootstrapContext<Enchantment> context) {
      HolderGetter<Item> items = context.lookup(Registries.ITEM);
      HolderGetter<DamageType> damageTypes = context.lookup(Registries.DAMAGE_TYPE);
      HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
      addTsukumogami(context, items, enchantments);
      context.register(
         Enchantments.PROTECTION,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                  10,
                  4,
                  Enchantment.dynamicCost(1, 11),
                  Enchantment.dynamicCost(12, 11),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ARMOR}
               )
            )
            .withEffect(
               EnchantmentEffectComponents.DAMAGE_PROTECTION,
               new AddValue(LevelBasedValue.perLevel(1.0F)),
               DamageSourceCondition.hasDamageSource(
                  Builder.damageType()
                     .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))
                     .tag(TagPredicate.isNot(TensuraTags.DamageTypes.BYPASS_PROTECTION_ENCHANTMENT))
               )
            )
            .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE))
            .build(Enchantments.PROTECTION.location())
      );
      context.register(
         CRUSHING,
         addWeaponMultiplier(
               Enchantment.enchantment(
                  Enchantment.definition(
                     items.getOrThrow(TensuraItemTags.HANDHELD_ENCHANTABLE),
                     1,
                     1,
                     Enchantment.constantCost(50),
                     Enchantment.dynamicCost(50, 50),
                     1,
                     new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
                  )
               ),
               new WeaponBaseAttributeMultiplier(
                  ResourceLocation.withDefaultNamespace("enchantment.crushing"),
                  Attributes.ATTACK_DAMAGE,
                  LevelBasedValue.perLevel(0.5F),
                  Optional.empty(),
                  EquipmentSlotGroup.MAINHAND,
                  false
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.crushing"), Attributes.MINING_EFFICIENCY, new LevelsSquared(4.0F), Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER, EnchantmentTarget.ATTACKER, new DamageItem(LevelBasedValue.perLevel(5.0F))
            )
            .withEffect(
               EnchantmentEffectComponents.DAMAGE,
               new AddValue(LevelBasedValue.perLevel(0.5F)),
               LootItemEntityPropertyCondition.hasProperties(
                  EntityTarget.DIRECT_ATTACKER, net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS).build()
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(CRUSHING.location())
      );
      context.register(
         STURDY,
         addWeaponMultiplier(
               Enchantment.enchantment(
                  Enchantment.definition(
                     items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                     1,
                     1,
                     Enchantment.constantCost(50),
                     Enchantment.dynamicCost(50, 50),
                     1,
                     new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
                  )
               ),
               new WeaponBaseAttributeMultiplier(
                  ResourceLocation.withDefaultNamespace("enchantment.sturdy"),
                  Attributes.ATTACK_DAMAGE,
                  LevelBasedValue.perLevel(0.3F),
                  Optional.empty(),
                  EquipmentSlotGroup.MAINHAND,
                  false
               ),
               new WeaponBaseAttributeMultiplier(
                  ResourceLocation.withDefaultNamespace("enchantment.sturdy"),
                  Attributes.ARMOR,
                  LevelBasedValue.perLevel(0.1F),
                  Optional.empty(),
                  EquipmentSlotGroup.ARMOR,
                  false
               ),
               new WeaponBaseAttributeMultiplier(
                  ResourceLocation.withDefaultNamespace("enchantment.sturdy"),
                  Attributes.MINING_EFFICIENCY,
                  LevelBasedValue.perLevel(3.0F),
                  Optional.empty(),
                  EquipmentSlotGroup.MAINHAND,
                  true
               )
            )
            .withEffect(
               EnchantmentEffectComponents.DAMAGE,
               new AddValue(LevelBasedValue.perLevel(0.3F)),
               LootItemEntityPropertyCondition.hasProperties(
                  EntityTarget.DIRECT_ATTACKER, net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS).build()
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ITEM_DAMAGE,
               new RemoveBinomial(new Fraction(LevelBasedValue.perLevel(10.0F), LevelBasedValue.perLevel(10.0F, 5.0F))),
               MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.ARMOR_ENCHANTABLE))
            )
            .withEffect(
               EnchantmentEffectComponents.ITEM_DAMAGE,
               new RemoveBinomial(new Fraction(LevelBasedValue.perLevel(5.0F), LevelBasedValue.perLevel(2.0F, 1.0F))),
               InvertedLootItemCondition.invert(
                  MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(ItemTags.ARMOR_ENCHANTABLE))
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(STURDY.location())
      );
      context.register(
         SWIFT,
         addWeaponMultiplier(
               Enchantment.enchantment(
                  Enchantment.definition(
                     items.getOrThrow(TensuraItemTags.HANDHELD_ENCHANTABLE),
                     1,
                     1,
                     Enchantment.constantCost(50),
                     Enchantment.dynamicCost(50, 50),
                     1,
                     new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
                  )
               ),
               new WeaponBaseAttributeMultiplier(
                  ResourceLocation.withDefaultNamespace("enchantment.swift"),
                  Attributes.ATTACK_DAMAGE,
                  LevelBasedValue.perLevel(0.1F),
                  Optional.empty(),
                  EquipmentSlotGroup.MAINHAND,
                  false
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.swift"), Attributes.ATTACK_SPEED, LevelBasedValue.perLevel(0.2F), Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.swift"),
                  Attributes.ENTITY_INTERACTION_RANGE,
                  LevelBasedValue.perLevel(1.0F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.swift"), Attributes.MINING_EFFICIENCY, new LevelsSquared(2.0F), Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.DAMAGE,
               new AddValue(LevelBasedValue.perLevel(0.1F)),
               LootItemEntityPropertyCondition.hasProperties(
                  EntityTarget.DIRECT_ATTACKER, net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS).build()
               )
            )
            .withSpecialEffect(EnchantmentEffectComponents.CROSSBOW_CHARGE_TIME, new AddValue(LevelBasedValue.perLevel(-0.5F)))
            .withSpecialEffect(
               EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS,
               List.of(
                  new ChargingSounds(Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_1), Optional.empty(), Optional.of(SoundEvents.CROSSBOW_LOADING_END)),
                  new ChargingSounds(Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_2), Optional.empty(), Optional.of(SoundEvents.CROSSBOW_LOADING_END)),
                  new ChargingSounds(Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_3), Optional.empty(), Optional.of(SoundEvents.CROSSBOW_LOADING_END))
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(SWIFT.location())
      );
      context.register(
         MAGIC_WEAPON,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(TensuraItemTags.HANDHELD_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .withEffect(
               (DataComponentType)TensuraEnchantmentEffectComponents.AFTER_ATTACK.get(),
               EnchantmentTarget.DAMAGING_ENTITY,
               EnchantmentTarget.VICTIM,
               new AdditionalDamageEntity(
                  LevelBasedValue.perLevel(1.0F),
                  EnchantmentPostDamageWithTypeEffect.DamageCalculationType.TOTAL_ATTACK_MULTIPLY,
                  damageTypes.getOrThrow(TensuraDamageTypes.MAGIC_GENERIC)
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(MAGIC_WEAPON.location())
      );
      context.register(
         HOLY_WEAPON,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(TensuraItemTags.HANDHELD_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .withEffect(
               (DataComponentType)TensuraEnchantmentEffectComponents.AFTER_ATTACK.get(),
               EnchantmentTarget.DAMAGING_ENTITY,
               EnchantmentTarget.VICTIM,
               new AdditionalDamageEntity(
                  LevelBasedValue.perLevel(1.0F),
                  EnchantmentPostDamageWithTypeEffect.DamageCalculationType.TOTAL_ATTACK_MULTIPLY,
                  damageTypes.getOrThrow(TensuraDamageTypes.HOLY_DAMAGE)
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(HOLY_WEAPON.location())
      );
      context.register(
         INTANGIBILITY,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(TensuraItemTags.HANDHELD_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .withEffect(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS, new AddValue(LevelBasedValue.perLevel(-1.0F)))
            .withEffect(EnchantmentEffectComponents.PROJECTILE_PIERCING, new AddValue(LevelBasedValue.perLevel(5.0F)))
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(INTANGIBILITY.location())
      );
      context.register(
         ENERGY_STEAL,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(TensuraItemTags.HANDHELD_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .withEffect(
               (DataComponentType)TensuraEnchantmentEffectComponents.AFTER_DAMAGE.get(),
               EnchantmentTarget.ATTACKER,
               EnchantmentTarget.VICTIM,
               new EnergyStealEntity(LevelBasedValue.perLevel(0.01F), 20)
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(ENERGY_STEAL.location())
      );
      context.register(
         SOUL_EATER,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(TensuraItemTags.HANDHELD_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .withEffect(
               (DataComponentType)TensuraEnchantmentEffectComponents.AFTER_ATTACK.get(),
               EnchantmentTarget.ATTACKER,
               EnchantmentTarget.VICTIM,
               new SpiritualDamageEntity(
                  LevelBasedValue.perLevel(1.0F),
                  EnchantmentPostDamageWithTypeEffect.DamageCalculationType.TOTAL_ATTACK_MULTIPLY,
                  damageTypes.getOrThrow(TensuraDamageTypes.SOUL_SCATTER)
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(SOUL_EATER.location())
      );
      context.register(
         SEVERANCE,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(TensuraItemTags.HANDHELD_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .withEffect(EnchantmentEffectComponents.DAMAGE, new AddValue(LevelBasedValue.perLevel(3.0F)))
            .withEffect(
               (DataComponentType)TensuraEnchantmentEffectComponents.AFTER_DAMAGE.get(),
               EnchantmentTarget.ATTACKER,
               EnchantmentTarget.VICTIM,
               new SeveranceDamageEntity(LevelBasedValue.perLevel(0.5F), 0.5F, true, false)
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(SEVERANCE.location())
      );
      context.register(
         BARRIER_PIERCING,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(TensuraItemTags.HANDHELD_ENCHANTABLE),
                  1,
                  2,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .withEffect(
               (DataComponentType)TensuraEnchantmentEffectComponents.AFTER_ATTACK.get(),
               EnchantmentTarget.ATTACKER,
               EnchantmentTarget.VICTIM,
               new BarrierPiercingEntity(LevelBasedValue.perLevel(0.1F))
            )
            .withEffect(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS, new AddValue(LevelBasedValue.perLevel(-0.2F)))
            .withEffect(EnchantmentEffectComponents.PROJECTILE_PIERCING, new AddValue(LevelBasedValue.perLevel(3.0F)))
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(BARRIER_PIERCING.location())
      );
      context.register(
         SLOTTING,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(TensuraItemTags.HANDHELD_ENCHANTABLE),
                  1,
                  3,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .withEffect(
               (DataComponentType)TensuraEnchantmentEffectComponents.AFTER_ATTACK.get(),
               EnchantmentTarget.ATTACKER,
               EnchantmentTarget.VICTIM,
               new ElementalCoreDamageEntity(
                  LevelBasedValue.constant(1.0F), EnchantmentPostDamageWithTypeEffect.DamageCalculationType.ENCHANTED_WEAPON_DAMAGE_MULTIPLY
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(SLOTTING.location())
      );
      context.register(
         MAGIC_CAPACITY,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(TensuraItemTags.SPELL_CAST_WEAPONS),
                  1,
                  3,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(MAGIC_CAPACITY.location())
      );
      context.register(
         BREATHING_SUPPORT,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.HEAD}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(BREATHING_SUPPORT.location())
      );
      context.register(
         ELEMENTAL_BOOST,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ARMOR}
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_boost"),
                  TensuraAttributes.EARTH_BOOST,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_boost"),
                  TensuraAttributes.DARKNESS_BOOST,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_boost"),
                  TensuraAttributes.FLAME_BOOST,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_boost"),
                  TensuraAttributes.LIGHT_BOOST,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_boost"),
                  TensuraAttributes.SPACE_BOOST,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_boost"),
                  TensuraAttributes.WATER_BOOST,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_boost"),
                  TensuraAttributes.WIND_BOOST,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(ELEMENTAL_BOOST.location())
      );
      context.register(
         ELEMENTAL_RESISTANCE,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                  1,
                  2,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ARMOR}
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_resistance"),
                  TensuraAttributes.EARTH_RESISTANCE,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_resistance"),
                  TensuraAttributes.DARKNESS_RESISTANCE,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_resistance"),
                  TensuraAttributes.FLAME_RESISTANCE,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_resistance"),
                  TensuraAttributes.LIGHT_RESISTANCE,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_resistance"),
                  TensuraAttributes.SPACE_RESISTANCE,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_resistance"),
                  TensuraAttributes.WATER_RESISTANCE,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.elemental_resistance"),
                  TensuraAttributes.WIND_RESISTANCE,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(ELEMENTAL_RESISTANCE.location())
      );
      context.register(
         MAGIC_PROTECTION,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ARMOR}
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.magic_protection"),
                  TensuraAttributes.MAGIC_RESISTANCE,
                  LevelBasedValue.perLevel(0.1F),
                  Operation.ADD_VALUE
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(MAGIC_PROTECTION.location())
      );
      context.register(
         ENERGY_PROTECTION,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ARMOR}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(ENERGY_PROTECTION.location())
      );
      context.register(
         SEVERANCE_PROTECTION,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ARMOR}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(SEVERANCE_PROTECTION.location())
      );
      context.register(
         SPIRITUAL_PROTECTION,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ARMOR}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(SPIRITUAL_PROTECTION.location())
      );
      context.register(
         MAGICULE_ABSORPTION,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                  1,
                  2,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ARMOR}
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.magicule_absorption"),
                  TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER,
                  LevelBasedValue.perLevel(1.0F),
                  Operation.ADD_VALUE
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(MAGICULE_ABSORPTION.location())
      );
      net.minecraft.advancements.critereon.EntityPredicate.Builder holyCoatPredicate = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .subPredicate(new AdditionalRacialPredicate(Optional.empty(), Optional.of(true), Optional.empty(), Optional.empty()));
      context.register(
         HOLY_COAT,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .withEffect(
               EnchantmentEffectComponents.DAMAGE_PROTECTION,
               new AddValue(LevelBasedValue.perLevel(4.0F)),
               DamageSourceCondition.hasDamageSource(
                  Builder.damageType().direct(holyCoatPredicate).tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))
               )
            )
            .withEffect(
               EnchantmentEffectComponents.DAMAGE,
               new MultiplyValue(LevelBasedValue.perLevel(1.25F, 0.25F)),
               LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, holyCoatPredicate)
            )
            .withEffect(
               EnchantmentEffectComponents.DAMAGE,
               new AddValue(LevelBasedValue.perLevel(0.25F)),
               LootItemEntityPropertyCondition.hasProperties(
                  EntityTarget.DIRECT_ATTACKER, net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS).build()
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(HOLY_COAT.location())
      );
      context.register(
         DEAD_END_RAINBOW,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(TensuraItemTags.HANDHELD_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.MAINHAND}
               )
            )
            .withEffect(EnchantmentEffectComponents.ARMOR_EFFECTIVENESS, new AddValue(LevelBasedValue.perLevel(-1.0F)))
            .withEffect(
               (DataComponentType)TensuraEnchantmentEffectComponents.AFTER_ATTACK.get(),
               EnchantmentTarget.ATTACKER,
               EnchantmentTarget.VICTIM,
               new DeadEndRainbowEntity(7, LevelBasedValue.perLevel(0.1F), LevelBasedValue.perLevel(10.0F), LevelBasedValue.perLevel(500.0F), 20)
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(DEAD_END_RAINBOW.location())
      );
      context.register(
         MAGIC_INTERFERENCE,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(50),
                  Enchantment.dynamicCost(50, 50),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(MAGIC_INTERFERENCE.location())
      );
      EntitySubPredicate undead = new AdditionalRacialPredicate(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(false));
      net.minecraft.advancements.critereon.EntityPredicate.Builder builder = net.minecraft.advancements.critereon.EntityPredicate.Builder.entity()
         .periodicTick(20)
         .subPredicate(undead);
      context.register(
         ENERVATION,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  3,
                  Enchantment.constantCost(100),
                  Enchantment.dynamicCost(100, 100),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .withEffect(
               EnchantmentEffectComponents.TICK,
               new ApplySaturationEntity(
                  Optional.of(LevelBasedValue.perLevel(-2.0F)),
                  Optional.of(LevelBasedValue.constant(4.0F)),
                  Optional.of(5.0),
                  Optional.of(LevelBasedValue.perLevel(-5.0F)),
                  Optional.of(LevelBasedValue.constant(4.0F)),
                  Optional.empty(),
                  Optional.of(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().subPredicate(undead).build())
               ),
               LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, builder)
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(ENERVATION.location())
      );
      context.register(
         LETHARGY,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(100),
                  Enchantment.dynamicCost(100, 100),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.lethargy"),
                  TensuraAttributes.AURA_GAIN,
                  LevelBasedValue.perLevel(-0.2F),
                  Operation.ADD_MULTIPLIED_TOTAL
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.lethargy"),
                  TensuraAttributes.MAGICULE_GAIN,
                  LevelBasedValue.perLevel(-0.2F),
                  Operation.ADD_MULTIPLIED_TOTAL
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(LETHARGY.location())
      );
      context.register(
         SEALING,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(100),
                  Enchantment.dynamicCost(100, 100),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.SEALING_EXCLUSIVE))
            .build(SEALING.location())
      );
      context.register(
         STAGNATION,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(100),
                  Enchantment.dynamicCost(100, 100),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(STAGNATION.location())
      );
      context.register(
         RUINATION,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(100),
                  Enchantment.dynamicCost(100, 100),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .withEffect(EnchantmentEffectComponents.REPAIR_WITH_XP, new MultiplyValue(LevelBasedValue.constant(0.0F)))
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(RUINATION.location())
      );
      context.register(
         VITALITY,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  3,
                  Enchantment.constantCost(100),
                  Enchantment.dynamicCost(100, 100),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .withEffect(
               EnchantmentEffectComponents.TICK,
               new ApplySaturationEntity(
                  Optional.of(LevelBasedValue.perLevel(2.0F)),
                  Optional.empty(),
                  Optional.of(5.0),
                  Optional.of(LevelBasedValue.perLevel(5.0F)),
                  Optional.empty(),
                  Optional.empty(),
                  Optional.of(net.minecraft.advancements.critereon.EntityPredicate.Builder.entity().subPredicate(undead).build())
               ),
               LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, builder)
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(VITALITY.location())
      );
      context.register(
         VIGOR,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(100),
                  Enchantment.dynamicCost(100, 100),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.vigor"),
                  TensuraAttributes.AURA_GAIN,
                  LevelBasedValue.perLevel(0.2F),
                  Operation.ADD_MULTIPLIED_TOTAL
               )
            )
            .withEffect(
               EnchantmentEffectComponents.ATTRIBUTES,
               new EnchantmentAttributeEffect(
                  ResourceLocation.withDefaultNamespace("enchantment.vigor"),
                  TensuraAttributes.MAGICULE_GAIN,
                  LevelBasedValue.perLevel(0.2F),
                  Operation.ADD_MULTIPLIED_TOTAL
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(VIGOR.location())
      );
      context.register(
         TRANSCENDENCE,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(100),
                  Enchantment.dynamicCost(100, 100),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(TRANSCENDENCE.location())
      );
      context.register(
         GROWTH,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(100),
                  Enchantment.dynamicCost(100, 100),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(GROWTH.location())
      );
      context.register(
         RESTORATION,
         Enchantment.enchantment(
               Enchantment.definition(
                  items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                  1,
                  1,
                  Enchantment.constantCost(100),
                  Enchantment.dynamicCost(100, 100),
                  1,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
               )
            )
            .withEffect(EnchantmentEffectComponents.REPAIR_WITH_XP, new MultiplyValue(LevelBasedValue.perLevel(2.0F)))
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(RESTORATION.location())
      );
   }

   public static ResourceKey<Enchantment> create(String name) {
      return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }

   public static net.minecraft.world.item.enchantment.Enchantment.Builder addWeaponMultiplier(
      net.minecraft.world.item.enchantment.Enchantment.Builder builder, WeaponBaseAttributeMultiplier... effects
   ) {
      for (WeaponBaseAttributeMultiplier effect : effects) {
         builder.getEffectsList((DataComponentType)TensuraEnchantmentEffectComponents.WEAPON_MULTIPLIER.get()).add(effect);
      }

      return builder;
   }

   public static void addTsukumogami(BootstrapContext<Enchantment> context, HolderGetter<Item> items, HolderGetter<Enchantment> enchantments) {
      float[] inactiveValues = new float[]{0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F};
      List<WeaponBaseAttributeMultiplier> multipliers = new ArrayList<>();

      for (float inactiveValue : inactiveValues) {
         ItemPredicate predicate = net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
            .hasComponents(DataComponentPredicate.builder().expect((DataComponentType)TensuraDataComponents.TSUKUMOGAMI_INACTIVE.get(), inactiveValue).build())
            .build();
         ResourceLocation id = ResourceLocation.withDefaultNamespace("enchantment.tsukumogami");
         LevelBasedValue value = LevelBasedValue.constant(-inactiveValue);
         multipliers.add(new WeaponBaseAttributeMultiplier(id, Attributes.ATTACK_DAMAGE, value, Optional.of(predicate), EquipmentSlotGroup.MAINHAND, false));
         multipliers.add(new WeaponBaseAttributeMultiplier(id, Attributes.ARMOR, value, Optional.of(predicate), EquipmentSlotGroup.ARMOR, false));
         multipliers.add(new WeaponBaseAttributeMultiplier(id, Attributes.ARMOR_TOUGHNESS, value, Optional.of(predicate), EquipmentSlotGroup.ARMOR, false));
         multipliers.add(new WeaponBaseAttributeMultiplier(id, Attributes.KNOCKBACK_RESISTANCE, value, Optional.of(predicate), EquipmentSlotGroup.ARMOR, false));
      }

      context.register(
         TSUKUMOGAMI,
         addWeaponMultiplier(
               Enchantment.enchantment(
                  Enchantment.definition(
                     items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                     1,
                     1,
                     Enchantment.constantCost(50),
                     Enchantment.dynamicCost(50, 50),
                     1,
                     new EquipmentSlotGroup[]{EquipmentSlotGroup.ANY}
                  )
               ),
               multipliers.toArray(new WeaponBaseAttributeMultiplier[0])
            )
            .exclusiveWith(enchantments.getOrThrow(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE))
            .build(TSUKUMOGAMI.location())
      );
   }
}
