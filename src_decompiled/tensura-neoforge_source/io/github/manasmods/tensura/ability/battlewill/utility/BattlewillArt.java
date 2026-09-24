package io.github.manasmods.tensura.ability.battlewill.utility;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BattlewillArt extends Battlewill {
   private static final BattlewillConfig.Battlewill CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).Battlewill;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks > 0 && heldTicks % 30 == 0) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         double maxAP = EnergyHelper.getMaxAura(entity);
         if (existence.getAura() >= maxAP) {
            return false;
         }

         double maxMP = EnergyHelper.getMaxMagicule(entity);
         double convert = instance.isMastered(entity) ? maxMP * CONFIG.percentage / 50.0 : maxMP * CONFIG.percentage / 100.0;
         if (existence.getMagicule() < convert) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.lack_magicule").withStyle(ChatFormatting.RED), true);
            }

            return false;
         }

         existence.setMagicule(existence.getMagicule() - convert);
         EnergyHelper.gainAura(entity, convert, EnergyHelper.GainType.NORMAL);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
            );
         if (existence.getAura() >= maxAP) {
            return false;
         }
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.ELECTRIC_SPARK, 1.0);
      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
         );
      return true;
   }
}
