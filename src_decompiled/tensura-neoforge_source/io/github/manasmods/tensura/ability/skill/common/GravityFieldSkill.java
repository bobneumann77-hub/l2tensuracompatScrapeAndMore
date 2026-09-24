package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.entity.magic.field.GravityField;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class GravityFieldSkill extends Skill {
   private static final CommonSkillConfig.GravityField CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).GravityField;

   public GravityFieldSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return switch (mode) {
            case 0 -> instance.isMastered(entity) ? 2 : 1;
            case 1 -> 0;
            case 2 -> 1;
            default -> -1;
         };
      } else {
         return switch (mode) {
            case 0 -> 1;
            case 1 -> instance.isMastered(entity) ? 2 : 0;
            default -> 0;
         };
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "gravity_field.self";
         case 1 -> "gravity_field.5";
         case 2 -> "gravity_field.10";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.isShiftKeyDown()) {
         if (mode == 0) {
            entity.removeEffect(MobEffects.MOVEMENT_SPEED);
            entity.removeEffect(MobEffects.SLOW_FALLING);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                  TensuraSkill.ABILITY_SOUND,
                  0.5F,
                  0.5F
               );
         } else {
            GravityField field = ObjectSelectionHelper.getTargetingEntity(GravityField.class, entity, 15.0, 0.2, false, false, false);
            if (field != null && entity == field.getOwner()) {
               field.remove();
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.5F,
                     0.5F
                  );
            }
         }
      } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         instance.addMasteryPoint(entity);
         instance.setCoolDown(CONFIG.cooldown, mode);
         int fieldLife = instance.isMastered(entity) ? CONFIG.fieldDuration * 2 : CONFIG.fieldDuration;
         switch (mode) {
            case 0:
               entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, fieldLife, CONFIG.speedLevel - 1, false, true), entity);
               entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, fieldLife, CONFIG.slowFallLevel - 1, false, true), entity);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.5F,
                     0.5F
                  );
               break;
            case 1: {
               GravityField field = new GravityField(entity.level(), entity);
               field.setLife(fieldLife);
               field.setSize(CONFIG.fieldRadius);
               field.setSkill(entity, instance, this, mode);
               field.setPos(entity.position().add(0.0, -1 * CONFIG.fieldRadius, 0.0));
               entity.level().addFreshEntity(field);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.5F,
                     0.5F
                  );
               break;
            }
            case 2: {
               GravityField field = new GravityField(entity.level(), entity);
               field.setLife(fieldLife);
               field.setSize(CONFIG.fieldRadiusMastered);
               field.setSkill(entity, instance, this, mode);
               field.setPos(entity.position().add(0.0, -1 * CONFIG.fieldRadiusMastered, 0.0));
               entity.level().addFreshEntity(field);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.5F,
                     0.5F
                  );
            }
         }
      }
   }
}
