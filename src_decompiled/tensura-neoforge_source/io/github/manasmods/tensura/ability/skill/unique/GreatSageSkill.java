package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.ThoughtAccelerationSkill;
import io.github.manasmods.tensura.ability.subclass.IRefining;
import io.github.manasmods.tensura.ability.subclass.IRepeatCrafting;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.attribute.TensuraGlobalAttributeIds;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class GreatSageSkill extends Skill implements IRefining<GreatSageSkill>, IRepeatCrafting<GreatSageSkill> {
   public static final UniqueSkillConfig.GreatSage CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).GreatSage;
   protected static final ResourceLocation GREAT_SAGE = ResourceLocation.fromNamespaceAndPath("tensura", "great_sage");

   public GreatSageSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return true;
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return mode == 0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "great_sage.analytical_appraisal";
         case 1 -> "great_sage.analysis";
         case 2 -> "great_sage.refine";
         default -> super.getModeId(instance, mode);
      };
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, GREAT_SAGE, true);
      AttributeInstance learning = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
      if (learning != null) {
         learning.addOrReplacePermanentModifier(new AttributeModifier(GREAT_SAGE, CONFIG.learningPoint, Operation.ADD_VALUE));
      }

      AttributeInstance mastery = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (mastery != null) {
         mastery.addOrReplacePermanentModifier(new AttributeModifier(GREAT_SAGE, CONFIG.masteryPoint, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeChantSpeed(entity, CONFIG.chantSpeed);
      ThoughtAccelerationSkill.onToggle(instance, entity, GREAT_SAGE, false);
      AttributeInstance learning = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
      if (learning != null) {
         learning.removeModifier(GREAT_SAGE);
      }

      AttributeInstance mastery = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (mastery != null) {
         mastery.removeModifier(GREAT_SAGE);
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      switch (mode) {
         case 0:
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
                        Component.translatable("tensura.skill.analytical.analyzing_mode.entity").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)),
                        true
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
            break;
         case 1:
            TensuraProjectile projectile = ObjectSelectionHelper.getTargetingEntity(
               TensuraProjectile.class, entity, CONFIG.copyRangeMagic, 0.5, true, true, false
            );
            if (projectile != null && projectile.isAlive()) {
               ManasSkillInstance targetInstance = projectile.getSkill();
               if (targetInstance != null && targetInstance.getMastery() >= 0.0 && targetInstance.is(TensuraSkillTags.COPIABLE_MAGIC)) {
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  Changeable<ManasSkill> changeable = Changeable.of(targetInstance.getSkill());
                  if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker())
                     .plunder(projectile.getOwner(), entity, false, changeable)
                     .isFalse()) {
                     instance.addMasteryPoint(entity);
                     if (SkillHelper.learnSkill(entity, (ManasSkill)changeable.get(), instance.getRemoveTime())) {
                        instance.setCoolDown(CONFIG.copyCooldownSuccess, mode);
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
                        instance.setCoolDown(CONFIG.copyCooldownFail, mode);
                     }
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
                  instance.setCoolDown(CONFIG.copyCooldownFail, mode);
               }

               return;
            }

            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.copyRange, false);
            if (target != null && target.isAlive()) {
               if (target instanceof Player player && player.getAbilities().invulnerable) {
                  entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
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
                  return;
               }

               entity.swing(InteractionHand.MAIN_HAND, true);
               ServerLevel level = (ServerLevel)entity.level();
               double chance = instance.isMastered(entity) ? CONFIG.copyChanceMastered : CONFIG.copyChance;
               boolean failed = true;
               if (entity.getRandom().nextInt(100) <= chance) {
                  List<ManasSkillInstance> collection = SkillAPI.getSkillsFrom(target).getLearnedSkills().stream().filter(this::canCopy).toList();
                  if (!collection.isEmpty()) {
                     ManasSkill skill = collection.get(target.getRandom().nextInt(collection.size())).getSkill();
                     Changeable<ManasSkill> changeable = Changeable.of(skill);
                     if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker())
                        .plunder(target, entity, false, changeable)
                        .isFalse()) {
                        instance.addMasteryPoint(entity);
                        if (SkillHelper.learnSkill(entity, (ManasSkill)changeable.get(), instance.getRemoveTime())) {
                           instance.setCoolDown(CONFIG.copyCooldownSuccess, mode);
                           failed = false;
                           level.playSound(
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
                              Component.translatable("tensura.ability.activation_failed.plunder", new Object[]{skill.getChatDisplayName(true)})
                                 .withStyle(ChatFormatting.RED)
                           );
                        }
                     }
                  } else {
                     entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.plunder.empty").withStyle(ChatFormatting.RED));
                  }
               } else {
                  entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
               }

               if (failed) {
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
                  instance.setCoolDown(CONFIG.copyCooldownFail, mode);
               }
            } else {
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
            }
            break;
         case 2:
            if (entity.isShiftKeyDown()) {
               this.openRefiningMenu(entity, instance);
            } else {
               this.openRepeatCraftingMenu(entity, instance);
            }
      }
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (instance.isToggled()) {
         this.gainMastery(instance, entity);
      }

      if (entity instanceof Player player) {
         if (this.onRepeatCraftingTick(instance, player)) {
            this.gainMastery(instance, entity);
         }

         if (this.onRefiningTick(instance, player)) {
            this.gainMastery(instance, entity);
         }
      }
   }

   @Override
   public int getSpatialStorageIdOffset() {
      return 11;
   }

   @Override
   public boolean isAutoRefiningAllowed() {
      return true;
   }

   @Override
   public boolean hasAutoCraftingTab() {
      return true;
   }

   @NotNull
   @Override
   public SpatialStorageContainer getSpatialStorage(ManasSkillInstance instance, Provider provide) {
      SpatialStorageContainer container = new SpatialStorageContainer(20, 128);
      container.fromTag(instance.getOrCreateTag().getList("SpatialStorage", 10), provide);
      return container;
   }

   @Override
   public void openSpatialStoragePage(ServerPlayer player, LivingEntity owner, ManasSkillInstance instance, int page) {
      this.openRepeatCraftingMenu(player, owner, instance);
   }

   private void gainMastery(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(entity);
      }

      tag.putInt("activatedTimes", time + 1);
   }

   public boolean canCopy(ManasSkillInstance instance) {
      if (instance.isTemporarySkill() || instance.getMastery() < 0.0) {
         return false;
      } else if (instance.is(TensuraSkillTags.NO_PLUNDERING)) {
         return false;
      } else if (instance.is(TensuraSkillTags.INTRINSIC_SKILLS)) {
         return true;
      } else {
         return instance.is(TensuraSkillTags.COMMON_SKILLS) ? true : instance.is(TensuraSkillTags.EXTRA_SKILLS);
      }
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
