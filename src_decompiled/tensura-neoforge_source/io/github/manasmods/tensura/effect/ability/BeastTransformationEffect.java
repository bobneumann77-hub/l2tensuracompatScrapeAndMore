package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
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
import net.minecraft.world.entity.player.Player;

public class BeastTransformationEffect extends TensuraMobEffect implements ITransformation {
   public BeastTransformationEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(0, 210, 255, 255).getRGB());
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("tensura", "beast"), 15.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("tensura", "beast"), 10.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("tensura", "beast"), 0.04F, Operation.ADD_VALUE);
      this.addAttributeModifier(
         TensuraAttributes.MAX_MAGICULE, ResourceLocation.fromNamespaceAndPath("tensura", "beast_energy"), 1.0, Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(
         TensuraAttributes.MAX_AURA, ResourceLocation.fromNamespaceAndPath("tensura", "beast_energy"), 1.0, Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(
         TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE,
         ResourceLocation.fromNamespaceAndPath("tensura", "beast_energy"),
         1.0,
         Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(
         TensuraAttributes.LIMITED_SPIRITUAL_MAX_AURA, ResourceLocation.fromNamespaceAndPath("tensura", "beast_energy"), 1.0, Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public void addAttributeModifiers(AttributeMap attributeMap, int i) {
      for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
         AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
         if (attributeInstance != null) {
            attributeInstance.removeModifier(entry.getValue().id());
            int level = entry.getValue().id().equals(ResourceLocation.fromNamespaceAndPath("tensura", "beast_energy")) ? 0 : i;
            attributeInstance.addOrReplacePermanentModifier(entry.getValue().create(level));
         }
      }
   }

   public void onEffectStarted(LivingEntity entity, int i) {
      super.onEffectStarted(entity, i);
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      existence.setAura(EnergyHelper.getMaxAura(entity));
      existence.setMagicule(EnergyHelper.getMaxMagicule(entity));
      if (i >= 1 && entity instanceof Player player) {
         if (player.isCreative() || player.isSpectator()) {
            return;
         }

         if (player.getAbilities().mayfly) {
            return;
         }

         player.getAbilities().mayfly = true;
         player.getAbilities().flying = true;
         player.onUpdateAbilities();
      }
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (this.failedToActivate(entity, TensuraMobEffects.getReference(TensuraMobEffects.BEAST_TRANSFORMATION))) {
         return false;
      }

      ParticleOptions particle = pAmplifier >= 1
         ? (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get()
         : (ParticleOptions)TensuraParticleTypes.LIGHTNING_SPARK.get();
      TensuraParticleHelper.addParticlesAroundSelf(entity, particle);
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
      if (instance.getAmplifier() >= 1 && entity instanceof Player player) {
         if (player.isCreative() || player.isSpectator()) {
            return;
         }

         if (!player.getAbilities().mayfly) {
            return;
         }

         player.getAbilities().mayfly = false;
         player.getAbilities().flying = false;
         player.onUpdateAbilities();
      }
   }

   @Override
   public void onEffectRemoved(LivingEntity entity, MobEffectInstance instance) {
      this.applyDebuff(entity);
   }
}
