package io.github.manasmods.tensura.ability.skill.resist;

import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.SkillEvents.UnlockSkillEvent;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ResistanceConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.ExistenceStorage;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules.BooleanValue;
import org.jetbrains.annotations.Nullable;

public class ResistSkill extends Skill {
   public static final ResistanceConfig CONFIG = (ResistanceConfig)ConfigRegistry.getConfig(ResistanceConfig.class);
   private double cachedDamageRequirement = Double.NaN;
   private int cachedPointRequirement = -1;
   private final ResistSkill.ResistType resistType;

   public ResistSkill(Skill.SkillType skillType, ResistSkill.ResistType resistType) {
      super(skillType);
      this.resistType = resistType;
   }

   public ResistSkill(ResistSkill.ResistType resistType) {
      super(Skill.SkillType.RESISTANCE);
      this.resistType = resistType;
   }

   public ResistSkill() {
      this(Skill.SkillType.RESISTANCE, ResistSkill.ResistType.RESISTANCE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return this.getResistType().equals(ResistSkill.ResistType.NULLIFICATION) ? SKILL_CONFIG.mpAcquirementNullification : SKILL_CONFIG.mpAcquirementResistance;
   }

   private void resolveCachedTier() {
      if (this.getRegistryName() == null) {
         this.cachedDamageRequirement = 5.0;
         this.cachedPointRequirement = 100;
      } else {
         String name = this.getRegistryName().toString();
         if (CONFIG.EasyResistance.easyResistances.contains(name)) {
            this.cachedDamageRequirement = CONFIG.EasyResistance.easyResistanceDamageRequirement;
            this.cachedPointRequirement = CONFIG.EasyResistance.easyResistancePointRequirement;
         } else if (CONFIG.MediumResistance.mediumResistances.contains(name)) {
            this.cachedDamageRequirement = CONFIG.MediumResistance.mediumResistanceDamageRequirement;
            this.cachedPointRequirement = CONFIG.MediumResistance.mediumResistancePointRequirement;
         } else if (CONFIG.HardResistance.hardResistances.contains(name)) {
            this.cachedDamageRequirement = CONFIG.HardResistance.hardResistanceDamageRequirement;
            this.cachedPointRequirement = CONFIG.HardResistance.hardResistancePointRequirement;
         } else {
            this.cachedDamageRequirement = 5.0;
            this.cachedPointRequirement = 100;
         }
      }
   }

   public double getDamageAmountForLearning() {
      if (Double.isNaN(this.cachedDamageRequirement)) {
         this.resolveCachedTier();
      }

      return this.cachedDamageRequirement;
   }

   public int getLearningPointRequirement() {
      if (this.cachedPointRequirement < 0) {
         this.resolveCachedTier();
      }

      return this.cachedPointRequirement;
   }

   @Override
   protected boolean isAffectedByStatus(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (entity.getActiveEffects().isEmpty()) {
         return false;
      } else {
         return this.isAffectedByAbility(instance, entity, mode)
            ? true
            : entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT));
      }
   }

   @Override
   protected boolean isAffectedByAbility(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.REST));
   }

   @Override
   public boolean canInteractSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (ExistenceStorage.isInSleepMode(TensuraStorages.getExistenceFrom(entity))) {
         return false;
      } else {
         return this.isAffectedByStatus(instance, entity, 0) ? false : this.canActivateInArea(instance, entity, -1, false, false);
      }
   }

   @Override
   public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return false;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() < 0.0 ? false : this.getResistType().equals(ResistSkill.ResistType.RESISTANCE) || !isNullificationDisabled(living.level());
   }

   public void onToggleOn(ManasSkillInstance skillInstance, LivingEntity living) {
      List<Holder<MobEffect>> list = this.getImmuneEffects(skillInstance, living);
      if (!list.isEmpty()) {
         TensuraMobEffect.removePredicateEffect(living, list::contains);
      }
   }

   public boolean isNullificationBypass(DamageSource source) {
      return source.tensura$getResistanceBypassLevel() >= 2.0F
         ? true
         : source.getEntity() instanceof LivingEntity living
            && TensuraStorages.getAbilityFrom(living).isAbilityInActivePreset((ManasSkill)UniqueSkills.ANTI_SKILL.get());
   }

   public boolean isResistanceBypass(DamageSource source) {
      return source.tensura$getResistanceBypassLevel() >= 1.0F
         ? true
         : source.getEntity() instanceof LivingEntity living
            && TensuraStorages.getAbilityFrom(living).isAbilityInActivePreset((ManasSkill)UniqueSkills.ANTI_SKILL.get());
   }

   public boolean isDamageResisted(LivingEntity entity, DamageSource damageSource, ManasSkillInstance instance) {
      return false;
   }

   protected double getHpMultiplierForResistance(boolean nullification) {
      return nullification ? CONFIG.hpDamageBypassNullification : CONFIG.hpDamageBypassResistance;
   }

   protected double getResistanceDamageMultiplier(boolean nullification) {
      return nullification ? CONFIG.nullificationDamageMultiplier : CONFIG.resistanceDamageMultiplier;
   }

   public boolean onBeingDamaged(ManasSkillInstance instance, LivingEntity entity, DamageSource source, float amount) {
      if (!instance.isToggled()) {
         return true;
      }

      if (!this.isDamageResisted(entity, source, instance)) {
         return true;
      }

      boolean nullDisabled = isNullificationDisabled(entity.level());
      if (this.getResistType().equals(ResistSkill.ResistType.NULLIFICATION)) {
         if (nullDisabled) {
            return true;
         }

         if (this.getResistanceDamageMultiplier(true) > 0.0) {
            return true;
         }

         if (this.getHpMultiplierForResistance(true) >= 0.0) {
            return true;
         }
      } else {
         if (!nullDisabled) {
            ManasSkill nullification = this.getNullificationForm();
            if (nullification != null && SkillUtils.isSkillToggled(entity, nullification)) {
               return true;
            }
         }

         double hpMultiplier = this.getHpMultiplierForResistance(false);
         if (hpMultiplier > 0.0 && amount > entity.getHealth() * hpMultiplier && this.getResistanceDamageMultiplier(false) > 0.0) {
            return true;
         }
      }

      return this.isNullificationBypass(source) ? true : this.isResistanceBypass(source);
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity owner, DamageSource source, Changeable<Float> amount) {
      if (instance.getMastery() < 0.0) {
         if (!this.isDamageResisted(owner, source, instance)) {
            return true;
         }

         if (!this.getResistType().equals(ResistSkill.ResistType.RESISTANCE)) {
            return true;
         }

         if (((Float)amount.get()).floatValue() > this.getDamageAmountForLearning()) {
            this.addLearnPoint(instance, owner, 0, owner.getAttributeValue(TensuraAttributes.ABILITY_LEARNING_GAIN));
         } else {
            CompoundTag tag = instance.getOrCreateTag();
            tag.putDouble("damagePoint", tag.getDouble("damagePoint") + ((Float)amount.get()).floatValue());
            double requirement = this.getDamageAmountForLearning() * CONFIG.damagePointMultiplier;
            if (tag.getDouble("damagePoint") >= requirement) {
               this.addLearnPoint(instance, owner, 0, owner.getAttributeValue(TensuraAttributes.ABILITY_LEARNING_GAIN));
            }

            instance.markDirty();
         }

         return true;
      } else {
         if (!instance.isToggled()) {
            return true;
         }

         if (!this.isDamageResisted(owner, source, instance)) {
            return true;
         }

         boolean nullDisabled = isNullificationDisabled(owner.level());
         boolean bypass = this.isResistanceBypass(source);
         if (this.getResistType().equals(ResistSkill.ResistType.NULLIFICATION)) {
            if (nullDisabled || this.isNullificationBypass(source)) {
               return true;
            }

            if (bypass) {
               double hpMultiplier = this.getHpMultiplierForResistance(false);
               if (!(hpMultiplier < 0.0) && !(((Float)amount.get()).floatValue() < owner.getHealth() * hpMultiplier)) {
                  float multiplier = (float)this.getResistanceDamageMultiplier(false);
                  if (multiplier <= 0.0F) {
                     return false;
                  }

                  amount.set((Float)amount.get() * multiplier);
                  return true;
               } else {
                  return false;
               }
            } else {
               double hpMultiplier = this.getHpMultiplierForResistance(true);
               if (!(hpMultiplier < 0.0) && !(((Float)amount.get()).floatValue() < owner.getHealth() * hpMultiplier)) {
                  float multiplier = (float)this.getResistanceDamageMultiplier(true);
                  if (multiplier <= 0.0F) {
                     return false;
                  }

                  amount.set((Float)amount.get() * multiplier);
                  return true;
               } else {
                  return false;
               }
            }
         } else if (this.resistType.equals(ResistSkill.ResistType.RESISTANCE) && !bypass) {
            if (!nullDisabled) {
               ManasSkill nullification = this.getNullificationForm();
               if (nullification != null && SkillUtils.isSkillToggled(owner, nullification)) {
                  return true;
               }
            }

            double hpMultiplier = this.getHpMultiplierForResistance(false);
            if (!(hpMultiplier < 0.0) && !(((Float)amount.get()).floatValue() < owner.getHealth() * hpMultiplier)) {
               float multiplier = (float)this.getResistanceDamageMultiplier(false);
               if (multiplier <= 0.0F) {
                  return false;
               }

               amount.set((Float)amount.get() * multiplier);
               return true;
            } else {
               return false;
            }
         } else {
            return true;
         }
      }
   }

   public boolean onEffectAdded(ManasSkillInstance instance, LivingEntity entity, @Nullable Entity source, Changeable<MobEffectInstance> effect) {
      if (!instance.isToggled()) {
         return true;
      }

      if (this.getResistType().equals(ResistSkill.ResistType.NULLIFICATION) && isNullificationDisabled(entity.level())) {
         return true;
      }

      List<Holder<MobEffect>> list = this.getImmuneEffects(instance, entity);
      return !list.isEmpty() && !effect.isEmpty() ? !list.contains(((MobEffectInstance)effect.get()).getEffect()) : true;
   }

   protected List<Holder<MobEffect>> getImmuneEffects(ManasSkillInstance instance, LivingEntity entity) {
      return List.of();
   }

   private void removeDamagePoint(ManasSkillInstance instance, boolean removeTag) {
      CompoundTag tag = instance.getTag();
      if (tag != null) {
         tag.remove("damagePoint");
         if (removeTag && tag.isEmpty()) {
            instance.setTag(null);
         }

         instance.markDirty();
      }
   }

   @Override
   public boolean addLearnPoint(ManasSkillInstance instance, LivingEntity entity, int mode, double point) {
      if (instance.getMastery() < 0.0) {
         double oldMastery = instance.getMastery();
         Changeable<Double> newPoint = Changeable.of(oldMastery + point);
         EventResult result = ((TensuraSkillEvents.SkillLearningEvent)TensuraSkillEvents.SKILL_LEARNING.invoker()).learn(instance, entity, mode, 0.0, newPoint);
         if (result.isFalse()) {
            return false;
         }

         if (oldMastery < 0.0 && (Double)newPoint.get() >= 0.0) {
            instance.setMastery(Math.min((Double)newPoint.get(), 0.0));
            Changeable<MutableComponent> unlockMessage = Changeable.of(
               Component.translatable("tensura.skill.acquire_learning", new Object[]{instance.getChatDisplayName(true)})
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
            );
            EventResult unlockResult = ((UnlockSkillEvent)SkillEvents.UNLOCK_SKILL.invoker()).unlockSkill(instance, entity, unlockMessage);
            if (unlockResult.isFalse()) {
               instance.setMastery(oldMastery);
               return false;
            }

            instance.onLearnSkill(entity);
            instance.setToggled(true);
            instance.onToggleOn(entity);
            if (unlockMessage.isPresent()) {
               entity.sendSystemMessage((Component)unlockMessage.get());
            }

            this.removeDamagePoint(instance, true);
         } else {
            instance.setMastery(Math.min((Double)newPoint.get(), 0.0));
            this.removeDamagePoint(instance, false);
         }
      }

      return true;
   }

   public void evolveToNullification(ManasSkillInstance instance, LivingEntity entity) {
      if (!(instance.getMastery() < 0.0)) {
         if (!instance.isTemporarySkill()) {
            ManasSkill nullification = this.getNullificationForm();
            if (nullification != null) {
               ManasSkillInstance skillInstance = new TensuraSkillInstance(nullification);
               if (SkillHelper.learnSkill(entity, skillInstance)) {
                  Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
                  if (optional.isEmpty()) {
                     return;
                  }

                  optional.get().addIntrinsicSkill(nullification);
                  optional.get().markDirty();
                  RaceAPI.getRaceFrom(entity).markDirty();
               }
            }
         }
      }
   }

   @Nullable
   protected ManasSkill getNullificationForm() {
      return null;
   }

   public static boolean isNullificationDisabled(Level level) {
      return ((BooleanValue)level.getGameRules().getRule(TensuraGameRules.DISABLE_NULLIFICATION)).get();
   }

   @Generated
   public ResistSkill.ResistType getResistType() {
      return this.resistType;
   }

   public enum ResistType {
      RESISTANCE,
      NULLIFICATION;
   }
}
