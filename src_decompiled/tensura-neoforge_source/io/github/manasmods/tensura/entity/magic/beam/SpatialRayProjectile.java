package io.github.manasmods.tensura.entity.magic.beam;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import java.awt.Color;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SpatialRayProjectile extends BeamProjectile {
   public SpatialRayProjectile(EntityType<? extends SpatialRayProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.setElement(Element.SPACE);
      this.beamColorAndSize.put(new Color(211, 0, 255, 255), 0.15F);
      this.beamColorAndSize.put(new Color(91, 32, 105, 150), 0.3F);
      this.beamColorAndSize.put(new Color(202, 136, 209, 30), 0.5F);
   }

   public SpatialRayProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends SpatialRayProjectile>)MiscEntityTypes.SPATIAL_RAY.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.DIMENSION_RAY;
   }

   @Override
   public boolean piercingBlock() {
      return true;
   }

   @Override
   public void startParticles(Vec3 start) {
      if (this.tickCount % 7 == 0) {
         TensuraParticleHelper.addParticlesAroundPos(this.level().random, this.level(), start, ParticleTypes.REVERSE_PORTAL, this.getSize(), 3);
      }
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      if (this.tickCount % 3 == 0) {
         Vec3 end = new Vec3(x, y, z);
         TensuraParticleHelper.addParticlesAroundPos(this.level().random, this.level(), end, ParticleTypes.REVERSE_PORTAL, this.getSize(), 3);
      }
   }

   @Override
   public void rayParticles(Vec3 pos, int i) {
      if (this.tickCount % this.random.nextInt(10, 16) == 0) {
         TensuraParticleHelper.addParticlesAroundPos(this.random, this.level(), pos, ParticleTypes.REVERSE_PORTAL, this.getVisualSize() / 2.0F, 1);
      }
   }
}
