package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierPart;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SupermassiveSlimeEntity extends SlimeEntity {
   private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(), BossBarColor.BLUE, BossBarOverlay.NOTCHED_20);

   public SupermassiveSlimeEntity(EntityType<? extends SupermassiveSlimeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setSize(this.getRandom().nextInt(18, 21), false, false, false);
      this.setMassive(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 200.0)
         .add(Attributes.ARMOR, 2.0)
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.JUMP_STRENGTH, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.5)
         .add(Attributes.STEP_HEIGHT, 3.0)
         .add(Attributes.SAFE_FALL_DISTANCE, 50.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 3.0)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 4.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 4.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 4.0);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }
   }

   @Override
   public boolean isTamingFood(ItemStack stack) {
      return false;
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   protected float getWaterSlowDown() {
      return 0.9F;
   }

   @Override
   public boolean hurt(DamageSource pSource, float amount) {
      Entity sourceEntity = pSource.getEntity();
      if (sourceEntity instanceof SlimeEntity) {
         return false;
      } else if (pSource.getDirectEntity() instanceof Projectile projectile && !projectile.getType().is(TensuraEntityTags.CANNOT_DODGE)) {
         this.triggerAnim("miscController", "damage");
         this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, 0.8F);
         this.level().playSound(null, this, (SoundEvent)TensuraSoundEvents.EATER.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.8F);
         projectile.remove(RemovalReason.KILLED);
         return false;
      } else {
         if (sourceEntity instanceof ServerPlayer serverPlayer) {
            this.startSeenByPlayer(serverPlayer);
         }

         return super.hurt(pSource, amount);
      }
   }

   public void setCustomName(@Nullable Component pName) {
      super.setCustomName(pName);
      this.bossEvent.setName(this.getDisplayName());
   }

   public void startSeenByPlayer(ServerPlayer pPlayer) {
      super.startSeenByPlayer(pPlayer);
      if (!this.isTame()) {
         this.bossEvent.addPlayer(pPlayer);
      }
   }

   public void stopSeenByPlayer(ServerPlayer pPlayer) {
      super.stopSeenByPlayer(pPlayer);
      this.bossEvent.removePlayer(pPlayer);
   }

   @Override
   protected void applyTamingSideEffects() {
      super.applyTamingSideEffects();
      this.bossEvent.removeAllPlayers();
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         if (this.isAlive()) {
            if (this.isInWater() && !this.isVehicle()) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.05, 0.0));
            }
         }
      }
   }

   @Override
   protected void selfRegen() {
      this.heal(20.0F);
      this.selfRegen = 20;
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      super.customServerAiStep();
      if (!this.isTame()) {
         this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
      }

      if (this.tickCount % 20 == 0) {
         List<BarrierPart> list = this.level().getEntitiesOfClass(BarrierPart.class, this.getBoundingBox().inflate(1.0));
         if (!list.isEmpty()) {
            for (BarrierPart barrier : list) {
               this.doHurtTarget(barrier);
            }
         }
      }

      if (!this.isTame()) {
         int broken = this.breakBlocks(this, 1.0F, false);
         if (broken > 0) {
            this.triggerAnim("miscController", "jump");
            EnergyHelper.gainMagicule(this, 100 * broken, EnergyHelper.GainType.NORMAL);
         }
      }
   }

   public boolean killedEntity(ServerLevel pLevel, LivingEntity pEntity) {
      boolean wasKilled = super.killedEntity(pLevel, pEntity);
      if (wasKilled && this.isAlive()) {
         EnergyHelper.gainMagicule(this, EnergyHelper.getMaxEP(pEntity), EnergyHelper.GainType.NORMAL);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.COMPOSTER);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.COMPOSTER, 1.0);
      }

      return wasKilled;
   }

   @Override
   public boolean breakableBlocks(LivingEntity living, BlockPos pos, BlockState state) {
      return this.isTame() ? state.is(TensuraBlockTags.BREAKABLE_BY_MONSTER) : !state.is(TensuraBlockTags.BOSS_IMMUNE);
   }

   @Override
   public boolean dropBlockLoot(LivingEntity entity, BlockState state) {
      return this.isTame() ? true : !state.is(TensuraBlockTags.SKILL_BREAK_EASY);
   }

   @NotNull
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      this.setSize(this.getRandom().nextInt(18, 21), false, false, false);
      this.setMassive(true);
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }
}
