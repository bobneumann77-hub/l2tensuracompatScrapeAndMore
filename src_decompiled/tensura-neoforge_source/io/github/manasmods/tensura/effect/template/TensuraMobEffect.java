package io.github.manasmods.tensura.effect.template;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.entity.EffectConfig;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class TensuraMobEffect extends MobEffect {
   public static final EffectConfig CONFIG = (EffectConfig)ConfigRegistry.getConfig(EffectConfig.class);

   public TensuraMobEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   protected TensuraMobEffect(MobEffectCategory mobEffectCategory, int i, ParticleOptions particleOptions) {
      super(mobEffectCategory, i, particleOptions);
   }

   public void onAttributeRemoved(LivingEntity entity, MobEffectInstance instance) {
   }

   public void onEffectRemoved(LivingEntity entity, MobEffectInstance instance) {
   }

   public static boolean addEffect(
      LivingEntity entity,
      Holder<MobEffect> mobEffectHolder,
      int duration,
      int level,
      boolean ambient,
      boolean visible,
      boolean showIcon,
      @Nullable UUID source,
      @Nullable ManasSkill ability,
      int mode
   ) {
      MobEffectInstance instance = new MobEffectInstance(mobEffectHolder, duration, level, ambient, visible, showIcon);
      return addEffect(entity, instance, source, ability, mode);
   }

   public static boolean addEffect(LivingEntity entity, MobEffectInstance instance, @Nullable UUID source, @Nullable ManasSkill ability, int mode) {
      instance.tensura$setSource(source);
      instance.tensura$setSourceAbility(ability, mode);
      return entity.addEffect(instance, source != null ? entity.level().getPlayerByUUID(source) : null);
   }

   public static boolean addEffect(
      LivingEntity entity,
      Holder<MobEffect> mobEffectHolder,
      int duration,
      int level,
      boolean ambient,
      boolean visible,
      boolean showIcon,
      @Nullable UUID source,
      @Nullable AbilitySlot ability
   ) {
      MobEffectInstance instance = new MobEffectInstance(mobEffectHolder, duration, level, ambient, visible, showIcon);
      return addEffect(entity, instance, source, ability);
   }

   public static boolean addEffect(LivingEntity entity, MobEffectInstance instance, @Nullable UUID source, @Nullable AbilitySlot ability) {
      instance.tensura$setSource(source);
      instance.tensura$setSourceAbility(ability);
      return entity.addEffect(instance, source != null ? entity.level().getPlayerByUUID(source) : null);
   }

   public static boolean addEffect(LivingEntity entity, MobEffectInstance instance, @Nullable Entity source, @Nullable ManasSkill ability, int mode) {
      if (source != null) {
         instance.tensura$setSource(source.getUUID());
      } else {
         instance.tensura$setSource(null);
      }

      instance.tensura$setSourceAbility(ability, mode);
      return entity.addEffect(instance, source);
   }

   public static boolean addEffect(LivingEntity entity, MobEffectInstance instance, @Nullable Entity source, @Nullable ManasSkill ability) {
      if (source != null) {
         instance.tensura$setSource(source.getUUID());
      } else {
         instance.tensura$setSource(null);
      }

      instance.tensura$setSourceAbility(ability, 0);
      return entity.addEffect(instance, source);
   }

   public static boolean removePredicateEffect(LivingEntity target, Predicate<Holder<MobEffect>> predicate) {
      return removePredicateEffect(target, predicate, 0.0);
   }

   public static boolean removePredicateEffect(LivingEntity target, Predicate<Holder<MobEffect>> predicate, double mpCost) {
      return removePredicateEffect(target, predicate, mpCost, 0.0);
   }

   public static boolean removePredicateEffect(LivingEntity target, Predicate<Holder<MobEffect>> predicate, double mpCost, double apCost) {
      boolean success = false;

      for (MobEffectInstance effect : List.copyOf(target.getActiveEffects())) {
         if (predicate.test(effect.getEffect())) {
            int level = effect.getAmplifier() + 1;
            if (!EnergyHelper.isOutOfEnergy(target, apCost * level, mpCost * level) && target.removeEffect(effect.getEffect())) {
               success = true;
            }
         }
      }

      return success;
   }

   public static void removeLevelsOfEffect(LivingEntity entity, Holder<MobEffect> effect, int level) {
      MobEffectInstance instance = entity.getEffect(effect);
      if (instance != null) {
         int effectLevel = instance.getAmplifier();
         if (effectLevel - level < 0) {
            entity.removeEffect(effect);
         } else {
            int duration = instance.getDuration();
            boolean ambient = instance.isAmbient();
            boolean visible = instance.isVisible();
            boolean showIcon = instance.showIcon();
            UUID source = instance.tensura$getSource();
            AbilitySlot skill = instance.tensura$getSourceAbility();
            CompoundTag tag = instance.tensura$getOrCreateTag();
            entity.removeEffect(effect);
            MobEffectInstance newInstance = new MobEffectInstance(effect, duration, effectLevel - level, ambient, visible, showIcon);
            newInstance.tensura$setSource(source);
            newInstance.tensura$setSourceAbility(skill);
            newInstance.tensura$setTag(tag);
            entity.addEffect(newInstance);
         }
      }
   }
}
