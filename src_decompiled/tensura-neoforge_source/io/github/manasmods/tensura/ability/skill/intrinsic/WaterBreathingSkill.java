package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class WaterBreathingSkill extends Skill {
   private static final IntrinsicSkillConfig.WaterBreathing CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).WaterBreathing;

   public WaterBreathingSkill() {
      super(Skill.SkillType.INTRINSIC);
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

      entity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 240, CONFIG.waterBreathLevel - 1, false, false, false));
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      MobEffectInstance effectInstance = entity.getEffect(MobEffects.WATER_BREATHING);
      if (effectInstance != null && effectInstance.getAmplifier() == CONFIG.waterBreathLevel - 1) {
         entity.removeEffect(MobEffects.WATER_BREATHING);
      }
   }
}
