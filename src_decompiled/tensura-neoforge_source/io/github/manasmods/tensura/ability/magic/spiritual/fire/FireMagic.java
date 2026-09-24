package io.github.manasmods.tensura.ability.magic.spiritual.fire;

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
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class FireMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.Fire CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Fire;

   public FireMagic() {
      super(Element.FLAME, SpiritualMagic.SpiritLevel.LESSER);
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
            MagicCircleVariant.FLAME,
            entity,
            instance.getOrCreateTag(),
            1.0F,
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
         Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, 0.2, false, true, true);
         if (target != null) {
            if (target instanceof LivingEntity living) {
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               living.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), 100));
               if (living instanceof Creeper creeper) {
                  creeper.ignite();
               }

               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null, living.getX(), living.getY(), living.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               return;
            }

            if (target instanceof MinecartTNT tnt) {
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               tnt.primeFuse();
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               return;
            }
         }

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
            if (result.getType() == Type.BLOCK) {
               BlockPos pos = result.getBlockPos();
               BlockState state = level.getBlockState(pos);
               if (state.getBlock() instanceof TntBlock) {
                  if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                     return;
                  }

                  if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                     .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                     .isFalse()) {
                     instance.addMasteryPoint(entity);
                     TntBlock.explode(level, pos);
                     level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
                     ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                        .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
                  }

                  entity.swing(InteractionHand.MAIN_HAND, true);
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               } else if (!CampfireBlock.canLight(state) && !CandleBlock.canLight(state) && !CandleCakeBlock.canLight(state)) {
                  BlockPos relative = pos.relative(result.getDirection());
                  if (BaseFireBlock.canBePlacedAt(level, relative, entity.getDirection())) {
                     if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                        return;
                     }

                     if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ())
                        .isFalse()) {
                        instance.addMasteryPoint(entity);
                        BlockState relativeState = BaseFireBlock.getState(level, relative);
                        level.setBlock(relative, relativeState, 11);
                        level.gameEvent(entity, GameEvent.BLOCK_PLACE, pos);
                        ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                           .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ());
                     }

                     entity.swing(InteractionHand.MAIN_HAND, true);
                     entity.level()
                        .playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                  }
               } else {
                  if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                     return;
                  }

                  if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                     .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                     .isFalse()) {
                     instance.addMasteryPoint(entity);
                     level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.LIT, Boolean.TRUE), 11);
                     level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
                     ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                        .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
                  }

                  entity.swing(InteractionHand.MAIN_HAND, true);
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               }
            }
         }
      }
   }
}
