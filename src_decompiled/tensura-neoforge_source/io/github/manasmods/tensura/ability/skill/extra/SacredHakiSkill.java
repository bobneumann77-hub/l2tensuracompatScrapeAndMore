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
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class SacredHakiSkill extends Skill {
   public static final ExtraSkillConfig.SacredHaki CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).SacredHaki;
   private static final ResourceLocation HAKI = ResourceLocation.fromNamespaceAndPath("tensura", "sacred_haki");

   public SacredHakiSkill() {
      super(Skill.SkillType.EXTRA);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, HAKI, HakiSkill.CONFIG.speedMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.HERO_HAKI.get()) ? false : TensuraStorages.getExistenceFrom(entity).isTrueHero();
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

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == 0 && instance.getMastery() >= 0.0;
   }

   @Override
   public boolean shouldTriggerReleaseOnHeldInterrupt(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      return mode == 0;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCost;
         case 1 -> CONFIG.magiculeCostCoat;
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

      double scale = instance.getTag() != null && instance.getTag().contains("scale") ? instance.getTag().getDouble("scale") : 0.0;
      double multiplier = scale == 0.0 ? 1.0 : Math.min(scale, 1.0);
      double ownerEP = EnergyHelper.getMaxEP(entity) * multiplier;
      HakiField haki = HakiField.getHaki(
         (EntityType<? extends HakiField>)MiscEntityTypes.SACRED_HAKI_FIELD.get(),
         HakiField.HakiVariant.SACRED,
         HakiSkill.CONFIG.hakiRadius,
         CONFIG.epDifferenceMultiplier,
         HakiSkill.CONFIG.fearDuration,
         entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0),
         entity,
         instance,
         this,
         mode
      );
      if (heldTicks == 1) {
         if (haki != null) {
            haki.setEp(ownerEP);
            haki.setDamage(CONFIG.healHP);
         }

         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.HAKI_START.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      } else {
         if (haki == null) {
            return false;
         }

         if (heldTicks >= 40) {
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.HAKI_LOOP.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      }

      return true;
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 0) {
         if (this.hasAttributeApplied(entity, Attributes.MOVEMENT_SPEED, HAKI)) {
            instance.setCoolDown(instance.isMastered(entity) ? HakiSkill.CONFIG.cooldownMastered : HakiSkill.CONFIG.cooldown, mode);
         }
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
               .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         } else {
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.HAKI_COAT));
            entity.level()
               .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      HakiSkill.changeEPUsed(instance, entity, delta);
   }
}
