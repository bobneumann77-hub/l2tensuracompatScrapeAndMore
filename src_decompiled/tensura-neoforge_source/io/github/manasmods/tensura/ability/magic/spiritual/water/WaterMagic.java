package io.github.manasmods.tensura.ability.magic.spiritual.water;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class WaterMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.Water CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Water;

   public WaterMagic() {
      super(Element.WATER, SpiritualMagic.SpiritLevel.LESSER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            0.5F,
            25,
            MagicCircleVariant.WATER,
            entity,
            instance.getOrCreateTag(),
            0.75F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         Level level = entity.level();
         if (!isWaterEvaporated(entity, level)) {
            if (!TensuraGameRules.canSkillGrief(level)) {
               entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.gamerule").withStyle(ChatFormatting.RED));
               level.playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            } else {
               instance.addMasteryPoint(entity);
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, CONFIG.range);
               if (result.getType() != Type.ENTITY) {
                  if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                     return;
                  }

                  BlockPos pos = result.getBlockPos();
                  BlockState state = level.getBlockState(pos);
                  BlockPos relative = pos.relative(result.getDirection());
                  BlockState relativeState = level.getBlockState(relative);
                  if (result.getType() == Type.MISS && relativeState.getFluidState().is(Fluids.WATER)) {
                     if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ())
                        .isFalse()) {
                        level.setBlock(relative, Blocks.ICE.defaultBlockState(), 11);
                        level.gameEvent(entity, GameEvent.BLOCK_PLACE, relative);
                        instance.addMasteryPoint(entity);
                        level.playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.CAST_ICE.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                        ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                           .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ());
                     }
                  } else if (result.getType() == Type.BLOCK) {
                     if (state.is(Blocks.ICE)) {
                        if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                           .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                           .isFalse()) {
                           level.setBlock(pos, Blocks.WATER.defaultBlockState(), 11);
                           level.gameEvent(entity, GameEvent.BLOCK_PLACE, pos);
                           instance.addMasteryPoint(entity);
                           level.playSound(
                              null,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)TensuraSoundEvents.CAST_ICE.get(),
                              TensuraSkill.ABILITY_SOUND,
                              1.0F,
                              1.0F
                           );
                           ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                              .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
                        }
                     } else if (state.hasProperty(BlockStateProperties.WATERLOGGED) && !(Boolean)state.getValue(BlockStateProperties.WATERLOGGED)) {
                        if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                           .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                           .isFalse()) {
                           level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, true), 11);
                           level.gameEvent(entity, GameEvent.FLUID_PLACE, pos);
                           instance.addMasteryPoint(entity);
                           level.playSound(
                              null,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)TensuraSoundEvents.CAST_WATER.get(),
                              TensuraSkill.ABILITY_SOUND,
                              1.0F,
                              1.0F
                           );
                           ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                              .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
                        }
                     } else if (canFillInCauldron(state)) {
                        if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                           .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                           .isFalse()) {
                           level.setBlock(pos, (BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), 11);
                           level.gameEvent(entity, GameEvent.FLUID_PLACE, pos);
                           instance.addMasteryPoint(entity);
                           level.playSound(
                              null,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)TensuraSoundEvents.CAST_WATER.get(),
                              TensuraSkill.ABILITY_SOUND,
                              1.0F,
                              1.0F
                           );
                           ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                              .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
                        }
                     } else if (relativeState.getFluidState().is(Fluids.WATER)) {
                        if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                           .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ())
                           .isFalse()) {
                           level.setBlock(relative, Blocks.ICE.defaultBlockState(), 11);
                           level.gameEvent(entity, GameEvent.BLOCK_PLACE, relative);
                           instance.addMasteryPoint(entity);
                           level.playSound(
                              null,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)TensuraSoundEvents.CAST_ICE.get(),
                              TensuraSkill.ABILITY_SOUND,
                              1.0F,
                              1.0F
                           );
                           ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                              .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ());
                        }
                     } else if (relativeState.canBeReplaced()
                        && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                           .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ())
                           .isFalse()) {
                        level.setBlock(relative, Blocks.WATER.defaultBlockState(), 11);
                        level.gameEvent(entity, GameEvent.FLUID_PLACE, relative);
                        instance.addMasteryPoint(entity);
                        level.playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.CAST_WATER.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                        ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                           .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ());
                     }
                  }

                  entity.swing(InteractionHand.MAIN_HAND, true);
               }
            }
         }
      }
   }

   public static boolean isWaterEvaporated(LivingEntity entity, Level level) {
      if (!level.dimensionType().ultraWarm()) {
         return false;
      }

      level.playSound(
         null,
         entity.getOnPos().above(),
         SoundEvents.FIRE_EXTINGUISH,
         TensuraSkill.ABILITY_SOUND,
         0.5F,
         2.6F + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.8F
      );
      TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.POOF);
      TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.POOF, 2.0);
      return true;
   }

   public static boolean canFillInCauldron(BlockState state) {
      if (state.is(Blocks.CAULDRON)) {
         return true;
      } else {
         return !state.is(Blocks.WATER_CAULDRON) ? false : (Integer)state.getValue(LayeredCauldronBlock.LEVEL) < 3;
      }
   }
}
