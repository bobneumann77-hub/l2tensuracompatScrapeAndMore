package io.github.manasmods.tensura.ability.magic.aspectual.enhancement;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ProtectionMagic extends AspectualMagic {
   public static final AspectualMagicConfig.Protection CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Protection;
   public static final ResourceLocation PROTECTION = ResourceLocation.fromNamespaceAndPath("tensura", "protection");

   public ProtectionMagic() {
      super(AspectualMagic.AspectualType.ENHANCEMENT);
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
            MagicCircleVariant.ENHANCEMENT,
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
            entity.swing(InteractionHand.MAIN_HAND, true);
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false);
            if (target != null && entity.isShiftKeyDown()) {
               if (target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.PROTECTION))) {
                  return;
               }

               target.addEffect(
                  new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.PROTECTION),
                     instance.isMastered(entity) ? CONFIG.protectionDurationMastered : CONFIG.protectionDuration,
                     CONFIG.protectionLevel - 1,
                     true,
                     false,
                     true
                  ),
                  entity
               );
               target.level()
                  .playSound(
                     null,
                     target.getX(),
                     target.getY(),
                     target.getZ(),
                     (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            } else {
               if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.PROTECTION))) {
                  return;
               }

               MobEffectInstance protection = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.PROTECTION),
                  instance.isMastered(entity) ? CONFIG.protectionDurationMastered : CONFIG.protectionDuration,
                  CONFIG.protectionLevel - 1,
                  true,
                  false,
                  true
               );
               TensuraMobEffect.addEffect(entity, protection, entity, this);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            }
         }
      }
   }

   @Override
   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      if (!(instance.getMastery() < 0.0) && !instance.isMastered(entity)) {
         MobEffectInstance protection = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.PROTECTION));
         if (protection == null) {
            return false;
         } else {
            return !Objects.equals(protection.tensura$getSource(), entity.getUUID())
               ? false
               : protection.tensura$getSourceAbility() != null && protection.tensura$getSourceAbility().getSkill() == this;
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
