package io.github.manasmods.tensura.ability.magic.aspectual.mental;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.field.AreaField;
import io.github.manasmods.tensura.entity.magic.field.MarionetteLines;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DemonMarionetteMagic extends AspectualMagic {
   private static final AspectualMagicConfig.DemonMarionette CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).DemonMarionette;

   public DemonMarionetteMagic() {
      super(AspectualMagic.AspectualType.MENTAL);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public boolean isInstantCast(ManasSkillInstance instance, LivingEntity entity) {
      return false;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryGreat;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public boolean canLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (!super.canLearnSkill(instance, entity)) {
         return false;
      }

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.DEMON_DOMINATE.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((DemonDominateMagic)AspectualMagics.DEMON_DOMINATE.get()).getChatDisplayName(false)}
                  )
                  .withStyle(ChatFormatting.RED),
               true
            );
         }

         return false;
      } else {
         return true;
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, false);
      if (target == null || !target.isAlive()) {
         entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
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
      } else if (entity.isShiftKeyDown() && DominateMagic.cancelDominate(entity, target)) {
         instance.getOrCreateTag().putInt("FieldID", 0);
         instance.markDirty();
      } else if (!SubordinateHelper.isSubordinate(entity, target) && !RaceUtils.isSpiritual(target)) {
         if (!CharmSkill.isMindControlFailed(entity, target, entity.level(), false)) {
            double ep = instance.isMastered(entity) ? CONFIG.epRequirementMastered : CONFIG.epRequirement;
            double resisted = instance.isMastered(entity) ? CONFIG.resistedRequirementMastered : CONFIG.resistedRequirement;
            if (EnergyHelper.getMaxEP(target)
               > (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get()) ? resisted : ep)) {
               entity.sendSystemMessage(Component.translatable("tensura.targeting.ep_not_meet").withStyle(ChatFormatting.RED));
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.5F,
                     0.5F
                  );
            } else {
               CompoundTag tag = instance.getOrCreateTag();
               tag.putInt("FieldID", 0);
               MobEffectInstance slow = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), 15, CONFIG.slowLevel - 1, false, false, false
               );
               if (AreaField.getLastingField(
                  (EntityType<? extends AreaField>)MiscEntityTypes.MARIONETTE_LINES.get(),
                  0.0F,
                  target.getBbWidth() / 0.6F,
                  25,
                  10,
                  slow,
                  target.position(),
                  entity,
                  instance,
                  mode,
                  Pair.of(0.0, 0.0),
                  Pair.of(0.0, 0.0),
                  0
               ) instanceof MarionetteLines lines) {
                  lines.setVisualSize(target.getBbHeight() / 2.0F);
                  lines.setTarget(target);
                  target.setPos(lines.position());
                  target.hurtMarked = true;
               }
            }
         }
      } else {
         entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
         entity.level()
            .playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
               TensuraSkill.ABILITY_SOUND,
               0.5F,
               0.5F
            );
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      CompoundTag tag = instance.getOrCreateTag();
      if (entity.level().getEntity(tag.getInt("FieldID")) instanceof MarionetteLines lines) {
         double range = CONFIG.range;
         if (lines.distanceToSqr(entity) > range * range) {
            tag.putInt("FieldID", 0);
            instance.markDirty();
            return false;
         }

         lines.setAge(0);
         if (lines.getTarget() != null && lines.getTarget().isAlive()) {
            MagicCircle.castTargetedMagicCircle(
               lines.getTarget().getBbWidth() * 2.0F,
               25,
               lines.position(),
               MagicCircleVariant.MENTAL,
               false,
               entity,
               instance.getOrCreateTag(),
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
         }

         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      } else {
         tag.putInt("FieldID", 0);
         instance.markDirty();
         return false;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         if (entity.level().getEntity(tag.getInt("FieldID")) instanceof MarionetteLines lines) {
            if (lines.isAlive()) {
               Entity target = lines.getTarget();
               double range = CONFIG.range;
               if (target != null && target.isAlive() && !(lines.distanceToSqr(entity) > range * range)) {
                  if (target instanceof LivingEntity living) {
                     if (RaceUtils.isSpiritual(living)) {
                        entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
                        entity.level()
                           .playSound(
                              null,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                              TensuraSkill.ABILITY_SOUND,
                              0.5F,
                              0.5F
                           );
                        return;
                     }

                     if (CharmSkill.isMindControlFailed(entity, living, entity.level(), false)) {
                        return;
                     }

                     double ep = instance.isMastered(entity) ? CONFIG.epRequirementMastered : CONFIG.epRequirement;
                     double resisted = instance.isMastered(entity) ? CONFIG.resistedRequirementMastered : CONFIG.resistedRequirement;
                     int duration = instance.isMastered(entity) ? CONFIG.controlDurationMastered : CONFIG.controlDuration;
                     DominateMagic.dominate(
                        instance,
                        entity,
                        mode,
                        living,
                        SkillUtils.isSkillToggled(living, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get()) ? resisted : ep,
                        duration
                     );
                  }
               } else {
                  entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                        TensuraSkill.ABILITY_SOUND,
                        0.5F,
                        0.5F
                     );
               }
            }
         } else {
            tag.putInt("FieldID", 0);
            instance.markDirty();
         }
      }
   }
}
