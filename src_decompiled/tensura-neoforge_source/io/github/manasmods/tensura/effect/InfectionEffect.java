package io.github.manasmods.tensura.effect;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.awt.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public final class InfectionEffect extends TensuraMobEffect implements DamageAction {
   public InfectionEffect() {
      super(MobEffectCategory.HARMFUL, new Color(87, 3, 3).getRGB());
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("tensura", "infection"), -0.15F, Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("tensura", "infection"), -0.2F, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public void addAttributeModifiers(AttributeMap attributeMap, int i) {
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (this.cancelInfection(entity)) {
         return false;
      }

      if (entity.level() instanceof ServerLevel level) {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION));
         if (instance == null) {
            return true;
         }

         if (pAmplifier >= 4) {
            entity.hurt(
               TensuraDamageHelper.getUUIDDamageSource(TensuraDamageTypes.INFECTION, level, instance.tensura$getSource(), instance.tensura$getSourceAbility()),
               entity.getMaxHealth()
            );
         } else if (pAmplifier >= 1) {
            entity.hurt(
               TensuraDamageHelper.getUUIDDamageSource(TensuraDamageTypes.INFECTION, level, instance.tensura$getSource(), instance.tensura$getSourceAbility()),
               2.0F * pAmplifier
            );
         }

         this.increaseInfection(entity, instance, pAmplifier);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 60 == 0;
   }

   private void increaseInfection(LivingEntity entity, MobEffectInstance instance, int pAmplifier) {
      int maxInfectionAge = 10;
      if (pAmplifier == 1) {
         maxInfectionAge = 5;
      }

      if (pAmplifier > 1) {
         maxInfectionAge = 3;
      }

      CompoundTag tag = instance.tensura$getOrCreateTag();
      int age = tag.getInt("age");
      if (age < maxInfectionAge) {
         tag.putInt("age", age + 1);
         if (pAmplifier > 1) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false, false));
            if (pAmplifier > 2) {
               entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 200, pAmplifier - 2, true, false, true));
            }
         }
      } else {
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION));
         MobEffectInstance infection = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.INFECTION), 360, pAmplifier + 1, true, false, true
         );
         TensuraMobEffect.addEffect(entity, infection, instance.tensura$getSource(), instance.tensura$getSourceAbility());
         super.addAttributeModifiers(entity.getAttributes(), pAmplifier);
      }
   }

   public void onMobHurt(LivingEntity entity, int i, DamageSource source, float f) {
      if (TensuraDamageHelper.isPhysicalAttack(source)) {
         if (source.getDirectEntity() instanceof LivingEntity attacker) {
            MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION));
            if (instance != null && instance.tensura$hasAbility()) {
               MobEffectInstance infection = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION), 900, 0, true, false, true);
               TensuraMobEffect.addEffect(attacker, infection, instance.tensura$getSource(), instance.tensura$getSourceAbility());
            }
         }
      }
   }

   @Override
   public boolean onDamagingEntity(LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (TensuraDamageHelper.isPhysicalAttack(source) && source.getDirectEntity() == attacker) {
         MobEffectInstance instance = attacker.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION));
         if (instance != null && instance.tensura$hasAbility()) {
            MobEffectInstance infection = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION), 900, 0, true, false, true);
            TensuraMobEffect.addEffect(target, infection, instance.tensura$getSource(), instance.tensura$getSourceAbility());
            return true;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   private boolean cancelInfection(LivingEntity entity) {
      if (entity.getHealth() <= 0.0F) {
         return true;
      } else {
         return RaceUtils.isUndead(entity) ? true : RaceUtils.isSpiritual(entity);
      }
   }
}
