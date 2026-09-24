package io.github.manasmods.tensura.ability.battlewill.melee;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class EarthshatterKickArt extends Battlewill {
   private static final BattlewillConfig.EarthshatterKick CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).EarthshatterKick;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.onGround() && !entity.isInLiquid()) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            Level level = entity.level();
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            int radius = (int)CONFIG.radius;
            AABB aabb = entity.getBoundingBox().inflate(radius);
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, living -> living.onGround() && living != entity);
            if (!list.isEmpty()) {
               float damage = instance.isMastered(entity) ? CONFIG.baseDamage * 2.0F : CONFIG.baseDamage;

               for (LivingEntity target : list) {
                  DamageSource source = this.createSource(instance, entity, DamageTypes.PLAYER_ATTACK, mode);
                  target.hurt(source, damage);
                  SkillHelper.knockBack(entity, target, 0.5F);
               }
            }

            EffectStorage.setCameraShake(entity, radius, 0.02F, 15);
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION);
            TensuraParticleHelper.spawnServerParticles(
               level, TensuraParticleUtils.getColorlessWave(0.9F, radius), entity.getX(), entity.getY() + 0.2F, entity.getZ()
            );
            level.playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(),
               TensuraSkill.ABILITY_SOUND,
               1.0F,
               1.0F
            );
            if (TensuraGameRules.canSkillGrief(level)) {
               SkillHelper.launchBlock(
                  entity,
                  entity.position(),
                  radius,
                  1,
                  0.5F,
                  0.5F,
                  blockState -> entity.getRandom().nextInt(3) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE),
                  pos -> !pos.equals(entity.getOnPos()) && !pos.equals(entity.getOnPos().below()),
                  instance
               );
            }
         }
      }
   }
}
