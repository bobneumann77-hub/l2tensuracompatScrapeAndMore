package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FlameOrbProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public FlameOrbProjectile(EntityType<? extends FlameOrbProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public FlameOrbProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends Projectile>)ProjectileEntityTypes.FLAME_ORB.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.BURN;
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
      return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/mad_orbs.png")};
   }

   @Override
   public DamageSource getExplosionDamageSource() {
      return this.getDamageSource();
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)SoundEvents.GENERIC_EXPLODE.value());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.EXPLOSION, x, y, z, 1, 0.12, 0.12, 0.12, 0.15, false);
      TensuraParticleHelper.spawnServerParticles(this.level(), (ParticleOptions)TensuraParticleTypes.HEAT_EFFECT.get(), x, y, z, 10, 0.5, 0.5, 0.5, 0.1, false);
   }

   @Override
   public void flyingParticles() {
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "loopController", 3, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.flying_orb.loop")))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
