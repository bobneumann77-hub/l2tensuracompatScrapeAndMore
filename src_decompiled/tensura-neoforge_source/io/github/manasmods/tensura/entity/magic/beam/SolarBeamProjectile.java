package io.github.manasmods.tensura.entity.magic.beam;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.awt.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SolarBeamProjectile extends BeamProjectile {
   public SolarBeamProjectile(EntityType<? extends SolarBeamProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.beamColorAndSize.put(new Color(252, 186, 3, 255), 0.33F);
      this.beamColorAndSize.put(new Color(252, 186, 3, 120), 0.66F);
      this.beamColorAndSize.put(new Color(255, 255, 255, 30), 1.0F);
   }

   public SolarBeamProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends SolarBeamProjectile>)MiscEntityTypes.SOLAR_BEAM.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.LIGHT_ELEMENTAL;
   }

   @Override
   protected boolean dealDamage(Entity target) {
      if (this.damage <= 0.0F || target instanceof ItemEntity) {
         return false;
      } else if (super.dealDamage(target)) {
         TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get());
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void interactBlocks(BlockPos pos) {
      if (this.canDestroyBlock(pos)) {
         if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
            .isFalse()) {
            if (this.level().removeBlock(pos, false)) {
               ((ServerLevel)this.level()).sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.04, 0.06, 0.04, 0.05);
            }

            this.placeFires(pos);
            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
         }
      }
   }

   @Override
   protected boolean canDestroyBlock(BlockPos pos) {
      BlockState state = this.level().getBlockState(pos);
      if (state.is(TensuraBlockTags.SKILL_SMELT_EASY)) {
         return true;
      } else if (!state.canBeReplaced()) {
         return false;
      } else {
         return state.is(Blocks.FIRE) ? false : !this.level().getFluidState(pos).isSource();
      }
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      if (this.tickCount % 3 == 0) {
         Vec3 end = new Vec3(x, y, z);
         TensuraParticleHelper.addParticlesAroundPos(
            this.level().random, this.level(), end, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get(), this.getSize(), 1
         );
      }
   }
}
