package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class SniperGrenadeProjectile extends TensuraFlyingProjectile {
   public SniperGrenadeProjectile(EntityType<? extends SniperGrenadeProjectile> entityType, Level level) {
      super(entityType, level);
      this.setSize(0.5F);
   }

   public SniperGrenadeProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends Projectile>)ProjectileEntityTypes.SNIPER_GRENADE.get(), levelIn);
      this.setOwner(shooter);
      this.setSize(0.5F);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.BULLET;
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
   public ResourceLocation[] getTextureLocation() {
      return new ResourceLocation[]{ResourceLocation.withDefaultNamespace("textures/block/blast_furnace_side.png")};
   }

   @Override
   public void setPosAndShoot(LivingEntity entity) {
      this.setPos(entity.position().add(0.0, entity.getEyeHeight(), 0.0));
      this.shootFromRot(entity.getLookAngle());
   }

   @Override
   public void onExplosion(double x, double y, double z) {
      if (!(this.getExplosionRadius() <= 0.0F)) {
         super.onExplosion(x, y, z);
         this.discard();
      }
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)SoundEvents.GENERIC_EXPLODE.value());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.EXPLOSION, x, y, z, 1, 0.12, 0.12, 0.12, 0.15, false);
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.FLASH, x, y, z, 3, 0.5, 0.5, 0.5, 0.1, false);
   }

   @Override
   public void flyingParticles() {
      float radius = this.getBbWidth();
      double x = this.getX() + (this.level().random.nextDouble() - 0.5) * radius;
      double y = this.getY() + (this.level().random.nextDouble() - 0.5) * radius;
      double z = this.getZ() + (this.level().random.nextDouble() - 0.5) * radius;

      for (int j = 0; j < 10; j++) {
         double newX = x + this.random.nextGaussian() / 4.0;
         double newY = y + this.random.nextGaussian() / 4.0;
         double newZ = z + this.random.nextGaussian() / 4.0;
         this.level().addParticle(ParticleTypes.SMOKE, newX, newY, newZ, 0.0, 0.0, 0.0);
      }
   }
}
