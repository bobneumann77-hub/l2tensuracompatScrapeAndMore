package io.github.manasmods.tensura.ability.magic.spiritual.light;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.block.LightAirBlock;
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

public class LightMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.Light CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Light;

   public LightMagic() {
      super(Element.LIGHT, SpiritualMagic.SpiritLevel.LESSER);
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
            MagicCircleVariant.LIGHT,
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
               BlockPos relative = pos.relative(result.getDirection());
               boolean placeableOnBlock = level.getBlockState(relative).isAir() && result.getType() == Type.BLOCK;
               if (placeableOnBlock || level.getBlockState(relative).getFluidState().is(Fluids.WATER)) {
                  if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                     return;
                  }

                  if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                     .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ())
                     .isFalse()) {
                     instance.addMasteryPoint(entity);
                     BlockState lightAir = ((Block)TensuraBlocks.LIGHT_AIR.get()).defaultBlockState();
                     if (level.getBlockState(relative).getFluidState().isSourceOfType(Fluids.WATER)) {
                        lightAir.setValue(LightAirBlock.WATERLOGGED, true);
                     }

                     level.setBlock(relative, lightAir, 11);
                     level.gameEvent(entity, GameEvent.BLOCK_PLACE, relative);
                     if (!instance.isMastered(entity)) {
                        level.scheduleTick(relative, (Block)TensuraBlocks.LIGHT_AIR.get(), 60);
                     }

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
                        (SoundEvent)TensuraSoundEvents.CAST_LIGHT.get(),
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
