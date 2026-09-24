package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillClientUtils;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.level.block.state.BlockState;

public class SenseHeatSourceSkill extends Skill {
   public static final ExtraSkillConfig.SenseHeatSource CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).SenseHeatSource;
   public static final ResourceLocation HEAT_SENSE = ResourceLocation.fromNamespaceAndPath("tensura", "heat_sense");
   public static final ResourceLocation BLOCK_SENSE = ResourceLocation.fromNamespaceAndPath("tensura", "block_sense");

   public SenseHeatSourceSkill() {
      super(Skill.SkillType.EXTRA);
      SkillClientUtils.XRAY_TYPES.add(player -> {
         AttributeInstance radius = player.getAttribute(TensuraAttributes.HEAT_SENSE_RADIUS);
         if (radius == null || radius.getValue() <= 0.0 || radius.hasModifier(BLOCK_SENSE)) {
            return null;
         }

         if (player.level().dimensionType().ultraWarm()) {
            return null;
         }

         Predicate<BlockState> heatSource = state -> state.is(TensuraBlockTags.HEAT_SOURCE_BLOCKS) && state.getLightEmission() >= 3;
         return new SkillClientUtils.XrayType(heatSource, (int)radius.getValue(), 0.96F, 0.51F, 0.12F, 1.0F);
      });
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0) && !instance.isTemporarySkill()) {
         if (SkillUtils.hasSkillFully(entity, (ManasSkill)ExtraSkills.SENSE_SOUNDWAVE.get())) {
            if (SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.MAGIC_SENSE.get())) {
               SkillHelper.learnSkill(entity, ((UniversalPerceptionSkill)ExtraSkills.UNIVERSAL_PERCEPTION.get()).createLearningInstance(entity));
            }
         }
      }
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(entity);
      }

      tag.putInt("activatedTimes", time + 1);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (instance.isMastered(entity)) {
         AttributeInstance heatSense = entity.getAttribute(TensuraAttributes.HEAT_SENSE_RADIUS);
         if (heatSense != null) {
            CompoundTag tag = instance.getOrCreateTag();
            if (heatSense.hasModifier(BLOCK_SENSE)) {
               heatSense.removeModifier(BLOCK_SENSE);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               tag.putBoolean("CancelBlockSense", true);
            } else {
               AttributeModifier modifier = new AttributeModifier(BLOCK_SENSE, 0.1, Operation.ADD_VALUE);
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
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPermanentAttributeIfHigher(entity, TensuraAttributes.HEAT_SENSE_RADIUS, HEAT_SENSE, CONFIG.heatRadius, Operation.ADD_VALUE);
      if (instance.getOrCreateTag().getBoolean("CancelBlockSense")) {
         AttributeInstance heatSense = entity.getAttribute(TensuraAttributes.HEAT_SENSE_RADIUS);
         if (heatSense != null) {
            heatSense.removeModifier(BLOCK_SENSE);
         }
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeAttributeIfCorrect(entity, TensuraAttributes.HEAT_SENSE_RADIUS, HEAT_SENSE, CONFIG.heatRadius);
   }
}
