package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class UniversalPerceptionSkill extends Skill {
   private static final ExtraSkillConfig.UniversalPerception CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).UniversalPerception;
   private static final ResourceLocation UNIVERSAL = ResourceLocation.fromNamespaceAndPath("tensura", "universal_perception");

   public UniversalPerceptionSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (EnergyHelper.isOutOfEnergy(entity, instance, 0)) {
         entity.sendSystemMessage(
            Component.translatable("tensura.skill.lack_magicule.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
         );
         instance.setToggled(false);
      } else if (!instance.isMastered(entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         int time = tag.getInt("activatedTimes");
         if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
            instance.addMasteryPoint(entity);
         }

         tag.putInt("activatedTimes", time + 1);
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      AttributeInstance heatSense = entity.getAttribute(TensuraAttributes.HEAT_SENSE_RADIUS);
      if (heatSense != null) {
         CompoundTag tag = instance.getOrCreateTag();
         if (heatSense.hasModifier(SenseHeatSourceSkill.BLOCK_SENSE)) {
            heatSense.removeModifier(SenseHeatSourceSkill.BLOCK_SENSE);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            tag.putBoolean("CancelBlockSense", true);
         } else {
            AttributeModifier modifier = new AttributeModifier(SenseHeatSourceSkill.BLOCK_SENSE, 0.1, Operation.ADD_VALUE);
            heatSense.addOrReplacePermanentModifier(modifier);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            tag.putBoolean("CancelBlockSense", false);
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPresenceSense(entity, CONFIG.presenceSense);
      AttributeHelper.addPermanentAttribute(entity, TensuraAttributes.PRESENCE_SENSE_RADIUS, UNIVERSAL, CONFIG.presenceRadius, Operation.ADD_VALUE);
      AttributeHelper.addPermanentAttributeIfHigher(entity, TensuraAttributes.PRESENCE_SENSE_RADIUS, SenseSoundwaveSkill.SOUND_SENSE, 0.1, Operation.ADD_VALUE);
      AttributeHelper.addPermanentAttributeIfHigher(
         entity, TensuraAttributes.HEAT_SENSE_RADIUS, SenseHeatSourceSkill.HEAT_SENSE, CONFIG.heatRadius, Operation.ADD_VALUE
      );
      if (instance.getOrCreateTag().getBoolean("CancelBlockSense")) {
         AttributeInstance heatSense = entity.getAttribute(TensuraAttributes.HEAT_SENSE_RADIUS);
         if (heatSense != null) {
            heatSense.removeModifier(SenseHeatSourceSkill.BLOCK_SENSE);
         }
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removePresenceSense(entity, CONFIG.presenceSense);
      AttributeHelper.removeAttribute(entity, TensuraAttributes.PRESENCE_SENSE_RADIUS, UNIVERSAL);
      AttributeHelper.removeAttributeIfCorrect(entity, TensuraAttributes.PRESENCE_SENSE_RADIUS, SenseSoundwaveSkill.SOUND_SENSE, 0.1);
      AttributeHelper.removeAttributeIfCorrect(entity, TensuraAttributes.HEAT_SENSE_RADIUS, SenseHeatSourceSkill.HEAT_SENSE, CONFIG.heatRadius);
   }
}
