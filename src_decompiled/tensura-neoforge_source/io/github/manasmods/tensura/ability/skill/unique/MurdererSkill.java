package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class MurdererSkill extends Skill {
   private static final UniqueSkillConfig.Murderer CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Murderer;
   private static final ResourceLocation MURDERER = ResourceLocation.fromNamespaceAndPath("tensura", "murderer");

   public MurdererSkill() {
      super(Skill.SkillType.UNIQUE);
      this.addHeldAttributeModifier(Attributes.ATTACK_DAMAGE, MURDERER, CONFIG.damageBoost, Operation.ADD_VALUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getTag() != null && instance.getTag().getBoolean("Concealing");
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
      return instance.isMastered(entity) ? CONFIG.damageBoostMastery / CONFIG.damageBoost : 1.0;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (EnergyHelper.isOutOfEnergy(entity, instance, 0, 5.0F)) {
         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.lack_aura.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED),
               false
            );
         }

         if (instance.isToggled()) {
            instance.setToggled(false);
            instance.onToggleOff(entity);
         }

         CompoundTag tag = instance.getOrCreateTag();
         if (tag.getBoolean("Concealing")) {
            tag.putBoolean("Concealing", false);
            AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attack != null) {
               attack.removeModifier(MURDERER);
            }

            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT));
         }
      } else {
         entity.addEffect(
            new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT), 220, CONFIG.concealmentLevel - 1, false, false, false)
         );
      }
   }

   public void addHeldAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (!instance.isMastered(entity)) {
         super.addHeldAttributeModifiers(instance, entity, mode);
      }
   }

   public void removeAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (!instance.isMastered(entity)) {
         super.removeAttributeModifiers(instance, entity, mode);
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.isMastered(entity)) {
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      entity.addEffect(
         new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT), 5, CONFIG.concealmentLevel - 1, false, false, false)
      );
      if (heldTicks == 0) {
         entity.level()
            .playSound(null, entity.getX(), entity.getY(), entity.getZ(), TensuraSoundEvents.PRESENCE_CONCEALMENT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      return true;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      entity.setSilent(true);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (instance.isMastered(entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         if (tag.getBoolean("Concealing")) {
            tag.putBoolean("Concealing", false);
            AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attack != null) {
               attack.removeModifier(MURDERER);
            }

            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT));
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
         } else {
            tag.putBoolean("Concealing", true);
            AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attack != null) {
               double damage = instance.isMastered(entity) ? CONFIG.damageBoostMastery : CONFIG.damageBoost;
               attack.addOrReplacePermanentModifier(new AttributeModifier(MURDERER, damage, Operation.ADD_VALUE));
            }

            entity.addEffect(
               new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.PRESENCE_CONCEALMENT), 220, CONFIG.concealmentLevel - 1, false, false, false
               )
            );
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.PRESENCE_CONCEALMENT.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
         }
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      entity.setSilent(false);
   }
}
