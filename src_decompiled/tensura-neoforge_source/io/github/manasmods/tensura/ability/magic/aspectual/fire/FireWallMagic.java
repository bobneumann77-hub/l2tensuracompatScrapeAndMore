package io.github.manasmods.tensura.ability.magic.aspectual.fire;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.spike.FirePillarEntity;
import io.github.manasmods.tensura.entity.magic.spike.PillarEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class FireWallMagic extends AspectualMagic {
   private static final AspectualMagicConfig.FireWall CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).FireWall;

   public FireWallMagic() {
      super(AspectualMagic.AspectualType.FIRE);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
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
            0.75F,
            25,
            MagicCircleVariant.FLAME,
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
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, CONFIG.range);
            if (result.getType() == Type.BLOCK) {
               BlockPos clickPos = result.getBlockPos();
               if (clickPos.getY() > entity.getOnPos().getY() + 1) {
                  return;
               }

               this.placeWall(instance, entity, entity.level(), clickPos, CONFIG.height, CONFIG.width, 0);
            }
         }
      }
   }

   private void placeWall(ManasSkillInstance instance, LivingEntity entity, Level level, BlockPos clickPos, int height, int width, int depth) {
      Block block = level.getBlockState(clickPos).getBlock();
      if (block instanceof BushBlock) {
         clickPos = clickPos.below();
      }

      Direction direction = entity.getDirection();
      boolean success = false;

      for (int i = depth * -1; i < Math.max(depth, 1); i++) {
         BlockPos pos = clickPos.relative(direction, i);
         success = this.placePillars(level, pos, entity, instance, height);
         BlockPos blockPos = pos;

         for (int w = 0; w < width; w++) {
            blockPos = blockPos.relative(direction.getClockWise());
            success = this.placePillars(level, blockPos, entity, instance, height) || success;
         }

         BlockPos blockPosCounter = pos;

         for (int w = 0; w < width; w++) {
            blockPosCounter = blockPosCounter.relative(direction.getCounterClockWise());
            success = this.placePillars(level, blockPosCounter, entity, instance, height) || success;
         }
      }

      if (success) {
         instance.addMasteryPoint(entity);
         entity.swing(InteractionHand.MAIN_HAND, true);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }

   private boolean placePillars(Level level, BlockPos pos, LivingEntity entity, ManasSkillInstance instance, int height) {
      Vec3 spawnPos = Vec3.atCenterOf(pos.above()).add(0.0, -0.5, 0.0);
      List<PillarEntity> list = level.getEntitiesOfClass(
         PillarEntity.class,
         new AABB(spawnPos, spawnPos.add(0.0, height, 0.0)),
         pillarx -> pillarx.getOwner() == entity && pillarx.getSkill() != null && pillarx.getSkill().getSkill() == instance.getSkill()
      );
      if (!list.isEmpty()) {
         return false;
      }

      FirePillarEntity pillar = new FirePillarEntity(level, entity);
      pillar.setPos(spawnPos);
      pillar.setLife(instance.isMastered(entity) ? CONFIG.wallDurationMastered : CONFIG.wallDuration);
      pillar.setHealth(entity.getHealth());
      pillar.setExtendingTick(20);
      pillar.setHeight(height);
      pillar.setBurnTicks(100);
      pillar.setDamage(CONFIG.fireDamage);
      pillar.setKnockForce(CONFIG.knockback);
      pillar.setSecondaryDamage(CONFIG.magicDamage);
      pillar.setContactInterval(instance.isMastered(entity) ? CONFIG.damageIntervalMastered : CONFIG.damageInterval);
      pillar.setContactRange(0.75F);
      pillar.setContactDamage(CONFIG.fireDamage);
      pillar.setContactPush(CONFIG.knockback);
      pillar.setContactSecondaryDamage(CONFIG.magicDamage);
      if (instance.isMastered(entity)) {
         pillar.setKnockResistNegate(CONFIG.knockResistNegate);
      }

      pillar.setElementalAttack(true);
      pillar.setSkill(entity, instance, this, 0);
      entity.level().addFreshEntity(pillar);
      return true;
   }
}
