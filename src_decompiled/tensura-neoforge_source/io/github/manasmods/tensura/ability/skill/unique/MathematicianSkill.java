package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.attribute.TensuraGlobalAttributeIds;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.AttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class MathematicianSkill extends Skill {
   private static final UniqueSkillConfig.Mathematician CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Mathematician;
   protected static final ResourceLocation MATHEMATICIAN = ResourceLocation.fromNamespaceAndPath("tensura", "mathematician");

   public MathematicianSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity instanceof ServerPlayer player) {
         if (player.isShiftKeyDown()) {
            ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
            switch (data.getAnalysisMode()) {
               case 1:
                  data.setAnalysisMode(2);
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.analytical.analyzing_mode.block").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
                  );
                  break;
               case 2:
                  data.setAnalysisMode(0);
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.analytical.analyzing_mode.both").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
                  );
                  break;
               default:
                  data.setAnalysisMode(1);
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.analytical.analyzing_mode.entity").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
                  );
            }

            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            data.markDirty();
         } else {
            CompoundTag tag = instance.getOrCreateTag();
            AttributeInstance level = player.getAttribute(TensuraAttributes.ANALYSIS_LEVEL);
            if (level != null && level.hasModifier(TensuraGlobalAttributeIds.ANALYSIS)) {
               AttributeHelper.removeAnalysisAttributes(player, true, true, false);
               tag.putBoolean("Activated", false);
            } else {
               AttributeHelper.addAnalysisAttributes(
                  player,
                  instance.isMastered(entity) ? CONFIG.analysisLevelMastered : CONFIG.analysisLevel,
                  instance.isMastered(entity) ? CONFIG.analysisRadiusMastered : CONFIG.analysisRadius
               );
               tag.putBoolean("Activated", true);
            }

            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, MATHEMATICIAN, true);
      AttributeInstance degrade = entity.getAttribute(TensuraAttributes.RESISTANCE_DEGRADATION);
      if (degrade != null) {
         degrade.addOrReplacePermanentModifier(new AttributeModifier(MATHEMATICIAN, 1.0, Operation.ADD_VALUE));
      }

      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.addOrReplacePermanentModifier(new AttributeModifier(MATHEMATICIAN, CONFIG.meleeDodge, Operation.ADD_VALUE));
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.addOrReplacePermanentModifier(new AttributeModifier(MATHEMATICIAN, CONFIG.projectileDodge, Operation.ADD_VALUE));
      }

      AttributeInstance dodgeNegate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (dodgeNegate != null) {
         dodgeNegate.addOrReplacePermanentModifier(new AttributeModifier(MATHEMATICIAN, CONFIG.dodgeNegation, Operation.ADD_VALUE));
      }

      AttributeInstance critical = entity.getAttribute(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE);
      if (critical != null) {
         critical.addOrReplacePermanentModifier(new AttributeModifier(MATHEMATICIAN, CONFIG.critChance, Operation.ADD_VALUE));
      }

      AttributeInstance learning = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
      if (learning != null) {
         learning.addOrReplacePermanentModifier(new AttributeModifier(MATHEMATICIAN, CONFIG.learningPoint, Operation.ADD_VALUE));
      }

      AttributeInstance mastery = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (mastery != null) {
         mastery.addOrReplacePermanentModifier(new AttributeModifier(MATHEMATICIAN, CONFIG.masteryPoint, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, MATHEMATICIAN, false);
      AttributeInstance degrade = entity.getAttribute(TensuraAttributes.RESISTANCE_DEGRADATION);
      if (degrade != null) {
         degrade.removeModifier(MATHEMATICIAN);
      }

      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.removeModifier(MATHEMATICIAN);
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.removeModifier(MATHEMATICIAN);
      }

      AttributeInstance negate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (negate != null) {
         negate.removeModifier(MATHEMATICIAN);
      }

      AttributeInstance critical = entity.getAttribute(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE);
      if (critical != null) {
         critical.removeModifier(MATHEMATICIAN);
      }

      AttributeInstance learning = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
      if (learning != null) {
         learning.removeModifier(MATHEMATICIAN);
      }

      AttributeInstance mastery = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (mastery != null) {
         mastery.removeModifier(MATHEMATICIAN);
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

   public boolean onDeath(ManasSkillInstance instance, LivingEntity owner, DamageSource source) {
      AttributeInstance level = owner.getAttribute(TensuraAttributes.ANALYSIS_LEVEL);
      if (level != null && !level.hasModifier(TensuraGlobalAttributeIds.ANALYSIS)) {
         instance.getOrCreateTag().putBoolean("Activated", false);
      }

      return true;
   }

   public void onRespawn(ManasSkillInstance instance, ServerPlayer player, boolean conqueredEnd) {
      if (instance.getOrCreateTag().getBoolean("Activated")) {
         AttributeInstance level = player.getAttribute(TensuraAttributes.ANALYSIS_LEVEL);
         if (level != null) {
            AttributeHelper.addAnalysisAttributes(
               player,
               instance.isMastered(player) ? CONFIG.analysisLevelMastered : CONFIG.analysisLevel,
               instance.isMastered(player) ? CONFIG.analysisRadiusMastered : CONFIG.analysisRadius
            );
         }
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      if (entity instanceof ServerPlayer player) {
         AttributeHelper.removeAnalysisAttributes(player, true, true, false);
      }
   }
}
