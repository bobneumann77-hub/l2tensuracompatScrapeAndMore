package io.github.manasmods.tensura.ability.magic.aspectual.ice;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class FreezeMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Freeze CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Freeze;

   public FreezeMagic() {
      super(AspectualMagic.AspectualType.ICE);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? (instance.isMastered(entity) ? 1 : -1) : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return mode == 1 ? "freeze.frost_walk" : super.getModeId(instance, mode);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public void addHeldAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (mode != 1) {
         super.addHeldAttributeModifiers(instance, entity, mode);
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (mode != 0) {
         MagicCircle.castMagicCircle(
            entity.getBbWidth() * 3.0F,
            25,
            MagicCircleVariant.ICE,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      } else if (castTime > 1) {
         MagicCircle.castMagicCircle(
            0.5F,
            25,
            MagicCircleVariant.ICE,
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

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (mode == 0) {
         return super.onHeld(instance, entity, heldTicks, mode);
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      if (heldTicks % 10 == 0) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         this.applyFrostWalk((ServerLevel)entity.level(), entity, entity.position());
         entity.level()
            .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_ICE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      this.applyCastingVisual(instance, entity, heldTicks, mode);
      return true;
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         Level level = entity.level();
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
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, CONFIG.range);
            if (result.getType() != Type.ENTITY) {
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               entity.swing(InteractionHand.MAIN_HAND, true);
               BlockPos pos = result.getBlockPos();
               BlockState state = level.getBlockState(pos);
               BlockPos relative = pos.relative(result.getDirection());
               BlockState relativeState = level.getBlockState(relative);
               if (result.getType() == Type.MISS) {
                  if (relativeState.getFluidState().is(Fluids.WATER)) {
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
                  } else if (relativeState.getFluidState().is(Fluids.LAVA)) {
                     if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ())
                        .isFalse()) {
                        level.setBlock(relative, Blocks.OBSIDIAN.defaultBlockState(), 11);
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
                  } else if (relativeState.getFluidState().is(Fluids.FLOWING_LAVA)
                     && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ())
                        .isFalse()) {
                     level.setBlock(relative, Blocks.BASALT.defaultBlockState(), 11);
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
                  if (relativeState.getFluidState().is(Fluids.WATER)) {
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
                  } else if (relativeState.getFluidState().is(Fluids.LAVA)) {
                     if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ())
                        .isFalse()) {
                        level.setBlock(relative, Blocks.OBSIDIAN.defaultBlockState(), 11);
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
                  } else if (relativeState.getFluidState().is(Fluids.FLOWING_LAVA)) {
                     if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ())
                        .isFalse()) {
                        level.setBlock(relative, Blocks.BASALT.defaultBlockState(), 11);
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
                  } else if (state.is(Blocks.ICE)) {
                     if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                        .isFalse()) {
                        level.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 11);
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
                  } else if (state.is(Blocks.PACKED_ICE)
                     && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                        .isFalse()) {
                     level.setBlock(pos, Blocks.BLUE_ICE.defaultBlockState(), 11);
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
               }
            }
         }
      }
   }

   public void applyFrostWalk(ServerLevel serverLevel, Entity entity, Vec3 vec3) {
      int radius = CONFIG.frostWalkMode;
      BlockPos blockPos = BlockPos.containing(vec3).offset(0, -1, 0);
      BlockPredicate icePredicate = BlockPredicate.allOf(
         new BlockPredicate[]{
            BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR),
            BlockPredicate.matchesBlocks(new Block[]{Blocks.WATER}),
            BlockPredicate.matchesFluids(new net.minecraft.world.level.material.Fluid[]{Fluids.WATER}),
            BlockPredicate.unobstructed()
         }
      );
      BlockPredicate lavaPredicate = BlockPredicate.allOf(
         new BlockPredicate[]{
            BlockPredicate.matchesTag(new Vec3i(0, 1, 0), BlockTags.AIR),
            BlockPredicate.matchesBlocks(new Block[]{Blocks.LAVA}),
            BlockPredicate.matchesFluids(new net.minecraft.world.level.material.Fluid[]{Fluids.LAVA}),
            BlockPredicate.unobstructed()
         }
      );

      for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-radius, 0, -radius), blockPos.offset(radius, 0, radius))) {
         if (pos.distToCenterSqr(vec3.x(), pos.getY() + 0.5F, vec3.z()) < Mth.square(radius)) {
            if (icePredicate.test(serverLevel, pos) && serverLevel.setBlockAndUpdate(pos, Blocks.FROSTED_ICE.defaultBlockState())) {
               serverLevel.gameEvent(entity, GameEvent.BLOCK_PLACE, pos);
            } else if (lavaPredicate.test(serverLevel, pos) && serverLevel.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState())) {
               serverLevel.gameEvent(entity, GameEvent.BLOCK_PLACE, pos);
            }
         }
      }
   }
}
