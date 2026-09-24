package io.github.manasmods.tensura.ability.magic.spiritual.light;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SolarFlareMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.SolarFlare CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).SolarFlare;

   public SolarFlareMagic() {
      super(Element.LIGHT, SpiritualMagic.SpiritLevel.GREATER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
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
            (float)CONFIG.radius / 2.0F,
            25,
            MagicCircleVariant.LIGHT,
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
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_LIGHT.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get());
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get(), 4.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get(), 8.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get(), 12.0);
            List<LivingEntity> list = entity.level()
               .getEntitiesOfClass(
                  LivingEntity.class,
                  entity.getBoundingBox().inflate(CONFIG.radius),
                  living -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity)
               );
            if (!list.isEmpty()) {
               int duration = instance.isMastered(entity) ? CONFIG.flareDurationMastered : CONFIG.flareDuration;
               DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.LIGHT_ELEMENTAL, mode);

               for (LivingEntity target : list) {
                  if (!(target instanceof Player player && player.getAbilities().invulnerable)) {
                     target.hurt(source, CONFIG.flareDamage);
                     if (!SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.ABNORMAL_CONDITION_NULLIFICATION.get())) {
                        target.addEffect(
                           new MobEffectInstance(
                              TensuraMobEffects.getReference(TensuraMobEffects.FLASHED_BLINDNESS), CONFIG.flareBlindness - 1, duration, false, false, false
                           )
                        );
                        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, CONFIG.flareNausea - 1, duration, false, false, false));
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, CONFIG.flareSlowness - 1, duration, false, false, false));
                     }
                  }
               }
            }
         }
      }
   }
}
