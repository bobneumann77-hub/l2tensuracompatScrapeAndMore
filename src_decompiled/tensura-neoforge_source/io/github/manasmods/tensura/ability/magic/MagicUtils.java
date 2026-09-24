package io.github.manasmods.tensura.ability.magic;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class MagicUtils {
   public static boolean hasChantAnnulment(LivingEntity entity) {
      if (SkillUtils.isSkillToggled(entity, (ManasSkill)ExtraSkills.CHANT_ANNULMENT.get())) {
         return true;
      } else if (SkillUtils.isSkillToggled(entity, (ManasSkill)UniqueSkills.ANALYST.get())) {
         return true;
      } else {
         return SkillUtils.isSkillToggled(entity, (ManasSkill)UniqueSkills.GREAT_SAGE.get())
            ? true
            : SkillUtils.isSkillToggled(entity, (ManasSkill)UniqueSkills.SEEKER.get());
      }
   }

   public static int getChantTime(LivingEntity entity, int castTime) {
      AttributeInstance instance = entity.getAttribute(TensuraAttributes.CHANT_SPEED);
      return instance != null && instance.getValue() != 0.0 ? Math.max((int)(castTime / instance.getValue()), 1) : Math.max(castTime, 1);
   }
}
