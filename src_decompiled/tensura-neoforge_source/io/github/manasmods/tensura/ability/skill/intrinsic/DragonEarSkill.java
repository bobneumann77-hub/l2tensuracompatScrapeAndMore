package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.SenseSoundwaveSkill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class DragonEarSkill extends Skill {
   private static final IntrinsicSkillConfig.DragonEar CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).DragonEar;

   public DragonEarSkill() {
      super(Skill.SkillType.INTRINSIC);
      this.addHeldAttributeModifier(TensuraAttributes.PRESENCE_SENSE_RADIUS, SenseSoundwaveSkill.SOUND_SENSE, CONFIG.bonusRadius, Operation.ADD_VALUE);
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, 0)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      return true;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPermanentAttributeIfHigher(
         entity, TensuraAttributes.PRESENCE_SENSE_RADIUS, SenseSoundwaveSkill.SOUND_SENSE, CONFIG.bonusRadius, Operation.ADD_VALUE
      );
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeAttributeIfCorrect(entity, TensuraAttributes.PRESENCE_SENSE_RADIUS, SenseSoundwaveSkill.SOUND_SENSE, CONFIG.bonusRadius);
   }

   public void removeAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (!instance.isToggled()) {
         super.removeAttributeModifiers(instance, entity, mode);
      }
   }
}
