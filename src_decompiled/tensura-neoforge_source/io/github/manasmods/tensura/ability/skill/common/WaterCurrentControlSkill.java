package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class WaterCurrentControlSkill extends Skill {
   private static final CommonSkillConfig.WaterCurrentControl CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).WaterCurrentControl;
   protected static final ResourceLocation WCC = ResourceLocation.fromNamespaceAndPath("tensura", "water_current_control");

   public WaterCurrentControlSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() < 0.0;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         SkillHelper.learnSkill(entity, ((HydraulicPropulsionSkill)CommonSkills.HYDRAULIC_PROPULSION.get()).createLearningInstance(entity));
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance swim = entity.getAttribute(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER);
      if (swim != null) {
         AttributeModifier attributemodifier = new AttributeModifier(WCC, CONFIG.swimBoost - 1.0, Operation.ADD_VALUE);
         if (!swim.hasModifier(WCC)) {
            swim.addOrReplacePermanentModifier(attributemodifier);
         }
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance swim = entity.getAttribute(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER);
      if (swim != null) {
         swim.removeModifier(WCC);
      }
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return !instance.isToggled() ? false : entity.isInWaterOrBubble();
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(entity);
      }

      tag.putInt("activatedTimes", time + 1);
   }
}
