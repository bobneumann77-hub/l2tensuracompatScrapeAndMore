package io.github.manasmods.tensura.effect;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class WebbedEffect extends TensuraMobEffect {
   private static final ResourceLocation WEBBED = ResourceLocation.fromNamespaceAndPath("tensura", "webbed");

   public WebbedEffect() {
      super(MobEffectCategory.HARMFUL, new Color(187, 180, 180).getRGB());
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, WEBBED, -0.99F, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, WEBBED, -0.99F, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, WEBBED, -0.5, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, WEBBED, -0.5, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, WEBBED, -0.5, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.FOLLOW_RANGE, WEBBED, -0.8F, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public void onEffectStarted(LivingEntity entity, int i) {
      super.onEffectStarted(entity, i);
      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SILENCE))) {
         IEffect effect = TensuraStorages.getEffectFrom(entity);
         effect.setLockedXRot(entity.getXRot());
         effect.setLockedYRot(entity.getYHeadRot());
         effect.markDirty();
      }
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      lockRotation(entity);
      if (entity.isOnFire()) {
         if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SILENCE))) {
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.SILENCE));
         }

         return false;
      } else {
         return true;
      }
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration > 0;
   }

   public static boolean isFullyWebbed(LivingEntity entity) {
      AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      if (speed != null && speed.hasModifier(WEBBED)) {
         AttributeInstance chant = entity.getAttribute(TensuraAttributes.CHANT_SPEED);
         return chant != null && chant.hasModifier(SilenceEffect.SILENCE);
      } else {
         return false;
      }
   }

   public static void lockRotation(LivingEntity entity) {
      IEffect effect = TensuraStorages.getEffectFrom(entity);
      float lockX = effect.getLockedXRot();
      float lockY = effect.getLockedYRot();
      if (entity.getXRot() != lockX || entity.getYRot() != lockY || entity.getYHeadRot() != lockY || entity.yBodyRot != lockY) {
         entity.setXRot(lockX);
         entity.setYRot(lockY);
         entity.setYHeadRot(lockY);
         entity.setYBodyRot(lockY);
         entity.xRotO = lockX;
         entity.yRotO = lockY;
         entity.yHeadRotO = lockY;
         entity.yBodyRotO = lockY;
      }
   }
}
