package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class ScaleArmorSkill extends Skill {
   private static final IntrinsicSkillConfig.ScaleArmor CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).ScaleArmor;
   protected static final ResourceLocation SCALE_ARMOR = ResourceLocation.fromNamespaceAndPath("tensura", "scale_armor");

   public ScaleArmorSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance swim = entity.getAttribute(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER);
      if (swim != null) {
         swim.addOrReplacePermanentModifier(new AttributeModifier(SCALE_ARMOR, CONFIG.swimMultiplier - 1, Operation.ADD_VALUE));
      }

      AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
      if (armor != null) {
         armor.addOrReplacePermanentModifier(new AttributeModifier(SCALE_ARMOR, CONFIG.armorPoint, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance swim = entity.getAttribute(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER);
      if (swim != null) {
         swim.removeModifier(SCALE_ARMOR);
      }

      AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
      if (armor != null) {
         armor.removeModifier(SCALE_ARMOR);
      }
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
}
