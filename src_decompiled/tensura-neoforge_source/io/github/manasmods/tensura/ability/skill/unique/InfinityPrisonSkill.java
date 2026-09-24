package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class InfinityPrisonSkill extends Skill implements ISpatialStorage {
   private static final UniqueSkillConfig.InfinityPrison CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).InfinityPrison;

   public InfinityPrisonSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCostImprison;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "infinity_prison.imprison";
         case 1 -> "infinity_prison.imaginary_space";
         default -> super.getModeId(instance, mode);
      };
   }

   public boolean onBeingDamaged(ManasSkillInstance instance, LivingEntity entity, DamageSource damageSource, float amount) {
      if (!this.isInSlot(entity, instance)) {
         return true;
      }

      if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      }

      if (damageSource.is(TensuraTags.DamageTypes.BYPASS_DISTORTION_FIELD)) {
         return true;
      }

      if (damageSource.tensura$getBarrierBypassLevel() >= 2.0F) {
         return true;
      }

      if (damageSource.getEntity() instanceof LivingEntity source) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         if (TensuraStorages.getExistenceFrom(source).getEP() >= existence.getEP() * CONFIG.guardEP) {
            return true;
         }

         double cost = amount * CONFIG.magiculeCostGuard;
         if (existence.getMagicule() < cost) {
            return true;
         }

         EnergyHelper.isOutOfEnergy(entity, 0.0, cost);
         return false;
      } else {
         return true;
      }
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity owner, DamageSource damageSource, Changeable<Float> amount) {
      if (!this.isInSlot(owner, instance)) {
         return true;
      }

      if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      }

      if (damageSource.is(TensuraTags.DamageTypes.BYPASS_DISTORTION_FIELD)) {
         return true;
      }

      if (damageSource.tensura$getBarrierBypassLevel() >= 2.0F) {
         return true;
      }

      if (damageSource.getEntity() instanceof LivingEntity source) {
         IExistence existence = TensuraStorages.getExistenceFrom(owner);
         if (TensuraStorages.getExistenceFrom(source).getEP() >= existence.getEP() * CONFIG.guardEP) {
            return true;
         }

         float damageAmount = (Float)amount.get();
         double lackedMana = EnergyHelper.isOutOfMagiculeConsuming(owner, (int)(damageAmount * CONFIG.magiculeCostGuard));
         if (lackedMana > 0.0) {
            damageAmount = (float)(damageAmount - lackedMana / CONFIG.magiculeCostGuard);
         }

         if (damageSource.tensura$getMagiculeCost() > 0.0) {
            CompoundTag tag = instance.getOrCreateTag();
            double mp = tag.getDouble("mpStorage") + damageSource.tensura$getMagiculeCost() * damageAmount / ((Float)amount.get()).floatValue();
            tag.putDouble("mpStorage", mp);
            instance.markDirty();
         }

         if (!(damageAmount < (Float)amount.get())) {
            return false;
         }

         amount.set((Float)amount.get() - damageAmount);
      }

      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0) {
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(LivingEntity.class, entity, CONFIG.imprisonRange, 0.75, false, true, false);
         if (target == null) {
            entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownImprisonMastered : CONFIG.cooldownImprison, mode);
            return;
         }

         if (target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT))) {
            target.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT));
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.DEBUFF_DEACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.FLASH, 1.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.COMPOSTER, 1.0);
         } else {
            if (target instanceof Player player && player.getAbilities().invulnerable) {
               return;
            }

            double cost = this.getMagiculeCost(entity, instance, mode) + EnergyHelper.getMaxMagicule(target) * CONFIG.magiculeCostImprisonTarget;
            if (EnergyHelper.isOutOfEnergy(entity, 0.0, cost)) {
               return;
            }

            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownImprisonMastered : CONFIG.cooldownImprison, mode);
            int duration = instance.isMastered(entity) ? CONFIG.imprisonDurationMastered : CONFIG.imprisonDuration;
            MobEffectInstance prison = new MobEffectInstance(
               TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT), duration, 0, false, false, false
            );
            TensuraMobEffect.addEffect(target, prison, entity, this, mode);
            TensuraDamageHelper.markHurt(target, entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.FLASH, 1.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.EXPLOSION, 1.0);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
         }
      } else {
         this.openSpatialStorage(entity, instance);
      }
   }

   @NotNull
   @Override
   public SpatialStorageContainer getSpatialStorage(ManasSkillInstance instance, Provider provide) {
      SpatialStorageContainer container = new SpatialStorageContainer(90, 999);
      container.fromTag(instance.getOrCreateTag().getList("SpatialStorage", 10), provide);
      return container;
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0)) {
         AttributeInstance water = entity.getAttribute(TensuraAttributes.WATER_CAPACITY);
         if (water != null) {
            water.setBaseValue(water.getValue() + CONFIG.waterCapacity);
         }

         AttributeInstance lava = entity.getAttribute(TensuraAttributes.LAVA_CAPACITY);
         if (lava != null) {
            lava.setBaseValue(lava.getValue() + CONFIG.lavaCapacity);
         }
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      if (!(instance.getMastery() < 0.0)) {
         IAbility ability = TensuraStorages.getAbilityFrom(entity);
         AttributeInstance water = entity.getAttribute(TensuraAttributes.WATER_CAPACITY);
         if (water != null) {
            water.setBaseValue(water.getValue() - CONFIG.waterCapacity);
            ability.setWaterPoint(Math.min(water.getValue(), ability.getWaterPoint()));
            ability.markDirty();
         }

         AttributeInstance lava = entity.getAttribute(TensuraAttributes.LAVA_CAPACITY);
         if (lava != null) {
            lava.setBaseValue(lava.getValue() - CONFIG.lavaCapacity);
            ability.setLavaPoint(Math.min(lava.getValue(), ability.getLavaPoint()));
            ability.markDirty();
         }
      }
   }
}
