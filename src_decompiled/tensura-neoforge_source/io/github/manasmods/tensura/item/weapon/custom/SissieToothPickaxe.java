package io.github.manasmods.tensura.item.weapon.custom;

import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.tool.SimplePickaxeItem;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class SissieToothPickaxe extends SimplePickaxeItem {
   public static final ResourceLocation BASE_SUBMERGED_MINING_SPEED_ID = ResourceLocation.withDefaultNamespace("base_submerged_mining_speed");

   public SissieToothPickaxe() {
      super(TensuraToolTiers.LOW_MAGISTEEL, new Properties().arch$tab(TensuraCreativeTabs.GEARS), createAttributes(TensuraToolTiers.LOW_MAGISTEEL, 5.0F, -3.0F));
   }

   public static ItemAttributeModifiers createAttributes(Tier tier, float f, float g) {
      return ItemAttributeModifiers.builder()
         .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(BASE_ATTACK_DAMAGE_ID, f + tier.getAttackDamageBonus(), Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, g, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.SUBMERGED_MINING_SPEED, new AttributeModifier(BASE_SUBMERGED_MINING_SPEED_ID, 0.8F, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .build();
   }

   public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
      return pRepair.is((Item)TensuraMobDropItems.SISSIE_TOOTH.get());
   }
}
