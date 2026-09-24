package io.github.manasmods.tensura.entity.projectile;

import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InvisibleArrow extends AbstractArrow {
   public InvisibleArrow(EntityType<? extends InvisibleArrow> entityType, Level level) {
      super(entityType, level);
   }

   public InvisibleArrow(Level level, double d, double e, double f, ItemStack itemStack, @Nullable ItemStack itemStack2) {
      super((EntityType)ProjectileEntityTypes.INVISIBLE_ARROW.get(), d, e, f, level, itemStack, itemStack2);
   }

   public InvisibleArrow(Level level, LivingEntity livingEntity, ItemStack itemStack, @Nullable ItemStack itemStack2) {
      super((EntityType)ProjectileEntityTypes.INVISIBLE_ARROW.get(), livingEntity, level, itemStack, itemStack2);
   }

   @NotNull
   protected ItemStack getDefaultPickupItem() {
      return new ItemStack((ItemLike)TensuraToolItems.INVISIBLE_ARROW.get());
   }

   protected void onHitEntity(EntityHitResult pResult) {
      super.onHitEntity(pResult);
      Entity entity = pResult.getEntity();
      Entity owner = this.getOwner();
      if (entity.getType() == EntityType.ENDERMAN) {
         if (entity instanceof LivingEntity target) {
            if (!this.level().isClientSide && this.getPierceLevel() <= 0) {
               target.setArrowCount(target.getArrowCount() + 1);
            }

            DamageSource damageSource = this.damageSources().arrow(this, (Entity)(owner != null ? owner : this));
            this.doKnockback(target, damageSource);
            if (this.level() instanceof ServerLevel level) {
               EnchantmentHelper.doPostAttackEffectsWithItemSource(level, target, damageSource, this.getWeaponItem());
            }

            this.doPostHurtEffects(target);
            if (target != owner && target instanceof Player && owner instanceof ServerPlayer player && !this.isSilent()) {
               player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
            }
         }

         this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         if (this.getPierceLevel() <= 0) {
            this.discard();
         }
      }
   }

   protected float getWaterInertia() {
      return 0.5F;
   }

   public boolean isInvisible() {
      return true;
   }

   public boolean isInvisibleTo(Player pPlayer) {
      if (pPlayer.isSpectator()) {
         return false;
      } else if (this.getOwner() == pPlayer) {
         return false;
      } else {
         return SkillUtils.canSeeIllusion(pPlayer) ? false : this.isInvisible();
      }
   }

   public void tickDespawn() {
      if (++this.life >= TensuraProjectile.CONFIG.spearDespawnTick) {
         this.discard();
      }
   }
}
