package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MajestySkill extends Skill {
   private static final ExtraSkillConfig.Majesty CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).Majesty;

   public MajestySkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return entity instanceof ServerPlayer player ? player.getStats().getValue(Stats.CUSTOM.get(Stats.RAID_WIN)) >= CONFIG.raidNumber : false;
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

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isMastered(entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         int time = tag.getInt("activatedTimes");
         if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
            instance.addMasteryPoint(entity);
         }

         tag.putInt("activatedTimes", time + 1);
      }

      entity.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 200, CONFIG.heroLevel - 1, false, false));
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      entity.removeEffect(MobEffects.HERO_OF_THE_VILLAGE);
   }
}
