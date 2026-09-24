package io.github.manasmods.tensura.ability;

import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillMasteryEvent;
import io.github.manasmods.manascore.skill.api.SkillEvents.UnlockSkillEvent;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.subclass.ISubAbilityModeHolder;
import io.github.manasmods.tensura.advancement.AbilityTrigger;
import io.github.manasmods.tensura.config.ability.AbilityConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.debuff.SleepEffect;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.boss.template.BossFightInstance;
import io.github.manasmods.tensura.storage.restriction.template.WorldRestrictionInstance;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.TensuraEnumHelper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class TensuraSkill extends ManasSkill {
   public static final AbilityConfig BASE_CONFIG = (AbilityConfig)ConfigRegistry.getConfig(AbilityConfig.class);
   public static SoundSource ABILITY_SOUND = TensuraEnumHelper.createSoundSource("ABILITY", "ability");

   public TensuraSkillInstance createDefaultInstance() {
      return new TensuraSkillInstance(this);
   }

   public TensuraSkillInstance createLearningInstance(LivingEntity entity) {
      TensuraSkillInstance instance = new TensuraSkillInstance(this);
      instance.setMastery(this.getAcquirementMastery(entity));
      instance.markDirty();
      return instance;
   }

   @Nullable
   public MutableComponent getColoredName() {
      MutableComponent name = super.getName();
      return name == null ? null : name.withStyle(ChatFormatting.WHITE);
   }

   public MutableComponent getChatDisplayName(boolean withDescription) {
      Style style = Style.EMPTY.withColor(ChatFormatting.GRAY);
      if (withDescription) {
         MutableComponent hoverMessage = this.getColoredName().append("\n");
         hoverMessage.append(this.getSkillDescription().withStyle(ChatFormatting.GRAY));
         hoverMessage.append("\n").append(Component.literal(SkillAPI.getSkillRegistry().getId(this).toString()).withStyle(ChatFormatting.DARK_GRAY));
         style = style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, hoverMessage));
      }

      MutableComponent component = Component.literal("[").append(this.getColoredName()).append("]");
      return component.withStyle(style);
   }

   public DamageSource createSource(ManasSkillInstance instance, LivingEntity attacker, ResourceKey<DamageType> type, int mode) {
      return TensuraDamageTypes.getEntityDamageSource(attacker.level(), type, attacker)
         .tensura$setAbilityInstance(instance)
         .tensura$setAbilityMode(mode)
         .tensura$setAuraCost(this.getAuraCost(attacker, instance, mode))
         .tensura$setMagiculeCost(this.getMagiculeCost(attacker, instance, mode));
   }

   public void addEffect(LivingEntity target, ManasSkillInstance instance, int mode, LivingEntity owner, Holder<MobEffect> holder, int duration, int level) {
      MobEffectInstance effect = new MobEffectInstance(holder, duration, level, false, false, false);
      TensuraMobEffect.addEffect(target, effect, owner, instance.getSkill(), mode);
   }

   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (instance.isToggled()) {
         instance.onToggleOff(entity);
      }

      if (!instance.isSubInstance()) {
         IAbility ability = TensuraStorages.getAbilityFrom(entity);
         if (ability.removeSkillFromPresets(instance.getSkill())) {
            ability.markDirty();
            entity.manasCore$sync();
         }
      }
   }

   public boolean shouldShowCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode);
   }

   public boolean shouldTriggerReleaseOnHeldInterrupt(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      return !this.isInSlot(entity, instance);
   }

   public List<Integer> getModeLearningList(ManasSkillInstance instance) {
      return List.of();
   }

   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return 0;
   }

   public String getModeId(ManasSkillInstance instance, int mode) {
      return "default";
   }

   public Component getModeName(ManasSkillInstance instance, int mode) {
      MutableComponent modeName = Component.translatable(instance.getSkillId().getNamespace() + ".skill.mode." + this.getModeId(instance, mode));
      return modeName.withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
   }

   public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return true;
   }

   public boolean isInSlot(LivingEntity entity, ManasSkillInstance instance, int mode) {
      IAbility ability = TensuraStorages.getAbilityFrom(entity);
      ManasSkill parentSkill = instance.getParentSkill();
      if (parentSkill != null) {
         if (!ability.isAbilityInActivePreset(parentSkill)) {
            return false;
         }

         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(entity).getSkill(parentSkill);
         return optional.filter(manasSkillInstance -> ISubAbilityModeHolder.isModeInSlot(manasSkillInstance, entity, this, mode)).isPresent();
      } else {
         return ability.isAbilityInActivePreset(instance.getSkill(), mode);
      }
   }

   public boolean isInSlot(LivingEntity entity, ManasSkillInstance instance) {
      IAbility ability = TensuraStorages.getAbilityFrom(entity);
      ManasSkill parentSkill = instance.getParentSkill();
      return parentSkill != null && ability.isAbilityInActivePreset(parentSkill) ? true : ability.isAbilityInActivePreset(instance.getSkill());
   }

   public boolean isInSlot(LivingEntity entity) {
      return TensuraStorages.getAbilityFrom(entity).isAbilityInActivePreset(this);
   }

   public int getMaxHeldTime(ManasSkillInstance instance, LivingEntity entity) {
      if (!this.isInSlot(entity, instance)) {
         return Magic.isStaffCasting(instance, entity) ? 7200 : 20;
      } else {
         return 72000;
      }
   }

   protected boolean hasAttributeApplied(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation location) {
      AttributeInstance attributeInstance = entity.getAttribute(attribute);
      return attributeInstance == null ? false : attributeInstance.hasModifier(location);
   }

   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return false;
   }

   public int getAcquirementMastery(LivingEntity entity) {
      return BASE_CONFIG.Learning.learningPointRequirement * -1;
   }

   public double getAcquiringMagiculeCost(ManasSkillInstance instance) {
      if (instance.isTemporarySkill()) {
         return 0.0;
      } else {
         return instance.getMastery() < 0.0 ? 0.0 : this.getDefaultAcquiringMagiculeCost();
      }
   }

   public double getDefaultAcquiringMagiculeCost() {
      return 0.0;
   }

   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return 0.0;
   }

   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return 0.0;
   }

   public boolean isOutOfEnergy(LivingEntity entity, ManasSkillInstance instance, int mode, float costMultiplier) {
      return this.isOutOfEnergy(
         entity, instance, this.getAuraCost(entity, instance, mode) * costMultiplier, this.getMagiculeCost(entity, instance, mode) * costMultiplier
      );
   }

   public boolean isOutOfEnergy(LivingEntity entity, ManasSkillInstance instance, double apCost, double mpCost) {
      return EnergyHelper.isOutOfEnergy(entity, apCost, mpCost);
   }

   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (entity instanceof ServerPlayer player) {
         this.addLearningStatistic(instance, player);
      }
   }

   protected double getDefaultMasteryIncrement(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return entity.getAttributeValue(TensuraAttributes.ABILITY_MASTERY_GAIN);
   }

   public void addMasteryPoint(ManasSkillInstance instance, LivingEntity entity) {
      this.addMasteryPoint(instance, entity, this.getDefaultMasteryIncrement(instance, entity, 0));
   }

   public void addMasteryPoint(ManasSkillInstance instance, LivingEntity entity, double point) {
      if (!instance.isTemporarySkill()) {
         if (!this.isMastered(instance, entity)) {
            if (instance.getMastery() < 0.0) {
               this.addLearnPoint(instance, entity, 0, this.getDefaultLearningIncrement(instance, entity, 0));
            } else {
               double oldMastery = instance.getMaxMastery();
               Changeable<Double> newMastery = Changeable.of(instance.getMastery() + point);
               EventResult result = ((SkillMasteryEvent)SkillEvents.SKILL_MASTERY.invoker()).master(instance, entity, newMastery);
               if (!result.isFalse()) {
                  double mastery = Math.min((Double)newMastery.get(), this.getMaxMastery());
                  instance.setMastery(mastery);
                  SkillAPI.getSkillsFrom(entity).markDirty();
                  if (this.isMastered(instance, entity)) {
                     instance.onSkillMastered(entity);
                     if (entity instanceof ServerPlayer player) {
                        this.addMasteryStatistic(player);
                        player.displayClientMessage(
                           Component.translatable("tensura.skill.mastery", new Object[]{instance.getChatDisplayName(true)})
                              .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                           false
                        );
                     }
                  } else if (oldMastery > 0.0 && point > 0.0 && entity instanceof Player player) {
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.mastery.point_added", new Object[]{instance.getChatDisplayName(false)})
                           .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
                        true
                     );
                  }
               }
            }
         }
      }
   }

   public void addMasteryStatistic(ServerPlayer player) {
      player.awardStat(TensuraStats.SKILL_MASTERED);
      ((AbilityTrigger)TensuraCriteriaTriggers.SKILL_MASTERED.get()).trigger(player, this);
   }

   public float getLearningCostMultiplier(int mode) {
      return BASE_CONFIG.Learning.learningCostMultiplier;
   }

   public double getLearningPointRequirement(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return BASE_CONFIG.Learning.learningPointRequirement;
   }

   public double getDefaultLearningIncrement(ManasSkillInstance instance, LivingEntity entity, int mode) {
      int min = BASE_CONFIG.Learning.minBonus;
      int max = BASE_CONFIG.Learning.maxBonus;
      int bonus = max <= min ? min : entity.getRandom().nextInt(min, max + 1);
      return entity.getAttributeValue(TensuraAttributes.ABILITY_LEARNING_GAIN) + bonus;
   }

   public boolean addLearnPoint(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return this.addLearnPoint(instance, entity, mode, this.getDefaultLearningIncrement(instance, entity, mode));
   }

   public boolean addLearnPoint(ManasSkillInstance instance, LivingEntity entity, int mode, double point) {
      if (instance.getMastery() >= 0.0) {
         return false;
      }

      if (EnergyHelper.isOutOfEnergy(entity, instance, mode, this.getLearningCostMultiplier(mode))) {
         return false;
      }

      double oldMastery = instance.getMastery();
      Changeable<Double> newPoint = Changeable.of(oldMastery + point);
      EventResult result = ((TensuraSkillEvents.SkillLearningEvent)TensuraSkillEvents.SKILL_LEARNING.invoker()).learn(instance, entity, mode, 0.0, newPoint);
      if (result.isFalse()) {
         return false;
      }

      if (oldMastery < 0.0 && (Double)newPoint.get() >= 0.0) {
         instance.setMastery(Math.min((Double)newPoint.get(), 0.0));
         SkillAPI.getSkillsFrom(entity).markDirty();
         Changeable<MutableComponent> unlockMessage = Changeable.of(
            Component.translatable("tensura.skill.acquire_learning", new Object[]{instance.getChatDisplayName(true)})
               .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
         );
         EventResult unlockResult = ((UnlockSkillEvent)SkillEvents.UNLOCK_SKILL.invoker()).unlockSkill(instance, entity, unlockMessage);
         if (unlockResult.isFalse()) {
            instance.setMastery(oldMastery);
            return false;
         }

         instance.onLearnSkill(entity);
         if (unlockMessage.isPresent()) {
            entity.sendSystemMessage((Component)unlockMessage.get());
         }

         if (entity instanceof ServerPlayer player) {
            player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
         }
      } else {
         instance.setCoolDown(BASE_CONFIG.Learning.learningCooldown, mode);
         instance.setMastery((Double)newPoint.get());
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable("tensura.skill.learn_points.added", new Object[]{instance.getChatDisplayName(false)})
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
               true
            );
         }
      }

      return true;
   }

   public boolean learnMode(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return this.learnMode(instance, entity, mode, this.getDefaultLearningIncrement(instance, entity, mode), true);
   }

   public boolean learnMode(ManasSkillInstance instance, LivingEntity entity, int mode, double point, boolean sendMessage) {
      CompoundTag tag = instance.getOrCreateTag();
      double learnPoint = tag.getDouble(this.getModeId(instance, mode));
      double requirement = this.getLearningPointRequirement(instance, entity, mode);
      if (learnPoint < requirement) {
         Changeable<Double> newPoint = Changeable.of(learnPoint + point);
         EventResult result = ((TensuraSkillEvents.SkillLearningEvent)TensuraSkillEvents.SKILL_LEARNING.invoker())
            .learn(instance, entity, mode, requirement, newPoint);
         if (result.isFalse()) {
            return true;
         }

         tag.putDouble(this.getModeId(instance, mode), (Double)newPoint.get());
         if (entity instanceof Player player) {
            if (tag.getDouble(this.getModeId(instance, mode)) >= requirement) {
               if (sendMessage) {
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.acquire_learning", new Object[]{this.getModeName(instance, mode)})
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                     false
                  );
               }
            } else {
               instance.setCoolDown(BASE_CONFIG.Learning.learningCooldown, mode);
               SkillHelper.applyLearningPenalty(entity);
               if (sendMessage) {
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.learn_points.added", new Object[]{this.getModeName(instance, mode)})
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
                     true
                  );
               }
            }

            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         }

         instance.markDirty();
         return true;
      } else {
         return false;
      }
   }

   public void addLearningStatistic(ManasSkillInstance instance, ServerPlayer player) {
      if (!instance.isTemporarySkill() && !instance.isSubInstance() && !(instance.getMastery() < 0.0)) {
         player.awardStat(TensuraStats.SKILL_LEARNT);
         ((AbilityTrigger)TensuraCriteriaTriggers.SKILL_LEARNT.get()).trigger(player, this);
      }
   }

   public boolean canLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (entity.isSpectator()) {
         return false;
      }

      if (!entity.getActiveEffects().isEmpty()) {
         if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.REST))) {
            return false;
         }

         if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT))) {
            return false;
         }
      }

      return !SkillUtils.shouldCancelInteraction(entity);
   }

   protected boolean isAffectedByStatus(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (entity.getActiveEffects().isEmpty()) {
         return false;
      } else if (this.isAffectedByAbility(instance, entity, mode)) {
         return true;
      } else {
         return !entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.BATS_MODE))
               && !entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT))
            ? false
            : !this.canActivateInRaceLimit(instance, mode);
      }
   }

   protected boolean isAffectedByAbility(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.REST))
         ? true
         : entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SHADOW_STEP)) && !this.canActivateInRaceLimit(instance, mode);
   }

   public boolean canInteractSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (entity.isSpectator() || instance.getMastery() < 0.0) {
         return false;
      } else {
         return !SleepEffect.isForcedSleeping(entity) && !this.isAffectedByStatus(instance, entity, 0)
            ? this.canActivateInArea(instance, entity, -1, false, false)
            : false;
      }
   }

   public boolean canActivateSkill(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (entity.isSpectator() || instance.getMastery() < 0.0) {
         return false;
      }

      if (!SleepEffect.isForcedSleeping(entity) && !this.isAffectedByStatus(instance, entity, mode)) {
         return this.canActivateInArea(instance, entity, mode, true, true);
      }

      if (entity instanceof Player player) {
         player.displayClientMessage(
            Component.translatable("tensura.ability.activation_failed.status", new Object[]{instance.getChatDisplayName(false)}).withStyle(ChatFormatting.RED),
            true
         );
      }

      return false;
   }

   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return false;
   }

   protected boolean canActivateInArea(ManasSkillInstance instance, LivingEntity entity, int mode, boolean worldRestriction, boolean message) {
      Level level = entity.level();
      if (level.isClientSide()) {
         return true;
      }

      MinecraftServer server = level.getServer();
      if (server != null && entity instanceof Player player) {
         ServerLevel overworld = server.overworld();
         if (worldRestriction) {
            BlockPos pos = entity.blockPosition();
            Map<String, WorldRestrictionInstance> restrictions = TensuraStorages.getWorldRestrictionFrom(overworld).getRestrictions();
            if (!restrictions.isEmpty()) {
               for (WorldRestrictionInstance restriction : restrictions.values()) {
                  if (restriction.getDimension().equals(level.dimension()) && restriction.contains(pos) && restriction.isAbilityBanned(this, mode)) {
                     this.restrictUsage(instance, entity, message);
                     return false;
                  }
               }
            }
         }

         UUID playerUUID = player.getUUID();
         Map<String, BossFightInstance> fights = TensuraStorages.getBossFightHolder(overworld).getBossFights();
         if (!fights.isEmpty()) {
            for (BossFightInstance fight : fights.values()) {
               if (fight.getJoinedPlayers().contains(playerUUID) && fight.getBannedAbilities().contains(this)) {
                  this.restrictUsage(instance, entity, message);
                  return false;
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }

   private void restrictUsage(ManasSkillInstance instance, LivingEntity entity, boolean message) {
      if (instance.isToggled()) {
         instance.setToggled(false);
         instance.onToggleOff(entity);
      }

      if (entity instanceof Player player) {
         player.displayClientMessage(
            Component.translatable("tensura.world_restriction.banned", new Object[]{instance.getChatDisplayName(false)}).withStyle(ChatFormatting.RED), true
         );
      }
   }

   public boolean onAbilityEquipped(
      Player player, Changeable<ManasSkillInstance> instance, Changeable<Integer> mode, Changeable<Integer> slot, Changeable<Integer> preset
   ) {
      return true;
   }

   public void onNumberKeyPress(ManasSkillInstance instance, Player player, int keyNumber) {
   }

   public void onSubordinateDeath(ManasSkillInstance instance, LivingEntity owner, LivingEntity subordinate, DamageSource source) {
   }
}
