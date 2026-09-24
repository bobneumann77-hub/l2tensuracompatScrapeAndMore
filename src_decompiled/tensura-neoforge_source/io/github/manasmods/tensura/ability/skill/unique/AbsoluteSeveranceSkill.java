package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.entity.projectile.magic.SeveranceCutterProjectile;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class AbsoluteSeveranceSkill extends Skill {
   private static final UniqueSkillConfig.AbsoluteSeverance CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).AbsoluteSeverance;

   public AbsoluteSeveranceSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "absolute_severance.coat";
         case 1 -> "absolute_severance.projectile";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCostCoating;
         case 1 -> CONFIG.magiculeCostProjectile;
         default -> 0.0;
      };
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.SEVERANCE_BLADE));
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         switch (mode) {
            case 0:
               if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SEVERANCE_BLADE))) {
                  entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.SEVERANCE_BLADE));
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               } else {
                  int severancex = instance.isMastered(entity) ? CONFIG.coatingLevelMastered - 1 : CONFIG.coatingLevel - 1;
                  entity.addEffect(
                     new MobEffectInstance(
                        TensuraMobEffects.getReference(TensuraMobEffects.SEVERANCE_BLADE), CONFIG.coatingDuration, severancex, true, false, true
                     )
                  );
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
               break;
            case 1:
               SeveranceCutterProjectile severance = new SeveranceCutterProjectile(entity.level(), entity);
               severance.setDelayVec(entity.getLookAngle().normalize().scale(2.5));
               severance.setDelayTick(3);
               severance.setSpeed(2.5F);
               severance.setDamage(instance.isMastered(entity) ? CONFIG.projectileDamageMastered : CONFIG.projectileDamage);
               severance.setSize(instance.isMastered(entity) ? CONFIG.projectileSizeMastered : CONFIG.projectileSize);
               severance.setSkill(entity, instance, this, mode);
               severance.setLife(instance.isMastered(entity) ? CONFIG.projectileDurationMastered : CONFIG.projectileDuration);
               severance.setNoGravity(true);
               severance.setPos(
                  entity.position().add(entity.getLookAngle().scale(2.0)).add(0.0, entity.getEyeHeight() - severance.getBoundingBox().getYsize() * 0.5, 0.0)
               );
               severance.updateShootRotation();
               entity.level().addFreshEntity(severance);
               instance.addMasteryPoint(entity);
               instance.setCoolDown(CONFIG.projectileCooldown, mode);
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
         }
      }
   }
}
