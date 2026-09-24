package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class IceLanceProjectile extends WaterBallProjectile implements GeoEntity {
   protected float chillBonusDamage = 0.0F;
   protected float frostBonusDamage = 0.0F;
   protected boolean iceBreaker = false;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public IceLanceProjectile(EntityType<? extends IceLanceProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.setSize(0.5F);
   }

   public IceLanceProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends IceLanceProjectile>)ProjectileEntityTypes.ICE_LANCE.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putFloat("ChillBonusDamage", this.getChillBonusDamage());
      compound.putFloat("FrostBonusDamage", this.getFrostBonusDamage());
      compound.putBoolean("IceBreaker", this.isIceBreaker());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setChillBonusDamage(compound.getFloat("ChillBonusDamage"));
      this.setFrostBonusDamage(compound.getFloat("FrostBonusDamage"));
      this.setIceBreaker(compound.getBoolean("IceBreaker"));
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/lance/ice_lance.png");
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.ICE_ELEMENTAL;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide() && this.getDelayTick() <= 0 && this.tickCount % 2 == 0) {
         if (this.isIceBreaker() && !this.level().dimensionType().ultraWarm() && this.shouldGrief()) {
            this.placeIces(this, (int)this.getHitRadius());
         }
      }
   }

   @Override
   protected boolean dealDamage(Entity target, float damage, float costMultiplier) {
      if (target instanceof LivingEntity entity) {
         if (this.getFrostBonusDamage() > 0.0F) {
            MobEffectInstance frost = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FROST));
            if (frost != null) {
               damage += (frost.getAmplifier() + 1) * this.getFrostBonusDamage();
            }
         }

         if (this.getChillBonusDamage() > 0.0F) {
            MobEffectInstance chill = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.CHILL));
            if (chill != null) {
               damage += (chill.getAmplifier() + 1) * this.getChillBonusDamage();
            }
         }

         if (super.dealDamage(target, damage, costMultiplier)) {
            if (this.getFrostBonusDamage() > 0.0F) {
               entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.FROST));
            }

            if (this.getChillBonusDamage() > 0.0F) {
               entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.CHILL));
            }

            return true;
         } else {
            return false;
         }
      } else {
         return super.dealDamage(target, damage, costMultiplier);
      }
   }

   @Override
   protected void onHitBlock(@NotNull BlockHitResult pResult) {
      if (!this.level().isClientSide() && this.isIceBreaker() && !this.level().dimensionType().ultraWarm() && this.shouldGrief()) {
         this.placeIces(this, (int)this.getHitRadius() + 1);
         this.breakIces(this);
         EffectStorage.setCameraShake(this, this.getHitRadius() + 1.0F, 0.01F, 10);
      }

      super.onHitBlock(pResult);
   }

   protected void placeIces(Entity entity, int radius) {
      int yPos = Mth.floor(entity.getY());
      int xPos = Mth.floor(entity.getX());
      int zPos = Mth.floor(entity.getZ());
      boolean removeBlock = false;

      for (int j = -radius; j <= radius; j++) {
         for (int k = -radius; k <= radius; k++) {
            for (int i = -radius; i <= radius; i++) {
               if (!(entity.getRandom().nextFloat() >= 0.75)) {
                  int newYPos = yPos + i;
                  int newXPos = xPos + j;
                  int newZPos = zPos + k;
                  BlockPos blockpos = new BlockPos(newXPos, newYPos, newZPos);
                  BlockState state = entity.level().getBlockState(blockpos);
                  if (!state.is(BlockTags.ICE)
                     && (state.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE) || state.is(Blocks.WATER) || state.is(TensuraBlockTags.SKILL_BREAK_EASY))
                     && !(blockpos.distToCenterSqr(entity.position()) > radius * radius)
                     && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(this.getSkill(), this.level(), this.getOwner(), newXPos, newYPos, newZPos)
                        .isFalse()) {
                     removeBlock = this.level()
                           .setBlock(
                              blockpos, entity.getRandom().nextBoolean() ? Blocks.BLUE_ICE.defaultBlockState() : Blocks.PACKED_ICE.defaultBlockState(), 11
                           )
                        || removeBlock;
                     ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                        .grief(this.getSkill(), this.level(), this.getOwner(), newXPos, newYPos, newZPos);
                  }
               }
            }
         }
      }

      if (removeBlock && this.getOwner() != null) {
         this.level().gameEvent(this.getOwner(), GameEvent.BLOCK_CHANGE, this.blockPosition());
      }
   }

   protected void breakIces(Entity entity) {
      int yPos = Mth.floor(entity.getY());
      int xPos = Mth.floor(entity.getX());
      int zPos = Mth.floor(entity.getZ());
      boolean removeBlock = false;
      int radius = (int)this.getHitRadius();

      for (int j = -radius; j <= radius; j++) {
         for (int k = -radius; k <= radius; k++) {
            for (int i = -radius; i <= radius; i++) {
               int newYPos = yPos + i;
               int newXPos = xPos + j;
               int newZPos = zPos + k;
               BlockPos blockpos = new BlockPos(newXPos, newYPos, newZPos);
               if (this.level().getBlockState(blockpos).is(BlockTags.ICE)
                  && !(blockpos.distToCenterSqr(entity.position()) > radius * radius)
                  && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                     .grief(this.getSkill(), this.level(), this.getOwner(), newXPos, newYPos, newZPos)
                     .isFalse()) {
                  removeBlock = this.level().destroyBlock(blockpos, false, this.getOwner()) || removeBlock;
                  ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                     .grief(this.getSkill(), this.level(), this.getOwner(), newXPos, newYPos, newZPos);
               }
            }
         }
      }

      if (removeBlock && this.getOwner() != null) {
         this.level().gameEvent(this.getOwner(), GameEvent.BLOCK_DESTROY, this.blockPosition());
      }
   }

   @Override
   public Optional<SoundEvent> delayShootSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_ICE.get());
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_ICE.get());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.SNOWFLAKE.get(), x, y, z, 15 * (int)this.getSize(), 0.1, 0.1, 0.1, 0.1 * this.getSize(), true
      );
   }

   @Override
   public void flyingParticles() {
      if (this.getDelayTick() <= 0) {
         float radius = this.getHitRadius();

         for (int i = 0; i < Math.max(1.0F, this.getChillBonusDamage() / 20.0F); i++) {
            double speed = 0.05;
            double dx = this.level().random.nextDouble() * radius * speed - speed;
            double dy = this.level().random.nextDouble() * radius * speed - speed;
            double dz = this.level().random.nextDouble() * radius * speed - speed;
            this.level().addParticle((ParticleOptions)TensuraParticleTypes.SNOWFLAKE.get(), this.getX() + dx, this.getY() + dy, this.getZ() + dz, dx, dy, dz);
         }
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllers) {
   }

   @Override
   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   public float getChillBonusDamage() {
      return this.chillBonusDamage;
   }

   @Generated
   public void setChillBonusDamage(float chillBonusDamage) {
      this.chillBonusDamage = chillBonusDamage;
   }

   @Generated
   public float getFrostBonusDamage() {
      return this.frostBonusDamage;
   }

   @Generated
   public void setFrostBonusDamage(float frostBonusDamage) {
      this.frostBonusDamage = frostBonusDamage;
   }

   @Generated
   public boolean isIceBreaker() {
      return this.iceBreaker;
   }

   @Generated
   public void setIceBreaker(boolean iceBreaker) {
      this.iceBreaker = iceBreaker;
   }
}
