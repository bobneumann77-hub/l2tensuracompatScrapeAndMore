package io.github.manasmods.tensura.entity.magic.lightning;

import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LightningBolt extends TensuraLightningBolt {
   public LightningBolt(EntityType<? extends LightningBolt> type, Level pLevel) {
      super(type, pLevel);
      this.setElementalAttack(true);
   }

   public LightningBolt(Level pLevel, Entity owner) {
      super((EntityType<? extends net.minecraft.world.entity.LightningBolt>)MiscEntityTypes.LIGHTNING_BOLT.get(), pLevel, owner);
   }

   @Override
   protected ParticleOptions getLightningParticle() {
      return this.getSkill() != null && this.getSkill().is(TensuraSkillTags.ASPECTUAL_MAGIC)
         ? (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get()
         : (ParticleOptions)TensuraParticleTypes.LIGHTNING_EFFECT.get();
   }
}
