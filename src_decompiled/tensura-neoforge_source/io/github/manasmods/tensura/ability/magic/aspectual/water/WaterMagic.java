package io.github.manasmods.tensura.ability.magic.aspectual.water;

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
import java.util.ArrayDeque;
import java.util.HashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class WaterMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Water CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Water;

   public WaterMagic() {
      super(AspectualMagic.AspectualType.WATER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public int getModes(ManasSkillInstance instance) {
      return 4;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return switch (mode) {
            case 0 -> instance.isMastered(entity) ? 3 : -1;
            default -> 0;
            case 2 -> instance.isMastered(entity) ? 1 : 0;
            case 3 -> instance.isMastered(entity) ? 2 : 0;
         };
      } else {
         return switch (mode) {
            case 0 -> instance.isMastered(entity) ? 1 : -1;
            case 1 -> instance.isMastered(entity) ? 2 : 0;
            case 2 -> instance.isMastered(entity) ? 3 : 0;
            default -> 0;
         };
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "water.1x1";
         case 1 -> "water.3x3";
         case 2 -> "water.5x5";
         case 3 -> "water.10x10";
         default -> super.getModeId(instance, mode);
      };
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
         if (!io.github.manasmods.tensura.ability.magic.spiritual.water.WaterMagic.isWaterEvaporated(entity, level)) {
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
                  BlockPos pos = result.getBlockPos();
                  BlockState state = level.getBlockState(pos);
                  BlockPos center = pos.relative(result.getDirection());
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  if (result.getType() == Type.BLOCK) {
                     if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && !(Boolean)state.getValue(BlockStateProperties.WATERLOGGED)) {
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
                              center = pos;
                           }
                        } else if (io.github.manasmods.tensura.ability.magic.spiritual.water.WaterMagic.canFillInCauldron(state)) {
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
                              center = pos;
                           }
                        } else {
                           if (!level.getBlockState(center).canBeReplaced()) {
                              return;
                           }

                           if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                              .grief(instance, level, entity, center.getX(), center.getY(), center.getZ())
                              .isFalse()) {
                              level.setBlock(center, Blocks.WATER.defaultBlockState(), 11);
                              level.gameEvent(entity, GameEvent.FLUID_PLACE, center);
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
                                 .grief(instance, level, entity, center.getX(), center.getY(), center.getZ());
                           }
                        }

                        if (mode != 0) {
                           int maxRadius = switch (mode) {
                              case 2 -> 2;
                              case 3 -> 5;
                              default -> 1;
                           };
                           breadthWaterSpread(level, center, maxRadius, entity, instance, mode);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void breadthWaterSpread(Level level, BlockPos center, int maxRadius, LivingEntity entity, ManasSkillInstance instance, int mode) {
      ArrayDeque<BlockPos> queue = new ArrayDeque<>();
      HashMap<BlockPos, Integer> dist = new HashMap<>();
      queue.add(center);
      dist.put(center, 0);

      while (!queue.isEmpty()) {
         BlockPos current = queue.poll();
         int d = dist.getOrDefault(current, 0);
         if (d < maxRadius) {
            for (Direction dir : Plane.HORIZONTAL) {
               BlockPos next = current.relative(dir);
               if (!dist.containsKey(next)) {
                  BlockState nextState = level.getBlockState(next);
                  boolean isReplaceable = nextState.canBeReplaced();
                  boolean isWaterloggable = nextState.hasProperty(BlockStateProperties.WATERLOGGED)
                     && !(Boolean)nextState.getValue(BlockStateProperties.WATERLOGGED);
                  boolean isCauldronFillable = io.github.manasmods.tensura.ability.magic.spiritual.water.WaterMagic.canFillInCauldron(nextState);
                  if (isReplaceable || isWaterloggable || isCauldronFillable) {
                     if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                        return;
                     }

                     dist.put(next, d + 1);
                     if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, next.getX(), next.getY(), next.getZ())
                        .isFalse()) {
                        if (isReplaceable) {
                           level.setBlock(next, Blocks.WATER.defaultBlockState(), 11);
                        } else if (isWaterloggable) {
                           level.setBlock(next, (BlockState)nextState.setValue(BlockStateProperties.WATERLOGGED, true), 11);
                        } else {
                           level.setBlock(next, (BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), 11);
                        }

                        level.gameEvent(entity, GameEvent.FLUID_PLACE, next);
                        ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                           .grief(instance, level, entity, next.getX(), next.getY(), next.getZ());
                     }

                     if (isReplaceable || isWaterloggable) {
                        queue.add(next);
                     }
                  }
               }
            }
         }
      }
   }
}
