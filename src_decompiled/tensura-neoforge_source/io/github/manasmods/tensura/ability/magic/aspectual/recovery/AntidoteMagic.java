package io.github.manasmods.tensura.ability.magic.aspectual.recovery;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class AntidoteMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Antidote CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Antidote;

   public AntidoteMagic() {
      super(AspectualMagic.AspectualType.RECOVERY);
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
            0.5F,
            25,
            MagicCircleVariant.RECOVERY,
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
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false);
         if (target != null) {
            if (this.canActivateAntidote(instance, entity, target)) {
               instance.addMasteryPoint(entity);
               entity.swing(InteractionHand.MAIN_HAND, true);
               target.removeEffect(MobEffects.CONFUSION);
               target.removeEffect(MobEffects.POISON);
               target.removeEffect(MobEffects.WEAKNESS);
               if (instance.isMastered(entity)) {
                  MobEffectInstance effect = target.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON));
                  if (effect != null) {
                     int level = effect.getAmplifier() - CONFIG.fatalLevel;
                     int duration = effect.getDuration() - CONFIG.fatalDuration;
                     target.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON));
                     if (level >= 0 && duration > 0) {
                        target.addEffect(new MobEffectInstance(effect.getEffect(), duration, level, effect.isAmbient(), effect.isVisible(), effect.showIcon()));
                     }
                  }
               }

               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getLightGreenWave(0.9F, target.getBbWidth() * 3.0F, -0.5F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.33,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getBlueWave(0.9F, target.getBbWidth() * 3.0F, -0.5F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.66,
                  target.getZ()
               );
            } else {
               entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
            }
         } else if (this.canActivateAntidote(instance, entity, entity)) {
            instance.addMasteryPoint(entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.removeEffect(MobEffects.CONFUSION);
            entity.removeEffect(MobEffects.POISON);
            entity.removeEffect(MobEffects.WEAKNESS);
            if (instance.isMastered(entity)) {
               MobEffectInstance effect = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON));
               if (effect != null) {
                  int level = effect.getAmplifier() - CONFIG.fatalLevel;
                  int duration = effect.getDuration() - CONFIG.fatalDuration;
                  entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON));
                  if (level >= 0 && duration > 0) {
                     entity.addEffect(new MobEffectInstance(effect.getEffect(), duration, level, effect.isAmbient(), effect.isVisible(), effect.showIcon()));
                  }
               }
            }

            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getLightGreenWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.33,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getBlueWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.66,
               entity.getZ()
            );
         } else {
            entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
         }
      }
   }

   private boolean canActivateAntidote(ManasSkillInstance instance, LivingEntity entity, LivingEntity target) {
      if (target.hasEffect(MobEffects.CONFUSION)) {
         return true;
      } else if (target.hasEffect(MobEffects.POISON)) {
         return true;
      } else {
         return target.hasEffect(MobEffects.WEAKNESS)
            ? true
            : instance.isMastered(entity) && target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON));
      }
   }
}
