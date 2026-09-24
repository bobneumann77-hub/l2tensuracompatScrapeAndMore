package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.extra.DemonLordHakiSkill;
import io.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class VillainSkill extends Skill {
   public static final UniqueSkillConfig.Villain CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Villain;
   public static final ResourceLocation VILLAIN = ResourceLocation.fromNamespaceAndPath("tensura", "villain");

   public VillainSkill() {
      super(Skill.SkillType.UNIQUE);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, VILLAIN, HakiSkill.CONFIG.speedMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled() && instance.isMastered(entity) || this.isInSlot(entity, instance);
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == 0 && instance.getMastery() >= 0.0;
   }

   @Override
   public boolean shouldTriggerReleaseOnHeldInterrupt(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      return mode == 0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "villain.haki";
         case 1 -> "villain.charisma";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCostHaki;
         case 1 -> CONFIG.magiculeCostCharisma;
         default -> 0.0;
      };
   }

   public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
      if (mode != 0) {
         return 0.0;
      } else {
         return instance.isMastered(entity) ? (HakiSkill.CONFIG.speedMultiplierMastered - 1.0) / (HakiSkill.CONFIG.speedMultiplier - 1.0) : 1.0;
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      this.onTick(instance, entity);
      AttributeInstance aura = entity.getAttribute(TensuraAttributes.AURA_GAIN);
      if (aura != null && !aura.hasModifier(VILLAIN)) {
         aura.addOrReplacePermanentModifier(new AttributeModifier(VILLAIN, CONFIG.auraPercentage, Operation.ADD_VALUE));
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAGICULE_GAIN);
      if (magicule != null && !magicule.hasModifier(VILLAIN)) {
         magicule.addOrReplacePermanentModifier(new AttributeModifier(VILLAIN, CONFIG.magiculePercentage, Operation.ADD_VALUE));
      }

      AttributeInstance negate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (negate != null && !negate.hasModifier(VILLAIN)) {
         negate.addOrReplacePermanentModifier(new AttributeModifier(VILLAIN, CONFIG.dodgeNegateChance, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.HAKI_COAT));
      AttributeInstance aura = entity.getAttribute(TensuraAttributes.AURA_GAIN);
      if (aura != null) {
         aura.removeModifier(VILLAIN);
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAGICULE_GAIN);
      if (magicule != null) {
         magicule.removeModifier(VILLAIN);
      }

      AttributeInstance negate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (negate != null) {
         negate.removeModifier(VILLAIN);
      }
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (instance.isToggled() && instance.isMastered(entity)) {
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.HAKI_COAT), 240, 0, false, false, false));
      }

      if (this.isInSlot(entity, instance)) {
         List<LivingEntity> list = entity.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               entity.getBoundingBox().inflate(CONFIG.intimidationRadius),
               living -> living.isAlive() && (living.isAlliedTo(entity) || living.is(entity))
            );
         if (list.isEmpty()) {
            return;
         }

         for (LivingEntity target : list) {
            MobEffectInstance allyBoost = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ALLY_BOOST), 240, 0, false, false, false);
            target.addEffect(allyBoost, entity);
         }
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 0) {
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      return DemonLordHakiSkill.summonDemonLordHaki(instance, entity, mode, heldTicks, this);
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      HakiSkill.changeEPUsed(instance, entity, delta);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 0) {
         if (this.hasAttributeApplied(entity, Attributes.MOVEMENT_SPEED, VILLAIN)) {
            instance.setCoolDown(instance.isMastered(entity) ? HakiSkill.CONFIG.cooldownMastered : HakiSkill.CONFIG.cooldown, mode);
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0) {
         instance.getOrCreateTag().putInt("HakiID", 0);
         instance.markDirty();
      } else {
         Level level = entity.level();
         List<LivingEntity> list = level.getEntitiesOfClass(
            LivingEntity.class, entity.getBoundingBox().inflate(CONFIG.controlRadius), targetx -> ChosenOneSkill.canApplyCharisma(targetx, entity)
         );
         if (list.isEmpty()) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
            }

            level.playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
               TensuraSkill.ABILITY_SOUND,
               1.0F,
               1.0F
            );
         } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            instance.addMasteryPoint(entity);
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );

            for (LivingEntity target : list) {
               IExistence existence = TensuraStorages.getExistenceFrom(target);
               if (Objects.equals(existence.getPermanentOwner(), entity.getUUID())) {
                  return;
               }

               int duration = SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())
                  ? CONFIG.controlResistedDuration
                  : CONFIG.controlDuration;
               if (!((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker()).tame(target, entity, duration != -1).isFalse()) {
                  if (duration != -1) {
                     MobEffectInstance mindControl = new MobEffectInstance(
                        TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL), duration, 0, false, false, false
                     );
                     TensuraMobEffect.addEffect(target, mindControl, entity, instance.getSkill(), mode);
                     if (!target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL))) {
                        return;
                     }
                  }

                  existence.setTemporaryOwner(entity.getUUID());
                  if (target instanceof Mob mob) {
                     SubordinateHelper.removeTarget(mob);
                  }

                  if (target instanceof ISubordinate subordinate && entity instanceof Player player) {
                     subordinate.tame(player);
                  }

                  entity.swing(InteractionHand.MAIN_HAND, true);
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.TRIAL_OMEN);
               }
            }
         }
      }
   }
}
