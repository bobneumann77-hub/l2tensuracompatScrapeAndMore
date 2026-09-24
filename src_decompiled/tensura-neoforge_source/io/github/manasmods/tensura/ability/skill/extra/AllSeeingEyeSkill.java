package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class AllSeeingEyeSkill extends Skill {
   private static final ExtraSkillConfig.AllSeeingEye CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).AllSeeingEye;
   protected static final ResourceLocation ALL_SEEING = ResourceLocation.fromNamespaceAndPath("tensura", "all_seeing");
   protected static final ResourceLocation ALL_SEEING_EYE = ResourceLocation.fromNamespaceAndPath("tensura", "all_seeing_eye");

   public AllSeeingEyeSkill() {
      super(Skill.SkillType.EXTRA);
      this.addHeldAttributeModifier(Attributes.ATTACK_SPEED, ALL_SEEING, CONFIG.attackSpeed, Operation.ADD_VALUE);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, ALL_SEEING, CONFIG.movementSpeed, Operation.ADD_VALUE);
      this.addHeldAttributeModifier(Attributes.BLOCK_BREAK_SPEED, ALL_SEEING, CONFIG.miningSpeed, Operation.ADD_VALUE);
      this.addHeldAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, ALL_SEEING, CONFIG.swimSpeed, Operation.ADD_VALUE);
      this.addHeldAttributeModifier(TensuraAttributes.PRESENCE_SENSE, ALL_SEEING, CONFIG.presenceSense, Operation.ADD_VALUE);
      this.addHeldAttributeModifier(TensuraAttributes.PRESENCE_SENSE_RADIUS, ALL_SEEING, CONFIG.presenceRadius, Operation.ADD_VALUE);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.isMastered(living);
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return 10.0;
   }

   public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
      return instance.isMastered(entity) ? CONFIG.boostMultiplierMastered : 1.0;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!instance.isToggled()) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(entity);
         if (data != null) {
            data.setForcedThirdPerson(true);
            data.markDirty();
         }
      }

      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
         );
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      return true;
   }

   public void removeAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      super.removeAttributeModifiers(instance, entity, mode);
      if (!instance.isToggled()) {
         ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(entity);
         if (data != null) {
            data.setForcedThirdPerson(false);
            data.markDirty();
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
         );
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (EnergyHelper.isOutOfEnergy(entity, instance, 0, 5.0F)) {
         entity.sendSystemMessage(
            Component.translatable("tensura.skill.lack_magicule.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
         );
         instance.setToggled(false);
         instance.onToggleOff(entity);
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(entity);
      if (data != null) {
         data.setForcedThirdPerson(true);
         data.markDirty();
      }

      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.addOrReplacePermanentModifier(new AttributeModifier(ALL_SEEING_EYE, CONFIG.meleeDodge, Operation.ADD_VALUE));
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.addOrReplacePermanentModifier(new AttributeModifier(ALL_SEEING_EYE, CONFIG.projectileDodge, Operation.ADD_VALUE));
      }

      AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
      if (invulnerability != null) {
         invulnerability.addOrReplacePermanentModifier(new AttributeModifier(ALL_SEEING_EYE, CONFIG.dodgeInvulnerability, Operation.ADD_VALUE));
      }

      AttributeMap attributeMap = entity.getAttributes();

      for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
         AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
         if (attributeInstance != null) {
            attributeInstance.removeModifier(entry.getValue().id());
            AttributeModifier modifier = new AttributeModifier(ALL_SEEING_EYE, entry.getValue().amount(), entry.getValue().operation());
            attributeInstance.addOrUpdateTransientModifier(modifier);
         }
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(entity);
      if (data != null) {
         data.setForcedThirdPerson(false);
         data.markDirty();
      }

      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.removeModifier(ALL_SEEING_EYE);
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.removeModifier(ALL_SEEING_EYE);
      }

      AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
      if (invulnerability != null) {
         invulnerability.removeModifier(ALL_SEEING_EYE);
      }

      AttributeMap map = entity.getAttributes();

      for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
         AttributeInstance attributeInstance = map.getInstance(entry.getKey());
         if (attributeInstance != null) {
            attributeInstance.removeModifier(ALL_SEEING_EYE);
         }
      }
   }
}
