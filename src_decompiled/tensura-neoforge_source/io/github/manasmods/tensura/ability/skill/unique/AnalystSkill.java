package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.attribute.TensuraGlobalAttributeIds;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class AnalystSkill extends Skill {
   private static final UniqueSkillConfig.Analyst CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Analyst;
   protected static final ResourceLocation ANALYST = ResourceLocation.fromNamespaceAndPath("tensura", "analyst");

   public AnalystSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return mode != 1;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "analyst.appraisal";
         case 1 -> "analyst.analyze";
         default -> super.getModeId(instance, mode);
      };
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         TensuraSkillInstance skill = new TensuraSkillInstance((ManasSkill)ExtraSkills.LAW_MANIPULATION.get());
         skill.getOrCreateTag().putBoolean("NoMagiculeCost", true);
         SkillHelper.learnSkill(entity, skill, instance.getRemoveTime());
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0) {
         if (!(entity instanceof ServerPlayer player)) {
            return;
         }

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
            return;
         }

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
      } else {
         CompoundTag tag = instance.getOrCreateTag();
         tag.putInt("CopyID", 0);
         instance.markDirty();
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode == 0) {
         return false;
      }

      TensuraProjectile projectile = ObjectSelectionHelper.getTargetingEntity(TensuraProjectile.class, entity, CONFIG.analyzeRange, 0.5, false, true, false);
      if (projectile != null && projectile.isAlive()) {
         CompoundTag tag = instance.getOrCreateTag();
         if (heldTicks == 0) {
            ManasSkillInstance targetInstance = projectile.getSkill();
            if (targetInstance != null && targetInstance.getMastery() >= 0.0 && targetInstance.is(TensuraSkillTags.COPIABLE_MAGIC)) {
               tag.putInt("CopyID", projectile.getId());
               return true;
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
                     1.0F,
                     1.0F
                  );
               return false;
            }
         } else if (tag.getInt("CopyID") == projectile.getId()) {
            ManasSkillInstance targetInstance = projectile.getSkill();
            if (targetInstance != null && targetInstance.getMastery() >= 0.0 && targetInstance.is(TensuraSkillTags.COPIABLE_MAGIC)) {
               int time = instance.isMastered(entity) ? CONFIG.analyzeTimeMastered : CONFIG.analyzeTime;
               double chantSpeed = entity.getAttributeValue(TensuraAttributes.CHANT_SPEED);
               if (chantSpeed != 1.0) {
                  double off = chantSpeed - 1.0;
                  if (instance.isToggled()) {
                     off -= CONFIG.chantSpeed;
                  }

                  time = (int)(time / (1.0 + off / 2.0));
               }

               CompoundTag learning;
               if (tag.contains("learning")) {
                  learning = tag.getCompound("learning");
               } else {
                  learning = new CompoundTag();
               }

               int learnPoint = learning.getInt(targetInstance.getSkillId().toString());
               if (learnPoint >= time) {
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  Changeable<ManasSkill> changeable = Changeable.of(targetInstance.getSkill());
                  if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker())
                     .plunder(projectile.getOwner(), entity, false, changeable)
                     .isFalse()) {
                     instance.addMasteryPoint(entity);
                     if (SkillHelper.learnSkill(entity, (ManasSkill)changeable.get(), instance.getRemoveTime())) {
                        entity.level()
                           .playSound(
                              null,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                              TensuraSkill.ABILITY_SOUND,
                              1.0F,
                              1.0F
                           );
                     } else {
                        entity.sendSystemMessage(
                           Component.translatable("tensura.ability.activation_failed.plunder", new Object[]{targetInstance.getChatDisplayName(true)})
                              .withStyle(ChatFormatting.RED)
                        );
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
                     }
                  }

                  learning.remove(targetInstance.getSkillId().toString());
                  tag.put("learning", learning);
                  return false;
               } else {
                  if (entity instanceof Player player) {
                     player.displayClientMessage(
                        Component.translatable(
                              "tensura.magic.cast_time.remaining",
                              new Object[]{SkillUtils.ROUND_DOUBLE.format(learnPoint / 20.0), SkillUtils.ROUND_DOUBLE.format(time / 20.0)}
                           )
                           .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                        true
                     );
                  }

                  learning.putInt(targetInstance.getSkillId().toString(), learnPoint + 1);
                  tag.put("learning", learning);
                  return true;
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
                     1.0F,
                     1.0F
                  );
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, ANALYST, true);
      AttributeInstance learning = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
      if (learning != null) {
         learning.addOrReplacePermanentModifier(new AttributeModifier(ANALYST, CONFIG.learningPoint, Operation.ADD_VALUE));
      }

      AttributeInstance mastery = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (mastery != null) {
         mastery.addOrReplacePermanentModifier(new AttributeModifier(ANALYST, CONFIG.masteryPoint, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, ANALYST, false);
      AttributeInstance learning = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
      if (learning != null) {
         learning.removeModifier(ANALYST);
      }

      AttributeInstance mastery = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (mastery != null) {
         mastery.removeModifier(ANALYST);
      }
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled() && !instance.isMastered(entity);
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
