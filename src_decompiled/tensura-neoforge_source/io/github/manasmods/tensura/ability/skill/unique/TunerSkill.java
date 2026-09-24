package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class TunerSkill extends Skill {
   public static final UniqueSkillConfig.Tuner CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Tuner;

   public TunerSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return !instance.isToggled() && !this.isInSlot(entity, instance) ? false : entity.getHealth() < entity.getMaxHealth() * CONFIG.hpMultiplier;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      int level = instance.isMastered(entity) ? 1 : 0;
      entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATE_CHANGE), 240, level, false, false, false));
      if (level == 0) {
         int time = tag.getInt("activatedTimes");
         if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
            instance.addMasteryPoint(entity);
         }

         tag.putInt("activatedTimes", time + 1);
      }
   }

   public static void clearDeathTypes(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      if (tag.contains("deaths")) {
         tag.remove("deaths");
         instance.markDirty();
         entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
      }
   }

   private boolean checkDeathType(ManasSkillInstance instance, DamageSource source) {
      CompoundTag tag = instance.getOrCreateTag();
      if (!tag.contains("deaths")) {
         return false;
      }

      ListTag deaths = (ListTag)tag.get("deaths");
      if (deaths == null) {
         return false;
      }

      for (Tag value : deaths) {
         if (value instanceof CompoundTag target && target.contains(source.getMsgId())) {
            return true;
         }
      }

      return false;
   }

   private void addDeathType(ManasSkillInstance instance, DamageSource source) {
      CompoundTag tag = instance.getOrCreateTag();
      if (!tag.contains("deaths")) {
         ListTag deaths = new ListTag();
         CompoundTag death = new CompoundTag();
         death.putBoolean(source.getMsgId(), true);
         deaths.add(death);
         tag.put("deaths", deaths);
      } else {
         ListTag deaths = (ListTag)tag.get("deaths");
         if (deaths == null) {
            return;
         }

         CompoundTag death = new CompoundTag();
         death.putBoolean(source.getMsgId(), true);
         deaths.add(death);
         tag.put("deaths", deaths);
      }

      instance.markDirty();
   }

   public boolean onDeath(ManasSkillInstance instance, LivingEntity entity, DamageSource source) {
      if (!source.is(DamageTypes.FELL_OUT_OF_WORLD) && !source.is(DamageTypes.GENERIC_KILL)) {
         if (source.tensura$getBarrierBypassLevel() >= 3.0F) {
            return true;
         }

         if (entity.isAlive()) {
            return true;
         }

         if (source.getEntity() != null) {
            if (source.getEntity() == entity) {
               return true;
            }

            if (Objects.equals(source.getEntity().getUUID(), SubordinateHelper.getSubordinateOwnerUUID(entity))) {
               return true;
            }
         }

         if (this.checkDeathType(instance, source)) {
            return true;
         }

         instance.addMasteryPoint(entity);
         entity.setHealth(Math.max(entity.getHealth(), entity.getMaxHealth() * CONFIG.hpRevive));
         entity.invulnerableTime = Math.max(60, entity.invulnerableTime);
         Predicate<Holder<MobEffect>> predicate = effect -> ((MobEffect)effect.value()).getCategory() == MobEffectCategory.HARMFUL;
         TensuraMobEffect.removePredicateEffect(entity, predicate);
         TensuraStorages.resetEffect(entity);
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         existence.setSpiritualHealth(
            Math.max(existence.getSpiritualHealth(), entity.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH) * CONFIG.shpRevive)
         );
         double epRevive = instance.isMastered(entity) ? CONFIG.epReviveMastered : CONFIG.epRevive;
         existence.setAura(Math.max(existence.getAura(), EnergyHelper.getMaxAura(entity) * epRevive));
         existence.setMagicule(Math.max(existence.getMagicule(), EnergyHelper.getMaxMagicule(entity) * epRevive));
         existence.markDirty();
         this.addDeathType(instance, source);
         if (!instance.onCoolDown(0)) {
            instance.setCoolDown(CONFIG.deathReset, 0);
         }

         entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 1.0);
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.FLASH, 1.0);
         return false;
      } else {
         return true;
      }
   }
}
