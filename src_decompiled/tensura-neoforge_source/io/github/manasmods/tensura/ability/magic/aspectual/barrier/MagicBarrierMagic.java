package io.github.manasmods.tensura.ability.magic.aspectual.barrier;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
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

public class MagicBarrierMagic extends AspectualMagic {
   private static final AspectualMagicConfig.MagicBarrier CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).MagicBarrier;

   public MagicBarrierMagic() {
      super(AspectualMagic.AspectualType.BARRIER);
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
      return Math.max(CONFIG.minCost, EnergyHelper.getBaseMaxMagicule(entity) * CONFIG.magiculeMultiplierCost);
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
            new Vec3(0.0, entity.getBbHeight() / 2.0F, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_BARRIER))) {
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
            MobEffectInstance barrier = new MobEffectInstance(
               TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_BARRIER),
               instance.isMastered(entity) ? CONFIG.barrierDurationMastered : CONFIG.barrierDuration,
               0,
               true,
               false,
               true
            );
            CompoundTag tag = barrier.tensura$getOrCreateTag();
            tag.putFloat("DamageThreshold", (instance.isMastered(entity) ? CONFIG.barrierThresholdMastered : CONFIG.barrierThreshold) * entity.getHealth());
            tag.putFloat("UnderReduction", (instance.isMastered(entity) ? CONFIG.belowReductionMastered : CONFIG.belowReduction) * entity.getHealth());
            tag.putFloat("AboveReduction", (instance.isMastered(entity) ? CONFIG.aboveReductionMastered : CONFIG.aboveReduction) * entity.getHealth());
            entity.addEffect(barrier, entity);
            instance.addMasteryPoint(entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      }
   }
}
