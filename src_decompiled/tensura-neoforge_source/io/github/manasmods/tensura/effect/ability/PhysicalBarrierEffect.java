package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.barrier.RangedBarrierEntity;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class PhysicalBarrierEffect extends TensuraMobEffect implements DamageAction {
   private static final ResourceLocation PHYSICAL_BARRIER = ResourceLocation.fromNamespaceAndPath("tensura", "physical_barrier");

   public PhysicalBarrierEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(85, 252, 223).getRGB());
      this.addAttributeModifier(TensuraAttributes.PHYSICAL_BARRIER, PHYSICAL_BARRIER, 1.0, Operation.ADD_VALUE);
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
      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return true;
      } else if (source.getEntity() instanceof LivingEntity attacker && RangedBarrierEntity.shouldInstaBreak(attacker, entity)) {
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.PHYSICAL_BARRIER));
         entity.level().playSound(null, entity.blockPosition(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         return true;
      } else {
         MobEffectInstance barrier = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.PHYSICAL_BARRIER));
         if (barrier == null) {
            return true;
         } else {
            float threshold = barrier.tensura$getOrCreateTag().getFloat("DamageThreshold");
            if ((Float)amount.get() < threshold) {
               amount.set(Math.max((Float)amount.get() - barrier.tensura$getOrCreateTag().getFloat("UnderReduction"), 0.0F));
               return true;
            } else {
               amount.set(Math.max((Float)amount.get() - barrier.tensura$getOrCreateTag().getFloat("AboveReduction"), 0.0F));
               return true;
            }
         }
      }
   }
}
