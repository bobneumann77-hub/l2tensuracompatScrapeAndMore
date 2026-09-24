package io.github.manasmods.tensura.item.armor.custom;

import io.github.manasmods.tensura.item.armor.SimpleArmorItem;
import io.github.manasmods.tensura.item.armor.SimpleBootsItem;
import io.github.manasmods.tensura.registry.item.misc.TensuraArmorMaterials;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class WingedShoesItem extends SimpleBootsItem {
   public WingedShoesItem() {
      super(
         TensuraArmorMaterials.WINGED_SHOES,
         new Properties()
            .arch$tab(TensuraCreativeTabs.ARMOR)
            .attributes(createWingedAttributes(Type.BOOTS, (ArmorMaterial)TensuraArmorMaterials.WINGED_SHOES.get())),
         33
      );
   }

   public static ItemAttributeModifiers createWingedAttributes(Type type, ArmorMaterial armorMaterial) {
      EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
      ResourceLocation location = ResourceLocation.withDefaultNamespace("armor." + type.getName());
      return SimpleArmorItem.createAttributes(type, armorMaterial)
         .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(location, 0.1, Operation.ADD_VALUE), equipmentSlotGroup)
         .add(Attributes.STEP_HEIGHT, new AttributeModifier(location, 1.0, Operation.ADD_VALUE), equipmentSlotGroup)
         .add(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(location, 3.0, Operation.ADD_VALUE), equipmentSlotGroup)
         .add(Attributes.GRAVITY, new AttributeModifier(location, -0.5, Operation.ADD_MULTIPLIED_TOTAL), equipmentSlotGroup)
         .build();
   }
}
