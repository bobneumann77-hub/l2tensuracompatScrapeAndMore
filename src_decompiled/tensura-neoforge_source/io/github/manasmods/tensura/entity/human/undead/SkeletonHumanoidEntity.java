package io.github.manasmods.tensura.entity.human.undead;

import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.entity.variant.SkeletonVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkeletonHumanoidEntity extends UndeadHumanoidEntity implements VariantHolder<SkeletonVariant> {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(SkeletonHumanoidEntity.class, EntityDataSerializers.INT);

   public SkeletonHumanoidEntity(EntityType<? extends UndeadHumanoidEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ID_TYPE_VARIANT, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(DATA_ID_TYPE_VARIANT, compound.getInt("Variant"));
   }

   public SkeletonVariant getVariant() {
      return SkeletonVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(SkeletonVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
   }

   @Override
   protected void applyBurn() {
      if (this.getVariant() != SkeletonVariant.HOT) {
         super.applyBurn();
      }
   }

   @NotNull
   @Override
   public ItemStack getProjectile(ItemStack itemStack) {
      ItemStack stack = super.getProjectile(itemStack);
      if (stack.isEmpty()) {
         return switch (this.getVariant()) {
            case DEFAULT -> Items.ARROW.getDefaultInstance();
            case HOT, COLD -> {
               ItemStack arrow = Items.TIPPED_ARROW.getDefaultInstance();
               arrow.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WEAKNESS));
               yield arrow;
            }
            case MOSSY -> {
               ItemStack arrow = Items.TIPPED_ARROW.getDefaultInstance();
               arrow.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.POISON));
               yield arrow;
            }
         };
      } else {
         return stack;
      }
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData
   ) {
      if (this.canRandomizeSpawnData(mobSpawnType)) {
         this.biomesBasedVariant(serverLevelAccessor);
      }

      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData);
   }

   public void biomesBasedVariant(ServerLevelAccessor pLevel) {
      Holder<Biome> biomes = pLevel.getBiome(this.getOnPos());
      if (biomes.is(TensuraBiomeTags.IS_DESERT)) {
         this.setVariant(SkeletonVariant.HOT);
      } else if (biomes.is(TensuraBiomeTags.IS_COLD)) {
         this.setVariant(SkeletonVariant.COLD);
      } else if (biomes.is(TensuraBiomeTags.IS_SWAMP)) {
         this.setVariant(SkeletonVariant.MOSSY);
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SKELETON_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.SKELETON_STEP;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }
}
