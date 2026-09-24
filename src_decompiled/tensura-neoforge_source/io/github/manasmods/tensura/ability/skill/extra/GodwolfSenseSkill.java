package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class GodwolfSenseSkill extends Skill {
   public static final ExtraSkillConfig.GodwolfSense CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).GodwolfSense;

   public GodwolfSenseSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(entity).getRace();
      return !race.isEmpty() && race.get().is(TensuraRaceTags.BEASTFOLK) ? newEP > CONFIG.epAcquirement : false;
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() < 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (EnergyHelper.isOutOfEnergy(entity, instance, 0)) {
         entity.sendSystemMessage(
            Component.translatable("tensura.skill.lack_magicule.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
         );
         instance.setToggled(false);
         instance.onToggleOff(entity);
      } else {
         CompoundTag tag = instance.getOrCreateTag();
         int time = tag.getInt("activatedTimes");
         if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
            instance.addMasteryPoint(entity);
         }

         tag.putInt("activatedTimes", time + 1);
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPresenceSense(entity, CONFIG.presenceSense);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removePresenceSense(entity, CONFIG.presenceSense);
   }
}
