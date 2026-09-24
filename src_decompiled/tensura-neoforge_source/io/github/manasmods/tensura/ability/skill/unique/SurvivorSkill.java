package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SurvivorSkill extends ResistSkill {
   private static final UniqueSkillConfig.Survivor CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Survivor;

   public SurvivorSkill() {
      super(Skill.SkillType.UNIQUE, ResistSkill.ResistType.RESISTANCE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() < 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   @Override
   public boolean isDamageResisted(LivingEntity entity, DamageSource damageSource, ManasSkillInstance instance) {
      if (TensuraDamageHelper.isAbnormal(damageSource)) {
         return true;
      } else if (TensuraDamageHelper.isNaturalEffects(damageSource)) {
         return true;
      } else {
         return TensuraDamageHelper.isPoison(damageSource) ? true : TensuraDamageHelper.isPhysicalAttack(damageSource);
      }
   }

   @NotNull
   @Override
   public List<Holder<MobEffect>> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
      return List.of(
         MobEffects.HUNGER,
         MobEffects.POISON,
         MobEffects.BLINDNESS,
         MobEffects.CONFUSION,
         MobEffects.DARKNESS,
         MobEffects.DIG_SLOWDOWN,
         MobEffects.MOVEMENT_SLOWDOWN,
         MobEffects.WEAKNESS,
         TensuraMobEffects.getReference(TensuraMobEffects.BURDEN),
         TensuraMobEffects.getReference(TensuraMobEffects.CHILL),
         TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY),
         TensuraMobEffects.getReference(TensuraMobEffects.CURSE),
         TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON),
         TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS),
         TensuraMobEffects.getReference(TensuraMobEffects.PETRIFICATION)
      );
   }

   @Override
   public boolean onBeingDamaged(ManasSkillInstance instance, LivingEntity entity, DamageSource source, float amount) {
      if (!instance.isToggled()) {
         return true;
      } else if (!TensuraDamageHelper.isAbnormal(source)) {
         return true;
      } else if (ResistSkill.isNullificationDisabled(entity.level())) {
         return true;
      } else if (this.getResistanceDamageMultiplier(true) > 0.0) {
         return true;
      } else if (this.getHpMultiplierForResistance(true) >= 0.0) {
         return true;
      } else {
         return this.isNullificationBypass(source) ? true : this.isResistanceBypass(source);
      }
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(entity);
      }

      tag.putInt("activatedTimes", time + 1);
      entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.INSTANT_REGENERATION), 240, 0, false, false, false));
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      MobEffectInstance regeneration = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSTANT_REGENERATION));
      if (regeneration != null && regeneration.getAmplifier() < 1) {
         entity.removeEffect(regeneration.getEffect());
      }
   }
}
