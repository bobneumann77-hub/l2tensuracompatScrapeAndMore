package io.github.manasmods.tensura.entity.magic.breath;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class ThunderBreathProjectile extends BreathEntity {
   public ThunderBreathProjectile(EntityType<? extends ThunderBreathProjectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
      this.setBurnTicks(50);
   }

   public ThunderBreathProjectile(Level level, LivingEntity entity) {
      this((EntityType<? extends ThunderBreathProjectile>)MiscEntityTypes.THUNDER_BREATH.get(), level);
      this.setOwner(entity);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.THUNDER_BREATH;
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
      if (state.is(BlockTags.SNOW)) {
         if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y(), pos.z())
            .isFalse()) {
            this.level().destroyBlock(blockPos, false);
            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y(), pos.z());
         }
      } else if ((state.is(Blocks.ICE) || state.is(Blocks.FROSTED_ICE))
         && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y(), pos.z())
            .isFalse()) {
         this.level().setBlockAndUpdate(blockPos, Blocks.WATER.defaultBlockState());
         ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y(), pos.z());
      }
   }

   @Override
   public void spawnParticle() {
      if (this.getOwner() instanceof LivingEntity owner) {
         Vec3 var21 = owner.getLookAngle().normalize();
         Vec3 pos = owner.position().add(var21.scale(1.6));
         double x = pos.x;
         double y = pos.y + owner.getEyeHeight() * 0.9F;
         double z = pos.z;
         double speed = owner.getRandom().nextDouble() * 0.35 + 0.6;

         for (int i = 0; i < 15; i++) {
            double ox = Math.random() * 0.3 - 0.15;
            double oy = Math.random() * 0.3 - 0.15;
            double oz = Math.random() * 0.3 - 0.15;
            Vec3 randomVec = new Vec3(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).normalize();
            Vec3 result = var21.scale(3.0).add(randomVec).normalize().scale(speed);
            owner.level().addParticle((ParticleOptions)TensuraParticleTypes.LIGHTNING_EFFECT.get(), x + ox, y + oy, z + oz, result.x, result.y, result.z);
         }
      }
   }
}
