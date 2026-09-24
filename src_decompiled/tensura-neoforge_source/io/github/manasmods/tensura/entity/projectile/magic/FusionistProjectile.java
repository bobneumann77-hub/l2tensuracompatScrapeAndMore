package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.util.TensuraExplosionDamageCalculator;
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

public class FusionistProjectile extends TensuraFlyingProjectile {
   public FusionistProjectile(EntityType<? extends FusionistProjectile> entityType, Level level) {
      super(entityType, level);
      this.setSize(0.25F);
   }

   public FusionistProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends Projectile>)ProjectileEntityTypes.FUSIONIST_PROJECTILE.get(), levelIn);
      this.setOwner(shooter);
      this.setSize(0.25F);
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
      return new ResourceLocation[]{ResourceLocation.withDefaultNamespace("textures/block/cobblestone.png")};
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
   protected void explode(double x, double y, double z, boolean fire) {
      TensuraExplosionDamageCalculator calculator = new TensuraExplosionDamageCalculator(this.shouldGrief(), true, Optional.of(1.0F));
      this.level()
         .explode(
            this.getOwner(),
            this.getExplosionDamageSource(),
            calculator,
            x,
            y,
            z,
            this.getExplosionRadius(),
            fire,
            this.getExplosionInteraction(),
            ParticleTypes.EXPLOSION,
            ParticleTypes.EXPLOSION_EMITTER,
            SoundEvents.GENERIC_EXPLODE
         );
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
   }
}
