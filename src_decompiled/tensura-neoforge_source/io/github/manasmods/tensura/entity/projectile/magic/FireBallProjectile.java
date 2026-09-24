package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FireBallProjectile extends FireBoltProjectile {
   public FireBallProjectile(EntityType<? extends FireBallProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.setImpactParticleCount(8);
   }

   public FireBallProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends FireBallProjectile>)ProjectileEntityTypes.FIRE_BALL.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   protected void onHitBlock(@NotNull BlockHitResult pResult) {
      if (!this.level().isClientSide && this.shouldGrief()) {
         this.placeFire(this);
         BlockPos pos = pResult.getBlockPos().relative(pResult.getDirection());
         if (this.level().isEmptyBlock(pos)
            && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
               .isFalse()) {
            this.level().setBlockAndUpdate(pos, BaseFireBlock.getState(this.level(), pos));
            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
         }
      }

      super.onHitBlock(pResult);
   }

   protected void placeFire(Entity entity) {
      if (this.getImpactParticleCount() >= 5) {
         int yPos = Mth.floor(entity.getY()) - 1;
         int xPos = Mth.floor(entity.getX());
         int zPos = Mth.floor(entity.getZ());
         BlockState fire = (BlockState)Blocks.FIRE.defaultBlockState().setValue(FireBlock.AGE, 0);
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
                           placeFire = this.level().setBlockAndUpdate(blockpos, fire) || placeFire;
                           this.level().scheduleTick(blockpos, blockState.getBlock(), 30 + this.level().getRandom().nextInt(10));
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
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/fire.png");
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of(SoundEvents.DRAGON_FIREBALL_EXPLODE);
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.getDeltaMovement();
      double d0 = this.getX() - vec3.x;
      double d1 = this.getY() - vec3.y;
      double d2 = this.getZ() - vec3.z;

      for (int i = 0; i < 8; i++) {
         Vec3 motion = this.getRandomVec3().scale(0.1F).subtract(this.getDeltaMovement().scale(0.1F));
         Vec3 pos = this.getRandomVec3().scale(0.3F);
         this.level()
            .addParticle(
               (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(),
               d0 + pos.x,
               d1 + this.getBbHeight() / 2.0F + pos.y,
               d2 + pos.z,
               motion.x,
               motion.y,
               motion.z
            );
      }
   }
}
