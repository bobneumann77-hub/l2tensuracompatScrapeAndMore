package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AcidBallProjectile extends WaterBallProjectile {
   public AcidBallProjectile(EntityType<? extends AcidBallProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.setSize(2.0F);
   }

   public AcidBallProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends AcidBallProjectile>)ProjectileEntityTypes.ACID_BALL.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/acid.png");
   }

   @Override
   public Optional<SoundEvent> delayShootSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_WATER.get());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getAcidEffect(), x, y, z, 55, 0.08, 0.08, 0.08, 0.15, true);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getAcidBubble(), x, y, z, 25, 0.08, 0.08, 0.08, 0.15, false);
   }

   @Override
   public void flyingParticles() {
      if (this.getDelayTick() <= 0) {
         Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));
         this.level().addParticle(TensuraParticleUtils.getAcidBubble(), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);

         for (int i = 0; i < 2; i++) {
            Vec3 random = this.getRandomVec3().scale(0.1F);
            this.level().addParticle(TensuraParticleUtils.getAcidEffect(), vec3.x, vec3.y, vec3.z, random.x, random.y, random.z);
         }
      }
   }
}
