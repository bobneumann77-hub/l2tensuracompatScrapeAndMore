package io.github.manasmods.tensura.ability;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public class TensuraSkillInstance extends ManasSkillInstance {
   public TensuraSkillInstance(ManasSkill skill) {
      super(skill);
   }

   public TensuraSkillInstance clone() {
      TensuraSkillInstance clone = new TensuraSkillInstance(this.getSkill());
      clone.deserialize(this.toNBT());
      return clone;
   }

   public double getMagiculeCost(LivingEntity entity, int mode) {
      return ((TensuraSkill)this.getSkill()).getMagiculeCost(entity, this, mode);
   }

   public double getAuraCost(LivingEntity entity, int mode) {
      return ((TensuraSkill)this.getSkill()).getAuraCost(entity, this, mode);
   }

   public MutableComponent getDisplayName() {
      return ((TensuraSkill)this.getSkill()).getColoredName();
   }
}
