package io.github.manasmods.tensura.ability.magic.aspectual.barrier;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ReinforcedBarrierMagic extends AspectualMagic {
   private static final AspectualMagicConfig.ReinforcedBarrier CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).ReinforcedBarrier;

   public ReinforcedBarrierMagic() {
      super(AspectualMagic.AspectualType.BARRIER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (mode == 0) {
         return instance.isMastered(entity) ? (reverse ? 2 : 1) : -1;
      } else if (mode == 1) {
         return reverse ? 0 : (instance.isMastered(entity) ? 2 : 0);
      } else if (mode == 2) {
         return reverse ? (instance.isMastered(entity) ? 1 : 0) : 0;
      } else {
         return 0;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> "reinforced_barrier.magic";
         case 2 -> "reinforced_barrier.physic";
         default -> "reinforced_barrier.combined";
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return Math.max(CONFIG.minCost, EnergyHelper.getBaseMaxMagicule(entity) * CONFIG.magiculeMultiplierCost);
   }

   @Override
   public boolean canLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (!super.canLearnSkill(instance, entity)) {
         return false;
      }

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.BARRIER.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((BarrierMagic)AspectualMagics.BARRIER.get()).getChatDisplayName(false)}
                  )
                  .withStyle(ChatFormatting.RED),
               true
            );
         }

         return false;
      } else if (!SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.MAGIC_BARRIER.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((MagicBarrierMagic)AspectualMagics.MAGIC_BARRIER.get()).getChatDisplayName(false)}
                  )
                  .withStyle(ChatFormatting.RED),
               true
            );
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            entity.getBbWidth() * 2.0F,
            25,
            MagicCircleVariant.BARRIER,
            true,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            new Vec3(0.0, entity.getBbHeight() * 1.0F / 3.0F, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
         MagicCircle.castMagicCircle(
            "MagicCircleID2",
            entity.getBbWidth() * 2.0F,
            25,
            MagicCircleVariant.BARRIER,
            true,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            0.0F,
            new Vec3(0.0, entity.getBbHeight() * 2.0F / 3.0F, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_BARRIER))
            || entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.PHYSICAL_BARRIER))) {
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
         } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            switch (mode) {
               case 1: {
                  MobEffectInstance magicBarrier = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_BARRIER), CONFIG.barrierDurationMagic, 0, true, false, true
                  );
                  CompoundTag magicTag = magicBarrier.tensura$getOrCreateTag();
                  magicTag.putFloat("DamageThreshold", CONFIG.barrierThresholdMagic * entity.getHealth());
                  magicTag.putFloat("UnderReduction", CONFIG.belowReductionMagic * entity.getHealth());
                  magicTag.putFloat("AboveReduction", CONFIG.aboveReductionMagic * entity.getHealth());
                  entity.addEffect(magicBarrier, entity);
                  instance.setCoolDown(CONFIG.cooldownMagic, mode);
                  break;
               }
               case 2: {
                  MobEffectInstance physicalBarrier = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.PHYSICAL_BARRIER), CONFIG.barrierDurationPhysical, 0, true, false, true
                  );
                  CompoundTag physicalTag = physicalBarrier.tensura$getOrCreateTag();
                  physicalTag.putFloat("DamageThreshold", CONFIG.barrierThresholdPhysical * entity.getHealth());
                  physicalTag.putFloat("UnderReduction", CONFIG.belowReductionPhysical * entity.getHealth());
                  physicalTag.putFloat("AboveReduction", CONFIG.aboveReductionPhysical * entity.getHealth());
                  entity.addEffect(physicalBarrier, entity);
                  instance.setCoolDown(CONFIG.cooldownPhysical, mode);
                  break;
               }
               default: {
                  MobEffectInstance magicBarrier = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_BARRIER), CONFIG.barrierDuration, 0, true, false, true
                  );
                  CompoundTag magicTag = magicBarrier.tensura$getOrCreateTag();
                  magicTag.putFloat("DamageThreshold", CONFIG.barrierThreshold * entity.getHealth());
                  magicTag.putFloat("UnderReduction", CONFIG.belowReduction * entity.getHealth());
                  magicTag.putFloat("AboveReduction", CONFIG.aboveReduction * entity.getHealth());
                  entity.addEffect(magicBarrier, entity);
                  MobEffectInstance physicalBarrier = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.PHYSICAL_BARRIER), CONFIG.barrierDuration, 0, true, false, true
                  );
                  CompoundTag physicalTag = physicalBarrier.tensura$getOrCreateTag();
                  physicalTag.putFloat("DamageThreshold", CONFIG.barrierThreshold * entity.getHealth());
                  physicalTag.putFloat("UnderReduction", CONFIG.belowReduction * entity.getHealth());
                  physicalTag.putFloat("AboveReduction", CONFIG.aboveReduction * entity.getHealth());
                  entity.addEffect(physicalBarrier, entity);
                  instance.addMasteryPoint(entity);
               }
            }

            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      }
   }
}
