package io.github.manasmods.tensura.entity.magic.breath;

import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class WindBreathProjectile extends BreathEntity {
   public WindBreathProjectile(EntityType<? extends WindBreathProjectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public WindBreathProjectile(Level level, LivingEntity entity) {
      this((EntityType<? extends WindBreathProjectile>)MiscEntityTypes.WIND_BREATH.get(), level);
      this.setOwner(entity);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.WIND_BREATH;
   }

   @Override
   public boolean hasBlockInteraction() {
      return true;
   }

   @Override
   protected boolean hitEntity(Entity entity, ProjectileHitResult customResult) {
      if (this.getOwner() != null) {
         SkillHelper.pushBackFromPos(ObjectSelectionHelper.getBlockPos(this.getOwner().getEyePosition()), entity, this.getOwner(), this.getSkill(), 0.2F, 0.1F);
      }

      return super.hitEntity(entity, customResult);
   }

   @Override
   protected boolean dealDamage(Entity target, float damage, float costMultiplier) {
      return !(target instanceof LivingEntity) ? false : super.dealDamage(target, damage, costMultiplier);
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
      if (state.is(BlockTags.FIRE)
         && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), pos.x(), pos.y(), pos.z())
            .isFalse()) {
         this.level().removeBlock(blockPos, false);
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
         double speed = rand.nextDouble() * 0.35 + 0.45;

         for (int i = 0; i < 20; i++) {
            double ox = rand.nextDouble() * 0.3 - 0.15;
            double oy = rand.nextDouble() * 0.3 - 0.15;
            double oz = rand.nextDouble() * 0.3 - 0.15;
            Vec3 randomVec = new Vec3(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5).normalize();
            Vec3 result = var22.scale(3.0).add(randomVec).normalize().scale(speed);
            owner.level()
               .addParticle(
                  (ParticleOptions)(rand.nextDouble() < 0.75 ? ParticleTypes.CLOUD : TensuraParticleUtils.getWhiteAura(0.75F, 2.0F, -0.05F, 20)),
                  x + ox,
                  y + oy,
                  z + oz,
                  result.x,
                  result.y,
                  result.z
               );
         }
      }
   }
}
