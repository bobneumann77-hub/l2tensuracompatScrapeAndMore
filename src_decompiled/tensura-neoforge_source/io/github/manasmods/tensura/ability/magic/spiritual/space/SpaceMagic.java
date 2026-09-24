package io.github.manasmods.tensura.ability.magic.spiritual.space;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.block.SolidSpaceBlock;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class SpaceMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.Space CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Space;

   public SpaceMagic() {
      super(Element.SPACE, SpiritualMagic.SpiritLevel.LESSER);
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
            MagicCircleVariant.SPACE,
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
      } else if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      } else if (instance.isMastered(entity) && this.isOnAir(entity)) {
         this.solidSpace(instance, entity, mode);
         return true;
      } else {
         return super.onHeld(instance, entity, heldTicks, mode);
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      int castingTime = this.getCastingTime(instance, entity);
      if (castingTime != 0 || !this.isOnAir(entity)) {
         if (heldTicks >= this.getCastingTime(instance, entity)) {
            this.solidSpace(instance, entity, mode);
         }
      }
   }

   private void solidSpace(ManasSkillInstance instance, LivingEntity entity, int mode) {
      Level level = entity.level();
      if (!TensuraGameRules.canSkillGrief(level)) {
         entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.gamerule").withStyle(ChatFormatting.RED));
         level.playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
      } else if (this.isOnAir(entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            BlockPos belowPos = new BlockPos((int)entity.getX(), entity.getBlockY() - 1, (int)entity.getZ());
            if (!level.getBlockState(belowPos).is((Block)TensuraBlocks.SOLID_SPACE.get())) {
               if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                  .grief(instance, level, entity, belowPos.getX(), belowPos.getY(), belowPos.getZ())
                  .isFalse()) {
                  instance.addMasteryPoint(entity);
                  BlockState lightAir = ((Block)TensuraBlocks.SOLID_SPACE.get()).defaultBlockState();
                  level.setBlock(belowPos, lightAir, 11);
                  level.gameEvent(entity, GameEvent.BLOCK_PLACE, belowPos);
                  level.scheduleTick(belowPos, (Block)TensuraBlocks.SOLID_SPACE.get(), CONFIG.duration);
                  ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                     .grief(instance, level, entity, belowPos.getX(), belowPos.getY(), belowPos.getZ());
               }

               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }
         }
      } else {
         BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, CONFIG.range);
         if (result.getType() != Type.ENTITY) {
            BlockPos pos = result.getBlockPos();
            BlockPos relative = pos.relative(result.getDirection());
            if (level.getBlockState(relative).isAir() || level.getBlockState(relative).getFluidState().is(Fluids.WATER)) {
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                  .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ())
                  .isFalse()) {
                  instance.addMasteryPoint(entity);
                  BlockState lightAir = ((Block)TensuraBlocks.SOLID_SPACE.get()).defaultBlockState();
                  if (level.getBlockState(relative).getFluidState().isSourceOfType(Fluids.WATER)) {
                     lightAir.setValue(SolidSpaceBlock.WATERLOGGED, true);
                  }

                  level.setBlock(relative, lightAir, 11);
                  level.gameEvent(entity, GameEvent.BLOCK_PLACE, relative);
                  if (!instance.isMastered(entity)) {
                     level.scheduleTick(relative, (Block)TensuraBlocks.SOLID_SPACE.get(), CONFIG.duration);
                  }

                  ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                     .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ());
               }

               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }
         }
      }
   }

   private boolean isOnAir(LivingEntity entity) {
      if (!entity.isShiftKeyDown()) {
         return false;
      }

      BlockPos belowPos = new BlockPos((int)entity.getX(), entity.getBlockY() - 1, (int)entity.getZ());
      boolean canFly = entity instanceof Player player && player.getAbilities().flying;
      return canFly
         ? false
         : entity.level().getBlockState(belowPos).isAir() || entity.level().getBlockState(belowPos).is((Block)TensuraBlocks.SOLID_SPACE.get());
   }
}
