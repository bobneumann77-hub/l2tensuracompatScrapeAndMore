package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FrostBallProjectile extends WaterBallProjectile {
   public FrostBallProjectile(EntityType<? extends FrostBallProjectile> entityType, Level level) {
      super(entityType, level);
      this.setSize(0.5F);
   }

   public FrostBallProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends WaterBallProjectile>)ProjectileEntityTypes.FROST_BALL.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public boolean shouldDiscardInWater() {
      return true;
   }

   @Override
   public ResourceLocation[] getTextureLocation() {
      return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/frost_ball.png")};
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.ICE_ELEMENTAL;
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of(SoundEvents.SNOW_BREAK);
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), (ParticleOptions)TensuraParticleTypes.SNOWFLAKE.get(), x, y, z, 15, 0.08, 0.08, 0.08, 0.05, true);
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.ITEM_SNOWBALL, x, y, z, 15, 0.1, 0.1, 0.1, 0.1, true);
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.SNOWFLAKE, x, y, z, 35, 0.08, 0.08, 0.08, 0.08, true);
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));
      if (this.random.nextFloat() <= 0.8) {
         double speed = 0.05;
         double dx = this.level().random.nextDouble() * 2.0 * speed - speed;
         double dy = this.level().random.nextDouble() * 2.0 * speed - speed;
         double dz = this.level().random.nextDouble() * 2.0 * speed - speed;
         this.level().addParticle((ParticleOptions)TensuraParticleTypes.SNOWFLAKE.get(), this.getX() + dx, this.getY() + dy, this.getZ() + dz, dx, dy, dz);
      }

      Vec3 random = this.getRandomVec3().scale(0.01F);

      for (int i = 0; i < 3; i++) {
         this.level().addParticle(ParticleTypes.SNOWFLAKE, vec3.x, vec3.y, vec3.z, random.x, random.y, random.z);
      }
   }
}
