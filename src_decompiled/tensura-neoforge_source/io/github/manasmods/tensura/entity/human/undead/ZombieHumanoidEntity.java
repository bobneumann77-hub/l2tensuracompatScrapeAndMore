package io.github.manasmods.tensura.entity.human.undead;

import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.entity.variant.ZombieVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.Zombie.ZombieGroupData;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ZombieHumanoidEntity extends UndeadHumanoidEntity implements VariantHolder<ZombieVariant> {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(ZombieHumanoidEntity.class, EntityDataSerializers.INT);

   public ZombieHumanoidEntity(EntityType<? extends UndeadHumanoidEntity> pEntityType, Level pLevel) {
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

   public ZombieVariant getVariant() {
      return ZombieVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(ZombieVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
   }

   @Override
   protected void applyBurn() {
      if (this.getVariant() != ZombieVariant.HOT) {
         super.applyBurn();
      }
   }

   @Override
   public boolean doHurtTarget(Entity entity) {
      boolean hurt = super.doHurtTarget(entity);
      if (hurt) {
         if (this.getVariant() != ZombieVariant.HOT) {
            return true;
         }

         if (this.getMainHandItem().isEmpty() && entity instanceof LivingEntity target && target.getLastHurtByMobTimestamp() == target.tickCount) {
            float f = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 140 * (int)f), this);
         }
      }

      return hurt;
   }

   public boolean killedEntity(ServerLevel serverLevel, LivingEntity livingEntity) {
      boolean killed = super.killedEntity(serverLevel, livingEntity);
      if ((serverLevel.getDifficulty() == Difficulty.EASY || serverLevel.getDifficulty() == Difficulty.HARD) && livingEntity instanceof Villager villager) {
         ZombieVillager zombieVillager = (ZombieVillager)villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
         if (zombieVillager != null) {
            zombieVillager.finalizeSpawn(
               serverLevel, serverLevel.getCurrentDifficultyAt(zombieVillager.blockPosition()), MobSpawnType.CONVERSION, new ZombieGroupData(false, true)
            );
            zombieVillager.setVillagerData(villager.getVillagerData());
            zombieVillager.setGossips((Tag)villager.getGossips().store(NbtOps.INSTANCE));
            zombieVillager.setTradeOffers(villager.getOffers().copy());
            zombieVillager.setVillagerXp(villager.getVillagerXp());
            if (!this.isSilent()) {
               serverLevel.levelEvent(null, 1026, this.blockPosition(), 0);
            }

            killed = false;
         }
      }

      return killed;
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
         this.setVariant(ZombieVariant.HOT);
      } else if (biomes.is(TensuraBiomeTags.IS_COLD)) {
         this.setVariant(ZombieVariant.COLD);
      } else if (biomes.is(TensuraBiomeTags.IS_SWAMP) || biomes.is(BiomeTags.IS_OCEAN) || biomes.is(BiomeTags.IS_RIVER)) {
         this.setVariant(ZombieVariant.MOSSY);
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ZOMBIE_STEP;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }
}
