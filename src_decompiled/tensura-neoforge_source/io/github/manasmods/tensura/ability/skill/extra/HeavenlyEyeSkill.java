package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.util.AttributeHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class HeavenlyEyeSkill extends Skill {
   private static final ExtraSkillConfig.HeavenlyEye CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).HeavenlyEye;
   public static final ResourceLocation HEAVENLY_EYE = ResourceLocation.fromNamespaceAndPath("tensura", "heavenly_eye");

   public HeavenlyEyeSkill() {
      super(Skill.SkillType.EXTRA);
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

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(entity);
      }

      tag.putInt("activatedTimes", time + 1);
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPresenceSense(entity, CONFIG.presenceSense);
      AttributeInstance dodgeNegate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (dodgeNegate != null) {
         dodgeNegate.addOrReplacePermanentModifier(new AttributeModifier(HEAVENLY_EYE, 50.0, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removePresenceSense(entity, CONFIG.presenceSense);
      AttributeInstance negate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (negate != null) {
         negate.removeModifier(HEAVENLY_EYE);
      }
   }
}
