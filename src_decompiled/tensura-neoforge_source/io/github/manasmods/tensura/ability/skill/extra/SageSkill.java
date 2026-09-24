package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class SageSkill extends Skill {
   private static final ExtraSkillConfig.Sage CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).Sage;
   protected static final ResourceLocation SAGE = ResourceLocation.fromNamespaceAndPath("tensura", "sage");

   public SageSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      if (entity instanceof ServerPlayer player) {
         int magics = player.getStats().getValue(Stats.CUSTOM.get(TensuraStats.MAGIC_MASTERED));
         int battlewills = player.getStats().getValue(Stats.CUSTOM.get(TensuraStats.BATTLEWILL_MASTERED));
         return magics + battlewills >= CONFIG.abilityMastered;
      } else {
         return false;
      }
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance learning = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
      if (learning != null) {
         learning.addOrReplacePermanentModifier(new AttributeModifier(SAGE, CONFIG.learningPoint, Operation.ADD_VALUE));
      }

      AttributeInstance mastery = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (mastery != null) {
         mastery.addOrReplacePermanentModifier(new AttributeModifier(SAGE, CONFIG.masteryPoint, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
      if (projectile != null) {
         projectile.removeModifier(SAGE);
      }

      AttributeInstance negate = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (negate != null) {
         negate.removeModifier(SAGE);
      }
   }
}
