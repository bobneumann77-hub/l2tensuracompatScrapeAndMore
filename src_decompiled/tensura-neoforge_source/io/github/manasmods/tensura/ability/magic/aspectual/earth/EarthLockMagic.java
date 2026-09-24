package io.github.manasmods.tensura.ability.magic.aspectual.earth;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EarthLockMagic extends AspectualMagic {
   public static final AspectualMagicConfig.EarthLock CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).EarthLock;

   public EarthLockMagic() {
      super(AspectualMagic.AspectualType.EARTH);
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
      return mode == 1 ? "earth_lock.self" : "earth_lock.earth";
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (mode == 0) {
         BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(
            entity.level(), entity, Fluid.NONE, net.minecraft.world.level.ClipContext.Block.OUTLINE, CONFIG.range
         );
         MagicCircle.castTargetedMagicCircle(
            CONFIG.radius,
            25,
            result.getLocation(),
            MagicCircleVariant.EARTH,
            false,
            entity,
            instance.getOrCreateTag(),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      } else if (castTime > 1) {
         MagicCircle.castMagicCircle(
            entity.getBbWidth() * 2.0F,
            25,
            MagicCircleVariant.EARTH,
            true,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            new Vec3(0.0, entity.getBbHeight() / 2.0F, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            if (mode == 0) {
               if (!TensuraGameRules.canSkillGrief(entity.level())) {
                  entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.gamerule").withStyle(ChatFormatting.RED));
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
                  return;
               }

               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(
                  entity.level(), entity, Fluid.NONE, net.minecraft.world.level.ClipContext.Block.OUTLINE, CONFIG.range
               );
               this.lockBlocks(instance, entity, entity.level(), result, mode);
            } else {
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.addEffect(
                  new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.EARTH_LOCK), CONFIG.knockbackResistanceDuration, 0, false, false, false
                  )
               );
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }
         }
      }
   }

   private void lockBlocks(ManasSkillInstance instance, LivingEntity entity, Level level, BlockHitResult result, int mode) {
      if (result.getType() == Type.BLOCK) {
         BlockPos targetedPos = result.getBlockPos();
         Block block = level.getBlockState(targetedPos).getBlock();
         if (block instanceof BushBlock) {
            targetedPos = targetedPos.below();
         }

         Map<Block, Block> map = this.getSolidBlock();
         int radius = CONFIG.radius;
         boolean success = false;
         MutableBlockPos blockPos = new MutableBlockPos();

         for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
               for (int z = -radius; z <= radius; z++) {
                  blockPos.set(targetedPos.getX() + x, targetedPos.getY() + y, targetedPos.getZ() + z);
                  BlockState blockState = level.getBlockState(blockPos);
                  if (map.containsKey(blockState.getBlock())
                     && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, blockPos.getX(), blockPos.getY(), blockPos.getZ())
                        .isFalse()) {
                     level.destroyBlock(blockPos, false);
                     level.setBlock(blockPos, map.get(blockState.getBlock()).defaultBlockState(), 11);
                     level.gameEvent(entity, GameEvent.BLOCK_PLACE, blockPos);
                     success = true;
                     ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                        .grief(instance, level, entity, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                  }
               }
            }
         }

         if (!success) {
            entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            return;
         }

         instance.addMasteryPoint(entity);
         entity.swing(InteractionHand.MAIN_HAND, true);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         entity.level()
            .playSound(
               null,
               targetedPos.getX(),
               targetedPos.getY(),
               targetedPos.getZ(),
               (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(),
               TensuraSkill.ABILITY_SOUND,
               1.0F,
               1.0F
            );
      }
   }

   private Map<Block, Block> getSolidBlock() {
      return Map.of(
         (Block)TensuraBlocks.LOOSE_DIRT.get(),
         Blocks.DIRT,
         (Block)TensuraBlocks.LOOSE_GRAVEL.get(),
         Blocks.GRAVEL,
         (Block)TensuraBlocks.QUICKMUD.get(),
         Blocks.MUD,
         (Block)TensuraBlocks.QUICKSAND.get(),
         Blocks.SAND,
         (Block)TensuraBlocks.RED_QUICKSAND.get(),
         Blocks.RED_SAND,
         (Block)TensuraBlocks.SARASA_QUICKSAND.get(),
         (Block)TensuraBlocks.SARASA_SAND.get()
      );
   }
}
