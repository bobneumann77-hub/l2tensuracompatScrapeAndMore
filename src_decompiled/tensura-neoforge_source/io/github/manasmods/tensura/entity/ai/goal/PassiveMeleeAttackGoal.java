package io.github.manasmods.tensura.entity.ai.goal;

import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class PassiveMeleeAttackGoal extends MeleeAttackGoal {
   public PassiveMeleeAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
      super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
   }

   public boolean canUse() {
      return super.canUse();
   }

   protected void checkAndPerformAttack(LivingEntity target) {
      if (this.canPerformAttack(target)) {
         this.resetAttackCooldown();
         this.mob.swing(InteractionHand.MAIN_HAND);
         this.doHurtTarget(target);
      }
   }

   public void doHurtTarget(Entity target) {
      if (this.mob.level() instanceof ServerLevel level) {
         DamageSource source = level.damageSources().mobAttack(this.mob);
         float f = EnchantmentHelper.modifyDamage(level, this.mob.getMainHandItem(), target, source, 1.0F);
         float f1 = EnchantmentHelper.modifyKnockback(level, this.mob.getMainHandItem(), target, source, 1.0F);
         int i = TensuraEnchantmentHelper.getEnchantmentLevel(level, Enchantments.FIRE_ASPECT, this.mob);
         if (i > 0) {
            target.setRemainingFireTicks(i * 80);
         }

         if (!target.hurt(source, f)) {
            return;
         }

         if (f1 > 0.0F && target instanceof LivingEntity living) {
            living.knockback(f1 * 0.5F, Mth.sin(this.mob.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.mob.getYRot() * (float) (Math.PI / 180.0)));
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().multiply(0.6, 1.0, 0.6));
         }

         EnchantmentHelper.doPostAttackEffects(level, target, source);
         this.mob.setLastHurtMob(target);
      }
   }
}
