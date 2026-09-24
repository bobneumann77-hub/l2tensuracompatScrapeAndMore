package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.ElementalTransformSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class FlameTransformSkill extends ElementalTransformSkill {
   public FlameTransformSkill() {
      super(Skill.SkillType.INTRINSIC, Element.FLAME);
   }

   @Override
   protected void applyVisualEffect(LivingEntity entity) {
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BREATH_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), 1.0);
      double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getFlameOrangeAura(0.8F, (float)size, -0.3F), 3, 0.01);
   }

   @Override
   protected void onDamageEntity(ManasSkillInstance instance, LivingEntity target, DamageSource source) {
      target.setRemainingFireTicks(100);
      this.placeFire(instance, target, target.getOnPos());
      target.hurt(source, CONFIG.damage);
   }

   private void placeFire(ManasSkillInstance instance, LivingEntity entity, BlockPos onPos) {
      Level level = entity.level();
      if (!TensuraGameRules.canSkillGrief(level)) {
         BlockPos pos = onPos.above();
         BlockState blockState = level.getBlockState(pos);
         if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
            .isFalse()) {
            if (blockState.canBeReplaced() && blockState.getFluidState().isEmpty()) {
               BlockState blockStateDown = level.getBlockState(onPos);
               if (blockStateDown.isFaceSturdy(level, onPos, Direction.UP)) {
                  level.removeBlock(pos, true);
                  level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
               }
            }

            if (BaseFireBlock.canBePlacedAt(level, pos, Direction.UP)) {
               level.setBlockAndUpdate(pos, BaseFireBlock.getState(level, pos));
               level.scheduleTick(pos, blockState.getBlock(), 20);
               level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            }

            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
         }
      }
   }
}
