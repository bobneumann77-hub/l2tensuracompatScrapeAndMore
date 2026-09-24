package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class ThoughtAccelerationSkill extends Skill {
   private static final ExtraSkillConfig.ThoughtAcceleration CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).ThoughtAcceleration;
   protected static final ResourceLocation ACCELERATION = ResourceLocation.fromNamespaceAndPath("tensura", "thought_acceleration");

   public ThoughtAccelerationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      onToggle(instance, entity, ACCELERATION, true);
      AttributeInstance chantSpeed = entity.getAttribute(TensuraAttributes.CHANT_SPEED);
      if (chantSpeed != null && !chantSpeed.hasModifier(ACCELERATION)) {
         chantSpeed.addOrReplacePermanentModifier(new AttributeModifier(ACCELERATION, CONFIG.chantSpeed, Operation.ADD_VALUE));
      }

      if (instance.isMastered(entity)) {
         AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
         if (invulnerability != null) {
            invulnerability.addOrReplacePermanentModifier(new AttributeModifier(ACCELERATION, CONFIG.dodgeInvulnerability, Operation.ADD_VALUE));
         }
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      onToggle(instance, entity, ACCELERATION, false);
      AttributeInstance chantSpeed = entity.getAttribute(TensuraAttributes.CHANT_SPEED);
      if (chantSpeed != null) {
         chantSpeed.removeModifier(ACCELERATION);
      }

      AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
      if (invulnerability != null) {
         invulnerability.removeModifier(ACCELERATION);
      }
   }

   public static void onToggle(ManasSkillInstance instance, LivingEntity entity, ResourceLocation id, boolean on) {
      if (on) {
         AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
         if (speed != null && !speed.hasModifier(id)) {
            speed.addOrReplacePermanentModifier(
               new AttributeModifier(id, instance.isMastered(entity) ? CONFIG.movementSpeedMastered : CONFIG.movementSpeed, Operation.ADD_VALUE)
            );
         }

         AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
         if (attackSpeed != null && !attackSpeed.hasModifier(id)) {
            attackSpeed.addOrReplacePermanentModifier(
               new AttributeModifier(id, instance.isMastered(entity) ? CONFIG.attackSpeedMastered : CONFIG.attackSpeed, Operation.ADD_VALUE)
            );
         }
      } else {
         AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
         if (speed != null) {
            speed.removeModifier(id);
         }

         AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
         if (attackSpeed != null) {
            attackSpeed.removeModifier(id);
         }
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
