package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class GourmandSkill extends Skill {
   private static final UniqueSkillConfig.Gourmand CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Gourmand;
   protected static final ResourceLocation GOURMAND = ResourceLocation.fromNamespaceAndPath("tensura", "gourmand");

   public GourmandSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      double bonusAura = instance.isMastered(entity) ? CONFIG.auraPercentageMastered : CONFIG.auraPercentage;
      AttributeInstance aura = entity.getAttribute(TensuraAttributes.AURA_GAIN);
      if (aura != null && !aura.hasModifier(GOURMAND)) {
         aura.addOrReplacePermanentModifier(new AttributeModifier(GOURMAND, bonusAura, Operation.ADD_VALUE));
      }

      double bonusMagicule = instance.isMastered(entity) ? CONFIG.magiculePercentageMastered : CONFIG.magiculePercentage;
      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAGICULE_GAIN);
      if (magicule != null && !magicule.hasModifier(GOURMAND)) {
         magicule.addOrReplacePermanentModifier(new AttributeModifier(GOURMAND, bonusMagicule, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance aura = entity.getAttribute(TensuraAttributes.AURA_GAIN);
      if (aura != null) {
         aura.removeModifier(GOURMAND);
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAGICULE_GAIN);
      if (magicule != null) {
         magicule.removeModifier(GOURMAND);
      }
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(attacker, instance)) {
         return true;
      }

      double chance = instance.isMastered(attacker) ? CONFIG.epStealChanceMastered : CONFIG.epStealChance;
      if (attacker.getRandom().nextFloat() > chance / 100.0) {
         return true;
      }

      if (EnergyHelper.drainEnergy(target, attacker, CONFIG.epStealPercentage, true, EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NORMAL)
         && attacker instanceof Player player) {
         player.playNotifySound(SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
      }

      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE), false);
      if (target != null) {
         if (!target.hasInfiniteMaterials()) {
            double EP = TensuraStorages.getExistenceFrom(entity).getEP();
            double targetEP = TensuraStorages.getExistenceFrom(target).getEP();
            MobEffectInstance fear = target.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FEAR));
            if (fear != null && fear.getAmplifier() >= CONFIG.fearHeartEat - 1.0 || targetEP <= EP * CONFIG.epHeartEat) {
               DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.HEART_EAT, mode);
               if (target.hurt(source, target.getMaxHealth() * 10.0F)) {
                  if (!target.isAlive() && !target.getType().is(TensuraEntityTags.NO_EP_PLUNDER)) {
                     instance.addMasteryPoint(entity);
                     EnergyHelper.gainAura(entity, targetEP * CONFIG.heartEatEpMultiplier / 2.0, EnergyHelper.GainType.NORMAL);
                     EnergyHelper.gainMagicule(entity, targetEP * CONFIG.heartEatEpMultiplier / 2.0, EnergyHelper.GainType.NORMAL);
                  }

                  entity.level()
                     .playSound(
                        null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.EATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                     );
                  double size = target.getAttributeValue(Attributes.SCALE) * 4.0;
                  TensuraParticleHelper.addServerAuraParticles(target, TensuraParticleUtils.getCrimsonAura(0.5F, (float)size, -0.3F), 10, 0.01);
               }
            }
         }
      }
   }
}
