package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class ObsidianShotProjectile extends StoneShotProjectile {
   public ObsidianShotProjectile(EntityType<? extends ObsidianShotProjectile> entityType, Level level) {
      super(entityType, level);
      this.setArmorHurt(10);
   }

   public ObsidianShotProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends ObsidianShotProjectile>)ProjectileEntityTypes.OBSIDIAN_SHOT.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/lance/obsidian_shot.png");
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OBSIDIAN.defaultBlockState()), x, y, z, 10, 0.08, 0.08, 0.08, 0.1, false
      );
   }

   @Override
   public void flyingParticles() {
      if (this.getDelayTick() <= 0) {
         Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));
         this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OBSIDIAN.defaultBlockState()), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
      }
   }
}
