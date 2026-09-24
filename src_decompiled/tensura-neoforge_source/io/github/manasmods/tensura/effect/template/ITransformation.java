package io.github.manasmods.tensura.effect.template;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface ITransformation {
   default boolean failedToActivate(LivingEntity entity, @Nullable Holder<MobEffect> effect) {
      if (effect != null && this.isAlreadyTransformed(entity, effect)) {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.skill.transformed").withStyle(ChatFormatting.RED), true);
         }

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
         return true;
      } else {
         MobEffectInstance interference = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE));
         if (interference != null && interference.getAmplifier() >= 1) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.magic_interference").withStyle(ChatFormatting.RED), true);
            }

            if (effect != null && entity.hasEffect(effect)) {
               entity.removeEffect(effect);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   default boolean isAlreadyTransformed(LivingEntity entity, Holder<MobEffect> effect) {
      Predicate<MobEffectInstance> predicate = mobEffect -> mobEffect.is(effect) ? false : mobEffect.getEffect().is(TensuraTags.MobEffects.TRANSFORMATION);
      return entity.getActiveEffects().stream().anyMatch(predicate);
   }

   default void applyDebuff(LivingEntity entity) {
      entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 12000, 1, false, false));
      entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 12000, 1, true, false, true));
      entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 12000, 0, true, false, true));
   }
}
