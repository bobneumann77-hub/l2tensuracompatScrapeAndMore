package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.entity.magic.field.haki.HakiField;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class HeroHakiSkill extends Skill {
   private static final ExtraSkillConfig.HeroHaki CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).HeroHaki;
   private static final ResourceLocation HAKI = ResourceLocation.fromNamespaceAndPath("tensura", "hero_haki");

   public HeroHakiSkill() {
      super(Skill.SkillType.EXTRA);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, HAKI, HakiSkill.CONFIG.speedMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      if (existence.getAlignment().equals(Alignment.MAJIN)) {
         return false;
      } else {
         return !SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.HAKI.get()) ? false : newEP > CONFIG.epAcquirement;
      }
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public boolean shouldTriggerReleaseOnHeldInterrupt(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      return true;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return HakiSkill.CONFIG.magiculeCost;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         if (TensuraStorages.getExistenceFrom(entity).isTrueHero()) {
            SkillHelper.learnSkill(entity, ((SacredHakiSkill)ExtraSkills.SACRED_HAKI.get()).createLearningInstance(entity));
         }
      }
   }

   public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
      return instance.isMastered(entity) ? (HakiSkill.CONFIG.speedMultiplierMastered - 1.0) / (HakiSkill.CONFIG.speedMultiplier - 1.0) : 1.0;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      instance.getOrCreateTag().putInt("HakiID", 0);
      instance.markDirty();
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      return activateHeroHaki(instance, entity, mode, heldTicks, this);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (this.hasAttributeApplied(entity, Attributes.MOVEMENT_SPEED, HAKI)) {
         instance.setCoolDown(instance.isMastered(entity) ? HakiSkill.CONFIG.cooldownMastered : HakiSkill.CONFIG.cooldown, mode);
      }
   }

   public static boolean activateHeroHaki(ManasSkillInstance instance, LivingEntity entity, int mode, int heldTicks, TensuraSkill skill) {
      return HakiSkill.summonHaki(instance, entity, mode, heldTicks, skill, HakiField.HakiVariant.HERO, CONFIG.epDifferenceMultiplier);
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      HakiSkill.changeEPUsed(instance, entity, delta);
   }
}
