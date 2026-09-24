package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VoiceCannonSkill extends Skill {
   private static final CommonSkillConfig.VoiceCannon CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).VoiceCannon;

   public VoiceCannonSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, (ManasSkill)CommonSkills.COERCION.get()) ? false : newEP > CONFIG.epAcquirement;
   }

   @Override
   public boolean canActivateSkill(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (entity.getAirSupply() >= entity.getMaxAirSupply() && !entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SILENCE))) {
         return super.canActivateSkill(instance, entity, mode);
      }

      entity.sendSystemMessage(
         Component.translatable("tensura.ability.activation_failed.status", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
      );
      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
      return false;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         Level level = entity.level();
         instance.addMasteryPoint(entity);
         instance.setCoolDown(CONFIG.cannonCooldown, mode);
         double range = instance.isMastered(entity) ? CONFIG.cannonRangeMastered : CONFIG.cannonRange;
         Vec3 target = entity.position().add(entity.getLookAngle().scale(range));
         Vec3 source = entity.position().add(0.0, entity.getEyeHeight(), 0.0);
         Vec3 sourceToTarget = target.subtract(source);
         Vec3 normalizes = sourceToTarget.normalize();
         level.playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.COERCION.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F
         );
         float damage = instance.isMastered(entity) ? CONFIG.cannonDamageMastered : CONFIG.cannonDamage;

         for (int particleIndex = 1; particleIndex < Mth.floor(sourceToTarget.length()); particleIndex++) {
            Vec3 particlePos = source.add(normalizes.scale(particleIndex));
            TensuraParticleHelper.spawnServerParticles(level, TensuraParticleUtils.getColorlessSonic(0.75F, 1.5F), particlePos.x, particlePos.y, particlePos.z);
            AABB aabb = new AABB(new BlockPos((int)particlePos.x, (int)particlePos.y, (int)particlePos.z)).inflate(2.0);
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, entityData -> !entityData.is(entity) && !entityData.isAlliedTo(entity));
            if (!list.isEmpty()) {
               for (LivingEntity living : list) {
                  if (!RaceUtils.isSpiritual(living)) {
                     DamageSource damageSource = this.createSource(instance, entity, TensuraDamageTypes.SOUND_BLAST, mode);
                     if (living.hurt(damageSource, damage)) {
                        EffectStorage.setCameraShake(living, 0.01F, 7);
                     }
                  }
               }
            }
         }
      }
   }
}
