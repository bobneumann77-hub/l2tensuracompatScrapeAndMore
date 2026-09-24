package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.entity.magic.field.haki.HakiField;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class DemonLordHakiSkill extends Skill {
   private static final ExtraSkillConfig.DemonLordHaki CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).DemonLordHaki;
   private static final ResourceLocation HAKI = ResourceLocation.fromNamespaceAndPath("tensura", "demon_lord_haki");

   public DemonLordHakiSkill() {
      super(Skill.SkillType.EXTRA);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, HAKI, HakiSkill.CONFIG.speedMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      Alignment alignment = existence.getAlignment();
      if (!alignment.isCanBecomeDemonLord()) {
         return false;
      } else {
         return !SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.HAKI.get()) ? false : newEP > CONFIG.epAcquirement;
      }
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
         case 0 -> "haki.release";
         case 1 -> "haki.coat";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 1 ? CONFIG.magiculeCostCoat : CONFIG.magiculeCost;
   }

   public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
      if (mode != 0) {
         return 0.0;
      } else {
         return instance.isMastered(entity) ? (HakiSkill.CONFIG.speedMultiplierMastered - 1.0) / (HakiSkill.CONFIG.speedMultiplier - 1.0) : 1.0;
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0) {
         instance.getOrCreateTag().putInt("HakiID", 0);
         instance.markDirty();
      } else {
         if (!entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.HAKI_COAT))) {
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.HAKI_COAT), CONFIG.coatDuration, 0, false, false, false));
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.HAKI_COAT));
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
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

      return summonDemonLordHaki(instance, entity, mode, heldTicks, this);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 0) {
         if (this.hasAttributeApplied(entity, Attributes.MOVEMENT_SPEED, HAKI)) {
            instance.setCoolDown(instance.isMastered(entity) ? HakiSkill.CONFIG.cooldownMastered : HakiSkill.CONFIG.cooldown, mode);
         }
      }
   }

   public static boolean summonDemonLordHaki(ManasSkillInstance instance, LivingEntity entity, int mode, int heldTicks, TensuraSkill skill) {
      return HakiSkill.summonHaki(instance, entity, mode, heldTicks, skill, HakiField.HakiVariant.DEMON_LORD, CONFIG.epDifferenceMultiplier);
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      HakiSkill.changeEPUsed(instance, entity, delta);
   }
}
