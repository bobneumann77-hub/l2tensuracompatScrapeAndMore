package io.github.manasmods.tensura.ability.magic.aspectual.illusion;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Objects;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class InvisibleMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Invisible CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Invisible;

   public InvisibleMagic() {
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
            entity.getBbWidth() * 2.0F,
            25,
            MagicCircleVariant.ILLUSION,
            true,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            new Vec3(0.0, entity.getBbHeight() / 2.0F, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            if (instance.isMastered(entity)) {
               int duration = CONFIG.concealmentDuration;
               AttributeInstance illusionBoost = entity.getAttribute(TensuraAttributes.ILLUSION_BOOST);
               if (illusionBoost != null) {
                  duration = (int)(duration * illusionBoost.getValue());
               }

               entity.addEffect(
                  new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT), duration, CONFIG.concealmentLevel - 1, false, false, false
                  ),
                  entity
               );
            } else {
               int duration = CONFIG.invisibleDuration;
               AttributeInstance illusionBoost = entity.getAttribute(TensuraAttributes.ILLUSION_BOOST);
               if (illusionBoost != null) {
                  duration = (int)(duration * illusionBoost.getValue());
               }

               MobEffectInstance invisibility = new MobEffectInstance(MobEffects.INVISIBILITY, duration, CONFIG.invisibleLevel - 1, true, false, true);
               TensuraMobEffect.addEffect(entity, invisibility, entity, this);
            }

            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      }
   }

   @Override
   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      if (!(instance.getMastery() < 0.0) && !instance.isMastered(entity)) {
         MobEffectInstance invisibility = entity.getEffect(MobEffects.INVISIBILITY);
         if (invisibility == null) {
            return false;
         } else {
            return !Objects.equals(invisibility.tensura$getSource(), entity.getUUID())
               ? false
               : invisibility.tensura$getSourceAbility() != null && invisibility.tensura$getSourceAbility().getSkill() == this;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      instance.addMasteryPoint(entity);
   }
}
