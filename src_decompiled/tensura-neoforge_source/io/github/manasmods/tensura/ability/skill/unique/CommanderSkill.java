package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.common.ThoughtCommunicationSkill;
import io.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class CommanderSkill extends Skill {
   public static final UniqueSkillConfig.Commander CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Commander;
   protected static final ResourceLocation COMMANDER = ResourceLocation.fromNamespaceAndPath("tensura", "commander");

   public CommanderSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "commander.movement_communication";
         case 1 -> "commander.targeting_communication";
         case 2 -> "commander.thought_domination";
         default -> super.getModeId(instance, mode);
      };
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      List<LivingEntity> list = entity.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            entity.getBoundingBox().inflate(CONFIG.inspireRadius),
            living -> !living.is(entity) && living.isAlive() && living.isAlliedTo(entity)
         );
      if (!list.isEmpty()) {
         int level = instance.isMastered(entity) ? 1 : 0;

         for (LivingEntity target : list) {
            if (Objects.equals(SubordinateHelper.getSubordinateOwnerUUID(target), entity.getUUID())) {
               target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.INSPIRATION), 240, level, false, false, false), entity);
            }
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      switch (mode) {
         case 0:
            ThoughtCommunicationSkill.movementBehaviour(instance, entity);
            break;
         case 1:
            ThoughtCommunicationSkill.targetingBehaviour(instance, entity);
            break;
         case 2:
            CharmSkill.charm(instance, entity, mode);
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, COMMANDER, true);
      AttributeInstance degrade = entity.getAttribute(TensuraAttributes.RESISTANCE_DEGRADATION);
      if (degrade != null) {
         degrade.addOrReplacePermanentModifier(new AttributeModifier(COMMANDER, 1.0, Operation.ADD_VALUE));
      }

      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.addOrReplacePermanentModifier(new AttributeModifier(COMMANDER, CONFIG.meleeDodge, Operation.ADD_VALUE));
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.addOrReplacePermanentModifier(new AttributeModifier(COMMANDER, CONFIG.projectileDodge, Operation.ADD_VALUE));
      }

      AttributeInstance negate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (negate != null) {
         negate.addOrReplacePermanentModifier(new AttributeModifier(COMMANDER, CONFIG.dodgeNegation, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, COMMANDER, false);
      AttributeInstance degrade = entity.getAttribute(TensuraAttributes.RESISTANCE_DEGRADATION);
      if (degrade != null) {
         degrade.removeModifier(COMMANDER);
      }

      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.removeModifier(COMMANDER);
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.removeModifier(COMMANDER);
      }

      AttributeInstance negate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (negate != null) {
         negate.removeModifier(COMMANDER);
      }
   }
}
