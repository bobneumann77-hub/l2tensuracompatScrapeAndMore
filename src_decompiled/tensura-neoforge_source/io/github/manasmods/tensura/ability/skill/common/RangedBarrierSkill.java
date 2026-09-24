package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.MultilayerBarrierSkill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierPart;
import io.github.manasmods.tensura.entity.magic.barrier.RangedBarrierEntity;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RangedBarrierSkill extends Skill {
   private static final CommonSkillConfig.RangedBarrier CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).RangedBarrier;

   public RangedBarrierSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      if (entity instanceof ServerPlayer player) {
         return player.getStats().getValue(((StatType)TensuraStats.BOSS_KILLED.get()).get((EntityType)MonsterEntityTypes.IFRIT.get()))
               < CONFIG.ifritAcquirement
            ? false
            : newEP >= CONFIG.epAcquirement;
      } else {
         return false;
      }
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return switch (mode) {
            case 0 -> instance.isMastered(entity) ? 2 : 1;
            case 1 -> 0;
            case 2 -> 1;
            default -> -1;
         };
      } else {
         return switch (mode) {
            case 0 -> 1;
            case 1 -> instance.isMastered(entity) ? 2 : 0;
            default -> 0;
         };
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "ranged_barrier.5";
         case 1 -> "ranged_barrier.10";
         case 2 -> "ranged_barrier.20";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost * this.getRadiusMode(mode);
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         SkillHelper.learnSkill(entity, ((MultilayerBarrierSkill)ExtraSkills.MULTILAYER_BARRIER.get()).createLearningInstance(entity));
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      Class<? extends Entity> targetClass = entity.isShiftKeyDown() ? Entity.class : LivingEntity.class;
      Entity target = ObjectSelectionHelper.getTargetingEntity(targetClass, entity, CONFIG.barrierRange, 0.1, false, false, false);
      Vec3 pos;
      if (target != null) {
         pos = target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0);
         if (entity.isShiftKeyDown()
            && target instanceof BarrierPart part
            && part.getBarrier() instanceof RangedBarrierEntity barrier
            && barrier.getOwner() == entity
            && barrier.getLife() - barrier.getAge() > 20) {
            barrier.setRemoveIn(20);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                  TensuraSkill.ABILITY_SOUND,
                  0.5F,
                  0.5F
               );
            entity.level()
               .playSound(
                  null,
                  barrier.getX(),
                  barrier.getY(),
                  barrier.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            return;
         }
      } else {
         BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, Block.OUTLINE, 30.0);
         pos = result.getLocation().add(0.0, 1.0, 0.0);
      }

      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         RangedBarrierEntity barrier = new RangedBarrierEntity(level, entity);
         barrier.setOwner(entity);
         barrier.setHealth(entity.getMaxHealth() / 2.0F);
         barrier.setLife(instance.isMastered(entity) ? CONFIG.barrierDuration * 2 : CONFIG.barrierDuration);
         barrier.setApCost(this.getAuraCost(entity, instance, mode));
         barrier.setMpCost(this.getMagiculeCost(entity, instance, mode));
         barrier.setSkill(entity, instance, this, mode);
         float size = this.getRadiusMode(mode);
         barrier.setPos(pos.add(0.0, size / 2.0F, 0.0));
         barrier.setSize(size);
         barrier.setVisualSize(size);
         entity.level().addFreshEntity(barrier);
         if (target != null) {
            instance.addMasteryPoint(entity);
         }

         instance.setCoolDown(mode + 1, mode);
         entity.swing(InteractionHand.MAIN_HAND, true);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
            );
      }
   }

   public int getRadiusMode(int mode) {
      return switch (mode) {
         case 0 -> CONFIG.barrierRadius;
         case 1 -> CONFIG.barrierRadiusSecond;
         case 2 -> CONFIG.barrierRadiusThird;
         default -> 0;
      };
   }
}
