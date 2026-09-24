package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;

public class SteamBallProjectile extends WaterBallProjectile {
   public SteamBallProjectile(EntityType<? extends SteamBallProjectile> entityType, Level level) {
      super(entityType, level);
      this.setSize(0.5F);
   }

   public SteamBallProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends WaterBallProjectile>)ProjectileEntityTypes.STEAM_BALL.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public boolean shouldDiscardInWater() {
      return true;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_sphere/steam_sphere.png");
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)SoundEvents.WIND_CHARGE_BURST.value());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.CLOUD, x, y, z, 10, 0.08, 0.08, 0.08, 0.1, false);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getSteamEffect(), x, y, z, 40, 0.08, 0.08, 0.08, 0.1, true);
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));
      this.level().addParticle(TensuraParticleUtils.getSteamEffect(), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);

      for (int i = 0; i < 2; i++) {
         Vec3 random = this.getRandomVec3().scale(0.05F);
         this.level().addParticle(TensuraParticleUtils.getSteamEffect(), vec3.x, vec3.y, vec3.z, random.x, random.y, random.z);
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_sphere.loop")))
      );
   }
}
