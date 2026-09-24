package io.github.manasmods.tensura.entity.magic.spike;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class FirePillarEntity extends PillarEntity {
   private static final EntityDataAccessor<Boolean> ILLUSION = SynchedEntityData.defineId(FirePillarEntity.class, EntityDataSerializers.BOOLEAN);

   public FirePillarEntity(EntityType<? extends FirePillarEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.blocksBuilding = false;
      this.setElementalAttack(true);
      this.setBlockState(Blocks.FIRE.defaultBlockState());
   }

   public FirePillarEntity(Level pLevel, LivingEntity pOwner) {
      this((EntityType<? extends FirePillarEntity>)MiscEntityTypes.FIRE_PILLAR.get(), pLevel);
      this.setOwner(pOwner);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(ILLUSION, Boolean.FALSE);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Illusion", this.isIllusion());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setIllusion(compound.getBoolean("Illusion"));
   }

   public boolean isIllusion() {
      return (Boolean)this.entityData.get(ILLUSION);
   }

   public void setIllusion(boolean illusion) {
      this.entityData.set(ILLUSION, illusion);
   }

   @Override
   public boolean canCollideWith(@NotNull Entity entity) {
      return false;
   }

   @Override
   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canBeHitByProjectile() {
      return false;
   }

   @Override
   public boolean shouldTakeDamage(DamageSource pSource) {
      return false;
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return this.isIllusion() ? TensuraDamageTypes.MAGIC_GENERIC : TensuraDamageTypes.FIRE_ELEMENTAL;
   }

   @Override
   public ResourceKey<DamageType> getContactDamageType() {
      return this.getDamageType();
   }

   @Override
   public DamageSource getDamageSource(ResourceKey<DamageType> type, float costMultiplier) {
      return super.getDamageSource(type, costMultiplier).tensura$setDodgeBypass();
   }

   @Override
   public boolean applyBlockParticle() {
      return false;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.tickCount % 2 == 0) {
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRE_AMBIENT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      if (this.level().isClientSide()) {
         RandomSource rand = this.getRandom();

         for (int j = 0; j < 4; j++) {
            double dX = rand.nextGaussian() * 0.5;
            double dY = rand.nextGaussian() * 0.25;
            double dZ = rand.nextGaussian() * 0.5;
            ParticleOptions particleOptions = this.isIllusion()
               ? (ParticleOptions)TensuraParticleTypes.ILLUSION_FIRE.get()
               : (ParticleOptions)TensuraParticleTypes.RED_FIRE.get();
            this.level()
               .addParticle(
                  particleOptions,
                  this.position().x + dX,
                  this.position().y + dY,
                  this.position().z + dZ,
                  0.0,
                  rand.nextDouble() * 0.1 * this.getHeight() + 0.05,
                  0.0
               );
         }
      }
   }
}
