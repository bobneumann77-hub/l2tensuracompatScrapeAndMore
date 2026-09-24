package io.github.manasmods.tensura.entity.magic.spike;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PillarEntity extends SpikeEntity {
   private static final EntityDataAccessor<BlockState> STATE = SynchedEntityData.defineId(PillarEntity.class, EntityDataSerializers.BLOCK_STATE);

   public PillarEntity(EntityType<? extends PillarEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setElementalAttack(true);
      this.noCulling = true;
   }

   public PillarEntity(Level pLevel, LivingEntity pOwner) {
      this((EntityType<? extends PillarEntity>)MiscEntityTypes.EARTH_PILLAR.get(), pLevel);
      this.setOwner(pOwner);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(STATE, Blocks.DIRT.defaultBlockState());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setBlockState(NbtUtils.readBlockState(VanillaRegistries.createLookup().lookupOrThrow(Registries.BLOCK), pCompound.getCompound("BlockState")));
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.put("BlockState", NbtUtils.writeBlockState(this.getBlockState()));
   }

   public void setBlockState(BlockState state) {
      this.entityData.set(STATE, state);
      this.copyBlockHealth(state.getBlock(), 300.0F);
   }

   public BlockState getBlockState() {
      return (BlockState)this.entityData.get(STATE);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.EARTH_ELEMENTAL;
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return true;
   }

   public boolean applyBlockParticle() {
      return true;
   }

   @Override
   public void applyEffect(LivingEntity target) {
      if (this.getAge() <= this.getExtendingTick()) {
         super.applyEffect(target);
      }
   }

   protected void markHurt() {
      super.markHurt();
      if (this.applyBlockParticle()) {
         BlockState state = this.getBlockState();
         TensuraParticleHelper.addServerParticlesAroundSelf(this, new BlockParticleOption(ParticleTypes.BLOCK, state), 0.5);
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), state.getSoundType().getHitSound(), this.getSoundSource(), 2.0F, 1.0F);
      }
   }

   @Override
   public void tick() {
      super.tick();
      float tick = this.getExtendingTick();
      int time = this.getLife() - this.getAge();
      if (time <= tick) {
         this.level()
            .playSound(null, this.getX(), this.getY(), this.getZ(), this.getBlockState().getSoundType().getBreakSound(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   @Override
   public void onBreak() {
      if (this.applyBlockParticle()) {
         BlockState state = this.getBlockState();
         ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, particle, 0.5);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, particle, 0.5);
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), state.getSoundType().getBreakSound(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      super.onBreak();
   }
}
