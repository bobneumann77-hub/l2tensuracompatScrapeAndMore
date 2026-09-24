package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.entity.magic.lightning.LightningBolt;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class LightningManipulationSkill extends Skill {
   public static final ExtraSkillConfig.LightningManipulation CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).LightningManipulation;

   public LightningManipulationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return TensuraStorages.getSpiritFrom(entity).getSpiritLevelId(Element.WIND) >= 1;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         if (!(EnergyHelper.getBaseMaxEP(entity) < CONFIG.dominationEpAcquirement)) {
            SkillHelper.learnSkill(entity, ((LightningDominationSkill)ExtraSkills.LIGHTNING_DOMINATION.get()).createLearningInstance(entity));
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.LIGHTNING_BOOST, CONFIG.manipulationBoost);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.LIGHTNING_BOOST, CONFIG.manipulationBoost);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      if (!level.isThundering()) {
         entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.time").withStyle(ChatFormatting.RED));
         entity.level()
            .playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
               TensuraSkill.ABILITY_SOUND,
               1.0F,
               1.0F
            );
      } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         instance.addMasteryPoint(entity);
         Entity target = ObjectSelectionHelper.getTargetingEntity(entity, 30.0, false, false);
         Vec3 pos;
         if (target != null) {
            pos = target.position();
         } else {
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, 30.0);
            pos = result.getLocation();
         }

         LightningBolt bolt = new LightningBolt(level, entity);
         bolt.setCause(entity instanceof ServerPlayer serverPlayer ? serverPlayer : null);
         bolt.setSkill(entity, instance, this, mode);
         bolt.setTensuraDamage(CONFIG.boltDamage);
         bolt.setAdditionalVisual(2);
         bolt.setRadius(CONFIG.boltRange);
         bolt.setSkill(instance);
         bolt.setPos(pos);
         level.addFreshEntity(bolt);
         entity.swing(InteractionHand.MAIN_HAND, true);
         entity.playSound((SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get());
      }
   }

   public static void learnLightningManipulation(ManasSkillInstance instance, LivingEntity entity) {
      if (!SkillUtils.hasSkillPermanently(entity, (ManasSkill)ExtraSkills.LIGHTNING_MANIPULATION.get())) {
         int skills = instance.is(TensuraSkillTags.LIGHTNING_SKILLS) ? 1 : 0;

         for (ManasSkillInstance skill : SkillAPI.getSkillsFrom(entity).getLearnedSkills()) {
            if (!skill.isTemporarySkill() && skill.isMastered(entity) && skill.is(TensuraSkillTags.LIGHTNING_SKILLS)) {
               skills++;
            }
         }

         if (!(skills < CONFIG.lightningSkillAcquirement)) {
            SkillHelper.learnSkill(entity, ((LightningManipulationSkill)ExtraSkills.LIGHTNING_MANIPULATION.get()).createLearningInstance(entity));
         }
      }
   }
}
