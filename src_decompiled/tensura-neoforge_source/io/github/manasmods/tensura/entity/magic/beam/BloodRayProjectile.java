package io.github.manasmods.tensura.entity.magic.beam;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import java.awt.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BloodRayProjectile extends BeamProjectile {
   public BloodRayProjectile(EntityType<? extends BloodRayProjectile> entityType, Level level) {
      super(entityType, level);
      this.beamColorAndSize.put(new Color(255, 0, 0, 255), 0.2F);
      this.beamColorAndSize.put(new Color(255, 122, 122, 100), 0.4F);
   }

   public BloodRayProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends BloodRayProjectile>)MiscEntityTypes.BLOOD_RAY.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.BLOOD_RAY;
   }

   @Override
   protected double getBlockInteractionRangeMultiplier() {
      return 6.0;
   }

   @Override
   protected boolean canDestroyBlock(BlockPos pos) {
      BlockState state = this.level().getBlockState(pos);
      return state.is(TensuraBlockTags.SKILL_BREAK_EASY) || state.canBeReplaced() && !this.level().getFluidState(pos).isSource();
   }

   @Override
   public void startParticles(Vec3 start) {
      if (this.tickCount % 5 == 0) {
         TensuraParticleHelper.addParticlesAroundPos(this.level().random, this.level(), start, DustParticleOptions.REDSTONE, this.getSize(), 2);
      }
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      if (this.tickCount % 3 == 0) {
         Vec3 end = new Vec3(x, y, z);
         TensuraParticleHelper.addParticlesAroundPos(this.level().random, this.level(), end, DustParticleOptions.REDSTONE, this.getSize(), 3);
      }
   }

   @Override
   public void rayParticles(Vec3 pos, int i) {
      if (this.tickCount % this.random.nextInt(15, 20) == 0) {
         TensuraParticleHelper.addParticlesAroundPos(this.random, this.level(), pos, DustParticleOptions.REDSTONE, this.getVisualSize() / 2.0F, 1);
      }
   }
}
