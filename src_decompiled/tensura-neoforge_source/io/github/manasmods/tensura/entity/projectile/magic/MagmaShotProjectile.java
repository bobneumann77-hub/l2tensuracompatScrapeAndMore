package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MagmaShotProjectile extends FireBoltProjectile {
   public MagmaShotProjectile(EntityType<? extends MagmaShotProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElement(Element.EARTH);
      this.setElementalAttack(true);
      this.setSize(1.0F);
   }

   public MagmaShotProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends MagmaShotProjectile>)ProjectileEntityTypes.MAGMA_SHOT.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.EARTH_ELEMENTAL;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public void updateMovement() {
      Vec3 movement = this.getDeltaMovement();
      this.setPos(this.position().add(movement));
      if (this.getDelayTick() <= 0) {
         Entity target = this.getHomingTarget();
         if (target != null) {
            double distance = this.distanceTo(target);
            double x = (target.getX() - this.getX()) / distance;
            double y = (target.getY() + target.getBbHeight() / 2.0F - this.getY()) / distance;
            double z = (target.getZ() - this.getZ()) / distance;
            double speed = Math.max(this.getSpeed(), this.getDeltaMovement().length());
            this.setDeltaMovement(new Vec3(x, y, z).scale(speed));
            if (distance < 1.0) {
               this.setHomingTarget(null);
            }
         } else if (this.getLookDistance() <= 0.0F && !this.isNoGravity()) {
            this.setDeltaMovement(movement.scale(0.99F));
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.02F, 0.0));
         }
      }
   }

   @Override
   protected void onHitBlock(@NotNull BlockHitResult pResult) {
      List<LivingEntity> livingEntityList = this.level()
         .getEntitiesOfClass(
            LivingEntity.class, this.getBoundingBox().inflate(2.0), entity -> !entity.fireImmune() && !entity.hasEffect(MobEffects.FIRE_RESISTANCE)
         );
      if (!livingEntityList.isEmpty()) {
         for (LivingEntity pLivingEntity : livingEntityList) {
            pLivingEntity.setRemainingFireTicks(this.getBurnTicks());
         }
      }

      super.onHitBlock(pResult);
      if (!this.level().isClientSide) {
         this.placeLava(this);
      }

      EffectStorage.setCameraShake(this, 5.0, 0.01F, 10);
   }

   protected void onHitEntity(@NotNull EntityHitResult result) {
      List<LivingEntity> livingEntityList = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            this.getBoundingBox().inflate(2.0),
            entity -> !entity.fireImmune() && !entity.hasEffect(MobEffects.FIRE_RESISTANCE) && !entity.equals(result.getEntity())
         );
      if (!livingEntityList.isEmpty()) {
         for (LivingEntity pLivingEntity : livingEntityList) {
            pLivingEntity.setRemainingFireTicks(this.getBurnTicks());
            pLivingEntity.hurt(TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.BURN, this.getOwner()), this.getDamage());
            if (this.getOwner() instanceof Player player) {
               pLivingEntity.setLastHurtByPlayer(player);
            }
         }
      }

      super.onHitEntity(result);
      if (!this.level().isClientSide) {
         this.placeLava(this);
      }

      EffectStorage.setCameraShake(this, 5.0, 0.01F, 10);
   }

   @Override
   public void setPosAndShoot(LivingEntity entity) {
      this.setPosAndShoot(entity, 0.0F);
   }

   public void setPosAndShoot(LivingEntity entity, float pInaccuracy) {
      this.setPos(entity.position().add(0.0, entity.getEyeHeight() - this.getBoundingBox().getYsize() * 0.5, 0.0));
      Vec3 vector = entity.getViewVector(2.0F);
      this.shoot(vector.x(), vector.y(), vector.z(), this.getSpeed(), pInaccuracy);
   }

   protected void placeLava(Entity entity) {
      if (this.shouldGrief()) {
         int yPos = Mth.floor(entity.getY()) - 1;
         int xPos = Mth.floor(entity.getX());
         int zPos = Mth.floor(entity.getZ());
         BlockState lava = (BlockState)Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, 12);
         boolean placeFire = false;
         boolean removeBlock = false;

         for (int j = -2; j <= 2; j++) {
            for (int k = -2; k <= 2; k++) {
               for (int i = -1; i <= 3; i++) {
                  if (this.level().random.nextDouble() > 0.3) {
                     int newYPos = yPos + i;
                     int newXPos = xPos + j;
                     int newZPos = zPos + k;
                     BlockPos blockpos = new BlockPos(newXPos, newYPos, newZPos);
                     BlockState blockState = this.level().getBlockState(blockpos);
                     if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(this.getSkill(), this.level(), this.getOwner(), newXPos, newYPos, newZPos)
                        .isFalse()) {
                        if (blockState.canBeReplaced() && blockState.getFluidState().isEmpty()) {
                           BlockPos blockPosDown = blockpos.below();
                           BlockState blockStateDown = this.level().getBlockState(blockPosDown);
                           if (blockStateDown.isFaceSturdy(this.level(), blockPosDown, Direction.UP)) {
                              removeBlock = this.level().removeBlock(blockpos, true) || removeBlock;
                           }
                        }

                        if (FireBlock.canBePlacedAt(this.level(), blockpos, Direction.UP)) {
                           placeFire = this.level().setBlockAndUpdate(blockpos, lava) || placeFire;
                        }

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

         if (placeFire && this.getOwner() != null) {
            this.level().gameEvent(this.getOwner(), GameEvent.BLOCK_CHANGE, this.blockPosition());
         }
      }
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)SoundEvents.GENERIC_EXPLODE.value());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.LAVA, x, y, z, 30, 1.5, 0.1, 1.5, 1.0, false);
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.getDeltaMovement();
      double d0 = this.getX() - vec3.x;
      double d1 = this.getY() - vec3.y;
      double d2 = this.getZ() - vec3.z;

      for (int i = 0; i < 4; i++) {
         Vec3 random = this.getRandomVec3().scale(0.2F);
         this.level().addParticle(ParticleTypes.SMOKE, d0 - random.x, d1 + 0.5 - random.y, d2 - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
      }
   }
}
