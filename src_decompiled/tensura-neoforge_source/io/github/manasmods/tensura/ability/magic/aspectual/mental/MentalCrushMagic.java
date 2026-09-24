package io.github.manasmods.tensura.ability.magic.aspectual.mental;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class MentalCrushMagic extends AspectualMagic {
   public static final AspectualMagicConfig.MentalCrush CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).MentalCrush;

   public MentalCrushMagic() {
      super(AspectualMagic.AspectualType.MENTAL);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryHigh;
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
            0.5F,
            25,
            MagicCircleVariant.MENTAL,
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
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
         if (target == null) {
            entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  0.5F,
                  0.5F
               );
         } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
               entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.5F,
                     0.5F
                  );
            } else {
               IExistence existence = TensuraStorages.getExistenceFrom(target);
               double maxSHP = target.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
               double shp = existence.getSpiritualHealth();
               boolean weakened = shp < maxSHP * CONFIG.weakenedMultiplier;
               double damage;
               if (instance.isMastered(entity) && weakened) {
                  if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
                     damage = Math.max(maxSHP * CONFIG.shpMultiplierWeakenedResist, CONFIG.minSHPResisted);
                  } else {
                     damage = Math.max(maxSHP * CONFIG.shpMultiplierWeakened, CONFIG.minSHP);
                  }
               } else if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
                  damage = Math.max(maxSHP * CONFIG.shpMultiplierResist, CONFIG.minSHPResisted);
               } else {
                  damage = Math.max(maxSHP * CONFIG.shpMultiplier, CONFIG.minSHP);
               }

               DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.MIND_CRUSH, mode);
               TensuraDamageHelper.directSpiritualHurt(target, entity, source, (float)damage, 0.0F);
               target.addEffect(
                  new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), CONFIG.fragilityDuration, CONFIG.fragilityLevel - 1, true, false, true
                  ),
                  entity
               );
               target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FLASHED_BLINDNESS), 2, 1, false, false, false), entity);
               if (entity.getRandom().nextFloat() <= (weakened ? CONFIG.insanityChanceWeakened : CONFIG.insanityChance)) {
                  int insanityLevel = entity.getRandom().nextInt(CONFIG.insanityLevel);
                  MobEffectInstance mobEffect = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.INSANITY), CONFIG.insanityDuration, insanityLevel, true, false, true
                  );
                  TensuraMobEffect.addEffect(target, mobEffect, entity, this, mode);
               }

               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_DARK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ELECTRIC_SPARK);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.FLASH);
               entity.swing(InteractionHand.MAIN_HAND, true);
               instance.addMasteryPoint(entity);
               instance.setCoolDown(CONFIG.cooldown, mode);
            }
         }
      }
   }
}
