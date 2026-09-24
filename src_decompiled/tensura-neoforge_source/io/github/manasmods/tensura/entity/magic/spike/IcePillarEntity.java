package io.github.manasmods.tensura.entity.magic.spike;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class IcePillarEntity extends PillarEntity {
   public IcePillarEntity(EntityType<? extends IcePillarEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setElementalAttack(true);
   }

   public IcePillarEntity(Level pLevel, LivingEntity pOwner) {
      this((EntityType<? extends IcePillarEntity>)MiscEntityTypes.ICE_PILLAR.get(), pLevel);
      this.setOwner(pOwner);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.ICE_ELEMENTAL;
   }

   @Override
   public void tick() {
      super.tick();
      int aoeInterval = this.getAoeInterval();
      if (this.getAoeDamage() > 0.0F && (aoeInterval == 0 || this.getAge() % aoeInterval == 0)) {
         TensuraParticleHelper.addServerParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.SNOWFLAKE.get(), this.getAoeRange());
      }
   }
}
