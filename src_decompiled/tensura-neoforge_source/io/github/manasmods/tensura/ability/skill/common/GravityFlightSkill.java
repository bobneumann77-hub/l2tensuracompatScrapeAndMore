package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class GravityFlightSkill extends Skill {
   private static final CommonSkillConfig.GravityFlight CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).GravityFlight;

   public GravityFlightSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE))) {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.skill.magic_interference").withStyle(ChatFormatting.RED), true);
         }

         return false;
      } else {
         if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
            instance.addMasteryPoint(entity);
         }

         entity.resetFallDistance();
         this.spawnParticles(entity);
         Vec3 delta = entity.getDeltaMovement();
         double dy = delta.y <= 0.0 ? 0.12F * CONFIG.upMultiplier : delta.y + 0.12F * CONFIG.upMultiplier;
         entity.setDeltaMovement(delta.x(), dy, delta.z());
         entity.hurtMarked = true;
         entity.hasImpulse = true;
         return true;
      }
   }

   private void spawnParticles(LivingEntity entity) {
      for (int i = 0; i < 5; i++) {
         float random = (entity.getRandom().nextFloat() - 0.5F) * 0.1F;
         TensuraParticleHelper.spawnServerParticles(
            entity.level(), ParticleTypes.WHITE_ASH, entity.getRandomX(1.0), entity.getRandomY(), entity.getRandomZ(1.0), 0, random, -0.2, random, 1.0, false
         );
         TensuraParticleHelper.spawnServerParticles(
            entity.level(), ParticleTypes.ASH, entity.getRandomX(1.0), entity.getRandomY(), entity.getRandomZ(1.0), 0, random, -0.2, random, 1.0, false
         );
      }
   }
}
