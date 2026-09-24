package io.github.manasmods.tensura.ability.magic.aspectual.illusion;

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
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ConfusionMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Confusion CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Confusion;

   public ConfusionMagic() {
      super(AspectualMagic.AspectualType.ILLUSION);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            entity.getBbWidth() * 3.0F,
            25,
            MagicCircleVariant.ILLUSION,
            true,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            Level level = entity.level();
            instance.addMasteryPoint(entity);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_DARK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
            TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(1.0F, (float)size, -0.3F), 3, 0.01);
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getBlackWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.5,
               entity.getZ()
            );
            double radius = CONFIG.radius;
            List<LivingEntity> list = level.getEntitiesOfClass(
               LivingEntity.class, entity.getBoundingBox().inflate(radius), living -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity)
            );
            if (!list.isEmpty()) {
               double ownerEP = EnergyHelper.getMaxEP(entity);
               int duration = instance.isMastered(entity) ? CONFIG.confusionDurationMastered : CONFIG.confusionDuration;
               AttributeInstance illusionBoost = entity.getAttribute(TensuraAttributes.ILLUSION_BOOST);
               if (illusionBoost != null) {
                  duration = (int)(duration * illusionBoost.getValue());
               }

               for (LivingEntity target : list) {
                  if (!(target instanceof Player player && player.getAbilities().invulnerable)) {
                     double targetEP = EnergyHelper.getMaxEP(target);
                     double difference = targetEP / ownerEP;
                     if (!(difference > CONFIG.bypassEP)) {
                        int maxLevel = instance.isMastered(entity) ? CONFIG.confusionMaxLevelMastered : CONFIG.confusionMaxLevel;
                        int confusion = Math.clamp((int)((4.0 - Math.floor(difference * 4.0)) * CONFIG.confusionLevel), 0, maxLevel - 1);
                        target.addEffect(
                           new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CONFUSION), duration, confusion, true, false, true), entity
                        );
                     }
                  }
               }
            }
         }
      }
   }
}
