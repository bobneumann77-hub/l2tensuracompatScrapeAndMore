package io.github.manasmods.tensura.item.weapon.custom;

import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.weapon.TwoHandedSwordItem;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class VortexSpearItem extends TwoHandedSwordItem {
   public VortexSpearItem() {
      super(
         TensuraToolTiers.PURE_MAGISTEEL,
         3,
         -2.6F,
         1.0,
         -1.0,
         0.0,
         0.0,
         2,
         -2.8F,
         1.0,
         -1.0,
         0.0,
         0.0,
         new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()
      );
   }

   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.SPEAR;
   }

   public int getUseDuration(ItemStack pStack, LivingEntity entity) {
      return 72000;
   }

   public void releaseUsing(ItemStack stack, Level pLevel, LivingEntity entity, int pTimeLeft) {
      if (entity instanceof Player player && !this.isOutOfEP(stack, player)) {
         int i = this.getUseDuration(stack, entity) - pTimeLeft;
         if (i < 10) {
            return;
         }

         if (!pLevel.isClientSide) {
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
         }

         player.awardStat(Stats.ITEM_USED.get(this));
         if (!player.hasInfiniteMaterials()) {
            double cost = 200 + TensuraEnchantmentHelper.getEnchantmentLevel(player.level(), Enchantments.RIPTIDE, stack) * 100;
            double EP = stack.has((DataComponentType)TensuraDataComponents.EP_DURABILITY.get())
               ? (Double)stack.get((DataComponentType)TensuraDataComponents.EP_DURABILITY.get()) - cost
               : 0.0;
            stack.set((DataComponentType)TensuraDataComponents.EP_DURABILITY.get(), EP);
         }

         int j = 3 + TensuraEnchantmentHelper.getEnchantmentLevel(player.level(), Enchantments.RIPTIDE, stack);
         float f7 = player.getYRot();
         float f = player.getXRot();
         float f1 = -Mth.sin(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f * (float) (Math.PI / 180.0));
         float f2 = -Mth.sin(f * (float) (Math.PI / 180.0));
         float f3 = Mth.cos(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f * (float) (Math.PI / 180.0));
         float f4 = Mth.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
         float f5 = 0.75F * j;
         f1 *= f5 / f4;
         f2 *= f5 / f4;
         f3 *= f5 / f4;
         player.push(f1, f2, f3);
         player.startAutoSpinAttack(20, (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE), stack);
         if (player.onGround()) {
            player.move(MoverType.SELF, new Vec3(0.0, 1.1999999F, 0.0));
         }

         pLevel.playSound(null, player, (SoundEvent)SoundEvents.TRIDENT_RIPTIDE_3.value(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand pHand) {
      ItemStack itemstack = player.getItemInHand(pHand);
      if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
         return InteractionResultHolder.fail(itemstack);
      }

      if (this.isOutOfEP(itemstack, player)) {
         return InteractionResultHolder.fail(itemstack);
      }

      player.startUsingItem(pHand);
      return InteractionResultHolder.consume(itemstack);
   }

   private boolean isOutOfEP(ItemStack stack, Player player) {
      if (player.hasInfiniteMaterials()) {
         return false;
      }

      if (!stack.has((DataComponentType)TensuraDataComponents.EP_DURABILITY.get())) {
         return false;
      }

      double cost = 200 + TensuraEnchantmentHelper.getEnchantmentLevel(player.level(), Enchantments.RIPTIDE, stack) * 100;
      return (Double)stack.get((DataComponentType)TensuraDataComponents.EP_DURABILITY.get()) < cost;
   }

   public static boolean onHit(LivingEntity attacker, @Nullable LivingEntity target, Vec3 position) {
      if (!attacker.getItemBySlot(EquipmentSlot.MAINHAND).is((Item)TensuraToolItems.VORTEX_SPEAR.get())
         && !attacker.getItemBySlot(EquipmentSlot.OFFHAND).is((Item)TensuraToolItems.VORTEX_SPEAR.get())) {
         return false;
      }

      if (!attacker.isAutoSpinAttack()) {
         return false;
      }

      attacker.resetFallDistance();
      if (TensuraGameRules.canSkillGrief(attacker.level())) {
         SkillHelper.launchBlock(
            attacker,
            position,
            4,
            2,
            0.25F,
            0.25F,
            blockState -> attacker.getRandom().nextInt(3) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE),
            blockPos -> true
         );
      }

      AABB aabb = attacker.getBoundingBox().inflate(2.0);
      List<LivingEntity> list = attacker.level().getEntitiesOfClass(LivingEntity.class, aabb, livingx -> !livingx.is(attacker) && livingx != target);
      if (!list.isEmpty()) {
         for (LivingEntity living : list) {
            DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(attacker.level(), DamageTypes.MOB_ATTACK, attacker);
            if (living.hurt(damageSource, (float)attacker.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
               TensuraParticleHelper.spawnServerGroundSlamParticle(living, 10, 2.0F);
               living.getDeltaMovement().add(0.0, 0.1, 0.0);
            }
         }
      }

      return true;
   }
}
