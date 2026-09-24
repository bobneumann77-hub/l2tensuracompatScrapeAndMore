package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ThunderLanceProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ThunderLanceProjectile(EntityType<? extends ThunderLanceProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.setPiercingEntity(true);
   }

   public ThunderLanceProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends ThunderLanceProjectile>)ProjectileEntityTypes.THUNDER_LANCE.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.LIGHTNING_ELEMENTAL;
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
   public ResourceLocation getTexture() {
      return this.getVehicle() != null ? null : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/lance/thunder_lance.png");
   }

   @Override
   public Optional<SoundEvent> delayShootSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get());
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), x, y, z, 10, 0.5, 0.5, 0.5, 0.1, false
      );
   }

   @Override
   public void flyingParticles() {
      if (this.getDelayTick() <= 0 && !this.isPassenger()) {
         this.level().addParticle((ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
