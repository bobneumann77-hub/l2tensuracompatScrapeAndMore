package io.github.manasmods.tensura.effect;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.ability.skill.unique.WrathSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect.AttributeTemplate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class RampageEffect extends TensuraMobEffect implements DamageAction {
   public RampageEffect() {
      super(MobEffectCategory.NEUTRAL, new Color(84, 29, 29).getRGB());
      this.addAttributeModifier(Attributes.ARMOR, WrathSkill.WRATH, WrathSkill.CONFIG.rampageArmor, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, WrathSkill.WRATH, WrathSkill.CONFIG.rampageAttack, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, WrathSkill.WRATH, WrathSkill.CONFIG.rampageAttackSpeed, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, WrathSkill.WRATH, WrathSkill.CONFIG.rampageSpeed, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, WrathSkill.WRATH, WrathSkill.CONFIG.rampageKnockbackResistance, Operation.ADD_VALUE);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, WrathSkill.WRATH, WrathSkill.CONFIG.rampageSpeed, Operation.ADD_VALUE);
      this.addAttributeModifier(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER, WrathSkill.WRATH, WrathSkill.CONFIG.rampageSpeed, Operation.ADD_VALUE);
   }

   public void addAttributeModifiers(AttributeMap attributeMap, int i) {
   }

   public void onEffectStarted(LivingEntity entity, int i) {
      super.onEffectStarted(entity, i);
      MobEffectInstance rampage = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
      if (rampage != null) {
         if (rampage.tensura$hasSource() && entity.level() instanceof ServerLevel level) {
            AttributeMap map = entity.getAttributes();
            if (level.getEntity(rampage.tensura$getSource()) == entity) {
               super.addAttributeModifiers(map, i);
            } else {
               for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
                  if (!((Attribute)entry.getKey().value()).equals(Attributes.ARMOR)
                     && !((Attribute)entry.getKey().value()).equals(Attributes.KNOCKBACK_RESISTANCE)) {
                     AttributeInstance attributeInstance = map.getInstance(entry.getKey());
                     if (attributeInstance != null) {
                        attributeInstance.removeModifier(entry.getValue().id());
                        double amount = entry.getValue().amount() * (i + 1);
                        if (((Attribute)entry.getKey().value()).equals(Attributes.ATTACK_DAMAGE)) {
                           amount /= 20.0;
                        }

                        AttributeModifier modifier = new AttributeModifier(entry.getValue().id(), amount, entry.getValue().operation());
                        attributeInstance.addOrReplacePermanentModifier(modifier);
                     }
                  }
               }
            }
         }
      }
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.getType().is(TensuraEntityTags.CLONES)) {
         return true;
      }

      if (entity instanceof Mob mob) {
         if (mob.getTarget() != null || mob.getType().is(TensuraEntityTags.NO_MIND_CONTROL)) {
            return true;
         }

         if (mob.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSPIRATION))) {
            return true;
         }

         List<LivingEntity> list = mob.level()
            .getEntitiesOfClass(
               LivingEntity.class, mob.getBoundingBox().inflate(CONFIG.Rampage.mobAggroRadius), target -> this.targetFilter(mob, target, pAmplifier)
            );
         if (list.isEmpty()) {
            return true;
         }

         SubordinateHelper.setFollow(mob);
         Iterator existence = list.iterator();
         if (existence.hasNext()) {
            LivingEntity target = (LivingEntity)existence.next();
            mob.setTarget(target);
         }
      } else if (entity instanceof Player player) {
         MobEffectInstance instance = player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
         if (instance == null) {
            return true;
         }

         if (instance.getDuration() > CONFIG.Rampage.playerDamageDuration) {
            return true;
         }

         if (entity.getHealth() > 1.0F) {
            player.hurt(
               TensuraDamageTypes.getDamageSource(player.level(), TensuraDamageTypes.INSANITY),
               Math.min(player.getMaxHealth() * CONFIG.Rampage.playerDamageHP, player.getHealth() - 1.0F)
            );
         }

         IExistence existence = TensuraStorages.getExistenceFrom(player);
         if (existence.getSpiritualHealth() > 1.0) {
            double maxSHP = player.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
            TensuraDamageHelper.directSpiritualHurt(
               player, player, (float)Math.min(maxSHP * CONFIG.Rampage.playerDamageSHP, existence.getSpiritualHealth() - 1.0)
            );
         }
      }

      return true;
   }

   @Override
   public boolean onDamagingEntity(LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      MobEffectInstance instance = attacker.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
      if (instance == null) {
         return true;
      }

      if (instance.getDuration() >= CONFIG.Rampage.replenishDamage) {
         return true;
      }

      int duration = Math.min(instance.getDuration() + CONFIG.Rampage.replenishEach, CONFIG.Rampage.replenishDamage);
      TensuraMobEffect.addEffect(
         attacker,
         TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE),
         duration,
         instance.getAmplifier(),
         instance.isAmbient(),
         instance.isVisible(),
         instance.showIcon(),
         instance.tensura$getSource(),
         instance.tensura$getSourceAbility()
      );
      return true;
   }

   @Override
   public void onPostBeingDamaged(LivingEntity entity, DamageSource source, float amount) {
      if (source.getEntity() != null) {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
         if (instance != null) {
            if (instance.getDuration() < CONFIG.Rampage.replenishHurt) {
               int duration = Math.max(instance.getDuration() + CONFIG.Rampage.replenishEach, CONFIG.Rampage.replenishHurt);
               TensuraMobEffect.addEffect(
                  entity,
                  TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE),
                  duration,
                  instance.getAmplifier(),
                  instance.isAmbient(),
                  instance.isVisible(),
                  instance.showIcon(),
                  instance.tensura$getSource(),
                  instance.tensura$getSourceAbility()
               );
            }
         }
      }
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 20 == 0;
   }

   private boolean targetFilter(Mob mob, LivingEntity target, int level) {
      if (mob == target) {
         return false;
      } else if (!target.isAlive()) {
         return false;
      } else if (target.hasInfiniteMaterials()) {
         return false;
      } else {
         return level == 0 ? target.getType().equals(EntityType.PLAYER) && !target.isAlliedTo(mob) : !target.isAlliedTo(mob) || level >= 2;
      }
   }
}
