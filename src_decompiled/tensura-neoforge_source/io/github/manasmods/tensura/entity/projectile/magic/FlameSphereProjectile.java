package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.monster.IfritEntity;
import io.github.manasmods.tensura.entity.monster.SalamanderEntity;
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
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FlameSphereProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public FlameSphereProjectile(EntityType<? extends FlameSphereProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.setSize(0.5F);
   }

   public FlameSphereProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends FlameSphereProjectile>)ProjectileEntityTypes.FLAME_SPHERE.get(), levelIn);
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
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_sphere/flame_sphere.png");
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      return (pTarget instanceof IfritEntity || pTarget instanceof SalamanderEntity) && this.getOwner() != null && this.getOwner().isAlliedTo(pTarget)
         ? false
         : super.canHitEntity(pTarget);
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
      if (this.random.nextFloat() <= 0.8) {
         double dx = this.level().random.nextDouble() * 0.05 - 0.05;
         double dy = this.level().random.nextDouble() * 0.05 - 0.05;
         double dz = this.level().random.nextDouble() * 0.05 - 0.05;
         double x = (this.level().random.nextDouble() - 0.5) * 4.0;
         double y = (this.level().random.nextDouble() - 0.5) * 4.0;
         double z = (this.level().random.nextDouble() - 0.5) * 4.0;
         this.level().addParticle((ParticleOptions)TensuraParticleTypes.HEAT_EFFECT.get(), this.getX() + x, this.getY() + y, this.getZ() + z, dx, dy, dz);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_sphere.circling")))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
