package io.github.manasmods.tensura.handler;

import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.SkillEvents.RemoveSkillEvent;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillActivationEvent;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillMasteryEvent;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillScrollEvent;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillUpdateCooldownEvent;
import io.github.manasmods.manascore.skill.api.SkillEvents.UnlockSkillEvent;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.extra.EarthManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.FlameManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.GravityManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.LightningManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.MolecularManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.SoundManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.SpatialManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.WaterManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.WeatherManipulationSkill;
import io.github.manasmods.tensura.ability.skill.extra.WindManipulationSkill;
import io.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import io.github.manasmods.tensura.ability.skill.unique.MartialMasterSkill;
import io.github.manasmods.tensura.ability.skill.unique.SeekerSkill;
import io.github.manasmods.tensura.ability.skill.unique.TunerSkill;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.advancement.AbilityTrigger;
import io.github.manasmods.tensura.config.ability.AbilityConfig;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.event.TensuraInputEvents;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

public class AbilityHandler {
   public static void init() {
      SkillEvents.UNLOCK_SKILL
         .register(
            (UnlockSkillEvent)(instance, entity, unlockMessage) -> {
               if (instance.getMastery() < 0.0) {
                  return EventResult.pass();
               }

               if (entity instanceof ServerPlayer player) {
                  ((AbilityTrigger)TensuraCriteriaTriggers.ABILITY_OBTAINED.get()).trigger(player, instance.getSkill());
               }

               if (!instance.isTemporarySkill()) {
                  CompoundTag tag = instance.getTag();
                  if (tag != null && tag.getBoolean("NoMagiculeCost")) {
                     tag.remove("NoMagiculeCost");
                     if (tag.isEmpty()) {
                        instance.setTag(null);
                     }
                  } else if (!entity.hasInfiniteMaterials() && instance.getSkill() instanceof TensuraSkill skill) {
                     Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
                     if (optional.isEmpty() || !optional.get().isIntrinsicSkill(entity, skill)) {
                        double cost = skill.getAcquiringMagiculeCost(instance) * entity.level().getGameRules().getInt(TensuraGameRules.MP_SKILL_COST) / 100.0;
                        AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
                        if (magicule == null) {
                           return EventResult.pass();
                        }

                        if (magicule.getValue() <= cost) {
                           entity.sendSystemMessage(
                              Component.translatable("tensura.skill.acquire_failed.mp", new Object[]{skill.getChatDisplayName(true)})
                                 .withStyle(ChatFormatting.RED)
                           );
                           return EventResult.interruptFalse();
                        }

                        magicule.setBaseValue(magicule.getBaseValue() - cost);
                        IExistence existence = TensuraStorages.getExistenceFrom(entity);
                        if (!magicule.getModifiers().isEmpty()) {
                           existence.setMagicule(Math.min(existence.getMagicule() - cost, magicule.getValue()));
                        } else {
                           existence.setMagicule(existence.getMagicule() - cost);
                        }

                        existence.markDirty();
                     }
                  }
               } else {
                  CompoundTag tag = instance.getTag();
                  if (tag != null && tag.contains("Creator")) {
                     ResourceLocation location = ResourceLocation.parse(tag.getString("Creator"));
                     ManasSkill creator = (ManasSkill)SkillAPI.getSkillRegistry().get(location);
                     if (creator == null) {
                        return EventResult.interruptFalse();
                     }

                     Optional<ManasSkillInstance> optionalCreator = SkillAPI.getSkillsFrom(entity).getSkill(creator);
                     if (optionalCreator.isPresent()) {
                        CompoundTag creatorTag = optionalCreator.get().getTag();
                        if (creatorTag == null || !creatorTag.contains("CreatedSkill")) {
                           return EventResult.interruptFalse();
                        }

                        if (!creatorTag.getString("CreatedSkill").equals(instance.getSkillId().toString())) {
                           return EventResult.interruptFalse();
                        }
                     } else if (!entity.getType().equals(HumanEntityTypes.CLONE.get())) {
                        return EventResult.interruptFalse();
                     }
                  }
               }

               return EventResult.pass();
            }
         );
      TensuraSkillEvents.SKILL_LEARNING.register((TensuraSkillEvents.SkillLearningEvent)(instance, entity, mode, requirementPoint, newPoint) -> {
         if (instance.is(TensuraSkillTags.BATTLEWILL)) {
            if (SkillUtils.hasSkill(entity, (ManasSkill)UniqueSkills.MARTIAL_MASTER.get())) {
               newPoint.set((Double)newPoint.get() + MartialMasterSkill.CONFIG.learningPoint);
            }

            if (SkillUtils.isSkillToggled(entity, (ManasSkill)UniqueSkills.FIGHTER.get())) {
               newPoint.set(requirementPoint);
            }
         } else if (instance.is(TensuraSkillTags.MAGIC)) {
            if (SkillUtils.hasSkill(entity, (ManasSkill)UniqueSkills.SEEKER.get())) {
               newPoint.set((Double)newPoint.get() + SeekerSkill.CONFIG.learningPoint);
            }

            if (SkillUtils.isSkillToggled(entity, (ManasSkill)UniqueSkills.FIGHTER.get())) {
               newPoint.set(requirementPoint);
            }
         } else if (mode != 0 && SkillUtils.isSkillToggled(entity, (ManasSkill)UniqueSkills.FIGHTER.get())) {
            newPoint.set(requirementPoint);
         }

         return EventResult.pass();
      });
      SkillEvents.SKILL_MASTERY.register((SkillMasteryEvent)(instance, entity, newMastery) -> {
         if (instance.is(TensuraSkillTags.BATTLEWILL) && SkillUtils.hasSkill(entity, (ManasSkill)UniqueSkills.MARTIAL_MASTER.get())) {
            newMastery.set((Double)newMastery.get() + MartialMasterSkill.CONFIG.masteryPoint);
         }

         if (instance.is(TensuraSkillTags.MAGIC) && SkillUtils.hasSkill(entity, (ManasSkill)UniqueSkills.SEEKER.get())) {
            newMastery.set((Double)newMastery.get() + SeekerSkill.CONFIG.masteryPoint);
         }

         if (instance.getMaxMastery() > (Double)newMastery.get()) {
            return EventResult.pass();
         }

         EarthManipulationSkill.learnEarthManipulation(instance, entity);
         FlameManipulationSkill.learnFlameManipulation(instance, entity);
         SpatialManipulationSkill.learnSpaceManipulation(instance, entity);
         WaterManipulationSkill.learnWaterManipulation(instance, entity);
         WindManipulationSkill.learnWindManipulation(instance, entity);
         GravityManipulationSkill.learnGravityManipulation(instance, entity);
         LightningManipulationSkill.learnLightningManipulation(instance, entity);
         MolecularManipulationSkill.learnMolecularManipulation(instance, entity);
         SoundManipulationSkill.learnSoundManipulation(instance, entity);
         WeatherManipulationSkill.learnWeatherManipulation(instance, entity);
         return EventResult.pass();
      });
      SkillEvents.REMOVE_SKILL
         .register(
            (RemoveSkillEvent)(instance, entity, message) -> {
               instance.onToggleOff(entity);
               if (instance.getRemoveTime() != -1) {
                  if (instance.shouldRemove()) {
                     entity.sendSystemMessage(
                        Component.translatable("tensura.skill.temporary.remove", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
                     );
                  }

                  if (instance.getTag() != null && instance.getTag().getInt("OldRemoval") == -1 && instance.getTag().contains("OldMastery")) {
                     instance.setRemoveTime(-1);
                     instance.setMastery(instance.getTag().getInt("OldMastery"));
                     if (instance.getMastery() < 0.0 && instance.isToggled()) {
                        instance.setToggled(false);
                        instance.onToggleOff(entity);
                     }

                     instance.getTag().remove("OldMastery");
                     return EventResult.interruptFalse();
                  }
               }

               if (ReincarnationMenu.CONFIG.Skills.learnableResistances.contains(instance.getSkillId().toString())) {
                  int learning;
                  if (instance.getSkill() instanceof ResistSkill resist) {
                     learning = resist.getLearningPointRequirement();
                  } else {
                     learning = TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement;
                  }

                  instance.setMastery(learning * -1);
                  instance.setRemoveTime(-1);
                  instance.setToggled(false);
                  instance.onToggleOff(entity);
                  return EventResult.interruptFalse();
               } else {
                  if (instance.getSkill() instanceof ISpatialStorage storage && entity instanceof Player player) {
                     storage.dropAllItems(instance, player);
                  }

                  return EventResult.pass();
               }
            }
         );
      SkillEvents.ACTIVATE_SKILL
         .register(
            (SkillActivationEvent)(skillInstance, owner, keyNumber, mode) -> {
               ManasSkillInstance instance = (ManasSkillInstance)skillInstance.get();
               if (!(instance.getSkill() instanceof TensuraSkill)) {
                  return EventResult.pass();
               }

               IAbility ability = TensuraStorages.getAbilityFrom(owner);
               if (!Magic.isStaffCasting(instance, owner) && !ability.isAbilityInActivePreset(keyNumber, instance.getSkill(), mode)) {
                  return EventResult.interruptFalse();
               }

               ManasSkill manasSkill = instance.getSkill();
               if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(owner, mode)) {
                  if (owner instanceof Player player) {
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.cooldown", new Object[]{instance.getChatDisplayName(false)}).withStyle(ChatFormatting.RED), true
                     );
                  }

                  return EventResult.pass();
               } else {
                  if (!(instance.getMastery() < 0.0)) {
                     return EventResult.pass();
                  }

                  if (manasSkill instanceof TensuraSkill skill && skill.canLearnSkill(instance, owner)) {
                     if (!skill.addLearnPoint(instance, owner, mode)) {
                        return EventResult.interruptFalse();
                     }

                     if (checkFailChance(owner, instance.getMastery()) && SkillHelper.applyLearningPenalty(owner) && instance.getMastery() < 0.0) {
                        AbilityConfig.Learning learning = TensuraSkill.BASE_CONFIG.Learning;
                        int min = learning.failingPenaltyMin;
                        int max = learning.failingPenaltyMax;
                        int penalty = max <= min ? min : owner.getRandom().nextInt(min, max + 1);
                        instance.setMastery(Math.max(instance.getMastery() - penalty, learning.learningPointRequirement * -1));
                     }
                  } else if (!instance.onCoolDown(mode) && owner instanceof Player player) {
                     instance.setCoolDown(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown, mode);
                     player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.learn_points.failed", new Object[]{instance.getChatDisplayName(false)})
                           .withStyle(ChatFormatting.RED),
                        true
                     );
                  }

                  return EventResult.interruptFalse();
               }
            }
         );
      SkillEvents.SKILL_UPDATE_COOLDOWN.register((SkillUpdateCooldownEvent)(instance, owner, mode, currentCooldown, newCooldown) -> {
         if ((Integer)newCooldown.get() <= 0 && instance.getSkill().equals(UniqueSkills.TUNER.get())) {
            TunerSkill.clearDeathTypes(instance, owner);
         }

         return EventResult.pass();
      });
      SkillEvents.SKILL_SCROLL
         .register(
            (SkillScrollEvent)(skillInstance, owner, mode, delta) -> {
               if (!(((ManasSkillInstance)skillInstance.get()).getSkill() instanceof TensuraSkill)) {
                  return EventResult.pass();
               } else {
                  return !Magic.isStaffCasting((ManasSkillInstance)skillInstance.get(), owner)
                        && !TensuraStorages.getAbilityFrom(owner)
                           .isAbilityInActivePreset(((ManasSkillInstance)skillInstance.get()).getSkill(), (Integer)mode.get())
                     ? EventResult.interruptFalse()
                     : EventResult.pass();
               }
            }
         );
      TensuraInputEvents.SKILL_NUMBER_KEY
         .register(
            (TensuraInputEvents.NumberKeyPressEvent)(skillInstance, owner, key) -> !TensuraStorages.getAbilityFrom(owner)
                  .isAbilityInActivePreset(((ManasSkillInstance)skillInstance.get()).getSkill())
               ? EventResult.interruptFalse()
               : EventResult.pass()
         );
      TensuraSkillEvents.SKILL_PLUNDER
         .register(
            (TensuraSkillEvents.SkillPlunderEvent)(target, owner, steal, skill) -> {
               if (target == null) {
                  return EventResult.pass();
               } else {
                  return !TensuraBehaviourHelper.CONFIG.Boss.bossAreaSkillPlunder && target.level().dimension().equals(TensuraDimensions.BOSS_AREA)
                     ? EventResult.interruptFalse()
                     : EventResult.pass();
               }
            }
         );
      TensuraSkillEvents.SKILL_GRIEF_PRE
         .register(
            (TensuraSkillEvents.SkillGriefEvent)(instance, level, owner, x, y, z) -> {
               if (owner == null) {
                  return EventResult.pass();
               } else {
                  return !TensuraBehaviourHelper.CONFIG.Boss.bossAreaSkillGrief && level.dimension().equals(TensuraDimensions.BOSS_AREA)
                     ? EventResult.interruptFalse()
                     : EventResult.pass();
               }
            }
         );
   }

   private static boolean checkFailChance(LivingEntity entity, double mastery) {
      if (mastery <= -80.0) {
         return entity.getRandom().nextFloat() >= 0.05;
      } else if (mastery <= -60.0) {
         return entity.getRandom().nextFloat() >= 0.1;
      } else if (mastery <= -40.0) {
         return entity.getRandom().nextFloat() >= 0.2;
      } else {
         return mastery <= -20.0 ? entity.getRandom().nextFloat() >= 0.3 : entity.getRandom().nextFloat() >= 0.4;
      }
   }
}
