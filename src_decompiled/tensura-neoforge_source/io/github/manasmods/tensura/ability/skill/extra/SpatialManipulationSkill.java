package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class SpatialManipulationSkill extends Skill {
   public static final ResourceLocation SPATIAL_MANIPULATION = ResourceLocation.fromNamespaceAndPath("tensura", "spatial_manipulation");

   public SpatialManipulationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return TensuraStorages.getSpiritFrom(entity).getSpiritLevelId(Element.SPACE) >= 1;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public List<Integer> getModeLearningList(ManasSkillInstance instance) {
      return List.of(0);
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return "spatial_domination.warp_shot";
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return SpatialDominationSkill.CONFIG.magiculeCostWarpShot;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         if (!(EnergyHelper.getBaseMaxEP(entity) < SpatialDominationSkill.CONFIG.dominationEpAcquirement)) {
            SkillHelper.learnSkill(entity, ((SpatialDominationSkill)ExtraSkills.SPATIAL_DOMINATION.get()).createLearningInstance(entity));
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.SPACE_BOOST, SpatialDominationSkill.CONFIG.manipulationBoost);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.SPACE_BOOST, SpatialDominationSkill.CONFIG.manipulationBoost);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         if (!this.learnMode(instance, entity, mode)) {
            AttributeInstance warpShot = entity.getAttribute(TensuraAttributes.WARP_SHOT);
            if (warpShot != null) {
               AttributeModifier modifier = warpShot.getModifier(SPATIAL_MANIPULATION);
               if (modifier != null) {
                  AttributeHelper.removeAttributeIfCorrect(
                     entity, TensuraAttributes.WARP_SHOT, SPATIAL_MANIPULATION, SpatialDominationSkill.CONFIG.warpShotManipulation
                  );
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               } else if (AttributeHelper.addPermanentAttributeIfHigher(
                  entity, TensuraAttributes.WARP_SHOT, SPATIAL_MANIPULATION, SpatialDominationSkill.CONFIG.warpShotManipulation, Operation.ADD_VALUE
               )) {
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               }
            }
         }
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      AttributeInstance attribute = entity.getAttribute(TensuraAttributes.WARP_SHOT);
      if (attribute != null) {
         if (attribute.getModifier(SPATIAL_MANIPULATION) != null) {
            attribute.removeModifier(SPATIAL_MANIPULATION);
         }
      }
   }

   public static void learnSpaceManipulation(ManasSkillInstance instance, LivingEntity entity) {
      if (!SkillUtils.hasSkillPermanently(entity, (ManasSkill)ExtraSkills.SPATIAL_MANIPULATION.get())) {
         int skills = instance.is(TensuraSkillTags.SPACE_SKILLS) ? 1 : 0;

         for (ManasSkillInstance skill : SkillAPI.getSkillsFrom(entity).getLearnedSkills()) {
            if (!skill.isTemporarySkill() && skill.isMastered(entity) && skill.is(TensuraSkillTags.SPACE_SKILLS)) {
               skills++;
            }
         }

         if (!(skills < SpatialDominationSkill.CONFIG.spaceSkillAcquirement)) {
            SkillHelper.learnSkill(entity, ((SpatialManipulationSkill)ExtraSkills.SPATIAL_MANIPULATION.get()).createLearningInstance(entity));
         }
      }
   }
}
