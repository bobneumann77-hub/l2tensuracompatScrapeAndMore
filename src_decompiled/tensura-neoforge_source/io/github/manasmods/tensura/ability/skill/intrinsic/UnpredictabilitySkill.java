package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class UnpredictabilitySkill extends Skill {
   private static final IntrinsicSkillConfig.Unpredictability CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).Unpredictability;
   public static final ResourceLocation UNPREDICTABILITY = ResourceLocation.fromNamespaceAndPath("tensura", "unpredictability");

   public UnpredictabilitySkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      return existence.isTrueHero();
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      return existence.isTrueHero();
   }

   @Override
   public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() < 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance dodgeNegate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (dodgeNegate != null) {
         dodgeNegate.addOrReplacePermanentModifier(new AttributeModifier(UNPREDICTABILITY, CONFIG.negateDodge, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance skillInstance, LivingEntity entity) {
      AttributeInstance negate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (negate != null) {
         negate.removeModifier(UNPREDICTABILITY);
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
