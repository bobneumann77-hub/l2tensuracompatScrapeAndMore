package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class InfiniteRegenerationSkill extends Skill {
   public static final ExtraSkillConfig.InfiniteRegeneration CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).InfiniteRegeneration;

   public InfiniteRegenerationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.ULTRASPEED_REGENERATION.get()) ? false : newEP > CONFIG.epAcquirement;
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
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(entity);
      }

      tag.putInt("activatedTimes", time + 1);
      this.onToggleOn(instance, entity);
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      MobEffectInstance regeneration = new MobEffectInstance(
         TensuraMobEffects.getReference(TensuraMobEffects.INSTANT_REGENERATION), 240, 1, false, false, false
      );
      TensuraMobEffect.addEffect(entity, regeneration, entity, this);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSTANT_REGENERATION));
   }
}
