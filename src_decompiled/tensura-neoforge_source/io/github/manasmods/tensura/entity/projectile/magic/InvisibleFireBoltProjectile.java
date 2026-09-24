package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class InvisibleFireBoltProjectile extends FireBoltProjectile {
   public InvisibleFireBoltProjectile(EntityType<? extends InvisibleFireBoltProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public InvisibleFireBoltProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends FireBoltProjectile>)ProjectileEntityTypes.INVISIBLE_FIRE_BOLT.get(), levelIn);
      this.setOwner(shooter);
      this.visible = false;
   }

   @Override
   public ResourceLocation[] getTextureLocation() {
      return new ResourceLocation[]{
         ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/fire_bolt/firebolt_0.png"),
         ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/fire_bolt/firebolt_1.png"),
         ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/fire_bolt/firebolt_2.png"),
         ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/fire_bolt/firebolt_3.png"),
         ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/fire_bolt/firebolt_4.png"),
         ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/fire_bolt/firebolt_5.png")
      };
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getOwner() instanceof ServerPlayer player) {
         TensuraParticleHelper.spawnParticlesToOnePLayer(
            player, (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), this.getX(), this.getY(), this.getZ(), 2, 0.0, 0.0, 0.0, 0.0, false
         );
      }
   }

   @Override
   public void flyingParticles() {
   }
}
