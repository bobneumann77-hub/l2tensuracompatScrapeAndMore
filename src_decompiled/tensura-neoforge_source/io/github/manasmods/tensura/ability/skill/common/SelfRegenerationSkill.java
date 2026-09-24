package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SelfRegenerationSkill extends Skill {
   public static final CommonSkillConfig.SelfRegeneration CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).SelfRegeneration;

   public SelfRegenerationSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      if (entity instanceof ServerPlayer player) {
         int slime = player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType)MonsterEntityTypes.SLIME.get()));
         int metal = player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType)MonsterEntityTypes.METAL_SLIME.get()));
         int supermassive = player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType)MonsterEntityTypes.SUPERMASSIVE_SLIME.get()));
         return metal + slime + supermassive >= CONFIG.slimeAcquirement;
      } else {
         return false;
      }
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return true;
   }

   @Override
   public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() < 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      double maxHealth = EffectStorage.getSeveranceMaxHealth(entity);
      if (entity instanceof Player) {
         if (entity.getHealth() >= maxHealth) {
            return;
         }

         if (EnergyHelper.isOutOfEnergy(entity, instance, 0)) {
            entity.sendSystemMessage(
               Component.translatable("tensura.skill.lack_magicule.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
            );
            instance.setToggled(false);
            instance.onToggleOff(entity);
            instance.markDirty();
            return;
         }
      }

      entity.addEffect(
         new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.SELF_REGENERATION),
            240,
            instance.isMastered(entity) ? CONFIG.regenLevelMastered - 1 : CONFIG.regenLevel - 1,
            false,
            false,
            false
         )
      );
      if (entity.getHealth() < maxHealth) {
         CompoundTag tag = instance.getOrCreateTag();
         int time = tag.getInt("activatedTimes");
         if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
            instance.addMasteryPoint(entity);
         }

         tag.putInt("activatedTimes", time + 1);
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      double maxHealth = EffectStorage.getSeveranceMaxHealth(entity);
      if (!(entity.getHealth() < maxHealth) || !EnergyHelper.isOutOfEnergy(entity, instance, 0)) {
         entity.addEffect(
            new MobEffectInstance(
               TensuraMobEffects.getReference(TensuraMobEffects.SELF_REGENERATION), 250, instance.isMastered(entity) ? 1 : 0, false, false, false
            )
         );
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.SELF_REGENERATION));
   }
}
