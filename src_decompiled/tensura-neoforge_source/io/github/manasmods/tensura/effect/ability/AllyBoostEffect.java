package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import io.github.manasmods.tensura.ability.skill.unique.ChosenOneSkill;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.ExistenceStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.awt.Color;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect.AttributeTemplate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class AllyBoostEffect extends TensuraMobEffect implements DamageAction {
   private static final ResourceLocation ALLY_BOOST = ResourceLocation.fromNamespaceAndPath("tensura", "ally_boost");

   public AllyBoostEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(255, 183, 0).getRGB());
      this.addAttributeModifier(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE, ALLY_BOOST, ChosenOneSkill.CONFIG.allyCritChance, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE, ALLY_BOOST, ChosenOneSkill.CONFIG.meleeDodge, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE, ALLY_BOOST, ChosenOneSkill.CONFIG.projectileDodge, Operation.ADD_VALUE);
   }

   public void addAttributeModifiers(AttributeMap attributeMap, int i) {
      for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
         AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
         if (attributeInstance != null && (entry.getKey().equals(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE) || i > 0)) {
            attributeInstance.removeModifier(entry.getValue().id());
            int level = entry.getKey().equals(ManasCoreAttributes.CRITICAL_ATTACK_CHANCE) ? i : i - 1;
            attributeInstance.addOrReplacePermanentModifier(entry.getValue().create(level));
         }
      }
   }

   private boolean canControlTarget(LivingEntity target, LivingEntity source) {
      if (!CharmSkill.canMindControl(target, target.level())) {
         return false;
      } else if (target.getType().is(EntityTypeTags.UNDEAD)) {
         return false;
      } else {
         return target.isAlliedTo(source) ? false : !ExistenceStorage.isSummon(target);
      }
   }

   @Override
   public boolean onKillEntity(LivingEntity source, LivingEntity target, DamageSource damageSource) {
      if (!this.canControlTarget(target, source)) {
         return true;
      }

      if (target.isAlive()) {
         return true;
      }

      MobEffectInstance boost = source.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.ALLY_BOOST));
      if (boost == null || !boost.tensura$hasAbility()) {
         return true;
      }

      if (!boost.tensura$hasSource()) {
         return true;
      }

      if (source.level() instanceof ServerLevel level) {
         Entity owner = level.getEntity(boost.tensura$getSource());
         if (owner == null) {
            return true;
         }

         if (!((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker()).tame(target, owner, false).isFalse()) {
            IExistence existence = TensuraStorages.getExistenceFrom(target);
            existence.setTemporaryOwner(owner.getUUID());
            if (existence.getEP() <= 0.0) {
               existence.setMagicule(50.0);
            }

            double SHP = target.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
            if (existence.getSpiritualHealth() < SHP * ChosenOneSkill.CONFIG.shpMultiplier) {
               existence.setSpiritualHealth(SHP * ChosenOneSkill.CONFIG.shpMultiplier);
            }

            if (owner instanceof Player player && target instanceof ISubordinate subordinate) {
               subordinate.tame(player);
            }

            SubordinateHelper.removeTarget(target);
            existence.markDirty();
            if (target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FEAR))) {
               target.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.FEAR));
            }

            target.setHealth(target.getMaxHealth() * ChosenOneSkill.CONFIG.hpMultiplier);
            TensuraStorages.resetEffect(target);
            target.invulnerableTime = 60;
            target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.TOTEM_USE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.TOTEM_OF_UNDYING, 1.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.FLASH, 1.0);
            return false;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }
}
