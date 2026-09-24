package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class PoisonSkill extends Skill {
   private static final CommonSkillConfig.Poison CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).Poison;

   public PoisonSkill() {
      super(Skill.SkillType.COMMON);
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      if (entity instanceof ServerPlayer player) {
         return player.getStats().getValue(Stats.ITEM_USED.get(Items.SPIDER_EYE)) >= CONFIG.spiderEyeAcquirement
            ? true
            : player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType)MonsterEntityTypes.BLACK_SPIDER.get())) >= CONFIG.spiderAcquirement;
      } else {
         return false;
      }
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(attacker, instance) && !instance.isToggled()) {
         return true;
      }

      if (source.getDirectEntity() != attacker) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return true;
      }

      Level level = attacker.level();
      Holder<MobEffect> poison = instance.isMastered(attacker) ? TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON) : MobEffects.POISON;
      TensuraMobEffect.addEffect(target, poison, CONFIG.poisonDuration, CONFIG.poisonLevel - 1, true, false, true, attacker.getUUID(), this, 0);
      level.playSound(
         null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.ACID_SIZZLE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
      );
      ((ServerLevel)level)
         .sendParticles(
            TensuraParticleUtils.getPoisonBubble(),
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
         instance.addMasteryPoint(attacker);
      }

      tag.putInt("activatedTimes", time + 1);
      return true;
   }
}
