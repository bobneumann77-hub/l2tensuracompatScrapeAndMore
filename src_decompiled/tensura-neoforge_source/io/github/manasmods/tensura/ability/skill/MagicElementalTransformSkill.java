package io.github.manasmods.tensura.ability.skill;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public abstract class MagicElementalTransformSkill extends Skill implements ITransformation {
   public static final ExtraSkillConfig.MagicElementalTransform CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).MagicElementalTransform;

   public MagicElementalTransformSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public MagicElementalTransformSkill(Skill.SkillType type) {
      super(type);
   }

   protected abstract Element getMagicElement();

   protected abstract ManasSkill getElementalTransform();

   protected Holder<MobEffect> getMagicElementalEffect() {
      return TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_ELEMENTAL_TRANSFORMATION);
   }

   protected abstract void doVisualEffect(LivingEntity var1);

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, this.getElementalTransform()) ? false : newEP > CONFIG.epAcquirement;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return entity.level().isClientSide() ? false : this.canTick(instance, entity);
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return hasMagicTransformEffect(entity, this.getMagicElement());
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(entity);
      }

      tag.putInt("activatedTimes", time + 1);
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      entity.removeEffect(this.getMagicElementalEffect());
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0) && !instance.isTemporarySkill()) {
         for (ManasSkill manasSkill : SkillAPI.getSkillRegistry()) {
            if (manasSkill instanceof SpiritualMagic skill
               && skill.getElemental() == this.getMagicElement()
               && !(skill.getLevel().getId() > CONFIG.spiritLevel)) {
               SkillHelper.learnSkill(entity, skill);
            }
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!this.failedToActivate(entity, this.getMagicElementalEffect())) {
         if (hasMagicTransformEffect(entity, this.getMagicElement())) {
            entity.removeEffect(this.getMagicElementalEffect());
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            instance.setCoolDown(CONFIG.cooldown, mode);
         } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            instance.addMasteryPoint(entity);
            int duration = instance.isMastered(entity) ? CONFIG.transformationDurationMastered : CONFIG.transformationDuration;
            instance.setCoolDown(duration / 20 + CONFIG.cooldown, mode);
            MobEffectInstance transform = new MobEffectInstance(this.getMagicElementalEffect(), duration, 0, false, false, false);
            CompoundTag tag = transform.tensura$getOrCreateTag();
            tag.putInt("elemental", this.getMagicElement().getId());
            entity.addEffect(transform);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            this.doVisualEffect(entity);
         }
      }
   }

   public static boolean hasMagicTransformEffect(LivingEntity entity, Element element) {
      MobEffectInstance effect = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_ELEMENTAL_TRANSFORMATION));
      return effect == null ? false : effect.tensura$getOrCreateTag().getInt("elemental") == element.getId();
   }
}
