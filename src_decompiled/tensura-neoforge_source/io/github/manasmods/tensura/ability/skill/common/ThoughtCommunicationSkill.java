package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class ThoughtCommunicationSkill extends Skill {
   private static final CommonSkillConfig.ThoughtCommunication CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).ThoughtCommunication;

   public ThoughtCommunicationSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (instance.isMastered(entity)) {
         return mode == 0 ? 1 : 0;
      } else {
         return mode == 0 ? -1 : 0;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "thought_communication.movement";
         case 1 -> "thought_communication.targeting";
         default -> super.getModeId(instance, mode);
      };
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0) {
         movementBehaviour(instance, entity);
      } else {
         targetingBehaviour(instance, entity);
      }
   }

   public static void movementBehaviour(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      if (entity.isShiftKeyDown()) {
         List<Mob> list = entity.level()
            .getEntitiesOfClass(Mob.class, entity.getBoundingBox().inflate(CONFIG.telepathyRadius), living -> living.isAlliedTo(entity) && !living.is(entity));
         if (list.isEmpty()) {
            if (entity instanceof Player player) {
               player.displayClientMessage(
                  Component.translatable("tensura.telepathy.subordinate_all.not_found").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true
               );
            }

            return;
         }

         if (tag.getInt("usedTimes") % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
            instance.addMasteryPoint(entity);
         }

         tag.putInt("usedTimes", tag.getInt("usedTimes") + 1);
         int command = tag.getInt("command");
         command = command == 3 ? 1 : command + 1;
         tag.putInt("command", command);
         instance.markDirty();

         for (Mob mob : list) {
            MutableComponent message = switch (command) {
               case 2 -> {
                  SubordinateHelper.setFollow(mob);
                  yield Component.translatable("tensura.telepathy.subordinate_all.follow");
               }
               case 3 -> {
                  SubordinateHelper.setWander(mob);
                  yield Component.translatable("tensura.telepathy.subordinate_all.wander");
               }
               default -> {
                  SubordinateHelper.setStay(mob);
                  yield Component.translatable("tensura.telepathy.subordinate_all.stay");
               }
            };
            if (entity instanceof Player player) {
               player.displayClientMessage(message.setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true);
            }

            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
               );
         }
      } else {
         movementTelepathy(instance, entity);
      }
   }

   public static void movementTelepathy(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.telepathyRadius, false, false);
      if (target == null) {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.targeting.not_targeted").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true);
         }
      } else {
         if (SubordinateHelper.isSubordinate(entity, target) && target instanceof Mob mob) {
            TelepathySkill.telepathy(instance, entity, mob);
         } else if (attackCommand(entity, entity, target, CONFIG.telepathyRadius)) {
            if (tag.getInt("usedTimes") % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
               instance.addMasteryPoint(entity);
            }

            tag.putInt("usedTimes", tag.getInt("usedTimes") + 1);
            instance.markDirty();
         }
      }
   }

   public static boolean attackCommand(Entity directCommander, LivingEntity entity, LivingEntity target, double radius) {
      if (target.hasInfiniteMaterials()) {
         return false;
      }

      List<Mob> list = directCommander.level()
         .getEntitiesOfClass(Mob.class, directCommander.getBoundingBox().inflate(radius), mobx -> SubordinateHelper.isSubordinate(entity, mobx));
      if (list.isEmpty()) {
         if (directCommander == entity && entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.telepathy.subordinate_all.not_found").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true
            );
         }

         return false;
      } else {
         for (Mob mob : list) {
            mob.setTarget(target);
            SubordinateHelper.setFollow(mob);
         }

         if (directCommander == entity) {
            entity.swing(InteractionHand.MAIN_HAND, true);
         }

         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
            );
         return true;
      }
   }

   public static void targetingBehaviour(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      if (entity.isShiftKeyDown()) {
         List<Mob> list = entity.level()
            .getEntitiesOfClass(Mob.class, entity.getBoundingBox().inflate(15.0), living -> living.isAlliedTo(entity) && !living.is(entity));
         if (list.isEmpty()) {
            if (entity instanceof Player player) {
               player.displayClientMessage(
                  Component.translatable("tensura.telepathy.subordinate_all.not_found").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true
               );
            }

            return;
         }

         int command = tag.getInt("targetingCommand");
         command = command == 3 ? 0 : command + 1;
         tag.putInt("targetingCommand", command);
         instance.markDirty();

         for (Mob mob : list) {
            MutableComponent message = switch (command) {
               case 1 -> {
                  SubordinateHelper.setAggressive(mob);
                  yield Component.translatable("tensura.telepathy.subordinate_all.aggressive");
               }
               case 2 -> {
                  SubordinateHelper.setProtect(mob);
                  yield Component.translatable("tensura.telepathy.subordinate_all.protect");
               }
               case 3 -> {
                  SubordinateHelper.setNeutral(mob);
                  yield Component.translatable("tensura.telepathy.subordinate_all.neutral");
               }
               default -> {
                  SubordinateHelper.setPassive(mob);
                  yield Component.translatable("tensura.telepathy.subordinate_all.passive");
               }
            };
            if (entity instanceof Player player) {
               player.displayClientMessage(message.setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true);
            }

            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
               );
         }
      } else {
         targetingTelepathy(entity);
      }
   }

   public static void targetingTelepathy(LivingEntity entity) {
      Mob mob = ObjectSelectionHelper.getTargetingEntity(Mob.class, entity, CONFIG.telepathyRadius, 0.2, false, false, false);
      if (mob != null && SubordinateHelper.isSubordinate(entity, mob)) {
         if (mob instanceof ISubordinate tamable) {
            tamable.cycleBehaviour(mob, entity);
         }

         entity.swing(InteractionHand.MAIN_HAND, true);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
            );
      } else if (entity instanceof Player player) {
         player.displayClientMessage(Component.translatable("tensura.telepathy.subordinate.not_found").withStyle(ChatFormatting.RED), true);
      }
   }
}
