package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.impl.TickingSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class FarsightSkill extends Skill {
   private static final CommonSkillConfig.FarSight CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).FarSight;
   private final ResourceLocation ZOOM = ResourceLocation.fromNamespaceAndPath("tensura", "farsight");

   public FarsightSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement ? true : SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.CLAIRVOYANCE.get());
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   @Override
   public boolean shouldTriggerReleaseOnHeldInterrupt(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity instanceof ServerPlayer player) {
         CompoundTag tag = instance.getOrCreateTag();
         if (!tag.contains("range")) {
            tag.putDouble("range", 2.0);
         }

         instance.markDirty();
         AttributeInstance zoom = entity.getAttribute(TensuraAttributes.VIEW_ZOOM);
         if (zoom != null) {
            zoom.addOrUpdateTransientModifier(new AttributeModifier(this.ZOOM, tag.getDouble("range"), Operation.ADD_VALUE));
         }

         if (instance.isMastered(player)) {
            int distance = Math.min((int)tag.getDouble("range") * 2, CONFIG.maxSenseRadius);
            AttributeInstance attribute = entity.getAttribute(TensuraAttributes.ANALYSIS_DISTANCE);
            if (attribute != null) {
               attribute.addOrUpdateTransientModifier(new AttributeModifier(this.ZOOM, distance, Operation.ADD_VALUE));
            }
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (entity instanceof ServerPlayer player) {
         AttributeInstance zoom = entity.getAttribute(TensuraAttributes.VIEW_ZOOM);
         if (zoom != null) {
            zoom.removeModifier(this.ZOOM);
         }

         if (instance.isMastered(player) && TickingSkill.isTickingSkill(entity, this, mode)) {
            AttributeInstance attribute = entity.getAttribute(TensuraAttributes.ANALYSIS_DISTANCE);
            if (attribute != null) {
               attribute.removeModifier(this.ZOOM);
            }
         }
      }
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      double newRange = tag.getDouble("range") + delta;
      if (newRange > CONFIG.maxZoom) {
         newRange = CONFIG.maxZoom;
      } else if (newRange < 2.0) {
         newRange = 2.0;
      }

      if (tag.getDouble("range") != newRange) {
         tag.putDouble("range", newRange);
         instance.markDirty();
         AttributeInstance zoom = entity.getAttribute(TensuraAttributes.VIEW_ZOOM);
         if (zoom != null) {
            zoom.addOrUpdateTransientModifier(new AttributeModifier(this.ZOOM, tag.getDouble("range"), Operation.ADD_VALUE));
         }

         if (instance.isMastered(entity)) {
            int distance = Math.min((int)newRange * 2, CONFIG.maxSenseRadius);
            AttributeInstance attribute = entity.getAttribute(TensuraAttributes.ANALYSIS_DISTANCE);
            if (attribute != null) {
               attribute.addOrUpdateTransientModifier(new AttributeModifier(this.ZOOM, distance, Operation.ADD_VALUE));
            }
         }
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      return true;
   }
}
