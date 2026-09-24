package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MudShotProjectile extends StoneShotProjectile {
   public MudShotProjectile(EntityType<? extends MudShotProjectile> entityType, Level level) {
      super(entityType, level);
      this.setSize(1.5F);
   }

   public MudShotProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends MudShotProjectile>)ProjectileEntityTypes.MUD_SHOT.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/mud.png");
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of(SoundEvents.MUD_BREAK);
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getMudEffect(), x, y, z, 55, 0.08, 0.08, 0.08, 0.1, true);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getMudBubble(), x, y, z, 25, 0.08, 0.08, 0.08, 0.1, false);
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));
      this.level().addParticle(TensuraParticleUtils.getMudBubble(), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);

      for (int i = 0; i < 2; i++) {
         Vec3 random = this.getRandomVec3().scale(0.1F);
         this.level().addParticle(TensuraParticleUtils.getMudEffect(), vec3.x, vec3.y, vec3.z, random.x, random.y, random.z);
      }
   }
}
