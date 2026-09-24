package io.github.manasmods.tensura.ability.magic.aspectual.recovery;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class HealingMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Healing CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Healing;

   public HealingMagic() {
      super(AspectualMagic.AspectualType.RECOVERY);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.minCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            0.5F,
            25,
            MagicCircleVariant.RECOVERY,
            entity,
            instance.getOrCreateTag(),
            0.75F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false);
         if (target != null) {
            if (target.getHealth() < target.getMaxHealth()) {
               instance.addMasteryPoint(entity);
               float heal = instance.isMastered(entity) ? Math.max(CONFIG.hpHealMastered, CONFIG.hpHealPercentage * target.getMaxHealth()) : CONFIG.hpHeal;
               float healingHP = Math.min(target.getMaxHealth() - target.getHealth(), heal);
               double cost = Math.max(healingHP * CONFIG.magiculeCost, this.getMagiculeCost(entity, instance, mode));
               double lackedMana = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)cost);
               if (lackedMana > 0.0) {
                  healingHP = (float)(healingHP - lackedMana / cost);
               }

               if (healingHP < 0.0F) {
                  return;
               }

               target.heal(healingHP);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getLightGreenWave(0.9F, target.getBbWidth() * 3.0F, -0.5F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.33,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getLightGreenWave(0.9F, target.getBbWidth() * 3.0F, -0.5F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.66,
                  target.getZ()
               );
            }
         } else if (entity.getHealth() < entity.getMaxHealth()) {
            instance.addMasteryPoint(entity);
            float heal = instance.isMastered(entity) ? Math.max(CONFIG.hpHealMastered, CONFIG.hpHealPercentage * entity.getMaxHealth()) : CONFIG.hpHeal;
            float healingHP = Math.min(entity.getMaxHealth() - entity.getHealth(), heal);
            double cost = Math.max(healingHP * CONFIG.magiculeCost, this.getMagiculeCost(entity, instance, mode));
            double lackedMana = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)cost);
            if (lackedMana > 0.0) {
               healingHP = (float)(healingHP - lackedMana / cost);
            }

            if (healingHP < 0.0F) {
               return;
            }

            entity.heal(healingHP);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getLightGreenWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.33,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getLightGreenWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.66,
               entity.getZ()
            );
         }
      }
   }
}
