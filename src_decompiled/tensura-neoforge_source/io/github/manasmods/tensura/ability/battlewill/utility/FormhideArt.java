package io.github.manasmods.tensura.ability.battlewill.utility;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.registry.battlewill.UtilityArts;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class FormhideArt extends Battlewill {
   private static final BattlewillConfig.Formhide CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).Formhide;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      entity.addEffect(
         new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT), 5, CONFIG.concealment - 1, false, false, false)
      );
      if (heldTicks == 0) {
         entity.level()
            .playSound(null, entity.getX(), entity.getY(), entity.getZ(), TensuraSoundEvents.PRESENCE_CONCEALMENT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      return true;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         SkillHelper.learnSkill(entity, ((HazeArt)UtilityArts.HAZE.get()).createLearningInstance(entity));
      }
   }
}
