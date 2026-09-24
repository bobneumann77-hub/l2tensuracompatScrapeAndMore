package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.IAbilityCreator;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.attribute.TensuraGlobalAttributeIds;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.AttributeHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

public class CreatorSkill extends Skill implements IAbilityCreator {
   private static final UniqueSkillConfig.Creator CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Creator;

   public CreatorSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return mode == 1;
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
         case 0 -> "creator.analytical_appraisal";
         case 1 -> "creator.skill_creation";
         default -> super.getModeId(instance, mode);
      };
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
            if (entity instanceof ServerPlayer serverPlayer) {
               List<ResourceLocation> list = CONFIG.uniqueSkills
                  .stream()
                  .<ResourceLocation>map(ResourceLocation::parse)
                  .filter(location -> this.canCreateSkill(location, serverPlayer, instance))
                  .toList();
               this.openSkillCreationMenu(instance, serverPlayer, mode, list);
            }
      }
   }

   @Override
   public int getCreationCooldown(ManasSkillInstance instance, Player player) {
      return CONFIG.creationCooldown;
   }

   @Override
   public int getCreatedSkillTimer(ManasSkillInstance instance, Player player) {
      return CONFIG.creationVanishTimer;
   }

   @Override
   public void onGainingCreatingMastery(ManasSkillInstance instance, Player player) {
      this.addMasteryPoint(instance, player, this.getDefaultMasteryIncrement(instance, player, 0) * CONFIG.masteryGainMultiplier);
   }

   @Override
   public void onCreateAbility(ManasSkillInstance creator, Player player, ManasSkillInstance created) {
      if (creator.isMastered(player)) {
         if (created.getSkill().equals(UniqueSkills.ANTI_SKILL.get())) {
            ((AntiSkill)UniqueSkills.ANTI_SKILL.get()).addMasteryPoint(created, player, created.getMaxMastery());
         }
      }
   }

   @Override
   public boolean allowToCreateDuplicateAbility(ManasSkillInstance creator, Player player, ManasSkill created) {
      return creator.isMastered(player);
   }

   private boolean canCreateSkill(ResourceLocation location, ServerPlayer serverPlayer, ManasSkillInstance instance) {
      ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(location);
      if (skill == null) {
         return false;
      }

      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(serverPlayer).getSkill(skill);
      if (instance.isMastered(serverPlayer)) {
         return optional.<Boolean>map(ManasSkillInstance::isTemporarySkill).orElse(true);
      }

      CompoundTag tag = instance.getTag();
      if (tag != null && tag.contains("CreatedSkill")) {
         ResourceLocation created = ResourceLocation.parse(tag.getString("CreatedSkill"));
         if (created.equals(location)) {
            return false;
         }
      }

      return optional.isEmpty();
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
