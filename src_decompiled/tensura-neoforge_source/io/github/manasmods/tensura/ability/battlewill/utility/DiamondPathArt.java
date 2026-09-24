package io.github.manasmods.tensura.ability.battlewill.utility;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DiamondPathArt extends Battlewill {
   private static final BattlewillConfig.DiamondPath CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).DiamondPath;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (EnergyHelper.isOutOfEnergy(entity, instance, 0)) {
         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.lack_aura.toggled_off", new Object[]{instance.getSkill().getName()}).withStyle(ChatFormatting.RED), false
            );
         }

         instance.setToggled(false);
      } else {
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.DIAMOND_PATH), 240, 0, false, false, false));
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.DIAMOND_PATH))) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            instance.addMasteryPoint(entity);
            int duration = instance.isMastered(entity) ? CONFIG.effectTimeMastered : CONFIG.effectTime;
            int level = instance.isMastered(entity) ? 1 : 0;
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.DIAMOND_PATH), duration, level, false, false, false));
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  0.5F,
                  1.0F
               );
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      this.onTick(instance, entity);
      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
         );
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.DIAMOND_PATH));
      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.DEFENCE_DEACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
         );
   }
}
