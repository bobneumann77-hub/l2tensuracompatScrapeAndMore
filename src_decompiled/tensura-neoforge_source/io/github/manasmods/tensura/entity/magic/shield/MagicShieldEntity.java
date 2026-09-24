package io.github.manasmods.tensura.entity.magic.shield;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MagicShieldEntity extends ShieldEntity {
   protected float contactSpeedMultiplier = 0.5F;
   protected float projectileMagicReduction = 0.0F;

   public MagicShieldEntity(EntityType<? extends MagicShieldEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setElementalAttack(true);
   }

   public MagicShieldEntity(Level pLevel, LivingEntity pOwner) {
      this((EntityType<? extends MagicShieldEntity>)MiscEntityTypes.MAGIC_SHIELD.get(), pLevel);
      this.setOwner(pOwner);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setContactSpeedMultiplier(pCompound.getFloat("ContactSpeedMultiplier"));
      this.setProjectileMagicReduction(pCompound.getFloat("ProjectileMagicReduction"));
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("ContactSpeedMultiplier", this.getContactSpeedMultiplier());
      pCompound.putFloat("ProjectileMagicReduction", this.getProjectileMagicReduction());
   }

   @Override
   public boolean shouldPush() {
      return false;
   }

   @Override
   public boolean canCollideWith(@NotNull Entity entity) {
      return false;
   }

   @Override
   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canBeHitByProjectile() {
      return this.isAlive() && this.isPickable() && this.getProjectileMagicReduction() <= 0.0F;
   }

   @Override
   public boolean canHitEntity(Entity pTarget) {
      if (pTarget == this) {
         return false;
      } else if (pTarget == this.getOwner()) {
         return false;
      } else {
         return this.getProjectileMagicReduction() > 0.0F && pTarget instanceof TensuraProjectile
            ? true
            : !(this.getOwner() instanceof LivingEntity owner && owner.isAlliedTo(pTarget));
      }
   }

   @Override
   public boolean applyEffect(Entity target) {
      super.applyEffect(target);
      float speed = this.getContactSpeedMultiplier();
      if (speed != 0.0F) {
         target.makeStuckInBlock(this.level().getBlockState(this.getOnPos()), new Vec3(speed, speed, speed));
      }

      if (target instanceof TensuraFlyingProjectile projectile && projectile.getPortalCooldown() <= 0) {
         float damage = projectile.getDamage();
         float secondDamage = projectile.getSecondaryDamage();
         float damageReduction = this.getProjectileMagicReduction();
         if (damage + secondDamage < damageReduction) {
            projectile.remove();
         } else if (projectile.isElementalAttack()) {
            projectile.setSecondaryDamage(secondDamage - damageReduction);
         } else {
            projectile.setDamage(damage - damageReduction);
         }

         projectile.setPortalCooldown(10);
         if (target.getType().is(TensuraEntityTags.NO_FORCED_MOVE)) {
            return true;
         }

         Changeable<Vec3> changeable = Changeable.of(projectile.getDeltaMovement().scale(this.getContactSpeedMultiplier()));
         if (((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker())
            .move(target, this.getOwner(), this.getSkill(), changeable)
            .isFalse()) {
            return true;
         }

         projectile.setDeltaMovement((Vec3)changeable.get());
      }

      return true;
   }

   @Generated
   public float getContactSpeedMultiplier() {
      return this.contactSpeedMultiplier;
   }

   @Generated
   public void setContactSpeedMultiplier(float contactSpeedMultiplier) {
      this.contactSpeedMultiplier = contactSpeedMultiplier;
   }

   @Generated
   public float getProjectileMagicReduction() {
      return this.projectileMagicReduction;
   }

   @Generated
   public void setProjectileMagicReduction(float projectileMagicReduction) {
      this.projectileMagicReduction = projectileMagicReduction;
   }
}
