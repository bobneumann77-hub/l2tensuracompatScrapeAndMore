package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class HydraulicPropulsionSkill extends Skill {
   private static final CommonSkillConfig.HydraulicPropulsion CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).HydraulicPropulsion;

   public HydraulicPropulsionSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.isInWaterOrBubble()) {
         if (!entity.isAutoSpinAttack()) {
            if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               instance.addMasteryPoint(entity);
               if (entity instanceof Player player) {
                  player.startAutoSpinAttack(
                     CONFIG.riptideDuration, (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE) * CONFIG.riptideMultiplier, entity.getMainHandItem()
                  );
               }

               SkillHelper.riptidePush(entity, CONFIG.riptideLevel);
               entity.hurtMarked = true;
               entity.level()
                  .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TRIDENT_RIPTIDE_3, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(), TensuraParticleUtils.getWaterEffect(), entity.getX(), entity.getY(), entity.getZ(), 55, 0.08, 0.08, 0.08, 0.15, true
               );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(), TensuraParticleUtils.getWaterEffect(), entity.getX(), entity.getY(), entity.getZ(), 25, 0.08, 0.08, 0.08, 0.15, false
               );
            }
         }
      }
   }
}
