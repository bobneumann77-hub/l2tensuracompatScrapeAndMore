package io.github.manasmods.tensura.ability.magic.spiritual.earth;

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
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class EarthJailMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.EarthJail CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).EarthJail;

   public EarthJailMagic() {
      super(Element.EARTH, SpiritualMagic.SpiritLevel.GREATER);
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
            0.75F,
            25,
            MagicCircleVariant.EARTH,
            entity,
            instance.getOrCreateTag(),
            1.0F,
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
            double radius = instance.isMastered(entity) ? CONFIG.rangeMastered : CONFIG.range;
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, radius, false, true);
            if (target != null) {
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               TensuraParticleHelper.addServerParticlesAroundSelf(target, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD_BRICKS.defaultBlockState()));
               TensuraParticleHelper.addServerParticlesAroundSelf(target, TensuraParticleUtils.getBogEffect());
               DamageSource damageSource = this.createSource(instance, entity, TensuraDamageTypes.EARTH_ELEMENTAL, mode);
               if (target.hurt(damageSource, CONFIG.jailDamage)) {
                  if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.EARTH_ATTACK_NULLIFICATION.get())) {
                     return;
                  }

                  instance.addMasteryPoint(entity);
                  instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
                  int slowness = CONFIG.jailSpeed - 1;
                  if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.EARTH_ATTACK_RESISTANCE.get())) {
                     slowness -= CONFIG.jailSpeedResisted;
                  }

                  target.addEffect(
                     new MobEffectInstance(
                        TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), CONFIG.jailDuration, slowness, false, false, false
                     )
                  );
                  if (instance.isMastered(entity)) {
                     target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, CONFIG.jailDuration, CONFIG.jailFatigue, true, false, true));
                  }

                  target.addEffect(
                     new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.BURDEN), CONFIG.jailDuration, CONFIG.jailBurden, true, false, true)
                  );
                  target.addEffect(
                     new MobEffectInstance(
                        TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), CONFIG.jailDuration, CONFIG.jailFragility, true, false, true
                     )
                  );
               }
            }
         }
      }
   }
}
