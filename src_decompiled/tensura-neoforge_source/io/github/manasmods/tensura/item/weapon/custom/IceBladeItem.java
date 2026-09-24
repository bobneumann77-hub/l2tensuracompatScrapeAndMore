package io.github.manasmods.tensura.item.weapon.custom;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.IceLanceProjectile;
import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.weapon.TwoHandedSwordItem;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class IceBladeItem extends TwoHandedSwordItem {
   public IceBladeItem() {
      super(
         TensuraToolTiers.HIGH_MAGISTEEL,
         4,
         -2.6F,
         1.0,
         0.25,
         0.0,
         0.0,
         3,
         -2.8F,
         1.0,
         0.0,
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

   public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
      boolean hurt = super.hurtEnemy(pStack, pTarget, pAttacker);
      if (!hurt) {
         return false;
      }

      MobEffectInstance chill = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CHILL), 100, 1, true, true, true);
      TensuraMobEffect.addEffect(pTarget, chill, pAttacker, (ManasSkill)AspectualMagics.FREEZE.get());
      pTarget.level().playSound(null, pTarget, SoundEvents.PLAYER_HURT_FREEZE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      if (pTarget.getTicksFrozen() < 350) {
         pTarget.setTicksFrozen(400);
      } else {
         pTarget.setTicksFrozen(pTarget.getTicksFrozen() + 50);
      }

      if (pTarget.isOnFire()) {
         pTarget.clearFire();
      }

      return true;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      pPlayer.startUsingItem(pHand);
      return InteractionResultHolder.consume(itemstack);
   }

   public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity entity, int pTimeLeft) {
      if (entity instanceof Player player) {
         int useTicks = this.getUseDuration(pStack, entity) - pTimeLeft;
         if (useTicks >= 7) {
            double EP = pStack.has((DataComponentType)TensuraDataComponents.EP_DURABILITY.get())
               ? (Double)pStack.get((DataComponentType)TensuraDataComponents.EP_DURABILITY.get())
               : 0.0;
            double cost = 100.0;
            if (EP < cost && !player.hasInfiniteMaterials()) {
               pLevel.playSound(null, player, (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            } else {
               if (!pLevel.isClientSide) {
                  boolean left = player.getUsedItemHand() == InteractionHand.OFF_HAND && player.getMainArm() == HumanoidArm.RIGHT
                     || player.getUsedItemHand() == InteractionHand.MAIN_HAND && player.getMainArm() == HumanoidArm.LEFT;
                  this.icicle(pLevel, player, !left);
                  pLevel.playSound(null, player, (SoundEvent)TensuraSoundEvents.CAST_ICE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  if (!player.hasInfiniteMaterials()) {
                     pStack.set((DataComponentType)TensuraDataComponents.EP_DURABILITY.get(), EP - cost);
                  }
               }

               player.awardStat(Stats.ITEM_USED.get(this));
               player.swing(player.getUsedItemHand(), true);
            }
         }
      }
   }

   public void icicle(Level pLevel, Player player, boolean right) {
      IceLanceProjectile icicle = new IceLanceProjectile(pLevel, player);
      icicle.setPosDirection(player, right ? TensuraFlyingProjectile.PositionDirection.RIGHT : TensuraFlyingProjectile.PositionDirection.LEFT);
      icicle.setDamage(TensuraDamageHelper.getWeaponDamage(player, null, player.getUsedItemHand() == InteractionHand.OFF_HAND, null));
      icicle.setMpCost(100.0);
      icicle.setBurnTicks(-1);
      icicle.setMobEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CHILL), 200, 2, true, false, true));
      Vec3 vec3 = player.getViewVector(1.0F);
      icicle.shoot(vec3.x(), vec3.y(), vec3.z(), 2.2F, 0.0F);
      pLevel.addFreshEntity(icicle);
   }
}
