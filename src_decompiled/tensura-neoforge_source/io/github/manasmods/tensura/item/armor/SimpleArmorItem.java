package io.github.manasmods.tensura.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;

public class SimpleArmorItem extends ArmorItem {
   public SimpleArmorItem(Holder<ArmorMaterial> pMaterial, Type type, Properties properties, int durabilityMultiplier) {
      super(pMaterial, type, properties.durability(type.getDurability(durabilityMultiplier)));
   }

   public static Builder createAttributes(Type type, int armor, float toughness, double knockbackResist) {
      EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
      ResourceLocation location = ResourceLocation.withDefaultNamespace("armor." + type.getName());
      Builder builder = ItemAttributeModifiers.builder()
         .add(Attributes.ARMOR, new AttributeModifier(location, armor, Operation.ADD_VALUE), equipmentSlotGroup)
         .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(location, toughness, Operation.ADD_VALUE), equipmentSlotGroup);
      return knockbackResist > 0.0
         ? builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(location, knockbackResist, Operation.ADD_VALUE), equipmentSlotGroup)
         : builder;
   }

   public static Builder createAttributes(Type type, ArmorMaterial armorMaterial) {
      int armor = armorMaterial.getDefense(type);
      float toughness = armorMaterial.toughness();
      float knockbackResist = armorMaterial.knockbackResistance();
      EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
      ResourceLocation location = ResourceLocation.withDefaultNamespace("armor." + type.getName());
      Builder builder = ItemAttributeModifiers.builder()
         .add(Attributes.ARMOR, new AttributeModifier(location, armor, Operation.ADD_VALUE), equipmentSlotGroup)
         .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(location, toughness, Operation.ADD_VALUE), equipmentSlotGroup);
      return knockbackResist > 0.0F
         ? builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(location, knockbackResist, Operation.ADD_VALUE), equipmentSlotGroup)
         : builder;
   }
}
