package io.github.manasmods.tensura.ability.battlewill.melee;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.battlewill.MeleeArts;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HeavySlashArt extends Battlewill {
   private static final BattlewillConfig.HeavySlash CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).HeavySlash;

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return SkillUtils.isSkillMastered(entity, (ManasSkill)MeleeArts.AURA_SLASH.get());
   }

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(
            entity, instance.isMastered(entity) ? CONFIG.maxDistance * 2.0 : CONFIG.maxDistance, false
         );
         Level level = entity.level();
         entity.swing(InteractionHand.MAIN_HAND, true);
         double attack = entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
         if (target != null) {
            DamageSource source = this.createSource(instance, entity, DamageTypes.PLAYER_ATTACK, mode);
            if (target.hurt(source, (float)(attack * CONFIG.meleeDamageMultiplier))) {
               SkillHelper.knockBack(entity, target, 2.0F);
               instance.addMasteryPoint(entity);
               level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }
         } else {
            instance.addMasteryPoint(entity);
            this.slash(entity, level, 3.0F, instance, mode);
         }
      }
   }

   private void slash(LivingEntity entity, Level level, float distance, ManasSkillInstance instance, int mode) {
      Vec3 target = entity.position().add(entity.getLookAngle().scale(distance));
      Vec3 source = entity.position().add(0.0, entity.getEyeHeight(), 0.0);
      Vec3 sourceToTarget = target.subtract(source);
      Vec3 normalizes = sourceToTarget.normalize();
      level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_ATTACK_IMPACT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);

      for (int particleIndex = 1; particleIndex < Mth.floor(sourceToTarget.length()); particleIndex++) {
         Vec3 particlePos = source.add(normalizes.scale(particleIndex));
         TensuraParticleHelper.spawnServerParticles(level, TensuraParticleUtils.getColorlessWave(0.9F, 2.0F), particlePos.x, particlePos.y, particlePos.z);
         if (TensuraGameRules.canSkillGrief(level)) {
            SkillHelper.launchBlock(
               entity,
               particlePos,
               2,
               1,
               0.3F,
               0.2F,
               blockState -> entity.getRandom().nextInt(2) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE),
               pos -> !pos.equals(entity.getOnPos()) && !pos.equals(entity.getOnPos().below()),
               instance
            );
         }

         AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos)).inflate(2.0);
         List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, entityData -> !entityData.is(entity));
         if (!list.isEmpty()) {
            for (LivingEntity living : list) {
               DamageSource damageSource = this.createSource(instance, entity, DamageTypes.PLAYER_ATTACK, mode);
               if (living.hurt(damageSource, (float)(entity.getAttributeValue(Attributes.ATTACK_DAMAGE) * CONFIG.projectileDamageMultiplier))) {
                  TensuraParticleHelper.spawnServerGroundSlamParticle(living, 10, 2.0F);
                  living.getDeltaMovement().add(0.0, 0.3, 0.0);
               }
            }
         }
      }
   }
}
