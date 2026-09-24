package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SnakeEyeSkill extends Skill {
   private static final ExtraSkillConfig.SnakeEye CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).SnakeEye;

   public SnakeEyeSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public int getModes(ManasSkillInstance instance) {
      return 5;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 4 : mode - 1;
      } else {
         return mode == 4 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "snake_eye.corrosion";
         case 1 -> "snake_eye.poison";
         case 2 -> "snake_eye.paralysis";
         case 3 -> "snake_eye.petrification";
         case 4 -> "snake_eye.insanity";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.maxRange, false);
      double ownerEP = EnergyHelper.getMaxEP(entity);
      if (target != null) {
         if (target instanceof Player player && player.getAbilities().invulnerable) {
            return false;
         }

         if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
            instance.addMasteryPoint(entity);
         }

         entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PARROT_IMITATE_SPIDER, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);

         Holder<MobEffect> effect = switch (mode) {
            case 0 -> TensuraMobEffects.getReference(TensuraMobEffects.CORROSION);
            case 1 -> TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON);
            case 2 -> TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS);
            case 3 -> TensuraMobEffects.getReference(TensuraMobEffects.PETRIFICATION);
            default -> TensuraMobEffects.getReference(TensuraMobEffects.INSANITY);
         };
         int duration = 2;
         int level = 0;
         MobEffectInstance effectInstance = target.getEffect(effect);
         if (effectInstance != null) {
            duration = effectInstance.getDuration() + 2;
            level = duration / CONFIG.increaseTick;
            if (EnergyHelper.getMaxEP(target) / ownerEP < 0.5) {
               level *= 2;
            }
         }

         MobEffectInstance mobEffectInstance = new MobEffectInstance(effect, duration, level, true, false, true);
         TensuraMobEffect.addEffect(target, mobEffectInstance, entity, this, mode);
      }

      return true;
   }
}
