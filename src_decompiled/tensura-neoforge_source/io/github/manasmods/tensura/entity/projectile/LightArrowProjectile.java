package io.github.manasmods.tensura.entity.projectile;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class LightArrowProjectile extends TensuraFlyingProjectile {
   public LightArrowProjectile(EntityType<? extends LightArrowProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElement(Element.LIGHT);
      this.setElementalAttack(true);
   }

   public LightArrowProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends LightArrowProjectile>)ProjectileEntityTypes.LIGHT_ARROW.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceLocation[] getTextureLocation() {
      return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/light_arrow.png")};
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.LIGHT_ELEMENTAL;
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
   }
}
