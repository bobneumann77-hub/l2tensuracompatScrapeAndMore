package io.github.manasmods.tensura.effect;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import java.awt.Color;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class InsanityEffect extends TensuraMobEffect {
   public InsanityEffect() {
      super(MobEffectCategory.HARMFUL, new Color(255, 0, 0).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get())) {
         pAmplifier -= 2;
      }

      if (pAmplifier < 0) {
         return true;
      } else {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY));
         if (instance == null) {
            return true;
         } else {
            damageEntity(entity, instance, pAmplifier);
            if (!(entity instanceof Player player)) {
               return true;
            } else {
               if (player.isSleeping()) {
                  return true;
               }

               if (instance.getDuration() % 80 != 0) {
                  return true;
               }

               if (player.getRandom().nextInt(10) == 3) {
                  SoundEvent event = (SoundEvent)SoundEvents.AMBIENT_CAVE.value();
                  if ((player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS)) && player.getRandom().nextInt(20) == 1) {
                     event = (SoundEvent)SoundEvents.MUSIC_BIOME_DEEP_DARK.value();
                  }

                  player.playNotifySound(event, SoundSource.HOSTILE, 0.5F, 1.0F);
               }

               return true;
            }
         }
      }
   }

   public static void damageEntity(LivingEntity entity, @Nullable MobEffectInstance instance, int amplifier) {
      if (amplifier > 0) {
         if (entity.level() instanceof ServerLevel level) {
            if (!(entity instanceof Player player && player.isSleeping())) {
               float spiritualDamage = 2.0F + amplifier * 2;
               Entity source = instance != null && instance.tensura$hasSource() ? level.getEntity(instance.tensura$getSource()) : null;
               DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(level, TensuraDamageTypes.INSANITY, source);
               TensuraDamageHelper.directSpiritualHurt(entity, null, damageSource, spiritualDamage);
               if (entity.level().getMaxLocalRawBrightness(entity.blockPosition()) < 1 + amplifier * 3) {
                  entity.hurt(damageSource, amplifier);
               }
            }
         }
      }
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 40 == 0;
   }
}
