package io.github.manasmods.tensura.ability.battlewill.melee;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.ability.battlewill.projectile.OgreSwordCannonArt;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.registry.battlewill.ProjectileArts;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class OgreSwordGuillotineArt extends Battlewill {
   private static final BattlewillConfig.OgreSwordGuillotine CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).OgreSwordGuillotine;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_GUILLOTINE));
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      instance.addMasteryPoint(entity);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_GUILLOTINE))) {
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_GUILLOTINE));
      } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         entity.addEffect(
            new MobEffectInstance(
               TensuraMobEffects.getReference(TensuraMobEffects.OGRE_GUILLOTINE),
               instance.isMastered(entity) ? CONFIG.effectTimeMastered : CONFIG.effectTime,
               instance.isMastered(entity) ? 1 : 0,
               true,
               false,
               true
            )
         );
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
            );
         entity.swing(InteractionHand.MAIN_HAND, true);
      }
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         SkillHelper.learnSkill(entity, ((OgreSwordCannonArt)ProjectileArts.OGRE_SWORD_CANNON.get()).createLearningInstance(entity));
      }
   }
}
