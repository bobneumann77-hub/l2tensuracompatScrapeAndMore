package io.github.manasmods.tensura.ability.magic.aspectual.earth;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.spike.PillarEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
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

public class LiquidizeMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Liquidize CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Liquidize;

   public LiquidizeMagic() {
      super(AspectualMagic.AspectualType.EARTH);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      PillarEntity target = ObjectSelectionHelper.getTargetingEntity(PillarEntity.class, entity, CONFIG.range, 0.2, false);
      Vec3 vec3;
      if (target == null) {
         vec3 = ObjectSelectionHelper.getPlayerPOVHitResult(
               entity.level(), entity, Fluid.NONE, net.minecraft.world.level.ClipContext.Block.OUTLINE, CONFIG.range
            )
            .getLocation();
      } else {
         vec3 = target.position();
      }

      MagicCircle.castTargetedMagicCircle(
         CONFIG.radius,
         25,
         vec3,
         MagicCircleVariant.EARTH,
         false,
         entity,
         instance.getOrCreateTag(),
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            PillarEntity target = ObjectSelectionHelper.getTargetingEntity(PillarEntity.class, entity, CONFIG.range, 0.2, false);
            if (target != null && target.isAlive() && target.getType().equals(MiscEntityTypes.EARTH_PILLAR.get())) {
               for (PillarEntity pillar : entity.level()
                  .getEntitiesOfClass(
                     PillarEntity.class,
                     target.getBoundingBox().inflate(CONFIG.radius),
                     pillarx -> pillarx.getType().equals(MiscEntityTypes.EARTH_PILLAR.get()) && pillarx.getLife() - pillarx.getAge() > 20
                  )) {
                  pillar.setRemoveIn(pillar.getExtendingTick());
               }

               instance.addMasteryPoint(entity);
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     0.5F
                  );
               entity.level()
                  .playSound(
                     null,
                     target.getX(),
                     target.getY(),
                     target.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     0.5F
                  );
            } else if (!TensuraGameRules.canSkillGrief(entity.level())) {
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
            } else {
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(
                  entity.level(), entity, Fluid.NONE, net.minecraft.world.level.ClipContext.Block.OUTLINE, CONFIG.range
               );
               this.liquidizeBlocks(instance, entity, entity.level(), result, mode);
            }
         }
      }
   }

   private void liquidizeBlocks(ManasSkillInstance instance, LivingEntity entity, Level level, BlockHitResult result, int mode) {
      if (result.getType() == Type.BLOCK) {
         BlockPos targetedPos = result.getBlockPos();
         Block block = level.getBlockState(targetedPos).getBlock();
         if (block instanceof BushBlock) {
            targetedPos = targetedPos.below();
         }

         Map<Block, Block> map = this.getLiquidizedBlocks();
         int radius = CONFIG.radius;
         boolean success = false;
         MutableBlockPos blockPos = new MutableBlockPos();

         for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
               for (int z = -radius; z <= radius; z++) {
                  blockPos.set(targetedPos.getX() + x, targetedPos.getY() + y, targetedPos.getZ() + z);
                  BlockState blockState = level.getBlockState(blockPos.immutable());
                  if (map.containsKey(blockState.getBlock())
                     && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, blockPos.getX(), blockPos.getY(), blockPos.getZ())
                        .isFalse()) {
                     level.destroyBlock(blockPos.immutable(), false);
                     level.setBlock(blockPos.immutable(), map.get(blockState.getBlock()).defaultBlockState(), 11);
                     level.gameEvent(entity, GameEvent.BLOCK_PLACE, blockPos.immutable());
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

   private Map<Block, Block> getLiquidizedBlocks() {
      return Map.ofEntries(
         Map.entry(Blocks.DIRT, (Block)TensuraBlocks.LOOSE_DIRT.get()),
         Map.entry(Blocks.GRAVEL, (Block)TensuraBlocks.LOOSE_GRAVEL.get()),
         Map.entry(Blocks.GRASS_BLOCK, (Block)TensuraBlocks.LOOSE_DIRT.get()),
         Map.entry(Blocks.DIRT_PATH, (Block)TensuraBlocks.LOOSE_DIRT.get()),
         Map.entry(Blocks.COARSE_DIRT, (Block)TensuraBlocks.LOOSE_DIRT.get()),
         Map.entry(Blocks.FARMLAND, (Block)TensuraBlocks.LOOSE_DIRT.get()),
         Map.entry(Blocks.MYCELIUM, (Block)TensuraBlocks.LOOSE_DIRT.get()),
         Map.entry(Blocks.PODZOL, (Block)TensuraBlocks.LOOSE_DIRT.get()),
         Map.entry(Blocks.ROOTED_DIRT, (Block)TensuraBlocks.LOOSE_DIRT.get()),
         Map.entry(Blocks.MUD, (Block)TensuraBlocks.QUICKMUD.get()),
         Map.entry(Blocks.MUDDY_MANGROVE_ROOTS, (Block)TensuraBlocks.QUICKMUD.get()),
         Map.entry(Blocks.SAND, (Block)TensuraBlocks.QUICKSAND.get()),
         Map.entry(Blocks.RED_SAND, (Block)TensuraBlocks.RED_QUICKSAND.get()),
         Map.entry((Block)TensuraBlocks.SARASA_SAND.get(), (Block)TensuraBlocks.SARASA_QUICKSAND.get())
      );
   }
}
