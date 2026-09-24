package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class SpatialArrowProjectile extends TensuraFlyingProjectile {
   public SpatialArrowProjectile(EntityType<? extends SpatialArrowProjectile> entityType, Level level) {
      super(entityType, level);
      this.setPiercingBlock(true);
      this.setElement(Element.SPACE);
   }

   public SpatialArrowProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends Projectile>)ProjectileEntityTypes.SPATIAL_ARROW.get(), levelIn);
      this.setOwner(shooter);
      this.setElement(Element.SPACE);
   }

   @Override
   public ResourceLocation[] getTextureLocation() {
      return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/energy_arrow.png")};
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.BULLET;
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
   protected boolean canHitEntity(Entity pTarget) {
      return pTarget == this.getOwner() ? false : super.canHitEntity(pTarget);
   }

   @Override
   protected void playHitSound(SoundEvent sound, HitResult hitresult) {
      if (hitresult.getType().equals(Type.ENTITY)) {
         super.playHitSound(sound, hitresult);
      }
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.ENCHANTED_HIT, x, y, z, 15, 0.1, 0.1, 0.1, 0.1, true);
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));

      for (int i = 0; i < 2; i++) {
         Vec3 random = this.getRandomVec3().scale(0.1F);
         this.level().addParticle(ParticleTypes.ENCHANTED_HIT, vec3.x, vec3.y, vec3.z, random.x, random.y, random.z);
      }
   }
}
