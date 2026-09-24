package io.github.manasmods.tensura.ability.magic.spiritual.darkness;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.field.ShadowBindHands;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ShadowBindMagic extends SpiritualMagic {
   public static final SpiritualMagicConfig.ShadowBind CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).ShadowBind;

   public ShadowBindMagic() {
      super(Element.DARKNESS, SpiritualMagic.SpiritLevel.MEDIUM);
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
            0.5F,
            25,
            MagicCircleVariant.DARK,
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
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
            if (target != null) {
               if (target.onGround()) {
                  if (instance.isMastered(entity) || this.inShadow(target)) {
                     entity.level()
                        .playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.CAST_DARK.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SQUID_INK);
                     if (!SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.DARKNESS_ATTACK_NULLIFICATION.get())) {
                        instance.addMasteryPoint(entity);
                        ShadowBindHands hands = new ShadowBindHands(entity.level(), entity);
                        hands.setTarget(target);
                        hands.setPos(target.position());
                        hands.setSkill(entity, instance, this, mode);
                        float damage = CONFIG.bindDamage;
                        if (instance.isMastered(entity)) {
                           damage += (float)target.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH) * CONFIG.bindDamageMastered;
                        }

                        hands.setDamage(damage);
                        hands.setLife(CONFIG.bindDuration);
                        hands.setSize(target.getBbWidth() / 0.6F);
                        hands.setVisualSize(target.getBbHeight() / 1.8F);
                        int bindLevel = SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.DARKNESS_ATTACK_RESISTANCE.get())
                           ? CONFIG.bindLevelResisted
                           : CONFIG.bindLevel;
                        MobEffectInstance slowness = new MobEffectInstance(
                           TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), 25, bindLevel, false, false, false
                        );
                        hands.setMobEffect(slowness);
                        entity.level().addFreshEntity(hands);
                        hands.triggerAnim("controller", "start");
                        entity.swing(InteractionHand.MAIN_HAND, true);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean inShadow(LivingEntity entity) {
      if (!entity.isAlive()) {
         return false;
      } else if (entity instanceof Player player && player.getAbilities().invulnerable) {
         return false;
      } else {
         return entity.level().isNight()
            ? true
            : entity.getLightLevelDependentMagicValue() < 0.5F || !entity.level().canSeeSky(ObjectSelectionHelper.getBlockPos(entity.getEyePosition()));
      }
   }
}
