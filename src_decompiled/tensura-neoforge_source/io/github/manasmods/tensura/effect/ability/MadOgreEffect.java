package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.awt.Color;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect.AttributeTemplate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class MadOgreEffect extends TensuraMobEffect implements ITransformation {
   public static final ResourceLocation MAD = ResourceLocation.fromNamespaceAndPath("tensura", "mad_ogre");
   protected static final ResourceLocation MAD_UNSTACK = ResourceLocation.fromNamespaceAndPath("tensura", "mad_ogre_unstack");

   public MadOgreEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(227, 15, 15).getRGB());
      this.addAttributeModifier(Attributes.ARMOR, MAD, 20.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, MAD, 45.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, MAD, 0.4, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.BURNING_TIME, MAD_UNSTACK, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.STEP_HEIGHT, MAD_UNSTACK, 1.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MAD_UNSTACK, 0.04, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, MAD_UNSTACK, 2.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, MAD_UNSTACK, 2.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.SCALE, MAD_UNSTACK, 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public void addAttributeModifiers(AttributeMap attributeMap, int i) {
      for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
         AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
         if (attributeInstance != null) {
            attributeInstance.removeModifier(entry.getValue().id());
            int level = entry.getValue().id().equals(MAD_UNSTACK) ? 0 : i;
            attributeInstance.addOrReplacePermanentModifier(entry.getValue().create(level));
         }
      }
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.level().isClientSide() && entity.getRandom().nextBoolean()) {
         TensuraParticleHelper.addParticlesAroundSelf(entity, ParticleTypes.LAVA);
      }

      if (!entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))) {
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), 600, 2, true, false, true));
      }

      if (SkillUtils.shouldCancelInteraction(entity)) {
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAD_OGRE));
      }

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
