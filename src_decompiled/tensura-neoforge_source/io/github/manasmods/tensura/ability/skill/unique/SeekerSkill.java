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
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SeekerSkill extends Skill {
   public static final UniqueSkillConfig.Seeker CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Seeker;
   protected static final ResourceLocation SEEKER = ResourceLocation.fromNamespaceAndPath("tensura", "seeker");

   public SeekerSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      tag.putInt("CopyID", 0);
      instance.markDirty();
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
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

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0) && !instance.isTemporarySkill()) {
         TensuraSkillInstance manipulation = new TensuraSkillInstance((ManasSkill)ExtraSkills.LAW_MANIPULATION.get());
         manipulation.getOrCreateTag().putBoolean("NoMagiculeCost", true);
         SkillHelper.learnSkill(entity, manipulation);
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, SEEKER, true);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, SEEKER, false);
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

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      if (entity instanceof ServerPlayer player) {
         AttributeHelper.removeAnalysisAttributes(player, true, true, false);
      }
   }
}
