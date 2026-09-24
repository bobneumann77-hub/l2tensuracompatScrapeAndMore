package io.github.manasmods.tensura.ability.magic.spiritual.earth;

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
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EarthMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.Earth CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Earth;

   public EarthMagic() {
      super(Element.EARTH, SpiritualMagic.SpiritLevel.LESSER);
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
      return instance.isMastered(entity) ? CONFIG.magiculeCostMastered : CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            0.5F,
            25,
            MagicCircleVariant.EARTH,
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
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, 6.0);
            if (result.getType() == Type.BLOCK) {
               BlockPos pos = result.getBlockPos();
               BlockPos relative = pos.relative(result.getDirection());
               if (level.getBlockState(pos).canBeReplaced()) {
                  relative = pos;
               }

               if (level.getBlockState(relative).canBeReplaced()) {
                  if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                     return;
                  }

                  if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                     .grief(instance, level, entity, relative.getX(), relative.getY(), relative.getZ())
                     .isFalse()) {
                     instance.addMasteryPoint(entity);
                     BlockState earthBlock = this.biomeBlock(level, relative).defaultBlockState();
                     level.setBlock(relative, earthBlock, 11);
                     level.gameEvent(entity, GameEvent.BLOCK_PLACE, relative);
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
                        (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               }
            }
         }
      }
   }

   private Block biomeBlock(Level level, BlockPos pos) {
      Holder<Biome> biome = level.getBiome(pos);
      if (((Biome)biome.value()).toString().contains("desert")) {
         return Blocks.SAND;
      }

      if (!biome.is(BiomeTags.IS_BEACH) && !biome.is(BiomeTags.IS_OCEAN) && !biome.is(BiomeTags.IS_DEEP_OCEAN)) {
         if (((Biome)biome.value()).toString().contains("cave")) {
            return Blocks.STONE;
         } else if (biome.is(BiomeTags.IS_MOUNTAIN)) {
            return Blocks.STONE;
         } else if (biome.is(BiomeTags.IS_NETHER)) {
            return Blocks.NETHERRACK;
         } else {
            return biome.is(BiomeTags.IS_END) ? Blocks.END_STONE : Blocks.DIRT;
         }
      } else {
         return Blocks.SAND;
      }
   }
}
