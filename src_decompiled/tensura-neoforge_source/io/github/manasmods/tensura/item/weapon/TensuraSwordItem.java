package io.github.manasmods.tensura.item.weapon;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import lombok.Generated;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class TensuraSwordItem extends SwordItem {
   public static final ResourceLocation BASE_ATTACK_RANGE_ID = ResourceLocation.withDefaultNamespace("base_attack_range");
   public static final ResourceLocation BASE_ATTACK_KNOCKBACK_ID = ResourceLocation.withDefaultNamespace("base_attack_knockback");
   public static final ResourceLocation BASE_SWEEP_RATIO_ID = ResourceLocation.withDefaultNamespace("base_sweep_ratio");
   public static final ResourceLocation BASE_CRIT_CHANCE_ID = ResourceLocation.withDefaultNamespace("base_crit_chance");
   public static final ResourceLocation BASE_CRIT_MULTIPLIER_ID = ResourceLocation.withDefaultNamespace("base_crit_multiplier");
   public final double range;

   public TensuraSwordItem(
      Tier pTier, int damage, float speed, double range, double sweepRatio, double critChance, double critMultiplier, Properties pProperties
   ) {
      super(pTier, pProperties.attributes(createAttributes(pTier, damage, speed, range, sweepRatio, critChance, critMultiplier)));
      this.range = range;
   }

   public TensuraSwordItem(
      Tier pTier, int damage, float speed, double range, double sweepRatio, double critChance, double critMultiplier, double knockback, Properties pProperties
   ) {
      super(pTier, pProperties.attributes(createAttributes(pTier, damage, speed, range, sweepRatio, critChance, critMultiplier, knockback)));
      this.range = range;
   }

   public static ItemAttributeModifiers createAttributes(Tier tier, int damage, float speed, double range, double ratio, double chance, double multiplier) {
      return ItemAttributeModifiers.builder()
         .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage + tier.getAttackDamageBonus(), Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(BASE_ATTACK_RANGE_ID, range, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.SWEEPING_DAMAGE_RATIO, new AttributeModifier(BASE_SWEEP_RATIO_ID, ratio, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE, new AttributeModifier(BASE_CRIT_CHANCE_ID, chance, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(
            ManasCoreAttributes.CRITICAL_DAMAGE_MULTIPLIER,
            new AttributeModifier(BASE_CRIT_MULTIPLIER_ID, multiplier, Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .build();
   }

   public static ItemAttributeModifiers createAttributes(
      Tier tier, int damage, float speed, double range, double ratio, double chance, double multiplier, double knockback
   ) {
      return ItemAttributeModifiers.builder()
         .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage + tier.getAttackDamageBonus(), Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(BASE_ATTACK_RANGE_ID, range, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.SWEEPING_DAMAGE_RATIO, new AttributeModifier(BASE_SWEEP_RATIO_ID, ratio, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE, new AttributeModifier(BASE_CRIT_CHANCE_ID, chance, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(
            ManasCoreAttributes.CRITICAL_DAMAGE_MULTIPLIER,
            new AttributeModifier(BASE_CRIT_MULTIPLIER_ID, multiplier, Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(BASE_ATTACK_KNOCKBACK_ID, knockback, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .build();
   }

   @Generated
   public double getRange() {
      return this.range;
   }
}
