package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.attribute.TensuraGlobalAttributeIds;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.AttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

public class AnalyticalAppraisalSkill extends Skill {
   private static final ExtraSkillConfig.AnalyticalAppraisal CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).AnalyticalAppraisal;

   public AnalyticalAppraisalSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement ? true : SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.ANALYZE.get());
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
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
                  instance.isMastered(entity) ? CONFIG.levelMastered : CONFIG.level,
                  instance.isMastered(entity) ? CONFIG.radiusMastered : CONFIG.radius
               );
               tag.putBoolean("Activated", true);
            }

            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return entity instanceof Player player ? player.getAttributeValue(TensuraAttributes.ANALYSIS_LEVEL) >= CONFIG.level : false;
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
               player, instance.isMastered(player) ? CONFIG.levelMastered : CONFIG.level, instance.isMastered(player) ? CONFIG.radiusMastered : CONFIG.radius
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
