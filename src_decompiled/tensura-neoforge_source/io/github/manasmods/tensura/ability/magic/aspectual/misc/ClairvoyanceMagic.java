package io.github.manasmods.tensura.ability.magic.aspectual.misc;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class ClairvoyanceMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Clairvoyance CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Clairvoyance;
   protected static final ResourceLocation ZOOM = ResourceLocation.fromNamespaceAndPath("tensura", "clairvoyance");

   public ClairvoyanceMagic() {
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

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      int castTime = this.getCastingTime(instance, entity);
      if (heldTicks == castTime) {
         this.onZoom(instance, entity);
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

   public void onZoom(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      if (!tag.contains("range")) {
         tag.putDouble("range", 2.0);
      }

      instance.markDirty();
      AttributeInstance zoom = entity.getAttribute(TensuraAttributes.VIEW_ZOOM);
      if (zoom != null) {
         zoom.addOrUpdateTransientModifier(new AttributeModifier(ZOOM, tag.getDouble("range"), Operation.ADD_VALUE));
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      AttributeInstance zoom = entity.getAttribute(TensuraAttributes.VIEW_ZOOM);
      if (zoom != null) {
         zoom.removeModifier(ZOOM);
      }
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      double newRange = tag.getDouble("range") + delta;
      double max = instance.isMastered(entity) ? CONFIG.maxZoom : CONFIG.maxZoomMastered;
      if (newRange > max) {
         newRange = max;
      } else if (newRange < 2.0) {
         newRange = 2.0;
      }

      if (tag.getDouble("range") != newRange) {
         tag.putDouble("range", newRange);
         instance.markDirty();
         AttributeInstance zoom = entity.getAttribute(TensuraAttributes.VIEW_ZOOM);
         if (zoom != null) {
            zoom.addOrUpdateTransientModifier(new AttributeModifier(ZOOM, tag.getDouble("range"), Operation.ADD_VALUE));
         }
      }
   }
}
