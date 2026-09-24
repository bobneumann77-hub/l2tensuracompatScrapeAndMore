package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BulletProjectile extends TensuraFlyingProjectile {
   protected boolean magic = false;

   public BulletProjectile(EntityType<? extends BulletProjectile> entityType, Level level) {
      super(entityType, level);
      this.setSize(0.25F);
   }

   public BulletProjectile(Level worldIn, LivingEntity shooter, boolean right) {
      super((EntityType<? extends Projectile>)ProjectileEntityTypes.BULLET.get(), worldIn);
      this.setOwner(shooter);
      this.setSize(0.25F);
      float rot = shooter.yHeadRot + (right ? 60 : -60);
      this.setPos(
         shooter.getX() - shooter.getBbWidth() * 0.5 * Mth.sin(rot * (float) (Math.PI / 180.0)),
         shooter.getEyeY() - 0.2F,
         shooter.getZ() + shooter.getBbWidth() * 0.5 * Mth.cos(rot * (float) (Math.PI / 180.0))
      );
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Magic", this.isMagic());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setMagic(compound.getBoolean("Magic"));
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return this.isMagic() ? TensuraDamageTypes.BULLET_MAGIC : TensuraDamageTypes.BULLET;
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of(SoundEvents.ITEM_BREAK);
   }

   @Override
   public void hitParticles(double x, double y, double z) {
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));
      this.level().addParticle(ParticleTypes.SMOKE, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
   }

   @Generated
   public void setMagic(boolean magic) {
      this.magic = magic;
   }

   @Generated
   public boolean isMagic() {
      return this.magic;
   }
}
