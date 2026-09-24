package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class DiamondPathEffect extends TensuraMobEffect implements DamageAction {
   private static final BattlewillConfig.DiamondPath CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).DiamondPath;
   public static final ResourceLocation DIAMOND_PATH = ResourceLocation.fromNamespaceAndPath("tensura", "diamond");

   public DiamondPathEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(6, 255, 255).getRGB());
      this.addAttributeModifier(Attributes.ARMOR, DIAMOND_PATH, CONFIG.damageBoost, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, DIAMOND_PATH, CONFIG.knockBackResistanceBoost, Operation.ADD_VALUE);
   }

   public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
      if (pLivingEntity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.CHILL))) {
         pLivingEntity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.CHILL));
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 20 == 0;
   }

   @Override
   public boolean onBeingDamaged(LivingEntity entity, DamageSource source, Changeable<Float> amount) {
      if (source.tensura$getResistanceBypassLevel() >= 1.0F) {
         return true;
      } else if (!TensuraDamageHelper.isNaturalEffects(source)) {
         return true;
      } else if (source.getEntity() instanceof LivingEntity living && living.getAttributeValue(TensuraAttributes.RESISTANCE_DEGRADATION) >= 1.0) {
         return true;
      } else {
         double hpMultiplier = ResistSkill.CONFIG.hpDamageBypassResistance;
         if (!(hpMultiplier < 0.0) && !(((Float)amount.get()).floatValue() < entity.getHealth() * hpMultiplier)) {
            float multiplier = (float)ResistSkill.CONFIG.hpDamageBypassResistance;
            if (multiplier <= 0.0F) {
               return false;
            }

            amount.set((Float)amount.get() * multiplier);
            return true;
         } else {
            return false;
         }
      }
   }
}
