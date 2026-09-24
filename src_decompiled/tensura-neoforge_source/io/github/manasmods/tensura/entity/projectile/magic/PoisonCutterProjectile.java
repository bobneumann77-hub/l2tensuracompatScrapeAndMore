package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PoisonCutterProjectile extends WaterBallProjectile {
   public PoisonCutterProjectile(EntityType<? extends PoisonCutterProjectile> entityType, Level level) {
      super(entityType, level);
      this.setSize(1.5F);
   }

   public PoisonCutterProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends WaterBallProjectile>)ProjectileEntityTypes.POISON_CUTTER.get(), levelIn);
      this.setOwner(shooter);
      this.setSize(1.5F);
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/slash/poison_blade.png");
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getPoisonEffect(10), x, y, z, 55, 0.08, 0.08, 0.08, 0.15, true);
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));
      this.level().addParticle(TensuraParticleUtils.getPoisonBubble(), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
      Vec3 random = this.getRandomVec3().scale(0.1F);
      this.level().addParticle(TensuraParticleUtils.getPoisonEffect(10), vec3.x, vec3.y, vec3.z, random.x, random.y, random.z);
   }
}
