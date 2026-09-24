package io.github.manasmods.tensura.entity.magic.spike;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.template.subclass.IElementalSpirit;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DripstoneSpikeEntity extends MagicSpikeEntity {
   public DripstoneSpikeEntity(EntityType<? extends DripstoneSpikeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setElementalAttack(true);
      this.copyBlockHealth(Blocks.DRIPSTONE_BLOCK, 300.0F);
   }

   public DripstoneSpikeEntity(Level pLevel, LivingEntity pOwner) {
      this((EntityType<? extends DripstoneSpikeEntity>)MiscEntityTypes.EARTH_SPIKE.get(), pLevel);
      this.setOwner(pOwner);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.EARTH_ELEMENTAL;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/spike/dripstone_spike.png");
   }

   @Override
   public boolean canCollideWith(@NotNull Entity entity) {
      return entity instanceof IElementalSpirit ? false : super.canCollideWith(entity);
   }

   protected void markHurt() {
      super.markHurt();
      BlockState state = Blocks.DRIPSTONE_BLOCK.defaultBlockState();
      TensuraParticleHelper.addServerParticlesAroundSelf(this, new BlockParticleOption(ParticleTypes.BLOCK, state), 0.5);
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), state.getSoundType().getHitSound(), this.getSoundSource(), 2.0F, 1.0F);
   }

   @Override
   public void onBreak() {
      BlockState state = Blocks.DRIPSTONE_BLOCK.defaultBlockState();
      ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, particle, 0.5);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, particle, 0.5);
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), state.getSoundType().getBreakSound(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      super.onBreak();
   }
}
