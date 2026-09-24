package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.util.AttributeHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class SenseSoundwaveSkill extends Skill {
   public static final ResourceLocation SOUND_SENSE = ResourceLocation.fromNamespaceAndPath("tensura", "sound_sense");

   public SenseSoundwaveSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return 0.0;
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0) && !instance.isTemporarySkill()) {
         if (SkillUtils.hasSkillFully(entity, (ManasSkill)ExtraSkills.SENSE_HEAT_SOURCE.get())) {
            if (SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.MAGIC_SENSE.get())) {
               SkillHelper.learnSkill(entity, ((UniversalPerceptionSkill)ExtraSkills.UNIVERSAL_PERCEPTION.get()).createLearningInstance(entity));
            }
         }
      }
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
      AttributeHelper.addPermanentAttributeIfHigher(entity, TensuraAttributes.PRESENCE_SENSE_RADIUS, SOUND_SENSE, 0.1, Operation.ADD_VALUE);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeAttributeIfCorrect(entity, TensuraAttributes.PRESENCE_SENSE_RADIUS, SOUND_SENSE, 0.1);
   }
}
