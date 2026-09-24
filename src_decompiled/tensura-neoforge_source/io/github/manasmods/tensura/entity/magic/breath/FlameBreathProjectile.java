package io.github.manasmods.tensura.entity.magic.breath;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class FlameBreathProjectile extends BreathEntity {
   public FlameBreathProjectile(EntityType<? extends FlameBreathProjectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
      this.setBurnTicks(100);
      this.setElementalAttack(true);
   }

   public FlameBreathProjectile(Level level, LivingEntity entity) {
      this((EntityType<? extends FlameBreathProjectile>)MiscEntityTypes.FLAME_BREATH.get(), level);
      this.setOwner(entity);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.FLAME_BREATH;
   }

   @Override
   public boolean hasBlockInteraction() {
      return true;
   }

   @Override
   protected void handleBlockInteraction(Player player, Vec3 lookAngle) {
      ClipContext context = new ClipContext(
         player.getEyePosition(), player.getEyePosition().add(lookAngle.scale(this.getLength())), Block.OUTLINE, Fluid.NONE, this
      );
      BlockHitResult result = this.level().clip(context);
      if (result.getType() == Type.BLOCK) {
         BlockPos blockPos = result.getBlockPos();
         BlockState state = this.level().getBlockState(blockPos);
         this.applyBlockInteraction(result.getLocation(), blockPos, state);
      }
   }

   private void applyBlockInteraction(Vec3 pos, BlockPos blockPos, BlockState state) {
      if (state.is(BlockTags.SNOW) || state.is(BlockTags.LEAVES)) {
         if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y(), pos.z())
            .isFalse()) {
            this.level().destroyBlock(blockPos, false);
            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y(), pos.z());
         }
      } else if (state.is(Blocks.ICE) || state.is(Blocks.FROSTED_ICE)) {
         if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y(), pos.z())
            .isFalse()) {
            this.level().setBlockAndUpdate(blockPos, Blocks.WATER.defaultBlockState());
            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y(), pos.z());
         }
      } else if (!CampfireBlock.canLight(state) && !CandleBlock.canLight(state) && !CandleCakeBlock.canLight(state)) {
         if (this.level().getBlockState(blockPos.above()).isAir()
            && Blocks.FIRE.defaultBlockState().canSurvive(this.level(), blockPos.above())
            && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y() + 1.0, pos.z())
               .isFalse()) {
            this.level().setBlockAndUpdate(blockPos.above(), Blocks.FIRE.defaultBlockState());
            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y() + 1.0, pos.z());
         }
      } else if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
         .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y(), pos.z())
         .isFalse()) {
         this.level().setBlock(blockPos, (BlockState)state.setValue(BlockStateProperties.LIT, true), 11);
         ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y(), pos.z());
      }
   }

   @Override
   public void spawnParticle() {
      if (this.getOwner() instanceof LivingEntity owner) {
         Vec3 var22 = owner.getLookAngle().normalize();
         Vec3 pos = owner.position().add(var22.scale(1.6));
         double x = pos.x;
         double y = pos.y + owner.getEyeHeight() * 0.9F;
         double z = pos.z;
         RandomSource rand = owner.getRandom();
         double speed = rand.nextDouble() * 0.35 + 0.55;

         for (int i = 0; i < 30; i++) {
            double ox = rand.nextDouble() * 0.3 - 0.15;
            double oy = rand.nextDouble() * 0.3 - 0.15;
            double oz = rand.nextDouble() * 0.3 - 0.15;
            Vec3 randomVec = new Vec3(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5).normalize();
            Vec3 result = var22.scale(3.0).add(randomVec).normalize().scale(speed);
            owner.level().addParticle((ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), x + ox, y + oy, z + oz, result.x, result.y, result.z);
         }
      }
   }
}
