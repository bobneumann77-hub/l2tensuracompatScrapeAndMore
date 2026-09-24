package io.github.manasmods.tensura.ability.magic.aspectual.misc;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class AnalyzeMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Analyze CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Analyze;
   protected static final ResourceLocation ANALYZE = ResourceLocation.fromNamespaceAndPath("tensura", "analyze");

   public AnalyzeMagic() {
      super(AspectualMagic.AspectualType.MISC);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   @Override
   public boolean shouldTriggerReleaseOnHeldInterrupt(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      return true;
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      int castTime = this.getCastingTime(instance, entity);
      instance.markDirty();
      if (heldTicks == castTime) {
         this.onAnalyze(instance, entity);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      } else if (heldTicks > castTime && heldTicks % 20 == 0) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         instance.addMasteryPoint(entity);
      }

      this.applyCastingVisual(instance, entity, heldTicks, mode);
      return true;
   }

   public void onAnalyze(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance analysis = entity.getAttribute(TensuraAttributes.ANALYSIS_LEVEL);
      if (analysis != null) {
         double level = instance.isMastered(entity) ? CONFIG.bonusAnalysisMastered : CONFIG.bonusAnalysis;
         analysis.addOrUpdateTransientModifier(new AttributeModifier(ANALYZE, level, Operation.ADD_VALUE));
      }

      AttributeInstance distance = entity.getAttribute(TensuraAttributes.ANALYSIS_DISTANCE);
      if (distance != null) {
         double level = instance.isMastered(entity) ? CONFIG.bonusAnalysisRadiusMastered : CONFIG.bonusAnalysisRadius;
         distance.addOrUpdateTransientModifier(new AttributeModifier(ANALYZE, level, Operation.ADD_VALUE));
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      AttributeInstance analysis = entity.getAttribute(TensuraAttributes.ANALYSIS_LEVEL);
      if (analysis != null) {
         analysis.removeModifier(ANALYZE);
      }

      AttributeInstance distance = entity.getAttribute(TensuraAttributes.ANALYSIS_DISTANCE);
      if (distance != null) {
         distance.removeModifier(ANALYZE);
      }
   }
}
