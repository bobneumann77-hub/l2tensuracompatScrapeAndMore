package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class FloatSphereProjectile extends TensuraFlyingProjectile {
   public FloatSphereProjectile(EntityType<? extends FloatSphereProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public FloatSphereProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends Projectile>)ProjectileEntityTypes.FLOAT_SPHERE.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of(SoundEvents.SHULKER_BULLET_HIT);
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.END_ROD, x, y, z, 5, 0.1, 0.1, 0.1, 0.1, true);
   }

   @Override
   public void flyingParticles() {
      double speed = 0.05;
      double dx = this.level().random.nextDouble() * 2.0 * speed - speed;
      double dy = this.level().random.nextDouble() * 2.0 * speed - speed;
      double dz = this.level().random.nextDouble() * 2.0 * speed - speed;
      this.level().addParticle(ParticleTypes.END_ROD, this.getX() + dx, this.getY() + dy, this.getZ() + dz, dx, dy, dz);
   }
}
