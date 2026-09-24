package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.magic.field.haki.HakiField;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.text.DecimalFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HakiSkill extends Skill {
   public static final ExtraSkillConfig.Haki CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).Haki;
   private static final ResourceLocation HAKI = ResourceLocation.fromNamespaceAndPath("tensura", "haki");
   private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");

   public HakiSkill() {
      super(Skill.SkillType.EXTRA);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, HAKI, CONFIG.speedMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, (ManasSkill)CommonSkills.COERCION.get()) ? false : newEP > CONFIG.epAcquirement;
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public boolean shouldTriggerReleaseOnHeldInterrupt(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      return true;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         SkillHelper.learnSkill(entity, ((MortalFearSkill)ExtraSkills.MORTAL_FEAR.get()).createLearningInstance(entity));
      }
   }

   public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
      return instance.isMastered(entity) ? (CONFIG.speedMultiplierMastered - 1.0) / (CONFIG.speedMultiplier - 1.0) : 1.0;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      instance.getOrCreateTag().putInt("HakiID", 0);
      instance.markDirty();
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      return summonHaki(instance, entity, mode, heldTicks, this, HakiField.HakiVariant.DEFAULT, CONFIG.epDifferenceMultiplier);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (this.hasAttributeApplied(entity, Attributes.MOVEMENT_SPEED, HAKI)) {
         instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
      }
   }

   public static boolean summonHaki(
      ManasSkillInstance instance,
      LivingEntity entity,
      int mode,
      int heldTicks,
      TensuraSkill skill,
      HakiField.HakiVariant variant,
      double epDifferenceMultiplier
   ) {
      double scale = instance.getTag() != null && instance.getTag().contains("scale") ? instance.getTag().getDouble("scale") : 0.0;
      double multiplier = scale == 0.0 ? 1.0 : Math.min(scale, 1.0);
      double ownerEP = EnergyHelper.getMaxEP(entity) * multiplier;
      HakiField haki = HakiField.getHaki(
         variant,
         CONFIG.hakiRadius,
         epDifferenceMultiplier,
         CONFIG.fearDuration,
         entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0),
         entity,
         instance,
         skill,
         mode
      );
      if (heldTicks == 1) {
         if (haki != null) {
            haki.setEp(ownerEP);
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

   public static void hakiPush(LivingEntity target, @Nullable Entity source, ManasSkillInstance skill, int fearLevel) {
      if (fearLevel >= 1 && source != null) {
         if (!target.getType().is(TensuraEntityTags.NO_FORCED_MOVE)) {
            double knockResist = 1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            double multiplier = Math.min(0.04 * fearLevel, 0.2) * knockResist;
            Vec3 vec3 = target.getEyePosition().subtract(source.getEyePosition()).normalize().scale(multiplier);
            Changeable<Vec3> changeable = Changeable.of(vec3);
            if (!((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker()).move(target, source, skill, changeable).isFalse()
               )
             {
               target.push((Vec3)changeable.get());
            }
         }
      }
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      changeEPUsed(instance, entity, delta);
   }

   public static void changeEPUsed(ManasSkillInstance instance, LivingEntity entity, double delta) {
      if (instance.isMastered(entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         double oldScale = tag.getDouble("scale");
         double newScale = getNewScale(delta, oldScale);
         if (tag.getDouble("scale") != newScale) {
            tag.putDouble("scale", newScale);
            if (entity instanceof Player player) {
               player.displayClientMessage(
                  Component.translatable("tensura.skill.power_scale", new Object[]{decimalFormat.format(newScale * 100.0) + "%"})
                     .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)),
                  true
               );
            }

            instance.markDirty();
         }
      }
   }

   private static double getNewScale(double delta, double oldScale) {
      double newScale;
      if (oldScale == 0.1) {
         if (delta >= 0.0) {
            newScale = 0.2;
         } else {
            newScale = 0.05;
         }
      } else if (oldScale == 0.05) {
         if (delta >= 0.0) {
            newScale = 0.1;
         } else {
            newScale = 0.01;
         }
      } else if (oldScale == 0.01) {
         if (delta >= 0.0) {
            newScale = 0.05;
         } else {
            newScale = 0.001;
         }
      } else if (oldScale <= 0.001) {
         if (delta >= 0.0) {
            newScale = 0.01;
         } else {
            newScale = 1.0;
         }
      } else {
         newScale = oldScale + delta * 0.1;
         if (newScale > 1.0) {
            newScale = 0.001;
         }
      }

      return Math.max(newScale, 0.001);
   }
}
