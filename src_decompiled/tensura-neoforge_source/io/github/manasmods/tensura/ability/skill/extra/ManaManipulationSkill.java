package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.util.AttributeHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class ManaManipulationSkill extends Skill {
   public static final ExtraSkillConfig.ManaManipulation CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).ManaManipulation;
   public static final ResourceLocation MANA_MANIPULATION = ResourceLocation.fromNamespaceAndPath("tensura", "mana_manipulation");

   public ManaManipulationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.MOLECULAR_MANIPULATION.get())) {
         return false;
      } else {
         return !SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.MAGIC_JAMMING.get()) ? false : newEP > CONFIG.epAcquirement;
      }
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPermanentAttribute(
         entity,
         TensuraAttributes.MAGIC_COST_MULTIPLIER,
         MANA_MANIPULATION,
         (instance.isMastered(entity) ? CONFIG.magicCostReductionMastered : CONFIG.magicCostReduction) * -1.0F,
         Operation.ADD_VALUE
      );
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeAttribute(entity, TensuraAttributes.MAGIC_COST_MULTIPLIER, MANA_MANIPULATION);
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity owner, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled()) {
         return true;
      }

      if (source.tensura$getMagiculeCost() > 0.0) {
         amount.set((Float)amount.get() * CONFIG.magiculeDamageMultiplier);
      }

      return true;
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

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      if (entity instanceof ServerPlayer player) {
         AttributeHelper.removeAnalysisAttributes(player, true, true, false);
      }
   }
}
