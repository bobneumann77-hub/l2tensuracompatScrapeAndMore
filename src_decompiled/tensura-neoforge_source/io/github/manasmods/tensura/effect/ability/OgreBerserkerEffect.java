package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.awt.Color;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class OgreBerserkerEffect extends TensuraMobEffect implements ITransformation {
   protected static final ResourceLocation OGRE = ResourceLocation.fromNamespaceAndPath("tensura", "ogre_berserker");

   public OgreBerserkerEffect() {
      super(MobEffectCategory.NEUTRAL, new Color(124, 81, 175).getRGB());
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, OGRE, 30.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ARMOR, OGRE, 10.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, OGRE, 0.03F, Operation.ADD_VALUE);
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (pAmplifier < 1 && this.failedToActivate(entity, TensuraMobEffects.getReference(TensuraMobEffects.OGRE_BERSERKER))) {
         return false;
      }

      float damage = pAmplifier * 10.0F;
      DamageSource source = TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.ENERGY_DRAIN);
      if (damage > 0.0F) {
         entity.hurt(source, damage);
      }

      TensuraParticleHelper.addParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.PURPLE_LIGHTNING_SPARK.get());
      return true;
   }

   @Override
   public void onEffectRemoved(LivingEntity entity, MobEffectInstance instance) {
      this.applyDebuff(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int amplifier) {
      return pDuration % 20 == 0;
   }
}
