package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.MaceItem;

public class GravityManipulationSkill extends Skill {
   public static final ExtraSkillConfig.GravityManipulation CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).GravityManipulation;

   public GravityManipulationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         if (!(EnergyHelper.getBaseMaxEP(entity) < CONFIG.dominationEpAcquirement)) {
            SkillHelper.learnSkill(entity, ((GravityDominationSkill)ExtraSkills.GRAVITY_DOMINATION.get()).createLearningInstance(entity));
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.GRAVITY_BOOST, CONFIG.manipulationBoost);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.GRAVITY_BOOST, CONFIG.manipulationBoost);
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled()) {
         return true;
      }

      if (source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK)) {
         if (!(source.getDirectEntity() instanceof LivingEntity attacker)) {
            return true;
         }

         if (!MaceItem.canSmashAttack(attacker)) {
            return true;
         }

         amount.set((Float)amount.get() * (float)CONFIG.manipulationBoost);
      }

      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE))) {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.skill.magic_interference").withStyle(ChatFormatting.RED), true);
         }
      } else if (entity instanceof Player player) {
         if (!player.isCreative() && !player.isSpectator()) {
            if (!EnergyHelper.isOutOfEnergy(player, instance, mode)) {
               if (player.getAbilities().mayfly) {
                  player.getAbilities().mayfly = false;
                  player.getAbilities().flying = false;
                  TensuraParticleHelper.spawnServerParticles(
                     entity.level(),
                     TensuraParticleUtils.getPurpleWave(0.9F, entity.getBbWidth() * 3.0F, 0.5F, true),
                     entity.getX(),
                     entity.getY() + entity.getBbHeight() * 0.33,
                     entity.getZ()
                  );
                  TensuraParticleHelper.spawnServerParticles(
                     entity.level(),
                     TensuraParticleUtils.getPurpleWave(0.9F, entity.getBbWidth() * 3.0F, 0.5F, true),
                     entity.getX(),
                     entity.getY() + entity.getBbHeight() * 0.66,
                     entity.getZ()
                  );
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               } else {
                  instance.addMasteryPoint(entity);
                  player.getAbilities().mayfly = true;
                  player.getAbilities().flying = true;
                  TensuraParticleHelper.spawnServerParticles(
                     entity.level(),
                     TensuraParticleUtils.getPurpleWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
                     entity.getX(),
                     entity.getY() + entity.getBbHeight() * 0.33,
                     entity.getZ()
                  );
                  TensuraParticleHelper.spawnServerParticles(
                     entity.level(),
                     TensuraParticleUtils.getPurpleWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
                     entity.getX(),
                     entity.getY() + entity.getBbHeight() * 0.66,
                     entity.getZ()
                  );
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               }

               player.onUpdateAbilities();
            }
         }
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      if (entity instanceof Player player && player.getAbilities().mayfly && !SkillUtils.canFlyLegit(player)) {
         player.getAbilities().mayfly = false;
         player.getAbilities().flying = false;
         player.onUpdateAbilities();
      }
   }

   public static void learnGravityManipulation(ManasSkillInstance instance, LivingEntity entity) {
      if (!SkillUtils.hasSkillPermanently(entity, (ManasSkill)ExtraSkills.GRAVITY_MANIPULATION.get())) {
         int skills = instance.is(TensuraSkillTags.GRAVITY_SKILLS) ? 1 : 0;

         for (ManasSkillInstance skill : SkillAPI.getSkillsFrom(entity).getLearnedSkills()) {
            if (!skill.isTemporarySkill() && skill.isMastered(entity) && skill.is(TensuraSkillTags.GRAVITY_SKILLS)) {
               skills++;
            }
         }

         if (!(skills < CONFIG.gravitySkillMasteredAcquirement)) {
            SkillHelper.learnSkill(entity, ((GravityManipulationSkill)ExtraSkills.GRAVITY_MANIPULATION.get()).createLearningInstance(entity));
         }
      }
   }
}
