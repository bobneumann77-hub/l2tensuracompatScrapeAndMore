package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.block.BlackFireBlock;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BlackFlameBallProjectile extends FireBoltProjectile {
   public BlackFlameBallProjectile(EntityType<? extends BlackFlameBallProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public BlackFlameBallProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends FireBoltProjectile>)ProjectileEntityTypes.BLACK_FLAME_BALL.get(), levelIn);
      this.setOwner(shooter);
      this.setElementalAttack(true);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.BLACK_FLAME;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/black_fire.png");
   }

   protected void onHitEntity(@NotNull EntityHitResult result) {
      if (result.getEntity() instanceof LivingEntity target) {
         target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), 200));
         MobEffectInstance instance = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.BLACK_BURN), 200, 0, true, true, true);
         ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
         TensuraMobEffect.addEffect(target, instance, this.getOwner(), skill, this.getMode());
      }

      super.onHitEntity(result);
   }

   @Override
   protected void onHitBlock(@NotNull BlockHitResult pResult) {
      if (!this.level().isClientSide) {
         boolean skillGrief = this.shouldGrief();
         if (skillGrief) {
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
      }

      super.onHitBlock(pResult);
   }

   @Override
   public void onExplosion(double x, double y, double z) {
      if (!(this.getExplosionRadius() <= 0.0F)) {
         super.onExplosion(x, y, z);
         this.discard();
      }
   }

   @Override
   public DamageSource getExplosionDamageSource() {
      return this.getDamageSource();
   }

   protected void placeFire(Entity entity) {
      int yPos = Mth.floor(entity.getY()) - 1;
      int xPos = Mth.floor(entity.getX());
      int zPos = Mth.floor(entity.getZ());
      BlockState fire = (BlockState)((Block)TensuraBlocks.BLACK_FIRE.get()).defaultBlockState().setValue(FireBlock.AGE, 0);
      boolean placeFire = false;
      boolean removeBlock = false;

      for (int j = -1; j <= 1; j++) {
         for (int k = -1; k <= 1; k++) {
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

                     if (BlackFireBlock.canBePlacedAt(this.level(), blockpos)) {
                        placeFire = this.level().setBlockAndUpdate(blockpos, fire) || placeFire;
                        this.level().scheduleTick(blockpos, blockState.getBlock(), BlackFireBlock.getFireTickDelay(this.level().random));
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

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.EXPLOSION, x, y, z, 1, 0.12, 0.12, 0.12, 0.15, false);
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.FLASH, x, y, z, 3, 0.12, 0.12, 0.12, 0.15, false);
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
            .addParticle((ParticleOptions)TensuraParticleTypes.BLACK_FIRE.get(), d0 + pos.x, d1 + 0.5 + pos.y, d2 + pos.z, motion.x, motion.y, motion.z);
      }
   }
}
