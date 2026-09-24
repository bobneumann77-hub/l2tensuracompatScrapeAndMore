package io.github.manasmods.tensura.ability.magic.aspectual.ice;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.spike.IcePillarEntity;
import io.github.manasmods.tensura.entity.magic.spike.PillarEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class IceWallMagic extends AspectualMagic {
   private static final AspectualMagicConfig.IceWall CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).IceWall;

   public IceWallMagic() {
      super(AspectualMagic.AspectualType.ICE);
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
            MagicCircleVariant.ICE,
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
            if (instance.isMastered(entity)) {
               Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
               if (target != null && target.isAlive() && target.onGround()) {
                  this.placeWall(instance, entity, entity.level(), target.blockPosition(), 4, 4, 4);
                  return;
               }
            }

            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, CONFIG.range);
            if (result.getType() == Type.BLOCK) {
               BlockPos clickPos = result.getBlockPos();
               if (clickPos.getY() > entity.getOnPos().getY() + 1) {
                  return;
               }

               this.placeWall(instance, entity, entity.level(), clickPos, 8, 4, 0);
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
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
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

      IcePillarEntity pillar = new IcePillarEntity(level, entity);
      pillar.setPos(spawnPos);
      pillar.setLife(CONFIG.wallDuration);
      BlockState state = entity.getRandom().nextBoolean() ? Blocks.BLUE_ICE.defaultBlockState() : Blocks.PACKED_ICE.defaultBlockState();
      pillar.setBlockState(state);
      pillar.setExtendingTick(10);
      pillar.setHeight(height);
      pillar.setAoeDamage(CONFIG.aoeDamage);
      pillar.setAoeRange(CONFIG.aoeRadius);
      if (instance.isMastered(entity)) {
         pillar.setContactSecondaryDamage(CONFIG.contactDamageMastered);
         MobEffectInstance chill = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.CHILL),
            CONFIG.contactChillDurationMastered,
            CONFIG.contactChillLevelMastered - 1,
            true,
            false,
            true
         );
         pillar.setMobEffect(chill);
         MobEffectInstance frost = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.FROST), CONFIG.breakFrostDuration, CONFIG.breakFrostLevel - 1, true, false, true
         );
         pillar.setBreakEffect(frost);
      } else {
         pillar.setContactSecondaryDamage(CONFIG.contactDamage);
         MobEffectInstance chill = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.CHILL), CONFIG.contactChillDuration, CONFIG.contactChillLevel - 1, true, false, true
         );
         pillar.setMobEffect(chill);
      }

      pillar.setElementalAttack(true);
      pillar.setSkill(entity, instance, this, 0);
      entity.level().addFreshEntity(pillar);
      EffectStorage.setCameraShake(pillar, 3.0, 0.01F, 10);
      TensuraParticleHelper.spawnServerParticles(
         level, new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.08, 0.08, 0.08, 0.1, false
      );
      level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), (SoundEvent)TensuraSoundEvents.CAST_ICE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      return true;
   }
}
