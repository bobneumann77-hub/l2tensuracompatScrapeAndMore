package io.github.manasmods.tensura.ability.skill.resist;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.config.ability.skill.ResistanceConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class PainResistanceSkill extends ResistSkill {
   @Override
   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity owner, DamageSource source, Changeable<Float> amount) {
      if (instance.getMastery() >= 0.0) {
         return true;
      }

      if (((Float)amount.get()).floatValue() > this.getDamageAmountForLearning()) {
         this.addLearnPoint(instance, owner, 0, owner.getAttributeValue(TensuraAttributes.ABILITY_LEARNING_GAIN));
      } else {
         CompoundTag tag = instance.getOrCreateTag();
         tag.putDouble("damagePoint", tag.getDouble("damagePoint") + ((Float)amount.get()).floatValue());
         double requirement = this.getDamageAmountForLearning() * ((ResistanceConfig)ConfigRegistry.getConfig(ResistanceConfig.class)).damagePointMultiplier;
         if (tag.getDouble("damagePoint") >= requirement) {
            this.addLearnPoint(instance, owner, 0, owner.getAttributeValue(TensuraAttributes.ABILITY_LEARNING_GAIN));
         }

         instance.markDirty();
      }

      return true;
   }

   @Nullable
   @Override
   protected ManasSkill getNullificationForm() {
      return (ManasSkill)ResistanceSkills.PAIN_NULLIFICATION.get();
   }
}
