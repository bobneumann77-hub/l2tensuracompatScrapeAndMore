package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ISubAbilityModeHolder;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class PrideSkill extends Skill implements ISubAbilityModeHolder {
   private static final UniqueSkillConfig.Pride CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Pride;

   public PrideSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   public int getMaxMastery() {
      return SKILL_CONFIG.Mastery.masteryUniqueSin;
   }

   @Override
   public int getSubModeOffset(ManasSkillInstance instance) {
      return 1;
   }

   public int getModes(ManasSkillInstance instance) {
      List<AbilitySlot> subSlots = ISubAbilityModeHolder.getSubSlots(instance);
      return subSlots.isEmpty() ? 1 : 1 + subSlots.size();
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? instance.getModes() - 1 : mode - 1;
      } else {
         return mode == instance.getModes() - 1 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return "pride.copy";
   }

   @Override
   public Component getModeName(ManasSkillInstance instance, int mode) {
      if (mode == 0) {
         return super.getModeName(instance, mode);
      }

      List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
      if (slots.isEmpty()) {
         return super.getModeName(instance, mode);
      }

      int index = mode - this.getSubModeOffset(instance);
      if (index >= 0 && index < slots.size()) {
         AbilitySlot slot = slots.get(index);
         if (slot.getSkill() instanceof TensuraSkill skill) {
            Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();
            ManasSkillInstance subInstance = subInstances.get(slot.getSkill());
            return (Component)(Objects.equals(skill.getModeId(subInstance, slot.getMode()), "default")
               ? skill.getName()
               : skill.getModeName(subInstance, slot.getMode()));
         } else {
            return slot.getSkill().getName();
         }
      } else {
         return super.getModeName(instance, mode);
      }
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (mode == 0) {
         return false;
      } else {
         List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
         if (slots.isEmpty()) {
            return false;
         } else {
            int index = mode - this.getSubModeOffset(instance);
            if (index >= 0 && index < slots.size()) {
               AbilitySlot slot = slots.get(mode - this.getSubModeOffset(instance));
               return ((ManasSkillInstance)instance.getSubInstances().get(slot.getSkill())).canScroll(entity, slot.getMode());
            } else {
               return false;
            }
         }
      }
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (mode == 0) {
         return true;
      } else {
         List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
         if (slots.isEmpty()) {
            return false;
         } else {
            int index = mode - this.getSubModeOffset(instance);
            if (index >= 0 && index < slots.size()) {
               AbilitySlot slot = slots.get(mode - this.getSubModeOffset(instance));
               return ((ManasSkillInstance)instance.getSubInstances().get(slot.getSkill())).canIgnoreCoolDown(entity, slot.getMode());
            } else {
               return false;
            }
         }
      }
   }

   @Override
   public boolean shouldShowCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == 0 ? instance.onCoolDown(mode) : super.shouldShowCoolDown(instance, entity, mode);
   }

   public boolean onBeingDamaged(ManasSkillInstance instance, LivingEntity entity, DamageSource source, float amount) {
      List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
      Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();

      for (AbilitySlot slot : slots) {
         if (this.isInSlot(entity, instance, slots.indexOf(slot) + this.getSubModeOffset(instance))
            && !subInstances.get(slot.getSkill()).onBeingDamaged(entity, source, amount)) {
            return false;
         }
      }

      if (instance.onCoolDown(0) || !this.isInSlot(entity, instance, 0)) {
         return true;
      }

      if (source.getEntity() == entity) {
         return true;
      }

      if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      }

      if (source.getEntity() != null && source.getEntity().getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)) {
         return true;
      }

      if (source.tensura$getBarrierBypassLevel() >= 1.75) {
         return true;
      }

      ManasSkillInstance targetInstance = source.tensura$getAbilityInstance();
      if (targetInstance != null && !targetInstance.isTemporarySkill() && source.tensura$getAbilityMode() >= 0 && targetInstance.getSkill() != this) {
         if (source.getEntity() instanceof Player player && player.getAbilities().invulnerable) {
            return true;
         } else {
            ManasSkill skill = targetInstance.getSkill();
            this.copySkill(instance, entity, skill, source.tensura$getAbilityMode(), this.copyOnDamage(entity, instance, targetInstance, source));
            return true;
         }
      } else {
         return true;
      }
   }

   public boolean onEffectAdded(ManasSkillInstance instance, LivingEntity entity, @Nullable Entity source, Changeable<MobEffectInstance> effect) {
      List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
      Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();

      for (AbilitySlot slot : slots) {
         if (this.isInSlot(entity, instance, slots.indexOf(slot) + this.getSubModeOffset(instance))
            && !subInstances.get(slot.getSkill()).onEffectAdded(entity, source, effect)) {
            return false;
         }
      }

      MobEffectInstance effectInstance = (MobEffectInstance)effect.get();
      if (effectInstance == null || source == entity) {
         return true;
      }

      if (instance.onCoolDown(0) || !instance.isMastered(entity)) {
         return true;
      }

      if (!this.isInSlot(entity, instance, 0)) {
         return true;
      }

      if (source != null && source.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)) {
         return true;
      }

      AbilitySlot slot = effectInstance.tensura$getSourceAbility();
      if (slot != null && slot.getSkill() != null && slot.getMode() >= 0 && slot.getSkill() != this) {
         if (source instanceof Player player && player.getAbilities().invulnerable) {
            return true;
         } else {
            ManasSkill skill = slot.getSkill();
            this.copySkill(instance, entity, skill, slot.getMode(), this.copyOnEffect(entity, instance, skill));
            return true;
         }
      } else {
         return true;
      }
   }

   private void copySkill(ManasSkillInstance instance, LivingEntity entity, ManasSkill skill, int mode, double chance) {
      if (!SkillUtils.hasSkill(entity, skill) && !(chance <= 0.0)) {
         if (!this.learnSubSkill(instance, entity, skill, mode, chance)) {
            if (entity instanceof Player player) {
               player.displayClientMessage(
                  Component.translatable("tensura.ability.activation_failed.plunder", new Object[]{skill.getChatDisplayName(false)})
                     .withStyle(ChatFormatting.RED),
                  true
               );
               player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }
         } else {
            if (skill instanceof TensuraSkill tensuraSkill) {
               double mastery = tensuraSkill.getAcquiringMagiculeCost(instance) * CONFIG.copyMastery;
               this.addMasteryPoint(instance, entity, (int)(mastery + this.getDefaultMasteryIncrement(instance, entity, 0)));
               instance.setCoolDown(Math.max((int)(CONFIG.copyCooldown * mastery), 1), 0);
            } else {
               instance.addMasteryPoint(entity);
            }

            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F
               );
         }
      } else {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.ability.activation_failed.plunder.empty").withStyle(ChatFormatting.RED), true);
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0) {
         if (entity instanceof ServerPlayer serverPlayer) {
            this.openSubAbilitySelectionMenu(serverPlayer, this);
         }
      } else {
         List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
         if (!slots.isEmpty()) {
            int index = mode - this.getSubModeOffset(instance);
            if (index >= 0 && index < slots.size()) {
               AbilitySlot slot = slots.get(mode - this.getSubModeOffset(instance));
               Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();
               ManasSkillInstance subInstance = subInstances.get(slot.getSkill());
               subInstance.onPressed(entity, keyNumber, slot.getMode());
               subInstance.addHeldAttributeModifiers(entity, slot.getMode());
               instance.setCoolDown(Math.max(subInstance.getCoolDown(slot.getMode()), instance.getCoolDown(mode)), mode);
               subInstance.setCoolDown(0, slot.getMode());
            }
         }
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode == 0) {
         return false;
      } else {
         List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
         if (slots.isEmpty()) {
            return false;
         } else {
            int index = mode - this.getSubModeOffset(instance);
            if (index >= 0 && index < slots.size()) {
               AbilitySlot slot = slots.get(mode - this.getSubModeOffset(instance));
               Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();
               return subInstances.get(slot.getSkill()).onHeld(entity, heldTicks, slot.getMode());
            } else {
               return false;
            }
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode != 0) {
         List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
         if (!slots.isEmpty()) {
            int index = mode - this.getSubModeOffset(instance);
            if (index >= 0 && index < slots.size()) {
               AbilitySlot slot = slots.get(mode - this.getSubModeOffset(instance));
               Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();
               ManasSkillInstance subInstance = subInstances.get(slot.getSkill());
               subInstance.onRelease(entity, heldTicks, keyNumber, slot.getMode());
               subInstance.removeAttributeModifiers(entity, slot.getMode());
               instance.setCoolDown(Math.max(subInstance.getCoolDown(slot.getMode()), instance.getCoolDown(mode)), mode);
               subInstance.setCoolDown(0, slot.getMode());
            }
         }
      }
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      if (mode != 0) {
         List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
         if (!slots.isEmpty()) {
            int index = mode - this.getSubModeOffset(instance);
            if (index >= 0 && index < slots.size()) {
               AbilitySlot slot = slots.get(mode - this.getSubModeOffset(instance));
               Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();
               subInstances.get(slot.getSkill()).onScroll(entity, delta, slot.getMode());
            }
         }
      }
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
      Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();

      for (AbilitySlot slot : slots) {
         if (this.isInSlot(entity, instance, slots.indexOf(slot) + this.getSubModeOffset(instance))
            && !subInstances.get(slot.getSkill()).onDamageEntity(entity, target, source, amount)) {
            return false;
         }
      }

      return true;
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
      Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();

      for (AbilitySlot slot : slots) {
         if (this.isInSlot(entity, instance, slots.indexOf(slot) + this.getSubModeOffset(instance))
            && !subInstances.get(slot.getSkill()).onTouchEntity(entity, target, source, amount)) {
            return false;
         }
      }

      return true;
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity entity, DamageSource source, Changeable<Float> amount) {
      List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
      Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();

      for (AbilitySlot slot : slots) {
         if (this.isInSlot(entity, instance, slots.indexOf(slot) + this.getSubModeOffset(instance))
            && !subInstances.get(slot.getSkill()).onTakenDamage(entity, source, amount)) {
            return false;
         }
      }

      return true;
   }

   public void onProjectileHit(
      ManasSkillInstance instance,
      LivingEntity entity,
      EntityHitResult hitResult,
      Projectile projectile,
      Changeable<ProjectileDeflection> deflection,
      Changeable<ProjectileHitResult> result
   ) {
      if (this.isInSlot(entity)) {
         List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
         Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();

         for (AbilitySlot slot : slots) {
            ManasSkillInstance subInstance = subInstances.get(slot.getSkill());
            if (this.isInSlot(entity, instance, slots.indexOf(slot) + this.getSubModeOffset(instance))) {
               subInstance.onProjectileHit(entity, hitResult, projectile, deflection, result);
            }
         }
      }
   }

   public boolean onDeath(ManasSkillInstance instance, LivingEntity entity, DamageSource source) {
      List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
      Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();

      for (AbilitySlot slot : slots) {
         if (!subInstances.get(slot.getSkill()).onDeath(entity, source)) {
            return false;
         }
      }

      return true;
   }

   public void onRespawn(ManasSkillInstance instance, ServerPlayer entity, boolean conqueredEnd) {
      List<AbilitySlot> slots = ISubAbilityModeHolder.getSubSlots(instance);
      Map<ManasSkill, ManasSkillInstance> subInstances = instance.getSubInstances();

      for (AbilitySlot slot : slots) {
         subInstances.get(slot.getSkill()).onRespawn(entity, conqueredEnd);
      }
   }

   private double copyOnDamage(LivingEntity owner, ManasSkillInstance pride, ManasSkillInstance sourceAbility, DamageSource source) {
      if (sourceAbility.isTemporarySkill() || sourceAbility.isSubInstance()) {
         return 0.0;
      } else if (sourceAbility.is(TensuraSkillTags.MAGIC) && !sourceAbility.is(TensuraSkillTags.COPIABLE_MAGIC)) {
         return 0.0;
      } else if (sourceAbility.is(TensuraSkillTags.ULTIMATE_SKILLS)) {
         return 0.0;
      } else {
         return pride.isMastered(owner) ? CONFIG.copyChanceMastered : CONFIG.copyChance;
      }
   }

   private double copyOnEffect(LivingEntity owner, ManasSkillInstance pride, ManasSkill sourceAbility) {
      ManasSkillInstance copy = sourceAbility.createDefaultInstance();
      if (copy.is(TensuraSkillTags.MAGIC) && !copy.is(TensuraSkillTags.COPIABLE_MAGIC)) {
         return 0.0;
      } else if (copy.is(TensuraSkillTags.ULTIMATE_SKILLS)) {
         return 0.0;
      } else {
         return pride.isMastered(owner) ? CONFIG.copyChanceMastered : CONFIG.copyChance;
      }
   }

   @Override
   public void onLearnFail(ManasSkillInstance instance, LivingEntity entity, ManasSkill toCopy) {
      ManasSkillInstance copy = toCopy.createDefaultInstance();
      if (!copy.is(TensuraSkillTags.MAGIC) || copy.is(TensuraSkillTags.COPIABLE_MAGIC)) {
         if (!copy.is(TensuraSkillTags.ULTIMATE_SKILLS)) {
            if (toCopy instanceof TensuraSkill tensuraSkill) {
               double mastery = tensuraSkill.getAcquiringMagiculeCost(instance) * CONFIG.copyMasteryFail;
               this.addMasteryPoint(instance, entity, (int)(mastery + this.getDefaultMasteryIncrement(instance, entity, 0)));
               instance.setCoolDown(Math.max((int)(CONFIG.copyCooldownFail * mastery), 1), 0);
            } else {
               instance.addMasteryPoint(entity);
            }
         }
      }
   }
}
