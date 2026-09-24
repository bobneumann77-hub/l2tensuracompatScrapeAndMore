package io.github.manasmods.tensura.item.tool;

import io.github.manasmods.tensura.item.weapon.TensuraSwordItem;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Tool.Rule;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class MultitoolItem extends Item {
   public static final ResourceLocation BASE_ATTACK_KNOCKBACK_ID = ResourceLocation.withDefaultNamespace("base_attack_knockback");
   private final Tier tier;

   public MultitoolItem(Tier tier, TagKey<Block> tagKey, Properties properties, ItemAttributeModifiers modifiers) {
      super(properties.attributes(modifiers).component(DataComponents.TOOL, createToolProperties(tagKey, tier)));
      this.tier = tier;
   }

   public static Tool createToolProperties(TagKey<Block> tagKey, Tier tier) {
      return new Tool(
         List.of(
            Rule.deniesDrops(tier.getIncorrectBlocksForDrops()),
            Rule.minesAndDrops(tagKey, tier.getSpeed()),
            Rule.minesAndDrops(List.of(Blocks.COBWEB), 15.0F),
            Rule.overrideSpeed(BlockTags.SWORD_EFFICIENT, 1.5F)
         ),
         1.0F,
         1
      );
   }

   public static ItemAttributeModifiers createAttributes(int damage, float speed, double knock, double ratio) {
      return ItemAttributeModifiers.builder()
         .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(BASE_ATTACK_KNOCKBACK_ID, knock, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(
            Attributes.SWEEPING_DAMAGE_RATIO,
            new AttributeModifier(TensuraSwordItem.BASE_SWEEP_RATIO_ID, ratio, Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .build();
   }

   public Tier getTier() {
      return this.tier;
   }

   public int getEnchantmentValue() {
      return this.tier.getEnchantmentValue();
   }

   public boolean isValidRepairItem(ItemStack arg, ItemStack arg2) {
      return this.tier.getRepairIngredient().test(arg2) || super.isValidRepairItem(arg, arg2);
   }

   public boolean hurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
      return true;
   }

   public void postHurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
      itemStack.hurtAndBreak(1, livingEntity2, EquipmentSlot.MAINHAND);
   }
}
