package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PoisonBallProjectile extends WaterBallProjectile {
   public PoisonBallProjectile(EntityType<? extends PoisonBallProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.setSize(1.75F);
   }

   public PoisonBallProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends PoisonBallProjectile>)ProjectileEntityTypes.POISON_BALL.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/poison.png");
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getPoisonEffect(10), x, y, z, 55, 0.08, 0.08, 0.08, 0.15, true);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getPoisonBubble(), x, y, z, 25, 0.08, 0.08, 0.08, 0.15, false);
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));
      this.level().addParticle(TensuraParticleUtils.getPoisonBubble(), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);

      for (int i = 0; i < 2; i++) {
         Vec3 random = this.getRandomVec3().scale(0.1F);
         this.level().addParticle(TensuraParticleUtils.getPoisonEffect(10), vec3.x, vec3.y, vec3.z, random.x, random.y, random.z);
      }
   }
}
