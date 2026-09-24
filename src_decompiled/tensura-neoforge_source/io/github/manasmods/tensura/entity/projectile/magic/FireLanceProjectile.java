package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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

public class FireLanceProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public FireLanceProjectile(EntityType<? extends FireLanceProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElement(Element.FLAME);
      this.setElementalAttack(true);
   }

   public FireLanceProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends FireLanceProjectile>)ProjectileEntityTypes.FIRE_LANCE.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.FIRE_ELEMENTAL;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/lance/fire_lance.png");
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
   public Optional<SoundEvent> delayShootSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_FIRE.get());
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_FIRE.get());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), x, y, z, 5, 0.1, 0.1, 0.1, 0.25, true);
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.SMOKE, x, y, z, 5, 0.1, 0.1, 0.1, 0.25, true);
   }

   @Override
   public void flyingParticles() {
      if (this.getDelayTick() <= 0) {
         this.level()
            .addParticle(
               (ParticleOptions)(this.level().random.nextDouble() < 0.3 ? ParticleTypes.SMOKE : (ParticleOptions)TensuraParticleTypes.RED_FIRE.get()),
               this.getX(),
               this.getY(),
               this.getZ(),
               0.0,
               0.0,
               0.0
            );
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
