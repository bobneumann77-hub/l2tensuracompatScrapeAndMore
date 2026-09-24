package io.github.manasmods.tensura.item.weapon.custom;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.weapon.SimpleShortSwordItem;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class SpiderDaggerItem extends SimpleShortSwordItem {
   public SpiderDaggerItem() {
      super(TensuraToolTiers.LOW_MAGISTEEL, 1, -2.0F, -1.0, -1.0, 50.0, 0.0, new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(150));
   }

   public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
      boolean canAttack = super.hurtEnemy(pStack, pTarget, pAttacker);
      if (canAttack) {
         MobEffectInstance poison = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 100, 0);
         TensuraMobEffect.addEffect(pTarget, poison, pAttacker, null);
      }

      return canAttack;
   }

   public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
      return pRepair.is((Item)TensuraMobDropItems.SPIDER_FANG.get());
   }
}
