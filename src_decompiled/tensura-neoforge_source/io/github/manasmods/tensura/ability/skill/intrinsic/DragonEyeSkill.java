package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class DragonEyeSkill extends Skill {
   private static final IntrinsicSkillConfig.DragonEye CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).DragonEye;
   private final ResourceLocation ZOOM = ResourceLocation.fromNamespaceAndPath("tensura", "dragon_eye");

   public DragonEyeSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() >= 0.0;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

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
            if (!tag.contains("range")) {
               tag.putDouble("range", 2.0);
            }

            instance.markDirty();
            int level = instance.isMastered(entity) ? CONFIG.senseLevelMastered : CONFIG.senseLevel;
            int distance = Math.min((int)tag.getDouble("range") * 5, CONFIG.maxSenseRadius);
            AttributeHelper.addAnalysisAttributes(player, level, distance, tag.getDouble("range"), this.ZOOM);
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (entity instanceof ServerPlayer player) {
         AttributeHelper.removeAnalysisAttributes(player, true, true, true, this.ZOOM);
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
}
