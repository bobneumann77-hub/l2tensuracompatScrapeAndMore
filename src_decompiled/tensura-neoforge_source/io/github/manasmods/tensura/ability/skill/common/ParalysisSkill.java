package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ParalysisSkill extends Skill {
   private static final CommonSkillConfig.Paralysis CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).Paralysis;

   public ParalysisSkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return entity instanceof ServerPlayer player
         ? player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType)MonsterEntityTypes.EVIL_CENTIPEDE.get())) >= CONFIG.centipedeAcquirement
         : false;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity owner, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(owner, instance) && !instance.isToggled()) {
         return true;
      }

      if (source.getDirectEntity() != owner) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return true;
      }

      Level level = owner.level();
      int paralysis = instance.isMastered(owner) ? CONFIG.paralysisLevelMastered - 1 : CONFIG.paralysisLevel - 1;
      target.addEffect(
         new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), CONFIG.paralysisDuration, paralysis, true, false, true), owner
      );
      level.playSound(
         null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.ACID_SIZZLE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
      );
      ((ServerLevel)level)
         .sendParticles(
            TensuraParticleUtils.getParalyzingBubble(),
            target.position().x,
            target.position().y + target.getBbHeight() / 2.0,
            target.position().z,
            20,
            0.08,
            0.08,
            0.08,
            0.15
         );
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(owner);
      }

      tag.putInt("activatedTimes", time + 1);
      return true;
   }
}
