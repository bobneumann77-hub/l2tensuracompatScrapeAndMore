package io.github.manasmods.tensura.item.weapon.ranged;

import io.github.manasmods.tensura.entity.projectile.KunaiProjectile;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class MagisteelKunaiItem extends KunaiItem {
   public MagisteelKunaiItem(Tier pTier, float baseProjectileDamage, int durability) {
      super(pTier, baseProjectileDamage, new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(durability).fireResistant());
   }

   @Override
   protected int getDurabilityUse(ItemStack itemStack) {
      return 10;
   }

   @Override
   protected Projectile createProjectile(Level level, LivingEntity entity, InteractionHand hand, ItemStack itemStack, ItemStack source, boolean canPick) {
      boolean left = hand == InteractionHand.OFF_HAND && entity.getMainArm() == HumanoidArm.RIGHT
         || hand == InteractionHand.MAIN_HAND && entity.getMainArm() == HumanoidArm.LEFT;
      KunaiProjectile kunai = new KunaiProjectile(level, entity, itemStack, !left);
      kunai.pickup = entity == null ? Pickup.ALLOWED : Pickup.CREATIVE_ONLY;
      return kunai;
   }
}
