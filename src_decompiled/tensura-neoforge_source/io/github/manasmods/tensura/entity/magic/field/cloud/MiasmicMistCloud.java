package io.github.manasmods.tensura.entity.magic.field.cloud;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MiasmicMistCloud extends AreaCloud {
   public MiasmicMistCloud(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public MiasmicMistCloud(Level level, LivingEntity entity) {
      this((EntityType<? extends Projectile>)MiscEntityTypes.MIASMIC_MIST.get(), level);
      this.setOwner(entity);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.WATER_ELEMENTAL;
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return super.canHitEntity(pTarget) && !RaceUtils.isUndead(pTarget) ? this.getOwner() == null || !pTarget.isAlliedTo(this.getOwner()) : false;
      }
   }

   @Override
   public void spawnAmbientParticles() {
      float size = this.getVisualSize() - 1.0F;
      float f = Mth.clamp(3.0F * size, 0.75F, 30.0F);

      for (int i = 0; i < f; i++) {
         if (f - i < 1.0F && this.random.nextFloat() > f - i) {
            return;
         }

         float distance = size * (1.0F - this.random.nextFloat() * this.random.nextFloat());
         float v = this.random.nextFloat() * 6.28F;
         Vec3 pos = new Vec3(distance * Mth.cos(v), 0.2 + (this.random.nextFloat() - 0.5F) * 2.0F, distance * Mth.sin(v));
         Vec3 motion = new Vec3((2.0 * Math.random() - 1.0) * 0.03, this.random.nextDouble() * 0.01, (2.0 * Math.random() - 1.0) * 0.03);
         this.level()
            .addParticle(TensuraParticleUtils.getMiasmicMist(), this.getX() + pos.x, this.getY() + pos.y, this.getZ() + pos.z, motion.x, motion.y, motion.z);
      }
   }
}
