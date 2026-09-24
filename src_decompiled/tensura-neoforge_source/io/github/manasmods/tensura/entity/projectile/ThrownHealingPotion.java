package io.github.manasmods.tensura.entity.projectile;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.item.consumable.ManaPotionItem;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import java.awt.Color;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThrownHealingPotion extends ThrowableItemProjectile implements ItemSupplier {
   public ThrownHealingPotion(EntityType<? extends ThrownHealingPotion> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public ThrownHealingPotion(Level pLevel, LivingEntity pShooter) {
      super((EntityType)ProjectileEntityTypes.HEALING_POTION.get(), pShooter, pLevel);
   }

   public ThrownHealingPotion(Level pLevel, double pX, double pY, double pZ) {
      super((EntityType)ProjectileEntityTypes.HEALING_POTION.get(), pX, pY, pZ, pLevel);
   }

   @NotNull
   protected Item getDefaultItem() {
      return (Item)TensuraConsumableItems.FULL_POTION.get();
   }

   protected double getDefaultGravity() {
      return 0.05;
   }

   protected void onHit(HitResult pResult) {
      super.onHit(pResult);
      if (!this.level().isClientSide) {
         this.applySplash(this.getItem(), pResult.getType() == Type.ENTITY ? ((EntityHitResult)pResult).getEntity() : null);
         int color = this.getItem().is(TensuraItemTags.ARCANE_POTIONS) ? new Color(136, 39, 246).getRGB() : new Color(196, 249, 239).getRGB();
         this.level().levelEvent(2007, this.blockPosition(), color);
         this.discard();
      }
   }

   private void applySplash(ItemStack stack, @Nullable Entity direct) {
      if (stack.getItem() instanceof ManaPotionItem potion) {
         if (direct instanceof LivingEntity target) {
            this.healTarget(potion, target, 1.0F);
         }

         AABB aabb = this.getBoundingBox().inflate(3.0, 3.0, 3.0);
         List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
         if (!list.isEmpty()) {
            for (LivingEntity target : list) {
               if (target != direct) {
                  double distance = this.distanceToSqr(target);
                  if (!(distance >= 9.0)) {
                     this.healTarget(potion, target, (float)(1.0 - Math.sqrt(distance) / 3.0));
                  }
               }
            }
         }
      }
   }

   private void healTarget(ManaPotionItem item, LivingEntity target, float multiplier) {
      item.applyEffect(target, multiplier);
      target.level().playSound(null, target, SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.COMPOSTER);
   }
}
