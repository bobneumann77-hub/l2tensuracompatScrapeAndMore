package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class UltraspeedRegenerationSkill extends Skill {
   private static final ExtraSkillConfig.UltraspeedRegeneration CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).UltraspeedRegeneration;

   public UltraspeedRegenerationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)CommonSkills.SELF_REGENERATION.get())) {
         return false;
      } else {
         return entity instanceof ServerPlayer player
               && player.getStats().getValue(((StatType)TensuraStats.BOSS_KILLED.get()).get((EntityType)MonsterEntityTypes.CHARYBDIS.get())) > 0
            ? newEP > CONFIG.epAcquirement
            : false;
      }
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
         TensuraMobEffects.getReference(TensuraMobEffects.INSTANT_REGENERATION), 240, 0, false, false, false
      );
      TensuraMobEffect.addEffect(entity, regeneration, entity, this);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      MobEffectInstance effectInstance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSTANT_REGENERATION));
      if (effectInstance != null && effectInstance.getAmplifier() < 1) {
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSTANT_REGENERATION));
      }
   }
}
