package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import java.awt.Color;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
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

public class DragonModeEffect extends TensuraMobEffect implements ITransformation {
   public static final ResourceLocation DRAGON_MODE = ResourceLocation.fromNamespaceAndPath("tensura", "dragon_mode");
   public static final ResourceLocation DRAGON_ENERGY = ResourceLocation.fromNamespaceAndPath("tensura", "dragon_energy");

   public DragonModeEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(255, 181, 0, 255).getRGB());
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, DRAGON_MODE, 12.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ARMOR, DRAGON_MODE, 20.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, DRAGON_MODE, 0.05F, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.MAX_MAGICULE, DRAGON_ENERGY, 1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.MAX_AURA, DRAGON_ENERGY, 1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE, DRAGON_ENERGY, 1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.LIMITED_SPIRITUAL_MAX_AURA, DRAGON_ENERGY, 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public void addAttributeModifiers(AttributeMap attributeMap, int i) {
      for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
         AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
         if (attributeInstance != null) {
            attributeInstance.removeModifier(entry.getValue().id());
            int level = entry.getValue().id().equals(DRAGON_ENERGY) ? 0 : i;
            attributeInstance.addOrReplacePermanentModifier(entry.getValue().create(level));
         }
      }
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (this.failedToActivate(entity, TensuraMobEffects.getReference(TensuraMobEffects.DRAGON_MODE))) {
         return false;
      }

      TensuraParticleHelper.addParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get());
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int amplifier) {
      return pDuration % 10 == 0;
   }

   @Override
   public void onAttributeRemoved(LivingEntity entity, MobEffectInstance instance) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      existence.setMagicule(existence.getMagicule() / 2.0);
      existence.setAura(existence.getAura() / 2.0);
      existence.markDirty();
   }

   @Override
   public void onEffectRemoved(LivingEntity entity, MobEffectInstance instance) {
      this.applyDebuff(entity);
   }
}
