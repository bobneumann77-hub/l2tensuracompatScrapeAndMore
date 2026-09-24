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
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.race.RaceUtils;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DemonDominateMagic extends AspectualMagic {
   private static final AspectualMagicConfig.DemonDominate CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).DemonDominate;

   public DemonDominateMagic() {
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

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.DOMINATE.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((DominateMagic)AspectualMagics.DOMINATE.get()).getChatDisplayName(false)}
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
      CompoundTag tag = instance.getOrCreateTag();
      tag.putInt("TargetID", 0);
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
            double requirement = SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())
               ? CONFIG.resistedRequirement
               : CONFIG.epRequirement;
            if (EnergyHelper.getMaxEP(target) > requirement) {
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
               tag.putInt("TargetID", target.getId());
               instance.markDirty();
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
      } else if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      } else {
         CompoundTag tag = instance.getOrCreateTag();
         int id = tag.getInt("TargetID");
         if (id == 0) {
            return false;
         } else {
            Entity target = entity.level().getEntity(id);
            double range = CONFIG.range;
            if (target instanceof LivingEntity living && living.isAlive() && living.distanceToSqr(entity) <= range * range) {
               MagicCircle.castTargetedMagicCircle(
                  target.getBbWidth() * 2.0F,
                  25,
                  target.position(),
                  MagicCircleVariant.MENTAL,
                  false,
                  entity,
                  instance.getOrCreateTag(),
                  instance,
                  mode,
                  Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
               );
               this.applyCastingVisual(instance, entity, heldTicks, mode);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         int id = tag.getInt("TargetID");
         if (id != 0) {
            Entity target = entity.level().getEntity(id);
            if (target instanceof LivingEntity living) {
               double range = CONFIG.range;
               if (!target.isAlive() || target.distanceToSqr(entity) > range * range) {
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
               } else if (RaceUtils.isSpiritual(living)) {
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
               } else if (!CharmSkill.isMindControlFailed(entity, living, entity.level(), false)) {
                  double requirement = SkillUtils.isSkillToggled(living, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())
                     ? CONFIG.resistedRequirement
                     : CONFIG.epRequirement;
                  int duration = instance.isMastered(entity) ? CONFIG.controlDurationMastered : CONFIG.controlDuration;
                  DominateMagic.dominate(instance, entity, mode, living, requirement, duration);
               }
            }
         }
      }
   }
}
