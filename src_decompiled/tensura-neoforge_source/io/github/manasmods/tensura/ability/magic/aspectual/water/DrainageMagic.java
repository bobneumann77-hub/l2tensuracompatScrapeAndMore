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
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class DrainageMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Drainage CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Drainage;

   public DrainageMagic() {
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

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
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
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      int castTime = this.getCastingTime(instance, entity);
      if (heldTicks >= castTime) {
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
            return false;
         }

         if (heldTicks == castTime || heldTicks % 5 == 0) {
            int drained = 0;
            int radius = instance.isMastered(entity) ? CONFIG.radiusMastered : CONFIG.radius;
            MutableBlockPos pos = new MutableBlockPos();

            for (int x = -radius; x < radius; x++) {
               for (int y = -radius; y < radius; y++) {
                  for (int z = -radius; z < radius; z++) {
                     pos.set(entity.getX() + x, entity.getY() + y, entity.getZ() + z);
                     if (!(entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > radius * radius)) {
                        BlockState state = level.getBlockState(pos);
                        if (state.is(Blocks.WATER) || state.is(Blocks.BUBBLE_COLUMN)) {
                           if (!this.drainWater(instance, entity, mode, level, pos, state, DrainageMagic.DrainType.WATER)) {
                              return false;
                           }

                           drained++;
                        }

                        if (!state.is(Blocks.SEAGRASS) && !state.is(Blocks.TALL_SEAGRASS) && !state.is(Blocks.KELP) && !state.is(Blocks.KELP_PLANT)) {
                           if (state.hasProperty(BlockStateProperties.WATERLOGGED) && (Boolean)state.getValue(BlockStateProperties.WATERLOGGED)) {
                              if (!this.drainWater(instance, entity, mode, level, pos, state, DrainageMagic.DrainType.WATERLOGGED)) {
                                 return false;
                              }

                              drained++;
                           }
                        } else {
                           if (!this.drainWater(instance, entity, mode, level, pos, state, DrainageMagic.DrainType.WATER_PLANTS)) {
                              return false;
                           }

                           drained++;
                        }
                     }
                  }
               }
            }

            if (drained > 0) {
               if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0) {
                  instance.addMasteryPoint(entity);
               }

               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            }
         }
      }

      this.applyCastingVisual(instance, entity, heldTicks, mode);
      return true;
   }

   private boolean drainWater(
      ManasSkillInstance instance, LivingEntity entity, int mode, Level level, BlockPos pos, BlockState state, DrainageMagic.DrainType drainType
   ) {
      if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
         .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
         .isFalse()) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         if (drainType != DrainageMagic.DrainType.WATERLOGGED
            && state.getFluidState().isSource()
            && entity.getAttributeValue(TensuraAttributes.WATER_CAPACITY) > 0.0) {
            IAbility ability = TensuraStorages.getAbilityFrom(entity);
            ability.setWaterPoint(ability.getWaterPoint() + 1.0);
            ability.markDirty();
         }

         switch (drainType) {
            case WATER:
               level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
               break;
            case WATER_PLANTS:
               level.destroyBlock(pos, true);
               level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
               break;
            case WATERLOGGED:
               level.setBlockAndUpdate(pos, (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, false));
         }

         if (entity.getRandom().nextFloat() < 0.05F) {
            ((ServerLevel)level).sendParticles(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.04, 0.06, 0.04, 0.05);
         }

         ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker()).grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
      }

      return true;
   }

   enum DrainType {
      WATER,
      WATER_PLANTS,
      WATERLOGGED;
   }
}
