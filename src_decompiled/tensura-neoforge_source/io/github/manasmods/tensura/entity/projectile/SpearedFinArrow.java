package io.github.manasmods.tensura.entity.projectile;

import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpearedFinArrow extends AbstractArrow {
   public SpearedFinArrow(EntityType<? extends SpearedFinArrow> entityType, Level level) {
      super(entityType, level);
   }

   public SpearedFinArrow(Level level, double d, double e, double f, ItemStack itemStack, @Nullable ItemStack itemStack2) {
      super((EntityType)ProjectileEntityTypes.SPEARED_FIN_ARROW.get(), d, e, f, level, itemStack, itemStack2);
   }

   public SpearedFinArrow(Level level, LivingEntity livingEntity, ItemStack itemStack, @Nullable ItemStack itemStack2) {
      super((EntityType)ProjectileEntityTypes.SPEARED_FIN_ARROW.get(), livingEntity, level, itemStack, itemStack2);
   }

   @NotNull
   protected ItemStack getDefaultPickupItem() {
      return new ItemStack((ItemLike)TensuraToolItems.SPEARED_FIN_ARROW.get());
   }

   protected float getWaterInertia() {
      return 1.1F;
   }

   public void tickDespawn() {
      if (++this.life >= TensuraProjectile.CONFIG.spearDespawnTick) {
         this.discard();
      }
   }
}
