package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class FighterSkill extends Skill {
   private static final UniqueSkillConfig.Fighter CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Fighter;
   protected static final ResourceLocation FIGHTER = ResourceLocation.fromNamespaceAndPath("tensura", "fighter");

   public FighterSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return true;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return true;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance mastery = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (mastery != null) {
         mastery.addOrReplacePermanentModifier(new AttributeModifier(FIGHTER, CONFIG.masteryPoint, Operation.ADD_VALUE));
      }

      AttributeInstance dodge = entity.getAttribute(TensuraAttributes.DODGE_STRENGTH);
      if (dodge != null) {
         dodge.addOrReplacePermanentModifier(new AttributeModifier(FIGHTER, CONFIG.dodgeStrength, Operation.ADD_VALUE));
      }

      AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
      if (invulnerability != null) {
         invulnerability.addOrReplacePermanentModifier(new AttributeModifier(FIGHTER, CONFIG.dodgeInvulnerability, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance mastery = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
      if (mastery != null) {
         mastery.removeModifier(FIGHTER);
      }

      AttributeInstance dodge = entity.getAttribute(TensuraAttributes.DODGE_STRENGTH);
      if (dodge != null) {
         dodge.removeModifier(FIGHTER);
      }

      AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
      if (invulnerability != null) {
         invulnerability.removeModifier(FIGHTER);
      }
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(attacker, instance)) {
         return true;
      }

      if (source.getDirectEntity() != attacker) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return true;
      }

      float damage = instance.getTag() != null && instance.getTag().contains("scale")
         ? instance.getTag().getFloat("scale")
         : (instance.isMastered(attacker) ? CONFIG.attackBoostMastered : CONFIG.attackBoost);
      amount.set((Float)amount.get() + damage);
      if (!instance.onCoolDown(0)) {
         instance.addMasteryPoint(attacker);
         instance.setCoolDown(3, 0);
      }

      return true;
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      float newScale = tag.getFloat("scale") + (int)delta * 5;
      float maxDamage = instance.isMastered(entity) ? CONFIG.attackBoostMastered : CONFIG.attackBoost;
      if (newScale > maxDamage) {
         newScale = 5.0F;
      } else if (newScale < 5.0F) {
         newScale = maxDamage;
      }

      if (tag.getFloat("scale") != newScale) {
         tag.putFloat("scale", newScale);
         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.power_scale", new Object[]{newScale}).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
            );
         }

         instance.markDirty();
      }
   }
}
