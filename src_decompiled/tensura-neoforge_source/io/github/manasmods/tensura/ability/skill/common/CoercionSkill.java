package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CoercionSkill extends Skill {
   public static final CommonSkillConfig.Coercion CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).Coercion;

   public CoercionSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement;
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         Level level = entity.level();
         Vec3 targetPos = entity.getEyePosition().add(entity.getLookAngle().scale(CONFIG.roarRange));
         Vec3 source = entity.getEyePosition().add(entity.getLookAngle().scale(2.0));
         Vec3 offSetToTarget = targetPos.subtract(source);
         Vec3 normalizes = offSetToTarget.normalize();
         double scale = instance.getTag() != null && instance.getTag().contains("scale") ? Math.min(instance.getTag().getDouble("scale"), 1.0) : 1.0;
         double ownerEP = EnergyHelper.getMaxEP(entity) * scale;
         entity.level()
            .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.COERCION.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         boolean success = false;

         for (int particleIndex = 1; particleIndex < Mth.floor(offSetToTarget.length()); particleIndex++) {
            Vec3 particlePos = source.add(normalizes.scale(particleIndex));
            TensuraParticleHelper.spawnServerParticles(level, TensuraParticleUtils.getColorlessSonic(0.85F, 2.5F), particlePos.x, particlePos.y, particlePos.z);
            AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos)).inflate(4.0);
            List<LivingEntity> livingEntityList = level.getEntitiesOfClass(
               LivingEntity.class, aabb, entityData -> !entityData.is(entity) && !entityData.isAlliedTo(entity)
            );
            if (!livingEntityList.isEmpty()) {
               success = true;

               for (LivingEntity target : livingEntityList) {
                  double targetEP = EnergyHelper.getMaxEP(target);
                  double difference = ownerEP / targetEP;
                  if (!(difference <= 2.0)) {
                     EffectStorage.setCameraShake(target, 0.01F, 10);
                     int fearLevel = (int)(CONFIG.epDifferenceMultiplier * (difference - 2.0));
                     fearLevel = Math.min(fearLevel, TensuraMobEffect.CONFIG.maxFear);
                     TensuraMobEffect.addEffect(
                        target,
                        TensuraMobEffects.getReference(TensuraMobEffects.FEAR),
                        CONFIG.fearDuration,
                        fearLevel,
                        true,
                        false,
                        true,
                        entity.getUUID(),
                        this,
                        mode
                     );
                     if (target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, CONFIG.fearDuration, fearLevel, false, false), entity)) {
                        HakiSkill.hakiPush(target, entity, instance, fearLevel);
                     }
                  }
               }
            }
         }

         if (success) {
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldown / 2 : CONFIG.cooldown, mode);
         }
      }
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      HakiSkill.changeEPUsed(instance, entity, delta);
   }
}
