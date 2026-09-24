package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.entity.magic.spike.PillarEntity;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EarthManipulationSkill extends Skill {
   public static final ExtraSkillConfig.EarthManipulation CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).EarthManipulation;

   public EarthManipulationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return TensuraStorages.getSpiritFrom(entity).getSpiritLevelId(Element.EARTH) >= 1;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "earth_manipulation.wall";
         case 1 -> "earth_manipulation.break";
         case 2 -> "earth_manipulation.pit";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> CONFIG.magiculeCostBreak;
         case 2 -> CONFIG.magiculeCostPit;
         default -> CONFIG.magiculeCostWall;
      };
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         if (!(EnergyHelper.getBaseMaxEP(entity) < CONFIG.dominationEpAcquirement)) {
            SkillHelper.learnSkill(entity, ((EarthDominationSkill)ExtraSkills.EARTH_DOMINATION.get()).createLearningInstance(entity));
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.EARTH_BOOST, CONFIG.manipulationBoost);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.EARTH_BOOST, CONFIG.manipulationBoost);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, 5.0);
         switch (mode) {
            case 0:
               this.placeWall(instance, entity, level, result);
               break;
            case 1:
               this.breakWall(instance, entity, level, result);
               break;
            case 2:
               this.pit(instance, entity, level);
         }
      }
   }

   public void pit(ManasSkillInstance instance, LivingEntity entity, Level level) {
      if (!TensuraGameRules.canSkillGrief(level)) {
         entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.gamerule").withStyle(ChatFormatting.RED));
         level.playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
      } else {
         BlockPos pos = entity.getOnPos();
         int radius = CONFIG.pitRadius;
         boolean success = false;

         for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
               for (int y = -radius * 2; y <= 0; y++) {
                  BlockPos newPos = pos.relative(Axis.Y, y).relative(Axis.X, x).relative(Axis.Z, z);
                  success = this.breakBlock(level, newPos, entity, instance) || success;
               }
            }
         }

         if (success) {
            instance.addMasteryPoint(entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
            EffectStorage.setCameraShake(entity, radius + 3, 0.03F, 15);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(),
                  TensuraSkill.ABILITY_SOUND,
                  0.5F,
                  0.5F
               );
            TensuraParticleHelper.spawnServerParticles(
               level, TensuraParticleUtils.getColorlessWave(0.9F, radius * 2), entity.getX(), entity.getY() + 0.1F, entity.getZ()
            );
         }
      }
   }

   private void breakWall(ManasSkillInstance instance, LivingEntity entity, Level level, BlockHitResult result) {
      if (result.getType() == Type.BLOCK) {
         BlockPos pos = result.getBlockPos();
         if (!level.getBlockState(pos).is(TensuraBlockTags.EARTH_MANIPULATING)) {
            return;
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
            return;
         }

         int radius = CONFIG.breakRadius;
         boolean sucess = false;
         Axis playerAxis = result.getDirection().getAxis();
         Axis[] axes = Arrays.stream(Axis.values()).filter(a -> a != playerAxis).toArray(Axis[]::new);

         for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
               BlockPos newPos = pos.relative(axes[0], x).relative(axes[1], y);
               sucess = this.breakBlock(level, newPos, entity, instance) || sucess;
            }
         }

         if (sucess) {
            instance.addMasteryPoint(entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
            EffectStorage.setCameraShake(entity, 3.0, 0.005F, 5);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(),
                  TensuraSkill.ABILITY_SOUND,
                  0.5F,
                  0.5F
               );
            TensuraParticleHelper.spawnServerParticles(
               level, TensuraParticleUtils.getColorlessSonic(0.9F, radius * 2), pos.getX(), pos.getY() + 0.1F, pos.getZ()
            );
         }
      }
   }

   private boolean breakBlock(Level level, BlockPos pos, LivingEntity entity, ManasSkillInstance instance) {
      if (level.getBlockState(pos).is(TensuraBlockTags.EARTH_MANIPULATING)
         && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
            .isFalse()) {
         boolean success = level.destroyBlock(pos, true);
         ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker()).grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
         return success;
      } else {
         return false;
      }
   }

   private void placeWall(ManasSkillInstance instance, LivingEntity entity, Level level, BlockHitResult result) {
      if (result.getType() == Type.BLOCK) {
         BlockPos clickPos = result.getBlockPos();
         if (clickPos.getY() > entity.getOnPos().getY() + 1) {
            return;
         }

         Block block = level.getBlockState(clickPos).getBlock();
         if (block instanceof BushBlock) {
            clickPos = clickPos.below();
         }

         int side = CONFIG.wallRadius;
         Direction direction = entity.getDirection();
         boolean success = this.placePillars(level, clickPos, entity, instance);
         BlockPos blockPos = clickPos;

         for (int w = 0; w < side; w++) {
            blockPos = blockPos.relative(direction.getClockWise());
            success = this.placePillars(level, blockPos, entity, instance) || success;
         }

         BlockPos blockPosCounter = clickPos;

         for (int w = 0; w < side; w++) {
            blockPosCounter = blockPosCounter.relative(direction.getCounterClockWise());
            success = this.placePillars(level, blockPosCounter, entity, instance) || success;
         }

         if (success) {
            instance.addMasteryPoint(entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
         }
      }
   }

   private boolean placePillars(Level level, BlockPos pos, LivingEntity entity, ManasSkillInstance instance) {
      BlockState state = level.getBlockState(pos);
      if (!state.is(TensuraBlockTags.EARTH_MANIPULATING)) {
         return false;
      }

      float height = CONFIG.wallHeight;
      Vec3 spawnPos = Vec3.atCenterOf(pos.above()).add(0.0, -0.5, 0.0);
      List<PillarEntity> list = level.getEntitiesOfClass(
         PillarEntity.class,
         new AABB(spawnPos, spawnPos.add(0.0, height, 0.0)),
         pillarx -> pillarx.getOwner() == entity && pillarx.getSkill() != null && pillarx.getSkill().getSkill() == instance.getSkill()
      );
      if (!list.isEmpty()) {
         return false;
      }

      PillarEntity pillar = new PillarEntity(level, entity);
      pillar.setPos(spawnPos);
      pillar.setDamage(CONFIG.wallDamage);
      pillar.setLife(CONFIG.wallDuration);
      pillar.setBlockState(state);
      pillar.setExtendingTick(10);
      pillar.setHeight(height);
      pillar.setPushEntityUp(true);
      pillar.setSkill(entity, instance, this, 0);
      entity.level().addFreshEntity(pillar);
      EffectStorage.setCameraShake(pillar, 3.0, 0.01F, 10);
      TensuraParticleHelper.spawnServerParticles(
         level, new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.08, 0.08, 0.08, 0.1, false
      );
      level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      return true;
   }

   public static void learnEarthManipulation(ManasSkillInstance instance, LivingEntity entity) {
      if (!SkillUtils.hasSkillPermanently(entity, (ManasSkill)ExtraSkills.EARTH_MANIPULATION.get())) {
         int skills = instance.is(TensuraSkillTags.EARTH_SKILLS) ? 1 : 0;

         for (ManasSkillInstance skill : SkillAPI.getSkillsFrom(entity).getLearnedSkills()) {
            if (!skill.isTemporarySkill() && skill.isMastered(entity) && skill.is(TensuraSkillTags.EARTH_SKILLS)) {
               skills++;
            }
         }

         if (!(skills < CONFIG.earthSkillAcquirement)) {
            SkillHelper.learnSkill(entity, ((EarthManipulationSkill)ExtraSkills.EARTH_MANIPULATION.get()).createLearningInstance(entity));
         }
      }
   }
}
